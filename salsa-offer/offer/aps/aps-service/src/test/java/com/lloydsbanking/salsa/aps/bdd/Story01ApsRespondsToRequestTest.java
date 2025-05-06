package com.lloydsbanking.salsa.aps.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import lib_sim_productsalesreferencedatamanager.id_determineproducteligibilitytype.AdministerProductSelectionDataNotAvailableErrorMsg;
import lib_sim_productsalesreferencedatamanager.id_determineproducteligibilitytype.AdministerProductSelectionInternalServiceErrorMsg;
import lib_sim_productsalesreferencedatamanager.id_determineproducteligibilitytype.AdministerProductSelectionResourceNotAvailableErrorMsg;
import lib_sim_productsalesreferencedatamanager.messages.AdministerProductSelectionRequest;
import lib_sim_productsalesreferencedatamanager.messages.AdministerProductSelectionResponse;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story01ApsRespondsToRequestTest extends AbstractApsJBehaveTestBase {

    AdministerProductSelectionRequest request;
    AdministerProductSelectionResponse response;

    @When("UI calls APS with valid request")
    public void whenUICallsAPSWithValidRequest() throws AdministerProductSelectionResourceNotAvailableErrorMsg, AdministerProductSelectionInternalServiceErrorMsg, AdministerProductSelectionDataNotAvailableErrorMsg {
        mockScenarioHelper.expectMaxEligibleFeatureAvailableWithMaxValueTwo();
        mockScenarioHelper.expectDetailsInProductEligibilityRules();
        mockScenarioHelper.expectProductFromExternalSystemProducts();
        request = dataHelper.createAdministerRequestSameTypeProduct();
        mockControl.go();
        response = apsClient.administerProductSelection(request);
    }

    @Then("APS returns valid response")
    public void thenAPSReturnsValidResponse() {
        assertNotNull(response);
        assertEquals("CO_HOLD",response.getProductEligibilityType());
    }


}
