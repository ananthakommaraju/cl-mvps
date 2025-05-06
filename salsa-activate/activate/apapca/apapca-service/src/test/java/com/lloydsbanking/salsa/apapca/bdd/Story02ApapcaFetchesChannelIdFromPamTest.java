package com.lloydsbanking.salsa.apapca.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.KycStatus;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.Ignore;
import org.junit.experimental.categories.Category;

import java.math.BigInteger;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class})
public class Story02ApapcaFetchesChannelIdFromPamTest extends AbstractApapcaJBehaveTestBase {
    ActivateProductArrangementRequest request;

    ActivateProductArrangementResponse response;

    ProductArrangement productArrangement;

    ActivateProductArrangementDataNotAvailableErrorMsg dataNotAvailableErrorFaultMsg;
    String accountNumber;
    ProductTypes productTypesCurrent = new ProductTypes("102", "Current Account");

    @BeforeScenario
    public void resetData() {
        mockControl.reset();
        mockScenarioHelper.clearUp();
        request = null;
        response = null;
        accountNumber = null;
    }

    @Given("Contact point id of request is mapped in PAM and Source System Identifier is not Two")
    public void whenContactPointIdOfRequestIsMappedInPAMAndSourceSystemIdentifierIsNotTwo() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        ApplicationStatus applicationStatus = new ApplicationStatus("1002", "Approved");
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForPca();
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypesCurrent);

        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());

        mockScenarioHelper.expectLookUpValuesWithISOCode();
        mockScenarioHelper.expectRetrieveProductCondition((DepositArrangement) productArrangement,request.getHeader());


    }

    @Given("sort code is present in request")
    public void givenSortCodeIsPresentInRequest() {
        mockScenarioHelper.expectB766Call(request.getHeader(), productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode());
        accountNumber = mockScenarioHelper.expectE229Call(request.getHeader(), "779129");
        productArrangement.setAccountNumber(accountNumber);
        mockScenarioHelper.expectCreateAccountB675Call(request.getHeader(),productArrangement);
        mockScenarioHelper.expectB751CallWithAppIdAndAppVer(productArrangement, request.getHeader(), BigInteger.valueOf(227323270), accountNumber, BigInteger.valueOf(971461460), BigInteger.ZERO);
        //TODO need to populate channel from correct location
        mockScenarioHelper.expectC808Call("773315", accountNumber, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC846Call("0071776000", "1", "R", 50, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC812Call("0071776000", "1", "773315", 6363l, 4, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), accountNumber, request.getHeader());
        mockScenarioHelper.expectE226AddsOverdraftDetail(Long.valueOf(productArrangement.getArrangementId()), "LTB", request.getHeader());
        mockScenarioHelper.expectC658Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectC234Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectF060Call(productArrangement,request.getHeader(),0);
        mockScenarioHelper.expectActivateBenefitCall(productArrangement, request.getHeader(), 0);
    }

    @When("There is call to APAPCA with valid request when sort code present")
    public void whenThereisCallToAPAPCAWithValidRequestWhenSortCodePresent() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        mockControl.go();
        try {
            response = apaPcaClient.activateProductArrangement(request);
        } catch (ActivateProductArrangementDataNotAvailableErrorMsg errorMsg) {
            this.dataNotAvailableErrorFaultMsg = errorMsg;
        }
        mockScenarioHelper.sleep();
    }

    @Then("APAPCA responds")
    public void thenAPAPCAResponds() {
        assertNotNull(response);
    }

    @Given("Contact point id of request is not mapped in PAM")
    public void whenContactPointIdOfRequestIsNotMappedInPAM() {
        request = testDataHelper.createApaRequestForPca();
        request.setHeader(testDataHelper.createApaRequestHeaderWithInvalidContactPoint());
        mockScenarioHelper.expectChannelIdByContactPointID();
    }

    @When("There is call to APAPCA with valid request")
    public void whenThereisCallToAPAPCAWithValidRequest() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        mockControl.go();
        try {
            response = apaPcaClient.activateProductArrangement(request);
        } catch (ActivateProductArrangementDataNotAvailableErrorMsg errorMsg) {
            this.dataNotAvailableErrorFaultMsg = errorMsg;
        }
        mockScenarioHelper.sleep();
    }

    @Then("APAPCA returns error and throws exception as dataNotAvailable")
    public void thenAPAPCAReturnsErrorAndThrowExceptionAsDataNotAvailable() {
        assertNotNull(dataNotAvailableErrorFaultMsg);
    }
}
