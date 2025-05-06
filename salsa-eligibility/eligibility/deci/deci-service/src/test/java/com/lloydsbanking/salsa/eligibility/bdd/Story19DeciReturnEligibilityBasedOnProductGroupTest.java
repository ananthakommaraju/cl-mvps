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
public class Story19DeciReturnEligibilityBasedOnProductGroupTest extends AbstractDeciJBehaveTestBase {

    DetermineElegibileInstructionsRequest request;

    DetermineElegibleInstructionsResponse response;

    String channel;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("customer is applying for balance transfer and have eligible credit card account/s")
    public void givenCustomerIsApplyingForBalanceTransferAndHaveEligibleCreditCardAccounts() {
        channel = "IBL0";
        request = dataHelper.createEligibilityRequest("G_BT", TestDataHelper.TEST_OCIS_ID, channel, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectCompositeInstructionConditionCall("G_BT", "GR002", "No valid credit card account for balance transfer", "GR002", "CR011", "Customer doesn’t have any eligible credit card accounts for Balance transfer", "CR011", "GRT", "AGT", "G_CREDCARD", channel, null);
        mockScenarioHelper.expectGetProductArrangementInstructionCall("P_CC_PLAT", "F", "13", channel, "55215700");
        request.getCustomerArrangements().add(dataHelper.createArrangementOfSpecProduct("F", "55215700"));
    }


    @Given("customer is applying for balance transfer and does not have any eligible credit card account")
    public void givenCustomerIsApplyingForBalanceTransferAndDoesNotHaveAnyEligibleCreditCardAccount() {
        channel = "IBL2";
        request = dataHelper.createEligibilityRequest("G_BT", TestDataHelper.TEST_OCIS_ID, channel, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectCompositeInstructionConditionCall("G_BT", "GR002", "No valid credit card account for balance transfer", "GR002", "CR011", "Customer doesn’t have any eligible credit card accounts for Balance transfer", "CR011", "GRT", "AGT", "G_CREDCARD", channel, null);

    }

    @When("the UI calls DECI with valid request")
    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, channel);

        mockControl.go();


        response = eligibilityClient.determineEligibleInstructions(request);
    }

    @Then("DECI evaluates eligibility to true")
    public void thenDECIEvaluatesEligibilityToTrue() {

        assertTrue(response.getCustomerInstructions().get(0).isEligibilityIndicator());
    }


    @Then("DECI evaluates eligibility to false for balance transfer and returns error condition for ineligible credit card accounts")
    public void thenDECIEvaluatesEligibilityToFalseForBalanceTransferAndReturnsErrorConditionForIneligibleCreditCardAccounts() {
        assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        assertEquals("CR011", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
    }

}



