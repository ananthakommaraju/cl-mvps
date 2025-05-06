package com.lloydsbanking.salsa.ppae.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.loan.client.LoanClient;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import com.lloydsbanking.salsa.ppae.service.convert.B233RequestFactory;
import com.lloydsbanking.salsa.soap.fs.loan.StError;
import com.lloydsbanking.salsa.soap.pad.q028.objects.Q028Resp;
import com.lloydstsb.ib.wsbridge.loan.StB233ALoanIllustrate;
import com.lloydstsb.ib.wsbridge.loan.StB233BLoanIllustrate;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class PrepareFinanceServiceArrangementTest {
    PrepareFinanceServiceArrangement retriever;
    TestDataHelper testDataHelper;
    ProcessPendingArrangementEventRequest upstreamRequest;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        retriever = new PrepareFinanceServiceArrangement();
        retriever.b233RequestFactory = mock(B233RequestFactory.class);
        retriever.loanClient = mock(LoanClient.class);
        upstreamRequest = testDataHelper.createPpaeRequest("1", "LBG");
    }

    @Test
    public void testRetrieve() {
        Q028Resp q028Resp = new Q028Resp();
        StB233ALoanIllustrate b233ALoanIllustrate = testDataHelper.createB233Request();
        StB233BLoanIllustrate stB233BLoanIllustrate = new StB233BLoanIllustrate();
        stB233BLoanIllustrate.setSterror(new StError());
        stB233BLoanIllustrate.getSterror().setErrorno(0);
        when(retriever.b233RequestFactory.convert(q028Resp, upstreamRequest.getHeader())).thenReturn(b233ALoanIllustrate);
        when(retriever.loanClient.b233BLoanIllustrate(b233ALoanIllustrate)).thenReturn(stB233BLoanIllustrate);
        retriever.process(q028Resp, upstreamRequest.getHeader());
        verify(retriever.loanClient).b233BLoanIllustrate(b233ALoanIllustrate);
    }

    @Test
    public void testRetrieveWithError() {
        Q028Resp q028Resp = new Q028Resp();
        StB233ALoanIllustrate b233ALoanIllustrate = testDataHelper.createB233Request();
        StB233BLoanIllustrate stB233BLoanIllustrate = new StB233BLoanIllustrate();
        stB233BLoanIllustrate.setSterror(new StError());
        stB233BLoanIllustrate.getSterror().setErrorno(2);
        when(retriever.b233RequestFactory.convert(q028Resp, upstreamRequest.getHeader())).thenReturn(b233ALoanIllustrate);
        when(retriever.loanClient.b233BLoanIllustrate(b233ALoanIllustrate)).thenReturn(stB233BLoanIllustrate);
        retriever.process(q028Resp, upstreamRequest.getHeader());
        verify(retriever.loanClient).b233BLoanIllustrate(b233ALoanIllustrate);
    }

    @Test
    public void testRetrieveWithWebServiceException() {
        Q028Resp q028Resp = new Q028Resp();
        StB233ALoanIllustrate b233ALoanIllustrate = testDataHelper.createB233Request();
        StB233BLoanIllustrate stB233BLoanIllustrate = new StB233BLoanIllustrate();
        stB233BLoanIllustrate.setSterror(new StError());
        stB233BLoanIllustrate.getSterror().setErrorno(0);
        when(retriever.b233RequestFactory.convert(q028Resp, upstreamRequest.getHeader())).thenReturn(b233ALoanIllustrate);
        when(retriever.loanClient.b233BLoanIllustrate(b233ALoanIllustrate)).thenReturn(stB233BLoanIllustrate);
        retriever.process(q028Resp, upstreamRequest.getHeader());
        verify(retriever.loanClient).b233BLoanIllustrate(b233ALoanIllustrate);
    }

}
