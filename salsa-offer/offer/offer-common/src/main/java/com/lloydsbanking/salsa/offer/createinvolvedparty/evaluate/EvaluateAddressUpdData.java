package com.lloydsbanking.salsa.offer.createinvolvedparty.evaluate;


import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.AddressUpdDataType;
import lib_sim_bo.businessobjects.PostalAddress;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class EvaluateAddressUpdData {

    private static final String ADDRESS_TYPE_CURRENT = "CURRENT";

    private static final String ADDRESS_TYPE_CODE_PREVIOUS = "002";

    private static final String ADDRESS_TYPE_CODE_CURRENT = "001";

    public static final String DATE_FORMAT = "ddMMyyyy";

    @Autowired
    DateFactory dateFactory;

    public void generateAddressUpdData(List<PostalAddress> postalAddressList, AddressUpdDataType addressUpdDataType) throws ParseException {
        if (!postalAddressList.isEmpty()) {
            for (PostalAddress postalAddress : postalAddressList) {
                if (ADDRESS_TYPE_CURRENT.equalsIgnoreCase(postalAddress.getStatusCode())) {
                    getAmendmendEffectiveDate(addressUpdDataType, postalAddress.getEffectiveFrom(), postalAddress.getDurationofStay());
                    addressUpdDataType.setAddressStatusCd(ADDRESS_TYPE_CODE_CURRENT);
                    addressUpdDataType.setAddressCareOfNm(postalAddress.getCareOfName());
                    break;
                } else {
                    addressUpdDataType.setAddressStatusCd(ADDRESS_TYPE_CODE_PREVIOUS);
                    addressUpdDataType.setAddressCareOfNm(postalAddress.getCareOfName());
                }
            }
        }
    }

    private void getAmendmendEffectiveDate(AddressUpdDataType addressUpdDataType, XMLGregorianCalendar effectiveFrom, String durationofStay) throws ParseException {
        if (null != effectiveFrom) {
            addressUpdDataType.setAmdEffDt(getDateInDdMMYyyyFormat(effectiveFrom));
        } else if (!StringUtils.isEmpty(durationofStay)) {
            addressUpdDataType.setAmdEffDt(convertDurationToStringDate(durationofStay));
        }
    }

    private String getDateInDdMMYyyyFormat(XMLGregorianCalendar date) throws ParseException {
        return new SimpleDateFormat(DATE_FORMAT).format(DateFactory.toDate(date));
    }

    private String convertDurationToStringDate(String durationOfStay) {
        return dateFactory.convertDurationYYMMToStringDateFormat(durationOfStay, DATE_FORMAT);
    }
}
