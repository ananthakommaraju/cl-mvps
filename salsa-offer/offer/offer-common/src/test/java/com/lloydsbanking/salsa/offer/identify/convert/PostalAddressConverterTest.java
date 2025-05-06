package com.lloydsbanking.salsa.offer.identify.convert;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.AddressData;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.AddressLinePaf;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.StructuredAddress;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.UnstructuredAddress;
import lib_sim_bo.businessobjects.PostalAddress;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

@Category(UnitTest.class)
public class PostalAddressConverterTest {

    private PostalAddressConverter postalAddressConverter;

    @Before
    public void setUp() {
        postalAddressConverter = new PostalAddressConverter();
        postalAddressConverter.dateFactory = mock(DateFactory.class);
    }

    @Test
    public void testGetPostalAddress() throws ParseException, DatatypeConfigurationException {
        AddressData addressData = new AddressData();
        addressData.setAddressStatusCd("001");
        postalAddressConverter.dateFactory = new DateFactory();
        addressData.setAmdEffDt("10102010");

        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date sampleDate = sdf.parse("10102010");
        GregorianCalendar gregory = new GregorianCalendar();
        gregory.setTime(sampleDate);
        XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory);

        PostalAddress postalAddress = postalAddressConverter.getPostalAddress(addressData);

        assertEquals("CURRENT", postalAddress.getStatusCode());
        assertEquals(calendar, postalAddress.getEffectiveFrom());
    }

    @Test
    public void testGetPostalAddressWithNullAmdEffDt() throws ParseException, DatatypeConfigurationException {
        AddressData addressData = new AddressData();
        addressData.setAddressStatusCd("001");
        postalAddressConverter.dateFactory = new DateFactory();
        addressData.setAmdEffDt("");

        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date sampleDate = sdf.parse("10102010");
        GregorianCalendar gregory = new GregorianCalendar();
        gregory.setTime(sampleDate);
        XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory);

        PostalAddress postalAddress = postalAddressConverter.getPostalAddress(addressData);

        assertEquals("CURRENT", postalAddress.getStatusCode());
        assertNull(postalAddress.getEffectiveFrom());
    }

    @Test
    public void testGetStructuredAddress() throws ParseException, DatatypeConfigurationException {


        AddressData addressData = new AddressData();

        StructuredAddress structuredAddress = new StructuredAddress();
        structuredAddress.setBuildingNm("building");
        structuredAddress.setBuildingNo("123");
        structuredAddress.setOrganisationNm("orgName");
        structuredAddress.setSubBuildingNm("subBuilding");
        AddressLinePaf addressLinePaf = new AddressLinePaf();
        addressLinePaf.setAddressLinePafTx("paf");
        structuredAddress.getAddressLinePaf().add(addressLinePaf);
        structuredAddress.setAddressDistrictNm("district");
        structuredAddress.setInPostCd("in");
        structuredAddress.setOutPostCd("out");
        structuredAddress.setAddressPostTownNm("postTown");
        structuredAddress.setDelivPointSuffixCd("suffix");
        structuredAddress.setAddressCountyNm("countNm");

        addressData.setStructuredAddress(structuredAddress);


        PostalAddress postalAddress = postalAddressConverter.getPostalAddress(addressData);
        assertEquals("building", postalAddress.getStructuredAddress().getBuilding());
        assertEquals("orgName", postalAddress.getStructuredAddress().getOrganisation());
        assertEquals("subBuilding", postalAddress.getStructuredAddress().getSubBuilding());
        assertEquals("123", postalAddress.getStructuredAddress().getBuildingNumber());
        assertEquals("paf", postalAddress.getStructuredAddress().getAddressLinePAFData().get(0));
        assertEquals("district", postalAddress.getStructuredAddress().getDistrict());
        assertEquals("in", postalAddress.getStructuredAddress().getPostCodeIn());
        assertEquals("out", postalAddress.getStructuredAddress().getPostCodeOut());
        assertEquals("postTown", postalAddress.getStructuredAddress().getPostTown());
        assertEquals("suffix", postalAddress.getStructuredAddress().getPointSuffix());
        assertEquals("countNm", postalAddress.getStructuredAddress().getCounty());


    }

    @Test
    public void testGetUnstructuredAddress() throws ParseException, DatatypeConfigurationException {
        AddressData addressData = new AddressData();
        UnstructuredAddress unstructuredAddress = new UnstructuredAddress();
        unstructuredAddress.setAddressLine1Tx("add1");
        unstructuredAddress.setAddressLine2Tx("add2");
        unstructuredAddress.setAddressLine3Tx("add3");
        unstructuredAddress.setAddressLine4Tx("add4");
        unstructuredAddress.setAddressLine5Tx("add5");
        unstructuredAddress.setAddressLine6Tx("add6");
        unstructuredAddress.setAddressLine7Tx("add7");
        unstructuredAddress.setPostCd("postCd");
        addressData.setUnstructuredAddress(unstructuredAddress);

        PostalAddress postalAddress = postalAddressConverter.getPostalAddress(addressData);

        assertEquals("add1", postalAddress.getUnstructuredAddress().getAddressLine1());
        assertEquals("add2", postalAddress.getUnstructuredAddress().getAddressLine2());
        assertEquals("add3", postalAddress.getUnstructuredAddress().getAddressLine3());
        assertEquals("add4", postalAddress.getUnstructuredAddress().getAddressLine4());
        assertEquals("add5", postalAddress.getUnstructuredAddress().getAddressLine5());
        assertEquals("add6", postalAddress.getUnstructuredAddress().getAddressLine6());
        assertEquals("add7", postalAddress.getUnstructuredAddress().getAddressLine7());
        assertEquals("postCd", postalAddress.getUnstructuredAddress().getPostCode());
    }
}
