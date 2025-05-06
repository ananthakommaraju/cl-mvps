package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.brand.ChannelToBrandMapping;
import com.lloydsbanking.salsa.downstream.cbs.client.e220.E220Client;
import com.lloydsbanking.salsa.downstream.switches.SwitchServiceImpl;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.converter.CbsRequestFactory;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.header.HeaderRetriever;
import com.lloydsbanking.salsa.soap.cbs.e220.objects.DecisionGp;
import com.lloydsbanking.salsa.soap.cbs.e220.objects.DecnSubGp;
import com.lloydsbanking.salsa.soap.cbs.e220.objects.E220Req;
import com.lloydsbanking.salsa.soap.cbs.e220.objects.E220Resp;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.infrastructure.soap.CBSAppGrp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lb_gbo_sales.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class ShadowLimitRetrieverTest {
    ShadowLimitRetriever retriever;

    RequestHeader requestHeader;

    TestDataHelper testDataHelper;

    ServiceRequest serviceRequest;

    SecurityHeaderType securityHeaderType;

    ContactPoint contactPoint;

    HeaderRetriever headerRetriever = new HeaderRetriever();

    CBSAppGrp cbsAppGrp = new CBSAppGrp();

    BapiInformation bapiInformation;
    E220Req e220Req;

    E220Resp e220Resp;
    DecnSubGp subGrp;

    @Before
    public void setUp() {
        e220Req = new E220Req();
        e220Resp = new E220Resp();
        subGrp = new DecnSubGp();
        retriever = new ShadowLimitRetriever();
        retriever.headerRetriever = mock(HeaderRetriever.class);
        retriever.cbsRequestFactory = mock(CbsRequestFactory.class);

        retriever.channelToBrandMapping = new ChannelToBrandMapping();

        final Map<String, E220Client> clientMap;
        clientMap = new HashMap<String, E220Client>();
        clientMap.put("LTB", mock(E220Client.class));


        retriever.cbsE220ClientMap = clientMap;
        retriever.switchClient = mock(SwitchServiceImpl.class);
        testDataHelper = new TestDataHelper();
        requestHeader = testDataHelper.createEligibilityRequestHeader("IBL", TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        serviceRequest = headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders());
        contactPoint = headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        securityHeaderType = headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());
        cbsAppGrp.setCBSApplicationGroupNumber("01");
        bapiInformation = headerRetriever.getBapiInformationHeader(requestHeader);
        subGrp.setShdwDcnLoanLwrLmtAm("012");
        e220Resp.setDecisionGp(new DecisionGp());
        e220Resp.getDecisionGp().getDecnSubGp().add(subGrp);

    }

    @Test
    public void testShadowLimitRetriever() throws Exception {

        when(retriever.cbsRequestFactory.createE220Request("777009", "12345")).thenReturn(e220Req);
        when(retriever.headerRetriever.getContactPoint(requestHeader.getLloydsHeaders())).thenReturn(contactPoint);
        when(retriever.headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders())).thenReturn(serviceRequest);
        when(retriever.headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders())).thenReturn(securityHeaderType);
        when(retriever.headerRetriever.getBapiInformationHeader(requestHeader)).thenReturn(bapiInformation);
        when(retriever.cbsE220ClientMap.get("LTB").getShadowLimit(e220Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp)).thenReturn(e220Resp);

        retriever.getShadowLimit(requestHeader, "777009", "12345", "01");
        verify(retriever.cbsE220ClientMap.get("LTB")).getShadowLimit(e220Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp);
    }

    @Test
    public void testShadowLimitRetrieverForStrictFlag() throws Exception {

        when(retriever.cbsRequestFactory.createE220Request("777009", "12345")).thenReturn(e220Req);
        when(retriever.headerRetriever.getContactPoint(requestHeader.getLloydsHeaders())).thenReturn(contactPoint);
        when(retriever.headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders())).thenReturn(serviceRequest);
        when(retriever.headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders())).thenReturn(securityHeaderType);
        when(retriever.headerRetriever.getBapiInformationHeader(requestHeader)).thenReturn(bapiInformation);
        when(retriever.cbsE220ClientMap.get("LTB").getShadowLimit(e220Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp)).thenReturn(e220Resp);
        retriever.getStrictFlag(requestHeader, "777009", "12345", "01");
        verify(retriever.cbsE220ClientMap.get("LTB")).getShadowLimit(e220Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp);
    }

    @Test(expected = SalsaInternalServiceException.class)
    public void testShadowLimitRetrieverWithExceptionFromCreateE220Request() throws Exception {

        when(retriever.cbsRequestFactory.createE220Request("777009", "12345")).thenThrow(SalsaInternalServiceException.class);

        retriever.getShadowLimit(requestHeader, "777009", "12345", "01");
    }


    @Test(expected = Exception.class)
    public void testIsGenericGatewayEnabledFalse() throws Exception {

        when(retriever.cbsRequestFactory.createE220Request("777009", "12345")).thenReturn(e220Req);
        when(retriever.switchClient.getBrandedSwitchValue("SW_CBSGenGtwy", retriever.headerRetriever.getBapiInformationHeader(requestHeader).getBAPIHeader().getChanid())).thenThrow(Exception.class);
        when(retriever.headerRetriever.getContactPoint(requestHeader.getLloydsHeaders())).thenReturn(contactPoint);
        when(retriever.headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders())).thenReturn(serviceRequest);
        when(retriever.headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders())).thenReturn(securityHeaderType);
        when(retriever.headerRetriever.getBapiInformationHeader(requestHeader)).thenReturn(bapiInformation);
        when(retriever.cbsE220ClientMap.get("LTB").getShadowLimit(e220Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp)).thenReturn(e220Resp);

        retriever.getShadowLimit(requestHeader, "777009", "12345", "01");

    }

}
