package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
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
public class QuestionHasMoreThanThresholdInstancesOfCreditCardTest {

    private ProductArrangement productArrangement;

    private List<ProductArrangementFacade> productArrangementFacadeList;

    private ProductArrangementFacade productArrangementFacade;

    @Before
    public void setUp() throws Exception {
        productArrangementFacadeList = new ArrayList();
        productArrangement = mock(ProductArrangement.class);
        productArrangementFacade = new ProductArrangementFacade(productArrangement);

    }

    @Test
    public void testHasMoreThanThresholdInstancesOfCreditCard() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        String threshold = "2";
        Product product = mock(Product.class);
        when(product.getProductType()).thenReturn("3");
        when(productArrangement.getAssociatedProduct()).thenReturn(product);
        productArrangementFacadeList.add(productArrangementFacade);
        productArrangementFacadeList.add(productArrangementFacade);
        productArrangementFacadeList.add(productArrangementFacade);

        boolean ask = QuestionHasMoreThanThresholdInstancesOfCreditCard.pose().givenAProductList(productArrangementFacadeList).givenAValue(threshold).ask();

        assertTrue(ask);
        verify(product, times(6)).getProductType();
        verify(productArrangement, times(3)).getAssociatedProduct();

    }

    @Test
    public void testHasEqualsToThresholdInstancesOfCreditCard() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        String threshold = "2";
        Product product = mock(Product.class);
        when(product.getProductType()).thenReturn("3");
        when(productArrangement.getAssociatedProduct()).thenReturn(product);
        productArrangementFacadeList.add(productArrangementFacade);
        productArrangementFacadeList.add(productArrangementFacade);

        boolean ask = QuestionHasMoreThanThresholdInstancesOfCreditCard.pose().givenAProductList(productArrangementFacadeList).givenAValue(threshold).ask();

        assertFalse(ask);
        verify(product, times(4)).getProductType();
        verify(productArrangement, times(2)).getAssociatedProduct();
    }

}