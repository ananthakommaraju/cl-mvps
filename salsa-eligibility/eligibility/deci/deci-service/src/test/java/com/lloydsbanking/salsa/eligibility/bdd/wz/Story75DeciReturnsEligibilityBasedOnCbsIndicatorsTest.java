package com.lloydsbanking.salsa.eligibility.bdd.wz;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.service.utility.header.GmoToGboRequestHeaderConverter;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
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

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story75DeciReturnsEligibilityBasedOnCbsIndicatorsTest extends AbstractDeciJBehaveTestBase {
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
        header = dataHelperWZ.createEligibilityRequestHeader("IBL", TestDataHelper.TEST_INTERACTION, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, "0000777505");
        gboHeader = headerConverter.convert(header);
        headerRetriever = new HeaderRetriever();
    }

    @Given("customer do not have cbs accounts with indicators")
    public void givenCustomerDoNotHaveCbsAccountsWithIndicators() throws DetermineEligibleInstructionsInternalServiceErrorMsg {
        List<Integer> indicatorList = new ArrayList<>();
        indicatorList.add(650);
        request = dataHelperWZ.createEligibilityRequest("P_CISA_SAV", TestDataHelper.TEST_OCIS_ID, "IBL", "0000777505");
        mockScenarioHelperWZ.expectCBSGenericGatewaySwitchCall(headerRetriever.getChannelId(header), true);
        mockScenarioHelperWZ.expectB766Call(gboHeader, "111618");
        request.setArrangementType("CA");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
        mockScenarioHelperWZ.expectF075Call(gboHeader, "F", "542107294", "097", "082", "097", "082");
        mockScenarioHelperWZ.expectE141Call(gboHeader, indicatorList, "111618", "50001762", "0");

    }

    @Given("rule is CR041")

    public void givenRuleIsCR041() {
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_CISA_SAV", "GR007", "Customer is not eligible for an ISA ", "CR041", "GRP", "Customer has Frozen Account", "13:657", "AGA", "LTB", new BigDecimal("1"));
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_CISA_SAV", "Cash ISA Saver", "G_ISA", "Cash ISA Saver", null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("G_ISA", "Cash ISA Saver", null, "Cash ISA Saver", null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_CISA_SAV", "00004", "3001116000", "LTB");
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

    @Given("customer have cbs accounts with indicators")

    public void givenCustomerHaveCbsAccountsWithIndicators() throws DetermineEligibleInstructionsInternalServiceErrorMsg {
        List<Integer> indicatorList = new ArrayList<>();
        indicatorList.add(657);
        request = dataHelperWZ.createEligibilityRequest("P_CISA_SAV", TestDataHelper.TEST_OCIS_ID, "IBL", "0000777505");
        mockScenarioHelperWZ.expectCBSGenericGatewaySwitchCall(headerRetriever.getChannelId(header), true);
        mockScenarioHelperWZ.expectB766Call(gboHeader, "111618");
        request.setArrangementType("CA");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
        mockScenarioHelperWZ.expectF075Call(gboHeader, "F", "542107294", "097", "082", "097", "082");
        mockScenarioHelperWZ.expectE141Call(gboHeader, indicatorList, "111618", "50001762", "0");
    }

    @Then("DECI evaluates eligibility to false and returns customer have CBS accounts with indicators")

    public void thenDECIEvaluatesEligibilityToFalseAndReturnsCustomerHaveCBSAccountsWithIndicators() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertTrue(response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getDescription().contains("Customer have CBS accounts with "));
    }

}
