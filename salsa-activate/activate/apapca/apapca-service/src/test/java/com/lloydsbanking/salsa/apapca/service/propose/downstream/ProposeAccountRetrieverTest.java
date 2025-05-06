package com.lloydsbanking.salsa.apapca.service.propose.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.downstream.cbs.client.e229.E229Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.cbs.e229.objects.E229Req;
import com.lloydsbanking.salsa.soap.cbs.e229.objects.E229Resp;
import com.lloydsbanking.salsa.soap.cbs.e229.objects.E229Result;
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

import javax.xml.ws.WebServiceException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class ProposeAccountRetrieverTest {
    private ProposeAccountRetriever proposeAccountRetriever;

    Map<String, E229Client> clientE229Map;

    RequestHeader requestHeader;

    TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        clientE229Map = new HashMap<String, E229Client>();
        clientE229Map.put("LTB", mock(E229Client.class));
        clientE229Map.put("HLX", mock(E229Client.class));
        proposeAccountRetriever = new ProposeAccountRetriever();
        proposeAccountRetriever.cbsE229ClientMap = clientE229Map;
        proposeAccountRetriever.headerRetriever = new HeaderRetriever();
        proposeAccountRetriever.exceptionUtilityActivate = mock(ExceptionUtilityActivate.class);
        testDataHelper = new TestDataHelper();
        requestHeader = testDataHelper.createApaRequestHeader();
    }

    @Test
    public void testProposeAccount() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        E229Client e229Client = proposeAccountRetriever.cbsE229ClientMap.get("LTB");
        when(e229Client.proposeAccount(any(E229Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(CBSAppGrp.class))).thenReturn(testDataHelper.createE229Resp());
        E229Resp response = proposeAccountRetriever.proposeAccount(requestHeader, "00705", "779129");
        assertEquals("779129", response.getStemId());
        assertEquals("Error while getting response", response.getE229Result().getResultCondition().getReasonText());
        assertEquals("1458", response.getE229Result().getResultCondition().getReasonCode().toString());
    }

    @Test(expected = ActivateProductArrangementResourceNotAvailableErrorMsg.class)
    public void testCheckResponseThrowsResourceNotAvailableErrorMsg() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        E229Client e229Client = proposeAccountRetriever.cbsE229ClientMap.get("LTB");
        when(e229Client.proposeAccount(any(E229Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(CBSAppGrp.class))).thenThrow(WebServiceException.class);
        when(proposeAccountRetriever.exceptionUtilityActivate.resourceNotAvailableError(any(RequestHeader.class), any(String.class))).thenReturn(new ActivateProductArrangementResourceNotAvailableErrorMsg());
        proposeAccountRetriever.proposeAccount(requestHeader, "00705", "779129");
    }

    @Test(expected = ActivateProductArrangementExternalSystemErrorMsg.class)
    public void testCheckResponseThrowsExternalSystemErrorMsg() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        E229Client e229Client = proposeAccountRetriever.cbsE229ClientMap.get("LTB");
        E229Resp e229Resp = testDataHelper.createE229Resp();
        e229Resp.setE229Result(new E229Result());
        e229Resp.getE229Result().setResultCondition(new ResultCondition());
        e229Resp.getE229Result().getResultCondition().setReasonCode(145);
        when(e229Client.proposeAccount(any(E229Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(CBSAppGrp.class))).thenReturn(e229Resp);
        when(proposeAccountRetriever.exceptionUtilityActivate.externalServiceError(requestHeader, null, "145")).thenReturn(new ActivateProductArrangementExternalSystemErrorMsg());
        proposeAccountRetriever.proposeAccount(requestHeader, "00705", "779129");
    }

    @Test
    public void testCheckResponseThrowsExternalSystemErrorMsgWithReasonCodeAsSuccess() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        E229Client e229Client = proposeAccountRetriever.cbsE229ClientMap.get("LTB");
        E229Resp e229Resp = testDataHelper.createE229Resp();
        e229Resp.setE229Result(new E229Result());
        e229Resp.getE229Result().setResultCondition(new ResultCondition());
        e229Resp.getE229Result().getResultCondition().setReasonCode(0);
        when(e229Client.proposeAccount(any(E229Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(CBSAppGrp.class))).thenReturn(testDataHelper.createE229RespWithoutError());
        E229Resp response = proposeAccountRetriever.proposeAccount(requestHeader, "00705", "779129");
        assertEquals("779129", response.getStemId());
        assertEquals("0", response.getE229Result().getResultCondition().getReasonCode().toString());
        assertEquals("6", response.getCbsAcTypeChkDgt().get(0).getCBSAccountTypeCd());
    }

    @Test
    public void testFetchSwitchValueWithError() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        E229Client e229Client = proposeAccountRetriever.cbsE229ClientMap.get("LTB");
        when(e229Client.proposeAccount(any(E229Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(CBSAppGrp.class))).thenReturn(testDataHelper.createE229Resp());
        E229Resp response = proposeAccountRetriever.proposeAccount(requestHeader, "00705", "779129");
        assertEquals("779129", response.getStemId());
        assertEquals("Error while getting response", response.getE229Result().getResultCondition().getReasonText());
        assertEquals("1458", response.getE229Result().getResultCondition().getReasonCode().toString());
    }


}


