package com.lloydsbanking.salsa.offer.apply.downstream;

import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import com.lloydsbanking.salsa.logging.application.ProductFamilyTraceLog;
import com.lloydsbanking.salsa.logging.application.ProductTraceLog;
import com.lloydsbanking.salsa.offer.AsmDecision;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.apply.convert.AsmResponseToProductFamilyConverter;
import com.lloydsbanking.salsa.offer.apply.convert.OfferToRpcRequestConverterForCreditCard;
import com.lloydsbanking.salsa.offer.apply.evaluate.ApplicationStatusEvaluator;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Resp;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.exception.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class ApplyServiceCC {

    private static final Logger LOGGER = Logger.getLogger(ApplyServiceCC.class);
    private static final String DIRECT_DEBIT_REFERRAL_CODE_ACCEPT = "613";
    private static final String DIRECT_DEBIT_REFERRAL_CODE_REFER = "213";
    private static final String REFERRAL_CODE_FOR_UNSCORED = "501";
    private static final int INDEX_OF_CUSTOMER_SCORE_FOR_APPLY = 1;
    @Autowired
    CreditDecisionRetriever creditDecisionRetriever;
    @Autowired
    OfferToRpcRequestConverterForCreditCard offerToRpcRequestConverterForCreditCard;
    @Autowired
    HeaderRetriever headerRetriever;
    @Autowired
    AsmResponseToProductFamilyConverter asmResponseToProductFamilyConverter;
    @Autowired
    RpcRetriever rpcRetriever;
    @Autowired
    ApplicationStatusEvaluator applicationStatusEvaluator;
    @Autowired
    AdministerProductService administerProductService;
    @Autowired
    ProductArrangementTraceLog productArrangementTraceLog;
    @Autowired
    ProductTraceLog productTraceLog;
    @Autowired
    ProductFamilyTraceLog productFamilyTraceLog;

    public void applyCreditRatingScaleForCC(String productEligibilityTypeCode, ProductArrangement productArrangement, RequestHeader requestHeader) throws OfferException {
        LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(productArrangement, "Entering ApplyCreditRatingScale "));
        if (productArrangement instanceof FinanceServiceArrangement && !CollectionUtils.isEmpty(productArrangement.getPrimaryInvolvedParty().getCustomerScore())) {
            FinanceServiceArrangement financeServiceArrangement = (FinanceServiceArrangement) productArrangement;
            CustomerScore customerScoreApply = new CustomerScore();
            financeServiceArrangement.getPrimaryInvolvedParty().getCustomerScore().add(INDEX_OF_CUSTOMER_SCORE_FOR_APPLY, customerScoreApply);
            F424Resp f424Resp = callF424(productEligibilityTypeCode, requestHeader, financeServiceArrangement);
            if (f424Resp != null) {
                String applicationStatus = null;
                try {
                    applicationStatus = applicationStatusEvaluator.getApplicationStatusForAsmCreditDecisionForCC(customerScoreApply, f424Resp, requestHeader);
                    LOGGER.info("Application Status For Asm Credit Decision: " + applicationStatus);
                    financeServiceArrangement.setApplicationStatus(applicationStatus);
                    financeServiceArrangement.setIsDirectDebitRequired(isDirectDebitRequired(customerScoreApply.getReferralCode(), applicationStatus));
                    if (!ApplicationStatus.DECLINED.getValue().equals(financeServiceArrangement.getApplicationStatus())) {
                        LOGGER.info("Application Status is not Declined, Calling RetrieveProductConditions(RPC)");
                        List<ProductFamily> productFamilyList = asmResponseToProductFamilyConverter.creditDecisionResponseToProductFamilyConverter(f424Resp);
                        RetrieveProductConditionsRequest rpcRequest = offerToRpcRequestConverterForCreditCard.convertOfferToRpcRequestForCreditCard(requestHeader, productFamilyList, financeServiceArrangement);
                        RetrieveProductConditionsResponse rpcResponse = callPrd(rpcRequest);
                        financeServiceArrangement.getOfferedProducts().addAll(rpcResponse.getProduct());
                        administerProductService.callAdministerProductSelectionService(financeServiceArrangement, productEligibilityTypeCode);
                        clearOfferAmount(financeServiceArrangement, isOfferAmountDeleted(financeServiceArrangement));
                    }
                } catch (DataNotAvailableErrorMsg | InternalServiceErrorMsg | ResourceNotAvailableErrorMsg errorMsg) {
                    throw new OfferException(errorMsg);
                }
            }
            emptyExistingProductsWhenAppStatusIsDecline(financeServiceArrangement);
        }
    }

    private RetrieveProductConditionsResponse callPrd(RetrieveProductConditionsRequest rpcRequest) throws OfferException {
        LOGGER.info(productTraceLog.getProductTraceEventMessage(rpcRequest.getProduct(), "Entering RetrieveProductConditions "));
        LOGGER.info(productFamilyTraceLog.getProdFamilyListTraceEventMessage(rpcRequest.getProductFamily(), "Entering RetrieveProductConditions "));
        RetrieveProductConditionsResponse rpcResponse;
        try {
            rpcResponse = rpcRetriever.callRpcService(rpcRequest);
        } catch (DataNotAvailableErrorMsg | InternalServiceErrorMsg | ResourceNotAvailableErrorMsg errorMsg) {
            throw new OfferException(errorMsg);
        }
        LOGGER.info(productTraceLog.getProdListTraceEventMessage(rpcResponse.getProduct(), "Exiting RetrieveProductConditions "));
        return rpcResponse;
    }

    private F424Resp callF424(String productEligibilityTypeCode, RequestHeader requestHeader, FinanceServiceArrangement financeServiceArrangement) throws OfferException {
        F424Resp f424Resp;
        try {
            f424Resp = retrieveCreditDecisionForCC(financeServiceArrangement, productEligibilityTypeCode, requestHeader);
        } catch (ExternalBusinessErrorMsg | ResourceNotAvailableErrorMsg | ExternalServiceErrorMsg errorMsg) {
            throw new OfferException(errorMsg);
        }
        return f424Resp;
    }

    private void clearOfferAmount(FinanceServiceArrangement financeServiceArrangement, boolean isOfferAmountDeleted) {
        if (isOfferAmountDeleted) {
            LOGGER.info("Emptying Offered Amount");
            if (financeServiceArrangement.getOfferedProducts() != null && !financeServiceArrangement.getOfferedProducts().isEmpty()) {
                for (Product product : financeServiceArrangement.getOfferedProducts()) {
                    if (product.getProductoffer() != null && !product.getProductoffer().isEmpty()) {
                        for (ProductOffer productOffer : product.getProductoffer()) {
                            productOffer.setOfferAmount(null);
                        }
                    }
                }
            }
        }
    }

    private void emptyExistingProductsWhenAppStatusIsDecline(FinanceServiceArrangement financeServiceArrangement) {
        if (!(ApplicationStatus.APPROVED.getValue().equals(financeServiceArrangement.getApplicationStatus()) || ApplicationStatus.REFERRED.getValue()
                .equals(financeServiceArrangement.getApplicationStatus()))) {
            if (financeServiceArrangement.getExistingProducts() != null && !financeServiceArrangement.getExistingProducts().isEmpty()) {
                LOGGER.info("Emptying Existing Products When Application Status Is Declined");
                financeServiceArrangement.getExistingProducts().clear();
            }
        }
    }

    private boolean isOfferAmountDeleted(FinanceServiceArrangement financeServiceArrangement) {
        boolean deleteOfferedAmount = false;
        if (financeServiceArrangement.getPrimaryInvolvedParty().getCustomerScore() != null && financeServiceArrangement.getPrimaryInvolvedParty()
                .getCustomerScore()
                .get(0)
                .getReferralCode() != null) {
            if (AsmDecision.REFERRED.getValue()
                    .equals(financeServiceArrangement.getPrimaryInvolvedParty().getCustomerScore().get(INDEX_OF_CUSTOMER_SCORE_FOR_APPLY).getScoreResult())) {
                if (financeServiceArrangement.getOfferedProducts() != null && financeServiceArrangement.getPrimaryInvolvedParty()
                        .getCustomerScore()
                        .get(INDEX_OF_CUSTOMER_SCORE_FOR_APPLY)
                        .getReferralCode() != null) {
                    for (ReferralCode referralCode : financeServiceArrangement.getPrimaryInvolvedParty()
                            .getCustomerScore()
                            .get(INDEX_OF_CUSTOMER_SCORE_FOR_APPLY)
                            .getReferralCode()) {
                        if (REFERRAL_CODE_FOR_UNSCORED.equals(referralCode.getCode())) {
                            deleteOfferedAmount = true;
                        }
                    }
                }
            }
        }
        return deleteOfferedAmount;
    }

    private F424Resp retrieveCreditDecisionForCC(FinanceServiceArrangement financeServiceArrangement, String productEligibilityTypeCode, RequestHeader requestHeader) throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg {
        LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(financeServiceArrangement, "Entering RetrieveCreditDecision (ASM F424) "));
        String contactPointId = headerRetriever.getContactPoint(requestHeader).getContactPointId();
        String subChannelCode = financeServiceArrangement.getInitiatedThrough() != null ? financeServiceArrangement.getInitiatedThrough().getSubChannelCode() : null;
        String affiliateIdentifier = !CollectionUtils.isEmpty(financeServiceArrangement.getAffiliatedetails()) ? financeServiceArrangement.getAffiliatedetails()
                .get(0)
                .getAffiliateIdentifier() : null;
        F424Resp f424Resp = null;
        Product associatedProduct = financeServiceArrangement.getAssociatedProduct();
        if (associatedProduct != null) {
            String productIdentifier = !CollectionUtils.isEmpty(associatedProduct.getExternalSystemProductIdentifier()) ? associatedProduct.getExternalSystemProductIdentifier()
                    .get(0)
                    .getProductIdentifier() : null;
            f424Resp = creditDecisionRetriever.retrieveCreditDecision(contactPointId, financeServiceArrangement.getArrangementId(), associatedProduct.getGuaranteedOfferCode(), productIdentifier, productEligibilityTypeCode, subChannelCode, affiliateIdentifier, financeServiceArrangement
                    .getPrimaryInvolvedParty(), financeServiceArrangement.getArrangementType(), financeServiceArrangement.isMarketingPrefereceIndicator(), financeServiceArrangement
                    .getBalanceTransfer(), financeServiceArrangement.getTotalBalanceTransferAmount(), requestHeader);
        }
        LOGGER.info("Exiting RetrieveCreditDecision (ASM F424)");
        return f424Resp;
    }

    private boolean isDirectDebitRequired(List<ReferralCode> referralCodeList, String applicationStatus) {
        boolean directDebitIndicator = false;
        String directDebitReferralCode = null;
        if (ApplicationStatus.APPROVED.getValue().equals(applicationStatus)) {
            directDebitReferralCode = DIRECT_DEBIT_REFERRAL_CODE_ACCEPT;
        } else if (ApplicationStatus.REFERRED.getValue().equals(applicationStatus) || ApplicationStatus.UNSCORED.getValue().equals(applicationStatus)) {
            directDebitReferralCode = DIRECT_DEBIT_REFERRAL_CODE_REFER;
        }
        for (ReferralCode referralCode : referralCodeList) {
            if (referralCode.getCode().equals(directDebitReferralCode)) {
                directDebitIndicator = true;
                break;
            }
        }
        return directDebitIndicator;
    }
}
