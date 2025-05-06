package com.lloydsbanking.salsa.apacc.service.fulfil.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apacc.TestDataHelper;
import com.lloydsbanking.salsa.soap.fdi.f241.objects.*;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class F241RequestFactoryTest {
    F241RequestFactory f241RequestFactory;
    FinanceServiceArrangement financeServiceArrangement;
    TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        f241RequestFactory = new F241RequestFactory();
        testDataHelper = new TestDataHelper();
        financeServiceArrangement = testDataHelper.createFinanceServiceArrangement();
        f241RequestFactory.f241MarketingDataSegmentFactory = mock(F241MarketingDataSegmentFactory.class);
        f241RequestFactory.f241CustomerDataSegmentFactory = mock(F241CustomerDataSegmentFactory.class);
        f241RequestFactory.f241AccountDataSegmentFactory = mock(F241AccountDataSegmentFactory.class);
    }

    @Test
    public void testConvert() {
        when(f241RequestFactory.f241MarketingDataSegmentFactory.getMarketingDataSegment(financeServiceArrangement)).thenReturn(new MarketingDataSegment());
        when(f241RequestFactory.f241CustomerDataSegmentFactory.getCustomerDataSegment(financeServiceArrangement)).thenReturn(new CustomerDataSegment());
        when(f241RequestFactory.f241AccountDataSegmentFactory.getAccountDataSegment(any(FinanceServiceArrangement.class), any(CardDataSegment.class))).thenReturn(new AccountDataSegment());
        F241Req f241Req = f241RequestFactory.convert(financeServiceArrangement);
        assertEquals("E8V4", f241Req.getMessageVersionNo());
        assertEquals("A", f241Req.getCustRecActionCd());
        assertEquals(Short.valueOf("1"), f241Req.getCardDataSegment().getCardReissueStatusCd());
    }

    @Test
    public void testConvertForNotJointParty() {
        financeServiceArrangement.setIsJointParty(false);
        when(f241RequestFactory.f241MarketingDataSegmentFactory.getMarketingDataSegment(financeServiceArrangement)).thenReturn(new MarketingDataSegment());
        when(f241RequestFactory.f241CustomerDataSegmentFactory.getCustomerDataSegment(financeServiceArrangement)).thenReturn(new CustomerDataSegment());
        when(f241RequestFactory.f241AccountDataSegmentFactory.getAccountDataSegment(any(FinanceServiceArrangement.class), any(CardDataSegment.class))).thenReturn(new AccountDataSegment());
        F241Req f241Req = f241RequestFactory.convert(financeServiceArrangement);
        assertEquals("E8V4", f241Req.getMessageVersionNo());
        assertEquals("A", f241Req.getCustRecActionCd());
        assertEquals(Short.valueOf("1"), f241Req.getCardDataSegment().getCardReissueStatusCd());
    }
}
