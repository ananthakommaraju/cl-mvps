package com.lloydsbanking.salsa.apasa.service.fulfil.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.soap.cbs.e502.objects.E502Req;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;

import static junit.framework.Assert.assertEquals;

@Category(UnitTest.class)
public class E502RequestFactoryTest {
    E502RequestFactory e502RequestFactory;

    @Before
    public void setUp() throws DatatypeConfigurationException {
        e502RequestFactory = new E502RequestFactory();
    }

    @Test
    public void testConvert() {
        E502Req e502Req = e502RequestFactory.convert("779129", "24630368");
        assertEquals(10, e502Req.getMaxRepeatGroupQy());
        assertEquals("779129", e502Req.getCBSAccountNoId());
        assertEquals("24630368", e502Req.getRollOverAccountNo());


    }
}
