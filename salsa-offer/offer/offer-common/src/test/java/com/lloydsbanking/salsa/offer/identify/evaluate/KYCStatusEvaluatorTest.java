package com.lloydsbanking.salsa.offer.identify.evaluate;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.*;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.Individual;
import lib_sim_bo.businessobjects.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class KYCStatusEvaluatorTest {

    KYCStatusEvaluator kycStatusEvaluator;


    @Before
    public void setUp() {
        kycStatusEvaluator = new KYCStatusEvaluator();
        kycStatusEvaluator.dateFactory = new DateFactory();
    }

    @Test
    public void testIsKycCompliant() throws ParseException, DatatypeConfigurationException {

        List<Product> productList = new ArrayList<>();
        Product product = new Product();
        product.setBrandName("LTB");
        product.setStatusCode("001");
        productList.add(product);

        Date sampleDate = (new SimpleDateFormat("yyyyMMdd")).parse("20101010");
        GregorianCalendar gregory = new GregorianCalendar();
        gregory.setTime(sampleDate);
        XMLGregorianCalendar customerOcisDob = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory);

        Date sampleDate1 = (new SimpleDateFormat("yyyyMMdd")).parse("20101010");
        GregorianCalendar gregory1 = new GregorianCalendar();
        gregory1.setTime(sampleDate1);
        XMLGregorianCalendar customerRequestDob = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory);

        Customer customer = new Customer();
        Individual individual = new Individual();
        individual.setBirthDate(customerRequestDob);
        customer.setIsPlayedBy(individual);


        PartyEnqData partyEnqData = new PartyEnqData();
        PersonalData personalData = new PersonalData();
        personalData.setBirthDt("10102010");
        partyEnqData.setPersonalData(personalData);

        EvidenceData evidenceData = new EvidenceData();
        AddrEvid addrEvid = new AddrEvid();
        addrEvid.setAddrEvidTypeCd("Code");
        evidenceData.getAddrEvid().add(addrEvid);
        PartyEvid partyEvid = new PartyEvid();
        partyEvid.setPartyEvidTypeCd("code");
        evidenceData.getPartyEvid().add(partyEvid);
        partyEnqData.setEvidenceData(evidenceData);


        boolean isKycCompliant = kycStatusEvaluator.isKycCompliant(customer, productList, partyEnqData);
        assertTrue(isKycCompliant);

    }

    @Test
    public void testIsNotKycCompliant() {

        Customer customer = new Customer();
        customer.setIsPlayedBy(new Individual());
        List<Product> productHoldings = new ArrayList<>();
        PartyEnqData partyEnqData = null;

        boolean isKycCompliant = kycStatusEvaluator.isKycCompliant(customer, productHoldings, partyEnqData);

        assertFalse(isKycCompliant);
    }

    @Test
    public void testIsNotKycAndNoValidHoldingExists() throws ParseException, DatatypeConfigurationException {

        List<Product> productList = new ArrayList<>();
        Product product = new Product();
        product.setBrandName("LTB");
        product.setStatusCode("002");
        productList.add(product);

        Date sampleDate = (new SimpleDateFormat("yyyyMMdd")).parse("20101010");
        GregorianCalendar gregory = new GregorianCalendar();
        gregory.setTime(sampleDate);
        XMLGregorianCalendar customerOcisDob = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory);

        Date sampleDate1 = (new SimpleDateFormat("yyyyMMdd")).parse("20101010");
        GregorianCalendar gregory1 = new GregorianCalendar();
        gregory1.setTime(sampleDate1);
        XMLGregorianCalendar customerRequestDob = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory);

        Customer customer = new Customer();
        Individual individual = new Individual();
        individual.setBirthDate(customerRequestDob);
        customer.setIsPlayedBy(individual);


        PartyEnqData partyEnqData = new PartyEnqData();
        PersonalData personalData = new PersonalData();
        personalData.setBirthDt("10102010");
        partyEnqData.setPersonalData(personalData);

        EvidenceData evidenceData = new EvidenceData();
        AddrEvid addrEvid = new AddrEvid();
        addrEvid.setAddrEvidTypeCd("Code");
        evidenceData.getAddrEvid().add(addrEvid);
        PartyEvid partyEvid = new PartyEvid();
        partyEvid.setPartyEvidTypeCd("code");
        evidenceData.getPartyEvid().add(partyEvid);
        partyEnqData.setEvidenceData(evidenceData);

        boolean isKycCompliant = kycStatusEvaluator.isKycCompliant(customer, productList, partyEnqData);

        assertFalse(isKycCompliant);
    }

    @Test
    public void testIsNotKycAndDobDoesNotMatch() throws ParseException, DatatypeConfigurationException {

        kycStatusEvaluator.dateFactory = mock(DateFactory.class);
        List<Product> productList = new ArrayList<>();
        Product product = new Product();
        product.setBrandName("LTB");
        product.setStatusCode("001");
        productList.add(product);

        Date sampleDate = (new SimpleDateFormat("yyyyMMdd")).parse("20101010");
        GregorianCalendar gregory = new GregorianCalendar();
        gregory.setTime(sampleDate);
        XMLGregorianCalendar customerOcisDob = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory);

        Date sampleDate1 = (new SimpleDateFormat("yyyyMMdd")).parse("20191010");
        GregorianCalendar gregory1 = new GregorianCalendar();
        gregory1.setTime(sampleDate1);
        XMLGregorianCalendar customerRequestDob = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory);

        Customer customer = new Customer();
        Individual individual = new Individual();
        individual.setBirthDate(customerRequestDob);
        customer.setIsPlayedBy(individual);

        PartyEnqData partyEnqData = new PartyEnqData();
        PersonalData personalData = new PersonalData();
        personalData.setBirthDt("10102010");
        partyEnqData.setPersonalData(personalData);

        EvidenceData evidenceData = new EvidenceData();
        AddrEvid addrEvid = new AddrEvid();
        addrEvid.setAddrEvidTypeCd("Code");
        evidenceData.getAddrEvid().add(addrEvid);
        PartyEvid partyEvid = new PartyEvid();
        partyEvid.setPartyEvidTypeCd("code");
        evidenceData.getPartyEvid().add(partyEvid);
        partyEnqData.setEvidenceData(evidenceData);

        when(kycStatusEvaluator.dateFactory.stringToXMLGregorianCalendar(any(String.class), any(SimpleDateFormat.class))).thenReturn(customerOcisDob);

        when(kycStatusEvaluator.dateFactory.differenceInDays(any(Date.class), any(Date.class))).thenReturn(2l);

        boolean isKycCompliant = kycStatusEvaluator.isKycCompliant(customer, productList, partyEnqData);

        assertFalse(isKycCompliant);
    }
}
