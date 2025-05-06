package com.lloydsbanking.salsa.activate.converter;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.soap.fs.account.StAddress;
import lib_sim_bo.businessobjects.StructuredAddress;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class ObtainAddressProductAccountAndTariffTest {

    ObtainAddressProductAccountAndTariff obtainAddressandTariff;
    UnstructuredAddress unstructuredAddress;
    StructuredAddress structuredAddress;
    TestDataHelper testDataHelper;

    @Before
    public void setUp() {

        testDataHelper = new TestDataHelper();
        obtainAddressandTariff = new ObtainAddressProductAccountAndTariff();
        structuredAddress = testDataHelper.getStructureAddress();
        unstructuredAddress = testDataHelper.getUnstructuredAddress();
    }

    @Test
    public void testAccountTariff() {

        String tariffType = obtainAddressandTariff.getAccountTariff(1);
        assertEquals("1YTRF", tariffType);

        tariffType = obtainAddressandTariff.getAccountTariff(2);
        assertEquals("2YTRF", tariffType);

        tariffType = obtainAddressandTariff.getAccountTariff(3);
        assertEquals("3YTRF", tariffType);

        tariffType = obtainAddressandTariff.getAccountTariff(4);
        assertEquals("4YTRF", tariffType);

        tariffType = obtainAddressandTariff.getAccountTariff(5);
        assertEquals("5YTRF", tariffType);

        tariffType = obtainAddressandTariff.getAccountTariff(6);
        assertEquals("6YTRF", tariffType);

        tariffType = obtainAddressandTariff.getAccountTariff(7);
        assertEquals("NYTRF", tariffType);
    }

    @Test
    public void testStructureAddress() {
        StAddress b765StructureAddress = obtainAddressandTariff.getStructureAddress(structuredAddress);
        assertEquals("15    23", b765StructureAddress.getPostcode());
    }

    @Test
    public void testUnstructuredAddress() {
        StAddress b765StructureAddress = obtainAddressandTariff.getUnstructuredAddress(unstructuredAddress);
        assertEquals("postc ode", b765StructureAddress.getPostcode());
    }

    @Test
    public void testGetProdAccWhenSortCodeIsNull() {
        List<String> productAcc = obtainAddressandTariff.getProdAcc(null, "0071001", "CA");
        assertEquals("0071", productAcc.get(1));
        assertEquals("1", productAcc.get(0));
    }

    @Test
    public void testGetProdAccWhenSortCodeStartsWithTsbWithOldId() {
        List<String> productAcc = obtainAddressandTariff.getProdAcc("8787", "0071001", "CA");
        assertEquals("449", productAcc.get(1));
        assertEquals("1", productAcc.get(0));
    }

    @Test
    public void testGetProdAccWhenSortCodeStartsWithTsbWithNewId() {
        List<String> productAcc = obtainAddressandTariff.getProdAcc("8787", "2071001", "CA");
        assertEquals("2449", productAcc.get(1));
        assertEquals("1", productAcc.get(0));
    }

    @Test
    public void testGetProdAccWhenSortCodeStartsWithTsbWithDefaultId() {
        List<String> productAcc = obtainAddressandTariff.getProdAcc("8787", "1234001", "CA");
        assertEquals("1234", productAcc.get(1));
        assertEquals("1", productAcc.get(0));
    }
}
