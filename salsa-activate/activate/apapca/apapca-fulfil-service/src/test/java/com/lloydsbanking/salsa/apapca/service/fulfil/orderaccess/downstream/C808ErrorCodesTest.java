package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@Category(UnitTest.class)
public class C808ErrorCodesTest {
    @Test
    public void testExternalServiceError() {

        assertTrue(C808ErrorCodes.isExternalErrorForC808(218008));
        assertFalse(C808ErrorCodes.isExternalErrorForC808(1234));
    }

}
