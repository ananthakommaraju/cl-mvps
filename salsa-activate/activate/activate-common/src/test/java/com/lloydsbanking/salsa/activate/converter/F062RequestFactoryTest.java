package com.lloydsbanking.salsa.activate.converter;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Req;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.StructuredAddressType;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.UnstructuredAddressType;
import lib_sim_bo.businessobjects.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class F062RequestFactoryTest {

    private TestDataHelper testDataHelper;
    private F062RequestFactory f062RequestFactory;

    @Before
    public void setUp() {
        f062RequestFactory = new F062RequestFactory();
        testDataHelper = new TestDataHelper();
        f062RequestFactory.structuredAddressFactory = mock(StructuredAddressFactory.class);
        f062RequestFactory.unstructuredAddressFactory = mock(UnstructuredAddressFactory.class);
    }

    @Test
    public void testConvert() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangementForB750();
        Customer customer = depositArrangement.getPrimaryInvolvedParty();
        customer.getPostalAddress().get(0).setUnstructuredAddress(new UnstructuredAddress());
        customer.setApplicantType("10001");
        Individual individual = customer.getIsPlayedBy();
        individual.setCurrentEmployer(new Employer());
        individual.getCurrentEmployer().setName("asdadsdf");
        individual.getCurrentEmployer().getHasPostalAddress().add(new PostalAddress());
        individual.getCurrentEmployer().getHasPostalAddress().get(0).setUnstructuredAddress(testDataHelper.getUnstructuredAddress());
        when(f062RequestFactory.unstructuredAddressFactory.generateUnstructuredAddress(any(UnstructuredAddress.class), any(Boolean.class))).thenReturn(new UnstructuredAddressType());
        when(f062RequestFactory.structuredAddressFactory.generateStructuredAddress(any(StructuredAddress.class))).thenReturn(new StructuredAddressType());
        F062Req f062Req = f062RequestFactory.convert(customer, "CA", new AssessmentEvidence());
        assertNotNull(f062Req);
        assertNotNull(f062Req.getPartyUpdData());
    }

    @Test
    public void testConvertWithDefaults() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangementForB750();
        Customer customer = depositArrangement.getPrimaryInvolvedParty();
        customer.getPostalAddress().set(0, null);
        customer.getTelephoneNumber().add(new TelephoneNumber());
        customer.getTelephoneNumber().set(0, null);
        customer.setApplicantType("10001");
        Individual individual = customer.getIsPlayedBy();
        individual.setCurrentEmployer(new Employer());
        F062Req f062Req = f062RequestFactory.convert(customer, "CA", new AssessmentEvidence());
        assertNotNull(f062Req);
        assertEquals(0, f062Req.getMaxRepeatGroupQy());
        assertEquals("AbcdeFHI", f062Req.getPartyUpdData().getPersonalUpdData().getFirstForeNm());
    }

}
