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
public class QuestionIsCustomerHoldingVantageProductTest {
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
    public void testHasVantageProductTrue() throws EligibilityException {

        Product product = mock(Product.class);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);
        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        when(instructionDetails.getInstructionMnemonic()).thenReturn("P_vanatage");
        when(instructionDetails.getParentInstructionMnemonic()).thenReturn("G_VANTAGE");
        when(productArrangement.getAssociatedProduct()).thenReturn(product);

        boolean ask = QuestionIsCustomerHoldingVantageProduct.pose().givenAProductList(productArrangementFacadeList).givenAValue("2").ask();

        assertTrue(ask);
        verify(instructionDetails, times(2)).getParentInstructionMnemonic();
        verify(productArrangement, times(4)).getAssociatedProduct();

    }

    @Test
    public void testHasVantageProductGreaterThanThresholdTrue() throws EligibilityException {

        Product product = mock(Product.class);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);
        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        when(instructionDetails.getInstructionMnemonic()).thenReturn("P_vanatage");
        when(instructionDetails.getParentInstructionMnemonic()).thenReturn("G_VANTAGE");
        when(productArrangement.getAssociatedProduct()).thenReturn(product);

        boolean ask = QuestionIsCustomerHoldingVantageProduct.pose().givenAProductList(productArrangementFacadeList).givenAValue("0").ask();

        assertFalse(ask);
        verify(instructionDetails, times(2)).getParentInstructionMnemonic();
        verify(productArrangement, times(4)).getAssociatedProduct();

    }


    @Test
    public void testHasNOProductHoldingTrue() throws EligibilityException {

        Product product = mock(Product.class);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);
        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        when(instructionDetails.getInstructionMnemonic()).thenReturn("P_vanatage");
        when(instructionDetails.getParentInstructionMnemonic()).thenReturn("G_VANTAGE");
        when(productArrangement.getAssociatedProduct()).thenReturn(product);
        productArrangementFacadeList.clear();
        boolean ask = QuestionIsCustomerHoldingVantageProduct.pose().givenAProductList(productArrangementFacadeList).givenAValue("0").ask();

        assertTrue(ask);
        verify(instructionDetails, times(0)).getParentInstructionMnemonic();
        verify(productArrangement, times(0)).getAssociatedProduct();

    }

}