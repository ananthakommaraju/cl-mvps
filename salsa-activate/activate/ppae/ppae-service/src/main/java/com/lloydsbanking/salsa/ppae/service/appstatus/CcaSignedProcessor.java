package com.lloydsbanking.salsa.ppae.service.appstatus;


import com.lloydsbanking.salsa.activate.communication.convert.NotificationEmailTemplates;
import com.lloydsbanking.salsa.activate.communication.downstream.CommunicationManager;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.ppae.service.constant.PPAEServiceConstant;
import com.lloydsbanking.salsa.ppae.service.downstream.EstablishFinanceServiceArrangementRetriever;
import com.lloydsbanking.salsa.ppae.service.downstream.F263LoanDetailsRetriever;
import com.lloydsbanking.salsa.soap.pad.f263.objects.F263Resp;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

@Component
public class CcaSignedProcessor {

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    CommunicationManager communicationManager;

    @Autowired
    NotificationEmailTemplates notificationEmailTemplates;

    @Autowired
    LookUpValueRetriever lookUpValueRetriever;

    @Autowired
    EmailDateSender emailDateSender;

    @Autowired
    F263LoanDetailsRetriever f263LoanDetailsRetriever;

    @Autowired
    EstablishFinanceServiceArrangementRetriever establishFinanceServiceArrangementRetriever;

    private static final Logger LOGGER = Logger.getLogger(CcaSignedProcessor.class);

    private static final String CCA_SIGNED_DAYS = "CCA_SIGNED_DAYS";

    private static final String CCA_PENDING_DAYS = "CCA_PENDING_DAYS";

    public void processingPendingApplications(ProductArrangement productArrangement, ProcessPendingArrangementEventRequest upStreamRequest, PpaeInvocationIdentifier ppaeInvocationIdentifier) {
        String padStatus = null;
        F263Resp f263Resp = f263LoanDetailsRetriever.invokeF263(upStreamRequest, productArrangement);
        if (null != f263Resp && null != f263Resp.getApplicationDetails() && null != f263Resp.getApplicationDetails().getLoanApplnStatusCd()) {
            padStatus = String.valueOf(f263Resp.getApplicationDetails().getLoanApplnStatusCd());
        }
        if (productArrangement.getApplicationStatus() != null && padStatus != null) {
            checkingPadAndPamStatus(padStatus, productArrangement, upStreamRequest, f263Resp, ppaeInvocationIdentifier);
        }
    }

    private void checkingPadAndPamStatus(String padStatus, ProductArrangement productArrangement, ProcessPendingArrangementEventRequest upStreamRequest, F263Resp f263Resp, PpaeInvocationIdentifier ppaeInvocationIdentifier) {
        if (productArrangement.getApplicationStatus().equals(PPAEServiceConstant.APP_STATUS_CCA_SIGNED) && padStatus.equals(PPAEServiceConstant.PAD_STATUS_OPEN)) {
            invokeEstablishFinanceServiceArrangement(productArrangement, upStreamRequest, f263Resp, ppaeInvocationIdentifier);
        } else if (productArrangement.getApplicationStatus().equals(PPAEServiceConstant.APP_STATUS_CCA_SIGNED) && padStatus.equals(PPAEServiceConstant.PAD_STATUS_CCA_SIGNED)) {
            if (null != productArrangement.getPrimaryInvolvedParty().isIsRegistrationSelected() && !productArrangement.getPrimaryInvolvedParty().isIsRegistrationSelected() && productArrangement.getPrimaryInvolvedParty().getEmailAddress() != null) {
                int emailDate = getDate(productArrangement.getLastModifiedDate(), upStreamRequest, CCA_SIGNED_DAYS);
                sendEmail(productArrangement, upStreamRequest, emailDate, notificationEmailTemplates.getNotificationEmailForCcaSigned(emailDate));
            }
        } else if (isFinanceArrInstance(productArrangement, padStatus)) {
            int emailDate = getDate(productArrangement.getLastModifiedDate(), upStreamRequest, CCA_PENDING_DAYS);
            sendEmail(productArrangement, upStreamRequest, emailDate, notificationEmailTemplates.getNotificationEmailForCcaPending(emailDate));
        }
    }

    private void invokeEstablishFinanceServiceArrangement(ProductArrangement productArrangement, ProcessPendingArrangementEventRequest upStreamRequest, F263Resp f263Resp, PpaeInvocationIdentifier ppaeInvocationIdentifier) {
        try {
            establishFinanceServiceArrangementRetriever.retrieve(upStreamRequest.getHeader(), f263Resp, productArrangement.getLastModifiedDate());
        } catch (DatatypeConfigurationException e) {
            LOGGER.info("Error while invoking B232 Datatypeconfig Error - Prepare Finance Service Arrangement" + e);
        }
        ppaeInvocationIdentifier.setInvokeModifyProductArrangementFlag(true);
        productArrangement.setApplicationStatus(ApplicationStatus.FULFILLED.getValue());
        LOGGER.info("Exiting establishFinanceServiceArrangement ArrangementSetUp");
    }

    private void sendEmail(ProductArrangement productArrangement, ProcessPendingArrangementEventRequest upStreamRequest, int emailDate, String template) {
        if (emailDate > 0) {
            communicationManager.callSendCommunicationService(productArrangement, template, upStreamRequest.getHeader(), null, ActivateCommonConstant.CommunicationTypes.EMAIL);
        }
    }

    private int getDate(XMLGregorianCalendar lastModifiedDate, ProcessPendingArrangementEventRequest upStreamRequest, String ccaDays) {
        ArrayList<String> groupCodeList = new ArrayList<>();
        groupCodeList.add(ccaDays);
        List<ReferenceDataLookUp> lookUpList = new ArrayList<>();
        try {
            LOGGER.info("Entering RetrieveContactPointId with Channel | GroupCode " + upStreamRequest.getHeader().getChannelId() + " | " + groupCodeList.get(0));
            lookUpList = lookUpValueRetriever.getLookUpValues(groupCodeList, upStreamRequest.getHeader().getChannelId());
        } catch (DataAccessException e) {
            LOGGER.info("Error while retrieving lookUp values for App Status CCAsigned and Pending: " + e);
        }
        LOGGER.info("Exiting RetrieveLookupValues");
        return emailDateSender.getEmailDate(lastModifiedDate, lookUpList);
    }

    private boolean isFinanceArrInstance(ProductArrangement productArrangement, String padStatus) {
        if (productArrangement instanceof FinanceServiceArrangement) {
            if (productArrangement.getApplicationStatus().equalsIgnoreCase(PPAEServiceConstant.APP_STATUS_CCA_PENDING) && checkPadStatus(padStatus) && !productArrangement.getPrimaryInvolvedParty().isIsRegistrationSelected() && productArrangement.getPrimaryInvolvedParty().getEmailAddress() != null) {
                if (((FinanceServiceArrangement) productArrangement).isNameAndAddressVerifiedFlag() != null && ((FinanceServiceArrangement) productArrangement).isNameAndAddressVerifiedFlag()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkPadStatus(String padStatus) {
        return (padStatus.equals(PPAEServiceConstant.PAD_SATUS_QUOTATION_GIVEN) || padStatus.equals(PPAEServiceConstant.PAD_SATUS_QUOTATION_PENDING));
    }

}
