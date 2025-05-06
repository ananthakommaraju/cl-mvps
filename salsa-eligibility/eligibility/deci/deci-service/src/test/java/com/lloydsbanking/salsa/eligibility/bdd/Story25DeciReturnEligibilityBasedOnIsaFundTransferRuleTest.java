package com.lloydsbanking.salsa.eligibility.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import lb_gbo_sales.DepositArrangement;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.businessobjects.LimitCondition;
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
public class Story25DeciReturnEligibilityBasedOnIsaFundTransferRuleTest extends AbstractDeciJBehaveTestBase {

    DetermineElegibileInstructionsRequest request;

    DetermineElegibleInstructionsResponse response;

    BigDecimal maximumTransactionAmount;

    BigDecimal headRoomAmount;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;

    }

    @Given("rule is $rule")
    public void givenRuleIsCR024(String rule) {
        List<RefInstructionRulesDto> ruleList = new ArrayList();
        if (rule.equals("CR015")) {
            ruleList.add(new RefInstructionRulesDto("P_Onl_F_4Y", "GR008", "Customer is not eligible for a Cash ISA", "GR008", "CR015", "Funds has been deposited this year ", "CR015", "GRP", "AGA", "null", "IBL", null));
            ruleList.add(new RefInstructionRulesDto("G_ISA", "GR007", "Customer is not eligible for an ISA", "GR007", "CR015", "Funds has been deposited this year ", "CR015", "GRP", "AGA", "null", "IBL", null));
        }
        mockScenarioHelper.expectGetCompositeInstructionConditionCall(ruleList);

    }

    @Given("max amount and head room amount are set equal on a ISA account")
    public void givenMaxAmountAndHeadRoomAmountAreSetOnAAccountForWhichISAIsBeingApplied() {
        mockScenarioHelper.expectGetProductArrangementInstructionCall("P_Onl_F_4Y", "T", "4", "IBL", "0071776000");
        mockScenarioHelper.expectGetParentInstructionCall("P_Onl_F_4Y", "4 year LB Online FRISA", null, "G_ISA", "IBL", "IBL");
        maximumTransactionAmount = new BigDecimal(100);
        headRoomAmount = new BigDecimal(100);
    }

    @When("the UI calls DECI for ISA")
    public void whenTheUICallsDECIForISA() {
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID);

        request = dataHelper.createEligibilityRequest("P_Onl_F_4Y", TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        for (ProductArrangement productArrangement : request.getCustomerArrangements()) {
            if (productArrangement instanceof DepositArrangement) {

                LimitCondition maxTransactionAmoutLimitCondition = new LimitCondition();
                maxTransactionAmoutLimitCondition.setValue(maximumTransactionAmount);
                LimitCondition headRoomAmountLimitCondition = new LimitCondition();
                headRoomAmountLimitCondition.setValue(headRoomAmount);
                ((DepositArrangement) productArrangement).setMaximumTransactionAmount(maxTransactionAmoutLimitCondition);
                ((DepositArrangement) productArrangement).setHeadRoomAmount(headRoomAmountLimitCondition);
                ((DepositArrangement) productArrangement).setParentInstructionMnemonic("G_ISA");
            }
        }
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

    @Given("max amount and head room amount are not equal on a ISA account")
    public void givenMaxAmountAndHeadRoomAmountAreNotEqualOnAAccountForWhichISAIsBeingApplied() {
        mockScenarioHelper.expectGetProductArrangementInstructionCall("P_Onl_F_4Y", "T", "4", "IBL", "0071776000");
        mockScenarioHelper.expectGetParentInstructionCall("P_Onl_F_4Y", "4 year LB Online FRISA", null, "G_ISA", "IBL", "IBL");
        maximumTransactionAmount = new BigDecimal(100);
        headRoomAmount = new BigDecimal(1);
    }

    @Then("DECI evaluates eligibility to false")
    public void thenDECIEvaluatesEligibilityToFalse() {

        assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        assertEquals("CR015", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
        assertEquals("Funds have been deposited this year", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonDescription());
    }

}
