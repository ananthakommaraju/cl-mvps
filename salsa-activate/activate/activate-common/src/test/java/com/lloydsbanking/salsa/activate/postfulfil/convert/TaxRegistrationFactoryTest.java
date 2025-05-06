package com.lloydsbanking.salsa.activate.postfulfil.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.common.MaintenanceAuditData;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.common.MaintenanceAuditElement;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.common.Place;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.common.TaxIdentificationNumberType;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.ip.TaxRegistration;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

@Category(UnitTest.class)
public class TaxRegistrationFactoryTest {
    private TaxRegistrationFactory taxRegistrationFactory;

    @Before
    public void setUp() {
        taxRegistrationFactory = new TaxRegistrationFactory();
    }

    @Test
    public void testCopyTaxRegistration() {
        TaxRegistration taxRegistration = new TaxRegistration();
        taxRegistration.setTaxIdentificationNumber(new TaxIdentificationNumberType());
        taxRegistration.getTaxIdentificationNumber().setName("001");
        taxRegistration.setIsIssuedIn(new Place());
        taxRegistration.getIsIssuedIn().setName("USA");
        TaxRegistration taxRegistrationCopy = taxRegistrationFactory.copyTaxRegistration(taxRegistration);
        assertEquals("USA", taxRegistrationCopy.getIsIssuedIn().getName());
    }

    @Test
    public void testCopyTaxIdentificationNumberType() {
        TaxIdentificationNumberType taxIdentificationNumberType = new TaxIdentificationNumberType();
        taxIdentificationNumberType.setName("001");
        TaxIdentificationNumberType taxIdentificationNumberTypeCopy = taxRegistrationFactory.copyTaxIdentificationNumberType(taxIdentificationNumberType);
        assertEquals("001", taxIdentificationNumberTypeCopy.getName());
    }

    @Test
    public void testSetTaxPayId() {
        List<MaintenanceAuditData> maintenanceAuditListTaxRegistration = new ArrayList<>();
        TaxIdentificationNumberType taxIdentificationNumberType = new TaxIdentificationNumberType();
        taxIdentificationNumberType.setName("001");
        taxIdentificationNumberType.setDescription("USA");
        MaintenanceAuditData maintenanceAuditData = new MaintenanceAuditData();
        maintenanceAuditData.getHasMaintenanceAuditElement().add(new MaintenanceAuditElement());
        maintenanceAuditData.getHasMaintenanceAuditElement().get(0).setExternalLocationIdentifier("0000805121");
        maintenanceAuditData.getHasMaintenanceAuditElement().get(0).setExternalSystemIdentifier("19");
        maintenanceAuditData.getHasMaintenanceAuditElement().get(0).setExternalUserIdentifier("UNAUTHSALE");
        maintenanceAuditListTaxRegistration.add(maintenanceAuditData);
        TaxRegistration taxRegistration = taxRegistrationFactory.setTaxPayID(taxIdentificationNumberType, "USA", maintenanceAuditListTaxRegistration);
        assertEquals(taxIdentificationNumberType.getDescription(), taxRegistration.getTaxIdentificationNumber().getDescription());
        assertEquals("001", taxRegistration.getTaxIdentificationNumber().getName());
    }

    @Test
    public void testGetTaxIdentificationNumberType() {
        TaxIdentificationNumberType taxIdentificationNumberType = taxRegistrationFactory.getTaxIdentificationNumberType("001", "USA");
        assertEquals("USA", taxIdentificationNumberType.getDescription());
        assertEquals("001", taxIdentificationNumberType.getName());
    }


    @Test
    public void testHasMaintenanceAuditData() {
        assertFalse(taxRegistrationFactory.hasMaintenanceAuditData(new ArrayList<MaintenanceAuditData>()));
        List<MaintenanceAuditData> maintenanceAuditListTaxRegistration = new ArrayList<>();
        MaintenanceAuditData maintenanceAuditData = new MaintenanceAuditData();
        maintenanceAuditData.getHasMaintenanceAuditElement().add(new MaintenanceAuditElement());
        maintenanceAuditData.getHasMaintenanceAuditElement().get(0).setExternalLocationIdentifier("0000805121");
        maintenanceAuditData.getHasMaintenanceAuditElement().get(0).setExternalSystemIdentifier("19");
        maintenanceAuditData.getHasMaintenanceAuditElement().get(0).setExternalUserIdentifier("UNAUTHSALE");
        maintenanceAuditListTaxRegistration.add(maintenanceAuditData);
        assertTrue(taxRegistrationFactory.hasMaintenanceAuditData(maintenanceAuditListTaxRegistration));
    }

    @Test
    public void testMaintenanceAuditDataEmpty() {
        assertFalse(taxRegistrationFactory.hasMaintenanceAuditData(new ArrayList<MaintenanceAuditData>()));
        List<MaintenanceAuditData> maintenanceAuditListTaxRegistration = new ArrayList<>();
        assertFalse(taxRegistrationFactory.hasMaintenanceAuditData(maintenanceAuditListTaxRegistration));
    }

}
