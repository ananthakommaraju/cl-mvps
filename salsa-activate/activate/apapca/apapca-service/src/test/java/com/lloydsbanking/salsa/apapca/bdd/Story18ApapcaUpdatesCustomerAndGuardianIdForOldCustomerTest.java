package com.lloydsbanking.salsa.apapca.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.KycStatus;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import com.lloydstsb.schema.involvedpartymanagement.ifw.ErrorInfo;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_communicationmanager.id_communicationmanagerrouter.*;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.Ignore;
import org.junit.experimental.categories.Category;

import static junit.framework.TestCase.assertNotNull;
@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story18ApapcaUpdatesCustomerAndGuardianIdForOldCustomerTest extends AbstractApapcaJBehaveTestBase {

    ActivateProductArrangementRequest request;

    ActivateProductArrangementResponse response;

    ProductArrangement productArrangement;

    ProductTypes productTypesCurrent = new ProductTypes("102", "Current Account");
    String channelId;

    @BeforeScenario
    public void resetData() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        mockControl.reset();
        mockScenarioHelper.clearUp();
        response = null;
        channelId = mockScenarioHelper.expectChannelIdByContactPointID();

        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectLookUpValuesWithISOCode();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        ApplicationStatus applicationStatus = new ApplicationStatus("1002", "Approved");

        request = testDataHelper.createApaRequestForPca();
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypesCurrent);
        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");

    }

    @Given("application sub status is update customer record failure")
    public void givenApplicationSubStatusIsUpdateCustomerRecordFailure() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg {
        request.getProductArrangement().setApplicationSubStatus("1018");
        mockScenarioHelper.expectRetrieveProductCondition((DepositArrangement) productArrangement,request.getHeader());
        mockScenarioHelper.expectB766Call(request.getHeader(), productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode());
        String accNo = mockScenarioHelper.expectE229Call(request.getHeader(), "779129");
        productArrangement.setAccountNumber(accNo);
    }

    @Given("evidence and purpose lookUp data call succeeds")
    public void givenEvidenceAndPurposeLookUpDataCallSucceeds() throws ErrorInfo, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectLookUpValuesForEvidenceAndPurposeData();
        mockScenarioHelper.expectF061Call(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier(), request.getHeader());
        String custId = mockScenarioHelper.expectF062Call(productArrangement.getPrimaryInvolvedParty(), request.getHeader());
        productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(custId);
        mockScenarioHelper.expectC658Call(request.getProductArrangement(), request.getHeader(), 1);
        mockScenarioHelper.expectC234Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectF060Call(productArrangement,request.getHeader(),0);
        mockScenarioHelper.expectActivateBenefitCall(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectFATCAUpdateSwitchCall(channelId, true);
        mockScenarioHelper.expectRecordCustomerDetails(productArrangement, request.getHeader(), false);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_WELCOME_MSG", request.getHeader(), null, "Email", true);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_BENEFITS_MSG", request.getHeader(), null, "Email", false);
    }

    @Given("evidence and purpose lookUp data call fails")
    public void givenEvidenceAndPurposeLookUpDataCallFails() throws ErrorInfo, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectF061Call(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier(), request.getHeader());
        String custId = mockScenarioHelper.expectF062Call(productArrangement.getPrimaryInvolvedParty(), request.getHeader());
        productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(custId);
        mockScenarioHelper.expectC658Call(request.getProductArrangement(), request.getHeader(), 1);
        mockScenarioHelper.expectC234Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectF060Call(productArrangement,request.getHeader(),0);
        mockScenarioHelper.expectActivateBenefitCall(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectFATCAUpdateSwitchCall(channelId, true);
        mockScenarioHelper.expectRecordCustomerDetails(productArrangement, request.getHeader(), false);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_WELCOME_MSG", request.getHeader(), null, "Email", true);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_BENEFITS_MSG", request.getHeader(), null, "Email", false);
    }

    @Given("call to F062 succeeds for customer details")
    public void givenCallToF062SucceedsForCustomerDetails() throws ErrorInfo, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectF061Call(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier(), request.getHeader());
        String custId = mockScenarioHelper.expectF062Call(productArrangement.getPrimaryInvolvedParty(), request.getHeader());
        productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(custId);
        mockScenarioHelper.expectC658Call(request.getProductArrangement(), request.getHeader(), 1);
        mockScenarioHelper.expectC234Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectF060Call(productArrangement,request.getHeader(),0);
        mockScenarioHelper.expectActivateBenefitCall(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectFATCAUpdateSwitchCall(channelId, true);
        mockScenarioHelper.expectRecordCustomerDetails(productArrangement, request.getHeader(), false);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_WELCOME_MSG", request.getHeader(), null, "Email", true);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_BENEFITS_MSG", request.getHeader(), null, "Email", false);
    }

    @Given("call to F062 fails for customer details")
    public void givenCallToF062FailsForCustomerDetails() {
        mockScenarioHelper.expectF061Call(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier(), request.getHeader());
        mockScenarioHelper.expectF062CallFails(productArrangement.getPrimaryInvolvedParty(), request.getHeader());
    }

    @Given("guardian details are not present")
    public void givenGuardianDetailsAreNotPresent() throws ErrorInfo, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectF061Call(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier(), request.getHeader());
        String custId = mockScenarioHelper.expectF062Call(productArrangement.getPrimaryInvolvedParty(), request.getHeader());
        productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(custId);
        mockScenarioHelper.expectC658Call(request.getProductArrangement(), request.getHeader(), 1);
        mockScenarioHelper.expectC234Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectF060Call(productArrangement,request.getHeader(),0);
        mockScenarioHelper.expectActivateBenefitCall(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectFATCAUpdateSwitchCall(channelId, true);
        mockScenarioHelper.expectRecordCustomerDetails(productArrangement, request.getHeader(), false);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_WELCOME_MSG", request.getHeader(), null, "Email", true);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_BENEFITS_MSG", request.getHeader(), null, "Email", false);
    }

    @When("there is call to APAPCA for old customer")
    public void whenThereIsCallToAPAPCAForOldCustomer() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        mockControl.go();
        response = apaPcaClient.activateProductArrangement(request);
        mockScenarioHelper.sleep();
    }

    @Then("service continues")
    public void thenServiceContinues() {
        assertNotNull(response);
    }

    @Then("update customer id and sub status as null")
    public void thenUpdateCustomerIdAndSubStatusAsNull() {
        assertNotNull(response);
    }

    @Then("update application sub status as update customer record failure")
    public void thenUpdateApplicationSubStatusAsUpdateCustomerRecordFailure() {
        assertNotNull(response);
    }

}
