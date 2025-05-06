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
public class Story15DeciReturnsEligibilityBasedOnOffshoreAccountsRuleTest extends AbstractDeciJBehaveTestBase {
    DetermineElegibileInstructionsRequest request;

    DetermineElegibleInstructionsResponse response;

    String channel;

    @BeforeScenario
    public void resetData() {
        request = null;
        response = null;
    }

    @Given("there is rule CR013 configured CR013 with threshold $threshold and channel is $channel")
    public void givenThereIsRuleCR013ConfiguredCR013WithThreshold5555(String threshold, String channel) {
        this.channel = channel;
        mockScenarioHelper.expectCompositeInstructionConditionCall("G_BT", "GR002", "No valid credit card account for balance transfer", "GR002", "CR013", "Credit card status must not be Stolen, Bankrupt or Charged off", "CR013", "GRT", "AGT", threshold, channel, null);
    }

    @When("deci is called with deposit Arrangement having sort code $sortCode")
    public void whenDeciIsCalledWithProductArrangementHavingSortCode5555(String sortCode) throws Exception {
        request = dataHelper.createEligibilityRequestWithDepositArrangementHavingSortCode("P_BT", TestDataHelper.TEST_OCIS_ID, channel, TestDataHelper.TEST_CONTACT_POINT_ID, sortCode);
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, channel);

        mockControl.go();
        response = eligibilityClient.determineEligibleInstructions(request);
    }

    @When("deci is called with no deposit Arrangement")
    public void whenDeciIsCalledWithNoDepositArrangement() throws Exception {
        request = dataHelper.createEligibilityRequestForCreditCard("P_BT", TestDataHelper.TEST_OCIS_ID, channel, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, channel);

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

    @Then("Reason description for rule CR013")
    public void thenReasonDescriptionForRuleCR013() {
        assertEquals("CR013", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
        assertEquals(DeclineReasons.CR013_DECLINE_REASON, response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonDescription());
    }
}
