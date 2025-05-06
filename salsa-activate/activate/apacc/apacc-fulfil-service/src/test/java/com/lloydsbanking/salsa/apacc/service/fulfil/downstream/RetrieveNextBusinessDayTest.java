package com.lloydsbanking.salsa.apacc.service.fulfil.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.apacc.TestDataHelper;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.fsystem.client.FsSystemClient;
import com.lloydsbanking.salsa.downstream.fsystem.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.fs.system.StError;
import com.lloydstsb.ib.wsbridge.system.StB748AWrkngDateAfterXDays;
import com.lloydstsb.ib.wsbridge.system.StB748BWrkngDateAfterXDays;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.WebServiceException;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class RetrieveNextBusinessDayTest {
    private RetrieveNextBusinessDay retrieveNextBusinessDay;
    private TestDataHelper testDataHelper;
    private RequestHeader requestHeader;

    @Before
    public void setUp() {
        retrieveNextBusinessDay = new RetrieveNextBusinessDay();
        retrieveNextBusinessDay.fsSystemClient = mock(FsSystemClient.class);
        retrieveNextBusinessDay.dateFactory = mock(DateFactory.class);
        testDataHelper = new TestDataHelper();
        requestHeader = testDataHelper.createApaRequestHeader();
        retrieveNextBusinessDay.exceptionUtilityActivate = mock(ExceptionUtilityActivate.class);
        retrieveNextBusinessDay.headerRetriever = new HeaderRetriever();
        retrieveNextBusinessDay.bapiHeaderToStHeaderConverterSystem = new BapiHeaderToStHeaderConverter();
    }

    @Test
    public void testRetrieveNextBusinessDay() throws DatatypeConfigurationException {
        ApplicationDetails applicationDetails = new ApplicationDetails();
        StB748AWrkngDateAfterXDays stB748AWrkngDateAfterXDays = createB748Request();
        StB748BWrkngDateAfterXDays stB748BWrkngDateAfterXDays = createB748Response();
        when(retrieveNextBusinessDay.fsSystemClient.retrieveNextBusinessDay(stB748AWrkngDateAfterXDays)).thenReturn(stB748BWrkngDateAfterXDays);
        retrieveNextBusinessDay.retrieveNextBusinessDay(requestHeader, applicationDetails);
        assertEquals("1013", applicationDetails.getApplicationStatus());
    }

    @Test
    public void testRetrieveNextBusinessDayFailureCase() throws DatatypeConfigurationException {
        ApplicationDetails applicationDetails = new ApplicationDetails();
        StB748AWrkngDateAfterXDays stB748AWrkngDateAfterXDays = createB748Request();
        when(retrieveNextBusinessDay.fsSystemClient.retrieveNextBusinessDay(stB748AWrkngDateAfterXDays)).thenThrow(new IllegalStateException());
        StB748BWrkngDateAfterXDays stB748BWrkngDateAfterXDays = retrieveNextBusinessDay.retrieveNextBusinessDay(requestHeader, applicationDetails);
        assertEquals("1013", applicationDetails.getApplicationStatus());
    }

    private StB748AWrkngDateAfterXDays createB748Request() throws DatatypeConfigurationException {
        StB748AWrkngDateAfterXDays stB748AWrkngDateAfterXDays = new StB748AWrkngDateAfterXDays();
        stB748AWrkngDateAfterXDays.setNumOfDays(1);
        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        stB748AWrkngDateAfterXDays.setDateUserRequested(datatypeFactory.newXMLGregorianCalendar(new GregorianCalendar()));

        return stB748AWrkngDateAfterXDays;
    }

    private StB748BWrkngDateAfterXDays createB748Response() throws DatatypeConfigurationException {
        StB748BWrkngDateAfterXDays stB748BWrkngDateAfterXDays = new StB748BWrkngDateAfterXDays();
        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        stB748BWrkngDateAfterXDays.setDateNextWorking(datatypeFactory.newXMLGregorianCalendar(new GregorianCalendar()));
        return stB748BWrkngDateAfterXDays;
    }

    @Test
    public void testRetrieveNextBusinessDayForExternalServiceError() throws DatatypeConfigurationException {
        ApplicationDetails applicationDetails = new ApplicationDetails();
        StB748BWrkngDateAfterXDays stB748BWrkngDateAfterXDays = new StB748BWrkngDateAfterXDays();
        stB748BWrkngDateAfterXDays.setSterror(new StError());
        stB748BWrkngDateAfterXDays.getSterror().setErrormsg("error");
        stB748BWrkngDateAfterXDays.getSterror().setErrorno(1);
        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        stB748BWrkngDateAfterXDays.setDateNextWorking(datatypeFactory.newXMLGregorianCalendar(new GregorianCalendar()));
        when(retrieveNextBusinessDay.fsSystemClient.retrieveNextBusinessDay(any(StB748AWrkngDateAfterXDays.class))).thenReturn(stB748BWrkngDateAfterXDays);
        when(retrieveNextBusinessDay.exceptionUtilityActivate.externalServiceError(any(RequestHeader.class), any(String.class), any(String.class))).thenThrow(ActivateProductArrangementExternalSystemErrorMsg.class);
        retrieveNextBusinessDay.retrieveNextBusinessDay(requestHeader, applicationDetails);
    }

    @Test
    public void testRetrieveNextBusinessDayForResourceNotAvailableError() {
        ApplicationDetails applicationDetails = new ApplicationDetails();
        when(retrieveNextBusinessDay.fsSystemClient.retrieveNextBusinessDay(any(StB748AWrkngDateAfterXDays.class))).thenThrow(WebServiceException.class);
        when(retrieveNextBusinessDay.exceptionUtilityActivate.resourceNotAvailableError(any(RequestHeader.class), any(String.class))).thenThrow(ActivateProductArrangementResourceNotAvailableErrorMsg.class);
        retrieveNextBusinessDay.retrieveNextBusinessDay(requestHeader, applicationDetails);
    }
}
