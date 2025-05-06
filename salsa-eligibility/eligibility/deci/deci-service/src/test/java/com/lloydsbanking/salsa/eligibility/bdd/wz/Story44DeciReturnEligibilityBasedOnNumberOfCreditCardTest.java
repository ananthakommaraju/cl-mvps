package com.lloydsbanking.salsa.eligibility.bdd.wz;


import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class})
public class Story44DeciReturnEligibilityBasedOnNumberOfCreditCardTest extends AbstractDeciJBehaveTestBase {

    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;


    String candidateInstruction;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("customer has less than 2 credit cards")

    public void givenCustomerHasLessThan2CreditCards() {
        candidateInstruction = "G_CREDCARD";
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_FIX_ISA", "ISA", "G_ISA", null, null, "LTB");

        mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_FIX_ISA", "00004", "3001116000", "LTB");

        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("G_CREDCARD", "Credit Card", null, "Credit Card", null, "LTB");


        request = dataHelperWZ.createEligibilityRequest(candidateInstruction, TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");
    }

    @Given("rule is $rule")

    public void givenRuleIsCR031(String rule) {
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("G_CREDCARD", "GR005", "Customer  not eligible for credit card", rule, "GRP", "Customer has 2 or more credit cards", "2", "AGT", "LTB", new BigDecimal(0));

    }

    @When("the UI calls DECI for product")

    public void whenTheUICallsDECIForProduct() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockControl.go();
        response = eligibilityClientWZ.determineEligibleInstructions(request);

    }

    @Then("DECI evaluates eligibility to true")

    public void thenDECIEvaluatesEligibilityToTure() {

        assertTrue(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
    }


    @Given("customer has more than thresholdCount credit cards")

    public void givenCustomerHasMoreThanThresholdCountCreditCards() {
        candidateInstruction = "G_CREDCARD";
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_FIX_ISA", "ISA", "G_ISA", null, null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_FIX_ISA", "00004", "3001116000", "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_CC_AVSP", "Credit Card", "G_CREDCARD", null, null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_CC_AVIOS", "Credit Card", "G_CREDCARD", null, null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_CC_ADV", "Credit Card", "G_CREDCARD", null, null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_CC_AVIOS", "00000", "120120377064", "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_CC_ADV", "00000", "120350546780", "LTB");

        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("G_CREDCARD", "Credit Card", null, "Credit Card", null, "LTB");


        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("G_CREDCARD", "GR005", "Customer  not eligible for credit card", "CR031", "GRP", "Customer has 2 or more credit cards", "2", "AGT", "LTB", new BigDecimal(0));

        request = dataHelperWZ.createEligibilityRequest(candidateInstruction, TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");
        List<ProductArrangement> productArrangementList = new ArrayList();

        productArrangementList.add(dataHelperWZ.createExistingProductArrangments("120350546780", "00000", "3", "50001763", 2013));
        productArrangementList.add(dataHelperWZ.createExistingProductArrangments("120120377064", "00000", "3", "50001765", 2014));
        productArrangementList.add(dataHelperWZ.createExistingProductArrangments("120130377064", "00000", "3", "50001766", 2014));
        request.getExistingProductArrangments().addAll(productArrangementList);

    }


    @Then("DECI evaluates eligibility to false and returns error condition for customer having 2 or more credit cards.")

    public void thenDECIEvaluatesEligibilityToFalseAndReturnsErrorConditionForCustomerHaving2OrMoreCreditCards() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
    }

}

