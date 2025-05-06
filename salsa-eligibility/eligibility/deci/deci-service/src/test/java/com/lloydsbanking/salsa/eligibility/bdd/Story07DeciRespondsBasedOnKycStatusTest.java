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
import lb_gbo_sales.messages.RequestHeader;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story07DeciRespondsBasedOnKycStatusTest extends AbstractDeciJBehaveTestBase {

    DetermineElegibileInstructionsRequest request;

    DetermineElegibleInstructionsResponse response;

    DetermineEligibleInstructionsExternalBusinessErrorMsg externalBusinessErrorMsg;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;

    }

    @Given("KYC status is Full for the customer")
    public void givenKYCStatusIsFullForTheCustomer() throws DetermineEligibleInstructionsInternalServiceErrorMsg {
        RequestHeader header = dataHelper.createEligibilityRequestHeader("IBH", TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectF075Call(header, TestDataHelper.KYC_STATUS_FULL);
    }

    @Given("KYC status is partial for the customer")
    public void givenKYCStatusIsPartialForTheCustomer() throws DetermineEligibleInstructionsInternalServiceErrorMsg {
        RequestHeader header = dataHelper.createEligibilityRequestHeader("IBH", TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectF075Call(header, TestDataHelper.KYC_STATUS_PARTIAL);
    }

    @Given("KYC status is not available for the customer")
    public void givenKYCStatusIsNotAvailableForTheCustomer() throws DetermineEligibleInstructionsInternalServiceErrorMsg {
        RequestHeader header = dataHelper.createEligibilityRequestHeader("IBH", TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        mockScenarioHelper.expectF075CallWithErrorCode(header, TestDataHelper.ERROR_CODE_163004);
    }

    @When("the UI calls DECI with valid request")
    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        mockScenarioHelper.expectGetChannelFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, "IBH");

        request = dataHelper.createEligibilityRequest("P_EASY_SVR", TestDataHelper.TEST_OCIS_ID, "IBH", TestDataHelper.TEST_CONTACT_POINT_ID);

        mockControl.go();

        try {
            response = eligibilityClient.determineEligibleInstructions(request);
        }
        catch (DetermineEligibleInstructionsExternalBusinessErrorMsg externalBusinessErrorMsg) {
            this.externalBusinessErrorMsg = externalBusinessErrorMsg;
        }
    }

    @Then("DECI evaluates eligibility to true")
    public void thenDECIEvaluatesEligibilityToTrue() {
        assertTrue(response.getCustomerInstructions().get(0).isEligibilityIndicator());
    }


    @Then("DECI evaluates eligibility to false and returns error condition for partial KYC status")
    public void thenDECIEvaluatesErrorConditionToFalseAndReturnsErrorConditionForPartialKYCStatus() {
        assertFalse(response.getCustomerInstructions().get(0).isEligibilityIndicator());
        assertEquals("CR026", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonCode());
        assertEquals("KnowYourCustomer check not complete", response.getCustomerInstructions().get(0).getDeclineReasons().get(0).getReasonDescription());
    }


    @Then("DECI  returns error condition for unavailable KYC status")
    public void thenDECIReturnsErrorConditionForUnavailableKYCStatus() {
        assertEquals("00720001", externalBusinessErrorMsg.getFaultInfo().getReasonCode());
        assertEquals("KYC status unavailable", externalBusinessErrorMsg.getFaultInfo().getDescription());
    }

}
