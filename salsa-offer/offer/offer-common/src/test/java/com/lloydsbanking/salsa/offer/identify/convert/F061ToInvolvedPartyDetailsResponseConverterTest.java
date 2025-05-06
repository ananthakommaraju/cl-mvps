package com.lloydsbanking.salsa.offer.identify.convert;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.F061Resp;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.PartyNonCoreData;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.PersonalData;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_bo.businessobjects.Customer;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class F061ToInvolvedPartyDetailsResponseConverterTest {

    private F061ToInvolvedPartyDetailsResponseConverter converter;
    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        converter = new F061ToInvolvedPartyDetailsResponseConverter();
        testDataHelper = new TestDataHelper();
        converter.auditDataFactory = new AuditDataFactory();
        converter.postalAddressConverter = new PostalAddressConverter();
    }

    @Test
    public void testSetInvolvedPartyDetailsResponse() throws ParseException, DatatypeConfigurationException, InternalServiceErrorMsg {
        F061Resp retrieveInvolvedPartyDetailsResponse = testDataHelper.createF061Resp(0);
        retrieveInvolvedPartyDetailsResponse.getPartyEnqData().setPersonalData(new PersonalData());
        retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPersonalData().setCIDPersId("123");
        retrieveInvolvedPartyDetailsResponse.getPartyEnqData().setPartyNonCoreData(new PartyNonCoreData());
        retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPartyNonCoreData().setStaffIn("1");

        Customer customer = testDataHelper.createPrimaryInvolvedParty();
        converter.setInvolvedPartyDetailsResponse(customer, true, retrieveInvolvedPartyDetailsResponse);

        assertEquals("123", customer.getCidPersID());
        assertTrue(customer.getIsPlayedBy().isIsStaffMember());
        assertEquals("type", customer.getAuditData().get(0).getAuditType());
        // assertEquals("careOfName", customer.getPostalAddress().get(0).getCareOfName());
        assertEquals("001", customer.getIsPlayedBy().getMaritalStatus());
        assertEquals("residentialStatus", customer.getIsPlayedBy().getResidentialStatus());
        assertEquals("006", customer.getIsPlayedBy().getEmploymentStatus());
    }

    @Test
    public void testSetInvolvedPartyDetailsWithDefaultValues() throws ParseException, DatatypeConfigurationException, InternalServiceErrorMsg {
        F061Resp retrieveInvolvedPartyDetailsResponse = testDataHelper.createF061Resp(0);
        retrieveInvolvedPartyDetailsResponse.getPartyEnqData().setPersonalData(new PersonalData());
        retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPersonalData().setCIDPersId("123");
        retrieveInvolvedPartyDetailsResponse.getPartyEnqData().setPartyNonCoreData(new PartyNonCoreData());
        retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPartyNonCoreData().setStaffIn("1");
        Customer customer = testDataHelper.createPrimaryInvolvedParty();
        converter.setInvolvedPartyDetailsResponse(customer, true, retrieveInvolvedPartyDetailsResponse);

        assertEquals("123", customer.getCidPersID());
        assertTrue(customer.getIsPlayedBy().isIsStaffMember());
        assertEquals("type", customer.getAuditData().get(0).getAuditType());
        assertEquals("001", customer.getIsPlayedBy().getMaritalStatus());
        assertEquals("residentialStatus", customer.getIsPlayedBy().getResidentialStatus());
        assertEquals("006", customer.getIsPlayedBy().getEmploymentStatus());
    }

    @Test
    public void testSetInvolvedPartyDetailsResponseOtherCases() throws ParseException, DatatypeConfigurationException, InternalServiceErrorMsg {

        F061Resp retrieveInvolvedPartyDetailsResponse = testDataHelper.createF061Resp(0);
        retrieveInvolvedPartyDetailsResponse.getPartyEnqData().setPersonalData(new PersonalData());
        retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPersonalData().setCIDPersId("123");
        retrieveInvolvedPartyDetailsResponse.getPartyEnqData().setPartyNonCoreData(new PartyNonCoreData());
        retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPartyNonCoreData().setStaffIn("2");

        Customer customer = testDataHelper.createPrimaryInvolvedParty();
        customer.getIsPlayedBy().setMaritalStatus(null);
        customer.getIsPlayedBy().setResidentialStatus(null);


        converter.setInvolvedPartyDetailsResponse(customer, false, retrieveInvolvedPartyDetailsResponse);

        assertEquals("123", customer.getCidPersID());
        assertFalse(customer.getIsPlayedBy().isIsStaffMember());
        assertEquals("type", customer.getAuditData().get(0).getAuditType());
        /*assertEquals(null, customer.getPostalAddress().get(0).getCareOfName());*/
        assertEquals("000", customer.getIsPlayedBy().getMaritalStatus());
        assertEquals("000", customer.getIsPlayedBy().getResidentialStatus());
    }

    @Test
    public void testSetInvolvedPartyDetailsResponseAuth() throws ParseException, DatatypeConfigurationException, InternalServiceErrorMsg {

        F061Resp retrieveInvolvedPartyDetailsResponse = testDataHelper.createF061Resp(0);
        retrieveInvolvedPartyDetailsResponse.getPartyEnqData().setPersonalData(new PersonalData());
        retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPersonalData().setCIDPersId("123");
        retrieveInvolvedPartyDetailsResponse.getPartyEnqData().setPartyNonCoreData(new PartyNonCoreData());
        retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPartyNonCoreData().setStaffIn("2");

        Customer customer = testDataHelper.createPrimaryInvolvedParty();
        customer.getIsPlayedBy().setMaritalStatus(null);
        customer.getIsPlayedBy().setResidentialStatus(null);


        converter.setInvolvedPartyDetailsResponse(customer, true, retrieveInvolvedPartyDetailsResponse);

        assertEquals("123", customer.getCidPersID());
        assertFalse(customer.getIsPlayedBy().isIsStaffMember());
        assertEquals("type", customer.getAuditData().get(0).getAuditType());
        /*assertEquals(null, customer.getPostalAddress().get(0).getCareOfName());*/
        assertEquals("000", customer.getIsPlayedBy().getMaritalStatus());
        assertEquals("000", customer.getIsPlayedBy().getResidentialStatus());
    }

    @Test
    public void testSetInvolvedPartyDetailsResponseGenderCodemale() throws ParseException, DatatypeConfigurationException, InternalServiceErrorMsg {

        F061Resp retrieveInvolvedPartyDetailsResponse = testDataHelper.createF061Resp(0);
        retrieveInvolvedPartyDetailsResponse.getPartyEnqData().setPersonalData(new PersonalData());
        retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPersonalData().setCIDPersId("123");
        retrieveInvolvedPartyDetailsResponse.getPartyEnqData().setPartyNonCoreData(new PartyNonCoreData());
        retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPartyNonCoreData().setStaffIn("2");

        Customer customer = testDataHelper.createPrimaryInvolvedParty();
        customer.getIsPlayedBy().setGender("1");
        customer.getIsPlayedBy().setResidentialStatus(null);


        converter.setInvolvedPartyDetailsResponse(customer, true, retrieveInvolvedPartyDetailsResponse);

        assertEquals("123", customer.getCidPersID());
        assertFalse(customer.getIsPlayedBy().isIsStaffMember());
        assertEquals("type", customer.getAuditData().get(0).getAuditType());
        /*assertEquals(null, customer.getPostalAddress().get(0).getCareOfName());*/
        assertEquals("001", customer.getIsPlayedBy().getMaritalStatus());
        assertEquals("000", customer.getIsPlayedBy().getResidentialStatus());
    }
}
