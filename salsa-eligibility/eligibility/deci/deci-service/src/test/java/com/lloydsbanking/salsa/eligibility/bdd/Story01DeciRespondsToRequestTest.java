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
public class Story01DeciRespondsToRequestTest extends AbstractDeciJBehaveTestBase {
    DetermineElegibileInstructionsRequest request;

    DetermineEligibleInstructionsInternalServiceErrorMsg internalServiceErrorMsg;

    DetermineEligibleInstructionsExternalServiceErrorMsg externalServiceErrorMsg;

    DetermineEligibleInstructionsResourceNotAvailableErrorMsg instructionsResourceNotAvailableErrorMsg;

    DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg instructionsDataNotAvailableErrorfault1Msg;

    DetermineEligibleInstructionsInternalServiceErrorMsg instructionsInternalServiceErrorMsg;

    DetermineEligibleInstructionsExternalBusinessErrorMsg instructionsExternalBusinessErrorMsg;

    DetermineElegibleInstructionsResponse response;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("DECI is running")
    public void givenDECIIsRunning() throws DetermineEligibleInstructionsInternalServiceErrorMsg {

    }

    @Given("invalid rule type is returned for customer instruction")
    public void givenInvalidRuleTypeIsReturnedForCustomerInstruction() {
        mockScenarioHelper.expectGetParentInstructionCall("P_INVALID", "Savings", Integer.valueOf(4), "G_INVALID", "IBL", "Savings");
        mockScenarioHelper.expectCompositeInstructionConditionCall("P_INVALID", "GR009", "Customer is not eligible for savings product", "GR009", "CR006", "Cannot have product already have", "CR006", "GRP", "XYZ", null, "IBL", null);
    }

    @When("the UI calls DECI with valid request")
    public void whenTheUICallsDECIWithValidRequest() {
        DetermineElegibileInstructionsRequest request;
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID);
        request = dataHelper.createEligibilityRequest("P_TRAV_MON", TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockControl.go();

        try {
            response = eligibilityClient.determineEligibleInstructions(request);
        }
        catch (DetermineEligibleInstructionsExternalServiceErrorMsg externalServiceErrorMsg) {
            this.externalServiceErrorMsg = externalServiceErrorMsg;
        }
        catch (DetermineEligibleInstructionsResourceNotAvailableErrorMsg instructionsResourceNotAvailableErrorMsg) {
            this.instructionsResourceNotAvailableErrorMsg = instructionsResourceNotAvailableErrorMsg;
        }
        catch (DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg instructionsDataNotAvailableErrorfault1Msg) {
            this.instructionsDataNotAvailableErrorfault1Msg = instructionsDataNotAvailableErrorfault1Msg;
        }
        catch (DetermineEligibleInstructionsInternalServiceErrorMsg instructionsInternalServiceErrorMsg) {
            this.instructionsInternalServiceErrorMsg = instructionsInternalServiceErrorMsg;
        }
        catch (DetermineEligibleInstructionsExternalBusinessErrorMsg instructionsExternalBusinessErrorMsg) {
            this.instructionsExternalBusinessErrorMsg = instructionsExternalBusinessErrorMsg;
        }

    }

    @Then("DECI responds")
    public void thenDECIResponds() {
        assertNotNull(response.getCustomerInstructions());

    }

    @When("the UI calls DECI with invalid requests")
    public void whenTheUICallsDECIWithInvalidRequests() {

        DetermineElegibileInstructionsRequest request = new DetermineElegibileInstructionsRequest();
        request.setHeader(new TestDataHelper().createEligibilityRequestHeader("IBL", TestDataHelper.TEST_INTERACTION, TestDataHelper.TEST_OCIS_ID, "12345", TestDataHelper.TEST_CONTACT_POINT_ID));
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID);
        mockControl.go();

        try {
            response = eligibilityClient.determineEligibleInstructions(request);
        }
        catch (DetermineEligibleInstructionsExternalServiceErrorMsg externalServiceErrorMsg) {
            this.externalServiceErrorMsg = externalServiceErrorMsg;

        }
        catch (DetermineEligibleInstructionsResourceNotAvailableErrorMsg instructionsResourceNotAvailableErrorMsg) {
            this.instructionsResourceNotAvailableErrorMsg = instructionsResourceNotAvailableErrorMsg;
        }
        catch (DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg instructionsDataNotAvailableErrorfault1Msg) {
            this.instructionsDataNotAvailableErrorfault1Msg = instructionsDataNotAvailableErrorfault1Msg;
        }
        catch (DetermineEligibleInstructionsInternalServiceErrorMsg instructionsInternalServiceErrorMsg) {
            this.instructionsInternalServiceErrorMsg = instructionsInternalServiceErrorMsg;
        }
        catch (DetermineEligibleInstructionsExternalBusinessErrorMsg instructionsExternalBusinessErrorMsg) {
            this.instructionsExternalBusinessErrorMsg = instructionsExternalBusinessErrorMsg;
        }
    }


    @When("the UI calls DECI")
    public void whenTheUICallsDECI() {
        DetermineElegibileInstructionsRequest request = new DetermineElegibileInstructionsRequest();
        request = dataHelper.createEligibilityRequest("P_INVALID", TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID);
        mockControl.go();

        try {
            response = eligibilityClient.determineEligibleInstructions(request);
        }
        catch (DetermineEligibleInstructionsExternalServiceErrorMsg externalServiceErrorMsg) {
            this.externalServiceErrorMsg = externalServiceErrorMsg;

        }
        catch (DetermineEligibleInstructionsResourceNotAvailableErrorMsg instructionsResourceNotAvailableErrorMsg) {
            this.instructionsResourceNotAvailableErrorMsg = instructionsResourceNotAvailableErrorMsg;
        }
        catch (DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg instructionsDataNotAvailableErrorfault1Msg) {
            this.instructionsDataNotAvailableErrorfault1Msg = instructionsDataNotAvailableErrorfault1Msg;
        }
        catch (DetermineEligibleInstructionsInternalServiceErrorMsg instructionsInternalServiceErrorMsg) {
            this.instructionsInternalServiceErrorMsg = instructionsInternalServiceErrorMsg;
        }
        catch (DetermineEligibleInstructionsExternalBusinessErrorMsg instructionsExternalBusinessErrorMsg) {
            this.instructionsExternalBusinessErrorMsg = instructionsExternalBusinessErrorMsg;
        }
        catch (Exception e) {
            //wps is throwing internalServiceError without initializing it
            //[6/21/15 17:19:32:160 GMT] 0000004e ExceptionUtil E   CNTR0020E: EJB threw an unexpected (non-declared) exception during invocation of method "processMessage" on bean "BeanId(PM_DetermineEligibleCustomerInstructions#PM_DetermineEligibleCustomerInstructionsEJB.jar#component.A_DetermineEligibilityCriteria, null)". Exception data: com.ibm.bpe.api.StandardFaultException: CWWBE0071E: A two-way request for port type 'IA_DetermineEligibilityCriteria' and operation 'determineEligibilityCriteria' was accepted by activity 'Receive'. The process ended before a corresponding reply activity was executed.
            //com.ibm.bpe.api.StandardFaultException: CWWBE0068E: During process execution, activity 'ThrowInternalServiceError' tried to access an uninitialized part in variable 'internalServiceError'.
            //if we don't catch this exception this will fail at WPS as they are throwing generic exception
        }
    }


    @Then("DECI responds with error")
    public void thenDECIRespondsWithError() {
        assertNull(response);
        assertNotNull(instructionsInternalServiceErrorMsg);
        assertEquals("No product arrangements supplied", instructionsInternalServiceErrorMsg.getFaultInfo().getDescription());

    }

    @Then("DECI responds with internal service error")
    public void thenDECIRespondsWithInternalServiceError() {
        assertNull(response);
    }
}















