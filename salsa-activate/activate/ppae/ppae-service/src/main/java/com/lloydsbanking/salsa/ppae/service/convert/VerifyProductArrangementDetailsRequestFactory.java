package com.lloydsbanking.salsa.ppae.service.convert;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydstsb.schema.enterprise.lcsm_arrangement.wz.ArrangementAssociation;
import com.lloydstsb.schema.enterprise.lcsm_arrangement.wz.DepositArrangement;
import com.lloydstsb.schema.enterprise.lcsm_arrangement.wz.FinanceServiceArrangement;
import com.lloydstsb.schema.enterprise.lcsm_arrangement.wz.ProductAccessArrangement;
import com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.VerifyProductArrangementDetailsRequest;
import com.lloydstsb.schema.enterprise.lcsm_common.wz.AlternateId;
import com.lloydstsb.schema.enterprise.lcsm_common.wz.ObjectReference;
import com.lloydstsb.schema.enterprise.lcsm_common.wz.RuleCondition;
import com.lloydstsb.schema.enterprise.lcsm_involvedparty.wz.*;
import com.lloydstsb.schema.enterprise.lcsm_resourceitem.wz.FinancialTransactionCard;
import lib_sim_bo.businessobjects.BalanceTransfer;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class VerifyProductArrangementDetailsRequestFactory {
    private static final Logger LOGGER = Logger.getLogger(VerifyProductArrangementDetailsRequestFactory.class);
    @Autowired
    PostalAddressHelper postalAddressHelper;
    private static final String RULE_CONDITION_BANK_ENHANCED = "BANK_ENHANCED";
    private static final String RULE_CONDITION_ID_ENHANCED = "ID_ENHANCED";
    private static final String RULE_CONDITION_ZODIAC = "ZODIAC";
    private static final String RULE_CONDITION_DELIVERY_FRAUD = "DELIVERY_FRAUD";
    private static final String RULE_CONDITION_EMAIL_VALIDATE = "EMAIL_VALIDATE";
    private static final String RULE_CONDITION_IP_ADDRESS = "IP_ADDRESS";
    private static final String RULE_CONDITION_BANK_STANDARD = "BANK_STANDARD";
    private static final String RULE_CONDITION_CARD_LIVE = "CARD_LIVE";
    private static final String RULE_CONDITION_CARD_ENHANCED = "CARD_ENHANCED";
    private static final String RULE_CONDITION_TRIGGER = "TRIGGER";
    private static final String RULE_CONDITION_RESULT_NO = "No";
    private static final String RULE_CONDITION_RESULT_YES = "Yes";
    private static final String RULE_CONDITION_RESULT_CALL_VALIDATE_CHECKS = "CALL_VALIDATE_CHECKS";
    private static final String INVOLVED_PARTY_ORGANIZATION = "FINANCIAL_INSTITUTION";
    private static final String INVOLVED_PARTY_INDIVIDUAL = "CUSTOMER";
    private static final String ATTRIBUTE_CARD_NUMBER = "CARD_NUMBER";
    private static final String ATTRIBUTE_ACCOUNT_NUMBER = "ACCOUNT_NUMBER";
    private static final String ATTRIBUTE_SORT_CODE = "BANK_SORT_CODE";

    public VerifyProductArrangementDetailsRequest convert(BalanceTransfer balanceTransfer, lib_sim_bo.businessobjects.Customer customer) {
        VerifyProductArrangementDetailsRequest request = new VerifyProductArrangementDetailsRequest();
        if (null != balanceTransfer) {
            boolean isCreditCardNumberPresent = false;
            boolean isAccountNumberPresent = false;
            if (!StringUtils.isEmpty(balanceTransfer.getCreditCardNumber())) {
                isCreditCardNumberPresent = true;
                FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
                financeServiceArrangement.getRoles().addAll(getRoleList(customer, balanceTransfer, isAccountNumberPresent));
                financeServiceArrangement.getHasObjectConditions().add(getRuleCondition(RULE_CONDITION_BANK_STANDARD, RULE_CONDITION_RESULT_NO));
                financeServiceArrangement.getHasObjectConditions().add(getRuleCondition(RULE_CONDITION_TRIGGER, RULE_CONDITION_RESULT_CALL_VALIDATE_CHECKS));

                ProductAccessArrangement productAccessArrangement = new ProductAccessArrangement();
                productAccessArrangement.getHasCards().add(createFinancialTransactionCard(balanceTransfer, isCreditCardNumberPresent, isAccountNumberPresent));

                ArrangementAssociation arrangementAssociation = new ArrangementAssociation();
                arrangementAssociation.setRelatedArrangement(productAccessArrangement);
                financeServiceArrangement.getArrangementAssociations().add(arrangementAssociation);
                request.getArrangementToVerify().add(financeServiceArrangement);
            }
            if (balanceTransfer.getCurrentAccountDetails() != null && !StringUtils.isEmpty(balanceTransfer.getCurrentAccountDetails().getSortCode()) && !StringUtils.isEmpty(balanceTransfer.getCurrentAccountDetails().getAccountNumber())) {
                isAccountNumberPresent = true;
                DepositArrangement depositArrangement = new DepositArrangement();
                depositArrangement.getRoles().addAll(getRoleList(customer, balanceTransfer, isAccountNumberPresent));
                depositArrangement.getHasObjectConditions().add(getRuleCondition(RULE_CONDITION_BANK_STANDARD, RULE_CONDITION_RESULT_NO));
                depositArrangement.getHasObjectConditions().add(getRuleCondition(RULE_CONDITION_TRIGGER, RULE_CONDITION_RESULT_CALL_VALIDATE_CHECKS));
                ProductAccessArrangement productAccessArrangement = new ProductAccessArrangement();
                productAccessArrangement.getHasCards().add(createFinancialTransactionCard(balanceTransfer, isCreditCardNumberPresent, isAccountNumberPresent));
                ArrangementAssociation arrangementAssociation = new ArrangementAssociation();
                arrangementAssociation.setRelatedArrangement(productAccessArrangement);
                depositArrangement.getArrangementAssociations().add(arrangementAssociation);
                request.getArrangementToVerify().add(depositArrangement);
            }
        }
        return request;
    }

    private PostalAddress createPostalAddress(List<lib_sim_bo.businessobjects.PostalAddress> postalAddressList) {
        PostalAddress postalAddress = null;
        for (lib_sim_bo.businessobjects.PostalAddress boPostalAddress : postalAddressList) {
            if (ActivateCommonConstant.AddressType.CURRENT.equalsIgnoreCase(boPostalAddress.getStatusCode())) {
                if (null != boPostalAddress.isIsPAFFormat() && boPostalAddress.isIsPAFFormat()) {
                    postalAddress = postalAddressHelper.createPostalAddressFromStructuredAddress(boPostalAddress.getStructuredAddress());
                } else {
                    postalAddress = postalAddressHelper.createPostalAddressFromUnstructuredAddress(boPostalAddress.getUnstructuredAddress());
                }
            }
        }
        return postalAddress;
    }

    private List<InvolvedPartyRole> getRoleList(lib_sim_bo.businessobjects.Customer customer, BalanceTransfer balanceTransfer, boolean isAccountNumberPresent) {
        List<InvolvedPartyRole> roleList = new ArrayList<>();
        Organization organization = new Organization();
        organization.getHasObjectConditions().add(getRuleCondition(RULE_CONDITION_BANK_ENHANCED, RULE_CONDITION_RESULT_NO));
        roleList.add(getRole(INVOLVED_PARTY_ORGANIZATION, organization));
        if (isAccountNumberPresent) {
            ObjectReference objectReference = new ObjectReference();
            objectReference.getAlternateId().add(createAlternateId(ATTRIBUTE_SORT_CODE, balanceTransfer.getCurrentAccountDetails().getSortCode()));
            roleList.get(0).setObjectReference(objectReference);
        }
        roleList.add(getRole(INVOLVED_PARTY_INDIVIDUAL, createIndividual(customer, isAccountNumberPresent)));
        return roleList;
    }

    private Individual createIndividual(lib_sim_bo.businessobjects.Customer customer, boolean isAccountNumberPresent) {
        Individual individual = new Individual();
        individual.getHasObjectConditions().add(getRuleCondition(RULE_CONDITION_ID_ENHANCED, RULE_CONDITION_RESULT_NO));
        individual.getHasObjectConditions().add(getRuleCondition(RULE_CONDITION_ZODIAC, RULE_CONDITION_RESULT_NO));
        ContactPreference contactPreference1 = new ContactPreference();
        contactPreference1.getHasObjectConditions().add(getRuleCondition(RULE_CONDITION_DELIVERY_FRAUD, RULE_CONDITION_RESULT_NO));
        individual.getContactPreferences().add(contactPreference1);
        ContactPreference contactPreference2 = new ContactPreference();
        contactPreference2.getHasObjectConditions().add(getRuleCondition(RULE_CONDITION_EMAIL_VALIDATE, RULE_CONDITION_RESULT_NO));
        individual.getContactPreferences().add(contactPreference2);
        ContactPreference contactPreference3 = new ContactPreference();
        contactPreference3.getHasObjectConditions().add(getRuleCondition(RULE_CONDITION_IP_ADDRESS, RULE_CONDITION_RESULT_NO));
        individual.getContactPreferences().add(contactPreference3);
        individual.getContactPoint().add(createPostalAddress(customer.getPostalAddress()));
        if (isAccountNumberPresent) {
            TelephoneNumber telephoneNumber = new TelephoneNumber();
            telephoneNumber.setFullNumber(customer.getTelephoneNumber().get(0).getPhoneNumber());
            individual.getContactPoint().add(telephoneNumber);
            ElectronicAddress electronicAddress = new ElectronicAddress();
            electronicAddress.setEmail(customer.getEmailAddress());
            individual.getContactPoint().add(electronicAddress);
            individual.setBirthDate(customer.getIsPlayedBy().getBirthDate());
            IndividualName individualName = new IndividualName();
            individualName.setPrefixTitle(customer.getIsPlayedBy().getIndividualName().get(0).getPrefixTitle());
            individualName.setFirstName(customer.getIsPlayedBy().getIndividualName().get(0).getFirstName());
            individualName.getLastName().add(customer.getIsPlayedBy().getIndividualName().get(0).getLastName());
            individual.getInvolvedPartyName().add(individualName);
        }
        return individual;
    }

    private RuleCondition getRuleCondition(String name, String result) {
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName(name);
        ruleCondition.setResult(result);
        return ruleCondition;
    }

    private InvolvedPartyRole getRole(String roleTypeValue, InvolvedParty involvedParty) {
        InvolvedPartyRole involvedPartyRole = new InvolvedPartyRole();
        InvolvedPartyRoleType involvedPartyRoleType = new InvolvedPartyRoleType();
        involvedPartyRoleType.setValue(roleTypeValue);
        involvedPartyRole.setType(involvedPartyRoleType);
        involvedPartyRole.setInvolvedParty(involvedParty);
        return involvedPartyRole;
    }
    private FinancialTransactionCard createFinancialTransactionCard(BalanceTransfer balanceTransfer, boolean isCreditCardNumberPresent, boolean isAccountNumberPresent) {
        FinancialTransactionCard financialTransactionCard = new FinancialTransactionCard();
        financialTransactionCard.getHasObjectConditions().add(getRuleCondition(RULE_CONDITION_CARD_LIVE, RULE_CONDITION_RESULT_YES));
        financialTransactionCard.getHasObjectConditions().add(getRuleCondition(RULE_CONDITION_CARD_ENHANCED, RULE_CONDITION_RESULT_NO));
        ObjectReference objectReference = new ObjectReference();
        if (isCreditCardNumberPresent) {
            objectReference.getAlternateId().add(createAlternateId(ATTRIBUTE_CARD_NUMBER, balanceTransfer.getCreditCardNumber()));
        } else if (isAccountNumberPresent) {
            objectReference.getAlternateId().add(createAlternateId(ATTRIBUTE_ACCOUNT_NUMBER, balanceTransfer.getCurrentAccountDetails().getAccountNumber()));
        }
        financialTransactionCard.setObjectReference(objectReference);
        financialTransactionCard.setExpirationDate(getDateFromStringPattern(balanceTransfer.getExpiryDate()));
        return financialTransactionCard;
    }
    private AlternateId createAlternateId(String attrString, String value) {
        AlternateId alternateId = new AlternateId();
        alternateId.setAttributeString(attrString);
        alternateId.setValue(value);
        return alternateId;
    }
    private XMLGregorianCalendar getDateFromStringPattern(String dateString) {
        XMLGregorianCalendar xmlGregorianCalendar = null;
        try {
            GregorianCalendar gc = new GregorianCalendar();
            Date dt = new SimpleDateFormat("MMyyyy").parse(dateString);
            gc.setTimeInMillis(dt.getTime());
            gc.setTimeZone(TimeZone.getTimeZone("GMT"));
            xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
            xmlGregorianCalendar.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
            xmlGregorianCalendar.setTime(DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED);
        } catch (DatatypeConfigurationException | ParseException e) {
            LOGGER.info("Error while parsing date: " + dateString + " Error: " + e);
        }
        return xmlGregorianCalendar;
    }

}
