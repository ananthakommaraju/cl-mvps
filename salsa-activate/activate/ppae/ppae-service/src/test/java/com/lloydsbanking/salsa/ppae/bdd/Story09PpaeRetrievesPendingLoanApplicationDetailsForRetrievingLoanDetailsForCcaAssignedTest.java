package com.lloydsbanking.salsa.ppae.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
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
import org.junit.experimental.categories.Category;

import java.util.Calendar;

@Category({AcceptanceTest.class})
public class Story09PpaeRetrievesPendingLoanApplicationDetailsForRetrievingLoanDetailsForCcaAssignedTest extends AbstractPpaeJBehaveTestBase {

    ProcessPendingArrangementEventRequest request;

    ApplicationStatus applicationStatus = new ApplicationStatus("1015", "CCA_SIGNED_CCA_PENDING");

    ProductArrangement productArrangement;

    int year = Calendar.getInstance().get(Calendar.YEAR);
    int month = Calendar.getInstance().get(Calendar.MONTH);

    ProductTypes productTypesLoans = new ProductTypes("103", "Loan Account");
    Brands brands = new Brands("HLX", "Halifax");

    @BeforeScenario
    public void resetData() {
        mockScenarioHelper.clearUp();
        request = null;
        mockControl.reset();
    }

    @Given("application status is CCA Signed/Pending and application type is Loan and zero loanAgreementNumber")
    public void ApplicationStatusIsCcaSignedinRequest() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
       try {
           mockScenarioHelper.expectContactPointIDdByChannelID();
           request = testDataHelper.createPpaeRequest("2", "HLX");
           mockScenarioHelper.expectReferenceDataForPAM(applicationStatus, productTypesLoans, brands);
           String appId = String.valueOf(testDataHelper.createApplication(applicationStatus, year, month, Calendar.getInstance().get(Calendar.DAY_OF_MONTH), productTypesLoans, brands).getId());
           request.setApplicationId(appId);
           request.getHeader().setArrangementId(appId);
           productArrangement = mockScenarioHelper.expectProductArrangementDetails(request.getHeader().getChannelId(), request.getApplicationId(), null);
           mockScenarioHelper.expectF263CallForCcaSigned(productArrangement, request);
       }catch(Exception e){
           e.printStackTrace();
       }
    }

    @When("There is a call to PPAE")
    public void whenThereIsACallToPPAE() {
        mockControl.go();
        ppaeClient.processPendingArrangementEvent(request);
    }

    @Then("PPAE calls  RetrieveLoanDetails Service successfully")
    public void thenPPAECallsRetrieveLoanDetailsServiceSuccessfully() {
        //TODO
    }
}
