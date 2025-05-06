package com.lloydsbanking.salsa.eligibility.bdd.wz;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.eligibility.service.utility.header.GmoToGboRequestHeaderConverter;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydstsb.schema.enterprise.lcsm_involvedpartymanagement.ErrorInfo;
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
import org.junit.Assert;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class})
public class Story50DeciReturnEligibilityBasedOnShadowLimitAndThresholdTest extends AbstractDeciJBehaveTestBase {
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    RequestHeader header;

    GmoToGboRequestHeaderConverter headerConverter;

    lb_gbo_sales.messages.RequestHeader gboHeader;

    HeaderRetriever headerRetriever;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
        headerConverter = new GmoToGboRequestHeaderConverter();
        header = dataHelperWZ.createEligibilityRequestHeader("IBS", TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, "0000805121");
        gboHeader = headerConverter.convert(header);
        headerRetriever = new HeaderRetriever();
    }

    @Given("shadow limit amount is greater than threshold and rule is CR023")

    public void givenShadowLimitAmountIsGreaterThanAssignedForTheAccountAndRuleIsCR023() throws ErrorInfo {
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_LOAN_STP", "GR001", "Personal Loan", "CR023", "GRP", "Customer holds a current account and has a Shadow Limit grea", "8", "CST", "BOS", new BigDecimal("1"));

        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_LOAN_STP", "Personal Loan", "G_LOAN", "Personal Loan", null, "BOS");
        mockScenarioHelperWZ.expectCBSGenericGatewaySwitchCall(headerRetriever.getChannelId(header), true);
        mockScenarioHelperWZ.expectB766Call(gboHeader, "111618");
        mockScenarioHelperWZ.expectE220Call(gboHeader, "111618", "11161850000901", "10", "0");
        request = dataHelperWZ.createEligibilityRequest("P_LOAN_STP", TestDataHelper.TEST_OCIS_ID, "IBS", "0000805121");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setProductIdentifier("3001116000");
        request.setArrangementType("loans");
        request.getCustomerDetails().setInternalUserIdentifier("1");
        mockScenarioHelperWZ.expectB695Call(headerConverter.convert(request.getHeader()), "T3001116000", "37");
        mockScenarioHelperWZ.expectE141Call(headerConverter.convert(request.getHeader()), new ArrayList<Integer>(), "111618", "50001762", "10");
    }


    @Given("customer has current account")

    public void givenCustomerHasCurrentAccount() {
        request.getExistingProductArrangments().get(0).getAssociatedProduct().setProductType("1");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
    }

    @When("the UI calls DECI with valid request")

    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockControlService.go();
        response = eligibilityClientWZ.determineEligibleInstructions(request);
    }

    @Then("DECI evaluates eligibility to true")

    public void thenDECIEvaluatesEligibilityToTrue() {
        assertEquals("P_LOAN_STP", response.getProductEligibilityDetails().get(0).getProduct().get(0).getInstructionDetails().getInstructionMnemonic());
        assertTrue(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
    }


    @Given("shadow limit amount less than 0 and rule is CR023")

    public void givenShadowLimitAmountLessThan0ForTheAccountAndRuleIsCR023() throws ErrorInfo {
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_LOAN_STP", "GR001", "Personal Loan", "CR023", "GRP", "Customer holds a current account and has a Shadow Limit grea", "8", "CST", "BOS", new BigDecimal("1"));

        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_LOAN_STP", "Personal Loan", "G_LOAN", "Personal Loan", null, "BOS");
        mockScenarioHelperWZ.expectCBSGenericGatewaySwitchCall(headerRetriever.getChannelId(header), true);
        mockScenarioHelperWZ.expectB766Call(gboHeader, "111618");
        mockScenarioHelperWZ.expectE220Call(gboHeader, "111618", "11161850000901", "0", "0");
        request = dataHelperWZ.createEligibilityRequest("P_LOAN_STP", TestDataHelper.TEST_OCIS_ID, "IBS", "0000805121");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setProductIdentifier("3001116000");
        request.setArrangementType("loans");
        request.getCustomerDetails().setInternalUserIdentifier("1");
        mockScenarioHelperWZ.expectB695Call(headerConverter.convert(request.getHeader()), "T3001116000", "37");
        mockScenarioHelperWZ.expectE141Call(headerConverter.convert(request.getHeader()), new ArrayList<Integer>(), "111618", "50001762", "10");
    }


    @Given("shadow limit amount  is between 0 and threshold and rule is CR023")

    public void givenShadowLimitAmountIsBetween0AndThresholdAndRuleIsCR023() throws ErrorInfo {
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_LOAN_STP", "GR001", "Personal Loan", "CR023", "GRP", "Customer holds a current account and has a Shadow Limit grea", "8", "CST", "BOS", new BigDecimal("1"));
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_LOAN_STP", "Personal Loan", "G_LOAN", "Personal Loan", null, "BOS");
        mockScenarioHelperWZ.expectCBSGenericGatewaySwitchCall(headerRetriever.getChannelId(header), true);
        mockScenarioHelperWZ.expectB766Call(gboHeader, "111618");
        mockScenarioHelperWZ.expectE220Call(gboHeader, "111618", "11161850000901", "7", "0");
        request = dataHelperWZ.createEligibilityRequest("P_LOAN_STP", TestDataHelper.TEST_OCIS_ID, "IBS", "0000805121");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setProductIdentifier("3001116000");
        request.setArrangementType("loans");
        request.getCustomerDetails().setInternalUserIdentifier("1");
        mockScenarioHelperWZ.expectB695Call(headerConverter.convert(request.getHeader()), "T3001116000", "37");
        mockScenarioHelperWZ.expectE141Call(headerConverter.convert(request.getHeader()), new ArrayList<Integer>(), "111618", "50001762", "10");
    }

    @Given("customer does not have current account")

    public void givenCustomerDoesNotHaveCurrentAccount() {
        request.getExistingProductArrangments().get(0).getAssociatedProduct().setProductType("2");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
    }

    @Then("DECI evaluates eligibility to false and returns decline reasons in response for failing CR023")

    public void thenDECIEvaluatesEligibilityToFalseAndReturnsDeclineReasonsInResponseForFailingCR023() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        Assert.assertEquals("Customer has current account and Shadow Limit amount is less than threshold8", response.getProductEligibilityDetails()
                .get(0)
                .getDeclineReasons()
                .get(0)
                .getDescription());
    }

}
