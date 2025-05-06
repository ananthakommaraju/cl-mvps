package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.Mnemonics;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lb_gbo_sales.ProductArrangement;
import lib_sim_bo.businessobjects.InstructionDetails;
import lib_sim_bo.businessobjects.Product;
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
public class QuestionHasSameProductAlreadyTest {


    private List<ProductArrangementFacade> productArrangementFacadeList;

    private ProductArrangementFacade productArrangementFacade;

    @Before
    public void setUp() throws Exception {
        productArrangementFacadeList = new ArrayList();

    }

    @Test
    public void hasSameProductAlreadyShouldReturnTrueIfInstructionMnemonicIsTheEqualToCandidateInstruction() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        ProductArrangement productArrangement = mock(ProductArrangement.class);
        String candidateInstruction = "ins";

        when(productArrangement.getInstructionMnemonic()).thenReturn("ins");
        productArrangementFacade = new ProductArrangementFacade(productArrangement);
        productArrangementFacadeList.add(productArrangementFacade);

        boolean ask = QuestionHasSameProductAlready.pose().givenAProductList(productArrangementFacadeList).givenAValue(candidateInstruction).ask();

        assertTrue(ask);
        verify(productArrangement, times(2)).getInstructionMnemonic();

    }

    @Test
    public void hasSameProductAlreadyShouldReturnTrueIfInstructionMnemonicIsPCSHISAAndCandidateInstructionIsPCISASAV() throws Exception {
        ProductArrangement productArrangement = mock(ProductArrangement.class);
        String candidateInstruction = "P_CISA_SAV";

        when(productArrangement.getInstructionMnemonic()).thenReturn("P_CSH_ISA");
        productArrangementFacade = new ProductArrangementFacade(productArrangement);
        productArrangementFacadeList.add(productArrangementFacade);

        boolean ask = QuestionHasSameProductAlready.pose().givenAProductList(productArrangementFacadeList).givenAValue(candidateInstruction).ask();

        assertTrue(ask);
        verify(productArrangement, times(2)).getInstructionMnemonic();

    }

    @Test
    public void hasSameProductAlreadyShouldReturnFalseIfInstructionMnemonicAIsPCSHISA() throws Exception {
        ProductArrangement productArrangement = mock(ProductArrangement.class);
        String candidateInstruction = "ins";
        when(productArrangement.getInstructionMnemonic()).thenReturn("P_CSH_ISA");
        productArrangementFacade = new ProductArrangementFacade(productArrangement);
        productArrangementFacadeList.add(productArrangementFacade);

        boolean ask = QuestionHasSameProductAlready.pose().givenAProductList(productArrangementFacadeList).givenAValue(candidateInstruction).ask();
        assertFalse(ask);
        verify(productArrangement, times(2)).getInstructionMnemonic();

    }


    @Test
    public void testHasSameProductAlreadyReturnsTrueForWZ() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        lib_sim_bo.businessobjects.ProductArrangement productArrangement1 = mock(lib_sim_bo.businessobjects.ProductArrangement.class);
        Product product = mock(Product.class);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);

        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        when(instructionDetails.getInstructionMnemonic()).thenReturn("P_CASH");
        when(productArrangement1.getAssociatedProduct()).thenReturn(product);

        productArrangementFacade = new ProductArrangementFacade(productArrangement1);
        productArrangementFacadeList.add(productArrangementFacade);

        boolean ask = QuestionHasSameProductAlready.pose().givenAProductList(productArrangementFacadeList).givenAValue("P_CASH").ask();

        assertTrue(ask);
        verify(product, times(4)).getInstructionDetails();
        verify(instructionDetails, times(2)).getInstructionMnemonic();
        verify(productArrangement1, times(6)).getAssociatedProduct();
    }

    @Test
    public void testHasSameProductAlreadyReturnsFalse() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        lib_sim_bo.businessobjects.ProductArrangement productArrangement1 = mock(lib_sim_bo.businessobjects.ProductArrangement.class);
        Product product = mock(Product.class);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);

        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        when(instructionDetails.getInstructionMnemonic()).thenReturn("P_CASH");
        when(productArrangement1.getAssociatedProduct()).thenReturn(product);

        productArrangementFacade = new ProductArrangementFacade(productArrangement1);
        productArrangementFacadeList.add(productArrangementFacade);

        boolean ask = QuestionHasSameProductAlready.pose().givenAProductList(productArrangementFacadeList).givenAValue("P_AB_1Y_Y").ask();

        assertFalse(ask);
        verify(product, times(4)).getInstructionDetails();
        verify(instructionDetails, times(2)).getInstructionMnemonic();
        verify(productArrangement1, times(6)).getAssociatedProduct();

    }

    @Test
    public void testHasSameProductAlreadyWhenCandidateInstIsCashIsaSaver() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        lib_sim_bo.businessobjects.ProductArrangement productArrangement1 = mock(lib_sim_bo.businessobjects.ProductArrangement.class);
        Product product = mock(Product.class);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);

        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        when(instructionDetails.getInstructionMnemonic()).thenReturn(Mnemonics.CASH_ISA);
        when(productArrangement1.getAssociatedProduct()).thenReturn(product);

        productArrangementFacade = new ProductArrangementFacade(productArrangement1);
        productArrangementFacadeList.add(productArrangementFacade);

        boolean ask = QuestionHasSameProductAlready.pose().givenAProductList(productArrangementFacadeList).givenAValue(Mnemonics.CASH_ISA_SAVER).ask();

        assertTrue(ask);
        verify(product, times(4)).getInstructionDetails();
        verify(instructionDetails, times(2)).getInstructionMnemonic();
        verify(productArrangement1, times(6)).getAssociatedProduct();
    }

    @Test
    public void testHasSameProductAlreadyWhenProductArrangementsIsEmpty() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        boolean ask = QuestionHasSameProductAlready.pose().givenAProductList(productArrangementFacadeList).givenAValue(Mnemonics.CASH_ISA_SAVER).ask();

        assertFalse(ask);
    }

}