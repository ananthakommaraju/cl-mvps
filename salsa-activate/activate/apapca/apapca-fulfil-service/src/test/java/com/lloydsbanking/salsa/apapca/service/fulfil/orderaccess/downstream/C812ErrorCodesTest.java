package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@Category(UnitTest.class)
public class C812ErrorCodesTest {

    @Test
    public void testExternalServiceError() {

        assertTrue(C812ErrorCodes.isExternalServiceErrorForC812(218012));
        assertFalse(C812ErrorCodes.isExternalServiceErrorForC812(1234));
    }

}
