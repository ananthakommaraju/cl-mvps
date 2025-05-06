package com.lloydsbanking.salsa.opaloans.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydstsb.schema.enterprise.lcsm_arrangementreporting.ErrorInfo;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story01OpaloansRespondsToRequestTest extends AbstractOpaloansJBehaveTestBase {
    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;

    @When("UI calls OPALOANS with valid request")
    public void whenUICallsOPALOANSWithValidRequest() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, ErrorInfo, com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.ErrorInfo {
        request = dataHelper.generateOfferProductArrangementLoansRequest("IBV");

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

        mockControl.go();
        response = opaLoansClient.offerProductArrangement(request);
    }

    @Then("OPALOANS returns valid response")
    public void thenOPALOANSReturnsValidResponse() {
        assertNotNull(response);
    }
}