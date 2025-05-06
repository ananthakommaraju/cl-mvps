package com.lloydsbanking.salsa.opapca.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.offer.ApplicantType;
import com.lloydsbanking.salsa.opapca.service.utility.AppSubStatus;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.RuleCondition;
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
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Category({AcceptanceTest.class})
public class Story16OpapcaChecksSiraStatusTest extends AbstractOpapcaJBehaveTestBase {

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;
    int applicationParameterValueSize;
    long applicationId = 0l;
    String applicationStatus;

    @BeforeScenario
    public void resetResponse() {
        request = null;
        response = null;
        applicationId = 0;
        applicationStatus = null;
        applicationParameterValueSize = 0;
    }

    @Given("Sira decision is accept")
    public void givenSiraDecisionIsAccept() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
            request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        RuleCondition ruleCondition=new RuleCondition();
        ruleCondition.setName("INTEND_TO_SWITCH");
        request.getProductArrangement().getConditions().add(ruleCondition);
            Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
            customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());
            customer.getIsPlayedBy().setCustomerDeviceDetails(dataHelper.createCustomerDeviceDetails());
            mockScenarioHelper.expectRpcEmptyProductListResponse(request.getHeader());
            request.getProductArrangement().setSIRAEnabledSwitch(true);
            mockScenarioHelper.expectLookupDataForSira(request.getHeader().getChannelId());
        mockScenarioHelper.expectLookUpValuesWithISOCode();
            mockScenarioHelper.expectSiraCall(new BigInteger("0"), (DepositArrangement) request.getProductArrangement(),request.getHeader());

    }
    @Given("EIDV Score is accept")
    public void givenEIDVScoreIsAccept() throws OfferProductArrangementInternalServiceErrorMsg, ParseException, OfferProductArrangementDataNotAvailableErrorMsg, DatatypeConfigurationException, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {

        mockScenarioHelper.expectLookupDataForX711("LTB");

        mockScenarioHelper.expectX711Call(request.getProductArrangement().getPrimaryInvolvedParty(), request.getHeader());
        mockScenarioHelper.expectEligibilityCa("true", dataHelper.createOpaPcaRequestHeader("LTB"), false, 1);
    }

    @Given("ASM decision is accept")
    public void givenASMDecisionIsAccept() throws ParseException, DatatypeConfigurationException {
        mockScenarioHelper.expectF204Call(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
        mockScenarioHelper.expectF205Call(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code1", "description1"), 8);
    }

    @Given("Sira decision is refer Fraud")
    public void givenSiraDecisionIsReferFraud() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
            request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        RuleCondition ruleCondition=new RuleCondition();
        ruleCondition.setName("INTEND_TO_SWITCH");
        request.getProductArrangement().getConditions().add(ruleCondition);
            Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
            customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());
            customer.getIsPlayedBy().setCustomerDeviceDetails(dataHelper.createCustomerDeviceDetails());
            mockScenarioHelper.expectRpcEmptyProductListResponse(request.getHeader());
            request.getProductArrangement().setSIRAEnabledSwitch(true);
            mockScenarioHelper.expectLookupDataForSira(request.getHeader().getChannelId());
        mockScenarioHelper.expectLookUpValuesWithISOCode();
            mockScenarioHelper.expectSiraCall(new BigInteger("21"), (DepositArrangement) request.getProductArrangement(),request.getHeader());

    }
    @Given("Sira decision is refer IDV")
    public void givenSiraDecisionIsReferIDV() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        RuleCondition ruleCondition=new RuleCondition();
        ruleCondition.setName("INTEND_TO_SWITCH");
        request.getProductArrangement().getConditions().add(ruleCondition);
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());
        customer.getIsPlayedBy().setCustomerDeviceDetails(dataHelper.createCustomerDeviceDetails());
        mockScenarioHelper.expectRpcEmptyProductListResponse(request.getHeader());
        request.getProductArrangement().setSIRAEnabledSwitch(true);
        mockScenarioHelper.expectLookupDataForSira(request.getHeader().getChannelId());
        mockScenarioHelper.expectLookUpValuesWithISOCode();
        mockScenarioHelper.expectSiraCall(new BigInteger("422"), (DepositArrangement) request.getProductArrangement(),request.getHeader());
    }

    @Then("OPAPCA returns application status and total rule score as refer idv for the customer")
    public void thenOPAPCAReturnsApplicationStatusAndTotalRuleScoreAsReferIdvForTheCustomer() {
        assertEquals("1002", response.getProductArrangement().getApplicationStatus());
       assertNull(response.getProductArrangement().getApplicationSubStatus());

    }
    @Given("Sira decision is decline")
    public void givenSiraDecisionIsDecline() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        RuleCondition ruleCondition=new RuleCondition();
        ruleCondition.setName("INTEND_TO_SWITCH");
        request.getProductArrangement().getConditions().add(ruleCondition);
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());
        customer.getIsPlayedBy().setCustomerDeviceDetails(dataHelper.createCustomerDeviceDetails());
        mockScenarioHelper.expectRpcEmptyProductListResponse(request.getHeader());
        request.getProductArrangement().setSIRAEnabledSwitch(true);
        mockScenarioHelper.expectLookupDataForSira(request.getHeader().getChannelId());
        mockScenarioHelper.expectLookUpValuesWithISOCode();
        mockScenarioHelper.expectSiraCall(new BigInteger("124422"), (DepositArrangement) request.getProductArrangement(),request.getHeader());
    }
    @Then("OPAPCA returns application status and total rule score as decline for the customer")
    public void thenOPAPCAReturnsApplicationStatusAndTotalRuleScoreAsDeclineForTheCustomer() {
        assertEquals("1004", response.getProductArrangement().getApplicationStatus());
        assertEquals(AppSubStatus.SIRA_DECLINE.getValue(), response.getProductArrangement().getApplicationSubStatus());

    }
    @Then("OPAPCA returns application status and total rule score as refer fraud for the customer")
    public void thenOPAPCAReturnsApplicationStatusAndTotalRuleScoreAsReferFraudForTheCustomer() {
        assertEquals("1003", response.getProductArrangement().getApplicationStatus());
        assertEquals(AppSubStatus.SIRA_REFER.getValue(), response.getProductArrangement().getApplicationSubStatus());
    }

    @When("UI calls OPAPCA with valid request")
    public void whenUICallsOPAPCAWithValidRequest() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, ParseException {
       mockScenarioHelper.expectF447Call(dataHelper.createOpaPcaRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaPcaRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaPcaRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");

        List<String> groupCodeList = new ArrayList<>();
        groupCodeList.add("CC_CROSS_SELL_FC");
        mockScenarioHelper.expectLookupListFromChannelAndGroupCodeList("LTB", groupCodeList);

        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");

        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());
        mockScenarioHelper.expectF062Call("CA", false, request.getProductArrangement().getPrimaryInvolvedParty(), dataHelper.createOpaPcaRequestHeader("LTB"), null, null, 0);
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockControl.go();
        response = opaPcaClient.offerProductArrangement(request);
    }

    @Then("OPAPCA returns application status and total rule score as accept for the customer")
    public void thenOPAPCAReturnsApplicationStatusAndTotalRuleScoreAsAcceptForTheCustomer() {
        assertEquals("1002", response.getProductArrangement().getApplicationStatus());
        assertNull(response.getProductArrangement().getApplicationSubStatus());
    }
    @Given("ASM decision is refer")
    public void givenASMDecisionIsRefer() throws ParseException, DatatypeConfigurationException {
        mockScenarioHelper.expectF204Call(dataHelper.createOpaPcaRequestHeader("LTB"), "2", dataHelper.createReferralCodeList("code", "description"));
        mockScenarioHelper.expectF205Call(dataHelper.createOpaPcaRequestHeader("LTB"), "2", dataHelper.createReferralCodeList("code1", "description1"), 8);
    }
    @Then("OPAPCA returns application status and application sub status as Sira and Asm refer for the customer")
    public void thenOPAPCAReturnsApplicationStatusAndApplicationSubStatusAsSiraAndAsmReferForTheCustomer() {
        assertEquals("1003", response.getProductArrangement().getApplicationStatus());
        assertEquals(AppSubStatus.SIRA_AND_ASM_REFER.getValue(),response.getProductArrangement().getApplicationSubStatus());
    }
    @Then("OPAPCA returns application status and application sub status as Asm refer for the customer")
    public void thenOPAPCAReturnsApplicationStatusAndApplicationSubStatusAsAsmReferForTheCustomer() {
        assertEquals("1003", response.getProductArrangement().getApplicationStatus());
        assertEquals(AppSubStatus.ASM_REFER.getValue(),response.getProductArrangement().getApplicationSubStatus());
    }
    @Then("OPAPCA returns application status and application sub status as Sira decline for the customer")
    public void thenOPAPCAReturnsApplicationStatusAndApplicationSubStatusAsSiraDeclineForTheCustomer() {
        assertEquals("1004", response.getProductArrangement().getApplicationStatus());
        assertEquals(AppSubStatus.SIRA_DECLINE.getValue(),response.getProductArrangement().getApplicationSubStatus());
    }

    @Given("EIDV Score is refer idv")
    public void givenEIDVScoreIsReferIdv() throws DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");

        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaPcaRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaPcaRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061CallWithoutEvidenceData(dataHelper.createOpaPcaRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectLookupDataForX711("LTB");
        mockScenarioHelper.expectLookupDataForX711Refer("LTB");
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        mockScenarioHelper.expectEligibilityCa("true", dataHelper.createOpaPcaRequestHeader("LTB"), false, 1);
        //mockScenarioHelper.expectX711CallEidvRefer(customer, request.getHeader());

    }

    @Then("OPAPCA returns application status and application sub status as EIDV refer for the customer")
    public void thenOPAPCAReturnsApplicationStatusAndApplicationSubStatusAsEIDVReferForTheCustomer() {
        assertEquals("1007", response.getProductArrangement().getApplicationStatus());
        assertEquals(AppSubStatus.EIDV_REFER.getValue(),response.getProductArrangement().getApplicationSubStatus());

    }
    @Given("ASM decision is decline")
    public void givenASMDecisionIsDecline() throws ParseException, DatatypeConfigurationException {
        mockScenarioHelper.expectF204Call(dataHelper.createOpaPcaRequestHeader("LTB"), "3", dataHelper.createReferralCodeList("625", "description"));
        List<String> lookUpText = new ArrayList<>();
        lookUpText.add(0, "625");
        mockScenarioHelper.expectLookupListFromGroupCodeAndChannelAndLookUpText("ASM_DECLINE_CODE", "LTB", lookUpText);
    }

    @Then("OPAPCA returns application status and application sub status as ASM decline for the customer")
    public void thenOPAPCAReturnsApplicationStatusAndApplicationSubStatusAsASMDeclineForTheCustomer() {
        assertEquals("1004", response.getProductArrangement().getApplicationStatus());
        assertEquals(AppSubStatus.ASM_DECLINE.getValue(),response.getProductArrangement().getApplicationSubStatus());

    }


    @Then("OPAPCA returns application status and application sub status as ASM refer for the customer")
    public void thenOPAPCAReturnsApplicationStatusAndApplicationSubStatusAsASMReferForTheCustomer() {
        assertEquals("1003", response.getProductArrangement().getApplicationStatus());
        assertEquals(AppSubStatus.ASM_REFER.getValue(), response.getProductArrangement().getApplicationSubStatus());
    }
    @Given("Sira returns error")
    public void givenSiraReturnsError() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        RuleCondition ruleCondition=new RuleCondition();
        ruleCondition.setName("INTEND_TO_SWITCH");
        request.getProductArrangement().getConditions().add(ruleCondition);
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());
        customer.getIsPlayedBy().setCustomerDeviceDetails(dataHelper.createCustomerDeviceDetails());
        mockScenarioHelper.expectRpcEmptyProductListResponse(request.getHeader());
        request.getProductArrangement().setSIRAEnabledSwitch(true);
        mockScenarioHelper.expectLookupDataForSira(request.getHeader().getChannelId());
        mockScenarioHelper.expectLookUpValuesWithISOCode();
        mockScenarioHelper.expectSiraCallWithError(new BigInteger("0"), (DepositArrangement) request.getProductArrangement(),request.getHeader());
    }

    @Then("OPAPCA returns application status and application sub status as null and fault code in the response")
    public void thenOPAPCAReturnsApplicationStatusAndApplicationSubStatusAsNullAndFaultCodeInTheResponse() {
        assertEquals("1002", response.getProductArrangement().getApplicationStatus());
        assertNull(response.getProductArrangement().getApplicationSubStatus());
        assertEquals("E50035",response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(2).getCustomerDecision().getErrorReasonCode());
    }

}
