package com.lloydsbanking.salsa.eligibility.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
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
public class Story12DeciReturnEligibilityBasedOnRelatedEventAndDormantStatusRuleTest extends AbstractDeciJBehaveTestBase {
    DetermineElegibileInstructionsRequest request;

    DetermineElegibleInstructionsResponse response;

    String channel;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;

    }

    @Given("there is rule $rule with threshold $ruleParamValue and channel is $channel")
    public void givenThereIsRuleValueWithThresholdDormantAndChannelIsThis(String rule, String ruleParamValue, String channel) {
        mockScenarioHelper.expectCompositeInstructionConditionCall("G_BT", "GR002", "No valid credit card account for balance transfer", "GR002", rule, "Credit card status must not be Stolen, Bankrupt or Charged off", rule, "GRT", "AGT", ruleParamValue, channel, null);
        this.channel = channel;
    }

    @When("deci is called with product Arrangement having related event $relatedEvent and status $status")
    public void whenDeciIsCalledWithProductArrangementHavingRelatedEvent37AndStatusDormant(String relatedEvent, String status) throws Exception {
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, channel);

        request = dataHelper.createEligibilityRequestWithLifeCycleStatusAndRelatedEvent("P_BT", TestDataHelper.TEST_OCIS_ID, channel, TestDataHelper.TEST_CONTACT_POINT_ID, status, relatedEvent);
        mockControl.go();
        response = eligibilityClient.determineEligibleInstructions(request);
    }


    @When("deci is called with product Arrangement status $status")
    public void whenDeciIsCalledWithProductArrangementStatusDormant(String status) throws Exception {
        request = dataHelper.createEligibilityRequestWithLifeCycleStatus("P_BT", TestDataHelper.TEST_OCIS_ID, channel, TestDataHelper.TEST_CONTACT_POINT_ID, status);
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, channel);

        mockControl.go();
        response = eligibilityClient.determineEligibleInstructions(request);
    }

    @Then("deci returns eligibility $eligibility")
    public void thenDeciReturnsEligibilityTrue(Boolean eligibility) {
        if (eligibility) {
            assertTrue(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        }
        else {
            assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        }
    }

    @Then("Reason description for rule $rule and threshold $threshold")
    public void thenReasonDescriptionForRuleCR004AndThreshold37(String rule, String threshold) {
        assertEquals(rule, response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
        if (rule.equals("CR005")) {
            assertEquals("The customer's account is " + threshold, response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonDescription());
        }
        else {
            assertEquals("Customer doesn't have an existing product with the " + threshold + " event enabled", response.getCustomerInstructions()
                    .get(0)
                    .getDeclineReasons()
                    .get(0)
                    .getReasonDescription());
        }
    }

}
