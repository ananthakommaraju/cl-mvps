package com.lloydsbanking.salsa.eligibility.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story23DeciReturnEligibilityBasedOnCbsIndicatorTest extends AbstractDeciJBehaveTestBase {

    DetermineElegibileInstructionsRequest request;

    DetermineElegibleInstructionsResponse response;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;

    }

    @Given("rule is $rule")
    public void givenRuleIsCR024(String rule) {
        mockScenarioHelper.expectRBBSlookupCall("STL");
        List<RefInstructionRulesDto> ruleList = new ArrayList();
        ruleList.add(new RefInstructionRulesDto("P_BOD_RBB", "GR022", "Account is not eligible for Business Overdraft", "GR022", "CR002", "Customer cannot be younger that 18 years", "CR002", "GRT", "CST", "18", "STL", null));
        ruleList.add(new RefInstructionRulesDto("P_BLN_RBB", "GR022", "Account is not eligible for Business Overdraft", "GR022", "CR002", "Customer cannot be younger that 18 years", "CR002", "GRT", "CST", "18", "STL", null));

        if (rule.equals("CR056")) {
            ruleList.add(new RefInstructionRulesDto("P_BOD_RBB", "GR021", "Account is not eligible for Business Overdraft", "GR021", "CR056", "Transaction is not prohibited for selected current account ", "CR056", "GRP", "ASA", "646", "STL", BigDecimal.ONE));

        } else if (rule.equals("CR059")) {
            ruleList.add(new RefInstructionRulesDto("P_BLN_RBB", "GR022", "Business is not eligible for Business Loan", "GR022", "CR059", "Transaction is not prohibited for selected current account ", "CR059", "GRP", "AGA", "646", "STL", BigDecimal.ONE));
        } else if (rule.equals("CR061")) {
            ruleList.add(new RefInstructionRulesDto("P_BLN_RBB", "GR022", "Business is not eligible for Business Loan", "GR022", "CR061", "Transaction is not prohibited for selected current account ", "CR061", "GRP", "AGA", "8", "STL", BigDecimal.ONE));
            ruleList.add(new RefInstructionRulesDto("P_BOD_RBB", "GR021", "Account is not eligible for Business Overdraft", "GR021", "CR061", "Transaction is not prohibited for selected current account ", "CR061", "GRP", "AGA", "8", "STL", BigDecimal.ONE));
        }


        mockScenarioHelper.expectGetCompositeInstructionConditionCall(ruleList);

    }

    @Given("CBS 646 indicator is not set on a account for which overdraft is being applied")
    public void givenCBS646IndicatorIsNotSetOnAAccountForWhichOverdraftIsBeingApplied() {
        mockScenarioHelper.expectRBBSlookupCall("STL");
        RequestHeader header = dataHelper.createEligibilityRequestHeader(TestDataHelper.TEST_COMMERCIAL_CHANNEL_ID, TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectGetParentInstructionCall("P_BOD_RBB", "Apply Overdraft", null, "G_OD", "STL", "Business Overdraft");
        mockScenarioHelper.expectE184CallForCBSIndicator(header, TestDataHelper.TEST_SORT_CODE, TestDataHelper.TEST_ACCOUNT_NUMBER, 123);
        mockScenarioHelper.expectB766Call(header, TestDataHelper.TEST_SORT_CODE);
        mockScenarioHelper.expectCBSGenericGatewaySwitchCall(header.getChannelId(), true);

    }

    @When("the UI calls DECI for RBBS overdraft")
    public void whenTheUICallsDECIForRBBSOverdraft() throws DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {

        request = dataHelper.createEligibilityRequest("P_BOD_RBB", TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_COMMERCIAL_CHANNEL_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockControl.go();
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, TestDataHelper.TEST_COMMERCIAL_CHANNEL_ID);

        try {
            response = eligibilityClient.determineEligibleInstructions(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Then("DECI evaluates eligibility to true")
    public void thenDECIEvaluatesEligibilityToTrue() {
        assertTrue(response.getCustomerInstructions().get(0).isEligibilityIndicator());
    }


    @Given("CBS 8 indicator is not set on any account for RBBS Overdraft")
    public void givenCBS8IndicatorIsNotSetOnAnyODAccount() {
        mockScenarioHelper.expectRBBSlookupCall("STL");
        RequestHeader header = dataHelper.createEligibilityRequestHeader(TestDataHelper.TEST_COMMERCIAL_CHANNEL_ID, TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);

        mockScenarioHelper.expectE184CallForCBSIndicator(header, TestDataHelper.TEST_SORT_CODE, TestDataHelper.TEST_ACCOUNT_NUMBER, 123);
        mockScenarioHelper.expectGetParentInstructionCall("P_BOD_RBB", "Apply Overdraft", null, "G_OD", "STL", "Business Overdraft");
        mockScenarioHelper.expectB766Call(header, TestDataHelper.TEST_SORT_CODE);
        mockScenarioHelper.expectCBSGenericGatewaySwitchCall(header.getChannelId(), true);

    }


    @Given("CBS 8 indicator is not set on any account for RBBS loan")
    public void givenCBS8IndicatorIsNotSetOnAnyAccount() {
        mockScenarioHelper.expectRBBSlookupCall("STL");
        RequestHeader header = dataHelper.createEligibilityRequestHeader(TestDataHelper.TEST_COMMERCIAL_CHANNEL_ID, TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);

        mockScenarioHelper.expectE184CallForCBSIndicator(header, TestDataHelper.TEST_SORT_CODE, TestDataHelper.TEST_ACCOUNT_NUMBER, 123);
        mockScenarioHelper.expectGetParentInstructionCall("P_BOD_RBB", "Apply Overdraft", null, "G_OD", "STL", "Business Overdraft");
        mockScenarioHelper.expectB766Call(header, TestDataHelper.TEST_SORT_CODE);
        mockScenarioHelper.expectCBSGenericGatewaySwitchCall(header.getChannelId(), true);

    }

    @Given("CBS 646 indicator is  set on a account for which overdraft is being applied")
    public void givenCBS646IndicatorIsSetOnAAccountForWhichOverdraftIsBeingApplied() {
        mockScenarioHelper.expectRBBSlookupCall("STL");
        RequestHeader header = dataHelper.createEligibilityRequestHeader(TestDataHelper.TEST_COMMERCIAL_CHANNEL_ID, TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectE184CallForCBSIndicator(header, TestDataHelper.TEST_SORT_CODE, TestDataHelper.TEST_ACCOUNT_NUMBER, 646);

    }


    @Then("DECI evaluates eligibility to false")
    public void thenDECIEvaluatesEligibilityToFalse() {
        assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
    }

    @Given("CBS 646 indicator is not  set on any account")
    public void givenCBS646IndicatorIsNotSetOnAnyAccount() {
        mockScenarioHelper.expectRBBSlookupCall("STL");
        RequestHeader header = dataHelper.createEligibilityRequestHeader(TestDataHelper.TEST_COMMERCIAL_CHANNEL_ID, TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectE184CallForCBSIndicator(header, TestDataHelper.TEST_SORT_CODE, TestDataHelper.TEST_ACCOUNT_NUMBER, 123);
        mockScenarioHelper.expectGetParentInstructionCall("P_BLN_RBB", "Apply Loan", null, "G_LN", "STL", "Business Loan");
        mockScenarioHelper.expectB766Call(header, TestDataHelper.TEST_SORT_CODE);
        mockScenarioHelper.expectCBSGenericGatewaySwitchCall(header.getChannelId(), true);

    }

    @When("the UI calls DECI for RBBS loans")

    public void whenTheUICallsDECIForRBBSLoans() throws DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, TestDataHelper.TEST_COMMERCIAL_CHANNEL_ID);

        request = dataHelper.createEligibilityRequest("P_BLN_RBB", TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_COMMERCIAL_CHANNEL_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockControl.go();
        response = eligibilityClient.determineEligibleInstructions(request);
    }

    @Given("CBS 646 indicator is set on one account")
    public void givenCBS646IndicatorIsSetOnOneAccount() {
        mockScenarioHelper.expectRBBSlookupCall("STL");
        RequestHeader header = dataHelper.createEligibilityRequestHeader(TestDataHelper.TEST_COMMERCIAL_CHANNEL_ID, TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectE184CallForCBSIndicator(header, TestDataHelper.TEST_SORT_CODE, TestDataHelper.TEST_ACCOUNT_NUMBER, 646);
        mockScenarioHelper.expectGetParentInstructionCall("P_BLN_RBB", "Apply Loan", null, "G_LN", "STL", "Business Loan");
        mockScenarioHelper.expectB766Call(header, TestDataHelper.TEST_SORT_CODE);
        mockScenarioHelper.expectCBSGenericGatewaySwitchCall(header.getChannelId(), true);
    }

    @Given("CBS 8 indicator is set on one account for RBBS loan")
    public void givenCBS8IndicatorIsSetOnOneAccount() {
        mockScenarioHelper.expectRBBSlookupCall("STL");
        RequestHeader header = dataHelper.createEligibilityRequestHeader(TestDataHelper.TEST_COMMERCIAL_CHANNEL_ID, TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectE184CallForCBSIndicator(header, TestDataHelper.TEST_SORT_CODE, TestDataHelper.TEST_ACCOUNT_NUMBER, 8);
        mockScenarioHelper.expectGetParentInstructionCall("P_BLN_RBB", "Apply Loan", null, "G_LN", "STL", "Business Loan");
        mockScenarioHelper.expectB766Call(header, TestDataHelper.TEST_SORT_CODE);
        mockScenarioHelper.expectCBSGenericGatewaySwitchCall(header.getChannelId(), true);
    }

    @Given("CBS 8 indicator is set on one account for RBBS Overdraft")
    public void givenCBS8IndicatorIsSetOnOneOdAccount() {
        mockScenarioHelper.expectRBBSlookupCall("STL");
        RequestHeader header = dataHelper.createEligibilityRequestHeader(TestDataHelper.TEST_COMMERCIAL_CHANNEL_ID, TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectE184CallForCBSIndicator(header, TestDataHelper.TEST_SORT_CODE, TestDataHelper.TEST_ACCOUNT_NUMBER, 8);
        mockScenarioHelper.expectGetParentInstructionCall("P_BOD_RBB", "Apply Overdraft", null, "G_OD", "STL", "Business Overdraft");
        mockScenarioHelper.expectB766Call(header, TestDataHelper.TEST_SORT_CODE);
        mockScenarioHelper.expectCBSGenericGatewaySwitchCall(header.getChannelId(), true);
    }

}
