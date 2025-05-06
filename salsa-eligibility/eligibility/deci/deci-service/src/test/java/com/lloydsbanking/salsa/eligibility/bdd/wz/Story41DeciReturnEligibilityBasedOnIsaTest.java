package com.lloydsbanking.salsa.eligibility.bdd.wz;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.service.utility.header.GmoToGboRequestHeaderConverter;
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
import java.util.Calendar;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story41DeciReturnEligibilityBasedOnIsaTest extends AbstractDeciJBehaveTestBase {
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    String candidateInstruction;

    RequestHeader header;

    lb_gbo_sales.messages.RequestHeader gboHeader;

    GmoToGboRequestHeaderConverter headerConverter;


    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
        headerConverter = new GmoToGboRequestHeaderConverter();
        header = dataHelperWZ.createEligibilityRequestHeader("IBL", TestDataHelper.TEST_INTERACTION, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, "0000777505");
        gboHeader = headerConverter.convert(header);
    }

    @Given(" ISA is not opened this Tax year")

    public void givenISAIsNotOpenedThisTaxYear() {
        candidateInstruction = "P_CISA_SAV";
        mockScenarioHelperWZ.expectGetPrdFetchChildInstructionData("P_CISA_SAV", "Cash ISA Saver", null, "1000494", "G_ISA", "ISA", "1000006", "LTB");

        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_FIX_ISA", "ISA", "G_ISA", null, null, "LTB");

        mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_FIX_ISA", "00004", "3001116000", "LTB");

        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_CISA_SAV", "GR007", "Customer is not eligible for an ISA", "CR016", "GRP", "ISA opened this tax year", null, "AGA", "LTB", new BigDecimal(0));
        List<Integer> indicators = new ArrayList<>();
        indicators.add(1);
        mockScenarioHelperWZ.expectE141Call(gboHeader, indicators, "111618", "50001762", "10");

        request = dataHelperWZ.createEligibilityRequest(candidateInstruction, TestDataHelper.TEST_OCIS_ID, "IBL", "0000777505");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
    }

    @When("UI calls DECI for ISA")

    public void whenUICallsDECIForISA() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockControl.go();

        response = eligibilityClientWZ.determineEligibleInstructions(request);
    }

    @Then("DECI evaluates eligibility as true")

    public void thenDECIEvaluatesEligibilityAsTrue() {
        assertEquals("P_CISA_SAV", response.getProductEligibilityDetails().get(0).getProduct().get(0).getInstructionDetails().getInstructionMnemonic());
        assertTrue(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));

    }


    @Given(" ISA is opened this Tax year")

    public void givenISAIsOpenedThisTaxYear() {
        candidateInstruction = "P_CISA_SAV";
        mockScenarioHelperWZ.expectGetPrdFetchChildInstructionData("P_CISA_SAV", "Cash ISA Saver", null, "1000494", "G_ISA", "ISA", "1000006", "LTB");

        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_FIX_ISA", "ISA", "G_ISA", null, null, "LTB");

        mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_FIX_ISA", "00004", "3001116000", "LTB");

        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_CISA_SAV", "GR007", "Customer is not eligible for an ISA", "CR016", "GRP", "ISA opened this tax year", null, "AGA", "LTB", new BigDecimal(0));

        List<Integer> indicators = new ArrayList<>();
        indicators.add(1);
        mockScenarioHelperWZ.expectE141Call(gboHeader, indicators, "111618", "50001762", "10");
        Calendar today = Calendar.getInstance();
        request = dataHelperWZ.createEligibilityRequest(candidateInstruction, TestDataHelper.TEST_OCIS_ID, "IBL", "0000777505");
        request.getExistingProductArrangments().get(0).setArrangementStartDate((dataHelperWZ.createXMLGregorianCalendar(today.get(Calendar.YEAR), 5, 7)));
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");

    }


    @Then("DECI evaluates eligibility to false  and returns  error condition for already existing ISA in the given tax year")

    public void thenDECIEvaluatesEligibilityToFalseAndReturnsErrorConditionForAlreadyExistingISAInTheGivenTaxYear() {

        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));

        assertEquals("P_CISA_SAV", response.getProductEligibilityDetails().get(0).getProduct().get(0).getInstructionDetails().getInstructionMnemonic());

    }

}






