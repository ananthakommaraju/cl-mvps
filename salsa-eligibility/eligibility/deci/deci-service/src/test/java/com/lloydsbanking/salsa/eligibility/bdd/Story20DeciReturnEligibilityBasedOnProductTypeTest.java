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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story20DeciReturnEligibilityBasedOnProductTypeTest extends AbstractDeciJBehaveTestBase {
    DetermineElegibileInstructionsRequest request;

    DetermineElegibleInstructionsResponse response;

    DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg dataNotAvailableErrorFaultMsg;

    String channel;

    String candidateInstruction;


    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;

    }

    @Given("Customer has applied for cash ISA product")
    public void givenCustomerHasAppliedForCashISAProduct() {
        candidateInstruction = "P_CISA_SAV";
    }

    @Given("he does not have any existing cash ISA product")
    public void givenHeDoesNotHaveAnyExistingCashISAProduct() {
        channel = TestDataHelper.TEST_RETAIL_CHANNEL_ID;
        request = dataHelper.createEligibilityRequest(candidateInstruction, TestDataHelper.TEST_OCIS_ID, channel, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectGetParentInstructionCall("P_CISA_SAV", "Cash Isa Saver", null, "G_ISA", channel, "Isa Saver");
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_CISA_SAV", "GR009", "Customer is not eligible for savings product", "GR009", "CR006", "Cannot have product already have", "CR006", "GRP", "AGA", null, "IBL", null);

    }

    @Given("he has an existing cash ISA product")
    public void givenHeHasAnExistingCashISAProduct() {
        channel = "STL";
        request = dataHelper.createEligibilityRequest(candidateInstruction, TestDataHelper.TEST_OCIS_ID, channel, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectGetParentInstructionCall("P_CISA_SAV", "Cash Isa Saver", null, "G_ISA", channel, "Isa Saver");
        mockScenarioHelper.expectGetProductArrangementInstructionCall("P_CISA_SAV", "T", "4", channel, "0386306000");
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_CISA_SAV", "GR009", "Customer is not eligible for savings product", "GR009", "CR006", "Cannot have product already have", "CR006", "GRP", "AGA", null, channel, null);
        request.getCustomerArrangements().add(dataHelper.createArrangementOfSpecProduct("T", "0386306000"));

    }

    @When("the UI calls DECI with valid request")
    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {

        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, channel);

        mockControl.go();
        response = eligibilityClient.determineEligibleInstructions(request);
    }

    @Then("deci evaluates eligibility as true")
    public void thenDeciEvaluatesEligibilityAsTrue() {
        assertTrue(response.getCustomerInstructions().get(0).isEligibilityIndicator());

    }

    @Then("deci evaluates eligibility as false and returns decline reason for applied cash ISA product")
    public void thenDeciEvaluatesEligibilityAsFalseAndReturnsDeclineReasonForAppliedCashISAProduct() {

        assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        assertEquals("CR006", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
        assertEquals("Customer cannot apply for a product of the same type as they already have.", response.getCustomerInstructions()
                .get(0)
                .getDeclineReasons()
                .get(0)
                .getReasonDescription());
    }

}
