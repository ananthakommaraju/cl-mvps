package com.lloydsbanking.salsa.activate.converter;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.StructuredAddressType;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class StructuredAddressFactoryTest {

    StructuredAddressFactory structuredAddressFactory;
    StructuredAddressType structuredAddressType;
    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        structuredAddressFactory = new StructuredAddressFactory();
        structuredAddressType = null;
        testDataHelper = new TestDataHelper();
    }

    @Test
    public void testGenerateStructuredAddress() {
        structuredAddressType = structuredAddressFactory.generateStructuredAddress(testDataHelper.getStructureAddress());
        assertEquals("SA", structuredAddressType.getOrganisationNm());
        assertEquals("subBuilding", structuredAddressType.getSubBuildingNm());
        assertEquals("b1", structuredAddressType.getBuildingNm());
    }

}
