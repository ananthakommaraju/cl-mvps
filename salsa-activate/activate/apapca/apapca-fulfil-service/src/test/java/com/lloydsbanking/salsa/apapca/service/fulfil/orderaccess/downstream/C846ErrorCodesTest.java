package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@Category(UnitTest.class)
public class C846ErrorCodesTest {
    @Test
    public void testExternalServiceError() {
        assertTrue(C846ErrorCodes.isExternalServiceErrorForC846(218980));
        assertFalse(C846ErrorCodes.isExternalServiceErrorForC846(1234));
    }
}
