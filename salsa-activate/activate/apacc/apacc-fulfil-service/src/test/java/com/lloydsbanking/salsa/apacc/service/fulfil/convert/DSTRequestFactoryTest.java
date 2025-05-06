package com.lloydsbanking.salsa.apacc.service.fulfil.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apacc.TestDataHelper;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Category(UnitTest.class)
public class DSTRequestFactoryTest {

    DSTRequestFactory dstRequestFactory;
    TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        dstRequestFactory = new DSTRequestFactory();
        testDataHelper = new TestDataHelper();
        dstRequestFactory.dstFieldFactory = mock(DSTFieldFactory.class);
    }

    @Test
    public void convertForLTBTest() {
        byte[] byteArray = null;
        FinanceServiceArrangement financeServiceArrangement = testDataHelper.createFinanceServiceArrangement();
        dstRequestFactory.convert(financeServiceArrangement, "LTB", byteArray);
        verify(dstRequestFactory.dstFieldFactory).getFieldList(financeServiceArrangement);
        String abc = String.format("%04d", Long.valueOf("01"));
        System.out.println("abc = " + abc);
    }

    @Test
    public void convertForHLXTest() {
        byte[] byteArray = null;
        FinanceServiceArrangement financeServiceArrangement = testDataHelper.createFinanceServiceArrangement();
        dstRequestFactory.convert(financeServiceArrangement, "HLX", byteArray);
        verify(dstRequestFactory.dstFieldFactory).getFieldList(financeServiceArrangement);
    }

    @Test
    public void convertForVERTest() {
        byte[] byteArray = null;
        FinanceServiceArrangement financeServiceArrangement = testDataHelper.createFinanceServiceArrangement();
        dstRequestFactory.convert(financeServiceArrangement, "VER", byteArray);
        verify(dstRequestFactory.dstFieldFactory).getFieldList(financeServiceArrangement);
    }

    @Test
    public void convertForBOSTest() {
        byte[] byteArray = null;
        FinanceServiceArrangement financeServiceArrangement = testDataHelper.createFinanceServiceArrangement();
        dstRequestFactory.convert(financeServiceArrangement, "BOS", byteArray);
        verify(dstRequestFactory.dstFieldFactory).getFieldList(financeServiceArrangement);
    }

}
