package com.lloydsbanking.salsa.offer.createinvolvedparty.evaluate;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.AddressUpdDataType;
import lib_sim_bo.businessobjects.PostalAddress;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.text.ParseException;
import java.util.List;

import static org.junit.Assert.*;

@Category(UnitTest.class)
public class EvaluateAddressUpdDataTest {

    EvaluateAddressUpdData evaluateAddressUpdData = new EvaluateAddressUpdData();

    @Before
    public void setUp() {
        evaluateAddressUpdData.dateFactory = new DateFactory();
    }

    @Test
    public void generateAddressUpdDataForStructuredAddressTest() throws ParseException {
        List<PostalAddress> postalAddressList = new TestDataHelper().createPostalAddressList();
        postalAddressList.get(0).setDurationofStay("07072000");
        AddressUpdDataType addressUpdDataType = new AddressUpdDataType();
        evaluateAddressUpdData.generateAddressUpdData(postalAddressList, addressUpdDataType);
        assertEquals("001", addressUpdDataType.getAddressStatusCd());

    }

    @Test
    public void generateAddressUpdDataForStructuredAddressWithEmptyDurationOfStayTest() throws ParseException {
        List<PostalAddress> postalAddressList = new TestDataHelper().createPostalAddressList();
        postalAddressList.get(0).setDurationofStay("");
        AddressUpdDataType addressUpdDataType = new AddressUpdDataType();
        evaluateAddressUpdData.generateAddressUpdData(postalAddressList, addressUpdDataType);
        assertEquals("001", addressUpdDataType.getAddressStatusCd());

    }

    @Test
    public void generateAddressUpdDataForUnstructuredAddressTest() throws ParseException {
        List<PostalAddress> postalAddressList = new TestDataHelper().createPostalAddressListForUnstructuredAddress();
        postalAddressList.get(0).setDurationofStay("07072000");
        AddressUpdDataType addressUpdDataType = new AddressUpdDataType();
        evaluateAddressUpdData.generateAddressUpdData(postalAddressList, addressUpdDataType);
        assertEquals("001", addressUpdDataType.getAddressStatusCd());
        assertNull(addressUpdDataType.getStructuredAddress());
    }

    @Test
    public void testAmendmentEffectiveDate() throws ParseException {
        List<PostalAddress> postalAddressList = new TestDataHelper().createPostalAddressListForUnstructuredAddress();
        postalAddressList.get(0).setDurationofStay("0508");

        AddressUpdDataType addressUpdDataType = new AddressUpdDataType();
        evaluateAddressUpdData.generateAddressUpdData(postalAddressList, addressUpdDataType);

        assertEquals(evaluateAddressUpdData.dateFactory.convertDurationYYMMToStringDateFormat("0508", "ddMMyyyy"), addressUpdDataType.getAmdEffDt());


    }

    @Test
    public void testEvaluateAddressUpdData() throws ParseException {
        List<PostalAddress> postalAddressList = new TestDataHelper().createPostalAddressList();
        postalAddressList.get(0).setDurationofStay("07072000");
        postalAddressList.get(0).setStatusCode("Saving");
        AddressUpdDataType addressUpdDataType = new AddressUpdDataType();
        evaluateAddressUpdData.generateAddressUpdData(postalAddressList, addressUpdDataType);
        assertEquals("002", addressUpdDataType.getAddressStatusCd());
        assertEquals(null, addressUpdDataType.getAddressCareOfNm());

    }

    @Test
    public void testEvaluateAddressUpdDataWhenEffectiveFromIsNotnull() throws ParseException {
        List<PostalAddress> postalAddressList = new TestDataHelper().createPostalAddressList();
        postalAddressList.get(0).setDurationofStay("07072000");
        postalAddressList.get(0).setStatusCode("CURRENT");
        AddressUpdDataType addressUpdDataType = new AddressUpdDataType();
        evaluateAddressUpdData.generateAddressUpdData(postalAddressList, addressUpdDataType);
        assertEquals("001", addressUpdDataType.getAddressStatusCd());
        assertNotNull(addressUpdDataType.getAmdEffDt());

    }


}
