package com.lloydsbanking.salsa.opasaving.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.offer.ApplicantType;
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class DestructiveTests extends AbstractOpasavingJBehaveTestBase {

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;
    private Throwable throwable;

    @BeforeScenario
    public void resetResponse() {
        request = null;
        response = null;
        throwable = null;
    }

    @Given("an invalid email address is given in the request to a Salsa service")
    public void givenInvalidEmailAddressInSalsaRequest() throws ParseException, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DatatypeConfigurationException, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB", ".Not a valid email address+{±");
        setHappyPathExpectedCalls();
        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());
    }

    @Given("the UI prepares a request without a Product Arrangement set")
    public void givenUIPreparesReqWithoutFieldSet() throws ParseException, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DatatypeConfigurationException, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");
        setHappyPathExpectedCalls();
        request.setProductArrangement(null);
    }

    @Given("the UI prepares a request without a Primary Involved Party set")
    public void givenUIPreparesReqWithoutPrimaryInvolvedPartySet() throws ParseException, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DatatypeConfigurationException, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");
        setHappyPathExpectedCalls();
        request.getProductArrangement().setPrimaryInvolvedParty(null);
    }

    @When("the UI calls OPASaving")
    public void whenUICallsOPAPCAWithValidRequest() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg {
        mockControl.go();
        try {
            response = opasavingClient.offerProductArrangement(request);
        } catch (Exception e) {
            throwable = e;
        }
    }

    @Then("OPASaving returns valid response")
    public void thenOPAPCAReturnsValidResponse() {
        assertNotNull(response);
    }

    @Then("OPASaving throws an Internal Service Error")
    public void opaSavingReturnsInternalServiceError() {
        assertTrue(throwable instanceof OfferProductArrangementInternalServiceErrorMsg);
    }

    private void setHappyPathExpectedCalls() throws ParseException, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DatatypeConfigurationException, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaSavingRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaSavingRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaSavingRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectEligibility("true", dataHelper.createOpaSavingRequestHeader("LTB"), false, 1);
        mockScenarioHelper.expectSwitchValueFromSwitchName("SW_SvngCstSegPrc", "LTB", 3, "0");
        mockScenarioHelper.expectF204CallForExistingCustomers(dataHelper.createOpaSavingRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
        mockScenarioHelper.expectLookupListFromChannelAndEvidenceList("LTB");
    }
}
