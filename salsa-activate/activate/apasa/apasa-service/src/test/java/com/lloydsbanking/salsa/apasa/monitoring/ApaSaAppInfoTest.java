package com.lloydsbanking.salsa.apasa.monitoring;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertNotNull;

@Category(UnitTest.class)
public class ApaSaAppInfoTest {
    ApaSaAppInfo apaSaAppInfo;

    @Before
    public void setUp() {
        apaSaAppInfo = new ApaSaAppInfo();
    }

    @Test
    public void testGetServiceNames() throws Exception {
        assertNotNull(apaSaAppInfo.getDatabaseNames());
        assertNotNull(apaSaAppInfo.getServiceNames());

    }

}