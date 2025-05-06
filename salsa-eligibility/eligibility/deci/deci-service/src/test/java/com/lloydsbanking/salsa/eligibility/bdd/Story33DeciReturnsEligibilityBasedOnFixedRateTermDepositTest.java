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
public class Story33DeciReturnsEligibilityBasedOnFixedRateTermDepositTest extends AbstractDeciJBehaveTestBase {
    DetermineElegibileInstructionsRequest request;

    DetermineElegibleInstructionsResponse response;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("customer doesn't have more than 5 product holdings of fixed rate term deposit")

    public void givenCustomerDoesntHaveMoreThan5ProductHoldingsOfFixedRateTermDeposit() {
        request = dataHelper.createEligibilityRequest("P_EASY_SVR", TestDataHelper.TEST_OCIS_ID, "IBL", TestDataHelper.TEST_CONTACT_POINT_ID);
        request.getCustomerArrangements().add(dataHelper.createArrangementOfSpecProduct("T", "0386356000"));
        mockScenarioHelper.expectGetProductArrangementInstructionCall("P_EASY_SVR", "T", "4", TestDataHelper.TEST_RETAIL_CHANNEL_ID, "0386356000");
        mockScenarioHelper.expectGetParentInstructionCall("P_EASY_SVR", "Savings", null, "G_SAVINGS", TestDataHelper.TEST_RETAIL_CHANNEL_ID, "Easy  Saver");
        mockScenarioHelper.expectGetParentInstructionCall("G_SAVINGS", "Savings", null, "", TestDataHelper.TEST_RETAIL_CHANNEL_ID, "Group Savings");
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_EASY_SVR", "GR009", "Not a valid customer to apply for product", "GR009", "CR028", "Customer cannot have more than 5 instances of the product.", "CR028", "GRT", "AVN", "5", "IBL", null);

    }

    @Given("customer have more than 5 product holdings of fixed rate term deposit")

    public void givenCustomerHaveMoreThan5ProductHoldingsOfFixedRateTermDeposit() {
        request = dataHelper.createEligibilityRequest("P_TDO_3M", TestDataHelper.TEST_OCIS_ID, "IBH", TestDataHelper.TEST_CONTACT_POINT_ID);
        for (int index = 0; index < 7; index++) {
            request.getCustomerArrangements().add(dataHelper.createArrangementOfSpecProduct("T", "3514116000"));
            mockScenarioHelper.expectGetProductArrangementInstructionCall("P_TDO_3M", "T", "4", "IBH", "3514116000");
        }

        mockScenarioHelper.expectGetParentInstructionCall("P_TDO_3M", "Halifax TD Onl FR 3 Mon M", null, "G_ONL_FRTD", "IBH", "Fixed Online Saver");
        mockScenarioHelper.expectGetParentInstructionCall("G_ONL_FRTD", "Fixed Online Saver", null, "G_SAVINGS", "IBH", "Savings");
        mockScenarioHelper.expectCompositeInstructionConditionCall("G_ONL_FRTD", "GR016", "Customer is not eligible for Term Deposit.", "GR016", "CR028", "Have exceeded maximum number of product holdings", "CR028", "GRP", "AVN", "5", "IBH", null);

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
        assertEquals("CR028", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
        assertEquals("Customer cannot have more than 5 instances of the product.", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonDescription());

    }

}
