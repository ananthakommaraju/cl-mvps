package com.lloydsbanking.salsa.eligibility.bdd.wz;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.service.utility.header.GmoToGboRequestHeaderConverter;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
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
public class Story65DeciReturnsEligibilityBasedOnJointSignatoryAccountsTest extends AbstractDeciJBehaveTestBase {
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    String threshold;

    RequestHeader header;

    GmoToGboRequestHeaderConverter headerConverter;

    lb_gbo_sales.messages.RequestHeader gboHeader;

    HeaderRetriever headerRetriever;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
        headerConverter = new GmoToGboRequestHeaderConverter();
        header = dataHelperWZ.createEligibilityRequestHeader("IBS", TestDataHelper.TEST_INTERACTION, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, "0000805121");
        gboHeader = headerConverter.convert(header);
        headerRetriever = new HeaderRetriever();
    }

    @Given("customer has $some accounts as joint signatory account")
    public void givenCustomerHasSomeAccountsAsJointSignatoryAccount(String value) throws DetermineEligibleInstructionsInternalServiceErrorMsg {
        threshold = "664";
        request = dataHelperWZ.createEligibilityRequest("P_CAR_FIN", TestDataHelper.TEST_OCIS_ID, "IBS", "0000805121");
        mockScenarioHelperWZ.expectCBSGenericGatewaySwitchCall(headerRetriever.getChannelId(header), true);
        mockScenarioHelperWZ.expectB766Call(gboHeader, "111618");
        request.setArrangementType("CA");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
        mockScenarioHelperWZ.expectF075Call(gboHeader, "F", "542107294", "097", "082", "097", "082");

        List<Integer> indicatorList = new ArrayList<>();
        if (value.equalsIgnoreCase("some")) {
            indicatorList.add(665);
            mockScenarioHelperWZ.expectE141Call(gboHeader, indicatorList, "111618", "50001762", "0");
        }
        else {
            indicatorList.add(664);
            mockScenarioHelperWZ.expectE141Call(gboHeader, indicatorList, "111618", "50001762", "0");
        }

    }

    @Given("rule is CR068")
    public void givenRuleIsCR068() {
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_CAR_FIN", "GR023", "Customer not eligible for car finance", "CR068", "GRP", "Joint Signature present on all accounts", threshold, "AGA", "BOS", new BigDecimal("1"));
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_CAR_FIN", "CarFinance", "G_FINANCE", "Finance", null, "BOS");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("G_FINANCE", "Finance", null, null, null, "BOS");
        mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_CAR_FIN", "00004", "3001116000", "BOS");
    }

    @When("the UI calls DECI with valid request")
    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockControl.go();
        response = eligibilityClientWZ.determineEligibleInstructions(request);
    }

    @Then("DECI evaluates eligibility to true")
    public void thenDECIEvaluatesEligibilityToTrue() {
        assertTrue(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
    }

    @Then("DECI evaluates eligibility to false and returns Customer has all Joint Signatory Accounts")
    public void thenDECIEvaluatesEligibilityToFalseAndReturnsCustomerHasAllJointSignatoryAccounts() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertEquals("Customer has all Joint Signatory Accounts", response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getDescription());
    }
}
