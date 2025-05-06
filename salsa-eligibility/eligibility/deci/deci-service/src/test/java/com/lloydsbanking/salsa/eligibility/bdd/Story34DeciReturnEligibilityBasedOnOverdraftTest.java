package com.lloydsbanking.salsa.eligibility.bdd;


import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import lb_gbo_sales.businessobjects.ArrangementIdentifier;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story34DeciReturnEligibilityBasedOnOverdraftTest extends AbstractDeciJBehaveTestBase {

    DetermineElegibileInstructionsRequest request;

    DetermineElegibleInstructionsResponse response;


    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("customer is applying for amending business overdraft that has not been applied  in last threshold days")
    public void givenCustomerIsApplyingForAmendingBusinessOverdraftThatHasNotBeenAppliedInLastThresholdDays() {
        mockScenarioHelper.expectRBBSlookupCall("IBV");
        mockScenarioHelper.expectGetParentInstructionCall("P_BLN_RBB", "Apply Overdraft", null, "G_OD", "IBV", "Business Overdraft");
        List<RefInstructionRulesDto> ruleList = new ArrayList();
        ruleList.add(new RefInstructionRulesDto("P_BLN_RBB", "GR021", "Account is not eligible for Business Overdraft", "GR021", "CR050", "Overdraft, on selected account, has not started and is not expiring in given number of days", "CR050", "GRP", "ASA", "28:75", "IBV", new BigDecimal(1)));
        mockScenarioHelper.expectGetCompositeInstructionConditionCall(ruleList);
        request = dataHelper.createBusinessEligibilityRequest("P_BLN_RBB", null, dataHelper.TEST_OCIS_ID, "IBV", dataHelper.TEST_CONTACT_POINT_ID, 40, 25);
        ArrangementIdentifier arrangementIdentifier = new ArrangementIdentifier();
        arrangementIdentifier.setAccNum("225627689");
        arrangementIdentifier.setSortCode("770908");
        request.setSelctdArr(arrangementIdentifier);

    }

    @Given("customer is applying for amending business overdraft  that has been applied  in last threshold days.")
    public void givenCustomerIsApplyingForAmendingBusinessOverdraftThatHasBeenAppliedInLastThresholdDays() {
        mockScenarioHelper.expectRBBSlookupCall("IBV");
        mockScenarioHelper.expectGetParentInstructionCall("P_BLN_RBB", "Apply Overdraft", null, "G_OD", "IBV", "Business Overdraft");
        List<RefInstructionRulesDto> ruleList = new ArrayList();
        ruleList.add(new RefInstructionRulesDto("P_BLN_RBB", "GR021", "Account is not eligible for Business Overdraft", "GR021", "CR050", "Overdraft, on selected account, has not started and is not expiring in given number of days", "CR050", "GRP", "ASA", "28:75", "IBV", new BigDecimal(1)));
        mockScenarioHelper.expectGetCompositeInstructionConditionCall(ruleList);

        request = dataHelper.createBusinessEligibilityRequest("P_BLN_RBB", null, dataHelper.TEST_OCIS_ID, "IBV", dataHelper.TEST_CONTACT_POINT_ID, 40, 25);
        ArrangementIdentifier arrangementIdentifier = new ArrangementIdentifier();
        arrangementIdentifier.setAccNum("22562768");
        arrangementIdentifier.setSortCode("770908");
        request.setSelctdArr(arrangementIdentifier);

    }

    @Given("customer is applying for amending business overdraft that is not expiring in less than threshold days.")
    public void givenCustomerIsApplyingForAmendingBusinessOverdraftThatIsNotExpiringInLessThanThresholdDays() {
        mockScenarioHelper.expectRBBSlookupCall("IBV");
        mockScenarioHelper.expectGetParentInstructionCall("P_BLN_RBB", "Apply Overdraft", null, "G_OD", "IBV", "Business Overdraft");
        List<RefInstructionRulesDto> ruleList = new ArrayList();
        ruleList.add(new RefInstructionRulesDto("P_BLN_RBB", "GR021", "Account is not eligible for Business Overdraft", "GR021", "CR050", "Overdraft, on selected account, has not started and is not expiring in given number of days", "CR050", "GRP", "ASA", "28:75", "IBV", new BigDecimal(1)));
        mockScenarioHelper.expectGetCompositeInstructionConditionCall(ruleList);
        request = dataHelper.createBusinessEligibilityRequest("P_BLN_RBB", null, dataHelper.TEST_OCIS_ID, "IBV", dataHelper.TEST_CONTACT_POINT_ID, 40, 25);
        ArrangementIdentifier arrangementIdentifier = new ArrangementIdentifier();
        arrangementIdentifier.setAccNum("225627689");
        arrangementIdentifier.setSortCode("770908");
        request.setSelctdArr(arrangementIdentifier);

    }

    @Given("customer is applying for amending business overdraft that is expiring in less than threshold days.")
    public void givenCustomerIsApplyingForAmendingBusinessOverdraftThatIsExpiringInLessThanThresholdDays() {
        mockScenarioHelper.expectRBBSlookupCall("IBV");
        mockScenarioHelper.expectGetParentInstructionCall("P_BLN_RBB", "Apply Overdraft", null, "G_OD", "IBV", "Business Overdraft");
        List<RefInstructionRulesDto> ruleList = new ArrayList();
        ruleList.add(new RefInstructionRulesDto("P_BLN_RBB", "GR021", "Account is not eligible for Business Overdraft", "GR021", "CR050", "Overdraft, on selected account, has not started and is not expiring in given number of days", "CR050", "GRP", "ASA", "28:75", "IBV", new BigDecimal(1)));
        mockScenarioHelper.expectGetCompositeInstructionConditionCall(ruleList);

        request = dataHelper.createBusinessEligibilityRequest("P_BLN_RBB", null, dataHelper.TEST_OCIS_ID, "IBV", dataHelper.TEST_CONTACT_POINT_ID, 31, 78);
        ArrangementIdentifier arrangementIdentifier = new ArrangementIdentifier();
        arrangementIdentifier.setAccNum("22562768");
        arrangementIdentifier.setSortCode("770908");
        request.setSelctdArr(arrangementIdentifier);
        request.getCustomerArrangements().get(0).getOvrdrftDtls().setEndDate(dataHelper.addToCurrentDate(71));
    }

    @Given("customer is applying for amending business overdraft that has not expired.")

    public void givenCustomerIsApplyingForAmendingBusinessOverdraftThatHasNotExpired() {
        mockScenarioHelper.expectRBBSlookupCall("IBV");
        mockScenarioHelper.expectGetParentInstructionCall("P_BLN_RBB", "Apply Overdraft", null, "G_OD", "IBV", "Business Overdraft");
        List<RefInstructionRulesDto> ruleList = new ArrayList();
        ruleList.add(new RefInstructionRulesDto("P_BLN_RBB", "GR021", "Account is not eligible for Business Overdraft", "GR021", "CR050", "Overdraft, on selected account, has not started and is not expiring in given number of days", "CR050", "GRP", "ASA", "28:75", "IBV", new BigDecimal(1)));
        mockScenarioHelper.expectGetCompositeInstructionConditionCall(ruleList);

        request = dataHelper.createBusinessEligibilityRequest("P_BLN_RBB", null, dataHelper.TEST_OCIS_ID, "IBV", dataHelper.TEST_CONTACT_POINT_ID, 30, 30);
        ArrangementIdentifier arrangementIdentifier = new ArrangementIdentifier();
        arrangementIdentifier.setAccNum("22562768");
        arrangementIdentifier.setSortCode("770908");
        request.setSelctdArr(arrangementIdentifier);
        request.getCustomerArrangements().get(0).getOvrdrftDtls().setEndDate(dataHelper.addToCurrentDate(79));

    }

    @Given("customer is applying for amending business overdraft that has expired.")
    public void givenCustomerIsApplyingForAmendingBusinessOverdraftThatHasExpired() {
        mockScenarioHelper.expectRBBSlookupCall("IBV");
        mockScenarioHelper.expectGetParentInstructionCall("P_BLN_RBB", "Apply Overdraft", null, "G_OD", "IBV", "Business Overdraft");
        List<RefInstructionRulesDto> ruleList = new ArrayList();
        ruleList.add(new RefInstructionRulesDto("P_BLN_RBB", "GR021", "Account is not eligible for Business Overdraft", "GR021", "CR050", "Overdraft, on selected account, has not started and is not expiring in given number of days", "CR050", "GRP", "ASA", "28:75", "IBV", new BigDecimal(1)));
        mockScenarioHelper.expectGetCompositeInstructionConditionCall(ruleList);

        request = dataHelper.createBusinessEligibilityRequest("P_BLN_RBB", null, dataHelper.TEST_OCIS_ID, "IBV", dataHelper.TEST_CONTACT_POINT_ID, 30, 30);
        ArrangementIdentifier arrangementIdentifier = new ArrangementIdentifier();
        arrangementIdentifier.setAccNum("22562768");
        arrangementIdentifier.setSortCode("770908");
        request.setSelctdArr(arrangementIdentifier);
        request.getCustomerArrangements().get(0).getOvrdrftDtls().setEndDate(dataHelper.subtractFromCurrentDate(79));

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


    @Then("DECI evaluates eligibility to false and  returns error condition for overdraft applied in the last threshold days on the account.")
    public void thenDECIEvaluatesEligibilityToFalseAndReturnsErrorConditionForOverdraftAppliedInTheLastThresholdDaysOnTheAccount() {

        assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        assertEquals("CR050", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
        assertEquals("Overdraft has been applied in last 28 days on the account", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonDescription());

    }


    @Then("DECI evaluates eligibility to  false and  returns error condition for overdraft expiring in less than threshold days on the account.")
    public void thenDECIEvaluatesEligibilityToFalseAndReturnsErrorConditionForOverdraftExpiringInLessThanThresholdDaysOnTheAccount() {

        assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        assertEquals("CR050", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
        assertEquals("Overdraft is expiring in less than " + "75" + " days on the account", response.getCustomerInstructions()
                .get(0)
                .getDeclineReasons()
                .get(0)
                .getReasonDescription());

    }


    @Then("DECI evaluates eligibility to  false and  returns error condition for overdraft already expired.")
    public void thenDECIEvaluatesEligibilityToFalseAndReturnsErrorConditionForOverdraftAlreadyExpired() {
        assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        assertEquals("CR050", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
        assertEquals("Overdraft is already expired", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonDescription());

    }
}

