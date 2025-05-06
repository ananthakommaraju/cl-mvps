package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@Category(UnitTest.class)
public class C818ErrorCodesTest {
    @Test
    public void testExternalServiceError() {

        assertTrue(C818ErrorCodes.isExternalServiceErrorForC818(218118));
        assertFalse(C818ErrorCodes.isExternalServiceErrorForC818(1234));
    }
}
