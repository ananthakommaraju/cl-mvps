package com.lloydsbanking.salsa.activate.converter;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.UnstructuredAddressType;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category(UnitTest.class)
public class UnstructuredAddressFactoryTest {
    UnstructuredAddressFactory unstructuredAddressFactory;
    UnstructuredAddressType unstructuredAddressType;
    TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        unstructuredAddressFactory = new UnstructuredAddressFactory();
        unstructuredAddressType = null;
        testDataHelper = new TestDataHelper();
    }

    @Test
    public void testGenerateUnstructuredAddress() {
        unstructuredAddressType = unstructuredAddressFactory.generateUnstructuredAddress(testDataHelper.getUnstructuredAddress(), true);
        assertNotNull(unstructuredAddressType);
        assertEquals("lin1", unstructuredAddressType.getAddressLine1Tx());
        assertEquals("line2", unstructuredAddressType.getAddressLine2Tx());
        assertEquals("line3", unstructuredAddressType.getAddressLine3Tx());
        assertEquals("line4", unstructuredAddressType.getAddressLine4Tx());
    }

    @Test
    public void testGenerateUnstructuredAddressWithNullAddress() {
        unstructuredAddressType = unstructuredAddressFactory.generateUnstructuredAddress(null, true);
        assertNotNull(unstructuredAddressType);
        assertEquals(null, unstructuredAddressType.getAddressLine1Tx());
    }

    @Test
    public void testGenerateUnstructuredAddressWithFalseIndicator() {
        unstructuredAddressType = unstructuredAddressFactory.generateUnstructuredAddress(testDataHelper.getUnstructuredAddress(), false);
        assertNotNull(unstructuredAddressType);
        assertEquals("line3line2", unstructuredAddressType.getAddressLine1Tx());
        assertEquals("lin1line4", unstructuredAddressType.getAddressLine2Tx());
        assertEquals("line5", unstructuredAddressType.getAddressLine3Tx());
    }

    @Test
    public void testGenerateUnstructuredAddressWithFalseIndicatorAndNullAddress() {
        UnstructuredAddress unstructuredAddress = testDataHelper.getUnstructuredAddress();
        unstructuredAddress.setAddressLine1(null);
        unstructuredAddress.setAddressLine3(null);
        unstructuredAddress.setAddressLine5(null);
        unstructuredAddressType = unstructuredAddressFactory.generateUnstructuredAddress(unstructuredAddress, false);
        assertNotNull(unstructuredAddressType);
        assertEquals("line2", unstructuredAddressType.getAddressLine1Tx());
        assertEquals("line4", unstructuredAddressType.getAddressLine2Tx());
        assertEquals("line6", unstructuredAddressType.getAddressLine3Tx());
    }

    @Test
    public void testGenerateUnstructuredAddressWithFalseIndicatorAndAddress2Null() {
        UnstructuredAddress unstructuredAddress = testDataHelper.getUnstructuredAddress();
        unstructuredAddress.setAddressLine2(null);
        unstructuredAddressType = unstructuredAddressFactory.generateUnstructuredAddress(unstructuredAddress, false);
        assertNotNull(unstructuredAddressType);
        assertEquals("line3lin1line4", unstructuredAddressType.getAddressLine1Tx());
        assertEquals("line5", unstructuredAddressType.getAddressLine2Tx());
    }

    @Test
    public void testGenerateUnstructuredAddressWithFalseIndicatorAndAddress235Null() {
        UnstructuredAddress unstructuredAddress = testDataHelper.getUnstructuredAddress();
        unstructuredAddress.setAddressLine2(null);
        unstructuredAddress.setAddressLine3(null);
        unstructuredAddress.setAddressLine5(null);
        unstructuredAddressType = unstructuredAddressFactory.generateUnstructuredAddress(unstructuredAddress, false);
        assertNotNull(unstructuredAddressType);
        assertEquals("lin1line4", unstructuredAddressType.getAddressLine1Tx());
        assertEquals("line6", unstructuredAddressType.getAddressLine2Tx());
    }


}
