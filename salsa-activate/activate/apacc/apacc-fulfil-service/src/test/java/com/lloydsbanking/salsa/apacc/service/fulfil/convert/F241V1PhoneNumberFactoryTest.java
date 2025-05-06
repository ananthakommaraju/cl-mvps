package com.lloydsbanking.salsa.apacc.service.fulfil.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apacc.TestDataHelper;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.OwnerContactData;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.OwnerData;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class F241V1PhoneNumberFactoryTest {
    F241V1PhoneNumberFactory f241PhoneNumberFactory;
    TestDataHelper testDataHelper;
    FinanceServiceArrangement financeServiceArrangement;
    Customer customer;

    @Before
    public void setUp() {
        f241PhoneNumberFactory = new F241V1PhoneNumberFactory();
        testDataHelper = new TestDataHelper();
        financeServiceArrangement = testDataHelper.createFinanceServiceArrangement();
        customer = financeServiceArrangement.getPrimaryInvolvedParty();
    }

    @Test
    public void testGetPhoneDetails() {
        OwnerData ownerData = new OwnerData();
        OwnerContactData ownerContactData = new OwnerContactData();
        f241PhoneNumberFactory.getPhoneDetails(customer, ownerData, ownerContactData);
        assertEquals("04444", ownerData.getHomePhoneNo());
        assertEquals("1", ownerContactData.getTelephoneStatusCd());
    }

    @Test
    public void testGetPhoneDetailsWithAreaCode() {
        customer.getTelephoneNumber().get(0).setAreaCode("044");
        OwnerData ownerData = new OwnerData();
        OwnerContactData ownerContactData = new OwnerContactData();
        f241PhoneNumberFactory.getPhoneDetails(customer, ownerData, ownerContactData);
        assertEquals("04444", ownerData.getHomePhoneNo());
        assertEquals("1", ownerContactData.getTelephoneStatusCd());
    }

    @Test
    public void testGetPhoneDetailsWithMobileType() {
        customer.getTelephoneNumber().get(0).setTelephoneType("7");
        OwnerData ownerData = new OwnerData();
        OwnerContactData ownerContactData = new OwnerContactData();
        f241PhoneNumberFactory.getPhoneDetails(customer, ownerData, ownerContactData);
        assertEquals("044", ownerData.getMobilePhoneNo());
        assertEquals("1", ownerContactData.getMobileStatusCd());

    }

    @Test
    public void testGetPhoneDetailsWithMobileTypeAreaCode() {
        customer.getTelephoneNumber().get(0).setTelephoneType("7");
        customer.getTelephoneNumber().get(0).setAreaCode("044");
        OwnerData ownerData = new OwnerData();
        OwnerContactData ownerContactData = new OwnerContactData();
        f241PhoneNumberFactory.getPhoneDetails(customer, ownerData, ownerContactData);
        assertEquals("044", ownerData.getMobilePhoneNo());
        assertEquals("1", ownerContactData.getMobileStatusCd());
    }

    @Test
    public void testGetPhoneDetailsWithOfficePhone() {
        customer.getTelephoneNumber().get(0).setTelephoneType("4");
        OwnerData ownerData = new OwnerData();
        OwnerContactData ownerContactData = new OwnerContactData();
        f241PhoneNumberFactory.getPhoneDetails(customer, ownerData, ownerContactData);
        assertEquals("04444", ownerData.getOfficePhoneNo());
        assertEquals("1", ownerContactData.getPhoneStatusCd());
    }

    @Test
    public void testGetPhoneDetailsForOutCountry() {
        customer.getTelephoneNumber().get(0).setCountryPhoneCode("40");
        OwnerData ownerData = new OwnerData();
        OwnerContactData ownerContactData = new OwnerContactData();
        f241PhoneNumberFactory.getPhoneDetails(customer, ownerData, ownerContactData);
        assertEquals("404444", ownerData.getHomePhoneNo());
        assertEquals("1", ownerContactData.getTelephoneStatusCd());
    }

    @Test
    public void testGetPhoneNumberStartsWithZero() {
        customer.getTelephoneNumber().get(0).setTelephoneType("7");
        customer.getTelephoneNumber().get(0).setPhoneNumber("044");
        OwnerData ownerData = new OwnerData();
        OwnerContactData ownerContactData = new OwnerContactData();
        f241PhoneNumberFactory.getPhoneDetails(customer, ownerData, ownerContactData);
        assertEquals("044", ownerData.getMobilePhoneNo());
        assertEquals("1", ownerContactData.getMobileStatusCd());
    }

    @Test
    public void testGetPhoneDetailsWithMobileTypeForOutCountry() {
        customer.getTelephoneNumber().get(0).setTelephoneType("7");
        customer.getTelephoneNumber().get(0).setCountryPhoneCode("40");
        OwnerData ownerData = new OwnerData();
        OwnerContactData ownerContactData = new OwnerContactData();
        f241PhoneNumberFactory.getPhoneDetails(customer, ownerData, ownerContactData);
        assertEquals("4044", ownerData.getMobilePhoneNo());
        assertEquals("1", ownerContactData.getMobileStatusCd());
    }

    @Test
    public void testGetPhoneNumberStartsWithZeroForMobileType() {
        customer.getTelephoneNumber().get(0).setTelephoneType("7");
        customer.getTelephoneNumber().get(0).setCountryPhoneCode("40");
        customer.getTelephoneNumber().get(0).setPhoneNumber("04");
        OwnerData ownerData = new OwnerData();
        OwnerContactData ownerContactData = new OwnerContactData();
        f241PhoneNumberFactory.getPhoneDetails(customer, ownerData, ownerContactData);
        assertEquals("404", ownerData.getMobilePhoneNo());
        assertEquals("1", ownerContactData.getMobileStatusCd());
    }

    @Test
    public void testGetPhoneDetailsForInCountry() {
        customer.getTelephoneNumber().get(0).setPhoneNumber("4445643234");
        customer.getTelephoneNumber().get(0).setAreaCode("4457894445");
        OwnerData ownerData = new OwnerData();
        OwnerContactData ownerContactData = new OwnerContactData();
        f241PhoneNumberFactory.getPhoneDetails(customer, ownerData, ownerContactData);
        assertEquals("44578944454445643234", ownerData.getHomePhoneNo());
        assertEquals("1", ownerContactData.getTelephoneStatusCd());
    }

    @Test
    public void testGetPhoneNumberDetailsForOutCountry() {
        customer.getTelephoneNumber().get(0).setPhoneNumber("444564323");
        customer.getTelephoneNumber().get(0).setAreaCode("445789444");
        customer.getTelephoneNumber().get(0).setCountryPhoneCode("40");
        OwnerData ownerData = new OwnerData();
        OwnerContactData ownerContactData = new OwnerContactData();
        f241PhoneNumberFactory.getPhoneDetails(customer, ownerData, ownerContactData);
        assertEquals("40445789444444564323", ownerData.getHomePhoneNo());
        assertEquals("1", ownerContactData.getTelephoneStatusCd());
    }

    @Test
    public void testGetPhoneNumberDetailsForOutCountryAreaCodeStartsWithZero() {
        customer.getTelephoneNumber().get(0).setPhoneNumber("4445643237");
        customer.getTelephoneNumber().get(0).setAreaCode("0457894447");
        customer.getTelephoneNumber().get(0).setCountryPhoneCode("40");
        OwnerData ownerData = new OwnerData();
        OwnerContactData ownerContactData = new OwnerContactData();
        f241PhoneNumberFactory.getPhoneDetails(customer, ownerData, ownerContactData);
        assertEquals("04578944474445643237", ownerData.getHomePhoneNo());
        assertEquals("1", ownerContactData.getTelephoneStatusCd());
    }

    @Test
    public void testGetPhoneNumberDetailsForOutCountryAreaCode() {
        customer.getTelephoneNumber().get(0).setPhoneNumber("4445643237");
        customer.getTelephoneNumber().get(0).setAreaCode("4457894447");
        customer.getTelephoneNumber().get(0).setCountryPhoneCode("40");
        OwnerData ownerData = new OwnerData();
        OwnerContactData ownerContactData = new OwnerContactData();
        f241PhoneNumberFactory.getPhoneDetails(customer, ownerData, ownerContactData);
        assertEquals("44578944474445643237", ownerData.getHomePhoneNo());
        assertEquals("1", ownerContactData.getTelephoneStatusCd());
    }

    @Test
    public void testGetPhoneNumberCommonDetailsForOutCountry() {
        customer.getTelephoneNumber().get(0).setPhoneNumber("444564");
        customer.getTelephoneNumber().get(0).setAreaCode("04457894");
        customer.getTelephoneNumber().get(0).setCountryPhoneCode("40");
        OwnerData ownerData = new OwnerData();
        OwnerContactData ownerContactData = new OwnerContactData();
        f241PhoneNumberFactory.getPhoneDetails(customer, ownerData, ownerContactData);
        assertEquals("404457894444564", ownerData.getHomePhoneNo());
        assertEquals("1", ownerContactData.getTelephoneStatusCd());
    }
}
