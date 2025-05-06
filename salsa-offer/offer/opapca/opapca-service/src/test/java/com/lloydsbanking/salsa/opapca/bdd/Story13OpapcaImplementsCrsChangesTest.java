package com.lloydsbanking.salsa.opapca.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.pam.model.Individuals;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story13OpapcaImplementsCrsChangesTest extends AbstractOpapcaJBehaveTestBase{

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;
    int individualsSize;
    Individuals individuals;
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
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.getIsPlayedBy().getPreviousNationalities().add("IND");
        customer.getIsPlayedBy().getPreviousNationalities().add("PAK");

        customer.setTaxResidencyDetails(new TaxResidencyDetails());
        customer.getTaxResidencyDetails().getTaxResidencyCountries().add("US");
        customer.getTaxResidencyDetails().getTaxResidencyCountries().add("UKs");

        customer.getIdentificationDetails().add(new IdentificationDetails());
        customer.getIdentificationDetails().get(0).setCountryCode("01");
    }

    @When("UI calls OPAPCA with valid request")
    public void whenUICallsOPAPCAWithValidRequest() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, ParseException, DatatypeConfigurationException, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockScenarioHelper.expectF447Call(dataHelper.createOpaPcaRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaPcaRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaPcaRequestHeader("LTB"), "12345");

        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");

        mockScenarioHelper.expectF204Call(dataHelper.createOpaPcaRequestHeader("777505"), "1", dataHelper.createReferralCodeList("code", "description"));
        List<String> groupCodeList = new ArrayList<>();
        groupCodeList.add("CC_CROSS_SELL_FC");
        mockScenarioHelper.expectLookupListFromChannelAndGroupCodeList("LTB", groupCodeList);
        mockScenarioHelper.expectF205Call(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"), 8);

        mockScenarioHelper.expectRpcEmptyProductListResponse(request.getHeader());
        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());
        mockScenarioHelper.expectF062Call("CA", false, request.getProductArrangement().getPrimaryInvolvedParty(), dataHelper.createOpaPcaRequestHeader("LTB"), null, null, 0);
        mockScenarioHelper.expectEligibilityCa("true", dataHelper.createOpaPcaRequestHeader("LTB"), false,1);

        mockScenarioHelper.expectLookupDataForX711("LTB");
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        mockScenarioHelper.expectX711Call(customer, request.getHeader());


        individualsSize = mockScenarioHelper.expectIndividualsCreated();
        partyCountryAssociationsSize = mockScenarioHelper.expectPartyCountryAssociationCreated();


        mockControl.go();
        response = opaPcaClient.offerProductArrangement(request);

    }

    @Then("OPAPCA saves party contry associations with value and type in PAM")
    public void thenOPAPCASavesPartyContryAssociationsWithValueAndTypeInPAM() {
        assertEquals(individualsSize + 1, mockScenarioHelper.expectIndividualsCreated());
        assertEquals(partyCountryAssociationsSize + 5,mockScenarioHelper.expectPartyCountryAssociationCreated());
    }




}
