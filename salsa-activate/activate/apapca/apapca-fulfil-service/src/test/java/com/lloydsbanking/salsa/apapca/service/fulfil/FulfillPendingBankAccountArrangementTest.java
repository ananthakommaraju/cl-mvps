package com.lloydsbanking.salsa.apapca.service.fulfil;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.downstream.CreateAccountRetriever;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.activate.downstream.RetrieveProductFeatures;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.activate.postfulfil.AsyncProcessFulfilmentActivitiesCaller;
import com.lloydsbanking.salsa.activate.sira.downstream.SiraRetriever;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.apapca.service.fulfil.downstream.CBSCustDetailsTrialRetriever;
import com.lloydsbanking.salsa.apapca.service.fulfil.downstream.CreateCaseRetriever;
import com.lloydsbanking.salsa.apapca.service.fulfil.downstream.CreateOverdraft;
import com.lloydsbanking.salsa.apapca.service.fulfil.downstream.OrderAccessItemRetriever;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamServiceForActivateDA;
import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class FulfillPendingBankAccountArrangementTest {
    lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse activateResponse;
    Product product = new Product();
    ActivateProductArrangementRequest activateProductArrangementRequest;
    Map<String, String> accountPurposeMap;
    lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse response;
    ExtraConditions extraConditions;
    private FulfillPendingBankAccountArrangement fulfillPendingBankAccountArrangement;
    private TestDataHelper testDataHelper;
    private RequestHeader requestHeader;
    private DepositArrangement depositArrangement;
    private ApplicationDetails applicationDetails;

    @Before
    public void setUp() {
        activateResponse = new lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse();
        extraConditions = new ExtraConditions();
        response = new lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse();
        accountPurposeMap = new HashMap<>();
        accountPurposeMap.put("SPORI", "1");
        applicationDetails = null;
        ProductArrangement productArrangement = new ProductArrangement();
        ResultCondition resultCondition = new ResultCondition();
        response.setResultCondition(resultCondition);
        response.setProductArrangement(productArrangement);
        fulfillPendingBankAccountArrangement = new FulfillPendingBankAccountArrangement();
        fulfillPendingBankAccountArrangement.createAccountRetriever = mock(CreateAccountRetriever.class);
        fulfillPendingBankAccountArrangement.cbsCustDetailsTrialRetriever = mock(CBSCustDetailsTrialRetriever.class);
        fulfillPendingBankAccountArrangement.retrieveProductFeatures = mock(RetrieveProductFeatures.class);
        fulfillPendingBankAccountArrangement.createOverdraft = mock(CreateOverdraft.class);
        fulfillPendingBankAccountArrangement.validateFulfillPendingBankAccountArrangement = new ValidateFulfillPendingBankAccountArrangement();
        fulfillPendingBankAccountArrangement.validateFulfillPendingBankAccountArrangement.switchClient = mock(SwitchService.class);
        fulfillPendingBankAccountArrangement.createCaseRetriever = mock(CreateCaseRetriever.class);
        fulfillPendingBankAccountArrangement.orderAccessItemRetriever = mock(OrderAccessItemRetriever.class);
        fulfillPendingBankAccountArrangement.updateDepositArrangementConditionAndApplicationStatusHelper = new UpdateDepositArrangementConditionAndApplicationStatusHelper();
        testDataHelper = new TestDataHelper();
        requestHeader = testDataHelper.createApaRequestHeader();
        fulfillPendingBankAccountArrangement.lookUpValueRetriever = mock(LookUpValueRetriever.class);
        depositArrangement = testDataHelper.createDepositArrangement("95412");
        depositArrangement.setRetryCount(1);
        fulfillPendingBankAccountArrangement.asyncProcessFulfilmentActivitiesCaller = mock(AsyncProcessFulfilmentActivitiesCaller.class);
        activateProductArrangementRequest = testDataHelper.createApaRequestForPca(123);
        activateProductArrangementRequest.getProductArrangement().setRetryCount(1);
        Product product1 = new Product();
        List<ProductOffer> productOffers = new ArrayList<>();
        product1.getProductoffer().addAll(productOffers);
        activateProductArrangementRequest.getProductArrangement().setAssociatedProduct(product1);
        product.setProductName("Classic");
        fulfillPendingBankAccountArrangement.updatePamServiceForActivateDA = mock(UpdatePamServiceForActivateDA.class);
        fulfillPendingBankAccountArrangement.siraRetriever = mock(SiraRetriever.class);

        fulfillPendingBankAccountArrangement.productArrangementTraceLog = mock(ProductArrangementTraceLog.class);
        when(fulfillPendingBankAccountArrangement.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class), any(String.class))).thenReturn("ProdArrngemnt");

    }

    @Test
    public void testFulfillPendingBankAccountArrangement() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        List<String> groupCodes = new ArrayList<>();
        groupCodes.add("ISO_COUNTRY_CODE");
        List<ReferenceDataLookUp> referenceDataLookUpList = testDataHelper.createLookupData();
        referenceDataLookUpList.add(new ReferenceDataLookUp("ISO_COUNTRY_CODE", "1", "Purpose of Account", 1091L, "SPORI", "LTB", 1L));
        when(fulfillPendingBankAccountArrangement.retrieveProductFeatures.getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class))).thenReturn(product);
        when(fulfillPendingBankAccountArrangement.lookUpValueRetriever.retrieveLookUpValues(requestHeader, groupCodes)).thenReturn(referenceDataLookUpList);
        fulfillPendingBankAccountArrangement.fulfillPendingBankAccountArrangement(response, activateProductArrangementRequest, accountPurposeMap);
        verify(fulfillPendingBankAccountArrangement.retrieveProductFeatures).getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class));
    }

    @Test
    public void testFulfillPendingBankAccountArrangementWithError() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        List<String> groupCodes = new ArrayList<>();
        groupCodes.add("ISO_COUNTRY_CODE");
        List<ReferenceDataLookUp> referenceDataLookUpList = testDataHelper.createLookupData();
        referenceDataLookUpList.add(new ReferenceDataLookUp("ISO_COUNTRY_CODE", "1", "Purpose of Account", 1091L, "SPORI", "LTB", 1L));
        when(fulfillPendingBankAccountArrangement.retrieveProductFeatures.getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class))).thenReturn(product);
        when(fulfillPendingBankAccountArrangement.lookUpValueRetriever.retrieveLookUpValues(requestHeader, groupCodes)).thenReturn(referenceDataLookUpList);
        fulfillPendingBankAccountArrangement.fulfillPendingBankAccountArrangement(response, activateProductArrangementRequest, accountPurposeMap);
        verify(fulfillPendingBankAccountArrangement.retrieveProductFeatures).getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class));
    }

    @Test
    public void testFulfillPendingBankAccountArrangementWithSubStatusUpdateCustomerRecordFailure() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        Map<String, String> map = new HashMap<>();
        map.put("country", "code");
        activateProductArrangementRequest.getProductArrangement().setApplicationSubStatus(ActivateCommonConstant.AppSubStatus.UPDATE_CUSTOMER_RECORD_FAILURE);
        when(fulfillPendingBankAccountArrangement.retrieveProductFeatures.getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class))).thenReturn(product);
        fulfillPendingBankAccountArrangement.fulfillPendingBankAccountArrangement(response, activateProductArrangementRequest, accountPurposeMap);
        verify(fulfillPendingBankAccountArrangement.retrieveProductFeatures).getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class));
    }

    @Test
    public void testFulfillPendingBankAccountArrangementWithSubStatusFailedToUpdateEmail() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        Map<String, String> map = new HashMap<>();
        map.put("country", "code");
        activateProductArrangementRequest.getProductArrangement().setApplicationSubStatus(ActivateCommonConstant.AppSubStatus.FAILED_TO_UPDATE_EMAIL_ADDRESS);
        when(fulfillPendingBankAccountArrangement.retrieveProductFeatures.getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class))).thenReturn(product);
        fulfillPendingBankAccountArrangement.fulfillPendingBankAccountArrangement(response, activateProductArrangementRequest, accountPurposeMap);
        verify(fulfillPendingBankAccountArrangement.retrieveProductFeatures).getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class));
    }

    @Test
    public void testFulfillPendingBankAccountArrangementWithSubStatusMarketingPrefUpdateFailure() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        Map<String, String> map = new HashMap<>();
        map.put("country", "code");
        activateProductArrangementRequest.getProductArrangement().setApplicationSubStatus(ActivateCommonConstant.AppSubStatus.MARKETING_PREF_UPDATE_FAILURE);
        when(fulfillPendingBankAccountArrangement.retrieveProductFeatures.getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class))).thenReturn(product);
        fulfillPendingBankAccountArrangement.fulfillPendingBankAccountArrangement(response, activateProductArrangementRequest, accountPurposeMap);
        verify(fulfillPendingBankAccountArrangement.retrieveProductFeatures).getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class));
    }


    @Test
    public void testFulfillPendingBankAccountArrangementWithSubStatusCustomerDetailsUpdateFailure() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        Map<String, String> map = new HashMap<>();
        map.put("country", "code");
        activateProductArrangementRequest.getProductArrangement().setApplicationSubStatus(ActivateCommonConstant.AppSubStatus.CUSTOMER_DETAILS_UPDATE_FAILURE);
        when(fulfillPendingBankAccountArrangement.retrieveProductFeatures.getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class))).thenReturn(product);
        fulfillPendingBankAccountArrangement.fulfillPendingBankAccountArrangement(response, activateProductArrangementRequest, accountPurposeMap);
        verify(fulfillPendingBankAccountArrangement.retrieveProductFeatures).getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class));
    }

    @Test
    public void testFulfillPendingBankAccountArrangementWithSubStatusAwaitingCRSFulfilmentFailure() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        Map<String, String> map = new HashMap<>();
        map.put("country", "code");
        activateProductArrangementRequest.getProductArrangement().setApplicationSubStatus(ActivateCommonConstant.AppSubStatus.AWAITING_CRS_FULFILLMENT_FAILURE);
        when(fulfillPendingBankAccountArrangement.retrieveProductFeatures.getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class))).thenReturn(product);
        fulfillPendingBankAccountArrangement.fulfillPendingBankAccountArrangement(response, activateProductArrangementRequest, accountPurposeMap);
        verify(fulfillPendingBankAccountArrangement.retrieveProductFeatures).getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class));
    }

    @Test
    public void testFulfillPendingBankAccountArrangementWithSubStatusFailedToCreateCardWithCondition() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        Map<String, String> map = new HashMap<>();
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("DEBIT_CARD_REQUIRED_FLAG");
        ruleCondition.setResult("Y");
        map.put("country", "code");
        activateProductArrangementRequest.getProductArrangement().setApplicationSubStatus(ActivateCommonConstant.AppSubStatus.FAILED_TO_CREATE_CARD_ORDER);
        activateProductArrangementRequest.getProductArrangement().getConditions().add(ruleCondition);
        when(fulfillPendingBankAccountArrangement.retrieveProductFeatures.getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class))).thenReturn(product);
        fulfillPendingBankAccountArrangement.fulfillPendingBankAccountArrangement(response, activateProductArrangementRequest, accountPurposeMap);
        verify(fulfillPendingBankAccountArrangement.retrieveProductFeatures).getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class));
    }

    @Test
    public void testFulfillPendingBankAccountArrangementWithSubStatusFailedToCreateCard() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        Map<String, String> map = new HashMap<>();
        map.put("country", "code");
        activateProductArrangementRequest.getProductArrangement().setApplicationSubStatus(ActivateCommonConstant.AppSubStatus.FAILED_TO_CREATE_CARD_ORDER);
        when(fulfillPendingBankAccountArrangement.retrieveProductFeatures.getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class))).thenReturn(product);
        fulfillPendingBankAccountArrangement.fulfillPendingBankAccountArrangement(response, activateProductArrangementRequest, accountPurposeMap);
        verify(fulfillPendingBankAccountArrangement.retrieveProductFeatures).getProduct(any(DepositArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class));
    }

    @Test
    public void testCreateCase() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        RuleCondition ruleCondition = new RuleCondition();
        product.getProductoffer().add(new ProductOffer());
        ruleCondition.setName("INTEND_TO_SWITCH");
        ruleCondition.setResult("Y");
        RuleCondition ruleCondition1 = new RuleCondition();
        RuleCondition ruleCondition2 = new RuleCondition();
        ruleCondition2.setName("INTEND_TO_SWITCH");
        depositArrangement.getConditions().add(ruleCondition);
        depositArrangement.getConditions().add(ruleCondition1);
        depositArrangement.getConditions().add(ruleCondition2);
        activateProductArrangementRequest.setProductArrangement(depositArrangement);
        when(fulfillPendingBankAccountArrangement.retrieveProductFeatures.getProduct(depositArrangement, null, activateProductArrangementRequest.getHeader())).thenReturn(product);
        fulfillPendingBankAccountArrangement.fulfillPendingBankAccountArrangement(activateResponse, activateProductArrangementRequest, accountPurposeMap);
        assertNull(depositArrangement.getApplicationSubStatus());
    }

    @Test
    public void testCreateCaseWithApplicationDetails() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        RuleCondition ruleCondition = new RuleCondition();
        product.getProductoffer().add(new ProductOffer());
        ruleCondition.setName("INTEND_TO_SWITCH");
        ruleCondition.setResult("Y");
        RuleCondition ruleCondition1 = new RuleCondition();
        RuleCondition ruleCondition2 = new RuleCondition();
        ruleCondition2.setName("INTEND_TO_SWITCH");
        depositArrangement.getConditions().add(ruleCondition);
        depositArrangement.getConditions().add(ruleCondition1);
        depositArrangement.getConditions().add(ruleCondition2);
        activateProductArrangementRequest.setProductArrangement(depositArrangement);
        when(fulfillPendingBankAccountArrangement.retrieveProductFeatures.getProduct(depositArrangement, null, activateProductArrangementRequest.getHeader())).thenReturn(product);
        fulfillPendingBankAccountArrangement.fulfillPendingBankAccountArrangement(activateResponse, activateProductArrangementRequest, accountPurposeMap);
        assertEquals(null, depositArrangement.getApplicationSubStatus());
    }

    private ApplicationDetails getApplicationDetails(String subStatus) {
        ApplicationDetails applicationDetails1 = new ApplicationDetails();
        applicationDetails1.setApplicationSubStatus(subStatus);
        applicationDetails1.setApplicationStatus(ApplicationStatus.AWAITING_FULFILMENT.getValue());
        applicationDetails1.setApiFailureFlag(false);
        return applicationDetails1;
    }
}

