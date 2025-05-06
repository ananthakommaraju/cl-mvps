package com.lloydsbanking.salsa.eligibility.bdd.wz;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.client.wz.EligibilityRequestBuilder;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import lib_sim_gmo.messages.RequestHeader;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story66DeciReturnsEligibilityBasedOnLtsbProdArrngmntsTest extends AbstractDeciJBehaveTestBase {
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    RequestHeader header;

    String candidateInstruction;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("customer $account hold any ltsb product")
    public void givenCustomerDoesnotHoldAnyLtsbProduct(String account) {

        candidateInstruction = "P_ESAVER";
        header = dataHelperWZ.createEligibilityRequestHeader("IBL", TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, "0000777505");

        EligibilityRequestBuilder requestBuilder = new EligibilityRequestBuilder();
        List candidateInsList = new ArrayList();
        candidateInsList.add(candidateInstruction);
        request = requestBuilder.header(header).candidateInstructions(candidateInsList).customerDetails(dataHelperWZ.createCustomerDetails(1992, 02, 22)).build();

        if (!account.equalsIgnoreCase("doesnot")) {
            request.getExistingProductArrangments().add(dataHelperWZ.createExistingDepositArrangements());
        }

    }


    @Given("rule is CR033")
    public void givenRuleIsCR033() {
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_ESAVER", "Easy Saver", "G_SAVINGS", "esaevr", null, "LTB");

        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_ESAVER", "GR009", "Customer is not eligible for savings product", "CR033", "Customer Holds a LTSB product", "1", "GRP", "AGT", "LTB", new BigDecimal("1"));
    }

    @When("the UI calls DECI with valid request")
    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockControl.go();
        response = eligibilityClientWZ.determineEligibleInstructions(request);
    }

    @Then("DECI evaluates eligibility to false and returns Customer does not have LTSB account")
    public void thenDECIEvaluatesEligibilityToFalseAndReturnsCustomerDoesNotHaveLTSBAccount() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertEquals("Customer does not have LTSB account", response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getDescription());
    }


    @Then("DECI evaluates eligibility to true")
    public void thenDECIEvaluatesEligibilityToTrue() {
        assertTrue(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
    }

}
