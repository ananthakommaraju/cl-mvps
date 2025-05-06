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
public class QuestionHasExceededProductThresholdTest {

    private ProductArrangement productArrangement;

    private List<ProductArrangementFacade> productArrangementFacadeList;

    private ProductArrangementFacade productArrangementFacade;

    @Before
    public void setUp() throws Exception {
        productArrangementFacadeList = new ArrayList();
        productArrangement = mock(ProductArrangement.class);
        productArrangementFacade = new ProductArrangementFacade(productArrangement);
        productArrangementFacadeList.add(productArrangementFacade);

    }

    @Test
    public void testHasExceededProductThresholdReturnsFalse() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        Product product = mock(Product.class);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);
        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        when(instructionDetails.getInstructionMnemonic()).thenReturn("P_CASH");
        when(productArrangement.getAssociatedProduct()).thenReturn(product);
        productArrangementFacadeList.add(productArrangementFacade);

        boolean ask = QuestionHasExceededProductThreshold.pose().givenAProductList(productArrangementFacadeList).givenAValue("1").givenAnInstruction("P_CASH").ask();
        assertFalse(ask);
        verify(product, times(4)).getInstructionDetails();
        verify(instructionDetails, times(2)).getInstructionMnemonic();
        verify(productArrangement, times(6)).getAssociatedProduct();
    }

    @Test
    public void testHasExceededProductThresholdReturnsTrue() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        Product product = mock(Product.class);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);
        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        when(instructionDetails.getInstructionMnemonic()).thenReturn("P_CASH");
        when(productArrangement.getAssociatedProduct()).thenReturn(product);

        boolean ask = QuestionHasExceededProductThreshold.pose().givenAProductList(productArrangementFacadeList).givenAValue("2").givenAnInstruction("P_CASH").ask();
        assertTrue(ask);
        verify(product, times(4)).getInstructionDetails();
        verify(instructionDetails, times(2)).getInstructionMnemonic();
        verify(productArrangement, times(6)).getAssociatedProduct();
    }

}