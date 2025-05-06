package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.Mnemonics;
import lib_sim_bo.businessobjects.CurrencyAmount;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.ISABalance;
import lib_sim_bo.businessobjects.InstructionDetails;
import lib_sim_bo.businessobjects.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class QuestionsHasDepositedAnyFundsThisTaxYearTest {
    private DepositArrangement depositArrangement;

    private List<ProductArrangementFacade> productArrangementFacadeList;

    @Before
    public void setUp() throws Exception {
        productArrangementFacadeList = new ArrayList();

    }

    @Test
    public void testHasDepositedAnyFundsThisTaxYearWithAnyISAProductAmountIsGreaterThanZero() throws EligibilityException {
        depositArrangement = mock(DepositArrangement.class);
        depositArrangement = mock(DepositArrangement.class);
        Product product = mock(Product.class);
        ISABalance isaBalance = mock(ISABalance.class);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);
        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        when(instructionDetails.getParentInstructionMnemonic()).thenReturn(Mnemonics.ISA);
        when(isaBalance.getMaximumLimitAmount()).thenReturn(getCurrencyAmount(10));
        when(depositArrangement.getISABalance()).thenReturn(isaBalance);
        when(depositArrangement.getAssociatedProduct()).thenReturn(product);
        ProductArrangementFacade productArrangementFacade = new ProductArrangementFacade(depositArrangement);
        productArrangementFacadeList.add(productArrangementFacade);

        boolean ask = QuestionsHasDepositedAnyFundsThisTaxYear.pose().givenAProductList(productArrangementFacadeList).ask();
        assertFalse(ask);
        verify(depositArrangement, times(6)).getAssociatedProduct();
        verify(product, times(4)).getInstructionDetails();
        verify(instructionDetails, times(2)).getParentInstructionMnemonic();
        verify(depositArrangement, times(4)).getISABalance();
        verify(isaBalance, times(3)).getMaximumLimitAmount();
    }

    private CurrencyAmount getCurrencyAmount(int value) {
        BigDecimal bigDecimal = new BigDecimal(value);
        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setAmount(bigDecimal);
        return currencyAmount;
    }

    @Test
    public void testHasDepositedAnyFundsThisTaxYearWithISAProductandAmountIsLessThanZero() throws EligibilityException {
        depositArrangement = mock(DepositArrangement.class);
        Product product = mock(Product.class);
        ISABalance isaBalance = mock(ISABalance.class);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);
        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        when(instructionDetails.getParentInstructionMnemonic()).thenReturn(Mnemonics.ISA);
        when(isaBalance.getMaximumLimitAmount()).thenReturn(getCurrencyAmount(-1));
        when(depositArrangement.getAssociatedProduct()).thenReturn(product);
        when(depositArrangement.getISABalance()).thenReturn(isaBalance);
        ProductArrangementFacade productArrangementFacade = new ProductArrangementFacade(depositArrangement);
        productArrangementFacadeList.add(productArrangementFacade);

        boolean ask = QuestionsHasDepositedAnyFundsThisTaxYear.pose().givenAProductList(productArrangementFacadeList).ask();
        assertTrue(ask);
        verify(depositArrangement, times(6)).getAssociatedProduct();
        verify(product, times(4)).getInstructionDetails();
        verify(instructionDetails, times(2)).getParentInstructionMnemonic();
        verify(depositArrangement, times(4)).getISABalance();
        verify(isaBalance, times(3)).getMaximumLimitAmount();
    }

    @Test
    public void testHasDepositedAnyFundsThisTaxYearWithNoISAProductandAmountIsLessThanZero() throws EligibilityException {
        depositArrangement = mock(DepositArrangement.class);
        Product product = mock(Product.class);
        ISABalance isaBalance = mock(ISABalance.class);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);
        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        when(instructionDetails.getParentInstructionMnemonic()).thenReturn(Mnemonics.GROUP_ISA);
        when(isaBalance.getMaximumLimitAmount()).thenReturn(getCurrencyAmount(-1));
        when(depositArrangement.getAssociatedProduct()).thenReturn(product);
        when(depositArrangement.getISABalance()).thenReturn(isaBalance);
        ProductArrangementFacade productArrangementFacade = new ProductArrangementFacade(depositArrangement);
        productArrangementFacadeList.add(productArrangementFacade);

        boolean ask = QuestionsHasDepositedAnyFundsThisTaxYear.pose().givenAProductList(productArrangementFacadeList).ask();
        assertTrue(ask);
        verify(depositArrangement, times(6)).getAssociatedProduct();
        verify(product, times(4)).getInstructionDetails();
        verify(instructionDetails, times(2)).getParentInstructionMnemonic();
        verify(depositArrangement, times(4)).getISABalance();
        verify(isaBalance, times(3)).getMaximumLimitAmount();
    }

    @Test
    public void testHasDepositedAnyFundsThisTaxYearWithNoISAProductandAmountIsGreaterThanZero() throws EligibilityException {
        depositArrangement = mock(DepositArrangement.class);
        Product product = mock(Product.class);
        ISABalance isaBalance = mock(ISABalance.class);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);
        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        when(instructionDetails.getParentInstructionMnemonic()).thenReturn(Mnemonics.GROUP_ISA);
        when(isaBalance.getMaximumLimitAmount()).thenReturn(getCurrencyAmount(14));
        when(depositArrangement.getAssociatedProduct()).thenReturn(product);
        when(depositArrangement.getISABalance()).thenReturn(isaBalance);
        ProductArrangementFacade productArrangementFacade = new ProductArrangementFacade(depositArrangement);
        productArrangementFacadeList.add(productArrangementFacade);

        boolean ask = QuestionsHasDepositedAnyFundsThisTaxYear.pose().givenAProductList(productArrangementFacadeList).ask();
        assertFalse(ask);
        verify(depositArrangement, times(6)).getAssociatedProduct();
        verify(product, times(4)).getInstructionDetails();
        verify(instructionDetails, times(2)).getParentInstructionMnemonic();
        verify(depositArrangement, times(4)).getISABalance();
        verify(isaBalance, times(3)).getMaximumLimitAmount();

    }

}