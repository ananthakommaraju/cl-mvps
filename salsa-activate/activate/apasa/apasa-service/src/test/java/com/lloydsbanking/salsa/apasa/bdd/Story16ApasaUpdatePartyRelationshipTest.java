package com.lloydsbanking.salsa.apasa.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.KycStatus;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import com.lloydstsb.schema.involvedpartymanagement.ifw.ErrorInfo;
import lib_sim_bo.businessobjects.Customer;
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
import org.junit.experimental.categories.Category;

import java.math.BigInteger;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story16ApasaUpdatePartyRelationshipTest extends AbstractApasaJBehaveTestBase {

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
        productArrangement=null;
    }

    @Given("Sub status is null")
    public void givenSubStatusIsNull() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, ErrorInfo, ScheduleCommunicationResourceNotAvailableErrorMsg, ScheduleCommunicationExternalServiceErrorMsg, ScheduleCommunicationInternalServiceErrorMsg, ScheduleCommunicationDataNotAvailableErrorMsg, ScheduleCommunicationExternalBusinessErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValuesWithLifeStyleBenefitCode();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForSaWithLifeStyleBenefitCode();
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestDataWithNiNumber(applicationStatus, eidvStatus, "1", channelId, request, null, "123",productTypesSaving );
        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");
        request.getProductArrangement().setGuardianDetails(new Customer());
        request.getProductArrangement().getGuardianDetails().setCustomerIdentifier("1234");
        mockScenarioHelper.expectRpcCall((DepositArrangement) productArrangement, request.getHeader());
        mockScenarioHelper.expectB766Call(request.getHeader(), request.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode());
        mockScenarioHelper.expectCreateAccountB675Call(request.getHeader(), productArrangement);
        mockScenarioHelper.expectLookUpValuesWithISOCode();
        String userName=mockScenarioHelper.expectB751CallWithTacver(productArrangement, request.getHeader(), BigInteger.valueOf(227323270), productArrangement.getAccountNumber(), BigInteger.valueOf(971461460), BigInteger.ZERO);
        productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().setProfile(new InternetBankingProfile());
        productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().getProfile().setUserName(userName);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "IB_STP_LITE_REGISTRATION_SUCCESS_MAIL", request.getHeader(), null, "Email", true);
        mockScenarioHelper.expectC658Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectC234Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectF060Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectC241Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectActivateBenefitCall(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectFATCAUpdateSwitchCall(channelId, true);
        mockScenarioHelper.expectRecordCustomerDetails(productArrangement, request.getHeader(), false);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "SA_WELCOME_MSG", request.getHeader(), null, "Email", false);
        mockScenarioHelper.expectScheduleCommunicationCall((DepositArrangement) productArrangement, "SA_FUNDING_REM_MSG", request.getHeader(), "STPSAVINGS", "Email");


    }

    @When("UI calls APAD")
    public void whenUICallsAPAD() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        mockControl.go();
        response = apaSaClient.activateProductArrangement(request);
        mockScenarioHelper.sleep();
    }

    @Then("call OCIS API C241 to update party relationship.")
    public void thenCallOCISAPIC241ToUpdatePartyRelationship() {
        assertNotNull(response);
        mockScenarioHelper.verifyExpectCalls();
    }

    @Given("Sub status is Update Life Style Benefit(1029)")
    public void givenSubStatusIsUpdateLifeStyleBenefit1029() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValuesWithLifeStyleBenefitCode();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForSaWithLifeStyleBenefitCode();
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestDataWithNiNumber(applicationStatus, eidvStatus, "1", channelId, request, "1029", "123", productTypesSaving);
        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");
        request.getProductArrangement().setApplicationSubStatus("1029");
        mockScenarioHelper.expectRpcCall((DepositArrangement) productArrangement, request.getHeader());
        mockScenarioHelper.expectLookUpValuesWithISOCode();
        mockScenarioHelper.expectB766Call(request.getHeader(), request.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode());

    }

    @Given("update life style benefit call is successful")
    public void givenUpdateLifeStyleBenefitCallIsSuccessful() throws ErrorInfo, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, ScheduleCommunicationResourceNotAvailableErrorMsg, ScheduleCommunicationExternalServiceErrorMsg, ScheduleCommunicationInternalServiceErrorMsg, ScheduleCommunicationDataNotAvailableErrorMsg, ScheduleCommunicationExternalBusinessErrorMsg {
        mockScenarioHelper.expectF060Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectC241Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectActivateBenefitCall(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectFATCAUpdateSwitchCall(channelId, true);
        mockScenarioHelper.expectRecordCustomerDetails(productArrangement, request.getHeader(), false);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "SA_WELCOME_MSG", request.getHeader(), null, "Email", true);
        mockScenarioHelper.expectScheduleCommunicationCall((DepositArrangement) productArrangement, "SA_FUNDING_REM_MSG", request.getHeader(), "STPSAVINGS", "Email");

    }

    @Given("Sub status is Party Relationship Update Failure(1031)")
    public void givenSubStatusIsPartyRelationshipUpdateFailure1031() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForSaFor502();
        request.getProductArrangement().setApplicationSubStatus("1031");
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestDataWithNiNumber(applicationStatus, eidvStatus, "1", channelId, request, "1031", "123", productTypesSaving);
        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");
        mockScenarioHelper.expectRpcCall((DepositArrangement) productArrangement, request.getHeader());
        mockScenarioHelper.expectLookUpValuesWithISOCode();
        mockScenarioHelper.expectB766Call(request.getHeader(), request.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode());

    }

    @Given("C241 call fails")
    public void givenC241CallFails() {
        mockScenarioHelper.expectC241CallFails(productArrangement, request.getHeader(), 0);
    }
    @Given("C241 call succeeds")
    public void givenC241CallSucceeds() throws ErrorInfo, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, ScheduleCommunicationResourceNotAvailableErrorMsg, ScheduleCommunicationExternalServiceErrorMsg, ScheduleCommunicationInternalServiceErrorMsg, ScheduleCommunicationDataNotAvailableErrorMsg, ScheduleCommunicationExternalBusinessErrorMsg {
        mockScenarioHelper.expectC241Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectFATCAUpdateSwitchCall(channelId, true);
        mockScenarioHelper.expectRecordCustomerDetails(productArrangement, request.getHeader(), false);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "SA_WELCOME_MSG", request.getHeader(), null, "Email", true);
        mockScenarioHelper.expectScheduleCommunicationCall((DepositArrangement) productArrangement, "SA_FUNDING_REM_MSG", request.getHeader(), "STPSAVINGS", "Email");

    }

    @Then("update application status to Awaiting Fulfilment(1009), sub-status to Party Relationship Update Failure(1031) and increment Retry count in PAM")
    public void thenUpdateApplicationStatusToAwaitingFulfilment1009SubstatusToPartyRelationshipUpdateFailure1031AndIncrementRetryCountInPAM() {
        assertNotNull(response);
        mockScenarioHelper.verifyExpectCalls();
    }

    @Then("call OCIS API C241 to update party relationship with subStatus 1031.")
    public void thenCallOCISAPIC241ToUpdatePartyRelationshipWithSubStatus1031() {
        assertNotNull(response);
        assertEquals("1010", response.getProductArrangement().getApplicationStatus());
        assertNull(response.getProductArrangement().getRetryCount());
        assertEquals("1031",response.getProductArrangement().getApplicationSubStatus());
        mockScenarioHelper.verifyExpectCalls();
    }
    @Then("call OCIS API C241 to update party relationship with subStatus 1029.")
    public void thenCallOCISAPIC241ToUpdatePartyRelationshipWithSubStatus1029() {
        assertNotNull(response);
        assertEquals("1010", response.getProductArrangement().getApplicationStatus());
        assertNull(response.getProductArrangement().getRetryCount());
        assertEquals("1029",response.getProductArrangement().getApplicationSubStatus());
        mockScenarioHelper.verifyExpectCalls();
    }
    @Then("call OCIS API C241 to update party relationship with subStatus null.")
    public void thenCallOCISAPIC241ToUpdatePartyRelationshipWithSubStatusNull() {
        assertNotNull(response);
        assertEquals("1010", response.getProductArrangement().getApplicationStatus());
        assertNull(response.getProductArrangement().getRetryCount());
        assertNull(response.getProductArrangement().getApplicationSubStatus());
        mockScenarioHelper.verifyExpectCalls();
    }
}
