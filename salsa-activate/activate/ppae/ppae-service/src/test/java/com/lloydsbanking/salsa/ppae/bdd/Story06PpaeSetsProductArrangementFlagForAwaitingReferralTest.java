package com.lloydsbanking.salsa.ppae.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.Brands;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import java.util.Calendar;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story06PpaeSetsProductArrangementFlagForAwaitingReferralTest extends AbstractPpaeJBehaveTestBase {

    ProcessPendingArrangementEventRequest request;

    ApplicationStatus applicationStatus = new ApplicationStatus("1008", "AWAITING_REFERRAL");

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

    @Given("application status is Awaiting Referral in request")
    public void givenApplicationStatusIsAwaitingReferralInRequest() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg {

        mockScenarioHelper.expectContactPointIDdByChannelID();
        request = testDataHelper.createPpaeRequest("2", "LTB");
        mockScenarioHelper.expectReferenceDataForPAM(applicationStatus,productTypesCurrent,brands);
        String appId = String.valueOf(testDataHelper.createApplication(applicationStatus, year, month, Calendar.getInstance().get(Calendar.DAY_OF_MONTH),productTypesCurrent,brands).getId());
        request.setApplicationId(appId);
        request.getHeader().setArrangementId(appId);
    }

    @When("There is a call to PPAE")
    public void whenThereIsACallToPPAE() {
        mockControl.go();
        ppaeClient.processPendingArrangementEvent(request);
    }

    @Then("PPAE sets Activate Product Arrangement Flag To True")
    public void thenPPAESetsActivateProductArrangementFlagToTrue() {
        //mockScenarioHelper.verifyExpectCalls();

    }
}
