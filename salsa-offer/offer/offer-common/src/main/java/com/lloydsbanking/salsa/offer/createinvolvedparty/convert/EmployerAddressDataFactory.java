package com.lloydsbanking.salsa.offer.createinvolvedparty.convert;


import com.lloydsbanking.salsa.soap.ocis.f062.objects.EmployersAddrUpdDataType;
import lib_sim_bo.businessobjects.UnstructuredAddress;

public class EmployerAddressDataFactory {

    public EmployersAddrUpdDataType generateEmployerAddress(UnstructuredAddress address) {
        if(null != address) {
            EmployersAddrUpdDataType employersAddrUpdDataType = new EmployersAddrUpdDataType();
            employersAddrUpdDataType.setAddressLine1Tx(address.getAddressLine1());
            employersAddrUpdDataType.setAddressLine2Tx(address.getAddressLine2());
            employersAddrUpdDataType.setAddressLine3Tx(address.getAddressLine3());
            employersAddrUpdDataType.setAddressLine4Tx(address.getAddressLine4());
            employersAddrUpdDataType.setAddressLine5Tx(address.getAddressLine5());
            employersAddrUpdDataType.setAddressLine6Tx(address.getAddressLine6());
            employersAddrUpdDataType.setAddressLine7Tx(address.getAddressLine7());
            employersAddrUpdDataType.setPostCd(address.getPostCode());
            return employersAddrUpdDataType;
        }
        return null;
    }
}
