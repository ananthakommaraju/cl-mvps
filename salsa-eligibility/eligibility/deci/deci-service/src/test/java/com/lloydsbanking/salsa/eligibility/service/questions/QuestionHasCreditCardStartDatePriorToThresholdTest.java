package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.date.DateFactory;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class QuestionHasCreditCardStartDatePriorToThresholdTest {
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
    public void testHasCreditCardStartDatePriorToThresholdDateReturnsTrue() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        DateFactory dateFactory = new DateFactory();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        when(productArrangement.getArrangementStartDate()).thenReturn(dateFactory.stringToXMLGregorianCalendar("2015-10-12", sdf));
        Product product = mock(Product.class);
        when(productArrangement.getAssociatedProduct()).thenReturn(product);
        when(product.getProductType()).thenReturn("3");

        boolean ask = QuestionHasCreditCardStartDatePriorToThreshold.pose().givenAProductList(productArrangementFacadeList).givenAValue("500").ask();

        assertTrue(ask);
        verify(product, times(2)).getProductType();
        verify(productArrangement, times(1)).getAssociatedProduct();
        verify(productArrangement, times(1)).getArrangementStartDate();
    }

    @Test
    public void testHasCreditCardStartDatePriorToThresholdReturnsFalse() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        DateFactory dateFactory = new DateFactory();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        when(productArrangement.getArrangementStartDate()).thenReturn(dateFactory.stringToXMLGregorianCalendar("2017-10-12", sdf));
        Product product = mock(Product.class);
        when(productArrangement.getAssociatedProduct()).thenReturn(product);
        when(product.getProductType()).thenReturn("3");

        boolean ask = QuestionHasCreditCardStartDatePriorToThreshold.pose().givenAProductList(productArrangementFacadeList).givenAValue("150").ask();

        assertFalse(ask);
        verify(product, times(2)).getProductType();
        verify(productArrangement, times(1)).getAssociatedProduct();
        verify(productArrangement, times(1)).getArrangementStartDate();
    }

}