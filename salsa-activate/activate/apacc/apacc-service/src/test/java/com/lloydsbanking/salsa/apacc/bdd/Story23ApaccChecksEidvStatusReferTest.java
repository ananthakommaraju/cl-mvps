package com.lloydsbanking.salsa.apacc.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ApplicationsDao;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.KycStatus;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.F241Resp;
import com.lloydstsb.schema.involvedpartymanagement.ifw.ErrorInfo;
import lib_sim_bo.businessobjects.*;
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
import org.junit.Assert;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
//Not running it on wps,because response header is getting updated on SALSA but not on WPS
@Category({AcceptanceTest.class})
public class Story23ApaccChecksEidvStatusReferTest extends AbstractApaccJBehaveTestBase {

    private static final String STATUS_APPROVED_1 = "1";
    private static final String STATUS_REFERRED_2 = "2";
    private static final String STATUS_DECLINED_3 = "3";
    private static final String SOURCE_SYSTEM_ID_ONLINE = "1";
    private static final String SOURCE_SYSTEM_ID_OFFLINE = "4";

    ActivateProductArrangementDataNotAvailableErrorMsg dataNotAvailableErrorMsg;
    ActivateProductArrangementResourceNotAvailableErrorMsg resourceNotAvailableErrorMsg;
    ActivateProductArrangementExternalBusinessErrorMsg externalBusinessErrorMsg;
    ActivateProductArrangementInternalSystemErrorMsg internalSystemErrorMsg;
    ActivateProductArrangementExternalSystemErrorMsg externalSystemErrorMsg;
    ActivateProductArrangementRequest request;
    ActivateProductArrangementResponse response;
    ProductTypes productTypeCreditCard = new ProductTypes("100", "Credit Card");
    ProductArrangement productArrangement;
    RequestHeader header;
    List<ProductOffer> productOfferList;
    @Autowired
    ApplicationsDao applicationsDao;

    @BeforeScenario
    public void resetData() {
        mockScenarioHelper.clearUp();
        request = null;
        response = null;
        productOfferList = new ArrayList<>();
        dataNotAvailableErrorMsg = null;
        resourceNotAvailableErrorMsg = null;
        externalBusinessErrorMsg = null;
        internalSystemErrorMsg = null;
        externalSystemErrorMsg = null;
    }

    @Given("updated Application status is approved")
    public void givenUpdatedApplicationStatusIsApproved() {
        mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferenceDataForPAM();
    }
    @Given("related application id is not present in request")
    public void givenRelatedApplicationIdIsNotPresentInRequest() {
    }
    @Given("eidv status is refer")
    public void givenEidvStatusIsRefer() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ErrorInfo, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        ApplicationStatus applicationStatus = new ApplicationStatus("1002", "Approved");
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("REFER");
        request = testDataHelper.createApaRequestForCc();
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypeCreditCard);

        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        header = testDataHelper.createApaRequestForCc().getHeader();
        header.setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        header.setContactPointId("0000777505");

        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "REFERTOBRANCH_MSG", header, null, "Email", true);
    }
    @When("there is call to APACC")
    public void whenThereIsCallToAPAPCAWithRelatedApplicationId() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        mockControl.go();
        response = apaccClient.activateProductArrangement(request);
        mockScenarioHelper.sleep();
    }
    @Then("call send communication and set application status as manual idv")
    public void whenCallSendCommunicationAndSetApplicationStatusAsManualIdv() {
        assertNotNull(response);
        mockScenarioHelper.verifyExpectCalls();
    }


    @Given("call to ASM F425 gives error")
    public void givenCallToASMF425GivesError() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        ApplicationStatus applicationStatus = new ApplicationStatus("1003", "Referred");
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectReferenceDataForPAM();
        mockScenarioHelper.expectLookUpValues();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForCc();
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypeCreditCard);
        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        header = testDataHelper.createApaRequestForCc().getHeader();
        header.setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        header.setContactPointId("0000777505");
        //mockScenarioHelper.expectRetrieveProductCondition();

        mockScenarioHelper.expectF425CallWithError(header, productArrangement.getArrangementId(), "024");

    }

    @Given("refreshed credit decision is refer at ASM")
    public void givenRefreshedCreditDecisionIsReferAtASM() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        ApplicationStatus applicationStatus = new ApplicationStatus("1003", "Referred");
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectReferenceDataForPAM();
        mockScenarioHelper.expectLookUpValues();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForCc();
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypeCreditCard);
        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        header = testDataHelper.createApaRequestForCc().getHeader();
        header.setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        header.setContactPointId("0000777505");
        //mockScenarioHelper.expectRetrieveProductCondition();

        mockScenarioHelper.expectF425Call(header, productArrangement.getArrangementId(), "024", "1", STATUS_REFERRED_2);
    }

    @Given("APAPCC is invoked by galaxy online")
    public void givenAPAPCCIsInvokedByGalaxyOnline() {
        request.setSourceSystemIdentifier(SOURCE_SYSTEM_ID_ONLINE);
    }

    @Given("APAPCC is invoked by galaxy offline")
    public void givenAPAPCCIsInvokedByGalaxyOffline() {
        request.setSourceSystemIdentifier(SOURCE_SYSTEM_ID_OFFLINE);
    }

    @Given("call to sendCommunication for referred succeeds")
    public void givenCallToSendCommunicationForReferredSucceeds() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "ONLINETOOFFLINE_MSG", header, null, "Email", true);
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferralsTeamDetails("209178");
        mockScenarioHelper.expectX741Call(1, productArrangement, request.getHeader());
    }

    @Given("call to sendCommunication for referred gives error")
    public void givenCallToSendCommunicationForReferredGivesError() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCallWithError(productArrangement, "ONLINETOOFFLINE_MSG", header);
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferralsTeamDetails("209178");
        mockScenarioHelper.expectX741Call(1, productArrangement, header);
    }

    @Given("call to sendCommunication for decline succeeds")
    public void givenCallToSendCommunicationForDeclineSucceeds() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "DECLINE_BANK_MSG", header, null, "Email", true);
    }

    @Given("call to sendCommunication for decline gives error")
    public void givenCallToSendCommunicationForDeclineGivesError() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCallWithError(productArrangement, "DECLINE_BANK_MSG", header);
    }

    @Given("call to retrieveLookupValues with REFERRAL_TEAM_GROUPS succeeds")
    public void givenCallToRetrieveLookupValuesWithREFERRAL_TEAM_GROUPSSucceeds() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "ONLINETOOFFLINE_MSG", header, null, "Email", true);
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferralsTeamDetails("209178");
        mockScenarioHelper.expectX741Call(1, productArrangement, header);
    }

    @Given("call to retrieveLookupValues with REFERRAL_TEAM_GROUPS fails")
    public void givenCallToRetrieveLookupValuesWithREFERRAL_TEAM_GROUPSFails() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "ONLINETOOFFLINE_MSG", header, null, "Email", true);
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferralsTeamDetails("209178");
        mockScenarioHelper.expectX741Call(1, productArrangement, header);
    }

    @Given("call to retrieveReferralTeamDetails succeeds")
    public void givenCallToReferralTeamDetailsSucceeds() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "ONLINETOOFFLINE_MSG", header, null, "Email", true);
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferralsTeamDetails("209178");
        mockScenarioHelper.expectX741Call(1, productArrangement, header);
    }


    @Given("call to task creation succeeds")
    public void givenCallToTaskCreationSucceeds() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "ONLINETOOFFLINE_MSG", header, null, "Email", true);
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferralsTeamDetails("209178");
        mockScenarioHelper.expectX741Call(1, productArrangement, header);
    }

    @Given("call to task creation fails")
    public void givenCallToTaskCreationFails() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "ONLINETOOFFLINE_MSG", header, null, "Email", true);
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferralsTeamDetails("209178");
        mockScenarioHelper.expectX741Call(null, productArrangement, header);
    }

    @Given("refreshed credit decision is decline at ASM")
    public void givenRefreshedCreditDecisionIsDeclineAtASM() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        ApplicationStatus applicationStatus = new ApplicationStatus("1003", "Referred");
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectReferenceDataForPAM();
        mockScenarioHelper.expectLookUpValues();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForCc();
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypeCreditCard);
        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        header = testDataHelper.createApaRequestForCc().getHeader();
        header.setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        header.setContactPointId("0000777505");
        //mockScenarioHelper.expectRetrieveProductCondition();

        mockScenarioHelper.expectF425Call(header, productArrangement.getArrangementId(), "024", "1", STATUS_DECLINED_3);
    }

    @Given("refreshed credit decision is approved at ASM")
    public void givenRefreshedCreditDecisionIsApproveAtASM() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, GenerateDocumentResourceNotAvailableErrorMsg, GenerateDocumentInternalServiceErrorMsg, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        ApplicationStatus applicationStatus = new ApplicationStatus("1003", "Referred");
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectReferenceDataForPAM();
        mockScenarioHelper.expectLookUpValues();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForCc();
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypeCreditCard);
        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        header = testDataHelper.createApaRequestForCc().getHeader();
        header.setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        header.setContactPointId("0000777505");

        mockScenarioHelper.expectF425Call(header, productArrangement.getArrangementId(), "024", "1", STATUS_APPROVED_1);
        productOfferList.addAll(mockScenarioHelper.expectRetrieveProductCondition(productArrangement.getAssociatedProduct(), header));
        F241Resp f241Resp = mockScenarioHelper.expectCardAccountV1Call(productArrangement, request.getHeader(), "0");
        ( productArrangement).getPrimaryInvolvedParty().setPartyIdentifier(f241Resp.getCustomerNumberExternalId());
        String cardNo = f241Resp.getCardData().get(0).getCardNo();
        String cardNumber = f241Resp.getCardData().get(0).getCardNo().length() == 19 ? cardNo.substring(3) : cardNo;
        ((FinanceServiceArrangement) productArrangement).setCreditCardNumber(cardNumber);
        productArrangement.setAccountNumber(f241Resp.getAccountNumberExternalId());
        mockScenarioHelper.expectF259Call((FinanceServiceArrangement)productArrangement,request.getHeader(),"0");
        mockScenarioHelper.expectF251Call((FinanceServiceArrangement)productArrangement,request.getHeader(),"0000777505");
        mockScenarioHelper.expectGenerateDocumentCall(productArrangement, productOfferList, header);
        String username = mockScenarioHelper.expectB751Call(productArrangement, header, -5);
        updateUsernameInRequest(username, (FinanceServiceArrangement) productArrangement);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "IB_STP_REGISTRATION_SUCCESS_MAIL", header, null, "Email", true);
    }

    @When("there is a call to APAPCC")
    public void whenThereIsACallToAPAPCC() {
        mockControl.go();
        try {
            response = apaccClient.activateProductArrangement(request);
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

    @Then("update application status as awaiting referral Processing")
    public void thenUpdateApplicationStatusAsAwaitingReferralProcessing() {
        Assert.assertNotNull(response);
        mockScenarioHelper.verifyExpectCalls();
    }


    @Then("service continues")
    public void thenServiceContinues() {
        Assert.assertNotNull(response);
        mockScenarioHelper.verifyExpectCalls();
    }

    @Then("call retrieveLookupValues again with REFERRAL_TEAM_GROUPS and code_text 001")
    public void thenCallRetrieveLookupValuesAgainWithREFERRAL_TEAM_GROUPSAndCode_text001() {
        Assert.assertNotNull(response);
        mockScenarioHelper.verifyExpectCalls();
    }

    @Then("service set referral details in response")
    public void thenServiceSetReferralDetailsInResponse() {
        Assert.assertNotNull(response);
        mockScenarioHelper.verifyExpectCalls();
    }

    @Then("service set extra conditions in response")
    public void thenServiceSetExtraConditionsInResponse() {
        Assert.assertNotNull(response);
        mockScenarioHelper.verifyExpectCalls();
    }

    @Then("update application status as decline")
    public void thenUpdateApplicationStatusAsDecline() {
        Assert.assertNotNull(response);
        mockScenarioHelper.verifyExpectCalls();
    }



    private void updateUsernameInRequest(String username, FinanceServiceArrangement financeServiceArrangement) {
        Customer customer = new Customer();
        InternetBankingRegistration internetBankingRegistration = new InternetBankingRegistration();
        InternetBankingProfile internetBankingProfile = new InternetBankingProfile();
        internetBankingProfile.setUserName(username);
        internetBankingRegistration.setProfile(internetBankingProfile);
        customer.setIsRegisteredIn(internetBankingRegistration);
        financeServiceArrangement.getJointParties().add(customer);
    }
}

