package com.lloydsbanking.salsa.ppae.service.appstatus;


import com.lloydsbanking.salsa.activate.communication.downstream.CommunicationManager;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.constants.EmailTemplateEnum;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamService;
import com.lloydsbanking.salsa.ppae.service.constant.PPAEServiceConstant;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProcessAsmAcceptDecision {

    private static final Logger LOGGER = Logger.getLogger(ProcessAsmAcceptDecision.class);

    @Autowired
    CommunicationManager communicationManager;

    @Autowired
    UpdatePamService updatePamService;

    private static final String IS_ACCEPTED = "isAccepted";

    public void checkProductAndStatus(ProductArrangement productArrangement, RequestHeader requestHeader, String productOfferIdentifier, String productIdentifier, PpaeInvocationIdentifier ppaeInvocationIdentifier) {
        if (hasArrangementTypeCCorCA(productArrangement)) {
            assignProduct(productArrangement);
            addProductConditions(productArrangement);
            if (isProductNotMatching(productArrangement, productOfferIdentifier, productIdentifier)) {
                productArrangement.setApplicationStatus(ApplicationStatus.DECLINED.getValue());
                ppaeInvocationIdentifier.setInvokeModifyProductArrangementFlag(true);
                sendCommunicationEmail(productArrangement, requestHeader);
            } else {
                ppaeInvocationIdentifier.setInvokeActivateProductArrangementFlag(true);
                productArrangement.setRetryCount(0);
                ppaeInvocationIdentifier.setInvokeModifyProductArrangementFlag(true);
                if (isPrdOfferDtlsPresent(productArrangement, productOfferIdentifier)) {
                    LOGGER.info("Entering ModifyProductArrangement operation updateAppProductDetails with ProductArrangement " + productArrangement);
                    updatePamService.updateAppProductDetailsInPam(productArrangement.getPrimaryInvolvedParty().getCustomerScore(), getProductOffer(productArrangement, productOfferIdentifier), productArrangement.getArrangementId());
                    LOGGER.info("Exiting ModifyProductArrangement operation updateAppProductDetails with ProductArrangement " + productArrangement);
                }
            }
        } else {
            productArrangement.setRetryCount(0);
            ppaeInvocationIdentifier.setInvokeActivateProductArrangementFlag(true);
        }
    }

    private void addProductConditions(ProductArrangement productArrangement) {
        if ((null != productArrangement && !CollectionUtils.isEmpty(productArrangement.getOfferedProducts()) && null != productArrangement.getOfferedProducts().get(0).getProductoptions()) && productArrangement.getArrangementType().equalsIgnoreCase(ArrangementType.CURRENT_ACCOUNT.getValue())) {
            int productOptionSize = productArrangement.getOfferedProducts().get(0).getProductoptions().size();
            for (int index = productOptionSize - 1; index >= 0; index--) {
                ProductOptions productOptions = productArrangement.getOfferedProducts().get(0).getProductoptions().get(index);
                if (checkOptionCode(productOptions)) {
                    addConditions(productArrangement, productOptions.getOptionsCode(), productOptions.getOptionsValue());
                }
            }
        }
    }

    private List getProductOffer(ProductArrangement productArrangement, String productOfferIdentifier) {
        ArrayList<ProductOffer> productOfferList = new ArrayList<>();
        for (Product product : productArrangement.getOfferedProducts()) {
            if (product.getProductoffer().get(0).getProdOfferIdentifier().equalsIgnoreCase(productOfferIdentifier)) {
                productOfferList.add(product.getProductoffer().get(0));
            }
        }
        return productOfferList;
    }


    private boolean hasArrangementTypeCCorCA(ProductArrangement productArrangement) {
        return (productArrangement.getArrangementType().equalsIgnoreCase(ArrangementType.CREDITCARD.getValue()) || productArrangement.getArrangementType().equalsIgnoreCase(ArrangementType.CURRENT_ACCOUNT.getValue()));
    }

    private void assignProduct(ProductArrangement productArrangement) {
        if (null != productArrangement.getOfferedProducts() && !productArrangement.getOfferedProducts().isEmpty()) {
            for (Product offerProduct : productArrangement.getOfferedProducts()) {
                if (null != offerProduct.getStatusCode() && (IS_ACCEPTED).equalsIgnoreCase(offerProduct.getStatusCode())) {
                    productArrangement.getAssociatedProduct().getProductoffer().clear();
                    productArrangement.getAssociatedProduct().getProductoffer().add(offerProduct.getProductoffer().get(0));
                    break;
                }
            }
        }
    }

    private void sendCommunicationEmail(ProductArrangement productArrangement, RequestHeader requestHeader) {
        if (productArrangement.getArrangementType().equalsIgnoreCase(ArrangementType.CREDITCARD.getValue())) {
            communicationManager.callSendCommunicationService(productArrangement, EmailTemplateEnum.BANK_DECLINE_EMAIL.getTemplate(), requestHeader, null, ActivateCommonConstant.CommunicationTypes.EMAIL);
        } else {
            communicationManager.callSendCommunicationService(productArrangement, EmailTemplateEnum.CA_DECLINE_BANK_MSG.getTemplate(), requestHeader, null, ActivateCommonConstant.CommunicationTypes.EMAIL);
        }

    }

    private boolean checkOptionCode(ProductOptions productOptions) {
        boolean isCreditCardOfferedFlagFeet = productOptions.getOptionsCode().equalsIgnoreCase(PPAEServiceConstant.CREDIT_CARD_OFFERED_FLAG_FEAT);
        boolean isDebitCardOrOverDraftRiskCode = productOptions.getOptionsCode().equalsIgnoreCase(PPAEServiceConstant.DEBIT_CARD_RISK_CODE) || productOptions.getOptionsCode().equalsIgnoreCase(PPAEServiceConstant.OVERDRAFT_RISK_CODE);
        return isCreditCardOfferedFlagFeet || isDebitCardOrOverDraftRiskCode;
    }

    private void addConditions(ProductArrangement productArrangement, String optionCode, String optionValue) {
        if (productArrangement instanceof DepositArrangement) {
            DepositArrangement depositArrangement = (DepositArrangement) productArrangement;
            List newConditions = new ArrayList<RuleCondition>();
            for (RuleCondition condition : depositArrangement.getConditions()) {
                newConditions.add(condition);
            }
            if (PPAEServiceConstant.DEBIT_CARD_RISK_CODE.equalsIgnoreCase(optionCode)) {
                RuleCondition ruleCondition = new RuleCondition();
                ruleCondition.setName(PPAEServiceConstant.DEBIT_CARD_RISK_CODE.toString());
                ruleCondition.setResult(optionValue);
                newConditions.add(ruleCondition);
            } else if (PPAEServiceConstant.CREDIT_CARD_OFFERED_FLAG_FEAT.equalsIgnoreCase(optionCode)) {
                RuleCondition ruleCondition = new RuleCondition();
                ruleCondition.setName(PPAEServiceConstant.CREDIT_CARD_LIMIT_AMOUNT_FEAT.toString());
                ruleCondition.setResult(optionValue);
                depositArrangement.getConditions().add(ruleCondition);
                newConditions.add(ruleCondition);
            }
            depositArrangement.getConditions().addAll(newConditions);
        }
    }

    private boolean isProductNotMatching(ProductArrangement productArrangement, String productOfferIdentifier, String productIdentifier) {
        boolean productNotMatchingFlag = false;
        boolean currentAccIdentifierFlag = false;
        if (null != productArrangement && null != productArrangement.getOfferedProducts()) {
            for (Product product : productArrangement.getOfferedProducts()) {
                if (productArrangement.getArrangementType().equals(ArrangementType.CREDITCARD.getValue())) {
                    if (!product.getProductoffer().get(0).getProdOfferIdentifier().equalsIgnoreCase(productOfferIdentifier)) {
                        productNotMatchingFlag = true;
                    }
                } else if (product.getProductIdentifier().equalsIgnoreCase(productIdentifier)) {
                    currentAccIdentifierFlag = true;
                }
            }
        }
        if (null != productArrangement && productArrangement.getArrangementType().equalsIgnoreCase(ArrangementType.CURRENT_ACCOUNT.getValue())) {
            if (currentAccIdentifierFlag) {
                productNotMatchingFlag = false;
            } else {
                productNotMatchingFlag = true;
            }
        }
        return productNotMatchingFlag;
    }

    public boolean isPrdOfferDtlsPresent(ProductArrangement productArrangement, String productOfferIdentifier) {
        boolean hasOfferDetails = false;
        if (null != productArrangement && productArrangement.getArrangementType().equals(ArrangementType.CREDITCARD.getValue())) {
            if (null != productArrangement.getOfferedProducts().get(0).getProductoffer() && !productArrangement.getOfferedProducts().get(0).getProductoffer().isEmpty()) {
                for (Product product : productArrangement.getOfferedProducts()) {
                    if (product.getProductoffer().get(0).getProdOfferIdentifier().equalsIgnoreCase(productOfferIdentifier)) {
                        hasOfferDetails = true;
                    }
                }
            }
        }
        return hasOfferDetails;
    }
}
