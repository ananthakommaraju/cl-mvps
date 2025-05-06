package com.lloydsbanking.salsa.opasaving.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.offer.ApplicantType;
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
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story10OpasavingCreatesParentRecordInPamTest extends AbstractOpasavingJBehaveTestBase {

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;
    int individualsSize = 0;
    int applicationsSize = 0;
    int appParamSize = 0;
    int partyApplicationSize = 0;
    int appFeaturesSize = 0;
    long applicationId = 0l;
    HashMap<String,Long> appDetails = new HashMap<>();
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
    public void givenArrangementIdExistInPAM() throws ParseException, DatatypeConfigurationException, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");

        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaSavingRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaSavingRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaSavingRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectEligibility("true", dataHelper.createOpaSavingRequestHeader("LTB"), false, 1);
        mockScenarioHelper.expectSwitchValueFromSwitchName("SW_SvngCstSegPrc", "LTB", 3, "0");
        appDetails = mockScenarioHelper.expectChildApplication();

        individualsSize = mockScenarioHelper.expectIndividualsCreated();
        applicationsSize = mockScenarioHelper.expectApplicationsUpdated();
        appParamSize = mockScenarioHelper.expectApplicationParametersCreated(appDetails.get("appId"));
        appFeaturesSize = mockScenarioHelper.expectApplicationFeaturesCreated(appDetails.get("appId"));
        partyApplicationSize = mockScenarioHelper.expectPartyApplicationsCreated();

        request.getProductArrangement().setArrangementId(String.valueOf(appDetails.get("appId")));
        request.getProductArrangement().setApplicationStatus("1002");  ///TO BE CHECKED.
        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.GUARDIAN.getValue());
        request.getProductArrangement().getPrimaryInvolvedParty().setRelatedIndividualIdentifier(String.valueOf(appDetails.get("partyId")));
    }

    @When("UI calls OpaSaving with valid request")
    public void whenUICallsOpaSavingWithValidRequest() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg {
        mockControl.go();
        response = opasavingClient.offerProductArrangement(request);
    }

    @Then("OpaSaving return appId and application status in response.")
    public void thenOpaSavingReturnAppIdAndApplicationStatusInResponse() {
        assertNotNull(response);
        assertEquals(individualsSize + 1, mockScenarioHelper.expectIndividualsCreated());
        assertEquals(applicationsSize, mockScenarioHelper.expectApplicationsUpdated());
        assertEquals(appParamSize, mockScenarioHelper.expectApplicationParametersCreated(appDetails.get("appId")));
        //assertEquals(appFeaturesSize, mockScenarioHelper.expectApplicationFeaturesCreated(applicationId));
        //assertEquals(partyApplicationSize + 1, mockScenarioHelper.expectPartyApplicationsCreated());
    }
}
