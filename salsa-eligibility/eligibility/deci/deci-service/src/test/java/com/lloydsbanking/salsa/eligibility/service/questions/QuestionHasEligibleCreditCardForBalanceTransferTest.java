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
public class QuestionHasEligibleCreditCardForBalanceTransferTest {

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
    public void hasEligibleCreditCardForBalanceTransferReturnTrueIfParentInstructionMnemonicMatches() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        when(productArrangement.getParentInstructionMnemonic()).thenReturn("AGT");

        boolean ask = QuestionHasEligibleCreditCardForBalanceTransfer.pose().givenAProductList(productArrangementFacadeList).givenAValue("AGT").ask();

        assertTrue(ask);
        verify(productArrangement, times(1)).getParentInstructionMnemonic();

    }

    @Test
    public void hasEligibleCreditCardForBalanceTransferReturnFalseIfParentInstructionMnemonicDoesNotMatch() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        when(productArrangement.getParentInstructionMnemonic()).thenReturn("AGT");

        boolean ask = QuestionHasEligibleCreditCardForBalanceTransfer.pose().givenAProductList(productArrangementFacadeList).givenAValue("AAA").ask();

        assertFalse(ask);
        verify(productArrangement, times(1)).getParentInstructionMnemonic();

    }

}