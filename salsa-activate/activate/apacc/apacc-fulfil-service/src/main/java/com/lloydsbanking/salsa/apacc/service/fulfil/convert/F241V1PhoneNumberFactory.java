package com.lloydsbanking.salsa.apacc.service.fulfil.convert;


import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.OwnerContactData;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.OwnerData;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.TelephoneNumber;
import org.springframework.stereotype.Component;

@Component
public class F241V1PhoneNumberFactory {
    private static final String PHONE_CODE_HOME_PHONE = "1";
    private static final String PHONE_CODE_OFFICE_PHONE = "4";
    private static final String PHONE_CODE_MOBILE_PHONE = "7";
    private static final String COUNTRY_PHONE_CODE = "44";
    private static final int PHONE_NUMBER_LENGTH = 20;


    public void getPhoneDetails(Customer customer, OwnerData ownerData, OwnerContactData ownerContactData) {
        for (TelephoneNumber telephoneNumber : customer.getTelephoneNumber()) {

            if ((PHONE_CODE_HOME_PHONE).equalsIgnoreCase(telephoneNumber.getTelephoneType())) {
                if (telephoneNumber.getCountryPhoneCode() != null) {
                    ownerData.setHomePhoneNo(getPhone(telephoneNumber));
                }
                ownerContactData.setTelephoneStatusCd("1");
            } else if ((PHONE_CODE_MOBILE_PHONE).equalsIgnoreCase(telephoneNumber.getTelephoneType())) {
                if (telephoneNumber.getCountryPhoneCode() != null) {
                    ownerData.setMobilePhoneNo(getMobilePhone(telephoneNumber));
                }
                ownerContactData.setMobileStatusCd("1");
            } else if ((PHONE_CODE_OFFICE_PHONE).equalsIgnoreCase(telephoneNumber.getTelephoneType())) {
                if (telephoneNumber.getCountryPhoneCode() != null) {
                    ownerData.setOfficePhoneNo(getPhone(telephoneNumber));
                }
                ownerContactData.setPhoneStatusCd("1");
            }

        }
    }

    private String getPhone(TelephoneNumber telephoneNumber) {
        if (COUNTRY_PHONE_CODE.equalsIgnoreCase(telephoneNumber.getCountryPhoneCode())) {
            return getPhoneNumberForInCountry(telephoneNumber);
        } else {
            return getPhoneNumberForOutCountry(telephoneNumber);
        }
    }

    private String getMobilePhone(TelephoneNumber telephoneNumber) {
        if (COUNTRY_PHONE_CODE.equalsIgnoreCase(telephoneNumber.getCountryPhoneCode())) {
            if (telephoneNumber.getPhoneNumber().startsWith("0")) {
                return telephoneNumber.getPhoneNumber();
            } else {
                return "0".concat(telephoneNumber.getPhoneNumber());
            }
        } else {
            if (telephoneNumber.getPhoneNumber().startsWith("0")) {
                return telephoneNumber.getCountryPhoneCode().concat(telephoneNumber.getPhoneNumber().substring(1));
            } else {
                return telephoneNumber.getCountryPhoneCode().concat(telephoneNumber.getPhoneNumber());
            }
        }
    }

    private String getPhoneNumberForInCountry(TelephoneNumber telephoneNumber) {
        if (telephoneNumber.getAreaCode().length() + telephoneNumber.getPhoneNumber().length() == PHONE_NUMBER_LENGTH) {
            return (telephoneNumber.getAreaCode().concat(telephoneNumber.getPhoneNumber()));
        } else {
            if (telephoneNumber.getAreaCode().startsWith("0")) {
                return (telephoneNumber.getAreaCode().concat(telephoneNumber.getPhoneNumber()));
            } else {
                return ("0".concat(telephoneNumber.getAreaCode()).concat(telephoneNumber.getPhoneNumber()));
            }
        }

    }

    private String getPhoneNumberForOutCountry(TelephoneNumber telephoneNumber) {
        int length = telephoneNumber.getCountryPhoneCode().length() + telephoneNumber.getAreaCode().length() + telephoneNumber.getPhoneNumber().length();
        if (length == PHONE_NUMBER_LENGTH) {
            return getPhoneNumberCommon(telephoneNumber);
        } else if (length > PHONE_NUMBER_LENGTH) {
            if (telephoneNumber.getAreaCode().startsWith("0")) {
                return (telephoneNumber.getAreaCode().concat(telephoneNumber.getPhoneNumber()));
            } else {
                if (telephoneNumber.getAreaCode().length() + telephoneNumber.getPhoneNumber().length() == PHONE_NUMBER_LENGTH) {
                    return (telephoneNumber.getAreaCode().concat(telephoneNumber.getPhoneNumber()));
                } else {
                    return ("0".concat(telephoneNumber.getAreaCode()).concat(telephoneNumber.getPhoneNumber()));

                }
            }
        } else {
            return getPhoneNumberCommon(telephoneNumber);
        }

    }

    private String getPhoneNumberCommon(TelephoneNumber telephoneNumber) {
        if (telephoneNumber.getAreaCode().startsWith("0")) {
            return (telephoneNumber.getCountryPhoneCode().concat(telephoneNumber.getAreaCode().substring(1)).concat(telephoneNumber.getPhoneNumber()));
        } else {
            return (telephoneNumber.getCountryPhoneCode().concat(telephoneNumber.getAreaCode()).concat(telephoneNumber.getPhoneNumber()));
        }
    }
}
