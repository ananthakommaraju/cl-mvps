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
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story05DeciFetchesChildInsMnemonicForCandidateInsTest extends AbstractDeciJBehaveTestBase {
    DetermineElegibileInstructionsRequest request;

    DetermineElegibleInstructionsResponse response;

    DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg dataNotAvailableErrorFaultMsg;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @When("the UI calls DECI with a candidate instruction that has $childrenOrNot")
    public void whenTheUICallsDECIWithACandidateInstructionThatHasChildren(String childrenOrNot) throws DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg, DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg {
        mockControl.go();

        String insMnemonic;
        if (childrenOrNot.equals("children")) {
            insMnemonic = "P_TRAV_MON";
        }
        else {
            insMnemonic = "G_TRAV_MON";
        }
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID);

        request = dataHelper.createEligibilityRequest(insMnemonic, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        try {
            response = eligibilityClient.determineEligibleInstructions(request);
        }
        catch (DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg dataNotAvailableErrorfaultMsg) {
            this.dataNotAvailableErrorFaultMsg = dataNotAvailableErrorfaultMsg;
        }
    }

    @Then("DECI returns CustomerInstructions of Child Instructions in response")
    public void thenDECIReturnsCustomerInstructionsOfChildInstructionsInResponse() {
        assertNotNull(response.getCustomerInstructions());
        assertEquals("P_TRAV_MON2", response.getCustomerInstructions().get(0).getInstructionMnemonic());
        assertEquals("P_TRAV_MON", response.getCustomerInstructions().get(1).getInstructionMnemonic());
    }

    @Then("DECI returns CustomerInstructions of Candidate Instruction in response")
    public void thenDECIReturnsCustomerInstructionsOfCandidateInstructionInResponse() {
        assertEquals("P_TRAV_MON", response.getCustomerInstructions().get(0).getInstructionMnemonic());
    }

}
