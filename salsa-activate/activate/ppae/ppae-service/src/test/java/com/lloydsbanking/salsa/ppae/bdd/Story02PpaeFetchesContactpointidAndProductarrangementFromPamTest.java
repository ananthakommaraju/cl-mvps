package com.lloydsbanking.salsa.ppae.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.Brands;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import java.util.Calendar;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story02PpaeFetchesContactpointidAndProductarrangementFromPamTest extends AbstractPpaeJBehaveTestBase {

    ProcessPendingArrangementEventRequest request;

    ApplicationStatus applicationStatus;

    int year = Calendar.getInstance().get(Calendar.YEAR);
    int month = Calendar.getInstance().get(Calendar.MONTH);


    Brands brands = new Brands("LTB", "Lloyds");


    @BeforeScenario
    public void resetData() {
        mockScenarioHelper.clearUp();
        request = null;
        applicationStatus = new ApplicationStatus("1006", "Awaiting Rescore");
        mockControl.reset();
    }

    @Given("ChannelID is mapped in PAM database")
    public void givenContactPointIdIsAvailable() {
        mockScenarioHelper.expectContactPointIDdByChannelID();
        request = testDataHelper.createPpaeRequest("1", "LTB");
    }

    @Given("ChannelID is not mapped in PAM database")
    public void whenContactPointIdOfRequestIsNotMappedInPAM() {
        request = testDataHelper.createPpaeRequest("1", "ABC");
        mockScenarioHelper.expectContactPointIDdByChannelID();
    }

    @Given("request contains application id")
    public void givenRequestContainsApplicationId() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg {
        mockScenarioHelper.expectContactPointIDdByChannelID();
        request = testDataHelper.createPpaeRequest("2", "LTB");
        ProductTypes productTypes = new ProductTypes("100", "Credit Card");
        mockScenarioHelper.expectReferenceDataForPAM(applicationStatus, productTypes, brands);
        String appId = String.valueOf(testDataHelper.createApplication(applicationStatus, year, month, Calendar.getInstance().get(Calendar.DAY_OF_MONTH), productTypes, brands).getId());
        request.setApplicationId(appId);
        request.getHeader().setArrangementId(appId);
    }

    @When("There is call to PPAE with valid request")
    public void whenThereIsACallToPPAEWithValidRequest() {
        mockControl.go();
        ppaeClient.processPendingArrangementEvent(request);

    }

    @Then("PPAE service fetches ContactPointId")
    public void thenAPAPCAResponds() {
      //  mockScenarioHelper.verifyExpectCalls();
    }

    @Then("service retrieves ProductArrangement")
    public void thenServiceRetrievesProductArrangement() {
      //  mockScenarioHelper.verifyExpectCalls();

    }


    @Then("PPAE returns error and throws exception as dataNotAvailable")
    public void thenPPAEReturnsErrorAndThrowExceptionAsDataNotAvailable() {
        //mockScenarioHelper.verifyExpectCalls();
    }

}
