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

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story46DeciReturnsEligibilityBasedOnOffshoreAccountsRuleTest extends AbstractDeciJBehaveTestBase {
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    String candidateInstruction;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;

    }

    @Given("there is rule CR013 configured CR013 with threshold $threshold")

    public void givenThereIsRuleCR013ConfiguredCR013WithThreshold301642(String threshold) {
        candidateInstruction = "P_CISA_SAV";

        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_CISA_SAV", "Offshore Account", "G_ISA", "ISA", null, "LTB");

        mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_CISA_SAV", "00107", "902", "LTB");

        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_CISA_SAV", "GR008", "Customer is not eligible for a Cash ISA", "CR013", "GRP", "Customer Account has only off-shore accounts", threshold, "AGT", "LTB", new BigDecimal(1));

        request = dataHelperWZ.createEligibilityRequest(candidateInstruction, TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");

    }

    @When("deci is called with Product Arrangement having sort code 301642")

    public void whenDeciIsCalledWithProductArrangementHavingSortCode301642() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockControl.go();

        request.getExistingProductArrangments().get(0).getFinancialInstitution().getHasOrganisationUnits().get(0).setSortCode("301642");
        response = eligibilityClientWZ.determineEligibleInstructions(request);
    }


    @Then("deci returns eligibility false")
    public void thenDeciReturnsEligibilityFalse() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertEquals("P_CISA_SAV", response.getProductEligibilityDetails().get(0).getProduct().get(0).getInstructionDetails().getInstructionMnemonic());

    }

    @Then("Reason description for rule CR013")
    public void thenReasonDescriptionForRuleCR013() {


        assertEquals("CR013", response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getCode());
    }


    @Then("deci returns eligibility true")

    public void thenDeciReturnsEligibilityTrue() {
        assertTrue(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertEquals("P_CISA_SAV", response.getProductEligibilityDetails().get(0).getProduct().get(0).getInstructionDetails().getInstructionMnemonic());
    }

}
