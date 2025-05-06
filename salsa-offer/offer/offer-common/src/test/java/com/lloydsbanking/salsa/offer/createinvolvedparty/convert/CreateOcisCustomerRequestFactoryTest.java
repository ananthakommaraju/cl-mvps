package com.lloydsbanking.salsa.offer.createinvolvedparty.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.offer.createinvolvedparty.evaluate.EvaluateAddressUpdData;
import com.lloydsbanking.salsa.offer.createinvolvedparty.evaluate.EvaluatePersonalUpdData;
import com.lloydsbanking.salsa.offer.createinvolvedparty.evaluate.EvaluateStrength;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Req;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CreateOcisCustomerRequestFactoryTest {

    CreateOcisCustomerRequestFactory createOcisCustomerRequestFactory;

    @Before
    public void setUp() {
        createOcisCustomerRequestFactory = new CreateOcisCustomerRequestFactory();
        createOcisCustomerRequestFactory.employerAddressDataFactory = mock(EmployerAddressDataFactory.class);
        createOcisCustomerRequestFactory.evaluateAddressUpdData = mock(EvaluateAddressUpdData.class);
        createOcisCustomerRequestFactory.evaluatePersonalUpdData = mock(EvaluatePersonalUpdData.class);
        createOcisCustomerRequestFactory.evaluateStrength = mock(EvaluateStrength.class);
        createOcisCustomerRequestFactory.exceptionUtility = mock(ExceptionUtility.class);
        createOcisCustomerRequestFactory.partyNonCoreUpdDataFactory = mock(PartyNonCoreUpdDataFactory.class);
        createOcisCustomerRequestFactory.phoneUpdDataFactory = mock(PhoneUpdDataFactory.class);
    }

    @Test
    public void testConvert() throws ParseException, InternalServiceErrorMsg {
        Customer customer = new Customer();
        customer.setCustomerIdentifier("1001");
        customer.setIsPlayedBy(new Individual());
        customer.getIsPlayedBy().setCurrentEmployer(new Employer());
        customer.getIsPlayedBy().getCurrentEmployer().setName("ANkush");
        customer.getCustomerScore().add(new CustomerScore());
        customer.getCustomerScore().get(0).setScoreResult("ACCEPT");
        PostalAddress postalAddress=new PostalAddress();
        postalAddress.setUnstructuredAddress(new UnstructuredAddress());
        postalAddress.setStructuredAddress(new StructuredAddress());
        customer.getIsPlayedBy().getCurrentEmployer().getHasPostalAddress().add(postalAddress);
        F062Req f062Req=createOcisCustomerRequestFactory.convert("CA", customer, true, "LTB");
        assertNotNull(f062Req);
        assertEquals(0,f062Req.getMaxRepeatGroupQy());
        assertEquals("1001",f062Req.getPartyId().toString());
    }

    @Test
    public void testConvertWithEmptyCustomerIdentifier() throws ParseException, InternalServiceErrorMsg {
        Customer customer = new Customer();
        customer.setCustomerIdentifier("");
        customer.setIsPlayedBy(new Individual());
        customer.getIsPlayedBy().setCurrentEmployer(new Employer());
        customer.getIsPlayedBy().getCurrentEmployer().setName("ANkush");
        customer.getCustomerScore().add(new CustomerScore());
        customer.getCustomerScore().get(0).setScoreResult("ACCEPT");
        PostalAddress postalAddress=new PostalAddress();
        postalAddress.setUnstructuredAddress(new UnstructuredAddress());
        postalAddress.setStructuredAddress(new StructuredAddress());
        customer.getIsPlayedBy().getCurrentEmployer().getHasPostalAddress().add(postalAddress);
        F062Req f062Req=createOcisCustomerRequestFactory.convert("CA", customer, true, "LTB");
        assertNotNull(f062Req);
        assertEquals(0,f062Req.getMaxRepeatGroupQy());
        assertNull(f062Req.getPartyId());
    }

    @Test(expected=InternalServiceErrorMsg.class)
    public void testConvertWithException() throws ParseException, InternalServiceErrorMsg, DataNotAvailableErrorMsg {
        Customer customer = new Customer();
        customer.setCustomerIdentifier("1001");
        customer.setIsPlayedBy(new Individual());
        customer.getIsPlayedBy().setCurrentEmployer(new Employer());
        customer.getIsPlayedBy().getCurrentEmployer().setName("ANkush");
        customer.getCustomerScore().add(new CustomerScore());
        customer.getCustomerScore().get(0).setScoreResult("ACCEPT");
        PostalAddress postalAddress=new PostalAddress();
        postalAddress.setUnstructuredAddress(new UnstructuredAddress());
        postalAddress.setStructuredAddress(new StructuredAddress());
        customer.getIsPlayedBy().getCurrentEmployer().getHasPostalAddress().add(postalAddress);
        when(createOcisCustomerRequestFactory.evaluateStrength.fetchAddressAndPartyEvidenceAndPurposeCode(any(CustomerScore.class), any(String.class))).thenThrow(DataNotAvailableErrorMsg.class);
        when(createOcisCustomerRequestFactory.exceptionUtility.internalServiceError(any(String.class),any(String.class))).thenThrow(InternalServiceErrorMsg.class);
        createOcisCustomerRequestFactory.convert("CA", customer, true, "LTB");
    }

    @Test
    public void testConvertWithNullCustomerEmployer() throws ParseException, InternalServiceErrorMsg {
        Customer customer = new Customer();
        customer.setCustomerIdentifier("1001");
        customer.setIsPlayedBy(new Individual());
        customer.getCustomerScore().add(new CustomerScore());
        customer.getCustomerScore().get(0).setScoreResult("ACCEPT");
        F062Req f062Req=createOcisCustomerRequestFactory.convert("CA", customer, true, "LTB");
        assertNotNull(f062Req);
        assertEquals(0,f062Req.getMaxRepeatGroupQy());
        assertEquals("1001",f062Req.getPartyId().toString());
    }

    @Test
    public void testConvertWithNullName() throws ParseException, InternalServiceErrorMsg {
        Customer customer = new Customer();
        customer.setCustomerIdentifier("1001");
        customer.setIsPlayedBy(new Individual());
        customer.getIsPlayedBy().setCurrentEmployer(new Employer());
        customer.getCustomerScore().add(new CustomerScore());
        customer.getCustomerScore().get(0).setScoreResult("2");
        PostalAddress postalAddress=new PostalAddress();
        postalAddress.setUnstructuredAddress(new UnstructuredAddress());
        postalAddress.setStructuredAddress(new StructuredAddress());
        customer.getIsPlayedBy().getCurrentEmployer().getHasPostalAddress().add(postalAddress);
        createOcisCustomerRequestFactory.convert("CA", customer, true, "LTB");
    }

}
