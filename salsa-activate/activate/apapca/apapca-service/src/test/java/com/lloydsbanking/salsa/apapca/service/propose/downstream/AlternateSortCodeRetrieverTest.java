package com.lloydsbanking.salsa.apapca.service.propose.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.downstream.cbs.client.e469.E469Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.cbs.e469.objects.E469Req;
import com.lloydsbanking.salsa.soap.cbs.e469.objects.E469Resp;
import com.lloydstsb.schema.infrastructure.soap.CBSAppGrp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
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
public class AlternateSortCodeRetrieverTest {
    private AlternateSortCodeRetriever alternateSortCodeRetriever;

    Map<String, E469Client> clientE469Map;

    RequestHeader requestHeader;

    TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        clientE469Map = new HashMap<String, E469Client>();
        clientE469Map.put("LTB", mock(E469Client.class));
        clientE469Map.put("HLX", mock(E469Client.class));

        alternateSortCodeRetriever = new AlternateSortCodeRetriever();
        alternateSortCodeRetriever.cbsE469ClientMap = clientE469Map;
        alternateSortCodeRetriever.exceptionUtilityActivate = mock(ExceptionUtilityActivate.class);
        alternateSortCodeRetriever.headerRetriever = new HeaderRetriever();
        testDataHelper = new TestDataHelper();
        requestHeader = testDataHelper.createApaRequestHeader();
    }

    @Test
    public void testProposeAccount() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        E469Client e469Client = alternateSortCodeRetriever.cbsE469ClientMap.get("LTB");
        when(e469Client.retrieveAlternateSortCode(any(E469Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(CBSAppGrp.class))).thenReturn(testDataHelper.createE469Resp());
        String proposeAccount = alternateSortCodeRetriever.proposeAccount(requestHeader, "007505", TestDataHelper.TEST_CONTACT_POINT_ID);
        assertEquals("007505", proposeAccount);
    }

    @Test(expected = ActivateProductArrangementResourceNotAvailableErrorMsg.class)
    public void testCheckResponseThrowsResourceNotAvailableErrorMsg() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        E469Client e469Client = alternateSortCodeRetriever.cbsE469ClientMap.get("LTB");
        when(e469Client.retrieveAlternateSortCode(any(E469Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(CBSAppGrp.class))).thenThrow(WebServiceException.class);
        when(alternateSortCodeRetriever.exceptionUtilityActivate.resourceNotAvailableError(any(RequestHeader.class), any(String.class))).thenThrow(ActivateProductArrangementResourceNotAvailableErrorMsg.class);
        alternateSortCodeRetriever.proposeAccount(requestHeader, "007505", TestDataHelper.TEST_CONTACT_POINT_ID);
    }

    @Test(expected = ActivateProductArrangementExternalSystemErrorMsg.class)
    public void testCheckResponseThrowsExternalSystemErrorMsg() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        E469Client e469Client = alternateSortCodeRetriever.cbsE469ClientMap.get("LTB");
        E469Resp response = testDataHelper.createE469Resp();
        response.getE469Result().getResultCondition().setSeverityCode((byte) 1);
        when(e469Client.retrieveAlternateSortCode(any(E469Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(CBSAppGrp.class))).thenReturn(response);
        when(alternateSortCodeRetriever.exceptionUtilityActivate.externalServiceError(requestHeader, "Error while getting response", "131")).thenThrow(ActivateProductArrangementExternalSystemErrorMsg.class);
        alternateSortCodeRetriever.proposeAccount(requestHeader, "007505", TestDataHelper.TEST_CONTACT_POINT_ID);
    }

    @Test
    public void testFetchSwitchValueWithError() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        E469Client e469Client = alternateSortCodeRetriever.cbsE469ClientMap.get("LTB");
        when(e469Client.retrieveAlternateSortCode(any(E469Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(CBSAppGrp.class))).thenReturn(testDataHelper.createE469Resp());
        String proposeAccount = alternateSortCodeRetriever.proposeAccount(requestHeader, "007505", TestDataHelper.TEST_CONTACT_POINT_ID);
        assertEquals("007505", proposeAccount);
    }
}
