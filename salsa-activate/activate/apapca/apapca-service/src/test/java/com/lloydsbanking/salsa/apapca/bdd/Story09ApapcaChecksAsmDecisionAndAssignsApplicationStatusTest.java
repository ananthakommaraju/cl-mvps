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
import org.junit.experimental.categories.Category;

import java.math.BigInteger;

import static org.junit.Assert.*;

@Category({AcceptanceTest.class})
public class Story09ApapcaChecksAsmDecisionAndAssignsApplicationStatusTest extends AbstractApapcaJBehaveTestBase {
    private static final String STATUS_APPROVED_1 = "1";
    private static final String STATUS_REFERRED_2 = "2";
    private static final String STATUS_DECLINED_3 = "3";
    private static final String SOURCE_SYSTEM_ID_ONLINE = "1";
    private static final String SOURCE_SYSTEM_ID_OFFLINE = "4";
    ActivateProductArrangementRequest request;
    ActivateProductArrangementResponse response;
    ActivateProductArrangementDataNotAvailableErrorMsg dataNotAvailableErrorMsg;
    ActivateProductArrangementResourceNotAvailableErrorMsg resourceNotAvailableErrorMsg;
    ActivateProductArrangementExternalBusinessErrorMsg externalBusinessErrorMsg;
    ActivateProductArrangementInternalSystemErrorMsg internalSystemErrorMsg;
    ActivateProductArrangementExternalSystemErrorMsg externalSystemErrorMsg;
    ProductArrangement productArrangement;
    ProductTypes productTypesCurrent = new ProductTypes("102", "Current Account");


    @BeforeScenario
    public void resetData() {
        mockControl.reset();
        mockScenarioHelper.clearUp();
        request = null;
        response = null;
        dataNotAvailableErrorMsg = null;
        resourceNotAvailableErrorMsg = null;
        externalBusinessErrorMsg = null;
        internalSystemErrorMsg = null;
        externalSystemErrorMsg = null;
    }

    @Given("there is call to AdministerReferredArrangement for referred application")
    public void givenThereIsCallToAdministerReferredArrangementForReferredApplication() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        ApplicationStatus applicationStatus = new ApplicationStatus("1003", "Referred");
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForPca();
        request.getProductArrangement().setApplicationSubStatus("10027");
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypesCurrent);

        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");
        mockScenarioHelper.expectF425Call(request.getHeader(), productArrangement.getArrangementId(), "025", "1", STATUS_REFERRED_2);
    }

    @Given("call to ASM F425 gives error")
    public void givenCallToASMF425GivesError() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        ApplicationStatus applicationStatus = new ApplicationStatus("1003", "Referred");
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForPca();
        request.getProductArrangement().setApplicationSubStatus("10027");
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypesCurrent);

        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");

        mockScenarioHelper.expectF425CallWithError(request.getHeader(), productArrangement.getArrangementId(), "025");


    }

    @Given("credit decision by ASM with credit score source system as 025 is not decline")
    public void givenCreditDecisionByASMWithCreditScoreSourceSystemAs025IsNotDecline() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        ApplicationStatus applicationStatus = new ApplicationStatus("1003", "Referred");
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForPca();
        request.getProductArrangement().setApplicationSubStatus("10027");
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypesCurrent);

        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");

        mockScenarioHelper.expectF425Call(request.getHeader(), productArrangement.getArrangementId(), "012", "1", STATUS_REFERRED_2);

    }

    @Given("refreshed credit decision is refer at ASM")
    public void givenRefreshedCreditDecisionIsReferAtASM() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        ApplicationStatus applicationStatus = new ApplicationStatus("1003", "Referred");
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForPca();
        request.getProductArrangement().setApplicationSubStatus("10027");
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypesCurrent);

        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");

        mockScenarioHelper.expectF425Call(request.getHeader(), productArrangement.getArrangementId(), "012", "1", STATUS_REFERRED_2);

    }

    @Given("APAPCA is invoked by galaxy online")
    public void givenAPAPCAIsInvokedByGalaxyOnline() {
        request.setSourceSystemIdentifier(SOURCE_SYSTEM_ID_ONLINE);
    }

    @Given("APAPCA is invoked by galaxy offline")
    public void givenAPAPCAIsInvokedByGalaxyOffline() {
        request.setSourceSystemIdentifier(SOURCE_SYSTEM_ID_OFFLINE);
    }

    @Given("call to sendCommunication for referred succeeds")
    public void givenCallToSendCommunicationForReferredSucceeds() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_ONLINETOOFFLINE_MSG", request.getHeader(), true);
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferralsTeamDetails("209178");
        mockScenarioHelper.expectX741Call(1, productArrangement, request.getHeader());
    }

    @Given("call to sendCommunication for referred gives error")
    public void givenCallToSendCommunicationForReferredGivesError() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCallWithError((DepositArrangement) productArrangement, "CA_ONLINETOOFFLINE_MSG", request.getHeader());
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferralsTeamDetails("209178");
        mockScenarioHelper.expectX741Call(1, productArrangement, request.getHeader());
    }

    @Given("call to sendCommunication for decline succeeds")
    public void givenCallToSendCommunicationForDeclineSucceeds() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_DECLINE_BANK_MSG", request.getHeader(), true);
    }

    @Given("call to sendCommunication for decline gives error")
    public void givenCallToSendCommunicationForDeclineGivesError() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCallWithError((DepositArrangement) productArrangement, "CA_DECLINE_BANK_MSG", request.getHeader());
    }

    @Given("call to retrieveLookupValues with REFERRAL_TEAM_GROUPS succeeds")
    public void givenCallToRetrieveLookupValuesWithREFERRAL_TEAM_GROUPSSucceeds() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_ONLINETOOFFLINE_MSG", request.getHeader(), true);
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferralsTeamDetails("209178");
        mockScenarioHelper.expectX741Call(1, productArrangement, request.getHeader());
    }

    @Given("call to retrieveLookupValues with REFERRAL_TEAM_GROUPS fails")
    public void givenCallToRetrieveLookupValuesWithREFERRAL_TEAM_GROUPSFails() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_ONLINETOOFFLINE_MSG", request.getHeader(), true);
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferralsTeamDetails("209178");
        mockScenarioHelper.expectX741Call(1, productArrangement, request.getHeader());
    }

    @Given("call to retrieveReferralTeamDetails succeeds")
    public void givenCallToReferralTeamDetailsSucceeds() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_ONLINETOOFFLINE_MSG", request.getHeader(), true);
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferralsTeamDetails("209178");
        mockScenarioHelper.expectX741Call(1, productArrangement, request.getHeader());
    }

    @Given("call to retrieveReferralTeamDetails fails")
    public void givenCallToReferralTeamDetailsFails() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_ONLINETOOFFLINE_MSG", request.getHeader(), true);
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferralsTeamDetails("2");
        mockScenarioHelper.expectX741Call(1, productArrangement, request.getHeader());
    }

    @Given("call to task creation succeeds")
    public void givenCallToTaskCreationSucceeds() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_ONLINETOOFFLINE_MSG", request.getHeader(), true);
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferralsTeamDetails("209178");
        mockScenarioHelper.expectX741Call(1, productArrangement, request.getHeader());
    }

    @Given("call to task creation fails")
    public void givenCallToTaskCreationFails() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_ONLINETOOFFLINE_MSG", request.getHeader(), true);
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferralsTeamDetails("209178");
        mockScenarioHelper.expectX741Call(null, productArrangement, request.getHeader());
    }

    @Given("refreshed credit decision is decline at ASM")
    public void givenRefreshedCreditDecisionIsDeclineAtASM() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        ApplicationStatus applicationStatus = new ApplicationStatus("1003", "Referred");
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForPca();
        request.getProductArrangement().setApplicationSubStatus("10027");
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypesCurrent);

        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");

        mockScenarioHelper.expectF425Call(request.getHeader(), productArrangement.getArrangementId(), "012", "1", STATUS_DECLINED_3);
    }

    @Given("refreshed credit decision is approved at ASM")
    public void givenRefreshedCreditDecisionIsApproveAtASM() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ErrorInfo, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        ApplicationStatus applicationStatus = new ApplicationStatus("1003", "Referred");
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        mockScenarioHelper.expectLookUpValuesWithISOCode();
        request = testDataHelper.createApaRequestForPca();
        request.getProductArrangement().setApplicationSubStatus("10027");
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypesCurrent);
        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");
        mockScenarioHelper.expectF425Call(request.getHeader(), productArrangement.getArrangementId(), "012", "1", STATUS_APPROVED_1);
        mockScenarioHelper.expectB766Call(request.getHeader(), "779129");
        String accountNumber = mockScenarioHelper.expectE229Call(request.getHeader(), "779129");
        productArrangement.setAccountNumber(accountNumber);
        mockScenarioHelper.expectRetrieveProductCondition((DepositArrangement) productArrangement, request.getHeader());
        mockScenarioHelper.expectCreateAccountB675Call(request.getHeader(), productArrangement);
        String sortCode = productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode();
        mockScenarioHelper.expectC808Call(sortCode, accountNumber, Long.valueOf(request.getProductArrangement().getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC846Call("0071776000", "1", "R", 50, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC812Call("0071776000", "1", sortCode, 6363l, 4, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), accountNumber, request.getHeader());
        mockScenarioHelper.expectB751CallWithAppIdAndAppVer(productArrangement, request.getHeader(), BigInteger.valueOf(227323270), accountNumber, BigInteger.valueOf(971461460), BigInteger.ZERO);
        //TODO need to populate channel from correct location
        mockScenarioHelper.expectE226AddsOverdraftDetail(Long.valueOf(productArrangement.getArrangementId()), "LTB", request.getHeader());
        mockScenarioHelper.expectC658Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectC234Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectF060Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectActivateBenefitCall(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectFATCAUpdateSwitchCall(channelId, true);
        mockScenarioHelper.expectRecordCustomerDetails(productArrangement, request.getHeader(), false);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_WELCOME_MSG", request.getHeader(), null, "Email", true);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_BENEFITS_MSG", request.getHeader(), null, "Email", false);
    }

    @When("there is a call to APAPCA")
    public void whenThereIsACallToAPAPCA() {
        mockControl.go();
        try {
            response = apaPcaClient.activateProductArrangement(request);
        } catch (ActivateProductArrangementExternalSystemErrorMsg activateProductArrangementExternalSystemErrorMsg) {
            externalSystemErrorMsg = activateProductArrangementExternalSystemErrorMsg;
        } catch (ActivateProductArrangementExternalBusinessErrorMsg activateProductArrangementExternalBusinessErrorMsg) {
            externalBusinessErrorMsg = activateProductArrangementExternalBusinessErrorMsg;
        } catch (ActivateProductArrangementInternalSystemErrorMsg activateProductArrangementInternalSystemErrorMsg) {
            internalSystemErrorMsg = activateProductArrangementInternalSystemErrorMsg;
        } catch (ActivateProductArrangementResourceNotAvailableErrorMsg activateProductArrangementResourceNotAvailableErrorMsg) {
            resourceNotAvailableErrorMsg = activateProductArrangementResourceNotAvailableErrorMsg;
        } catch (ActivateProductArrangementDataNotAvailableErrorMsg activateProductArrangementDataNotAvailableErrorMsg) {
            dataNotAvailableErrorMsg = activateProductArrangementDataNotAvailableErrorMsg;
        }
    }

    @When("there is a call to APAPCA with approved ASM")
    public void whenThereIsACallToAPAPCAWithApprovedASM() {
        mockControl.go();
        try {
            response = apaPcaClient.activateProductArrangement(request);
        } catch (ActivateProductArrangementExternalSystemErrorMsg activateProductArrangementExternalSystemErrorMsg) {
            externalSystemErrorMsg = activateProductArrangementExternalSystemErrorMsg;
        } catch (ActivateProductArrangementExternalBusinessErrorMsg activateProductArrangementExternalBusinessErrorMsg) {
            externalBusinessErrorMsg = activateProductArrangementExternalBusinessErrorMsg;
        } catch (ActivateProductArrangementInternalSystemErrorMsg activateProductArrangementInternalSystemErrorMsg) {
            internalSystemErrorMsg = activateProductArrangementInternalSystemErrorMsg;
        } catch (ActivateProductArrangementResourceNotAvailableErrorMsg activateProductArrangementResourceNotAvailableErrorMsg) {
            resourceNotAvailableErrorMsg = activateProductArrangementResourceNotAvailableErrorMsg;
        } catch (ActivateProductArrangementDataNotAvailableErrorMsg activateProductArrangementDataNotAvailableErrorMsg) {
            dataNotAvailableErrorMsg = activateProductArrangementDataNotAvailableErrorMsg;
        }
        mockScenarioHelper.sleep();
    }

    @Then("call ASM API F425 to retrieve updated ASM decision with credit score source system as 025")
    public void thenCallASMAPIF425ToRetrieveUpdatedASMDecisionWithCreditScoreSourceSystemAs025() {
        assertNotNull(response);
        assertEquals("1003", response.getProductArrangement().getApplicationStatus());
        assertNull(response.getProductArrangement().getRetryCount());
        assertNull(response.getProductArrangement().getApplicationSubStatus());
    }

    @Then("update application status as awaiting referral Processing")
    public void thenUpdateApplicationStatusAsAwaitingReferralProcessing() {
        assertNotNull(response);
        assertEquals("1008", response.getProductArrangement().getApplicationStatus());
    }

    @Then("call ASM API F425 to retrieve updated ASM decision with credit score source system as 012")
    public void thenCallASMAPIF425ToRetrieveUpdatedASMDecisionWithCreditScoreSourceSystemAs012() {
        assertNotNull(response);
        assertEquals("1003", response.getProductArrangement().getApplicationStatus());
        assertNull(response.getProductArrangement().getRetryCount());
    }

    @Then("update application status as no modification required")
    public void thenUpdateApplicationStatusAsNoModificationRequired() {
        assertNotNull(response);
        assertEquals("1003", response.getProductArrangement().getApplicationStatus());
        assertNull(response.getProductArrangement().getRetryCount());
    }

    @Then("service continues")
    public void thenServiceContinues() {
        assertNotNull(response);
    }

    @Then("call retrieveLookupValues again with REFERRAL_TEAM_GROUPS and code_text 001")
    public void thenCallRetrieveLookupValuesAgainWithREFERRAL_TEAM_GROUPSAndCode_text001() {
        assertNotNull(response);
    }

    @Then("service returns error")
    public void thenServiceReturnsError() {
        assertNotNull(dataNotAvailableErrorMsg);
    }

    @Then("service set referral details in response")
    public void thenServiceSetReferralDetailsInResponse() {
        assertNotNull(response);
        assertNotNull(response.getProductArrangement().getReferral());
    }

    @Then("service set extra conditions in response")
    public void thenServiceSetExtraConditionsInResponse() {
        assertNotNull(response);
        assertNotNull(response.getResultCondition().getExtraConditions());
    }

    @Then("update application status as decline")
    public void thenUpdateApplicationStatusAsDecline() {
        assertNotNull(response);
        assertEquals("1004", response.getProductArrangement().getApplicationStatus());
    }

    @Then("update application status as approved")
    public void thenUpdateApplicationStatusAsApproved() {
        assertNotNull(response);
        assertEquals("1010", response.getProductArrangement().getApplicationStatus());
    }
}
