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

import static junit.framework.TestCase.assertNotNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story37DeciFetchesGroupMnemonicOrHierarchyDetailsTest extends AbstractDeciJBehaveTestBase {

    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg dataNotAvailableErrorFaultMsg;

    String candidateInstruction;


    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("candidateInstruction is G_ISA")
    public void givenCandidateInstructionIsG_ISA() {
        candidateInstruction = "G_ISA";

        mockScenarioHelperWZ.expectGetPrdFetchChildInstructionData("P_J_ISA_15", "Junior Cash ISA", null, "1000482", "G_ISA", "ISA", "1000006", "LTB");

        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_J_ISA_15", "GR007", "Customer is not eligible for an ISA", "CR001", "GRP", "ISA Opened and fund deposited within the same tax year", "16", "CST", "LTB", new BigDecimal("0"));
        request = dataHelperWZ.createEligibilityRequest(candidateInstruction, TestDataHelper.TEST_OCIS_ID, "IBL", "0000777505");
    }

    @When("DECI invokes retrieveInstructionHierarchyForGrandParent with a valid request")

    public void whenDECIInvokesRetrieveInstructionHierarchyForGrandParentWithAValidRequest() throws DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg {
        mockControl.go();


        response = eligibilityClientWZ.determineEligibleInstructions(request);

    }


    @Then("DECI responds")

    public void thenDECIResponds() {

        assertNotNull(response);

    }


    @Given("candidateInstruction is not G_ISA")

    public void givenCandidateInstructionIsNotG_ISA() {
        candidateInstruction = "G_SAVINGS";
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_ESAVER", "eSaver", "G_SAVINGS", "Savings", null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_EASY_SVR", "Easy Saver", "G_SAVINGS", "Savings", null, "LTB");

        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_EASY_SVR", "GR009", "Customer is not eligible for savings product", "CR003", "GRP", "Customer cannot be younger that 16 years", "16", "CST", "LTB", new BigDecimal("1"));
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_ESAVER", "GR009", "Customer is not eligible for savings product", "CR001", "GRP", "Have exceeded max number of products", "5", "CST", "LTB", new BigDecimal("1"));

        request = dataHelperWZ.createEligibilityRequest(candidateInstruction, TestDataHelper.TEST_OCIS_ID, "IBL", "0000777505");
    }

    @When("DECI invokes retrieveChildInstructionHierarchy with a valid request")
    public void whenDECIInvokesRetrieveChildInstructionHierarchyWithAValidRequest() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockControl.go();

        response = eligibilityClientWZ.determineEligibleInstructions(request);
    }

}
