package com.lloydsbanking.salsa.eligibility.bdd.wz;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.client.wz.EligibilityRequestBuilder;
import com.lloydsbanking.salsa.eligibility.service.utility.header.GmoToGboRequestHeaderConverter;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story48DeciReturnEligibilityBasedOnFundDepositedIsaThisTaxYearTest extends AbstractDeciJBehaveTestBase {
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    String candidateInstruction;

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

    @Given("user is applying for isa product with no arrangement and rule is CR015")
    public void givenUserIsApplyingForIsaProductWithNoArrangementAndRuleIsCR015() {
        candidateInstruction = "P_CISA_SAV";
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_CISA_SAV", "GR007", "Customer is not eligible for an ISA", "CR015", "GRP", "Funds has been deposited this year", null, "AGA", "LTB", new BigDecimal(0));

        header = dataHelperWZ.createEligibilityRequestHeader("IBL", TestDataHelper.TEST_INTERACTION, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, "0000777505");

        EligibilityRequestBuilder requestBuilder = new EligibilityRequestBuilder();
        List candidateInsList = new ArrayList();
        candidateInsList.add(candidateInstruction);
        request = requestBuilder.header(header).candidateInstructions(candidateInsList).build();
    }

    @When("the UI calls DECI for product")
    public void whenTheUICallsDECIForProductWithNoArrangement() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockControlService.go();
        response = eligibilityClientWZ.determineEligibleInstructions(request);
    }

    @Then("DECI evaluates eligibility to true")
    public void thenDECIEvaluatesEligibilityToTrue() {
        assertTrue(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
    }

    @Given("user is applying for isa product and the existing isa has been not funded and rule is CR015")
    public void givenUserIsApplyingForIsaProductAndTheExistingIsaHasBeenNotFundedAndRuleIsCR015() throws DetermineEligibleInstructionsInternalServiceErrorMsg {
        candidateInstruction = "P_CISA_SAV";

        expectPRDData();

        request = dataHelperWZ.createEligibilityRequest(candidateInstruction, TestDataHelper.TEST_OCIS_ID, "IBL", "0000777505");
        request.setArrangementType("CA");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().setProductIdentifier("3001116001");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");

        List<Integer> indicatorList = new ArrayList<>();
        mockScenarioHelperWZ.expectCBSGenericGatewaySwitchCall(headerRetriever.getChannelId(header), true);

        mockScenarioHelperWZ.expectB766Call(gboHeader, "111618");
        mockScenarioHelperWZ.expectE141Call(gboHeader, indicatorList, "111618", "50001762", "0");
        mockScenarioHelperWZ.expectF075Call(gboHeader, "F", "542107294", "097", "082", "097", "082");
    }

    private void expectPRDData() {
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_FIX_ISA", "Fixed Isa", "G_ISA", "ISA", null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_CISA_SAV", "ISA", "G_ISA", "ISA", null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_LOAN_STP", "Personal Loan", "G_LOAN", "Personal Loan", null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_LOAN_STP", "00004", "3001116000", "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_FIX_ISA", "00004", "3001116001", "LTB");
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_CISA_SAV", "GR007", "Customer is not eligible for an ISA", "CR015", "GRP", "Funds has been deposited this year", null, "AGA", "LTB", new BigDecimal(0));
    }

    @Given("user is applying for isa product and the existing isa has been funded and rule is CR015")
    public void givenUserIsApplyingForIsaProductAndTheExistingIsaHasBeenFundedAndRuleIsCR015() throws DetermineEligibleInstructionsInternalServiceErrorMsg {
        candidateInstruction = "P_CISA_SAV";

        expectPRDData();
        request = dataHelperWZ.createEligibilityRequest(candidateInstruction, TestDataHelper.TEST_OCIS_ID, "IBL", "0000777505");
        request.setArrangementType("CA");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().setProductIdentifier("3001116001");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");

        mockScenarioHelperWZ.expectMultipleCashISASwitchCall("IBL", false);
        List<Integer> indicatorList = new ArrayList<>();
        mockScenarioHelperWZ.expectCBSGenericGatewaySwitchCall(headerRetriever.getChannelId(header), true);
        mockScenarioHelperWZ.expectB766Call(gboHeader, "111618");
        mockScenarioHelperWZ.expectE141Call(gboHeader, indicatorList, "111618", "50001762", "1200");
        mockScenarioHelperWZ.expectF075Call(gboHeader, "F", "542107294", "097", "082", "097", "082");

    }

    @Then("DECI evaluates eligibility to false and return Funds have been deposited this year")
    public void thenDECIEvaluatesEligibilityToFalseAndReturnFundsHaveBeenDepositedThisYear() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
    }

}
