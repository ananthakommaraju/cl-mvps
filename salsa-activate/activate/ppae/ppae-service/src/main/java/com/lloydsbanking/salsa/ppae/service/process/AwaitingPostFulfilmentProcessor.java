package com.lloydsbanking.salsa.ppae.service.process;

import com.lloydsbanking.salsa.activate.communication.downstream.CommunicationManager;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.constants.EmailTemplateEnum;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.activate.downstream.PrdClient;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.ppae.service.appstatus.PpaeInvocationIdentifier;
import com.lloydsbanking.salsa.ppae.service.convert.PrdRequestFactory;
import com.lloydsbanking.salsa.ppae.service.downstream.EnquirePaymentRetriever;
import com.lloydsbanking.salsa.ppae.service.downstream.IssueInPaymentInstRetriever;
import com.lloydsbanking.salsa.ppae.service.downstream.VerifyProductArrangementDetails;
import lib_sim_bo.businessobjects.BalanceTransfer;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_bo.businessobjects.ProductAttributes;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import javax.xml.ws.WebServiceException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AwaitingPostFulfilmentProcessor {
    private static final Logger LOGGER = Logger.getLogger(AwaitingPostFulfilmentProcessor.class);
    private static final String BT_OFF_ATTRIBUTE_CODE = "BT_OFF_1";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String BT_AVS_THRESHOLD = "BT_AVS_THRESHOLD";
    private static final String BT_CA_AVS_THRESHOLD = "BT_CA_AVS_THRESHOLD";

    @Autowired
    PrdClient prdClient;
    @Autowired
    PrdRequestFactory rpcRequestFactory;
    @Autowired
    EnquirePaymentRetriever enquirePaymentRetriever;
    @Autowired
    VerifyProductArrangementDetails verifyProductArrangementDetails;
    @Autowired
    IssueInPaymentInstRetriever issueInPaymentInstRetriever;
    @Autowired
    CommunicationManager communicationManager;
    @Autowired
    LookUpValueRetriever lookUpValueRetriever;

    public void process(ProductArrangement productArrangement, ProcessPendingArrangementEventRequest upStreamRequest, PpaeInvocationIdentifier ppaeInvocationIdentifier) {
        RetrieveProductConditionsResponse retrieveProductConditionsResponse = callRetrieveProductConditions(productArrangement, upStreamRequest);
        String btOffAttributeValue = getBtOffAttributeValue(retrieveProductConditionsResponse);

        if (null != btOffAttributeValue && productArrangement instanceof FinanceServiceArrangement) {
            FinanceServiceArrangement financeServiceArrangement = (FinanceServiceArrangement) productArrangement;

            for (BalanceTransfer balanceTransfer : financeServiceArrangement.getBalanceTransfer()) {
                if (isEnquireRequired(balanceTransfer)) {
                    enquirePaymentRetriever.retrieve(upStreamRequest.getHeader(), balanceTransfer);
                }
                if (isCreditCardNoOrAccNoPresent(financeServiceArrangement.getCreditCardNumber(), balanceTransfer)) {
                    boolean isIssueCallRequired = true;
                    if (isVerifyCallRequired(upStreamRequest.getHeader().getChannelId(), balanceTransfer)) {
                        isIssueCallRequired = verifyProductArrangementDetails.verify(balanceTransfer, financeServiceArrangement.getPrimaryInvolvedParty(), upStreamRequest.getHeader());
                    }
                    if (isIssueCallRequired) {
                        issueInPaymentInstRetriever.invoke(btOffAttributeValue, upStreamRequest.getHeader(), balanceTransfer, financeServiceArrangement.getCreditCardNumber());
                    }
                }
                ppaeInvocationIdentifier.setInvokeModifyProductArrangementFlag(true);
            }
            financeServiceArrangement.setApplicationStatus(ApplicationStatus.FULFILLED.getValue());
            communicationManager.callSendCommunicationService(productArrangement, getNotificationTemplate(financeServiceArrangement.getBalanceTransfer()), upStreamRequest.getHeader(), null, ActivateCommonConstant.CommunicationTypes.EMAIL);
        }
    }

    private RetrieveProductConditionsResponse callRetrieveProductConditions(ProductArrangement productArrangement, ProcessPendingArrangementEventRequest upStreamRequest) {
        RetrieveProductConditionsResponse retrieveProductConditionsResponse = null;
        RetrieveProductConditionsRequest retrieveProductConditionsRequest = rpcRequestFactory.convert(productArrangement, upStreamRequest.getHeader());

        try {
            LOGGER.info("Entering retrieveProductConditions for BT Fulfilment ");
            retrieveProductConditionsResponse = prdClient.retrieveProductConditions(retrieveProductConditionsRequest);
        } catch (RetrieveProductConditionsInternalServiceErrorMsg | RetrieveProductConditionsDataNotAvailableErrorMsg | RetrieveProductConditionsResourceNotAvailableErrorMsg | RetrieveProductConditionsExternalServiceErrorMsg | RetrieveProductConditionsExternalBusinessErrorMsg | WebServiceException ex) {
            LOGGER.error("Error calling RetrieveProductConditions.Catching and moving forward" + ex);
        }
        LOGGER.info("Exiting retrieveProductConditions for BT Fulfilment");
        return retrieveProductConditionsResponse;
    }

    private boolean isCreditCardNoOrAccNoPresent(String sourceCreditCardNumber, BalanceTransfer balanceTransfer) {
        boolean isSortAccNoPresent = balanceTransfer.getCurrentAccountDetails() != null && !StringUtils.isEmpty(balanceTransfer.getCurrentAccountDetails().getAccountNumber()) && !StringUtils.isEmpty(balanceTransfer.getCurrentAccountDetails().getSortCode());
        boolean isCreditCardNoPresent = !StringUtils.isEmpty(balanceTransfer.getCreditCardNumber());
        return !StringUtils.isEmpty(sourceCreditCardNumber) && (isSortAccNoPresent || isCreditCardNoPresent);
    }

    private boolean isVerifyCallRequired(String channelId, BalanceTransfer balanceTransfer) {
        Map<String, String> thresholdValueMap = getBtAvsThresholdValues(channelId);
        boolean isValidCAAvsAmt = balanceTransfer.getAmount().getAmount().compareTo(new BigDecimal(thresholdValueMap.get(BT_CA_AVS_THRESHOLD))) >= 0;
        boolean isValidAvsAmt = balanceTransfer.getAmount().getAmount().compareTo(new BigDecimal(thresholdValueMap.get(BT_AVS_THRESHOLD))) >= 0;
        return isValidAvsAmt || isValidCAAvsAmt;
    }

    private String getBtOffAttributeValue(RetrieveProductConditionsResponse retrieveProductConditionsResponse) {
        if (null != retrieveProductConditionsResponse && !CollectionUtils.isEmpty(retrieveProductConditionsResponse.getProduct()) && !CollectionUtils.isEmpty(retrieveProductConditionsResponse.getProduct().get(0).getProductoffer())) {
            for (ProductAttributes productAttributes : retrieveProductConditionsResponse.getProduct().get(0).getProductoffer().get(0).getProductattributes()) {
                if (BT_OFF_ATTRIBUTE_CODE.equalsIgnoreCase(productAttributes.getAttributeCode())) {
                    return productAttributes.getAttributeValue();
                }
            }
        }
        return null;
    }

    private boolean isEnquireRequired(BalanceTransfer balanceTransfer) {
        boolean isCreditCardNoPresent = !StringUtils.isEmpty(balanceTransfer.getCreditCardNumber());
        boolean isCallFasterPayment = false;
        return isCreditCardNoPresent && isCallFasterPayment;
    }

    private String getNotificationTemplate(List<BalanceTransfer> balanceTransferList) {
        int successCount = 0;
        for (BalanceTransfer balanceTransfer : balanceTransferList) {
            if (STATUS_SUCCESS.equalsIgnoreCase(balanceTransfer.getStatus())) {
                successCount++;
            }
        }
        if (successCount > 0) {
            return successCount == balanceTransferList.size() ? EmailTemplateEnum.BT_FULFILLED_EMAIL.getTemplate() : EmailTemplateEnum.BT_PARTIALLY_FULFILLED_EMAIL.getTemplate();
        } else {
            return EmailTemplateEnum.BT_FAILED_EMAIL.getTemplate();
        }
    }

    private Map<String, String> getBtAvsThresholdValues(String channelId) {
        List<ReferenceDataLookUp> referenceDataLookUpList = null;
        try {
            referenceDataLookUpList = lookUpValueRetriever.getLookUpValues(Arrays.asList(BT_AVS_THRESHOLD, BT_CA_AVS_THRESHOLD), channelId);
        } catch (DataAccessException e) {
            LOGGER.info("Error while retrieving avs threshold value from refLookUp: " + e);
        }
        Map<String, String> thresholdValueMap = new HashMap<>();
        thresholdValueMap.put(BT_AVS_THRESHOLD, "0");
        thresholdValueMap.put(BT_CA_AVS_THRESHOLD, "0");
        if (!CollectionUtils.isEmpty(referenceDataLookUpList)) {
            for (ReferenceDataLookUp refLookUp : referenceDataLookUpList) {
                thresholdValueMap.put(refLookUp.getGroupCode(), refLookUp.getLookupValueDesc());
            }
        }
        return thresholdValueMap;
    }

}

