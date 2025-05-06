package com.lloydsbanking.salsa.apapca.service.fulfil.converter;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.downstream.account.client.b276.RetrieveAccProcessOverdraftRequestBuilder;
import lib_sim_bo.businessobjects.PostalAddress;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class AddressDetailsTest {
    AddressDetails addressDetails;

    List<PostalAddress> postalAddressList;

    RetrieveAccProcessOverdraftRequestBuilder builder;

    TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        addressDetails = new AddressDetails();
        postalAddressList = new ArrayList<>();
        builder = new RetrieveAccProcessOverdraftRequestBuilder();
        testDataHelper = new TestDataHelper();
    }

    @Test
    public void setAddressDetailsEmptyListTest() {
        addressDetails.setAddressDetails(postalAddressList, builder);
        assertEquals(0, builder.build().getAddrLineCurrent().size());
        assertEquals("0", builder.build().getPostcodeCurrent());
        assertEquals("0", builder.build().getPostcodePrevious());

    }

    @Test
    public void setAddressDetailsWithCurrentStausCodeTest() {
        postalAddressList.add(new PostalAddress());
        postalAddressList.get(0).setStatusCode("CURRENT");
        postalAddressList.get(0).setDurationofStay("0905");
        postalAddressList.get(0).setIsPAFFormat(true);
        postalAddressList.get(0).setStructuredAddress(testDataHelper.getStructureAddress());
        addressDetails.setAddressDetails(postalAddressList, builder);
        assertEquals("2315", builder.build().getPostcodeCurrent());
        assertEquals("0", builder.build().getPostcodePrevious());

    }

    @Test
    public void setAddressDetailsWithCurrentStausCodeWithFalsePAFFormatTest() {
        postalAddressList.add(new PostalAddress());
        postalAddressList.get(0).setStatusCode("CURRENT");
        postalAddressList.get(0).setDurationofStay("0905");
        postalAddressList.get(0).setIsPAFFormat(false);
        postalAddressList.get(0).setUnstructuredAddress(testDataHelper.getUnstructuredAddress());
        addressDetails.setAddressDetails(postalAddressList, builder);
        assertEquals("postcode", builder.build().getPostcodeCurrent());
        assertEquals("0", builder.build().getPostcodePrevious());

    }

    @Test
    public void setAddressDetailsWithPreviousStausCodeTest() {
        postalAddressList.add(new PostalAddress());
        postalAddressList.get(0).setStatusCode("PREVIOUS");
        postalAddressList.get(0).setDurationofStay("0905");
        postalAddressList.get(0).setIsPAFFormat(true);
        postalAddressList.get(0).setStructuredAddress(testDataHelper.getStructureAddress());
        addressDetails.setAddressDetails(postalAddressList, builder);
        assertEquals("2315", builder.build().getPostcodePrevious());
        assertEquals("0", builder.build().getPostcodeCurrent());

    }

    @Test
    public void setAddressDetailsWithPreviousStausCodeWithFalsePAFFormatTest() {
        postalAddressList.add(new PostalAddress());
        postalAddressList.get(0).setStatusCode("PREVIOUS");
        postalAddressList.get(0).setDurationofStay("0905");
        postalAddressList.get(0).setIsPAFFormat(false);
        postalAddressList.get(0).setUnstructuredAddress(testDataHelper.getUnstructuredAddress());
        addressDetails.setAddressDetails(postalAddressList, builder);
        assertEquals("postcode", builder.build().getPostcodePrevious());
        assertEquals("0", builder.build().getPostcodeCurrent());
    }
}
