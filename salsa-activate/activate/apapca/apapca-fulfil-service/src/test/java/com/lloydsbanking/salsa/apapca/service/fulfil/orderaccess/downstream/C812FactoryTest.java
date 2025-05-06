package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.soap.cmas.c812.objects.C812Req;
import com.lloydsbanking.salsa.soap.cmas.c812.objects.CardOrderCBSAddress;
import com.lloydsbanking.salsa.soap.cmas.c812.objects.CardOrderCBSData;
import com.lloydsbanking.salsa.soap.cmas.c812.objects.CardOrderNew;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertEquals;

@Category(UnitTest.class)
public class C812FactoryTest {
    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
    }

    @Test
    public void testCreateC812Request() {
        CardOrderNew cardOrderNew = testDataHelper.createCardOrderNew();
        CardOrderCBSAddress cardOrderCBSAddress = testDataHelper.createCardOrderCBSAddress();
        CardOrderCBSData cardOrderCBSData = testDataHelper.createCardOrderCBSData();
        C812Req c812Req = C812Factory.createC812Request(cardOrderNew, cardOrderCBSData, cardOrderCBSAddress);
        assertEquals(19, c812Req.getExtSysId());
        assertEquals(cardOrderCBSAddress.getAddressLine1Tx40(), c812Req.getCardOrderCBSAddress().getAddressLine1Tx40());
    }
}
