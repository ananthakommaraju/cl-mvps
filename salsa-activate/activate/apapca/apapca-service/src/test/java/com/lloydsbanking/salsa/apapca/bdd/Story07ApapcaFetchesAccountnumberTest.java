package com.lloydsbanking.salsa.apapca.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.KycStatus;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import com.lloydstsb.schema.involvedpartymanagement.ifw.ErrorInfo;
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

import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story07ApapcaFetchesAccountnumberTest extends AbstractApapcaJBehaveTestBase {
    ActivateProductArrangementRequest request;

    ActivateProductArrangementResponse response;

    ApplicationStatus applicationStatus;

    String accountNumber;
    ProductArrangement productArrangement;
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
        channelId = null;
    }

    @Given("that sort code is present in request")
    public void givenThatSortCodeIsPresentInRequest() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ErrorInfo, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForPca();
        ProductTypes productTypesCurrent = new ProductTypes("102", "Current Account");
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypesCurrent);

        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");
        mockScenarioHelper.expectB766Call(request.getHeader(), request.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode());
        accountNumber = mockScenarioHelper.expectE229Call(request.getHeader(), "779129");
        productArrangement.setAccountNumber(accountNumber);
        mockScenarioHelper.expectRetrieveProductCondition((DepositArrangement) productArrangement, request.getHeader());
        mockScenarioHelper.expectCreateAccountB675Call(request.getHeader(), productArrangement);
        mockScenarioHelper.expectLookUpValuesWithISOCode();
        mockScenarioHelper.expectB751CallWithAppIdAndAppVer(productArrangement, request.getHeader(), BigInteger.valueOf(227323270), accountNumber, BigInteger.valueOf(971461460), BigInteger.ZERO);
        mockScenarioHelper.expectE226AddsOverdraftDetail(Long.valueOf(productArrangement.getArrangementId()), request.getProductArrangement().getFinancialInstitution().getChannel(), request.getHeader());
        mockScenarioHelper.expectC808Call(request.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode(), accountNumber, Long.valueOf(request.getProductArrangement().getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC846Call("0071776000", "1", "R", 50, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC812Call("0071776000", "1", request.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode(), 6363l, 4, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), accountNumber, request.getHeader());
        mockScenarioHelper.expectC658Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectC234Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectF060Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectActivateBenefitCall(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectFATCAUpdateSwitchCall(channelId, true);
        mockScenarioHelper.expectRecordCustomerDetails(productArrangement, request.getHeader(), false);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_WELCOME_MSG", request.getHeader(), null, "Email", true);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_BENEFITS_MSG", request.getHeader(), null, "Email", false);

    }

    @Given("that sort code is not present in request")
    public void givenThatSortCodeIsNotPresentInRequest() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ErrorInfo, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForPca();
        ProductTypes productTypesCurrent = new ProductTypes("102", "Current Account");
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypesCurrent);
        request.getHeader().setContactPointId("0000777505");
        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).setSortCode(null);
       // mockScenarioHelper.expectB766Call(request.getHeader(), request.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode());
        mockScenarioHelper.expectE469Call(request.getHeader(), testDataHelper.getContactPointId(request.getHeader()).getContactPointId().toString());
        mockScenarioHelper.expectE229Call(request.getHeader(), "007505");
        mockScenarioHelper.expectRetrieveProductCondition((DepositArrangement) productArrangement, request.getHeader());
        mockScenarioHelper.expectCreateAccountB675Call(request.getHeader(), request.getProductArrangement());
        mockScenarioHelper.expectC808Call(request.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode(), accountNumber, Long.valueOf(request.getProductArrangement().getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC846Call("0071776000", "1", "R", 50, Long.valueOf(request.getProductArrangement().getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC812Call("0071776000", "1", request.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode(), 6363l, 4, Long.valueOf(request.getProductArrangement().getPrimaryInvolvedParty().getCustomerIdentifier()), accountNumber, request.getHeader());
        mockScenarioHelper.expectB751CallWithAppIdAndAppVer(productArrangement, request.getHeader(), BigInteger.valueOf(227323270), accountNumber, BigInteger.valueOf(971461460), BigInteger.ZERO);
        mockScenarioHelper.expectLookUpValuesWithISOCode();
        mockScenarioHelper.expectE226AddsOverdraftDetail(Long.valueOf(productArrangement.getArrangementId()), request.getProductArrangement().getFinancialInstitution().getChannel(), request.getHeader());
        mockScenarioHelper.expectC658Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectC234Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectF060Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectActivateBenefitCall(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectFATCAUpdateSwitchCall(channelId, true);
        mockScenarioHelper.expectRecordCustomerDetails(productArrangement, request.getHeader(), false);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_WELCOME_MSG", request.getHeader(), null, "Email", true);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_BENEFITS_MSG", request.getHeader(), null, "Email", false);

    }

    @Given("PAM contains lookup values for encryption key and purpose of account")
    public void givenPAMContainsLookupValuesForEncryptionKeyAndPurposeOfAccount() {
        mockScenarioHelper.expectLookUpValues();
    }

    @When("there is a call to APAD")
    public void whenThereIsACallToAPAD() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        mockControl.go();
        response = apaPcaClient.activateProductArrangement(request);
        mockScenarioHelper.sleep();
    }

    @Then("service responds")
    public void thenServiceResponds() {
        assertNotNull(response);
    }

}
