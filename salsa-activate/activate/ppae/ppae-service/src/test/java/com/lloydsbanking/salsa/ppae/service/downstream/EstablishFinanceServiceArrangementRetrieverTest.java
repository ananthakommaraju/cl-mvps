package com.lloydsbanking.salsa.ppae.service.downstream;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.loan.client.LoanClient;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import com.lloydsbanking.salsa.ppae.service.convert.B232RequestFactory;
import com.lloydsbanking.salsa.soap.fs.loan.StError;
import com.lloydsbanking.salsa.soap.pad.f263.objects.F263Resp;
import com.lloydstsb.ib.wsbridge.loan.StB232ALoanCCASign;
import com.lloydstsb.ib.wsbridge.loan.StB232BLoanCCASign;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.WebServiceException;

import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class EstablishFinanceServiceArrangementRetrieverTest {
    EstablishFinanceServiceArrangementRetriever retriever;
    TestDataHelper testDataHelper;
    F263Resp f263Resp;
    XMLGregorianCalendar lastModifiedDate;
    ProcessPendingArrangementEventRequest upstreamRequest;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        f263Resp = testDataHelper.createF263Resp();
        lastModifiedDate = new DateFactory().getCurrentDate();
        upstreamRequest = testDataHelper.createPpaeRequest("1", "LBG");
        retriever = new EstablishFinanceServiceArrangementRetriever();
        retriever.b232RequestFactory = mock(B232RequestFactory.class);
        retriever.loanClient = mock(LoanClient.class);
    }

    @Test
    public void testRetrieve() throws DatatypeConfigurationException {
        StB232ALoanCCASign b232Request = testDataHelper.createB232Request();
        when(retriever.b232RequestFactory.convert(upstreamRequest.getHeader(), f263Resp, lastModifiedDate)).thenReturn(b232Request);
        when(retriever.loanClient.b232LoanCCASign(b232Request)).thenReturn(new StB232BLoanCCASign());
        retriever.retrieve(upstreamRequest.getHeader(), f263Resp, lastModifiedDate);
        verify(retriever.loanClient).b232LoanCCASign(b232Request);
    }

    @Test
    public void testRetrieveWithError() throws DatatypeConfigurationException {
        F263Resp f263Resp = new F263Resp();
        StB232BLoanCCASign stB232BLoanCCASign = new StB232BLoanCCASign();
        stB232BLoanCCASign.setSterror(new StError());
        stB232BLoanCCASign.getSterror().setErrorno(2);
        StB232ALoanCCASign b232Request = testDataHelper.createB232Request();
        when(retriever.b232RequestFactory.convert(upstreamRequest.getHeader(), f263Resp, lastModifiedDate)).thenReturn(b232Request);
        when(retriever.loanClient.b232LoanCCASign(b232Request)).thenReturn(stB232BLoanCCASign);
        retriever.retrieve(upstreamRequest.getHeader(), f263Resp, lastModifiedDate);
        verify(retriever.loanClient).b232LoanCCASign(b232Request);
    }

    @Test
    public void testRetrieveWithException() throws DatatypeConfigurationException {
        StB232ALoanCCASign b232Request = testDataHelper.createB232Request();
        when(retriever.b232RequestFactory.convert(upstreamRequest.getHeader(), f263Resp, lastModifiedDate)).thenReturn(b232Request);
        when(retriever.loanClient.b232LoanCCASign(b232Request)).thenThrow(WebServiceException.class);
        retriever.retrieve(upstreamRequest.getHeader(), f263Resp, lastModifiedDate);
        verify(retriever.loanClient).b232LoanCCASign(b232Request);
    }

    @Test
    public void testRetrieveWithNoError() throws DatatypeConfigurationException {
        F263Resp f263Resp = new F263Resp();
        StB232BLoanCCASign stB232BLoanCCASign = new StB232BLoanCCASign();
        stB232BLoanCCASign.setSterror(new StError());
        stB232BLoanCCASign.getSterror().setErrorno(0);
        StB232ALoanCCASign b232Request = testDataHelper.createB232Request();
        when(retriever.b232RequestFactory.convert(upstreamRequest.getHeader(), f263Resp, lastModifiedDate)).thenReturn(b232Request);
        when(retriever.loanClient.b232LoanCCASign(b232Request)).thenReturn(stB232BLoanCCASign);
        retriever.retrieve(upstreamRequest.getHeader(), f263Resp, lastModifiedDate);
        verify(retriever.loanClient).b232LoanCCASign(b232Request);
    }

}
