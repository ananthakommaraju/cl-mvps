package com.lloydsbanking.salsa.eligibility.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.*;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import lb_gbo_sales.messages.DetermineElegibleInstructionsResponse;
import lb_gbo_sales.messages.RequestHeader;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;

import static org.junit.Assert.*;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})

public class Story11DeciReturnEligibilityBasedOnShadowlimitTest extends AbstractDeciJBehaveTestBase {
    DetermineElegibileInstructionsRequest request;

    DetermineElegibleInstructionsResponse response;

    RequestHeader header;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;

    }

    @Given("shadow limit amount is zero for the account")
    public void givenShadowLimitAmountIsZeroForTheAccount() {
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_LOANS", "GR001", "Customer holds a current account and the customer has a Shadow Limit of 0", "GR001", "CR022", "Customer holds a current account and has a Shadow Limit of 0", "CR022", "GRP", "CST", "10", "IBH", BigDecimal.ONE);
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_LOANS", "GR001", "Customer not eligible for product", "GR001", "CR025", "Customer holds current", "CR025", "GRP", "AGT", null, "IBH", null);
        header = dataHelper.createEligibilityRequestHeader("IBH", TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectCBSGenericGatewaySwitchCall(header.getChannelId(), true);
        mockScenarioHelper.expectB766Call(header, "772519");
        mockScenarioHelper.expectE220Call(header, "772519", "77251902224906", TestDataHelper.SHADOW_LIMIT_ZERO, "2");
    }

    @Given("shadow limit amount is non zero for the account")
    public void givenShadowLimitAmountIsNonZeroForTheAccount() {
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_LOANS", "GR001", "Customer holds a current account and the customer has a Shadow Limit of 0", "GR001", "CR022", "Customer holds a current account and has a Shadow Limit of 0", "CR022", "GRP", "CST", "10", "IBH", BigDecimal.ONE);
        header = dataHelper.createEligibilityRequestHeader("IBH", TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectCBSGenericGatewaySwitchCall(header.getChannelId(), true);
        mockScenarioHelper.expectB766Call(header, "772519");
        mockScenarioHelper.expectE220Call(header, "772519", "77251902224906", TestDataHelper.SHADOW_LIMIT_NON_ZERO, "1");

    }

    @Given("AccountType is not C")
    public void givenAccountTypeIsNotC() {
        mockScenarioHelper.expectB162Call(header, "1", "P", "P", header.getChannelId());
        mockScenarioHelper.expectF336Call(header, 3, 3);
    }


    @Given("AccountType is C and sellerLegalEntity matched")
    public void givenAccountTypeIsCAndSellerLegalEntityMatched() {
        mockScenarioHelper.expectB162Call(header, "1", "C", "P", "HAL");
        mockScenarioHelper.expectF336Call(header, 3, 3);

    }

    @Given("shadow limit amount is greater than assigned for the account")
    public void givenShadowLimitAmountIsGreaterThanAssignedForTheAccount() {
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_LOANS", "GR001", "Customer holds a current account and has a Shadow Limit greater than 0, but lower than threshold", "GR001", "CR023", "Customer holds a current account and has a Shadow Limit greater than 0, but lower than threshold", "CR023", "GRP", "CST", "3", "IBH", BigDecimal.ONE);
        header = dataHelper.createEligibilityRequestHeader("IBH", TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectCBSGenericGatewaySwitchCall(header.getChannelId(), true);
        mockScenarioHelper.expectB766Call(header, "772519");
        mockScenarioHelper.expectE220Call(header, "772519", "77251902224906", "4", "2");

    }

    @Given("shadow limit amount less than threshold for the account")
    public void givenShadowLimitAmountLessThanThresholdForTheAccount() {
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_LOANS", "GR001", "Customer holds a current account and has a Shadow Limit greater than 0, but lower than threshold", "GR001", "CR023", "Customer holds a current account and has a Shadow Limit greater than 0, but lower than threshold", "CR023", "GRP", "CST", "3", "IBH", BigDecimal.ONE);
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_LOANS", "GR001", "Customer not eligible for product", "GR001", "CR025", "Customer holds current", "CR025", "GRP", "AGT", null, "IBH", null);
        header = dataHelper.createEligibilityRequestHeader("IBH", TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectCBSGenericGatewaySwitchCall(header.getChannelId(), true);
        mockScenarioHelper.expectB766Call(header, "772519");
        mockScenarioHelper.expectE220Call(header, "772519", "77251902224906", "2", "2");
    }

    @Given("AccountType is C and sellerLegalEntity not matched")
    public void givenAccountTypeIsCAndSellerLegalEntityNotMatched() {
        mockScenarioHelper.expectB162Call(header, "1", "C", "P", "VER");
        mockScenarioHelper.expectF336Call(header, 3, 3);
    }

    @When("the UI calls DECI with valid request")
    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        mockControlService.go();
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, "IBH");
        request = dataHelper.createEligibilityRequestForLoans("P_LOANS", TestDataHelper.TEST_OCIS_ID, "IBH", TestDataHelper.TEST_CONTACT_POINT_ID, 1989, 01, 01);
        response = eligibilityClient.determineEligibleInstructions(request);

    }

    @Then("DECI evaluates eligibility to true")
    public void thenDECIEvaluatesEligibilityToTrue() {
        assertTrue(response.getCustomerInstructions().get(0).isEligibilityIndicator());
    }

    @Then("DECI evaluates eligibility to false and returns decline reasons in response for failing CR022")
    public void thenDECIEvaluatesEligibilityToFalseAndReturnsDeclineReasonsInResponseForFailingCR022() {
        assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        assertEquals("CR022", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
        assertEquals("Customer has current account and Shadow Limit amount is 0", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonDescription());

    }

    @Then("DECI evaluates eligibility to false and returns decline reasons in response for failing CR023")
    public void thenDECIEvaluatesEligibilityToFalseAndReturnsDeclineReasonsInResponseForFailingCR023() {
        assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        assertEquals("CR023", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
    }

}
