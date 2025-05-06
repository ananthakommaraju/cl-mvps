package com.lloydsbanking.salsa.opacc.service;

import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.downstream.pam.service.CreatePamService;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamService;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import com.lloydsbanking.salsa.offer.EIDVStatus;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.ProductEligibilityType;
import com.lloydsbanking.salsa.offer.apply.downstream.ApplyServiceCC;
import com.lloydsbanking.salsa.offer.createinvolvedparty.CreateInvolvedPartyService;
import com.lloydsbanking.salsa.offer.identify.IdentifyService;
import com.lloydsbanking.salsa.offer.pam.service.DuplicateApplicationCheckService;
import com.lloydsbanking.salsa.offer.verify.VerifyInvolvedPartyRoleService;
import com.lloydsbanking.salsa.opacc.logging.OpaccLogService;
import com.lloydsbanking.salsa.opacc.service.convert.OfferProductArrangementResponseFactory;
import com.lloydsbanking.salsa.opacc.service.downstream.EligibilityService;
import com.lloydsbanking.salsa.opacc.service.downstream.EncryptDataService;
import com.lloydsbanking.salsa.opacc.service.utility.ExceptionHelper;
import com.lloydsbanking.salsa.opacc.service.validate.RequestValidatorAndInitializer;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class OpaccService implements IAOfferProductArrangement {
    private static final Logger LOGGER = Logger.getLogger(OpaccService.class);

    @Autowired
    OpaccLogService opaccLogService;
    @Autowired
    IdentifyService identifyService;
    @Autowired
    RequestValidatorAndInitializer validatorAndInitializer;
    @Autowired
    ApplyServiceCC applyServiceCC;
    @Autowired
    EncryptDataService encryptDataService;
    @Autowired
    DuplicateApplicationCheckService duplicateApplicationCheckService;
    @Autowired
    CreatePamService createPamService;
    @Autowired
    EligibilityService eligibilityService;
    @Autowired
    UpdatePamService updatePamService;
    @Autowired
    VerifyInvolvedPartyRoleService verifyInvolvedPartyRoleService;
    @Autowired
    CreateInvolvedPartyService createInvolvedPartyService;
    @Autowired
    ExceptionHelper exceptionHelper;
    @Autowired
    RequestToResponseHeaderConverter responseHeaderConverter;
    @Autowired
    OfferProductArrangementResponseFactory offerProductArrangementResponseFactory;
    @Autowired
    ProductArrangementTraceLog productArrangementTraceLog;

    @Override
    public OfferProductArrangementResponse offerProductArrangement(final OfferProductArrangementRequest request) throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementExternalServiceErrorMsg {
        LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(request.getProductArrangement(), "Entering in Offer "));
        opaccLogService.initialiseContext(request.getHeader());
        OfferProductArrangementResponse response = new OfferProductArrangementResponse();
        response.setProductArrangement(new FinanceServiceArrangement());
        FinanceServiceArrangement requestFinanceServiceArrangement = (FinanceServiceArrangement) request.getProductArrangement();
        RequestHeader requestHeader = request.getHeader();
        try {
            validatorAndInitializer.initialiseVariables(request, response);
            if (isRelatedApplicationIdNotPresent(requestFinanceServiceArrangement)) {
                LOGGER.info("Related Application ID is not present");
                encryptDataService.retrieveEncryptData(requestFinanceServiceArrangement.getPrimaryInvolvedParty().getAccessToken(), requestHeader);
                if (!validatorAndInitializer.isBfpoAddress(requestFinanceServiceArrangement.getPrimaryInvolvedParty().getPostalAddress())) {
                    List<Product> productHoldings = identifyService.identifyInvolvedParty(requestHeader, requestFinanceServiceArrangement.getPrimaryInvolvedParty());
                    if (!CollectionUtils.isEmpty(productHoldings)) {
                        requestFinanceServiceArrangement.getExistingProducts().addAll(productHoldings);
                    }
                }
                if (isDuplicateApplication(requestFinanceServiceArrangement, requestHeader.getChannelId())) {
                    offerProductArrangementResponseFactory.setOfferProductArrangementResponse(requestFinanceServiceArrangement, (FinanceServiceArrangement) response.getProductArrangement(), true);
                    LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(response.getProductArrangement(), "Exit from Offer: Duplicate Application TRUE "));
                    return response;
                }
                String productEligibilityTypeCode = checkCustomerEligibility(requestHeader, requestFinanceServiceArrangement, response);
                if (isCustomerInEligible(requestFinanceServiceArrangement, response) || isEidvStatusDecline(requestFinanceServiceArrangement, response, requestHeader)) {
                    return response;
                }
                createNewCustomerOnOcis(request, requestFinanceServiceArrangement, requestHeader);
                createPamService.createPendingArrangement(requestFinanceServiceArrangement);
                request.getHeader().setArrangementId(requestFinanceServiceArrangement.getArrangementId());
                applyServiceCC.applyCreditRatingScaleForCC(productEligibilityTypeCode, requestFinanceServiceArrangement, requestHeader);

                LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(requestFinanceServiceArrangement, "Entering UpdatePendingArrangement (To update customer score and other application details) "));
                updatePamService.updatePamDetailsForOffer(requestFinanceServiceArrangement);
                LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(requestFinanceServiceArrangement, "Exiting UpdatePendingArrangement (Product Arrangement Details Updated) "));

                offerProductArrangementResponseFactory.setOfferProductArrangementResponse(requestFinanceServiceArrangement, (FinanceServiceArrangement) response.getProductArrangement(), false);
            } else {
                setOfferProductArrangementResponseForCrossSell(requestFinanceServiceArrangement, response, requestHeader);
            }
            LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(request.getProductArrangement(), "Exit from Offer "));
        } catch (OfferException exception) {
            LOGGER.info("Exiting from Offer with exception");
            exceptionHelper.setResponseHeaderAndThrowException(exception, responseHeaderConverter.convert(request.getHeader()));
        }
        return response;
    }

    private boolean isRelatedApplicationIdNotPresent(ProductArrangement requestProductArrangement) {
        return StringUtils.isEmpty(requestProductArrangement.getRelatedApplicationId());
    }

    private boolean isDuplicateApplication(FinanceServiceArrangement requestFinanceServiceArrangement, String channelId) throws OfferProductArrangementDataNotAvailableErrorMsg {
        LOGGER.info("Checking Duplicate Application");
        if (duplicateApplicationCheckService.checkDuplicateApplication(requestFinanceServiceArrangement, channelId)) {
            return true;
        }
        LOGGER.info("Duplicate Application Not Found");
        return false;
    }

    private String checkCustomerEligibility(RequestHeader requestHeader, FinanceServiceArrangement requestFinanceServiceArrangement, OfferProductArrangementResponse response) throws OfferException {
        String productEligibilityTypeCode = null;
        if (validatorAndInitializer.isUnAuthCustomer(requestHeader)) {
            productEligibilityTypeCode = eligibilityService.getProductEligibilityTypeCodeForUnAuthCustomer(requestFinanceServiceArrangement, requestHeader);
        } else {
            productEligibilityTypeCode = eligibilityService.getProductEligibilityTypeCodeForAuthCustomer(requestFinanceServiceArrangement);
        }
        return productEligibilityTypeCode;
    }

    private boolean isCustomerInEligible(FinanceServiceArrangement requestFinanceServiceArrangement, OfferProductArrangementResponse response) {
        if (ProductEligibilityType.INELIGIBLE.getValue().equalsIgnoreCase(requestFinanceServiceArrangement.getApplicationType())) {
            offerProductArrangementResponseFactory.setOfferProductArrangementResponse(requestFinanceServiceArrangement, (FinanceServiceArrangement) response.getProductArrangement(), false);
            LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(response.getProductArrangement(), "Exit from Offer: Eligibility FALSE "));
            return true;
        }
        return false;
    }

    private void createNewCustomerOnOcis(OfferProductArrangementRequest request, FinanceServiceArrangement requestFinanceServiceArrangement, RequestHeader requestHeader) throws OfferException {
        boolean isNewCustomer = null != requestFinanceServiceArrangement.getPrimaryInvolvedParty().isNewCustomerIndicator() ? requestFinanceServiceArrangement.getPrimaryInvolvedParty().isNewCustomerIndicator() : false;
        if (isNewCustomer) {
            LOGGER.info("Calling CreateInvolvedParty for New Customer");
            createInvolvedPartyService.createInvolvedParty(ArrangementType.CREDITCARD.getValue(), false, request.getProductArrangement().getPrimaryInvolvedParty(), requestHeader);
        }
    }

    private boolean isEidvStatusDecline(FinanceServiceArrangement requestFinanceServiceArrangement, OfferProductArrangementResponse response, RequestHeader requestHeader) throws OfferException {
        verifyInvolvedPartyRoleService.verify(requestFinanceServiceArrangement, requestHeader);
        if (EIDVStatus.DECLINE.getValue().equals(requestFinanceServiceArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult())) {
            LOGGER.info("EIDV status is DECLINED");
            requestFinanceServiceArrangement.setApplicationStatus(ApplicationStatus.DECLINED.getValue());
            requestFinanceServiceArrangement.getExistingProducts().clear();
            createPamService.createPendingArrangement(requestFinanceServiceArrangement);
            offerProductArrangementResponseFactory.setOfferProductArrangementResponse(requestFinanceServiceArrangement, (FinanceServiceArrangement) response.getProductArrangement(), false);
            LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(response.getProductArrangement(), "Exit from Offer: Application Type INELIGIBLE "));
            return true;
        }
        return false;
    }

    private void setOfferProductArrangementResponseForCrossSell(FinanceServiceArrangement requestFinanceServiceArrangement, OfferProductArrangementResponse response, RequestHeader requestHeader) throws OfferException {
        LOGGER.info("Cross Sell, Related Application ID: " + requestFinanceServiceArrangement.getRelatedApplicationId());
        createPamService.createPendingArrangement(requestFinanceServiceArrangement);
        requestHeader.setArrangementId(requestFinanceServiceArrangement.getArrangementId());
        applyServiceCC.applyCreditRatingScaleForCC("", requestFinanceServiceArrangement, requestHeader);
        LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(requestFinanceServiceArrangement, "Entering UpdatePendingArrangement (To update customer score and other application details) "));
        updatePamService.updatePamDetailsForOffer(requestFinanceServiceArrangement);
        LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(requestFinanceServiceArrangement, "Exiting UpdatePendingArrangement (Product Arrangement Details Updated) "));
        offerProductArrangementResponseFactory.setOfferProductArrangementResponseForCrossSell(requestFinanceServiceArrangement, (FinanceServiceArrangement) response.getProductArrangement());
    }
}