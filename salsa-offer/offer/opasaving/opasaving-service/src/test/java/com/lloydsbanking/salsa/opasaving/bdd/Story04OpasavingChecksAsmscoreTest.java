package com.lloydsbanking.salsa.opasaving.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.offer.ApplicantType;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story04OpasavingChecksAsmscoreTest extends AbstractOpasavingJBehaveTestBase {

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;
    String experian;

    @BeforeScenario
    public void resetResponse() {
        request = null;
        response = null;

    }

    @Given("Fraud check is negative and status is accept")
    public void givenFraudCheckIsNegativeAndStatusIsAccept() throws ParseException, DatatypeConfigurationException {
        mockScenarioHelper.expectF204Call(dataHelper.createOpaSavingRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
    }

    @Given("Fraud check is negative and status is refer")
    public void givenFraudCheckIsNegativeAndStatusIsRefer() throws ParseException, DatatypeConfigurationException {
        mockScenarioHelper.expectF204Call(dataHelper.createOpaSavingRequestHeader("LTB"), "2", dataHelper.createReferralCodeList("code", "description"));
    }

    @Given("Fraud check is decline")
    public void givenFraudCheckIsDecline() throws ParseException, DatatypeConfigurationException {
        mockScenarioHelper.expectF204Call(dataHelper.createOpaSavingRequestHeader("LTB"), "3", dataHelper.createReferralCodeList("625", "description"));
        List<String> lookUpText = new ArrayList<>();
        lookUpText.add(0, "625");
        mockScenarioHelper.expectLookupListFromGroupCodeAndChannelAndLookUpText("ASM_DECLINE_CODE", "LTB", lookUpText);
    }

    @Given("Fraud check is negative and status is refer and referral code is 501")
    public void givenFraudCheckIsNegativeAndStatusIsReferAndReferralCodeIs501() throws ParseException, DatatypeConfigurationException {
        mockScenarioHelper.expectF204Call(dataHelper.createOpaSavingRequestHeader("LTB"), "2", dataHelper.createReferralCodeList("501", "description"));
    }

    @When("UI calls OpaSaving with valid request")
    public void whenUICallsOpaSavingWithValidRequest() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, ParseException, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");
        request.getProductArrangement().setArrangementId(null);
        mockScenarioHelper.expectLookupDataForDuplicateApplication("LTB", "BRAND_COHOLDING");
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaSavingRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaSavingRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaSavingRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectEligibility("true", dataHelper.createOpaSavingRequestHeader("LTB"), false, 1);
        mockScenarioHelper.expectSwitchValueFromSwitchName("SW_SvngCstSegPrc", "LTB", 3, "0");
        mockScenarioHelper.expectLookupListFromChannelAndEvidenceList("LTB");

        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());

        mockControl.go();
        response = opasavingClient.offerProductArrangement(request);
    }

    @Then("OpaSaving returns application status and asm score as accept for the customer")
    public void thenOpaSavingReturnsApplicationStatusAndAsmScoreAsAcceptForTheCustomer() {
        assertEquals("1002", response.getProductArrangement().getApplicationStatus());
        assertEquals("1", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getScoreResult());
        assertEquals("ASM", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getAssessmentType());
        assertEquals("description", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getReferralCode().get(0).getDescription());
        assertEquals("code", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getReferralCode().get(0).getCode());
    }


    @Then("OpaSaving returns application status and asm score as unscored for the customer")
    public void thenOpaSavingReturnsApplicationStatusAndAsmScoreAsUnscoredForTheCustomer() {
        assertEquals("1005", response.getProductArrangement().getApplicationStatus());
        assertEquals("2", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getScoreResult());
        assertEquals("ASM", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getAssessmentType());
        assertEquals("code", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getReferralCode().get(0).getCode());
        assertEquals("description", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getReferralCode().get(0).getDescription());
    }

    @Then("OpaSaving returns application status and asm score as refer for the customer")
    public void thenOpaSavingReturnsApplicationStatusAndAsmScoreAsReferForTheCustomer() {
        assertEquals("1003", response.getProductArrangement().getApplicationStatus());
        assertEquals("2", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getScoreResult());
        assertEquals("ASM", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getAssessmentType());
    }

    @Then("OpaSaving returns application status as decline for the customer")
    public void thenOpaSavingReturnsApplicationStatusAsDeclineForTheCustomer() {
        assertEquals("1004", response.getProductArrangement().getApplicationStatus());
        assertEquals("3", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getScoreResult());
        assertEquals("ASM", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getAssessmentType());
        assertEquals("625", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getReferralCode().get(0).getCode());
        assertEquals("description", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getReferralCode().get(0).getDescription());
    }

    @Then("OpaSaving returns application status as unscored for the customer")
    public void thenOpaSavingReturnsApplicationStatusAsUnscoredForTheCustomer() {
        assertEquals("1005", response.getProductArrangement().getApplicationStatus());
        assertEquals("2", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getScoreResult());
        assertEquals("ASM", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getAssessmentType());
        assertEquals("501", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getReferralCode().get(0).getCode());
        assertEquals("description", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getReferralCode().get(0).getDescription());
    }
}
