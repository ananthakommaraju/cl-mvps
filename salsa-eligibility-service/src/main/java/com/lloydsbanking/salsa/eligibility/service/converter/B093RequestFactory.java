package com.lloydsbanking.salsa.eligibility.service.converter;

import com.lloydsbanking.salsa.soap.fs.system.StHeader;
import com.lloydstsb.ib.wsbridge.system.StB093AEventLogReadList;
import org.apache.commons.lang3.StringUtils;

import javax.xml.datatype.XMLGregorianCalendar;

public class B093RequestFactory {
    public StB093AEventLogReadList createB093Request(XMLGregorianCalendar startDate, XMLGregorianCalendar endDate, StHeader stHeader, String useridAuthor, String eventId) {
        StB093AEventLogReadList b093Request = new StB093AEventLogReadList();
        b093Request.setStheader(stHeader);
        if(!StringUtils.isEmpty(eventId)) {
            b093Request.setEvttype(eventId);
        }
        b093Request.setTmstmpEnd(endDate);
        b093Request.setTmstmpStart(startDate);
        b093Request.setUserid(useridAuthor);
        return b093Request;
    }
}
