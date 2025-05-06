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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class QuestionHasClubAccountTest {

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
    public void testHasClubAccountReturnsTrue() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        Product product = mock(Product.class);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);
        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        when(instructionDetails.getInstructionMnemonic()).thenReturn("P_PREM_CLB");
        when(productArrangement.getAssociatedProduct()).thenReturn(product);

        boolean ask = QuestionHasClubAccount.pose().givenAProductList(productArrangementFacadeList).ask();

        assertTrue(ask);
        verify(product, times(2)).getInstructionDetails();
        verify(instructionDetails, times(1)).getInstructionMnemonic();
        verify(productArrangement, times(3)).getAssociatedProduct();
    }

    @Test
    public void testHasClubAccountReturnsFalse() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        Product product = mock(Product.class);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);
        when(instructionDetails.getInstructionMnemonic()).thenReturn("G_ONL_FRTD");
        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        when(productArrangement.getAssociatedProduct()).thenReturn(product);

        boolean ask = QuestionHasClubAccount.pose().givenAProductList(productArrangementFacadeList).ask();

        assertFalse(ask);
        verify(product, times(2)).getInstructionDetails();
        verify(instructionDetails, times(1)).getInstructionMnemonic();
        verify(productArrangement, times(3)).getAssociatedProduct();
    }

    @Test
    public void askShouldReturnFalseWhenArrangementListIsEmpty() throws Exception {
        productArrangementFacadeList = new ArrayList();
        boolean ask = QuestionHasClubAccount.pose().givenAProductList(productArrangementFacadeList).ask();

        assertFalse(ask);
        verify(productArrangement, times(0)).getAssociatedProduct();

    }

    @Test
    public void testHasClubAccountReturnsFalseWhenInstructionDetailsIsNull() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        Product product = mock(Product.class);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);
        when(instructionDetails.getInstructionMnemonic()).thenReturn("G_ONL_FRTD");
        when(product.getInstructionDetails()).thenReturn(null);
        when(productArrangement.getAssociatedProduct()).thenReturn(product);

        boolean ask = QuestionHasClubAccount.pose().givenAProductList(productArrangementFacadeList).ask();

        assertFalse(ask);
        verify(product, times(1)).getInstructionDetails();
        verify(productArrangement, times(2)).getAssociatedProduct();
    }
}