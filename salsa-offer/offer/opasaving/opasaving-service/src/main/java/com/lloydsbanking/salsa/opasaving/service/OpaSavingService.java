package com.lloydsbanking.salsa.opasaving.service;

import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.downstream.pam.service.CreatePamService;
import com.lloydsbanking.salsa.downstream.pam.service.CreateParentArrangementService;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamService;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import com.lloydsbanking.salsa.offer.ApplicantType;
import com.lloydsbanking.salsa.offer.EIDVStatus;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.apply.ApplyService;
import com.lloydsbanking.salsa.offer.createinvolvedparty.CreateInvolvedPartyService;
import com.lloydsbanking.salsa.offer.eligibility.downstream.EligibilityService;
import com.lloydsbanking.salsa.offer.identify.IdentifyService;
import com.lloydsbanking.salsa.offer.pam.service.DuplicateApplicationCheckService;
import com.lloydsbanking.salsa.offer.verify.VerifyInvolvedPartyRoleService;
import com.lloydsbanking.salsa.opasaving.logging.OpaSavingLogService;
import com.lloydsbanking.salsa.opasaving.service.convert.OfferProductArrangementResponseFactory;
import com.lloydsbanking.salsa.opasaving.service.downstream.RPCService;
import com.lloydsbanking.salsa.opasaving.service.utility.ExceptionHelper;
import com.lloydsbanking.salsa.opasaving.service.validate.RequestValidatorAndInitializer;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
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
public class OpaSavingService implements IAOfferProductArrangement {
    private static final Logger LOGGER = Logger.getLogger(OpaSavingService.class);

    @Autowired
    CreateInvolvedPartyService createInvolvedPartyService;
    @Autowired
    OpaSavingLogService opasavingLogService;
    @Autowired
    ApplyService applyService;
    @Autowired
    CreatePamService createPamService;
    @Autowired
    DuplicateApplicationCheckService duplicateApplicationCheckService;
    @Autowired
    CreateParentArrangementService parentArrangementService;
    @Autowired
    UpdatePamService updatePamService;
    @Autowired
    IdentifyService identifyService;
    @Autowired
    RequestToResponseHeaderConverter responseHeaderConverter;
    @Autowired
    ExceptionHelper exceptionHelper;
    @Autowired
    VerifyInvolvedPartyRoleService verifyInvolvedPartyRoleService;
    @Autowired
    EligibilityService eligibilityService;
    @Autowired
    RequestValidatorAndInitializer validatorAndInitializer;
    @Autowired
    RPCService rpcService;
    @Autowired
    OfferProductArrangementResponseFactory offerProductArrangementResponseFactory;
    @Autowired
    ProductArrangementTraceLog productArrangementTraceLog;

    @Override
    public OfferProductArrangementResponse offerProductArrangement(final OfferProductArrangementRequest request) throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementExternalServiceErrorMsg {
        LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(request.getProductArrangement(), "Entering in Offer "));
        opasavingLogService.initialiseContext(request.getHeader());
        OfferProductArrangementResponse response = new OfferProductArrangementResponse();
        response.setProductArrangement(new DepositArrangement());
        DepositArrangement requestDepositArrangement = (DepositArrangement) request.getProductArrangement();
        RequestHeader requestHeader = request.getHeader();
        try {
            validatorAndInitializer.initialiseVariables(request, response);
            if (isRelatedApplicationIdNotPresent(requestDepositArrangement)) {
                if (offerProductArrangement(request, response, requestDepositArrangement, requestHeader)) {
                    return response;
                }
            } else {
                offerProductArrangementResponseFactory.offerProductArrangementForCrossSell(requestDepositArrangement, response);
            }
            LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(request.getProductArrangement(), "Exit from Offer "));
        } catch (OfferException exception) {
            LOGGER.info("Exiting from Offer with exception");
            exceptionHelper.setResponseHeaderAndThrowException(exception, responseHeaderConverter.convert(request.getHeader()));
        }
        return response;
    }

    private boolean offerProductArrangement(OfferProductArrangementRequest request, OfferProductArrangementResponse response, DepositArrangement requestDepositArrangement, RequestHeader requestHeader) throws OfferException {
        String applicationType = requestDepositArrangement.getPrimaryInvolvedParty().getApplicantType();
        boolean isBFPOIndicatorPresent = validatorAndInitializer.isBfpoAddress(requestDepositArrangement.getPrimaryInvolvedParty().getPostalAddress());
        if (!isBFPOIndicatorPresent) {
            List<Product> productHoldings = identifyService.identifyInvolvedParty(requestHeader, requestDepositArrangement.getPrimaryInvolvedParty());
            if (!CollectionUtils.isEmpty(productHoldings)) {
                requestDepositArrangement.getExistingProducts().addAll(productHoldings);
            }
        }
        if (isDuplicateApplication(requestDepositArrangement, requestHeader.getChannelId(), response) || isCustomerInEligible(requestDepositArrangement, requestHeader, isBFPOIndicatorPresent, response)) {
            return true;
        }
        rpcService.callRPCService(requestDepositArrangement, requestHeader);
        if (isEidvStatusDecline(requestDepositArrangement, response, requestHeader)) {
            LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(response.getProductArrangement(), "Exit from Offer: Application Type INELIGIBLE "));
            return true;
        }
        createNewCustomerOnOcis(request, requestDepositArrangement, requestHeader);
        if (!ApplicantType.GUARDIAN.getValue().equals(applicationType)) {
            createPamService.createPendingArrangement(requestDepositArrangement);
            request.getHeader().setArrangementId(requestDepositArrangement.getArrangementId());
            applyService.applyCreditRatingScale(requestDepositArrangement, requestHeader, isBFPOIndicatorPresent, false);
            LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(requestDepositArrangement, "Entering UpdatePendingArrangement (To update customer score and other application details) "));
            updatePamService.updatePamDetailsForOffer(requestDepositArrangement);
            LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(requestDepositArrangement, "Exiting UpdatePendingArrangement (Product Arrangement Details Updated) "));
        } else {
            createGuardianInPam(requestDepositArrangement);
            createGuardianCustomerScore(requestDepositArrangement);
            requestDepositArrangement.setApplicationStatus(ApplicationStatus.APPROVED.getValue());
        }
        offerProductArrangementResponseFactory.setOfferProductArrangementResponse(requestDepositArrangement, response.getProductArrangement(), false);
        return false;
    }

    private void createGuardianInPam(DepositArrangement requestDepositArrangement) throws OfferException {
        try {
            parentArrangementService.createParentArrangement(requestDepositArrangement);
        } catch (InternalServiceErrorMsg internalServiceErrorMsg) {
            throw new OfferException(internalServiceErrorMsg);
        }
    }

    private void createGuardianCustomerScore(DepositArrangement requestDepositArrangement) {
        CustomerScore customerScore = new CustomerScore();
        customerScore.setScoreResult("0");
        customerScore.setAssessmentType("ASM");
        requestDepositArrangement.getPrimaryInvolvedParty().getCustomerScore().add(customerScore);
    }

    private boolean isRelatedApplicationIdNotPresent(ProductArrangement requestProductArrangement) {
        return StringUtils.isEmpty(requestProductArrangement.getRelatedApplicationId());
    }

    private boolean isDuplicateApplication(DepositArrangement requestDepositArrangement, String channelId, OfferProductArrangementResponse response) {
        LOGGER.info("Checking Duplicate Application");
        if (duplicateApplicationCheckService.checkDuplicateApplication(requestDepositArrangement, channelId)) {
            offerProductArrangementResponseFactory.setOfferProductArrangementResponse(requestDepositArrangement, response.getProductArrangement(), true);
            LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(response.getProductArrangement(), "Exit from Offer: Duplicate Application TRUE "));
            return true;
        }
        LOGGER.info("Duplicate Application Not Found");
        return false;
    }

    private boolean isCustomerInEligible(DepositArrangement requestDepositArrangement, RequestHeader requestHeader, boolean isBFPOIndicatorPresent, OfferProductArrangementResponse response) throws OfferException {
        if (!eligibilityService.determineEligibility(requestDepositArrangement, response.getExistingProductArrangements(), response.getProductArrangement()
                .getExistingProducts(), requestHeader, isBFPOIndicatorPresent)) {
            offerProductArrangementResponseFactory.setOfferProductArrangementResponse(requestDepositArrangement, response.getProductArrangement(), false);
            LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(response.getProductArrangement(), "Exit from Offer: Eligibility FALSE "));
            return true;
        }
        return false;
    }

    private void createNewCustomerOnOcis(OfferProductArrangementRequest request, DepositArrangement requestDepositArrangement, RequestHeader requestHeader) throws OfferException {
        boolean isNewCustomer = requestDepositArrangement.getPrimaryInvolvedParty().isNewCustomerIndicator() != null ? requestDepositArrangement.getPrimaryInvolvedParty()
                .isNewCustomerIndicator() : false;
        if (isNewCustomer) {
            LOGGER.info("Calling CreateInvolvedParty for New Customer");
            createInvolvedPartyService.createInvolvedParty(ArrangementType.SAVINGS.getValue(), false, request.getProductArrangement().getPrimaryInvolvedParty(), requestHeader);
        }
    }

    private boolean isEidvStatusDecline(DepositArrangement requestDepositArrangement, OfferProductArrangementResponse response, RequestHeader requestHeader) throws OfferException {
        verifyInvolvedPartyRoleService.verify(requestDepositArrangement, requestHeader);
        if (EIDVStatus.DECLINE.getValue().equals(requestDepositArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult())) {
            LOGGER.info("EIDV status is DECLINED");
            requestDepositArrangement.setApplicationStatus(ApplicationStatus.DECLINED.getValue());
            requestDepositArrangement.getExistingProducts().clear();
            createPamService.createPendingArrangement(requestDepositArrangement);
            offerProductArrangementResponseFactory.setOfferProductArrangementResponse(requestDepositArrangement, (DepositArrangement) response.getProductArrangement(), false);
            return true;
        }
        return false;
    }
}