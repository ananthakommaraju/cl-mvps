package com.lloydsbanking.salsa.opacc.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.offer.ApplicantType;
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

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story07OpaccSetsMemorableinfoInRequestTest extends AbstractOpaccJBehaveTestBase {

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;

    @BeforeScenario
    public void resetResponse() {
        request = null;
        response = null;
    }

    @Given("Memorable info is present in request")
    public void givenMemorableInfoIsPresentInRequest() {
        mockScenarioHelper.expectF447Call(dataHelper.createOpaccRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaccRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");

        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("LTB"), "aaa");
        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("Cnt_Pnt_Prtflio", "description"));
    }

    @Given("Memorable info is not present in request")
    public void givenMemorableInfoIsNotPresentInRequest() {
        mockScenarioHelper.expectF447Call(dataHelper.createOpaccRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaccRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");

        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        request.getProductArrangement().getPrimaryInvolvedParty().setAccessToken(null);
        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("Cnt_Pnt_Prtflio", "description"));
    }

    @When("UI calls OPACC with valid request")
    public void whenUICallsOPACCWithValidRequest() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectSwitchDtoFromSwitchName("SW_EnblMCS", "LTB", 3, "0");


        mockScenarioHelper.expectLookupDataForX711Decline("LTB");
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());
        mockScenarioHelper.expectX711Call(customer, request.getHeader());

        mockScenarioHelper.expectRpcProducts(request.getHeader());
        mockControl.go();
        response = opaccClient.offerProductArrangement(request);
    }

    @Then("OPACC returns memorable info")
    public void thenOPACCReturnsMemorableInfo() {
        assertEquals(dataHelper.MEMORABLE_INFO, response.getProductArrangement().getPrimaryInvolvedParty().getAccessToken().getMemorableInfo());
    }

    @Then("OPACC returns memorable info as null")
    public void thenOPACCReturnsMemorableInfoAsNull() {
        assertNull(response.getProductArrangement().getPrimaryInvolvedParty().getAccessToken());
    }


}
