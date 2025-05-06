package com.lloydsbanking.salsa.offer.createinvolvedparty.convert;


import com.lloydsbanking.salsa.soap.ocis.f062.objects.AddressLinePafType;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.StructuredAddressType;
import lib_sim_bo.businessobjects.StructuredAddress;

import java.util.ArrayList;
import java.util.List;

public class StructuredAddressFactory {
    public StructuredAddressType generateStructuredAddress(StructuredAddress address) {
        StructuredAddressType structuredAddressType = new StructuredAddressType();
        structuredAddressType.setOrganisationNm(address.getOrganisation());
        structuredAddressType.setSubBuildingNm(address.getSubBuilding());
        structuredAddressType.setBuildingNm(address.getBuilding());
        structuredAddressType.setBuildingNo(address.getBuildingNumber());
        structuredAddressType.setAddressDistrictNm(address.getDistrict());
        structuredAddressType.setAddressPostTownNm(address.getPostTown());
        structuredAddressType.setAddressCountyNm(address.getCountry());
        structuredAddressType.setOutPostCd(address.getPostCodeOut());
        structuredAddressType.setInPostCd(address.getPostCodeIn());
        structuredAddressType.setDelivPointSuffixCd(address.getPointSuffix());
        structuredAddressType.getAddressLinePaf().addAll(getAddressTypePAF(address.getAddressLinePAFData()));
        return structuredAddressType;
    }

    private List<AddressLinePafType> getAddressTypePAF(List<String> addressLinePAFData) {
        List<AddressLinePafType> addressLinePafTypes = new ArrayList<>();
        AddressLinePafType addressLinePafType = new AddressLinePafType();
        for (String addressLine : addressLinePAFData) {
            addressLinePafType.setAddressLinePafTx(addressLine);
            addressLinePafTypes.add(addressLinePafType);
        }
        return addressLinePafTypes;
    }
}
