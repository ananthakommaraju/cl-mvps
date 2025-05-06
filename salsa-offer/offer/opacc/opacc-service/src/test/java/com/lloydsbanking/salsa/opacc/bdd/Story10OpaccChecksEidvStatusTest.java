package com.lloydsbanking.salsa.opacc.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementInternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story10OpaccChecksEidvStatusTest extends AbstractOpaccJBehaveTestBase {

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;

    @BeforeScenario
    public void resetResponse() {
        request = null;
        response = null;
    }


    @Given("Customer is not identified at Experian")
    public void givenCustomerIsNotIdentifiedAtExperian() throws OfferProductArrangementInternalServiceErrorMsg {
        mockScenarioHelper.expectF447Call(dataHelper.createOpaccRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061CallWithoutEvidenceData(dataHelper.createOpaccRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("000777505"), "aaa");

        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");

        mockScenarioHelper.expectSwitchDtoFromSwitchName("SW_EnblMCS", "LTB", 3, "0");

        mockScenarioHelper.expectLookupDataForX711Decline("LTB");

        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");

        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());

        mockScenarioHelper.expectX711Call(customer, request.getHeader());
        request.getHeader().setContactPointId("0000777505");

    }

    @When("UI calls OPACC with valid request")
    public void whenUICallsOPACCWithValidRequest() {
        try {
            mockControl.go();
            request.getProductArrangement().getPrimaryInvolvedParty().setCustomerIdentifier(null);

            response = opaccClient.offerProductArrangement(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Then("OPACC returns Customer Score as EIDV status Decline")
    public void thenOPACCReturnsCustomerScoreAsEIDVStatusDecline() {
        assertEquals("EIDV", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
        assertEquals("DECLINE", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult());
    }

    @Given("Customer contains BFPO address indicator")
    public void givenCustomerContainsBFPOAddressIndicator() throws ParseException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        mockScenarioHelper.expectF447Call(dataHelper.createOpaccRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061CallWithoutEvidenceData(dataHelper.createOpaccRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("000777505"), "aaa");

        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");

        mockScenarioHelper.expectSwitchDtoFromSwitchName("SW_EnblMCS", "LTB", 3, "0");

        mockScenarioHelper.expectLookupDataForX711("LTB");

        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");

        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());
        mockScenarioHelper.expectX711Call(customer, request.getHeader());
        request.getHeader().setContactPointId("0000777505");
        request.getProductArrangement().getPrimaryInvolvedParty().setCustomerIdentifier(null);
        request.getProductArrangement().getPrimaryInvolvedParty().getPostalAddress().get(0).setIsBFPOAddress(true);
        mockScenarioHelper.expectF062Call("CA", false, request.getProductArrangement().getPrimaryInvolvedParty(), dataHelper.createOpaccRequestHeader("LTB"), null, null, 0, "REFER", false);
        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeader("777505"), "1", dataHelper.createReferralCodeList("code", "description"));
        mockScenarioHelper.expectRpcProducts(request.getHeader());
    }

    @Then("OPACC returns Customer Score as EIDV status Refer")
    public void thenOPACCReturnsCustomerScoreAsEIDVStatusRefer() {
        assertEquals("EIDV", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
        assertEquals("REFER", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult());

    }

    @Given("Customer is approved at Experian")
    public void givenCustomerIsApprovedAtExperian() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, ParseException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg {
        mockScenarioHelper.expectF447Call(dataHelper.createOpaccRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061CallWithoutEvidenceData(dataHelper.createOpaccRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("000777505"), "aaa");

        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");

        mockScenarioHelper.expectSwitchDtoFromSwitchName("SW_EnblMCS", "LTB", 3, "0");

        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        mockScenarioHelper.expectLookupDataForX711("LTB");
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());
        mockScenarioHelper.expectX711Call(customer, request.getHeader());
        request.getHeader().setContactPointId("0000777505");
        request.getProductArrangement().getPrimaryInvolvedParty().setCustomerIdentifier(null);
        mockScenarioHelper.expectF062Call("CA", false, request.getProductArrangement().getPrimaryInvolvedParty(), dataHelper.createOpaccRequestHeader("LTB"), null, null, 0, "", false);
        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeader("777505"), "1", dataHelper.createReferralCodeList("code", "description"));
        mockScenarioHelper.expectRpcProducts(request.getHeader());
    }

    @Then("OPACC returns Customer Score as EIDV status Approved")
    public void thenOPACCReturnsCustomerScoreAsEIDVStatusApproved() {
        assertEquals("EIDV", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
        assertEquals("ACCEPT", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult());
    }

    @Given("Customer is referred at Experian")
    public void givenCustomerIsReferredAtExperian() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, ParseException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg {
        mockScenarioHelper.expectF447Call(dataHelper.createOpaccRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061CallWithoutEvidenceDataAndPersonalData(dataHelper.createOpaccRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("000777505"), "aaa");

        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");

        mockScenarioHelper.expectSwitchDtoFromSwitchName("SW_EnblMCS", "LTB", 3, "0");

        mockScenarioHelper.expectLookupDataForX711("LTB");
        mockScenarioHelper.expectLookupDataForX711Refer("LTB");

        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");

        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());

        mockScenarioHelper.expectX711CallEidvRefer(customer, request.getHeader());
        request.getHeader().setContactPointId("0000777505");
        request.getProductArrangement().getPrimaryInvolvedParty().setCustomerIdentifier(null);


        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeader("777505"), "1", dataHelper.createReferralCodeList("code", "description"));
        mockScenarioHelper.expectRpcProducts(request.getHeader());
    }


}
