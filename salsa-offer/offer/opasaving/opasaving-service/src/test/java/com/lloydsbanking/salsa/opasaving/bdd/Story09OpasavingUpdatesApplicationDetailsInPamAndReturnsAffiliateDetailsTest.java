package com.lloydsbanking.salsa.opasaving.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.offer.ApplicantType;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story09OpasavingUpdatesApplicationDetailsInPamAndReturnsAffiliateDetailsTest extends AbstractOpasavingJBehaveTestBase {

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;

    @Autowired
    Environment environment;

    @Given("applicant type is not guardian")
    public void givenCustomerApplicantTypeIsNotGuardian() {
        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");
        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());
        request.getProductArrangement().getAffiliatedetails().addAll(dataHelper.createAffiliateDetailsList());
    }

    @When("UI calls OpaSaving with valid request")
    public void whenUICallsOpaSavingWithValidRequest() throws Exception {
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaSavingRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaSavingRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaSavingRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectEligibility("true", dataHelper.createOpaSavingRequestHeader("LTB"), false, 1);
        mockScenarioHelper.expectSwitchValueFromSwitchName("SW_SvngCstSegPrc", "LTB", 3, "0");
        mockScenarioHelper.expectF204CallForExistingCustomers(dataHelper.createOpaSavingRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
        mockScenarioHelper.expectLookupListFromChannelAndEvidenceList("LTB");

        mockControl.go();
        response = opasavingClient.offerProductArrangement(request);
    }

    @Then("OpaSaving updates application details in PAM and returns affiliate details")
    public void thenOpaSavingUpdatesApplicationDetailsInPAMAndReturnsAffiliateDetails() {
        assertNotNull(response);

        if (!runningSpringProfile("remote-mock-downstream-wps")) {
            assertEquals(dataHelper.createAffiliateDetailsListForUpdate(), response.getProductArrangement().getAffiliatedetails());
        }
    }

    private boolean runningSpringProfile(String profileName) {
        String[] profiles = environment.getActiveProfiles();

        for(String profile : profiles) {
            if(profile.equals(profileName)) {
                return true;
            }
        }

        return false;
    }
}
