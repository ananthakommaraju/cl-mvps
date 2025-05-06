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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story08DeciReturnEligibilityStatusForChildMnemonicsTest extends AbstractDeciJBehaveTestBase {
    DetermineElegibileInstructionsRequest request;

    DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg dataNotAvailableErrorfaultMsg;

    TestDataHelper dataHelper = new TestDataHelper();

    DetermineElegibleInstructionsResponse response;

    String insMnemonic;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;

    }

    @Given("Candidate instruction has 3 child instruction mnemonics")
    public void givenCandidateInstructionHas3ChildInstructionMnemonics() {


        insMnemonic = "G_TRAV_MON";
        mockScenarioHelper.expectGetProductArrangementInstructionCall("P_CLASSIC", "T", "4", TestDataHelper.TEST_RETAIL_CHANNEL_ID, "0071776000");
        mockScenarioHelper.expectGetParentInstructionCall("P_CLASSIC", "Classic Account", null, "G_AVA", TestDataHelper.TEST_RETAIL_CHANNEL_ID, "Added Value Account");
        mockScenarioHelper.expectGetParentInstructionCallWithEmptyResult();

    }


    @Given("Candidate instruction is a child instruction")
    public void givenCandidateInstructionIsAChildInstruction() {


        insMnemonic = "P_TRAV_MON";
        mockScenarioHelper.expectGetProductArrangementInstructionCall("P_CLASSIC", "T", "4", TestDataHelper.TEST_RETAIL_CHANNEL_ID, "0071776000");
        mockScenarioHelper.expectGetParentInstructionCall("P_CLASSIC", "Classic Account", null, "G_AVA", TestDataHelper.TEST_RETAIL_CHANNEL_ID, "Added Value Account");
        mockScenarioHelper.expectGetParentInstructionCallWithEmptyResult();

    }

    @When("deci is called")
    public void whenDeciIsCalled() throws DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID);

        request = dataHelper.createEligibilityRequest(insMnemonic, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        request.getCandidateInstructions().add("P_TRAV_MONEY");
//        request.getCandidateInstructions().add("P_TRAV_MONEY0");
        mockControl.go();
        try {

            response = eligibilityClient.determineEligibleInstructions(request);
        }
        catch (DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg dataNotAvailableErrorfaultMsg) {
            this.dataNotAvailableErrorfaultMsg = dataNotAvailableErrorfaultMsg;

        }

    }


    @Then("deci returns eligibility for all 3 child mnemonics")
    public void thenDeciReturnsEligibilityForAll3ChildMnemonics() {

        assertNotNull(response.getCustomerInstructions());
        assertNotNull("P_TRAV_MON", response.getCustomerInstructions().get(0).getInstructionMnemonic());
        assertNotNull("P_TRAV_MON2", response.getCustomerInstructions().get(1).getInstructionMnemonic());
        assertNotNull("P_TRAV_MON3", response.getCustomerInstructions().get(2).getInstructionMnemonic());

    }


    @Then("deci returns eligibility for candidate instruction mnemonic")
    public void thenDeciReturnsEligibilityForCandidateInstructionMnemonic() {

        assertEquals("P_TRAV_MON", response.getCustomerInstructions().get(0).getInstructionMnemonic());

    }
}




