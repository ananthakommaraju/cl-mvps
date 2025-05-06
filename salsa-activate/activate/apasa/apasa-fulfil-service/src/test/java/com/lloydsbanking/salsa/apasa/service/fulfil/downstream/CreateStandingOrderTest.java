package com.lloydsbanking.salsa.apasa.service.fulfil.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.apasa.TestDataHelper;
import com.lloydsbanking.salsa.apasa.service.fulfil.convert.E032RequestFactory;
import com.lloydsbanking.salsa.downstream.cbs.client.e032.E032Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.cbs.e032.objects.E032Req;
import com.lloydsbanking.salsa.soap.cbs.e032.objects.E032Resp;
import com.lloydsbanking.salsa.soap.cbs.e032.objects.E032Result;
import com.lloydstsb.schema.infrastructure.soap.CBSAppGrp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.ws.WebServiceException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CreateStandingOrderTest {
    private TestDataHelper dataHelper;

    private CreateStandingOrder createStandingOrder;

    private E032Resp createStandingOrderResponse;

    private RequestHeader requestHeader;

    private ApplicationDetails applicationDetails;
    private CBSAppGrp cbsAppGrp;


    @Before
    public void setUp() throws DatatypeConfigurationException, ParseException {
        dataHelper = new TestDataHelper();
        cbsAppGrp = new CBSAppGrp();
        createStandingOrder = new CreateStandingOrder();
        createStandingOrder.appGroupRetriever = mock(AppGroupRetriever.class);
        createStandingOrder.headerRetriever = new HeaderRetriever();
        requestHeader = dataHelper.createApaRequestHeader();
        final Map<String, E032Client> clientMap;
        clientMap = new HashMap<String, E032Client>();
        clientMap.put("LTB", mock(E032Client.class));
        createStandingOrder.cbsE032ClientMap = clientMap;
        createStandingOrder.requestFactory = new E032RequestFactory();
        createStandingOrder.updateAppDetails = new UpdateDepositArrangementConditionAndApplicationStatusHelper();

    }

    @Test
    public void testCreateStandingOrder() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        applicationDetails = new ApplicationDetails();
        createStandingOrderResponse = new E032Resp();
        createStandingOrderResponse.setE032Result(new E032Result());
        createStandingOrderResponse.getE032Result().setResultCondition(new ResultCondition());
        createStandingOrderResponse.getE032Result().getResultCondition().setReasonCode(82);
        when(createStandingOrder.cbsE032ClientMap.get("LTB").createStandingOrder(any(E032Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(CBSAppGrp.class))).thenReturn(createStandingOrderResponse);
        assertNull(createStandingOrder.e032CreateStandingOrder("09", "770807", "24630368", "123", "234", requestHeader, "Internet Banking", applicationDetails,0));

    }

    @Test
    public void testCreateStandingOrderThrowsExternalServiceError() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        applicationDetails = new ApplicationDetails();
        createStandingOrderResponse = new E032Resp();
        createStandingOrderResponse.setE032Result(new E032Result());
        createStandingOrderResponse.getE032Result().setResultCondition(new ResultCondition());
        createStandingOrderResponse.getE032Result().getResultCondition().setReasonCode(8210002);
        createStandingOrderResponse.getE032Result().getResultCondition().setReasonText("External Service Error");
        when(createStandingOrder.cbsE032ClientMap.get("LTB").createStandingOrder(any(E032Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(CBSAppGrp.class))).thenReturn(createStandingOrderResponse);
        createStandingOrder.e032CreateStandingOrder("09", "770807", "24630368", "123", "234", requestHeader, "Internet Banking", applicationDetails,0);
        assertEquals("8210002", applicationDetails.getConditionList().get(0).getReasonCode());
        assertEquals("External Service Error", applicationDetails.getConditionList().get(0).getReasonText());
        assertTrue(applicationDetails.isApiFailureFlag());

    }

    @Test
    public void testCreateStandingOrderThrowsResourceNotAvailableError() {
        applicationDetails = new ApplicationDetails();
        when(createStandingOrder.appGroupRetriever.callRetrieveCBSAppGroup(requestHeader, "770807")).thenReturn("09");
        when(createStandingOrder.cbsE032ClientMap.get("LTB").createStandingOrder(any(E032Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(CBSAppGrp.class))).thenThrow(new WebServiceException("Resource not available"));
        createStandingOrder.e032CreateStandingOrder("09", "770807", "24630368", "123", "234", requestHeader, "Internet Banking", applicationDetails,0);
        assertEquals("820001", applicationDetails.getConditionList().get(0).getReasonCode());
        assertEquals("Resource not available", applicationDetails.getConditionList().get(0).getReasonText());
        assertTrue(applicationDetails.isApiFailureFlag());
    }

    @Test
    public void testCbsE032ClientMap() {
        Map<String, E032Client> e032ClientMap = new HashMap<>();
        e032ClientMap.put("LTB", mock(E032Client.class));
        createStandingOrder.setCbsE032ClientMap(e032ClientMap);
        assertEquals(e032ClientMap, createStandingOrder.getCbsE032ClientMap());
    }

    @Test
    public void testCreateStandingOrderWhenE032RespNullr() {
        applicationDetails = new ApplicationDetails();
        when(createStandingOrder.appGroupRetriever.callRetrieveCBSAppGroup(requestHeader, "770807")).thenReturn("09");
        createStandingOrderResponse = null;
        when(createStandingOrder.cbsE032ClientMap.get("LTB").createStandingOrder(any(E032Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(CBSAppGrp.class))).thenReturn(createStandingOrderResponse);
        createStandingOrder.e032CreateStandingOrder("09", "770807", "24630368", "123", "234", requestHeader, "Internet Banking", applicationDetails,0);
        assertFalse(applicationDetails.isApiFailureFlag());

    }
}
