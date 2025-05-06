package com.lloydsbanking.salsa.apapca.service.fulfil.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.apapca.service.fulfil.converter.DepositArrangementToE226Request;
import com.lloydsbanking.salsa.downstream.cbs.client.e226.E226Client;
import com.lloydsbanking.salsa.downstream.pam.service.constant.PamConstant;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.cbs.e226.objects.E226Req;
import com.lloydsbanking.salsa.soap.cbs.e226.objects.E226Resp;
import com.lloydsbanking.salsa.soap.cbs.e226.objects.E226Result;
import com.lloydstsb.schema.infrastructure.soap.CBSAppGrp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.CurrencyAmount;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.OverdraftDetails;
import lib_sim_bo.businessobjects.RuleCondition;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.ws.WebServiceException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CBSCustDetailsTrialRetrieverTest {
    CBSCustDetailsTrialRetriever cbsCustDetailsTrialRetriever;

    TestDataHelper testDataHelper;

    DepositArrangement depositArrangement;

    RequestHeader requestHeader;

    ApplicationDetails applicationDetails;

    ContactPoint contactPoint;

    CBSAppGrp cbsAppGrp;

    ServiceRequest serviceRequest;

    SecurityHeaderType securityHeaderType;

    E226Req e226Req;

    E226Resp e226Resp;

    Map<String, E226Client> clientE226Map;

    @Before
    public void setUp() {
        cbsCustDetailsTrialRetriever = new CBSCustDetailsTrialRetriever();
        clientE226Map = new HashMap<String, E226Client>();
        clientE226Map.put("LTB", mock(E226Client.class));
        clientE226Map.put("HLX", mock(E226Client.class));

        testDataHelper = new TestDataHelper();
        requestHeader = testDataHelper.createApaRequestHeader();
        e226Req = new E226Req();
        e226Resp = new E226Resp();
        e226Resp.setAdditionalDataIn(1234);

        e226Req.setCardOfferCd(123);
        contactPoint = new ContactPoint();
        contactPoint.setApplicationId("123");
        applicationDetails = new ApplicationDetails();
        applicationDetails.setApiFailureFlag(true);
        cbsAppGrp = new CBSAppGrp();
        cbsCustDetailsTrialRetriever.cbsE226ClientMap = clientE226Map;

        depositArrangement = testDataHelper.createDepositArrangement("123");
        requestHeader = testDataHelper.createApaRequestHeader();
        cbsCustDetailsTrialRetriever.updateDepositArrangementConditionAndApplicationStatusHelper = mock(UpdateDepositArrangementConditionAndApplicationStatusHelper.class);
        cbsCustDetailsTrialRetriever.headerRetriever = new HeaderRetriever();
        serviceRequest = new ServiceRequest();
        serviceRequest.setAction("asd");
        securityHeaderType = new SecurityHeaderType();

        cbsCustDetailsTrialRetriever.depositArrangementToE226Request = mock(DepositArrangementToE226Request.class);
        cbsCustDetailsTrialRetriever.updateDepositArrangementConditionAndApplicationStatusHelper = mock(UpdateDepositArrangementConditionAndApplicationStatusHelper.class);
        OverdraftDetails overdraftDetails = new OverdraftDetails();
        CurrencyAmount currencyAmount = new CurrencyAmount();
        overdraftDetails.setAmount(currencyAmount);
        overdraftDetails.getAmount().setAmount(BigDecimal.valueOf(1234));
        depositArrangement.setOverdraftDetails(overdraftDetails);
    }

    @Test
    public void testCBSCustDetailsItem() {
        E226Result e226Result = new E226Result();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setReasonCode(0);
        e226Result.setResultCondition(resultCondition);
        e226Resp.setE226Result(e226Result);
        E226Client e226Client = cbsCustDetailsTrialRetriever.cbsE226ClientMap.get("LTB");
        when(e226Client.createDecisionTrailersInCBS(any(E226Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(CBSAppGrp.class))).thenReturn(e226Resp);
        applicationDetails = new ApplicationDetails();
        cbsCustDetailsTrialRetriever.addDecisionTrial(depositArrangement, requestHeader, applicationDetails);
        assertNotNull(applicationDetails);
    }

    @Test
    public void testCBSCustDetailsItemWhenThrowException() {
        E226Client e226Client = cbsCustDetailsTrialRetriever.cbsE226ClientMap.get("LTB");
        when(e226Client.createDecisionTrailersInCBS(any(E226Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(CBSAppGrp.class))).thenThrow(WebServiceException.class);
        applicationDetails = new ApplicationDetails();
        cbsCustDetailsTrialRetriever.addDecisionTrial(depositArrangement, requestHeader, applicationDetails);
        assertFalse(applicationDetails.isApiFailureFlag());
    }

    @Test
    public void testCBSCustDSetailsItemWithOverdraftRequired() {
        depositArrangement.setIsOverdraftRequired(true);
        E226Result e226Result = new E226Result();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setReasonCode(1002);
        e226Result.setResultCondition(resultCondition);
        e226Resp.setE226Result(e226Result);
        E226Client e226Client = cbsCustDetailsTrialRetriever.cbsE226ClientMap.get("LTB");
        when(e226Client.createDecisionTrailersInCBS(any(E226Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(CBSAppGrp.class))).thenReturn(e226Resp);
        applicationDetails = new ApplicationDetails();
        cbsCustDetailsTrialRetriever.addDecisionTrial(depositArrangement, requestHeader, applicationDetails);
        assertNotNull(applicationDetails);
    }

    @Test
    public void testCBSCustDetailsItemWithSwitchClientException() {
        E226Result e226Result = new E226Result();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setReasonCode(3332);
        e226Result.setResultCondition(resultCondition);
        e226Resp.setE226Result(e226Result);
        E226Client e226Client = cbsCustDetailsTrialRetriever.cbsE226ClientMap.get("LTB");
        when(e226Client.createDecisionTrailersInCBS(any(E226Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(CBSAppGrp.class))).thenReturn(e226Resp);
        applicationDetails = new ApplicationDetails();
        cbsCustDetailsTrialRetriever.addDecisionTrial(depositArrangement, requestHeader, applicationDetails);
        assertNotNull(applicationDetails);
    }

    @Test
    public void testCBSCustDSetailsItemWithOverdraftNotRequired() {
        depositArrangement.setIsOverdraftRequired(false);
        RuleCondition condition = new RuleCondition();
        condition.setName("OFFERED_OVERDRAFT_AMOUNT");
        CurrencyAmount amount = new CurrencyAmount();
        amount.setAmount(BigDecimal.valueOf(1234));
        condition.setValue(amount);
        depositArrangement.getConditions().add(condition);
        E226Result e226Result = new E226Result();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setReasonCode(1002);
        e226Result.setResultCondition(resultCondition);
        e226Resp.setE226Result(e226Result);
        E226Client e226Client = cbsCustDetailsTrialRetriever.cbsE226ClientMap.get("LTB");
        when(e226Client.createDecisionTrailersInCBS(any(E226Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(CBSAppGrp.class))).thenReturn(e226Resp);
        applicationDetails = new ApplicationDetails();
        cbsCustDetailsTrialRetriever.addDecisionTrial(depositArrangement, requestHeader, applicationDetails);
        assertNotNull(applicationDetails);
    }

}
