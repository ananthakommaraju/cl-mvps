package com.lloydsbanking.salsa.offer.pam.service;


import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.pam.service.RetrievePamService;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class DuplicateApplicationCheckService {
    private static final Logger LOGGER = Logger.getLogger(DuplicateApplicationCheckService.class);

    private static final int DAYS_FOR_DUPLICATE_APPLICATION_CHK = 30;
    private static final String CREDIT_CARD_DUPLICATE_SWITCH = "CREDIT_CARD_DUPLICATE_SWITCH";
    private static final String DUPLICATE_APPLICATION_ASM_DECLINE_ERROR_CODE = "829002";
    private static final String DUPLICATE_APPLICATION_ASM_DECLINE_ERROR_DESC = "Duplicate Application with ASM Decline";
    private static final String DUPLICATE_APPLICATION_ERROR_CODE = "829001";
    private static final String DUPLICATE_APPLICATION_ERROR_DESC = "Duplicate Application";

    @Autowired
    DateFactory dateFactory;
    @Autowired(required = false)
    RetrievePamService retrievePamService;

    @Transactional
    public boolean checkDuplicateApplication(ProductArrangement reqProductArrangement, String channelId) {
        String cidPersId = reqProductArrangement.getPrimaryInvolvedParty().getCidPersID();
        String ocisId = reqProductArrangement.getPrimaryInvolvedParty().getCustomerIdentifier();
        LOGGER.info("Entering retrieveArrangementsForCustomer (To check duplicate application from the customer) with Customer ID | CidPersID ; " + ocisId + " | " + cidPersId);
        if (cidPersId != null && ocisId != null) {
            List<ProductArrangement> productArrangementList = null;
            try {
                productArrangementList = retrievePamService.retrieveArrangementForCustomer(channelId, ocisId);
            } catch (InternalServiceErrorMsg | DataNotAvailableErrorMsg | ResourceNotAvailableErrorMsg errorMsg) {
                LOGGER.info("Exception occurred while calling retrieveArrangementsForCustomer. Consuming this exception." + errorMsg);
            }

            if (!CollectionUtils.isEmpty(productArrangementList)) {
                LOGGER.info("Exiting retrieveArrangementsForCustomer (Product Arrangement Fetched for duplicate application check)");
                List<String> applicationStatusList = new ArrayList<>();
                applicationStatusList.add(ApplicationStatus.ABANDONED.getValue());
                applicationStatusList.add(ApplicationStatus.FULFILLED.getValue());
                applicationStatusList.add(ApplicationStatus.DECLINED.getValue());

                if (ArrangementType.CREDITCARD.getValue().equalsIgnoreCase(reqProductArrangement.getArrangementType())) {
                    return isDuplicateApplicationForCC(reqProductArrangement.getConditions(), reqProductArrangement, channelId, productArrangementList, applicationStatusList);
                } else {
                    return isDuplicateApplicationForOtherProducts(reqProductArrangement, channelId, productArrangementList, applicationStatusList);
                }
            }
        }
        return false;
    }

    private boolean isDuplicateApplicationForOtherProducts(ProductArrangement reqProductArrangement, String channelId, List<ProductArrangement> productArrangementList, List<String> applicationStatusList) {
        applicationStatusList.add(ApplicationStatus.CANCELLED.getValue());
        for (ProductArrangement productArrangement : productArrangementList) {
            if (isDuplicateApplication(channelId, reqProductArrangement.getArrangementType(), applicationStatusList, productArrangement)) {
                LOGGER.info("Duplicate Application Found");
                setProductInRequest(reqProductArrangement, DUPLICATE_APPLICATION_ERROR_CODE, DUPLICATE_APPLICATION_ERROR_DESC);
                return true;
            }
        }
        return false;
    }

    private boolean isDuplicateApplicationForCC(ProductArrangement reqProductArrangement, String channelId, List<ProductArrangement> productArrangementList, List<String> applicationStatusList) {
        for (ProductArrangement productArrangement : productArrangementList) {
            if (isDuplicateApplicationWithASMDecline(channelId, productArrangement)) {
                LOGGER.info("Duplicate Application with ASM Decline Found");
                setProductInRequest(reqProductArrangement, DUPLICATE_APPLICATION_ASM_DECLINE_ERROR_CODE, DUPLICATE_APPLICATION_ASM_DECLINE_ERROR_DESC);
                return true;
            } else if (isDuplicateApplication(channelId, reqProductArrangement.getArrangementType(), applicationStatusList, productArrangement)) {
                LOGGER.info("Duplicate Application Found");
                setProductInRequest(reqProductArrangement, DUPLICATE_APPLICATION_ERROR_CODE, DUPLICATE_APPLICATION_ERROR_DESC);
                return true;
            }
        }
        return false;
    }

    private void setProductInRequest(ProductArrangement reqProductArrangement, String errorCode, String errorDesc) {
        ProductEligibilityDetails productEligibilityDetails = new ProductEligibilityDetails();
        productEligibilityDetails.getDeclineReasons().add(getReasonCode(errorCode, errorDesc));
        Product product = new Product();
        product.setEligibilityDetails(productEligibilityDetails);
        reqProductArrangement.setAssociatedProduct(product);
    }

    private boolean isDuplicateApplicationWithASMDecline(String channelId, ProductArrangement productArrangement) {
        return ApplicationStatus.DECLINED.getValue().equals(productArrangement.getApplicationStatus()) &&
                channelId.equals(productArrangement.getAssociatedProduct().getBrandName()) &&
                isOlderThanDecisionDate(productArrangement.getLastModifiedDate(), DAYS_FOR_DUPLICATE_APPLICATION_CHK);
    }

    private ReasonCode getReasonCode(String code, String reasonDesc) {
        ReasonCode reasonCode = new ReasonCode();
        reasonCode.setCode(code);
        reasonCode.setDescription(reasonDesc);
        return reasonCode;
    }

    private boolean isDuplicateApplication(String channelId, String arrangementType, List<String> applicationStatusList, ProductArrangement productArrangement) {
        return (arrangementType.equals(productArrangement.getArrangementType()) &&
                !applicationStatusList.contains(productArrangement.getApplicationStatus()) &&
                channelId.equalsIgnoreCase(productArrangement.getAssociatedProduct().getBrandName()));

    }

    private boolean isOlderThanDecisionDate(XMLGregorianCalendar xmlDate, int days) {
        if (xmlDate != null) {
            return dateFactory.differenceInDays(xmlDate.toGregorianCalendar().getTime(), new Date()) <= days ? true : false;
        }
        return false;
    }

    private boolean isDuplicateApplicationForCC(List<RuleCondition> ruleConditionList, ProductArrangement reqProductArrangement, String channelId, List<ProductArrangement> productArrangementList, List<String> applicationStatusList) {
        boolean creditCardDuplicateSwitch = true;
        if (!CollectionUtils.isEmpty(ruleConditionList)) {
            for (RuleCondition condition : ruleConditionList) {
                if (CREDIT_CARD_DUPLICATE_SWITCH.equalsIgnoreCase(condition.getName()) && "0".equals(condition.getResult())) {
                    creditCardDuplicateSwitch = false;
                }
            }
        }
        if (creditCardDuplicateSwitch) {
            applicationStatusList.add(ApplicationStatus.APPROVED.getValue());
            applicationStatusList.add(ApplicationStatus.REFERRED.getValue());
            applicationStatusList.add(ApplicationStatus.UNSCORED.getValue());
            return isDuplicateApplicationForCC(reqProductArrangement, channelId, productArrangementList, applicationStatusList);
        }
        return false;
    }

}
