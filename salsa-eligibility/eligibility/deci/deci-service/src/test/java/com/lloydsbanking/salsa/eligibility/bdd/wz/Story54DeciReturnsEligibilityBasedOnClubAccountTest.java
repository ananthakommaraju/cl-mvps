package com.lloydsbanking.salsa.eligibility.bdd.wz;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import lib_sim_bo.businessobjects.InstructionDetails;
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
public class Story54DeciReturnsEligibilityBasedOnClubAccountTest extends AbstractDeciJBehaveTestBase {

    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("customer $has club account and rule is CR049")
    public void givenCustomerHasClubAccountAndRuleIsCR049(String has) {
        String insMnemonic;

        if (has.equalsIgnoreCase("has")) {
            insMnemonic = "P_SLVR_CLB";
            mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData(insMnemonic, "Club Lloyds Monthly Saver", "G_SAVINGS", "Savings", null, "LTB");
            mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_SLVR_CLB", "E Saser", "G_SAVINGS", "savings", null, "LTB");

        }
        else {
            insMnemonic = "P_ESAVER";
            mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData(insMnemonic, "eSaver", "G_SAVINGS", "Savings", null, "LTB");
            mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_ESAVER", "E Saser", "G_SAVINGS", "savings", null, "LTB");

        }
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("G_SAVINGS", "G Savings", null, "savings", null, "LTB");
        request = dataHelperWZ.createEligibilityRequest(insMnemonic, TestDataHelper.TEST_OCIS_ID, "IBL", "0000777505");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().setInstructionDetails(new InstructionDetails());

        mockScenarioHelperWZ.expectGetPrdInstructionLookupData(insMnemonic, "00004", "3001116000", "LTB");

        /*request.getExistingProductArrangments().get(0).getAssociatedProduct().getInstructionDetails().setInstructionMnemonic(insMnemonic);
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getInstructionDetails().setParentInstructionMnemonic("G_SAVINGS");*/

        mockScenarioHelperWZ.expectRefInstructionRulesPrdData(insMnemonic, "GR009", "Customer is not eligible for savings product", "CR049", "Customer holds Club Account", "GRP", "1", "CST", "LTB", new BigDecimal("1"));
        mockScenarioHelperWZ.expectGetPrdInstructionLookupData(insMnemonic, "00004", "3001116000", "LTB");
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

    @Then("DECI evaluates eligibility to false and returns description Customer does not hold Club Account")
    public void thenDECIEvaluatesEligibilityToFalseAndReturnsDescriptionForCR049() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertEquals("Customer does not hold Club Account", response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getDescription());
    }

}
