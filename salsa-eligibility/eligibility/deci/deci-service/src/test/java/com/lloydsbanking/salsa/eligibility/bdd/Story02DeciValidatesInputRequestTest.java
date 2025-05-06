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
public class Story02DeciValidatesInputRequestTest extends AbstractDeciJBehaveTestBase {
    DetermineElegibileInstructionsRequest request;

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
    public void givenDECIIsRunning() {

    }

    @When("the UI calls DECI with valid customer arrangement")
    public void whenTheUICallsDECIWithValidCustomerArrangement() {
        request = dataHelper.createEligibilityRequest("P_TRAV_MON", TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
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

    @Then("DECI returns customerInstructions in response")
    public void thenDECIReturnsCustomerInstructionsInResponse() {
        assertNotNull(response.getCustomerInstructions());
    }

    @When("the UI calls DECI with no customer arrangement")
    public void whenTheUICallsDECIWithNoCustomerArrangement() {
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID);
        request = new DetermineElegibileInstructionsRequest();
        request.setHeader(new TestDataHelper().createEligibilityRequestHeader("IBL", TestDataHelper.TEST_INTERACTION, TestDataHelper.TEST_OCIS_ID, "12345", TestDataHelper.TEST_CONTACT_POINT_ID));
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

    @Then("DECI returns error for missing customer arrangement")
    public void thenDECIReturnsErrorForMissingCustomerArrangement() {
        assertNull(response);
        assertNotNull(instructionsInternalServiceErrorMsg);
        assertEquals("No product arrangements supplied", instructionsInternalServiceErrorMsg.getFaultInfo().getDescription());

    }

    @When("the UI calls DECI with valid Account Type in customer arrangements")
    public void whenTheUICallsDECIWithValidAccountTypeInCustomerArrangements() {
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

    @When("the UI calls DECI with invalid Account Type in customer arrangements")
    public void whenTheUICallsDECIWithInvalidAccountTypeInCustomerArrangements() {
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID);
        request = dataHelper.createEligibilityRequest("P_TRAV_MON", TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        request.getCustomerArrangements().get(0).setAccountType(null);
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

    @Then("DECI returns error for invalid Account Type in response")
    public void thenDECIReturnsErrorForInvalidAccountTypeInResponse() {
        assertNull(response);
        assertNotNull(instructionsExternalBusinessErrorMsg);
        assertEquals("1234", instructionsExternalBusinessErrorMsg.getFaultInfo().getReasonCode());
        assertEquals("Account Type passed is NULL", instructionsExternalBusinessErrorMsg.getFaultInfo().getDescription());

    }
}



