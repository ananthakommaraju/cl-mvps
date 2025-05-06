package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.soap.cmas.c846.objects.C846Req;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertEquals;

@Category(UnitTest.class)
public class C846FactoryTest {
    @Test
    public void testCreateC846Request() {
        C846Req c846Req = C846Factory.createC846Request("0071776000", "1", "R", 50, Long.valueOf("123"));
        assertEquals(19, c846Req.getExtSysId());
        assertEquals("1", c846Req.getCCAApplicableIn());
    }
}
