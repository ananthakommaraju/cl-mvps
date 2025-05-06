package com.lloydsbanking.salsa.apapca.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.KycStatus;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import com.lloydstsb.schema.involvedpartymanagement.ifw.ErrorInfo;
import junit.framework.Assert;
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

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story06ApapcaFetchesRetrieveProductArrangementDetailsTest extends AbstractApapcaJBehaveTestBase {
    ActivateProductArrangementRequest request;

    ActivateProductArrangementResponse response;

    ActivateProductArrangementDataNotAvailableErrorMsg dataNotAvailableErrorMsg;

    ActivateProductArrangementResourceNotAvailableErrorMsg resourceNotAvailableErrorMsg;

    ActivateProductArrangementExternalBusinessErrorMsg externalBusinessErrorMsg;

    ActivateProductArrangementInternalSystemErrorMsg internalSystemErrorMsg;

    ActivateProductArrangementExternalSystemErrorMsg externalSystemErrorMsg;

    ApplicationStatus applicationStatus;

    ProductTypes productTypesCurrent = new ProductTypes("102", "Current Account");

    String accountNumber;
    ProductArrangement productArrangement;

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
        applicationStatus = new ApplicationStatus("1002", "Approved");
        accountNumber = null;
        productArrangement = null;
    }

    @Given("request contains TMS task Id")
    public void givenRequestContainsTMSTaskId() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ErrorInfo, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        request = testDataHelper.createApaRequestForPcaWithTmsTaskId();
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        mockScenarioHelper.expectApplicationIdFromReferralsTmsTaskId(applicationStatus, eidvStatus, "1", null, "123");
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypesCurrent);

        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");

        mockScenarioHelper.expectB766Call(request.getHeader(), productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode());
        accountNumber = mockScenarioHelper.expectE229Call(request.getHeader(), "779129");
        productArrangement.setAccountNumber(accountNumber);
        mockScenarioHelper.expectRetrieveProductCondition((DepositArrangement) productArrangement,request.getHeader());
        mockScenarioHelper.expectCreateAccountB675Call(request.getHeader(),productArrangement);
        String sortCode = productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode();
        mockScenarioHelper.expectC808Call(sortCode, accountNumber, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC846Call("0071776000", "1", "R", 50, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC812Call("0071776000", "1", sortCode, 6363l, 4, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), accountNumber, request.getHeader());
        mockScenarioHelper.expectLookUpValuesWithISOCode();
        mockScenarioHelper.expectB751CallWithAppIdAndAppVer(productArrangement, request.getHeader(), BigInteger.valueOf(227323270), accountNumber, BigInteger.valueOf(971461460), BigInteger.ZERO);
        mockScenarioHelper.expectE226AddsOverdraftDetail(Long.valueOf(productArrangement.getArrangementId()), productArrangement.getFinancialInstitution().getChannel(), request.getHeader());
        mockScenarioHelper.expectC658Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectC234Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectF060Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_WELCOME_MSG", request.getHeader(), null, "Email", true);

    }

    @When("there is a call to APAD")
    public void whenThereIsACallToAPAD() {
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

    @Then("service returns arrangement id in product arrangement")
    public void thenServiceReturnsArrangementIdInProductArrangement() {
        assertNotNull(response);
        assertEquals("1010",response.getProductArrangement().getApplicationStatus());
        assertNull(response.getProductArrangement().getApplicationSubStatus());
        assertNull(response.getProductArrangement().getRetryCount());
    }

    @Given("request contains arrangement Id")
    public void givenRequestContainsArrangementId() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ErrorInfo, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {

        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForPca();
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypesCurrent);

        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");


        mockScenarioHelper.expectB766Call(request.getHeader(), productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode());
        accountNumber = mockScenarioHelper.expectE229Call(request.getHeader(), "779129");
        productArrangement.setAccountNumber(accountNumber);
        mockScenarioHelper.expectRetrieveProductCondition((DepositArrangement) productArrangement,request.getHeader());
        mockScenarioHelper.expectCreateAccountB675Call(request.getHeader(),productArrangement);
        mockScenarioHelper.expectLookUpValuesWithISOCode();
        String sortCode = productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode();
        mockScenarioHelper.expectC808Call(sortCode, accountNumber, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC846Call("0071776000", "1", "R", 50, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC812Call("0071776000", "1", sortCode, 6363l, 4, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), accountNumber, request.getHeader());
        mockScenarioHelper.expectB751CallWithAppIdAndAppVer(productArrangement, request.getHeader(), BigInteger.valueOf(227323270), accountNumber, BigInteger.valueOf(971461460), BigInteger.ZERO);
        mockScenarioHelper.expectE226AddsOverdraftDetail(Long.valueOf(productArrangement.getArrangementId()), productArrangement.getFinancialInstitution().getChannel(), request.getHeader());
        mockScenarioHelper.expectC658Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectC234Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectF060Call(productArrangement,request.getHeader(),0);
        mockScenarioHelper.expectActivateBenefitCall(productArrangement, request.getHeader(), 0);
       mockScenarioHelper.expectFATCAUpdateSwitchCall(channelId, true);
       mockScenarioHelper.expectRecordCustomerDetails(productArrangement, request.getHeader(), false);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_WELCOME_MSG", request.getHeader(), null, "Email", true);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_BENEFITS_MSG", request.getHeader(), null, "Email", false);
    }

    @Then("service sets arrangement details in product arrangement")
    public void thenServiceReturnsArrangementDetailsInProductArrangement() {
        assertNotNull(response);
        Assert.assertEquals(productArrangement.getArrangementId(), response.getProductArrangement().getArrangementId());
    }

    @Given("request contains arrangement id with related application available")
    public void givenRequestContainsArrangementIdWithRelatedApplicationAvailable() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ErrorInfo, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {

        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForPca();
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestDataWithRelatedAplicationId(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypesCurrent);

        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");

        mockScenarioHelper.expectB766Call(request.getHeader(), productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode());
        accountNumber = mockScenarioHelper.expectE229Call(request.getHeader(), "779129");
        productArrangement.setAccountNumber(accountNumber);
        mockScenarioHelper.expectRetrieveProductCondition((DepositArrangement) productArrangement,request.getHeader());
        mockScenarioHelper.expectCreateAccountB675Call(request.getHeader(),productArrangement);
        mockScenarioHelper.expectLookUpValuesWithISOCode();
        String sortCode = productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode();
        mockScenarioHelper.expectC808Call(sortCode, accountNumber, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC846Call("0071776000", "1", "R", 50, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC812Call("0071776000", "1", sortCode, 6363l, 4, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), accountNumber, request.getHeader());
        mockScenarioHelper.expectE226AddsOverdraftDetail(Long.valueOf(productArrangement.getArrangementId()), productArrangement.getFinancialInstitution().getChannel(), request.getHeader());
        mockScenarioHelper.expectB751CallWithAppIdAndAppVer(productArrangement, request.getHeader(), BigInteger.valueOf(227323270), accountNumber, BigInteger.valueOf(971461460), BigInteger.ZERO);
        mockScenarioHelper.expectC658Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectC234Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectF060Call(productArrangement,request.getHeader(),0);
       mockScenarioHelper.expectActivateBenefitCall(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectFATCAUpdateSwitchCall(channelId, true);
        mockScenarioHelper.expectRecordCustomerDetails(productArrangement, request.getHeader(), false);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_WELCOME_MSG", request.getHeader(), null, "Email", true);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_BENEFITS_MSG", request.getHeader(), null, "Email", false);
    }

}
