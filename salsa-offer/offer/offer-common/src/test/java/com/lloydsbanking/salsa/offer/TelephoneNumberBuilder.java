package com.lloydsbanking.salsa.offer;

import lib_sim_bo.businessobjects.TelephoneNumber;

public class TelephoneNumberBuilder {
    TelephoneNumber telephoneNumber;

    public TelephoneNumberBuilder() {
        telephoneNumber = new TelephoneNumber();
    }

    public TelephoneNumber build() {
        return telephoneNumber;
    }

    public TelephoneNumberBuilder countryPhoneCode(String countryPhoneCode) {
        telephoneNumber.setCountryPhoneCode(countryPhoneCode);
        return this;
    }

    public TelephoneNumberBuilder phoneNumber(String phoneNumber) {
        telephoneNumber.setPhoneNumber(phoneNumber);
        return this;
    }

    public TelephoneNumberBuilder telephoneType(String telephoneType) {
        telephoneNumber.setTelephoneType(telephoneType);
        return this;
    }

    public TelephoneNumberBuilder deviceType(String deviceType) {
        telephoneNumber.setDeviceType(deviceType);
        return this;
    }
}
