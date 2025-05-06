package com.lloydsbanking.salsa.ppae.service.downstream;

import com.lloydsbanking.salsa.downstream.pp.client.PaymentProcessingClient;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.ppae.service.convert.EnquirePaymentRequestFactory;
import com.lloydsbanking.salsa.soap.fs.ftp.EnquirePaymentInstructionFacilityDetailsRequest;
import com.lloydsbanking.salsa.soap.fs.ftp.EnquirePaymentInstructionFacilityDetailsResponse;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.enterprise.lcsm_common.Condition;
import com.lloydstsb.schema.enterprise.lcsm_common.RuleCondition;
import com.lloydstsb.schema.enterprise.lcsm_financialtransactionprocessing.ErrorInfo;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.BalanceTransfer;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.ws.WebServiceException;
import java.util.List;

@Component
public class EnquirePaymentRetriever {

    private static final String FAST_PAYMENT_FAIL = "FP FAIL";
    private static final String FAST_PAYMENT_SYSTEM_ERROR = "FP SYSERR";
    private static final String FAST_PAYMENT_INDICATOR = "FAST_PAYMENT_SYSTEM_INDICATOR";
    private static final String FASTER_PAYMENT = "P";

    @Autowired
    EnquirePaymentRequestFactory enquirePaymentRequestFactory;
    @Autowired
    HeaderRetriever headerRetriever;
    @Autowired
    PaymentProcessingClient paymentProcessingClient;

    private static final Logger LOGGER = Logger.getLogger(EnquirePaymentRetriever.class);

    public void retrieve(RequestHeader header, BalanceTransfer balanceTransfer) {
        EnquirePaymentInstructionFacilityDetailsRequest request = enquirePaymentRequestFactory.convert(balanceTransfer.getCreditCardNumber());
        EnquirePaymentInstructionFacilityDetailsResponse response = null;
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        BapiInformation bapiInformation = headerRetriever.getBapiInformationHeader(header.getLloydsHeaders());
        try {
            LOGGER.info("Entering AVS enquirePaymentInstruction With CreditCardNo. " + balanceTransfer.getCreditCardNumber());
            response = paymentProcessingClient.enquirePaymentInstructionFacilityDetails(request, contactPoint, serviceRequest, securityHeaderType, bapiInformation);
        } catch (WebServiceException | ErrorInfo e) {
            LOGGER.info("Error while invoking enquire payment : " + e);
            updateApplication(balanceTransfer);
        }
        if (response != null) {
            if (isErrorScenario(response)) {
                LOGGER.info("External Service Error while invoking enquire payment : Reason Code " + response.getResponseHeader().getResultCondition().getReasonCode());
                updateApplication(balanceTransfer);
            } else {
                updateResponse(response, balanceTransfer);
            }
        }
        LOGGER.info("Exiting AVS enquirePaymentInstruction");
    }

    private void updateResponse(EnquirePaymentInstructionFacilityDetailsResponse response, BalanceTransfer balanceTransfer) {
        List<Condition> conditions = response.getPaymentInstruction().getSourceArrangement().getHasObjectConditions();
        String fpsEnabled = null;
        for (Condition condition : conditions) {
            if (condition instanceof RuleCondition) {
                RuleCondition ruleCondition = (RuleCondition) condition;
                if (FAST_PAYMENT_INDICATOR.equalsIgnoreCase(ruleCondition.getName())) {
                    fpsEnabled = ruleCondition.getResult();
                    break;
                }
            }
        }
        if (!FASTER_PAYMENT.equalsIgnoreCase(fpsEnabled)) {
            balanceTransfer.setStatus(FAST_PAYMENT_FAIL);
        }
    }

    private void updateApplication(BalanceTransfer balanceTransfer) {
        balanceTransfer.setStatus(FAST_PAYMENT_SYSTEM_ERROR);
    }

    private boolean isErrorScenario(EnquirePaymentInstructionFacilityDetailsResponse response) {
        if (response.getResponseHeader() != null && response.getResponseHeader().getResultCondition() != null && response.getResponseHeader().getResultCondition().getReasonCode() != null) {
            if (response.getResponseHeader().getResultCondition().getReasonCode() > 0) {
                return true;
            }
        }
        return false;
    }
}
