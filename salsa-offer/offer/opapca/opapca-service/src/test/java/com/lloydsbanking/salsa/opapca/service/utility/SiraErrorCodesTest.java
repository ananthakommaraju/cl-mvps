package com.lloydsbanking.salsa.opapca.service.utility;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

@Category(UnitTest.class)
public class SiraErrorCodesTest {
    @Test
    public void testSiraErrorCodes() {

        assertEquals("E50054", SiraErrorCodes.CRITICAL_SERVICE_RETURNED_ERROR.getSiraErrorCode());
        assertEquals("E50031", SiraErrorCodes.DUPLICATE_ITEM.getSiraErrorCode());
        assertEquals("E50037", SiraErrorCodes.DATA_PROCESSING_SERVICE_UNAVAILABLE.getSiraErrorCode());
        assertTrue(SiraErrorCodes.isErrorForSira("E50035"));
        assertFalse(SiraErrorCodes.isErrorForSira("E150042"));
    }
}
