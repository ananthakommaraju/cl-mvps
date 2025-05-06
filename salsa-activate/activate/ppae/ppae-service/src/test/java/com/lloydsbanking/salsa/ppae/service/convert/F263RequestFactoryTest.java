package com.lloydsbanking.salsa.ppae.service.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import com.lloydsbanking.salsa.soap.pad.f263.objects.F263Req;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.CustomerScore;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class F263RequestFactoryTest {

    F263RequestFactory f263RequestFactory;
    TestDataHelper testDataHelper;
    F263Req f263Req;
    Customer customer;

    @Before
    public void setUp() throws DatatypeConfigurationException {
        f263RequestFactory = new F263RequestFactory();
        testDataHelper = new TestDataHelper();
        f263Req = new F263Req();
        customer = new Customer();
    }

    @Test
    public void testGetLoanDetailsRequestForPpae() {
        customer.getCustomerScore().add(new CustomerScore());
        customer.getCustomerScore().get(0).setScoreIdentifier("9");
        f263Req = f263RequestFactory.createF263Req(customer);
        assertEquals(customer.getCustomerScore().get(0).getScoreIdentifier(), f263Req.getAIdentifiers().getRequestNo());
        assertEquals("009", f263Req.getAIdentifiers().getSourceSystemCd());
        assertEquals("0", f263Req.getAIdentifiers().getLoanAgreementNo());
    }

    @Test
    public void testGetLoanDetailsRequestForPpaeForNullCustomerScore() {
        f263Req = f263RequestFactory.createF263Req(customer);
        assertEquals(null, f263Req.getAIdentifiers().getRequestNo());
        assertEquals("009", f263Req.getAIdentifiers().getSourceSystemCd());
        assertEquals("0", f263Req.getAIdentifiers().getLoanAgreementNo());
    }

    @Test
    public void testGetLoanDetailsRequestForPpaeForNullCustomer() {

        f263Req = f263RequestFactory.createF263Req(null);
        assertEquals(null, f263Req.getAIdentifiers().getRequestNo());
        assertEquals("009", f263Req.getAIdentifiers().getSourceSystemCd());
        assertEquals("0", f263Req.getAIdentifiers().getLoanAgreementNo());
    }

    @Test
    public void testGetLoanDetailsRequestForPpaeForNullCustomerScoreElement() {
        customer.getCustomerScore().add(null);
        f263Req = f263RequestFactory.createF263Req(null);
        assertEquals(null, f263Req.getAIdentifiers().getRequestNo());
        assertEquals("009", f263Req.getAIdentifiers().getSourceSystemCd());
        assertEquals("0", f263Req.getAIdentifiers().getLoanAgreementNo());
    }
}

