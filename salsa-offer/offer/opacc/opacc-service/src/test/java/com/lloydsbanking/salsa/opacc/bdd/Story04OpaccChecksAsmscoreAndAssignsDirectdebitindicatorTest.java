package com.lloydsbanking.salsa.opacc.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.offer.ApplicantType;
import lib_sim_bo.businessobjects.Customer;
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

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story04OpaccChecksAsmscoreAndAssignsDirectdebitindicatorTest extends AbstractOpaccJBehaveTestBase {

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;
    String experian;

    @BeforeScenario
    public void resetResponse() {
        request = null;
        response = null;
    }

    @Given("Credit Decision is accept and Direct Debit is required")
    public void givenCreditDecisionIsAcceptAndDirectDebitIsRequired() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaccRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaccRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("LTB"), "aaa");
        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeader("LTB"), "1", dataHelper.createReferralCodeListForDirectDebitRequiredAccept("code", "description"));
        mockScenarioHelper.expectRpcProducts(request.getHeader());
    }


    @Given("Credit Decision is accept and Direct Debit is not required")
    public void givenCreditDecisionIsAcceptAndDirectDebitIsNotRequired() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaccRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaccRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("LTB"), "aaa");
        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
        mockScenarioHelper.expectRpcProducts(request.getHeader());
    }

    @Given("Credit Decision is refer and Direct Debit is not required and referral code is $isExperianAvailable1")
    public void givenCreditDecisionIsReferAndDirectDebitIsNotRequiredAndReferralCodeIsIsExperianAvailable(String isExperianAvailable1) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaccRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaccRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("LTB"), "aaa");
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
        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaccRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaccRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("LTB"), "aaa");
        if (isExperianAvailable2.equals("501")) {
            mockScenarioHelper.expectRpcProductsForUnscored(request.getHeader());
        } else {
            mockScenarioHelper.expectRpcProducts(request.getHeader());
        }
        experian = isExperianAvailable2;
        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeader("LTB"), "2", dataHelper.createReferralCodeListForDirectDebitRequiredRefer(isExperianAvailable2, "description"));
    }

    @Given("Credit Decision is decline")
    public void givenCreditDecisionIsDecline() {
        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaccRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaccRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("LTB"), "aaa");
        List<String> lookUpText = new ArrayList<>();
        lookUpText.add(0, "Cnt_Pnt_Prtflio");
        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeader("LTB"), "3", dataHelper.createReferralCodeList("Cnt_Pnt_Prtflio", "description"));
        mockScenarioHelper.expectLookupListFromGroupCodeAndChannelAndLookUpText("ASM_DECLINE_CODE", "LTB", lookUpText);
    }

    @When("UI calls OPACC with valid request")
    public void whenUICallsOPACCWithValidRequest() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg {
        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());
        mockScenarioHelper.expectSwitchDtoFromSwitchName("SW_EnblMCS", "LTB", 3, "0");
        mockScenarioHelper.expectLookupDataForX711("LTB");
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());
        mockScenarioHelper.expectX711Call(customer, request.getHeader());

        mockControl.go();
        response = opaccClient.offerProductArrangement(request);
    }


    @Then("OPACC returns application status and asm score as accept")
    public void thenOPACCReturnsApplicationStatusAndAsmScoreAsAccept() {
        assertEquals("1002", response.getProductArrangement().getApplicationStatus());
        assertEquals("1", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getScoreResult());
        assertEquals("ASM", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getAssessmentType());
    }

    @Then("OPACC returns application status and asm score as refer")
    public void thenOPACCReturnsApplicationStatusAndAsmScoreAsRefer() {
        assertEquals("1003", response.getProductArrangement().getApplicationStatus());
        assertEquals("2", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getScoreResult());
        assertEquals("ASM", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getAssessmentType());
    }

    @Then("OPACC returns application status and asm score as decline")
    public void thenOPACCReturnsApplicationStatusAndAsmScoreAsDecline() {
        assertEquals("1004", response.getProductArrangement().getApplicationStatus());
        assertEquals("3", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getScoreResult());
        assertEquals("ASM", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getAssessmentType());
    }


    @Then("OPACC returns application status and asm score as unscored")
    public void thenOPACCReturnsApplicationStatusAndAsmScoreAsUnscored() {
        assertEquals("1005", response.getProductArrangement().getApplicationStatus());
        assertEquals("2", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getScoreResult());
        assertEquals("ASM", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getAssessmentType());
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
