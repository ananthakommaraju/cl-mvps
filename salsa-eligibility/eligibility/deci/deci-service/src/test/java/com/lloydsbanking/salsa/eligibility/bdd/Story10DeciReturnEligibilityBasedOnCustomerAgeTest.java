package com.lloydsbanking.salsa.eligibility.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalBusinessErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsResourceNotAvailableErrorMsg;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import lb_gbo_sales.messages.DetermineElegibleInstructionsResponse;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story10DeciReturnEligibilityBasedOnCustomerAgeTest extends AbstractDeciJBehaveTestBase {
    DetermineElegibileInstructionsRequest request;

    DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg dataNotAvailableErrorfaultMsg;

    DetermineElegibleInstructionsResponse response;

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
    public void givenCustomerIsNotOlderThan74YearsOfAgeAndChannelIs(String channelCode) throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        channelId = channelCode;
        year = currentYear - 60;
        month = 01;
        day = 01;
    }

    @Given("customer is not younger than 18 years of age and channel is $channelCode")
    public void givenCustomerIsNotYoungerThan18YearsOfAgeAndChannelIs(String channelCode) throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        channelId = channelCode;
        year = currentYear - 20;
        month = 01;
        day = 01;
    }


    @Given("he is applying for personal loans")
    public void givenHeIsApplyingForPersonalLoans() {
        candidateInstruction = "P_LOAN";
    }

    @Given("customer is older than 74 years of age and channel is $channelCode")
    public void givenCustomerIsOlderThan74YearsOfAgeAndChannelIs(String channelCode) throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        channelId = channelCode;
        year = currentYear - 80;
        month = 01;
        day = 01;
    }

    @Given("customer is younger than 18 years of age and channel is $channelCode")
    public void givenCustomerIsYoungerThan18YearsOfAgeAndChannelIs(String channelCode) throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        channelId = channelCode;
        year = currentYear - 10;
        month = 01;
        day = 01;
    }

    @Given("customer is not younger than 16 years of age and channel is $channelCode")
    public void givenCustomerIsNotYoungerThan16YearsOfAgeAndChannelIs(String channelCode) throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        channelId = channelCode;
        year = currentYear - 20;
        month = 01;
        day = 01;
    }

    @Given("customer is younger than 16 years of age and channel is $channelCode")
    public void givenCustomerIsYoungerThan16YearsOfAgeAndChannelIs(String channelCode) throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        channelId = channelCode;
        year = currentYear - 5;
        month = 01;
        day = 01;
    }

    @Given("he is applying for cash isa")
    public void givenHeIsApplyingForCashIsa() {
        candidateInstruction = "P_CASH_ISA";
    }


    @When("the UI calls DECI with valid request")
    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, channelId);
        request = dataHelper.createEligibilityRequestForLoans(candidateInstruction, TestDataHelper.TEST_OCIS_ID, channelId, TestDataHelper.TEST_CONTACT_POINT_ID, year, month, day);
        mockControlService.go();
        try {
            response = eligibilityClient.determineEligibleInstructions(request);
        }
        catch (DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg dataNotAvailableErrorfault1Msg) {
            dataNotAvailableErrorfaultMsg = dataNotAvailableErrorfault1Msg;
        }
    }


    @Then("DECI evaluates eligibility to true")
    public void thenDECIEvaluatesEligibilityToTrue() {
        assertTrue(response.getCustomerInstructions().get(0).isEligibilityIndicator());
    }

    @Then("DECI evaluates eligibility to false and returns error condition for age older than threshold")
    public void thenDECIEvaluatesEligibilityToFalseAndReturnsErrorConditionForAgeOlderThanThreshold() {
        assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        assertEquals("CR001", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
        assertEquals("Customer's age is null or Customer cannot be older than 74 years.", response.getCustomerInstructions()
                .get(0)
                .getDeclineReasons()
                .get(0)
                .getReasonDescription());

    }

    @Then("DECI evaluates eligibility to false for personal loans and returns error condition for age younger than threshold")
    public void thenDECIEvaluatesEligibilityToFalseForPersonalLoansAndReturnsErrorConditionForAgeYoungerThanThreshold() {
        assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        assertEquals("CR002", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
        assertEquals("Customer's age is null or  Customer cannot be younger than 18 years.", response.getCustomerInstructions()
                .get(0)
                .getDeclineReasons()
                .get(0)
                .getReasonDescription());

    }

    @Then("DECI evaluates eligibility to false for cash isa and returns error condition for age younger than threshold")
    public void thenDECIEvaluatesEligibilityToFalseForCashIsaAndReturnsErrorConditionForAgeYoungerThanThreshold() {
        assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        assertEquals("CR003", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
        assertEquals("Customer's age is null or  Customer cannot be younger than 16 years.", response.getCustomerInstructions()
                .get(0)
                .getDeclineReasons()
                .get(0)
                .getReasonDescription());

    }

}
