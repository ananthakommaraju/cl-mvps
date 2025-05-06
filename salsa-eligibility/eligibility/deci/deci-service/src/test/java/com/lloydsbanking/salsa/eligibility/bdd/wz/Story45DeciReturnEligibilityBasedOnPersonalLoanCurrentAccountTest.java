package com.lloydsbanking.salsa.eligibility.bdd.wz;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.service.utility.header.GmoToGboRequestHeaderConverter;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story45DeciReturnEligibilityBasedOnPersonalLoanCurrentAccountTest extends AbstractDeciJBehaveTestBase {

    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    RequestHeader header;

    GmoToGboRequestHeaderConverter headerConverter;

    lb_gbo_sales.messages.RequestHeader gboHeader;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
        headerConverter = new GmoToGboRequestHeaderConverter();
        header = dataHelperWZ.createEligibilityRequestHeader("IBS", TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, "0000805121");
        gboHeader = headerConverter.convert(header);
    }

    @Given("Current account is available for the customer and rule is CR024")
    public void givenCurrentAccountIsAvailableForTheCustomerAndRuleIsCR024() {
        validDeciRequest();
        request.getExistingProductArrangments().get(0).getAssociatedProduct().setProductType("1");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");

    }

    private void validDeciRequest() {
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_LOAN_STP", "GR001", "Personal Loan", "CR024", "GRP", "Customer holds a current account and the customer has a Shadow Limit of 0", "9", "AGT", "BOS", new BigDecimal("1"));
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_LOAN_STP", "Personal Loan", "G_LOAN", "Personal Loan", null, "BOS");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("G_LOAN", "Personal Loan", null, "Personal Loan", null, "BOS");

        request = dataHelperWZ.createEligibilityRequest("P_LOAN_STP", TestDataHelper.TEST_OCIS_ID, "IBS", "0000805121");
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


    @Given("Current account is not available for the customer and rule is CR024")

    public void givenCurrentAccountIsNotAvailableForTheCustomerAndRuleIsCR024() {
        validDeciRequest();
        request.getExistingProductArrangments().get(0).getAssociatedProduct().setProductType("4");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");

    }


    @Then("DECI evaluates eligibility to false and return error condition Customer doesn\u2019t have current account of logged in Channel and  has a no loan")

    public void thenDECIEvaluatesEligibilityToFalseAndReturnErrorConditionCustomerDoesntHaveCurrentAccountOfLoggedInChannelAndHasANoLoan() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertEquals("CR024", response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getCode());

    }

}
