package com.lloydsbanking.salsa.activate.downstream;


import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.downstream.dp.client.encrypt.EncryptClient;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.logging.application.TraceLogUtility;
import com.lloydsbanking.salsa.soap.encrpyt.objects.*;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.Condition;
import lib_sim_bo.businessobjects.ExtraConditions;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.WebServiceException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EncryptDataRetriever {

    public static final String ENCRYPT_INP_CODE = "base64";
    public static final int MAX_CREDIT_CARD_LENGTH = 20;
    private static final Logger LOGGER = Logger.getLogger(EncryptDataRetriever.class);
    @Autowired
    HeaderRetriever headerRetriever;
    @Autowired
    EncryptClient encryptClient;
    @Autowired
    ExceptionUtilityActivate exceptionUtilityActivate;
    @Autowired
    TraceLogUtility traceLogUtility;

    public List<String> retrieveEncryptCardNumber(RequestHeader requestHeader, List<String> creditCardNumbers, String encryptKey) throws ActivateProductArrangementResourceNotAvailableErrorMsg {
        EncryptDataRequest encryptDataRequest = retrieveEncryptDataRequest(creditCardNumbers, encryptKey);
        List<String> encryptCardNumbers = new ArrayList<>();
        LOGGER.info("Entering RetrieveEncryptData with EncryptionKey: " + encryptKey);
        EncryptDataResponse encryptDataResponse = invokeClient(requestHeader, encryptDataRequest);
        if (encryptDataResponse != null && !CollectionUtils.isEmpty(encryptDataResponse.getOutdetails())) {
            for (Outdetails outDetail : encryptDataResponse.getOutdetails()) {
                encryptCardNumbers.add(outDetail.getOuttextDetails());
            }
            LOGGER.info("Exiting RetrieveEncryptData with Encrypted Debit Card Number" + encryptDataResponse.getOutdetails().get(0).getOuttextDetails());
        }
        return encryptCardNumbers;
    }

    private EncryptDataResponse invokeClient(RequestHeader requestHeader, EncryptDataRequest encryptDataRequest) throws ActivateProductArrangementResourceNotAvailableErrorMsg {
        EncryptDataResponse encryptDataResponse = null;
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders());
        ContactPoint contactPoint = headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(requestHeader);
        try {
            encryptDataResponse = encryptClient.retrieveEncryptData(encryptDataRequest, contactPoint, serviceRequest, securityHeaderType);
        } catch (WebServiceException e) {
            LOGGER.error("Error while calling retrieve encrypt data " + e);
            throw exceptionUtilityActivate.resourceNotAvailableError(requestHeader, "Error while calling retrieve encrypt data ");
        }
        return encryptDataResponse;
    }


    private EncryptDataRequest retrieveEncryptDataRequest(List<String> creditCardNumbers, String encryptKey) {
        EncryptDataRequest encryptDataRequest = new EncryptDataRequest();
        for (String creditCardNumber : creditCardNumbers) {
            Indetails indetails = new Indetails();
            indetails.setIntext(creditCardNumber);
            indetails.setEncryptKey(encryptKey);
            indetails.setEncryptType(EncryptionType.SYMM);
            indetails.setInpEncode(ENCRYPT_INP_CODE);
            encryptDataRequest.getIndetails().add(indetails);
        }
        return encryptDataRequest;
    }

    private Condition getCondition() {
        Condition condition = new Condition();
        condition.setReasonCode("011");
        condition.setReasonText("Failed to Encrypt Credit Card");
        return condition;
    }

    public void callEncryptData(String encryptionKey, FinanceServiceArrangement financeServiceArrangement, RequestHeader requestHeader, ApplicationDetails applicationDetails, ExtraConditions extraConditions) {
        LOGGER.info("Entering RetrieveEncryptData with encryptionKey: " + encryptionKey);
        if (financeServiceArrangement.getCreditCardNumber() != null && encryptionKey != null && financeServiceArrangement.getCreditCardNumber().length() <= MAX_CREDIT_CARD_LENGTH) {
            List<String> encryptValue = new ArrayList<>();
            encryptValue.add(financeServiceArrangement.getCreditCardNumber());
            if (financeServiceArrangement.getAddOnCreditCardNumber() != null) {
                encryptValue.add(financeServiceArrangement.getAddOnCreditCardNumber());
            }
            try {
                List<String> creditCardNumbers = retrieveEncryptCardNumber(requestHeader, encryptValue, encryptionKey);
                if (!StringUtils.isEmpty(creditCardNumbers)) {
                    financeServiceArrangement.setCreditCardNumber(creditCardNumbers.get(0));
                    if (creditCardNumbers.size() == 2 && creditCardNumbers.get(1) != null) {
                        financeServiceArrangement.setAddOnCreditCardNumber(creditCardNumbers.get(1));
                    }
                    LOGGER.info("Exiting RetrieveEncryptData, encryptedCreditCardNo: " + creditCardNumbers.get(0));
                }
            } catch (ActivateProductArrangementResourceNotAvailableErrorMsg resourceNotAvailableErrorMsg) {
                LOGGER.info("Error while calling retrieve encrypt data and the error is consumed " + resourceNotAvailableErrorMsg);
                applicationDetails.getConditionList().add(getCondition());
                extraConditions.getConditions().addAll(applicationDetails.getConditionList());
                financeServiceArrangement.setCreditCardNumber(null);
                if (!StringUtils.isEmpty(financeServiceArrangement.getAddOnCreditCardNumber())) {
                    financeServiceArrangement.setAddOnCreditCardNumber(null);
                }
            }

        }
    }

}
