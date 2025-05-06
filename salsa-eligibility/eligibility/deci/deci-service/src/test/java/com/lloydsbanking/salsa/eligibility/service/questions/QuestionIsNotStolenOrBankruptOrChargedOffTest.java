package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lb_gbo_sales.businessobjects.CreditCardFinanceServiceArrangement;
import lb_gbo_sales.businessobjects.CreditCardStatus;
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
public class QuestionIsNotStolenOrBankruptOrChargedOffTest {
    private List<ProductArrangementFacade> productArrangementsList;

    private ProductArrangementFacade productArrangementFacade;


    @Before
    public void before() {
        productArrangementsList = new ArrayList<>();
    }

    @Test
    public void isNotStolenOrBankruptOrChargedOffShouldReturnFalseIfStatusIsZ() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {


        CreditCardFinanceServiceArrangement creditCardFinanceServiceArrangement = mock(CreditCardFinanceServiceArrangement.class);
        when(creditCardFinanceServiceArrangement.getCardStatus()).thenReturn(CreditCardStatus.Z);;
        productArrangementFacade = new ProductArrangementFacade(creditCardFinanceServiceArrangement);
        productArrangementsList.add(productArrangementFacade);
        boolean ask = QuestionIsNotStolenOrBankruptOrChargedOff.pose().givenAProductList(productArrangementsList).ask();

        assertFalse(ask);

    }

    @Test
    public void isNotStolenOrBankruptOrChargedOffShouldReturnFalseIfStatusIsB() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        CreditCardFinanceServiceArrangement creditCardFinanceServiceArrangement = mock(CreditCardFinanceServiceArrangement.class);
        when(creditCardFinanceServiceArrangement.getCardStatus()).thenReturn(CreditCardStatus.B);
        productArrangementFacade = new ProductArrangementFacade(creditCardFinanceServiceArrangement);
        productArrangementsList.add(productArrangementFacade);

        boolean ask = QuestionIsNotStolenOrBankruptOrChargedOff.pose().givenAProductList(productArrangementsList).ask();

        assertFalse(ask);

    }

    @Test
    public void isNotStolenOrBankruptOrChargedOffShouldReturnFalseIfStatusIsU() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        CreditCardFinanceServiceArrangement creditCardFinanceServiceArrangement = mock(CreditCardFinanceServiceArrangement.class);
        when(creditCardFinanceServiceArrangement.getCardStatus()).thenReturn(CreditCardStatus.U);;
        productArrangementFacade = new ProductArrangementFacade(creditCardFinanceServiceArrangement);
        productArrangementsList.add(productArrangementFacade);

        boolean ask = QuestionIsNotStolenOrBankruptOrChargedOff.pose().givenAProductList(productArrangementsList).ask();
        assertFalse(ask);

    }

    @Test
    public void isNotStolenOrBankruptOrChargedOffShouldReturnTrueIfStatusIsNull() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        CreditCardFinanceServiceArrangement creditCardFinanceServiceArrangement = mock(CreditCardFinanceServiceArrangement.class);
        when(creditCardFinanceServiceArrangement.getCardStatus()).thenReturn(null);
        productArrangementFacade = new ProductArrangementFacade(creditCardFinanceServiceArrangement);
        productArrangementsList.add(productArrangementFacade);

        boolean ask = QuestionIsNotStolenOrBankruptOrChargedOff.pose().givenAProductList(productArrangementsList).ask();
        assertTrue(ask);
        


    }

    @Test
    public void isNotStolenOrBankruptOrChargedOffShouldReturnTrueIfStatusIsOtherThanBZU() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        CreditCardFinanceServiceArrangement creditCardFinanceServiceArrangement = mock(CreditCardFinanceServiceArrangement.class);
        when(creditCardFinanceServiceArrangement.getCardStatus()).thenReturn(CreditCardStatus.C);
        productArrangementFacade = new ProductArrangementFacade(creditCardFinanceServiceArrangement);
        productArrangementsList.add(productArrangementFacade);

        boolean ask = QuestionIsNotStolenOrBankruptOrChargedOff.pose().givenAProductList(productArrangementsList).ask();
        assertTrue(ask);
    }

}