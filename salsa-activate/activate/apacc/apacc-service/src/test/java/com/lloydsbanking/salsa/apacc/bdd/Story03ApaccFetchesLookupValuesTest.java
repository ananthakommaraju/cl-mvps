package com.lloydsbanking.salsa.apacc.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.KycStatus;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.F241Resp;
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
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story03ApaccFetchesLookupValuesTest extends AbstractApaccJBehaveTestBase {

    ActivateProductArrangementRequest request;

    ActivateProductArrangementResponse response;

    ApplicationStatus applicationStatus;

    ActivateProductArrangementDataNotAvailableErrorMsg dataNotAvailableErrorMsg;

    ProductTypes productTypeCreditCard = new ProductTypes("100", "Credit Card");

    ProductArrangement productArrangement;

    RequestHeader header;

    List<ProductOffer> productOfferList;

    @BeforeScenario
    public void resetData() {
        mockScenarioHelper.clearUp();
        request = null;
        response = null;
        header = null;
        applicationStatus = new ApplicationStatus("1002", "Approved");
        header = null;
        productOfferList = new ArrayList<>();
    }

    @Given("PAM contains lookup values for encryption key and purpose of account")
    public void givenPAMContainsLookupValuesForEncryptionKeyAndPurposeOfAccount() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, GenerateDocumentResourceNotAvailableErrorMsg, GenerateDocumentInternalServiceErrorMsg, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForCc();
        header = testDataHelper.createApaRequestForCc().getHeader();
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypeCreditCard);
        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        header = testDataHelper.createApaRequestForCc().getHeader();
        header.setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        header.setContactPointId("0000777505");
        productOfferList.addAll(mockScenarioHelper.expectRetrieveProductCondition(productArrangement.getAssociatedProduct(), header));
        mockScenarioHelper.expectLookUpValuesForEvidenceAndPurposeData();
        F241Resp f241Resp = mockScenarioHelper.expectCardAccountV1Call(productArrangement, header, "0");
        (productArrangement).getPrimaryInvolvedParty().setPartyIdentifier(f241Resp.getCustomerNumberExternalId());
        String cardNo = f241Resp.getCardData().get(0).getCardNo();
        String cardNumber = f241Resp.getCardData().get(0).getCardNo().length() == 19 ? cardNo.substring(3) : cardNo;
        ((FinanceServiceArrangement) productArrangement).setCreditCardNumber(cardNumber);
        productArrangement.setAccountNumber(f241Resp.getAccountNumberExternalId());
        mockScenarioHelper.expectF259Call((FinanceServiceArrangement) productArrangement, header, "0");
        mockScenarioHelper.expectF251Call((FinanceServiceArrangement) productArrangement, header, "0000777505");
        mockScenarioHelper.expectGenerateDocumentCall(productArrangement, productOfferList, header);
        String username = mockScenarioHelper.expectB751Call(productArrangement, header, -5);
        updateUsernameInRequest(username, (FinanceServiceArrangement) productArrangement);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "IB_STP_REGISTRATION_SUCCESS_MAIL", header, null, "Email", true);
        mockScenarioHelper.expectF060Call(productArrangement, header, 0);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "WELCOME_MSG", header, null, "Email", false);
        mockScenarioHelper.expectRetrieveEncryptData(((FinanceServiceArrangement)productArrangement), "WZ_ESB_V1-sscert.pem",header);
    }

    @Given("PAM does not contain lookup values for encryption key and purpose of account")
    public void givenPAMDoesNotContainLookupValuesForEncryptionKeyAndPurposeOfAccount() {
        request = testDataHelper.createApaRequestForCc(21L);
        mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValuesWithInCorrectGroupCodes();
    }

    @When("there is a call to APACC")
    public void whenThereIsACallToAPACC() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        mockControl.go();
        try {
            response = apaccClient.activateProductArrangement(request);
            mockScenarioHelper.sleep();
        } catch (ActivateProductArrangementDataNotAvailableErrorMsg errorMsg) {
            this.dataNotAvailableErrorMsg = errorMsg;
        }
    }

    @Then("service continues")
    public void thenServiceContinues() {
        assertNotNull(response);//ToDo more asserts to be applied as service integrates
        mockScenarioHelper.verifyExpectCalls();
    }

    @Then("service throws error")
    public void thenServiceThrowsError() {
        assertNotNull(dataNotAvailableErrorMsg);
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
