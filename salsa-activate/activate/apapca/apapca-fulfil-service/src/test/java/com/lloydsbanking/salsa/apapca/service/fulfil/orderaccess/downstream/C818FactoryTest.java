package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.soap.cmas.c818.objects.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertEquals;

@Category(UnitTest.class)
public class C818FactoryTest {
    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
    }

    @Test
    public void testCreateC818Request() {

        CardOrderAdd cardOrderAdd = testDataHelper.createCardOrderAdd();
        CardOrderAddNew cardOrderAddNew = testDataHelper.createCardOrderAddNew();
        CardOrderCBSCCA cardOrderCBSCCA = testDataHelper.createCardOrderCBSCCA();
        CardOrderActions cardOrderActions = testDataHelper.createCardOrderActions();
        C818Req c818Req = C818Factory.createC818Request(cardOrderAdd, cardOrderAddNew, cardOrderCBSCCA, new CardDeliveryAddress(), cardOrderActions);
        assertEquals(19, c818Req.getExtSysId());
        assertEquals(null, c818Req.getCardDeliveryAddress().getAddressLine1Tx());

    }
}
