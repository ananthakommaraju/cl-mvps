package com.lloydsbanking.salsa.eligibility.bdd.wz;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story62DeciReturnsEligibilityBasedOnNumberOfCurrentAccountsTest extends AbstractDeciJBehaveTestBase {
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    String threshold;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("customer has current accounts $less than threshold")
    public void givenCustomerHasCurrentAccountsLessThanThreshold(String value) {
        request = dataHelperWZ.createEligibilityRequest("P_NEW_BASIC", TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");
        if (value.equalsIgnoreCase("less")) {
            addProductArrangements(2);
        }
        else {
            addProductArrangements(6);
        }
    }

    private void addProductArrangements(int count) {
        request.getExistingProductArrangments().clear();
        for (int i = 0; i < count; i++) {
            ProductArrangement productArrangement = dataHelperWZ.createExistingDepositArrangements();
            productArrangement.getAssociatedProduct().setProductType("1");
            productArrangement.getAssociatedProduct().getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
            productArrangement.getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
            request.getExistingProductArrangments().add(productArrangement);
        }
    }

    @Given("rule is CR042")
    public void givenRuleIsCR042() {
        threshold = "5";
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_NEW_BASIC", "GR020", "Customer not eligible for Current Account", "CR042", "GRP", "Customer Applied product holding Limit exceeds", threshold, "AGA", "LTB", new BigDecimal("1"));
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_NEW_BASIC", "Basic", "G_ISA", "ISA", null, "LTB");
    }

    @Given("customer does not hold any product")
    public void givenCustomerDoesNotHoldAnyProduct() {
        request = dataHelperWZ.createEligibilityRequest("P_NEW_BASIC", TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");
        request.getExistingProductArrangments().clear();
    }

    @When("the UI calls DECI with valid request")
    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockControl.go();
        response = eligibilityClientWZ.determineEligibleInstructions(request);
    }

    @Then("DECI evaluates eligibility to true")
    public void thenDECIEvaluatesEligibilityToTrue() {
        assertTrue(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
    }

    @Then("DECI evaluates eligibility to false and returns Customer cannot have more than threshold instances of the product.")
    public void thenDECIEvaluatesEligibilityToFalseAndReturnsCustomerCannotHaveMoreThanThresholdInstancesOfTheProduct() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertTrue(response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getDescription().contains("Customer cannot have more than"));
    }

}
