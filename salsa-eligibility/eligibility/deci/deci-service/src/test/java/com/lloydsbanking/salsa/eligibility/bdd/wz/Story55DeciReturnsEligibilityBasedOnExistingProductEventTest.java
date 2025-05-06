package com.lloydsbanking.salsa.eligibility.bdd.wz;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.service.utility.header.GmoToGboRequestHeaderConverter;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import com.lloydstsb.schema.enterprise.lcsm_involvedpartymanagement.ErrorInfo;
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story55DeciReturnsEligibilityBasedOnExistingProductEventTest extends AbstractDeciJBehaveTestBase

{
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    GmoToGboRequestHeaderConverter headerConverter;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
        headerConverter = new GmoToGboRequestHeaderConverter();
    }

    @Given("there is rule CR037 with threshold 37 and channel is LTB")
    public void givenThereIsRuleCR037WithThreshold37AndChannelIsLTB() {

        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_LOAN_STP", "Personal Loan", "G_LOAN", "Personal Loan", null, "LTB");
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_LOAN_STP", "GR001", "Customer not eligible for personal loan ", "CR044", "GRP", "The customers account is dormant", "615", "AGT", "LTB", new BigDecimal("1"));
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_LOAN_STP", "GR001", "Customer not eligible for personal loan ", "CR037", "GRP", "The account has a direct debit facility", "37", "AGT", "LTB", new BigDecimal("1"));

        request = dataHelperWZ.createEligibilityRequest("P_LOAN_STP", TestDataHelper.TEST_OCIS_ID, "IBL", "0000777505");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setProductIdentifier("3001116000");
        request.getCustomerDetails().setInternalUserIdentifier("1");
        request.setArrangementType("loans");
    }

    @When("deci is called with product Arrangement having related event $relatedEvent and status $status")
    public void whenDeciIsCalledWithProductArrangementHavingRelatedEvent37AndStatusDormant(String relatedEvent, String status) throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, ErrorInfo {

        request.getExistingProductArrangments().get(0).setLifecycleStatus(status);
        mockScenarioHelperWZ.expectB695Call(headerConverter.convert(request.getHeader()), "T3001116000", relatedEvent);
        mockScenarioHelperWZ.expectB766Call(headerConverter.convert(request.getHeader()), "111618");
        mockScenarioHelperWZ.expectE141Call(headerConverter.convert(request.getHeader()), new ArrayList<Integer>(), "111618", "50001762", "10");

        mockControlService.go();
        response = eligibilityClientWZ.determineEligibleInstructions(request);
    }

    @Then("Reason description for rule CR037 and threshold 37")
    public void thenReasonDescriptionForRuleCR037AndThreshold37() {
        assertTrue(response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getDescription().contains("Customer doesn't have an existing product with the"));
    }

    @Then("deci returns eligibility $value")
    public void thenDeciReturnsEligibilityTrue(String value) {
        if (value.equals("true")) {
            assertTrue(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        }
        else {
            assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        }

    }
}
