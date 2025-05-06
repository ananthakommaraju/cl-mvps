package com.lloydsbanking.salsa.eligibility.bdd.wz;


import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import lib_sim_bo.businessobjects.Product;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;

import static org.junit.Assert.*;

@Category(AcceptanceTest.class )
public class Story73DeciReturnsEligibilityBasedOnCreditCardTest extends AbstractDeciJBehaveTestBase {
    DetermineEligibleCustomerInstructionsRequest request;
    DetermineEligibleCustomerInstructionsResponse response;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("customer is eligible to apply for a credit card")
    public void givenCustomerIsEligibleToApplyForACreditCard() {
        request = dataHelperWZ.createEligibilityRequest("P_CC_PLAT", TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().setProductType("2");

    }

    @Then("DECI evaluates eligibility to true")
    public void thenDECIEvaluatesEligibilityToTrue() {
        assertTrue(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
    }

    @Given("customer is not eligible to apply for a credit card")
    public void givenCustomerIsNotEligibleToApplyForACreditCard() {
        request = dataHelperWZ.createEligibilityRequest("P_CC_PLAT", TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().setProductType("3");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setProductIdentifier("2032306000");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
        request.setAssociatedProduct(new Product());
        request.getAssociatedProduct().setProductIdentifier("20197");
    }

    @Given("rule is CR048")
    public void givenRuleIsCR048() {
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_CC_PLAT", "GR005", "Customer  not eligible for credit card", "CR048", "GRP", "Check Product Eligibility Type", null, "AGA", "LTB", new BigDecimal(0));
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_CC_PLAT", "P_CC_PLAT", "G_CREDCARD", "Credit Card", null, "LTB");
        mockScenarioHelperWZ.expectExternalSystemProductsData(1001850L,"00004",	20047L,"2032306000");
        mockScenarioHelperWZ.expectProductEligibilityRulesData(1001850L,"CO_HOLD",30197L,20047L, null,null);
    }

    @When("the UI calls DECI with valid request")
    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockControlService.go();
        response =eligibilityClientWZ.determineEligibleInstructions(request);
    }

    @Then("DECI evaluates eligibility to false and returns customer not eligible to apply for a credit card")
    public void thenDECIEvaluatesEligibilityToFalseAndReturnsCustomerNotEligibleToApplyForACreditCard() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertEquals("Customer not eligible to apply for a credit card", response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getDescription());
    }
}