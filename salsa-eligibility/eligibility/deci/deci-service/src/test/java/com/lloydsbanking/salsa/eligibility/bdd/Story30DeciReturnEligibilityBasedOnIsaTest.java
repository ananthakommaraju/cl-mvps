

package com.lloydsbanking.salsa.eligibility.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import lb_gbo_sales.DepositArrangement;
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

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story30DeciReturnEligibilityBasedOnIsaTest extends AbstractDeciJBehaveTestBase {
    DetermineElegibileInstructionsRequest request;

    DetermineElegibleInstructionsResponse response;

    DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg dataNotAvailableErrorFaultMsg;

    String candidateInstruction;

    String channel = TestDataHelper.TEST_RETAIL_CHANNEL_ID;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given(" ISA is not opened this Tax year")

    public void givenISAIsNotOpenedThisTaxYear() {
        candidateInstruction = "P_CISA_SAV";
        request = dataHelper.createEligibilityRequest(candidateInstruction, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectGetParentInstructionCall("P_CISA_SAV", "Cash Isa Saver", null, "G_ISA", TestDataHelper.TEST_RETAIL_CHANNEL_ID, "Isa Saver");
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_CISA_SAV", "GR009", "Customer is not eligible for ISA", "GR009", "CR016", "ISA opened this year", "CR016", "GRP", "AGA", null, "IBL", null);

    }

    @Given(" ISA is opened this Tax year")

    public void givenISAIsOpenedThisTaxYear() {
        channel = "BBL";
        candidateInstruction = "P_CISA_SAV";
        Calendar today = Calendar.getInstance();
        request = dataHelper.createEligibilityRequest(candidateInstruction, TestDataHelper.TEST_OCIS_ID, channel, TestDataHelper.TEST_CONTACT_POINT_ID);
        DepositArrangement depositArrangement = dataHelper.createArrangementOfSpecProduct("T", "0386356000");
        depositArrangement.setStartDate(dataHelper.createXMLGregorianCalendar(today.get(Calendar.YEAR), 4, 7));
        request.getCustomerArrangements().add(depositArrangement);

        mockScenarioHelper.expectGetProductArrangementInstructionCall("P_CISA_SAV", "T", "4", channel, "0386356000");
        mockScenarioHelper.expectGetParentInstructionCall("G_ISA", "Cash Isa Saver", null, "", channel, "Isa Saver");
        mockScenarioHelper.expectGetParentInstructionCall("P_CISA_SAV", "Cash Isa Saver", null, "G_ISA", channel, "Isa Saver");
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_CISA_SAV", "GR009", "Customer is not eligible forISA", "GR009", "CR016", "ISA opened this year", "CR016", "GRP", "AGA", null, channel, null);

    }

    @When("UI calls DECI for ISA")

    public void whenUICallsDECIForISA() throws DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, request.getHeader().getChannelId());

        mockControl.go();

        response = eligibilityClient.determineEligibleInstructions(request);

    }

    @Then("DECI evaluates eligibility as true")

    public void thenDECIEvaluatesEligibilityAsTrue() {
        assertTrue(response.getCustomerInstructions().get(0).isEligibilityIndicator());
    }


    @Then("DECI evaluates eligibility to false  and returns  error condition for already existing ISA in the given tax year")
    public void thenDECIEvaluatesEligibilityToFalseAndReturnsErrorConditionForAlreadyExistingISAInTheGivenTaxYear() {
        assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        assertEquals("CR016", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
        assertEquals("ISA opened this year", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonDescription());
    }

}


