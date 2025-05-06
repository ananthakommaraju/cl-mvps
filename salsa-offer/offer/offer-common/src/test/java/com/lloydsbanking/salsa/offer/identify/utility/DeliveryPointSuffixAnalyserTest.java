package com.lloydsbanking.salsa.offer.identify.utility;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import lib_sim_bo.businessobjects.PostalAddress;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class DeliveryPointSuffixAnalyserTest {

    private DeliveryPointSuffixAnalyser deliveryPointSuffixAnalyser;
    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        deliveryPointSuffixAnalyser = new DeliveryPointSuffixAnalyser();
        testDataHelper = new TestDataHelper();
    }

    @Test
    public void testIsDeliveryPointSuffixPresent() {
        boolean isDeliveryPoint = deliveryPointSuffixAnalyser.isDeliveryPointSuffixPresent(testDataHelper.createPostalAddressList());
        assertTrue(isDeliveryPoint);
    }

    @Test
    public void testIsDeliveryPointSuffixPresentWhenPaffIsFalse() {
        boolean isDeliveryPoint = deliveryPointSuffixAnalyser.isDeliveryPointSuffixPresent(testDataHelper.createPostalAddressListWhenPaffIsFalse());
        assertTrue(isDeliveryPoint);
    }

    @Test
    public void testDeliveryPointSuffixNotPresent() {
        List<PostalAddress> postalAddressList = new ArrayList<>();
        boolean isDeliveryPoint = deliveryPointSuffixAnalyser.isDeliveryPointSuffixPresent(postalAddressList);
        assertFalse(isDeliveryPoint);
    }
}
