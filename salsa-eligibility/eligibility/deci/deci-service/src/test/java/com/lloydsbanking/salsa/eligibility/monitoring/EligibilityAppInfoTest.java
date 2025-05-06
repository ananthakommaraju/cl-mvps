package com.lloydsbanking.salsa.eligibility.monitoring;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertNotNull;

@Category(UnitTest.class)
public class EligibilityAppInfoTest {
    EligibilityAppInfo eligibilityAppInfo;

    @Before
    public void setUp() {
        eligibilityAppInfo = new EligibilityAppInfo();
    }

    @Test
    public void testGetServiceNames() throws Exception {
        assertNotNull(eligibilityAppInfo.getServiceNames());

    }

    @Test
    public void testGetDatabaseNames() throws Exception {
        assertNotNull(eligibilityAppInfo.getDatabaseNames());

    }
}