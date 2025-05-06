package com.lloydsbanking.salsa.activate.converter;

import com.lloydsbanking.salsa.soap.ocis.f062.objects.UnstructuredAddressType;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UnstructuredAddressFactory {
    private static final String UNSTRUCTURED_ADDRESS_POST_CD = "ZZ00ZZ";
    private static final int MAX_LENGTH = 60;

    public UnstructuredAddressType generateUnstructuredAddress(UnstructuredAddress unstructuredAddress, Boolean bfpoAddressIndicator) {
        UnstructuredAddressType unstructuredAddressType = new UnstructuredAddressType();
        if (null != unstructuredAddress) {
            if (bfpoAddressIndicator != null && bfpoAddressIndicator) {
                setUnstructuredAddressForBfpoIndicatorTrue(unstructuredAddress, unstructuredAddressType);
            } else {
                if (atleastOneAddressLinePresent(unstructuredAddress)) {
                    if (null != unstructuredAddress.getAddressLine2()) {
                        setUnstructuredAddressForAddrLine2NotNull(unstructuredAddress, unstructuredAddressType);
                    } else {
                        setUnstructuredAddressForAddrLine2Null(unstructuredAddress, unstructuredAddressType);
                    }
                }
            }
        }
        return unstructuredAddressType;
    }

    private void setUnstructuredAddressForBfpoIndicatorTrue(UnstructuredAddress unstructuredAddress, UnstructuredAddressType unstructuredAddressType) {
        unstructuredAddressType.setAddressLine1Tx(unstructuredAddress.getAddressLine1());
        unstructuredAddressType.setAddressLine2Tx(unstructuredAddress.getAddressLine2());
        unstructuredAddressType.setAddressLine3Tx(unstructuredAddress.getAddressLine3());
        unstructuredAddressType.setAddressLine4Tx(unstructuredAddress.getAddressLine4());
        unstructuredAddressType.setPostCd(UNSTRUCTURED_ADDRESS_POST_CD);
    }

    private void setUnstructuredAddressForAddrLine2NotNull(UnstructuredAddress unstructuredAddress, UnstructuredAddressType unstructuredAddressType) {
        if (null != unstructuredAddress.getAddressLine3()) {
            String combinedAddress = unstructuredAddress.getAddressLine3() + unstructuredAddress.getAddressLine2();
            unstructuredAddressType.setAddressLine1Tx(combinedAddress.length() <= MAX_LENGTH ? combinedAddress : combinedAddress.substring(0, MAX_LENGTH));
        } else {
            unstructuredAddressType.setAddressLine1Tx(unstructuredAddress.getAddressLine2());
        }
        if (null != unstructuredAddress.getAddressLine1()) {
            String combinedAddress = unstructuredAddress.getAddressLine1() + unstructuredAddress.getAddressLine4();
            unstructuredAddressType.setAddressLine2Tx(combinedAddress.length() <= MAX_LENGTH ? combinedAddress : combinedAddress.substring(0, MAX_LENGTH));

        } else {
            unstructuredAddressType.setAddressLine2Tx(unstructuredAddress.getAddressLine4());
        }

        if (null != unstructuredAddress.getAddressLine5()) {
            unstructuredAddressType.setAddressLine3Tx(unstructuredAddress.getAddressLine5());
            unstructuredAddressType.setAddressLine4Tx(unstructuredAddress.getAddressLine6());
        } else {
            unstructuredAddressType.setAddressLine3Tx(unstructuredAddress.getAddressLine6());
            unstructuredAddressType.setAddressLine4Tx(unstructuredAddress.getAddressLine7());

        }
    }

    private void setUnstructuredAddressForAddrLine2Null(UnstructuredAddress unstructuredAddress, UnstructuredAddressType unstructuredAddressType) {

        if (null != unstructuredAddress.getAddressLine3()) {
            String combinedAddress = unstructuredAddress.getAddressLine3() + unstructuredAddress.getAddressLine1() + unstructuredAddress.getAddressLine4();
            unstructuredAddressType.setAddressLine1Tx(combinedAddress.length() <= MAX_LENGTH ? combinedAddress : combinedAddress.substring(0, MAX_LENGTH));
        } else {
            String combinedAddress = unstructuredAddress.getAddressLine1() + unstructuredAddress.getAddressLine4();
            unstructuredAddressType.setAddressLine1Tx(combinedAddress.length() <= MAX_LENGTH ? combinedAddress : combinedAddress.substring(0, MAX_LENGTH));

        }
        if (null != unstructuredAddress.getAddressLine5()) {
            unstructuredAddressType.setAddressLine2Tx(unstructuredAddress.getAddressLine5());
            unstructuredAddressType.setAddressLine3Tx(unstructuredAddress.getAddressLine6());
            unstructuredAddressType.setAddressLine4Tx(unstructuredAddress.getAddressLine7());
        } else {
            unstructuredAddressType.setAddressLine2Tx(unstructuredAddress.getAddressLine6());
            unstructuredAddressType.setAddressLine3Tx(unstructuredAddress.getAddressLine7());
        }
    }

    private boolean atleastOneAddressLinePresent(UnstructuredAddress unstructuredAddress) {
        List<String> addressLineList = new ArrayList<>();
        addressLineList.add(unstructuredAddress.getAddressLine1());
        addressLineList.add(unstructuredAddress.getAddressLine2());
        addressLineList.add(unstructuredAddress.getAddressLine3());
        addressLineList.add(unstructuredAddress.getAddressLine4());
        addressLineList.add(unstructuredAddress.getAddressLine5());
        addressLineList.add(unstructuredAddress.getAddressLine6());
        addressLineList.add(unstructuredAddress.getAddressLine7());
        addressLineList.add(unstructuredAddress.getAddressLine8());
        for (String addressLine : addressLineList) {
            if (addressLine != null) {
                return true;
            }
        }
        return false;
    }
}
