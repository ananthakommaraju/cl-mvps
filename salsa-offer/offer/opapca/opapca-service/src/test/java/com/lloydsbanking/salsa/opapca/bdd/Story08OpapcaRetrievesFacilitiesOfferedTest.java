package com.lloydsbanking.salsa.opapca.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
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
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story08OpapcaRetrievesFacilitiesOfferedTest extends AbstractOpapcaJBehaveTestBase {
    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;

    @BeforeScenario
    public void resetResponse() {
        request = null;
        response = null;
    }

    @Given("application status is approved/refer and rpc is called")
    public void givenApplicationStatusIsApprovedreferAndRpcIsCalled() throws ParseException, DatatypeConfigurationException, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg {
        mockScenarioHelper.expectF447Call(dataHelper.createOpaPcaRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaPcaRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaPcaRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockScenarioHelper.expectLookupDataForDuplicateApplication("LTB", "BRAND_COHOLDING");
        mockScenarioHelper.expectF204Call(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        mockScenarioHelper.expectRpcEmptyProductListResponse(request.getHeader());
        List<String> groupCodeList = new ArrayList<>();
        groupCodeList.add("CC_CROSS_SELL_FC");
        mockScenarioHelper.expectLookupListFromChannelAndGroupCodeList("LTB", groupCodeList);

        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectF062Call("CA", false, request.getProductArrangement().getPrimaryInvolvedParty(), dataHelper.createOpaPcaRequestHeader("LTB"), null, null, 0);
    }

    @Given("Credit Score returns option code for check book")
    public void givenCreditScoreReturnsOptionCodeForCheckBookAndValueIsGreaterThan100() throws ParseException {
        mockScenarioHelper.expectF205Call(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code1", "description1"), 1);

    }

    @Given("Credit Score returns option code for overdraft")
    public void givenCreditScoreReturnsOptionCodeForOverdraftAndValueIsLessThan100() throws ParseException {

        mockScenarioHelper.expectF205Call(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code1", "description1"), 2);
    }

    @Given("Credit Score returns option code for credit card")
    public void givenCreditScoreReturnsOptionCodeForCreditCard() throws ParseException {
        mockScenarioHelper.expectF205Call(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code1", "description1"), 3);
    }

    @Given("Credit Score do not return option code for credit card, check book and overdraft")
    public void givenCreditScoreDoNotReturnOptionCodeForCreditCardCheckBookAndOverdraft() throws ParseException {
        mockScenarioHelper.expectF205Call(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code1", "description1"), 0);
    }

    @Given("Credit Score returns option code for check book and overdraft")
    public void givenCreditScoreReturnsOptionCodeForCheckBookAndOverdraft() throws ParseException {
        mockScenarioHelper.expectF205Call(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code1", "description1"), 4);
    }

    @Given("Credit Score returns option code for check book and credit card")
    public void givenCreditScoreReturnsOptionCodeForCheckBookAndCreditCard() throws ParseException {
        mockScenarioHelper.expectF205Call(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code1", "description1"), 5);
    }

    @Given("Credit Score returns option code for credit card and overdraft")
    public void givenCreditScoreReturnsOptionCodeForCreditCardAndOverdraft() throws ParseException {
        mockScenarioHelper.expectF205Call(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code1", "description1"), 6);
    }

    @Given("Credit Score returns option code for check book, overdraft and credit card")
    public void givenCreditScoreReturnsOptionCodeForCheckBookOverdraftAndCreditCard() throws ParseException {
        mockScenarioHelper.expectF205Call(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code1", "description1"), 7);
    }


    @When("UI calls OPAPCA with valid request")
    public void whenUICallsOPAPCAWithValidRequest() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, ParseException, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());

        mockScenarioHelper.expectF062Call("CA", false, request.getProductArrangement().getPrimaryInvolvedParty(), dataHelper.createOpaPcaRequestHeader("LTB"), null, null, 0);

        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockScenarioHelper.expectLookupDataForX711("LTB");
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());
        mockScenarioHelper.expectX711Call(customer, request.getHeader());

        mockScenarioHelper.expectEligibilityCa("true", dataHelper.createOpaPcaRequestHeader("LTB"), false,1);

        mockControl.go();
        response = opaPcaClient.offerProductArrangement(request);
    }


    @Then("OPAPCA returns conditions for check book and debit card")
    public void thenOPAPCAReturnsFacilityForCheckBook() {
        assertEquals(3, response.getProductArrangement().getConditions().size());
        assertEquals("EIDV_REFERRAL_DISABLED_SWITCH", response.getProductArrangement().getConditions().get(0).getName());
        assertEquals("CHECK_BOOK_OFFERED_FLAG", response.getProductArrangement().getConditions().get(1).getName());
        assertEquals("DEBIT_CARD_RISK_CODE", response.getProductArrangement().getConditions().get(2).getName());
        assertEquals("Y", response.getProductArrangement().getConditions().get(1).getResult());
        assertEquals("0000", response.getProductArrangement().getConditions().get(2).getResult());
    }

    @Then("OPAPCA returns conditions for overdraft and debit card")
    public void thenOPAPCAReturnsConditionsForOverdraftAndDebitCard() {
        assertEquals(2, response.getProductArrangement().getConditions().size());
        assertEquals("EIDV_REFERRAL_DISABLED_SWITCH", response.getProductArrangement().getConditions().get(0).getName());
        assertEquals("DEBIT_CARD_RISK_CODE", response.getProductArrangement().getConditions().get(1).getName());
        assertEquals("0000", response.getProductArrangement().getConditions().get(1).getResult());



    }

    @Then("OPAPCA returns conditions for credit card and debit card")
    public void thenOPAPCAReturnsConditionsForCreditCardAndDebitCard() {
        assertEquals(4, response.getProductArrangement().getConditions().size());
        assertEquals("EIDV_REFERRAL_DISABLED_SWITCH", response.getProductArrangement().getConditions().get(0).getName());
        assertEquals("CREDIT_CARD_LIMIT_AMOUNT", response.getProductArrangement().getConditions().get(1).getName());
        assertEquals("CREDIT_CARD_FAMILY_CODE", response.getProductArrangement().getConditions().get(2).getName());
        assertEquals("DEBIT_CARD_RISK_CODE", response.getProductArrangement().getConditions().get(3).getName());

        assertEquals(BigDecimal.valueOf(3), response.getProductArrangement().getConditions().get(1).getValue().getAmount());
        assertEquals("103", response.getProductArrangement().getConditions().get(2).getResult());
        assertEquals("0000", response.getProductArrangement().getConditions().get(3).getResult());

    }

    @Then("OPAPCA returns conditions for debit card")
    public void thenOPAPCAReturnsConditionsDebitCard() {
        assertEquals(2, response.getProductArrangement().getConditions().size());
        assertEquals("EIDV_REFERRAL_DISABLED_SWITCH", response.getProductArrangement().getConditions().get(0).getName());
        assertEquals("DEBIT_CARD_RISK_CODE", response.getProductArrangement().getConditions().get(1).getName());
        assertEquals("50", response.getProductArrangement().getConditions().get(1).getResult());
    }


    @Then("OPAPCA returns conditions for check book, overdraft and debit card")
    public void thenOPAPCAReturnsConditions() {
        assertEquals(3, response.getProductArrangement().getConditions().size());
        assertEquals("EIDV_REFERRAL_DISABLED_SWITCH", response.getProductArrangement().getConditions().get(0).getName());
        assertEquals("CHECK_BOOK_OFFERED_FLAG", response.getProductArrangement().getConditions().get(1).getName());
        assertEquals("DEBIT_CARD_RISK_CODE", response.getProductArrangement().getConditions().get(2).getName());
        assertEquals("Y", response.getProductArrangement().getConditions().get(1).getResult());
        assertEquals("0000", response.getProductArrangement().getConditions().get(2).getResult());


    }


    @Then("OPAPCA returns conditions for check book, credit card and debit card")
    public void thenOPAPCAReturnsConditionsForCheckBookCreditCardAndDebitCard() {
        assertEquals(5, response.getProductArrangement().getConditions().size());
        assertEquals("EIDV_REFERRAL_DISABLED_SWITCH", response.getProductArrangement().getConditions().get(0).getName());
        assertEquals("CHECK_BOOK_OFFERED_FLAG", response.getProductArrangement().getConditions().get(1).getName());
        assertEquals("CREDIT_CARD_LIMIT_AMOUNT", response.getProductArrangement().getConditions().get(2).getName());
        assertEquals("CREDIT_CARD_FAMILY_CODE", response.getProductArrangement().getConditions().get(3).getName());
        assertEquals("DEBIT_CARD_RISK_CODE", response.getProductArrangement().getConditions().get(4).getName());
        assertEquals("Y", response.getProductArrangement().getConditions().get(1).getResult());
        assertEquals(null, response.getProductArrangement().getConditions().get(2).getResult());
        assertEquals("103", response.getProductArrangement().getConditions().get(3).getResult());
        assertEquals("0000", response.getProductArrangement().getConditions().get(4).getResult());

    }


    @Then("OPAPCA returns conditions for credit card, overdraft and debit card")
    public void thenOPAPCAReturnsConditionsForCreditCardOverdraftAndDebitCard() {
        assertEquals(4, response.getProductArrangement().getConditions().size());
        assertEquals("EIDV_REFERRAL_DISABLED_SWITCH", response.getProductArrangement().getConditions().get(0).getName());
        assertEquals("CREDIT_CARD_LIMIT_AMOUNT", response.getProductArrangement().getConditions().get(1).getName());
        assertEquals("CREDIT_CARD_FAMILY_CODE", response.getProductArrangement().getConditions().get(2).getName());
        assertEquals("DEBIT_CARD_RISK_CODE", response.getProductArrangement().getConditions().get(3).getName());

        assertEquals(BigDecimal.valueOf(3), response.getProductArrangement().getConditions().get(1).getValue().getAmount());
        assertEquals("103", response.getProductArrangement().getConditions().get(2).getResult());
        assertEquals("0000", response.getProductArrangement().getConditions().get(3).getResult());

    }

    @Then("OPAPCA returns conditions for check book, overdraft, credit card and debit card")
    public void thenOPAPCAReturnsConditionsForCheckBookOverdraftCreditCardAndDebitCard() {
        assertEquals(5, response.getProductArrangement().getConditions().size());
        assertEquals("EIDV_REFERRAL_DISABLED_SWITCH", response.getProductArrangement().getConditions().get(0).getName());
        assertEquals("CHECK_BOOK_OFFERED_FLAG", response.getProductArrangement().getConditions().get(1).getName());
        assertEquals("CREDIT_CARD_LIMIT_AMOUNT", response.getProductArrangement().getConditions().get(2).getName());
        assertEquals("CREDIT_CARD_FAMILY_CODE", response.getProductArrangement().getConditions().get(3).getName());
        assertEquals("DEBIT_CARD_RISK_CODE", response.getProductArrangement().getConditions().get(4).getName());
        assertEquals("Y", response.getProductArrangement().getConditions().get(1).getResult());
        assertEquals(BigDecimal.valueOf(3), response.getProductArrangement().getConditions().get(2).getValue().getAmount());
        assertEquals("103", response.getProductArrangement().getConditions().get(3).getResult());
        assertEquals("0000", response.getProductArrangement().getConditions().get(4).getResult());

    }

}
