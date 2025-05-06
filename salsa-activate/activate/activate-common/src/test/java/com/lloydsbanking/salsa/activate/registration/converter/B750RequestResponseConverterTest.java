package com.lloydsbanking.salsa.activate.registration.converter;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.brand.Brand;
import com.lloydsbanking.salsa.downstream.application.converter.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.fs.application.StHeader;
import com.lloydstsb.ib.wsbridge.application.StB750AAppPerCCRegCreate;
import com.lloydstsb.ib.wsbridge.application.StB750BAppPerCCRegCreate;
import com.lloydstsb.schema.enterprise.lcsm.BAPIHeader;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;


@Category(UnitTest.class)
public class B750RequestResponseConverterTest {

    B750RequestResponseConverter b750RequestResponseConverter;
    TestDataHelper dataHelper;

    DepositArrangement depositArrangement;
    HeaderRetriever headerRetriever = new HeaderRetriever();
    BAPIHeader bapiHeader;

    ServiceRequest serviceRequest;

    StHeader stHeader;

    ContactPoint contactPoint;

    @Before
    public void setUp() {
        b750RequestResponseConverter = new B750RequestResponseConverter();
        b750RequestResponseConverter.bapiHeaderToStHeaderConverter = mock(BapiHeaderToStHeaderConverter.class);

        b750RequestResponseConverter.headerRetriever = headerRetriever;
        dataHelper = new TestDataHelper();
        depositArrangement = dataHelper.createDepositArrangementAfterPAMCall();
        bapiHeader = (headerRetriever.getBapiInformationHeader(dataHelper.createApaRequestHeader())).getBAPIHeader();
        serviceRequest = headerRetriever.getServiceRequest(dataHelper.createApaRequestHeader());
        contactPoint = headerRetriever.getContactPoint(dataHelper.createApaRequestHeader());
        stHeader = b750RequestResponseConverter.bapiHeaderToStHeaderConverter.convertSalesUnauthHeader(bapiHeader, serviceRequest, contactPoint.getContactPointId(), new BigInteger("137178748"), Brand.fromString("LTB"));
    }

    @Test
    public void testCreateB750Request() {
        StB750AAppPerCCRegCreate b750Request = b750RequestResponseConverter.createB750Request(dataHelper.createApaRequestHeader(), depositArrangement.getPrimaryInvolvedParty(), null, 0, "C");
        assertEquals(stHeader, b750Request.getStheader());
        assertEquals("C", b750Request.getStacc().getProdtype());
        assertEquals("Fghi", b750Request.getSurname());
        assertEquals("AbcdeFHI", b750Request.getFirstname());
        assertEquals("SE19EQ", b750Request.getPostcode());
        assertEquals("****", b750Request.getPwdEmergingChannel());
        assertEquals("N", b750Request.getPbktyp());
        assertEquals("Mr", b750Request.getTitle());
        assertEquals(createXMLGregorianCalendar(1935, 01, 01), b750Request.getDateOfBirth());
        assertEquals("GalaxyTestAccount02@LloydsTSB.co.uk", b750Request.getEmailaddr());
        assertEquals(0, b750Request.getMktgindEmail());
    }

    @Test
    public void testMapB750ResponseAttributesToProductArrangement() {
        b750RequestResponseConverter.mapB750ResponseAttributesToProductArrangement(depositArrangement.getPrimaryInvolvedParty(), dataHelper.createB750Response());
        assertEquals("0", depositArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().getApplicationVersion());
        assertEquals("670779965", depositArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().getRegistrationIdentifier());
    }

    @Test
    public void testCreateB750RequestWhenAddressIsNotPAFFormat() {
        Customer primaryInvolvedParty = depositArrangement.getPrimaryInvolvedParty();
        primaryInvolvedParty.getPostalAddress().get(0).setIsPAFFormat(false);
        StB750AAppPerCCRegCreate b750Request = b750RequestResponseConverter.createB750Request(dataHelper.createApaRequestHeader(), primaryInvolvedParty, null, 0, "C");
        assertEquals(stHeader, b750Request.getStheader());
        assertEquals("C", b750Request.getStacc().getProdtype());
        assertEquals("Fghi", b750Request.getSurname());
        assertEquals("AbcdeFHI", b750Request.getFirstname());
        assertEquals("", b750Request.getPostcode());
    }

    @Test
    public void testCreateB750RequestWhenAddressIsNotPAFFormatAndUnstructuredAddressIsPresent() {
        Customer primaryInvolvedParty = depositArrangement.getPrimaryInvolvedParty();
        primaryInvolvedParty.getPostalAddress().get(0).setUnstructuredAddress(new UnstructuredAddress());
        primaryInvolvedParty.getPostalAddress().get(0).getUnstructuredAddress().setAddressLine1("addressLine1");
        primaryInvolvedParty.getPostalAddress().get(0).getUnstructuredAddress().setPostCode("postCode");
        primaryInvolvedParty.getPostalAddress().get(0).setIsPAFFormat(false);
        StB750AAppPerCCRegCreate b750Request = b750RequestResponseConverter.createB750Request(dataHelper.createApaRequestHeader(), primaryInvolvedParty, null, 0, "C");
        assertEquals(stHeader, b750Request.getStheader());
        assertEquals("C", b750Request.getStacc().getProdtype());
        assertEquals("Fghi", b750Request.getSurname());
        assertEquals("AbcdeFHI", b750Request.getFirstname());
        assertEquals("postCode", b750Request.getPostcode());
    }

    @Test
    public void testCreateB750RequestWhenMiddleNamePresent() {
        Customer primaryInvolvedParty = depositArrangement.getPrimaryInvolvedParty();
        primaryInvolvedParty.setExistingSortCode(null);
        primaryInvolvedParty.getIsPlayedBy().getIndividualName().get(0).getMiddleNames().add("MiddleName");
        StB750AAppPerCCRegCreate b750Request = b750RequestResponseConverter.createB750Request(dataHelper.createApaRequestHeader(), primaryInvolvedParty, null, 0, "C");
        assertEquals(stHeader, b750Request.getStheader());
        assertEquals("C", b750Request.getStacc().getProdtype());
        assertEquals("Fghi", b750Request.getSurname());
        assertEquals("AbcdeFHI", b750Request.getFirstname());
        assertEquals("SE19EQ", b750Request.getPostcode());
    }


    @Test
    public void testMapB750ResponseAttributesToProductArrangementWhenAppIdAndAppvrNewAreNull() {
        StB750BAppPerCCRegCreate b750Response = dataHelper.createB750Response();
        b750Response.setAppid(null);
        b750Response.setAppverNew(null);
        b750RequestResponseConverter.mapB750ResponseAttributesToProductArrangement(depositArrangement.getPrimaryInvolvedParty(), b750Response);
        assertEquals(null, depositArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().getApplicationVersion());
        assertEquals(null, depositArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().getRegistrationIdentifier());
    }

    private XMLGregorianCalendar createXMLGregorianCalendar(int year, int month, int day) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
            XMLGregorianCalendar xcal = datatypeFactory.newXMLGregorianCalendar();
            xcal.setYear(year);
            xcal.setMonth(month);
            xcal.setDay(day);
            return xcal;
        } catch (DatatypeConfigurationException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
