package com.lloydsbanking.salsa.eligibility.bdd.wz;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.service.utility.header.GmoToGboRequestHeaderConverter;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.messages.RequestHeader;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_bo.businessobjects.ProductEligibilityDetails;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsExternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsInternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story78DeciReturnsEligibilityBasedOnRestrictedPostCodeTest extends AbstractDeciJBehaveTestBase{
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    GmoToGboRequestHeaderConverter headerConverter;

    lib_sim_gmo.messages.RequestHeader header;

    RequestHeader gboHeader;

    @BeforeScenario
    public void initialize() {
        response = null;
        headerConverter=new GmoToGboRequestHeaderConverter();
        header=dataHelperWZ.createEligibilityRequestHeader("LTB", TestDataHelper.TEST_INTERACTION, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, "0000777505");
        gboHeader=headerConverter.convert(header);
    }

    @Given("Customer does not belong to restricted post code")
    public void givenCustomerDoesNotBelongToRestrictedPostCode() throws DetermineEligibleInstructionsInternalServiceErrorMsg {
        request = dataHelperWZ.createEligibilityRequest("G_INSURANCE", TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");
        request.setArrangementType("CA");
        request.getCustomerDetails().setForeignAddressIndicator(false);
        PostalAddress postalAddress=new PostalAddress();
        postalAddress.setUnstructuredAddress(new UnstructuredAddress());
        postalAddress.getUnstructuredAddress().setPostCode("IM14753");

        request.getCustomerDetails().getPostalAddress().add(postalAddress);
        mockScenarioHelperWZ.expectF075Call(gboHeader, "F", "542107294", "097", "082", "097", "082");
        mockScenarioHelperWZ.expectGetPamLookUpData("PARTY_EVID_TYPE_CODE", "097", "Party Evidence Type Code List", "097", "LTB", new Long("1"));
        mockScenarioHelperWZ.expectRefLookUpGrdData(new BigDecimal("14753"), "RSTRCTD_PST_CDE", "RESTRICTED_POST_CODE", "Y", "TL", null, "JE", null, null, "JE", "en", "ACTIVE", "IBL", null);
    }

    @Given("Customer has foreign address indicator true")
    public void givenCustomerHasForeignAddressIndicatorTrue() throws DetermineEligibleInstructionsInternalServiceErrorMsg {
        request = dataHelperWZ.createEligibilityRequest("G_INSURANCE", TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");
        request.setArrangementType("CA");
        request.getCustomerDetails().setForeignAddressIndicator(true);
        PostalAddress postalAddress=new PostalAddress();
        postalAddress.setUnstructuredAddress(new UnstructuredAddress());
        postalAddress.getUnstructuredAddress().setPostCode("IM14753");
        request.getCustomerDetails().getPostalAddress().add(postalAddress);
        mockScenarioHelperWZ.expectF075Call(gboHeader, "F", "542107294", "097", "082", "097", "082");
        mockScenarioHelperWZ.expectGetPamLookUpData("PARTY_EVID_TYPE_CODE", "097", "Party Evidence Type Code List", "097", "LTB", new Long("1"));
        mockScenarioHelperWZ.expectRefLookUpGrdData(new BigDecimal("14753"), "RSTRCTD_PST_CDE", "RESTRICTED_POST_CODE","Y", "TL", null, "JE", null,null,"JE","en","ACTIVE", "IBL", null);
    }

    @Given("Customer belongs to restricted post code")
    public void givenCustomerBelongsToRestrictedPostCode() throws DetermineEligibleInstructionsInternalServiceErrorMsg {
        request = dataHelperWZ.createEligibilityRequest("G_INSURANCE", TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");
        request.setArrangementType("CA");
        request.getCustomerDetails().setForeignAddressIndicator(true);
        PostalAddress postalAddress=new PostalAddress();
        postalAddress.setUnstructuredAddress(new UnstructuredAddress());
        postalAddress.getUnstructuredAddress().setPostCode("JE14753");
        request.getCustomerDetails().getPostalAddress().add(postalAddress);
        mockScenarioHelperWZ.expectF075Call(gboHeader, "F", "542107294", "097", "082", "097", "082");
        mockScenarioHelperWZ.expectGetPamLookUpData("PARTY_EVID_TYPE_CODE", "097", "Party Evidence Type Code List", "097", "LTB", new Long("1"));
        mockScenarioHelperWZ.expectRefLookUpGrdData(new BigDecimal("14753"), "RSTRCTD_PST_CDE", "RESTRICTED_POST_CODE","Y", "TL", null, "JE", null,null,"JE","en","ACTIVE", "IBL", null);
    }

    @Given("rule is CR066")
    public void givenRuleIsCR066() {
       mockScenarioHelperWZ.expectRefInstructionRulesPrdData("G_INSURANCE",	"GR024", "Customer is not eligible for Life Insurance",	"CR066", "GRP", "Customer does not fulfil UK Residency check", "10", "CST", "LTB", new BigDecimal("1"));
    }

    @When("the UI calls DECI with valid request")
    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockControlService.go();
        response = eligibilityClientWZ.determineEligibleInstructions(request);
    }

    @Then("DECI evaluates eligibility to true")
    public void thenDECIEvaluatesEligibilityToTrue() {
        boolean eligible=false;
        for(ProductEligibilityDetails eligibilityDetails: response.getProductEligibilityDetails()){
            if (Boolean.valueOf(eligibilityDetails.getIsEligible())){
                eligible=true;
                break;
            }
        }
        assertTrue(eligible);
    }

    @Then("DECI evaluates eligibility to false and returns Customer fails UK residency check")
    public void thenDECIEvaluatesEligibilityToFalseAndReturnsCustomerFailsUKResidencyCheck() {
        for(ProductEligibilityDetails eligibilityDetails: response.getProductEligibilityDetails()){
            if (!eligibilityDetails.getDeclineReasons().isEmpty()){
                assertFalse(Boolean.valueOf(eligibilityDetails.getIsEligible()));
                assertEquals("Customer fails UK residency check", eligibilityDetails.getDeclineReasons().get(0).getDescription());
                break;
            }
        }
    }

}
