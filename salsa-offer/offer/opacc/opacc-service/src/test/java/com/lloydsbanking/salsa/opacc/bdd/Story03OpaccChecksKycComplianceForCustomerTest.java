package com.lloydsbanking.salsa.opacc.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
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

import static junit.framework.TestCase.assertEquals;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story03OpaccChecksKycComplianceForCustomerTest extends AbstractOpaccJBehaveTestBase {

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;

    @BeforeScenario
    public void resetResponse() {
        request = null;
        response = null;
    }

    @Given("Customer is identified at OCIS")
    public void givenCustomerIsIdentifiedAtOCIS() throws ParseException, DatatypeConfigurationException {
        mockScenarioHelper.expectF447Call(dataHelper.createOpaccRequestHeaderForUnAuth("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
    }

    @Given("has product holdings")
    public void givenHasProductHoldings() {
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeaderForUnAuth("LTB"), 1, 2, "12345");
    }

    @Given("evidence data is available at OCIS")
    public void givenEvidenceDataIsAvailableAtOCIS() throws ParseException, DatatypeConfigurationException, OfferProductArrangementInternalServiceErrorMsg {
        mockScenarioHelper.expectF061Call(dataHelper.createOpaccRequestHeaderForUnAuth("LTB"), "12345");
        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeaderForUnAuth("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
        mockScenarioHelper.expectLookupDataForX711("LTB");
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());
        mockScenarioHelper.expectX711Call(customer, request.getHeader());
    }

    @Given("evidence data is not available at OCIS")
    public void givenEvidenceDataIsNotAvailableAtOCIS() throws ParseException, DatatypeConfigurationException, OfferProductArrangementInternalServiceErrorMsg {
        mockScenarioHelper.expectF061CallWithoutEvidenceData(dataHelper.createOpaccRequestHeaderForUnAuth("LTB"), "12345");
        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeaderForUnAuth("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
        mockScenarioHelper.expectLookupDataForX711Decline("LTB");
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        mockScenarioHelper.expectX711Call(customer, request.getHeader());
    }

    @When("UI calls OPACC with valid request")
    public void whenUICallsOPACCWithValidRequest() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, ParseException, DatatypeConfigurationException, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeaderForUnAuth("LTB"), "aaa");
        mockScenarioHelper.expectLookupCallForCoHolding("LTB");
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockScenarioHelper.expectSwitchDtoFromSwitchName("SW_EnblMCS", "LTB", 3, "0");
        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        mockScenarioHelper.expectRpcProducts(request.getHeader());
        request.getHeader().setContactPointId("0000777505");
        request.getProductArrangement().getPrimaryInvolvedParty().setCustomerIdentifier(null);

        mockControl.go();
        response = opaccClient.offerProductArrangement(request);
    }

    @Then("OPACC returns EIDV status as accept for the customer")
    public void thenOPACCReturnsEIDVStatusAsAccceptForTheCustomer() {
        assertEquals("ACCEPT", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult());
        assertEquals("EIDV", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
    }



    @Then("OPACC returns EIDV status as decline for the customer")
    public void thenOPACCReturnsEIDVStatusAsDeclineForTheCustomer() {
        assertEquals("DECLINE", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult());
        assertEquals("EIDV", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
    }


}
