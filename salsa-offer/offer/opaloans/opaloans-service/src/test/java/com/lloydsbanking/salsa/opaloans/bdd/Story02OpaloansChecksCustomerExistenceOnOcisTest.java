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
public class Story02OpaloansChecksCustomerExistenceOnOcisTest extends AbstractOpaloansJBehaveTestBase {
    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;
    OfferProductArrangementExternalBusinessErrorMsg externalBusinessErrorMsg;

    @BeforeScenario
    public void resetData() {
        request = null;
        response = null;
    }

    @Given("related application id and BFPO address indicator are not present")
    public void givenRelatedApplicationIdAndBFPOAddressIndicatorAreNotPresent() {
        request = dataHelper.generateOfferProductArrangementLoansRequest("IBV");
        request.getProductArrangement().setRelatedApplicationId(null);
    }

    @Given("OCIS ID is not present in request")
    public void givenOCISIDIsNotPresentInRequest() {
        request.getProductArrangement().getPrimaryInvolvedParty().setCustomerIdentifier(null);
    }

    @Given("customer is found with birth date matched uniquely with birth date from C216")
    public void givenCustomerIsFoundOnOCISWithBirthDateMatchedUniquelyWithBirthDateFromC216() {
        mockScenarioHelper.expectC216CallForUniqueDateOfBirth(request.getHeader(), request.getProductArrangement().getPrimaryInvolvedParty().getExistingSortCode(), request.getProductArrangement().getPrimaryInvolvedParty().getExistingAccountNumber(), 456662112l);
    }

    @Given("customer is found with birth date not matched uniquely with birth date from C216 and customer name in request matched with name returned by C216")
    public void givenCustomerIsFoundWithBirthDateNotMatchedUniquelyWithBirthDateFromC216AndCustomerNameInRequestMatchedWithNameReturnedByC216() {
        request.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().get(0).setFirstName("Ariyana");
        request.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().get(0).setLastName("Lockheart");
        mockScenarioHelper.expectC216CallForBirthDateNotMatched(request.getHeader(), request.getProductArrangement().getPrimaryInvolvedParty().getExistingSortCode(), request.getProductArrangement().getPrimaryInvolvedParty().getExistingAccountNumber(), "true", 456662112l);
    }

    @Given("customer is found with birth date not matched uniquely with birth date from C216 and customer name in request is not matched with name returned by C216")
    public void givenCustomerIsFoundWithBirthDateNotMatchedUniquelyWithBirthDateFromC216AndCustomerNameInRequestIsNotMatchedWithNameReturnedByC216() {
        request.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().get(0).setFirstName("Daniel");
        request.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().get(0).setLastName("Lockheart");
        mockScenarioHelper.expectC216CallForBirthDateNotMatched(request.getHeader(), request.getProductArrangement().getPrimaryInvolvedParty().getExistingSortCode(), request.getProductArrangement().getPrimaryInvolvedParty().getExistingAccountNumber(), "true", 456662112l);
    }

    @Given("customer is found with birth date not matched uniquely with birth date from C216 and customer name is not present in request")
    public void givenCustomerIsFoundWithBirthDateNotMatchedUniquelyWithBirthDateFromC216AndCustomerNameIsNotPresentInRequest() {
        mockScenarioHelper.expectC216CallForBirthDateNotMatched(request.getHeader(), request.getProductArrangement().getPrimaryInvolvedParty().getExistingSortCode(), request.getProductArrangement().getPrimaryInvolvedParty().getExistingAccountNumber(), "true", 456662112l);
    }

    @Given("customer is found with birth date not matched with birth date from C216")
    public void givenCustomerIsFoundWithBirthDateNotMatchedWithBirthDateFromC216() {
        mockScenarioHelper.expectC216CallForBirthDateNotMatched(request.getHeader(), request.getProductArrangement().getPrimaryInvolvedParty().getExistingSortCode(), request.getProductArrangement().getPrimaryInvolvedParty().getExistingAccountNumber(), "false", 456662112l);
    }

    @Given("customer KYC compliance is $complianceStatus")
    public void givenCustomerKYCComplianceIsTrue(String complianceStatus) {
        mockScenarioHelper.expectF336Call(request.getHeader(), "456662112");
        if ("true".equals(complianceStatus)) {
            mockScenarioHelper.expectF061Call(request.getHeader(), "456662112", "+00090001232");
        } else {
            mockScenarioHelper.expectF061CallForNonKycCompliance(request.getHeader(), "456662112", "+00090001232");
        }
    }

    @Given("verde switch is $switchStatus")
    public void givenVerdeSwitchIsON(String switchStatus) throws ErrorInfo, com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.ErrorInfo, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockScenarioHelper.expectVerdeSwitchCall("LBG", switchStatus);
        mockScenarioHelper.expectB231Call(request.getHeader(), "456662112", "+00090001232", 0);
        mockScenarioHelper.expectLRASwitchCall("VER", "ON");
        mockScenarioHelper.expectB237Call(request.getHeader(), "456662112", "+00090001232", "false", 0);
        mockScenarioHelper.expectPrdDbCalls("P_LOAN_STP", "VER", "CR001");
        mockScenarioHelper.expectEligibilityCall(request.getHeader(), "456662112", "true", null, null);
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

    @Then("OPALOANS returns customer details and existing products in response")
    public void thenOPALOANSReturnsCustomerDetailsAndExistingProductsInResponse() {
        assertEquals("456662112", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerIdentifier());
        assertEquals(request.getProductArrangement().getPrimaryInvolvedParty().getExistingSortCode(), response.getProductArrangement().getPrimaryInvolvedParty().getExistingSortCode());
        assertEquals(request.getProductArrangement().getPrimaryInvolvedParty().getExistingAccountNumber(), response.getProductArrangement().getPrimaryInvolvedParty().getExistingAccountNumber());
        assertEquals(false, response.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().isIsStaffMember());
        assertEquals("000", response.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().getResidentialStatus());
        assertEquals("1988-01-22", response.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().getBirthDate().toString());
        assertEquals("001", response.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().getMaritalStatus());
        assertEquals("001", response.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().getGender());
        assertEquals("023", response.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().getEmploymentStatus());
        assertEquals(false, response.getProductArrangement().getPrimaryInvolvedParty().isNewCustomerIndicator());
        assertEquals("3", response.getProductArrangement().getPrimaryInvolvedParty().getSourceSystemId());
        assertEquals("+00090001232", response.getProductArrangement().getPrimaryInvolvedParty().getCidPersID());
        assertEquals("1", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerSegment());
        assertEquals(false, response.getProductArrangement().getPrimaryInvolvedParty().isIsAuthCustomer());
        assertEquals(3, response.getProductArrangement().getPrimaryInvolvedParty().getAuditData().size());
        assertEquals(1, response.getProductArrangement().getExistingProducts().size());
        assertEquals("ACCEPT", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult());
        assertEquals("CREDIT_SCORE", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
    }

    @Then("OPALOANS returns new customer indicator as $newCustomerIndicator in response")
    public void thenOPALOANSReturnsNewCustomerIndicatorAsFalseInResponse(String newCustomerIndicator) {
        assertEquals(newCustomerIndicator, response.getProductArrangement().getPrimaryInvolvedParty().isNewCustomerIndicator().toString());
    }

    @Then("OPALOANS returns reason code as $reasonCode and description as $description in response")
    public void thenOPALOANSReturnsReasonCodeAs01AndDescriptionAsFirstNameAndLastNameNotMatchedWithOCISInResponse(String reasonCode, String description) {
        assertNotNull(response.getProductArrangement().getReasonCode());
        assertEquals(reasonCode, response.getProductArrangement().getReasonCode().getCode());
        assertEquals(description, response.getProductArrangement().getReasonCode().getDescription());
    }

    @Then("OPALOANS returns condition with name as $conditionName and result as $conditionResult in response")
    public void thenOPALOANSReturnsConditionWithNameAsADDITIONAL_DATA_REQUIRED_INDICATORAndResultAsTrueInResponse(String conditionName, String conditionResult) {
        assertFalse(response.getProductArrangement().getConditions().isEmpty());
        assertEquals(conditionName, response.getProductArrangement().getConditions().get(0).getName());
        assertEquals(conditionResult, response.getProductArrangement().getConditions().get(0).getResult());
    }
}

