package com.lloydsbanking.salsa.opapca.service;

import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.downstream.pam.service.CreatePamService;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamService;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import com.lloydsbanking.salsa.offer.EIDVStatus;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.apply.ApplyService;
import com.lloydsbanking.salsa.offer.createinvolvedparty.CreateInvolvedPartyService;
import com.lloydsbanking.salsa.offer.eligibility.downstream.EligibilityService;
import com.lloydsbanking.salsa.offer.identify.IdentifyService;
import com.lloydsbanking.salsa.offer.pam.service.DuplicateApplicationCheckService;
import com.lloydsbanking.salsa.offer.verify.VerifyInvolvedPartyRoleService;
import com.lloydsbanking.salsa.opapca.logging.OpapcaLogService;
import com.lloydsbanking.salsa.opapca.service.downstream.SiraRetriever;
import com.lloydsbanking.salsa.opapca.service.utility.ExceptionHelper;
import com.lloydsbanking.salsa.opapca.service.validate.RequestValidatorAndInitializer;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.DepositArrangement;
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
public class OpaPcaService implements IAOfferProductArrangement {
    private static final Logger LOGGER = Logger.getLogger(OpaPcaService.class);
    @Autowired
    CreateInvolvedPartyService createInvolvedPartyService;
    @Autowired
    OpapcaLogService opapcaLogService;
    @Autowired
    ApplyService applyService;
    @Autowired
    CreatePamService createPamService;
    @Autowired
    DuplicateApplicationCheckService duplicateApplicationCheckService;
    @Autowired
    UpdatePamService updatePamService;
    @Autowired
    IdentifyService involvedPartyIdentifier;
    @Autowired
    RequestToResponseHeaderConverter responseHeaderConverter;
    @Autowired
    ExceptionHelper exceptionHelper;
    @Autowired
    VerifyInvolvedPartyRoleService verifyInvolvedPartyRoleService;
    @Autowired
    EligibilityService eligibilityService;
    @Autowired
    SiraRetriever siraRetriever;
    @Autowired
    RequestValidatorAndInitializer validatorAndInitializer;
    @Autowired
    ProductArrangementTraceLog productArrangementTraceLog;
    @Override
    public OfferProductArrangementResponse offerProductArrangement(final OfferProductArrangementRequest request) throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementExternalServiceErrorMsg {
        LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(request.getProductArrangement(), "Entering in Offer "));
        opapcaLogService.initialiseContext(request.getHeader());
        OfferProductArrangementResponse response = new OfferProductArrangementResponse();
        DepositArrangement requestDepositArrangement = (DepositArrangement) request.getProductArrangement();
        response.setProductArrangement(new DepositArrangement());
        try {
            validatorAndInitializer.initialiseVariables(request, response);
            if (StringUtils.isEmpty(requestDepositArrangement.getRelatedApplicationId())) {
                boolean isBFPOIndicatorPresent = validatorAndInitializer.isBfpoAddress(requestDepositArrangement.getPrimaryInvolvedParty().getPostalAddress());
                if (!isBFPOIndicatorPresent) {
                    List<Product> productHoldings = involvedPartyIdentifier.identifyInvolvedParty(request.getHeader(), requestDepositArrangement.getPrimaryInvolvedParty());
                    if (!CollectionUtils.isEmpty(productHoldings)) {
                        requestDepositArrangement.getExistingProducts().addAll(productHoldings);
                    }
                }
                if (isDuplicateApplication(requestDepositArrangement, request.getHeader().getChannelId())) {
                    setOfferProductArrangementResponse(requestDepositArrangement, (DepositArrangement) response.getProductArrangement(), true);
                    LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(response.getProductArrangement(), "Exit from Offer: Duplicate Application TRUE "));
                    return response;
                }
                if (isCustomerInEligible(requestDepositArrangement, request.getHeader(), isBFPOIndicatorPresent, response) || isEidvStatusDecline(requestDepositArrangement, response, request.getHeader())) {
                    return response;
                }
                createNewCustomerOnOcis(request.getHeader(), requestDepositArrangement);
                createPamService.createPendingArrangement(requestDepositArrangement);
                request.getHeader().setArrangementId(requestDepositArrangement.getArrangementId());
                applyService.applyCreditRatingScale(requestDepositArrangement, request.getHeader(), isBFPOIndicatorPresent, false);
                siraRetriever.retrieveSiraDecision(requestDepositArrangement, request.getHeader());
                LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(requestDepositArrangement, "Entering UpdatePendingArrangement (To update customer score and other application details) "));
                updatePamService.updatePamDetailsForOffer(requestDepositArrangement);
                LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(requestDepositArrangement, "Exiting UpdatePendingArrangement (Product Arrangement Details Updated) "));
                setOfferProductArrangementResponse(requestDepositArrangement, response.getProductArrangement(), false);
            } else {
                offerProductArrangementForCrossSell(requestDepositArrangement, response);
            }
            LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(request.getProductArrangement(), "Exit from Offer "));
        } catch (OfferException exception) {
            LOGGER.info("Exiting from Offer with exception");
            exceptionHelper.setResponseHeaderAndThrowException(exception, responseHeaderConverter.convert(request.getHeader()));
        }
        return response;
    }
    private boolean isEidvStatusDecline(DepositArrangement requestDepositArrangement, OfferProductArrangementResponse response, RequestHeader requestHeader) throws OfferException {
        verifyInvolvedPartyRoleService.verify(requestDepositArrangement, requestHeader);
        if (EIDVStatus.DECLINE.getValue().equals(requestDepositArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult())) {
            LOGGER.info("EIDV status is DECLINED");
            requestDepositArrangement.setApplicationStatus(ApplicationStatus.DECLINED.getValue());
            requestDepositArrangement.getExistingProducts().clear();
            createPamService.createPendingArrangement(requestDepositArrangement);
            setOfferProductArrangementResponse(requestDepositArrangement, (DepositArrangement) response.getProductArrangement(), false);
            LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(response.getProductArrangement(), "Exit from Offer: Application Type INELIGIBLE "));
            return true;
        }
        return false;
    }
    private boolean isDuplicateApplication(DepositArrangement requestDepositArrangement, String channelId) throws OfferProductArrangementDataNotAvailableErrorMsg {
        LOGGER.info("Checking Duplicate Application");
        if (duplicateApplicationCheckService.checkDuplicateApplication(requestDepositArrangement, channelId)) {
            return true;
        }
        LOGGER.info("Duplicate Application Not Found");
        return false;
    }
    private boolean isCustomerInEligible(DepositArrangement requestDepositArrangement, RequestHeader requestHeader, boolean isBFPOIndicatorPresent, OfferProductArrangementResponse response) throws OfferException {
        if (!eligibilityService.determineEligibility(requestDepositArrangement, response.getExistingProductArrangements(), response.getProductArrangement().getExistingProducts(), requestHeader, isBFPOIndicatorPresent)) {
            setOfferProductArrangementResponse(requestDepositArrangement, response.getProductArrangement(), false);
            LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(response.getProductArrangement(), "Exit from Offer: Eligibility FALSE "));
            return true;
        }
        return false;
    }
    private void createNewCustomerOnOcis(RequestHeader requestHeader, DepositArrangement requestDepositArrangement) throws OfferException {
        boolean isNewCustomer = requestDepositArrangement.getPrimaryInvolvedParty().isNewCustomerIndicator() != null ? requestDepositArrangement.getPrimaryInvolvedParty().isNewCustomerIndicator() : false;
        if (isNewCustomer) {
            LOGGER.info("Calling CreateInvolvedParty for New Customer");
            createInvolvedPartyService.createInvolvedParty(ArrangementType.CURRENT_ACCOUNT.getValue(), false, requestDepositArrangement.getPrimaryInvolvedParty(), requestHeader);
        }
    }
    private void setOfferProductArrangementResponse(DepositArrangement requestProductArrangement, ProductArrangement responseProductArrangement, boolean isDuplicateApplication) {
        DepositArrangement responseDepositArrangement = (DepositArrangement) responseProductArrangement;
        responseDepositArrangement.setPrimaryInvolvedParty(new Customer());
        if (null != requestProductArrangement.getPrimaryInvolvedParty().getAuditData() && !requestProductArrangement.getPrimaryInvolvedParty().getAuditData().isEmpty()) {
            responseDepositArrangement.getPrimaryInvolvedParty().getAuditData().addAll(requestProductArrangement.getPrimaryInvolvedParty().getAuditData());
        }
        responseDepositArrangement.setAssociatedProduct(new Product());
        responseDepositArrangement.getAssociatedProduct().setEligibilityDetails(requestProductArrangement.getAssociatedProduct().getEligibilityDetails());
        responseDepositArrangement.getPrimaryInvolvedParty().getCustomerScore().addAll(requestProductArrangement.getPrimaryInvolvedParty().getCustomerScore());
        responseDepositArrangement.setApplicationStatus(requestProductArrangement.getApplicationStatus());
        responseDepositArrangement.setApplicationSubStatus(requestProductArrangement.getApplicationSubStatus());
        responseDepositArrangement.setArrangementType(requestProductArrangement.getArrangementType());
        responseDepositArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(requestProductArrangement.getPrimaryInvolvedParty().getCustomerIdentifier());
        responseDepositArrangement.getPrimaryInvolvedParty().setNewCustomerIndicator(requestProductArrangement.getPrimaryInvolvedParty().isNewCustomerIndicator());
        responseDepositArrangement.getPrimaryInvolvedParty().setIsAuthCustomer(requestProductArrangement.getPrimaryInvolvedParty().isIsAuthCustomer());
        responseDepositArrangement.getPrimaryInvolvedParty().setCustomerSegment(requestProductArrangement.getPrimaryInvolvedParty().getCustomerSegment());
        responseDepositArrangement.getPrimaryInvolvedParty().setCidPersID(requestProductArrangement.getPrimaryInvolvedParty().getCidPersID());
        responseDepositArrangement.setApplicationType(requestProductArrangement.getApplicationType());
        responseDepositArrangement.getPrimaryInvolvedParty().setIndividualIdentifier(requestProductArrangement.getPrimaryInvolvedParty().getIndividualIdentifier());
        responseDepositArrangement.setArrangementId(requestProductArrangement.getArrangementId());
        responseDepositArrangement.getOfferedProducts().addAll(requestProductArrangement.getOfferedProducts());
        if (!CollectionUtils.isEmpty(requestProductArrangement.getAffiliatedetails()) && !StringUtils.isEmpty(requestProductArrangement.getAffiliatedetails().get(0).getAffiliateIdentifier())) {
            responseDepositArrangement.getAffiliatedetails().addAll(requestProductArrangement.getAffiliatedetails());
        }
        if (null != requestProductArrangement.isIsOverdraftRequired() && requestProductArrangement.isIsOverdraftRequired()) {
            responseDepositArrangement.setOverdraftDetails(requestProductArrangement.getOverdraftDetails());
            responseDepositArrangement.setIsOverdraftRequired(requestProductArrangement.isIsOverdraftRequired());
        }
        if (isDuplicateApplication) {
            responseDepositArrangement.setAssociatedProduct(requestProductArrangement.getAssociatedProduct());
            responseDepositArrangement.getPrimaryInvolvedParty().getCustomerScore().clear();
        } else {
            responseDepositArrangement.getConditions().addAll(requestProductArrangement.getConditions());
            responseDepositArrangement.getPrimaryInvolvedParty().setCbsCustomerNumber(requestProductArrangement.getPrimaryInvolvedParty().getCbsCustomerNumber());
        }
    }
    private void offerProductArrangementForCrossSell(ProductArrangement requestDepositArrangement, OfferProductArrangementResponse response) {
        LOGGER.info("Cross Sell, Related Application ID: " + requestDepositArrangement.getRelatedApplicationId());
        if (!StringUtils.isEmpty(requestDepositArrangement.getRelatedApplicationStatus())) {
            requestDepositArrangement.setApplicationStatus(ApplicationStatus.FULFILLED.getValue().equals(requestDepositArrangement.getRelatedApplicationStatus()) ?
                    ApplicationStatus.APPROVED.getValue() : ApplicationStatus.AWAITING_RELATED_APPLICATION_FULFILMENT.getValue());
        }
        createPamService.createPendingArrangement(requestDepositArrangement);
        setOfferProductArrangementResponseForCrossSell((DepositArrangement) requestDepositArrangement, response.getProductArrangement());
    }
    private void setOfferProductArrangementResponseForCrossSell(DepositArrangement requestProductArrangement, ProductArrangement responseProductArrangement) {
        responseProductArrangement.setAssociatedProduct(new Product());
        responseProductArrangement.setPrimaryInvolvedParty(new Customer());
        responseProductArrangement.setArrangementId(requestProductArrangement.getArrangementId());
        responseProductArrangement.setArrangementType(requestProductArrangement.getArrangementType());
        responseProductArrangement.setApplicationType(requestProductArrangement.getApplicationType());
        responseProductArrangement.setApplicationStatus(requestProductArrangement.getApplicationStatus());
    }
}