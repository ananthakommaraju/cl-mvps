package com.lloydsbanking.salsa.opasaving.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
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

import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class})
public class Story14OpasavingRespondsToApiFailureTest extends AbstractOpasavingJBehaveTestBase {

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;

    private Throwable throwable;

    @BeforeScenario
    public void resetResponse() {
        request = null;
        response = null;
        throwable = null;
    }

    @Given("OCIS F447 is not available")
    public void givenOCISF447IsNotAvailable() {
        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");
    }

    @Given("OCIS F336 is not available")
    public void givenOCISF336IsNotAvailable() {
        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaSavingRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
    }

    @Given("OCIS F061 is not available")
    public void givenOCISF061IsNotAvailable() {
        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaSavingRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaSavingRequestHeader("LTB"), 1, 2, "12345");
    }

    @Given("DECI is not available")
    public void givenDECIIsNotAvailable() {
        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaSavingRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaSavingRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaSavingRequestHeader("LTB"), "12345");
    }

    @Given("F204 is not available")
    public void givenF204IsNotAvailable() throws DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg {
        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaSavingRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaSavingRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaSavingRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectEligibility("true", dataHelper.createOpaSavingRequestHeader("LTB"), false, 1);
        mockScenarioHelper.expectSwitchValueFromSwitchName("SW_SvngCstSegPrc", "LTB", 3, "0");
    }

    @Given("RPC is not available")
    public void givenRPCIsNotAvailable() throws DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaSavingRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaSavingRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaSavingRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectEligibility("true", dataHelper.createOpaSavingRequestHeader("LTB"), false, 1);
        mockScenarioHelper.expectSwitchValueFromSwitchName("SW_SvngCstSegPrc", "LTB", 3, "1");
    }

    @Given("F062 is not available")
    public void givenF062IsNotAvailable() throws DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg {
        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");
        mockScenarioHelper.expectF447CallForNewCustomer(dataHelper.createOpaSavingRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectEligibility("true", dataHelper.createOpaSavingRequestHeader("LTB"), false, 1);
        mockScenarioHelper.expectSwitchValueFromSwitchName("SW_SvngCstSegPrc", "LTB", 3, "0");
    }

    @Given("X711 is not available")
    public void givenX711IsNotAvailable() throws DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg {
        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaSavingRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaSavingRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061CallWithoutEvidenceData(dataHelper.createOpaSavingRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectEligibility("true", dataHelper.createOpaSavingRequestHeader("LTB"), false, 1);
        mockScenarioHelper.expectSwitchValueFromSwitchName("SW_SvngCstSegPrc", "LTB", 3, "0");
        mockScenarioHelper.expectLookupDataForX711Decline("LTB");
    }

    @Given("F447 returns error code in response")
    public void givenF447ReturnsErrorCodeInResponse() {
        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");
        mockScenarioHelper.expectF447CallWithError(dataHelper.createOpaSavingRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
    }

    @Given("F336 returns error code in response")
    public void givenF336ReturnsErrorCodeInResponse() {
        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaSavingRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336CallWithError(dataHelper.createOpaSavingRequestHeader("LTB"), 1, 2, "12345");
    }

    @Given("DCPC is not available")
    public void givenDCPCIsNotAvailable() throws DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaSavingRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaSavingRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaSavingRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectEligibility("true", dataHelper.createOpaSavingRequestHeader("LTB"), false, 1);
        mockScenarioHelper.expectSwitchValueFromSwitchName("SW_SvngCstSegPrc", "LTB", 3, "1");
        mockScenarioHelper.expectRpc(request.getProductArrangement(), dataHelper.createOpaSavingRequestHeader("LTB"));
    }

    @When("UI calls Opasaving with valid request")
    public void whenUICallsOpasavingWithValidRequest() {
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");

        mockControl.go();
        try {
            response = opasavingClient.offerProductArrangement(request);
        } catch (Exception e) {
            throwable = e;
        }
    }

    @Then("Opasaving throws OfferProductArrangementResourceNotAvailableErrorMsg In response")
    public void thenOpasavingThrowsOfferProductArrangementResourceNotAvailableErrorMsgInResponse() {
        assertTrue(throwable instanceof OfferProductArrangementResourceNotAvailableErrorMsg);
    }

    @Then("Opasaving throws OfferProductArrangementInternalServiceErrorMsg In response")
    public void thenOpasavingThrowsOfferProductArrangementInternalServiceErrorMsgInResponse() {
        assertTrue(throwable instanceof OfferProductArrangementInternalServiceErrorMsg);
    }

    @Then("Opasaving throws OfferProductArrangementExternalBusinessErrorMsg In response")
    public void thenOpasavingThrowsOfferProductArrangementExternalBusinessErrorMsgInResponse() {
        assertTrue(throwable instanceof OfferProductArrangementExternalBusinessErrorMsg);
    }

    @Then("Opasaving throws OfferProductArrangementExternalServiceErrorMsg In response")
    public void thenOpasavingThrowsOfferProductArrangementExternalServiceErrorMsgInResponse() {
        assertTrue(throwable instanceof OfferProductArrangementExternalServiceErrorMsg);
    }
}
