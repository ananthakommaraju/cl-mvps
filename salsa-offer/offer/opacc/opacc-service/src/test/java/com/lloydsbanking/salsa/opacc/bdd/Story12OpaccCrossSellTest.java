package com.lloydsbanking.salsa.opacc.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.pam.model.Applications;
import com.lloydsbanking.salsa.offer.EIDVStatus;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
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

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story12OpaccCrossSellTest extends AbstractOpaccJBehaveTestBase {

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;
    int applicationsSize;
    int individualsSize;
    int partyApplicationsSize;
    int streetAddressesSize;
    int promoPartyApplicationsSize;
    String experian;
    Applications applications;

    @BeforeScenario
    public void resetResponse() {
        request = null;
        response = null;
        applicationsSize = 0;
        individualsSize = 0;
        partyApplicationsSize = 0;
        streetAddressesSize = 0;
        promoPartyApplicationsSize = 0;
    }

    @Given("related application id present in request")
    public void givenRelatedApplicationIdPresentInRequest() {
        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        request.getProductArrangement().setRelatedApplicationId(String.valueOf(mockScenarioHelper.expectRelatedapplicationId()));
        request.getProductArrangement().setApplicationRelationShipType("20001");
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult(EIDVStatus.ACCEPT.getValue());
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).setAssessmentType("EIDV");
    }

    @Given("Credit Decision is accept and Direct Debit is required")
    public void givenCreditDecisionIsAcceptAndDirectDebitIsRequired() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeader("LTB"), "1", dataHelper.createReferralCodeListForDirectDebitRequiredAccept("code", "description"));
        mockScenarioHelper.expectRpcProducts(request.getHeader());
    }

    @Given("Credit Decision is accept and Direct Debit is not required")
    public void givenCreditDecisionIsAcceptAndDirectDebitIsNotRequired() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
        mockScenarioHelper.expectRpcProducts(request.getHeader());
    }

    @Given("Credit Decision is refer and Direct Debit is not required and referral code is $isExperianAvailable1")
    public void givenCreditDecisionIsReferAndDirectDebitIsNotRequiredAndReferralCodeIsIsExperianAvailable(String isExperianAvailable1) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        experian = isExperianAvailable1;
        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeader("LTB"), "2", dataHelper.createReferralCodeList(isExperianAvailable1, "description"));
        if (isExperianAvailable1.equals("501")) {
            mockScenarioHelper.expectRpcProductsForUnscored(request.getHeader());
        } else {
            mockScenarioHelper.expectRpcProducts(request.getHeader());
        }
    }

    @Given("Credit Decision is refer and Direct Debit is required and referral code is $isExperianAvailable2")
    public void givenCreditDecisionIsReferAndDirectDebitIsRequiredAndReferralCodeIsIsExperianAvailable(String isExperianAvailable2) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        experian = isExperianAvailable2;
        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeader("LTB"), "2", dataHelper.createReferralCodeListForDirectDebitRequiredRefer(isExperianAvailable2, "description"));
        if (isExperianAvailable2.equals("501")) {
            mockScenarioHelper.expectRpcProductsForUnscored(request.getHeader());
        } else {
            mockScenarioHelper.expectRpcProducts(request.getHeader());
        }
    }

    @Given("Credit Decision is decline")
    public void givenCreditDecisionIsDecline() {
        List<String> lookUpText = new ArrayList<>();
        lookUpText.add(0, "Cnt_Pnt_Prtflio");
        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeader("LTB"), "3", dataHelper.createReferralCodeList("Cnt_Pnt_Prtflio", "description"));
        mockScenarioHelper.expectLookupListFromGroupCodeAndChannelAndLookUpText("ASM_DECLINE_CODE", "LTB", lookUpText);
    }

    @When("UI calls OPACC with valid request")
    public void whenUICallsOPACCWithValidRequest() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg {
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        applicationsSize = mockScenarioHelper.expectApplicationsCreated();
        individualsSize = mockScenarioHelper.expectIndividualsCreated();
        partyApplicationsSize = mockScenarioHelper.expectPartyApplicationsCreated();
        streetAddressesSize = mockScenarioHelper.expectStreetAddressesCreated();
        promoPartyApplicationsSize = mockScenarioHelper.expectPromoPartyApplicationsCreated();

        mockControl.go();
        response = opaccClient.offerProductArrangement(request);
        applications = mockScenarioHelper.expectRetrieveApplicationFromPAM(Long.valueOf(response.getProductArrangement().getArrangementId()));
    }

    @Then("OPACC returns application status and asm score as accept")
    public void thenOPACCReturnsApplicationStatusAndAsmScoreAsAccept() {
        assertEquals("1002", response.getProductArrangement().getApplicationStatus());
        assertEquals("1", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getScoreResult());
        assertEquals("ASM", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getAssessmentType());
        assertEquals("ACCEPT", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult());
        assertEquals("EIDV", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
        assertNotNull(response.getProductArrangement().getArrangementId());
        assertEquals(applicationsSize + 1, mockScenarioHelper.expectApplicationsCreated());
        assertEquals(individualsSize + 1, mockScenarioHelper.expectIndividualsCreated());
        assertEquals(partyApplicationsSize + 1, mockScenarioHelper.expectPartyApplicationsCreated());
        assertEquals(streetAddressesSize, mockScenarioHelper.expectStreetAddressesCreated());
        assertEquals(promoPartyApplicationsSize + 1, mockScenarioHelper.expectPromoPartyApplicationsCreated());

        assertNotNull(applications.getApplicationStatus());
        assertNotNull(applications.getDateModified());
        assertEquals(response.getProductArrangement().getArrangementId(), String.valueOf(applications.getId()));
        assertEquals("1002", applications.getApplicationStatus().getStatus());
        assertNull(applications.getAbandonDeclineReasons());
    }

    @Then("OPACC returns application status and asm score as refer")
    public void thenOPACCReturnsApplicationStatusAndAsmScoreAsRefer() {
        assertEquals("1003", response.getProductArrangement().getApplicationStatus());
        assertEquals("2", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getScoreResult());
        assertEquals("ASM", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getAssessmentType());
        assertEquals("ACCEPT", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult());
        assertEquals("EIDV", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
        assertNotNull(response.getProductArrangement().getArrangementId());
        assertEquals(applicationsSize + 1, mockScenarioHelper.expectApplicationsCreated());
        assertEquals(individualsSize + 1, mockScenarioHelper.expectIndividualsCreated());
        assertEquals(partyApplicationsSize + 1, mockScenarioHelper.expectPartyApplicationsCreated());
        assertEquals(streetAddressesSize, mockScenarioHelper.expectStreetAddressesCreated());
        assertEquals(promoPartyApplicationsSize + 1, mockScenarioHelper.expectPromoPartyApplicationsCreated());

        assertNotNull(applications.getApplicationStatus());
        assertNotNull(applications.getDateModified());
        assertEquals(response.getProductArrangement().getArrangementId(), String.valueOf(applications.getId()));
        assertEquals("1003", applications.getApplicationStatus().getStatus());
        assertNull(applications.getAbandonDeclineReasons());
    }

    @Then("OPACC returns application status and asm score as decline")
    public void thenOPACCReturnsApplicationStatusAndAsmScoreAsDecline() {
        assertEquals("1004", response.getProductArrangement().getApplicationStatus());
        assertEquals("3", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getScoreResult());
        assertEquals("ASM", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getAssessmentType());
        assertEquals("ACCEPT", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult());
        assertEquals("EIDV", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
        assertNotNull(response.getProductArrangement().getArrangementId());
        assertEquals(applicationsSize + 1, mockScenarioHelper.expectApplicationsCreated());
        assertEquals(individualsSize + 1, mockScenarioHelper.expectIndividualsCreated());
        assertEquals(partyApplicationsSize + 1, mockScenarioHelper.expectPartyApplicationsCreated());
        assertEquals(streetAddressesSize, mockScenarioHelper.expectStreetAddressesCreated());
        assertEquals(promoPartyApplicationsSize + 1, mockScenarioHelper.expectPromoPartyApplicationsCreated());

        assertNotNull(applications.getApplicationStatus());
        assertNotNull(applications.getDateModified());
        assertEquals(response.getProductArrangement().getArrangementId(), String.valueOf(applications.getId()));
        assertEquals("1004", applications.getApplicationStatus().getStatus());
        //System.out.println("applications = " + applications.getAbandonDeclineReasons());
        //assertNull(applications.getAbandonDeclineReasons());
    }

    @Then("OPACC returns application status and asm score as unscored")
    public void thenOPACCReturnsApplicationStatusAndAsmScoreAsUnscored() {
        assertEquals("1005", response.getProductArrangement().getApplicationStatus());
        assertEquals("2", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getScoreResult());
        assertEquals("ASM", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getAssessmentType());
        assertEquals("ACCEPT", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult());
        assertEquals("EIDV", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
        assertNotNull(response.getProductArrangement().getArrangementId());
        assertEquals(applicationsSize + 1, mockScenarioHelper.expectApplicationsCreated());
        assertEquals(individualsSize + 1, mockScenarioHelper.expectIndividualsCreated());
        assertEquals(partyApplicationsSize + 1, mockScenarioHelper.expectPartyApplicationsCreated());
        assertEquals(streetAddressesSize, mockScenarioHelper.expectStreetAddressesCreated());
        assertEquals(promoPartyApplicationsSize + 1, mockScenarioHelper.expectPromoPartyApplicationsCreated());

        assertNotNull(applications.getApplicationStatus());
        assertNotNull(applications.getDateModified());
        assertEquals(response.getProductArrangement().getArrangementId(), String.valueOf(applications.getId()));
        assertEquals("1005", applications.getApplicationStatus().getStatus());
        assertNull(applications.getAbandonDeclineReasons());
    }

    @Then("direct debit indicator as false for the customer")
    public void thenDirectDebitIndicatorAsFalseForTheCustomer() {
        FinanceServiceArrangement financeServiceArrangement = (FinanceServiceArrangement) response.getProductArrangement();
        assertFalse(financeServiceArrangement.isIsDirectDebitRequired());
    }

    @Then("direct debit indicator as true for the customer")
    public void thenDirectDebitIndicatorAsTrueForTheCustomer() {
        FinanceServiceArrangement financeServiceArrangement = (FinanceServiceArrangement) response.getProductArrangement();
        assertTrue(financeServiceArrangement.isIsDirectDebitRequired());
    }
}
