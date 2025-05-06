package com.lloydsbanking.salsa.offer;


import com.lloydsbanking.salsa.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
@Category(UnitTest.class)
public class ProductEligibilityTypeTest {
    @Test
    public void testProductEligibilityType(){
        assertEquals("10001",ProductEligibilityType.NEW.getValue());
        assertEquals("10001",ProductEligibilityType.CO_HOLD.getValue());
        assertEquals("10002", ProductEligibilityType.TRADE.getValue());
        assertEquals("INELIGIBLE",ProductEligibilityType.INELIGIBLE.getValue());

        assertEquals("NEW", ProductEligibilityType.NEW.getKey());
        assertEquals("CO_HOLD", ProductEligibilityType.CO_HOLD.getKey());
        assertEquals("TRADE", ProductEligibilityType.TRADE.getKey());
        assertEquals("INELIGIBLE", ProductEligibilityType.INELIGIBLE.getKey());


        assertEquals("10001",ProductEligibilityType.getApplicationType("NEW"));
        assertEquals("10002",ProductEligibilityType.getApplicationType("TRADE"));
        assertEquals("INELIGIBLE",ProductEligibilityType.getApplicationType("KEY"));
    }
}
