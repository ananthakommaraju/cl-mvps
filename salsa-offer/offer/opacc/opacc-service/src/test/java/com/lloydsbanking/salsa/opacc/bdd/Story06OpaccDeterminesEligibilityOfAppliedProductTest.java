package com.lloydsbanking.salsa.opacc.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
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
import org.junit.Ignore;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertEquals;

@Ignore
@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story06OpaccDeterminesEligibilityOfAppliedProductTest extends AbstractOpaccJBehaveTestBase {

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;


    @BeforeScenario
    public void resetResponse() {
        request = null;
        response = null;
    }

    @Given("Customer is unauth customer and customer identifier is not present in request")
    public void givenCustomerIsUnauthCustomerAndCustomerIdentifierIsNotPresentInRequest() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");
        request = dataHelper.generateOfferProductArrangementPCCRequestForUnAuth("LTB");
        mockScenarioHelper.expectRpc(request.getHeader());
        mockScenarioHelper.expectLookupDataForX711Decline("LTB");
        request.getProductArrangement().getPrimaryInvolvedParty().setCustomerIdentifier(null);
        //request.getProductArrangement().getPrimaryInvolvedParty().getPostalAddress().clear();
        mockScenarioHelper.expectF424CallWithAddressDetailsWithResolutionNotPresent(dataHelper.createOpaccRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
    }

    @Given("Customer is unauth customer")
    public void givenCustomerIsUnauthCustomer() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        request = dataHelper.generateOfferProductArrangementPCCRequestForUnAuth("LTB");
        mockScenarioHelper.expectRpc(request.getHeader());
        mockScenarioHelper.expectLookupDataForX711Decline("LTB");
        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
    }

    @Given("Customer is auth customer")
    public void givenCustomerIsAuthCustomer() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        request = dataHelper.generateOfferProductArrangementPCCRequestAuth("LTB");
        mockScenarioHelper.expectRpc(request.getHeader());
        mockScenarioHelper.expectLookupDataForX711Decline("LTB");
        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
    }

    @Given("multi card switch is enabled")
    public void givenMultiCardSwitchIsEnabled() {
        mockScenarioHelper.expectSwitchDtoFromSwitchName("SW_EnblMCS", "LTB", 3, "1");
    }

    @Given("multi card switch is not enabled")
    public void givenMultiCardSwitchIsNotEnabled() {
        mockScenarioHelper.expectSwitchDtoFromSwitchName("SW_EnblMCS", "LTB", 3, "0");
    }

    @Given("eligibility is true")
    public void givenEligibilityIsTrue() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg {
        mockScenarioHelper.expectEligibility("true", dataHelper.createOpaccRequestHeaderForUnAuth("LTB"));
    }

    @Given("eligibility is true for new customer")
    public void givenEligibilityIsTrueForNewCustomer() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg {
        mockScenarioHelper.expectEligibilityForNewCustomer("true", dataHelper.createOpaccRequestHeaderForUnAuth("LTB"));
    }

    @Given("eligibility is false")
    public void givenEligibilityIsFalse() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg {
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectEligibility("false", dataHelper.createOpaccRequestHeader("LTB"));
    }

    @Given("number of credit card holds of same brand is zero")
    public void givenNumberOfCreditCardHoldsOfSameBrandIsZero() {
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");
    }

    @Given("number of credit card holds of same brand is greater than or equal to one")
    public void givenNumberOfCreditCardHoldsOfSameBrandIsGreaterThanOrEqualToOne() {
        mockScenarioHelper.expectF336CallWithProductHoldings(dataHelper.createOpaccRequestHeader("LTB"), 3, 2, "12345");
        request.getProductArrangement().getAssociatedProduct().setProductType("3");
    }

    @Given("application type is not present in request")
    public void givenApplicationTypeIsNotPresentInRequest() {
        request.getProductArrangement().setApplicationType("");
    }

    @Given("application type is present in request")
    public void givenApplicationTypeIsPresentInRequest() {
        request.getProductArrangement().setApplicationType("10003");
    }

    @Given("administerservice returns product eligibility type as co_hold")
    public void givenAdministerserviceReturnsProductEligibilityTypeAsCo_hold() {
        mockScenarioHelper.expectProductFeatureFromProductId((long) 20042);
        mockScenarioHelper.expectExternalSystemProductsPrdData("10106", "3");
        mockScenarioHelper.expectProductEligibilityRulesPrdData("CO_HOLD", "20042", "303");
    }

    @Given("administerservice returns product eligibility type as ineligible")
    public void givenAdministerserviceReturnsProductEligibilityTypeAsIneligible() {
        mockScenarioHelper.expectProductFeatureFromProductId((long) 20042);
        mockScenarioHelper.expectExternalSystemProductsPrdData("10106", "3");
        mockScenarioHelper.expectProductEligibilityRulesPrdData("INELIGIBLE", "20042", "303");
    }

    @When("UI calls OPACC with valid request")
    public void whenUICallsOPACCWithValidRequest() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg {
        mockScenarioHelper.expectF447Call(dataHelper.createOpaccRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());

        mockScenarioHelper.expectF061Call(dataHelper.createOpaccRequestHeader("LTB"), "12345");

        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("LTB"), "aaa");

        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");

        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());
        customer.setCustomerIdentifier("12345");
        mockScenarioHelper.expectX711Call(customer, request.getHeader());
        try {
            request.getHeader().setContactPointId("0000777505");
            request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType("2");
            request.getProductArrangement().getPrimaryInvolvedParty().setCustomerIdentifier(null);
            mockControl.go();
            response = opaccClient.offerProductArrangement(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Then("OPACC returns application type as 10001")
    public void thenOPACCReturnsApplicationTypeAs10001() {
        assertEquals("10001", response.getProductArrangement().getApplicationType());
    }

    @Then("OPACC returns application type as 10002")
    public void thenOPACCReturnsApplicationTypeAs10002() {
        assertEquals("10002", response.getProductArrangement().getApplicationType());
    }

    @Then("OPACC returns application type as ineligible")
    public void thenOPACCReturnsApplicationTypeAsIneligible() {
        assertEquals("INELIGIBLE", response.getProductArrangement().getApplicationType());
    }

    @Then("OPACC returns application type same as request")
    public void thenOPACCReturnsApplicationTypeSameAsRequest() {
        assertEquals("10003", response.getProductArrangement().getApplicationType());
    }
}
