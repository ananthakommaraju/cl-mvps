package com.lloydsbanking.salsa.eligibility.service.utility;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class DateUtilityTest {

    DateUtility dateUtility = new DateUtility();

    @Test
    public void testCalculateIndividualAge() {
        DatatypeFactory datatypeFactory = null;

        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        dateUtility.calculateIndividualAge(datatypeFactory.newXMLGregorianCalendar("2008-05-28"));
        dateUtility.calculateIndividualAge(datatypeFactory.newXMLGregorianCalendar("2008-05-30"));
        dateUtility.calculateIndividualAge(datatypeFactory.newXMLGregorianCalendar("2008-10-23"));

        assertEquals(8, dateUtility.calculateIndividualAge(datatypeFactory.newXMLGregorianCalendar("2008-05-28")));
        assertEquals(8, dateUtility.calculateIndividualAge(datatypeFactory.newXMLGregorianCalendar("2008-05-30")));
    }

    @Test
    public void testGetStartDate() throws DatatypeConfigurationException {
        String threshold = "11020214:51";
        String[] dateArray = threshold.split(":");

        Calendar date = new GregorianCalendar();
        date.add(Calendar.DATE, -Integer.parseInt(dateArray[0]));
        GregorianCalendar gregorianCalInstance = (GregorianCalendar) GregorianCalendar.getInstance();
        gregorianCalInstance.setTime(date.getTime());
        XMLGregorianCalendar gregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalInstance);
        XMLGregorianCalendar xmlGregorianCalendar = dateUtility.getStartDate(threshold);
        assertEquals(gregorianCalendar.getMonth(), xmlGregorianCalendar.getMonth());
        assertEquals(gregorianCalendar.getDay(), xmlGregorianCalendar.getDay());
        assertEquals(gregorianCalendar.getHour(), xmlGregorianCalendar.getHour());

    }

}
