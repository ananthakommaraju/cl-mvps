package com.lloydsbanking.salsa.apacc.service.fulfil.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apacc.TestDataHelper;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.*;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class F241V1RequestFactoryTest {
    F241V1RequestFactory f241RequestFactory;
    FinanceServiceArrangement financeServiceArrangement;
    TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        f241RequestFactory = new F241V1RequestFactory();
        testDataHelper = new TestDataHelper();
        financeServiceArrangement = testDataHelper.createFinanceServiceArrangement();
        f241RequestFactory.f241V1MarketingDataSegmentFactory = mock(F241V1MarketingDataSegmentFactory.class);
        f241RequestFactory.f241V1CustomerDataSegmentFactory = mock(F241V1CustomerDataSegmentFactory.class);
        f241RequestFactory.f241V1AccountDataSegmentFactory = mock(F241V1AccountDataSegmentFactory.class);
    }

    @Test
    public void testConvert() {
        when(f241RequestFactory.f241V1MarketingDataSegmentFactory.getMarketingDataSegment(financeServiceArrangement)).thenReturn(new MarketingDataSegment());
        when(f241RequestFactory.f241V1CustomerDataSegmentFactory.getCustomerDataSegment(financeServiceArrangement)).thenReturn(new CustomerDataSegment());
        F241Req f241Req = f241RequestFactory.convert(financeServiceArrangement);
        assertEquals("E8V7", f241Req.getMessageVersionNo());
    }

    @Test
    public void testConvertWithCodeACCLOGO() {
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(0).setAttributeCode("ACC_LOGO");
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(0).setAttributeValue("066");
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).setOfferAmount(null);
        when(f241RequestFactory.f241V1AccountDataSegmentFactory.getAccountDataSegment(any(FinanceServiceArrangement.class), any(CardDataSegment.class))).thenReturn(new AccountDataSegmentV1());
        when(f241RequestFactory.f241V1MarketingDataSegmentFactory.getMarketingDataSegment(financeServiceArrangement)).thenReturn(new MarketingDataSegment());
        when(f241RequestFactory.f241V1CustomerDataSegmentFactory.getCustomerDataSegment(financeServiceArrangement)).thenReturn(new CustomerDataSegment());
        F241Req f241Req = f241RequestFactory.convert(financeServiceArrangement);
        assertEquals("E8V7", f241Req.getMessageVersionNo());
    }
}