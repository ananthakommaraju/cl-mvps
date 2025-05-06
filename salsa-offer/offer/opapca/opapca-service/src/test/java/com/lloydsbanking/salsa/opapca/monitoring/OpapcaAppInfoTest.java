package com.lloydsbanking.salsa.opapca.monitoring;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category(UnitTest.class)
public class OpapcaAppInfoTest {
    OpapcaAppInfo opapcaAppInfo;

    @Before
    public void setUp() {
        opapcaAppInfo = new OpapcaAppInfo();
    }

    @Test
    public void testGetServiceNames() throws Exception {
        assertNotNull(opapcaAppInfo.getServiceNames());
        assertEquals(12, opapcaAppInfo.getServiceNames().size());

    }

    @Test
    public void testGetDatabaseNames() throws Exception {
        assertNotNull(opapcaAppInfo.getDatabaseNames());
        assertEquals(2, opapcaAppInfo.getDatabaseNames().size());
    }
}
