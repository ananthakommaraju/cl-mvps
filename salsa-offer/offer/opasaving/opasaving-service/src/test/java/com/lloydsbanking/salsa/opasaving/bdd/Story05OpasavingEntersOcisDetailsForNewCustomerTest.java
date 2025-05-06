package com.lloydsbanking.salsa.opasaving.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.offer.ApplicantType;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story05OpasavingEntersOcisDetailsForNewCustomerTest extends AbstractOpasavingJBehaveTestBase {

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;
    OfferProductArrangementInternalServiceErrorMsg offerProductArrangementInternalServiceErrorMsg;

    @BeforeScenario
    public void resetResponse() {
        request = null;
        response = null;
    }

    @Given("New Customer Indicator is true for a customer")
    public void givenNewCustomerIndicatorIsTrueForACustomer() {
        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");
        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());
        mockScenarioHelper.expectF447CallForNewCustomer(dataHelper.createOpaSavingRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
    }

    @Given("there is no error from ocis f062")
    public void givenThereIsNoErrorFromOcisF062() throws ParseException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementExternalServiceErrorMsg {
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        mockScenarioHelper.expectF062Call("SA", false, customer, dataHelper.createOpaSavingRequestHeader("LTB"), null, null, 0);
    }

    @Given("there is non zero error code from ocis f062")
    public void givenThereIsNonZeroErrorCodeFromOcisF062() throws ParseException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementExternalServiceErrorMsg {
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        mockScenarioHelper.expectF062Call("SA", false, customer, dataHelper.createOpaSavingRequestHeader("LTB"), null, null, 165036);
    }

    @When("UI calls OPASaving with valid request")
    public void whenUICallsOPASavingWithValidRequest() throws ParseException, OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockScenarioHelper.expectLookupListFromChannelAndEvidenceList("LTB");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaSavingRequestHeader("LTB"), "0");
        mockScenarioHelper.expectF336Call(dataHelper.createOpaSavingRequestHeader("LTB"), 1, 2, "0");
        mockScenarioHelper.expectEligibility("true", dataHelper.createOpaSavingRequestHeader("LTB"), false, 1);
        mockScenarioHelper.expectSwitchValueFromSwitchName("SW_SvngCstSegPrc", "LTB", 3, "0");
        mockScenarioHelper.expectF204CallForExistingCustomers(dataHelper.createOpaSavingRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
        mockScenarioHelper.expectLookupListFromChannelAndEvidenceList("LTB");

        mockControl.go();
        try {
            response = opasavingClient.offerProductArrangement(request);
        } catch (OfferProductArrangementInternalServiceErrorMsg internalServiceErrorMsg) {
            offerProductArrangementInternalServiceErrorMsg = internalServiceErrorMsg;
        }
    }

    @Then("OPASaving enters details in OCIS for the customer")
    public void thenOPALoansEntersDetailsInOCISForTheCustomer() {
        assertEquals("1002", response.getProductArrangement().getApplicationStatus());
        assertEquals("1", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getScoreResult());
        assertEquals("ASM", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getAssessmentType());
        assertEquals("code", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getReferralCode().get(0).getCode());
        assertEquals("description", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getReferralCode().get(0).getDescription());
        assertEquals("N/A", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult());
        assertEquals("EIDV", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
        assertEquals(2, response.getProductArrangement().getPrimaryInvolvedParty().getAuditData().size());
        assertEquals("ADDRESS_EVIDENCE", response.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).getAuditType());
        assertEquals("12345678", response.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).getAuditDate());
        assertEquals("123456", response.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).getAuditTime());
        assertEquals("ADDRESS", response.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(1).getAuditType());
        assertEquals("00267277", response.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(1).getAuditDate());
        assertEquals("345678", response.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(1).getAuditTime());
    }

    @Then("OPASaving throws exception to the calling component")
    public void thenOPASavingThrowsExceptionToTheCallingComponent() {
        assertEquals("820001", offerProductArrangementInternalServiceErrorMsg.getFaultInfo().getReasonCode());
        assertNull(response);
    }
}