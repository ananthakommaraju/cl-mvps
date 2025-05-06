package com.lloydsbanking.salsa.offer.verify.convert;


import com.lloydsbanking.salsa.downstream.eidv.client.x711.PersonalCustomerAddressBuilder;
import com.lloydsbanking.salsa.downstream.eidv.client.x711.PersonalCustomerNameAndTelephoneBuilder;
import com.lloydsbanking.salsa.downstream.eidv.client.x711.X711RequestBuilder;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import lib_sim_bo.businessobjects.*;
import lloydstsb.schema.personal.customer.partyidandv.IdentifyParty;
import lloydstsb.schema.personal.customer.partyidandv.v0001.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;
import java.util.List;

public class RetrieveEIDVScoreRequestFactory {

    @Autowired
    ExceptionUtility exceptionUtility;

    private static final Logger LOGGER = Logger.getLogger(RetrieveEIDVScoreRequestFactory.class);

    public IdentifyParty create(Customer customer, String contactPointId) throws InternalServiceErrorMsg {
        Individual individual = customer.getIsPlayedBy();
        List<IndividualName> individualNameList = individual.getIndividualName();
        X711RequestBuilder builder = new X711RequestBuilder();
        IdentifyParty x711Req = builder.defaults()
                .setSortCode(contactPointId)
                .setPersonalCustomerDetails(customer.getCustomerIdentifier(), individual.getNationality()
                        , customer.getEmailAddress(), individual.getResidentialStatus(), individual.getEmploymentStatus(),
                        getPersonalCustomerAddress(customer.getPostalAddress()),
                        getPersonalCustomerNames(individualNameList, individual.getGender()), getPersonalCustomerTelephones(customer.getTelephoneNumber()))
                .setBirthDate(individual.getBirthDate().toGregorianCalendar().getTime())
                .setNoOfDependents(individual.getNumberOfDependents())
                .setEmploymentEffectiveFrom(individual.getCurrentEmploymentDuration())
                .setEmployerName(individual.getCurrentEmployer())
                .build();

        return x711Req;
    }

    private PersonalCustomerTelephones getPersonalCustomerTelephones(List<TelephoneNumber> telephoneNumbers) {
        PersonalCustomerTelephones customerTelephones = new PersonalCustomerTelephones();
        if (!CollectionUtils.isEmpty(telephoneNumbers)) {
            for (TelephoneNumber telephoneNumber : telephoneNumbers) {
                PersonalCustomerNameAndTelephoneBuilder customerNameAndTelephoneBuilder = new PersonalCustomerNameAndTelephoneBuilder();
                PersonalCustomerTelephone personalCustomerTelephone = customerNameAndTelephoneBuilder.type(telephoneNumber.getTelephoneType())
                        .countryPhoneCode(telephoneNumber.getCountryPhoneCode())
                        .nationalDestinationNumber(telephoneNumber.getAreaCode())
                        .subscriberNumber(telephoneNumber.getPhoneNumber())
                        .buildPersonalCustomerTelephone();
                customerTelephones.getPersonalCustomerTelephone().add(personalCustomerTelephone);
            }
        }
        return customerTelephones;
    }

    private PersonalCustomerAddresses getPersonalCustomerAddress(List<PostalAddress> postalAddressList) throws InternalServiceErrorMsg {
        PersonalCustomerAddressBuilder addressBuilder = new PersonalCustomerAddressBuilder();
        PersonalCustomerAddresses customerAddresses = addressBuilder.build();
        try {
            addressBuilder.personalCustomerAddress(postalAddressList);
        } catch (ParseException e) {
            LOGGER.error("ParseException occurred ;", e);
            throw exceptionUtility.internalServiceError(null, e.getMessage());
        } catch (DatatypeConfigurationException e) {
            LOGGER.error("DatatypeConfigurationException occurred ;", e);
            throw exceptionUtility.internalServiceError(null, e.getMessage());
        }
        return customerAddresses;
    }

    private PersonalCustomerNames getPersonalCustomerNames(List<IndividualName> individualNameList, String gender) {
        PersonalCustomerNames personalCustomerNames = new PersonalCustomerNames();
        PersonalCustomerNameAndTelephoneBuilder customerNameAndTelephoneBuilder = new PersonalCustomerNameAndTelephoneBuilder();
        for (IndividualName individualName : individualNameList) {
            PersonalCustomerName personalCustomerName = customerNameAndTelephoneBuilder.buildPersonalCustomerName();
            customerNameAndTelephoneBuilder.name(individualName.getLastName(), individualName.getFirstName(), individualName.getPrefixTitle())
                    .gender(gender)
                    .middleNames(individualName.getMiddleNames());
            personalCustomerNames.getPersonalCustomerName().add(personalCustomerName);
        }
        return personalCustomerNames;
    }
}

