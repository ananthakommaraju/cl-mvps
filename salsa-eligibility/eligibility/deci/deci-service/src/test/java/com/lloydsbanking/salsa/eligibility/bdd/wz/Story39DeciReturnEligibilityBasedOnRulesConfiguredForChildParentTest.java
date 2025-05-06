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
import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story39DeciReturnEligibilityBasedOnRulesConfiguredForChildParentTest extends AbstractDeciJBehaveTestBase

{
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    String candidateInstruction = null;


    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("instruction rules are mapped for child Instruction")
    public void givenInstructionRulesAreMappedForChildInstruction() {
        candidateInstruction = "P_CISA_SAV";
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_CISA_SAV", "Cash ISA Saver", "G_ISA", "ISA", null, "LTB");
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_CISA_SAV", "GR007", "Customer is not eligible for an ISA", "CR003", "GRP", "Customer cannot be younger that 16 years", "16", "CST", "LTB", new BigDecimal("1"));
        request = dataHelperWZ.createEligibilityRequest(candidateInstruction, TestDataHelper.TEST_OCIS_ID, "IBL", "0000777505");

    }

    @When("UI calls DECI with a valid request")
    public void whenUICallsDECIWithAValidRequest() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockControl.go();

        response = eligibilityClientWZ.determineEligibleInstructions(request);

    }

    @Then("DECI evaluates eligibility based on candidate instruction mnemonic")
    public void thenDECIEvaluatesEligibilityBasedOnCandidateInstructionMnemonic() {
        assertNotNull(response);
        assertEquals("P_CISA_SAV", response.getProductEligibilityDetails().get(0).getProduct().get(0).getInstructionDetails().getInstructionMnemonic());

    }


    @Given("instruction rules are mapped for parent Instruction")
    public void givenInstructionRulesAreMappedForParentInstruction() {
        candidateInstruction = "E_BOND";
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("E_BOND", "eBond", "G_SAVINGS", "Savings", null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("G_SAVINGS", "savings", null, null, null, "LTB");
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("G_SAVINGS", "GR007", "Customer is not eligible for an ISA", "CR003", "GRP", "Customer cannot be younger that 16 years", "16", "CST", "LTB", new BigDecimal("1"));
        request = dataHelperWZ.createEligibilityRequest(candidateInstruction, TestDataHelper.TEST_OCIS_ID, "IBL", "0000777505");

    }


    @Then("DECI evaluates eligibility based on parent instruction mnemonic of candidate instruction")
    public void thenDECIEvaluatesEligibilityBasedOnParentInstructionMnemonicOfCandidateInstruction() {
        assertNotNull(response);
        assertEquals("E_BOND", response.getProductEligibilityDetails().get(0).getProduct().get(0).getInstructionDetails().getInstructionMnemonic());

    }

}




