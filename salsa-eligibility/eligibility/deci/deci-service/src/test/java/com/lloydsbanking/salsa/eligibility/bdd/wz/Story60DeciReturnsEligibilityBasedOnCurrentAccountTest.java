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
public class Story60DeciReturnsEligibilityBasedOnCurrentAccountTest extends AbstractDeciJBehaveTestBase {

    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("customer has current account and rule is CR036")

    public void givenCustomerHasCurrentAccountAndRuleIsCR036() {
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_LOAN_STP", "GR001", "Customer not eligible for personal loan ", "CR036", "GRP", "The account captured by the customer must be a current account", "1", "AGT", "LTB", new BigDecimal("1"));
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_LOAN_STP", "Personal Loan", "G_LOAN", "Personal Loan", null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("G_LOAN", "Personal Loan", null, "Personal Loan", null, "LTB");
        request = dataHelperWZ.createEligibilityRequest("P_LOAN_STP", TestDataHelper.TEST_OCIS_ID, "IBL", "0000777505");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().setProductType("1");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
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

    @Given("customer does not have current account and rule is CR036")

    public void givenCustomerDoesNotHaveCurrentAccountAndRuleIsCR036() {
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_LOAN_STP", "GR001", "Customer not eligible for personal loan ", "CR036", "GRP", "The account captured by the customer must be a current account", "1", "AGT", "LTB", new BigDecimal("1"));
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_LOAN_STP", "Personal Loan", "G_LOAN", "Personal Loan", null, "LTB");
        request = dataHelperWZ.createEligibilityRequest("P_LOAN_STP", TestDataHelper.TEST_OCIS_ID, "IBL", "0000777505");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().setProductType("4");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
    }

    @Then("DECI evaluates eligibility to false and returns Customer doesn't have a current account")

    public void thenDECIEvaluatesEligibilityToFalseAndReturnsCustomerDoesntHaveACurrentAccount() {
        {
            assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
            assertEquals("CR036", response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getCode());
        }
    }
}
