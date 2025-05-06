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
import lb_gbo_sales.messages.RequestHeader;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story18DeciReturnEligibilityBasedOnStrictFlagTest extends AbstractDeciJBehaveTestBase {
    DetermineElegibileInstructionsRequest request;

    DetermineElegibleInstructionsResponse response;

    String strctFlag;

    RequestHeader header;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;

    }

    @Given("strict flag value for the account is not equal to set threshold for which overdraft is being applied")
    public void givenStrictFlagValueForTheAccountIsNotEqualToSetThresholdForWhichOverdraftIsBeingApplied() {
        header = new TestDataHelper().createEligibilityRequestHeader("IBH", TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);

        //threshold is set "2"
        strctFlag = "1";
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_BODA_RBB", "GR022", "Account is not eligible for Business Overdraft", "GR022", "CR002", "Customer cannot be younger that 18 years", "CR002", "GRT", "CST", "18", "IBH", null);
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_BODA_RBB", "GR021", "Account is not eligible for Business Overdraft", "GR021", "CR058", "Strict flag is not set on current account", "CR058", "GRP", "ASB", "2", "IBH", BigDecimal.ONE);
        mockScenarioHelper.expectCBSGenericGatewaySwitchCall("IBH", true);
        mockScenarioHelper.expectB766Call(header, "772519");

        mockScenarioHelper.expectE220Call(header, "772519", "77251902224906", TestDataHelper.SHADOW_LIMIT_ZERO, strctFlag);

    }

    @Given("strict flag value for the account is equal to set threshold for which overdraft is being applied")
    public void givenStrictFlagValueForTheAccountIsEqualToSetThresholdForWhichOverdraftIsBeingApplied() {
        header = new TestDataHelper().createEligibilityRequestHeader("IBH", TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        //threshold is set "2"
        strctFlag = "2";
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_BODA_RBB", "GR022", "Account is not eligible for Business Overdraft", "GR022", "CR002", "Customer cannot be younger that 18 years", "CR002", "GRT", "CST", "18", "IBH", null);
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_BODA_RBB", "GR021", "Account is not eligible for Business Overdraft", "GR021", "CR058", "Strict flag is not set on current account", "CR058", "GRP", "ASB", "2", "IBH", BigDecimal.ONE);

        mockScenarioHelper.expectCBSGenericGatewaySwitchCall("IBH", true);
        mockScenarioHelper.expectB766Call(header, "772519");
        mockScenarioHelper.expectE220Call(header, "772519", "77251902224906", TestDataHelper.SHADOW_LIMIT_ZERO, strctFlag);

    }

    @When("the UI calls DECI for RBBS overdraft")
    public void whenTheUICallsDECIForRBBSOverdraft() throws DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, "IBH");

        mockScenarioHelper.expectRBBSlookupCall("IBH");
        mockControlService.go();
        request = dataHelper.createEligibilityRequestForLoans("P_BODA_RBB", TestDataHelper.TEST_OCIS_ID, "IBH", TestDataHelper.TEST_CONTACT_POINT_ID, 1989, 01, 01);
        request.getCustomerArrangements().get(0).setStrctFlag(strctFlag);
        response = eligibilityClient.determineEligibleInstructions(request);

    }

    @Then("DECI evaluates eligibility to true")
    public void thenDECIEvaluatesEligibilityToTrue() {
        assertTrue(response.getCustomerInstructions().get(0).isEligibilityIndicator());
    }

    @Then("DECI evaluates eligibility to false for RBBS overdraft and returns error condition")
    public void thenDECIEvaluatesEligibilityToFalseForRBBSOverdraftAndReturnsErrorCondition() {
        assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        assertEquals("CR058", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
        assertEquals("Customer has strict flag set on one or more of the current account holding(s)", response.getCustomerInstructions()
                .get(0)
                .getDeclineReasons()
                .get(0)
                .getReasonDescription());

    }

}
