package com.lloydsbanking.salsa.ppae.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.constants.EmailTemplateEnum;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.Brands;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import com.lloydsbanking.salsa.ppae.service.constant.PPAEServiceConstant;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_communicationmanager.id_communicationmanagerrouter.*;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


@Category({AcceptanceTest.class})
public class Story12PpaeSendEmailForAsmDeclineForAwaitingRescoreTest extends AbstractPpaeJBehaveTestBase {

    ProcessPendingArrangementEventRequest request;
    ApplicationStatus applicationStatus = new ApplicationStatus("1006", "AWAITING RESCORE");
    ProductArrangement productArrangement;
    int year = Calendar.getInstance().get(Calendar.YEAR);
    int month = Calendar.getInstance().get(Calendar.MONTH);
    Brands brands = new Brands("LTB", "Lloyds");

    @BeforeScenario
    public void resetData() {
        mockControl.reset();
        mockScenarioHelper.clearUp();
        request = null;


    }

    @Given("Asm decision is Decline,Arrangement Type  CC and decline source BUREAU")
    public void givenAsmDecisionIsDeclineAndArrangementTypeIsCC() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectContactPointIDdByChannelID();
        request = testDataHelper.createPpaeRequest("2", "LTB");
        ProductTypes productTypes = new ProductTypes("100", "Credit Card");
        mockScenarioHelper.expectApplicationRelatedData(applicationStatus, productTypes, brands);

        String appId = String.valueOf(testDataHelper.createApplication(applicationStatus, year, month, Calendar.getInstance().get(Calendar.DAY_OF_MONTH), productTypes, brands).getId());
        request.setApplicationId(appId);
        request.getHeader().setArrangementId(appId);
        productArrangement = mockScenarioHelper.expectProductArrangementDetails(request.getHeader().getChannelId(), request.getApplicationId(), null);
        mockScenarioHelper.expectRpcForOffer(request.getHeader(), "Yes");
        mockScenarioHelper.expectRetrieveProductCondition(request.getHeader(), productArrangement);
        productArrangement.getPrimaryInvolvedParty().setSourceSystemId(PPAEServiceConstant.SOURCE_SYSTEM_ID);
        productArrangement.getPrimaryInvolvedParty().setPartyIdentifier(PPAEServiceConstant.PARTY_IDENTIFIER);
        mockScenarioHelper.expectPrdDbCalls();
        List<String> lookUpText = new ArrayList<>();
        lookUpText.add(0, "Cnt_Pnt_Prtflio");
        mockScenarioHelper.expectF424Call(request.getHeader(), "3", testDataHelper.createReferralCodeList("Cnt_Pnt_Prtflio", "description"), "NoError");
        mockScenarioHelper.expectLookupListFromGroupCodeAndChannelAndLookUpText("ASM_DECLINE_CODE", "LTB", lookUpText);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, EmailTemplateEnum.BUREAU_DECLINE_EMAIL.getTemplate(), request.getHeader(), null, ActivateCommonConstant.CommunicationTypes.EMAIL);

    }

    @Given("Asm decision is Decline,Arrangement Type  SA and decline source BUREAU")
    public void givenAsmDecisionIsDeclineAndArrangementTypeIsSA() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, ParseException, DatatypeConfigurationException, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectContactPointIDdByChannelID();
        request = testDataHelper.createPpaeRequest("2", "LTB");
        ProductTypes productTypes = new ProductTypes("101", "Savings Account");
        mockScenarioHelper.expectApplicationRelatedData(applicationStatus, productTypes, brands);
        String appId = String.valueOf(testDataHelper.createApplication(applicationStatus, year, month, Calendar.getInstance().get(Calendar.DAY_OF_MONTH), productTypes, brands).getId());
        request.setApplicationId(appId);
        request.getHeader().setArrangementId(appId);
        productArrangement = mockScenarioHelper.expectProductArrangementDetails(request.getHeader().getChannelId(), request.getApplicationId(), null);
        //mockScenarioHelper.expectRpcForOffer(request.getHeader(), "Yes");
        mockScenarioHelper.expectRetrieveProductCondition(request.getHeader(), productArrangement);
        productArrangement.getPrimaryInvolvedParty().setSourceSystemId(PPAEServiceConstant.SOURCE_SYSTEM_ID);
        productArrangement.getPrimaryInvolvedParty().setPartyIdentifier(PPAEServiceConstant.PARTY_IDENTIFIER);
        mockScenarioHelper.expectPrdDbCalls();
        List<String> lookUpText = new ArrayList<>();
        lookUpText.add(0, "Cnt_Pnt_Prtflio");
        mockScenarioHelper.expectF204Call(request.getHeader(), "3", testDataHelper.createReferralCodeList("Cnt_Pnt_Prtflio", "description"), "NoError");
        mockScenarioHelper.expectLookupListFromGroupCodeAndChannelAndLookUpText("ASM_DECLINE_CODE", "LTB", lookUpText);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, EmailTemplateEnum.SA_BUREAU_DECLINE_EMAIL.getTemplate(), request.getHeader(), null, ActivateCommonConstant.CommunicationTypes.EMAIL);

    }


    @Given("Asm decision is Decline,Arrangement Type  CC and decline source duplicate application")
    public void givenAsmDecisionIsDeclineAndArrangementTypeCCAndDuplicateApplication() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectContactPointIDdByChannelID();
        request = testDataHelper.createPpaeRequest("2", "LTB");
        ProductTypes productTypes = new ProductTypes("100", "Credit Card");
        mockScenarioHelper.expectApplicationRelatedData(applicationStatus, productTypes, brands);
        String appId = String.valueOf(testDataHelper.createApplication(applicationStatus, year, month, Calendar.getInstance().get(Calendar.DAY_OF_MONTH), productTypes, brands).getId());
        request.setApplicationId(appId);
        request.getHeader().setArrangementId(appId);
        productArrangement = mockScenarioHelper.expectProductArrangementDetails(request.getHeader().getChannelId(), request.getApplicationId(), null);
        mockScenarioHelper.expectRpcForOffer(request.getHeader(), "Yes");
        mockScenarioHelper.expectRetrieveProductCondition(request.getHeader(), productArrangement);
        productArrangement.getPrimaryInvolvedParty().setSourceSystemId(PPAEServiceConstant.SOURCE_SYSTEM_ID);
        productArrangement.getPrimaryInvolvedParty().setPartyIdentifier(PPAEServiceConstant.PARTY_IDENTIFIER);
        mockScenarioHelper.expectPrdDbCalls();
        List<String> lookUpText = new ArrayList<>();
        lookUpText.add(0, "Cnt_Pnt_Prtflio");
        mockScenarioHelper.expectF424Call(request.getHeader(), "3", testDataHelper.createReferralCodeList("Cnt_Pnt_Prtflio", "description"), "NoError");
        mockScenarioHelper.expectLookupListFromGroupCodeAndChannelAndLookUpTextForDuplicate("ASM_DECLINE_CODE", "LTB", lookUpText);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, EmailTemplateEnum.BUREAU_DECLINE_EMAIL.getTemplate(), request.getHeader(), null, ActivateCommonConstant.CommunicationTypes.EMAIL);

    }


    @Given("Asm decision is Decline,Arrangement Type  SA and decline source duplicate application")
    public void givenAsmDecisionIsDeclineAndArrangementTypeIsSADuplicateApplication() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, ParseException, DatatypeConfigurationException, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectContactPointIDdByChannelID();
        request = testDataHelper.createPpaeRequest("2", "LTB");
        ProductTypes productTypes = new ProductTypes("101", "Savings Account");
        mockScenarioHelper.expectApplicationRelatedData(applicationStatus, productTypes, brands);
        String appId = String.valueOf(testDataHelper.createApplication(applicationStatus, year, month, Calendar.getInstance().get(Calendar.DAY_OF_MONTH), productTypes, brands).getId());
        request.setApplicationId(appId);
        request.getHeader().setArrangementId(appId);
        productArrangement = mockScenarioHelper.expectProductArrangementDetails(request.getHeader().getChannelId(), request.getApplicationId(), null);
        mockScenarioHelper.expectRpcForOffer(request.getHeader(), "Yes");
        mockScenarioHelper.expectRetrieveProductCondition(request.getHeader(), productArrangement);
        productArrangement.getPrimaryInvolvedParty().setSourceSystemId(PPAEServiceConstant.SOURCE_SYSTEM_ID);
        productArrangement.getPrimaryInvolvedParty().setPartyIdentifier(PPAEServiceConstant.PARTY_IDENTIFIER);
        mockScenarioHelper.expectPrdDbCalls();
        List<String> lookUpText = new ArrayList<>();
        lookUpText.add(0, "Cnt_Pnt_Prtflio");
        mockScenarioHelper.expectF204Call(request.getHeader(), "3", testDataHelper.createReferralCodeList("Cnt_Pnt_Prtflio", "description"), "NoError");
        mockScenarioHelper.expectLookupListFromGroupCodeAndChannelAndLookUpTextForDuplicate("ASM_DECLINE_CODE", "LTB", lookUpText);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, EmailTemplateEnum.SA_DUPLICATE_DECLINE_EMAIL.getTemplate(), request.getHeader(), null, ActivateCommonConstant.CommunicationTypes.EMAIL);

    }

    @Given("Asm decision is Decline,Arrangement Type  CA and decline source BUREAU")
    public void givenAsmDecisionIsDeclineAndArrangementTypeIsCA() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ParseException, DatatypeConfigurationException, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {

        mockScenarioHelper.expectContactPointIDdByChannelID();
        request = testDataHelper.createPpaeRequest("2", "LTB");
        ProductTypes productTypes = new ProductTypes("102", "Current Account");
        mockScenarioHelper.expectApplicationRelatedData(applicationStatus, productTypes, brands);
        String appId = String.valueOf(testDataHelper.createApplication(applicationStatus, year, month, Calendar.getInstance().get(Calendar.DAY_OF_MONTH), productTypes, brands).getId());
        request.setApplicationId(appId);
        request.getHeader().setArrangementId(appId);
        productArrangement = mockScenarioHelper.expectProductArrangementDetails(request.getHeader().getChannelId(), request.getApplicationId(), null);
        mockScenarioHelper.expectRpcForOffer(request.getHeader(), "Yes");
        mockScenarioHelper.expectRetrieveProductCondition(request.getHeader(), productArrangement);
        productArrangement.getPrimaryInvolvedParty().setSourceSystemId(PPAEServiceConstant.SOURCE_SYSTEM_ID);
        productArrangement.getPrimaryInvolvedParty().setPartyIdentifier(PPAEServiceConstant.PARTY_IDENTIFIER);
        mockScenarioHelper.expectPrdDbCalls();
        List<String> lookUpText = new ArrayList<>();
        lookUpText.add(0, "Cnt_Pnt_Prtflio");
        mockScenarioHelper.expectF204Call(request.getHeader(), "3", testDataHelper.createReferralCodeList("Cnt_Pnt_Prtflio", "description"), "NoError");
        mockScenarioHelper.expectLookupListFromGroupCodeAndChannelAndLookUpText("ASM_DECLINE_CODE", "LTB", lookUpText);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, EmailTemplateEnum.CA_DECLINE_BUREAU_MSG.getTemplate(), request.getHeader(), null, ActivateCommonConstant.CommunicationTypes.EMAIL);

    }

    @Given("Asm decision is Decline,Arrangement Type  CA and decline source duplicate application")
    public void givenAsmDecisionIsDeclineAndArrangementTypeIsCAForDuplicateApplication() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ParseException, DatatypeConfigurationException, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {

        mockScenarioHelper.expectContactPointIDdByChannelID();
        request = testDataHelper.createPpaeRequest("2", "LTB");
        ProductTypes productTypes = new ProductTypes("102", "Current Account");
        mockScenarioHelper.expectApplicationRelatedData(applicationStatus, productTypes, brands);
        String appId = String.valueOf(testDataHelper.createApplication(applicationStatus, year, month, Calendar.getInstance().get(Calendar.DAY_OF_MONTH), productTypes, brands).getId());
        request.setApplicationId(appId);
        request.getHeader().setArrangementId(appId);
        productArrangement = mockScenarioHelper.expectProductArrangementDetails(request.getHeader().getChannelId(), request.getApplicationId(), null);
        mockScenarioHelper.expectRpcForOffer(request.getHeader(), "Yes");
        mockScenarioHelper.expectRetrieveProductCondition(request.getHeader(), productArrangement);
        productArrangement.getPrimaryInvolvedParty().setSourceSystemId(PPAEServiceConstant.SOURCE_SYSTEM_ID);
        productArrangement.getPrimaryInvolvedParty().setPartyIdentifier(PPAEServiceConstant.PARTY_IDENTIFIER);
        mockScenarioHelper.expectPrdDbCalls();
        List<String> lookUpText = new ArrayList<>();
        lookUpText.add(0, "Cnt_Pnt_Prtflio");
        mockScenarioHelper.expectF204Call(request.getHeader(), "3", testDataHelper.createReferralCodeList("Cnt_Pnt_Prtflio", "description"), "NoError");
        mockScenarioHelper.expectLookupListFromGroupCodeAndChannelAndLookUpTextForDuplicate("ASM_DECLINE_CODE", "LTB", lookUpText);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, EmailTemplateEnum.CA_DECLINE_DPLICT_MSG.getTemplate(), request.getHeader(), null, ActivateCommonConstant.CommunicationTypes.EMAIL);

    }


    @When("There is a call to PPAE")
    public void whenThereIsACallToPPAE() {
        mockControl.go();
        ppaeClient.processPendingArrangementEvent(request);
        mockScenarioHelper.sleep();
    }

    @Then("PPAE calls send communications service")
    public void thenPPAECallsSendCommunicationsService() {
        // PENDING
    }

}
