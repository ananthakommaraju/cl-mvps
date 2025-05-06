package com.lloydsbanking.salsa.ppae.service.appstatus;


import com.lloydsbanking.salsa.activate.communication.convert.NotificationEmailTemplates;
import com.lloydsbanking.salsa.activate.communication.downstream.CommunicationManager;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.constants.SMSTemplateEnum;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.ppae.service.constant.PPAEServiceConstant;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_bo.businessobjects.TelephoneNumber;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class AwaitingManualIDProcessor {

    private static final Logger LOGGER = Logger.getLogger(AwaitingManualIDProcessor.class);

    @Autowired
    LookUpValueRetriever lookUpValueRetriever;

    @Autowired
    CommunicationManager communicationManager;

    @Autowired
    NotificationEmailTemplates notificationEmailTemplates;

    @Autowired
    DateFactory dateFactory;

    private static final long CONFIG_DAYS_FOR_SMS = 5;

    private static final String CUSTOMER_NO_SHOW_UPD_LKP_GRP = "CUSTOMER_NO_SHOW_UPD";

    private static final String CUSTOMER_NO_SHOW_UPD_LKP_TEXT = "Cust.NoShow_Upddays";

    public void processCommunications(ProductArrangement productArrangement, ProcessPendingArrangementEventRequest upStreamRequest) {
        List<ReferenceDataLookUp> lookupList = getLookUpData(productArrangement.getAssociatedProduct().getBrandName(), upStreamRequest);
        sendSMSAndEmail(lookupList, productArrangement, upStreamRequest);
    }

    private void sendSMSAndEmail(List<ReferenceDataLookUp> lookupList, ProductArrangement productArrangement, ProcessPendingArrangementEventRequest upStreamRequest) {
        int numberOfConfiguredDays = 0;
        if (null != getNoOfConfiguredDays(lookupList)) {
            numberOfConfiguredDays = Integer.valueOf(getNoOfConfiguredDays(lookupList));
        }
        long differenceInConfigDaysNLastUpdate = numberOfConfiguredDays - getNoOfDaysAfterUpdate(productArrangement.getLastModifiedDate());
        if (differenceInConfigDaysNLastUpdate > CONFIG_DAYS_FOR_SMS) {
            sendSMSForValidContactNumber(productArrangement, upStreamRequest, numberOfConfiguredDays);
        } else {
            sendEmailForValidEmailId(productArrangement, upStreamRequest, (int) differenceInConfigDaysNLastUpdate);
            sendSMSForValidContactNumber(productArrangement, upStreamRequest, numberOfConfiguredDays);
        }
    }

    private void sendEmailForValidEmailId(ProductArrangement productArrangement, ProcessPendingArrangementEventRequest upStreamRequest, int differenceInConfigDaysNLastUpdate) {
        String notificationEmail = notificationEmailTemplates.getNotificationEmailForDifferenceLessThanFive(productArrangement.getArrangementType());
        productArrangement.setFundingDays((int) differenceInConfigDaysNLastUpdate);
        communicationManager.callSendCommunicationService(productArrangement, notificationEmail, upStreamRequest.getHeader(), null, ActivateCommonConstant.CommunicationTypes.EMAIL);
    }

    private List<ReferenceDataLookUp> getLookUpData(String brand, ProcessPendingArrangementEventRequest upStreamRequest) {
        ArrayList<String> groupCodeList = new ArrayList<>();
        groupCodeList.add(CUSTOMER_NO_SHOW_UPD_LKP_GRP);
        List<ReferenceDataLookUp> lookUpList = new ArrayList<>();
        try {
            LOGGER.info("Entering retrieveLookupValues with group code | contactPointId " + CUSTOMER_NO_SHOW_UPD_LKP_GRP + " | " + upStreamRequest.getHeader().getContactPointId());
            lookUpList = lookUpValueRetriever.getLookUpValues(groupCodeList, brand);
        } catch (DataAccessException e) {
            LOGGER.info("Error while retrieving lookUp values for App Status Awaiting Manual ID & V: " + e);
        }
        LOGGER.info("Exiting retrieveLookupValues");
        return lookUpList;
    }

    private void sendSMSForValidContactNumber(ProductArrangement productArrangement, ProcessPendingArrangementEventRequest upStreamRequest, int numberOfConfiguredDays) {
        if (isValidMobileNumExists(productArrangement)) {
            String notificationSMS = SMSTemplateEnum.STPCCRREMINDER.getTemplate();
            String sourceSMS = SMSTemplateEnum.STP_CC_SOURCE.getTemplate();
            if ((productArrangement.getArrangementType().equalsIgnoreCase(ArrangementType.SAVINGS.getValue())) || productArrangement.getArrangementType().equalsIgnoreCase(ArrangementType.CURRENT_ACCOUNT.getValue())) {
                notificationSMS = SMSTemplateEnum.STPSAVREMINDER.getTemplate();
                sourceSMS = SMSTemplateEnum.STP_SAV_SOURCE.getTemplate();
            }
            communicationManager.callScheduleCommunicationServiceForPpae(productArrangement, notificationSMS, upStreamRequest.getHeader(), sourceSMS, ActivateCommonConstant.CommunicationTypes.SMS, numberOfConfiguredDays);
        }
    }

    private boolean isValidMobileNumExists(ProductArrangement productArrangement) {
        if (!isMobileNumberPresent(productArrangement.getPrimaryInvolvedParty())) {
            return isMobileNumberPresent(productArrangement.getGuardianDetails());

        }
        return true;


    }

    private String getNoOfConfiguredDays(List<ReferenceDataLookUp> referenceDataCustomerNOShowUpd) {
        for (ReferenceDataLookUp lookUp : referenceDataCustomerNOShowUpd) {
            if (lookUp.getLookupText().equalsIgnoreCase(CUSTOMER_NO_SHOW_UPD_LKP_TEXT)) {
                return lookUp.getLookupValueDesc();
            }
        }

        return null;
    }

    private Long getNoOfDaysAfterUpdate(XMLGregorianCalendar lastModifiedDate) {
        long numOfDaysAfterUpdate = 0;
        if (lastModifiedDate != null) {
            Date currentDate = new Date();
            Date modifiedDate = dateFactory.convertXMLGregorianToDateFormat(lastModifiedDate);
            numOfDaysAfterUpdate = dateFactory.differenceInDays(modifiedDate, currentDate);
        }
        return numOfDaysAfterUpdate;
    }


    private boolean isMobileNumberPresent(Customer customer) {
        boolean isTelephoneNumber = false;
        if (customer != null && !CollectionUtils.isEmpty(customer.getTelephoneNumber())) {
            for (TelephoneNumber item : customer.getTelephoneNumber()) {
                if (item.getTelephoneType().equalsIgnoreCase(PPAEServiceConstant.TELEPHONE_TYPE_UK) && item.getPhoneNumber().length() >= PPAEServiceConstant.TELEPHONE_NUMBER_LENGTH_UK) {
                    isTelephoneNumber = true;
                }
            }
        }
        return isTelephoneNumber;
    }

}
