package com.lloydsbanking.salsa.eligibility.bdd.wz;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsExternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsInternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse;
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
public class Story76DeciReturnsEligibilityBasedOnCurrentAccountTest extends AbstractDeciJBehaveTestBase {
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("customer has current account")

    public void givenCustomerHasCurrentAccount() {
        request = dataHelperWZ.createEligibilityRequest("P_ESAVER", TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_REWARD", "Reward saver", "G_PCA", "Current Account", null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_REWARD", "00004", "3001116000", "LTB");
    }

    @Given("rule is CR034")

    public void givenRuleIsCR034() {
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_ESAVER", "GR009", "Customer is not eligible for savings product ", "CR034", "GRP", "Customer holds a current account", null, "AGT", "LTB", new BigDecimal(0));
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_ESAVER", "eSaver", "G_SAVINGS", "Savings", null, "LTB");
    }

    @When("the UI calls DECI with valid request")

    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockControlService.go();
        response = eligibilityClientWZ.determineEligibleInstructions(request);
    }

    @Then("DECI evaluates eligibility to true")

    public void thenDECIEvaluatesEligibilityToTrue() {
        assertTrue(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
    }

    @Given("customer does not have current account")

    public void givenCustomerDoesNotHaveCurrentAccount() {
        request = dataHelperWZ.createEligibilityRequest("P_ESAVER", TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_INC_SVR", "Reward saver", "G_SAVINGS", "Current Account", null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_INC_SVR", "00004", "3001116000", "LTB");

    }

    @Then("DECI evaluates eligibility to false and returns Customer does not have a current account")

    public void thenDECIEvaluatesEligibilityToFalseAndReturnsCustomerDoesNotHaveACurrentAccount() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertEquals("Customer does not have a current account", response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getDescription());

    }
}
