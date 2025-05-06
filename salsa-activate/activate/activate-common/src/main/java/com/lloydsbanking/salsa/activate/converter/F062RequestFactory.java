package com.lloydsbanking.salsa.activate.converter;

import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.ocis.client.f062.*;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.*;
import lib_sim_bo.businessobjects.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class F062RequestFactory {

    private static final String ADDRESS_TYPE_CURRENT = "CURRENT";
    private static final String ARRANGEMENT_TYPE_SAVINGS = "SA";
    private static final String INDICATOR_TYPE_YES = "Y";
    private static final String INDICATOR_TYPE_NO = "N";
    private static final String APPLICANT_TYPE_DEPENDENT = "01";
    private static final String ADDRESS_TYPE_CODE_OLD = "002";
    private static final String ADDRESS_TYPE_CODE_CURRENT = "001";
    @Autowired
    StructuredAddressFactory structuredAddressFactory;
    @Autowired
    UnstructuredAddressFactory unstructuredAddressFactory;

    public F062Req convert(Customer customer, String arrangementType, AssessmentEvidence assessmentEvidence) {
        F062RequestBuilder f062RequestBuilder = new F062RequestBuilder();
        Long partyId = Long.parseLong(customer.getCustomerIdentifier());
        Individual individual = customer.getIsPlayedBy();
        List<AssessmentEvidence> assessmentEvidenceList = new ArrayList<>();
        assessmentEvidenceList.add(assessmentEvidence);

        f062RequestBuilder.defaults()
                .personalUpdBuilderAndPartyTI(getPersonalUpdDataType(customer.getIsPlayedBy(), customer.getApplicantType(), arrangementType), getPartyNonCoreUpdDataType(customer.getIsPlayedBy()), partyId)
                .kYCNonCorePartyUpdData(null, individual.getCurrentEmployer().getName(), null, null)
                .kYCPartyUpdData(null, null, null, null, null, individual.getNationality(), null)
                .partyUpdData(assessmentEvidenceList, null, getAddressUpdData(customer.getPostalAddress()), getPhoneUpdData(customer));


        if (individual.getCurrentEmployer() != null && !individual.getCurrentEmployer().getHasPostalAddress().isEmpty() && individual.getCurrentEmployer().getHasPostalAddress().get(0) != null) {
            UnstructuredAddress unstructuredAddress = customer.getIsPlayedBy().getCurrentEmployer().getHasPostalAddress().get(0).getUnstructuredAddress();
            f062RequestBuilder.employerAddressUpdData(unstructuredAddress.getAddressLine1(), unstructuredAddress.getAddressLine2(), unstructuredAddress.getAddressLine3(), unstructuredAddress.getAddressLine4(),
                    unstructuredAddress.getAddressLine5(), unstructuredAddress.getAddressLine6(), unstructuredAddress.getAddressLine7(), unstructuredAddress.getPostCode());
        }

        return f062RequestBuilder.build();
    }

    private List<PhoneUpdDataType> getPhoneUpdData(Customer customer) {
        List<PhoneUpdDataType> phoneUpdDataTypeList = new ArrayList<>();
        if (!customer.getTelephoneNumber().isEmpty() && customer.getTelephoneNumber().get(0) != null) {
            TelephoneNumber telephoneNumber = customer.getTelephoneNumber().get(0);
            PhoneUpdDataType phoneUpdDataType = new PhoneUpdDataBuilder().phoneUpdData(telephoneNumber.getTelephoneType(), telephoneNumber.getDeviceType(), telephoneNumber.getCountryPhoneCode(),
                    telephoneNumber.getAreaCode(), telephoneNumber.getPhoneNumber(), telephoneNumber.getExtensionNumber(), telephoneNumber.getContactPreference()).build();
            phoneUpdDataTypeList.add(phoneUpdDataType);
        }
        return phoneUpdDataTypeList;
    }

    private AddressUpdDataType getAddressUpdData(List<PostalAddress> postalAddressList) {
        AddressUpdDataType addressUpdDataType = null;
        if (!postalAddressList.isEmpty() && postalAddressList.get(0) != null) {
            PostalAddress postalAddress = postalAddressList.get(0);
            UnstructuredAddressType unstructuredAddressType = unstructuredAddressFactory.generateUnstructuredAddress(postalAddress.getUnstructuredAddress(), postalAddress.isIsBFPOAddress());
            StructuredAddressType structuredAddressType = structuredAddressFactory.generateStructuredAddress(postalAddress.getStructuredAddress());
            addressUpdDataType = new AddressUpdDataBuilder().addressUpdData(getAmdEffDt(postalAddressList), getAddressStatusCode(postalAddressList),
                    postalAddress.getCareOfName(), unstructuredAddressType, structuredAddressType).build();
        }
        return addressUpdDataType;
    }

    private PartyNonCoreUpdDataType getPartyNonCoreUpdDataType(Individual individual) {
        PartyNonCoreUpdDataBuilder partyNonOCreUpdDataBuilder = new PartyNonCoreUpdDataBuilder();
        partyNonOCreUpdDataBuilder.setStaffDetails("0", null)
                .employmentStatusCd(individual.getEmploymentStatus())
                .maritalStatusCd(individual.getMaritalStatus())
                .occupationalRoleCd(individual.getOccupation())
                .residentialStatusCdAuditDtAuditTm(individual.getResidentialStatus(), null, null);

        return partyNonOCreUpdDataBuilder.build();
    }

    private PersonalUpdDataType getPersonalUpdDataType(Individual individual, String applicantType, String arrangementType) {
        IndividualName individualName = individual.getIndividualName().get(0);
        PersonalUpdDataBuilder personalUpdDataBuilder = new PersonalUpdDataBuilder();
        personalUpdDataBuilder.birthDtGenderCdPartyTypeCd(new DateFactory().convertXMLGregorianToDateFormat(individual.getBirthDate()), individual.getGender())
                .namesAndPartyTI(individualName.getPrefixTitle(), individualName.getLastName(), individualName.getFirstName(),
                        individualName.getMiddleNames());

        PersonalUpdDataType personalUpdDataType = personalUpdDataBuilder.build();
        setMarketingPref(applicantType, arrangementType, personalUpdDataType, false);
        return personalUpdDataType;
    }

    private String getAmdEffDt(List<PostalAddress> postalAddressList) {
        String amdEffFrom = null;
        DateFactory dateFactory = new DateFactory();
        if (!postalAddressList.isEmpty()) {
            for (PostalAddress postalAddress : postalAddressList) {
                if (postalAddress.getStatusCode().equalsIgnoreCase(ADDRESS_TYPE_CURRENT)) {
                    if (null != postalAddress.getEffectiveFrom()) {
                        amdEffFrom = dateFactory.convertXMLGregorianToStringDateFormat(postalAddress.getEffectiveFrom(), "ddMMyyyy");
                    } else if (null != postalAddress.getDurationofStay()) {
                        amdEffFrom = dateFactory.convertDurationYYMMToStringDateFormat(postalAddress.getDurationofStay(), "ddMMyyyy");
                    }
                }
            }
        }
        return amdEffFrom;
    }

    private void setMarketingPref(String applicantType, String arrangementType, PersonalUpdDataType personalUpdDataType, boolean marketingPref) {
        if (arrangementType.equalsIgnoreCase(ARRANGEMENT_TYPE_SAVINGS)) {
            if (!applicantType.equals(APPLICANT_TYPE_DEPENDENT)) {
                personalUpdDataType.setPhoneMkt(new PhoneMktType());
                personalUpdDataType.setMailMkt(new MailMktType());
                if (marketingPref) {
                    personalUpdDataType.getMailMkt().setMktAuthMailIn(INDICATOR_TYPE_YES);
                    personalUpdDataType.getPhoneMkt().setMktAuthPhoneIn(INDICATOR_TYPE_YES);
                } else {
                    personalUpdDataType.getMailMkt().setMktAuthMailIn(INDICATOR_TYPE_NO);
                    personalUpdDataType.getPhoneMkt().setMktAuthPhoneIn(INDICATOR_TYPE_NO);
                }
            }
        }
    }

    private String getAddressStatusCode(List<PostalAddress> postalAddressList) {
        return postalAddressList.get(0).getStatusCode().equalsIgnoreCase(ADDRESS_TYPE_CURRENT) ? ADDRESS_TYPE_CODE_CURRENT : ADDRESS_TYPE_CODE_OLD;
    }
}
