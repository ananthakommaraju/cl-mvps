package com.lloydsbanking.salsa.offer.createinvolvedparty.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.PhoneUpdDataType;
import lib_sim_bo.businessobjects.TelephoneNumber;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class PhoneUpdDataFactoryTest {

    @Test
    public void generatePhoneUpdDataTest() {
        List<TelephoneNumber> telephoneNumbers = new TestDataHelper().createTelephoneNumber();
        List<PhoneUpdDataType> phoneUpdDataList = new PhoneUpdDataFactory().generatePhoneUpdData(telephoneNumbers);
        TelephoneNumber telephoneNumber = telephoneNumbers.get(0);
        PhoneUpdDataType phoneUpdDataType = phoneUpdDataList.get(0);
        assertEquals(telephoneNumber.getCountryPhoneCode(), phoneUpdDataType.getPhoneCountryCd());
        assertEquals(telephoneNumber.getAreaCode(), phoneUpdDataType.getPhoneAreaCd());
        assertEquals(telephoneNumber.getPhoneNumber(), phoneUpdDataType.getPhoneSubscriberNo());
        assertEquals(telephoneNumber.getExtensionNumber(), phoneUpdDataType.getPhoneExtensionNo());
        assertEquals(telephoneNumber.getContactPreference(), phoneUpdDataType.getContactPreferenceCd());
        assertEquals(5, phoneUpdDataType.getPartyPhoneTypeCd());
        assertEquals(4, phoneUpdDataType.getPhoneDeviceTypeCd());

    }

    @Test
    public void testGeneratePhoneUpdDataWhenTelTypePersonal() {
        List<TelephoneNumber> telephoneNumbers = new TestDataHelper().createTelephoneNumber();
        telephoneNumbers.get(0).setDeviceType("Fixed");
        telephoneNumbers.get(0).setTelephoneType("1");
        List<PhoneUpdDataType> phoneUpdDataList = new PhoneUpdDataFactory().generatePhoneUpdData(telephoneNumbers);
        TelephoneNumber telephoneNumber = telephoneNumbers.get(0);
        PhoneUpdDataType phoneUpdDataType = phoneUpdDataList.get(0);
        assertEquals(telephoneNumber.getCountryPhoneCode(), phoneUpdDataType.getPhoneCountryCd());
        assertEquals(telephoneNumber.getAreaCode(), phoneUpdDataType.getPhoneAreaCd());
        assertEquals(telephoneNumber.getPhoneNumber(), phoneUpdDataType.getPhoneSubscriberNo());
        assertEquals(telephoneNumber.getExtensionNumber(), phoneUpdDataType.getPhoneExtensionNo());
        assertEquals(telephoneNumber.getContactPreference(), phoneUpdDataType.getContactPreferenceCd());
        assertEquals(1, phoneUpdDataType.getPartyPhoneTypeCd());
        assertEquals(1, phoneUpdDataType.getPhoneDeviceTypeCd());
    }

    @Test
    public void testGeneratePhoneUpdDataWhenTelTypeOfficial() {
        List<TelephoneNumber> telephoneNumbers = new TestDataHelper().createTelephoneNumber();
        telephoneNumbers.get(0).setDeviceType("Fixed");
        telephoneNumbers.get(0).setTelephoneType("4");
        List<PhoneUpdDataType> phoneUpdDataList = new PhoneUpdDataFactory().generatePhoneUpdData(telephoneNumbers);
        TelephoneNumber telephoneNumber = telephoneNumbers.get(0);
        PhoneUpdDataType phoneUpdDataType = phoneUpdDataList.get(0);
        assertEquals(telephoneNumber.getCountryPhoneCode(), phoneUpdDataType.getPhoneCountryCd());
        assertEquals(telephoneNumber.getAreaCode(), phoneUpdDataType.getPhoneAreaCd());
        assertEquals(telephoneNumber.getPhoneNumber(), phoneUpdDataType.getPhoneSubscriberNo());
        assertEquals(telephoneNumber.getExtensionNumber(), phoneUpdDataType.getPhoneExtensionNo());
        assertEquals(telephoneNumber.getContactPreference(), phoneUpdDataType.getContactPreferenceCd());
        assertEquals(3, phoneUpdDataType.getPartyPhoneTypeCd());
        assertEquals(1, phoneUpdDataType.getPhoneDeviceTypeCd());
    }

}
