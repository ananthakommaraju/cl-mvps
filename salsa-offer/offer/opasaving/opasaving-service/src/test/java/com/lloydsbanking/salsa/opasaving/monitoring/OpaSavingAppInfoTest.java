package com.lloydsbanking.salsa.opasaving.monitoring;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category(UnitTest.class)
public class OpaSavingAppInfoTest {
    OpaSavingAppInfo opasavingAppInfo;

    @Before
    public void setUp() {
        opasavingAppInfo = new OpaSavingAppInfo();
    }

    @Test
    public void testGetServiceNames() throws Exception {
        assertNotNull(opasavingAppInfo.getServiceNames());
        assertEquals(12, opasavingAppInfo.getServiceNames().size());
    }

    @Test
    public void testGetDatabaseNames() throws Exception {
        assertNotNull(opasavingAppInfo.getDatabaseNames());
        assertEquals(3, opasavingAppInfo.getDatabaseNames().size());
    }
}
