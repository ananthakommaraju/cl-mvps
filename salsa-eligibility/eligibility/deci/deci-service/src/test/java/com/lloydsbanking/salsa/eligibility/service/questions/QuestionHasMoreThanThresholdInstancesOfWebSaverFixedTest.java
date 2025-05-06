package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lb_gbo_sales.ProductArrangement;
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
public class QuestionHasMoreThanThresholdInstancesOfWebSaverFixedTest {

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
    public void shouldReturnTrueWhenThresholdsDoNotExceeds() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        String threshold = "2";
        when(productArrangement.getParentInstructionMnemonic()).thenReturn("G_ONL_FRTD");
        productArrangementFacadeList.add(productArrangementFacade);

        boolean ask = QuestionHasMoreThanThresholdInstancesOfWebSaverFixed.pose().givenAProductList(productArrangementFacadeList).givenAValue(threshold).ask();

        assertTrue(ask);
        verify(productArrangement, times(1)).getParentInstructionMnemonic();

    }


    @Test
    public void shouldReturnFalseWhenTheNumberOfWebSaverIsEqualsToThreshold() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        String threshold = "2";
        when(productArrangement.getParentInstructionMnemonic()).thenReturn("G_ONL_FRTD");
        productArrangementFacadeList.add(productArrangementFacade);
        productArrangementFacadeList.add(productArrangementFacade);

        boolean ask = QuestionHasMoreThanThresholdInstancesOfWebSaverFixed.pose().givenAProductList(productArrangementFacadeList).givenAValue(threshold).ask();

        assertFalse(ask);
        verify(productArrangement, times(2)).getParentInstructionMnemonic();

    }

    @Test
    public void shouldReturnFalseWhenTheNumberOfWebSaverExceedsThreshold() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        String threshold = "2";
        when(productArrangement.getParentInstructionMnemonic()).thenReturn("G_ONL_FRTD");
        productArrangementFacadeList.add(productArrangementFacade);
        productArrangementFacadeList.add(productArrangementFacade);
        productArrangementFacadeList.add(productArrangementFacade);

        boolean ask = QuestionHasMoreThanThresholdInstancesOfWebSaverFixed.pose().givenAProductList(productArrangementFacadeList).givenAValue(threshold).ask();

        assertFalse(ask);
        verify(productArrangement, times(3)).getParentInstructionMnemonic();

    }
}