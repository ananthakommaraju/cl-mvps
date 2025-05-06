package com.lloydsbanking.salsa.ppae.monitoring;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertNotNull;

@Category(UnitTest.class)
public class PpaeAppInfoTest {
    PpaeAppInfo ppaeAppInfo;

    @Before
    public void setUp() {
        ppaeAppInfo = new PpaeAppInfo();
    }

    @Test
    public void testGetServiceNames() throws Exception {
        assertNotNull(ppaeAppInfo.getDatabaseNames());
        assertNotNull(ppaeAppInfo.getServiceNames());

    }

}