package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.fsystem.client.FsSystemClientImpl;
import com.lloydsbanking.salsa.downstream.fsystem.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.converter.B093RequestFactory;
import com.lloydsbanking.salsa.eligibility.service.utility.DateUtility;
import com.lloydsbanking.salsa.eligibility.service.utility.ExceptionUtility;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalBusinessException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.header.HeaderRetriever;
import com.lloydsbanking.salsa.header.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.soap.fs.system.StHeader;
import com.lloydstsb.ib.wsbridge.system.StB093AEventLogReadList;
import com.lloydstsb.ib.wsbridge.system.StB093BEventLogReadList;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalBusinessErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsResourceNotAvailableErrorMsg;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import lb_gbo_sales.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class AccountEventRetrieverTest {
    private AccountEventRetriever accountEventRetriever;

    private TestDataHelper testDataHelper;

    DetermineElegibileInstructionsRequest upstreamRequest;

    StB093AEventLogReadList stB093AEventLogReadList;

    StB093BEventLogReadList stB093BEventLogReadList;

    RequestHeader header;


    @Before
    public void setUp() throws DatatypeConfigurationException {
        accountEventRetriever = new AccountEventRetriever();
        testDataHelper = new TestDataHelper();

        accountEventRetriever.b093RequestFactory = mock(B093RequestFactory.class);
        accountEventRetriever.fsSystemClient = mock(FsSystemClientImpl.class);
        accountEventRetriever.dateUtility = mock(DateUtility.class);
        accountEventRetriever.headerRetriever = new HeaderRetriever();
        accountEventRetriever.exceptionUtility = new ExceptionUtility(new RequestToResponseHeaderConverter());

        header = testDataHelper.createEligibilityRequestHeader(TestDataHelper.TEST_RETAIL_CHANNEL_ID, TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);

        DatatypeFactory dataTypeFactory = DatatypeFactory.newInstance();

        BapiInformation bapiInformation = accountEventRetriever.headerRetriever.getBapiInformationHeader(header);
        ServiceRequest serviceRequest = accountEventRetriever.headerRetriever.getServiceRequest(header);
        ContactPoint contactPoint = accountEventRetriever.headerRetriever.getContactPoint(header);

        upstreamRequest = testDataHelper.createEligibilityRequest("abc", TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        stB093AEventLogReadList = testDataHelper.createB093Request(dataTypeFactory.newXMLGregorianCalendar("2015-05-05"), dataTypeFactory.newXMLGregorianCalendar("2020-05-05"), testDataHelper
                .createStHeader(bapiInformation, serviceRequest, contactPoint.getContactPointId()), "1212", "41766");
        stB093BEventLogReadList = testDataHelper.createB093Response(false);
        accountEventRetriever.bapiHeaderSystemToStHeaderConverter = mock(BapiHeaderToStHeaderConverter.class);
    }

    @Test
    public void testGetAccountEventsIsSuccessful() throws DatatypeConfigurationException, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg, DetermineEligibleInstructionsExternalServiceErrorMsg, SalsaExternalServiceException, SalsaInternalServiceException, SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException {
        when(accountEventRetriever.b093RequestFactory.createB093Request(any(XMLGregorianCalendar.class), any(XMLGregorianCalendar.class), any(StHeader.class), any(String.class), any(String.class)))
                .thenReturn(stB093AEventLogReadList);
        when(accountEventRetriever.fsSystemClient.fetchAccountEvent(any(StB093AEventLogReadList.class))).thenReturn(stB093BEventLogReadList);

        List<String> eventList = accountEventRetriever.getAccountEvents("1234", "Event", upstreamRequest.getHeader());

        assertEquals(stB093BEventLogReadList.getAsteventlogreadlist().get(0).getEvtlogtext(), eventList.get(0));

    }

    @Test(expected = SalsaInternalServiceException.class)
    public void testgetAccountEventsForInternalServiceErrorInReq() throws DatatypeConfigurationException, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg, DetermineEligibleInstructionsInternalServiceErrorMsg, SalsaExternalServiceException, SalsaInternalServiceException, SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException {
        when(accountEventRetriever.b093RequestFactory.createB093Request(any(XMLGregorianCalendar.class), any(XMLGregorianCalendar.class), any(StHeader.class), any(String.class), any(String.class)))
                .thenThrow(Exception.class);

        accountEventRetriever.getAccountEvents("1234", "Event", upstreamRequest.getHeader());
    }

    @Test(expected = SalsaInternalResourceNotAvailableException.class)
    public void testgetAccountEventsForResourceNotAvailableError() throws DatatypeConfigurationException, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg, DetermineEligibleInstructionsInternalServiceErrorMsg, SalsaExternalServiceException, SalsaInternalServiceException, SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException {
        when(accountEventRetriever.b093RequestFactory.createB093Request(any(XMLGregorianCalendar.class), any(XMLGregorianCalendar.class), any(StHeader.class), any(String.class), any(String.class)))
                .thenReturn(stB093AEventLogReadList);
        when(accountEventRetriever.fsSystemClient.fetchAccountEvent(any(StB093AEventLogReadList.class))).thenThrow(Exception.class);

        accountEventRetriever.getAccountEvents("1234", "Event", upstreamRequest.getHeader());
    }

    @Test
    public void testgetAccountEventsForInternalServiceErrorInResponse() throws DatatypeConfigurationException, SalsaExternalServiceException, SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException {
        stB093BEventLogReadList.getSterror().setErrorno(131008);
        stB093BEventLogReadList.getSterror().setErrormsg("returning internal service error in response");

        when(accountEventRetriever.b093RequestFactory.createB093Request(any(XMLGregorianCalendar.class), any(XMLGregorianCalendar.class), any(StHeader.class), any(String.class), any(String.class)))
                .thenReturn(stB093AEventLogReadList);
        when(accountEventRetriever.fsSystemClient.fetchAccountEvent(any(StB093AEventLogReadList.class))).thenReturn(stB093BEventLogReadList);

        try {
            accountEventRetriever.getAccountEvents("1234", "Event", upstreamRequest.getHeader());
        } catch (SalsaInternalServiceException errorMsg) {
            assertEquals("00720003", errorMsg.getReasonCode());
            assertEquals("returning internal service error in response", errorMsg.getDescription().getText());

        }
    }

    @Test
    public void testgetAccountEventsForExternalBusinessErrorInResponse() throws DatatypeConfigurationException, SalsaExternalServiceException, SalsaInternalServiceException, SalsaInternalResourceNotAvailableException {
        stB093BEventLogReadList.getSterror().setErrorno(131181);
        stB093BEventLogReadList.getSterror().setErrormsg("returning external business error in response");

        when(accountEventRetriever.b093RequestFactory.createB093Request(any(XMLGregorianCalendar.class), any(XMLGregorianCalendar.class), any(StHeader.class), any(String.class), any(String.class)))
                .thenReturn(stB093AEventLogReadList);
        when(accountEventRetriever.fsSystemClient.fetchAccountEvent(any(StB093AEventLogReadList.class))).thenReturn(stB093BEventLogReadList);

        try {
            accountEventRetriever.getAccountEvents("1234", "Event", upstreamRequest.getHeader());
        } catch (SalsaExternalBusinessException errorMsg) {
            assertEquals("131181", errorMsg.getReasonCode());
            assertEquals("returning external business error in response", errorMsg.getReasonText().getText());
        }
    }

    @Test
    public void testgetAccountEventsForExternalServiceErrorInResponse() throws DatatypeConfigurationException, SalsaInternalServiceException, SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException {
        stB093BEventLogReadList.getSterror().setErrorno(131009);
        stB093BEventLogReadList.getSterror().setErrormsg("returning external service error in response");

        when(accountEventRetriever.b093RequestFactory.createB093Request(any(XMLGregorianCalendar.class), any(XMLGregorianCalendar.class), any(StHeader.class), any(String.class), any(String.class)))
                .thenReturn(stB093AEventLogReadList);
        when(accountEventRetriever.fsSystemClient.fetchAccountEvent(any(StB093AEventLogReadList.class))).thenReturn(stB093BEventLogReadList);

        try {
            accountEventRetriever.getAccountEvents("1234", "Event", upstreamRequest.getHeader());
        } catch (SalsaExternalServiceException errorMsg) {
            assertEquals("131009", errorMsg.getReasonCode());
            assertEquals("returning external service error in response", errorMsg.getReasonText());
        }
    }
}


