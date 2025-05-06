package com.lloydsbanking.salsa.ppae.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.Brands;
import com.lloydsbanking.salsa.downstream.pam.model.ProductTypes;
import com.lloydsbanking.salsa.soap.ocis.f595.objects.F595Resp;
import com.lloydsbanking.salsa.soap.ocis.f595.objects.PersonalDetails;
import com.lloydsbanking.salsa.soap.pad.q028.objects.Q028Resp;
import lib_sim_bo.businessobjects.IndividualName;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import lib_sim_communicationmanager.id_communicationmanagerrouter.*;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.Ignore;
import org.junit.experimental.categories.Category;

import java.util.Calendar;

@Ignore
@Category({AcceptanceTest.class})
public class Story13PpaeProcessAwaitingReferralLraApplicationForFulfilmentTest extends AbstractPpaeJBehaveTestBase {
    ProcessPendingArrangementEventRequest request;
    ApplicationStatus applicationStatus;
    ProductTypes productTypes;
    ProductArrangement productArrangement;
    RequestHeader header;
    int year = Calendar.getInstance().get(Calendar.YEAR);
    int month = Calendar.getInstance().get(Calendar.MONTH);
    Brands brands = new Brands("LTB", "Lloyds");
    Q028Resp q028Resp;

    @BeforeScenario
    public void resetData() {
        mockControl.reset();
        mockScenarioHelper.clearUp();
        request = null;
        header = null;
        q028Resp = null;
    }

    @Given("application status is REFERRAL_PROCESSED and TMS task id is present in pam")
    public void givenAsmDecisionIsDeclineAndArrangementTypeIsCC() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        applicationStatus = new ApplicationStatus("1012", "REFERRAL_PROCESSED");
        productTypes = new ProductTypes("103", "Loan Account");
        request = testDataHelper.createPpaeRequest();
        header = testDataHelper.createPpaeRequest().getHeader();
        mockScenarioHelper.expectApplicationRelatedData(applicationStatus, productTypes, brands);
        String appId = String.valueOf(testDataHelper.createApplication(applicationStatus, year, month, Calendar.getInstance().get(Calendar.DAY_OF_MONTH), productTypes, brands).getId());
        request.setApplicationId(appId);
        request.getHeader().setArrangementId(appId);
        mockScenarioHelper.expectReferralDetails(1234l, request.getApplicationId());
        String contactPointId = mockScenarioHelper.expectContactPointIDdByChannelID();
        header.setContactPointId(contactPointId);
        productArrangement = mockScenarioHelper.expectProductArrangementDetails(header.getChannelId(), request.getApplicationId(), null);
    }

    @Given("retrieve loan details succeeds")
    public void givenRetrieveLoanDetailsSucceeds() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        q028Resp = mockScenarioHelper.expectQ028Call(productArrangement.getPrimaryInvolvedParty(), "1", header, 0);
        productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(String.valueOf(q028Resp.getApplicantDetails().getParty().get(0).getPartyId()));
        mockScenarioHelper.expectB233Call(q028Resp, header);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "LRA_ACCEPT_MSG", header, null, "Email");
        F595Resp f595Resp = mockScenarioHelper.expectF595Call(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier(), header, 0);
        setPersonalDetails(f595Resp, productArrangement);
    }

    @Given("retrieve loan details fails")
    public void givenRetrieveLoanDetailsFails() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        q028Resp = mockScenarioHelper.expectQ028Call(productArrangement.getPrimaryInvolvedParty(), "1", header, 1234);
    }

    @Given("asm decision is decline or accept")
    public void givenAsmDecisionIsDeclineOrAccept() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        q028Resp = mockScenarioHelper.expectQ028Call(productArrangement.getPrimaryInvolvedParty(), "1", header, 0);
        productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(String.valueOf(q028Resp.getApplicantDetails().getParty().get(0).getPartyId()));
    }

    @Given("prepare finance service arrangement succeeds")
    public void givePrepareFinanceServiceArrangementSucceeds() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectB233Call(q028Resp, header);
        F595Resp f595Resp = mockScenarioHelper.expectF595Call(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier(), header, 0);
        setPersonalDetails(f595Resp, productArrangement);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "LRA_ACCEPT_MSG", header, null, "Email");
    }

    @Given("retrieve personal details succeeds")
    public void giveRetrievePersonalDetailsSucceeds() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        q028Resp = mockScenarioHelper.expectQ028Call(productArrangement.getPrimaryInvolvedParty(), "1", header, 0);
        productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(String.valueOf(q028Resp.getApplicantDetails().getParty().get(0).getPartyId()));
        mockScenarioHelper.expectB233Call(q028Resp, header);
        F595Resp f595Resp = mockScenarioHelper.expectF595Call(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier(), header, 0);
        setPersonalDetails(f595Resp, productArrangement);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "LRA_ACCEPT_MSG", header, null, "Email");
    }

    @Given("retrieve personal details fails")
    public void giveRetrievePersonalDetailsFails() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        q028Resp = mockScenarioHelper.expectQ028Call(productArrangement.getPrimaryInvolvedParty(), "1", header, 0);
        productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(String.valueOf(q028Resp.getApplicantDetails().getParty().get(0).getPartyId()));
        mockScenarioHelper.expectB233Call(q028Resp, header);
        mockScenarioHelper.expectF595Call(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier(), header, 1234);
    }

    @Given("asm decision is accept")
    public void giveAsmDecisionIsAccept() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        q028Resp = mockScenarioHelper.expectQ028Call(productArrangement.getPrimaryInvolvedParty(), "1", header, 0);
        productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(String.valueOf(q028Resp.getApplicantDetails().getParty().get(0).getPartyId()));
        mockScenarioHelper.expectB233Call(q028Resp, header);
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "LRA_ACCEPT_MSG", header, null, "Email");
    }

    @Given("asm decision is decline")
    public void giveAsmDecisionIsDecline() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        q028Resp = mockScenarioHelper.expectQ028Call(productArrangement.getPrimaryInvolvedParty(), "3", header, 0);
        productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(String.valueOf(q028Resp.getApplicantDetails().getParty().get(0).getPartyId()));
        mockScenarioHelper.expectB233Call(q028Resp, header);
    }

    @Given("email is present")
    public void givenEmailIsPresent() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        F595Resp f595Resp = mockScenarioHelper.expectF595Call(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier(), header, 0);
        setPersonalDetails(f595Resp, productArrangement);
    }

    @Given("retrieve look up value for decline template succeeds")
    public void givenRetrieveLookUpValueForDeclineTemplateSucceeds() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        String lookUpDesc = mockScenarioHelper.expectLookUpValueForDeclineTemplate();
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "LRA_DECLINE_MSG" + lookUpDesc, header, null, "Email");
    }

    @Given("retrieve look up value for decline template fails")
    public void givenRetrieveLookUpValueForDeclineTemplateFails() throws SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        mockScenarioHelper.expectSendCommunicationCall(productArrangement, "LRA_DECLINE_MSG", header, null, "Email");
    }

    @When("there is call to PPAE for LRA application")
    public void whenThereIsCallToPPAEForLRAApplication() {
        mockControl.go();
        ppaeClient.processPendingArrangementEvent(request);
    }

    @Then("service continues")
    public void thenServiceContinues() {
        //mockScenarioHelper.verifyExpectCalls();
    }

    @Then("service communicates email to customer with template LRA_ACCEPT_MSG")
    public void thenServiceCommunicatesEmailToCustomerWithTemplateLRA_ACCEPT_MSG() {
        //mockScenarioHelper.verifyExpectCalls();
    }

    @Then("service communicates email to customer with decline template")
    public void thenServiceCommunicatesEmailToCustomerWithDeclineTemplate() {
        //mockScenarioHelper.verifyExpectCalls();
    }

    @Then("service communicates email to customer with default decline template LRA_DECLINE_MSG")
    public void thenServiceCommunicatesEmailToCustomerWithDefaultDeclineTemplateLRA_DECILNE_MSG() {
        //mockScenarioHelper.verifyExpectCalls();
    }

    private void setPersonalDetails(F595Resp f595Resp, ProductArrangement productArrangement) {
        PersonalDetails personalDetails = f595Resp.getPartyGroup().getPersonalDetails();
        productArrangement.getPrimaryInvolvedParty().setEmailAddress(personalDetails.getEmailAddressTx());
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().add(new IndividualName());
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().get(0).setLastName(personalDetails.getSurname());
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().get(0).setPrefixTitle(personalDetails.getPartyTl());
        productArrangement.getPrimaryInvolvedParty().getPostalAddress().add(new PostalAddress());
        productArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).setStatusCode("CURRENT");
        if (productArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress() == null) {
            productArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).setUnstructuredAddress(new UnstructuredAddress());
        }
        productArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setPostCode(f595Resp.getPartyGroup().getAddressGroup().getPostCd());
    }


}
