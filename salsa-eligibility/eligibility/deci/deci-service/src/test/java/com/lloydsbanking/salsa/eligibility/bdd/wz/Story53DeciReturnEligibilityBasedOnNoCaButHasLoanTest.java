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

import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story53DeciReturnEligibilityBasedOnNoCaButHasLoanTest extends AbstractDeciJBehaveTestBase {
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;


    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;

    }

    @Given("Loan is notAvailable and Current account is $availability for the customers existing product")
    public void givenLoanIsNotAvailableAndCurrentAccountIsNotAvailableForTheCustomersExistingProduct(String availability) {


        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_LOAN_STP", "Personal Loan", "G_LOAN", "Personal Loan", null, "BOS");

        request = dataHelperWZ.createEligibilityRequest("P_LOAN_STP", TestDataHelper.TEST_OCIS_ID, "IBS", "0000805121");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().setProductType("4");
        if (availability.equalsIgnoreCase("Available")) {

            request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00001");
        }
        else {

            request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00005");

        }
    }

    @Given("rule is CR025")
    public void givenRuleIsCR025() {
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_LOAN_STP", "GR001", "Customer not eligible for personal loan", "CR025", "GRP", "Customer holds no current account but holds a loan", "0", "AGT", "BOS", new BigDecimal("1"));

    }

    @When("the UI calls DECI for product")
    public void whenTheUICallsDECIForProduct() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockControl.go();
        response = eligibilityClientWZ.determineEligibleInstructions(request);

    }

    @Then("DECI evaluates eligibility to true")
    public void thenDECIEvaluatesEligibilityToTrue() {
        assertTrue(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));

    }

}
