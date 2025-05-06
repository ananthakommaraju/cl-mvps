package com.lloydsbanking.salsa.offer.createinvolvedparty.convert;

import com.lloydsbanking.salsa.soap.ocis.f062.objects.PhoneUpdDataType;
import lib_sim_bo.businessobjects.TelephoneNumber;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PhoneUpdDataFactory {
    private static final String TELEPHONE_TYPE_PERSONAL = "1";

    private static final int PARTY_PHONE_TYPE_PERSONAL = 1;

    private static final String TELEPHONE_TYPE_OFFICIAL = "4";

    private static final int PARTY_PHONE_TYPE_OFFICIAL = 3;

    private static final int PARTY_PHONE_TYPE_OTHER = 5;

    private static final String DEVICE_TYPE_FIXED = "Fixed";

    private static final int DEVICE_TYPE_OTHER = 004;

    private static final int DEVICE_TYPE_FIXED_CODE = 001;

    public List<PhoneUpdDataType> generatePhoneUpdData(List<TelephoneNumber> telephoneNumbers) {
        List<PhoneUpdDataType> phoneUpdDataList = new ArrayList<>();
        for (TelephoneNumber telephoneNumber : telephoneNumbers) {
            PhoneUpdDataType phoneUpdDataType = new PhoneUpdDataType();
            if (!StringUtils.isEmpty(telephoneNumber.getTelephoneType())) {
                phoneUpdDataType.setPartyPhoneTypeCd(getPartyPhoneTypeCode(telephoneNumber.getTelephoneType()));
            }
            if (!StringUtils.isEmpty(telephoneNumber.getDeviceType())) {
                phoneUpdDataType.setPhoneDeviceTypeCd(getPhoneDevicetypeCode(telephoneNumber.getDeviceType()));
            }
            phoneUpdDataType.setPhoneCountryCd(telephoneNumber.getCountryPhoneCode());
            phoneUpdDataType.setPhoneAreaCd(telephoneNumber.getAreaCode());
            phoneUpdDataType.setPhoneSubscriberNo(telephoneNumber.getPhoneNumber());
            phoneUpdDataType.setPhoneExtensionNo(telephoneNumber.getExtensionNumber());
            phoneUpdDataType.setContactPreferenceCd(telephoneNumber.getContactPreference());
            phoneUpdDataList.add(phoneUpdDataType);
        }
        return phoneUpdDataList;
    }

    private int getPhoneDevicetypeCode(String deviceType) {
        switch (deviceType) {
            case (DEVICE_TYPE_FIXED):
                return DEVICE_TYPE_FIXED_CODE;
            default:
                return DEVICE_TYPE_OTHER;
        }
    }

    private int getPartyPhoneTypeCode(String telephoneType) {
        switch (telephoneType) {
            case (TELEPHONE_TYPE_PERSONAL):
                return PARTY_PHONE_TYPE_PERSONAL;
            case (TELEPHONE_TYPE_OFFICIAL):
                return PARTY_PHONE_TYPE_OFFICIAL;
            default:
                return PARTY_PHONE_TYPE_OTHER;
        }

    }
}
