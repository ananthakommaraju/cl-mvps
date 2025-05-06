package com.lloydsbanking.salsa.eligibility.bdd.wz;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.service.utility.header.GmoToGboRequestHeaderConverter;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import com.lloydstsb.schema.enterprise.lcsm_involvedpartymanagement.ErrorInfo;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
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
public class Story67DeciReturnsEligibilityBasedOnCbsEventRuleTest extends AbstractDeciJBehaveTestBase {
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    GmoToGboRequestHeaderConverter headerConverter;


    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
        headerConverter=new GmoToGboRequestHeaderConverter();
    }

    @Given("customer does not return post from the address")

    public void givenCustomerDoesNotReturnPostFromTheAddress() throws DetermineEligibleInstructionsInternalServiceErrorMsg, ErrorInfo {
        request = dataHelperWZ.createEligibilityRequest("P_LOAN_STP", TestDataHelper.TEST_OCIS_ID, "IBL", "0000777505");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setProductIdentifier("3001116000");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
        request.setArrangementType("loans");
        mockScenarioHelperWZ.expectF075Call(headerConverter.convert(request.getHeader()),"F", "542107294", "097", "082", "097", "082");
        mockScenarioHelperWZ.expectB695Call(headerConverter.convert(request.getHeader()), "T3001116000", "37");
        List<Integer> indicators=new ArrayList<>();
        indicators.add(24);
        mockScenarioHelperWZ.expectE141Call(headerConverter.convert(request.getHeader()), indicators, "111618", "50001762", "10");
    }

    @Given("rule is CR046")

    public void givenRuleIsCR046() {
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_LOAN_STP", "GR001", "Customer not eligible for personal loan ", "CR046", "GRP", "Post returned from this address", "20", "CST", "LTB", new BigDecimal("1"));
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_LOAN_STP", "GR001", "Customer not eligible for personal loan ", "CR044", "GRP", "The customers account is dormant", "615", "AGT", "LTB", new BigDecimal("1"));
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_LOAN_STP", "Personal Loan", "G_LOAN", "Personal Loan", null, "LTB");
    }

    @Then("DECI evaluates eligibility to true")

    public void thenDECIEvaluatesEligibilityToTrue() {
        assertTrue(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
    }

    @Given("customer returns post from the address")

    public void givenCustomerReturnsPostFromTheAddress() throws DetermineEligibleInstructionsInternalServiceErrorMsg, ErrorInfo {
        request = dataHelperWZ.createEligibilityRequest("P_LOAN_STP", TestDataHelper.TEST_OCIS_ID, "IBL", "0000777505");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setProductIdentifier("3001116000");
        request.getExistingProductArrangments().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
        request.setArrangementType("loans");
        mockScenarioHelperWZ.expectF075Call(headerConverter.convert(request.getHeader()),"F", "542107294", "097", "082", "097", "082");
        mockScenarioHelperWZ.expectB695Call(headerConverter.convert(request.getHeader()), "T3001116000", "37");
        List<Integer> indicators=new ArrayList<>();
        indicators.add(20);
        mockScenarioHelperWZ.expectE141Call(headerConverter.convert(request.getHeader()), indicators, "111618", "50001762", "10");
    }

    @When("the UI calls DECI with valid request")

    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockControlService.go();
        response = eligibilityClientWZ.determineEligibleInstructions(request);
    }

    @Then("DECI evaluates eligibility to false and returns Post returned from this address")

    public void thenDECIEvaluatesEligibilityToFalseAndReturnsPostReturnedFromThisAddress() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertEquals("Post returned from this address", response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getDescription());
    }

}
