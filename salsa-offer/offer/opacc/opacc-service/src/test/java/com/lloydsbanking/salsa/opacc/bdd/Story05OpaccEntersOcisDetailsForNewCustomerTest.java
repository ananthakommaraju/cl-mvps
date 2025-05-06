package com.lloydsbanking.salsa.opacc.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ReferenceDataLookUpDao;
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
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story05OpaccEntersOcisDetailsForNewCustomerTest extends AbstractOpaccJBehaveTestBase {

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;

    @Autowired
    ReferenceDataLookUpDao dataLookUpDao;

    OfferProductArrangementInternalServiceErrorMsg offerProductArrangementInternalServiceErrorMsg;

    @BeforeScenario
    public void resetResponse() {
        request = null;
        response = null;
    }


    @Given("New Customer Indicator is true for a customer")
    public void givenNewCustomerIndicatorIsTrueForACustomer() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, OfferProductArrangementInternalServiceErrorMsg, ParseException, DatatypeConfigurationException, OfferProductArrangementDataNotAvailableErrorMsg {
       // mockScenarioHelper.expectF447CallForNewCustomer(dataHelper.createOpaccRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        //mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");
        //mockScenarioHelper.expectF061CallWithoutEvidenceData(dataHelper.createOpaccRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("000777505"), "aaa");
        mockScenarioHelper.expectLookupDataForEvaluateStrength("IBL");
        mockScenarioHelper.expectLookupDataForEvaluateStrength("LTB");
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");

        mockScenarioHelper.expectSwitchDtoFromSwitchName("SW_EnblMCS", "LTB", 3, "0");

        mockScenarioHelper.expectLookupDataForX711Refer("LTB");

        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");

        request.getHeader().setContactPointId("0000777505");
        request.getProductArrangement().getPrimaryInvolvedParty().setCustomerIdentifier(null);
        request.getProductArrangement().getPrimaryInvolvedParty().getPostalAddress().get(0).setDurationofStay("9090");
        request.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().setCurrentEmploymentDuration("9090");
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();

        customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());
        mockScenarioHelper.expectX711Call(customer, request.getHeader());

        request.getProductArrangement().getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setPointSuffix(null);
        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeader("777505"), "1", dataHelper.createReferralCodeList("code", "description"));
        mockScenarioHelper.expectRpcProducts(request.getHeader());
    }

    @When("UI calls OPACC with valid request")
    public void whenUICallsOPACAWithValidRequest() throws ParseException, OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockControl.go();
        try {
            request.getProductArrangement().getPrimaryInvolvedParty().setCustomerIdentifier(null);
            response = opaccClient.offerProductArrangement(request);
        } catch (OfferProductArrangementInternalServiceErrorMsg externalServiceErrorMsg) {
            offerProductArrangementInternalServiceErrorMsg = externalServiceErrorMsg;
        }
    }

    @Then("OPACC enters details in OCIS for the customer")
    public void thenOPALoansEntersDetailsInOCISForTheCustomer() {
        assertEquals("1002", response.getProductArrangement().getApplicationStatus());
        assertEquals("1", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getScoreResult());
        assertEquals("ASM", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getAssessmentType());
        assertEquals("code", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getReferralCode().get(0).getCode());
        assertEquals("description", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getReferralCode().get(0).getDescription());
        assertEquals("code", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getReferralCode().get(0).getCode());
        assertEquals("description", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getReferralCode().get(0).getDescription());
        assertEquals("ACCEPT", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult());
        assertEquals("EIDV", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
        assertEquals("ADDRESS_EVIDENCE", response.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).getAuditType());
        assertEquals("12345678", response.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).getAuditDate());
        assertEquals("123456", response.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).getAuditTime());
        assertEquals("ADDRESS", response.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(1).getAuditType());
        assertEquals("345678", response.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(1).getAuditTime());
        assertEquals("00267277", response.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(1).getAuditDate());

    }

    @Given("there is no error from ocis f062")
    public void givenThereIsNoErrorFromOcisF062() throws ParseException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementExternalServiceErrorMsg {
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier(null);
        mockScenarioHelper.expectF062CallSuccessful("CC", false, customer, dataHelper.createOpaccRequestHeader("777505"), "149:009", "148:010", 0, "ACCEPT", false);
    }

    @Given("there is non zero error code from ocis f062")
    public void givenThereIsNonZeroErrorCodeFromOcisF062() throws ParseException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementExternalServiceErrorMsg {
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        mockScenarioHelper.expectF062CallSuccessful("CC", false, customer, dataHelper.createOpaccRequestHeader("777505"), "149:009", "148:010", 165036, "REFER", false);
    }

    @Then("OPACC throws exception to the calling component")
    public void thenOPACCThrowsExceptionToTheCallingComponent() {
        assertEquals("820001", offerProductArrangementInternalServiceErrorMsg.getFaultInfo().getReasonCode());
        assertNull(response);
    }
}
