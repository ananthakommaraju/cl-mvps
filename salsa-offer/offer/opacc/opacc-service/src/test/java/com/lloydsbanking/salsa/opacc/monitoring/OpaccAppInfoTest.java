package com.lloydsbanking.salsa.opacc.monitoring;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category(UnitTest.class)
public class OpaccAppInfoTest {
    OpaccAppInfo opaccAppInfo;

    @Before
    public void setUp() {
        opaccAppInfo = new OpaccAppInfo();
    }

    @Test
    public void testGetServiceNames() throws Exception {
        assertNotNull(opaccAppInfo.getServiceNames());
        assertEquals(12, opaccAppInfo.getServiceNames().size());
    }

    @Test
    public void testGetDatabaseNames() throws Exception {
        assertNotNull(opaccAppInfo.getDatabaseNames());
        assertEquals(3, opaccAppInfo.getDatabaseNames().size());
    }
}
