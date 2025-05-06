package com.lloydsbanking.salsa.eligibility.service.utility;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtility {

    public int calculateIndividualAge(XMLGregorianCalendar birthDate) {

        Calendar birthDateInstance = Calendar.getInstance();
        birthDateInstance.setTime(birthDate.toGregorianCalendar().getTime());
        Calendar today = Calendar.getInstance();
        int yearsOld = today.get(Calendar.YEAR) - birthDateInstance.get(Calendar.YEAR);

        // decrement age if birthdate falls later this year
        if (today.get(Calendar.MONTH) < birthDateInstance.get(Calendar.MONTH)
                || (today.get(Calendar.MONTH) == birthDateInstance.get(Calendar.MONTH) && today
                .get(Calendar.DAY_OF_MONTH) < birthDateInstance
                .get(Calendar.DAY_OF_MONTH))) {
            yearsOld--;
        }

        return yearsOld;
    }

    public XMLGregorianCalendar getStartDate(String threshold) throws DatatypeConfigurationException {
        String[] dateArray = threshold.split(":");
        Calendar date = new GregorianCalendar();
        date.add(Calendar.DATE, -Integer.parseInt(dateArray[0]));
        return getDateInXMLGregorianCalendar(date.getTime());


    }

    public XMLGregorianCalendar getDateInXMLGregorianCalendar(Date dateTime) throws DatatypeConfigurationException {

        GregorianCalendar gregorianCalInstance = (GregorianCalendar) GregorianCalendar.getInstance();
        gregorianCalInstance.setTime(dateTime);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalInstance);
    }
}
