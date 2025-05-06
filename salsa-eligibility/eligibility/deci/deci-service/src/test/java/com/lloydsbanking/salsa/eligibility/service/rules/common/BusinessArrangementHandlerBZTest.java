package com.lloydsbanking.salsa.eligibility.service.rules.common;

import junit.framework.Assert;
import lb_gbo_sales.businessobjects.BusinessArrangement;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BusinessArrangementHandlerBZTest {

    BusinessArrangementHandlerBZ businessArrangementHandlerBZ;

    @Test
    public void getEntityTypesReturnsEntityTypeWhenBusinessIdMatches() {
        BusinessArrangement businessArrangement = new BusinessArrangement();
        businessArrangement.setEnttyTyp("011");
        businessArrangement.setBusinessId("1234");
        List<BusinessArrangement> businessArrangements = new ArrayList<>();
        businessArrangements.add(businessArrangement);
        businessArrangementHandlerBZ = new BusinessArrangementHandlerBZ(businessArrangements);
        String entityTypes = businessArrangementHandlerBZ.getEntityTypes("1234");

        assertEquals("011", entityTypes);

    }

    @Test
    public void getEntityTypesReturnsNullWhenBusinessIdDoesNotMatch() {
        BusinessArrangement businessArrangement = new BusinessArrangement();
        businessArrangement.setEnttyTyp("011");
        businessArrangement.setBusinessId("1233");
        List<BusinessArrangement> businessArrangements = new ArrayList<>();
        businessArrangements.add(businessArrangement);
        businessArrangementHandlerBZ = new BusinessArrangementHandlerBZ(businessArrangements);
        String entityTypes = businessArrangementHandlerBZ.getEntityTypes("1234");

        assertNull(entityTypes);

    }

    @Test
    public void getEntityTypesReturnsNullWhenBusinessArrangementsIsNull()
    {
        List<BusinessArrangement> businessArrangements=null;
        businessArrangementHandlerBZ=new BusinessArrangementHandlerBZ(businessArrangements);
        String entityTypes=businessArrangementHandlerBZ.getEntityTypes("1234");

        assertNull(entityTypes);
    }

    @Test
    public void getEntityTypesReturnsNullWhenBusinessArrangementsIsEmpty()
    {
        List<BusinessArrangement> businessArrangements = new ArrayList<>();
        businessArrangementHandlerBZ=new BusinessArrangementHandlerBZ(businessArrangements);
        String entityTypes=businessArrangementHandlerBZ.getEntityTypes("1234");

        assertNull(entityTypes);

    }
    @Test
    public void getEntityTypesReturnsNullWhenBusinessIdIsNull() {
        BusinessArrangement businessArrangement = new BusinessArrangement();
        businessArrangement.setEnttyTyp("011");
        List<BusinessArrangement> businessArrangements = new ArrayList<>();
        businessArrangements.add(businessArrangement);
        businessArrangementHandlerBZ = new BusinessArrangementHandlerBZ(businessArrangements);
        String entityTypes = businessArrangementHandlerBZ.getEntityTypes("1234");

        assertNull(entityTypes);

    }

    @Test
    public void hasMBCRoleReturnsTrueWhenBusinessIdMatchesAndRoleInCTextIsNotNull() {
        BusinessArrangement businessArrangement = new BusinessArrangement();
        businessArrangement.setBusinessId("1234");
        businessArrangement.setRolesInCtxt("CUS");
        List<BusinessArrangement> businessArrangements = new ArrayList<>();
        businessArrangements.add(businessArrangement);
        businessArrangementHandlerBZ = new BusinessArrangementHandlerBZ(businessArrangements);

        assertTrue(businessArrangementHandlerBZ.hasMBCRole("1234", "CUS"));

    }



    @Test
    public void hasMCBRoleReturnsFalseWhenBusinessIdDoesNotMatch()
    {
        BusinessArrangement businessArrangement = new BusinessArrangement();
        businessArrangement.setBusinessId("2331");
        businessArrangement.setRolesInCtxt("ABS");
        List<BusinessArrangement> businessArrangements = new ArrayList<>();
        businessArrangements.add(businessArrangement);
        businessArrangementHandlerBZ = new BusinessArrangementHandlerBZ(businessArrangements);

        assertFalse(businessArrangementHandlerBZ.hasMBCRole("1234", "CUS"));
    }


    @Test
    public void hasMBCRoleReturnsFalseWhenBusinessIdIsNull() {
        BusinessArrangement businessArrangement = new BusinessArrangement();
        businessArrangement.setBusinessId(null);
        businessArrangement.setRolesInCtxt("ABS");
        List<BusinessArrangement> businessArrangements = new ArrayList<>();
        businessArrangements.add(businessArrangement);
        businessArrangementHandlerBZ = new BusinessArrangementHandlerBZ(businessArrangements);

        assertFalse(businessArrangementHandlerBZ.hasMBCRole("1234", "CUS"));

    }

    @Test
    public void hasMBCRoleReturnsFalseWhenRoleInCTxtIsNull() {
        BusinessArrangement businessArrangement = new BusinessArrangement();
        businessArrangement.setBusinessId("1234");
        businessArrangement.setRolesInCtxt(null);
        List<BusinessArrangement> businessArrangements = new ArrayList<>();
        businessArrangements.add(businessArrangement);
        businessArrangementHandlerBZ = new BusinessArrangementHandlerBZ(businessArrangements);

        assertFalse(businessArrangementHandlerBZ.hasMBCRole("1234", "CUS"));

    }

    @Test
    public void hasMBCRoleReturnsFalseWhenRoleInCTextDoesNotMatchParamValue() {
        BusinessArrangement businessArrangement = new BusinessArrangement();
        businessArrangement.setBusinessId("1234");
        businessArrangement.setRolesInCtxt("ABS");
        List<BusinessArrangement> businessArrangements = new ArrayList<>();
        businessArrangements.add(businessArrangement);
        businessArrangementHandlerBZ = new BusinessArrangementHandlerBZ(businessArrangements);

        assertFalse(businessArrangementHandlerBZ.hasMBCRole("1234", "CUS"));

    }
}