package com.lloydsbanking.salsa.opaloans.service;

import com.lloydsbanking.salsa.downstream.pam.service.CreatePamService;
import com.lloydsbanking.salsa.downstream.pam.service.RetrievePamService;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.opaloans.logging.OpaloansLogService;
import com.lloydsbanking.salsa.opaloans.service.downstream.EligibilityService;
import com.lloydsbanking.salsa.opaloans.service.downstream.EligibleProductsRetriever;
import com.lloydsbanking.salsa.opaloans.service.identify.IdentifyService;
import com.lloydsbanking.salsa.opaloans.service.utility.ExceptionHelper;
import com.lloydsbanking.salsa.opaloans.service.validate.RequestValidatorAndInitializer;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpaLoansService implements IAOfferProductArrangement {
    private static final Logger LOGGER = Logger.getLogger(OpaLoansService.class);
    private static final String CURRENT_ADDRESS = "CURRENT";
    private static final String UNDEFINED = "U";
    private static final String GENDER_CODE_UNDEFINED = "000";
    private static final String INELIGIBLE_CUSTOMER_REASON_CODE_CR046 = "CR046";
    private static final String INELIGIBLE_CUSTOMER_REASON_CODE_CR047 = "CR047";
    private static final String RULE_CONDITION_NAME = "ELIGIBILITY";
    private static final String CUSTOMER_IS_NOT_ELIGIBLE = "N";
    private static final String CUSTOMER_IS_ELIGIBLE = "Y";
    private static final String ADDITIONAL_DATA_REQUIRED_CONDITION_NAME = "ADDITIONAL_DATA_REQUIRED_INDICATOR";

    @Autowired
    OpaloansLogService opaloansLogService;
    @Autowired
    RequestValidatorAndInitializer validatorAndInitializer;
    @Autowired
    RequestToResponseHeaderConverter responseHeaderConverter;
    @Autowired
    ExceptionHelper exceptionHelper;
    @Autowired
    EligibleProductsRetriever eligibleProductsRetriever;
    @Autowired
    EligibilityService eligibilityService;
    @Autowired
    IdentifyService identifyService;
    @Autowired
    CreatePamService createPamService;
    @Autowired
    RetrievePamService retrievePamService;

    @Override
    public OfferProductArrangementResponse offerProductArrangement(final OfferProductArrangementRequest request) throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementExternalServiceErrorMsg {
        LOGGER.info("Entering Offer : OPALOANS");
        opaloansLogService.initialiseContext(request.getHeader());
        OfferProductArrangementResponse response = new OfferProductArrangementResponse();
        response.setProductArrangement(new FinanceServiceArrangement());
        FinanceServiceArrangement requestFinanceServiceArrangement = (FinanceServiceArrangement) request.getProductArrangement();

        try {
            validatorAndInitializer.initialiseVariables(request, response);
            List<PostalAddress> postalAddressList = new ArrayList<>();

            if (StringUtils.isEmpty(requestFinanceServiceArrangement.getRelatedApplicationId())) {
                boolean isCustomerExist = false;
                boolean isEligible = false;
                if (!isBfpoAddress(requestFinanceServiceArrangement.getPrimaryInvolvedParty().getPostalAddress())) {
                    isCustomerExist = identifyService.identifyInvolvedPartyDetails(request.getHeader(), requestFinanceServiceArrangement);
                    if (isCustomerExist) {
                        eligibleProductsRetriever.fetchEligibleLoanProducts(request.getHeader(), requestFinanceServiceArrangement);
                        isEligible = eligibilityService.getCustomerEligibilityStatus(request.getHeader(), requestFinanceServiceArrangement);
                    }
                }
                if (!isEligible) {
                    LOGGER.info("Exiting Offer : OPALOANS");
                    setOfferProductArrangementResponse(requestFinanceServiceArrangement, (FinanceServiceArrangement) response.getProductArrangement(), isCustomerExist, isEligible);
                    return response;
                }
                postalAddressList.addAll(requestFinanceServiceArrangement.getPrimaryInvolvedParty().getPostalAddress());
                requestFinanceServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().clear();
            }
            performPAMOperations(requestFinanceServiceArrangement);

            if (!CollectionUtils.isEmpty(postalAddressList)) {
                requestFinanceServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().addAll(postalAddressList);
            }
            setOfferProductArrangementResponse(requestFinanceServiceArrangement, (FinanceServiceArrangement) response.getProductArrangement(), true, true);
            LOGGER.info("Exiting Offer : OPALOANS");
        } catch (OfferException exception) {
            exceptionHelper.setResponseHeaderAndThrowException(exception, responseHeaderConverter.convert(request.getHeader()));
        }
        return response;
    }

    private void performPAMOperations(FinanceServiceArrangement requestFinanceServiceArrangement) throws OfferException {
        String brandName = requestFinanceServiceArrangement.getAssociatedProduct().getBrandName();
        createPamService.createPendingArrangement(requestFinanceServiceArrangement);
        ProductArrangement productArrangement = null;
        try {
            productArrangement = retrievePamService.retrievePendingArrangement(brandName, requestFinanceServiceArrangement.getArrangementId(), null);
        } catch (InternalServiceErrorMsg | DataNotAvailableErrorMsg | ResourceNotAvailableErrorMsg errorMsg) {
            throw new OfferException(errorMsg);
        }
        if (null != productArrangement && !CollectionUtils.isEmpty(productArrangement.getAffiliatedetails())) {
            requestFinanceServiceArrangement.getAffiliatedetails().clear();
            requestFinanceServiceArrangement.getAffiliatedetails().addAll(productArrangement.getAffiliatedetails());
        }
    }

    private void setOfferProductArrangementResponse(FinanceServiceArrangement requestFinanceServiceArrangement, FinanceServiceArrangement responseFinanceServiceArrangement, boolean isCustomerExist, boolean isEligible) {
        responseFinanceServiceArrangement.setPrimaryInvolvedParty(new Customer());
        responseFinanceServiceArrangement.setAssociatedProduct(new Product());
        responseFinanceServiceArrangement.setApplicationType(requestFinanceServiceArrangement.getApplicationType());
        responseFinanceServiceArrangement.setArrangementType(requestFinanceServiceArrangement.getArrangementType());
        responseFinanceServiceArrangement.getOfferedProducts().addAll(requestFinanceServiceArrangement.getOfferedProducts());
        responseFinanceServiceArrangement.getExistingProducts().addAll(requestFinanceServiceArrangement.getExistingProducts());
        responseFinanceServiceArrangement.setReasonCode(requestFinanceServiceArrangement.getReasonCode());
        if (isCustomerExist && StringUtils.isEmpty(requestFinanceServiceArrangement.getRelatedApplicationId())) {
            responseFinanceServiceArrangement.setPrimaryInvolvedParty(requestFinanceServiceArrangement.getPrimaryInvolvedParty());
        }
        if (!isEligible) {
            responseFinanceServiceArrangement.getPrimaryInvolvedParty().setUserType(null);
            responseFinanceServiceArrangement.getPrimaryInvolvedParty().setPartyRole(null);
            responseFinanceServiceArrangement.getPrimaryInvolvedParty().setInternalUserIdentifier(null);
            if (!(requestFinanceServiceArrangement.getConditions().isEmpty()) && ADDITIONAL_DATA_REQUIRED_CONDITION_NAME.equals(requestFinanceServiceArrangement.getConditions().get(0).getName())) {
                responseFinanceServiceArrangement.getConditions().add(requestFinanceServiceArrangement.getConditions().get(0));
            } else {
                responseFinanceServiceArrangement.getConditions().add(getRuleCondition(RULE_CONDITION_NAME, CUSTOMER_IS_NOT_ELIGIBLE));
                if (!isValidReasonCode(requestFinanceServiceArrangement.getReasonCode())) {
                    responseFinanceServiceArrangement.getOfferedProducts().clear();
                    responseFinanceServiceArrangement.getExistingProducts().clear();
                }
            }
        } else {
            responseFinanceServiceArrangement.setArrangementId(requestFinanceServiceArrangement.getArrangementId());
            responseFinanceServiceArrangement.getAffiliatedetails().addAll(requestFinanceServiceArrangement.getAffiliatedetails());
            if (StringUtils.isEmpty(requestFinanceServiceArrangement.getRelatedApplicationId())) {
                responseFinanceServiceArrangement.getConditions().add(getRuleCondition(RULE_CONDITION_NAME, CUSTOMER_IS_ELIGIBLE));
            }
            if (isGenderUndefined(responseFinanceServiceArrangement.getPrimaryInvolvedParty().getIsPlayedBy())) {
                responseFinanceServiceArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setGender(GENDER_CODE_UNDEFINED);
            }
            responseFinanceServiceArrangement.getPrimaryInvolvedParty().setIndividualIdentifier(null);
            responseFinanceServiceArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(requestFinanceServiceArrangement.getPrimaryInvolvedParty().getCustomerIdentifier());
        }
    }

    private RuleCondition getRuleCondition(final String name, final String result) {
        RuleCondition condition = new RuleCondition();
        condition.setName(name);
        condition.setResult(result);
        return condition;
    }

    private boolean isGenderUndefined(final Individual isPlayedBy) {
        return null != isPlayedBy && UNDEFINED.equalsIgnoreCase(isPlayedBy.getGender());
    }

    private boolean isBfpoAddress(final List<PostalAddress> postalAddressList) {
        if (postalAddressList != null) {
            for (PostalAddress postalAddress : postalAddressList) {
                if (CURRENT_ADDRESS.equalsIgnoreCase(postalAddress.getStatusCode()) && null != postalAddress.isIsBFPOAddress() && postalAddress.isIsBFPOAddress()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isValidReasonCode(final ReasonCode reasonCode) {
        if (null != reasonCode) {
            return INELIGIBLE_CUSTOMER_REASON_CODE_CR046.equalsIgnoreCase(reasonCode.getCode()) || INELIGIBLE_CUSTOMER_REASON_CODE_CR047.equalsIgnoreCase(reasonCode.getCode());
        }
        return false;
    }
}