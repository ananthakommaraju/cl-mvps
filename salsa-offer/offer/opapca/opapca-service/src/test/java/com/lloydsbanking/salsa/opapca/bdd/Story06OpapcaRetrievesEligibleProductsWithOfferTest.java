package com.lloydsbanking.salsa.opapca.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
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

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class})
public class Story06OpapcaRetrievesEligibleProductsWithOfferTest extends AbstractOpapcaJBehaveTestBase {

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;

    @BeforeScenario
    public void resetResponse() {
        request = null;
        response = null;
    }

    @Given("application status is approved/refer")
    public void givenApplicationStatusIsApprovedrefer() throws ParseException, DatatypeConfigurationException {
        mockScenarioHelper.expectF447Call(dataHelper.createOpaPcaRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaPcaRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaPcaRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");

        mockScenarioHelper.expectF204Call(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
        mockScenarioHelper.expectF205Call(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code1", "description1"), 8);

        List<String> groupCodeList = new ArrayList<>();
        groupCodeList.add("CC_CROSS_SELL_FC");
        mockScenarioHelper.expectLookupListFromChannelAndGroupCodeList("LTB", groupCodeList);

        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
    }

    @Given("offered product list does not have associated product and eligibility is called")
    public void givenOfferedProductListDoesNotHaveAssociatedProductAndEligibilityIsCalled() throws ParseException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        mockScenarioHelper.expectF062Call("CA", false, request.getProductArrangement().getPrimaryInvolvedParty(), dataHelper.createOpaPcaRequestHeader("LTB"), null, null, 0);
        mockScenarioHelper.expectRpc(request.getHeader(), 2003);
        mockScenarioHelper.expectEligibility(1);

    }


    @Given("offered product list contains associated product and other products with higher priority than associated product and eligibility is called")
    public void givenOfferedProductListContainsAssociatedProductAndOtherProductsWithHigherPriorityThanAssociatedProductAndEligibilityIsCalled() throws ParseException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getProductArrangement().getAssociatedProduct().getProductoptions().get(0).setOptionsValue("2");
        mockScenarioHelper.expectF062Call("CA", false, request.getProductArrangement().getPrimaryInvolvedParty(), dataHelper.createOpaPcaRequestHeader("LTB"), null, null, 0);
        mockScenarioHelper.expectRpc(request.getHeader(), 2002);

        mockScenarioHelper.expectEligibility(2);


    }

    @Given("offered product list contains associated product of priority 0")
    public void givenOfferedProductListContainsAssociatedProductOfPriority0() throws ParseException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        mockScenarioHelper.expectF062Call("CA", false, request.getProductArrangement().getPrimaryInvolvedParty(), dataHelper.createOpaPcaRequestHeader("LTB"), null, null, 0);
        mockScenarioHelper.expectRpc(request.getHeader(), 2001);

    }

    @Given("offered product list contains associated product and other products with higher priority than associated product and switch value is zero")
    public void givenOfferedProductListContainsAssociatedProductAndOtherProductsWithHigherPriorityThanAssociatedProductAndSwitchValueIsZero() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, ParseException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        mockScenarioHelper.expectRpc(request.getHeader(), 2005);
        mockScenarioHelper.expectF062Call("CA", false, request.getProductArrangement().getPrimaryInvolvedParty(), dataHelper.createOpaPcaRequestHeader("LTB"), null, null, 0);

    }


    @When("UI calls OPAPCA with valid request")
    public void whenUICallsOPAPCAWithValidRequest() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());

        mockScenarioHelper.expectEligibilityCa("true", dataHelper.createOpaPcaRequestHeader("LTB"), false, 1);
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockScenarioHelper.expectLookupDataForX711("LTB");
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());
        mockScenarioHelper.expectX711Call(customer, request.getHeader());
        mockControl.go();
        response = opaPcaClient.offerProductArrangement(request);
    }


    @Then("OPAPCA returns  eligible products with downsell offer")
    public void thenOPAPCAReturnsEligibleProductsWithDownsellOffer() {
        assertEquals(1, response.getProductArrangement().getOfferedProducts().size());
        assertEquals("2003", response.getProductArrangement().getOfferedProducts().get(0).getProductoffer().get(0).getOfferType());
        assertEquals("11", response.getProductArrangement().getOfferedProducts().get(0).getProductIdentifier());

    }

    @Then("OPAPCA returns  eligible products with upsell offer")
    public void thenOPAPCAReturnsEligibleProductsWithUpsellOffer() {
        assertEquals(3, response.getProductArrangement().getOfferedProducts().size());
        assertEquals("2002", response.getProductArrangement().getOfferedProducts().get(0).getProductoffer().get(0).getOfferType());
        assertEquals("2", response.getProductArrangement().getOfferedProducts().get(0).getProductIdentifier());
        assertEquals("2", response.getProductArrangement().getOfferedProducts().get(1).getProductIdentifier());
        assertEquals("2", response.getProductArrangement().getOfferedProducts().get(2).getProductIdentifier());


    }

    @Then("OPAPCA returns applied product in response")
    public void thenOPAPCAReturnsAppliedProductInResponse() {
        assertEquals(1, response.getProductArrangement().getOfferedProducts().size());
    }

    @Then("OPAPCA does not return any eligible product")
    public void thenOPAPCADoesNotReturnAnyEligibleProduct() {
        assertTrue(response.getProductArrangement().getOfferedProducts().isEmpty());
        assertEquals(0, response.getProductArrangement().getOfferedProducts().size());
    }

}
