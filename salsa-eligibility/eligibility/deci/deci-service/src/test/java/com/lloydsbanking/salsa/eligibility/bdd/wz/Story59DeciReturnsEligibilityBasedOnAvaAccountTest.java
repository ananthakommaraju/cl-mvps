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
public class Story59DeciReturnsEligibilityBasedOnAvaAccountTest extends AbstractDeciJBehaveTestBase {
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("customer $has AVA account")
    public void givenCustomerHasAVAAccount(String value) {
        if (value.equalsIgnoreCase("has")) {
            request = dataHelperWZ.createEligibilityRequest("P_SILVER", TestDataHelper.TEST_OCIS_ID, "BOS", "0000805121");
            request.getExistingProductArrangments().get(0).getAssociatedProduct().setInstructionDetails(new InstructionDetails());
            request.getExistingProductArrangments().get(0).getAssociatedProduct().getInstructionDetails().setInstructionMnemonic("P_SILVER");
            mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_SILVER", "Silver AVA", "G_AVA", "Added Value Account", 5, "BOS");
            mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_SILVER", "00004", "3001116000", "BOS");
        }
        else {
            request = dataHelperWZ.createEligibilityRequest("P_CISA_SAV", TestDataHelper.TEST_OCIS_ID, "BOS", "0000805121");
            request.getExistingProductArrangments().get(0).getAssociatedProduct().setInstructionDetails(new InstructionDetails());
            request.getExistingProductArrangments().get(0).getAssociatedProduct().getInstructionDetails().setInstructionMnemonic("P_CISA_SAV");
            mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_CISA_SAV", "GR009", "Customer is not eligible for savings product", "CR032", "Customer holds an AVA account", "1", "GRP", "AGT", "BOS", new BigDecimal("1"));
            mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_CISA_SAV", "Access Cash ISA", "G_ISA", "ISA", null, "BOS");
            mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_CISA_SAV", "00004", "3001116000", "BOS");
        }
    }

    @Given("rule is CR032")
    public void givenRuleIsCR032() {
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_SILVER", "GR009", "Customer is not eligible for savings product", "CR032", "Customer holds an AVA account", "1", "GRP", "AGT", "BOS", new BigDecimal("1"));
    }

    @Given("customer does not hold any product")
    public void givenCustomerDoesNotHoldAnyProduct() {
        request = dataHelperWZ.createEligibilityRequest("P_SILVER", TestDataHelper.TEST_OCIS_ID, "BOS", "0000805121");
        request.getExistingProductArrangments().clear();
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_SILVER", "Silver AVA", "G_AVA", "Added Value Account", 5, "BOS");
        mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_SILVER", "00004", "3001116000", "BOS");
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

    @Then("DECI evaluates eligibility to false and returns Customer does not have AVA Account")
    public void thenDECIEvaluatesEligibilityToFalseAndReturnsCustomerDoesNotHaveAVAAccount() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertEquals("Customer does not have AVA Account", response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getDescription());
    }

    @Then("DECI evaluates eligibility to false and returns Customer does not have any Account")
    public void thenDECIEvaluatesEligibilityToFalseAndReturnsCustomerDoesNotHaveAnyAccount() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertEquals("Customer does not have any Account", response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getDescription());
    }
}
