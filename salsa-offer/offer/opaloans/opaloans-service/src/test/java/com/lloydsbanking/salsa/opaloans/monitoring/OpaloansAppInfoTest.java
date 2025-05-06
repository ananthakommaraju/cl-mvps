package com.lloydsbanking.salsa.opaloans.monitoring;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category(UnitTest.class)
public class OpaloansAppInfoTest {

    OpaloansAppInfo opaloansAppInfo;

    @Before
    public void setUp() {
        opaloansAppInfo = new OpaloansAppInfo();
    }

    @Test
    public void testGetServiceNames() throws Exception {
        assertNotNull(opaloansAppInfo.getServiceNames());
        assertEquals(9, opaloansAppInfo.getServiceNames().size());
    }

    @Test
    public void testGetDatabaseNames() throws Exception {
        assertNotNull(opaloansAppInfo.getDatabaseNames());
        assertEquals(3, opaloansAppInfo.getDatabaseNames().size());
    }
}
