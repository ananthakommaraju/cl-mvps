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

import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story38DeciFetchesTheInstructionMnemonicDetailsForTheProductHoldingsTest extends AbstractDeciJBehaveTestBase {
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg dataNotAvailableErrorFaultMsg;


    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("request  has existing product arrangement present")
    public void givenRequestHasExistingProductArrangementPresent() {
        request = dataHelperWZ.createEligibilityRequest("P_CISA_SAV", TestDataHelper.TEST_OCIS_ID, "IBL", "0000777505");

    }

    @Given("instruction mnemonic is present in prd for the existing product arrangement")
    public void givenInstructionMnemonicIsPresentInPrdForTheExistingProductArrangement() {

        mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_AB_1Y_Y", "00004", "3001116000", "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_AB_1Y_Y", "Access Bond 1 Year", "G_AB_TD", "Access Bond", null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_CISA_SAV", "Cash ISA Saver", "G_ISA", "ISA", null, "LTB");
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_CISA_SAV", "GR007", "Customer is not eligible for an ISA", "CR003", "GRP", "Customer cannot be younger that 16 years", "16", "CST", "LTB", new BigDecimal("1"));

    }


    @Given("instruction mnemonic is not present in prd for the existing product arrangement")
    public void givenInstructionMnemonicIsNotPresentInPrdForTheExistingProductArrangement() {
        mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_AB_1Y_Y", "00004", "12345896", "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_CISA_SAV", "Cash ISA Saver", "G_ISA", "ISA", null, "LTB");
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_CISA_SAV", "GR007", "Customer is not eligible for an ISA", "CR003", "GRP", "Customer cannot be younger that 16 years", "16", "CST", "LTB", new BigDecimal("1"));

    }

    @When("the UI calls DECI with a valid request")
    public void whenTheUICallsDECIWithAValidRequest() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockControl.go();

        response = eligibilityClientWZ.determineEligibleInstructions(request);

    }

    @Then("DECI responds")
    public void thenDECIResponds() {
        assertNotNull(response);
    }

}
