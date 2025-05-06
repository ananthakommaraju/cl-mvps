package com.lloydsbanking.salsa.opaloans;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class ProductOptionsCodeTest {
    @Test
    public void testGetProductOptions() {
        assertEquals(ProductOptionsCode.CURRENCY_CODE, ProductOptionsCode.getProductOptionsCode(ProductOptionsCode.CURRENCY_CODE.getValue()));
        assertEquals(ProductOptionsCode.ADMIN_CHARGES, ProductOptionsCode.getProductOptionsCode(ProductOptionsCode.ADMIN_CHARGES.getValue()));
        assertEquals(ProductOptionsCode.DAYS_INTEREST_CHARGED, ProductOptionsCode.getProductOptionsCode(ProductOptionsCode.DAYS_INTEREST_CHARGED.getValue()));
        assertEquals(ProductOptionsCode.INSURANCE_AVAILABLE_INDICATOR, ProductOptionsCode.getProductOptionsCode(ProductOptionsCode.INSURANCE_AVAILABLE_INDICATOR.getValue()));
        assertEquals(ProductOptionsCode.LETTER_CHARGES, ProductOptionsCode.getProductOptionsCode(ProductOptionsCode.LETTER_CHARGES.getValue()));
        assertEquals(ProductOptionsCode.LOAN_TERM_EXEMPTION_END_DATE, ProductOptionsCode.getProductOptionsCode(ProductOptionsCode.LOAN_TERM_EXEMPTION_END_DATE.getValue()));
        assertEquals(ProductOptionsCode.LOAN_TERM_EXEMPTION_START_DATE, ProductOptionsCode.getProductOptionsCode(ProductOptionsCode.LOAN_TERM_EXEMPTION_START_DATE.getValue()));
        assertEquals(ProductOptionsCode.MAXIMUM_CHARGE_AMOUNT, ProductOptionsCode.getProductOptionsCode(ProductOptionsCode.MAXIMUM_CHARGE_AMOUNT.getValue()));
        assertEquals(ProductOptionsCode.MAXIMUM_LOAN_AMOUNT, ProductOptionsCode.getProductOptionsCode(ProductOptionsCode.MAXIMUM_LOAN_AMOUNT.getValue()));
        assertEquals(ProductOptionsCode.MAXIMUM_LOAN_DEFER_TERM, ProductOptionsCode.getProductOptionsCode(ProductOptionsCode.MAXIMUM_LOAN_DEFER_TERM.getValue()));
        assertEquals(ProductOptionsCode.MINIMUM_LOAN_TERM, ProductOptionsCode.getProductOptionsCode(ProductOptionsCode.MINIMUM_LOAN_TERM.getValue()));
        assertEquals(ProductOptionsCode.MAXIMUM_LOAN_TERM, ProductOptionsCode.getProductOptionsCode(ProductOptionsCode.MAXIMUM_LOAN_TERM.getValue()));
        assertEquals(ProductOptionsCode.MINIMUM_LOAN_DEFER_TERM, ProductOptionsCode.getProductOptionsCode(ProductOptionsCode.MINIMUM_LOAN_DEFER_TERM.getValue()));
        assertEquals(ProductOptionsCode.MINIMUM_LOAN_TERM_REPAYMENT_HOLIDAYS, ProductOptionsCode.getProductOptionsCode(ProductOptionsCode.MINIMUM_LOAN_TERM_REPAYMENT_HOLIDAYS.getValue()));
        assertEquals(ProductOptionsCode.MAXIMUM_LOAN_TERM_REPAYMENT_HOLIDAYS, ProductOptionsCode.getProductOptionsCode(ProductOptionsCode.MAXIMUM_LOAN_TERM_REPAYMENT_HOLIDAYS.getValue()));
        assertEquals(ProductOptionsCode.URL_TEXT_DISPLAY, ProductOptionsCode.getProductOptionsCode(ProductOptionsCode.URL_TEXT_DISPLAY.getValue()));
        assertEquals(ProductOptionsCode.URL, ProductOptionsCode.getProductOptionsCode(ProductOptionsCode.URL.getValue()));
    }

}
