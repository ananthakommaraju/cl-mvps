package com.lloydsbanking.salsa.opasaving.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.offer.ApplicantType;
import com.lloydsbanking.salsa.opasaving.constants.SavingsProducts;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.When;
import org.jbehave.core.annotations.Then;
import org.junit.Assert;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story07OpasavingChecksForDuplicateApplicationsTest extends AbstractOpasavingJBehaveTestBase {

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;

    @BeforeScenario
    public void resetResponse() {
        request = null;
        response = null;
    }

    @Given("Duplicate applications for a $product product exists in PAM with a status of $status")
    public void givenDuplicateApplicationsExistsInPAM(@Named("product") String productName, @Named("status") String appStatus) throws ParseException, DatatypeConfigurationException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        ApplicationStatus status = ApplicationStatus.valueOf(appStatus.replace(" ", "_").toUpperCase());
        SavingsProducts productEnum = SavingsProducts.findProduct(productName);

        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");
        mockScenarioHelper.expectDuplicationApplications(status.getValue(), appStatus, productEnum.getProductId(), productEnum.getProductName());
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaSavingRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336CallForAccept(dataHelper.createOpaSavingRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaSavingRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectLookupListFromChannelAndEvidenceList("LTB");

        if(isApplicationStatusACompletedStatus(status)) {
            mockScenarioHelper.expectF204CallForExistingCustomers(dataHelper.createOpaSavingRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
            mockScenarioHelper.expectEligibility("true", dataHelper.createOpaSavingRequestHeader("LTB"), false, 1);
            mockScenarioHelper.expectSwitchValueFromSwitchName("SW_SvngCstSegPrc", "LTB", 3, "0");
        }

        //request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.GUARDIAN.getValue());
    }

    @Given("applications status is not ASM decline")
    public void givenApplicationsStatusIsNotASMDecline() {
        mockScenarioHelper.expectLookupDataForDuplicateApplication("LTB", "BRAND_COHOLDING");
        mockScenarioHelper.expectPAMReferenceData();
    }

    @Given("No duplicate applications exists in PAM")
    public void givenNoDuplicateApplicationsExistsInPAM() throws ParseException, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, DatatypeConfigurationException, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        request = dataHelper.generateOfferProductArrangementSavingRequest("LTB");

        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockScenarioHelper.expectF447Call(dataHelper.createOpaSavingRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaSavingRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaSavingRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectEligibility("true", dataHelper.createOpaSavingRequestHeader("LTB"), false, 1);
        mockScenarioHelper.expectSwitchValueFromSwitchName("SW_SvngCstSegPrc", "LTB", 3, "0");
        mockScenarioHelper.expectF204CallForExistingCustomers(dataHelper.createOpaSavingRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));

        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());
    }

    @When("UI calls OPASaving with valid request")
    public void whenUICallsOPAPCAWithValidRequest() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg {
        mockControl.go();
        response = opasavingClient.offerProductArrangement(request);
    }

    @Then("OPASaving returns duplicate application error in response")
    public void thenOPAPCAReturnsDuplicateApplicationErrorInResponse() {
        assertNotNull(response);
        assertEquals("829001", response.getProductArrangement().getAssociatedProduct().getEligibilityDetails().getDeclineReasons().get(0).getCode());
    }

    @Then("OPASaving returns valid response")
    public void thenOPAPCAReturnsValidResponse() {
        assertNotNull(response);
    }

    @Then("OpaSaving returns EIDV status as accept for the customer")
    public void thenOpaSavingReturnsEIDVStatusAsAcceptForTheCustomer() {
        Assert.assertEquals("ACCEPT", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult());
        Assert.assertEquals("EIDV", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
    }

    @Then("OpaSaving returns EIDV status as N/A for the customer")
    public void thenOpaSavingReturnsEIDVStatusAsNAForTheCustomer() {
        Assert.assertEquals("N/A", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult());
        Assert.assertEquals("EIDV", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
    }

    @Then("OpaSaving returns EIDV status as refer for the customer")
    public void thenOpaSavingReturnsEIDVStatusAsDeclineForTheCustomer() {
        Assert.assertEquals("REFER", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult());
        Assert.assertEquals("EIDV", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
    }

    @Then("OpaSaving returns ASM score as accept for the customer")
    public void thenOpaSavingReturnsApplicationStatusAndAsmScoreAsAcceptForTheCustomer() {
        Assert.assertEquals("1", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getScoreResult());
        Assert.assertEquals("ASM", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getAssessmentType());
        Assert.assertEquals("description", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getReferralCode().get(0).getDescription());
        Assert.assertEquals("code", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getReferralCode().get(0).getCode());
    }

    @Then("OpaSaving returns application status as $returnedStatus for the customer")
    public void thenOpaSavingReturnsApplicationStatusForTheCustomer(@Named("returnedStatus") String appStatus){
        String statusCode = ApplicationStatus.valueOf(appStatus.replace(" ", "_").toUpperCase()).getValue();

        Assert.assertEquals(statusCode, response.getProductArrangement().getApplicationStatus());
    }

    @Then("OpaSaving returns no application status in the response")
    public void thenOpaSavingReturnsNoApplicationStatusInTheResponse() {
        assertNull(response.getProductArrangement().getApplicationStatus());
    }

    private boolean isApplicationStatusACompletedStatus(ApplicationStatus status){
        if(ApplicationStatus.DECLINED.equals(status) || ApplicationStatus.ABANDONED.equals(status) ||
                ApplicationStatus.FULFILLED.equals(status) || ApplicationStatus.CANCELLED.equals(status)) {
            return true;
        }

        return false;
    }
}
