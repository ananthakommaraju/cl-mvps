package com.lloydsbanking.salsa.opasaving.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.offer.ApplicantType;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story13OpasavingCrossSellTest extends AbstractOpasavingJBehaveTestBase {

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;
    long applicationId = 0l;
    HashMap<String,Long> appDetails = new HashMap<>();
    @Given("related application id present in request")
    public void givenRelatedApplicationIdPresentInRequest() {
        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");
        request.getProductArrangement().setRelatedApplicationId(String.valueOf(mockScenarioHelper.expectRelatedApplicationId()));
        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.SELF.getValue());
        request.getProductArrangement().getAffiliatedetails().addAll(dataHelper.createAffiliateDetailsList());
        appDetails = mockScenarioHelper.expectChildApplication();
        request.getProductArrangement().setArrangementId(String.valueOf(appDetails.get("appId")));
        request.getProductArrangement().setApplicationRelationShipType("20001");
    }

    @Given("related application status is fulfilled")
    public void givenRelatedApplicationStatusIsFulfilled() {
        request.getProductArrangement().setRelatedApplicationStatus(ApplicationStatus.FULFILLED.getValue());
    }

    @Given("related application status is not fulfilled")
    public void givenRelatedApplicationStatusIsNotFulfilled() {
        request.getProductArrangement().setRelatedApplicationStatus(ApplicationStatus.REFERRED.getValue());
    }

    @Given("related application status is not present in request")
    public void givenRelatedApplicationStatusIsNotPresentInRequest() {
        request.getProductArrangement().setRelatedApplicationStatus(null);
    }

    @When("UI calls OpaSaving with valid request")
    public void whenUIcallsOpaSavingWithValidRequest() throws Exception {
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockControl.go();
        response = opasavingClient.offerProductArrangement(request);
    }

    @Then("OpaSaving returns application status as 1002")
    public void thenOpaSavingReturnsApplicationStatusAs1002() {
        assertEquals("1002", response.getProductArrangement().getApplicationStatus());
    }

    @Then("OpaSaving returns application status as 1014")
    public void thenOpaSavingReturnsApplicationStatusAs1014() {
        assertEquals("1014", response.getProductArrangement().getApplicationStatus());
    }

    @Then("OpaSaving returns application status same as request")
    public void thenOpaSavingReturnsApplicationStatusSameAsRequest() {
        assertNull(response.getProductArrangement().getApplicationStatus());
    }
}
