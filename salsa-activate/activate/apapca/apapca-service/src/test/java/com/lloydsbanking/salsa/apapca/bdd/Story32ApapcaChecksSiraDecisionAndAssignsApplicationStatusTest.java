package com.lloydsbanking.salsa.apapca.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.KycStatus;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import com.lloydsbanking.salsa.downstream.pam.model.ReferralTeams;
import lib_sim_bo.businessobjects.*;
import lib_sim_communicationmanager.id_communicationmanagerrouter.*;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class})
public class Story32ApapcaChecksSiraDecisionAndAssignsApplicationStatusTest extends AbstractApapcaJBehaveTestBase {
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

    @Given("refreshed credit decision is refer at SIRA")
    public void givenRefreshedCreditDecisionIsReferAtSIRA() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        ApplicationStatus applicationStatus = new ApplicationStatus("1003", "Referred");
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForPca();
        request.getProductArrangement().setApplicationSubStatus("5001");
        request.getProductArrangement().setSIRAEnabledSwitch(true);
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, "5001", "123", productTypesCurrent);

        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");

    }

    @Given("APAPCA is invoked by galaxy offline")
    public void givenAPAPCAIsInvokedByGalaxyOffline() {
        request.setSourceSystemIdentifier(SOURCE_SYSTEM_ID_OFFLINE);
    }

    @Given("call to task creation fails")
    public void givenCallToTaskCreationFails() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_ONLINETOOFFLINE_MSG", request.getHeader(), true);
        mockScenarioHelper.expectLookUpValues();
        List<ReferralTeams> referralTeamsList = mockScenarioHelper.expectReferralsTeamDetailsForSira("RFU(LTSB)");
        setReferralTeamDetailsInProductArrangement(productArrangement, referralTeamsList);
        mockScenarioHelper.expectX741Call(null, productArrangement, request.getHeader());
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

    @Then("service set extra conditions in response")
    public void thenServiceSetExtraConditionsInResponse() {
        assertNotNull(response);
        assertNotNull(response.getResultCondition().getExtraConditions());
    }
    @Given("call to retrieveReferralTeamDetails succeeds")
    public void givenCallToRetrieveReferralTeamDetailsSucceeds() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_ONLINETOOFFLINE_MSG", request.getHeader(), true);
        mockScenarioHelper.expectLookUpValues();
        List<ReferralTeams> referralTeamsList = mockScenarioHelper.expectReferralsTeamDetailsForSira("RFU(LTSB)");
        setReferralTeamDetailsInProductArrangement(productArrangement, referralTeamsList);
        mockScenarioHelper.expectX741Call(1, productArrangement, request.getHeader());
    }

    @Given("call to task creation succeeds")
    public void givenCallToTaskCreationSucceeds() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_ONLINETOOFFLINE_MSG", request.getHeader(), true);
        mockScenarioHelper.expectLookUpValues();
        List<ReferralTeams> referralTeamsList = mockScenarioHelper.expectReferralsTeamDetailsForSira("RFU(LTSB)");
        setReferralTeamDetailsInProductArrangement(productArrangement, referralTeamsList);
        mockScenarioHelper.expectX741Call(1, productArrangement, request.getHeader());
    }


    @Then("service set referral details in response")
    public void thenServiceSetReferralDetailsInResponse() {
        assertNotNull(response);
        assertNotNull(response.getProductArrangement().getReferral());
    }


    @Then("service continues")
    public void thenServiceContinues() {
        assertNotNull(response);
    }



    @Given("call to retrieveLookupValues with REFERRAL_TEAM_GROUPS succeeds")
    public void givenCallToRetrieveLookupValuesWithREFERRAL_TEAM_GROUPSSucceeds() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_ONLINETOOFFLINE_MSG", request.getHeader(), true);
        mockScenarioHelper.expectLookUpValues();
        List<ReferralTeams> referralTeamsList = mockScenarioHelper.expectReferralsTeamDetailsForSira("RFU(LTSB)");
        setReferralTeamDetailsInProductArrangement(productArrangement, referralTeamsList);
        mockScenarioHelper.expectX741Call(1, productArrangement, request.getHeader());
    }

    @Given("call to sendCommunication for referred gives error")
    public void givenCallToSendCommunicationForReferredGivesError() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCallWithError((DepositArrangement) productArrangement, "CA_ONLINETOOFFLINE_MSG", request.getHeader());
        mockScenarioHelper.expectLookUpValues();
        List<ReferralTeams> referralTeamsList = mockScenarioHelper.expectReferralsTeamDetailsForSira("RFU(LTSB)");
        setReferralTeamDetailsInProductArrangement(productArrangement, referralTeamsList);
        mockScenarioHelper.expectX741Call(1, productArrangement, request.getHeader());
    }

    @Given("call to sendCommunication for referred succeeds")
    public void givenCallToSendCommunicationForReferredSucceeds() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_ONLINETOOFFLINE_MSG", request.getHeader(), true);
        mockScenarioHelper.expectLookUpValues();
        List<ReferralTeams> referralTeamsList = mockScenarioHelper.expectReferralsTeamDetailsForSira("RFU(LTSB)");
        setReferralTeamDetailsInProductArrangement(productArrangement, referralTeamsList);
        mockScenarioHelper.expectX741Call(1, productArrangement, request.getHeader());
    }


    private void setReferralTeamDetailsInProductArrangement(ProductArrangement productArrangement, List<ReferralTeams> referralTeamsList){
        Organisation organisation = new Organisation();
        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisationUnit.setOrganisationUnitIdentifer(referralTeamsList.get(0).getOuId());
        organisation.getHasOrganisationUnits().add(organisationUnit);
        productArrangement.setFinancialInstitution(organisation);
        productArrangement.getReferral().add(new Referral());
        productArrangement.getReferral().get(0).setTaskTypeId(Integer.valueOf(referralTeamsList.get(0).getTaskType()));
        productArrangement.getReferral().get(0).setTaskTypeNarrative(referralTeamsList.get(0).getName());
    }
}