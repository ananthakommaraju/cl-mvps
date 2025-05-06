package com.lloydsbanking.salsa.eligibility.service.converter;


import com.lloydsbanking.salsa.soap.fs.system.StHeader;
import com.lloydstsb.ib.wsbridge.system.StB093AEventLogReadList;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class B093RequestFactoryTest {
    B093RequestFactory b093RequestFactory = new B093RequestFactory();
    XMLGregorianCalendar startDate;
    XMLGregorianCalendar endDate;
    StHeader stHeader;

    @Test
    public void createB093RequestTest(){
        String userIdAuthor = "11";
        String eventId = "5";
        DatatypeFactory datatypeFactory1 = null;


        try {
            datatypeFactory1 = DatatypeFactory.newInstance();

        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        startDate = datatypeFactory1.newXMLGregorianCalendar("2010-05-05");
        endDate = datatypeFactory1.newXMLGregorianCalendar("2012-03-03");
       stHeader = new StHeader();
        stHeader.setAcceptLanguage("english");
        stHeader.setChanid("123");

        StB093AEventLogReadList request = b093RequestFactory.createB093Request(startDate,endDate,stHeader,userIdAuthor,eventId);

        assertEquals("5",request.getEvttype());
        assertEquals("11",request.getUserid());
        assertEquals(startDate,request.getTmstmpStart());
        assertEquals(endDate,request.getTmstmpEnd());
    }
    public void createB093RequestWithNullEventIDTest()
    {
        String userIdAuthor = "11";
        String eventId = null;
        DatatypeFactory datatypeFactory1 = null;


        try {
            datatypeFactory1 = DatatypeFactory.newInstance();

        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        startDate = datatypeFactory1.newXMLGregorianCalendar("2010-05-05");
        endDate = datatypeFactory1.newXMLGregorianCalendar("2012-03-03");
        stHeader = new StHeader();
        stHeader.setAcceptLanguage("english");
        stHeader.setChanid("123");

        StB093AEventLogReadList request = b093RequestFactory.createB093Request(startDate,endDate,stHeader,userIdAuthor,eventId);

        //assertEquals("5",request.getEvttype());
        //assertEquals("11",request.getUserid());
        assertNull(request.getEvttype());

    }

}
