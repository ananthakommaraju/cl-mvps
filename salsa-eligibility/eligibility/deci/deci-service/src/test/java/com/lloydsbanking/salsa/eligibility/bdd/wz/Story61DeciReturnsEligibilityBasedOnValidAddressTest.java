package com.lloydsbanking.salsa.eligibility.bdd.wz;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
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
public class Story61DeciReturnsEligibilityBasedOnValidAddressTest extends AbstractDeciJBehaveTestBase {
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    String candidateInstruction;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("user is applying for product with $validAddress $structured address and rule is CR047")
    public void givenUserIsApplyingForProductWithValidPostCodeAddressAndRuleIsCR047(String validAddress, String structAdddress) {
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_LOAN_STP", "Personal Loan", "G_LOAN", "Personal Loan", null, "LTB");

        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_LOAN_STP", "GR001", "Customer not eligible for personal loan", "CR047", "GRP", "Address Validation Failed", "2", "CST", "LTB", new BigDecimal("1"));

        request = dataHelperWZ.createEligibilityRequest("P_LOAN_STP", dataHelperWZ.TEST_OCIS_ID, "IBL", "0000777505");
        if (validAddress.equalsIgnoreCase("valid")) {
            if (structAdddress.equalsIgnoreCase("structured")) {
                request.getCustomerDetails().getPostalAddress().addAll(dataHelperWZ.createStructuredPostalAddress());
            }
            else if (structAdddress.equalsIgnoreCase("unStructured")) {
                request.getCustomerDetails().getPostalAddress().addAll(dataHelperWZ.createUnstructuredPostalAddress());
            }
        }
        else if (validAddress.equalsIgnoreCase("inValid") && structAdddress.equalsIgnoreCase("unStructured")) {
            request.getCustomerDetails().getPostalAddress().addAll(dataHelperWZ.createUnstructuredPostalAddress());
            request.getCustomerDetails().getPostalAddress().get(0).getUnstructuredAddress().setPostCode(" ");
        }
    }

    @When("the UI calls DECI for product")
    public void whenTheUICallsDECIForProduct() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockControlService.go();
        response = eligibilityClientWZ.determineEligibleInstructions(request);
    }

    @Then("DECI evaluates eligibility to true")
    public void thenDECIEvaluatesEligibilityToTrue() {
        assertTrue(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
    }


    @Then("DECI evaluates eligibility to false and result as Address Validation Failed")
    public void thenDECIEvaluatesEligibilityToFalseAndResultAsAddressValidationFailed() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertEquals("Address Validation Failed", response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getDescription());
    }

}
