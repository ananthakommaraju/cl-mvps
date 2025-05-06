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
public class Story03OpaloansDeterminesEligibleProductsOfCustomerTest extends AbstractOpaloansJBehaveTestBase {
    OfferProductArrangementRequest request;

    OfferProductArrangementResponse response;

    OfferProductArrangementExternalBusinessErrorMsg externalBusinessErrorMsg;

    @BeforeScenario
    public void resetResponse() {
        request = null;
        response = null;
    }

    @Given("related application id and BFPO address indicator are not present")
    public void givenRelatedApplicationIdAndBFPOAddressIndicatorAreNotPresent() {
        request = dataHelper.generateOfferProductArrangementLoansRequest("IBV");
        request.getProductArrangement().setRelatedApplicationId(null);
    }

    @Given("customer is existing")
    public void givenCustomerIsExisting() throws com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.ErrorInfo, ErrorInfo, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockScenarioHelper.expectC216CallForUniqueDateOfBirth(request.getHeader(), request.getProductArrangement().getPrimaryInvolvedParty().getExistingSortCode(), request.getProductArrangement().getPrimaryInvolvedParty().getExistingAccountNumber(), 456662112l);
        mockScenarioHelper.expectF336Call(request.getHeader(), "456662112");
        mockScenarioHelper.expectF061Call(request.getHeader(), "456662112", "+00090001232");
        mockScenarioHelper.expectVerdeSwitchCall("LBG", "ON");
        mockScenarioHelper.expectLRASwitchCall("VER", "ON");
        mockScenarioHelper.expectB237Call(request.getHeader(), "456662112", "+00090001232", "false", 0);
        mockScenarioHelper.expectPrdDbCalls("P_LOAN_STP", "VER", "CR001");
        mockScenarioHelper.expectEligibilityCall(request.getHeader(), "456662112", "true", null, null);
    }

    @Given("B231 responded with error code $errorCode")
    public void givenB231RespondedWithErrorCode0(int errorCode) throws ErrorInfo, com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.ErrorInfo {
        mockScenarioHelper.expectB231Call(request.getHeader(), "456662112", "+00090001232", errorCode);
    }

    @When("UI calls OPALOANS with valid request")
    public void whenUICallsOPALOANSWithValidRequest() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg {
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000306993");
        mockScenarioHelper.expectLookupDataForLegalEntity("VER", "MAN_LEGAL_ENT_CODE");
        mockControl.go();
        try {
            response = opaLoansClient.offerProductArrangement(request);
        } catch (OfferProductArrangementExternalBusinessErrorMsg errorMsg) {
            externalBusinessErrorMsg = errorMsg;
        }
    }

    @Then("OPALOANS responds with customer number and customer score with score result as $scoreResult")
    public void thenOPALOANSRespondsWithCustomerNumberAndCustomerScoreWithScoreResultAsACCEPT(String scoreResult) {
        assertEquals("77714600421506", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerNumber());
        assertEquals(1, response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().size());
        assertEquals("CREDIT_SCORE", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
        assertEquals("STPL5819141218084429", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreIdentifier());
        assertEquals(scoreResult, response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult());
    }

    @Then("OPALOANS returns the eligible loan products of customer in response")
    public void thenOPALOANSReturnsTheEligibleLoanProductsOfCustomerInResponse() {
        assertEquals(1, response.getProductArrangement().getOfferedProducts().size());
        assertEquals("TSB PERSONAL LOAN", response.getProductArrangement().getOfferedProducts().get(0).getProductName());
        assertEquals("343", response.getProductArrangement().getOfferedProducts().get(0).getProductIdentifier());
        assertEquals(18, response.getProductArrangement().getOfferedProducts().get(0).getProductoptions().size());
    }

    @Then("OPALOANS responds with external business error")
    public void thenOPALOANSRespondsWithExternalBusinessError() {
        assertNull(response);
        assertNotNull(externalBusinessErrorMsg);
    }
}