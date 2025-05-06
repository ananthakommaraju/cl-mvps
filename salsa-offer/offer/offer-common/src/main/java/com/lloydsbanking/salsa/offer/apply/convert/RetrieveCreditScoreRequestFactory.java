package com.lloydsbanking.salsa.offer.apply.convert;

import com.lloydsbanking.salsa.downstream.asm.client.f205.*;
import com.lloydsbanking.salsa.soap.asm.f205.objects.*;
import lib_sim_bo.businessobjects.*;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class RetrieveCreditScoreRequestFactory {
    public F205Req create(String contactPointId, String arrangementId, String associatedProductBrandName, List<ExtSysProdIdentifier> externalSystemProductIdentifiers, Customer primaryInvolvedParty, String areaCode, String regionCode, String accountPurpose, List<Product> existingProducts,List<RuleCondition> ruleConditionList) throws ParseException {
        F205RequestBuilder builder = new F205RequestBuilder();
        F205Req f205Req = builder.defaults()
                .creditScoreRequestNumber(arrangementId)
                .csSecondaryAccountIn(existingProducts, associatedProductBrandName)
                .productId(externalSystemProductIdentifiers)
                .regionCd(regionCode)
                .areaCd(areaCode)
                .sortCd(contactPointId)
                .build();
        if (primaryInvolvedParty != null) {
            f205Req.getPersonalDetails()
                    .add(retrievePersonalDetails(primaryInvolvedParty.getIsPlayedBy(),
                            primaryInvolvedParty.getSourceSystemId(),
                            primaryInvolvedParty.getCidPersID(),
                            primaryInvolvedParty.getCbsCustomerNumber(),
                            existingProducts, associatedProductBrandName,
                            primaryInvolvedParty.getCustomerSegment(),
                            primaryInvolvedParty.getCustomerIdentifier(),
                            primaryInvolvedParty.getOtherBankDuration(),
                            primaryInvolvedParty.getTelephoneNumber(),
                            ruleConditionList,externalSystemProductIdentifiers));
            if (!f205Req.getPersonalDetails().isEmpty()) {
                PersonalDetails personalDetails = f205Req.getPersonalDetails().get(0);
                personalDetails.setIncomeExpenditureDetails(retrieveIncomeExpenditureDetails((primaryInvolvedParty.getIsPlayedBy()), accountPurpose));
                personalDetails.getAddressDetails().addAll(retrieveAddressDetails(primaryInvolvedParty.getPostalAddress()));
                personalDetails.setOtherBankDetails(retrieveOtherBankDetails(primaryInvolvedParty.getOtherBankDuration(),ruleConditionList));
            }
        }
        return f205Req;
    }


    private List<AddressDetails> retrieveAddressDetails(List<PostalAddress> postalAddresses) {
        List<AddressDetails> addressDetailsList = new ArrayList<>();
        if (postalAddresses != null) {
            for (PostalAddress postalAddress : postalAddresses) {
                short durationOfStay = postalAddress != null &&
                        postalAddress.getDurationofStay() != null ? Short.parseShort(postalAddress.getDurationofStay()) : 0;
                AddressDetails addressDetails = new AddressDetailsBuilder().defaults()
                        .addressResidenceDr(durationOfStay)
                        .address(postalAddress)
                        .build();
                addressDetailsList.add(addressDetails);
            }
        }
        return addressDetailsList;
    }

    private PersonalDetails retrievePersonalDetails(Individual isPlayedBy, String sourceSystemId, String cidPersId, String cbsCustomerNumber, List<Product> existingProducts, String associatedProductBrandName, String customerSegment, String customerIdentifier, String otherBankDuration, List<TelephoneNumber> telephoneNumbers,List<RuleCondition> ruleConditionList, List<ExtSysProdIdentifier> extSysProdIdentifierList) throws ParseException {
        PersonalDetailsBuilder builder = new PersonalDetailsBuilder();
        int customerIdentifierInt = customerIdentifier != null ? Integer.parseInt(customerIdentifier) : 0;
        builder.defaults().bankersAssociationDr(existingProducts, associatedProductBrandName);
        builder.telephoneDetails(telephoneNumbers);
        builder.partyIdentifiersAndFinancialDetails(sourceSystemId, cidPersId, cbsCustomerNumber,ruleConditionList,extSysProdIdentifierList);
        if (isPlayedBy != null) {
            String currentAreaOfStudy = (isPlayedBy.getCurrentYearOfStudy() != null) ? String.valueOf(isPlayedBy.getCurrentYearOfStudy()) : null;
            short currentEmploymentDuration = isPlayedBy.getCurrentEmploymentDuration() != null ? Short.parseShort(isPlayedBy.getCurrentEmploymentDuration()) : 0;
            boolean isStaffMember = isPlayedBy.isIsStaffMember() != null ? isPlayedBy.isIsStaffMember() : false;
            builder.setPersonalDetails(isPlayedBy.getMaritalStatus(), isPlayedBy.getEmploymentStatus(), currentEmploymentDuration, customerIdentifierInt, isPlayedBy.getGender(), customerSegment, isStaffMember, isPlayedBy.getOccupation(), isPlayedBy.getNumberOfDependents(), isPlayedBy.getResidentialStatus(), currentAreaOfStudy);
            builder.setBirthDtUKResidencytOtherBankInGraduation(convertXMLGregorianCalenderToDate(isPlayedBy.getBirthDate()), otherBankDuration, isPlayedBy.getAnticipateDateOfGraduation(), convertXMLGregorianCalenderToDate(isPlayedBy.getUKResidenceStartDate()));
            if (!isPlayedBy.getIndividualName().isEmpty()) {
                IndividualName individualName = isPlayedBy.getIndividualName().get(0);
                builder.names(individualName.getMiddleNames(), individualName.getFirstName(), individualName.getLastName(), individualName.getPrefixTitle());
                BigDecimal totalSavingsAmount = isPlayedBy.getTotalSavingsAmount() != null ? isPlayedBy.getTotalSavingsAmount().getAmount() : null;
                builder.setOtherPersonalDetails(totalSavingsAmount, isPlayedBy.getNationality());
            }
        }
        return builder.build();
    }

    private IncomeExpenditureDetails retrieveIncomeExpenditureDetails(Individual isPlayedBy, String accountPurpose) {
        IncomeExpenditureDetailsBuilder builder = new IncomeExpenditureDetailsBuilder();
        builder.defaults().mandatedSalaryIn(accountPurpose);
        if (isPlayedBy != null) {
            builder.periodNetIncomeAm(isPlayedBy.getNetMonthlyIncome());
            builder.loanCmmtmtMnthlyAm(isPlayedBy.getMonthlyLoanRepaymentAmount());
            builder.mnthlyAccmmnPaymntAm(isPlayedBy.getMonthlyMortgageAmount());
            builder.otherAnnualIncomeAm(isPlayedBy.getOtherMonthlyIncomeAmount());
        }
        return builder.build();
    }

    private OtherBankDetails retrieveOtherBankDetails(String otherBankDuration,List<RuleCondition> ruleConditionList) {
        short otherBankDurationShort = otherBankDuration != null ? Short.parseShort(otherBankDuration) : 0;
        return new OtherBankDetailsBuilder().defaults().otherBankAssctnDr(otherBankDurationShort).setOtherBankStatusCd(ruleConditionList).build();
    }

    private Date convertXMLGregorianCalenderToDate(XMLGregorianCalendar xmlDate) {
        if (xmlDate == null) {
            return null;
        } else {
            return xmlDate.toGregorianCalendar().getTime();
        }
    }
}
