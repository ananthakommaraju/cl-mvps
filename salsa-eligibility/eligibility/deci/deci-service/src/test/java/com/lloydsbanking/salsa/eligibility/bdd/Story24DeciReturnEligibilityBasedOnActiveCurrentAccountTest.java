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
public class Story24DeciReturnEligibilityBasedOnActiveCurrentAccountTest extends AbstractDeciJBehaveTestBase {

    DetermineElegibileInstructionsRequest request;

    DetermineElegibleInstructionsResponse response;

    boolean isRelatedEventsPresent;

    RequestHeader header;

    String channel;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;

    }

    @Given("shadow limit is greater than set threshold")
    public void givenShadowLimitIsGreaterThanSetThreshold() {
        channel = "IBH";
        isRelatedEventsPresent = false;
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_BLN_RBB", "GR022", "Account is not eligible for Business Overdraft", "GR022", "CR002", "Customer cannot be younger that 18 years", "CR002", "GRT", "CST", "18", channel, null);
        mockScenarioHelper.expectGetParentInstructionCall("P_BLN_RBB", "Apply Loan", null, "G_BLOAN", channel, "Business Loan");
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_BLN_RBB", "GR022", "Customer is not eligible for Business Loan", "GR002", "CR060", "Customer current account has no repayment shadow limit and direct debit option", "CR060", "GRP", "AGA", "3:37", channel, BigDecimal.ONE);

        mockScenarioHelper.expectRBBSlookupCall(channel);
        header = dataHelper.createEligibilityRequestHeader(channel, TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectCBSGenericGatewaySwitchCall(header.getChannelId(), true);
        mockScenarioHelper.expectB766Call(header, "772519");
        mockScenarioHelper.expectE220Call(header, "772519", "77251902224906", "44", "4");
    }

    @Given("related events are present")
    public void givenRelatedEventsArePresent() {
        isRelatedEventsPresent = true;
    }

    @Given("shadow limit is less than set threshold")
    public void givenShadowLimitIsLessThanSetThreshold() {
        channel = "IBL";
        isRelatedEventsPresent = false;
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_BLN_RBB", "GR022", "Account is not eligible for Business Overdraft", "GR022", "CR002", "Customer cannot be younger that 18 years", "CR002", "GRT", "CST", "18", channel, null);
        mockScenarioHelper.expectGetParentInstructionCall("P_BLN_RBB", "Apply Loan", null, "G_BLOAN", channel, "Business Loan");
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_BLN_RBB", "GR022", "Customer is not eligible for Business Loan", "GR002", "CR060", "Customer current account has no repayment shadow limit and direct debit option", "CR060", "GRP", "AGA", "0:37", channel, BigDecimal.ONE);

        mockScenarioHelper.expectRBBSlookupCall(channel);

        header = dataHelper.createEligibilityRequestHeader(channel, TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectCBSGenericGatewaySwitchCall(header.getChannelId(), true);
        mockScenarioHelper.expectB766Call(header, "772519");
        mockScenarioHelper.expectE220Call(header, "772519", "77251902224906", "000", "0");
    }

    @Given("related events are not present")
    public void givenRelatedEventsAreNotPresent() {
        isRelatedEventsPresent = false;
    }

    @When("the UI calls DECI with valid request")
    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, channel);

        mockControlService.go();
        request = dataHelper.createEligibilityRequestForLoans("P_BLN_RBB", TestDataHelper.TEST_OCIS_ID, channel, TestDataHelper.TEST_CONTACT_POINT_ID, 1989, 01, 01);
        request.getCustomerArrangements().get(0).setCapAccountRestricted(false);
        if (isRelatedEventsPresent) {
            request.getCustomerArrangements().get(0).getRelatedEvents().add("37");
        }
        response = eligibilityClient.determineEligibleInstructions(request);

    }

    @Then("DECI evaluates eligibility to false and returns error condition")
    public void thenDECIEvaluatesEligibilityToFalseAndReturnsErrorCondition() {
        assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        assertEquals("CR060", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
        assertEquals("Customer does not hold an active current account having DirectDebit and shadow limit greater than 0.", response.getCustomerInstructions()
                .get(0)
                .getDeclineReasons()
                .get(0)
                .getReasonDescription());

    }

    @Then("DECI evaluates eligibility to true")
    public void thenDECIEvaluatesEligibilityToTrue() {
        assertTrue(response.getCustomerInstructions().get(0).isEligibilityIndicator());
    }

}
