package com.lloydsbanking.salsa.opasaving.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.offer.ApplicantType;
import lib_sim_bo.businessobjects.Customer;
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
import javax.xml.datatype.DatatypeFactory;
import java.text.ParseException;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story08OpasavingDeterminesEligibilityOfAppliedProductTest extends AbstractOpasavingJBehaveTestBase {

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;
    long applicationId = 0;
    HashMap<String, Long> appDetails = new HashMap<>();

    @BeforeScenario
    public void resetResponse() {
        request = null;
        response = null;
    }

    @Given("Customer is guardian")
    public void givenCustomerIsGuardian() throws ParseException, DatatypeConfigurationException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg {
        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");
        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.GUARDIAN.getValue());
        mockScenarioHelper.expectSwitchValueFromSwitchName("SW_SvngCstSegPrc", "LTB", 3, "0");
        mockScenarioHelper.expectF204Call(dataHelper.createOpaSavingRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
        appDetails = mockScenarioHelper.expectChildApplication();
        applicationId = appDetails.get("appId");
        request.getProductArrangement().getPrimaryInvolvedParty().setRelatedIndividualIdentifier(String.valueOf(appDetails.get("partyId")));

        request.getProductArrangement().setArrangementId(String.valueOf(applicationId));
        request.getProductArrangement().setApplicationStatus("1002");
    }

    @Given("Customer is not guardian")
    public void givenCustomerIsNotGuardian() {
        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");
        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.SELF.getValue());
    }

    @Given("BFPO Indicator is not present and eligibility is true")
    public void givenBFPOIndicatorIsNotPresentAndEligibilityIsTrue() throws ParseException, DatatypeConfigurationException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockScenarioHelper.expectEligibility("true", dataHelper.createOpaSavingRequestHeader("LTB"), false, 1);
        mockScenarioHelper.expectSwitchValueFromSwitchName("SW_SvngCstSegPrc", "LTB", 3, "0");
        mockScenarioHelper.expectLookupDataForX711("LTB");
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        mockScenarioHelper.expectX711Call(customer, request.getHeader());
        customer.setCustomerIdentifier(null);
        mockScenarioHelper.expectF204Call(dataHelper.createOpaSavingRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
    }

    @Given("BFPO Indicator is present and eligibility is true")
    public void givenBFPOIndicatorIsPresentAndEligibilityIsTrue() throws ParseException, DatatypeConfigurationException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        request.getProductArrangement().getPrimaryInvolvedParty().getPostalAddress().get(0).setIsBFPOAddress(true);
        mockScenarioHelper.expectF204Call(dataHelper.createOpaSavingRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
        mockScenarioHelper.expectF062Call("SA", false, request.getProductArrangement().getPrimaryInvolvedParty(), dataHelper.createOpaSavingRequestHeader("LTB"), null, null, 0);
        mockScenarioHelper.expectEligibility("true", dataHelper.createOpaSavingRequestHeader("LTB"), true, 2);
        mockScenarioHelper.expectSwitchValueFromSwitchName("SW_SvngCstSegPrc", "LTB", 3, "0");
        mockScenarioHelper.expectLookupDataForX711Decline("LTB");
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        mockScenarioHelper.expectX711Call(customer, request.getHeader());
        customer.setCustomerIdentifier(null);
    }

    @Given("BFPO Indicator is not present and eligibility is false")
    public void givenBFPOIndicatorIsNotPresentAndEligibilityIsFalse() throws DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, OfferProductArrangementInternalServiceErrorMsg, ParseException, DatatypeConfigurationException {
        mockScenarioHelper.expectF204Call(dataHelper.createOpaSavingRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
        mockScenarioHelper.expectInstructionRulesViewCallForDecline("P_CLUB", "LTB");
        mockScenarioHelper.expectEligibilityFailureWhenCustomerYoungerThan200(dataHelper.createOpaSavingRequestHeader("LTB"), false, 1);
    }

    @Given("BFPO Indicator is present and eligibility is false")
    public void givenBFPOIndicatorIsPresentAndEligibilityIsFalse() throws DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, OfferProductArrangementInternalServiceErrorMsg, ParseException, DatatypeConfigurationException {
        DatatypeFactory datatypeFactory = null;
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        request.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().setBirthDate(datatypeFactory.newXMLGregorianCalendar("2016-03-03T06:40:56.046Z"));
        request.getProductArrangement().getPrimaryInvolvedParty().getPostalAddress().get(0).setIsBFPOAddress(true);
        mockScenarioHelper.expectF204Call(dataHelper.createOpaSavingRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));

        mockScenarioHelper.expectEligibilityFailure(dataHelper.createOpaSavingRequestHeader("LTB"), true, 1);
        mockScenarioHelper.expectF062Call("SA", false, request.getProductArrangement().getPrimaryInvolvedParty(), dataHelper.createOpaSavingRequestHeader("LTB"), null, null, 0);
    }

    @When("UI calls OPASaving with valid request")
    public void whenUICallsOPASavingWithValidRequest() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg {
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaSavingRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF061Call(dataHelper.createOpaSavingRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectF336CallWithProductHoldings(dataHelper.createOpaSavingRequestHeader("LTB"), 3, 2, "12345");
        mockScenarioHelper.expectLookupListFromChannelAndEvidenceList("LTB");

        mockControl.go();
        response = opasavingClient.offerProductArrangement(request);
    }

    @Then("OPASaving returns existing product arrangements in response")
    public void thenOPASavingReturnsEligibilityDetailsAndExistingProductArrangementsInResponse() {
        DatatypeFactory datatypeFactory = null;
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        System.out.println("response 1= " + response.getExistingProductArrangements());
        assertEquals("1", response.getExistingProductArrangements().get(0).getAssociatedProduct().getProductIdentifier());
        assertEquals("LTB", response.getExistingProductArrangements().get(0).getAssociatedProduct().getBrandName());
        assertEquals("001", response.getExistingProductArrangements().get(0).getAssociatedProduct().getStatusCode());
        assertEquals(datatypeFactory.newXMLGregorianCalendar("2016-01-01"), response.getExistingProductArrangements().get(0).getAssociatedProduct().getAmendmentEffectiveDate());
        assertEquals("1", response.getExistingProductArrangements().get(0).getAssociatedProduct().getProductIdentifier());
        assertEquals("00007", response.getExistingProductArrangements().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).getSystemCode());
        assertEquals("1", response.getExistingProductArrangements().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).getProductIdentifier());
        assertEquals("00007", response.getExistingProductArrangements().get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(1).getSystemCode());
        assertEquals("3", response.getExistingProductArrangements().get(0).getAssociatedProduct().getProductType());
        assertFalse(response.getExistingProductArrangements().get(0).getFinancialInstitution().getHasOrganisationUnits().isEmpty());
    }

    @Then("OPASaving returns no existing product arrangements in response")
    public void thenOPASavingReturnsNoExistingProductArrangementsInResponse() {
        assertTrue(response.getExistingProductArrangements().isEmpty());
    }

    @Then("eligibility details are returned in response")
    public void thenEligibilityDetailsAreReturnedInResponse() {
        assertEquals("false", response.getProductArrangement().getAssociatedProduct().getEligibilityDetails().getIsEligible());
        assertEquals("CR002", response.getProductArrangement().getAssociatedProduct().getEligibilityDetails().getDeclineReasons().get(0).getCode());
    }

    @Then("eligibility details are not returned in response")
    public void thenEligibilityDetailsAreNotReturnedInResponse() {
        assertNull(response.getProductArrangement().getAssociatedProduct().getEligibilityDetails());
    }
}
