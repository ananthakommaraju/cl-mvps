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
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class QuestionHasFixedTermDepositsMoreThanThresholdTest {
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
    public void testHasFixedTermDepositsMoreThanThresholdReturnsFalse() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        Product product = mock(Product.class);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);
        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        when(instructionDetails.getInstructionMnemonic()).thenReturn("G_ONL_FRTD");
        when(instructionDetails.getParentInstructionMnemonic()).thenReturn("G_ONL_FRTD");
        when(productArrangement.getAssociatedProduct()).thenReturn(product);

        productArrangementFacadeList.add(productArrangementFacade);
        boolean ask = QuestionHasFixedTermDepositsMoreThanThreshold.pose().givenAProductList(productArrangementFacadeList).givenAValue("1").givenAnInstruction("G_ONL_FRTD").ask();
        assertFalse(ask);
    }

    @Test
    public void testHasFixedTermDepositsMoreThanThresholdReturnsTrue() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        Product product = mock(Product.class);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);
        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        when(instructionDetails.getInstructionMnemonic()).thenReturn("G_ONL_FRTD");
        when(instructionDetails.getParentInstructionMnemonic()).thenReturn("G_ONL_FRTD");
        when(productArrangement.getAssociatedProduct()).thenReturn(product);

        productArrangementFacadeList.add(productArrangementFacade);
        boolean ask = QuestionHasFixedTermDepositsMoreThanThreshold.pose().givenAProductList(productArrangementFacadeList).givenAValue("4").givenAnInstruction("G_ONL_FRTD").ask();
        assertTrue(ask);
    }

}