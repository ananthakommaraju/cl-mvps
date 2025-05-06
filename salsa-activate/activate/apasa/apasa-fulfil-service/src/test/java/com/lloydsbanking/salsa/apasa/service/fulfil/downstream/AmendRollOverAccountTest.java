package com.lloydsbanking.salsa.apasa.service.fulfil.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.apasa.TestDataHelper;
import com.lloydsbanking.salsa.apasa.service.fulfil.convert.E502RequestFactory;
import com.lloydsbanking.salsa.downstream.cbs.client.e502.E502Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.cbs.e502.objects.E502Req;
import com.lloydsbanking.salsa.soap.cbs.e502.objects.E502Resp;
import com.lloydsbanking.salsa.soap.cbs.e502.objects.E502Result;
import com.lloydstsb.schema.infrastructure.soap.CBSAppGrp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import junit.framework.Assert;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.ExtraConditions;
import lib_sim_bo.businessobjects.RuleCondition;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalBusinessErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class AmendRollOverAccountTest {
    private TestDataHelper dataHelper;

    private AmendRollOverAccount amendRollOverAccount;

    private E502Resp amendRollOverAccountResponse;

    private RequestHeader requestHeader;
    DepositArrangement depositArrangement;
    ExtraConditions extraConditions;


    @Before
    public void setUp() throws DatatypeConfigurationException, ParseException {

        dataHelper = new TestDataHelper();
        extraConditions = new ExtraConditions();
        depositArrangement = dataHelper.createDepositArrangement("123");
        RuleCondition ruleCondition = new RuleCondition();
        depositArrangement.getConditions().add(ruleCondition);
        amendRollOverAccount = new AmendRollOverAccount();
        amendRollOverAccount.appGroupRetriever = mock(AppGroupRetriever.class);
        amendRollOverAccount.headerRetriever = new HeaderRetriever();
        requestHeader = dataHelper.createApaRequestHeader();
        final Map<String, E502Client> clientMap;
        clientMap = new HashMap<String, E502Client>();
        clientMap.put("LTB", mock(E502Client.class));
        amendRollOverAccount.cbsE502ClientMap = clientMap;
        amendRollOverAccount.requestFactory = new E502RequestFactory();
    }

    @Test
    public void testCreateStandingOrder() throws ActivateProductArrangementExternalBusinessErrorMsg {
        when(amendRollOverAccount.appGroupRetriever.callRetrieveCBSAppGroup(requestHeader, "770807")).thenReturn("09");
        amendRollOverAccountResponse = new E502Resp();
        amendRollOverAccountResponse.setE502Result(new E502Result());
        amendRollOverAccountResponse.getE502Result().setResultCondition(new ResultCondition());
        amendRollOverAccountResponse.getE502Result().getResultCondition().setReasonCode(0);
        when(amendRollOverAccount.cbsE502ClientMap.get("LTB").amendRollOverAccount(any(E502Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(CBSAppGrp.class))).thenReturn(amendRollOverAccountResponse);
        amendRollOverAccount.amendRollOverAccount(depositArrangement, requestHeader);
        Assert.assertEquals("LINKING", depositArrangement.getConditions().get(0).getName());
        Assert.assertEquals("TRUE", depositArrangement.getConditions().get(0).getResult());
    }


    @Test
    public void testCbsE502ClientMap() {
        Map<String, E502Client> e502ClientMap = new HashMap<>();
        e502ClientMap.put("LTB", mock(E502Client.class));
        amendRollOverAccount.setCbsE502ClientMap(e502ClientMap);
        assertEquals(e502ClientMap, amendRollOverAccount.getCbsE502ClientMap());
    }

    @Test
    public void testCreateStandingOrderErrorScenario() throws ActivateProductArrangementExternalBusinessErrorMsg {
        when(amendRollOverAccount.appGroupRetriever.callRetrieveCBSAppGroup(requestHeader, "770807")).thenReturn("09");
        amendRollOverAccountResponse = new E502Resp();
        amendRollOverAccountResponse.setE502Result(new E502Result());
        amendRollOverAccountResponse.getE502Result().setResultCondition(new ResultCondition());
        amendRollOverAccountResponse.getE502Result().getResultCondition().setReasonCode(1);
        when(amendRollOverAccount.cbsE502ClientMap.get("LTB").amendRollOverAccount(any(E502Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(CBSAppGrp.class))).thenReturn(amendRollOverAccountResponse);
        amendRollOverAccount.amendRollOverAccount(depositArrangement, requestHeader);
        assertEquals("FALSE", depositArrangement.getConditions().get(0).getResult());
        assertEquals("LINKING", depositArrangement.getConditions().get(0).getName());
    }

    @Test
    public void testCheckResponse() {
        assertNull(amendRollOverAccount.checkResponse(null, new DepositArrangement()));
    }
}
