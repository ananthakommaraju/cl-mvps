package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lib_sim_bo.businessobjects.InstructionDetails;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class QuestionHasCurrentAccountTest {
    private ProductArrangement productArrangement;

    private List<ProductArrangementFacade> productArrangementFacadeList;

    @Before
    public void setUp() throws Exception {
        productArrangement = mock(ProductArrangement.class);
        ProductArrangementFacade productArrangementFacade = new ProductArrangementFacade(productArrangement);
        productArrangementFacadeList = new ArrayList();
        productArrangementFacadeList.add(productArrangementFacade);

    }

    @Test
    public void testHasCurrentAccountReturnsTrue() throws EligibilityException {
        Product product = mock(Product.class);
        when(productArrangement.getAssociatedProduct()).thenReturn(product);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);
        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        when(instructionDetails.getInstructionMnemonic()).thenReturn("G_ONL_FRTD");
        when(instructionDetails.getParentInstructionMnemonic()).thenReturn("G_PCA");

        boolean ask = QuestionHasCurrentAccount.pose().givenAProductList(productArrangementFacadeList).ask();
        assertTrue(ask);
        verify(productArrangement, times(4)).getAssociatedProduct();
        verify(product, times(3)).getInstructionDetails();
        verify(instructionDetails, times(0)).getInstructionMnemonic();
        verify(instructionDetails, times(2)).getParentInstructionMnemonic();
    }


    @Test
    public void testHasCurrentAccountReturnsFalse() throws EligibilityException {
        Product product = mock(Product.class);
        when(productArrangement.getAssociatedProduct()).thenReturn(product);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);
        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        when(instructionDetails.getInstructionMnemonic()).thenReturn("GG_ONL_FRTD");
        when(instructionDetails.getParentInstructionMnemonic()).thenReturn("G_ONL_FRTD");

        boolean ask = QuestionHasCurrentAccount.pose().givenAProductList(productArrangementFacadeList).ask();
        assertFalse(ask);
        verify(productArrangement, times(4)).getAssociatedProduct();
        verify(product, times(3)).getInstructionDetails();
        verify(instructionDetails, times(0)).getInstructionMnemonic();
        verify(instructionDetails, times(2)).getParentInstructionMnemonic();
    }
}