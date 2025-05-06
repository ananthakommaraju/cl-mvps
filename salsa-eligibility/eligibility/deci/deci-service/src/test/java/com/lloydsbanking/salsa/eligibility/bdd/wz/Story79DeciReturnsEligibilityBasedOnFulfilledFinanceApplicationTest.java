package com.lloydsbanking.salsa.eligibility.bdd.wz;


import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.service.utility.header.GmoToGboRequestHeaderConverter;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
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

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story79DeciReturnsEligibilityBasedOnFulfilledFinanceApplicationTest extends AbstractDeciJBehaveTestBase {

    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;
    RequestHeader header;

    GmoToGboRequestHeaderConverter headerConverter;

    lb_gbo_sales.messages.RequestHeader gboHeader;

    HeaderRetriever headerRetriever;

    @BeforeScenario
    public void initialize() {
        response = null;
        request = null;
        headerConverter = new GmoToGboRequestHeaderConverter();
        header = dataHelperWZ.createEligibilityRequestHeader("IBL", TestDataHelper.TEST_INTERACTION_ID, "542107294", "542107294", "0000777505");
        gboHeader = headerConverter.convert(header);
        headerRetriever = new HeaderRetriever();
    }

    @Given("customer has no fulfilled finance application")

    public void givenCustomerHasNoFulfilledFinanceApplication() {
        request = dataHelperWZ.createEligibilityRequest("P_CAR_FIN", TestDataHelper.TEST_OCIS_ID, "IBL", "0000777505");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
        mockScenarioHelperWZ.expectPamApplications("542107294", 35);
        mockScenarioHelperWZ.expectE591Call(gboHeader, "111618", "11161850000901", "10", "0", "R");

    }

    @Given("rule is CR063")

    public void givenRuleIsCR063() {

        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_CAR_FIN", "GR023", "Customer not eligible for car finance", "CR063", "GRP", "Customer already has fulfilled finance application", "null", "CST", "LTB", new BigDecimal(1));
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_CAR_FIN", "GR023", "Customer not eligible for car finance", "CR062", "GRP", "Customer has invalid decision code", "R", "CST", "LTB", new BigDecimal("1"));
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_CAR_FIN", "CarFinance", "G_FINANCE", "Finance", null, "LTB");
        mockScenarioHelperWZ.expectCBSGenericGatewaySwitchCall(headerRetriever.getChannelId(header), true);
        mockScenarioHelperWZ.expectB766Call(gboHeader, "111618");


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

    @Given("customer has fulfilled finance application")

    public void givenCustomerHasFulfilledFinanceApplication() {

        request = dataHelperWZ.createEligibilityRequest("P_CAR_FIN", TestDataHelper.TEST_OCIS_ID, "IBL", "0000777505");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
        mockScenarioHelperWZ.expectPamApplications("542107294", 29);
        mockScenarioHelperWZ.expectE591Call(gboHeader, "111618", "11161850000901", "10", "0", "R");

    }

    @Then("DECI evaluates eligibility to false and returns Customer already has fulfilled finance application")

    public void thenDECIEvaluatesEligibilityToFalseAndReturnsCustomerAlreadyHasFulfilledFinanceApplication() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertEquals("Customer already has fulfilled finance application", response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getDescription());
    }
}
