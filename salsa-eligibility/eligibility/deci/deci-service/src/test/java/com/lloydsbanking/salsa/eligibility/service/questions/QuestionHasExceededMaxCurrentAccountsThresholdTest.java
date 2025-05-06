package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
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
public class QuestionHasExceededMaxCurrentAccountsThresholdTest {

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
    public void testHasExceededMaxCurrentAccountsCountReturnsTrue() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        ExtSysProdIdentifier extSysProdIdentifier = mock(ExtSysProdIdentifier.class);
        Product product = mock(Product.class);
        ArrayList<ExtSysProdIdentifier> extSysProdIdentifiers = new ArrayList<ExtSysProdIdentifier>();

        when(product.getProductType()).thenReturn("1");
        when(productArrangement.getAssociatedProduct()).thenReturn(product);
        when(product.getExternalSystemProductIdentifier()).thenReturn(extSysProdIdentifiers);
        when(extSysProdIdentifier.getSystemCode()).thenReturn("00004");
        extSysProdIdentifiers.add(extSysProdIdentifier);
        boolean ask = QuestionHasExceededMaxCurrentAccountsThreshold.pose().givenAProductList(productArrangementFacadeList).givenAValue("3").ask();
        assertFalse(ask);
        verify(productArrangement, times(2)).getAssociatedProduct();
        verify(product, times(3)).getExternalSystemProductIdentifier();
        verify(extSysProdIdentifier, times(1)).getSystemCode();
    }

    @Test
    public void testHasExceededMaxCurrentAccountsCountReturnsFalse() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        ExtSysProdIdentifier extSysProdIdentifier = mock(ExtSysProdIdentifier.class);
        Product product = mock(Product.class);
        ArrayList<ExtSysProdIdentifier> extSysProdIdentifiers = new ArrayList<ExtSysProdIdentifier>();

        when(product.getProductType()).thenReturn("1");
        when(productArrangement.getAssociatedProduct()).thenReturn(product);
        when(product.getExternalSystemProductIdentifier()).thenReturn(extSysProdIdentifiers);
        when(extSysProdIdentifier.getSystemCode()).thenReturn("00004");
        extSysProdIdentifiers.add(extSysProdIdentifier);
        productArrangementFacadeList.add(productArrangementFacade);
        productArrangementFacadeList.add(productArrangementFacade);
        productArrangementFacadeList.add(productArrangementFacade);
        boolean ask = QuestionHasExceededMaxCurrentAccountsThreshold.pose().givenAProductList(productArrangementFacadeList).givenAValue("2").ask();

        assertTrue(ask);
        verify(productArrangement, times(4)).getAssociatedProduct();
        verify(product, times(6)).getExternalSystemProductIdentifier();
        verify(extSysProdIdentifier, times(2)).getSystemCode();
    }


    @Test(expected = EligibilityException.class)
    public void testAskForExternalSystemProductIdentifierListEmpty() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        ExtSysProdIdentifier extSysProdIdentifier = mock(ExtSysProdIdentifier.class);
        Product product = mock(Product.class);
        ArrayList<ExtSysProdIdentifier> extSysProdIdentifiers = new ArrayList<ExtSysProdIdentifier>();

        when(product.getProductType()).thenReturn("1");
        when(productArrangement.getAssociatedProduct()).thenReturn(product);
        when(product.getExternalSystemProductIdentifier()).thenReturn(new ArrayList<ExtSysProdIdentifier>());
        productArrangementFacadeList.add(productArrangementFacade);
        productArrangementFacadeList.add(productArrangementFacade);
        productArrangementFacadeList.add(productArrangementFacade);
        QuestionHasExceededMaxCurrentAccountsThreshold.pose().givenAProductList(productArrangementFacadeList).givenAValue("2").ask();
    }

}