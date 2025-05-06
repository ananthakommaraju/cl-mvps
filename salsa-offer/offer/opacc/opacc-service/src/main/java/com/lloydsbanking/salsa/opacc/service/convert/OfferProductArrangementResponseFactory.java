package com.lloydsbanking.salsa.opacc.service.convert;

import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.offer.ProductEligibilityType;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.Product;
import org.springframework.util.CollectionUtils;

public class OfferProductArrangementResponseFactory {

    public void setOfferProductArrangementResponse(FinanceServiceArrangement requestFinanceServiceArrangement, FinanceServiceArrangement responseFinanceServiceArrangement, boolean isDuplicateDecline) {
        responseFinanceServiceArrangement.setPrimaryInvolvedParty(new Customer());
        if (!CollectionUtils.isEmpty(requestFinanceServiceArrangement.getPrimaryInvolvedParty().getCustomerScore())) {
            responseFinanceServiceArrangement.getPrimaryInvolvedParty().getCustomerScore().addAll(requestFinanceServiceArrangement.getPrimaryInvolvedParty().getCustomerScore());
        }
        if (!CollectionUtils.isEmpty(requestFinanceServiceArrangement.getPrimaryInvolvedParty().getAuditData())) {
            responseFinanceServiceArrangement.getPrimaryInvolvedParty().getAuditData().addAll(requestFinanceServiceArrangement.getPrimaryInvolvedParty().getAuditData());
        }
        responseFinanceServiceArrangement.setArrangementId(requestFinanceServiceArrangement.getArrangementId());
        responseFinanceServiceArrangement.getPrimaryInvolvedParty().setIndividualIdentifier(requestFinanceServiceArrangement.getPrimaryInvolvedParty().getIndividualIdentifier());
        responseFinanceServiceArrangement.setApplicationStatus(requestFinanceServiceArrangement.getApplicationStatus());
        responseFinanceServiceArrangement.setIsDirectDebitRequired(requestFinanceServiceArrangement.isIsDirectDebitRequired());
        responseFinanceServiceArrangement.setArrangementType(requestFinanceServiceArrangement.getArrangementType());
        responseFinanceServiceArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(requestFinanceServiceArrangement.getPrimaryInvolvedParty().getCustomerIdentifier());
        responseFinanceServiceArrangement.getPrimaryInvolvedParty().setNewCustomerIndicator(requestFinanceServiceArrangement.getPrimaryInvolvedParty().isNewCustomerIndicator());
        responseFinanceServiceArrangement.getPrimaryInvolvedParty().setIsAuthCustomer(requestFinanceServiceArrangement.getPrimaryInvolvedParty().isIsAuthCustomer());
        responseFinanceServiceArrangement.getPrimaryInvolvedParty().setCustomerSegment(requestFinanceServiceArrangement.getPrimaryInvolvedParty().getCustomerSegment());
        responseFinanceServiceArrangement.getPrimaryInvolvedParty().setCidPersID(requestFinanceServiceArrangement.getPrimaryInvolvedParty().getCidPersID());
        responseFinanceServiceArrangement.setApplicationType(requestFinanceServiceArrangement.getApplicationType());
        responseFinanceServiceArrangement.setAssociatedProduct(new Product());
        if (!ApplicationStatus.DECLINED.getValue().equals(requestFinanceServiceArrangement.getApplicationStatus()) && !isDuplicateDecline) {
            responseFinanceServiceArrangement.getOfferedProducts().addAll(requestFinanceServiceArrangement.getOfferedProducts());
            responseFinanceServiceArrangement.getExistingProducts().addAll(requestFinanceServiceArrangement.getExistingProducts());
        }
        if (isDuplicateDecline) {
            responseFinanceServiceArrangement.setAssociatedProduct(requestFinanceServiceArrangement.getAssociatedProduct());
            responseFinanceServiceArrangement.getPrimaryInvolvedParty().getCustomerScore().clear();
        }
        if (ProductEligibilityType.INELIGIBLE.getValue().equals(responseFinanceServiceArrangement.getApplicationType())) {
            responseFinanceServiceArrangement.getPrimaryInvolvedParty().getCustomerScore().clear();
        }
        for (Product offeredProduct : responseFinanceServiceArrangement.getOfferedProducts()) {
            offeredProduct.setProductType(null);
        }
        responseFinanceServiceArrangement.getPrimaryInvolvedParty().setCbsCustomerNumber(requestFinanceServiceArrangement.getPrimaryInvolvedParty().getCbsCustomerNumber());
    }

    public void setOfferProductArrangementResponseForCrossSell(FinanceServiceArrangement requestFinanceServiceArrangement, FinanceServiceArrangement responseFinanceServiceArrangement) {
        responseFinanceServiceArrangement.setArrangementType(requestFinanceServiceArrangement.getArrangementType());
        responseFinanceServiceArrangement.setArrangementId(requestFinanceServiceArrangement.getArrangementId());
        responseFinanceServiceArrangement.setApplicationStatus(requestFinanceServiceArrangement.getApplicationStatus());
        responseFinanceServiceArrangement.setAssociatedProduct(new Product());
        responseFinanceServiceArrangement.setPrimaryInvolvedParty(new Customer());
        responseFinanceServiceArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(requestFinanceServiceArrangement.getPrimaryInvolvedParty().getCustomerIdentifier());
        if (!CollectionUtils.isEmpty(requestFinanceServiceArrangement.getPrimaryInvolvedParty().getCustomerScore())) {
            responseFinanceServiceArrangement.getPrimaryInvolvedParty().getCustomerScore().addAll(requestFinanceServiceArrangement.getPrimaryInvolvedParty().getCustomerScore());
        }
        responseFinanceServiceArrangement.setIsDirectDebitRequired(null != requestFinanceServiceArrangement.isIsDirectDebitRequired() ? requestFinanceServiceArrangement.isIsDirectDebitRequired() : false);
        responseFinanceServiceArrangement.getOfferedProducts().addAll(requestFinanceServiceArrangement.getOfferedProducts());
        responseFinanceServiceArrangement.setApplicationType(requestFinanceServiceArrangement.getApplicationType());
    }
}
