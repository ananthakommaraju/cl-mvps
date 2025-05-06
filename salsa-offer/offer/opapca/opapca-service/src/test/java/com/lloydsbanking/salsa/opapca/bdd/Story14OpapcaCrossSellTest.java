package com.lloydsbanking.salsa.opapca.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story14OpapcaCrossSellTest extends AbstractOpapcaJBehaveTestBase {

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;
    long applicationId = 0l;

    @Given("related application id present in request")
    public void givenRelatedApplicationIdPresentInRequest() {
        request = dataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getProductArrangement().setRelatedApplicationId(String.valueOf(mockScenarioHelper.expectRelatedapplicationId()));
        request.getProductArrangement().setApplicationRelationShipType("20001");
        request.getProductArrangement().getAffiliatedetails().addAll(dataHelper.createAffiliateDetailsList());
        applicationId = mockScenarioHelper.expectChildApplication();
        request.getProductArrangement().setRelatedApplicationId(String.valueOf(applicationId));
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

    @When("UI calls OPAPCA with valid request")
    public void whenUIcallsOPAPCAWithValidRequest() throws Exception {
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockControl.go();
        response = opaPcaClient.offerProductArrangement(request);
    }

    @Then("OPAPCA returns application status as 1002")
    public void thenOPAPCAReturnsApplicationStatusAs1002() {
        assertEquals("1002", response.getProductArrangement().getApplicationStatus());
    }

    @Then("OPAPCA returns application status as 1014")
    public void thenOPAPCAReturnsApplicationStatusAs1014() {
        assertEquals("1014", response.getProductArrangement().getApplicationStatus());
    }

    @Then("OPAPCA returns application status as null")
    public void thenOPAPCAReturnsApplicationStatusAsNull() {
        assertNull(response.getProductArrangement().getApplicationStatus());
    }
}
