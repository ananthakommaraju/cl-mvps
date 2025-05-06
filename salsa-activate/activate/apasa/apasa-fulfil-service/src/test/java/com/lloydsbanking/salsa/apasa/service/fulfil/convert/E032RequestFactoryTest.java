package com.lloydsbanking.salsa.apasa.service.fulfil.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.soap.cbs.e032.objects.E032Req;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;

import static junit.framework.Assert.assertEquals;

@Category(UnitTest.class)
public class E032RequestFactoryTest {
    E032RequestFactory e032RequestFactory;

    @Before
    public void setUp() throws DatatypeConfigurationException {
        e032RequestFactory = new E032RequestFactory();
    }

    @Test
    public void testConvertWhenPaymentIdIsNotNull() throws DatatypeConfigurationException, ParseException {
        E032Req e032Req = e032RequestFactory.convert("779129", "24630368", "123","234","456");
        assertEquals(0, e032Req.getMaxRepeatGroupQy());
        assertEquals("77912924630368", e032Req.getCBSAccountNoId());
        assertEquals(0, e032Req.getCBSRequestGp2().getInputOfficerFlagStatusCd());
        assertEquals(1, e032Req.getCBSRequestGp2().getOverrideDetailsCd());
        E032Req e032Req1 = e032RequestFactory.convert("779129", null, "123",null,"345");
        assertEquals(null, e032Req1.getCBSAccountNoId());

    }


}
