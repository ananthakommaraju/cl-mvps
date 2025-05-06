package com.lloydsbanking.salsa.ppae.bdd.scheduler;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.Brands;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import com.lloydsbanking.salsa.ppae.bdd.AbstractPpaeJBehaveTestBase;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import java.util.Calendar;

import static org.junit.Assert.assertFalse;

@Category({AcceptanceTest.class})
public class Story01PpaescheduleServiceProcessesScheduledAppEventsTest extends AbstractPpaeJBehaveTestBase {

    ApplicationStatus applicationStatus = new ApplicationStatus("1006", "Awaiting Rescore");
    int year = Calendar.getInstance().get(Calendar.YEAR);
    int month = Calendar.getInstance().get(Calendar.MONTH);

    ProductTypes productTypesCurrent = new ProductTypes("102", "Current Account");

    Brands brands = new Brands("LTB", "Lloyds");


    @BeforeScenario
    public void resetData() {
        mockScenarioHelper.clearUp();

    }

    @Given("PPAE salsa switch is ON")
    public void givenPPAESalsaSwitchIsON() {

        mockScenarioHelper.setPPAEBatchSwitch(true);

    }

    @Given("There are scheduled application events")
    public void givenThereAreScheduledApplicationEvents() {
        mockScenarioHelper.expectContactPointIDdByChannelID();
        mockScenarioHelper.expectApplicationDataForPAM(applicationStatus,productTypesCurrent,brands);
        mockScenarioHelper.expectScheduleEvent(String.valueOf(testDataHelper.createApplication(applicationStatus, year, month, Calendar.getInstance().get(Calendar.DAY_OF_MONTH),productTypesCurrent,brands).getId()));

    }

    @When("PPAE Scheduled Job runs")
    public void whenPPAEScheduledJobRuns() throws InterruptedException {
        mockControl.go();
        Thread.sleep(70000);
    }

    @Then("pending applications gets picked for processing")
    public void thenPendingApplicationsGetsPickedForProcessing() {
        assertFalse(mockScenarioHelper.expectUpdatedDao());
    }
}