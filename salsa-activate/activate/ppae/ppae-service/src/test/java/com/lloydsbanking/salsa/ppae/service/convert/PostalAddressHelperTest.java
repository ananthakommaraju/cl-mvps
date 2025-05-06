package com.lloydsbanking.salsa.ppae.service.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class PostalAddressHelperTest {

    PostalAddressHelper postalAddressHelper;
    TestDataHelper testDataHelper;

    @Before
    public void setUp() throws DatatypeConfigurationException {
        postalAddressHelper = new PostalAddressHelper();
        testDataHelper = new TestDataHelper();
    }

    @Test
    public void getAddressLinesTestWithEmptyUnstructuredAddress() {
        List<String> addressLines = postalAddressHelper.getAddressLines(new UnstructuredAddress());
        assertEquals(0, addressLines.size());
    }

    @Test
    public void getAddressLinesTestWithUnstructuredAddress() {
        UnstructuredAddress unstructuredAddress = new UnstructuredAddress();
        unstructuredAddress.setAddressLine1("line 1");
        unstructuredAddress.setAddressLine2("line 2");
        unstructuredAddress.setAddressLine3("line 3");
        List<String> addressLines = postalAddressHelper.getAddressLines(unstructuredAddress);
        assertEquals(2, addressLines.size());
    }

    @Test
    public void getAddressLinesTestWithNullFirstLine() {
        UnstructuredAddress unstructuredAddress = new UnstructuredAddress();
        unstructuredAddress.setAddressLine1(null);
        unstructuredAddress.setAddressLine2("line 2");
        unstructuredAddress.setAddressLine3("line 3");
        List<String> addressLines = postalAddressHelper.getAddressLines(unstructuredAddress);
        assertEquals(2, addressLines.size());
    }

    @Test
    public void getAddressLinesTestWithNullFirstAndThirdLine() {
        UnstructuredAddress unstructuredAddress = new UnstructuredAddress();
        unstructuredAddress.setAddressLine1(null);
        unstructuredAddress.setAddressLine2("line 2");
        unstructuredAddress.setAddressLine3(null);
        List<String> addressLines = postalAddressHelper.getAddressLines(unstructuredAddress);
        assertEquals(1, addressLines.size());
    }

    @Test
    public void getAddressLinesTestWithEmptyAddressLineList() {
        List<String> addressLines = postalAddressHelper.getAddressLines(new ArrayList<String>());
        assertEquals(0, addressLines.size());
    }

    @Test
    public void getAddressLinesTestWithAddressLineList() {
        List<String> addressLineList = new ArrayList<>();
        addressLineList.add("line 1");
        addressLineList.add("line 2");
        List<String> addressLines = postalAddressHelper.getAddressLines(addressLineList);
        assertEquals(2, addressLines.size());
    }


}
