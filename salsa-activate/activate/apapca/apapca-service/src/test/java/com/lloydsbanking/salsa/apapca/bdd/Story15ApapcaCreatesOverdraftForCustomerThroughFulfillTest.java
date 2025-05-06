package com.lloydsbanking.salsa.apapca.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.KycStatus;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import lib_sim_bo.businessobjects.CurrencyAmount;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.OverdraftDetails;
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

import javax.xml.datatype.DatatypeConfigurationException;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story15ApapcaCreatesOverdraftForCustomerThroughFulfillTest extends AbstractApapcaJBehaveTestBase {
    ActivateProductArrangementRequest request;

    ActivateProductArrangementResponse response;
    ProductArrangement productArrangement;
    ProductTypes productTypesCurrent = new ProductTypes("102", "Current Account");
    String accNumber;

    @BeforeScenario
    public void resetData() {
        mockControl.reset();
        mockScenarioHelper.clearUp();
        request = null;
        response = null;
        accNumber = null;
        productArrangement = null;
    }

    @Given("Fulfill bank account arrangement is called for all Api")
    public void givenFulfillBankAccountArrangementIsCalledForAllApi() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        ApplicationStatus applicationStatus = new ApplicationStatus("1002", "Approved");
        String channelId = mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectChannelIdByContactPointID();
        mockScenarioHelper.expectLookUpValues();
        mockScenarioHelper.expectReferenceDataForPAM();
        KycStatus eidvStatus = new KycStatus("ACCEPT");
        request = testDataHelper.createApaRequestForPca();
        productArrangement = mockScenarioHelper.expectApplicationDetailsHavingPamAndRequestData(applicationStatus, eidvStatus, "1", channelId, request, null, "123", productTypesCurrent);

        request.getProductArrangement().setArrangementId(productArrangement.getArrangementId());
        request.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
        request.getHeader().setContactPointId("0000777505");
        DepositArrangement depositArrangement = (DepositArrangement) request.getProductArrangement();
        OverdraftDetails overdraftDetails = new OverdraftDetails();
        depositArrangement.setIsOverdraftRequired(true);
        CurrencyAmount amount = new CurrencyAmount();
        amount.setAmount(BigDecimal.ONE);
        overdraftDetails.setAmount(amount);
        overdraftDetails.setIsChargingEnabled(true);
        depositArrangement.setOverdraftDetails(overdraftDetails);
        mockScenarioHelper.expectB766Call(request.getHeader(), request.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode());
        accNumber = mockScenarioHelper.expectE229Call(request.getHeader(), "779129");
        productArrangement.setAccountNumber(accNumber);
        //set overdraft Details
        ((DepositArrangement) productArrangement).setOverdraftDetails(overdraftDetails);
        ((DepositArrangement) productArrangement).setIsOverdraftRequired(true);
        mockScenarioHelper.expectCreateAccountB675Call(request.getHeader(), productArrangement);
        String sortCode = productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode();
        mockScenarioHelper.expectLookUpValuesWithISOCode();
        mockScenarioHelper.expectRetrieveProductCondition((DepositArrangement) productArrangement,request.getHeader());
        mockScenarioHelper.expectC808Call(sortCode, accNumber, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC846Call("0071776000", "1", "R", 50, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), request.getHeader());
        mockScenarioHelper.expectC812Call("0071776000", "1", sortCode, 6363l, 4, Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()), accNumber, request.getHeader());

        mockScenarioHelper.expectE226AddsOverdraftDetailInSecondScenario(Long.valueOf(productArrangement.getArrangementId()), productArrangement.getFinancialInstitution().getChannel(), request.getHeader());
    }

    @Given("Create Overdraft is called by substatus null")
    public void givenCreateOverdraftIsCalledBySubstatusNull() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectC234Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectF060Call(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectC658Call(request.getProductArrangement(), request.getHeader(), 0);
        mockScenarioHelper.expectB751CallWithAppIdAndAppVer(request.getProductArrangement(), request.getHeader(), BigInteger.valueOf(227323270), accNumber, BigInteger.valueOf(971461460), BigInteger.ZERO);
        mockScenarioHelper.expectB276Call((DepositArrangement) productArrangement, request.getHeader());
        mockScenarioHelper.expectActivateBenefitCall(productArrangement, request.getHeader(), 0);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_WELCOME_MSG", request.getHeader(), null, "Email", true);
        mockScenarioHelper.expectSendCommunicationCall((DepositArrangement) productArrangement, "CA_BENEFITS_MSG", request.getHeader(), null, "Email", false);

    }

    @Given("Create Overdraft is called by substatus null call fails")
    public void givenCreateOverdraftIsCalledBySubstatusNullCallFails() {
        mockScenarioHelper.expectB276CallBySubstatusNullCallFails((DepositArrangement) productArrangement, request.getHeader());
    }

    @When("there is a call to APAD")
    public void whenThereIsACallToAPAD() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        mockControl.go();
        response = apaPcaClient.activateProductArrangement(request);
        mockScenarioHelper.sleep();
    }

    @Then("APAPCA returns response")
    public void thenAPAPCAReturnsResponse() {
        assertNotNull(response);
        assertEquals("1010", response.getProductArrangement().getApplicationStatus());
    }


    @Then("APAPCA returns response with status as Awaiting Fulfill(1009)")
    public void thenAPAPCAReturnsResponseWithStatusAsAwaitingFulfill1009() {
        assertNotNull(response);
        assertEquals("1009", response.getProductArrangement().getApplicationStatus());
        assertEquals("007", response.getResultCondition().getExtraConditions().getConditions().get(0).getReasonCode());
        assertEquals("Failed to create overdraft", response.getResultCondition().getExtraConditions().getConditions().get(0).getReasonText());
    }
}
