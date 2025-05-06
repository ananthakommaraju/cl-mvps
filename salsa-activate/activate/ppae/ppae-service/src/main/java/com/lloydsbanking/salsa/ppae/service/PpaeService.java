package com.lloydsbanking.salsa.ppae.service;


import com.lloydsbanking.salsa.activate.communication.convert.NotificationEmailTemplates;
import com.lloydsbanking.salsa.activate.communication.downstream.CommunicationManager;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.constants.EmailTemplateEnum;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.downstream.pam.service.RetrievePamService;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import com.lloydsbanking.salsa.ppae.logging.PpaeLogService;
import com.lloydsbanking.salsa.ppae.service.appstatus.*;
import com.lloydsbanking.salsa.ppae.service.constant.AppStatusConstant;
import com.lloydsbanking.salsa.ppae.service.process.AwaitingFulfilmentApplicationProcessor;
import com.lloydsbanking.salsa.ppae.service.process.AwaitingPostFulfilmentProcessor;
import com.lloydsbanking.salsa.ppae.service.process.AwaitingReferralLRAApplicationProcessor;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_processpendingarrangementevent.IAProcessPendingArrangementEvent;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PpaeService implements IAProcessPendingArrangementEvent {

    private static final Logger LOGGER = Logger.getLogger(PpaeService.class);

    @Autowired
    PpaeLogService ppaeLogService;
    @Autowired
    RetrievePamService retrievePamService;
    @Autowired
    LookUpValueRetriever lookUpValueRetriever;
    @Autowired
    ApplicationStatusIdentifier applicationStatusIdentifier;
    @Autowired
    AwaitingRescoreProcessor awaitingRescoreProcessor;
    @Autowired
    NotificationEmailTemplates notificationEmailTemplates;
    @Autowired
    CommunicationManager communicationManager;
    @Autowired
    AwaitingManualIDProcessor awaitingManualIDProcessor;
    @Autowired
    CcaSignedProcessor ccaSignedProcessor;
    @Autowired
    ProcessPendingApplications processPendingApplications;
    @Autowired
    AwaitingFulfilmentApplicationProcessor awaitingFulfilmentApplicationProcessor;
    @Autowired
    AwaitingReferralLRAApplicationProcessor awaitingReferralLRAApplicationProcessor;
    @Autowired
    AwaitingPostFulfilmentProcessor awaitingPostFulfilmentProcessor;
    @Autowired
    ProductArrangementTraceLog productArrangementTraceLog;

    @Override
    public void processPendingArrangementEvent(ProcessPendingArrangementEventRequest upStreamRequest) {
        ppaeLogService.initialiseContext(upStreamRequest.getHeader());
        LOGGER.info("Entering processPendingArrangementEvent service with Application ID | ChannelId : " + upStreamRequest.getApplicationId() + " | " + upStreamRequest.getHeader().getChannelId());
        upStreamRequest.getHeader().setArrangementId(upStreamRequest.getApplicationId());
        upStreamRequest.getHeader().setContactPointId(lookUpValueRetriever.retrieveContactPointId(upStreamRequest.getHeader().getChannelId(), upStreamRequest.getHeader()));
        ProductArrangement productArrangement;
        productArrangement = callRetrievePendingArrangement(upStreamRequest);

        String appStatus = productArrangement != null ? applicationStatusIdentifier.retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getArrangementType(), productArrangement.getReferral()) : null;
        if (null != appStatus) {
            PpaeInvocationIdentifier ppaeInvocationIdentifier = processApplicationStatus(appStatus, productArrangement, upStreamRequest);
            if (null != productArrangement) {
                processPendingApplications.modifyAndActivatePendingApplications(productArrangement, upStreamRequest.getHeader(), ppaeInvocationIdentifier);
            }
        }
        LOGGER.info("Exiting processPendingArrangementEvent service");
    }

    private ProductArrangement callRetrievePendingArrangement(ProcessPendingArrangementEventRequest upStreamRequest) {
        ProductArrangement productArrangement = null;
        try {
            LOGGER.info("Entering retrieveProductArrangementDetails with channelId | ApplicationId : " + upStreamRequest.getHeader().getChannelId() + " | " + upStreamRequest.getApplicationId());
            productArrangement = retrievePamService.retrievePendingArrangement(upStreamRequest.getHeader().getChannelId(), upStreamRequest.getApplicationId(), null);
        } catch (DataNotAvailableErrorMsg dataNotAvailableErrorMsg) {
            LOGGER.info("Error while calling retrievePendingArrangement with DataNotAvailableError" + dataNotAvailableErrorMsg);
        } catch (ResourceNotAvailableErrorMsg resourceNotAvailableErrorMsg) {
            LOGGER.info("Error while calling retrievePendingArrangement with ResourceNotAvailableError" + resourceNotAvailableErrorMsg);
        } catch (InternalServiceErrorMsg internalServiceErrorMsg) {
            LOGGER.info("Error while calling retrievePendingArrangement with InternalServiceError" + internalServiceErrorMsg);
        }
        LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(productArrangement, "Exiting retrieveProductArrangementDetails"));
        return productArrangement;
    }

    private PpaeInvocationIdentifier processApplicationStatus(String appStatus, ProductArrangement productArrangement, ProcessPendingArrangementEventRequest upStreamRequest) {
        PpaeInvocationIdentifier ppaeInvocationIdentifier = new PpaeInvocationIdentifier();
        switch (appStatus) {
            case AppStatusConstant.AWAITING_RESCORE:
                awaitingRescoreProcessor.retrieveProductDetailsAndCreditRating(productArrangement, upStreamRequest.getHeader(), ppaeInvocationIdentifier);
                break;

            case AppStatusConstant.AWAITING_REFERRAL:
                ppaeInvocationIdentifier.setInvokeActivateProductArrangementFlag(true);
                break;

            case AppStatusConstant.AWAITING_MANUAL_ID_V:
                awaitingManualIDProcessor.processCommunications(productArrangement, upStreamRequest);
                break;

            case AppStatusConstant.ABANDONED:
                communicationManager.callSendCommunicationService(productArrangement, notificationEmailTemplates.getNotificationEmailForAbandonedStatus(productArrangement.getArrangementType()), upStreamRequest.getHeader(), null, ActivateCommonConstant.CommunicationTypes.EMAIL);
                break;

            case AppStatusConstant.AWAITING_FULFILMENT:
                awaitingFulfilmentApplicationProcessor.process(productArrangement, upStreamRequest.getHeader());
                break;

            case AppStatusConstant.AWAITING_REFERRAL_LRA:
                awaitingReferralLRAApplicationProcessor.process(productArrangement, upStreamRequest.getHeader());
                break;

            case AppStatusConstant.NEW_CAR:
                communicationManager.callSendCommunicationService(productArrangement, EmailTemplateEnum.CAR_FINANCE_DD_REMINDER_EMAIL.getTemplate(), upStreamRequest.getHeader(), null, ActivateCommonConstant.CommunicationTypes.EMAIL);
                break;

            case AppStatusConstant.CCA_SIGNED_CCA_PENDING:
                ccaSignedProcessor.processingPendingApplications(productArrangement, upStreamRequest, ppaeInvocationIdentifier);
                break;

            case AppStatusConstant.AWAITING_POST_FULFILMENT_PROCESS:
                awaitingPostFulfilmentProcessor.process(productArrangement, upStreamRequest, ppaeInvocationIdentifier);
                break;
            default:
        }
        return ppaeInvocationIdentifier;
    }


}

