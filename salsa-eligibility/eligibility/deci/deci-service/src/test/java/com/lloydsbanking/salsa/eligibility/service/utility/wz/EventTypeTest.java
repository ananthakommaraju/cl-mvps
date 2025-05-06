package com.lloydsbanking.salsa.eligibility.service.utility.wz;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EventTypeTest {
    @Test
    public void testGetEvenTypeFromString() {
        EventType evtType = EventType.valueOf("B040");
        assertEquals("37", evtType.asString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEvenTypeFromStringWhenEventTypeNotAvailable() {
        String evtType = EventType.valueOf("").asString();
    }
}
