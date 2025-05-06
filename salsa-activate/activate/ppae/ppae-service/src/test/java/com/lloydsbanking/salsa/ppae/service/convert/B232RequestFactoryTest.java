package com.lloydsbanking.salsa.ppae.service.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.loan.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import com.lloydsbanking.salsa.soap.fs.loan.St2LoanCoreDetails;
import com.lloydsbanking.salsa.soap.fs.loan.StHeader;
import com.lloydsbanking.salsa.soap.fs.loan.StParty;
import com.lloydsbanking.salsa.soap.pad.f263.objects.F263Resp;
import com.lloydstsb.ib.wsbridge.loan.StB232ALoanCCASign;
import com.lloydstsb.schema.enterprise.lcsm.BAPIHeader;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class B232RequestFactoryTest {
    F263Resp f263Resp;
    TestDataHelper testDataHelper;
    B232RequestFactory b232RequestFactory;
    ProcessPendingArrangementEventRequest upstreamRequest;
    XMLGregorianCalendar lastModifiedDate;
    XMLGregorianCalendar date;

    @Before
    public void setUp() throws DatatypeConfigurationException {
        testDataHelper = new TestDataHelper();
        lastModifiedDate = new DateFactory().getCurrentDate();
        date = DatatypeFactory.newInstance().newXMLGregorianCalendar();
        upstreamRequest = testDataHelper.createPpaeRequest("1", "LBG");
        f263Resp = testDataHelper.createF263Resp();
        b232RequestFactory = new B232RequestFactory();
        date.setYear(lastModifiedDate.getYear());
        date.setMonth(lastModifiedDate.getMonth());
        date.setDay(lastModifiedDate.getDay());
        b232RequestFactory.headerRetriever = new HeaderRetriever();
        b232RequestFactory.headerConverter = mock(BapiHeaderToStHeaderConverter.class);
        b232RequestFactory.stLoanCoreFactory = mock(StLoanCoreFactory.class);
        StHeader stHeader=new StHeader();
        stHeader.setStpartyObo(new StParty());
        when(b232RequestFactory.headerConverter.convert(any(BAPIHeader.class), any(ServiceRequest.class), any(String.class))).thenReturn(stHeader);
    }

    @Test
    public void convertTest() throws DatatypeConfigurationException {
        when(b232RequestFactory.stLoanCoreFactory.getStLoanDetails(f263Resp)).thenReturn(new St2LoanCoreDetails());
        StB232ALoanCCASign stB232Request = b232RequestFactory.convert(upstreamRequest.getHeader(), f263Resp, lastModifiedDate);
        assertNotNull(stB232Request);
        assertFalse(stB232Request.isBBatchRetry());
        assertEquals(404441619, stB232Request.getCreditscoreid());
        assertEquals("HALIFAX CLARITY LOAN", stB232Request.getLoanprodtxt());
        assertEquals(date, stB232Request.getAstccasignletter().get(0).getDatPAMLstUpdt());

    }

    @Test
    public void convertTestWithNullModifiedDate() throws DatatypeConfigurationException {
        when(b232RequestFactory.stLoanCoreFactory.getStLoanDetails(f263Resp)).thenReturn(new St2LoanCoreDetails());
        StB232ALoanCCASign stB232Request = b232RequestFactory.convert(upstreamRequest.getHeader(), f263Resp, null);
        assertNotNull(stB232Request);
        assertFalse(stB232Request.isBBatchRetry());
        assertEquals(404441619, stB232Request.getCreditscoreid());
        assertEquals("HALIFAX CLARITY LOAN", stB232Request.getLoanprodtxt());
        assertNull(stB232Request.getAstccasignletter().get(0).getDatPAMLstUpdt());
    }
}
