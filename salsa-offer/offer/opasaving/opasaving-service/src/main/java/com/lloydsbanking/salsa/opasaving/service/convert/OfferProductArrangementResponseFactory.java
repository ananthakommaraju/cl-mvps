package com.lloydsbanking.salsa.opasaving.service.convert;


import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.service.CreatePamService;
import com.lloydsbanking.salsa.downstream.pam.service.CreateParentArrangementService;
import com.lloydsbanking.salsa.offer.ApplicantType;
import com.lloydsbanking.salsa.offer.OfferException;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class OfferProductArrangementResponseFactory {
    private static final Logger LOGGER = Logger.getLogger(OfferProductArrangementResponseFactory.class);

    @Autowired
    CreateParentArrangementService parentArrangementService;

    @Autowired
    CreatePamService createPamService;

    public void setOfferProductArrangementResponse(DepositArrangement requestProductArrangement, ProductArrangement responseProductArrangement, boolean isDuplicateDecline) {
        DepositArrangement responseDepositArrangement = new DepositArrangement();
        if (responseProductArrangement instanceof DepositArrangement) {
            responseDepositArrangement = (DepositArrangement) responseProductArrangement;
        }
        responseDepositArrangement.setPrimaryInvolvedParty(new Customer());
        if (!CollectionUtils.isEmpty(requestProductArrangement.getPrimaryInvolvedParty().getCustomerScore())) {
            responseDepositArrangement.getPrimaryInvolvedParty().getCustomerScore().addAll(requestProductArrangement.getPrimaryInvolvedParty().getCustomerScore());
        }
        if (null != requestProductArrangement.getPrimaryInvolvedParty().getAuditData() && !requestProductArrangement.getPrimaryInvolvedParty().getAuditData().isEmpty()) {
            responseDepositArrangement.getPrimaryInvolvedParty().getAuditData().addAll(requestProductArrangement.getPrimaryInvolvedParty().getAuditData());
        }
        responseDepositArrangement.setApplicationStatus(requestProductArrangement.getApplicationStatus());
        responseDepositArrangement.setArrangementType(requestProductArrangement.getArrangementType());
        responseDepositArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(requestProductArrangement.getPrimaryInvolvedParty().getCustomerIdentifier());
        responseDepositArrangement.getPrimaryInvolvedParty().setNewCustomerIndicator(requestProductArrangement.getPrimaryInvolvedParty().isNewCustomerIndicator());
        responseDepositArrangement.getPrimaryInvolvedParty().setIsAuthCustomer(requestProductArrangement.getPrimaryInvolvedParty().isIsAuthCustomer());
        responseDepositArrangement.getPrimaryInvolvedParty().setCustomerSegment(requestProductArrangement.getPrimaryInvolvedParty().getCustomerSegment());
        responseDepositArrangement.getPrimaryInvolvedParty().setCidPersID(requestProductArrangement.getPrimaryInvolvedParty().getCidPersID());
        responseDepositArrangement.setApplicationType(requestProductArrangement.getApplicationType());
        responseDepositArrangement.setArrangementId(requestProductArrangement.getArrangementId());
        responseDepositArrangement.getPrimaryInvolvedParty().setIndividualIdentifier(requestProductArrangement.getPrimaryInvolvedParty().getIndividualIdentifier());
        if (!ApplicationStatus.DECLINED.getValue().equals(requestProductArrangement.getApplicationStatus()) && !isDuplicateDecline) {
            responseDepositArrangement.getOfferedProducts().addAll(requestProductArrangement.getOfferedProducts());
            responseDepositArrangement.getExistingProducts().addAll(requestProductArrangement.getExistingProducts());
        }
        responseDepositArrangement.setAssociatedProduct(new Product());
        responseDepositArrangement.getAssociatedProduct().setEligibilityDetails(requestProductArrangement.getAssociatedProduct().getEligibilityDetails());
        setAffiliateDetailsInResponse(requestProductArrangement.getAffiliatedetails(), responseDepositArrangement);
        if (!CollectionUtils.isEmpty(requestProductArrangement.getAssociatedProduct().getProductPreferentialRate())) {
            responseDepositArrangement.getAssociatedProduct().getProductPreferentialRate().add(new PreferentialRate());
            responseDepositArrangement.getAssociatedProduct()
                    .getProductPreferentialRate()
                    .get(0)
                    .setPreferentialRateIdentifier(requestProductArrangement.getAssociatedProduct().getProductPreferentialRate().get(0).getPreferentialRateIdentifier());
        }
        if (isDuplicateDecline) {
            responseDepositArrangement.setAssociatedProduct(requestProductArrangement.getAssociatedProduct());
            responseDepositArrangement.getPrimaryInvolvedParty().getCustomerScore().clear();
        }
        for (Product offeredProduct : responseDepositArrangement.getOfferedProducts()) {
            offeredProduct.setProductType(null);
        }
        responseDepositArrangement.getPrimaryInvolvedParty().setCbsCustomerNumber(requestProductArrangement.getPrimaryInvolvedParty().getCbsCustomerNumber());
    }

    private void setAffiliateDetailsInResponse(List<AffiliateDetails> affiliateDetailsList, DepositArrangement responseDepositArrangement) {
        if (!CollectionUtils.isEmpty(affiliateDetailsList) && !StringUtils.isEmpty(affiliateDetailsList.get(0).getAffiliateIdentifier())) {
            responseDepositArrangement.getAffiliatedetails().addAll(affiliateDetailsList);
        }
    }

    public void offerProductArrangementForCrossSell(ProductArrangement requestDepositArrangement, OfferProductArrangementResponse response) throws OfferException {
        LOGGER.info("Cross Sell, Related Application ID: " + requestDepositArrangement.getRelatedApplicationId());
        if (!StringUtils.isEmpty(requestDepositArrangement.getRelatedApplicationStatus())) {
            requestDepositArrangement.setApplicationStatus(ApplicationStatus.FULFILLED.getValue()
                    .equals(requestDepositArrangement.getRelatedApplicationStatus()) ? ApplicationStatus.APPROVED.getValue() : ApplicationStatus.AWAITING_RELATED_APPLICATION_FULFILMENT
                    .getValue());
        }
        if (!ApplicantType.GUARDIAN.getValue().equals(requestDepositArrangement.getPrimaryInvolvedParty().getApplicantType())) {
            LOGGER.info("Customer is NOT GUARDIAN");
            createPamService.createPendingArrangement(requestDepositArrangement);
            setOfferProductArrangementResponseForCrossSell(requestDepositArrangement, response.getProductArrangement());
        } else {
            LOGGER.info("Customer is GUARDIAN");
            try {
                parentArrangementService.createParentArrangement(requestDepositArrangement);
            } catch (InternalServiceErrorMsg internalServiceErrorMsg) {
                throw new OfferException(internalServiceErrorMsg);
            }
            response.setProductArrangement(requestDepositArrangement);
        }
    }

    public void setOfferProductArrangementResponseForCrossSell(ProductArrangement requestProductArrangement, ProductArrangement responseProductArrangement) {
        responseProductArrangement.setAssociatedProduct(new Product());
        responseProductArrangement.setPrimaryInvolvedParty(new Customer());
        responseProductArrangement.setArrangementId(requestProductArrangement.getArrangementId());
        responseProductArrangement.setArrangementType(requestProductArrangement.getArrangementType());
        responseProductArrangement.setApplicationType(requestProductArrangement.getApplicationType());
        responseProductArrangement.setApplicationStatus(requestProductArrangement.getApplicationStatus());
    }

}
