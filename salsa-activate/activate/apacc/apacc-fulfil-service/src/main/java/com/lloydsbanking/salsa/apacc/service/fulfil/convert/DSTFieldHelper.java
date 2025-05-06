package com.lloydsbanking.salsa.apacc.service.fulfil.convert;

import com.lloydsbanking.salsa.date.DateFactory;
import lib_sbo_cardacquire.businessojects.Field;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;

@Component
public class DSTFieldHelper {

    private static final String STATUS_YES = "Y";
    private static final String STATUS_NO = "N";
    private static final String CCA_SEQ = "001";

    public Field getFieldForStringValue(String key, String value) {
        String finalValue = value != null ? value : "";
        return getField(key, finalValue);
    }

    public Field getFieldForBooleanValue(String key, Boolean value) {
        String finalValue = (value != null && value) ? STATUS_YES : STATUS_NO;
        return getField(key, finalValue);
    }

    public Field getFieldForDateValue(String key, XMLGregorianCalendar value, String format, String defaultValue) {
        String finalValue = value != null ? new DateFactory().convertXMLGregorianToStringDateFormat(value, format) : defaultValue;
        return getField(key, finalValue);
    }

    public String getFormattedString(String rawString, int maxLength) {
        return (rawString != null && rawString.length() > maxLength) ? rawString.substring(0, maxLength - 1) : rawString;
    }

    public Field getField(String key, String value) {
        Field field = new Field();
        field.setName(key);
        field.setValue(value);
        field.setSeq(CCA_SEQ);
        return field;
    }
}
