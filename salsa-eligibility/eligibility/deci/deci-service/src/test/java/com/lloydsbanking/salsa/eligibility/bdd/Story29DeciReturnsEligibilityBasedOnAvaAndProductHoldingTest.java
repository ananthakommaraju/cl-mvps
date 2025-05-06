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
public class Story29DeciReturnsEligibilityBasedOnAvaAndProductHoldingTest extends AbstractDeciJBehaveTestBase {
    DetermineElegibileInstructionsRequest request;

    DetermineElegibleInstructionsResponse response;

    String channel = "IBL";

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("customer doesn't have more than 10 AVA's and rule is CR017")
    public void givenCustomerDoesntHaveMoreThan10AVAsAndRuleIsCR017() {
        request = dataHelper.createEligibilityRequest("P_EASY_SVR", TestDataHelper.TEST_OCIS_ID, "IBL", TestDataHelper.TEST_CONTACT_POINT_ID);
        request.getCustomerArrangements().add(dataHelper.createArrangementOfSpecProduct("T", "0386356000"));
        mockScenarioHelper.expectGetProductArrangementInstructionCall("P_EASY_SVR", "T", "4", TestDataHelper.TEST_RETAIL_CHANNEL_ID, "0386356000");
        mockScenarioHelper.expectGetParentInstructionCall("P_EASY_SVR", "Savings", null, "G_SAVINGS", TestDataHelper.TEST_RETAIL_CHANNEL_ID, "Easy  Saver");
        mockScenarioHelper.expectGetParentInstructionCall("G_SAVINGS", "Savings", null, "", TestDataHelper.TEST_RETAIL_CHANNEL_ID, "Group Savings");
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_EASY_SVR", "GR009", "Not a valid customer to apply for product", "GR009", "CR017", "Customer cannot have more than 10 instances of the product.", "CR017", "GRT", "AVN", "10", "IBL", null);

    }


    @Given("customer  have more than 10 AVA's and rule is CR017")

    public void givenCustomerHaveMoreThan10AVAsAndRuleIsCR017() {
        request = dataHelper.createEligibilityRequest("P_EASY_SVR", TestDataHelper.TEST_OCIS_ID, "IBL", TestDataHelper.TEST_CONTACT_POINT_ID);
        for (int index = 0; index < 12; index++) {
            request.getCustomerArrangements().add(dataHelper.createArrangementOfSpecProduct("T", "0386356000".concat(String.valueOf(index))));
            mockScenarioHelper.expectGetProductArrangementInstructionCall("P_EASY_SVR", "T", "4", TestDataHelper.TEST_RETAIL_CHANNEL_ID, "0386356000".concat(String.valueOf(index)));
        }

        mockScenarioHelper.expectGetParentInstructionCall("P_EASY_SVR", "Savings", null, "G_SAVINGS", TestDataHelper.TEST_RETAIL_CHANNEL_ID, "Easy  Saver");
        mockScenarioHelper.expectGetParentInstructionCall("G_SAVINGS", "Savings", null, "", TestDataHelper.TEST_RETAIL_CHANNEL_ID, "Group Savings");
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_EASY_SVR", "GR009", "Not a valid customer to apply for product", "GR009", "CR017", "Customer cannot have more than 10 instances of the product.", "CR017", "GRT", "AVN", "10", "IBL", null);

    }


    @Given("customer doesn't have more than 5 product holdings and rule is CR020")

    public void givenCustomerDoesntHaveMoreThan5ProductHoldingsAndRuleIsCR020() {
        request = dataHelper.createEligibilityRequest("P_EASY_SVR", TestDataHelper.TEST_OCIS_ID, "IBL", TestDataHelper.TEST_CONTACT_POINT_ID);
        request.getCustomerArrangements().add(dataHelper.createArrangementOfSpecProduct("T", "0386356000"));
        mockScenarioHelper.expectGetProductArrangementInstructionCall("P_EASY_SVR", "T", "4", TestDataHelper.TEST_RETAIL_CHANNEL_ID, "0386356000");
        mockScenarioHelper.expectGetParentInstructionCall("P_EASY_SVR", "Savings", null, "G_SAVINGS", TestDataHelper.TEST_RETAIL_CHANNEL_ID, "Easy  Saver");
        mockScenarioHelper.expectGetParentInstructionCall("G_SAVINGS", "Savings", null, "", TestDataHelper.TEST_RETAIL_CHANNEL_ID, "Group Savings");
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_EASY_SVR", "GR009", "Not a valid customer to apply for product", "GR009", "CR020", "Customer cannot have more than 5 instances of the product.", "CR020", "GRT", "AVN", "5", "IBL", null);

    }

    @Given("customer have more than 5 product holdings and rule is CR020")

    public void givenCustomerHaveMoreThan5ProductHoldingsAndRuleIsCR020() {

        channel = "STL";
        request = dataHelper.createEligibilityRequest("P_EASY_SVR", TestDataHelper.TEST_OCIS_ID, channel, TestDataHelper.TEST_CONTACT_POINT_ID);
        for (int index = 0; index < 7; index++) {
            request.getCustomerArrangements().add(dataHelper.createArrangementOfSpecProduct("T", "0386356000".concat(String.valueOf(index))));
            mockScenarioHelper.expectGetProductArrangementInstructionCall("P_EASY_SVR", "T", "4", channel, "0386356000".concat(String.valueOf(index)));
        }

        mockScenarioHelper.expectGetParentInstructionCall("P_EASY_SVR", "Savings", null, "G_SAVINGS", channel, "Easy  Saver");
        mockScenarioHelper.expectGetParentInstructionCall("G_SAVINGS", "Savings", null, "", channel, "Group Savings");
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_EASY_SVR", "GR009", "Not a valid customer to apply for product", "GR009", "CR020", "Customer cannot have more than 5 instances of the product.", "CR020", "GRT", "AVN", "5", channel, null);

    }


    @When("the UI calls DECI with valid request")
    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, request.getHeader().getChannelId());
        mockControl.go();


        response = eligibilityClient.determineEligibleInstructions(request);
    }

    @Then("DECI evaluates eligibility to true")
    public void thenDECIEvaluatesEligibilityToTrue() {

        assertTrue(response.getCustomerInstructions().get(0).isEligibilityIndicator());
    }

    @Then("DECI evaluates eligibility to false and returns error condition for customers having more than 5 product holdings")

    public void thenDECIEvaluatesEligibilityToFalseAndReturnsErrorConditionForCustomersHavingMoreThan5ProductHoldings() {
        assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        assertEquals("CR020", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
        assertEquals("Customer cannot have more than 5 instances of the product.", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonDescription());

    }

    @Then("DECI evaluates eligibility to false and returns error condition for customers having more than 10 AVA's")

    public void thenDECIEvaluatesEligibilityToFalseAndReturnsErrorConditionForCustomersHavingMoreThan10AVAs() {
        assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        assertEquals("CR017", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
        assertEquals("Customer cannot have more than 10 instances of the product.", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonDescription());

    }
}
