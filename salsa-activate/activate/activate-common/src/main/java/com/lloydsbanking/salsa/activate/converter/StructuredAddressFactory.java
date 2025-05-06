package com.lloydsbanking.salsa.activate.converter;


import com.lloydsbanking.salsa.soap.ocis.f062.objects.AddressLinePafType;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.StructuredAddressType;
import lib_sim_bo.businessobjects.StructuredAddress;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StructuredAddressFactory {
    public StructuredAddressType generateStructuredAddress(StructuredAddress address) {
        StructuredAddressType structuredAddressType = new StructuredAddressType();
        structuredAddressType.setOrganisationNm(address.getOrganisation());
        structuredAddressType.setSubBuildingNm(address.getSubBuilding());
        structuredAddressType.setBuildingNm(address.getBuildingNumber());
        structuredAddressType.setAddressDistrictNm(address.getDistrict());
        structuredAddressType.setAddressPostTownNm(address.getPostTown());
        structuredAddressType.setAddressCountyNm(address.getCountry());
        structuredAddressType.setOutPostCd(address.getPostCodeOut());
        structuredAddressType.setInPostCd(address.getPostCodeIn());
        structuredAddressType.setDelivPointSuffixCd(address.getPointSuffix());
        structuredAddressType.getAddressLinePaf().add(getAddressTypePAF(address.getAddressLinePAFData()));
        return structuredAddressType;
    }

    private AddressLinePafType getAddressTypePAF(List<String> addressLinePAFData) {
        AddressLinePafType addressLinePafType = new AddressLinePafType();
        for (String addressLine : addressLinePAFData) {
            addressLinePafType.setAddressLinePafTx(addressLine);
        }
        return addressLinePafType;
    }
}
