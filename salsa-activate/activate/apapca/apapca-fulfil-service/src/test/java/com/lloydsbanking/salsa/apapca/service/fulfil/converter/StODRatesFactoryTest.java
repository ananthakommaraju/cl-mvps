package com.lloydsbanking.salsa.apapca.service.fulfil.converter;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.soap.fs.account.StODRates;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;


@Category(UnitTest.class)
public class StODRatesFactoryTest {

    StODRatesFactory stODRatesFactory;
    TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        stODRatesFactory = new StODRatesFactory();
        testDataHelper = new TestDataHelper();
    }

    @Test
    public void getStODRatesTest() {
        Map<String, BigDecimal> interestRateMap = new HashMap<>();
        interestRateMap.put("INT_FREE_OVERDRAFT", new BigDecimal(0));
        interestRateMap.put("EXCESS_FEE_CAP", new BigDecimal(0));
        StODRates stODRates = stODRatesFactory.getStODRates(true, null, interestRateMap);
        assertEquals(new BigInteger("0"), stODRates.getNExcessFeeCap());
        assertEquals(new BigDecimal("0"), stODRates.getAmtIntFreeOverdraft());
    }

    @Test
    public void getStODRatesTestWithAuthMonthlyWithDecimalPlaces() {
        Map<String, BigDecimal> interestRateMap = new HashMap<>();
        interestRateMap.put("INT_FREE_OVERDRAFT", new BigDecimal(0));
        interestRateMap.put("EXCESS_FEE_CAP", new BigDecimal(0));
        interestRateMap.put("AUTH_MONTHLY", new BigDecimal("10.00"));
        StODRates stODRates = stODRatesFactory.getStODRates(true, null, interestRateMap);
        assertEquals(new BigInteger("0"), stODRates.getNExcessFeeCap());
        assertEquals(new BigDecimal("0"), stODRates.getAmtIntFreeOverdraft());
        assertEquals("0100000", stODRates.getIntrateAuthMnthly());
    }

    @Test
    public void getStODRatesTestWithAuthMonthlyWithNonZeroDecimalPlaces() {
        Map<String, BigDecimal> interestRateMap = new HashMap<>();
        interestRateMap.put("INT_FREE_OVERDRAFT", new BigDecimal(0));
        interestRateMap.put("EXCESS_FEE_CAP", new BigDecimal(0));
        interestRateMap.put("AUTH_MONTHLY", new BigDecimal("10.20"));
        StODRates stODRates = stODRatesFactory.getStODRates(true, null, interestRateMap);
        assertEquals(new BigInteger("0"), stODRates.getNExcessFeeCap());
        assertEquals(new BigDecimal("0"), stODRates.getAmtIntFreeOverdraft());
        assertEquals("0102000", stODRates.getIntrateAuthMnthly());
    }
}
