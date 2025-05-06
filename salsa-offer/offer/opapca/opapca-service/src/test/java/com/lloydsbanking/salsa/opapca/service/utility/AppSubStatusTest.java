package com.lloydsbanking.salsa.opapca.service.utility;


import com.lloydsbanking.salsa.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.TestCase.assertEquals;

@Category(UnitTest.class)
public class AppSubStatusTest {

    @Test
    public void testAppSubStatus() {

        assertEquals("5002", AppSubStatus.ASM_REFER.getValue());
        assertEquals("5006", AppSubStatus.SIRA_DECLINE.getValue());
        assertEquals("5004", AppSubStatus.EIDV_REFER.getValue());
        assertEquals(AppSubStatus.SIRA_IDV_REFER, AppSubStatus.getAppSubtatus("5005"));
        assertEquals(AppSubStatus.SIRA_AND_ASM_REFER, AppSubStatus.getAppSubtatus("5003"));
        assertEquals(AppSubStatus.ASM_DECLINE, AppSubStatus.getAppSubtatus("5007"));

    }

}

