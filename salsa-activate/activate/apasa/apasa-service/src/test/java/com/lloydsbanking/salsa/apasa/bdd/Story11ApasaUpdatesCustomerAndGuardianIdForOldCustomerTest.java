package com.lloydsbanking.salsa.apasa.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.KycStatus;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import com.lloydstsb.schema.involvedpartymanagement.ifw.ErrorInfo;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.InternetBankingProfile;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_communicationmanager.id_communicationeventscheduler.*;
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
import org.junit.Assert;
import org.junit.experimental.categories.Category;

import java.math.BigInteger;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story11ApasaUpdatesCustomerAndGuardianIdForOldCustomerTest extends AbstractApasaJBehaveTestBase {

    ActivateProductArrangementRequest request;

    ActivateProductArrangementResponse response;

    ApplicationStatus applicationStatus;

    ProductTypes productTypesSaving = new ProductTypes("101", "Saving Account");

    ProductArrangement productArrangement;

    String accountNumber;

    RequestHeader header;
    String channelId;

    @BeforeScenario
    public void resetData() {
        mockScenarioHelper.clearUp();
        request = null;
        response = null;
        applicationStatus = new ApplicationStatus("1002", "Approved");
        accountNumber = null;
        header = null;
        channelId = null;
    }

    @Given("application sub status is update customer record failure")
    public void givenApplicationSubStatusIsUpdateCustomerRecordFailure() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, ScheduleCommunicationResourceNotAvailableErrorMsg, ScheduleCommunicationExternalServiceErrorMsg, ScheduleCommunicationInternalServiceErrorMsg, ScheduleCommunicationDataNotAvailableErrorMsg, ScheduleCommunicationExternalBusinessErrorMsg, ErrorInfo, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");

        request = testDataHelper.createApaRequestForSaFor502();
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestDataWithNiNumberWithoutGuardianDetails(applicationStatus, eidvStatus, "1", channelId, request, "1018", "123", productTypesSaving);

        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");
        request.getProductArrangement().setApplicationSubStatus("1018");
        mockScenarioHelper.expectRpcCall((DepositArrangement) productArrangement, request.getHeader());
        mockScenarioHelper.expectLookUpValuesWithISOCode();
        mockScenarioHelper.expectB766Call(request.getHeader(), request.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode());


    }


    @Given("guardian details are not present")
    public void givenGuardianDetailsAreNotPresent() throws ErrorInfo, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, ScheduleCommunicationResourceNotAvailableErrorMsg, ScheduleCommunicationExternalServiceErrorMsg, ScheduleCommunicationInternalServiceErrorMsg, ScheduleCommunicationDataNotAvailableErrorMsg, ScheduleCommunicationExternalBusinessErrorMsg {
        mockScenarioHelper.expectF061Call(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier(), request.getHeader());
        String custId = mockScenarioHelper.expectF062Call(productArrangement.getPrimaryInvolvedParty(), request.getHeader());
        productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(custId);
        mockScenarioHelper.expectC658Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectC234Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectF060Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectFATCAUpdateSwitchCall(channelId, true);
        mockScenarioHelper.expectRecordCustomerDetails(productArrangement, request.getHeader(), false);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "SA_WELCOME_MSG", request.getHeader(), null, "Email", true);
        mockScenarioHelper.expectScheduleCommunicationCall((DepositArrangement) productArrangement, "SA_FUNDING_REM_MSG", request.getHeader(), "STPSAVINGS", "Email");

    }

    @When("there is call to APASA for old customer")
    public void whenThereIsCallToAPASAForOldCustomer() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        mockControl.go();
        response = apaSaClient.activateProductArrangement(request);
        mockScenarioHelper.sleep();
    }

    @Then("service continues")
    public void thenServiceContinues() {
        assertNotNull(response);
        mockScenarioHelper.verifyExpectCalls();
    }

    @Then("update application sub status as update customer record failure")
    public void thenUpdateApplicationSubStatusAsUpdateCustomerRecordFailure() {
        assertNotNull(response);
        assertEquals("1010", response.getProductArrangement().getApplicationStatus());
        assertNull(response.getProductArrangement().getRetryCount());
        assertEquals("1018",response.getProductArrangement().getApplicationSubStatus());
        mockScenarioHelper.verifyExpectCalls();
    }

    @Then("update guardian id and sub status as null")
    public void thenUpdateGuardianIdAndSubStatusAsNull() {
        assertNotNull(response);
        assertEquals("1010", response.getProductArrangement().getApplicationStatus());
        assertNull(response.getProductArrangement().getRetryCount());
        assertEquals("1018",response.getProductArrangement().getApplicationSubStatus());
        mockScenarioHelper.verifyExpectCalls();
    }

    @Given("call to F062 fails for customer details")
    public void givenCallToF062FailsForCustomerDetails() {
        mockScenarioHelper.expectF061Call(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier(), request.getHeader());
        mockScenarioHelper.expectF062CallFails(productArrangement.getPrimaryInvolvedParty(), request.getHeader());
    }

    @Given("call to F062 succeeds for customer details")
    public void givenCallToF062SucceedsForCustomerDetails() throws ErrorInfo, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, ScheduleCommunicationResourceNotAvailableErrorMsg, ScheduleCommunicationExternalServiceErrorMsg, ScheduleCommunicationInternalServiceErrorMsg, ScheduleCommunicationDataNotAvailableErrorMsg, ScheduleCommunicationExternalBusinessErrorMsg {
        mockScenarioHelper.expectF061Call(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier(), request.getHeader());
        String custId = mockScenarioHelper.expectF062Call(productArrangement.getPrimaryInvolvedParty(), request.getHeader());
        productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(custId);
        mockScenarioHelper.expectC658Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectC234Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectF060Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectFATCAUpdateSwitchCall(channelId, true);
        mockScenarioHelper.expectRecordCustomerDetails(productArrangement, request.getHeader(), false);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "SA_WELCOME_MSG", request.getHeader(), null, "Email", true);
        mockScenarioHelper.expectScheduleCommunicationCall((DepositArrangement) productArrangement, "SA_FUNDING_REM_MSG", request.getHeader(), "STPSAVINGS", "Email");

    }
    @Given("application sub status is update customer record failure and guardian details are present")
    public void givenApplicationSubStatusIsUpdateCustomerRecordFailureAndGuardianDetailsArePresent() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");

        request = testDataHelper.createApaRequestForSaFor502();
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestDataWithNiNumber(applicationStatus, eidvStatus, "1", channelId, request, "1018", "123", productTypesSaving);

        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");
        request.getProductArrangement().setApplicationSubStatus("1018");
        mockScenarioHelper.expectRpcCall((DepositArrangement) productArrangement, request.getHeader());
        mockScenarioHelper.expectLookUpValuesWithISOCode();
        mockScenarioHelper.expectB766Call(request.getHeader(), request.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode());


    }
    @Given("call to F062 succeeds for guardian details")
    public void givenCallToF062SucceedsForGuardianDetails() throws ErrorInfo, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, ScheduleCommunicationResourceNotAvailableErrorMsg, ScheduleCommunicationExternalServiceErrorMsg, ScheduleCommunicationInternalServiceErrorMsg, ScheduleCommunicationDataNotAvailableErrorMsg, ScheduleCommunicationExternalBusinessErrorMsg {
        mockScenarioHelper.expectF061Call(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier(), request.getHeader());
        String custId = mockScenarioHelper.expectF062Call(productArrangement.getPrimaryInvolvedParty(), request.getHeader());
        productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(custId);
        mockScenarioHelper.expectF061Call(productArrangement.getGuardianDetails().getCustomerIdentifier(), request.getHeader());
        productArrangement.getGuardianDetails().setCustomerIdentifier(mockScenarioHelper.expectF062Call(productArrangement.getGuardianDetails(), request.getHeader()));
        mockScenarioHelper.expectC658Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectC234Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectC241Call(productArrangement,request.getHeader(),0);
        mockScenarioHelper.expectF060Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectFATCAUpdateSwitchCall(channelId, true);
        mockScenarioHelper.expectRecordCustomerDetails(productArrangement, request.getHeader(), false);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "SA_WELCOME_MSG", request.getHeader(), null, "Email", true);
        mockScenarioHelper.expectScheduleCommunicationCall((DepositArrangement) productArrangement, "SA_FUNDING_REM_MSG", request.getHeader(), "STPSAVINGS", "Email");

    }
    @Given("call to F062 fails for guardian details")
    public void givenCallToF062FailsForGuardianDetails() {
        mockScenarioHelper.expectF061Call(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier(), request.getHeader());
        String custId = mockScenarioHelper.expectF062Call(productArrangement.getPrimaryInvolvedParty(), request.getHeader());
        productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(custId);
        mockScenarioHelper.expectF061Call(productArrangement.getGuardianDetails().getCustomerIdentifier(), request.getHeader());
        mockScenarioHelper.expectF062CallFails(productArrangement.getGuardianDetails(), request.getHeader());

    }
    @Given("application sub status is null and a case of OAP")
    public void givenApplicationSubStatusIsNullAndACaseOfOAP() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, ErrorInfo, ScheduleCommunicationResourceNotAvailableErrorMsg, ScheduleCommunicationExternalServiceErrorMsg, ScheduleCommunicationInternalServiceErrorMsg, ScheduleCommunicationDataNotAvailableErrorMsg, ScheduleCommunicationExternalBusinessErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
           channelId = mockScenarioHelper.expectChannelIdByContactPointID();
           mockScenarioHelper.expectLookUpValues();
           mockScenarioHelper.expectReferenceDataForPAM();
           KycStatus eidvStatus = new KycStatus("ACCEPT");
           ApplicationStatus applicationStatus1=new ApplicationStatus("1007","AwaitingManualIdv");
           request = testDataHelper.createApaRequestForSaForOAPCase();
           productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestDataWithNiNumberWithoutGuardianDetails(applicationStatus1, eidvStatus, "1", channelId, request, null, "123", productTypesSaving);
           request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
           request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
           request.getHeader().setContactPointId("0000777505");
           mockScenarioHelper.expectRpcCall((DepositArrangement) productArrangement, request.getHeader());
           mockScenarioHelper.expectCreateAccountB675Call(request.getHeader(), productArrangement);
           mockScenarioHelper.expectF425Call(request.getHeader(), productArrangement.getArrangementId(), "025", "1", "1");
           mockScenarioHelper.expectB766Call(request.getHeader(), request.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode());
           mockScenarioHelper.expectLookUpValuesWithISOCode();
           String userName=mockScenarioHelper.expectB751CallWithTacver(productArrangement, request.getHeader(), BigInteger.valueOf(227323270), productArrangement.getAccountNumber(), BigInteger.valueOf(971461460), BigInteger.ZERO);
           productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().setProfile(new InternetBankingProfile());
           productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().getProfile().setUserName(userName);
           mockScenarioHelper.expectSendCommunicationCall(productArrangement, "IB_STP_LITE_REGISTRATION_SUCCESS_MAIL", request.getHeader(), null, "Email", true);
           mockScenarioHelper.expectF061Call(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier(), request.getHeader());
           String custId = mockScenarioHelper.expectF062Call(productArrangement.getPrimaryInvolvedParty(), request.getHeader());
           productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(custId);
           mockScenarioHelper.expectC658Call(productArrangement, request.getHeader(), 0);
           mockScenarioHelper.expectC234Call(productArrangement, request.getHeader(), 0);
           mockScenarioHelper.expectF060Call(productArrangement, request.getHeader(), 0);
           mockScenarioHelper.expectFATCAUpdateSwitchCall(channelId, true);
           mockScenarioHelper.expectRecordCustomerDetails(productArrangement, request.getHeader(), false);
           mockScenarioHelper.expectSendCommunicationCall(productArrangement, "SA_WELCOME_MSG", request.getHeader(), null, "Email", false);
           mockScenarioHelper.expectScheduleCommunicationCall((DepositArrangement) productArrangement, "SA_FUNDING_REM_MSG", request.getHeader(), "STPSAVINGS", "Email");
    }
    @Then("update customer id and sub status as null")
    public void thenUpdateCustomerIdAndSubStatusAsNull() {
        Assert.assertNotNull(response);
        assertEquals("1010", response.getProductArrangement().getApplicationStatus());
        assertNull(response.getProductArrangement().getRetryCount());
        assertEquals("1018",response.getProductArrangement().getApplicationSubStatus());
        mockScenarioHelper.verifyExpectCalls();
    }

}