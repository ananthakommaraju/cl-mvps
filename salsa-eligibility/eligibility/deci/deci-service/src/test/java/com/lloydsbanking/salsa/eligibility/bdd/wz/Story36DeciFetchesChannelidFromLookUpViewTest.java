package com.lloydsbanking.salsa.eligibility.bdd.wz;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.AcceptanceTestWps;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
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

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category({AcceptanceTest.class, AcceptanceTestWps.class})
public class Story36DeciFetchesChannelidFromLookUpViewTest extends AbstractDeciJBehaveTestBase

{
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg dataNotAvailableErrorFaultMsg;

    String candidateInstruction = "P_CISA_SAV";


    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;
    }

    @Given("contact point id of request is mapped in pam")
    public void givenContactPointIdOfRequestIsMappedInPam() {

        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_CISA_SAV", "Cash ISA Saver", "G_ISA", "ISA", null, "LTB");
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_CISA_SAV", "GR007", "Customer is not eligible for an ISA", "CR003", "GRP", "Customer cannot be younger that 16 years", "16", "CST", "LTB", new BigDecimal("1"));
        request = dataHelperWZ.createEligibilityRequest(candidateInstruction, TestDataHelper.TEST_OCIS_ID, "IBL", "0000777505");

    }

    @When("the UI calls DECI with valid request")
    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockControl.go();

        try {
            response = eligibilityClientWZ.determineEligibleInstructions(request);
        }
        catch (DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg determineEligibleCustomerInstructionsDataNotAvailableErrorMsg) {
            this.dataNotAvailableErrorFaultMsg = determineEligibleCustomerInstructionsDataNotAvailableErrorMsg;

        }

    }

    @Then("DECI responds")
    public void thenDECIReturnsCustomerInstructionsInResponse() {
        assertNotNull(response);

    }


    @Given("contact point id of request is not  mapped in pam")
    public void givenContactPointIdOfRequestIsNotMappedInPam() {
        request = dataHelperWZ.createEligibilityRequest(candidateInstruction, TestDataHelper.TEST_OCIS_ID, "IBL", "12347895");

    }


    @Then("DECI returns error and throws exception as dataNotAvailable")
    public void thenDECIReturnsErrorAndThrowsExceptionAsDataNotAvailable() {
        assertNotNull(dataNotAvailableErrorFaultMsg);
        assertEquals("No matching records found, error code: ", dataNotAvailableErrorFaultMsg.getFaultInfo().getDescription());
        assertEquals("REF_DATA_LOOKUP_VW", dataNotAvailableErrorFaultMsg.getFaultInfo().getEntity());
    }

}




