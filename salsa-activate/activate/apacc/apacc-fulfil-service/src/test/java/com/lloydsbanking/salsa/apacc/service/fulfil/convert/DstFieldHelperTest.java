package com.lloydsbanking.salsa.apacc.service.fulfil.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apacc.TestDataHelper;
import com.lloydsbanking.salsa.date.DateFactory;
import lib_sbo_cardacquire.businessojects.Field;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class DstFieldHelperTest {

    DSTFieldHelper dstFieldHelper;

    TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        dstFieldHelper = new DSTFieldHelper();
    }

    @Test
    public void testgetFieldForStringValue() {
        Field field = dstFieldHelper.getFieldForStringValue("123", "000015");
        Field field1 = dstFieldHelper.getFieldForStringValue("123", null);
        assertEquals("123", field.getName());
        assertEquals("000015", field.getValue());
        assertEquals("123", field1.getName());
        assertEquals("", field1.getValue());
    }

    @Test
    public void testGetFieldForBooleanValue() {
        Field field = dstFieldHelper.getFieldForBooleanValue("123", true);
        Field field1 = dstFieldHelper.getFieldForBooleanValue("123", false);
        Field field2 = dstFieldHelper.getFieldForBooleanValue("123", null);
        assertEquals("123", field.getName());
        assertEquals("Y", field.getValue());
        assertEquals("001", field.getSeq());
        assertEquals("N", field1.getValue());
        assertEquals("N", field2.getValue());
    }

    @Test
    public void testGetFieldForDateValue() {
        Field field = dstFieldHelper.getFieldForDateValue("123", null, "dd/MM/yyyy", "09/02/2015");
        Field field1 = dstFieldHelper.getFieldForDateValue("123", new DateFactory().stringToXMLGregorianCalendar("09032016", FastDateFormat.getInstance("ddMMyyyy")), "dd/MM/yyyy", "09/02/2015");
        assertEquals("123", field.getName());
        assertEquals("09/02/2015", field.getValue());
        assertEquals("09/03/2016", field1.getValue());
    }

    @Test
    public void testGetFormattedString() {
        String string = dstFieldHelper.getFormattedString("1235412412", 5);
        String string1 = dstFieldHelper.getFormattedString("1235412412", 20);
        assertEquals("1235", string);
        assertEquals("1235412412", string1);
    }

    @Test
    public void testField() {
        Field field = dstFieldHelper.getField("123", "0025120");
        assertEquals("123", field.getName());
        assertEquals("0025120", field.getValue());
        assertEquals("001", field.getSeq());
    }
}
