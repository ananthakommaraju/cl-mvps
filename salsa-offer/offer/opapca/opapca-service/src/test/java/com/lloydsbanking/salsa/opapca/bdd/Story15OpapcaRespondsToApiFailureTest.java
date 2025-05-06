package com.lloydsbanking.salsa.opapca.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
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

import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class})
public class Story15OpapcaRespondsToApiFailureTest extends AbstractOpapcaJBehaveTestBase {

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
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getHeader().setContactPointId("0000777505");
    }

    @Given("OCIS F336 is not available")
    public void givenOCISF336IsNotAvailable() {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getHeader().setContactPointId("0000777505");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaPcaRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
    }

    @Given("OCIS F061 is not available")
    public void givenOCISF061IsNotAvailable() {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getHeader().setContactPointId("0000777505");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaPcaRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaPcaRequestHeader("LTB"), 1, 2, "12345");
    }

    @Given("DECI is not available")
    public void givenDECIIsNotAvailable() {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getHeader().setContactPointId("0000777505");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaPcaRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaPcaRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaPcaRequestHeader("LTB"), "12345");
    }

    @Given("F204 is not available")
    public void givenF204IsNotAvailable() throws DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getHeader().setContactPointId("0000777505");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaPcaRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaPcaRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaPcaRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectEligibilityCa("true", dataHelper.createOpaPcaRequestHeader("LTB"), false, 1);
    }

    @Given("F205 is not available")
    public void givenF205IsNotAvailable() throws DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, ParseException, DatatypeConfigurationException {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getHeader().setContactPointId("0000777505");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaPcaRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaPcaRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaPcaRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectEligibilityCa("true", dataHelper.createOpaPcaRequestHeader("LTB"), false, 1);
        mockScenarioHelper.expectF204Call(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
    }

    @Given("RPC is not available")
    public void givenRPCIsNotAvailable() throws DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, ParseException, DatatypeConfigurationException, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getHeader().setContactPointId("0000777505");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaPcaRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaPcaRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaPcaRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectEligibilityCa("true", dataHelper.createOpaPcaRequestHeader("LTB"), false, 1);
        mockScenarioHelper.expectF204Call(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
        mockScenarioHelper.expectF205Call(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code1", "description1"), 8);
        List<String> groupCodeList = new ArrayList<>();
        groupCodeList.add("CC_CROSS_SELL_FC");
        mockScenarioHelper.expectLookupListFromChannelAndGroupCodeList("LTB", groupCodeList);
    }

    @Given("F062 is not available")
    public void givenF062IsNotAvailable() throws DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, ParseException, DatatypeConfigurationException, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getHeader().setContactPointId("0000777505");
        mockScenarioHelper.expectF447CallForNewCustomer(dataHelper.createOpaPcaRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectEligibilityCa("true", dataHelper.createOpaPcaRequestHeader("LTB"), false, 1);
    }

    @Given("X711 is not available")
    public void givenX711IsNotAvailable() throws DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, ParseException, DatatypeConfigurationException, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getHeader().setContactPointId("0000777505");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaPcaRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaPcaRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061CallWithoutEvidenceData(dataHelper.createOpaPcaRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectEligibilityCa("true", dataHelper.createOpaPcaRequestHeader("LTB"), true, 1);
    }

    @Given("F447 returns error code in response")
    public void givenF447ReturnsErrorCodeInResponse() throws DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, ParseException, DatatypeConfigurationException, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getHeader().setContactPointId("0000777505");
        mockScenarioHelper.expectF447CallWithError(dataHelper.createOpaPcaRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
    }

    @Given("F336 returns error code in response")
    public void givenF336ReturnsErrorCodeInResponse() throws DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getHeader().setContactPointId("0000777505");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaPcaRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336CallWithError(dataHelper.createOpaPcaRequestHeader("LTB"), "12345");
    }

    @Given("F061 returns error code in response")
    public void givenF061ReturnsErrorCodeInResponse() throws DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getHeader().setContactPointId("0000777505");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaPcaRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaPcaRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061CallWithError(dataHelper.createOpaPcaRequestHeader("LTB"), "12345");
    }

    @Given("F061 returns improper response")
    public void givenF061ReturnsImproperResponse() throws DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getHeader().setContactPointId("0000777505");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaPcaRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaPcaRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061CallWithImproperResponse(dataHelper.createOpaPcaRequestHeader("LTB"), "12345");
    }

    @Given("F204 returns external business error code in response")
    public void givenF204ReturnsExternalBusinessErrorCodeInResponse() throws DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, ParseException, DatatypeConfigurationException {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getHeader().setContactPointId("0000777505");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaPcaRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaPcaRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaPcaRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectEligibilityCa("true", dataHelper.createOpaPcaRequestHeader("LTB"), false, 1);
        mockScenarioHelper.expectF204CallWithExternalBusinessError(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
    }

    @Given("F204 returns external service error code in response")
    public void givenF204ReturnsExternalServiceErrorCodeInResponse() throws DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getHeader().setContactPointId("0000777505");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaPcaRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaPcaRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaPcaRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectEligibilityCa("true", dataHelper.createOpaPcaRequestHeader("LTB"), false, 1);
        mockScenarioHelper.expectF204CallWithExternalServiceError(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
    }

    @Given("F205 returns external service error code in response")
    public void givenF205ReturnsExternalServiceErrorCodeInResponse() throws DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, ParseException, DatatypeConfigurationException {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getHeader().setContactPointId("0000777505");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaPcaRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaPcaRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaPcaRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectEligibilityCa("true", dataHelper.createOpaPcaRequestHeader("LTB"), false, 1);
        mockScenarioHelper.expectF204Call(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
        mockScenarioHelper.expectF205CallWithExternalServiceError(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code1", "description1"), 8);
    }

    @Given("F205 returns external business error code in response")
    public void givenF205ReturnsExternalBusinessErrorCodeInResponse() throws DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, ParseException, DatatypeConfigurationException {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getHeader().setContactPointId("0000777505");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaPcaRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaPcaRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaPcaRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectEligibilityCa("true", dataHelper.createOpaPcaRequestHeader("LTB"), false, 1);
        mockScenarioHelper.expectF204Call(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
        mockScenarioHelper.expectF205CallWithExternalBusinessError(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code1", "description1"), 8);
    }

    @Given("F205 returns internal service error code in response")
    public void givenF205ReturnsInternalServiceErrorCodeInResponse() throws DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, ParseException, DatatypeConfigurationException {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getHeader().setContactPointId("0000777505");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaPcaRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaPcaRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaPcaRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectEligibilityCa("true", dataHelper.createOpaPcaRequestHeader("LTB"), false, 1);
        mockScenarioHelper.expectF204Call(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
        mockScenarioHelper.expectF205CallWithInternalServiceError(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code1", "description1"), 8);
    }

    @When("UI calls OPAPCA with valid request")
    public void whenUICallsOPAPCAWithValidRequest() {
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockControl.go();
        try {
            response = opaPcaClient.offerProductArrangement(request);
        } catch (Exception e) {
            throwable = e;
        }
    }

    @Then("OPAPCA throws OfferProductArrangementResourceNotAvailableErrorMsg In response")
    public void thenOPAPCAThrowsOfferProductArrangementResourceNotAvailableErrorMsgInResponse() {
        assertTrue(throwable instanceof OfferProductArrangementResourceNotAvailableErrorMsg);
    }

    @Then("OPAPCA throws OfferProductArrangementInternalServiceErrorMsg In response")
    public void thenOPAPCAThrowsOfferProductArrangementInternalServiceErrorMsgInResponse() {
        assertTrue(throwable instanceof OfferProductArrangementInternalServiceErrorMsg);
    }

    @Then("OPAPCA throws OfferProductArrangementExternalBusinessErrorMsg In response")
    public void thenOPAPCAThrowsOfferProductArrangementExternalBusinessErrorMsgInResponse() {
        assertTrue(throwable instanceof OfferProductArrangementExternalBusinessErrorMsg);
    }

    @Then("OPAPCA throws OfferProductArrangementExternalServiceErrorMsg In response")
    public void thenOPAPCAThrowsOfferProductArrangementExternalServiceErrorMsgInResponse() {
        assertTrue(throwable instanceof OfferProductArrangementExternalServiceErrorMsg);
    }
}
