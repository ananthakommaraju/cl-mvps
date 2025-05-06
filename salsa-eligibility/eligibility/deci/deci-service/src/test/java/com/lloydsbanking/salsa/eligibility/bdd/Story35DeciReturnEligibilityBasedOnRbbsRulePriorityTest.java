package com.lloydsbanking.salsa.eligibility.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
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
import org.junit.Assert;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story35DeciReturnEligibilityBasedOnRbbsRulePriorityTest extends AbstractDeciJBehaveTestBase {


    DetermineElegibileInstructionsRequest request;

    DetermineElegibleInstructionsResponse response;


    String productMnemonic;

    RequestHeader header;

    boolean allRulesPassed = false;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("Audit Events rule set prior to default rules")
    public void givenAuditEventsRuleSetPriorToDefaultRules() throws DetermineEligibleInstructionsInternalServiceErrorMsg {
        mockScenarioHelper.expectRBBSlookupCall("BTV");

        List<RefInstructionRulesDto> ruleList = new ArrayList();
        ruleList.add(new RefInstructionRulesDto("P_BLN_RBB", "GR022", "Customer is not eligible for Business Loan", "GR022", "CR057", "Customer has not applied for business loan/overdraft in given number of days through digital", "CR057", "GRP", "CST", "02:01", "BTV", new BigDecimal(1)));
        ruleList.add(new RefInstructionRulesDto("P_BLN_RBB", "GR022", "Customer is not eligible for Business Loan", "GR022", "CR052", "Customer does not have MBC role.", "CR052", "GRP", "ASB", "MBC", "BTV", new BigDecimal(1)));

        mockScenarioHelper.expectGetCompositeInstructionConditionCall(ruleList);

    }

    @Given("all rules are passed")
    public void givenAllRulesArePassed() throws DetermineEligibleInstructionsInternalServiceErrorMsg {
        mockScenarioHelper.expectRBBSlookupCall("BTV");
        allRulesPassed = true;
        header = dataHelper.createEligibilityRequestHeader("BTV", dataHelper.TEST_INTERACTION_ID, dataHelper.TEST_OCIS_ID, dataHelper.TEST_CUSTOMER_ID, dataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectB093Call("02:01", header, false);

    }


    @Given("$default rule has error")
    public void givenDefaultRuleHasError(String ruleType) throws DetermineEligibleInstructionsInternalServiceErrorMsg {
        mockScenarioHelper.expectRBBSlookupCall("BTV");
        header = dataHelper.createEligibilityRequestHeader("BTV", dataHelper.TEST_INTERACTION_ID, dataHelper.TEST_OCIS_ID, dataHelper.TEST_CUSTOMER_ID, dataHelper.TEST_CONTACT_POINT_ID);
        if ("default".equals(ruleType)) {
            mockScenarioHelper.expectB093Call("02:01", header, false);
        }
        else if ("Audit".equals(ruleType)) {
            mockScenarioHelper.expectB093Call("02:01", header, true);
        }

    }

    @When("the UI calls DECI with valid request")
    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, "BTV");

        productMnemonic = "P_BLN_RBB";
        request = dataHelper.createBusinessEligibilityRequest(productMnemonic, null, dataHelper.TEST_OCIS_ID, "BTV", dataHelper.TEST_CONTACT_POINT_ID, 40, 25);
        if (allRulesPassed) {
            request.getBusinessArrangements().get(0).setRolesInCtxt("CUS1");
        }

        mockControl.go();
        response = eligibilityClient.determineEligibleInstructions(request);
    }

    @Then("DECI evaluates eligibility to true")
    public void thenDECIEvaluatesEligibilityToTrue() {
        assertTrue(response.getCustomerInstructions().get(0).isEligibilityIndicator());
    }


    @When("the UI calls DECI with $ruleType rule condition fail")
    public void whenTheUICallsDECIWithDefaultRuleConditionFail(String ruleType) throws DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, "BTV");

        productMnemonic = "P_BLN_RBB";
        request = dataHelper.createBusinessEligibilityRequest(productMnemonic, null, dataHelper.TEST_OCIS_ID, "BTV", dataHelper.TEST_CONTACT_POINT_ID, 40, 25);

        if ("default".equals(ruleType)) {
            request.getBusinessArrangements().get(0).setRolesInCtxt("MBC");
        }

        mockControl.go();
        response = eligibilityClient.determineEligibleInstructions(request);

    }

    @Then("DECI evaluates eligibility to false with $ruleType rule decline condition")
    public void thenDECIEvaluatesEligibilityToFalseWithDefaultRuleDeclineCondition(String ruleType) {

        if ("default".equals(ruleType)) {
            assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
            assertEquals("CR052", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
            assertEquals("Customer has MBC role.", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonDescription());

        }
        else if ("Audit".equals(ruleType)) {


            assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
            Assert.assertEquals("CR057", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
            Assert.assertEquals("Customer has applied for loan/overdraft 2 times which is more than threshold 01  in last 02 days", response.getCustomerInstructions()
                    .get(0)
                    .getDeclineReasons()
                    .get(0)
                    .getReasonDescription());

        }
    }

}
