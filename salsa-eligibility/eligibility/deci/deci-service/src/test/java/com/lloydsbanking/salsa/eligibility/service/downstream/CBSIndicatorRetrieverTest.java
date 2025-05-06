package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.brand.ChannelToBrandMapping;
import com.lloydsbanking.salsa.downstream.cbs.client.e184.E184Client;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.header.HeaderRetriever;
import com.lloydsbanking.salsa.soap.cbs.e184.objects.E184Req;
import com.lloydsbanking.salsa.soap.cbs.e184.objects.E184Resp;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.infrastructure.soap.CBSAppGrp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import lb_gbo_sales.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class CBSIndicatorRetrieverTest {
    TestDataHelper dataHelper = new TestDataHelper();

    RequestHeader header;

    CBSIndicatorRetriever cbsIndicatorRetriever;

    ServiceRequest serviceRequest;

    SecurityHeaderType securityHeaderType;

    ContactPoint contactPoint;

    HeaderRetriever headerRetriever = new HeaderRetriever();

    DetermineElegibileInstructionsRequest upStreamRequest;

    CBSAppGrp cbsAppGrp = new CBSAppGrp();

    ChannelToBrandMapping ChannelToBrandMapping;
    BapiInformation bapiInformation;

    @Before
    public void setUp() {

        cbsIndicatorRetriever = new CBSIndicatorRetriever();
        ChannelToBrandMapping = new ChannelToBrandMapping();
        cbsIndicatorRetriever.headerRetriever = mock(HeaderRetriever.class);
        cbsIndicatorRetriever.channelToBrandMapping = mock(ChannelToBrandMapping.class);

        final Map<String, E184Client> clientMap;
        clientMap = new HashMap<String, E184Client>();
        clientMap.put(dataHelper.TEST_CHANNEL_ID_CBS, mock(E184Client.class));
        cbsIndicatorRetriever.cbsE184ClientMap = clientMap;
        upStreamRequest = dataHelper.createEligibilityRequest("P_BODA_RBB", dataHelper.TEST_OCIS_ID, dataHelper.TEST_COMMERCIAL_CHANNEL_ID, dataHelper.TEST_CONTACT_POINT_ID);
        header = upStreamRequest.getHeader();
        serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders(), "{http://www.lloydstsb.com/Schema/Enterprise/LCSM_CommunicationManagement}CommunicationAcceptanceService", "determineEligibleCustomerInstruction");
        contactPoint = headerRetriever.getContactPoint(upStreamRequest.getHeader().getLloydsHeaders());
        securityHeaderType = headerRetriever.getSecurityHeader(upStreamRequest.getHeader().getLloydsHeaders());
        cbsAppGrp.setCBSApplicationGroupNumber(dataHelper.TEST_CBS_APP_GRP);
        bapiInformation = headerRetriever.getBapiInformationHeader(header);
    }

    @Test
    public void testGetCbsIndicator() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException {
        E184Req e184Req = new E184Req();

        e184Req.setCBSAccountNoId(dataHelper.TEST_SORT_CODE + dataHelper.TEST_ACCOUNT_NUMBER);

        E184Resp e184Resp = dataHelper.createE141Response(646);
        when(cbsIndicatorRetriever.headerRetriever.getContactPoint(upStreamRequest.getHeader().getLloydsHeaders())).thenReturn(contactPoint);
        when(cbsIndicatorRetriever.headerRetriever.getSecurityHeader(upStreamRequest.getHeader().getLloydsHeaders())).thenReturn(securityHeaderType);
        when(cbsIndicatorRetriever.headerRetriever.getServiceRequest(header.getLloydsHeaders(), "{http://www.lloydstsb.com/Schema/Enterprise/LCSM_CommunicationManagement}CommunicationAcceptanceService", "determineEligibleCustomerInstruction"))
                .thenReturn(serviceRequest);
        when((cbsIndicatorRetriever.cbsE184ClientMap.get(dataHelper.TEST_CHANNEL_ID_CBS)
                .getCbsIndicator(e184Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp))).thenReturn(e184Resp);
        when(cbsIndicatorRetriever.headerRetriever.getBapiInformationHeader(upStreamRequest.getHeader())).thenReturn(bapiInformation);
        assertEquals(dataHelper.TEST_CBS_INDICATOR, cbsIndicatorRetriever.getCbsIndicator(upStreamRequest.getHeader(), dataHelper.TEST_SORT_CODE, dataHelper.TEST_ACCOUNT_NUMBER, dataHelper.TEST_CBS_APP_GRP)
                .get(0)
                .getIndicator1Cd());
        assertEquals(dataHelper.TEST_CHANNEL_ID_CBS, ChannelToBrandMapping.getBrandForChannel(dataHelper.TEST_COMMERCIAL_CHANNEL_ID));
        verify(cbsIndicatorRetriever.cbsE184ClientMap.get(dataHelper.TEST_CHANNEL_ID_CBS)).getCbsIndicator(e184Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp);
    }


}