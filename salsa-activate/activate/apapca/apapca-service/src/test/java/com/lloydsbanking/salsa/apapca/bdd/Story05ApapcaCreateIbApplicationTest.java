package com.lloydsbanking.salsa.apapca.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.KycStatus;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_communicationmanager.id_communicationmanagerrouter.*;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import java.math.BigInteger;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertNotNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story05ApapcaCreateIbApplicationTest extends AbstractApapcaJBehaveTestBase {
    ActivateProductArrangementRequest request;

    lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse response;

    ActivateProductArrangementDataNotAvailableErrorMsg dataNotAvailableErrorMsg;

    ActivateProductArrangementResourceNotAvailableErrorMsg resourceNotAvailableErrorMsg;

    ActivateProductArrangementExternalBusinessErrorMsg externalBusinessErrorMsg;

    ActivateProductArrangementInternalSystemErrorMsg internalSystemErrorMsg;

    ActivateProductArrangementExternalSystemErrorMsg externalSystemErrorMsg;
    String accountNumber;
    ProductArrangement productArrangement;
    ProductTypes productTypesCurrent = new ProductTypes("102", "Current Account");
    BigInteger appID;

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
        productArrangement = null;
        appID = null;
    }

    @Given("Application is valid for IB Registration")
    public void givenApplicationIsValidForRegistration() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        request = testDataHelper.createApaRequestForPca();
        request.getProductArrangement().getPrimaryInvolvedParty().setIsRegistrationSelected(true);
        request.getProductArrangement().setApplicationType("10002");
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();

        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferenceDataForPAM();

        ApplicationStatus applicationStatus = new ApplicationStatus("1002", "Approved");
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestDataWithoutIbRegistrationDetails(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypesCurrent);

        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");

        appID = mockScenarioHelper.expectB750Call(request.getHeader(), productArrangement);

    }

    @Given("sort code is present in request")
    public void givenSortCodeIsPresentInRequest() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg {
        mockScenarioHelper.expectB766Call(request.getHeader(), request.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode());
        accountNumber = mockScenarioHelper.expectE229Call(request.getHeader(), "779129");
        productArrangement.setAccountNumber(accountNumber);

        mockScenarioHelper.expectCreateAccountB675Call(request.getHeader(),productArrangement);
        mockScenarioHelper.expectRetrieveProductCondition((DepositArrangement) productArrangement,request.getHeader());
        mockScenarioHelper.expectE226AddsOverdraftDetail(Long.valueOf(productArrangement.getArrangementId()), productArrangement.getFinancialInstitution().getChannel(), request.getHeader());
        mockScenarioHelper.expectLookUpValuesWithISOCode();
        String sortCode = productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode();
        mockScenarioHelper.expectC808Call(sortCode, accountNumber, Long.valueOf(request.getProductArrangement().getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC846Call("0071776000", "1", "R", 50, Long.valueOf(request.getProductArrangement().getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC812Call("0071776000", "1", sortCode, 6363l, 4, Long.valueOf(request.getProductArrangement().getPrimaryInvolvedParty().getCustomerIdentifier()), accountNumber, request.getHeader());
    }


    @Given("activate IB Registration is called when B750 is called")
    public void givenActivateIBRegistrationIsCalledWhenB750IsCalled() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectB751CallWithAppIdAndAppVer(productArrangement, request.getHeader(), BigInteger.valueOf(227323270), accountNumber, appID, BigInteger.ZERO);
        mockScenarioHelper.expectC658Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectC234Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectF060Call(productArrangement,request.getHeader(),0);
        mockScenarioHelper.expectActivateBenefitCall(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_WELCOME_MSG", request.getHeader(), null, "Email", true);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_BENEFITS_MSG", request.getHeader(), null, "Email", false);
    }

    @Given("activate IB Registration is called when B750 is not called")
    public void givenActivateIBRegistrationIsCalledWhenB750IsNotCalled() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectB751CallWithAppIdAndAppVer(productArrangement, request.getHeader(), BigInteger.valueOf(227323270), accountNumber, BigInteger.valueOf(971461460), BigInteger.ZERO);
        mockScenarioHelper.expectC658Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectC234Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectF060Call(productArrangement,request.getHeader(),0);
        mockScenarioHelper.expectActivateBenefitCall(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_WELCOME_MSG", request.getHeader(), null, "Email", true);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_BENEFITS_MSG", request.getHeader(), null, "Email", false);

    }


    @Given("Application type is eligible for fulfilment")
    public void givenApplicationTypeIsEligibleForFulfilment() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        ApplicationStatus applicationStatus = new ApplicationStatus("1002", "Approved");
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForPca();
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypesCurrent);

        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");
        request.getProductArrangement().setApplicationType("10002");

    }

    @Given("Application Type is invalid")
    public void givenApplicationTypeIsInvalid() {
        request = testDataHelper.createApaRequestForPca();
        request.getProductArrangement().getPrimaryInvolvedParty().setIsRegistrationSelected(true);
        request.setSourceSystemIdentifier("2");
        request.getProductArrangement().setApplicationType("9999");
        mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValues();
    }

    @When("there is a call to APAD")
    public void whenThereIsACallToAPAD() {
        {
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
    }

    @When("there is a call to APAD For Error")
    public void whenThereIsACallToAPADForError() {
        {
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
    }

    @Then("service continues")
    public void serviceContinues() {
        assertNotNull(response);
        assertNull(response.getProductArrangement().getApplicationSubStatus());
        assertNull(response.getProductArrangement().getRetryCount());
    }


    @Then("Throw Invalid Application type Error")
    public void thenThrowInvalidApplicationTypeError() {
        assertNotNull(internalSystemErrorMsg);
    }
}
