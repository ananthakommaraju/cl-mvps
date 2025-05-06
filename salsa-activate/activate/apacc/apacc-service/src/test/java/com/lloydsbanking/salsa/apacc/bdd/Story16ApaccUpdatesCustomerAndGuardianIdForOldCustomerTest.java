package com.lloydsbanking.salsa.apacc.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.KycStatus;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_bo.businessobjects.ProductOffer;
import lib_sim_communicationmanager.ia_generatedocument.GenerateDocumentInternalServiceErrorMsg;
import lib_sim_communicationmanager.ia_generatedocument.GenerateDocumentResourceNotAvailableErrorMsg;
import lib_sim_communicationmanager.id_communicationmanagerrouter.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story16ApaccUpdatesCustomerAndGuardianIdForOldCustomerTest extends AbstractApaccJBehaveTestBase {

    ActivateProductArrangementRequest request;

    ActivateProductArrangementResponse response;

    ApplicationStatus applicationStatus;

    ProductTypes productTypeCreditCard = new ProductTypes("100", "Credit Card");

    ProductArrangement productArrangement;

    String accountNumber;

    RequestHeader header;

    List<ProductOffer> productOfferList;

    @BeforeScenario
    public void resetData() {
        mockScenarioHelper.clearUp();
        request = null;
        response = null;
        applicationStatus = new ApplicationStatus("1002", "Approved");
        accountNumber = null;
        header = null;
        productOfferList = new ArrayList<>();
    }

    @Given("application sub status is update customer record failure")
    public void givenApplicationSubStatusIsUpdateCustomerRecordFailure() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, GenerateDocumentResourceNotAvailableErrorMsg, GenerateDocumentInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectReferenceDataForPAM();
        mockScenarioHelper.expectLookUpValues();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForCc();
        request.getProductArrangement().setApplicationSubStatus("1018");
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, "1018", "123", productTypeCreditCard);
        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        header = testDataHelper.createApaRequestForCc().getHeader();
        header.setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        header.setContactPointId("0000777505");
        productOfferList.addAll(mockScenarioHelper.expectRetrieveProductCondition(productArrangement.getAssociatedProduct(), header));
        mockScenarioHelper.expectLookUpValuesForEvidenceAndPurposeData();

    }

    @When("there is call to APACC for old customer")
    public void whenThereIsCallToAPACCForOldCustomer() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        mockControl.go();
        response = apaccClient.activateProductArrangement(request);
        mockScenarioHelper.sleep();
    }


    @Given("call to F062 succeeds for customer details")
    public void givenCallToF062SucceedsForCustomerDetails() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectF061Call(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier(), header);
        String custId = mockScenarioHelper.expectF062Call(productArrangement.getPrimaryInvolvedParty(), header);
        productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(custId);
        mockScenarioHelper.expectF060Call(productArrangement, header, 0);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "WELCOME_MSG", header, null, "Email", true);
    }


    @Then("update customer id and sub status as null")
    public void thenUpdateCustomerIdAndSubStatusAsNull() {
        assertNotNull(response);
        mockScenarioHelper.verifyExpectCalls();
    }


    @Given("call to F062 fails for customer details")
    public void givenCallToF062FailsForCustomerDetails() {
        mockScenarioHelper.expectF061Call(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier(), header);
        mockScenarioHelper.expectF062CallFails(productArrangement.getPrimaryInvolvedParty(), header);

    }

    @Then("update guardian id and sub status as null")
    public void thenUpdateGuardianIdAndSubStatusAsNull() {
        assertNotNull(response);
        mockScenarioHelper.verifyExpectCalls();
    }


    @Then("update application sub status as update customer record failure")
    public void thenUpdateApplicationSubStatusAsUpdateCustomerRecordFailure() {
        assertNotNull(response);
        mockScenarioHelper.verifyExpectCalls();
    }


    @Given("guardian details are not present")
    public void givenGuardianDetailsAreNotPresent() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectF061Call(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier(), header);
        String custId = mockScenarioHelper.expectF062Call(productArrangement.getPrimaryInvolvedParty(), header);
        productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(custId);
        mockScenarioHelper.expectF060Call(productArrangement, header, 0);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "WELCOME_MSG", header, null, "Email", true);
    }
    @Then("service continues")
    public void thenServiceContinues() {
        assertNotNull(response);
        mockScenarioHelper.verifyExpectCalls();
    }


}
