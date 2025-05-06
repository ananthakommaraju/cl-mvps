package com.lloydsbanking.salsa.apacc.service.fulfil.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apacc.TestDataHelper;
import com.lloydsbanking.salsa.soap.fdi.f241.objects.AccountDataSegment;
import com.lloydsbanking.salsa.soap.fdi.f241.objects.CardDataSegment;
import lib_sim_bo.businessobjects.AffiliateDetails;
import lib_sim_bo.businessobjects.CurrencyAmount;
import lib_sim_bo.businessobjects.DirectDebit;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Category(UnitTest.class)
public class F241AccountDataSegmentFactoryTest {
    F241AccountDataSegmentFactory factory;
    TestDataHelper testDataHelper;
    FinanceServiceArrangement financeServiceArrangement;

    @Before
    public void setUp() {
        factory = new F241AccountDataSegmentFactory();
        testDataHelper = new TestDataHelper();
        financeServiceArrangement = testDataHelper.createFinanceServiceArrangement();
    }

    @Test
    public void testGetAccountDataSegment() {
        CardDataSegment cardDataSegment = new CardDataSegment();
        AccountDataSegment accountDataSegment = factory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
        assertEquals("N", accountDataSegment.getConvChequeOrderIn());
        assertEquals("D", accountDataSegment.getCardDeliveryMethodCd());
        assertEquals("0", accountDataSegment.getPersonEmployeeCd());
        assertEquals(financeServiceArrangement.getAffiliatedetails().get(0).getAffiliateIdentifier(), accountDataSegment.getIntermediaryId());
        assertEquals("10200000000000000000", accountDataSegment.getPromotionalDataTx());
    }

    @Test
    public void testGetAccountDataSegmentWithStaffMemberFalse() {
        financeServiceArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setIsStaffMember(false);
        financeServiceArrangement.getAffiliatedetails().clear();
        AffiliateDetails affiliateDetails = testDataHelper.getAffiliateDetails();
        affiliateDetails.setIsCreditIntermediary(false);
        financeServiceArrangement.getAffiliatedetails().add(affiliateDetails);
        financeServiceArrangement.setIsDirectDebitRequired(true);
        financeServiceArrangement.setDirectDebit(getDirectDebit("FIXED"));
        CardDataSegment cardDataSegment = new CardDataSegment();
        AccountDataSegment accountDataSegment = factory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
        assertEquals("N", accountDataSegment.getConvChequeOrderIn());
        assertEquals("D", accountDataSegment.getCardDeliveryMethodCd());
        assertEquals("0", accountDataSegment.getPersonEmployeeCd());
        assertNull(accountDataSegment.getIntermediaryId());
        assertEquals(Short.valueOf("2"), accountDataSegment.getCreditCardDDPaymentCd());
        assertEquals(Short.valueOf("1"), accountDataSegment.getDDNomAmtFlagCd());
    }

    @Test
    public void testGetAccountDataSegmentWithFullDebit() {
        financeServiceArrangement.setIsDirectDebitRequired(true);
        financeServiceArrangement.setDirectDebit(getDirectDebit("FULL"));
        CardDataSegment cardDataSegment = new CardDataSegment();
        AccountDataSegment accountDataSegment = factory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
        assertEquals("N", accountDataSegment.getConvChequeOrderIn());
        assertEquals("D", accountDataSegment.getCardDeliveryMethodCd());
        assertEquals("0", accountDataSegment.getPersonEmployeeCd());
        assertEquals(financeServiceArrangement.getAffiliatedetails().get(0).getAffiliateIdentifier(), accountDataSegment.getIntermediaryId());
        assertEquals("10200000000000000000", accountDataSegment.getPromotionalDataTx());
        assertEquals(Short.valueOf("2"), accountDataSegment.getCreditCardDDPaymentCd());
        assertEquals(Short.valueOf("3"), accountDataSegment.getDDNomAmtFlagCd());
    }

    @Test
    public void testGetAccountDataSegmentWithMinDebit() {
        financeServiceArrangement.setIsDirectDebitRequired(true);
        financeServiceArrangement.setDirectDebit(getDirectDebit("MIN"));
        CardDataSegment cardDataSegment = new CardDataSegment();
        AccountDataSegment accountDataSegment = factory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
        assertEquals("N", accountDataSegment.getConvChequeOrderIn());
        assertEquals("D", accountDataSegment.getCardDeliveryMethodCd());
        assertEquals("0", accountDataSegment.getPersonEmployeeCd());
        assertEquals(financeServiceArrangement.getAffiliatedetails().get(0).getAffiliateIdentifier(), accountDataSegment.getIntermediaryId());
        assertEquals("10200000000000000000", accountDataSegment.getPromotionalDataTx());
        assertEquals(Short.valueOf("1"), accountDataSegment.getCreditCardDDPaymentCd());
        assertEquals(Short.valueOf("0"), accountDataSegment.getDDNomAmtFlagCd());
    }

    @Test
    public void testGetAccountDataSegmentWithDebit() {
        financeServiceArrangement.setIsDirectDebitRequired(true);
        financeServiceArrangement.setDirectDebit(getDirectDebit(null));
        CardDataSegment cardDataSegment = new CardDataSegment();
        AccountDataSegment accountDataSegment = factory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
        assertEquals("N", accountDataSegment.getConvChequeOrderIn());
        assertEquals("D", accountDataSegment.getCardDeliveryMethodCd());
        assertEquals("0", accountDataSegment.getPersonEmployeeCd());
        assertEquals(financeServiceArrangement.getAffiliatedetails().get(0).getAffiliateIdentifier(), accountDataSegment.getIntermediaryId());
        assertEquals("10200000000000000000", accountDataSegment.getPromotionalDataTx());
        assertEquals(Short.valueOf("0"), accountDataSegment.getCreditCardDDPaymentCd());
        assertEquals(Short.valueOf("0"), accountDataSegment.getDDNomAmtFlagCd());
    }

    @Test
    public void testGetAccountDataSegmentForSrvcCharge() {
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(0).setAttributeCode("SrvcChrgFeeOvrd");
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(0).setAttributeValue("9990");
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(1).setAttributeValue("999");
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(2).setAttributeValue("999");
        CardDataSegment cardDataSegment = new CardDataSegment();
        AccountDataSegment accountDataSegment = factory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
        assertEquals("N", accountDataSegment.getConvChequeOrderIn());
        assertEquals("D", accountDataSegment.getCardDeliveryMethodCd());
        assertEquals("0", accountDataSegment.getPersonEmployeeCd());
        assertEquals(financeServiceArrangement.getAffiliatedetails().get(0).getAffiliateIdentifier(), accountDataSegment.getIntermediaryId());
        assertEquals("10200000000000000000", accountDataSegment.getPromotionalDataTx());

    }

    @Test
    public void testGetAccountDataSegmentForSrvcChargeFeeEndt() {
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(0).setAttributeCode("SrvcChrgFeeOvrd");
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(0).setAttributeValue("9990");
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(1).setAttributeValue("999");
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(2).setAttributeValue("998");
        CardDataSegment cardDataSegment = new CardDataSegment();
        AccountDataSegment accountDataSegment = factory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
        assertEquals("N", accountDataSegment.getConvChequeOrderIn());
        assertEquals("D", accountDataSegment.getCardDeliveryMethodCd());
        assertEquals("0", accountDataSegment.getPersonEmployeeCd());
        assertEquals(financeServiceArrangement.getAffiliatedetails().get(0).getAffiliateIdentifier(), accountDataSegment.getIntermediaryId());
        assertEquals("10200000000000000000", accountDataSegment.getPromotionalDataTx());
    }

    private DirectDebit getDirectDebit(String ddType) {
        DirectDebit directDebit = new DirectDebit();
        directDebit.setDdType(ddType);
        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setAmount(new BigDecimal("200"));
        directDebit.setAmount(currencyAmount);
        directDebit.setSortCode("2265325");
        return directDebit;
    }

    private DirectDebit getDirectDebit(String ddType, BigDecimal amount) {
        DirectDebit directDebit = getDirectDebit(ddType);
        directDebit.getAmount().setAmount(amount);
        return directDebit;
    }

    @Test
    public void testGetAccountDataSegmentWithDirectDebitAmountWithDecimalPlaces() {
        financeServiceArrangement.setIsDirectDebitRequired(true);
        financeServiceArrangement.setDirectDebit(getDirectDebit("FIXED", new BigDecimal("129.00")));
        CardDataSegment cardDataSegment = new CardDataSegment();
        AccountDataSegment accountDataSegment = factory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
        assertEquals(Short.valueOf("2"), accountDataSegment.getCreditCardDDPaymentCd());
        assertEquals(Short.valueOf("1"), accountDataSegment.getDDNomAmtFlagCd());
        assertEquals("12900", accountDataSegment.getDDNomAm());
    }

    @Test
    public void testGetAccountDataSegmentWithDirectDebitAmountWithoutDecimalPlaces() {
        financeServiceArrangement.setIsDirectDebitRequired(true);
        financeServiceArrangement.setDirectDebit(getDirectDebit("FIXED", new BigDecimal("129")));
        CardDataSegment cardDataSegment = new CardDataSegment();
        AccountDataSegment accountDataSegment = factory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
        assertEquals(Short.valueOf("2"), accountDataSegment.getCreditCardDDPaymentCd());
        assertEquals(Short.valueOf("1"), accountDataSegment.getDDNomAmtFlagCd());
        assertEquals("12900", accountDataSegment.getDDNomAm());
    }

    @Test
    public void testGetAccountDataSegmentWithDirectDebitAmountWithNonZeroDecimalPlaces() {
        financeServiceArrangement.setIsDirectDebitRequired(true);
        financeServiceArrangement.setDirectDebit(getDirectDebit("FIXED", new BigDecimal("129.34")));
        CardDataSegment cardDataSegment = new CardDataSegment();
        AccountDataSegment accountDataSegment = factory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
        assertEquals(Short.valueOf("2"), accountDataSegment.getCreditCardDDPaymentCd());
        assertEquals(Short.valueOf("1"), accountDataSegment.getDDNomAmtFlagCd());
        assertEquals("12934", accountDataSegment.getDDNomAm());
    }

}
