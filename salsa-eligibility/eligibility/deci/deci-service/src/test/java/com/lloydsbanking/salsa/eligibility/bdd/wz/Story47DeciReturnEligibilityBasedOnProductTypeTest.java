package com.lloydsbanking.salsa.eligibility.bdd.wz;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import lib_sim_bo.businessobjects.InstructionDetails;
import lib_sim_bo.businessobjects.Product;
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
public class Story47DeciReturnEligibilityBasedOnProductTypeTest extends AbstractDeciJBehaveTestBase {
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    String candidateInstruction;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("Customer has applied for MonthlySaver product")
    public void givenCustomerHasAppliedForMonthlySaverProduct() {
        candidateInstruction = "P_MON_SVR";
        request = dataHelperWZ.createEligibilityRequest(candidateInstruction, TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");
    }

    @Given("he has an existing MonthlySaver product")
    public void givenHeHasAnExistingMonthlySaverProduct() {
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData(candidateInstruction, "GR009", "Customer is not eligible for savings product", "CR006", "GRP", "Cannot have product already have.", "1", "AGA", "LTB", new BigDecimal("1"));
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData(candidateInstruction, "Monthly Saver", "G_SAVINGS", "Savings", null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionLookupData(candidateInstruction, "00004", "3001116000", "LTB");
        request.setAssociatedProduct(new Product());
        request.getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        request.getAssociatedProduct().getInstructionDetails().setInstructionMnemonic(candidateInstruction);
    }

    @Given("he does not have any existing MonthlySaver product")
    public void givenHeDoesNotHaveAnyExistingMonthlySaverProduct() {
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData(candidateInstruction, "GR009", "Customer is not eligible for savings product", "CR006", "GRP", "Cannot have product already have.", "1", "AGA", "LTB", new BigDecimal("1"));
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_CASH", "Cash Account", "G_PCA", "PCA", null, "LTB");
        request.setAssociatedProduct(new Product());
        request.getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        request.getAssociatedProduct().getInstructionDetails().setInstructionMnemonic("P_CASH");
        mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_CASH", "00004", "3001116000", "LTB");
    }

    @When("the UI calls DECI with valid request")
    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockControlService.go();
        response = eligibilityClientWZ.determineEligibleInstructions(request);
    }

    @Then("DECI evaluates eligibility as true")
    public void thenDECIEvaluatesEligibilityAsTrue() {
        assertTrue(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
    }

    @Then("DECI evaluates eligibility as false and returns decline reason for applied product")
    public void thenDECIEvaluatesEligibilityAsFalseAndReturnsDeclineReasonForAppliedProduct() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertEquals("Customer cannot apply for a product of the same type as they already have.", response.getProductEligibilityDetails()
                .get(0)
                .getDeclineReasons()
                .get(0)
                .getDescription());
    }

}
