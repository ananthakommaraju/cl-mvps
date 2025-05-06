package com.lloydsbanking.salsa.ppae.service.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.loan.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import com.lloydsbanking.salsa.soap.pad.q028.objects.Q028Resp;
import com.lloydstsb.ib.wsbridge.loan.StB233ALoanIllustrate;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@Category(UnitTest.class)
public class B233RequestFactoryTest {
    TestDataHelper testDataHelper;
    B233RequestFactory b233RequestFactory;
    Q028Resp q028Resp;
    ProcessPendingArrangementEventRequest upstreamRequest;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        b233RequestFactory = new B233RequestFactory();
        q028Resp = testDataHelper.createQ028Response();
        upstreamRequest = testDataHelper.createPpaeRequest("1", "LBG");
        b233RequestFactory.headerRetriever = new HeaderRetriever();
        b233RequestFactory.headerConverter = mock(BapiHeaderToStHeaderConverter.class);
    }

    @Test
    public void testConvert() {
        StB233ALoanIllustrate b233Request = b233RequestFactory.convert(q028Resp, upstreamRequest.getHeader());
        assertEquals(new BigInteger("84"), b233Request.getLoantermProdMax());
        assertEquals("9", b233Request.getLoanpadstatus());
        assertEquals(q028Resp.getApplicationDetails().getLoanPurposeCd(), b233Request.getLoanpurpose());
        assertEquals(q028Resp.getApplicantDetails().getParty().get(0).getEmploymentStatusCd(), b233Request.getStloanillreq().getEmploymtstatuscd());
        assertEquals(getTime(), b233Request.getTmstmpLastPadUpdate());
    }

    @Test
    public void testConvertWithStatus() {
        q028Resp.getApplicationDetails().setLoanApplnStatusCd(1);
        StB233ALoanIllustrate b233Request = b233RequestFactory.convert(q028Resp, upstreamRequest.getHeader());
        assertEquals(new BigInteger("84"), b233Request.getLoantermProdMax());
        assertEquals("9", b233Request.getLoanpadstatus());
        assertEquals(q028Resp.getApplicationDetails().getLoanPurposeCd(), b233Request.getLoanpurpose());
        assertEquals(q028Resp.getApplicantDetails().getParty().get(0).getEmploymentStatusCd(), b233Request.getStloanillreq().getEmploymtstatuscd());
    }

    private XMLGregorianCalendar getTime() {
        String date = "30042015105314";
        DateFactory dateFactory = new DateFactory();
        FastDateFormat fastDateFormat = FastDateFormat.getInstance("ddMMyyyyHHmmss");
        return dateFactory.stringToXMLGregorianCalendar(date, fastDateFormat);
    }
}
