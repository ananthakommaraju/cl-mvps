package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
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
public class QuestionHasExceededThresholdForMnemonicsGroupTest {
    private ProductArrangement productArrangement;

    private List<ProductArrangementFacade> productArrangementFacadeList;

    private String thresholdMnemonic = "P_CLSCVTG:P_SLVRVTG:P_GOLDVTG:P_PLATVTG";

    @Before
    public void setUp() throws Exception {
        productArrangement = mock(ProductArrangement.class);
        productArrangementFacadeList = new ArrayList();

    }

    @Test
    public void returnsFalseWhenThereIsNoProductArrangement() throws Exception {
        productArrangementFacadeList = new ArrayList();
        boolean ask = QuestionHasExceededThresholdForMnemonicsGroup.pose().givenAProductList(productArrangementFacadeList).ask();
        assertFalse(ask);
    }

    @Test
    public void returnsTrueWhenProductCountIsGreaterThanThresholdSet() throws Exception {
        Product product = mock(Product.class);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);

        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        when(instructionDetails.getInstructionMnemonic()).thenReturn("P_CLSCVTG");

        ProductArrangementFacade productArrangementFacade = new ProductArrangementFacade(productArrangement);
        when(productArrangement.getAssociatedProduct()).thenReturn(product);
        productArrangementFacadeList.add(productArrangementFacade);
        productArrangementFacadeList.add(productArrangementFacade);
        productArrangementFacadeList.add(productArrangementFacade);
        productArrangementFacadeList.add(productArrangementFacade);
        productArrangementFacadeList.add(productArrangementFacade);

        boolean ask = QuestionHasExceededThresholdForMnemonicsGroup.pose()
                .givenAGroupOfProductMnemonic(thresholdMnemonic)
                .givenThresholdCount("3")
                .givenAProductList(productArrangementFacadeList)
                .ask();
        assertTrue(ask);
        verify(product, times(6)).getInstructionDetails();
        verify(instructionDetails, times(3)).getInstructionMnemonic();
        verify(productArrangement, times(9)).getAssociatedProduct();

    }

    @Test
    public void returnsTrueWhenProductCountIsGreaterThanThresholdSetForOnlyOneMnemonic() throws Exception {
        Product product = mock(Product.class);
        thresholdMnemonic = "P_CLSCVTG";
        InstructionDetails instructionDetails = mock(InstructionDetails.class);

        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        when(instructionDetails.getInstructionMnemonic()).thenReturn("P_CLSCVTG");

        ProductArrangementFacade productArrangementFacade = new ProductArrangementFacade(productArrangement);
        when(productArrangement.getAssociatedProduct()).thenReturn(product);
        productArrangementFacadeList.add(productArrangementFacade);
        productArrangementFacadeList.add(productArrangementFacade);
        productArrangementFacadeList.add(productArrangementFacade);
        productArrangementFacadeList.add(productArrangementFacade);
        productArrangementFacadeList.add(productArrangementFacade);

        boolean ask = QuestionHasExceededThresholdForMnemonicsGroup.pose()
                .givenAGroupOfProductMnemonic(thresholdMnemonic)
                .givenThresholdCount("3")
                .givenAProductList(productArrangementFacadeList)
                .ask();
        assertTrue(ask);
        verify(product, times(6)).getInstructionDetails();
        verify(instructionDetails, times(3)).getInstructionMnemonic();
        verify(productArrangement, times(9)).getAssociatedProduct();

    }

    @Test
    public void returnsFalseWhenProductCountIsLessThanThresholdSet() throws Exception {
        Product product = mock(Product.class);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);

        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        when(instructionDetails.getInstructionMnemonic()).thenReturn("P_CLSCVTG");

        ProductArrangementFacade productArrangementFacade = new ProductArrangementFacade(productArrangement);
        when(productArrangement.getAssociatedProduct()).thenReturn(product);
        productArrangementFacadeList.add(productArrangementFacade);
        productArrangementFacadeList.add(productArrangementFacade);

        boolean ask = QuestionHasExceededThresholdForMnemonicsGroup.pose()
                .givenAGroupOfProductMnemonic(thresholdMnemonic)
                .givenThresholdCount("3")
                .givenAProductList(productArrangementFacadeList)
                .ask();
        assertFalse(ask);
        verify(product, times(4)).getInstructionDetails();
        verify(instructionDetails, times(2)).getInstructionMnemonic();
        verify(productArrangement, times(6)).getAssociatedProduct();

    }

}