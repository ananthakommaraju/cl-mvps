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
import org.junit.Ignore;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.util.StringTokenizer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
@Ignore
@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story57DeciReturnsEligibilityBasedOnNumberOfGroupMnemonicsTest extends AbstractDeciJBehaveTestBase {
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("customer has products that $doesOrDoesNot exceed the maximum products defined for a mnemonic group and rule is CR043")
    public void givenCustomerHasLesserNumberOfProductsThanMaximumProductsDefinedForAMnemonicsGroupAndRuleIsCR043(String value) {
        String thresholdMnemonic = "P_CLSCVTG:P_SLVRVTG:P_GOLDVTG:P_PLATVTG";
        String threshold = "3";
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_CLSCVTG", "GR020", "Customer not eligible for Current Account", "CR043", "Customer Product holding Limit exceeds", "GRP", thresholdMnemonic, "AGA", "LTB", new BigDecimal("1"));
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_CLSCVTG", "GR020", "Customer not eligible for Current Account", "CR043", "Customer Product holding Limit exceeds", "GRP", threshold, "AGA", "LTB", new BigDecimal("2"));

        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_CLSCVTG_T", "Classic with Vantage Account", "G_VANTAGE_T", "Vantage", null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_CLSCVTG", "Classic with Vantage Account", "G_VANTAGE", "Vantage", null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_SLVRVTG", "Classic with Vantage Account", "G_VANTAGE", "Vantage", null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_GOLDVTG", "Classic with Vantage Account", "G_VANTAGE", "Vantage", null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_PLATVTG", "Classic with Vantage Account", "G_VANTAGE", "Vantage", null, "LTB");
        request = dataHelperWZ.createEligibilityRequest("P_CLSCVTG", TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");

        if (value.equalsIgnoreCase("does not")) {

            mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_CLSCVTG_T", "00004", "3001116000", "LTB");

        }
        else {
            StringTokenizer indicators = new StringTokenizer(thresholdMnemonic, ":");
            int i = 0;
            while (indicators.hasMoreTokens()) {
                ProductArrangement productArrangement = dataHelperWZ.createExistingProductArrangments("300111600" + i, "00004", null, "50001762", 2014);
                request.getExistingProductArrangments().add(productArrangement);
                mockScenarioHelperWZ.expectGetPrdInstructionLookupData(indicators.nextToken(),"00004", "300111600" + i, "LTB");
                i++;
            }
        }

    }

    @Given("customer does not have any existing product and rule is CR043")
    public void givenCustomerDoesNotHaveAnyExistingProductAndRuleIsCR043() {

        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_CLSCVTG", "Classic with Vantage Account", "G_VANTAGE", "Vantage", null, "LTB");
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_CLSCVTG", "GR020", "Customer not eligible for Current Account", "CR043", "Customer Product holding Limit exceeds", "GRP", "TRYU", "AGA", "LTB", new BigDecimal("1"));
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_CLSCVTG", "GR020", "Customer not eligible for Current Account", "CR043", "Customer Product holding Limit exceeds", "GRP", "2", "AGA", "LTB", new BigDecimal("2"));
        request = dataHelperWZ.createEligibilityRequest("P_CLSCVTG", TestDataHelper.TEST_OCIS_ID, "IBL", "0000777505");


    }

    @When("the UI calls DECI with valid request")
    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockControlService.go();
        response = eligibilityClientWZ.determineEligibleInstructions(request);
    }

    @Then("DECI evaluates eligibility to true")
    public void thenDECIEvaluatesEligibilityToTrue() {
        assertTrue(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
    }

    @Then("DECI evaluates eligibility to false and returns Customer cannot have more than threshold products for group of mnemonics defined (cr 043 rule)")
    public void thenDECIEvaluatesEligibilityToFalseAndReturnsCustomerCannotHaveMoreThanThresholdProductsForGroupOfMnemonicsDefinedcr043Rule() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertTrue(response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getDescription().contains("Customer cannot have more than"));
    }
}
