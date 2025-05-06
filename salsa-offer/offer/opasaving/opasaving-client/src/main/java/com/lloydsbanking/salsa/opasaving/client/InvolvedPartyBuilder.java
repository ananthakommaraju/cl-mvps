package com.lloydsbanking.salsa.opasaving.client;

import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.Individual;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_bo.businessobjects.TelephoneNumber;

import java.util.List;

public class InvolvedPartyBuilder {
    Customer involvedParty;

    public InvolvedPartyBuilder() {
        this.involvedParty = new Customer();
    }

    public Customer build() {
        return involvedParty;
    }

    public InvolvedPartyBuilder partyIdentifier(String partIdentifier) {
        involvedParty.setPartyIdentifier(partIdentifier);
        return this;
    }

    public InvolvedPartyBuilder applicationType(String applicationType) {
        involvedParty.setApplicantType(applicationType);
        return this;
    }

    public InvolvedPartyBuilder emailAddress(String emailAddress) {
        involvedParty.setEmailAddress(emailAddress);
        return this;
    }

    public InvolvedPartyBuilder postalAddress(List<PostalAddress> address) {
        involvedParty.getPostalAddress().addAll(address);
        return this;
    }

    public InvolvedPartyBuilder telephoneNumber(List<TelephoneNumber> telephoneNumbers) {
        involvedParty.getTelephoneNumber().addAll(telephoneNumbers);
        return this;
    }

    public InvolvedPartyBuilder isPlayedBy(Individual individual) {
        involvedParty.setIsPlayedBy(individual);
        return this;
    }

    public InvolvedPartyBuilder userType(String userType) {
        involvedParty.setUserType(userType);
        return this;
    }

    public InvolvedPartyBuilder internalUserIdentifier(String internalUserIdentifier) {
        involvedParty.setInternalUserIdentifier(internalUserIdentifier);
        return this;
    }

    public InvolvedPartyBuilder partyRole(String partyRole) {
        involvedParty.setPartyRole(partyRole);
        return this;
    }

    public InvolvedPartyBuilder customerSegment(String customerSegment) {
        involvedParty.setCustomerSegment(customerSegment);
        return this;
    }

    public InvolvedPartyBuilder otherBankDuration(String otherBankDuration) {
        involvedParty.setOtherBankDuration(otherBankDuration);
        return this;
    }

}
