package com.lloydsbanking.salsa.apacc.service.fulfil.convert;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apacc.TestDataHelper;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.OwnerData;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.OwnerPersonalData;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class F241V1PostalAddressFactoryTest {
    F241V1PostalAddressFactory f241PostalAddressFactory;
    FinanceServiceArrangement financeServiceArrangement;
    TestDataHelper testDataHelper;
    Customer customer;
    OwnerData ownerData;
    OwnerPersonalData ownerPersonalData;

    @Before
    public void setUp() {
        f241PostalAddressFactory = new F241V1PostalAddressFactory();
        testDataHelper = new TestDataHelper();
        financeServiceArrangement = testDataHelper.createFinanceServiceArrangement();
        ownerData = new OwnerData();
        ownerPersonalData = new OwnerPersonalData();
        ownerData = new OwnerData();
        ownerPersonalData = new OwnerPersonalData();
    }

    @Test
    public void testGetPostalAddress() {
        f241PostalAddressFactory.getPostalAddress(financeServiceArrangement.getPrimaryInvolvedParty(), ownerData, ownerPersonalData);
        assertEquals(financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().getAddressLinePAFData().get(0),
                ownerData.getAddressLine1Tx());
    }

    @Test
    public void testAddressLinePAFDataEqualToThree() {
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().getAddressLinePAFData().add("abc");
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().getAddressLinePAFData().add("ab");
        f241PostalAddressFactory.getPostalAddress(financeServiceArrangement.getPrimaryInvolvedParty(), ownerData, ownerPersonalData);
        f241PostalAddressFactory.getPostalAddress(financeServiceArrangement.getPrimaryInvolvedParty(), ownerData, ownerPersonalData);
        assertEquals(financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().getAddressLinePAFData().get(0),
                ownerData.getAddressLine1Tx());
    }

    @Test
    public void testAddressLinePAFDataEqualToTwo() {
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().getAddressLinePAFData().add("ab");
        f241PostalAddressFactory.getPostalAddress(financeServiceArrangement.getPrimaryInvolvedParty(), ownerData, ownerPersonalData);
        assertEquals(financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().getAddressLinePAFData().get(0),
                ownerData.getAddressLine1Tx());
    }

    @Test
    public void testAddressLinePAFDataEqualToOne() {
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().getAddressLinePAFData().add("a");
        f241PostalAddressFactory.getPostalAddress(financeServiceArrangement.getPrimaryInvolvedParty(), ownerData, ownerPersonalData);
        assertEquals(financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().getAddressLinePAFData().get(0),
                ownerData.getAddressLine1Tx());
    }

    @Test
    public void testSubBuildingWithSomeValue() {
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().setSubBuilding("sub");
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().setBuilding("abc");
        f241PostalAddressFactory.getPostalAddress(financeServiceArrangement.getPrimaryInvolvedParty(), ownerData, ownerPersonalData);
        assertEquals("1", ownerPersonalData.getBuildingNo());
        assertEquals("sub abc", ownerPersonalData.getBuildingNm());
    }

    @Test
    public void testSubBuildingWithNullBuildingValue() {
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().setSubBuilding("sub");
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().setBuilding(null);
        f241PostalAddressFactory.getPostalAddress(financeServiceArrangement.getPrimaryInvolvedParty(), ownerData, ownerPersonalData);
        assertEquals("1", ownerPersonalData.getBuildingNo());
        assertEquals("sub", ownerPersonalData.getBuildingNm());
    }

    @Test
    public void testSubBuildingWithNullValue() {
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().setSubBuilding(null);
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().setBuilding("abc");
        f241PostalAddressFactory.getPostalAddress(financeServiceArrangement.getPrimaryInvolvedParty(), ownerData, ownerPersonalData);
        assertEquals("1", ownerPersonalData.getBuildingNo());
        assertEquals("abc", ownerPersonalData.getBuildingNm());
        assertEquals(null, ownerPersonalData.getAddressCountyNm());
    }

    @Test
    public void testIsPAFFormatWithFalseValue() {
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).setIsPAFFormat(false);
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().setBuilding("abc");
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).setUnstructuredAddress(new UnstructuredAddress());
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setPostCode("aaa");
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine8("United Kingdom");
        f241PostalAddressFactory.getPostalAddress(financeServiceArrangement.getPrimaryInvolvedParty(), ownerData, ownerPersonalData);
        assertEquals(null, ownerPersonalData.getAddressCountyNm());
        assertEquals("aaa", ownerData.getPostCd());
        assertEquals("GBR", ownerData.getCountryCd());
    }

    @Test
    public void testGetPostalAddressUnstructured() {
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().clear();
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().add(getPostalUnstructuredAddress());
        financeServiceArrangement.getPrimaryInvolvedParty().setIsAuthCustomer(true);
        f241PostalAddressFactory.getPostalAddress(financeServiceArrangement.getPrimaryInvolvedParty(), ownerData, ownerPersonalData);
        assertEquals("building", ownerPersonalData.getBuildingNm());
        assertEquals("flat", ownerData.getAddressLine1Tx());
        assertEquals("district", ownerData.getCityNm());
        assertEquals("pin", ownerPersonalData.getAddressCountyNm());
    }

    @Test
    public void testGetPostalAddressUnstructuredForNotAuth() {
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().clear();
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().add(getPostalUnstructuredAddress());
        financeServiceArrangement.getPrimaryInvolvedParty().setIsAuthCustomer(false);
        f241PostalAddressFactory.getPostalAddress(financeServiceArrangement.getPrimaryInvolvedParty(), ownerData, ownerPersonalData);
        assertEquals("flat", ownerPersonalData.getBuildingNm());
        assertEquals("landmark", ownerData.getAddressLine1Tx());
        assertEquals("district", ownerData.getCityNm());
        assertEquals("pin", ownerPersonalData.getAddressCountyNm());
    }

    private PostalAddress getPostalUnstructuredAddress() {
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsPAFFormat(false);
        UnstructuredAddress unstructuredAddress = new UnstructuredAddress();
        unstructuredAddress.setAddressLine1("building");
        unstructuredAddress.setAddressLine2("flat");
        unstructuredAddress.setAddressLine3("street");
        unstructuredAddress.setAddressLine4("landmark");
        unstructuredAddress.setAddressLine5("city");
        unstructuredAddress.setAddressLine6("district");
        unstructuredAddress.setAddressLine7("pin");
        postalAddress.setUnstructuredAddress(unstructuredAddress);
        return postalAddress;
    }

    @Test
    public void testStructuredAddressWhenBuildingNumberIsNull() {
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().setSubBuilding("sub");
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().setBuildingNumber(null);
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().getAddressLinePAFData().add("abc");
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().getAddressLinePAFData().add("ab");
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().getAddressLinePAFData().add("a");
        f241PostalAddressFactory.getPostalAddress(financeServiceArrangement.getPrimaryInvolvedParty(), ownerData, ownerPersonalData);
        assertEquals("sub", ownerPersonalData.getBuildingNo());
        assertEquals("sub", ownerPersonalData.getBuildingNm());
    }

    @Test
    public void testUnstructuredAddressListEqualToSix() {
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().clear();
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().add(getPostalUnstructuredAddress());
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine1(null);
        financeServiceArrangement.getPrimaryInvolvedParty().setIsAuthCustomer(true);
        f241PostalAddressFactory.getPostalAddress(financeServiceArrangement.getPrimaryInvolvedParty(), ownerData, ownerPersonalData);
        assertEquals("street", ownerData.getAddressLine2Tx());
    }

    @Test
    public void testUnstructuredAddressListEqualToFive() {
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().clear();
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().add(getPostalUnstructuredAddress());
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine1(null);
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine2(null);
        financeServiceArrangement.getPrimaryInvolvedParty().setIsAuthCustomer(true);
        f241PostalAddressFactory.getPostalAddress(financeServiceArrangement.getPrimaryInvolvedParty(), ownerData, ownerPersonalData);
        assertEquals("landmark", ownerData.getAddressLine2Tx());
    }

    @Test
    public void testUnstructuredAddressListEqualToFour() {
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().clear();
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().add(getPostalUnstructuredAddress());
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine1(null);
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine2(null);
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine3(null);
        financeServiceArrangement.getPrimaryInvolvedParty().setIsAuthCustomer(true);
        f241PostalAddressFactory.getPostalAddress(financeServiceArrangement.getPrimaryInvolvedParty(), ownerData, ownerPersonalData);
        assertEquals("city", ownerData.getAddressLine2Tx());
    }

    @Test
    public void testUnstructuredAddressListEqualToThree() {
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().clear();
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().add(getPostalUnstructuredAddress());
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine1(null);
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine2(null);
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine3(null);
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine4(null);
        financeServiceArrangement.getPrimaryInvolvedParty().setIsAuthCustomer(true);
        f241PostalAddressFactory.getPostalAddress(financeServiceArrangement.getPrimaryInvolvedParty(), ownerData, ownerPersonalData);
        assertEquals("district", ownerData.getAddressLine2Tx());
    }

    @Test
    public void testUnstructuredAddressListEqualToTwo() {
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().clear();
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().add(getPostalUnstructuredAddress());
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine1(null);
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine2(null);
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine3(null);
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine4(null);
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine5(null);
        financeServiceArrangement.getPrimaryInvolvedParty().setIsAuthCustomer(true);
        f241PostalAddressFactory.getPostalAddress(financeServiceArrangement.getPrimaryInvolvedParty(), ownerData, ownerPersonalData);
        assertEquals("pin", ownerData.getAddressLine2Tx());
    }

    @Test
    public void testUnstructuredAddressListEqualToOne() {
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().clear();
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().add(getPostalUnstructuredAddress());
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine1(null);
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine2(null);
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine3(null);
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine4(null);
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine5(null);
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine6(null);
        financeServiceArrangement.getPrimaryInvolvedParty().setIsAuthCustomer(true);
        f241PostalAddressFactory.getPostalAddress(financeServiceArrangement.getPrimaryInvolvedParty(), ownerData, ownerPersonalData);
        assertEquals("pin", ownerData.getAddressLine1Tx());
    }
}
