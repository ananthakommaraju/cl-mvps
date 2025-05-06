package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.cbs.client.e591.E591Client;
import com.lloydsbanking.salsa.downstream.switches.SwitchServiceImpl;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.header.GmoToGboRequestHeaderConverter;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import com.lloydsbanking.salsa.header.HeaderRetriever;
import com.lloydsbanking.salsa.soap.cbs.e591.objects.DecnSubGp;
import com.lloydsbanking.salsa.soap.cbs.e591.objects.E591Req;
import com.lloydsbanking.salsa.soap.cbs.e591.objects.E591Resp;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.infrastructure.soap.CBSAppGrp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CustomerDecisionDetailsRetrieverTest {
    private CustomerDecisionDetailsRetriever retriever;

    TestDataHelper testDataHelper;

    CBSAppGrp cbsAppGrp = new CBSAppGrp();

    RequestHeader header;

    lb_gbo_sales.messages.RequestHeader gboHeader;

    GmoToGboRequestHeaderConverter headerConverter;

    ServiceRequest serviceRequest;

    ContactPoint contactPoint;

    SecurityHeaderType securityHeaderType;

    HeaderRetriever headerRetriever;

    BapiInformation bapiInformation;

    @Before
    public void setUp() {
        retriever = new CustomerDecisionDetailsRetriever();
        retriever.cbsE591ClientMap = new HashMap<>();
        retriever.headerRetriever = mock(HeaderRetriever.class);
        retriever.switchClient = mock(SwitchServiceImpl.class);
        retriever.cbsE591ClientMap.put("BOS", mock(E591Client.class));
        retriever.cbsE591ClientMap.put("LTB", mock(E591Client.class));
        testDataHelper = new TestDataHelper();
        headerRetriever = new HeaderRetriever();
        cbsAppGrp.setCBSApplicationGroupNumber("01");
        headerConverter = new GmoToGboRequestHeaderConverter();
        header = testDataHelper.createEligibilityRequestHeader("IBS", com.lloydsbanking.salsa.eligibility.wz.TestDataHelper.TEST_INTERACTION_ID, com.lloydsbanking.salsa.eligibility.wz.TestDataHelper.TEST_OCIS_ID, com.lloydsbanking.salsa.eligibility.wz.TestDataHelper.TEST_CUSTOMER_ID, "0000805121");
        gboHeader = headerConverter.convert(header);
        serviceRequest = headerRetriever.getServiceRequest(gboHeader.getLloydsHeaders());
        contactPoint = headerRetriever.getContactPoint(gboHeader.getLloydsHeaders());
        securityHeaderType = headerRetriever.getSecurityHeader(gboHeader.getLloydsHeaders());
        bapiInformation = headerRetriever.getBapiInformationHeader(gboHeader);
    }

    @Test
    public void testGetShadowLimit() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException {
        E591Req e591Req = testDataHelper.createE591Request("12345");

        E591Resp e591Resp = testDataHelper.createE591Response("10");

        when(retriever.headerRetriever.getContactPoint(gboHeader.getLloydsHeaders())).thenReturn(contactPoint);
        when(retriever.headerRetriever.getServiceRequest(gboHeader.getLloydsHeaders())).thenReturn(serviceRequest);
        when(retriever.headerRetriever.getSecurityHeader(gboHeader.getLloydsHeaders())).thenReturn(securityHeaderType);

        when(retriever.switchClient.getBrandedSwitchValue("SW_CBSGenGtwy", "IBS", false)).thenReturn(true);
        when(retriever.headerRetriever.getBapiInformationHeader(gboHeader)).thenReturn(bapiInformation);
        when(retriever.cbsE591ClientMap.get("BOS").enqCbsCustDecnTrl(e591Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp)).thenReturn(e591Resp);

        DecnSubGp decnSubGp = retriever.getCustomerDecisionDetails(gboHeader, "12345", "01");


        assertEquals(e591Resp.getDecisionGp().getDecnSubGp().get(0), decnSubGp);
    }

    @Test(expected = SalsaInternalResourceNotAvailableException.class)
    public void testGetShadowLimitReturnsInternalServiceException() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException {
        E591Req e591Req = testDataHelper.createE591Request("12345");

        when(retriever.headerRetriever.getContactPoint(gboHeader.getLloydsHeaders())).thenReturn(contactPoint);
        when(retriever.headerRetriever.getServiceRequest(gboHeader.getLloydsHeaders())).thenReturn(serviceRequest);
        when(retriever.headerRetriever.getSecurityHeader(gboHeader.getLloydsHeaders())).thenReturn(securityHeaderType);

        when(retriever.switchClient.getBrandedSwitchValue("SW_CBSGenGtwy", "IBS", false)).thenReturn(true);
        when(retriever.headerRetriever.getBapiInformationHeader(gboHeader)).thenReturn(bapiInformation);
        when(retriever.cbsE591ClientMap.get("BOS").enqCbsCustDecnTrl(e591Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp)).thenThrow(SOAPFaultException.class);

        retriever.getCustomerDecisionDetails(gboHeader, "12345", "01");
    }

    @Test
    public void testGetShadowLimitSwitchClientReturnsInternalServiceException() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException {
        E591Req e591Req = testDataHelper.createE591Request("12345");
        E591Resp e591Resp = testDataHelper.createE591Response("10");

        when(retriever.headerRetriever.getContactPoint(gboHeader.getLloydsHeaders())).thenReturn(contactPoint);
        when(retriever.headerRetriever.getServiceRequest(gboHeader.getLloydsHeaders())).thenReturn(serviceRequest);
        when(retriever.headerRetriever.getSecurityHeader(gboHeader.getLloydsHeaders())).thenReturn(securityHeaderType);

        when(retriever.switchClient.getBrandedSwitchValue("SW_CBSGenGtwy", "IBS", false)).thenThrow(WebServiceException.class);
        when(retriever.headerRetriever.getBapiInformationHeader(gboHeader)).thenReturn(bapiInformation);
        when(retriever.cbsE591ClientMap.get("LTB").enqCbsCustDecnTrl(e591Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp)).thenReturn(e591Resp);

        DecnSubGp decnSubGp = retriever.getCustomerDecisionDetails(gboHeader, "12345", "01");

        assertEquals(e591Resp.getDecisionGp().getDecnSubGp().get(0), decnSubGp);
    }
}
