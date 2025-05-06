package com.lloydsbanking.salsa.opaloans.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydstsb.schema.enterprise.lcsm_arrangementreporting.ErrorInfo;
import lib_sim_bo.businessobjects.RuleCondition;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story04OpaloansTerminatesOrContinueBasedOnLraSwitchAndDuplicateLoanStatusAndDeciResponseTest extends AbstractOpaloansJBehaveTestBase {
    OfferProductArrangementRequest request;

    OfferProductArrangementResponse response;

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
    public void givenCustomerIsExisting() {
        mockScenarioHelper.expectC216CallForUniqueDateOfBirth(request.getHeader(), request.getProductArrangement().getPrimaryInvolvedParty().getExistingSortCode(), request.getProductArrangement().getPrimaryInvolvedParty().getExistingAccountNumber(), 456662112l);
        mockScenarioHelper.expectF336Call(request.getHeader(), "456662112");
        mockScenarioHelper.expectF061Call(request.getHeader(), "456662112", "+00090001232");
        mockScenarioHelper.expectVerdeSwitchCall("LBG", "ON");
    }

    @Given("customer is not existing with additional data indicator $additionalDataIndicator")
    public void givenCustomerIsNotExistingWithAdditionalDataIndicatorTrue(String additionalDataIndicator) {
        mockScenarioHelper.expectC216CallForBirthDateNotMatched(request.getHeader(), request.getProductArrangement().getPrimaryInvolvedParty().getExistingSortCode(), request.getProductArrangement().getPrimaryInvolvedParty().getExistingAccountNumber(), additionalDataIndicator, 456662112l);
    }

    @Given("B231 responded with eligible loan products")
    public void givenB231RespondedWithEligibleLoanProducts() throws ErrorInfo, com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.ErrorInfo {
        mockScenarioHelper.expectB231Call(request.getHeader(), "456662112", "+00090001232", 0);
    }

    @Given("B231 responded without eligible loan products")
    public void givenB231RespondedWithoutEligibleLoanProducts() throws com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.ErrorInfo {
        mockScenarioHelper.expectB231CallWithoutEligibleProducts(request.getHeader(), "456662112", "+00090001232", 0);
    }

    @Given("LRA switch is ON and duplicate saved loan status is $duplicateStatus")
    public void givenLRASwitchIsONAndDuplicateSavedLoanStatusIsFalse(String duplicateStatus) throws com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.ErrorInfo, ErrorInfo {
        mockScenarioHelper.expectLRASwitchCall("VER", "ON");
        mockScenarioHelper.expectB237Call(request.getHeader(), "456662112", "+00090001232", duplicateStatus, 0);
    }

    @Given("LRA switch is $switchStatus")
    public void givenLRASwitchIsOFF(String switchStatus) {
        mockScenarioHelper.expectLRASwitchCall("VER", switchStatus);
    }

    @Given("DECI responded with eligibility status as $eligibilityStatus and reason code as $code and description as $description")
    public void givenDECIRespondedWithEligibilityStatusAsFalseAndReasonCodeAsCR047(String eligibilityStatus, String code, String description) throws Exception {
        mockScenarioHelper.expectPrdDbCalls("P_LOAN_STP", "VER", code);
        mockScenarioHelper.expectEligibilityCall(request.getHeader(), "456662112", eligibilityStatus, code, description);
        if ("CR046".equals(code)) {
            List<Integer> indicators = new ArrayList<>();
            indicators.add(20);
            mockScenarioHelper.expectE141Call(request.getHeader(), indicators, "777146", "03182268", "100");
            mockScenarioHelper.expectB695Call(request.getHeader(), "T2071776000", "37");
        }
    }

    @When("UI calls OPALOANS with valid request")
    public void whenUICallsOPALOANSWithValidRequest() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000306993");
        mockScenarioHelper.expectLookupDataForLegalEntity("VER", "MAN_LEGAL_ENT_CODE");

        mockControl.go();
        response = opaLoansClient.offerProductArrangement(request);
    }

    @Then("OPALOANS returns reason code as $code and description as $description in response")
    public void thenOPALOANSReturnsReasonCodeAs02AndDescriptionAsNoEligibleLoanProductsInResponse(String code, String description) {
        assertNotNull(response.getProductArrangement().getReasonCode());
        assertEquals(code, response.getProductArrangement().getReasonCode().getCode());
        assertEquals(description, response.getProductArrangement().getReasonCode().getDescription());
    }

    @Then("OPALOANS does not clear eligible products and does not return ELIGIBILITY rule condition in response")
    public void thenOPALOANSDoesNotClearEligibleProductsAndDoesNotReturnELIGIBILITYRuleConditionInResponse() {
        assertFalse(response.getProductArrangement().getOfferedProducts().isEmpty());
        for (RuleCondition condition : response.getProductArrangement().getConditions()) {
            assertNotEquals("ELIGIBILITY", condition.getName());
        }
    }

    @Then("OPALOANS does not return ELIGIBILITY rule condition in response")
    public void thenOPALOANSDoesNotReturnELIGIBILITYRuleConditionInResponse() {
        for (RuleCondition condition : response.getProductArrangement().getConditions()) {
            assertNotEquals("ELIGIBILITY", condition.getName());
        }
    }

    @Then("OPALOANS returns rule condition with name $name and result $result in response")
    public void thenOPALOANSReturnsRuleConditionWithNameELIGIBILITYAndResultYInResponse(String name, String result) {
        assertEquals(1, response.getProductArrangement().getConditions().size());
        assertEquals(name, response.getProductArrangement().getConditions().get(0).getName());
        assertEquals(result, response.getProductArrangement().getConditions().get(0).getResult());
    }

    @Then("OPALOANS does not clear eligible and existing products")
    public void thenOPALOANSDoesNotClearEligibleAndExistingProducts() {
        assertFalse(response.getProductArrangement().getExistingProducts().isEmpty());
        assertFalse(response.getProductArrangement().getOfferedProducts().isEmpty());
    }

    @Then("OPALOANS clears eligible and existing products")
    public void thenOPALOANSClearsEligibleAndExistingProducts() {
        assertTrue(response.getProductArrangement().getExistingProducts().isEmpty());
        assertTrue(response.getProductArrangement().getOfferedProducts().isEmpty());
    }

    @Then("OPALOANS updates score identifier in customer score")
    public void thenOPALOANSUpdatesScoreIdentifierInCustomerScore() {
        assertEquals("1", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreIdentifier());
    }

    @Then("OPALOANS does not return user type and party role and internal user identifier in response and terminates")
    public void thenOPALOANSDoesNotReturnUserTypeAndPartyRoleAndInternalUserIdentifierInResponseAndTerminates() {
        assertNull(response.getProductArrangement().getPrimaryInvolvedParty().getUserType());
        assertNull(response.getProductArrangement().getPrimaryInvolvedParty().getPartyRole());
        assertNull(response.getProductArrangement().getPrimaryInvolvedParty().getInternalUserIdentifier());
        assertNull(response.getProductArrangement().getArrangementId());
    }

    @Then("OPALOANS returns user type and party role and internal user identifier in response and continues")
    public void thenOPALOANSReturnsUserTypeAndPartyRoleAndInternalUserIdentifierInResponseAndContinues() {
        assertEquals("1001", response.getProductArrangement().getPrimaryInvolvedParty().getUserType());
        assertEquals("0001", response.getProductArrangement().getPrimaryInvolvedParty().getPartyRole());
        assertEquals("stploan_user", response.getProductArrangement().getPrimaryInvolvedParty().getInternalUserIdentifier());
        assertNotNull(response.getProductArrangement().getArrangementId());
    }
}
