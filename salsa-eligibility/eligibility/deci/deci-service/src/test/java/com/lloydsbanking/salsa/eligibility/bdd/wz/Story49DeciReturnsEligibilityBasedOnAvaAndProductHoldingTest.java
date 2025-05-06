package com.lloydsbanking.salsa.eligibility.bdd.wz;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import lib_sim_bo.businessobjects.InstructionDetails;
import lib_sim_bo.businessobjects.ProductArrangement;
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
public class Story49DeciReturnsEligibilityBasedOnAvaAndProductHoldingTest extends AbstractDeciJBehaveTestBase {
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("customer has AVAs $value than maximum AVAs and rule is CR017")
    public void givenCustomerHasAVAsLessThanMaximumAVAsAndRuleIsCR017(String value) {
        request = dataHelperWZ.createEligibilityRequest("P_EASY_SVR", TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");
        request.getExistingProductArrangments().clear();
        if (value.equalsIgnoreCase("more")) {
            addProductArrangementToList(6);
        }
        else if (value.equalsIgnoreCase("less")) {
            addProductArrangementToList(4);
        }
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_EASY_SVR", "GR009", "Customer is not eligible for savings product", "CR017", "Have exceeded max number of products", "GRP", "5", "AVN", "LTB", new BigDecimal("1"));
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_EASY_SVR", "Easy Saver", "G_SAVINGS", "Savings", null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_EASY_SVR", "00004", "3001116000", "LTB");
    }

    private void addProductArrangementToList(int count) {
        for (int i = 0; i < count; i++) {
            ProductArrangement productArrangement = dataHelperWZ.createExistingProductArrangments("3001116000", "00004", null, "50001762", 2014);
            productArrangement.getAssociatedProduct().setInstructionDetails(new InstructionDetails());
            productArrangement.getAssociatedProduct().getInstructionDetails().setInstructionMnemonic("P_EASY_SVR");
            request.getExistingProductArrangments().add(productArrangement);
        }

    }

    @Given("customer has product holdings $value than maximum product holdings and rule is CR020")
    public void givenCustomerHasProductHoldingsLessThanMaximumProductHoldingsAndRuleIsCR020(String value) {
        request = dataHelperWZ.createEligibilityRequest("P_INS_SAV", TestDataHelper.TEST_OCIS_ID, "HLX", "0000115219");
        request.getExistingProductArrangments().clear();
        if (value.equalsIgnoreCase("more")) {
            addProductArrangementToList(6);
        }
        else if (value.equalsIgnoreCase("less")) {
            addProductArrangementToList(4);
        }
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_INS_SAV", "GR009", "Customer is not eligible for savings product", "CR020", "Have exceeded maximum number of product holdings", "GRP", "5", "AVN", "HLX", new BigDecimal("1"));
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_INS_SAV", "Web Saver", "G_SAVINGS", "Savings", null, "HLX");
        mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_INS_SAV", "00004", "3001116000", "HLX");
    }

    @Given("customer does not have any product holding and rule is CR017")
    public void givenCustomerDoesNotHaveAnyProductHolding() {
        request = dataHelperWZ.createEligibilityRequest("P_EASY_SVR", TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");
        request.getExistingProductArrangments().clear();
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_EASY_SVR", "GR009", "Customer is not eligible for savings product", "CR017", "Have exceeded max number of products", "GRP", "5", "AVN", "LTB", new BigDecimal("1"));
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_EASY_SVR", "Easy Saver", "G_SAVINGS", "Savings", null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_EASY_SVR", "00004", "3001116000", "LTB");
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

    @Then("DECI evaluates eligibility to false and returns description for $rule")
    public void thenDECIEvaluatesEligibilityToFalseAndReturnsErrorCondition(String rule) {
        if (rule.equalsIgnoreCase("CR017")) {
            assertEquals("CR017", response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getCode());
        }
        else if (rule.equalsIgnoreCase("CR020")) {
            assertEquals("CR020", response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getCode());
        }
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertEquals("Customer cannot have more than 5 instances of the product.", response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getDescription());
    }

}
