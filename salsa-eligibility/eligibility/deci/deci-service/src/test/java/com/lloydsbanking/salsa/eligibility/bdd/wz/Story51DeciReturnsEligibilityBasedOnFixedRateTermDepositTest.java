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
public class Story51DeciReturnsEligibilityBasedOnFixedRateTermDepositTest extends AbstractDeciJBehaveTestBase {
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("customer doesn't have more than 5 product holdings of fixed rate term deposit ($mnemonic)")
    public void givenCustomerDoesntHaveMoreThan5ProductHoldingsOfFixedRateTermDepositG_ONL_FRTD(String insMnemonic) {
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData(insMnemonic, "GR016", "Customer is not eligible for Term Deposit.", "CR028", "Have exceeded maximum number of product holdings", "GRP", "5", "AVN", "HLX", new BigDecimal("1"));

        request = dataHelperWZ.createEligibilityRequest(insMnemonic, TestDataHelper.TEST_OCIS_ID, "HLX", "0000115219");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getInstructionDetails().setParentInstructionMnemonic(insMnemonic);

        if (insMnemonic.equalsIgnoreCase("G_ONL_FRTD")) {
            request.getExistingProductArrangments().get(0).getAssociatedProduct().getInstructionDetails().setInstructionMnemonic("P_TDO_1Y_M");
            mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_TDO_1Y_M", "Fixed Online Saver 1 Year", "G_ONL_FRTD", "Web Saver Fixed", null, "HLX");
            mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_TDO_1Y_M", "00004", "3001116000", "HLX");
        }
        else if (insMnemonic.equalsIgnoreCase("G_FR_TD")) {
            request.getExistingProductArrangments().get(0).getAssociatedProduct().getInstructionDetails().setInstructionMnemonic("P_TDF_1Y_M");
            mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_TDF_1Y_M", "Fixed Saver 1 Year", "G_FR_TD", "Fixed Saver", null, "HLX");
            mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_TDF_1Y_M", "00004", "3001116000", "HLX");
        }

    }

    @Given("customer have more than 5 product holdings of fixed rate term deposit ($insMnemonic)")
    public void givenCustomerHaveMoreThan5ProductHoldingsOfFixedRateTermDepositG_ONL_FRTD(String insMnemonic) {
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData(insMnemonic, "GR016", "Customer is not eligible for Term Deposit.", "CR028", "Have exceeded maximum number of product holdings", "GRP", "5", "AVN", "HLX", new BigDecimal("1"));

        request = dataHelperWZ.createEligibilityRequest(insMnemonic, TestDataHelper.TEST_OCIS_ID, "HLX", "0000115219");
        if (insMnemonic.equalsIgnoreCase("G_ONL_FRTD")) {
            for (int i = 0; i <= 6; i++) {
                ProductArrangement productArrangement = dataHelperWZ.createExistingProductArrangments("3001116000", "00004", null, "50001762", 2014);
                productArrangement.getAssociatedProduct().setInstructionDetails(new InstructionDetails());
                productArrangement.getAssociatedProduct().getInstructionDetails().setParentInstructionMnemonic(insMnemonic);
                productArrangement.getAssociatedProduct().getInstructionDetails().setInstructionMnemonic("P_TDO_1Y_M");
                request.getExistingProductArrangments().add(productArrangement);
            }

            mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_TDO_1Y_M", "Fixed Online Saver 1 Year", "G_ONL_FRTD", "Web Saver Fixed", null, "HLX");
            mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_TDO_1Y_M", "00004", "3001116000", "HLX");
        }
        else if (insMnemonic.equalsIgnoreCase("G_FR_TD")) {
            for (int i = 0; i <= 6; i++) {
                ProductArrangement productArrangement = dataHelperWZ.createExistingProductArrangments("3001116000", "00004", null, "50001762", 2014);
                productArrangement.getAssociatedProduct().setInstructionDetails(new InstructionDetails());
                productArrangement.getAssociatedProduct().getInstructionDetails().setParentInstructionMnemonic(insMnemonic);
                productArrangement.getAssociatedProduct().getInstructionDetails().setInstructionMnemonic("P_TDF_1Y_M");
                request.getExistingProductArrangments().add(productArrangement);
            }

            mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_TDF_1Y_M", "Fixed Saver 1 Year", "G_FR_TD", "Fixed Saver", null, "HLX");
            mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_TDF_1Y_M", "00004", "3001116000", "HLX");
        }
    }

    @Given("customer does not have any existing product arrangement and rule is CR028")
    public void givenCustomerDoesNotHaveAnyExistingProductArrangementAndRuleIsCR028() {
        request = dataHelperWZ.createEligibilityRequest("P_TDO_1Y_M", TestDataHelper.TEST_OCIS_ID, "HLX", "0000115219");
        request.getExistingProductArrangments().clear();
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_TDO_1Y_M", "GR016", "Customer is not eligible for Term Deposit.", "CR028", "Have exceeded maximum number of product holdings", "GRP", "5", "AVN", "HLX", new BigDecimal("1"));
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

    @Then("DECI evaluates eligibility to false and returns description for CR028")
    public void thenDECIEvaluatesEligibilityToFalseAndReturnsDescriptionForCR028() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertEquals("Customer cannot have more than 5 instances of the product.", response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getDescription());
    }

}
