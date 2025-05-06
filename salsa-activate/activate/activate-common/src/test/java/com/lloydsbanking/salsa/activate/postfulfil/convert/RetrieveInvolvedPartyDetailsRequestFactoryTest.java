package com.lloydsbanking.salsa.activate.postfulfil.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RetrieveInvolvedPartyDetailsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class RetrieveInvolvedPartyDetailsRequestFactoryTest {
    private RetrieveInvolvedPartyDetailsRequestFactory requestFactory;

    @Before
    public void setUp() {
        requestFactory = new RetrieveInvolvedPartyDetailsRequestFactory();
    }

    @Test
    public void testConvert() {
        RetrieveInvolvedPartyDetailsRequest request = requestFactory.convert("216248241");
        assertEquals("216248241", request.getInvolvedParty().getObjectReference().getIdentifier());
        assertEquals("19", request.getRequestHeader().getDatasourceName());
    }


}
