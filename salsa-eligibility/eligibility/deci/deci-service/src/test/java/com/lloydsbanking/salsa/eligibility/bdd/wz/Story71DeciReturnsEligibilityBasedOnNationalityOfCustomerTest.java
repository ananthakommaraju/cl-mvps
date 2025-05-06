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
public class Story71DeciReturnsEligibilityBasedOnNationalityOfCustomerTest extends AbstractDeciJBehaveTestBase {
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;


    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;

    }

    @Given("customer nationality is allowed")

    public void givenCustomerNationalityIsAllowed() {
        request = dataHelperWZ.createEligibilityRequest("P_CISA_SAV", TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");
        request.getCustomerDetails().getIsPlayedBy().setNationality("AHR");
    }


    @Then("DECI evaluates eligibility to true")

    public void thenDECIEvaluatesEligibilityToTrue() {
        assertTrue(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
    }


    @Given("customer nationality is not allowed")

    public void givenCustomerNationalityIsNotAllowed() {
        request = dataHelperWZ.createEligibilityRequest("P_CISA_SAV", TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");
        request.getCustomerDetails().getIsPlayedBy().setNationality("AFG");
    }

    @Given("rule is CR039")

    public void givenRuleIsCR039() {
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_CISA_SAV", "GR007", "Customer is not eligible for an ISA", "CR039", "GRP", "Customer belongs to High risk Country", "AFG", "CST", "LTB", new BigDecimal("1"));
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_CISA_SAV", "Cash ISA Saver", "G_ISA", "ISA", null, "LTB");

    }

    @When("the UI calls DECI with valid request")

    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockControlService.go();
        response = eligibilityClientWZ.determineEligibleInstructions(request);
    }

    @Then("DECI evaluates eligibility to false and returns nationality is not allowed")

    public void thenDECIEvaluatesEligibilityToFalseAndReturnsNationalityIsNotAllowed() {

        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertEquals("nationality is not allowed", response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getDescription());
    }

}
