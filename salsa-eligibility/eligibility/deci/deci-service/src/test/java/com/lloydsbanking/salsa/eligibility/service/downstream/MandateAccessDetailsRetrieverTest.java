package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.downstream.fsystem.client.FsSystemClient;
import com.lloydsbanking.salsa.downstream.fsystem.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalBusinessException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import com.lloydsbanking.salsa.header.BapiInformationBuilder;
import com.lloydsbanking.salsa.header.HeaderRetriever;
import com.lloydsbanking.salsa.soap.fs.system.StError;
import com.lloydsbanking.salsa.soap.fs.system.StEventType;
import com.lloydsbanking.salsa.soap.fs.system.StHeader;
import com.lloydstsb.ib.wsbridge.system.StB695AProductEventReadList;
import com.lloydstsb.ib.wsbridge.system.StB695BProductEventReadList;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lb_gbo_sales.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;

import javax.xml.ws.WebServiceException;
import java.math.BigInteger;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MandateAccessDetailsRetrieverTest {
    MandateAccessDetailsRetriever retriever;

    TestDataHelper dataHelper;

    RequestHeader header;

    BapiInformationBuilder bapiInformationBuilder;

    BapiInformation bapiInformation;

    ServiceRequest serviceRequest;

    ContactPoint contactPoint;

    @Before
    public void setUp() {
        retriever = new MandateAccessDetailsRetriever();
        retriever.bapiHeaderToStHeaderConverter = mock(BapiHeaderToStHeaderConverter.class);
        retriever.systemClient = mock(FsSystemClient.class);
        retriever.headerRetriever = mock(HeaderRetriever.class);
        dataHelper = new TestDataHelper();
        header = new com.lloydsbanking.salsa.eligibility.TestDataHelper().createEligibilityRequestHeader(TestDataHelper.TEST_RETAIL_CHANNEL_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_INTERACTION_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_OCIS_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_CUSTOMER_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_CONTACT_POINT_ID);
        bapiInformationBuilder = new BapiInformationBuilder();
        bapiInformation = bapiInformationBuilder.bapiHeader("", "", "", new BigInteger("10"), "IBL", "", "", "", "", "", "").build();
        serviceRequest = new ServiceRequest();
        contactPoint = new ContactPoint();
    }

    @Test
    public void testGetRelatedEvents() throws SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException {
        when(retriever.headerRetriever.getBapiInformationHeader(header)).thenReturn(bapiInformation);
        when(retriever.headerRetriever.getServiceRequest(header)).thenReturn(serviceRequest);
        when(retriever.headerRetriever.getContactPoint(header)).thenReturn(contactPoint);
        when(retriever.bapiHeaderToStHeaderConverter.convert(bapiInformation.getBAPIHeader(), serviceRequest, contactPoint.getContactPointId())).thenReturn(new StHeader());
        StB695AProductEventReadList b695Req = new StB695AProductEventReadList();
        b695Req.setStheader(new StHeader());
        b695Req.setAcctype("T1234567");

        StB695BProductEventReadList b695Resp = new StB695BProductEventReadList();
        b695Resp.setSterror(new StError());
        b695Resp.getSterror().setErrorno(0);
        b695Resp.setMoreind("N");
        StEventType evtType = new StEventType();
        evtType.setEvttype("B040");
        b695Resp.getAstevttype().add(evtType);

        when(retriever.systemClient.retMandateDetails(b695Req)).thenReturn(b695Resp);

        List<String> result = retriever.getRelatedEvents(header, "T1234567");
        verify(retriever.headerRetriever, times(1)).getBapiInformationHeader(header);
        verify(retriever.headerRetriever, times(1)).getServiceRequest(header);
        verify(retriever.headerRetriever, times(1)).getContactPoint(header);

        verify(retriever.bapiHeaderToStHeaderConverter, times(1)).convert(bapiInformation.getBAPIHeader(), serviceRequest, contactPoint.getContactPointId());
        verify(retriever.systemClient, times(1)).retMandateDetails(b695Req);
        assertFalse(result.isEmpty());
        assertEquals("37", result.get(0));
    }

    @Test
    public void testGetRelatedEventsWhenEventTypeIsNotPresent() throws SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException {
        when(retriever.headerRetriever.getBapiInformationHeader(header)).thenReturn(bapiInformation);
        when(retriever.headerRetriever.getServiceRequest(header)).thenReturn(serviceRequest);
        when(retriever.headerRetriever.getContactPoint(header)).thenReturn(contactPoint);
        when(retriever.bapiHeaderToStHeaderConverter.convert(bapiInformation.getBAPIHeader(), serviceRequest, contactPoint.getContactPointId())).thenReturn(new StHeader());
        StB695AProductEventReadList b695Req = new StB695AProductEventReadList();
        b695Req.setStheader(new StHeader());
        b695Req.setAcctype("T1234567");

        StB695BProductEventReadList b695Resp = new StB695BProductEventReadList();
        b695Resp.setSterror(new StError());
        b695Resp.getSterror().setErrorno(0);
        b695Resp.setMoreind("N");
        StEventType evtType = new StEventType();
        evtType.setEvttype("B000");
        StEventType evtType1 = new StEventType();
        evtType1.setEvttype("XXXX");
        b695Resp.getAstevttype().add(evtType);
        b695Resp.getAstevttype().add(evtType1);

        when(retriever.systemClient.retMandateDetails(b695Req)).thenReturn(b695Resp);

        List<String> result = retriever.getRelatedEvents(header, "T1234567");
        verify(retriever.headerRetriever, times(1)).getBapiInformationHeader(header);
        verify(retriever.headerRetriever, times(1)).getServiceRequest(header);
        verify(retriever.headerRetriever, times(1)).getContactPoint(header);

        verify(retriever.bapiHeaderToStHeaderConverter, times(1)).convert(bapiInformation.getBAPIHeader(), serviceRequest, contactPoint.getContactPointId());
        verify(retriever.systemClient, times(1)).retMandateDetails(b695Req);
        assertTrue(result.isEmpty());
    }

    @Test(expected = SalsaInternalResourceNotAvailableException.class)
    public void testGetRelatedEventsReturnsResourceNotAvailableException() throws SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException {
        when(retriever.headerRetriever.getBapiInformationHeader(header)).thenReturn(bapiInformation);
        when(retriever.headerRetriever.getServiceRequest(header)).thenReturn(serviceRequest);
        when(retriever.headerRetriever.getContactPoint(header)).thenReturn(contactPoint);
        when(retriever.bapiHeaderToStHeaderConverter.convert(bapiInformation.getBAPIHeader(), serviceRequest, contactPoint.getContactPointId())).thenReturn(new StHeader());
        StB695AProductEventReadList b695Req = new StB695AProductEventReadList();
        b695Req.setStheader(new StHeader());
        b695Req.setAcctype("T1234567");

        StB695BProductEventReadList b695Resp = new StB695BProductEventReadList();
        b695Resp.setSterror(new StError());
        b695Resp.getSterror().setErrorno(0);
        b695Resp.setMoreind("N");
        StEventType evtType = new StEventType();
        evtType.setEvttype("B040");
        b695Resp.getAstevttype().add(evtType);

        when(retriever.systemClient.retMandateDetails(b695Req)).thenThrow(WebServiceException.class);

        retriever.getRelatedEvents(header, "T1234567");
        verify(retriever.headerRetriever, times(1)).getBapiInformationHeader(header);
        verify(retriever.headerRetriever, times(1)).getServiceRequest(header);
        verify(retriever.headerRetriever, times(1)).getContactPoint(header);

        verify(retriever.bapiHeaderToStHeaderConverter, times(1)).convert(bapiInformation.getBAPIHeader(), serviceRequest, contactPoint.getContactPointId());
        verify(retriever.systemClient, times(1)).retMandateDetails(b695Req);
    }

    @Test(expected = SalsaExternalBusinessException.class)
    public void testGetRelatedEventsReturnsExternalBusinessException() throws SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException {
        when(retriever.headerRetriever.getBapiInformationHeader(header)).thenReturn(bapiInformation);
        when(retriever.headerRetriever.getServiceRequest(header)).thenReturn(serviceRequest);
        when(retriever.headerRetriever.getContactPoint(header)).thenReturn(contactPoint);
        when(retriever.bapiHeaderToStHeaderConverter.convert(bapiInformation.getBAPIHeader(), serviceRequest, contactPoint.getContactPointId())).thenReturn(new StHeader());
        StB695AProductEventReadList b695Req = new StB695AProductEventReadList();
        b695Req.setStheader(new StHeader());
        b695Req.setAcctype("T1234567");

        StB695BProductEventReadList b695Resp = new StB695BProductEventReadList();
        b695Resp.setSterror(new StError());
        b695Resp.getSterror().setErrorno(56);
        b695Resp.setMoreind("N");
        StEventType evtType = new StEventType();
        evtType.setEvttype("B040");
        b695Resp.getAstevttype().add(evtType);

        when(retriever.systemClient.retMandateDetails(b695Req)).thenReturn(b695Resp);

        retriever.getRelatedEvents(header, "T1234567");
        verify(retriever.headerRetriever, times(1)).getBapiInformationHeader(header);
        verify(retriever.headerRetriever, times(1)).getServiceRequest(header);
        verify(retriever.headerRetriever, times(1)).getContactPoint(header);

        verify(retriever.bapiHeaderToStHeaderConverter, times(1)).convert(bapiInformation.getBAPIHeader(), serviceRequest, contactPoint.getContactPointId());
        verify(retriever.systemClient, times(1)).retMandateDetails(b695Req);
    }
}
