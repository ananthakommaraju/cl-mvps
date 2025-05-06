package com.lloydsbanking.salsa.activate.postfulfil.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RecordInvolvedPartyDetailsRequest;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.common.*;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.ip.Individual;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.ip.NationalRegistration;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.ip.TaxRegistration;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.TaxResidencyDetails;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@Category(UnitTest.class)
public class RecordInvolvedPartyDetailsRequestFactoryTest {

    private RecordInvolvedPartyDetailsRequestFactory requestFactory;

    @Before
    public void setUp() {
        requestFactory = new RecordInvolvedPartyDetailsRequestFactory();
        requestFactory.taxRegistrationFactory = new TaxRegistrationFactory();
    }

    @Test
    public void testConvertWhenRegistrationTypeIsInstanceOfNationalRegistration() {
        Individual individual = new Individual();
        NationalRegistration nationalRegistration = new NationalRegistration();
        nationalRegistration.setCountryCode("44");
        nationalRegistration.setDescription("Residential");
        nationalRegistration.setRegistrationType(new RegistrationType());
        nationalRegistration.getRegistrationType().setName("5");
        MaintenanceAuditData maintenanceAuditData = new MaintenanceAuditData();
        maintenanceAuditData.getHasMaintenanceAuditElement().add(new MaintenanceAuditElement());
        maintenanceAuditData.getHasMaintenanceAuditElement().get(0).setExternalLocationIdentifier("0000805121");
        maintenanceAuditData.getHasMaintenanceAuditElement().get(0).setExternalSystemIdentifier("19");
        maintenanceAuditData.getHasMaintenanceAuditElement().get(0).setExternalUserIdentifier("UNAUTHSALE");
        nationalRegistration.getMaintenanceAuditData().add(maintenanceAuditData);
        individual.getRegistration().add(nationalRegistration);
        TaxResidencyDetails taxResidencyDetails = new TaxResidencyDetails();
        taxResidencyDetails.getTaxResidencyCountries().add("GBR");
        Customer customer = new Customer();
        customer.setCustomerIdentifier("216248241");
        customer.setIsPlayedBy(new lib_sim_bo.businessobjects.Individual());
        customer.getIsPlayedBy().setNationality("United Kingdom");
        customer.setTaxResidencyDetails(taxResidencyDetails);
        RecordInvolvedPartyDetailsRequest request = requestFactory.convert(individual, customer, "GBR", true);
        assertEquals(4, request.getInvolvedParty().getRegistration().size());
        assertEquals(1, request.getInvolvedParty().getRegistration().get(2).getMaintenanceAuditData().size());
        assertTrue(request.getInvolvedParty().getRegistration().get(1).getMaintenanceAuditData().isEmpty());
        individual.getRegistration().get(0).getRegistrationType().setName("1");
        RecordInvolvedPartyDetailsRequest request1 = requestFactory.convert(individual, customer, "GBR", true);
        assertEquals(4, request1.getInvolvedParty().getRegistration().size());
        assertEquals(1, request1.getInvolvedParty().getRegistration().get(0).getMaintenanceAuditData().size());
        individual.getRegistration().get(0).getRegistrationType().setName("2");
        RecordInvolvedPartyDetailsRequest request2 = requestFactory.convert(individual, customer, "GBR", true);
        assertEquals(4, request2.getInvolvedParty().getRegistration().size());
        assertEquals(1, request2.getInvolvedParty().getRegistration().get(1).getMaintenanceAuditData().size());
        individual.getRegistration().get(0).getRegistrationType().setName("8");
        RecordInvolvedPartyDetailsRequest request3 = requestFactory.convert(individual, customer, "GBR", true);
        assertEquals(4, request3.getInvolvedParty().getRegistration().size());
        assertEquals(1, request3.getInvolvedParty().getRegistration().get(3).getMaintenanceAuditData().size());
        List<String> previousNationalities = new ArrayList<>();
        previousNationalities.add("GBR");
        individual.getRegistration().get(0).getRegistrationType().setName("1");
        RecordInvolvedPartyDetailsRequest request4 = requestFactory.convert(individual, customer, "GBR", true);
        assertEquals(4, request4.getInvolvedParty().getRegistration().size());
        assertEquals(0, request4.getInvolvedParty().getRegistration().get(3).getMaintenanceAuditData().size());
        assertEquals(1, request4.getInvolvedParty().getRegistration().get(0).getMaintenanceAuditData().size());
    }

    @Test
    public void testConvertWhenRegistrationTypeIsInstanceOfTaxRegistration() {
        Individual individual = new Individual();
        TaxRegistration taxRegistration = new TaxRegistration();
        taxRegistration.setTaxIdentificationNumber(new TaxIdentificationNumberType());
        taxRegistration.getTaxIdentificationNumber().setName("001");
        taxRegistration.setIsIssuedIn(new Place());
        taxRegistration.getIsIssuedIn().setName("USA");
        MaintenanceAuditData maintenanceAuditData = new MaintenanceAuditData();
        maintenanceAuditData.getHasMaintenanceAuditElement().add(new MaintenanceAuditElement());
        maintenanceAuditData.getHasMaintenanceAuditElement().get(0).setExternalLocationIdentifier("0000805121");
        maintenanceAuditData.getHasMaintenanceAuditElement().get(0).setExternalSystemIdentifier("19");
        maintenanceAuditData.getHasMaintenanceAuditElement().get(0).setExternalUserIdentifier("UNAUTHSALE");
        taxRegistration.getMaintenanceAuditData().add(maintenanceAuditData);
        individual.getRegistration().add(taxRegistration);
        TaxResidencyDetails taxResidencyDetails = new TaxResidencyDetails();
        taxResidencyDetails.getTaxResidencyCountries().add("GBR");
        Customer customer = new Customer();
        customer.setCustomerIdentifier("216248241");
        customer.setIsPlayedBy(new lib_sim_bo.businessobjects.Individual());
        customer.getIsPlayedBy().setNationality("United Kingdom");
        customer.setTaxResidencyDetails(taxResidencyDetails);
        RecordInvolvedPartyDetailsRequest request = requestFactory.convert(individual, customer, "GBR", false);
        assertEquals(4, request.getInvolvedParty().getRegistration().size());
        assertTrue(request.getInvolvedParty().getRegistration().get(0).getMaintenanceAuditData().isEmpty());
        assertTrue(request.getInvolvedParty().getRegistration().get(1).getMaintenanceAuditData().isEmpty());
        assertTrue(request.getInvolvedParty().getRegistration().get(2).getMaintenanceAuditData().isEmpty());
        assertTrue(request.getInvolvedParty().getRegistration().get(3).getMaintenanceAuditData().isEmpty());
        taxResidencyDetails.setTaxPayerIdNumber("2");
        RecordInvolvedPartyDetailsRequest request1 = requestFactory.convert(individual, customer, "GBR", false);
        assertEquals(5, request1.getInvolvedParty().getRegistration().size());
        assertEquals(1, request1.getInvolvedParty().getRegistration().get(4).getMaintenanceAuditData().size());
        assertTrue(request1.getControlCommand().isEmpty());
        taxRegistration.setDescription("UNKNOWN");
        individual.getRegistration().add(taxRegistration);
        RecordInvolvedPartyDetailsRequest request2 = requestFactory.convert(individual, customer, "GBR", false);
        assertEquals(6, request2.getInvolvedParty().getRegistration().size());
        assertEquals(2, request2.getInvolvedParty().getRegistration().get(4).getMaintenanceAuditData().size());
        assertFalse(request2.getControlCommand().isEmpty());
    }
}
