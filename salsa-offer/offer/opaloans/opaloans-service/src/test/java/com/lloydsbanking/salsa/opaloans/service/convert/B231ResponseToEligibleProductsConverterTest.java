package com.lloydsbanking.salsa.opaloans.service.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.opaloans.ProductOptionsCode;
import com.lloydsbanking.salsa.opaloans.service.TestDataHelper;
import com.lloydstsb.ib.wsbridge.loan.StB231BLoanPartyProductsGet;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.ProductOptions;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category(UnitTest.class)
public class B231ResponseToEligibleProductsConverterTest {
    B231ResponseToEligibleProductsConverter converter;

    TestDataHelper dataHelper;

    FinanceServiceArrangement productArrangement;

    @Before
    public void setUp() {
        converter = new B231ResponseToEligibleProductsConverter();
        dataHelper = new TestDataHelper();
        productArrangement = dataHelper.createFinanceServiceArrangement("1988-01-22T06:40:56.046Z", "777146", "03182268");
    }

    @Test
    public void testConvertB231ResponseToEligibleProductsForInsuranceAvailable() {
        StB231BLoanPartyProductsGet b231Response = dataHelper.createB231Response(0);
        b231Response.getAstloanproduct().get(0).setInsuranceavail("Y");

        converter.convert(b231Response, productArrangement);

        assertEquals(1, productArrangement.getOfferedProducts().size());
        assertEquals("TSB PERSONAL LOAN", productArrangement.getOfferedProducts().get(0).getProductName());
        assertEquals("343", productArrangement.getOfferedProducts().get(0).getProductIdentifier());
        assertEquals(18, productArrangement.getOfferedProducts().get(0).getProductoptions().size());
        assertEquals(1, productArrangement.getPrimaryInvolvedParty().getCustomerScore().size());
        assertEquals("CREDIT_SCORE", productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
        assertEquals("STPL5819141218084429", productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreIdentifier());
        assertEquals("77714600421506", productArrangement.getPrimaryInvolvedParty().getCustomerNumber());

        Iterator iterator = productArrangement.getOfferedProducts().get(0).getProductoptions().iterator();
        while (iterator.hasNext()) {
            ProductOptions productOptions = (ProductOptions) iterator.next();
            ProductOptionsCode productOptionsCode = ProductOptionsCode.getProductOptionsCode(productOptions.getOptionsCode());

            switch (productOptionsCode) {
                case CURRENCY_CODE: {
                    assertEquals("GBP", productOptions.getOptionsValue());
                    break;
                }
                case MINIMUM_LOAN_AMOUNT: {
                    assertEquals("1000", productOptions.getOptionsValue());
                    break;
                }
                case MAXIMUM_LOAN_AMOUNT: {
                    assertEquals("25000", productOptions.getOptionsValue());
                    break;
                }
                case MINIMUM_LOAN_TERM: {
                    assertEquals("12", productOptions.getOptionsValue());
                    break;
                }
                case MAXIMUM_LOAN_TERM: {
                    assertEquals("84", productOptions.getOptionsValue());
                    break;
                }
                case INSURANCE_AVAILABLE_INDICATOR: {
                    assertEquals("true", productOptions.getOptionsValue());
                    break;
                }
                case LETTER_CHARGES: {
                    assertEquals("25", productOptions.getOptionsValue());
                    break;
                }
                case DAYS_INTEREST_CHARGED: {
                    assertEquals("58", productOptions.getOptionsValue());
                    break;
                }
                case MAXIMUM_CHARGE_AMOUNT: {
                    assertEquals("250", productOptions.getOptionsValue());
                    break;
                }
                case ADMIN_CHARGES: {
                    assertEquals("1", productOptions.getOptionsValue());
                    break;
                }
                case LOAN_TERM_EXEMPTION_START_DATE: {
                    assertEquals("30", productOptions.getOptionsValue());
                    break;
                }
                case LOAN_TERM_EXEMPTION_END_DATE: {
                    assertEquals("90", productOptions.getOptionsValue());
                    break;
                }
                case MINIMUM_LOAN_DEFER_TERM: {
                    assertEquals("0", productOptions.getOptionsValue());
                    break;
                }
                case MAXIMUM_LOAN_DEFER_TERM: {
                    assertEquals("0", productOptions.getOptionsValue());
                    break;
                }
                case MINIMUM_LOAN_TERM_REPAYMENT_HOLIDAYS: {
                    assertEquals("0", productOptions.getOptionsValue());
                    break;
                }
                case MAXIMUM_LOAN_TERM_REPAYMENT_HOLIDAYS: {
                    assertEquals("0", productOptions.getOptionsValue());
                    break;
                }
                case URL_TEXT_DISPLAY: {
                    assertEquals("Not", productOptions.getOptionsValue());
                    break;
                }
                case URL: {
                    assertEquals("Not", productOptions.getOptionsValue());
                    break;
                }
                default:
                    assertNotNull(productOptions);
            }
        }
    }

    @Test
    public void testConvertB231ResponseToEligibleProductsForInsuranceNotAvailable() {
        StB231BLoanPartyProductsGet b231Response = dataHelper.createB231Response(0);

        converter.convert(b231Response, productArrangement);

        assertEquals(1, productArrangement.getOfferedProducts().size());
        assertEquals("TSB PERSONAL LOAN", productArrangement.getOfferedProducts().get(0).getProductName());
        assertEquals("343", productArrangement.getOfferedProducts().get(0).getProductIdentifier());
        assertEquals(18, productArrangement.getOfferedProducts().get(0).getProductoptions().size());
        assertEquals(1, productArrangement.getPrimaryInvolvedParty().getCustomerScore().size());
        assertEquals("CREDIT_SCORE", productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
        assertEquals("STPL5819141218084429", productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreIdentifier());
        assertEquals("77714600421506", productArrangement.getPrimaryInvolvedParty().getCustomerNumber());

        Iterator iterator = productArrangement.getOfferedProducts().get(0).getProductoptions().iterator();
        while (iterator.hasNext()) {
            ProductOptions productOptions = (ProductOptions) iterator.next();
            ProductOptionsCode productOptionsCode = ProductOptionsCode.getProductOptionsCode(productOptions.getOptionsCode());

            switch (productOptionsCode) {
                case CURRENCY_CODE: {
                    assertEquals("GBP", productOptions.getOptionsValue());
                    break;
                }
                case MINIMUM_LOAN_AMOUNT: {
                    assertEquals("1000", productOptions.getOptionsValue());
                    break;
                }
                case MAXIMUM_LOAN_AMOUNT: {
                    assertEquals("25000", productOptions.getOptionsValue());
                    break;
                }
                case MINIMUM_LOAN_TERM: {
                    assertEquals("12", productOptions.getOptionsValue());
                    break;
                }
                case MAXIMUM_LOAN_TERM: {
                    assertEquals("84", productOptions.getOptionsValue());
                    break;
                }
                case INSURANCE_AVAILABLE_INDICATOR: {
                    assertEquals("false", productOptions.getOptionsValue());
                    break;
                }
                case LETTER_CHARGES: {
                    assertEquals("25", productOptions.getOptionsValue());
                    break;
                }
                case DAYS_INTEREST_CHARGED: {
                    assertEquals("58", productOptions.getOptionsValue());
                    break;
                }
                case MAXIMUM_CHARGE_AMOUNT: {
                    assertEquals("250", productOptions.getOptionsValue());
                    break;
                }
                case ADMIN_CHARGES: {
                    assertEquals("1", productOptions.getOptionsValue());
                    break;
                }
                case LOAN_TERM_EXEMPTION_START_DATE: {
                    assertEquals("30", productOptions.getOptionsValue());
                    break;
                }
                case LOAN_TERM_EXEMPTION_END_DATE: {
                    assertEquals("90", productOptions.getOptionsValue());
                    break;
                }
                case MINIMUM_LOAN_DEFER_TERM: {
                    assertEquals("0", productOptions.getOptionsValue());
                    break;
                }
                case MAXIMUM_LOAN_DEFER_TERM: {
                    assertEquals("0", productOptions.getOptionsValue());
                    break;
                }
                case MINIMUM_LOAN_TERM_REPAYMENT_HOLIDAYS: {
                    assertEquals("0", productOptions.getOptionsValue());
                    break;
                }
                case MAXIMUM_LOAN_TERM_REPAYMENT_HOLIDAYS: {
                    assertEquals("0", productOptions.getOptionsValue());
                    break;
                }
                case URL_TEXT_DISPLAY: {
                    assertEquals("Not", productOptions.getOptionsValue());
                    break;
                }
                case URL: {
                    assertEquals("Not", productOptions.getOptionsValue());
                    break;
                }
                default:
                    assertNotNull(productOptions);
            }
        }
    }

    @Test
    public void testConvertB231ResponseToEligibleProductsForEmptyAstloanproduct() {
        StB231BLoanPartyProductsGet b231Response = dataHelper.createB231Response(0);
        b231Response.getAstloanproduct().clear();

        converter.convert(b231Response, productArrangement);

        assertEquals(0, productArrangement.getOfferedProducts().size());
        assertEquals(1, productArrangement.getPrimaryInvolvedParty().getCustomerScore().size());
        assertEquals("CREDIT_SCORE", productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
        assertEquals("ACCEPT", productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult());
        assertEquals("STPL5819141218084429", productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreIdentifier());
        assertEquals("77714600421506", productArrangement.getPrimaryInvolvedParty().getCustomerNumber());
    }

    @Test
    public void testConvertB231ResponseToEligibleProductsForNullCustomerNumber() {
        StB231BLoanPartyProductsGet b231Response = dataHelper.createB231Response(0);
        b231Response.getStloanheader().setCustnum(null);

        converter.convert(b231Response, productArrangement);

        assertEquals(1, productArrangement.getOfferedProducts().size());
        assertEquals("TSB PERSONAL LOAN", productArrangement.getOfferedProducts().get(0).getProductName());
        assertEquals("343", productArrangement.getOfferedProducts().get(0).getProductIdentifier());
        assertEquals(18, productArrangement.getOfferedProducts().get(0).getProductoptions().size());
        assertEquals(1, productArrangement.getPrimaryInvolvedParty().getCustomerScore().size());
        assertEquals("CREDIT_SCORE", productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
        assertEquals("ACCEPT", productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult());
        assertEquals("STPL5819141218084429", productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreIdentifier());
        assertEquals(null, productArrangement.getPrimaryInvolvedParty().getCustomerNumber());
    }

    @Test
    public void testConvertB231ResponseToEligibleProductsForNullStloancharges() {
        StB231BLoanPartyProductsGet b231Response = dataHelper.createB231Response(0);
        b231Response.getAstloanproduct().get(0).setStloancharges(null);

        converter.convert(b231Response, productArrangement);

        assertEquals(1, productArrangement.getOfferedProducts().size());
        assertEquals("TSB PERSONAL LOAN", productArrangement.getOfferedProducts().get(0).getProductName());
        assertEquals("343", productArrangement.getOfferedProducts().get(0).getProductIdentifier());
        assertEquals(12, productArrangement.getOfferedProducts().get(0).getProductoptions().size());
        assertEquals(1, productArrangement.getPrimaryInvolvedParty().getCustomerScore().size());
        assertEquals("CREDIT_SCORE", productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
        assertEquals("STPL5819141218084429", productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreIdentifier());
        assertEquals("77714600421506", productArrangement.getPrimaryInvolvedParty().getCustomerNumber());
    }

    @Test
    public void testConvertB231ResponseToEligibleProductsForNullUrltxtDisplayAndUrltxtURL() {
        StB231BLoanPartyProductsGet b231Response = dataHelper.createB231Response(0);
        b231Response.getAstloanproduct().get(0).setUrltxtDisplay(null);
        b231Response.getAstloanproduct().get(0).setUrltxtURL(null);

        converter.convert(b231Response, productArrangement);

        assertEquals(1, productArrangement.getOfferedProducts().size());
        assertEquals("TSB PERSONAL LOAN", productArrangement.getOfferedProducts().get(0).getProductName());
        assertEquals("343", productArrangement.getOfferedProducts().get(0).getProductIdentifier());
        assertEquals(16, productArrangement.getOfferedProducts().get(0).getProductoptions().size());
        assertEquals(1, productArrangement.getPrimaryInvolvedParty().getCustomerScore().size());
        assertEquals("CREDIT_SCORE", productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
        assertEquals("STPL5819141218084429", productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreIdentifier());
        assertEquals("77714600421506", productArrangement.getPrimaryInvolvedParty().getCustomerNumber());
    }
}
