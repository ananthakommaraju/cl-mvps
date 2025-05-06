package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.businessobjects.CreditCardFinanceServiceArrangement;
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
public class QuestionHasEligibleProductForPPCTest {
    private CreditCardFinanceServiceArrangement productArrangement;

    private List<ProductArrangementFacade> productArrangementFacadeList;

    private ProductArrangementFacade productArrangementFacade;

    @Before
    public void setUp() throws Exception {
        productArrangementFacadeList = new ArrayList();
        productArrangement = mock(CreditCardFinanceServiceArrangement.class);
        productArrangementFacade = new ProductArrangementFacade(productArrangement);
        productArrangementFacadeList.add(productArrangementFacade);

    }

    @Test
    public void hasEligibleProductForPPCReturnTrueWhenIsHasEmbeddedInsuranceIsSetToFalse() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        when(productArrangement.isHasEmbeddedInsurance()).thenReturn(false);

        boolean ask = QuestionHasEligibleProductForPPC.pose().givenAProductList(productArrangementFacadeList).ask();
        assertTrue(ask);
        verify(productArrangement, times(2)).isHasEmbeddedInsurance();
    }

    @Test
    public void hasEligibleProductForPPCReturnFalseWhenIsHasEmbeddedInsuranceIsSetToTrue() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        when(productArrangement.isHasEmbeddedInsurance()).thenReturn(true);

        boolean ask = QuestionHasEligibleProductForPPC.pose().givenAProductList(productArrangementFacadeList).ask();
        assertFalse(ask);
        verify(productArrangement, times(2)).isHasEmbeddedInsurance();
    }

    @Test
    public void hasEligibleProductForPPCReturnFalseWhenInstanceNotCreditCardFinanceServiceArrangement() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        productArrangementFacadeList = new ArrayList<>();
        ProductArrangement productArrangement1 = new ProductArrangement();
        ProductArrangementFacade productArrangementFacade1 = new ProductArrangementFacade(productArrangement1);
        productArrangementFacadeList.add(productArrangementFacade1);

        boolean ask = QuestionHasEligibleProductForPPC.pose().givenAProductList(productArrangementFacadeList).ask();
        assertFalse(ask);
    }

}