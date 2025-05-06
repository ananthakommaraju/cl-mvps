package com.lloydsbanking.salsa.eligibility.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
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
public class Story13DeciReturnEligibilityBasedOnCreditCardStatusRuleTest extends AbstractDeciJBehaveTestBase {
    DetermineElegibileInstructionsRequest request;

    DetermineElegibleInstructionsResponse response;

    @BeforeScenario
    public void resetData() {
        request = null;
        response = null;
    }

    @Given("CR007 rules is configured")
    public void givenCR007RulesIsConfigured() {
        mockScenarioHelper.expectCompositeInstructionConditionCall("G_BT", "GR002", "No valid credit card account for balance transfer", "GR002", "CR007", "Credit card status must not be Stolen, Bankrupt or Charged off", "CR007", "GRT", "AGT", null, "BBL", null);
    }

    @When("deci is called with credit card product Arrangement having card status $cardStatus")
    public void whenDeciIsCalledWithCreditCardProductArrangementHavingCardStatusB(String cardStatus) throws Exception {
        request = dataHelper.createEligibilityRequestWithCreditCardStatusCode("P_BT", TestDataHelper.TEST_OCIS_ID, "BBL", TestDataHelper.TEST_CONTACT_POINT_ID, cardStatus);
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, "BBL");

        mockControl.go();
        response = eligibilityClient.determineEligibleInstructions(request);
    }

    @When("deci is called with no credit card product Arrangement")
    public void whenDeciIsCalledWithNoCreditCardProductArrangement() throws Exception {
        request = dataHelper.createEligibilityRequestForLoans("P_BT", TestDataHelper.TEST_OCIS_ID, "BBL", TestDataHelper.TEST_CONTACT_POINT_ID, 1968, 12, 4);
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, "BBL");

        mockControl.go();
        response = eligibilityClient.determineEligibleInstructions(request);
    }

    @Then("deci returns eligibility $eligibility")
    public void thenDeciReturnsEligibilityFalse(Boolean eligibility) {
        if (eligibility) {
            assertTrue(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        }
        else {
            assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        }

    }

    @Then("Reason description for CR007")
    public void thenReasonDescriptionForCR007() {
        assertEquals("CR007", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
        assertEquals(DeclineReasons.CR007_DECLINE_REASON, response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonDescription());
    }

}
