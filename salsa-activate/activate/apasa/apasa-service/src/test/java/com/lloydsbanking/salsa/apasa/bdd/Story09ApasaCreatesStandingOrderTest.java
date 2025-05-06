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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story09ApasaCreatesStandingOrderTest extends AbstractApasaJBehaveTestBase {

    ActivateProductArrangementRequest request;

    ActivateProductArrangementResponse response;
    ProductArrangement productArrangement;
    ProductTypes productTypesSaving = new ProductTypes("101", "Saving Account");

    ApplicationStatus applicationStatus;
    String channelId;

    @BeforeScenario
    public void resetData() {
        mockScenarioHelper.clearUp();
        request = null;
        response = null;
        applicationStatus = new ApplicationStatus("1002", "Approved");
        channelId = null;
    }

    @Given("interest remittance details were captured")
    public void givenInterestRemittanceDetailsWereCaptured() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
            channelId = mockScenarioHelper.expectChannelIdByContactPointID();
            mockScenarioHelper.expectLookUpValues();
            mockScenarioHelper.expectReferenceDataForPAM();
            KycStatus eidvStatus = new KycStatus("ACCEPT");
            request = testDataHelper.createApaRequestForSaFor502();
            productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestDataWithNiNumberWithInterestRemittanceDetails(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypesSaving);

            request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
            request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
            request.getHeader().setContactPointId("0000777505");
            mockScenarioHelper.expectRpcCall((DepositArrangement) productArrangement, request.getHeader());
            mockScenarioHelper.expectCreateAccountB675Call(request.getHeader(), productArrangement);
            mockScenarioHelper.expectLookUpValuesWithISOCode();
            mockScenarioHelper.expectE502Call(request.getHeader(), (DepositArrangement) productArrangement);
            mockScenarioHelper.expectB766Call(request.getHeader(), request.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode());
        }


    @Given("Create standing order responds without exception")
    public void whenCreateStandingOrderRespondsWithoutException() throws ErrorInfo, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, ScheduleCommunicationResourceNotAvailableErrorMsg, ScheduleCommunicationExternalServiceErrorMsg, ScheduleCommunicationInternalServiceErrorMsg, ScheduleCommunicationDataNotAvailableErrorMsg, ScheduleCommunicationExternalBusinessErrorMsg {
            mockScenarioHelper.expectCreateStandingOrderCall(request.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode(), productArrangement.getAccountNumber(), ((DepositArrangement) productArrangement).getInterestRemittanceAccountDetails().getAccountNumber(), ((DepositArrangement) productArrangement).getInterestRemittanceAccountDetails().getSortCode(), ((DepositArrangement) productArrangement).getInterestRemittanceAccountDetails().getBeneficiaryName(), request.getHeader(), (byte) 0, request.getProductArrangement().getFinancialInstitution().getChannel());
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

    @When("there is a call to APAD")
    public void whenThereIsACallToAPAD() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        mockControl.go();
        response = apaSaClient.activateProductArrangement(request);
        mockScenarioHelper.sleep();
    }


    @Then("APAPCA responds successfully")
    public void thenAPAPCARespondsSuccessfully() {
        assertNotNull(response);
        assertEquals("1010", response.getProductArrangement().getApplicationStatus());
        assertNull(response.getProductArrangement().getRetryCount());
        assertNull(response.getProductArrangement().getApplicationSubStatus());
        mockScenarioHelper.verifyExpectCalls();
    }

    @Given("Create standing order responds with exception")
    public void whenCreateStandingOrderRespondsWithException() {
        mockScenarioHelper.expectCreateStandingOrderCall(productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode(), productArrangement.getAccountNumber(),((DepositArrangement)productArrangement).getInterestRemittanceAccountDetails().getAccountNumber(),((DepositArrangement)productArrangement).getInterestRemittanceAccountDetails().getSortCode(), ((DepositArrangement)productArrangement).getInterestRemittanceAccountDetails().getBeneficiaryName(), request.getHeader(), (byte) 1, productArrangement.getFinancialInstitution().getChannel());
    }

    @Then("APAPCA service continues")
    public void thenAPAPCAServiceContinues() {
        assertNotNull(response);
        assertEquals(1,response.getProductArrangement().getRetryCount().intValue());
        assertEquals("1010", response.getProductArrangement().getApplicationStatus());
        assertEquals("1020",response.getProductArrangement().getApplicationSubStatus());
        mockScenarioHelper.verifyExpectCalls();
    }


}
