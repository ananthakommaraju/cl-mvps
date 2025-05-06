package com.lloydsbanking.salsa.eligibility.bdd.wz;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.service.utility.header.GmoToGboRequestHeaderConverter;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
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
public class Story68DeciReturnsEligibilityBasedOnValidShadowLimitTest extends AbstractDeciJBehaveTestBase {
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

    @Given("customer has $valid shadow limit")
    public void givenCustomerHasValidShadowLimit(String value) {
        mockScenarioHelperWZ.expectCBSGenericGatewaySwitchCall(headerRetriever.getChannelId(header), true);
        mockScenarioHelperWZ.expectB766Call(gboHeader, "111618");
        if (value.equalsIgnoreCase("valid")) {
            mockScenarioHelperWZ.expectE591Call(gboHeader, "111618", "11161850000901", "10.2", "0", "R");
        }
        else {
            mockScenarioHelperWZ.expectE591Call(gboHeader, "111618", "11161850000901", "0.0", "0", "R");
        }
        request = dataHelperWZ.createEligibilityRequest("P_CAR_FIN", TestDataHelper.TEST_OCIS_ID, "IBS", "0000805121");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
    }

    @Given("rule is CR067")
    public void givenRuleIsCR067() {
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_CAR_FIN", "GR023", "Customer not eligible for car finance", "CR067", "GRP", "Shadow limit is less than zero", "0", "CST", "BOS", new BigDecimal("1"));
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_CAR_FIN", "GR023", "Customer not eligible for car finance", "CR062", "GRP", "Customer has invalid decision code", "R", "CST", "BOS", new BigDecimal("1"));
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_CAR_FIN", "CarFinance", "G_FINANCE", "Finance", null, "BOS");
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

    @Then("DECI evaluates eligibility to false and returns Customer has invalid shadowLimit")
    public void thenDECIEvaluatesEligibilityToFalseAndReturnsCustomerHasInvalidShadowLimit() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertEquals("Customer has invalid shadowLimit", response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getDescription());
    }
}
