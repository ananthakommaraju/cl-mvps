package com.lloydsbanking.salsa.activate.postfulfil.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.soap.ocis.c658.objects.C658Req;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.DepositArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Category(UnitTest.class)
public class C658RequestFactoryTest {
    private C658RequestFactory c658RequestFactory;

    private TestDataHelper testDataHelper;

    private DepositArrangement depositArrangement;

    @Before
    public void setUp() {
        c658RequestFactory = new C658RequestFactory();
        testDataHelper = new TestDataHelper();
        depositArrangement = testDataHelper.createDepositArrangementResp();

    }

    @Test
    public void testConvertWithPrimaryCustomer() {
        depositArrangement.getPrimaryInvolvedParty().setCustomerIdentifier("123");
        depositArrangement.getPrimaryInvolvedParty().setCidPersID("1");
        C658Req c658Req = c658RequestFactory.convert(depositArrangement);
        assertEquals("1", c658Req.getExtPartyIdTx());
        assertEquals("123", c658Req.getPartyId().toString());
        assertEquals((short) 19, c658Req.getExtSysId());
    }

    @Test
    public void testConvertWithGuardianCustomer() {
        depositArrangement.setGuardianDetails(new Customer());
        depositArrangement.getGuardianDetails().setEmailAddress("abc@123.com");
        depositArrangement.getGuardianDetails().setCidPersID("1");
        depositArrangement.getGuardianDetails().setCustomerIdentifier("123");
        C658Req c658Req = c658RequestFactory.convert(depositArrangement);
        assertEquals("1", c658Req.getExtPartyIdTx());
        assertEquals("123", c658Req.getPartyId().toString());
        assertEquals((short) 19, c658Req.getExtSysId());
    }

    @Test
    public void testConvertWithPrimaryCustomerAndCustomerIdAsNull() {
        depositArrangement.getPrimaryInvolvedParty().setCustomerIdentifier("123");
        depositArrangement.getPrimaryInvolvedParty().setCidPersID("1");
        depositArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(null);
        C658Req c658Req = c658RequestFactory.convert(depositArrangement);
        assertEquals("1", c658Req.getExtPartyIdTx());
        assertNull(c658Req.getPartyId());
        assertEquals((short) 19, c658Req.getExtSysId());
    }

    @Test
    public void testConvertCustomerIdentifierBlank() {
        depositArrangement.getPrimaryInvolvedParty().setCustomerIdentifier("");
        depositArrangement.getPrimaryInvolvedParty().setCidPersID("1");
        C658Req c658Req = c658RequestFactory.convert(depositArrangement);
        assertEquals("1", c658Req.getExtPartyIdTx());
        assertNull(c658Req.getPartyId());
        assertEquals((short) 19, c658Req.getExtSysId());
    }
}
