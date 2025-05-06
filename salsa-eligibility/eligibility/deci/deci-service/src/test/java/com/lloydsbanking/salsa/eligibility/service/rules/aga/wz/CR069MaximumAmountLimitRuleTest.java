package com.lloydsbanking.salsa.eligibility.service.rules.aga.wz;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import lib_sim_bo.businessobjects.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CR069MaximumAmountLimitRuleTest {

    private CR069MaximumAmountLimitRule rule;

    RuleDataHolder ruleDataHolder;

    TestDataHelper dataHelper;

    @Before
    public void setUp() {
        rule = new CR069MaximumAmountLimitRule();
        ruleDataHolder = new RuleDataHolder();
        dataHelper = new TestDataHelper();
        rule.switchClient = mock(SwitchService.class);
    }

    @Test
    public void testEvaluateReturnsTrue() throws EligibilityException {
        List<ProductArrangement> productArrangements = new ArrayList<>();
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setAssociatedProduct(new Product());
        productArrangement.getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        productArrangement.getAssociatedProduct().getInstructionDetails().setParentInstructionMnemonic("ISA");
        productArrangements.add(productArrangement);


        DepositArrangement depositArrangement = new DepositArrangement();
        depositArrangement.setAssociatedProduct(new Product());
        ISABalance isaBalance = new ISABalance();
        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setAmount(new BigDecimal(-1));
        isaBalance.setMaximumLimitAmount(currencyAmount);
        depositArrangement.setISABalance(isaBalance);
        depositArrangement.getISABalance().getMaximumLimitAmount().getAmount();
        productArrangement.setAssociatedProduct(new Product());

        depositArrangement.getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        depositArrangement.getAssociatedProduct().getInstructionDetails().setParentInstructionMnemonic("ISA");

        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        when(rule.switchClient.getBrandedSwitchValue((any(String.class)), any(String.class), any(boolean.class))).thenReturn(false);
        productArrangements.add(depositArrangement);
        EligibilityDecision evaluate = rule.evaluate("G_CREDCARD", ruleDataHolder);
        assertTrue(evaluate.isEligible());

    }

    @Test
    public void testEvaluateReturnsDeclineReason() throws EligibilityException {
        List<ProductArrangement> productArrangements = new ArrayList<>();
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setAssociatedProduct(new Product());
        productArrangement.getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        productArrangement.getAssociatedProduct().getInstructionDetails().setParentInstructionMnemonic("ISA");
        productArrangements.add(productArrangement);


        DepositArrangement depositArrangement = new DepositArrangement();
        depositArrangement.setAssociatedProduct(new Product());
        ISABalance isaBalance = new ISABalance();
        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setAmount(new BigDecimal(14));
        isaBalance.setMaximumLimitAmount(currencyAmount);
        depositArrangement.setISABalance(isaBalance);
        depositArrangement.getISABalance().getMaximumLimitAmount().getAmount();
        productArrangement.setAssociatedProduct(new Product());

        depositArrangement.getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        depositArrangement.getAssociatedProduct().getInstructionDetails().setParentInstructionMnemonic("ISA");
        productArrangements.add(depositArrangement);
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        List<String> candidateInstructions = new ArrayList<>();
        candidateInstructions.add("G_ISA");
        ruleDataHolder.setCandidateInstructions(candidateInstructions);
        when(rule.switchClient.getBrandedSwitchValue((any(String.class)), any(String.class), any(boolean.class))).thenReturn(true);
        EligibilityDecision evaluate = rule.evaluate("G_CREDCARD", ruleDataHolder);
        assertTrue((evaluate.isEligible()));
        assertEquals("Funds deposited within the same tax year.", evaluate.getReasonText());
    }

    @Test
    public void testEvaluateReturnsWhenMultiCashSwitchIsOff() throws EligibilityException {
        List<ProductArrangement> productArrangements = new ArrayList<>();
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setAssociatedProduct(new Product());
        productArrangement.getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        productArrangement.getAssociatedProduct().getInstructionDetails().setParentInstructionMnemonic("ISA");
        productArrangements.add(productArrangement);


        DepositArrangement depositArrangement = new DepositArrangement();
        depositArrangement.setAssociatedProduct(new Product());
        ISABalance isaBalance = new ISABalance();
        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setAmount(new BigDecimal(14));
        isaBalance.setMaximumLimitAmount(currencyAmount);
        depositArrangement.setISABalance(isaBalance);
        depositArrangement.getISABalance().getMaximumLimitAmount().getAmount();
        productArrangement.setAssociatedProduct(new Product());

        depositArrangement.getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        depositArrangement.getAssociatedProduct().getInstructionDetails().setParentInstructionMnemonic("ISA");
        productArrangements.add(depositArrangement);
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        List<String> candidateInstructions = new ArrayList<>();
        candidateInstructions.add("G_ISA");
        ruleDataHolder.setCandidateInstructions(candidateInstructions);
        when(rule.switchClient.getBrandedSwitchValue((any(String.class)), any(String.class), any(boolean.class))).thenReturn(true);
        EligibilityDecision evaluate = rule.evaluate("G_CREDCARD", ruleDataHolder);
        assertTrue(evaluate.isEligible());
    }

    @Test
    public void testEvaluateReturnsTrueForNonISACandidateInstruction() throws EligibilityException {
        List<String> candidateInstructions = new ArrayList<>();
        candidateInstructions.add("P_STUDENT");
        ruleDataHolder.setCandidateInstructions(candidateInstructions);
        EligibilityDecision evaluate = rule.evaluate("P_STUDENT", ruleDataHolder);

        verify(rule.switchClient, never()).getBrandedSwitchValue((any(String.class)), any(String.class), any(boolean.class));
        assertTrue(evaluate.isEligible());
        assertNull(evaluate.getReasonText());
    }
}

