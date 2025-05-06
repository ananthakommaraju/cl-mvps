package com.lloydsbanking.salsa.offer;

import lib_sim_bo.businessobjects.*;

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

    public InvolvedPartyBuilder sourceSystemId(String sourceSystemId) {
        involvedParty.setSourceSystemId(sourceSystemId);
        return this;
    }

    public InvolvedPartyBuilder customerIdentifier(String customerIdentifier) {
        involvedParty.setCustomerIdentifier(customerIdentifier);
        return this;
    }

    public InvolvedPartyBuilder customerScore(List<CustomerScore> customerScore) {
        involvedParty.getCustomerScore().addAll(customerScore);
        return this;
    }

    public InvolvedPartyBuilder existingSortCode(String existingSortCode) {
        involvedParty.setExistingSortCode(existingSortCode);
        return this;
    }

    public InvolvedPartyBuilder existingAccountNumber(String existingAccountNumber) {
        involvedParty.setExistingAccountNumber(existingAccountNumber);
        return this;
    }

    public InvolvedPartyBuilder hasExistingCreditCard(boolean hasExistingCreditCard) {
        if (hasExistingCreditCard) {
            involvedParty.isHasExistingCreditCard();
        }
        return this;
    }

    public InvolvedPartyBuilder accessToken(AccessToken accessToken) {
        involvedParty.setAccessToken(accessToken);
        return this;
    }


}
