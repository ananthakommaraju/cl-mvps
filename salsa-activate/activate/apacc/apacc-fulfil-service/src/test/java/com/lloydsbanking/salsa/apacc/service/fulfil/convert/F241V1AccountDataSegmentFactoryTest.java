package com.lloydsbanking.salsa.apacc.service.fulfil.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apacc.TestDataHelper;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.AccountDataSegmentV1;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.CardDataSegment;
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
public class F241V1AccountDataSegmentFactoryTest {
    F241V1AccountDataSegmentFactory factory;
    TestDataHelper testDataHelper;
    FinanceServiceArrangement financeServiceArrangement;

    @Before
    public void setUp() {
        factory = new F241V1AccountDataSegmentFactory();
        testDataHelper = new TestDataHelper();
        financeServiceArrangement = testDataHelper.createFinanceServiceArrangement();
    }

    @Test
    public void testGetAccountDataSegment() {
        CardDataSegment cardDataSegment = new CardDataSegment();
        AccountDataSegmentV1 accountDataSegment = factory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
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
        AccountDataSegmentV1 accountDataSegment = factory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
        assertEquals("N", accountDataSegment.getConvChequeOrderIn());
        assertEquals("D", accountDataSegment.getCardDeliveryMethodCd());
        assertEquals("0", accountDataSegment.getPersonEmployeeCd());
        assertNull(accountDataSegment.getIntermediaryId());
        assertEquals("A1800000000000000000", accountDataSegment.getPromotionalDataTx());
        assertEquals(Short.valueOf("2"), accountDataSegment.getCreditCardDDPaymentCd());
        assertEquals(Short.valueOf("1"), accountDataSegment.getDDNomAmtFlagCd());
    }

    @Test
    public void testGetAccountDataSegmentWithFullDebit() {
        financeServiceArrangement.setIsDirectDebitRequired(true);
        financeServiceArrangement.setDirectDebit(getDirectDebit("FULL"));
        CardDataSegment cardDataSegment = new CardDataSegment();
        AccountDataSegmentV1 accountDataSegment = factory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
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
        AccountDataSegmentV1 accountDataSegment = factory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
        assertEquals("N", accountDataSegment.getConvChequeOrderIn());
        assertEquals("D", accountDataSegment.getCardDeliveryMethodCd());
        assertEquals("0", accountDataSegment.getPersonEmployeeCd());
        assertEquals(financeServiceArrangement.getAffiliatedetails().get(0).getAffiliateIdentifier(), accountDataSegment.getIntermediaryId());
        assertEquals("10200000000000000000", accountDataSegment.getPromotionalDataTx());
        assertEquals(Short.valueOf("1"), accountDataSegment.getCreditCardDDPaymentCd());
        assertEquals(Short.valueOf("0"), accountDataSegment.getDDNomAmtFlagCd());
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
    public void testGetAccountDataSegmentWithDebit() {
        financeServiceArrangement.setIsDirectDebitRequired(true);
        financeServiceArrangement.setDirectDebit(getDirectDebit(null));
        CardDataSegment cardDataSegment = new CardDataSegment();
        AccountDataSegmentV1 accountDataSegment = factory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
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
        AccountDataSegmentV1 accountDataSegment = factory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
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
        AccountDataSegmentV1 accountDataSegment = factory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
        assertEquals("N", accountDataSegment.getConvChequeOrderIn());
        assertEquals("D", accountDataSegment.getCardDeliveryMethodCd());
        assertEquals("0", accountDataSegment.getPersonEmployeeCd());
        assertEquals(financeServiceArrangement.getAffiliatedetails().get(0).getAffiliateIdentifier(), accountDataSegment.getIntermediaryId());
        assertEquals("10200000000000000000", accountDataSegment.getPromotionalDataTx());

    }

    @Test
    public void testConvertWithCodeACCLOGO() {
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(0).setAttributeCode("ACC_LOGO");
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(0).setAttributeValue("066");
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).setOfferAmount(null);
        CardDataSegment cardDataSegment = new CardDataSegment();
        AccountDataSegmentV1 accountDataSegment = factory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
        assertEquals("N", accountDataSegment.getConvChequeOrderIn());
        assertEquals("D", accountDataSegment.getCardDeliveryMethodCd());
        assertEquals("0", accountDataSegment.getPersonEmployeeCd());
        assertEquals(financeServiceArrangement.getAffiliatedetails().get(0).getAffiliateIdentifier(), accountDataSegment.getIntermediaryId());
        assertEquals("10200000000000000000", accountDataSegment.getPromotionalDataTx());
    }

    @Test
    public void testConvertForNotJointPartyForStateIssue() {
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(0).setAttributeCode("ISS_ID");
        financeServiceArrangement.setIsJointParty(false);
        CardDataSegment cardDataSegment = new CardDataSegment();
        AccountDataSegmentV1 accountDataSegment = factory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
        assertEquals("N", accountDataSegment.getConvChequeOrderIn());
        assertEquals("D", accountDataSegment.getCardDeliveryMethodCd());
        assertEquals("0", accountDataSegment.getPersonEmployeeCd());
        assertEquals(financeServiceArrangement.getAffiliatedetails().get(0).getAffiliateIdentifier(), accountDataSegment.getIntermediaryId());
        assertEquals("10200000000000000000", accountDataSegment.getPromotionalDataTx());
        assertEquals(Integer.valueOf("999999998"), accountDataSegment.getSortCd());
        assertEquals("N", accountDataSegment.getConvChequeOrderIn());
        assertEquals("102", accountDataSegment.getIntermediaryId());
    }

    @Test
    public void testConvertForNotJointPartyForCashPlan() {
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(0).setAttributeCode("DEF_CH_PL");
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(0).setAttributeValue("066");
        financeServiceArrangement.setIsJointParty(false);
        CardDataSegment cardDataSegment = new CardDataSegment();
        AccountDataSegmentV1 accountDataSegment = factory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
        assertEquals("N", accountDataSegment.getConvChequeOrderIn());
        assertEquals("D", accountDataSegment.getCardDeliveryMethodCd());
        assertEquals("0", accountDataSegment.getPersonEmployeeCd());
        assertEquals(Integer.valueOf("999999998"), accountDataSegment.getSortCd());
        assertEquals("N", accountDataSegment.getConvChequeOrderIn());
        assertEquals("102", accountDataSegment.getIntermediaryId());
    }

    @Test
    public void testConvertForNotJointPartyForRetailPlan() {
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(0).setAttributeCode("DEF_RTL_PL");
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(0).setAttributeValue("066");
        financeServiceArrangement.setIsJointParty(false);
        CardDataSegment cardDataSegment = new CardDataSegment();
        AccountDataSegmentV1 accountDataSegment = factory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
        assertEquals("N", accountDataSegment.getConvChequeOrderIn());
        assertEquals("D", accountDataSegment.getCardDeliveryMethodCd());
        assertEquals("0", accountDataSegment.getPersonEmployeeCd());
        assertEquals(Integer.valueOf("999999998"), accountDataSegment.getSortCd());
        assertEquals("N", accountDataSegment.getConvChequeOrderIn());
        assertEquals("102", accountDataSegment.getIntermediaryId());
    }

    @Test
    public void testConvertForNotJointPartyForCreditPlan() {
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(0).setAttributeCode("DEF_PRM_PL");
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(0).setAttributeValue("066");
        financeServiceArrangement.setIsJointParty(false);
        CardDataSegment cardDataSegment = new CardDataSegment();
        AccountDataSegmentV1 accountDataSegment = factory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
        assertEquals("N", accountDataSegment.getConvChequeOrderIn());
        assertEquals("D", accountDataSegment.getCardDeliveryMethodCd());
        assertEquals("0", accountDataSegment.getPersonEmployeeCd());
        assertEquals(Integer.valueOf("999999998"), accountDataSegment.getSortCd());
        assertEquals("N", accountDataSegment.getConvChequeOrderIn());
        assertEquals("102", accountDataSegment.getIntermediaryId());
    }

    @Test
    public void testConvertForNotJointPartyForCampaign() {
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(0).setAttributeCode("MAC");
        financeServiceArrangement.setIsJointParty(false);
        CardDataSegment cardDataSegment = new CardDataSegment();
        AccountDataSegmentV1 accountDataSegment = factory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
        assertEquals("N", accountDataSegment.getConvChequeOrderIn());
        assertEquals("D", accountDataSegment.getCardDeliveryMethodCd());
        assertEquals("0", accountDataSegment.getPersonEmployeeCd());
        assertEquals(Integer.valueOf("999999998"), accountDataSegment.getSortCd());
        assertEquals("N", accountDataSegment.getConvChequeOrderIn());
        assertEquals("102", accountDataSegment.getIntermediaryId());
    }

    @Test
    public void testConvertForDirectDebit() {
        financeServiceArrangement.setIsDirectDebitRequired(true);
        DirectDebit directDebit = new DirectDebit();
        directDebit.setSortCode("0010");
        directDebit.setDdType("FIXED");
        directDebit.setAmount(new CurrencyAmount());
        directDebit.getAmount().setAmount(BigDecimal.ONE);
        financeServiceArrangement.setDirectDebit(directDebit);
        CardDataSegment cardDataSegment = new CardDataSegment();
        AccountDataSegmentV1 accountDataSegment = factory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
        assertEquals("N", accountDataSegment.getConvChequeOrderIn());
        assertEquals("D", accountDataSegment.getCardDeliveryMethodCd());
        assertEquals("0", accountDataSegment.getPersonEmployeeCd());
        assertEquals(Integer.valueOf("999999998"), accountDataSegment.getSortCd());
        assertEquals("N", accountDataSegment.getConvChequeOrderIn());
        assertEquals("102", accountDataSegment.getIntermediaryId());
    }

    @Test
    public void testGetAccountDataSegmentWithDirectDebitAmountWithDecimalPlaces() {
        financeServiceArrangement.setIsDirectDebitRequired(true);
        financeServiceArrangement.setDirectDebit(getDirectDebit("FIXED", new BigDecimal("129.00")));
        CardDataSegment cardDataSegment = new CardDataSegment();
        AccountDataSegmentV1 accountDataSegment = factory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
        assertEquals(Short.valueOf("2"), accountDataSegment.getCreditCardDDPaymentCd());
        assertEquals(Short.valueOf("1"), accountDataSegment.getDDNomAmtFlagCd());
        assertEquals("12900", accountDataSegment.getDDNomAm());
    }
}
