package com.lloydsbanking.salsa.apacc.service;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.administer.AdministerServiceCallValidator;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.downstream.EncryptDataRetriever;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.activate.registration.RegistrationService;
import com.lloydsbanking.salsa.activate.retrieve.RetrievePendingArrangementService;
import com.lloydsbanking.salsa.activate.utility.ActivateRequestValidator;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.activate.validator.EidvStatusProcessor;
import com.lloydsbanking.salsa.apacc.TestDataHelper;
import com.lloydsbanking.salsa.apacc.logging.ApaCcLogService;
import com.lloydsbanking.salsa.apacc.service.fulfil.FulfilPendingCreditCardArrangement;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamServiceForActivateDA;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamServiceForActivateFSA;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import lib_sim_bo.businessobjects.BalanceTransfer;
import lib_sim_bo.businessobjects.ExtraConditions;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class ApaCcServiceTest {
    ApaCcService apaCcService;

    TestDataHelper testDataHelper;

    @Before
    public void setUp() throws DatatypeConfigurationException, ActivateProductArrangementInternalSystemErrorMsg {
        apaCcService = new ApaCcService();
        apaCcService.apaCcLogService = mock(ApaCcLogService.class);
        apaCcService.lookUpValueRetriever = mock(LookUpValueRetriever.class);
        apaCcService.retrievePendingArrangementService = mock(RetrievePendingArrangementService.class);
        apaCcService.activateRequestValidator = mock(ActivateRequestValidator.class);
        apaCcService.exceptionUtilityActivate = mock(ExceptionUtilityActivate.class);
        apaCcService.fulfilPendingCreditCardArrangement = mock(FulfilPendingCreditCardArrangement.class);
        apaCcService.administerServiceCallValidator = mock(AdministerServiceCallValidator.class);
        apaCcService.eidvStatusProcessor = mock(EidvStatusProcessor.class);
        apaCcService.registrationService = mock(RegistrationService.class);
        apaCcService.requestToResponseHeaderConverter = mock(RequestToResponseHeaderConverter.class);
        apaCcService.updatePamServiceForActivateFSA = mock(UpdatePamServiceForActivateFSA.class);
        apaCcService.updatePamDetailsForActivateDA = mock(UpdatePamServiceForActivateDA.class);
        apaCcService.encryptDataRetriever = mock(EncryptDataRetriever.class);
        testDataHelper = new TestDataHelper();
        apaCcService.productArrangementTraceLog = mock(ProductArrangementTraceLog.class);
        when(apaCcService.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class), any(String.class))).thenReturn("ProdArrngemnt");

    }

    @Test
    public void testActivateProductArrangement() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForCc(1L);
        when(apaCcService.lookUpValueRetriever.getChannelId("0000777505", request.getHeader())).thenReturn(testDataHelper.createChannelIdLookupData());
        when(apaCcService.lookUpValueRetriever.getLookUpValues(TestDataHelper.groupCodeList, "LTB")).thenReturn(testDataHelper.createLookupData());
        when(apaCcService.activateRequestValidator.validateRequest("1002", "1")).thenReturn(true);
        request.setProductArrangement(testDataHelper.createFinanceServiceArrangement("1024"));
        request.getProductArrangement().setApplicationStatus("1002");
        ActivateProductArrangementResponse activateProductArrangementResponse = apaCcService.activateProductArrangement(request);
        assertNotNull(activateProductArrangementResponse);
    }

    @Test(expected = ActivateProductArrangementInternalSystemErrorMsg.class)
    public void testValidateRequestErrorScenario() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestWithInvalidStatus(1L);
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        when(apaCcService.lookUpValueRetriever.getChannelId("0000777505", request.getHeader())).thenReturn(testDataHelper.createChannelIdLookupData());
        when(apaCcService.lookUpValueRetriever.getLookUpValues(TestDataHelper.groupCodeList, "LTB")).thenReturn(testDataHelper.createLookupData());
        when(apaCcService.activateRequestValidator.validateRequest("1002", "3")).thenReturn(false);
        when(apaCcService.exceptionUtilityActivate.internalServiceError("810002", "Application State and Source System Identifier Combination is Invalid", request.getHeader())).thenReturn(new ActivateProductArrangementInternalSystemErrorMsg());
        apaCcService.activateProductArrangement(request);
    }

    @Test(expected = ActivateProductArrangementDataNotAvailableErrorMsg.class)
    public void testValidateRequestForDataNotAvailable() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestWithInvalidStatus(1L);
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        when(apaCcService.lookUpValueRetriever.retrieveEncryptionKeyAndAccountPurposeMap(request.getHeader())).thenThrow(ActivateProductArrangementDataNotAvailableErrorMsg.class);
        apaCcService.activateProductArrangement(request);
    }

    @Test
    public void testAssociatedProductOffer() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForCc(1L);
        when(apaCcService.lookUpValueRetriever.getChannelId("0000777505", request.getHeader())).thenReturn(testDataHelper.createChannelIdLookupData());
        when(apaCcService.lookUpValueRetriever.getLookUpValues(TestDataHelper.groupCodeList, "LTB")).thenReturn(testDataHelper.createLookupData());
        when(apaCcService.activateRequestValidator.validateRequest("1002", "1")).thenReturn(true);
        when(apaCcService.eidvStatusProcessor.validateApplicationAndGetFulfilmentEligibilityFlag(any(ProductArrangement.class), any(RequestHeader.class),any(String.class))).thenReturn(true);
        request.setProductArrangement(testDataHelper.createFinanceServiceArrangement("1024"));
        request.getProductArrangement().setApplicationStatus("1002");
        apaCcService.activateProductArrangement(request);
        verify(apaCcService.fulfilPendingCreditCardArrangement).fulfilPendingCreditCardArrangement(any(ActivateProductArrangementRequest.class), any(ActivateProductArrangementResponse.class), any(Map.class));
    }

    @Test
    public void testAssociatedProductOfferForIsAcceptedStatus() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForCc(1L);
        when(apaCcService.lookUpValueRetriever.getChannelId("0000777505", request.getHeader())).thenReturn(testDataHelper.createChannelIdLookupData());
        when(apaCcService.lookUpValueRetriever.getLookUpValues(TestDataHelper.groupCodeList, "LTB")).thenReturn(testDataHelper.createLookupData());
        when(apaCcService.activateRequestValidator.validateRequest("1002", "2")).thenReturn(true);
        when(apaCcService.eidvStatusProcessor.validateApplicationAndGetFulfilmentEligibilityFlag(any(ProductArrangement.class), any(RequestHeader.class),any(String.class))).thenReturn(true);
        when(apaCcService.administerServiceCallValidator.checkApplicationStatusAndCallAdministerService(any(ProductArrangement.class), any(RequestHeader.class), any(String.class))).thenReturn(new ExtraConditions());
        request.setProductArrangement(testDataHelper.createFinanceServiceArrangement("1024"));
        request.getProductArrangement().setApplicationStatus("1002");
        request.setSourceSystemIdentifier("2");
        apaCcService.activateProductArrangement(request);
        verify(apaCcService.fulfilPendingCreditCardArrangement).fulfilPendingCreditCardArrangement(any(ActivateProductArrangementRequest.class), any(ActivateProductArrangementResponse.class), any(Map.class));
    }

    @Test
    public void testEncryptCallData() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForCc(1L);
        Map<String, String> encryptionKeyMap = new HashMap<>();
        Map<String, Map<String, String>> keyMap = new HashMap<>();
        keyMap.put(ActivateCommonConstant.PamGroupCodes.ENCRYPT_KEY_GROUP_CODE, encryptionKeyMap);
        FinanceServiceArrangement financeServiceArrangement = testDataHelper.createFinanceServiceArrangement("1024");
        BalanceTransfer balanceTransfer = new BalanceTransfer();
        balanceTransfer.setCreditCardNumber("87340076");
        financeServiceArrangement.getBalanceTransfer().add(balanceTransfer);
        when(apaCcService.lookUpValueRetriever.getChannelId("0000777505", request.getHeader())).thenReturn(testDataHelper.createChannelIdLookupData());
        when(apaCcService.lookUpValueRetriever.getLookUpValues(TestDataHelper.groupCodeList, "LTB")).thenReturn(testDataHelper.createLookupData());
        when(apaCcService.activateRequestValidator.validateRequest("1002", "1")).thenReturn(true);
        when(apaCcService.lookUpValueRetriever.retrieveEncryptionKeyAndAccountPurposeMap(request.getHeader())).thenReturn(keyMap);
        when(apaCcService.encryptDataRetriever.retrieveEncryptCardNumber(any(RequestHeader.class), any(List.class), any(String.class))).thenReturn(new ArrayList<String>());
        request.setProductArrangement(financeServiceArrangement);
        request.getProductArrangement().setApplicationStatus("1002");
        apaCcService.activateProductArrangement(request);
        verify(apaCcService.retrievePendingArrangementService).retrievePendingArrangements(request);
    }
}