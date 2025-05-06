package com.lloydsbanking.salsa.apacc.service.fulfil.convert;


import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.soap.fdi.f241.objects.CustomerDataSegment;
import com.lloydsbanking.salsa.soap.fdi.f241.objects.OwnerContactData;
import com.lloydsbanking.salsa.soap.fdi.f241.objects.OwnerData;
import com.lloydsbanking.salsa.soap.fdi.f241.objects.OwnerPersonalData;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.Individual;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class F241CustomerDataSegmentFactory {
    private static final int CID_PERS_ID_MAX_LENGTH = 10;

    @Autowired
    F241PostalAddressFactory f241PostalAddressFactory;

    @Autowired
    F241PhoneNumberFactory f241PhoneNumberFactory;


    public CustomerDataSegment getCustomerDataSegment(FinanceServiceArrangement financeServiceArrangement) {
        DateFactory dateFactory = new DateFactory();
        CustomerDataSegment customerDataSegment = new CustomerDataSegment();
        OwnerData ownerData = new OwnerData();
        OwnerContactData ownerContactData = new OwnerContactData();
        OwnerPersonalData ownerPersonalData = new OwnerPersonalData();
        if (financeServiceArrangement.isIsJointParty()) {
            Individual individual = financeServiceArrangement.getJointParties().get(0).getIsPlayedBy();
            customerDataSegment.setAcctCustNoSameIn("0");
            ownerData.setPartyTl(individual.getIndividualName().get(0).getPrefixTitle());
            ownerData.setBirthDt(dateFactory.convertXMLGregorianToStringDateFormat(individual.getBirthDate(), "yyyyMMdd"));
            ownerContactData.setTelephoneStatusCd("0");
            ownerContactData.setMobileStatusCd("0");
            ownerContactData.setEmailAddressIn("0");
            ownerContactData.setSMSComfirmationIn((short) 0);
            setNameDetails(individual, ownerContactData, ownerPersonalData);


        } else {
            Customer customer = financeServiceArrangement.getPrimaryInvolvedParty();
            if (customer.getCustomerNumber() != null) {
                customerDataSegment.setCustomerNumberExternalId(customer.getCustomerNumber());
                customerDataSegment.setAcctCustNoSameIn("0");
                customerDataSegment.setOwnershipCd((short) 1);
                customerDataSegment.setForeignUseIn("0");
                if (!customer.getTelephoneNumber().isEmpty()) {
                    f241PhoneNumberFactory.getPhoneDetails(customer, ownerData, ownerContactData);
                }
                if (!customer.getPostalAddress().isEmpty()) {
                    f241PostalAddressFactory.getPostalAddress(customer, ownerData, ownerPersonalData);
                }
                getEmailAddress(customer, ownerData, ownerContactData);
                ownerContactData.setSMSComfirmationIn((short) 1);
            } else {
                customerDataSegment.setAcctCustNoSameIn("0");
                setUniqueCustomerId(financeServiceArrangement, customerDataSegment);
                if (!customer.getPostalAddress().isEmpty()) {
                    f241PostalAddressFactory.getPostalAddress(customer, ownerData, ownerPersonalData);
                }
                ownerData.setPartyTl(customer.getIsPlayedBy().getIndividualName().get(0).getPrefixTitle());
                getEmailAddress(customer, ownerData, ownerContactData);
                ownerData.setBirthDt(dateFactory.convertXMLGregorianToStringDateFormat(customer.getIsPlayedBy().getBirthDate(), "yyyyMMdd"));
                if (!customer.getTelephoneNumber().isEmpty()) {
                    f241PhoneNumberFactory.getPhoneDetails(customer, ownerData, ownerContactData);
                }
                ownerContactData.setSMSComfirmationIn((short) 1);
                Individual individual1 = customer.getIsPlayedBy();
                setNameDetails(individual1, ownerContactData, ownerPersonalData);
            }
            customerDataSegment.setCreditCardMemorableWordTx(customer.getAccessToken().getEncryptedMemorableInfo());

        }

        customerDataSegment.setOwnershipCd((short) 1);
        customerDataSegment.setForeignUseIn("0");
        ownerContactData.setFaxStatusCd("0");
        customerDataSegment.setOwnerData(ownerData);
        customerDataSegment.setOwnerPersonalData(ownerPersonalData);
        customerDataSegment.setOwnerContactData(ownerContactData);
        return customerDataSegment;
    }

    private void setUniqueCustomerId(FinanceServiceArrangement financeServiceArrangement, CustomerDataSegment customerDataSegment) {
        String cidPersId = financeServiceArrangement.getPrimaryInvolvedParty().getCidPersID();
        String id = "";
        if (!StringUtils.isEmpty(cidPersId)) {
            id = cidPersId.length() > CID_PERS_ID_MAX_LENGTH ? cidPersId.substring(cidPersId.length() - CID_PERS_ID_MAX_LENGTH) : cidPersId;
        }
        String brandName = financeServiceArrangement.getAssociatedProduct().getBrandName();
        if (brandName != null) {
            if ("VER".equalsIgnoreCase(brandName)) {
                customerDataSegment.setUniqueCustomerId("000170000".concat(id));
            } else {
                customerDataSegment.setUniqueCustomerId("000120000".concat(id));
            }
        }
    }

    private void setNameDetails(Individual individual, OwnerContactData ownerContactData, OwnerPersonalData ownerPersonalData) {
        if ("001".equalsIgnoreCase(individual.getGender())) {
            ownerContactData.setGenderCd("1");
        } else if ("002".equalsIgnoreCase(individual.getGender())) {
            ownerContactData.setGenderCd("2");
        }
        ownerPersonalData.setFirstForeNm(individual.getIndividualName().get(0).getFirstName());
        if (individual.getIndividualName().get(0).getMiddleNames() != null && !individual.getIndividualName().get(0).getMiddleNames().isEmpty()) {
            ownerPersonalData.setFirstMiddleNm(individual.getIndividualName().get(0).getMiddleNames().get(0));
        }
        ownerPersonalData.setSurname(individual.getIndividualName().get(0).getLastName());
        ownerPersonalData.setFirstNationltyCd(individual.getNationality());
    }

    private void getEmailAddress(Customer customer, OwnerData ownerData, OwnerContactData ownerContactData) {
        ownerContactData.setEmailAddressIn("0");
        if (!StringUtils.isEmpty(customer.getEmailAddress())) {
            ownerData.setEmailAddressTx(customer.getEmailAddress());
            ownerContactData.setEmailAddressIn("1");
        }
    }
}
