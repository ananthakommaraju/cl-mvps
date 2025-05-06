package com.lloydsbanking.salsa.opasaving.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.offer.ApplicantType;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementInternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import java.text.ParseException;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class})
public class Story11OpasavingDeterminesCustomerProductConditionTest extends AbstractOpasavingJBehaveTestBase {

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;

    @Given("Customer is existing")
    public void givenCustomerIsExisting() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, InternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg {
        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");

        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaSavingRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaSavingRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaSavingRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectEligibility("true", dataHelper.createOpaSavingRequestHeader("LTB"), false, 1);
        mockScenarioHelper.expectRpc(request.getProductArrangement(), dataHelper.createOpaSavingRequestHeader("LTB"));
        mockScenarioHelper.expectDcpc(dataHelper.rpcResponse(), request.getProductArrangement(), dataHelper.createOpaSavingRequestHeader("LTB"));
        mockScenarioHelper.expectF204CallForExistingCustomers(dataHelper.createOpaSavingRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
        mockScenarioHelper.expectLookupListFromChannelAndEvidenceList("LTB");

        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());
    }

    @Given("Customer is new customer")
    public void givenCustomerIsNewCustomer() throws DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, ParseException {
        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");

        mockScenarioHelper.expectF447CallForNewCustomer(dataHelper.createOpaSavingRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockScenarioHelper.expectLookupListFromChannelAndEvidenceList("LTB");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaSavingRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectF336Call(dataHelper.createOpaSavingRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectEligibility("true", dataHelper.createOpaSavingRequestHeader("LTB"), false, 1);
        mockScenarioHelper.expectSwitchValueFromSwitchName("SW_SvngCstSegPrc", "LTB", 3, "0");
        Customer customer = dataHelper.createPrimaryInvolvedParty();
        mockScenarioHelper.expectF062Call("SA", false, customer, dataHelper.createOpaSavingRequestHeader("LTB"), null, null, 0);
        mockScenarioHelper.expectF204CallForExistingCustomers(dataHelper.createOpaSavingRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));

        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());
    }

    @Given("SCSP Switch is enabled")
    public void givenSCSPSwitchIsEnabled() {
        mockScenarioHelper.expectSwitchValueFromSwitchName("SW_SvngCstSegPrc", "LTB", 3, "1");
    }

    @Given("SCSP Switch is not enabled")
    public void givenSCSPSwitchIsNotEnabled() {
        mockScenarioHelper.expectSwitchValueFromSwitchName("SW_SvngCstSegPrc", "LTB", 3, "0");
    }

    @When("UI calls OpaSaving with valid request")
    public void whenUICallsOpaSavingWithValidRequest() throws Exception {
        mockControl.go();
        response = opasavingClient.offerProductArrangement(request);
    }

    @Then("OpaSaving return preferentialRateIdentifier in response")
    public void thenOpaSavingReturnPreferentialRateIdentifierInResponse() {
        assertEquals("pprid", response.getProductArrangement().getAssociatedProduct().getProductPreferentialRate().get(0).getPreferentialRateIdentifier());
    }

    @Then("OpaSaving returns valid response")
    public void thenOpaSavingReturnsValidResponse() {
        assertTrue(response.getProductArrangement().getAssociatedProduct().getProductPreferentialRate().isEmpty());
    }
}
