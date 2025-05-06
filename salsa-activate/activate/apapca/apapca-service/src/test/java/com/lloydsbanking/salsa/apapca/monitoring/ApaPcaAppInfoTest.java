package com.lloydsbanking.salsa.apapca.monitoring;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertNotNull;

@Category(UnitTest.class)
public class ApaPcaAppInfoTest {
    ApaPcaAppInfo apaPcaAppInfo;

    @Before
    public void setUp() {
        apaPcaAppInfo = new ApaPcaAppInfo();
    }

    @Test
    public void testGetServiceNames() throws Exception {
        assertNotNull(apaPcaAppInfo.getDatabaseNames());
        assertNotNull(apaPcaAppInfo.getServiceNames());

    }

}