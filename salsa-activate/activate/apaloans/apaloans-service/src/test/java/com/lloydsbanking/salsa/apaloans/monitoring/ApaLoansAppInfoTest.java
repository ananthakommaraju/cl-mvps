package com.lloydsbanking.salsa.apaloans.monitoring;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertNotNull;

@Category(UnitTest.class)
public class ApaLoansAppInfoTest {
    ApaLoansAppInfo apaLoansAppInfo;

    @Before
    public void setUp() {
        apaLoansAppInfo = new ApaLoansAppInfo();
    }

    @Test
    public void testGetServiceNames() throws Exception {
        assertNotNull(apaLoansAppInfo.getDatabaseNames());
        assertNotNull(apaLoansAppInfo.getServiceNames());

    }

}