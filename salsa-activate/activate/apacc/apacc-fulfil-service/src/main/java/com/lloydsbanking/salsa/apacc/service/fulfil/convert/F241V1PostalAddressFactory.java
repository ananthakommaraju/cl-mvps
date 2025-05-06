package com.lloydsbanking.salsa.apacc.service.fulfil.convert;


import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.OwnerData;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.OwnerPersonalData;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_bo.businessobjects.StructuredAddress;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class F241V1PostalAddressFactory {

    private static final String CURRENT_POSTAL_ADDRESS = "CURRENT";
    private static final String COUNTRY_UNITED_KINGDOM = "United Kingdom";
    private static final String COUNTRY_CODE_GBR = "GBR";
    private static final int ADDRESS_LENGTH_20 = 20;
    private static final int ADDRESS_LENGTH_40 = 40;
    private static final int ADDRESS_LENGTH_30 = 30;
    private static final int ADDRESS_LINE_1 = 1;
    private static final int ADDRESS_LINE_2 = 2;
    private static final int ADDRESS_LINE_3 = 3;
    private static final int ADDRESS_LINE_4 = 4;
    private static final int ADDRESS_LINE_5 = 5;
    private static final int ADDRESS_LINE_6 = 6;
    private static final int ADDRESS_SIZE_COUNT_1 = 1;
    private static final int ADDRESS_SIZE_COUNT_2 = 2;
    private static final int ADDRESS_SIZE_COUNT_3 = 3;
    private static final int ADDRESS_SIZE_COUNT_4 = 4;
    private static final int ADDRESS_SIZE_COUNT_5 = 5;
    private static final int ADDRESS_SIZE_COUNT_6 = 6;
    private static final int ADDRESS_SIZE_COUNT_7 = 7;

    public void getPostalAddress(Customer customer, OwnerData ownerData, OwnerPersonalData ownerPersonalData) {
        for (PostalAddress postalAddress : customer.getPostalAddress()) {
            if (CURRENT_POSTAL_ADDRESS.equalsIgnoreCase(postalAddress.getStatusCode())) {
                if (postalAddress.isIsPAFFormat() != null && postalAddress.isIsPAFFormat()) {
                    setDataForStructuredAddress(ownerData, ownerPersonalData, postalAddress.getStructuredAddress());
                } else {
                    setDataForUnstructuredAddress(customer, ownerData, ownerPersonalData, postalAddress.getUnstructuredAddress());
                }
            }
        }
    }

    private void setDataForUnstructuredAddress(Customer customer, OwnerData ownerData, OwnerPersonalData ownerPersonalData, UnstructuredAddress unstructuredAddress) {
        if (customer.isIsAuthCustomer()) {
            setUnstructuredAddress(ownerData, ownerPersonalData, unstructuredAddress);
        } else {
            setOwnerData(getSubString(ADDRESS_LENGTH_40, unstructuredAddress.getAddressLine4()), null, getSubString(ADDRESS_LENGTH_30, unstructuredAddress.getAddressLine6()), ownerData);
            if (unstructuredAddress.getAddressLine2() != null) {
                ownerPersonalData.setBuildingNm(unstructuredAddress.getAddressLine2());
            }
            if (unstructuredAddress.getAddressLine7() != null) {
                ownerPersonalData.setAddressCountyNm(getSubString(ADDRESS_LENGTH_30, unstructuredAddress.getAddressLine7()));
            }
            if (StringUtils.isEmpty(unstructuredAddress.getAddressLine1())) {
                ownerPersonalData.setBuildingNo(unstructuredAddress.getAddressLine1());
            } else if (StringUtils.isEmpty(unstructuredAddress.getAddressLine3())) {
                ownerPersonalData.setBuildingNo(unstructuredAddress.getAddressLine3());
            }
        }
        ownerData.setPostCd(unstructuredAddress.getPostCode());
        if (unstructuredAddress.getAddressLine8() != null && COUNTRY_UNITED_KINGDOM.equalsIgnoreCase(unstructuredAddress.getAddressLine8())) {
            ownerData.setCountryCd(COUNTRY_CODE_GBR);
        }
    }

    private void setDataForStructuredAddress(OwnerData ownerData, OwnerPersonalData ownerPersonalData, StructuredAddress structuredAddress) {
        if (!structuredAddress.getAddressLinePAFData().isEmpty()) {
            setAddressStructured(ownerData, ownerPersonalData, structuredAddress);
        }
        ownerData.setCityNm(structuredAddress.getPostTown());
        ownerData.setPostCd(structuredAddress.getPostCodeOut() + " " + structuredAddress.getPostCodeIn());
        if (structuredAddress.getSubBuilding() == null) {
            ownerPersonalData.setBuildingNm(structuredAddress.getBuilding());
            ownerPersonalData.setBuildingNo(structuredAddress.getBuildingNumber());
        } else {
            if (structuredAddress.getBuildingNumber() != null) {
                ownerPersonalData.setBuildingNo(structuredAddress.getBuildingNumber());
                if (structuredAddress.getBuilding() != null) {
                    ownerPersonalData.setBuildingNm(structuredAddress.getSubBuilding() + " " + structuredAddress.getBuilding());
                } else {
                    ownerPersonalData.setBuildingNm(structuredAddress.getSubBuilding());
                }
            } else {
                String subBuilding = structuredAddress.getSubBuilding().length() > ADDRESS_LENGTH_20 ? structuredAddress.getSubBuilding().substring(0, ADDRESS_LENGTH_20) : structuredAddress.getSubBuilding();
                ownerPersonalData.setBuildingNo(subBuilding);
                ownerPersonalData.setBuildingNm(structuredAddress.getSubBuilding());
            }
        }
        ownerPersonalData.setAddressCountyNm(structuredAddress.getCounty());
        ownerPersonalData.setDelivPointSuffixCd(structuredAddress.getPointSuffix());
        ownerData.setCountryCd("GBR");
    }

    private void setUnstructuredAddress(OwnerData ownerData, OwnerPersonalData ownerPersonalData, UnstructuredAddress unstructuredAddress) {
        List<String> addressList = getUnstructuredAddressList(unstructuredAddress);
        int size = addressList.size();
        if (size == ADDRESS_SIZE_COUNT_7) {
            setOwnerData(getSubString(ADDRESS_LENGTH_40, addressList.get(ADDRESS_LINE_1)), getSubString(ADDRESS_LENGTH_40, addressList.get(ADDRESS_LINE_2)), getSubString(ADDRESS_LENGTH_40, addressList.get(ADDRESS_LINE_5)), ownerData);
            setOwnerPersonalData(getSubString(ADDRESS_LENGTH_40, addressList.get(ADDRESS_LINE_3)), getSubString(ADDRESS_LENGTH_40, addressList.get(ADDRESS_LINE_4)), getSubString(ADDRESS_LENGTH_40, addressList.get(ADDRESS_LINE_6)), ownerPersonalData);
            ownerPersonalData.setBuildingNm(getSubString(ADDRESS_LENGTH_40, addressList.get(0)));
        } else if (size == ADDRESS_SIZE_COUNT_6) {
            setOwnerData(getSubString(ADDRESS_LENGTH_40, addressList.get(0)), getSubString(ADDRESS_LENGTH_40, addressList.get(ADDRESS_LINE_1)), getSubString(ADDRESS_LENGTH_30, addressList.get(ADDRESS_LINE_4)), ownerData);
            setOwnerPersonalData(getSubString(ADDRESS_LENGTH_40, addressList.get(ADDRESS_LINE_2)), getSubString(ADDRESS_LENGTH_40, addressList.get(ADDRESS_LINE_3)), getSubString(ADDRESS_LENGTH_30, addressList.get(ADDRESS_LINE_5)), ownerPersonalData);
        } else if (size == ADDRESS_SIZE_COUNT_5) {
            setOwnerData(getSubString(ADDRESS_LENGTH_40, addressList.get(0)), getSubString(ADDRESS_LENGTH_40, addressList.get(ADDRESS_LINE_1)), getSubString(ADDRESS_LENGTH_30, addressList.get(ADDRESS_LINE_4)), ownerData);
            setOwnerPersonalData(getSubString(ADDRESS_LENGTH_40, addressList.get(ADDRESS_LINE_2)), getSubString(ADDRESS_LENGTH_40, addressList.get(ADDRESS_LINE_3)), null, ownerPersonalData);
        } else if (size == ADDRESS_SIZE_COUNT_4) {
            setOwnerData(getSubString(ADDRESS_LENGTH_40, addressList.get(0)), getSubString(ADDRESS_LENGTH_40, addressList.get(ADDRESS_LINE_1)), null, ownerData);
            setOwnerPersonalData(getSubString(ADDRESS_LENGTH_40, addressList.get(ADDRESS_LINE_2)), getSubString(ADDRESS_LENGTH_40, addressList.get(ADDRESS_LINE_3)), null, ownerPersonalData);
        } else if (size == ADDRESS_SIZE_COUNT_3) {
            setOwnerData(getSubString(ADDRESS_LENGTH_40, addressList.get(0)), getSubString(ADDRESS_LENGTH_40, addressList.get(ADDRESS_LINE_1)), null, ownerData);
            setOwnerPersonalData(getSubString(ADDRESS_LENGTH_40, addressList.get(ADDRESS_LINE_2)), null, null, ownerPersonalData);
        } else if (size == ADDRESS_SIZE_COUNT_2) {
            setOwnerData(getSubString(ADDRESS_LENGTH_40, addressList.get(0)), getSubString(ADDRESS_LENGTH_40, addressList.get(ADDRESS_LINE_1)), null, ownerData);
        } else if (size == ADDRESS_SIZE_COUNT_1) {
            setOwnerData(getSubString(ADDRESS_LENGTH_40, addressList.get(0)), null, null, ownerData);
        }
    }

    private List<String> getUnstructuredAddressList(UnstructuredAddress unstructuredAddress) {
        List<String> addressList = new ArrayList<>();
        if (!StringUtils.isEmpty(unstructuredAddress.getAddressLine1())) {
            addressList.add(unstructuredAddress.getAddressLine1());
        }
        if (!StringUtils.isEmpty(unstructuredAddress.getAddressLine2())) {
            addressList.add(unstructuredAddress.getAddressLine2());
        }
        if (!StringUtils.isEmpty(unstructuredAddress.getAddressLine3())) {
            addressList.add(unstructuredAddress.getAddressLine3());
        }
        if (!StringUtils.isEmpty(unstructuredAddress.getAddressLine4())) {
            addressList.add(unstructuredAddress.getAddressLine4());
        }
        if (!StringUtils.isEmpty(unstructuredAddress.getAddressLine5())) {
            addressList.add(unstructuredAddress.getAddressLine5());
        }
        if (!StringUtils.isEmpty(unstructuredAddress.getAddressLine6())) {
            addressList.add(unstructuredAddress.getAddressLine6());
        }
        if (!StringUtils.isEmpty(unstructuredAddress.getAddressLine7())) {
            addressList.add(unstructuredAddress.getAddressLine7());
        }
        return addressList;
    }

    private void setAddressStructured(OwnerData ownerData, OwnerPersonalData ownerPersonalData, StructuredAddress structuredAddress) {
        int size = structuredAddress.getAddressLinePAFData().size();
        List<String> addressList = structuredAddress.getAddressLinePAFData();
        String district = structuredAddress.getDistrict();
        if (size > ADDRESS_SIZE_COUNT_3) {
            setOwnerData(addressList.get(0), addressList.get(ADDRESS_LINE_1), null, ownerData);
            setOwnerPersonalData(addressList.get(ADDRESS_LINE_2), addressList.get(ADDRESS_LINE_3), null, ownerPersonalData);
        } else if (size == ADDRESS_SIZE_COUNT_3) {
            setOwnerData(addressList.get(0), addressList.get(ADDRESS_LINE_1), null, ownerData);
            setOwnerPersonalData(addressList.get(ADDRESS_LINE_2), district, null, ownerPersonalData);
        } else if (size == ADDRESS_SIZE_COUNT_2) {
            setOwnerData(addressList.get(0), addressList.get(ADDRESS_LINE_1), null, ownerData);
            setOwnerPersonalData(district, null, null, ownerPersonalData);
        } else if (size == ADDRESS_SIZE_COUNT_1) {
            setOwnerData(addressList.get(0), district, null, ownerData);
        }
    }

    private void setOwnerPersonalData(String addressLine1, String addressLine2, String countryName, OwnerPersonalData ownerPersonalData) {
        if (!StringUtils.isEmpty(addressLine1)) {
            ownerPersonalData.setAddressLine1Tx(addressLine1);
        }
        if (!StringUtils.isEmpty(addressLine2)) {
            ownerPersonalData.setAddressLine2Tx(addressLine2);
        }
        if (!StringUtils.isEmpty(countryName)) {
            ownerPersonalData.setAddressCountyNm(countryName);
        }
    }
    private void setOwnerData(String addressLine1, String addressLine2, String cityName, OwnerData ownerData) {
        if (!StringUtils.isEmpty(addressLine1)) {
            ownerData.setAddressLine1Tx(addressLine1);
        }
        if (!StringUtils.isEmpty(addressLine2)) {
            ownerData.setAddressLine2Tx(addressLine2);
        }
        if (!StringUtils.isEmpty(cityName)) {
            ownerData.setCityNm(cityName);
        }
    }
    private String getSubString(int size, String name) {
        if (!StringUtils.isEmpty(name)) {
            return (name.length() > size) ? name.substring(0, size) : name;
        }
        return null;
    }
}