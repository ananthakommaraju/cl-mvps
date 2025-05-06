package com.lloydsbanking.salsa.opacc.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.ProductOffer;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.Ignore;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@Ignore
@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story11OpaccRetrievesProductsOfferedTest extends AbstractOpaccJBehaveTestBase {

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;

    @BeforeScenario
    public void resetResponse() {
        request = null;
        response = null;
    }

    @Given("application status is accept and rpc is called")
    public void givenApplicationStatusIsAcceptAndRpcIsCalled() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg {
        mockScenarioHelper.expectF447Call(dataHelper.createOpaccRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());

        mockScenarioHelper.expectF061Call(dataHelper.createOpaccRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");

        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("LTB"), "aaa");
        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
        request = dataHelper.generateOfferProductArrangementPCCRequestForUnAuth("LTB");
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        mockScenarioHelper.expectLookupDataForX711("LTB");
        mockScenarioHelper.expectX711Call(customer, request.getHeader());

        mockScenarioHelper.expectRpc(request.getHeader());
    }

    @Given("product eligibility type of applied product is CO_HOLD")
    public void givenProductEligibilityTypeOfAppliedProductIsCO_HOLD() throws DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, OfferProductArrangementInternalServiceErrorMsg {
        mockScenarioHelper.expectSwitchDtoFromSwitchName("SW_EnblMCS", "LTB", 3, "1");

        mockScenarioHelper.expectEligibility("true", dataHelper.createOpaccRequestHeaderForUnAuth("LTB"));

        mockScenarioHelper.expectF336CallWithProductHoldings(dataHelper.createOpaccRequestHeader("LTB"), 3, 2, "12345");
        request.getProductArrangement().getAssociatedProduct().setProductType("3");
    }


    @Given("number of offered products with product Eligibility type as CO_HOLD is greater than 0")
    public void givenNumberOfOfferedProductsWithProductEligibilityTypeAsCO_HOLDIsGreaterThan0() {

        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");

        mockScenarioHelper.expectProductFeatureFromProductId((long) 20042);
        mockScenarioHelper.expectExternalSystemProductsPrdData("10106", "3");
        mockScenarioHelper.expectProductEligibilityRulesPrdData("CO_HOLD", "20042", "303");
    }


    @Given("number of offered products with product Eligibility type as CO_HOLD is 0")
    public void givenNumberOfOfferedProductsWithProductEligibilityTypeAsCO_HOLDIs0() {
        mockScenarioHelper.expectF447Call(dataHelper.createOpaccRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());

        mockScenarioHelper.expectF061Call(dataHelper.createOpaccRequestHeader("LTB"), "12345");

        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");

        mockScenarioHelper.expectProductFeatureFromProductIdForIneligibleOfferedProducts((long) 20042);
        mockScenarioHelper.expectExternalSystemProductsPrdData("10106", "3");
        mockScenarioHelper.expectProductEligibilityRulesPrdData("CO_HOLD", "20042", "303");
    }

    @Given("product eligibility type of applied product is not CO_HOLD")
    public void givenProductEligibilityTypeOfAppliedProductIsNotCo_Hold() throws DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, OfferProductArrangementInternalServiceErrorMsg {
        mockScenarioHelper.expectSwitchDtoFromSwitchName("SW_EnblMCS", "LTB", 3, "1");
        mockScenarioHelper.expectEligibility("true", dataHelper.createOpaccRequestHeaderForUnAuth("LTB"));
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");

        request.getProductArrangement().setApplicationType("");
        request.getProductArrangement().getAssociatedProduct().setProductType("3");

        mockScenarioHelper.expectF447Call(dataHelper.createOpaccRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());

        mockScenarioHelper.expectF061Call(dataHelper.createOpaccRequestHeader("LTB"), "12345");

        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
    }

    @Given("application status is unscored and rpc is called")
    public void givenApplicationStatusIsUnscoredAndRpcIsCalled() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("LTB"), "aaa");
        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeader("LTB"), "2", dataHelper.createReferralCodeListForDirectDebitRequiredRefer("501", "description"));

        request = dataHelper.generateOfferProductArrangementPCCRequestForUnAuth("LTB");
        request.getProductArrangement().getAssociatedProduct().getProductoffer().add(1, new ProductOffer());
        request.getProductArrangement().getAssociatedProduct().getProductoffer().get(1).setProdOfferIdentifier("23120");
        mockScenarioHelper.expectRpcWhenApplicationStatusIsUnscored(request.getHeader());
    }

    @When("UI calls OPACC with valid request")
    public void whenUICallsOPACCWithValidRequest() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg {
        request.getHeader().setContactPointId("0000777505");
        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType("02");
        request.getProductArrangement().getPrimaryInvolvedParty().setCustomerIdentifier(null);
        mockControl.go();
        response = opaccClient.offerProductArrangement(request);
    }


    @Then("OPACC returns offered products")
    public void thenOPACCReturnsOfferedProducts() {
        assertEquals(3, response.getProductArrangement().getOfferedProducts().size());
        assertEquals("20051", response.getProductArrangement().getOfferedProducts().get(0).getProductIdentifier());
        assertEquals("20052", response.getProductArrangement().getOfferedProducts().get(1).getProductIdentifier());
        assertEquals("20042", response.getProductArrangement().getOfferedProducts().get(2).getProductIdentifier());
    }

    @Then("application status as accept and application type as 10001")
    public void thenApplicationStatusAsAcceptAndApplicationTypeAs10001() {
        assertEquals("1002", response.getProductArrangement().getApplicationStatus());
        assertEquals("10001", response.getProductArrangement().getApplicationType());
    }


    @Then("OPACC returns associated product as offered products")
    public void thenOPACCReturnsAssociatedProductOfferedProducts() {
        assertEquals(1, response.getProductArrangement().getOfferedProducts().size());
        assertEquals("20042", response.getProductArrangement().getOfferedProducts().get(0).getProductIdentifier());
    }

    @Then("application status as decline and application type as INELIGIBLE")
    public void thenApplicationStatusAsDeclineAndApplicationTypeAsINELIGIBLE() {
        assertEquals("1004", response.getProductArrangement().getApplicationStatus());
        assertEquals("INELIGIBLE", response.getProductArrangement().getApplicationType());
    }

    @Then("OPACC returns offered products from rpc")
    public void thenOPACCReturnsOfferedProductsFromRpc() {
        assertEquals(4, response.getProductArrangement().getOfferedProducts().size());
    }

    @Then("application status as unscored and application type as 10001")
    public void thenApplicationStatusAsUnscoredAndApplicationTypeAs10001() {
        assertEquals("1005", response.getProductArrangement().getApplicationStatus());
        assertEquals("10001", response.getProductArrangement().getApplicationType());
    }

    @Then("offer amount is null")
    public void thenOfferAmountIsNull() {
        assertNull(response.getProductArrangement().getOfferedProducts().get(0).getProductoffer().get(0).getOfferAmount());
        assertNull(response.getProductArrangement().getOfferedProducts().get(1).getProductoffer().get(0).getOfferAmount());
    }

    @Given("application status is decline")
    public void givenApplicationStatusIsDecline() throws OfferProductArrangementInternalServiceErrorMsg {
        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaccRequestHeader("LTB"), "1985856187");
        mockScenarioHelper.expectF336CallWithProductHoldings(dataHelper.createOpaccRequestHeader("LTB"), 3, 2, "12345");
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("LTB"), "aaa");
        List<String> lookUpText = new ArrayList<>();
        lookUpText.add(0, "Cnt_Pnt_Prtflio");
        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeader("LTB"), "3", dataHelper.createReferralCodeList("Cnt_Pnt_Prtflio", "description"));
        mockScenarioHelper.expectLookupListFromGroupCodeAndChannelAndLookUpText("ASM_DECLINE_CODE", "LTB", lookUpText);
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");

        mockScenarioHelper.expectLookupDataForX711("LTB");
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());
        mockScenarioHelper.expectX711Call(customer, request.getHeader());
    }

    @When("UI calls OPACC with valid request for application status decline")
    public void whenUICallsOPACCWithValidRequestForApplicationStatusDecline() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg {
        request.getHeader().setContactPointId("0000777505");
        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType("01");

        mockScenarioHelper.expectSwitchDtoFromSwitchName("SW_EnblMCS", "LTB", 3, "0");
        mockControl.go();
        response = opaccClient.offerProductArrangement(request);
    }


    @Then("OPACC empties the existing products")
    public void thenOPACCEmptiesTheExistingProducts() {
        assertTrue(response.getProductArrangement().getExistingProducts().isEmpty());
        assertEquals(1, response.getProductArrangement().getOfferedProducts().size());
    }

}
