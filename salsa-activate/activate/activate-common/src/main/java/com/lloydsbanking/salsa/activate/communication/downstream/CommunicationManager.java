package com.lloydsbanking.salsa.activate.communication.downstream;

import com.lloydsbanking.salsa.activate.communication.convert.CommunicationRequestFactory;
import com.lloydsbanking.salsa.downstream.cm.client.ScheduleCommunicationClient;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_communicationmanager.id_communicationeventscheduler.*;
import lib_sim_communicationmanager.id_communicationmanagerrouter.*;
import lib_sim_communicationmanager.messages.ScheduleCommunicationResponse;
import lib_sim_communicationmanager.messages.SendCommunicationResponse;
import com.lloydsbanking.salsa.downstream.cm.client.SendCommunicationClient;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.ws.WebServiceException;

@Component
public class CommunicationManager {
    private static final Logger LOGGER = Logger.getLogger(CommunicationManager.class);

    @Autowired
    SendCommunicationClient sendCommunicationClient;

    @Autowired
    ScheduleCommunicationClient scheduleCommunicationClient;

    @Autowired
    CommunicationRequestFactory communicationRequestFactory;

    public SendCommunicationResponse callSendCommunicationService(ProductArrangement productArrangement, String notificationTemplate, RequestHeader requestHeader, String source, String communicationType) {
        try {
            return sendCommunicationClient.sendCommunication(communicationRequestFactory.convertToSendCommunicationRequest(productArrangement, notificationTemplate, requestHeader, source, communicationType));
        } catch (WebServiceException | SendCommunicationExternalBusinessErrorMsg | SendCommunicationResourceNotAvailableErrorMsg | SendCommunicationExternalServiceErrorMsg | SendCommunicationDataNotAvailableErrorMsg | SendCommunicationInternalServiceErrorMsg errorMsg) {
            LOGGER.info("Error while calling Send Communication with template: " + notificationTemplate + " Error: " + errorMsg);
        }
        return null;
    }

    public ScheduleCommunicationResponse callScheduleCommunicationService(ProductArrangement productArrangement, String notificationTemplate, RequestHeader requestHeader, String source, String communicationType) {
        try {
            return scheduleCommunicationClient.scheduleCommunication(communicationRequestFactory.convertToScheduleCommunicationRequest(productArrangement, notificationTemplate, requestHeader, source, communicationType));
        } catch (WebServiceException | ScheduleCommunicationExternalBusinessErrorMsg | ScheduleCommunicationResourceNotAvailableErrorMsg | ScheduleCommunicationExternalServiceErrorMsg | ScheduleCommunicationDataNotAvailableErrorMsg | ScheduleCommunicationInternalServiceErrorMsg errorMsg) {
            LOGGER.info("Error while calling Schedule Communication with template: " + notificationTemplate + " Error: " + errorMsg);
        }
        return null;
    }

    public ScheduleCommunicationResponse callScheduleCommunicationServiceForPpae(ProductArrangement productArrangement, String notificationTemplate, RequestHeader requestHeader, String source, String communicationType, int configuredDays) {
        try {
            return scheduleCommunicationClient.scheduleCommunication(communicationRequestFactory.convertToScheduleCommunicationRequestWithConfiguredDays(productArrangement, notificationTemplate, requestHeader, source, communicationType,configuredDays));
        } catch (WebServiceException | ScheduleCommunicationExternalBusinessErrorMsg | ScheduleCommunicationResourceNotAvailableErrorMsg | ScheduleCommunicationExternalServiceErrorMsg | ScheduleCommunicationDataNotAvailableErrorMsg | ScheduleCommunicationInternalServiceErrorMsg errorMsg) {
            LOGGER.info("Error while calling Schedule Communication with template: " + notificationTemplate + " Error: " + errorMsg);
        }
        return null;
    }
}

