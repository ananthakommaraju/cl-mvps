package com.lloydsbanking.salsa.eligibility.bdd.wz;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.client.wz.EligibilityRequestBuilder;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;
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

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story56DeciReturnsEligibilityBasedOnIsaAccountsTest extends AbstractDeciJBehaveTestBase {
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    String candidateInstruction;

    RequestHeader header;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }


    @Given("existing arrangement does not exits in the request")
    public void givenExistingArrangementDoesNotExitsInTheRequest() {
        candidateInstruction = "P_ISA_F_4Y";
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_ISA_F_1Y", "Fixed Isa One Year", "G_ISA", "fixed ISA", null, "HLX");

        header = dataHelperWZ.createEligibilityRequestHeader("IBH", TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, "0000115219");

        EligibilityRequestBuilder requestBuilder = new EligibilityRequestBuilder();
        List candidateInsList = new ArrayList();
        candidateInsList.add(candidateInstruction);
        request = requestBuilder.header(header).candidateInstructions(candidateInsList).customerDetails(dataHelperWZ.createCustomerDetails(1992, 8, 17)).build();

    }

    @Given("rule is CR035")
    public void givenRuleIsCR035() {
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_ISA_F_4Y", "Fixed Isa four Year", "G_ISA", "fixed ISA", null, "HLX");
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_ISA_F_4Y", "GR007", "Customer is not eligible for an ISA", "CR035", "GRP", "Maximum number of fixed rate ISA’s held", null, "AGT", "HLX", new BigDecimal("1"));

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


    @Given("fixed rate isa $isaExist in customers existing product")
    public void givenFixedRateIsaHeldInCustomersExistingProduct(String isaExist) {
        candidateInstruction = "P_ISA_F_4Y";
        if (isaExist.equalsIgnoreCase("held")) {
            mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_ISA_F_1Y", "Fixed Isa One Year", "G_ISA", "fixed ISA", null, "HLX");
            mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_ISA_F_1Y", "00004", "3001116001", "HLX");
        } else if (isaExist.equalsIgnoreCase("notHeld")) {
            mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_MON_SVR", "Monthly saver", "G_Savings", "Savings", null, "HLX");
            mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_MON_SVR", "00004", "3001116001", "HLX");
            mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_ISA_F_1Y", "Fixed Isa One Year", "G_ISA", "fixed ISA", null, "HLX");

        }


        EligibilityRequestBuilder requestBuilder = new EligibilityRequestBuilder();
        List candidateInsList = new ArrayList();
        candidateInsList.add(candidateInstruction);
        header = dataHelperWZ.createEligibilityRequestHeader("IBH", TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, "0000115219");
        request = requestBuilder.header(header).candidateInstructions(candidateInsList).customerDetails(dataHelperWZ.createCustomerDetails(1992, 8, 17)).build();
        request.getExistingProductArrangments().add(dataHelperWZ.createExistingDepositArrangements());

    }


    @Then("DECI evaluates eligibility to false and returns Maximum number of fixed rate ISA\u2019s held")
    public void thenDECIEvaluatesEligibilityToFalseAndReturnsMaximumNumberOfFixedRateISAsHeld() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertTrue(response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getDescription().contains("Maximum number of fixed rate ISA"));
    }

}
