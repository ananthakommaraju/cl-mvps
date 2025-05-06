package com.lloydsbanking.salsa.apapca.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.KycStatus;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import com.lloydstsb.schema.involvedpartymanagement.ifw.ErrorInfo;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.DirectDebit;
import lib_sim_bo.businessobjects.Location;
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
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class})
public class Story30ApapcaSetsSortCodeAndEncryptsDataInCaseOfPcaReengineeringTest extends AbstractApapcaJBehaveTestBase {
    ActivateProductArrangementRequest request;

    ActivateProductArrangementResponse response;

    ProductArrangement productArrangement;
    ProductTypes productTypesCurrent = new ProductTypes("102", "Current Account");
    String accountNumber;
    ApplicationStatus applicationStatus;
    String channelId;

    @BeforeScenario
    public void resetData() {
        mockControl.reset();
        mockScenarioHelper.clearUp();
        request = null;
        response = null;
        applicationStatus = new ApplicationStatus("1002", "Approved");
        accountNumber = null;
        productArrangement = null;
    }

    @Given("that sort code is present in request")
    public void givenThatSortCodeIsPresentInRequest() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ErrorInfo, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForPca();
        request.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().setCustomerLocation(new Location());
        request.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().getCustomerLocation().setLatitude("10");
        request.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().getCustomerLocation().setLongitude("20");
        DepositArrangement depositArrangement=(DepositArrangement)request.getProductArrangement();
        depositArrangement.setAccountSwitchingDetails(new DirectDebit());
        depositArrangement.getAccountSwitchingDetails().setCardNumber("123");
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestDataWithIntendToSwitch(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypesCurrent);
        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");

        mockScenarioHelper.expectRetrieveEncryptData(Arrays.asList(depositArrangement.getAccountSwitchingDetails().getCardNumber()), null,request.getHeader());


        mockScenarioHelper.expectLookUpValuesWithISOCode();
        mockScenarioHelper.expectB766Call(request.getHeader(), "779129");
        accountNumber = mockScenarioHelper.expectE229Call(request.getHeader(), "779129");
        productArrangement.setAccountNumber(accountNumber);
        mockScenarioHelper.expectCreateAccountB675Call(request.getHeader(),productArrangement);
        String sortCode = productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode();
        //TODO need to populate channel from correct location
        mockScenarioHelper.expectE226AddsOverdraftDetail(Long.valueOf(productArrangement.getArrangementId()), "LTB", request.getHeader());
        mockScenarioHelper.expectRetrieveProductCondition((DepositArrangement) productArrangement,request.getHeader());
        mockScenarioHelper.expectPegaCall(depositArrangement, request.getHeader());
        mockScenarioHelper.expectC808Call(sortCode, accountNumber, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC846Call("0071776000", "1", "R", 50, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC812Call("0071776000", "1", sortCode, 6363l, 4, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), accountNumber, request.getHeader());

        mockScenarioHelper.expectB751CallWithAppIdAndAppVer(productArrangement, request.getHeader(), BigInteger.valueOf(227323270), accountNumber, BigInteger.valueOf(971461460), BigInteger.ZERO);

        mockScenarioHelper.expectC658Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectC234Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectF060Call(productArrangement,request.getHeader(),0);
        mockScenarioHelper.expectActivateBenefitCall(productArrangement, request.getHeader(), 0);
      //  mockScenarioHelper.expectFATCAUpdateSwitchCall(channelId, true);
     //   mockScenarioHelper.expectRecordCustomerDetails(productArrangement, request.getHeader(), false);
        mockScenarioHelper.expectDurableMediumSwitchCall(channelId, true);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_WELCOME_MSG", request.getHeader(), null, "Email", true);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_BENEFITS_MSG", request.getHeader(), null, "Email", false);
        testDataHelper.setSwitchingDetailsInRequest(((DepositArrangement) productArrangement).getAccountSwitchingDetails());

    }

    @Given("PAM contains lookup values for encryption key and purpose of account")
    public void givenPAMContainsLookupValuesForEncryptionKeyAndPurposeOfAccount() {
        mockScenarioHelper.expectLookUpValuesWithIntendToSwitch();
    }

    @When("there is a call to APAD")
    public void whenThereIsACallToAPAD() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        mockControl.go();
        response = apaPcaClient.activateProductArrangement(request);
    }

    @Then("service responds")
    public void thenServiceResponds() {
        assertNotNull(response);
    }

    @Given("that sort code is not present in request")
    public void givenThatSortCodeIsNotPresentInRequest() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ErrorInfo, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValuesWithIntendToSwitch();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForPca();
        request.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().setCustomerLocation(new Location());
        request.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().getCustomerLocation().setLatitude("10");
        request.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().getCustomerLocation().setLongitude("20");
        DepositArrangement depositArrangement=(DepositArrangement)request.getProductArrangement();
        depositArrangement.setAccountSwitchingDetails(new DirectDebit());
        depositArrangement.getAccountSwitchingDetails().setCardNumber("123");
        request.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).setSortCode("");
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestDataWithIntendToSwitch(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypesCurrent);
        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");

        mockScenarioHelper.expectRetrieveEncryptData(Arrays.asList(((DepositArrangement)productArrangement).getAccountSwitchingDetails().getCardNumber()), null,request.getHeader());


        mockScenarioHelper.expectLookUpValuesWithISOCode();
        mockScenarioHelper.expectB766Call(request.getHeader(), "779129");
        accountNumber = mockScenarioHelper.expectE229Call(request.getHeader(), "779129");
        productArrangement.setAccountNumber(accountNumber);
        mockScenarioHelper.expectCreateAccountB675Call(request.getHeader(),productArrangement);
        String sortCode = productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode();
        //TODO need to populate channel from correct location
        mockScenarioHelper.expectE226AddsOverdraftDetail(Long.valueOf(productArrangement.getArrangementId()), "LTB", request.getHeader());
        mockScenarioHelper.expectRetrieveProductCondition((DepositArrangement) productArrangement,request.getHeader());
        mockScenarioHelper.expectPegaCall(depositArrangement, request.getHeader());
        mockScenarioHelper.expectC808Call(sortCode, accountNumber, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC846Call("0071776000", "1", "R", 50, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC812Call("0071776000", "1", sortCode, 6363l, 4, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), accountNumber, request.getHeader());

        mockScenarioHelper.expectB751CallWithAppIdAndAppVer(productArrangement, request.getHeader(), BigInteger.valueOf(227323270), accountNumber, BigInteger.valueOf(971461460), BigInteger.ZERO);

        mockScenarioHelper.expectC658Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectC234Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectF060Call(productArrangement,request.getHeader(),0);
        mockScenarioHelper.expectActivateBenefitCall(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectFATCAUpdateSwitchCall(channelId, true);
        mockScenarioHelper.expectRecordCustomerDetails(productArrangement, request.getHeader(), false);
        mockScenarioHelper.expectDurableMediumSwitchCall(channelId, true);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_WELCOME_MSG", request.getHeader(), null, "Email", true);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_BENEFITS_MSG", request.getHeader(), null, "Email", false);
        testDataHelper.setSwitchingDetailsInRequest(((DepositArrangement) productArrangement).getAccountSwitchingDetails());
    }

    @Given("H071 is called with success")
    public void givenH071IsCalledWithSuccess() {
        mockScenarioHelper.expectH071Call(productArrangement.getPrimaryInvolvedParty(), request.getHeader());
    }

}

