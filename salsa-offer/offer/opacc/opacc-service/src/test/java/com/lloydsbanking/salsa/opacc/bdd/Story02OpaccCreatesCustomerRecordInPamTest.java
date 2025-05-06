package com.lloydsbanking.salsa.opacc.bdd;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.offer.ApplicantType;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.NetworkAffiliatedDetails;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story02OpaccCreatesCustomerRecordInPamTest extends AbstractOpaccJBehaveTestBase {
    OfferProductArrangementRequest request;
    OfferProductArrangementResponse response;
    int applicationsSize;
    int individualsSize;
    int partyApplicationsSize;
    int streetAddressesSize;
    int promoPartyApplicationsSize;

    @BeforeScenario
    public void resetResponse() {
        request = null;
        response = null;
        applicationsSize = 0;
        individualsSize = 0;
        partyApplicationsSize = 0;
        streetAddressesSize = 0;
        promoPartyApplicationsSize = 0;
    }

    @Given("affiliate details are present")
    public void givenAffiliateDetailsArePresent() {
        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");
    }

    @Given("affiliate details are present with affiliateNetworkAffiliateId")
    public void givenAffiliateDetailsArePresentWithAffiliateNetworkAffiliateId() {
        request = dataHelper.generateOfferProductArrangementPCCRequest("LTB");

        NetworkAffiliatedDetails networkAffiliatedDetails = new NetworkAffiliatedDetails();
        networkAffiliatedDetails.setAffiliateNetworkAffiliateId("1520");
        request.getProductArrangement().getAffiliatedetails().get(0).getIsAffiliateNetworkFor().add(networkAffiliatedDetails);
    }

    @Given("record exists in PROMOTION_PARTY_EXT_SYSTEMS table for given affiliateNetworkAffiliateId")
    public void givenRecordExistsInPROMOTION_PARTY_EXT_SYSTEMSTableForGivenAffiliateNetworkAffiliateId() {
        mockScenarioHelper.expectSavePromotionPartyExtSystemsInPamDb(
                request.getProductArrangement().getAffiliatedetails().get(0).getIsAffiliateNetworkFor().get(0).getAffiliateNetworkAffiliateId());
    }

    @Given("no record exists in PROMOTION_PARTY_EXT_SYSTEMS table for given affiliateNetworkAffiliateId")
    public void givenNoRecordExistsInPROMOTION_PARTY_EXT_SYSTEMSTableForGivenAffiliateNetworkAffiliateId() {
        mockScenarioHelper.expectSavePromotionPartyExtSystemsInPamDb("1521");
    }

    @When("UI calls OPACC with valid request")
    public void whenUICallsOPACCWithValidRequest() throws Exception {
        mockScenarioHelper.expectF447Call(dataHelper.createOpaccRequestHeader("LTB"), dataHelper.createPostalAddressList(), dataHelper.createIsPlayedBy());
        mockScenarioHelper.expectF336Call(dataHelper.createOpaccRequestHeader("LTB"), 1, 2, "12345");
        mockScenarioHelper.expectF061Call(dataHelper.createOpaccRequestHeader("LTB"), "12345");
        mockScenarioHelper.expectLookupListtFromChannelAndGroupCodeList("ENCRYPT_KEY_GROUP", "LTB");
        mockScenarioHelper.expectEncryptDataServiceCall(dataHelper.createOpaccRequestHeader("LTB"), "aaa");
        mockScenarioHelper.expectGetChannelIdFromContactPointId("0000777505");
        mockScenarioHelper.expectLookupDataForLegalEntity("LTB", "MAN_LEGAL_ENT_CODE");
        mockScenarioHelper.expectF424Call(dataHelper.createOpaccRequestHeader("LTB"), "1", dataHelper.createReferralCodeList("code", "description"));

        applicationsSize = mockScenarioHelper.expectApplicationsCreated();
        individualsSize = mockScenarioHelper.expectIndividualsCreated();
        partyApplicationsSize = mockScenarioHelper.expectPartyApplicationsCreated();
        streetAddressesSize = mockScenarioHelper.expectStreetAddressesCreated();
        promoPartyApplicationsSize = mockScenarioHelper.expectPromoPartyApplicationsCreated();

        mockScenarioHelper.expectLookupDataForX711("LTB");
        Customer customer = request.getProductArrangement().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("12345");
        customer.getTelephoneNumber().addAll(dataHelper.createTelephoneNumber());
        mockScenarioHelper.expectX711Call(customer, request.getHeader());

        request.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());
        mockScenarioHelper.expectRpcProducts(request.getHeader());
        mockScenarioHelper.expectSwitchDtoFromSwitchName("SW_EnblMCS", "LTB", 3, "0");
        mockControl.go();
        response = opaccClient.offerProductArrangement(request);
    }

    @Then("OPACC returns individual identifier and PAM appId")
    public void thenOPACCReturnsIndividualIdentifierAndPAMAppId() {
        assertNotNull(response);
        assertNotNull(response.getProductArrangement().getArrangementId());
        assertEquals(applicationsSize + 1, mockScenarioHelper.expectApplicationsCreated());
        assertEquals(individualsSize + 1, mockScenarioHelper.expectIndividualsCreated());
        assertEquals(partyApplicationsSize + 1, mockScenarioHelper.expectPartyApplicationsCreated());
        assertEquals(streetAddressesSize + 1, mockScenarioHelper.expectStreetAddressesCreated());
    }

    @Then("OPACC saves $promoPartyAppSize record in promo party applications table in PAM DB")
    public void thenOPACCSaves0RecordInPromoPartyApplicationsTableInPAMDB(String promoPartyAppSize) {
        assertEquals(promoPartyApplicationsSize + Integer.parseInt(promoPartyAppSize), mockScenarioHelper.expectPromoPartyApplicationsCreated());
    }
}
