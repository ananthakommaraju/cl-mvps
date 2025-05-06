package com.lloydsbanking.salsa.ppae.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.Brands;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import java.util.Calendar;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story03PpaeFetchesProductDataForApplicationTest extends AbstractPpaeJBehaveTestBase {


    ProcessPendingArrangementEventRequest request;

    ApplicationStatus applicationStatus;
    ProductArrangement productArrangement;
    int year = Calendar.getInstance().get(Calendar.YEAR);
    int month = Calendar.getInstance().get(Calendar.MONTH);

    ProductTypes productTypesCurrent = new ProductTypes("102", "Current Account");

    Brands brands = new Brands("LTB", "Lloyds");

    @BeforeScenario
    public void resetData() {
        mockControl.reset();
        mockScenarioHelper.clearUp();
        request = null;
        applicationStatus = new ApplicationStatus("1006", "Awaiting Rescore");
    }

    @Given("Application status as awaiting rescore")
    public void givenApplicationStatusAsAwaitingRescore() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg {
        mockScenarioHelper.expectContactPointIDdByChannelID();
        request = testDataHelper.createPpaeRequest("2", "LTB");
        ProductTypes productTypes = new ProductTypes("100", "Credit Card");
        mockScenarioHelper.expectReferenceDataForPAM(applicationStatus, productTypes, brands);
        String appId = String.valueOf(testDataHelper.createApplication(applicationStatus, year, month, Calendar.getInstance().get(Calendar.DAY_OF_MONTH), productTypes, brands).getId());
        request.setApplicationId(appId);
        request.getHeader().setArrangementId(appId);
        productArrangement = mockScenarioHelper.expectProductArrangementDetails(request.getHeader().getChannelId(), request.getApplicationId(), null);

    }

    @Given("There is a internal call to RetrieveProductConditions with input")
    public void givenThereIsAInternalCallToRetrieveProductConditionsWithInput() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg {

        mockScenarioHelper.expectRetrieveProductCondition(request.getHeader(), productArrangement);


    }

    @When("There is a call to PPAE")
    public void whenThereIsACallToPPAE() {
        mockControl.go();
        ppaeClient.processPendingArrangementEvent(request);
    }

    @Then("PPAE fetches Product Conditions successfully")
    public void thenPPAEFetchesProductConditionsSuccessfully() {
        //TODO


    }

}
