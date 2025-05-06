package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
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
public class QuestionHasAVAAccountTest {

    private List<ProductArrangementFacade> productArrangementFacadeList;

    @Before
    public void setUp() throws Exception {
        productArrangementFacadeList = new ArrayList();

    }

    @Test
    public void testHasAVAAccountReturnsTrue() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        ProductArrangement productArrangement = mock(ProductArrangement.class);
        Product product = mock(Product.class);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);
        when(productArrangement.getAssociatedProduct()).thenReturn(product);
        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        when(instructionDetails.getInstructionMnemonic()).thenReturn("P_PLAT");

        ProductArrangementFacade productArrangementFacade = new ProductArrangementFacade(productArrangement);
        productArrangementFacadeList.add(productArrangementFacade);

        boolean ask = QuestionHasAVAAccount.pose().givenAProductList(productArrangementFacadeList).ask();

        verify(productArrangement, times(3)).getAssociatedProduct();
        verify(product, times(2)).getInstructionDetails();
        verify(instructionDetails, times(2)).getInstructionMnemonic();
        assertTrue(ask);
    }

    @Test
    public void testHasAVAAccountReturnsFalse() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        ProductArrangement productArrangement = mock(ProductArrangement.class);
        Product product = mock(Product.class);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);
        when(productArrangement.getAssociatedProduct()).thenReturn(product);
        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        when(instructionDetails.getInstructionMnemonic()).thenReturn("G_ONL_FRTD");

        ProductArrangementFacade productArrangementFacade = new ProductArrangementFacade(productArrangement);
        productArrangementFacadeList.add(productArrangementFacade);


        boolean ask = QuestionHasAVAAccount.pose().givenAProductList(productArrangementFacadeList).ask();

        verify(productArrangement, times(3)).getAssociatedProduct();
        verify(product, times(2)).getInstructionDetails();
        verify(instructionDetails, times(2)).getInstructionMnemonic();

        assertFalse(ask);

    }

    @Test
    public void testHasExceededMaxCurrentAccountsCountReturnsTrue() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        ProductArrangement productArrangement = mock(ProductArrangement.class);
        Product product = mock(Product.class);
        when(productArrangement.getAssociatedProduct()).thenReturn(product);
        when(product.getProductType()).thenReturn("1");
        ArrayList<ExtSysProdIdentifier> extSysProdIdentifiers = new ArrayList();
        ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
        extSysProdIdentifier.setSystemCode("0004");
        extSysProdIdentifiers.add(extSysProdIdentifier);
        when(product.getExternalSystemProductIdentifier()).thenReturn(extSysProdIdentifiers);

        ProductArrangementFacade productArrangementFacade = new ProductArrangementFacade(productArrangement);
        productArrangementFacadeList.add(productArrangementFacade);


        boolean ask = QuestionHasAVAAccount.pose().givenAProductList(productArrangementFacadeList).ask();

        verify(productArrangement, times(2)).getAssociatedProduct();
        verify(product, times(1)).getInstructionDetails();

        assertFalse(ask);

    }

}