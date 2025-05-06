package com.lloydsbanking.salsa.ppae.service.appstatus;


import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.List;

@Component
public class EmailDateSender {

    private static final Logger LOGGER = Logger.getLogger(EmailDateSender.class);

    public int getEmailDate(XMLGregorianCalendar lastModifiedDate, List<ReferenceDataLookUp> referenceDataCustomerNOShowUpd) {
        int emailDate = 0;
        for (ReferenceDataLookUp lookUp : referenceDataCustomerNOShowUpd) {
            int days = Integer.valueOf(lookUp.getLookupValueDesc());
            if (days == getNoOfDaysAfterUpdate(lastModifiedDate)) {
                emailDate = days;
            }
        }
        return emailDate;
    }

    private Long getNoOfDaysAfterUpdate(XMLGregorianCalendar lastModifiedDate) {
        long numOfDaysAfterUpdate = 0;
        if (lastModifiedDate != null) {
            Date currentDate = new Date();
            Date modifiedDate = new DateFactory().convertXMLGregorianToDateFormat(lastModifiedDate);
            numOfDaysAfterUpdate = new DateFactory().differenceInDays(modifiedDate, currentDate);
        }
        return numOfDaysAfterUpdate;
    }
}
