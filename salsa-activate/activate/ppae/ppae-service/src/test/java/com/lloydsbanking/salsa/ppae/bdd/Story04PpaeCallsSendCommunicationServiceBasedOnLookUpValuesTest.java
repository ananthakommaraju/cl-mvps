package com.lloydsbanking.salsa.ppae.bdd;


import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.Brands;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_communicationmanager.id_communicationeventscheduler.*;
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
public class Story04PpaeCallsSendCommunicationServiceBasedOnLookUpValuesTest extends AbstractPpaeJBehaveTestBase {

    ProcessPendingArrangementEventRequest request;

    ApplicationStatus applicationStatus = new ApplicationStatus("1007", "AWAITING MANUAL ID V");

    ProductArrangement productArrangement;

    int year = Calendar.getInstance().get(Calendar.YEAR);
    int month = Calendar.getInstance().get(Calendar.MONTH);

    ProductTypes productTypesCurrent = new ProductTypes("102", "Current Account");

    Brands brands = new Brands("LTB", "Lloyds");

    @BeforeScenario
    public void resetData() {
        mockScenarioHelper.clearUp();
        request = null;
        mockControl.reset();
    }

    @Given("lookup data from PAM is available and the difference between the no. of configured days")
    public void givenLookupDataFromPAMIsAvailableAndTheDifferenceBetweenTheNoOfConfiguredDays() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg {

        mockScenarioHelper.expectContactPointIDdByChannelID();
        request = testDataHelper.createPpaeRequest("2", "LTB");
        mockScenarioHelper.expectReferenceDataForPAM(applicationStatus,productTypesCurrent,brands);
    }

    @Given("the no. of days after update is less than five in request")
    public void givenTheNoOfDaysAfterUpdateIsLessThanFiveInRequest() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg {
        year = year - 1;
        String appId = String.valueOf(testDataHelper.createApplication(applicationStatus, year, month, Calendar.getInstance().get(Calendar.DAY_OF_MONTH),productTypesCurrent,brands).getId());
        request.setApplicationId(appId);
        request.getHeader().setArrangementId(appId);
        productArrangement = mockScenarioHelper.expectProductArrangementDetails(request.getHeader().getChannelId(), request.getApplicationId(), null);
        mockScenarioHelper.expectLookUpValuesFromPAMToRetrieveLookUpData();
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "CA_EIDNV_REMINDER_MSG", request.getHeader(), null, ActivateCommonConstant.CommunicationTypes.EMAIL);
    }


    @Given("the no. of days after update is greater than five in request")
    public void givenTheNoOfDaysAfterUpdateIsGreaterThanFiveInRequest() throws ScheduleCommunicationResourceNotAvailableErrorMsg, ScheduleCommunicationExternalServiceErrorMsg, ScheduleCommunicationInternalServiceErrorMsg, ScheduleCommunicationDataNotAvailableErrorMsg, ScheduleCommunicationExternalBusinessErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {

        year = Calendar.getInstance().get(Calendar.YEAR);
        String appId = String.valueOf(testDataHelper.createApplication(applicationStatus, year, month, Calendar.getInstance().get(Calendar.DAY_OF_MONTH),productTypesCurrent,brands).getId());
        request.setApplicationId(appId);
        request.getHeader().setArrangementId(appId);
        productArrangement = mockScenarioHelper.expectProductArrangementDetails(request.getHeader().getChannelId(), request.getApplicationId(), null);
        mockScenarioHelper.expectLookUpValuesFromPAMToRetrieveLookUpData();
        mockScenarioHelper.expectScheduleCommunicationCall(productArrangement, "CA_EIDNV_REMINDER_MSG", request.getHeader(), null, ActivateCommonConstant.CommunicationTypes.EMAIL, 35);

    }

    @When("There is a call to PPAE")
    public void whenThereIsACallToPPAE() {
        mockControl.go();
        ppaeClient.processPendingArrangementEvent(request);
    }

    @Then("PPAE calls sendCommunication service successfully")
    public void thenPPAECallsSendCommunicationServiceSuccessfully() {
        //TODO

    }


    @Then("PPAE calls scheduleCommunication service successfully")
    public void thenPPAECallsScheduleCommunicationServiceSuccessfully() {
        //TODO

    }


}
