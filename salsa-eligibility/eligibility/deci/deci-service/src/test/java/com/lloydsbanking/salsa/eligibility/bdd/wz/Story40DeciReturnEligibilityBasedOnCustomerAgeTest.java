package com.lloydsbanking.salsa.eligibility.bdd.wz;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
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
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story40DeciReturnEligibilityBasedOnCustomerAgeTest extends AbstractDeciJBehaveTestBase {
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg dataNotAvailableErrorfault1Msg;

    String candidateInstruction;

    int year;

    int month;

    int day;

    int currentYear = Calendar.getInstance().get(Calendar.YEAR);

    String channelId;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("customer is not older than 74 years of age and channel is $channelCode")
    public void givenCustomerIsNotOlderThan74YearsOfAgeAndChannelIs(String channelCode) {
        channelId = channelCode;
        year = currentYear - 60;
        month = 01;
        day = 01;
    }

    @Given("he is applying for personal loans")
    public void givenHeIsApplyingForPersonalLoans() {
        candidateInstruction = "P_LOAN";

        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_LOAN", "Personal Loan", "G_LOAN", "Loan", null, "LTB");
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_LOAN", "GR001", "Customer not eligible for personal loan", "CR001", "GRP", "Customer cannot be older that 74 years", "74", "CST", "LTB", new BigDecimal("1"));

    }


    @Given("customer is older than 74 years of age and channel is $channelCode")
    public void givenCustomerIsOlderThan74YearsOfAgeAndChannelIs(String channelCode) {
        channelId = channelCode;
        year = currentYear - 80;
        month = 01;
        day = 01;
    }


    @Given("customer is not younger than 16 years of age and channel is $channelCode")
    public void givenCustomerIsNotYoungerThan16YearsOfAgeAndChannelIs(String channelCode) {
        channelId = channelCode;
        year = currentYear - 20;
        month = 01;
        day = 01;
    }

    @Given("he is applying for cash isa")
    public void givenHeIsApplyingForCashIsa() {
        candidateInstruction = "P_CISA_SAV";
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_CISA_SAV", "Cash ISA Saver", "G_ISA", "ISA", null, "LTB");
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_CISA_SAV", "GR007", "Customer is not eligible for an ISA", "CR003", "GRP", "Customer cannot be younger that 16 years ", "16", "CST", "LTB", new BigDecimal("1"));

    }

    @Given("customer is younger than 16 years of age and channel is $channelCode")
    public void givenCustomerIsYoungerThan16YearsOfAgeAndChannelIs(String channelCode) {
        channelId = channelCode;
        year = currentYear - 5;
        month = 01;
        day = 01;
    }

    @Given("customer is not younger than 18 years of age and channel is $channelCode")
    public void givenCustomerIsNotYoungerThan18YearsOfAgeAndChannelIs(String channelCode) {
        channelId = channelCode;
        year = currentYear - 20;
        month = 01;
        day = 01;

    }

    @Given("he is applying for P_LOAN_STP")
    public void givenHeIsApplyingForP_LOAN_STP() {
        candidateInstruction = "P_LOAN_STP";
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_LOAN_STP", "Personal Loan", "G_LOAN", "Personal Loan", null, "LTB");
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_LOAN_STP", "GR001", "Customer not eligible for personal loan ", "CR002", "GRP", "Customer cannot be younger that 18 years", "18", "CST", "LTB", new BigDecimal("1"));

    }

    @Given("customer is  younger than 18 years of age and channel is $channelCode")
    public void givenCustomerIsYoungerThan18YearsOfAgeAndChannelIs(String channelCode) {
        channelId = channelCode;
        year = currentYear - 10;
        month = 01;
        day = 01;
    }

    @When("the UI calls DECI with valid request")
    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {

        request = dataHelperWZ.createEligibilityRequestBOD(candidateInstruction, dataHelperWZ.TEST_OCIS_ID, channelId, "0000777505", year, month, day);
        mockControlService.go();
        try {
            response = eligibilityClientWZ.determineEligibleInstructions(request);
        }
        catch (DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg dataNotAvailableErrorfault1Msg) {
            dataNotAvailableErrorfault1Msg = dataNotAvailableErrorfault1Msg;
        }
    }


    @Then("DECI evaluates eligibility to true")
    public void thenDECIEvaluatesEligibilityToTrue() {
        assertTrue(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
    }

    @Then("DECI evaluates eligibility to false and returns error condition for age older than threshold")
    public void thenDECIEvaluatesEligibilityToFalseAndReturnsErrorConditionForAgeOlderThanThreshold() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertEquals("CR001", response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getCode());
        assertEquals("Customer's age is null or Customer cannot be older than 74 years.", response.getProductEligibilityDetails()
                .get(0)
                .getDeclineReasons()
                .get(0)
                .getDescription());

    }


    @Then("DECI evaluates eligibility to false for cash isa and returns error condition for age younger than threshold")
    public void thenDECIEvaluatesEligibilityToFalseForCashIsaAndReturnsErrorConditionForAgeYoungerThanThreshold() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertEquals("CR003", response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getCode());
        assertEquals("Customer's age is null or  Customer cannot be younger than 16 years.", response.getProductEligibilityDetails()
                .get(0)
                .getDeclineReasons()
                .get(0)
                .getDescription());

    }


    @Then("DECI evaluates eligibility to false for P_LOAN_STP and returns error condition for customer age younger than threshold")
    public void thenDECIEvaluatesEligibilityToFalseForP_LOAN_STPAndReturnsErrorConditionForCustomerAgeYoungerThanThreshold() {
        assertFalse(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertEquals("CR002", response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getCode());
        assertEquals("Customer's age is null or  Customer cannot be younger than 18 years.", response.getProductEligibilityDetails()
                .get(0)
                .getDeclineReasons()
                .get(0)
                .getDescription());

    }
}


