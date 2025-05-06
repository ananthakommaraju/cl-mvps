package com.lloydsbanking.salsa.apapca.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.KycStatus;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import com.lloydstsb.schema.involvedpartymanagement.ifw.ErrorInfo;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_communicationmanager.id_communicationmanagerrouter.*;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.jbehave.core.annotations.*;
import org.junit.Ignore;
import org.junit.experimental.categories.Category;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class})
public class Story23ApapcaUpdatePartyRelationshipTest extends AbstractApapcaJBehaveTestBase {
    ActivateProductArrangementRequest request;

    ActivateProductArrangementResponse response;

    ProductArrangement productArrangement;

    ProductTypes productTypesCurrent = new ProductTypes("102", "Current Account");

    String accountNumber;

    @BeforeScenario
    public void resetData() {
        mockControl.reset();
        mockScenarioHelper.clearUp();
        request = null;
        response = null;
        accountNumber = null;
    }

    @Pending
    @Given("C241 call fails")
    public void givenC241CallFails() {
        // PENDING
    }

    @Pending
    @Then("update application status to Awaiting Fulfilment(1009), sub-status to Party Relationship Update Failure(1031) and increment Retry count in PAM")
    public void thenUpdateApplicationStatusToAwaitingFulfilment1009SubstatusToPartyRelationshipUpdateFailure1031AndIncrementRetryCountInPAM() {
        // PENDING
    }

    @Pending
    @Given("Sub status is Party Relationship Update Failure(1031)")
    public void givenSubStatusIsPartyRelationshipUpdateFailure1031() {
        // PENDING
    }

    @Given("update life style benefit call is successful")
    public void givenUpdateLifeStyleBenefitCallIsSuccessful() {
        mockScenarioHelper.expectActivateBenefitCall(productArrangement, request.getHeader(), 0);
    }

    @Given("Sub status is null")
    public void givenSubStatusIsNull() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ErrorInfo, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        ApplicationStatus applicationStatus = new ApplicationStatus("1002", "Approved");
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValuesWithLifeStyleBenefitCode();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForPca();
        request.getProductArrangement().setGuardianDetails(new Customer());
        request.getProductArrangement().getGuardianDetails().setCustomerIdentifier("1234");
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, null, null, productTypesCurrent);
        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");
        mockScenarioHelper.expectLookUpValuesWithISOCode();
        mockScenarioHelper.expectB766Call(request.getHeader(), "779129");
        accountNumber = mockScenarioHelper.expectE229Call(request.getHeader(), "779129");
        productArrangement.setAccountNumber(accountNumber);
        mockScenarioHelper.expectCreateAccountB675Call(request.getHeader(),productArrangement);
        mockScenarioHelper.expectRetrieveProductCondition((DepositArrangement) productArrangement,request.getHeader());
        String sortCode = productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode();
        mockScenarioHelper.expectC808Call(sortCode, accountNumber, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC846Call("0071776000", "1", "R", 50, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC812Call("0071776000", "1", sortCode, 6363l, 4, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), accountNumber, request.getHeader());
        mockScenarioHelper.expectB751CallWithAppIdAndAppVer(productArrangement, request.getHeader(), BigInteger.valueOf(227323270), accountNumber, BigInteger.valueOf(971461460), BigInteger.ZERO);
        //TODO need to populate channel from correct location
        mockScenarioHelper.expectE226AddsOverdraftDetail(Long.valueOf(productArrangement.getArrangementId()), "LTB", request.getHeader());
        mockScenarioHelper.expectC658Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectF060Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectActivateBenefitCall(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectC241Call(productArrangement, request.getHeader(), 0);
       // mockScenarioHelper.expectFATCAUpdateSwitchCall(channelId, true);
       // mockScenarioHelper.expectRecordCustomerDetails(productArrangement, request.getHeader(), false);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_WELCOME_MSG", request.getHeader(), null, "Email", true);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_BENEFITS_MSG", request.getHeader(), null, "Email", false);
    }

    @When("UI calls APAD")
    public void whenUICallsAPAD() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        mockControl.go();
        response = apaPcaClient.activateProductArrangement(request);
    }

    @When("UI calls APAD when sub status null")
    public void whenUICallsAPADSubStatusNull() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        mockControl.go();
        response = apaPcaClient.activateProductArrangement(request);
    }

    @Then("call OCIS API C241 to update party relationship.")
    public void thenCallOCISAPIC241ToUpdatePartyRelationship() {
        assertNotNull(response);
        assertEquals("1010",response.getProductArrangement().getApplicationStatus());
    }

    @Given("Sub status is Update Life Style Benefit(1029)")
    public void givenSubStatusIsUpdateLifeStyleBenefit1029() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ErrorInfo, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        ApplicationStatus applicationStatus = new ApplicationStatus("1002", "Approved");
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValuesWithLifeStyleBenefitCode();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForPca();
        request.getProductArrangement().setGuardianDetails(new Customer());
        request.getProductArrangement().getGuardianDetails().setCustomerIdentifier("1234");
        request.getProductArrangement().setApplicationSubStatus("1029");
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, "1029", "", productTypesCurrent);
        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");
        mockScenarioHelper.expectLookUpValuesWithISOCode();
        mockScenarioHelper.expectRetrieveProductCondition((DepositArrangement) productArrangement,request.getHeader());
        mockScenarioHelper.expectB766Call(request.getHeader(), productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode());
        accountNumber = mockScenarioHelper.expectE229Call(request.getHeader(), "779129");
        productArrangement.setAccountNumber(accountNumber);
        mockScenarioHelper.expectF060Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectC241Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectFATCAUpdateSwitchCall(channelId, true);
        mockScenarioHelper.expectRecordCustomerDetails(productArrangement, request.getHeader(), false);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_WELCOME_MSG", request.getHeader(), null, "Email", true);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_BENEFITS_MSG", request.getHeader(), null, "Email", false);

    }
}
