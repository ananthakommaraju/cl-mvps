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

import javax.xml.datatype.DatatypeConfigurationException;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story17DeciReturnEligibilityBasedOnLoanAppliedTimesTest extends AbstractDeciJBehaveTestBase {
    DetermineElegibileInstructionsRequest request;

    DetermineElegibleInstructionsResponse response;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;

    }

    @Given("loan applied times is less than threshold for an account for which overdraft is being applied")
    public void givenLoanAppliedTimesIsLessThanThresholdForAnAccountForWhichOverdraftIsBeingApplied() throws DetermineEligibleInstructionsInternalServiceErrorMsg, DatatypeConfigurationException {
        mockScenarioHelper.expectRBBSlookupCall("IBH");
        RequestHeader header = dataHelper.createEligibilityRequestHeader("IBH", TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectB093Call("30:1", header, false);
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_BOD_RBB", "GR022", "Account is not eligible for Business Overdraft", "GR022", "CR002", "Customer cannot be younger that 18 years", "CR002", "GRT", "CST", "18", "IBH", null);
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_BOD_RBB", "GR022", "Account is not eligible for Business Overdraft", "GR022", "CR057", "Customer has not applied for business loan/overdraft in given number of days through digital", "CR057", "GRP", "CST", "30:1", "IBH", BigDecimal.ONE);

    }

    @Given("loan applied times is more than threshold for an account for which overdraft is being applied")
    public void givenLoanAppliedTimesIsMoreThanThresholdForAnAccountForWhichOverdraftIsBeingApplied() throws DetermineEligibleInstructionsInternalServiceErrorMsg, DatatypeConfigurationException {
        mockScenarioHelper.expectRBBSlookupCall("IBH");
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_BOD_RBB", "GR022", "Account is not eligible for Business Overdraft", "GR022", "CR002", "Customer cannot be younger that 18 years", "CR002", "GRT", "CST", "18", "IBH", null);
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_BOD_RBB", "GR022", "Account is not eligible for Business Overdraft", "GR022", "CR057", "Customer has not applied for business loan/overdraft in given number of days through digital", "CR057", "GRP", "CST", "30:1", "IBH", BigDecimal.ONE);
        RequestHeader header = dataHelper.createEligibilityRequestHeader("IBH", TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectB093Call("30:1", header, true);
    }

    @When("the UI calls DECI for RBBS overdraft")
    public void whenTheUICallsDECIForRBBSOverdraft() throws DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, "IBH");

        mockControlService.go();
        request = dataHelper.createEligibilityRequestForLoans("P_BOD_RBB", TestDataHelper.TEST_OCIS_ID, "IBH", TestDataHelper.TEST_CONTACT_POINT_ID, 1989, 01, 01);
        response = eligibilityClient.determineEligibleInstructions(request);
    }

    @Then("DECI evaluates eligibility to true")
    public void thenDECIEvaluatesEligibilityToTrue() {
        assertTrue(response.getCustomerInstructions().get(0).isEligibilityIndicator());
    }

    @Then("DECI evaluates eligibility to false for RBBS overdraft and returns error condition")
    public void thenDECIEvaluatesEligibilityToFalseForRBBSOverdraftAndReturnsErrorCondition() {
        assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        assertEquals("CR057", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
        assertEquals("Customer has applied for loan/overdraft 2 times which is more than threshold 1  in last 30 days", response.getCustomerInstructions()
                .get(0)
                .getDeclineReasons()
                .get(0)
                .getReasonDescription());

    }

}
