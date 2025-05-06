package com.lloydsbanking.salsa.opacc.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.offer.ApplicantType;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementExternalBusinessErrorMsg;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementExternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementInternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class})
public class Story13OpaccRespondsToApiFailureTest extends AbstractOpaccJBehaveTestBase {

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;

    private Throwable throwable;


    @BeforeScenario
    public void resetResponse() {
        request = null;
        response = null;
        throwable = null;
    }


    @Given("OCIS F447 is not available")
    public void givenOCISF447IsNotAvailable() {
        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        request.getProductArrangement().getPrimaryInvolvedParty().setCustomerIdentifier(null);
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("LTB"), "aaa");
    }

    @Given("OCIS F336 is not available")
    public void givenOCISF336IsNotAvailable() {
        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("LTB"), "aaa");
    }

    @Given("OCIS F061 is not available")
    public void givenOCISF061IsNotAvailable() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("LTB"), "aaa");
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");
        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());
    }

    @Given("Data Encrypt Service is not available")
    public void givenDataEncryptServiceIsNotAvailable() {
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaccRequestHeader("LTB"), "12345");

        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");

        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());
    }

    @Given("DECI is not available")
    public void givenDECIIsNotAvailable() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaccRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("LTB"), "aaa");
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());
        mockScenarioHelper.expectLookupDataForX711Decline("LTB");
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());
        mockScenarioHelper.expectSwitchDtoFromSwitchName("SW_EnblMCS", "LTB", 3, "1");
        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
    }

    @Given("RPC is not available")
    public void givenRPCIsNotAvailable() {
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaccRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("LTB"), "aaa");
        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());
        mockScenarioHelper.expectLookupDataForX711Decline("LTB");
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());

        mockScenarioHelper.expectSwitchDtoFromSwitchName("SW_EnblMCS", "LTB", 3, "0");
    }

    @Given("F062 is not available")
    public void givenF062IsNotAvailable() throws OfferProductArrangementInternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
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
        request.getProductArrangement().getPrimaryInvolvedParty().setCustomerIdentifier(null);
    }

    @Given("F424 is not available")
    public void givenF424IsNotAvailable() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaccRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("LTB"), "aaa");
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());
        mockScenarioHelper.expectRpcProducts(request.getHeader());
        mockScenarioHelper.expectLookupDataForX711Decline("LTB");
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());
        mockScenarioHelper.expectSwitchDtoFromSwitchName("SW_EnblMCS", "LTB", 3, "0");
    }

    @Given("F424 return external service error code")
    public void givenF424ReturnExternalServiceErrorCode() {
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaccRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("LTB"), "aaa");
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());
        mockScenarioHelper.expectLookupDataForX711Decline("LTB");
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());
        mockScenarioHelper.expectSwitchDtoFromSwitchName("SW_EnblMCS", "LTB", 3, "0");
        mockScenarioHelper.expectF424CallWithExternalServiceError(dataHelper.createOpaccRequestHeader("777505"), "1", dataHelper.createReferralCodeList("code", "description"));
    }

    @Given("F424 return external business error code")
    public void givenF424ReturnExternalBusinessErrorCode() {
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaccRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("LTB"), "aaa");
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());
        mockScenarioHelper.expectLookupDataForX711Decline("LTB");
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());
        mockScenarioHelper.expectSwitchDtoFromSwitchName("SW_EnblMCS", "LTB", 3, "0");
        mockScenarioHelper.expectF424CallWithExternalBusinessError(dataHelper.createOpaccRequestHeader("777505"), "1", dataHelper.createReferralCodeList("code", "description"));
    }

    @When("UI calls OPACC with valid request")
    public void whenUICallsOPACCWithValidRequest() {
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");

        mockControl.go();
        try {
            response = opaccClient.offerProductArrangement(request);
        } catch (Exception e) {
            throwable = e;
        }
    }

    @Then("OPACC throws OfferProductArrangementResourceNotAvailableErrorMsg In response")
    public void thenOPACCThrowsOfferProductArrangementResourceNotAvailableErrorMsgInResponse() {
        assertTrue(throwable instanceof OfferProductArrangementResourceNotAvailableErrorMsg);
    }

    @Then("OPACC throws OfferProductArrangementInternalServiceErrorMsg In response")
    public void thenOPACCThrowsOfferProductArrangementInternalServiceErrorMsgInResponse() {
        assertTrue(throwable instanceof OfferProductArrangementInternalServiceErrorMsg);
    }

    @Then("OPACC throws OfferProductArrangementExternalBusinessErrorMsg In response")
    public void thenOPACCThrowsOfferProductArrangementExternalBusinessErrorMsgInResponse() {
        assertTrue(throwable instanceof OfferProductArrangementExternalBusinessErrorMsg);
    }

    @Then("OPACC throws OfferProductArrangementExternalServiceErrorMsg In response")
    public void thenOPACCThrowsOfferProductArrangementExternalServiceErrorMsgInResponse() {
        assertTrue(throwable instanceof OfferProductArrangementExternalServiceErrorMsg);
    }
}
