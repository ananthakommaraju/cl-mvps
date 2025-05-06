package com.lloydsbanking.salsa.apapca.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.KycStatus;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import com.lloydstsb.schema.involvedpartymanagement.ifw.ErrorInfo;
import lib_sim_bo.businessobjects.DepositArrangement;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story25ApadScheduleCommunicationForFundReminderForSavingArrangementTypeTest extends AbstractApapcaJBehaveTestBase {
    ActivateProductArrangementRequest request;

    ActivateProductArrangementResponse response;

    ProductArrangement productArrangement;

    String accountNumber;

    ProductTypes productTypesSaving = new ProductTypes("101", "Saving Account");

    @BeforeScenario
    public void resetData() {
        mockControl.reset();
        mockScenarioHelper.clearUp();
        request = null;
        response = null;
    }

    @Given("application sub status is null")
    public void givenApplicationSubStatusIsNull() {
    }

    @Given("arrangement type is saving")
    public void givenArrangementTypeIsSaving() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, ScheduleCommunicationResourceNotAvailableErrorMsg, ScheduleCommunicationExternalServiceErrorMsg, ScheduleCommunicationInternalServiceErrorMsg, ScheduleCommunicationDataNotAvailableErrorMsg, ScheduleCommunicationExternalBusinessErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ErrorInfo, ActivateProductArrangementInternalSystemErrorMsg {
        ApplicationStatus applicationStatus = new ApplicationStatus("1002", "Approved");
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValuesWithLifeStyleBenefitCode();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForPca();
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, null, null, productTypesSaving);

        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");
        mockScenarioHelper.expectLookUpValuesWithISOCode();
        mockScenarioHelper.expectB766Call(request.getHeader(), "779129");
        accountNumber = mockScenarioHelper.expectE229Call(request.getHeader(), "779129");
        productArrangement.setAccountNumber(accountNumber);
        mockScenarioHelper.expectRetrieveProductCondition((DepositArrangement) productArrangement,request.getHeader());
        mockScenarioHelper.expectCreateAccountB675Call(request.getHeader(),productArrangement);
        String sortCode = productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode();
        mockScenarioHelper.expectC808Call(sortCode, accountNumber, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC846Call("0071776000", "1", "R", 50, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC812Call("0071776000", "1", sortCode, 6363l, 4, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), accountNumber, request.getHeader());
        mockScenarioHelper.expectB751CallWithAppIdAndAppVer(productArrangement, request.getHeader(), BigInteger.valueOf(227323270), accountNumber, BigInteger.valueOf(971461460), BigInteger.ZERO);
        //TODO need to populate channel from correct location
        mockScenarioHelper.expectE226AddsOverdraftDetailWithSaving(Long.valueOf(productArrangement.getArrangementId()), "LTB", request.getHeader());
        mockScenarioHelper.expectC658Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectF060Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectActivateBenefitCall(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectFATCAUpdateSwitchCall(channelId, true);
        mockScenarioHelper.expectRecordCustomerDetails(productArrangement, request.getHeader(), false);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "SA_WELCOME_MSG", request.getHeader(), null, "Email", true);
        mockScenarioHelper.expectScheduleCommunicationCall((DepositArrangement) productArrangement, "SA_FUNDING_REM_MSG", request.getHeader(), "STPSAVINGS", "Email");
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_BENEFITS_MSG", request.getHeader(), null, "Email", false);
    }

    @Given("mobile number is present")
    public void givenMobileNumberIsPresent() throws ScheduleCommunicationResourceNotAvailableErrorMsg, ScheduleCommunicationExternalServiceErrorMsg, ScheduleCommunicationInternalServiceErrorMsg, ScheduleCommunicationDataNotAvailableErrorMsg, ScheduleCommunicationExternalBusinessErrorMsg {
       // mockScenarioHelper.expectScheduleCommunicationCall((DepositArrangement) productArrangement, "STPSAVFUNDREMIN", request.getHeader(), "STPSAVINGS", "SMS");
    }

    @When("UI calls APAD")
    public void whenUICallsAPAD() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        mockControl.go();
        response = apaPcaClient.activateProductArrangement(request);
        mockScenarioHelper.sleep();
    }

    @Then("schedule communication mail for fund reminder")
    public void whenScheduleCommunicationMailForFundReminder() {
        assertNotNull(response);
        assertEquals("1010",response.getProductArrangement().getApplicationStatus());
    }

    @Then("schedule communication sms for fund reminder")
    public void whenScheduleCommunicationSmsForFundReminder() {
        assertNotNull(response);
        assertEquals("1010",response.getProductArrangement().getApplicationStatus());
    }
}
