package com.lloydsbanking.salsa.eligibility.service.questions;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
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
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class QuestionIsISAOpenedThisYearTest {
    private List<ProductArrangementFacade> productArrangementFacadeList;

    private ProductArrangementFacade productArrangementFacade;

    private TestDataHelper testDataHelper;

    @Before
    public void setUp() throws Exception {
        testDataHelper = new TestDataHelper();
        productArrangementFacadeList = new ArrayList();

    }

    @Test
    public void testIsAccountOpenedThisYearTrueWhenMnemonicIsNotNullWZ() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        ProductArrangement productArrangement = mock(ProductArrangement.class);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);
        Product product = mock(Product.class);
        when(instructionDetails.getParentInstructionMnemonic()).thenReturn("G_ISA");
        when(productArrangement.getAssociatedProduct()).thenReturn(product);
        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        productArrangement.getAssociatedProduct().setInstructionDetails(instructionDetails);
        Calendar today = Calendar.getInstance();
        when(productArrangement.getArrangementStartDate()).thenReturn(testDataHelper.createXMLGregorianCalendar(today.get(Calendar.YEAR), 7, 4));

        productArrangementFacade = new ProductArrangementFacade(productArrangement);
        productArrangementFacadeList.add(productArrangementFacade);
        boolean ask = QuestionIsISAOpenedThisYear.pose().givenAProductList(productArrangementFacadeList).ask();
        assertTrue(ask);
        verify(instructionDetails, times(2)).getParentInstructionMnemonic();

    }

    @Test
    public void testIsAccountOpenedThisYearReturnFalseWhenMnemonicIsNullWZ() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        ProductArrangement productArrangement = mock(ProductArrangement.class);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);
        Product product = mock(Product.class);
        when(instructionDetails.getParentInstructionMnemonic()).thenReturn(null);
        when(productArrangement.getAssociatedProduct()).thenReturn(product);
        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        productArrangement.getAssociatedProduct().setInstructionDetails(instructionDetails);

        Calendar today = Calendar.getInstance();
        when(productArrangement.getArrangementStartDate()).thenReturn(testDataHelper.createXMLGregorianCalendar(today.get(Calendar.YEAR - 1), 4, 4));
        productArrangementFacade = new ProductArrangementFacade(productArrangement);
        productArrangementFacadeList.add(productArrangementFacade);

        boolean ask = QuestionIsISAOpenedThisYear.pose().givenAProductList(productArrangementFacadeList).ask();
        assertFalse(ask);
        verify(instructionDetails, times(1)).getParentInstructionMnemonic();

    }

    @Test
    public void isAccountOpenedThisYearShouldReturnTrueBZ() throws Exception {
        lb_gbo_sales.ProductArrangement productArrangement = mock(lb_gbo_sales.ProductArrangement.class);

        when(productArrangement.getParentInstructionMnemonic()).thenReturn("G_ISA");
        Calendar today = Calendar.getInstance();
        when(productArrangement.getStartDate()).thenReturn(testDataHelper.createXMLGregorianCalendar(today.get(Calendar.YEAR), 7, 4));


        productArrangementFacade = new ProductArrangementFacade(productArrangement);
        productArrangementFacadeList.add(productArrangementFacade);


        boolean ask = QuestionIsISAOpenedThisYear.pose().givenAProductList(productArrangementFacadeList).ask();
        assertTrue(ask);
        verify(productArrangement, times(2)).getParentInstructionMnemonic();
        verify(productArrangement).getStartDate();

    }

    @Test
    public void isAccountOpenedThisYearShouldReturnFalse() throws Exception {

        lb_gbo_sales.ProductArrangement productArrangement = mock(lb_gbo_sales.ProductArrangement.class);

        when(productArrangement.getParentInstructionMnemonic()).thenReturn("G_ISA");
        Calendar today = Calendar.getInstance();
        when(productArrangement.getStartDate()).thenReturn(testDataHelper.createXMLGregorianCalendar(today.get(Calendar.YEAR - 1), 4, 4));


        productArrangementFacade = new ProductArrangementFacade(productArrangement);
        productArrangementFacadeList.add(productArrangementFacade);

        boolean ask = QuestionIsISAOpenedThisYear.pose().givenAProductList(productArrangementFacadeList).ask();
        assertFalse(ask);
        verify(productArrangement, times(2)).getParentInstructionMnemonic();
        verify(productArrangement).getStartDate();

    }

}