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
import static org.junit.Assert.assertNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story03DeciFetchesParentMnemonicForInstructionsTest extends AbstractDeciJBehaveTestBase {
    DetermineElegibileInstructionsRequest request;

    DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg dataNotAvailableErrorfaultMsg;

    DetermineElegibleInstructionsResponse response;

    String channel;

    @BeforeScenario
    public void resetResponse() {
        response = null;

    }

    @Given("Parent Instructions are available for Instructions")
    public void givenParentInstructionsAreAvailableForInstructions() {
        channel = TestDataHelper.TEST_RETAIL_CHANNEL_ID;

    }

    @Given("Parent Instructions are unavailable for Instructions")
    public void givenParentInstructionsAreUnavailableForInstructions() {
        channel = "STL";
        mockScenarioHelper.clearUp();
        mockScenarioHelper.expectGetProductArrangementInstructionCall("P_CLASSIC", "T", "4", channel, "0071776000");

    }

    @When("the UI calls DECI with valid request")
    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, channel);
        request = dataHelper.createEligibilityRequest("P_TRAV_MON", TestDataHelper.TEST_OCIS_ID, channel, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockControl.go();
        try {

            response = eligibilityClient.determineEligibleInstructions(request);
        }
        catch (DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg dataNotAvailableErrorfault1Msg) {
            dataNotAvailableErrorfaultMsg = dataNotAvailableErrorfault1Msg;
        }
    }

    @Then("DECI returns customerInstructions in response")
    public void thenDECIReturnsCustomerInstructionsInResponse() {
        assertNotNull(response.getCustomerInstructions());
    }

    @Then("DECI returns errorcode for unavailable parent instruction")
    public void thenDECIReturnsErrorcodeForUnavailableParentInstruction() {
        assertNull(response);
        assertEquals("No matching records found, error code: ", dataNotAvailableErrorfaultMsg.getFaultInfo().getDescription());
        assertEquals("INSTRUCTION_HIERARCHY_VW", dataNotAvailableErrorfaultMsg.getFaultInfo().getEntity());
        assertEquals("ins_mnemonic", dataNotAvailableErrorfaultMsg.getFaultInfo().getField());
    }

}
