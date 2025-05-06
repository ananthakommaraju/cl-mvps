
package com.lloydsbanking.salsa.eligibility.service.converter;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.soap.cbs.e220.objects.E220Req;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertEquals;

@Category(UnitTest.class)
public class CbsRequestFactoryTest {
    CbsRequestFactory cbsRequestFactory = new CbsRequestFactory();

    @Test
    public void createE220RequestTest() {
        E220Req e220Req = cbsRequestFactory.createE220Request("770908", "101");
        assertEquals("101", e220Req.getCustNoGp().getCBSCustNo());
        assertEquals("77",e220Req.getCustNoGp().getNationalSortcodeId());
    }
    @Test
    public void createE220RequestTestWithMoreCustLengthId() {
        E220Req e220Req = cbsRequestFactory.createE220Request("770908", "1234567891234");
        assertEquals("34567891234", e220Req.getCustNoGp().getCBSCustNo());
        assertEquals("77",e220Req.getCustNoGp().getNationalSortcodeId());

    }

    @Test
    public void createE220RequestWZTest() {
        String customerId = "101";

        E220Req e220Req = cbsRequestFactory.createE220Request(null, customerId);
        assertEquals(10, e220Req.getMaxRepeatGroupQy());
        assertEquals("1", e220Req.getCustNoGp().getCBSCustNo());

    }

}
