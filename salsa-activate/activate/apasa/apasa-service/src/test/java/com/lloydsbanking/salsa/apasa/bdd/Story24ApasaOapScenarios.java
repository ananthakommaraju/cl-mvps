package com.lloydsbanking.salsa.apasa.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
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
import org.junit.experimental.categories.Category;

import java.math.BigInteger;

import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class})
public class Story24ApasaOapScenarios extends AbstractApasaJBehaveTestBase {
    private static final String STATUS_APPROVED_1 = "1";
    private static final String STATUS_REFERRED_2 = "2";
    private static final String STATUS_DECLINED_3 = "3";
    private static final String SOURCE_SYSTEM_ID_OAP = "3";
    ActivateProductArrangementRequest request;
    ActivateProductArrangementResponse response;
    ActivateProductArrangementDataNotAvailableErrorMsg dataNotAvailableErrorMsg;
    ActivateProductArrangementResourceNotAvailableErrorMsg resourceNotAvailableErrorMsg;
    ActivateProductArrangementExternalBusinessErrorMsg externalBusinessErrorMsg;
    ActivateProductArrangementInternalSystemErrorMsg internalSystemErrorMsg;
    ActivateProductArrangementExternalSystemErrorMsg externalSystemErrorMsg;
    ProductArrangement productArrangement;
    RequestHeader header;
    ProductTypes productTypesSavings = new ProductTypes("101", "Savings Account");


    @BeforeScenario
    public void resetData() {
        mockScenarioHelper.clearUp();
        request = null;
        response = null;
        dataNotAvailableErrorMsg = null;
        resourceNotAvailableErrorMsg = null;
        externalBusinessErrorMsg = null;
        internalSystemErrorMsg = null;
        externalSystemErrorMsg = null;
        header = null;
    }
    @Given("refreshed credit decision is decline at ASM and application status is awaiting manual IDV")
    public void givenRefreshedCreditDecisionIsDeclineAtASMAndApplicationStatusIsAwaitingManualIDV() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        ApplicationStatus applicationStatus = new ApplicationStatus("1007", "Awaiting manual IDV");
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectReferenceDataForPAM();
        mockScenarioHelper.expectLookUpValues();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForSa();
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypesSavings);
        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        header = testDataHelper.createApaRequestForSa().getHeader();
        header.setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        header.setContactPointId("0000777505");
        mockScenarioHelper.expectF425Call(header, productArrangement.getArrangementId(), "025", "1", STATUS_DECLINED_3);
    }
    @Given("APASA is invoked by galaxy OAP")
    public void givenAPASAIsInvokedByGalaxyOAP() {
        request.setSourceSystemIdentifier(SOURCE_SYSTEM_ID_OAP);
    }
    @Given("call to sendCommunication for decline succeeds")
    public void givenCallToSendCommunicationForDeclineSucceeds() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "SA_DECLINE_BANK_MSG", header, null, "Email", true);
    }
    @When("there is a call to APASA")
    public void whenThereIsACallToAPASA() {
        mockControl.go();
        try {
            response = apaSaClient.activateProductArrangement(request);
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
    @Then("update application status as decline")
    public void thenUpdateApplicationStatusAsDecline() {
        assertNotNull(response);
        mockScenarioHelper.verifyExpectCalls();
    }
    @Given("refreshed credit decision is refer at ASM and application status is awaiting manual IDV")
    public void givenRefreshedCreditDecisionIsReferAtASMAndApplicationStatusIsAwaitingManualIDV() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        ApplicationStatus applicationStatus = new ApplicationStatus("1007", "Awaiting manual IDV");
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectReferenceDataForPAM();
        mockScenarioHelper.expectLookUpValues();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForSa();
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypesSavings);
        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        header = testDataHelper.createApaRequestForSa().getHeader();
        header.setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        header.setContactPointId("0000777505");
        mockScenarioHelper.expectF425Call(header, productArrangement.getArrangementId(), "025", "1", STATUS_REFERRED_2);
    }
    @Given("call to sendCommunication for referred succeeds")
    public void givenCallToSendCommunicationForReferredSucceeds() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "SA_ONLINETOOFFLINE_MSG", header, null, "Email", true);
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferralsTeamDetails("209178");
        mockScenarioHelper.expectX741Call(1, productArrangement, request.getHeader());
    }
    @Then("service continues")
    public void thenServiceContinues() {
        assertNotNull(response);
        mockScenarioHelper.verifyExpectCalls();
    }
    @Given("refreshed credit decision is approved at ASM and application status is awaiting manual IDV")
    public void givenRefreshedCreditDecisionIsApprovedAtASMAndApplicationStatusIsAwaitingManualIDV() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, ErrorInfo, ScheduleCommunicationResourceNotAvailableErrorMsg, ScheduleCommunicationExternalServiceErrorMsg, ScheduleCommunicationInternalServiceErrorMsg, ScheduleCommunicationDataNotAvailableErrorMsg, ScheduleCommunicationExternalBusinessErrorMsg {
        ApplicationStatus applicationStatus = new ApplicationStatus("1007", "Awaiting manual IDV");
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectReferenceDataForPAM();
        mockScenarioHelper.expectLookUpValues();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForSa();
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestDataWithNiNumber(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypesSavings);
        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        header = testDataHelper.createApaRequestForSa().getHeader();
        header.setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        header.setContactPointId("0000777505");
        mockScenarioHelper.expectRpcCall((DepositArrangement)productArrangement,header);
        mockScenarioHelper.expectCreateAccountB675Call(request.getHeader(), productArrangement);
        mockScenarioHelper.expectF425Call(header, productArrangement.getArrangementId(), "025", "1", STATUS_APPROVED_1);
        mockScenarioHelper.expectB766Call(request.getHeader(), request.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode());
        mockScenarioHelper.expectLookUpValuesWithISOCode();
        String userName =mockScenarioHelper.expectB751CallWithTacver(productArrangement, request.getHeader(), BigInteger.valueOf(227323270), productArrangement.getAccountNumber(), BigInteger.valueOf(971461460), BigInteger.ZERO);
        productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().setProfile(new InternetBankingProfile());
        productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().getProfile().setUserName(userName);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "IB_STP_LITE_REGISTRATION_SUCCESS_MAIL", header, null, "Email", true
        );
        mockScenarioHelper.expectC658Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectC234Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectF060Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectC241Call(productArrangement,request.getHeader(),0);
        mockScenarioHelper.expectFATCAUpdateSwitchCall(channelId, true);
        mockScenarioHelper.expectRecordCustomerDetails(productArrangement,header, false);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "SA_WELCOME_MSG", header, null, "Email", false);
        mockScenarioHelper.expectScheduleCommunicationCall((DepositArrangement) productArrangement, "SA_FUNDING_REM_MSG", header, "STPSAVINGS", "Email");

    }

    @When("there is a call to APASA with call of processPostFulfil")
    public void whenThereIsACallToAPASAWithCallOfProcessPostFulfil() {
        mockControl.go();
        try {
            response = apaSaClient.activateProductArrangement(request);
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

    @Then("set credit limit and application status as approved")
    public void thenSetCreditLimitAndApplicationStatusAsApproved() {
        assertNotNull(response);
        mockScenarioHelper.verifyExpectCalls();
    }
}
