package com.lloydsbanking.salsa.ppae.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.Brands;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_communicationmanager.id_communicationmanagerrouter.*;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.Ignore;
import org.junit.experimental.categories.Category;

import java.util.Calendar;

@Ignore
@Category({AcceptanceTest.class})
public class Story10PpaeCallsSendCommunicationServiceBasedOnSendEmailDateTest extends AbstractPpaeJBehaveTestBase {

    ProcessPendingArrangementEventRequest request;

    ProductArrangement productArrangement;

    int year = Calendar.getInstance().get(Calendar.YEAR);
    int month = Calendar.getInstance().get(Calendar.MONTH);
    int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

    ProductTypes productTypesLoans = new ProductTypes("103", "Loan Account");
    Brands brands = new Brands("HLX", "Halifax");


    @BeforeScenario
    public void resetData() {
        mockControl.reset();
        mockScenarioHelper.clearUp();
        request = null;
    }

    @Given("application status is CCA Signed and application type is Loan and difference in current date and PAM last updated date is within configured threshold")
    public void applicationStatusIsCcaSignedinRequest() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectContactPointIDdByChannelID();
        request = testDataHelper.createPpaeRequest("2", "HLX");
        ApplicationStatus applicationStatus = new ApplicationStatus("1015", "CCA_SIGNED_CCA_PENDING");
        mockScenarioHelper.expectReferenceDataForPAM(applicationStatus, productTypesLoans, brands);
        day = day - 2;
        String appId = String.valueOf(testDataHelper.createApplication(applicationStatus, year, month, day, productTypesLoans, brands).getId());
        request.setApplicationId(appId);
        request.getHeader().setArrangementId(appId);
        productArrangement = mockScenarioHelper.expectProductArrangementDetails(request.getHeader().getChannelId(), request.getApplicationId(), null);
        mockScenarioHelper.expectF263CallForCcaSigned(productArrangement, request);
        mockScenarioHelper.expectLookUpValuesFromPAMToRetrieveLookUpData();
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "LOANS_COMPLETE_PRE_CCA_MSG", request.getHeader(), null, ActivateCommonConstant.CommunicationTypes.EMAIL);

    }

    @Given("application status is CCA Pending and application type is Loan and difference in current date and PAM last updated date is within configured threshold")
    public void applicationStatusIsCcaPendinginRequest() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectContactPointIDdByChannelID();
        request = testDataHelper.createPpaeRequest("2", "HLX");
        ApplicationStatus applicationStatus = new ApplicationStatus("1016", "CCA_PENDING");
        mockScenarioHelper.expectReferenceDataForPAM(applicationStatus, productTypesLoans, brands);
        day = day - 1;
        String appId = String.valueOf(testDataHelper.createApplication(applicationStatus, year, month, day, productTypesLoans, brands).getId());
        request.setApplicationId(appId);
        request.getHeader().setArrangementId(appId);
        productArrangement = mockScenarioHelper.expectProductArrangementDetails(request.getHeader().getChannelId(), request.getApplicationId(), null);
        mockScenarioHelper.expectF263CallForCcaPending(productArrangement, request);
        mockScenarioHelper.expectLookUpValuesFromPAMToRetrieveLookUpData();
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "", request.getHeader(), null, ActivateCommonConstant.CommunicationTypes.EMAIL);
    }

    @Given("application status is CCA Pending and application type is Loan and difference in current date and PAM last updated date is zero")
    public void statusIsCcaPendinginRequest() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectContactPointIDdByChannelID();
        request = testDataHelper.createPpaeRequest("2", "HLX");
        ApplicationStatus applicationStatus = new ApplicationStatus("1016", "CCA_PENDING");
        mockScenarioHelper.expectReferenceDataForPAM(applicationStatus, productTypesLoans, brands);
        String appId = String.valueOf(testDataHelper.createApplication(applicationStatus, year, month, day, productTypesLoans, brands).getId());
        request.setApplicationId(appId);
        request.getHeader().setArrangementId(appId);
        productArrangement = mockScenarioHelper.expectProductArrangementDetails(request.getHeader().getChannelId(), request.getApplicationId(), null);
        mockScenarioHelper.expectF263CallForCcaPending(productArrangement, request);
        mockScenarioHelper.expectLookUpValuesFromPAMToRetrieveLookUpData();

    }


    @When("There is a call to PPAE")
    public void whenThereIsACallToPPAE() {
        mockControl.go();
        ppaeClient.processPendingArrangementEvent(request);
    }

    @Then("PPAE calls SendCommunication Service successfully")
    public void thenPPAECallsSendCommunicationServiceSuccessfully() {
        //TODO
    }
}
