package com.lloydsbanking.salsa.ppae.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story01PpaeRespondsToRequestTest extends AbstractPpaeJBehaveTestBase {

    ProcessPendingArrangementEventRequest request;


    @When("The PPAE service gets called with valid request")
    public void whenThePPAEServiceCalledWithValidRequest() {
        request = testDataHelper.createPpaeRequest("1", "IBL");
        mockControl.go();
        ppaeClient.processPendingArrangementEvent(request);

    }

    @Then("PPAE service logs the application ID")
    public void thenPPAEServiceLogsTheApplicationID() {
        //Entering processPendingArrangementEvent service
    }

}
