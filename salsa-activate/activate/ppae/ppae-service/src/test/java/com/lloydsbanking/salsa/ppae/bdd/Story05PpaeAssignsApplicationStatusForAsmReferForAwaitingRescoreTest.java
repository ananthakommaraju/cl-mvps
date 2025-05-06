package com.lloydsbanking.salsa.ppae.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.Brands;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import com.lloydsbanking.salsa.ppae.service.constant.PPAEServiceConstant;
import lib_sim_bo.businessobjects.ProductArrangement;
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
import java.util.Calendar;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story05PpaeAssignsApplicationStatusForAsmReferForAwaitingRescoreTest extends AbstractPpaeJBehaveTestBase {


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
        mockControl.reset();

    }

    @Given("credit decision is refer and productType is CC")
    public void givenCreditDecisionIsReferAndProductIsCC() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, ParseException, DatatypeConfigurationException {
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
    }


    @Given("referral code is  501 for f424")
    public void givenReferralCodeis501ForF424() throws ParseException, DatatypeConfigurationException {
        mockScenarioHelper.expectF424Call(request.getHeader(), "2", testDataHelper.createReferralCodeList("501", "description"), "NoError");

    }

    @Given("referral code is not 501 for F424")
    public void givenReferralCodeIsNot501ForF424() {
        mockScenarioHelper.expectF424Call(request.getHeader(), "2", testDataHelper.createReferralCodeList("502", "description"), "NoERROR");

    }

    @Given("f424 throws error")
    public void givenF424ThrowsError() {
        mockScenarioHelper.expectF424Call(request.getHeader(), "2", testDataHelper.createReferralCodeList("501", "description"), "ERROR");

    }


    @Given("credit decision is refer and productType is SA")
    public void givenCreditDecisionIsReferAndProductIsSA() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, ParseException, DatatypeConfigurationException {
        mockScenarioHelper.expectContactPointIDdByChannelID();
        request = testDataHelper.createPpaeRequest("2", "LTB");
        ProductTypes productTypes = new ProductTypes("101", "Savings Account");
        mockScenarioHelper.expectApplicationRelatedData(applicationStatus, productTypes, brands);
        String appId = String.valueOf(testDataHelper.createApplication(applicationStatus, year, month, Calendar.getInstance().get(Calendar.DAY_OF_MONTH), productTypes, brands).getId());
        request.setApplicationId(appId);
        request.getHeader().setArrangementId(appId);
        productArrangement = mockScenarioHelper.expectProductArrangementDetails(request.getHeader().getChannelId(), request.getApplicationId(), null);
        // mockScenarioHelper.expectRpcForOffer(request.getHeader(), "Yes");
        mockScenarioHelper.expectRetrieveProductCondition(request.getHeader(), productArrangement);
        productArrangement.getPrimaryInvolvedParty().setSourceSystemId(PPAEServiceConstant.SOURCE_SYSTEM_ID);
        productArrangement.getPrimaryInvolvedParty().setPartyIdentifier(PPAEServiceConstant.PARTY_IDENTIFIER);
        mockScenarioHelper.expectPrdDbCalls();
    }

    @Given("credit decision is refer and productType is CA")
    public void givenCreditDecisionIsReferAndProductIsCA() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, ParseException, DatatypeConfigurationException {
        mockScenarioHelper.expectContactPointIDdByChannelID();
        request = testDataHelper.createPpaeRequest("2", "LTB");
        ProductTypes productTypes = new ProductTypes("102", "Current Account");
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
    }

    @Given("referral code is  501 for 204")
    public void givenReferralCodeis501For204() throws ParseException, DatatypeConfigurationException {
        mockScenarioHelper.expectF204Call(request.getHeader(), "2", testDataHelper.createReferralCodeList("501", "description"), "NoError");

    }

    @Given("f204 throws error")
    public void givenF204ThrowsError() throws ParseException, DatatypeConfigurationException {

        mockScenarioHelper.expectF204Call(request.getHeader(), "2", testDataHelper.createReferralCodeList("501", "description"), "ERROR");
    }


    @Given("referral code is not 501 for F204")
    public void givenReferralCodeIsNot501For204() throws ParseException, DatatypeConfigurationException {
        mockScenarioHelper.expectF204Call(request.getHeader(), "2", testDataHelper.createReferralCodeList("502", "description"), "NoError");

    }

    @Given("referral code is not 501 for F205 and 204")
    public void givenReferralCodeIsNot501ForF205AndF204() throws ParseException, DatatypeConfigurationException, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockScenarioHelper.expectF204Call(request.getHeader(), "2", testDataHelper.createReferralCodeList("502", "description"), "NoError");
        mockScenarioHelper.expectF205Call(request.getHeader(), "2", testDataHelper.createReferralCodeList("502", "description"), 8, "NoError");
        mockScenarioHelper.expectLookupListFromChannelAndGroupCodeList("LTB");
        mockScenarioHelper.expectRpcForF205(request.getHeader(), 2001);
        mockScenarioHelper.expectEligibilityCa(request, true);
    }

    @Given("f205 throws error")
    public void givenF205ThrowsError() throws ParseException, DatatypeConfigurationException, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockScenarioHelper.expectF204Call(request.getHeader(), "2", testDataHelper.createReferralCodeList("502", "description"), "NoError");
        mockScenarioHelper.expectF205Call(request.getHeader(), "2", testDataHelper.createReferralCodeList("502", "description"), 8, "ERROR");
        mockScenarioHelper.expectLookupListFromChannelAndGroupCodeList("LTB");
        mockScenarioHelper.expectRpcForF205(request.getHeader(), 2001);
        mockScenarioHelper.expectEligibilityCa(request, true);
    }

    @When("UI calls PPAE with valid request")
    public void whenUICallsPPAEWithValidRequest() {
        mockControl.go();
        ppaeClient.processPendingArrangementEvent(request);
        mockScenarioHelper.sleep();
    }

    @Then("PPAE invokes modify product arrangement")
    public void thenPPAERCallsCreditRatingService() {

        //TODO
    }

    @Then("PPAE does not change status")
    public void thenPPAEDoesNotChangeStatus() {
    }

    @Then("PPAE logs error")
    public void thenPPAELogsError() {

    }

}
