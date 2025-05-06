package com.lloydsbanking.salsa.ppae.service.appstatus;


import com.lloydsbanking.salsa.activate.downstream.PrdClient;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.logging.application.ProductTraceLog;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.apply.ApplyService;
import com.lloydsbanking.salsa.offer.apply.downstream.ApplyServiceCC;
import com.lloydsbanking.salsa.ppae.service.constant.PPAEServiceConstant;
import com.lloydsbanking.salsa.ppae.service.convert.PrdRequestFactory;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.ws.WebServiceException;
import java.util.List;

@Component
public class AwaitingRescoreProcessor {

    private static final Logger LOGGER = Logger.getLogger(AwaitingRescoreProcessor.class);
    private static final String CURRENT_ADDRESS = "CURRENT";

    @Autowired
    PrdClient prdClient;
    @Autowired
    PrdRequestFactory prdRequestFactory;
    @Autowired
    ApplyService applyService;
    @Autowired
    ApplyServiceCC applyServiceCC;
    @Autowired
    CreditRatingScoreDecisionEvaluator creditRatingScoreDecisionEvaluator;
    @Autowired
    ProductTraceLog productTraceLog;

    public void retrieveProductDetailsAndCreditRating(ProductArrangement productArrangement, RequestHeader requestHeader, PpaeInvocationIdentifier ppaeInvocationIdentifier) {
        String productOfferIdentifier = null;
        RetrieveProductConditionsRequest retrieveProductConditionsRequest = prdRequestFactory.convert(productArrangement, requestHeader);
        RetrieveProductConditionsResponse retrieveProductConditionsResponse = null;
        try {
            LOGGER.info("Entering retrieveProductConditions");
            retrieveProductConditionsResponse = prdClient.retrieveProductConditions(retrieveProductConditionsRequest);
        } catch (RetrieveProductConditionsInternalServiceErrorMsg | RetrieveProductConditionsDataNotAvailableErrorMsg | RetrieveProductConditionsResourceNotAvailableErrorMsg | RetrieveProductConditionsExternalServiceErrorMsg | RetrieveProductConditionsExternalBusinessErrorMsg | WebServiceException ex) {
            LOGGER.info("Error calling RetrieveProductConditions.Catching and moving forward" + ex);
        }
        LOGGER.info("Exiting retrieveProductConditions");
        mapPrdResponseToProductArrangement(productArrangement, retrieveProductConditionsResponse);
        if (productArrangement.getArrangementType().equals(ArrangementType.CREDITCARD.getValue())) {
            productOfferIdentifier = productArrangement.getAssociatedProduct().getProductoffer().get(0).getProdOfferIdentifier();
        }

        String productIdentifier = productArrangement.getAssociatedProduct().getProductIdentifier();
        LOGGER.info("Entering offer apply service with productId: " + productIdentifier);
        checkCreditRatingScale(productArrangement, requestHeader);
        LOGGER.info(productTraceLog.getProdListTraceEventMessage(productArrangement.getOfferedProducts(), "Exiting offer apply service with "));
        try {
            creditRatingScoreDecisionEvaluator.applyCreditRatingScore(productArrangement, requestHeader, productOfferIdentifier, productIdentifier, ppaeInvocationIdentifier);
        } catch (ActivateProductArrangementDataNotAvailableErrorMsg activateProductArrangementDataNotAvailableErrorMsg) {
            LOGGER.info("Error calling applyCreditRatingScore" + activateProductArrangementDataNotAvailableErrorMsg);
        } catch (ActivateProductArrangementResourceNotAvailableErrorMsg activateProductArrangementResourceNotAvailableErrorMsg) {
            LOGGER.info("Error calling applyCreditRatingScore " + activateProductArrangementResourceNotAvailableErrorMsg);
        }
    }

    private void checkCreditRatingScale(ProductArrangement productArrangement, RequestHeader requestHeader) {
        productArrangement.getPrimaryInvolvedParty().setSourceSystemId(PPAEServiceConstant.SOURCE_SYSTEM_ID);
        //Removing customer score of ASM type as same will be added by Apply credit card
        productArrangement.getPrimaryInvolvedParty().getCustomerScore().remove(1);
        if (productArrangement.getArrangementType().equalsIgnoreCase(ArrangementType.CREDITCARD.getValue())) {

            if (productArrangement instanceof FinanceServiceArrangement) {
                try {
                    LOGGER.info("Entering ApplyCreditRatingScaleForCC with ProductArrangement " + productArrangement);
                    applyServiceCC.applyCreditRatingScaleForCC(null, productArrangement, requestHeader);
                } catch (OfferException e) {
                    LOGGER.error("Error calling Credit Rating from Offer for CC.Catching and moving forward" + e);
                }
            }
        } else {
            if (productArrangement instanceof DepositArrangement) {
                try {
                    boolean isBFPOIndicatorPresent = isBfpoAddress(productArrangement.getPrimaryInvolvedParty().getPostalAddress());
                    LOGGER.info("Entering ApplyCreditRatingScale with ProductArrangement " + productArrangement);
                    applyService.applyCreditRatingScale((DepositArrangement) productArrangement, requestHeader, isBFPOIndicatorPresent, true);
                } catch (OfferException e) {
                    LOGGER.error("Error calling Credit Rating from offer for CA/SA.Catching and moving forward" + e);
                }
            }
        }
    }

    public boolean isBfpoAddress(List<PostalAddress> postalAddressList) {
        if (!CollectionUtils.isEmpty(postalAddressList)) {
            for (PostalAddress postalAddress : postalAddressList) {
                if (CURRENT_ADDRESS.equalsIgnoreCase(postalAddress.getStatusCode()) && null != postalAddress.isIsBFPOAddress() && postalAddress.isIsBFPOAddress()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void mapPrdResponseToProductArrangement(ProductArrangement productArrangement, RetrieveProductConditionsResponse retrieveProductConditionsResponse) {
        if (productArrangement.getArrangementType().equalsIgnoreCase(ArrangementType.CURRENT_ACCOUNT.getValue())) {
            productArrangement.getAssociatedProduct().getExternalSystemProductIdentifier().addAll(retrieveProductConditionsResponse.getProduct().get(0).getExternalSystemProductIdentifier());
        } else {
            productArrangement.getAssociatedProduct().getExternalSystemProductIdentifier().add(setExtSysProdIdentifier(retrieveProductConditionsResponse));
        }
        if (!hasProductPositionIdentifier(productArrangement, retrieveProductConditionsResponse)) {
            productArrangement.getAssociatedProduct().setProductPropositionIdentifier(retrieveProductConditionsResponse.getProduct().get(0).getProductPropositionIdentifier());
        }
    }

    private boolean hasProductPositionIdentifier(ProductArrangement productArrangement, RetrieveProductConditionsResponse retrieveProductConditionsResponse) {
        return (productArrangement.getArrangementType().equalsIgnoreCase(ArrangementType.SAVINGS.getValue()) || null == retrieveProductConditionsResponse.getProduct() || retrieveProductConditionsResponse.getProduct().isEmpty() || hasProductIdentifier(retrieveProductConditionsResponse));
        //Need to add null pointer exception
    }

    private boolean hasProductIdentifier(RetrieveProductConditionsResponse retrieveProductConditionsResponse) {
        return null == retrieveProductConditionsResponse.getProduct().get(0) || null == retrieveProductConditionsResponse.getProduct().get(0).getProductPropositionIdentifier();
    }

    private ExtSysProdIdentifier setExtSysProdIdentifier(RetrieveProductConditionsResponse retrieveProductConditionsResponse) {
        if (null != retrieveProductConditionsResponse && !retrieveProductConditionsResponse.getProduct().isEmpty() && !retrieveProductConditionsResponse.getProduct().get(0).getExternalSystemProductIdentifier().isEmpty()) {
            for (ExtSysProdIdentifier extSysProdIdentifier : retrieveProductConditionsResponse.getProduct().get(0).getExternalSystemProductIdentifier()) {
                if (extSysProdIdentifier.getSystemCode().equalsIgnoreCase(PPAEServiceConstant.SYSTEM_CODE)) {
                    return extSysProdIdentifier;
                }
            }
        }
        return null;
    }

}

