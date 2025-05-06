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

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story31DeciReturnEligibilityBasedOnMbcRoleTest extends AbstractDeciJBehaveTestBase {
    DetermineElegibileInstructionsRequest request;

    DetermineElegibleInstructionsResponse response;


    String candidateInstruction;


    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("customer doesnot has MBC role")
    public void givenCustomerDoesnotHasMBCRole() {
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_BLN_RBB", "GR022", "Account is not eligible for Business Overdraft", "GR022", "CR002", "Customer cannot be younger that 18 years", "CR002", "GRT", "CST", "18", "STL", null);

        candidateInstruction = "P_BLN_RBB";
        request = dataHelper.createBusinessEligibilityRequest(candidateInstruction, null, dataHelper.TEST_OCIS_ID, "STL", dataHelper.TEST_CONTACT_POINT_ID, 40, 25);
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_BLN_RBB", "GR009", "Not able to apply Business product", "GR009", "CR052", "Customer has MBC role.", "CR052", "GRP", "ASB", "CUS", "STL", null);
        request.setSelctdBusnsId("0123456");
    }

    @Given("customer has MBC role")
    public void givenCustomerHasMBCRole() {
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_BLN_RBB", "GR022", "Account is not eligible for Business Overdraft", "GR022", "CR002", "Customer cannot be younger that 18 years", "CR002", "GRT", "CST", "18", "STL", null);

        candidateInstruction = "P_BLN_RBB";
        request = dataHelper.createBusinessEligibilityRequest(candidateInstruction, null, dataHelper.TEST_OCIS_ID, "STL", dataHelper.TEST_CONTACT_POINT_ID, 40, 25);
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_BLN_RBB", "GR009", "Not able to apply Business product", "GR009", "CR052", "Customer has MBC role.", "CR052", "GRP", "ASB", "CUS", "STL", null);

    }


    @When("the UI calls DECI with valid request")

    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, request.getHeader().getChannelId());

        mockScenarioHelper.expectRBBSlookupCall("STL");
        mockControl.go();
        response = eligibilityClient.determineEligibleInstructions(request);

    }

    @Then("DECI evaluates eligibility to true")

    public void thenDECIEvaluatesEligibilityToTrue() {
        assertTrue(response.getCustomerInstructions().get(0).isEligibilityIndicator());
    }


    @Then("DECI evaluates eligibility to false and returns error condition for having MBC role.")

    public void thenDECIEvaluatesEligibilityToFalseAndReturnsErrorConditionForHavingMBCRole() {
        assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        assertEquals("CR052", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
        assertEquals("Customer has MBC role.", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonDescription());

    }
}





