package com.lloydsbanking.salsa.activate.communication.convert;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.constants.CommunicationKeysEnum;
import com.lloydsbanking.salsa.activate.constants.CommunicationTemplateSet;
import com.lloydsbanking.salsa.activate.constants.EmailTemplateEnum;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.date.DateFactory;
import lib_sim_bo.businessobjects.*;
import lib_sim_communicationmanager.messages.ScheduleCommunicationRequest;
import lib_sim_communicationmanager.messages.SendCommunicationRequest;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommunicationRequestFactory {
    private static final int ACCOUNT_NUMBER_START_INDEX = 4;
    private static final int ACCOUNT_NUMBER_END_INDEX = 8;
    private static final String ACCOUNT_NUMBER_PREFIX = "XXXX";
    private static final int MOBILE_NUMBER_LENGTH = 10;

    @Autowired
    ProductRelatedTokenFactory productRelatedTokenFactory;
    @Autowired
    PostCodeFactory postCodeFactory;
    @Autowired
    InformationContentFactory informationContentFactory;

    public SendCommunicationRequest convertToSendCommunicationRequest(ProductArrangement productArrangement, String notificationTemplate, RequestHeader header, String source, String communicationType) {
        SendCommunicationRequest sendCommunicationRequest = new SendCommunicationRequest();
        Communication communication = getCommunicationRequest(productArrangement, notificationTemplate, header.getChannelId(), source, communicationType, null);
        sendCommunicationRequest.setCommunication(communication);
        sendCommunicationRequest.setHeader(header);
        return sendCommunicationRequest;
    }

    public ScheduleCommunicationRequest convertToScheduleCommunicationRequest(ProductArrangement productArrangement, String notificationTemplate, RequestHeader header, String source, String communicationType) {
        ScheduleCommunicationRequest scheduleCommunicationRequest = new ScheduleCommunicationRequest();
        Communication communication = getCommunicationRequest(productArrangement, notificationTemplate, header.getChannelId(), source, communicationType, String.valueOf(new DateFactory().getTomorrowTimeStamp()));
        scheduleCommunicationRequest.setCommunication(communication);
        scheduleCommunicationRequest.setHeader(header);
        return scheduleCommunicationRequest;
    }

    public ScheduleCommunicationRequest convertToScheduleCommunicationRequestWithConfiguredDays(ProductArrangement productArrangement, String notificationTemplate, RequestHeader header, String source, String communicationType, int configuredDays) {
        ScheduleCommunicationRequest scheduleCommunicationRequest = new ScheduleCommunicationRequest();
        Communication communication = getCommunicationRequest(productArrangement, notificationTemplate, header.getChannelId(), source, communicationType, null);
        List<InformationContent> informationContentList = new ArrayList<>();
        informationContentList.add((informationContentFactory.getNoOfDaysInformationContent(productArrangement, configuredDays)));
        communication.getHasCommunicationContent().addAll(informationContentList);
        scheduleCommunicationRequest.setCommunication(communication);
        scheduleCommunicationRequest.setHeader(header);
        return scheduleCommunicationRequest;

    }


    private Communication getCommunicationRequest(ProductArrangement productArrangement, String notificationTemplate, String brand, String source, String communicationType, String sendDate) {
        Communication communication = new Communication();
        CommunicationTemplate communicationTemplate = new CommunicationTemplate();
        communicationTemplate.setTemplateId(ActivateCommonConstant.CommunicationTypes.SMS.equalsIgnoreCase(communicationType) ?
                (notificationTemplate + "_" + brand) : notificationTemplate);
        communication.setCommunicationTemplate(communicationTemplate);
        communication.setBrand(brand);
        communication.setCommunicationType(communicationType);
        communication.setContactPointId(getCustomerContactPointId(productArrangement, communicationType));
        if (source != null) {
            communication.setSource(source);
        }
        if (sendDate != null) {
            communication.setSendDate(sendDate);
        }
        List<InformationContent> informationContentList = new ArrayList<>();
        if (ActivateCommonConstant.CommunicationTypes.EMAIL.equalsIgnoreCase(communicationType) || ActivateCommonConstant.CommunicationTypes.ATTACHMENT.equalsIgnoreCase(communicationType)) {
            informationContentList = getInformationContentListForEmail(productArrangement, notificationTemplate);
        } else if (ActivateCommonConstant.CommunicationTypes.SMS.equalsIgnoreCase(communicationType)) {
            informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PRODUCT_NAME.getKey(), productArrangement.getAssociatedProduct().getProductName(), 0));

        }
        communication.getHasCommunicationContent().addAll(informationContentList);
        return communication;
    }

    private List<InformationContent> getInformationContentListForEmail(ProductArrangement productArrangement, String emailTemplate) {
        List<InformationContent> informationContentList = new ArrayList<>();
        if (CommunicationTemplateSet.isEmailTemplate(emailTemplate)) {
            informationContentList.addAll(getStandardDataTokens(productArrangement));
            informationContentList.addAll(productRelatedTokenFactory.getProductRelatedToken(productArrangement));
        } else if (EmailTemplateEnum.IB_STP_REGISTRATION_SUCCESS_MAIL.getTemplate().equals(emailTemplate) || EmailTemplateEnum.IB_STP_LITE_REGISTRATION_SUCCESS_MAIL.getTemplate().equals(emailTemplate)) {
            informationContentList.addAll(getStandardDataTokens(productArrangement));
            informationContentList.addAll(getIBRegistrationData(productArrangement));
        } else if (EmailTemplateEnum.BT_FAILED_EMAIL.getTemplate().equals(emailTemplate) || EmailTemplateEnum.BT_FULFILLED_EMAIL.getTemplate().equals(emailTemplate) || EmailTemplateEnum.BT_PARTIALLY_FULFILLED_EMAIL.getTemplate().equals(emailTemplate)) {
            if (productArrangement instanceof FinanceServiceArrangement) {
                informationContentList.addAll(productRelatedTokenFactory.getDataTokensForBTFulfilment((FinanceServiceArrangement) productArrangement));
            }
        } else {
            informationContentList.addAll(getStandardDataTokens(productArrangement));
        }
        return informationContentList;
    }

    private List<InformationContent> getStandardDataTokens(ProductArrangement productArrangement) {
        List<InformationContent> informationContentList = new ArrayList<>();
        informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.ARRANGEMENT_ID.getKey(), productArrangement.getArrangementId(), 0));
        if (!productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().isEmpty()) {
            IndividualName individualName = productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().get(0);
            informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.CUSTOMER_LASTNAME.getKey(), individualName.getLastName(), 0));
            informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.CUSTOMER_TITLE.getKey(), individualName.getPrefixTitle(), 0));
            informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.CUSTOMER_FIRSTNAME.getKey(), individualName.getFirstName(), 0));
        }
        informationContentList.addAll(getParentDetails(productArrangement.getGuardianDetails()));
        if (productArrangement.getAssociatedProduct() != null && productArrangement.getAssociatedProduct().getInstructionDetails() != null
                && productArrangement.getAssociatedProduct().getInstructionDetails().getInstructionMnemonic() != null) {
            informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PRODUCT_MNEMONIC.getKey(), productArrangement.getAssociatedProduct().getInstructionDetails().getInstructionMnemonic(), 0));
        }
        return informationContentList;
    }

    private List<InformationContent> getParentDetails(Customer customer) {
        List<InformationContent> informationContentList = new ArrayList<>();
        if (customer != null && customer.getIsPlayedBy() != null) {
            if (!customer.getIsPlayedBy().getIndividualName().isEmpty()) {
                IndividualName individualNameParent = customer.getIsPlayedBy().getIndividualName().get(0);
                informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PARENT_LASTNAME.getKey(), individualNameParent.getLastName(), 0));
                informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PARENT_TITLE.getKey(), individualNameParent.getPrefixTitle(), 0));
                informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PARENT_FIRSTNAME.getKey(), individualNameParent.getFirstName(), 0));
            }
        }
        return informationContentList;
    }

    private List<InformationContent> getIBRegistrationData(ProductArrangement productArrangement) {
        List<InformationContent> informationContentList = new ArrayList();
        Customer customer = null;
        if (ArrangementType.CREDITCARD.getValue().equals(productArrangement.getArrangementType())) {
            if (!productArrangement.getJointParties().isEmpty()) {
                customer = productArrangement.getJointParties().get(0);
            }
        } else {
            customer = productArrangement.getPrimaryInvolvedParty();
        }

        if (customer != null && customer.getIsRegisteredIn() != null && customer.getIsRegisteredIn().getProfile() != null) {
            informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.CUSTOMER_USERNAME.getKey(), customer.getIsRegisteredIn().getProfile().getUserName(), 0));
        }

        if (productArrangement.getAssociatedProduct() != null) {
            setMaskedPostCodeAndAccountNumber(productArrangement, informationContentList);
        }
        return informationContentList;
    }

    private void setMaskedPostCodeAndAccountNumber(ProductArrangement productArrangement, List<InformationContent> informationContentList) {
        String maskedPostCode = postCodeFactory.getMaskedPostcode(productArrangement);
        String accountNumber = productArrangement.getAccountNumber();
        if (!StringUtils.isEmpty(maskedPostCode)) {
            informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PRODUCT_MASKED_POSTCODE.getKey(), maskedPostCode, 0));
            if (StringUtils.isEmpty(accountNumber)) {
                informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PRODUCT_MASKED_ACCOUNT_NUMBER.getKey(), maskedPostCode, 0));
            }
        }
        if (!StringUtils.isEmpty(accountNumber)) {
            informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PRODUCT_SA_ACCOUNT_NUMBER.getKey(), accountNumber, 0));
            String maskedAccountNumber = ACCOUNT_NUMBER_PREFIX.concat(accountNumber.substring(ACCOUNT_NUMBER_START_INDEX, ACCOUNT_NUMBER_END_INDEX));
            informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PRODUCT_MASKED_ACCOUNT_NUMBER.getKey(), maskedAccountNumber, 0));
        }
    }

    private String getCustomerContactPointId(ProductArrangement productArrangement, String communicationType) {
        String contactPointId = null;
        if (ActivateCommonConstant.CommunicationTypes.EMAIL.equalsIgnoreCase(communicationType) || ActivateCommonConstant.CommunicationTypes.ATTACHMENT.equalsIgnoreCase(communicationType)) {
            contactPointId = productArrangement.getPrimaryInvolvedParty().getEmailAddress();
            if (contactPointId == null && productArrangement.getGuardianDetails() != null) {
                contactPointId = productArrangement.getGuardianDetails().getEmailAddress();
            }
        } else if (ActivateCommonConstant.CommunicationTypes.SMS.equalsIgnoreCase(communicationType)) {
            List<TelephoneNumber> telephoneNumberList = productArrangement.getPrimaryInvolvedParty().getTelephoneNumber();
            if (CollectionUtils.isEmpty(telephoneNumberList)) {
                telephoneNumberList = productArrangement.getGuardianDetails().getTelephoneNumber();
            }
            for (TelephoneNumber telephoneNumber : telephoneNumberList) {
                if (ActivateCommonConstant.TelephoneTypes.MOBILE.equalsIgnoreCase(telephoneNumber.getTelephoneType()) && telephoneNumber.getPhoneNumber().length() >= MOBILE_NUMBER_LENGTH) {
                    contactPointId = telephoneNumber.getCountryPhoneCode() + telephoneNumber.getPhoneNumber();
                    break;
                }
            }
        }
        return contactPointId;
    }

}
