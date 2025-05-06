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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story06DeciFetchesCompositeInstructionForChildCandidateInstructionTest extends AbstractDeciJBehaveTestBase {
    DetermineElegibileInstructionsRequest request;

    DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg dataNotAvailableErrorfaultMsg;

    DetermineElegibleInstructionsResponse response;

    String instructionMnemonic;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;

    }

    @Given("Instruction rules are available for candidate Instructions")
    public void givenInstructionRulesAreAvailableForCandidateInstructions() throws DetermineEligibleInstructionsInternalServiceErrorMsg {
        instructionMnemonic = "P_TRAV_MON";

    }

    @Given("Instructions rules are unavailable for candidate Instruction")
    public void givenInstructionsRulesAreUnavailableForCandidateInstruction() {
        instructionMnemonic = "P_TRAV_MONEY";
        //would be implmneted once eligibility criteria is done

    }

    @When("the UI calls DECI with valid request")
    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID);
        request = dataHelper.createEligibilityRequest(instructionMnemonic, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        try {

            response = eligibilityClient.determineEligibleInstructions(request);

        }
        catch (DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg dataNotAvailableErrorfault1Msg) {

            dataNotAvailableErrorfaultMsg = dataNotAvailableErrorfault1Msg;
        }
    }

    @Then("DECI returns CustomerInstructions of candidate Instructions in response")
    public void thenDECIReturnsCustomerInstructionsOfCandidateInstructionsInResponse() {

        assertNotNull(response.getCustomerInstructions());
        assertNull(dataNotAvailableErrorfaultMsg);
    }

    @Then("DECI fetches rules for parent instructions and returns CustomerInstructions of candidate Instructions")
    public void thenDECIFetchesRulesForParentInstructionsAndReturnsCustomerInstructionsOfCandidateInstructions() {
        assertNotNull(response.getCustomerInstructions());
    }

}
