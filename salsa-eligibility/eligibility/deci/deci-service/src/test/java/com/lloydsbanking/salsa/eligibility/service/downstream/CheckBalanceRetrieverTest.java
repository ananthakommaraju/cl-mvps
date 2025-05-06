package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.cbs.client.e141.E141Client;
import com.lloydsbanking.salsa.downstream.switches.SwitchServiceImpl;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.header.GmoToGboRequestHeaderConverter;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import com.lloydsbanking.salsa.header.HeaderRetriever;
import com.lloydsbanking.salsa.soap.cbs.e141.objects.E141Req;
import com.lloydsbanking.salsa.soap.cbs.e141.objects.E141Resp;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.infrastructure.soap.CBSAppGrp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.ProductArrangementIndicator;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.ws.WebServiceException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CheckBalanceRetrieverTest {

    private CheckBalanceRetriever retriever;

    TestDataHelper testDataHelper;

    CBSAppGrp cbsAppGrp = new CBSAppGrp();

    RequestHeader header;

    lb_gbo_sales.messages.RequestHeader gboHeader;

    GmoToGboRequestHeaderConverter headerConverter;

    ServiceRequest serviceRequest;

    ContactPoint contactPoint;

    SecurityHeaderType securityHeaderType;

    HeaderRetriever headerRetriever;

    BapiInformation bapiInformation = new BapiInformation();
    E141Req e141Req;
    E141Resp e141Resp;
    List<Integer> indicatorList;

    @Before
    public void setUp() {
        retriever = new CheckBalanceRetriever();
        retriever.cbsE141ClientMap = new HashMap<>();
        retriever.headerRetriever = new HeaderRetriever();
        retriever.switchClient = mock(SwitchServiceImpl.class);
        retriever.cbsE141ClientMap.put("BOS", mock(E141Client.class));
        retriever.cbsE141ClientMap.put("LTB", mock(E141Client.class));
        testDataHelper = new TestDataHelper();
        cbsAppGrp.setCBSApplicationGroupNumber("01");
        headerConverter = new GmoToGboRequestHeaderConverter();
        header = testDataHelper.createEligibilityRequestHeader("IBS", com.lloydsbanking.salsa.eligibility.wz.TestDataHelper.TEST_INTERACTION_ID, com.lloydsbanking.salsa.eligibility.wz.TestDataHelper.TEST_OCIS_ID, com.lloydsbanking.salsa.eligibility.wz.TestDataHelper.TEST_CUSTOMER_ID, "0000805121");
        gboHeader = headerConverter.convert(header);
        serviceRequest = retriever.headerRetriever.getServiceRequest(gboHeader.getLloydsHeaders());
        contactPoint = retriever.headerRetriever.getContactPoint(gboHeader.getLloydsHeaders());
        securityHeaderType = retriever.headerRetriever.getSecurityHeader(gboHeader.getLloydsHeaders());
        e141Req = testDataHelper.createE141Request("111618", "50001762");
        indicatorList = new ArrayList<>();
        indicatorList.add(664);
        e141Resp = testDataHelper.createE141Response(indicatorList, "0");

    }

    @Test
    public void testGetCheckBalance() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException {


        when(retriever.switchClient.getBrandedSwitchValue("SW_CBSGenGtwy", "IBS", false)).thenReturn(true);
        when(retriever.cbsE141ClientMap.get("BOS").getProductArrangementIndicator(e141Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp)).thenReturn(e141Resp);
        E141Resp result = retriever.getCheckBalance(gboHeader, "111618", "50001762", "01");
        assertEquals(e141Resp.getIndicator2Gp().getStandardIndicators2Gp().get(0).getIndicator2Cd(), result.getIndicator2Gp().getStandardIndicators2Gp().get(0).getIndicator2Cd());

    }

    @Test
    public void testGetCBSIndicators() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException {


        when(retriever.switchClient.getBrandedSwitchValue("SW_CBSGenGtwy", "IBS", false)).thenReturn(true);
        when(retriever.cbsE141ClientMap.get("BOS").getProductArrangementIndicator(e141Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp)).thenReturn(e141Resp);
        List<ProductArrangementIndicator> productArrangementIndicators = retriever.getCBSIndicators(gboHeader, "111618", "50001762", "01");
        assertEquals(e141Resp.getIndicator2Gp().getStandardIndicators2Gp().get(0).getIndicator2Cd(), productArrangementIndicators.get(0).getCode().intValue());

    }

    @Test
    public void testGetCBSIndicatorsForIndicatorG() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException {

        e141Resp.setIndicatorGp(testDataHelper.getIndicatorGp(indicatorList));
        when(retriever.switchClient.getBrandedSwitchValue("SW_CBSGenGtwy", "IBS", false)).thenReturn(true);
        when(retriever.cbsE141ClientMap.get("BOS").getProductArrangementIndicator(e141Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp)).thenReturn(e141Resp);
        List<ProductArrangementIndicator> productArrangementIndicators = retriever.getCBSIndicators(gboHeader, "111618", "50001762", "01");
        assertEquals(e141Resp.getIndicator2Gp().getStandardIndicators2Gp().get(0).getIndicator2Cd(), productArrangementIndicators.get(0).getCode().intValue());

    }

    @Test(expected = SalsaInternalResourceNotAvailableException.class)
    public void testGetShadowLimitReturnsInternalServiceException() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException {

        E141Req e141Req = testDataHelper.createE141Request("111618", "50001762");
        when(retriever.switchClient.getBrandedSwitchValue("SW_CBSGenGtwy", "IBS", false)).thenReturn(true);
        when(retriever.cbsE141ClientMap.get("BOS").getProductArrangementIndicator(e141Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp)).thenThrow(WebServiceException.class);
        retriever.getCheckBalance(gboHeader, "111618", "50001762", "01");
    }

    @Test
    public void testGetShadowLimitSwitchClientReturnsInternalServiceException() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException {


        when(retriever.switchClient.getBrandedSwitchValue("SW_CBSGenGtwy", "IBS", false)).thenThrow(WebServiceException.class);
        when(retriever.cbsE141ClientMap.get("LTB").getProductArrangementIndicator(e141Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp)).thenReturn(e141Resp);
        E141Resp result = retriever.getCheckBalance(gboHeader, "111618", "50001762", "01");
        assertEquals(e141Resp.getIndicator2Gp().getStandardIndicators2Gp().get(0).getIndicator2Cd(), result.getIndicator2Gp().getStandardIndicators2Gp().get(0).getIndicator2Cd());
    }

    @Test(expected = SalsaExternalServiceException.class)
    public void testGetCheckBalanceSalsaExternalServiceException() throws SalsaExternalServiceException, SalsaInternalResourceNotAvailableException {

        when(retriever.switchClient.getBrandedSwitchValue("SW_CBSGenGtwy", "IBS", false)).thenReturn(true);
        e141Resp.getE141Result().getResultCondition().setSeverityCode((byte) 3);
        when(retriever.cbsE141ClientMap.get("BOS").getProductArrangementIndicator(e141Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp)).thenThrow(SalsaExternalServiceException.class);
        E141Resp result = retriever.getCheckBalance(gboHeader, "111618", "50001762", "01");

    }

    @Test(expected = SalsaExternalServiceException.class)
    public void testGetCheckBalanceSalsaExternalServiceExceptionReasonCodeCheck() throws SalsaExternalServiceException, SalsaInternalResourceNotAvailableException {

        when(retriever.switchClient.getBrandedSwitchValue("SW_CBSGenGtwy", "IBS", false)).thenReturn(true);
        e141Resp.getE141Result().getResultCondition().setReasonCode(3);
        when(retriever.cbsE141ClientMap.get("BOS").getProductArrangementIndicator(e141Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp)).thenThrow(SalsaExternalServiceException.class);
        E141Resp result = retriever.getCheckBalance(gboHeader, "111618", "50001762", "01");

    }

    @Test
    public void testGetCBSIndicatorsForIndicatorGForReasonCodeNull() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException {
        e141Resp.setIndicatorGp(testDataHelper.getIndicatorGp(indicatorList));
        when(retriever.switchClient.getBrandedSwitchValue("SW_CBSGenGtwy", "IBS", false)).thenReturn(true);
        e141Resp.getE141Result().getResultCondition().setReasonCode(null);
        when(retriever.cbsE141ClientMap.get("BOS").getProductArrangementIndicator(e141Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp)).thenReturn(e141Resp);
        List<ProductArrangementIndicator> productArrangementIndicators = retriever.getCBSIndicators(gboHeader, "111618", "50001762", "01");
        assertEquals(e141Resp.getIndicator2Gp().getStandardIndicators2Gp().get(0).getIndicator2Cd(), productArrangementIndicators.get(0).getCode().intValue());
    }
}
