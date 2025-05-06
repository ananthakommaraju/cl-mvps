package com.lloydsbanking.salsa.ppae.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.Brands;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.ErrorInfo;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_communicationmanager.id_communicationmanagerrouter.*;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import javax.xml.bind.JAXBException;
import java.util.Calendar;

@Category({AcceptanceTest.class})
public class Story14PpaeProcessesAwaitingPostFulfilmentApplicationTest extends AbstractPpaeJBehaveTestBase {

    ProcessPendingArrangementEventRequest request;
    ApplicationStatus applicationStatus;
    ProductTypes productTypes;
    ProductArrangement productArrangement;
    RequestHeader header;
    Brands brands = new Brands("LTB", "Lloyds");
    int year = Calendar.getInstance().get(Calendar.YEAR);
    int month = Calendar.getInstance().get(Calendar.MONTH);


    @BeforeScenario
    public void resetData() {
        mockControl.reset();
        mockScenarioHelper.clearUp();
        request = null;
        header = null;
    }

    @Given("application status is AWAITING_POST_FULFILMENT_PROCESS (1013)")
    public void givenApplicationStatusIsAWAITING_POST_FULFILMENT_PROCESS() {
        applicationStatus = new ApplicationStatus("1013", "AWAITING_POST_FULFILMENT_PROCESS");
        productTypes = new ProductTypes("103", "Loan Account");
        request = testDataHelper.createPpaeRequest();
        header = testDataHelper.createPpaeRequest().getHeader();
        mockScenarioHelper.expectApplicationRelatedData(applicationStatus, productTypes, brands);
    }

    @Given("balance transfer is for greater than threshold amount")
    public void givenBalanceTransferIsForGreaterThanThresholdAmount() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg {
        String appId = String.valueOf(testDataHelper.createApplication(applicationStatus, year, month, Calendar.getInstance().get(Calendar.DAY_OF_MONTH), productTypes, brands).getId());
        request.setApplicationId(appId);
        request.getHeader().setArrangementId(appId);
        String contactPointId = mockScenarioHelper.expectContactPointIDdByChannelID();
        header.setContactPointId(contactPointId);
        header.setArrangementId(appId);
        productArrangement = mockScenarioHelper.expectProductArrangementDetails(header.getChannelId(), request.getApplicationId(), null);
    }

    @Given("balance transfer is for less than threshold amount")
    public void givenBalanceTransferIsForLessThanThresholdAmount() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg {
        String appId = String.valueOf(testDataHelper.createApplicationWithBTAmountLessThanThreshold(applicationStatus, year, month, Calendar.getInstance().get(Calendar.DAY_OF_MONTH), productTypes, brands).getId());
        request.setApplicationId(appId);
        request.getHeader().setArrangementId(appId);
        String contactPointId = mockScenarioHelper.expectContactPointIDdByChannelID();
        header.setContactPointId(contactPointId);
        header.setArrangementId(appId);
        productArrangement = mockScenarioHelper.expectProductArrangementDetails(header.getChannelId(), request.getApplicationId(), null);
    }

    @Given("retrieve product conditions succeeds with btOffAttribute")
    public void givenRetrieveProductConditionsSucceedsWithBtOffAttribute() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        mockScenarioHelper.expectRpcCallForBtOffAttribute(productArrangement, header, "true");
    }

    @Given("retrieve product conditions fails")
    public void givenRetrieveProductConditionsFails() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        mockScenarioHelper.expectRpcCallForBtOffAttribute(productArrangement, header, null);
    }

    @Given("application verification succeeds")
    public void givenApplicationVerificationSucceeds() throws ErrorInfo, JAXBException {
        mockScenarioHelper.expectVerifyCall(((FinanceServiceArrangement) productArrangement).getBalanceTransfer().get(0), productArrangement.getPrimaryInvolvedParty(), header, 0);
    }

    @Given("application verification fails")
    public void givenApplicationVerificationFails() throws ErrorInfo, JAXBException, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectVerifyCall(((FinanceServiceArrangement) productArrangement).getBalanceTransfer().get(0), productArrangement.getPrimaryInvolvedParty(), header, 1);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "BT_FAILED_MSG", header, null, "Email");
    }

    @Given("balance transfer status from issue in payment is not checked or failed for balance transfer")
    public void givenBalanceTransferStatusFromIssueInPaymentIsNotCheckedOrFailedForBalanceTransfer() throws com.lloydstsb.schema.enterprise.lcsm_financialtransactionprocessing.ErrorInfo, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        //Issue Fail for all BT
        mockScenarioHelper.expectIssueInPaymentCall("true", ((FinanceServiceArrangement) productArrangement).getBalanceTransfer().get(0), ((FinanceServiceArrangement) productArrangement).getCreditCardNumber(), header, 1);
        mockScenarioHelper.expectIssueInPaymentCall("true", ((FinanceServiceArrangement) productArrangement).getBalanceTransfer().get(1), ((FinanceServiceArrangement) productArrangement).getCreditCardNumber(), header, 1);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "BT_FAILED_MSG", header, null, "Email");
    }

    @Given("balance transfer status from issue in payment is not success for all balance transfer")
    public void givenBalanceTransferStatusFromIssueInPaymentIsNotSuccessForAllBalanceTransfer() throws com.lloydstsb.schema.enterprise.lcsm_financialtransactionprocessing.ErrorInfo, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        //Issue Success for one and fail for other
        mockScenarioHelper.expectIssueInPaymentCall("true", ((FinanceServiceArrangement) productArrangement).getBalanceTransfer().get(0), ((FinanceServiceArrangement) productArrangement).getCreditCardNumber(), header, 1);
        mockScenarioHelper.expectIssueInPaymentCall("true", ((FinanceServiceArrangement) productArrangement).getBalanceTransfer().get(1), ((FinanceServiceArrangement) productArrangement).getCreditCardNumber(), header, 0);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "BT_PRTLY_FULFLD_MSG", header, null, "Email");
    }

    @Given("balance transfer status from issue in payment is success for all balance transfer")
    public void givenBalanceTransferStatusFromIssueInPaymentIsSuccessForAllBalanceTransfer() throws com.lloydstsb.schema.enterprise.lcsm_financialtransactionprocessing.ErrorInfo, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        //Issue Success for all BT
        mockScenarioHelper.expectIssueInPaymentCall("true", ((FinanceServiceArrangement) productArrangement).getBalanceTransfer().get(0), ((FinanceServiceArrangement) productArrangement).getCreditCardNumber(), header, 0);
        mockScenarioHelper.expectIssueInPaymentCall("true", ((FinanceServiceArrangement) productArrangement).getBalanceTransfer().get(1), ((FinanceServiceArrangement) productArrangement).getCreditCardNumber(), header, 0);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "BT_FULFILLED_MSG", header, null, "Email");
    }

    @When("there is call to PPAE")
    public void whenThereIsCallToPPAE() throws InterruptedException {
        mockControl.go();
        ppaeClient.processPendingArrangementEvent(request);
        Thread.sleep(5000);
    }

    @Then("service communicates balance transfer status to customer")
    public void thenServiceCommunicatesBalanceTransferStatusToCustomer() {
    }

    @Then("service terminates")
    public void thenServiceTerminates() {
    }

}
