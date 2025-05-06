package com.lloydsbanking.salsa.opaloans.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydstsb.schema.enterprise.lcsm_arrangementreporting.ErrorInfo;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story06OpaloansRetrievesProductArrangementDetailsTest extends AbstractOpaloansJBehaveTestBase {
    OfferProductArrangementRequest request;

    OfferProductArrangementResponse response;

    @Given("application exists in PAM DB")
    public void givenApplicationExistsInPAMDB() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, ErrorInfo, com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.ErrorInfo {
        request = dataHelper.generateOfferProductArrangementLoansRequest("IBV");
        request.getProductArrangement().getAffiliatedetails().addAll(dataHelper.createAffiliateDetailsList());

        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000306993");
        mockScenarioHelper.expectLookupDataForLegalEntity("VER", "MAN_LEGAL_ENT_CODE");
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

    @Given("Gender is undefined")
    public void givenGenderIsUndefined() {
        request.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().setGender("U");
    }

    @When("UI calls OPALOANS with valid request")
    public void whenUICallsOPALOANSWithValidRequest() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg {
        mockControl.go();
        response = opaLoansClient.offerProductArrangement(request);
    }

    @Then("OPALOANS returns affiliate details in response")
    public void thenOPALOANSReturnsAffiliateDetailsInResponse() {
        assertNotNull(response);
        assertNotNull(response.getProductArrangement());
        assertEquals(dataHelper.createAffiliateDetailsList(), response.getProductArrangement().getAffiliatedetails());
    }

    @Then("OPALOANS updates Gender as $gender in response")
    public void thenOPALOANSUpdatesGenderAs000InResponse(String gender) {
        assertNotNull(response.getProductArrangement().getPrimaryInvolvedParty());
        assertNotNull(response.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy());
        assertEquals(gender, response.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().getGender());
    }
}