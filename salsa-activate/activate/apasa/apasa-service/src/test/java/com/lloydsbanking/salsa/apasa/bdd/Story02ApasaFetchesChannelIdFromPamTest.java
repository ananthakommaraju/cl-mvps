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
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class})
public class Story02ApasaFetchesChannelIdFromPamTest extends AbstractApasaJBehaveTestBase {
    ActivateProductArrangementRequest request;

    ActivateProductArrangementResponse response;

    ProductArrangement productArrangement;

    ApplicationStatus applicationStatus;

    ActivateProductArrangementDataNotAvailableErrorMsg dataNotAvailableErrorFaultMsg;
    ProductTypes productTypesSaving = new ProductTypes("101", "Saving Account");
    String channelId;

    @BeforeScenario
    public void resetData() {
        mockScenarioHelper.clearUp();
        request = null;
        response = null;
        applicationStatus = new ApplicationStatus("1002", "Approved");
        channelId = null;
    }

    @Given("Contact point id of request is mapped in PAM and Source System Identifier is not Two")
    public void givenContactPointIdOfRequestIsMappedInPAMAndSourceSystemIdentifierIsNotTwo() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, ErrorInfo, ScheduleCommunicationResourceNotAvailableErrorMsg, ScheduleCommunicationExternalServiceErrorMsg, ScheduleCommunicationInternalServiceErrorMsg, ScheduleCommunicationDataNotAvailableErrorMsg, ScheduleCommunicationExternalBusinessErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferenceDataForPAM();
        request = testDataHelper.createApaRequestForSa();
        request.getProductArrangement().setApplicationStatus("1002");
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestDataWithNiNumber(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypesSaving);
        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");
        mockScenarioHelper.expectRpcCall((DepositArrangement) productArrangement, request.getHeader());
        mockScenarioHelper.expectCreateAccountB675Call(request.getHeader(), productArrangement);
        mockScenarioHelper.expectB766Call(request.getHeader(), request.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode());
        mockScenarioHelper.expectLookUpValuesWithISOCode();
        String userName = mockScenarioHelper.expectB751CallWithTacver(request.getProductArrangement(), request.getHeader(), BigInteger.valueOf(227323270), productArrangement.getAccountNumber(), BigInteger.valueOf(971461460), BigInteger.ZERO);
        productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().setProfile(new InternetBankingProfile());
        productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().getProfile().setUserName(userName);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "IB_STP_LITE_REGISTRATION_SUCCESS_MAIL", request.getHeader(), null, "Email", true);
        mockScenarioHelper.expectC658Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectC234Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectF060Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectC241Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectFATCAUpdateSwitchCall(channelId, true);
        mockScenarioHelper.expectRecordCustomerDetails(productArrangement, request.getHeader(), false);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "SA_WELCOME_MSG", request.getHeader(), null, "Email", false);
        mockScenarioHelper.expectScheduleCommunicationCall((DepositArrangement) productArrangement, "SA_FUNDING_REM_MSG", request.getHeader(), "STPSAVINGS", "Email");

    }

    @Given("Contact point id of request is not mapped in PAM")
    public void givenContactPointIdOfRequestIsNotMappedInPAM() {
        request = testDataHelper.createApaRequestForSa();
        request.getProductArrangement().setApplicationStatus("1002");
        request.setHeader(testDataHelper.createApaRequestHeaderWithInvalidContactPoint());
        mockScenarioHelper.expectChannelIdByContactPointID();
    }

    @When("There is call to APASA with valid request")
    public void whenThereIsCallToAPASAWithValidRequest() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        mockControl.go();
        try {
            response = apaSaClient.activateProductArrangement(request);
        } catch (ActivateProductArrangementDataNotAvailableErrorMsg errorMsg) {
            this.dataNotAvailableErrorFaultMsg = errorMsg;
        }
        mockScenarioHelper.sleep();
    }

    @Then("APASA responds")
    public void thenAPASAResponds() {
        assertNotNull(response);
        assertEquals("1010", response.getProductArrangement().getApplicationStatus());
        assertNull(response.getProductArrangement().getApplicationSubStatus());
        assertNull(response.getProductArrangement().getRetryCount());
        mockScenarioHelper.verifyExpectCalls();
    }

    @Then("APASA returns error and throws exception as dataNotAvailable")
    public void thenAPASAReturnsErrorAndThrowsExceptionAsDataNotAvailable() {
        assertNotNull(dataNotAvailableErrorFaultMsg);
        assertNull(response);
        assertEquals("No matching records found, error code: ", dataNotAvailableErrorFaultMsg.getFaultInfo().getDescription());
        mockScenarioHelper.verifyExpectCalls();
    }

}
