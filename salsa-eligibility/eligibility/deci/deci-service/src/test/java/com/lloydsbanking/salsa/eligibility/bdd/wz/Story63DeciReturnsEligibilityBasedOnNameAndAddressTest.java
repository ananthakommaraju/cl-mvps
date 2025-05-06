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
public class Story63DeciReturnsEligibilityBasedOnNameAndAddressTest extends AbstractDeciJBehaveTestBase {
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    String candidateInstruction;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("user is applying for product with $validAddress unStructured address and individual name is $nameEmpty")
    public void givenUserIsApplyingForProductWithValidUnStructuredAddressAndIndividualNameIsNonempty(String validAddress, String nameEmpty) {

        request = dataHelperWZ.createEligibilityRequest("G_INSURANCE", dataHelperWZ.TEST_OCIS_ID, "IBL", "0000777505");
        request.getCustomerDetails().getPostalAddress().addAll(dataHelperWZ.createUnstructuredPostalAddress());
        request.getCustomerDetails().setIsPlayedBy(dataHelperWZ.createIndividual());
        if (validAddress.equalsIgnoreCase("inValid")) {
            request.getCustomerDetails().getPostalAddress().get(0).getUnstructuredAddress().setPostCode(" ");
        }
        if (nameEmpty.equalsIgnoreCase("empty")) {
            request.getCustomerDetails().getIsPlayedBy().getIndividualName().get(0).setLastName("");

        }

    }

    @Given("rule is CR065")
    public void givenRuleIsCR065() {
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("G_INSURANCE", "Life Insurance", "", "lifeinsurance", null, "LTB");
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("G_INSURANCE", "GR024", "Customer is not eligible for Life Insurance", "CR065", "GRP", "Customer name and address are not valid", "2", "CST", "LTB", new BigDecimal("1"));

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


    @Then("DECI evaluates eligibility to false and result as Name or Address Validation Failed")
    public void thenDECIEvaluatesEligibilityToFalseAndResultAsNameOrAddressValidationFailed() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertEquals("Name or Address Validation Failed", response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getDescription());
    }

}
