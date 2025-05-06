package com.lloydsbanking.salsa.opaloans.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydstsb.schema.enterprise.lcsm_arrangementreporting.ErrorInfo;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story05OpaloansCreatesCustomerRecordInPamTest extends AbstractOpaloansJBehaveTestBase {
    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;
    int applicationsSize;
    int individualsSize;

    @BeforeScenario
    public void resetResponse() {
        request = null;
        response = null;
        applicationsSize = 0;
        individualsSize = 0;
    }

    @Given("related application id and BFPO address indicator are not present")
    public void givenRelatedApplicationIdAndBFPOAddressIndicatorAreNotPresent() {
        request = dataHelper.generateOfferProductArrangementLoansRequest("IBV");
        request.getProductArrangement().setRelatedApplicationId(null);
        request.getProductArrangement().getPrimaryInvolvedParty().getPostalAddress().clear();
    }

    @Given("affiliate details are present")
    public void givenAffiliateDetailsArePresent() {
        request.getProductArrangement().getAffiliatedetails().addAll(dataHelper.createAffiliateDetailsList());
    }

    @Given("customer is existing")
    public void givenCustomerIsExisting() throws com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.ErrorInfo, ErrorInfo, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockScenarioHelper.expectC216CallForUniqueDateOfBirth(request.getHeader(), request.getProductArrangement().getPrimaryInvolvedParty().getExistingSortCode(), request.getProductArrangement().getPrimaryInvolvedParty().getExistingAccountNumber(), 456662112l);
        mockScenarioHelper.expectF336Call(request.getHeader(), "456662112");
        mockScenarioHelper.expectF061Call(request.getHeader(), "456662112", "+00090001232");
        mockScenarioHelper.expectVerdeSwitchCall("LBG", "ON");
        mockScenarioHelper.expectB231Call(request.getHeader(), "456662112", "+00090001232", 0);
        mockScenarioHelper.expectLRASwitchCall("VER", "ON");
        mockScenarioHelper.expectB237Call(request.getHeader(), "456662112", "+00090001232", "false", 0);
        mockScenarioHelper.expectPrdDbCalls("P_LOAN_STP", "VER", "CR001");
        mockScenarioHelper.expectEligibilityCall(request.getHeader(), "456662112", "true", null, null);
    }

    @Given("customer is not existing with additional data indicator $additionalDataIndicator")
    public void givenCustomerIsNotExistingWithAdditionalDataIndicatorTrue(String additionalDataIndicator) {
        mockScenarioHelper.expectC216CallForBirthDateNotMatched(request.getHeader(), request.getProductArrangement().getPrimaryInvolvedParty().getExistingSortCode(), request.getProductArrangement().getPrimaryInvolvedParty().getExistingAccountNumber(), additionalDataIndicator, 456662112l);
    }

    @When("UI calls OPALOANS with valid request")
    public void whenUICallsOPALOANSWithValidRequest() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, ErrorInfo, com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.ErrorInfo {
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000306993");
        mockScenarioHelper.expectLookupDataForLegalEntity("VER", "MAN_LEGAL_ENT_CODE");

        mockControl.go();
        response = opaLoansClient.offerProductArrangement(request);
    }

    @Then("OPALOANS creates customer record in PAM")
    public void thenOPALOANSCreatesCustomerRecordInPAM() {
        assertEquals(applicationsSize + 1, mockScenarioHelper.expectApplicationsCreated());
        assertEquals(individualsSize + 1, mockScenarioHelper.expectIndividualsCreated());
    }

    @Then("OPALOANS returns arrangement id and affiliate details in response")
    public void thenOPALOANSReturnsArrangementIdAndAffiliateDetailsInResponse() {
        assertNotNull(response);
        assertNotNull(response.getProductArrangement().getArrangementId());
        assertEquals(dataHelper.createAffiliateDetailsList(), response.getProductArrangement().getAffiliatedetails());
    }

    @Then("OPALOANS does not create customer record in PAM and does not return arrangement id and affiliate details in response")
    public void thenOPALOANSDoesNotCreateCustomerRecordInPAMAndDoesNotReturnArrangementIdAndAffiliateDetailsInResponse() {
        assertEquals(applicationsSize, mockScenarioHelper.expectApplicationsCreated());
        assertEquals(individualsSize, mockScenarioHelper.expectIndividualsCreated());
        assertNull(response.getProductArrangement().getArrangementId());
        assertTrue(response.getProductArrangement().getAffiliatedetails().isEmpty());
    }
}
