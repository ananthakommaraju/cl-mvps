package com.lloydsbanking.salsa.opacc.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.offer.ApplicantType;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.RetrieveProductConditionsDataNotAvailableErrorMsg;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.RetrieveProductConditionsExternalBusinessErrorMsg;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.RetrieveProductConditionsExternalServiceErrorMsg;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.RetrieveProductConditionsInternalServiceErrorMsg;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.RetrieveProductConditionsResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementExternalBusinessErrorMsg;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementExternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementInternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.OfferProductArrangementResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.Ignore;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
@Ignore
@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story08OpaccChecksForDuplicateApplicationsTest extends AbstractOpaccJBehaveTestBase {

    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;

    @BeforeScenario
    public void resetResponse() {
        request = null;
        response = null;
    }

    @Given("Duplicate applications exists in PAM")
    public void givenDuplicateApplicationsExistsInPAM() throws ParseException, DatatypeConfigurationException, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        mockScenarioHelper.expectF447Call(dataHelper.createOpaccRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaccRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("LTB"), "aaa");
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");

        mockScenarioHelper.expectLookupDataForX711("LTB");
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());
        mockScenarioHelper.expectX711Call(customer, request.getHeader());

        request.getHeader().setContactPointId("0000777505");
        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.GUARDIAN.getValue());
        request.getProductArrangement().getPrimaryInvolvedParty().setCustomerIdentifier(null);


    }

    @Given("applications status is not ASM decline")
    public void givenApplicationsStatusIsNotASMDecline() {
        mockScenarioHelper.expectLookupDataForDuplicateApplication("LTB", "BRAND_COHOLDING");
        mockScenarioHelper.expectPAMReferenceData();
        mockScenarioHelper.expectDuplicationApplications();
    }

    @When("UI calls OPACC with valid request")
    public void whenUICallsOPACCWithValidRequest() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg {
        mockScenarioHelper.expectSwitchDtoFromSwitchName("SW_EnblMCS", "LTB", 3, "0");
        try {
            mockControl.go();
            response = opaccClient.offerProductArrangement(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Given("No duplicate applications exists in PAM")
    public void givenNoDuplicateApplicationsExistsInPAM() throws ParseException, RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg {

        mockScenarioHelper.expectF447Call(dataHelper.createOpaccRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaccRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("LTB"), "aaa");

        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");

        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
        request.getHeader().setContactPointId("0000777505");
        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.GUARDIAN.getValue());
        request.getProductArrangement().getPrimaryInvolvedParty().setCustomerIdentifier(null);
        mockScenarioHelper.expectRpc(request.getHeader());

    }

    @Given("applications status is ASM decline")
    public void givenApplicationsStatusIsASMDecline() {
        mockScenarioHelper.expectLookupDataForDuplicateApplication("LTB", "BRAND_COHOLDING");
        mockScenarioHelper.expectPAMReferenceData();
        mockScenarioHelper.expectDuplicationApplicationsWithASMDecline();
    }

    @Then("OPACC returns duplicate application error in response")
    public void thenOPACCReturnsDuplicateApplicationErrorInResponse() {
        assertNotNull(response);
        assertEquals("829001", response.getProductArrangement().getAssociatedProduct().getEligibilityDetails().getDeclineReasons().get(0).getCode());
    }

    @Then("OPACC returns duplicate application with ASM decline error in response")
    public void thenOPACCReturnsDuplicateApplicationWithASMDeclineErrorInResponse() {
        assertNotNull(response);
        assertEquals("829002", response.getProductArrangement().getAssociatedProduct().getEligibilityDetails().getDeclineReasons().get(0).getCode());
    }

    @Then("OPACC returns valid response")
    public void thenOPACCReturnsValidResponse() {
        assertNotNull(response);
    }

}
