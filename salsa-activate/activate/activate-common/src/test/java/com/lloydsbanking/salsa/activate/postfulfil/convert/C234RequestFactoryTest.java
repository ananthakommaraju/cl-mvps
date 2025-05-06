package com.lloydsbanking.salsa.activate.postfulfil.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.soap.ocis.c234.objects.C234Req;
import lib_sim_bo.businessobjects.Customer;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertEquals;


@Category(UnitTest.class)
public class C234RequestFactoryTest {
    private C234RequestFactory c234RequestFactory;
    private TestDataHelper testDataHelper;
    private Customer customer;

    @Before
    public void setUp() {
        c234RequestFactory = new C234RequestFactory();
        testDataHelper = new TestDataHelper();
        customer = testDataHelper.createDepositArrangement("123").getPrimaryInvolvedParty();
    }

    @Test
    public void testConvert() {
        C234Req c234Req = c234RequestFactory.convert(customer);
        assertEquals((short) 0, c234Req.getDetailedPartyInfo().getExtSysId());
        assertEquals((short) 19, c234Req.getExtSysId());
        assertEquals("12", c234Req.getDetailedPartyInfo().getNationalInsNo());
    }
}
