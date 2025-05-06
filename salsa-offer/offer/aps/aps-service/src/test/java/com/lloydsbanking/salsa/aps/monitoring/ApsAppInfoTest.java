package com.lloydsbanking.salsa.aps.monitoring;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category(UnitTest.class)
public class ApsAppInfoTest {

    ApsAppInfo apsAppInfo;

    @Before
    public void setUp() throws Exception {
        apsAppInfo = new ApsAppInfo();
    }

    @Test
    public void testGetServiceNames() throws Exception {
        assertNotNull(apsAppInfo.getServiceNames());
    }

    @Test
    public void testGetDatabaseNames() throws Exception {
        assertNotNull(apsAppInfo.getDatabaseNames());
        assertEquals(1,apsAppInfo.getDatabaseNames().size());
    }
}