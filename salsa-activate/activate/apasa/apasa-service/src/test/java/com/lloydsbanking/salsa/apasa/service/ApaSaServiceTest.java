package com.lloydsbanking.salsa.apasa.service;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.administer.AdministerServiceCallValidator;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.activate.registration.RegistrationService;
import com.lloydsbanking.salsa.activate.retrieve.RetrievePendingArrangementService;
import com.lloydsbanking.salsa.activate.utility.ActivateRequestValidator;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.activate.validator.EidvStatusProcessor;
import com.lloydsbanking.salsa.apasa.TestDataHelper;
import com.lloydsbanking.salsa.apasa.logging.ApaSaLogService;
import com.lloydsbanking.salsa.apasa.service.fulfil.FulfilPendingSavingsArrangement;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamServiceForActivateDA;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import lib_sim_bo.businessobjects.Condition;
import lib_sim_bo.businessobjects.ExtraConditions;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class ApaSaServiceTest {
    ApaSaService apaSaService;

    TestDataHelper testDataHelper;
    Map<String, String> countryCodeMap;
    ActivateProductArrangementResponse response;
    Map<String, String> accountMap;

    @Before
    public void setUp() throws DatatypeConfigurationException {
        response = new ActivateProductArrangementResponse();
        accountMap = new HashMap<>();
        apaSaService = new ApaSaService();
        apaSaService.apaSaLogService = mock(ApaSaLogService.class);
        apaSaService.lookUpValueRetriever = mock(LookUpValueRetriever.class);
        apaSaService.retrievePendingArrangementService = mock(RetrievePendingArrangementService.class);
        apaSaService.activateRequestValidator = mock(ActivateRequestValidator.class);
        apaSaService.eidvStatusProcessor = mock(EidvStatusProcessor.class);
        apaSaService.administerServiceCallValidator = mock(AdministerServiceCallValidator.class);
        apaSaService.exceptionUtilityActivate = mock(ExceptionUtilityActivate.class);
        apaSaService.registrationService = mock(RegistrationService.class);
        testDataHelper = new TestDataHelper();
        apaSaService.fulfilPendingSavingsArrangement = mock(FulfilPendingSavingsArrangement.class);
        apaSaService.requestToResponseHeaderConverter = mock(RequestToResponseHeaderConverter.class);
        apaSaService.updatePamServiceForActivateDA = mock(UpdatePamServiceForActivateDA.class);
        countryCodeMap = new HashMap<>();
        countryCodeMap.put("1", "U.K");
        apaSaService.productArrangementTraceLog = mock(ProductArrangementTraceLog.class);
        when(apaSaService.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class), any(String.class))).thenReturn("ProdArrngemnt");
    }

    @Test
    public void testActivateProductArrangement() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForSa();
        when(apaSaService.lookUpValueRetriever.getChannelId("0000777505", request.getHeader())).thenReturn(testDataHelper.createChannelIdLookupData());
        when(apaSaService.lookUpValueRetriever.getLookUpValues(TestDataHelper.groupCodeList, "LTB")).thenReturn(testDataHelper.createLookupData());
        when(apaSaService.activateRequestValidator.validateRequest("1002", "1")).thenReturn(true);
        request.setProductArrangement(testDataHelper.createDepositArrangement("1024"));
        request.getProductArrangement().setApplicationStatus("1002");
        ActivateProductArrangementResponse activateProductArrangementResponse = apaSaService.activateProductArrangement(request);
        Assert.assertNotNull(activateProductArrangementResponse);
        assertNotNull(activateProductArrangementResponse);
    }

    @Test(expected = ActivateProductArrangementInternalSystemErrorMsg.class)
    public void testValidateRequestErrorScenario() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestWithInvalidStatus(1L);
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        when(apaSaService.lookUpValueRetriever.getChannelId("0000777505", request.getHeader())).thenReturn(testDataHelper.createChannelIdLookupData());
        when(apaSaService.lookUpValueRetriever.getLookUpValues(TestDataHelper.groupCodeList, "LTB")).thenReturn(testDataHelper.createLookupData());
        when(apaSaService.activateRequestValidator.validateRequest("1002", "3")).thenReturn(false);
        when(apaSaService.exceptionUtilityActivate.internalServiceError("810002", "Application State and Source System Identifier Combination is Invalid", request.getHeader())).thenReturn(new ActivateProductArrangementInternalSystemErrorMsg());
        apaSaService.activateProductArrangement(request);
    }

    @Test
    public void testWhenExtraConditionsNotNull() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        ExtraConditions extraConditions = new ExtraConditions();
        extraConditions.getConditions().add(new Condition());
        when(apaSaService.administerServiceCallValidator.checkApplicationStatusAndCallAdministerService(any(ProductArrangement.class), any(RequestHeader.class), any(String.class))).thenReturn(extraConditions);
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForSa();
        when(apaSaService.lookUpValueRetriever.getChannelId("0000777505", request.getHeader())).thenReturn(testDataHelper.createChannelIdLookupData());
        when(apaSaService.lookUpValueRetriever.getLookUpValues(TestDataHelper.groupCodeList, "LTB")).thenReturn(testDataHelper.createLookupData());
        when(apaSaService.activateRequestValidator.validateRequest("1002", "1")).thenReturn(true);
        request.setProductArrangement(testDataHelper.createDepositArrangement("1024"));
        request.getProductArrangement().setApplicationStatus("1002");
        ActivateProductArrangementResponse activateProductArrangementResponse = apaSaService.activateProductArrangement(request);
        Assert.assertNotNull(activateProductArrangementResponse);
        assertNotNull(activateProductArrangementResponse);
    }

    @Test(expected = ActivateProductArrangementInternalSystemErrorMsg.class)
    public void testInternalServiceError() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        ExtraConditions extraConditions = new ExtraConditions();
        extraConditions.getConditions().add(new Condition());
        when(apaSaService.administerServiceCallValidator.checkApplicationStatusAndCallAdministerService(any(ProductArrangement.class), any(RequestHeader.class), any(String.class))).thenReturn(extraConditions);
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForSa();
        when(apaSaService.lookUpValueRetriever.getChannelId("0000777505", request.getHeader())).thenReturn(testDataHelper.createChannelIdLookupData());
        when(apaSaService.lookUpValueRetriever.getLookUpValues(TestDataHelper.groupCodeList, "LTB")).thenReturn(testDataHelper.createLookupData());
        when(apaSaService.activateRequestValidator.validateRequest("1002", "1")).thenReturn(true);
        request.setProductArrangement(testDataHelper.createDepositArrangement("1024"));
        request.getProductArrangement().setApplicationStatus("1002");
        request.getProductArrangement().setApplicationType("1000");
        when(apaSaService.exceptionUtilityActivate.internalServiceError(any(String.class), any(String.class), any(RequestHeader.class))).thenReturn(new ActivateProductArrangementInternalSystemErrorMsg());
        ActivateProductArrangementResponse activateProductArrangementResponse = apaSaService.activateProductArrangement(request);
        Assert.assertNotNull(activateProductArrangementResponse);
        assertNotNull(activateProductArrangementResponse);
    }
}