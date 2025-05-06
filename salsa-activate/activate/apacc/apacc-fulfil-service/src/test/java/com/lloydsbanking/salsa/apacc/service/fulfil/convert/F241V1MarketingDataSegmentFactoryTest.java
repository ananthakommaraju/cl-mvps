package com.lloydsbanking.salsa.apacc.service.fulfil.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apacc.TestDataHelper;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.MarketingDataSegment;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Category(UnitTest.class)
public class F241V1MarketingDataSegmentFactoryTest {
    F241V1MarketingDataSegmentFactory dataSegmentFactory;
    TestDataHelper testDataHelper;
    FinanceServiceArrangement financeServiceArrangement;
    @Before
    public void setUp() {
        dataSegmentFactory = new F241V1MarketingDataSegmentFactory();
        testDataHelper = new TestDataHelper();
        financeServiceArrangement = testDataHelper.createFinanceServiceArrangement();
    }
    @Test
    public void testGetMarketingDataSegment() {
        MarketingDataSegment marketingDataSegment = dataSegmentFactory.getMarketingDataSegment(financeServiceArrangement);
        assertNull(marketingDataSegment.getCoOwnerMarketingProducts());
        assertEquals("0",marketingDataSegment.getOwnerMarketingChannels().getStmtMktgChanIn());
        assertEquals("0",marketingDataSegment.getOwnerMarketingChannels().getDirectMarketingIn());
        assertEquals("0",marketingDataSegment.getOwnerMarketingChannels().getPINMktgChanIn());
        assertEquals("0",marketingDataSegment.getOwnerMarketingChannels().getStmtMktgChanIn());
        assertEquals("0",marketingDataSegment.getOwnerMarketingChannels().getDirectMarketingIn());
        assertEquals("0",marketingDataSegment.getOwnerMarketingChannels().getCrdMktgChanIn());
    }
}
