package com.lloydsbanking.salsa.eligibility.bdd;


import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalBusinessErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsResourceNotAvailableErrorMsg;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import lb_gbo_sales.messages.DetermineElegibleInstructionsResponse;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story21DeciReturnsEligibilityBasedOnExistingPpcTest extends AbstractDeciJBehaveTestBase {

    DetermineElegibileInstructionsRequest request;

    DetermineElegibleInstructionsResponse response;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("embeddedInsurance is unavailable")
    public void givenEmbeddedInsuranceIsUnavailable() {
        request = dataHelper.createEligibilityRequest("P_BT", TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        request.getCustomerArrangements().get(1).setHasEmbeddedInsurance(false);
        request.getCustomerArrangements().get(0).setHasEmbeddedInsurance(false);
        mockScenarioHelper.expectCompositeInstructionConditionCall("G_BT", "GR002", "No valid credit card account for balance transfer", "GR002", "CR012", "Credit card status must not be Stolen, Bankrupt or Charged off", "CR012", "GRT", "AGT", "10", "IBL", null);
    }

    @Given("embeddedInsurance is available")
    public void givenEmbeddedInsuranceIsAvailable() {
        request = dataHelper.createEligibilityRequest("P_BT", TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        request.getCustomerArrangements().get(1).setHasEmbeddedInsurance(true);
        request.getCustomerArrangements().get(0).setHasEmbeddedInsurance(true);
        mockScenarioHelper.expectCompositeInstructionConditionCall("G_BT", "GR002", "No valid credit card account for balance transfer", "GR002", "CR012", "Credit card status must not be Stolen, Bankrupt or Charged off", "CR012", "GRT", "AGT", "10", "IBL", null);
    }

    @When("the UI calls DECI with valid request")
    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID);

        mockControl.go();
        response = eligibilityClient.determineEligibleInstructions(request);
    }

    @Then("DECI evaluates eligibility as true")
    public void thenDECIEvaluatesEligibilityAsTrue() {
        assertTrue(response.getCustomerInstructions().get(0).isEligibilityIndicator());
    }


    @Then("DECI evaluates eligibility to false  and returns  errror condition  for  available embeddedInsurance")
    public void thenDECIEvaluatesEligibilityToFalseAndReturnsErrrorConditionForAvailableEmbeddedInsurance() {
        assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        assertEquals("CR012", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
        assertEquals("Customer does not have an eligible product for PPC", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonDescription());

    }

}










