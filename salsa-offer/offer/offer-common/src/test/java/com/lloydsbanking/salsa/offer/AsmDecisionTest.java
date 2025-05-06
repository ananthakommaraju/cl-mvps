package com.lloydsbanking.salsa.offer;


import com.lloydsbanking.salsa.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
@Category(UnitTest.class)
public class AsmDecisionTest {

    @Test
    public void testAsmDecision(){

        assertEquals("1",AsmDecision.APPROVED.getValue());
        assertEquals("2",AsmDecision.REFERRED.getValue());
        assertEquals("3",AsmDecision.DECLINED.getValue());

        assertEquals(AsmDecision.APPROVED, AsmDecision.getAsmDecision("1"));
        assertEquals(AsmDecision.REFERRED, AsmDecision.getAsmDecision("2"));
        assertEquals(AsmDecision.DECLINED, AsmDecision.getAsmDecision("3"));

    }

}
