package com.lloydsbanking.salsa.offer.createinvolvedparty.convert;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.StructuredAddressType;
import lib_sim_bo.businessobjects.StructuredAddress;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;


@Category(UnitTest.class)
public class StructuredAddressFactoryTest {

    @Test
    public void testGenerateUnstructuredAddress() {
        StructuredAddress address = new TestDataHelper().createStructuredAddress();
        StructuredAddressType structuredAddressType = new StructuredAddressFactory().generateStructuredAddress(address);
        assertEquals(address.getOrganisation(), structuredAddressType.getOrganisationNm());
        assertEquals(address.getSubBuilding(), structuredAddressType.getSubBuildingNm());
        assertEquals(address.getBuilding(), structuredAddressType.getBuildingNm());
        assertEquals(address.getBuildingNumber(), structuredAddressType.getBuildingNo());
        assertEquals(address.getDistrict(), structuredAddressType.getAddressDistrictNm());
        assertEquals(address.getPostTown(), structuredAddressType.getAddressPostTownNm());
        assertEquals(address.getCountry(), structuredAddressType.getAddressCountyNm());
        assertEquals(address.getPostCodeOut(), structuredAddressType.getOutPostCd());
        assertEquals(address.getPostCodeIn(), structuredAddressType.getInPostCd());
        assertEquals(address.getPointSuffix(), structuredAddressType.getDelivPointSuffixCd());
        assertEquals("PARK STREET", structuredAddressType.getAddressLinePaf().get(0).getAddressLinePafTx());
    }


}
