package com.lloydsbanking.salsa.activate.retrieve;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertNull;

@Category(UnitTest.class)
public class RetrievePendingArrangementServiceHelperTest {
    RetrievePendingArrangementServiceHelper retrievePendingArrangementServiceHelper;
    TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        retrievePendingArrangementServiceHelper = new RetrievePendingArrangementServiceHelper();
        testDataHelper = new TestDataHelper();
    }

    @Test
    public void testCommonPamDetails() {
        ProductArrangement productArrangement = testDataHelper.createProductArrangement();
        ProductArrangement upstreamProductArrangement = testDataHelper.createDepositArrangement();
        productArrangement.setApplicationType("CA");
        retrievePendingArrangementServiceHelper.setCommonPamDetailsForGalaxy(upstreamProductArrangement, productArrangement);
        assertNull(upstreamProductArrangement.getApplicationStatus());
        Assert.assertEquals("CA", upstreamProductArrangement.getApplicationType());
    }
}
