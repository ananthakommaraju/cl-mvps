package com.lloydsbanking.salsa.ppae.client;

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

    public InvolvedPartyBuilder setPersonalDetails(String emailAddress, List<PostalAddress> address, List<TelephoneNumber> telephoneNumbers, Individual individual) {
        involvedParty.setEmailAddress(emailAddress);
        involvedParty.getPostalAddress().addAll(address);
        involvedParty.getTelephoneNumber().addAll(telephoneNumbers);
        involvedParty.setIsPlayedBy(individual);
        return this;
    }

    public InvolvedPartyBuilder setUserTypeAndPartyRole(String userType, String partyRole) {
        involvedParty.setUserType(userType);
        involvedParty.setPartyRole(partyRole);
        return this;
    }

    public InvolvedPartyBuilder internalUserIdentifier(String internalUserIdentifier) {
        involvedParty.setInternalUserIdentifier(internalUserIdentifier);
        return this;
    }


    public InvolvedPartyBuilder setCstSegmentAndSrcSystemId(String customerSegment, String sourceSystemId) {
        involvedParty.setCustomerSegment(customerSegment);
        involvedParty.setSourceSystemId(sourceSystemId);
        return this;
    }


    public InvolvedPartyBuilder customerIdentifier(String customerIdentifier) {
        involvedParty.setCustomerIdentifier(customerIdentifier);
        return this;
    }

    public InvolvedPartyBuilder hasExistingCreditCard(boolean hasExistingCreditCard) {
        if (hasExistingCreditCard) {
            involvedParty.isHasExistingCreditCard();
        }
        return this;
    }

    public InvolvedPartyBuilder setAccessTokenAndOtherBankAssTnDr(AccessToken accessToken, String otherBankAssctnDr) {
        involvedParty.setAccessToken(accessToken);
        involvedParty.setExistingAccountDuration(otherBankAssctnDr);
        return this;
    }


}
