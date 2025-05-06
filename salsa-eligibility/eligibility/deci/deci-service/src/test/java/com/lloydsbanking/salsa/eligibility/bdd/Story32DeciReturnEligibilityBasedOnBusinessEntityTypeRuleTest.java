package com.lloydsbanking.salsa.eligibility.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import lb_gbo_sales.businessobjects.BusinessArrangement;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story32DeciReturnEligibilityBasedOnBusinessEntityTypeRuleTest extends AbstractDeciJBehaveTestBase {

    DetermineElegibileInstructionsRequest request;

    DetermineElegibleInstructionsResponse response;

    String instructionMnemonic;

    String entityType;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;

    }

    @Given("rule is $rule")
    public void givenRuleIsCR024(String rule) {
        mockScenarioHelper.expectRBBSlookupCall("STL");
        List<RefInstructionRulesDto> ruleList = new ArrayList();
        if (rule.equals("CR051")) {
            ruleList.add(new RefInstructionRulesDto("P_BOD_RBB", "GR021", "Account is not eligible for Business Overdraft", "GR021", "CR051", "Business entity is valid for online lending ", "CR051", "GRP", "ASB", "002:004:005:006:007:012:013:014", "STL", new BigDecimal(1)));
            ruleList.add(new RefInstructionRulesDto("P_BLN_RBB", "GR022", "Customer is not eligible for Business Loan", "GR021", "CR051", "Business entity is valid for online lending ", "CR051", "GRP", "ASB", "004:005:006:012:013:014", "STL", new BigDecimal(1)));
        }
        mockScenarioHelper.expectGetCompositeInstructionConditionCall(ruleList);

    }

    @Given("valid business entity type is set on business for which OD is being applied")
    public void givenValidBusinessEntityTypeIsSetOnBusinessForWhichODIsBeingApplied() {
        mockScenarioHelper.expectRBBSlookupCall("STL");
        instructionMnemonic = "P_BOD_RBB";
        entityType = "002";
        mockScenarioHelper.expectGetParentInstructionCall("P_BOD_RBB", "Apply Overdraft", null, "G_OD", "STL", "Business Overdraft");
    }

    @Given("invalid business entity type is set on business for which OD is being applied")
    public void givenInvalidBusinessEntityTypeIsSetOnBusinessForWhichODIsBeingApplied() {
        mockScenarioHelper.expectRBBSlookupCall("STL");
        instructionMnemonic = "P_BOD_RBB";
        entityType = "011";
        mockScenarioHelper.expectGetParentInstructionCall("P_BOD_RBB", "Apply Overdraft", null, "G_OD", "STL", "Business Overdraft");
    }

    @Given("valid business entity type is set on business for which loans is being applied")
    public void givenValidBusinessEntityTypeIsSetOnBusinessForWhichLoansIsBeingApplied() {
        mockScenarioHelper.expectRBBSlookupCall("STL");
        instructionMnemonic = "P_BLN_RBB";
        entityType = "006";
        mockScenarioHelper.expectGetParentInstructionCall("P_BLN_RBB", "Apply Loan", null, "G_BLOAN", "STL", "Business Loan");
    }

    @Given("invalid business entity type is set on business for which loans is being applied")
    public void givenInvalidBusinessEntityTypeIsSetOnBusinessForWhichLoansIsBeingApplied() {
        mockScenarioHelper.expectRBBSlookupCall("STL");
        instructionMnemonic = "P_BLN_RBB";
        entityType = "002";
        mockScenarioHelper.expectGetParentInstructionCall("P_BLN_RBB", "Apply Loan", null, "G_BLOAN", "STL", "Business Loan");
    }

    @When("the UI calls DECI")
    public void whenTheUICallsDECI() {
        request = dataHelper.createEligibilityRequest(instructionMnemonic, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_COMMERCIAL_CHANNEL_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, request.getHeader().getChannelId());

        request.setSelctdBusnsId("1234");
        BusinessArrangement businessArrangement = new BusinessArrangement();
        businessArrangement.setBusinessId("1234");
        businessArrangement.setEnttyTyp(entityType);
        request.getBusinessArrangements().add(businessArrangement);
        mockControl.go();
        try {
            response = eligibilityClient.determineEligibleInstructions(request);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Then("DECI evaluates eligibility to true")
    public void thenDECIEvaluatesEligibilityToTrue() {
        assertTrue(response.getCustomerInstructions().get(0).isEligibilityIndicator());
    }


    @Then("DECI evaluates eligibility to false")
    public void thenDECIEvaluatesEligibilityToFalse() {

        assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        assertEquals("CR051", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
    }

}
