package com.lloydsbanking.salsa.eligibility.bdd.wz;

import com.lloydsbanking.salsa.AcceptanceTest;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import junit.framework.Assert;
import lib_sim_bo.businessobjects.CurrencyAmount;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.ISABalance;
import lib_sim_bo.businessobjects.ProductArrangement;
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

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category({AcceptanceTest.class})
//Removing Story from WPS. Having problem in mocking switch, will implement it later
public class Story74DeciReturnsEligibilityBasedOnMaximumAmountLimitTest extends AbstractDeciJBehaveTestBase {
    DetermineEligibleCustomerInstructionsRequest request;

    DetermineEligibleCustomerInstructionsResponse response;

    @BeforeScenario
    public void resetResponse() {
        response = null;
        request = null;

    }

    @Given("existing arrangement does not exist in the request")

    public void givenExistingArrangementDoesNotExistInTheRequest() {
        request = dataHelperWZ.createEligibilityRequest("G_ISA_DMY", TestDataHelper.TEST_OCIS_ID, "IBL", "0000777505");
        request.getExistingProductArrangments().clear();
    }

    @Given("rule is CR069")

    public void givenRuleIsCR069() {
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("G_ISA_DMY", "GR007", "Customer is not eligible for an ISA", "CR069", "GRP", "ISA Opened and fund deposited within the same tax year", null, "AGA", "LTB", new BigDecimal(0));
        mockScenarioHelperWZ.expectRefInstructionRulesPrdData("P_CLUB", "GR007", "Customer is not eligible for an ISA", "CR069", "GRP", "ISA Opened and fund deposited within the same tax year", null, "AGA", "LTB", new BigDecimal(0));
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_ISA_DMY", "ISA DUMMY", "G_ISA_DMY", "ISA", null, "LTB");
        mockScenarioHelperWZ.expectGetPrdInstructionHierarchyData("P_CLUB", "Club Lloyds", "G_PCA", "PCA", null, "LTB");
        mockScenarioHelperWZ.expectMultipleCashISASwitchCall("LTB", true);

    }

    @When("the UI calls DECI with valid request")
    public void whenTheUICallsDECIWithValidRequest() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        mockControlService.go();
        response = eligibilityClientWZ.determineEligibleInstructions(request);
    }

    @Then("DECI evaluates eligibility to true")

    public void thenDECIEvaluatesEligibilityToTrue() {
        assertTrue(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertTrue(response.getProductEligibilityDetails().get(0).getDeclineReasons().isEmpty());
    }

    @Given("parent instruction mnemonic is ISA and maximum limit amount of ISA balance is greater than 0")

    public void givenParentInstructionMnemonicIsISAAndMaximumLimitAmountOfISABalanceIsGreaterThan0() {
        request = dataHelperWZ.createEligibilityRequest("G_ISA_DMY", TestDataHelper.TEST_OCIS_ID, "IBL", "0000777505");
        request.getExistingProductArrangments().add(dataHelperWZ.createExistingDepositArrangements());
        for (ProductArrangement productArrangement : request.getExistingProductArrangments()) {
            if (productArrangement instanceof DepositArrangement) {
                CurrencyAmount currencyAmount = new CurrencyAmount();
                productArrangement.setArrangementStartDate(dataHelperWZ.createXMLGregorianCalendar(2015, 10, 11));
                currencyAmount.setAmount(new BigDecimal("9513"));
                ISABalance isaBalance = new ISABalance();
                isaBalance.setMaximumLimitAmount(currencyAmount);

                ((DepositArrangement) productArrangement).setISABalance(isaBalance);

            }
        }
        mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_ISA_DMY", "00004", "3001116001", "LTB");
    }

    @Then("DECI evaluates eligibility to true and returns decline reason as Funds deposited within the same tax year.")

    public void thenDECIEvaluatesEligibilityToFalseAndReturnsDeclineReasonAsFundsDepositedWithinTheSameTaxYear() {
        assertTrue(Boolean.valueOf(response.getProductEligibilityDetails().get(0).getIsEligible()));
        assertEquals("Funds deposited within the same tax year.", response.getProductEligibilityDetails().get(0).getDeclineReasons().get(0).getDescription());
    }

    @Given("parent instruction mnemonic does not have ISA")

    public void givenParentInstructionMnemonicDoesNotHaveISA() {
        request = dataHelperWZ.createEligibilityRequest("G_ISA_DMY", TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");
        request.getExistingProductArrangments().add(dataHelperWZ.createExistingDepositArrangements());
        mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_ISA_DMY", "00004", "3001116000", "LTB");

    }

    @Given("parent instruction mnemonic hasISA and maximum limit amount of ISA balance is less than 0")

    public void givenParentInstructionMnemonicHasISAAndMaximumLimitAmountOfISABalanceIsLessThan0() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        request = dataHelperWZ.createEligibilityRequest("G_ISA_DMY", TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");
        request.getExistingProductArrangments().add(dataHelperWZ.createExistingDepositArrangements());
        for (ProductArrangement productArrangement : request.getExistingProductArrangments()) {
            if (productArrangement instanceof DepositArrangement) {
                CurrencyAmount currencyAmount = new CurrencyAmount();
                currencyAmount.setAmount(new BigDecimal("-5"));

                ((DepositArrangement) productArrangement).getISABalance().setMaximumLimitAmount(currencyAmount);
            }

        }

        mockScenarioHelperWZ.expectGetPrdInstructionLookupData("P_ISA_DMY", "00004", "3001116001", "LTB");

    }

    @Given("candidateInstruction is non ISA")
    public void givenCandidateInstructionIsNonISA() {
        request = dataHelperWZ.createEligibilityRequest("P_CLUB", TestDataHelper.TEST_OCIS_ID, "LTB", "0000777505");
    }

}




