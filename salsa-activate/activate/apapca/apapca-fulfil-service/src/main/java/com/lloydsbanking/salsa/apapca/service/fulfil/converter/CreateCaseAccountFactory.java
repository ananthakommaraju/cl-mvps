package com.lloydsbanking.salsa.apapca.service.fulfil.converter;


import com.lloydsbanking.salsa.brand.Channel;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.xml.schema.pega.industryaccountswitching.*;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.DirectDebit;
import lib_sim_bo.businessobjects.Individual;
import lib_sim_bo.businessobjects.IndividualName;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class CreateCaseAccountFactory {
    public static final Logger LOGGER = Logger.getLogger(CreateCaseAccountFactory.class);
    private static final int LOWER_DATE_LIMIT = 0;
    private static final int UPPER_DATE_LIMIT = 2;
    private static final int LOWER_YEAR_LIMIT = 3;
    private static final int UPPER_YEAR_LIMIT = 5;
    public static final String PRIMARY = "PRIM";
    public static final String COUNTRY = "GB";

    public OldAccountType createOldAccountType(DirectDebit accountSwitchingDetails) {
        OldAccountType oldAccount = new OldAccountType();
        if (accountSwitchingDetails != null) {
            if (!StringUtils.isEmpty(accountSwitchingDetails.getSortCode())) {
                oldAccount.setSortCode(accountSwitchingDetails.getSortCode());
            }
            if (!StringUtils.isEmpty(accountSwitchingDetails.getAccountNumber())) {
                oldAccount.setAccountNumber(accountSwitchingDetails.getAccountNumber());
            }
            if (!StringUtils.isEmpty(accountSwitchingDetails.getAccountHolderName())) {
                oldAccount.setAccountName(accountSwitchingDetails.getAccountHolderName());
            }
            if (!StringUtils.isEmpty(accountSwitchingDetails.getBankName())) {
                oldAccount.setBankName(accountSwitchingDetails.getBankName());
            }
        }
        return oldAccount;
    }

    public NewAccountType createNewAccount(DepositArrangement depositArrangement, PartyPostalAddressType postalAddress, String channelId) {
        NewAccountType newAccount = new NewAccountType();
        if (depositArrangement.getAccountSwitchingDetails() != null
                && depositArrangement.getAccountSwitchingDetails().getAmount() != null && depositArrangement.getAccountSwitchingDetails().getAmount().getAmount() != null) {
            ActiveCurrencyAndAmountType amountType = new ActiveCurrencyAndAmountType();
            amountType.setValue(depositArrangement.getAccountSwitchingDetails().getAmount().getAmount());
            newAccount.setBalanceTransferFundingLimit(amountType);
        }
        if (depositArrangement.getFinancialInstitution() != null && !CollectionUtils.isEmpty(depositArrangement.getFinancialInstitution().getHasOrganisationUnits())) {
            if (!StringUtils.isEmpty(depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode())) {
                newAccount.setSortCode(depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode());
            }
        }
        if (!StringUtils.isEmpty(depositArrangement.getAccountNumber())) {
            newAccount.setAccountNumber(depositArrangement.getAccountNumber());
        }
        if (null != channelId) {
            newAccount.setBankName(getBrandFromChannel(channelId));
            newAccount.setBrand(getBrandFromChannel(channelId));
        }
        Individual individual = depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy();
        checkIndividual(individual, newAccount);
        newAccount.getAccountParty().add(getAccountParty(depositArrangement, postalAddress, createDebitCardDetailsType(depositArrangement.getAccountSwitchingDetails())));
        return newAccount;
    }

    private void checkIndividual(Individual individual, NewAccountType newAccount) {
        if (individual != null && !CollectionUtils.isEmpty(individual.getIndividualName())) {
            IndividualName individualName = individual.getIndividualName().get(0);
            if (!StringUtils.isEmpty(individualName.getFirstName())) {
                newAccount.setAccountName(individualName.getFirstName());
            }
            if (!CollectionUtils.isEmpty(individualName.getMiddleNames()) && !StringUtils.isEmpty(individualName.getMiddleNames().get(0))) {
                newAccount.setAccountName(individualName.getMiddleNames().get(0).concat(newAccount.getAccountName()));
            }
            if (!StringUtils.isEmpty(individualName.getLastName())) {
                newAccount.setAccountName(individualName.getLastName().concat(newAccount.getAccountName()));
            }
        }
    }

    private AccountPartyStructureType getAccountParty(DepositArrangement depositArrangement, PartyPostalAddressType postalAddress, DebitCardDetailsType debitCardDetails) {
        AccountPartyStructureType accountParty = new AccountPartyStructureType();
        if (depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy() != null) {
            if (depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getBirthDate() != null) {
                accountParty.setPartyBirthDate(depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getBirthDate());
            }
            if (depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getNationality() != null) {
                accountParty.setPartyNationality(depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getNationality());
            }
        }
        if (depositArrangement.getAccountSwitchingDetails() != null) {
            if (!StringUtils.isEmpty(depositArrangement.getAccountSwitchingDetails().getTextAlert())) {
                accountParty.setSendSMSUpdatesIndicator(Boolean.parseBoolean(depositArrangement.getAccountSwitchingDetails().getTextAlert()));
            }
            if (!StringUtils.isEmpty(depositArrangement.getAccountSwitchingDetails().getMobileNumber())) {
                accountParty.setMobilePhoneNumber(depositArrangement.getAccountSwitchingDetails().getMobileNumber());
            }
            if (!StringUtils.isEmpty(depositArrangement.getPrimaryInvolvedParty().getCustomerIdentifier())) {
                accountParty.setPartyId(depositArrangement.getPrimaryInvolvedParty().getCustomerIdentifier());
            }
        }
        accountParty.setAccountPartyType(PRIMARY);
        accountParty.setAuthorityIndicator(true);
        accountParty.setPartyCountryOfResidence(COUNTRY);
        accountParty.setOldDebitCardDetails(debitCardDetails);
        accountParty.setPartyPostalAddress(postalAddress);
        accountParty.setPartyName(createPartyName(depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy()));
        return accountParty;
    }

    private PartyNameType createPartyName(Individual individual) {
        PartyNameType partyName = new PartyNameType();
        if (individual != null && !CollectionUtils.isEmpty(individual.getIndividualName())) {
            IndividualName individualName = individual.getIndividualName().get(0);
            if (!StringUtils.isEmpty(individualName.getPrefixTitle())) {
                partyName.setNamePrefix(individualName.getPrefixTitle());
            }
            if (!StringUtils.isEmpty(individualName.getFirstName())) {
                partyName.setFirstName(individualName.getFirstName());
            }
            if (!CollectionUtils.isEmpty(individualName.getMiddleNames()) && !StringUtils.isEmpty(individualName.getMiddleNames().get(0))) {
                partyName.setSecondName(individualName.getMiddleNames().get(0));
            }
            if (!StringUtils.isEmpty(individualName.getLastName())) {
                partyName.setFamilyName(individualName.getLastName());
            }
        }
        return partyName;
    }

    private DebitCardDetailsType createDebitCardDetailsType(DirectDebit accountSwitchingDetails) {
        DebitCardDetailsType debitCardDetails = null;
        if (accountSwitchingDetails != null) {
            debitCardDetails = new DebitCardDetailsType();
            if (!StringUtils.isEmpty(accountSwitchingDetails.getCardNumber())) {
                debitCardDetails.setPAN(accountSwitchingDetails.getCardNumber());
            }
            if (!StringUtils.isEmpty(accountSwitchingDetails.getCardExpiryDate())) {
                String expiryDate = accountSwitchingDetails.getCardExpiryDate();
                debitCardDetails.setDebitCardExpiryDate(new DateFactory().stringToXMLGregorianCalendar(expiryDate.substring(LOWER_DATE_LIMIT, UPPER_DATE_LIMIT).concat("-").concat("20").concat(expiryDate.substring(LOWER_YEAR_LIMIT, UPPER_YEAR_LIMIT)), FastDateFormat.getInstance("MM-yyyy")));
            }
        }
        return debitCardDetails;
    }

    private static String getBrandFromChannel(String channel) {
        return Channel.getBrandForChannel(Channel.fromString(channel)).asString();
    }

}
