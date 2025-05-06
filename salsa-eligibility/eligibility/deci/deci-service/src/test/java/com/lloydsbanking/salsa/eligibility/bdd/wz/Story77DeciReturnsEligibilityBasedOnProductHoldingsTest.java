package com.lloydsbanking.salsa.eligibility.bdd.wz;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import lib_sim_bo.businessobjects.InstructionDetails;
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
import java.util.StringTokenizer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story77DeciReturnsEligibilityBasedOnProductHoldingsTest extends AbstractDeciJBehaveTestBase {
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("existing arrangement does not exist in the request")

    public void givenExistingArrangementDoesNotExistInTheRequest() {
        request = dataHelperWZ.createEligibilityRequest("P_PREMVTG", TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");
        request.getExistingProductArrangments().clear();
    }

    @Given("rule is CR040")
    public void givenRuleIsCR040() {
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_PREMVTG", "GR020", "Customer not eligible for Current Account", "CR040", "GRP", "Customer Vantage Product holding  Limit exceeds", "3", "AGA", "LTB", new BigDecimal("1"));
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_PREMVTG", "Premier with Vantage Account", "G_VANTAGE", "Vantage", null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_PREMVTG", "00004", "3001116000", "LTB");
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

    @Given("customer have more than maximum number of vantage instances of the product")
    public void givenCustomerHaveMoreThanMaximumNumberOfVantageInstancesOfTheProduct() {
        request = dataHelperWZ.createEligibilityRequest("P_PREMVTG", TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_CLSCVTG_T", "Classic with Vantage Account", "G_VANTAGE_T", "Vantage", null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_CLSCVTG", "Classic with Vantage Account", "G_VANTAGE", "Vantage", null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_SLVRVTG", "Classic with Vantage Account", "G_VANTAGE", "Vantage", null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_GOLDVTG", "Classic with Vantage Account", "G_VANTAGE", "Vantage", null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_PLATVTG", "Classic with Vantage Account", "G_VANTAGE", "Vantage", null, "LTB");
        String vantageProducts = "P_CLSCVTG:P_SLVRVTG:P_GOLDVTG:P_PLATVTG";
        StringTokenizer indicators = new StringTokenizer(vantageProducts, ":");
        int i = 0;
        while (indicators.hasMoreTokens()) {
            ProductArrangement productArrangement = dataHelperWZ.createExistingProductArrangments("300111600" + i, "00004", null, "50001762", 2014);
            request.getExistingProductArrangments().add(productArrangement);
            mockScenarioHelperWZ.expectGetPrdInstructionLookupData(indicators.nextToken(), "00004", "300111600" + i, "LTB");
            i++;

        }

    }

    @Then("DECI evaluates eligibility to false and returns Customer cannot have more than maxNumOfVantage instances of the product.")
    public void thenDECIEvaluatesEligibilityToFalseAndReturnsCustomerCannotHaveMoreThanMaxNumOfVantageInstancesOfTheProduct() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertTrue(response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getDescription().contains("Customer cannot have more than"));
    }

    @Given("customer do not have more than maximum number of vantage instances of the product")
    public void givenCustomerDoNotHaveMoreThanMaximumNumberOfVantageInstancesOfTheProduct() {
        request = dataHelperWZ.createEligibilityRequest("P_PREMVTG", TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getInstructionDetails().setInstructionMnemonic("P_PREMVTG");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getInstructionDetails().setParentInstructionMnemonic("G_VANTAGE");
    }
}
