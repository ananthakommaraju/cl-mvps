package com.lloydsbanking.salsa.aps.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import lib_sim_productsalesreferencedatamanager.id_determineproducteligibilitytype.AdministerProductSelectionDataNotAvailableErrorMsg;
import lib_sim_productsalesreferencedatamanager.id_determineproducteligibilitytype.AdministerProductSelectionInternalServiceErrorMsg;
import lib_sim_productsalesreferencedatamanager.id_determineproducteligibilitytype.AdministerProductSelectionResourceNotAvailableErrorMsg;
import lib_sim_productsalesreferencedatamanager.messages.AdministerProductSelectionRequest;
import lib_sim_productsalesreferencedatamanager.messages.AdministerProductSelectionResponse;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story02ApsChecksMultiCardEligibilityTest extends AbstractApsJBehaveTestBase {
    AdministerProductSelectionRequest request;
    AdministerProductSelectionResponse response;
    AdministerProductSelectionInternalServiceErrorMsg administerProductSelectionInternalServiceErrorMsg;
    AdministerProductSelectionDataNotAvailableErrorMsg administerProductSelectionDataNotAvailableErrorMsg;
    AdministerProductSelectionResourceNotAvailableErrorMsg administerProductSelectionResourceNotAvailableErrorMsg;

    @BeforeScenario
    public final void before() {
        request = null;
        response = null;
    }

    @Given("Customer holds a Advance Credit Card")
    public void givenCustomerHoldsAAdvanceCreditCard() {
    }

    @Given("Customer is eligible to co hold Advance card with Platinum Balance Transfer Card")
    public void givenCustomerIsEligibleToCoHoldAdvanceCardWithPlatinumBalanceTransferCard() {
        mockScenarioHelper.expectMaxEligibleFeatureAvailableWithMaxValueOne();
        mockScenarioHelper.expectDetailsInProductEligibilityRules();
        mockScenarioHelper.expectProductFromExternalSystemProducts();
        request = dataHelper.createAdministerRequestDifferentProductAuth();
    }

    @When("UI calls APS with valid request")
    public void whenUICallsAPSWithValidRequest() {
        mockControl.go();
        try {
            response = apsClient.administerProductSelection(request);
        } catch (AdministerProductSelectionDataNotAvailableErrorMsg administerProductSelectionDataNotAvailableErrorMsg) {
            this.administerProductSelectionDataNotAvailableErrorMsg = administerProductSelectionDataNotAvailableErrorMsg;
        } catch (AdministerProductSelectionInternalServiceErrorMsg administerProductSelectionInternalServiceErrorMsg) {
            this.administerProductSelectionInternalServiceErrorMsg = administerProductSelectionInternalServiceErrorMsg;
        } catch (AdministerProductSelectionResourceNotAvailableErrorMsg administerProductSelectionResourceNotAvailableErrorMsg) {
            this.administerProductSelectionResourceNotAvailableErrorMsg = administerProductSelectionResourceNotAvailableErrorMsg;
        }
    }

    @Then("APS returns Application Type Code as CO_HOLD in response")
    public void thenAPSReturnsApplicationTypeCodeAsCO_HOLDInResponse() {
        assertNotNull(response);
        assertEquals("CO_HOLD", response.getProductEligibilityType());
    }

    @Then("APS returns Application Type Code as INELIGIBLE in response")
    public void thenAPSReturnsApplicationTypeCodeAsINELIGIBLEInResponse() {
        assertNotNull(response);
        assertEquals("INELIGIBLE", response.getProductEligibilityType());
    }


    @Given("max eligible value is one for Advance Credit Card")
    public void givenMaxEligibleValueIsOneForAdvanceCreditCard() {
        mockScenarioHelper.expectMaxEligibleFeatureAvailableWithMaxValueOne();
        mockScenarioHelper.expectDetailsInProductEligibilityRules();
        mockScenarioHelper.expectProductFromExternalSystemProducts();
        request = dataHelper.createAdministerRequestSameTypeProduct();

    }


    @Given("max eligible value is two for Advance Credit Card")
    public void givenMaxEligibleValueIsTwoForAdvanceCreditCard() {
        mockScenarioHelper.expectMaxEligibleFeatureAvailableWithMaxValueTwo();
        mockScenarioHelper.expectDetailsInProductEligibilityRules();
        mockScenarioHelper.expectProductFromExternalSystemProducts();
        request = dataHelper.createAdministerRequestSameTypeProduct();
    }


    @Given("Customer is not eligible to co hold Advance card with Platinum Balance Transfer Card")
    public void givenCustomerIsNotEligibleToCoHoldAdvanceCardWithPlatinumBalanceTransferCard() {
        mockScenarioHelper.expectMaxEligibleFeatureAvailableWithMaxValueOne();
        mockScenarioHelper.expectProductFromExternalSystemProducts();
        request = dataHelper.createAdministerRequestDifferentProductAuth();
    }

    @Given("Product Type does not match for Applied and Existing product")
    public void givenProductTypeDoesNotMatchForAppliedAndExistingProduct() {
        mockScenarioHelper.expectMaxEligibleFeatureAvailableWithMaxValueTwo();
        mockScenarioHelper.expectDetailsInProductEligibilityRules();
        mockScenarioHelper.expectProductFromExternalSystemProducts();
        request = dataHelper.createAdministerRequestWithDifferentProductTypes();
    }

    @Then("APS throws Internal Service error")
    public void thenAPSThrowsInternalServiceError() {
        assertNull(response);
        assertNotNull(administerProductSelectionInternalServiceErrorMsg);
    }

}
