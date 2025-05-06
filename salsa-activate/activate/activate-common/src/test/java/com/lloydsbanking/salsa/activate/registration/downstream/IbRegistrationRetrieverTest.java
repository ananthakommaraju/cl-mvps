package com.lloydsbanking.salsa.activate.registration.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.registration.converter.B750RequestResponseConverter;
import com.lloydsbanking.salsa.downstream.application.client.ApplicationClient;
import com.lloydsbanking.salsa.soap.fs.application.StHeader;
import com.lloydstsb.ib.wsbridge.application.StB750AAppPerCCRegCreate;
import com.lloydstsb.ib.wsbridge.application.StB750BAppPerCCRegCreate;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.ws.WebServiceException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class IbRegistrationRetrieverTest {
    IbRegistrationRetriever ibRegistrationRetriever;

    TestDataHelper dataHelper;

    StHeader stHeader;

    RequestHeader requestHeader;

    @Before
    public void setUp() {
        ibRegistrationRetriever = new IbRegistrationRetriever();
        ibRegistrationRetriever.b750RequestResponseConverter = mock(B750RequestResponseConverter.class);
        ibRegistrationRetriever.applicationClient = mock(ApplicationClient.class);
        dataHelper = new TestDataHelper();
        requestHeader = dataHelper.createApaRequestHeader();
    }

    @Test
    public void testRegisterForInternetBanking() {
        StB750AAppPerCCRegCreate b750Request = dataHelper.createB750Request(stHeader);
        Customer customer = dataHelper.createDepositArrangementAfterPAMCall().getPrimaryInvolvedParty();
        when(ibRegistrationRetriever.b750RequestResponseConverter.createB750Request(requestHeader, customer, "1223514", 2, "C")).thenReturn(b750Request);
        when(ibRegistrationRetriever.applicationClient.createIBApplication(b750Request)).thenReturn(dataHelper.createB750Response());
        ibRegistrationRetriever.registerForInternetBanking(requestHeader, customer, "1223514", 2, "C");
        verify(ibRegistrationRetriever.b750RequestResponseConverter).mapB750ResponseAttributesToProductArrangement(customer, dataHelper.createB750Response());
    }

    @Test
    public void testRegisterForInternetBankingWhenB750RespondsWithError() {
        StB750AAppPerCCRegCreate b750Request = dataHelper.createB750Request(stHeader);
        StB750BAppPerCCRegCreate response = dataHelper.createB750Response();
        Customer customer = dataHelper.createDepositArrangementAfterPAMCall().getPrimaryInvolvedParty();
        when(ibRegistrationRetriever.b750RequestResponseConverter.createB750Request(requestHeader, customer, "1223514", 2, "C")).thenReturn(b750Request);
        response.getSterror().setErrorno(5);
        when(ibRegistrationRetriever.applicationClient.createIBApplication(b750Request)).thenReturn(response);
        ibRegistrationRetriever.registerForInternetBanking(requestHeader, customer, "1223514", 2, "C");
        verify(ibRegistrationRetriever.b750RequestResponseConverter, times(0)).mapB750ResponseAttributesToProductArrangement(customer, response);
    }

    @Test
    public void testRegisterForInternetBankingWithResourceNotAvailableError() {
        StB750AAppPerCCRegCreate b750Request = dataHelper.createB750Request(stHeader);
        Customer customer = dataHelper.createDepositArrangementAfterPAMCall().getPrimaryInvolvedParty();
        when(ibRegistrationRetriever.b750RequestResponseConverter.createB750Request(requestHeader, customer, "1223514", 2, "C")).thenReturn(b750Request);
        when(ibRegistrationRetriever.applicationClient.createIBApplication(b750Request)).thenThrow(WebServiceException.class);
        ibRegistrationRetriever.registerForInternetBanking(requestHeader, customer, "1223514", 2, "C");
        verify(ibRegistrationRetriever.b750RequestResponseConverter).mapB750ResponseAttributesToProductArrangement(customer, new StB750BAppPerCCRegCreate());
    }
}