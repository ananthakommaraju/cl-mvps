package com.lloydsbanking.salsa.opapca.service.utility;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.TestCase.assertEquals;

@Category(UnitTest.class)
public class SiraStatusTest {


    @Test
    public void testSiraStatus() {

        assertEquals("ACCEPT", SiraStatus.ACCEPT.getValue());
        assertEquals("REFER FRAUD", SiraStatus.REFER_FRAUD.getValue());
        assertEquals("REFER IDV", SiraStatus.REFER_IDV.getValue());
        assertEquals("DECLINE", SiraStatus.DECLINE.getValue());
        assertEquals(SiraStatus.ACCEPT, SiraStatus.getSiraDecision("ACCEPT"));
        assertEquals(SiraStatus.REFER_FRAUD, SiraStatus.getSiraDecision("REFER FRAUD"));
        assertEquals(SiraStatus.REFER_IDV, SiraStatus.getSiraDecision("REFER IDV"));

    }

}
