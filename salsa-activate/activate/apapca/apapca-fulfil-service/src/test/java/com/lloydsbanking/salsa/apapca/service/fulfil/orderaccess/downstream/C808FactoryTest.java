package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.soap.cmas.c808.objects.C808Req;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertEquals;

@Category(UnitTest.class)
public class C808FactoryTest {
    @Test
    public void testCreateC808Request() {
        C808Req c808Req = C808Factory.createC808Request("112345", "1100001156", Long.valueOf("123"));
        assertEquals(19, c808Req.getExtSysId());
        assertEquals("112345", c808Req.getSortCd());
    }
}
