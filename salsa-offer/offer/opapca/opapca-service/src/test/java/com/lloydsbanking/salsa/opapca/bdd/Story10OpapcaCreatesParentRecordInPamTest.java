package com.lloydsbanking.salsa.opapca.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
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

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class})

public class Story10OpapcaCreatesParentRecordInPamTest extends AbstractOpapcaJBehaveTestBase {

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;
    int individualsSize = 0;
    int applicationsSize = 0;
    int appParamSize = 0;
    int partyApplicationSize = 0;
    int appFeaturesSize = 0;
    long applicationId = 0l;

    @BeforeScenario
    public void resetResponse() {
        request = null;
        response = null;
        individualsSize = 0;
        applicationsSize = 0;
        appParamSize = 0;
        partyApplicationSize = 0;
        appFeaturesSize = 0;
        applicationId = 0;
    }

    @Given("arrangement Id exist in PAM")
    public void givenArrangementIdExistInPAM() throws ParseException, DatatypeConfigurationException, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg {

        mockScenarioHelper.expectF447Call(dataHelper.createOpaPcaRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaPcaRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaPcaRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectF204CallForExistingCustomers(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
        mockScenarioHelper.expectF205CallForExistingCustomer(dataHelper.createOpaPcaRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"), 8);
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        Customer customer = dataHelper.createPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        mockScenarioHelper.expectF062Call("CA", false, customer, dataHelper.createOpaPcaRequestHeader("LTB"), "149:009", "148:010", 0);
        mockScenarioHelper.expectLookupListFromChannelAndEvidenceList("LTB");
        List<String> groupCodeList = new ArrayList<>();
        groupCodeList.add("CC_CROSS_SELL_FC");
        mockScenarioHelper.expectLookupListFromChannelAndGroupCodeList("LTB", groupCodeList);
        applicationId = mockScenarioHelper.expectChildApplication();
        request.getHeader().setContactPointId("0000777505");
        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.GUARDIAN.getValue());
        request.getProductArrangement().getPrimaryInvolvedParty().setCustomerIdentifier(null);
        request.getProductArrangement().setApplicationStatus("1002");

        request.getProductArrangement().setArrangementId(String.valueOf(applicationId));
        mockScenarioHelper.expectRpcEmptyProductListResponse(request.getHeader());

        request.getProductArrangement().setApplicationStatus("1002");
        mockScenarioHelper.expectLookupDataForX711("LTB");
        Customer customer1 = request.getProductArrangement().getPrimaryInvolvedParty();
        customer1.setCustomerIdentifier("12345");
        customer1.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());
        mockScenarioHelper.expectX711Call(customer1, request.getHeader());



        individualsSize = mockScenarioHelper.expectIndividualsCreated();
        applicationsSize = mockScenarioHelper.expectApplicationsUpdated();
        appParamSize = mockScenarioHelper.expectApplicationParametersCreated(applicationId);
        appFeaturesSize = mockScenarioHelper.expectApplicationFeaturesCreated(applicationId);
        partyApplicationSize = mockScenarioHelper.expectPartyApplicationsCreated();

    }

    @When("UI calls OPAPCA with valid request")
    public void whenUICallsOPAPCAWithValidRequest() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg {
        mockControl.go();
        response = opaPcaClient.offerProductArrangement(request);

    }

    @Then("OPAPCA return appId and application status in response.")
    public void thenOPAPCAReturnAppIdAndApplicationStatusInResponse() {
        assertNotNull(response);
        assertEquals(individualsSize + 1, mockScenarioHelper.expectIndividualsCreated());
        assertEquals(applicationsSize+1, mockScenarioHelper.expectApplicationsUpdated());
        assertEquals(appParamSize , mockScenarioHelper.expectApplicationParametersCreated(applicationId));
        assertEquals(appFeaturesSize , mockScenarioHelper.expectApplicationFeaturesCreated(applicationId));
        assertEquals(partyApplicationSize + 1, mockScenarioHelper.expectPartyApplicationsCreated());
    }

}
