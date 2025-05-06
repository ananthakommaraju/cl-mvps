package com.lloydsbanking.salsa.apacc.monitoring;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertNotNull;

@Category(UnitTest.class)
public class ApaCcAppInfoTest {
    ApaCcAppInfo apaCcAppInfo;

    @Before
    public void setUp() {
        apaCcAppInfo = new ApaCcAppInfo();
    }

    @Test
    public void testGetServiceNames() throws Exception {
        assertNotNull(apaCcAppInfo.getDatabaseNames());
        assertNotNull(apaCcAppInfo.getServiceNames());

    }

}