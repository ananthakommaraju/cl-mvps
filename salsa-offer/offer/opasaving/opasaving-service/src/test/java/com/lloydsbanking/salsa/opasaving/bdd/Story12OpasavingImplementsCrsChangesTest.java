package com.lloydsbanking.salsa.opasaving.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.offer.ApplicantType;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.IdentificationDetails;
import lib_sim_bo.businessobjects.TaxResidencyDetails;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;

import static junit.framework.Assert.assertEquals;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story12OpasavingImplementsCrsChangesTest extends AbstractOpasavingJBehaveTestBase {

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;
    int individualsSize;
    int partyCountryAssociationsSize;

    @BeforeScenario
    public void resetResponse() {
        request = null;
        response = null;
        individualsSize = 0;
        partyCountryAssociationsSize = 0;
    }

    @Given("nationality is present")
    public void givenNationalityIsPresent() {
        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.getIsPlayedBy().getPreviousNationalities().add("IND");
        customer.getIsPlayedBy().getPreviousNationalities().add("PAK");
        customer.setTaxResidencyDetails(new TaxResidencyDetails());
        customer.getTaxResidencyDetails().getTaxResidencyCountries().add("US");
        customer.getTaxResidencyDetails().getTaxResidencyCountries().add("UKs");
        customer.getIdentificationDetails().add(new IdentificationDetails());
        customer.getIdentificationDetails().get(0).setCountryCode("01");
    }

    @When("UI calls OpaSaving with valid request")
    public void whenUICallsOPAPCAWithValidRequest() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, ParseException, DatatypeConfigurationException, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaSavingRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336CallForAccept(dataHelper.createOpaSavingRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaSavingRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectEligibility("true", dataHelper.createOpaSavingRequestHeader("LTB"), false, 1);
        mockScenarioHelper.expectSwitchValueFromSwitchName("SW_SvngCstSegPrc", "LTB", 3, "0");
        mockScenarioHelper.expectF204CallForExistingCustomers(dataHelper.createOpaSavingRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
        mockScenarioHelper.expectLookupListFromChannelAndEvidenceList("LTB");
        individualsSize = mockScenarioHelper.expectIndividualsCreated();
        partyCountryAssociationsSize = mockScenarioHelper.expectPartyCountryAssociationCreated();

        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.SELF.getValue());

        mockControl.go();
        System.out.println("request"+request.getProductArrangement().getPrimaryInvolvedParty());
        response = opasavingClient.offerProductArrangement(request);
    }

    @Then("OpaSaving saves party contry associations with value and type in PAM")
    public void thenOPAPCASavesPartyContryAssociationsWithValueAndTypeInPAM() {
        assertEquals(individualsSize + 1, mockScenarioHelper.expectIndividualsCreated());
        assertEquals(partyCountryAssociationsSize + 5, mockScenarioHelper.expectPartyCountryAssociationCreated());
    }
}
