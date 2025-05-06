package com.lloydsbanking.salsa.opapca.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story05OpapcaEntersOcisDetailsForNewCustomerTest extends AbstractOpapcaJBehaveTestBase {

    OfferProductArrangementRequest request;

    OfferProductArrangementResponse response;

    OfferProductArrangementInternalServiceErrorMsg offerProductArrangementInternalServiceErrorMsg;
    long applicationId = 0l;

    @BeforeScenario
    public void resetResponse() {
        mockControl.reset();
        request = null;
        response = null;
        applicationId = 0;
    }

    @Given("New Customer Indicator is true for a customer")
    public void givenNewCustomerIndicatorIsTrueForACustomer() {
        mockScenarioHelper.expectF447CallForNewCustomer(dataHelper.createOpaPcaRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
    }

    @When("UI calls OPAPCA with valid request")

    public void whenUICallsOPACAWithValidRequest() throws ParseException, OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {

        mockScenarioHelper.expectF204CallForExistingCustomers(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));

        mockScenarioHelper.expectF205CallForExistingCustomer(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"), 8);

        List<String> groupCodeList = new ArrayList<>();
        groupCodeList.add("CC_CROSS_SELL_FC");
        mockScenarioHelper.expectLookupListFromChannelAndGroupCodeList("LTB", groupCodeList);
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");

        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockScenarioHelper.expectLookupListFromChannelAndEvidenceList("LTB");
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getHeader().setContactPointId("0000777505");

        request.getProductArrangement().getPrimaryInvolvedParty().setCustomerIdentifier(null);
        mockScenarioHelper.expectRpcEmptyProductListResponse(request.getHeader());
        mockScenarioHelper.expectEligibilityCa("true", dataHelper.createOpaPcaRequestHeader("LTB"), false, 1);
        mockScenarioHelper.expectLookupDataForX711("LTB");
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());
        mockScenarioHelper.expectX711Call(customer, request.getHeader());

        customer.setCustomerIdentifier(null);
        mockControl.go();
        try {
            response = opaPcaClient.offerProductArrangement(request);
        } catch (OfferProductArrangementInternalServiceErrorMsg externalServiceErrorMsg) {
            offerProductArrangementInternalServiceErrorMsg = externalServiceErrorMsg;
        }

    }

    @Then("OPAPCA enters details in OCIS for the customer")
    public void thenOPALoansEntersDetailsInOCISForTheCustomer() {
        assertEquals("1002", response.getProductArrangement().getApplicationStatus());
        assertEquals("1", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getScoreResult());
        assertEquals("ASM", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getAssessmentType());
        assertEquals("code", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getReferralCode().get(0).getCode());
        assertEquals("description", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getReferralCode().get(0).getDescription());
        assertEquals("code", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getReferralCode().get(0).getCode());
        assertEquals("description", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getReferralCode().get(0).getDescription());
        //assertEquals("ACCEPT", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult());
        // TODO: Check all asserts
        assertEquals("EIDV", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
        assertEquals("ADDRESS_EVIDENCE", response.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).getAuditType());
        assertEquals("12345678", response.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).getAuditDate());
        assertEquals("123456", response.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).getAuditTime());
        assertEquals("ADDRESS", response.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(1).getAuditType());
        assertEquals("00267277", response.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(1).getAuditDate());
        assertEquals("345678", response.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(1).getAuditTime());

    }

    @Given("there is no error from ocis f062")
    public void givenThereIsNoErrorFromOcisF062() throws ParseException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementExternalServiceErrorMsg {

        Customer customer = dataHelper.createPrimaryInvolvedParty();
        mockScenarioHelper.expectF062Call("CA", false, customer, dataHelper.createOpaPcaRequestHeader("LTB"), null, null, 0);

    }

    @Given("there is non zero error code from ocis f062")
    public void givenThereIsNonZeroErrorCodeFromOcisF062() throws ParseException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementExternalServiceErrorMsg {

        Customer customer = dataHelper.createPrimaryInvolvedParty();
        mockScenarioHelper.expectF062Call("CA", false, customer, dataHelper.createOpaPcaRequestHeader("LTB"), null, null, 165036);

    }

    @Then("OPAPCA throws exception to the calling component")
    public void thenOPACAThrowsExceptionToTheCallingComponent() {
        assertEquals("820001", offerProductArrangementInternalServiceErrorMsg.getFaultInfo().getReasonCode());
        assertNull(response);
    }

}