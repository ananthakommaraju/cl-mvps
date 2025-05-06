package com.lloydsbanking.salsa.apapca.service;

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
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.apapca.logging.ApaPcaLogService;
import com.lloydsbanking.salsa.apapca.service.downstream.GetSortCodeByCoordinatesRetriever;
import com.lloydsbanking.salsa.apapca.service.fulfil.FulfillPendingBankAccountArrangement;
import com.lloydsbanking.salsa.apapca.service.propose.ProposedProductArrangementService;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamServiceForActivateDA;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class ApaPcaServiceTest {
    ApaPcaService apaPcaService;

    TestDataHelper testDataHelper;

    HeaderRetriever headerRetriever;

    @Before
    public void setUp() throws DatatypeConfigurationException, ActivateProductArrangementInternalSystemErrorMsg {
        apaPcaService = new ApaPcaService();
        apaPcaService.apaPcaLogService = mock(ApaPcaLogService.class);
        testDataHelper = new TestDataHelper();
        apaPcaService.headerRetriever = mock(HeaderRetriever.class);
        apaPcaService.lookUpValueRetriever = mock(LookUpValueRetriever.class);
        apaPcaService.exceptionUtilityActivate = mock(ExceptionUtilityActivate.class);
        apaPcaService.activateRequestValidator = mock(ActivateRequestValidator.class);
        apaPcaService.administerServiceCallValidator = mock(AdministerServiceCallValidator.class);
        apaPcaService.fulfillPendingBankAccountArrangement = mock(FulfillPendingBankAccountArrangement.class);
        apaPcaService.proposedProductArrangementService = mock(ProposedProductArrangementService.class);
        apaPcaService.retrievePendingArrangementService = mock(RetrievePendingArrangementService.class);
        apaPcaService.eidvStatusProcessor = mock(EidvStatusProcessor.class);
        when(apaPcaService.activateRequestValidator.validateRequest("1002", "1")).thenReturn(true);
        apaPcaService.registrationService = mock(RegistrationService.class);
        apaPcaService.requestToResponseHeaderConverter = mock(RequestToResponseHeaderConverter.class);
        apaPcaService.generateSortCodeByCoordinates = mock(GetSortCodeByCoordinatesRetriever.class);
        apaPcaService.encryptDataRetriever = mock(EncryptDataRetriever.class);
        apaPcaService.productArrangementTraceLog = mock(ProductArrangementTraceLog.class);
        apaPcaService.updatePamServiceForActivateDA = mock(UpdatePamServiceForActivateDA.class);
        when(apaPcaService.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class), any(String.class))).thenReturn("ProdArrngemnt");
    }


    @Test(expected = ActivateProductArrangementInternalSystemErrorMsg.class)
    public void testValidateRequestErrorScenario() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestWithInvalidStatus(1L);

        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        when(apaPcaService.headerRetriever.getContactPoint(request.getHeader().getLloydsHeaders())).thenReturn(contactPoint);
        when(apaPcaService.lookUpValueRetriever.getChannelId("0000777505", request.getHeader())).thenReturn(testDataHelper.createChannelIdLookupData());
        when(apaPcaService.lookUpValueRetriever.getLookUpValues(TestDataHelper.groupCodeList, "LTB")).thenReturn(testDataHelper.createLookupData());
        when(apaPcaService.activateRequestValidator.validateRequest("1002", "3")).thenReturn(false);
        when(apaPcaService.exceptionUtilityActivate.internalServiceError("810002", "Application State and Source System Identifier Combination is Invalid", request.getHeader())).thenReturn(new ActivateProductArrangementInternalSystemErrorMsg());
        apaPcaService.activateProductArrangement(request);
    }

    @Test
    public void getTimestamp() {
        java.util.Date d = new java.util.Date();
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DATE, 1);
        d = c.getTime();
        System.out.println((new java.sql.Timestamp(d.getTime())));
        //return new java.sql.Timestamp(d.getTime());
    }

    @Test
    public void testActivateProductArrangement() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForPca(1L);
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        when(apaPcaService.headerRetriever.getContactPoint(request.getHeader().getLloydsHeaders())).thenReturn(contactPoint);
        when(apaPcaService.lookUpValueRetriever.getChannelId("0000777505", request.getHeader())).thenReturn(testDataHelper.createChannelIdLookupData());
        when(apaPcaService.lookUpValueRetriever.getLookUpValues(TestDataHelper.groupCodeList, "LTB")).thenReturn(testDataHelper.createLookupData());
        when(apaPcaService.proposedProductArrangementService.callProposeProductArrangementAndSetAccountNumber(any(ActivateProductArrangementRequest.class), any(ActivateProductArrangementResponse.class))).thenReturn(true);
        when(apaPcaService.eidvStatusProcessor.validateApplicationAndGetFulfilmentEligibilityFlag(request.getProductArrangement(), request.getHeader(),request.getSourceSystemIdentifier())).thenReturn(true);
        request.setProductArrangement(testDataHelper.createDepositArrangementForB750());
        request.getProductArrangement().setGuardianDetails(new Customer());
        request.getProductArrangement().getGuardianDetails().getCustomerScore().add(new CustomerScore());
        request.getProductArrangement().getGuardianDetails().getCustomerScore().get(0).setScoreResult("REFER");
        request.getProductArrangement().setApplicationStatus("1002");
        apaPcaService.activateProductArrangement(request);
        verify(apaPcaService.activateRequestValidator).validateRequest(any(String.class), any(String.class));
    }

    @Test
    public void testActivateProductArrangementForAwaitingReScore() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForPca(1L);
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        request.setProductArrangement(testDataHelper.createDepositArrangementForB750());
        request.getProductArrangement().setApplicationStatus("1005");
        request.getProductArrangement().setRetryCount(1);
        when(apaPcaService.headerRetriever.getContactPoint(request.getHeader().getLloydsHeaders())).thenReturn(contactPoint);
        when(apaPcaService.lookUpValueRetriever.getChannelId("0000777505", request.getHeader())).thenReturn(testDataHelper.createChannelIdLookupData());
        when(apaPcaService.lookUpValueRetriever.getLookUpValues(TestDataHelper.groupCodeList, "LTB")).thenReturn(testDataHelper.createLookupData());
        when(apaPcaService.activateRequestValidator.validateRequest(any(String.class), any(String.class))).thenReturn(true);
        apaPcaService.activateProductArrangement(request);
        verify(apaPcaService.activateRequestValidator).validateRequest(any(String.class), any(String.class));
    }

    @Test
    public void testActivateProductArrangementForAwaitingManualId() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForPca(1L);
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        request.setProductArrangement(testDataHelper.createDepositArrangementForB750());
        request.getProductArrangement().setApplicationStatus("1007");
        request.getProductArrangement().setGuardianDetails(new Customer());
        request.getProductArrangement().getGuardianDetails().getCustomerScore().add(new CustomerScore());
        when(apaPcaService.headerRetriever.getContactPoint(request.getHeader().getLloydsHeaders())).thenReturn(contactPoint);
        when(apaPcaService.lookUpValueRetriever.getChannelId("0000777505", request.getHeader())).thenReturn(testDataHelper.createChannelIdLookupData());
        when(apaPcaService.lookUpValueRetriever.getLookUpValues(TestDataHelper.groupCodeList, "LTB")).thenReturn(testDataHelper.createLookupData());
        when(apaPcaService.activateRequestValidator.validateRequest(any(String.class), any(String.class))).thenReturn(true);
        apaPcaService.activateProductArrangement(request);
        verify(apaPcaService.activateRequestValidator).validateRequest(any(String.class), any(String.class));
    }

    @Test
    public void testActivateProductArrangementReEngineering() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForPca(1L);
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        request.getProductArrangement().setApplicationStatus("1002");
        request.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).setSortCode(null);
        Map<String, String> encryptionKeyMap = new HashMap<>();
        Map<String, Map<String, String>> keyMap = new HashMap<>();
        keyMap.put(ActivateCommonConstant.PamGroupCodes.PURPOSE_OF_ACCOUNT_CODE, encryptionKeyMap);
        when(apaPcaService.headerRetriever.getContactPoint(request.getHeader().getLloydsHeaders())).thenReturn(contactPoint);
        when(apaPcaService.lookUpValueRetriever.getChannelId("0000777505", request.getHeader())).thenReturn(testDataHelper.createChannelIdLookupData());
        when(apaPcaService.lookUpValueRetriever.getLookUpValues(TestDataHelper.groupCodeList, "LTB")).thenReturn(testDataHelper.createLookupData());
        when(apaPcaService.retrievePendingArrangementService.retrievePendingArrangements(request)).thenReturn(true);
        when(apaPcaService.lookUpValueRetriever.retrieveEncryptionKeyAndAccountPurposeMap(request.getHeader())).thenReturn(keyMap);
        when(apaPcaService.generateSortCodeByCoordinates.getSortCode(any(String.class), any(String.class), any(RequestHeader.class))).thenReturn("70762");
        when(apaPcaService.encryptDataRetriever.retrieveEncryptCardNumber(any(RequestHeader.class), any(List.class), any(String.class))).thenReturn(new ArrayList<String>());
        apaPcaService.activateProductArrangement(request);
        verify(apaPcaService.activateRequestValidator).validateRequest(any(String.class), any(String.class));
        verify(apaPcaService.retrievePendingArrangementService).retrievePendingArrangements(request);
    }

    @Test
    public void testActivateProductArrangementEidvStatusTrue() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForPca(1L);
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        when(apaPcaService.headerRetriever.getContactPoint(request.getHeader().getLloydsHeaders())).thenReturn(contactPoint);
        when(apaPcaService.lookUpValueRetriever.getChannelId("0000777505", request.getHeader())).thenReturn(testDataHelper.createChannelIdLookupData());
        when(apaPcaService.lookUpValueRetriever.getLookUpValues(TestDataHelper.groupCodeList, "LTB")).thenReturn(testDataHelper.createLookupData());
        request.setProductArrangement(testDataHelper.createDepositArrangementForB750());
        request.getProductArrangement().setGuardianDetails(new Customer());
        request.getProductArrangement().getGuardianDetails().getCustomerScore().add(new CustomerScore());
        request.getProductArrangement().getGuardianDetails().getCustomerScore().get(0).setScoreResult("REFER");
        request.getProductArrangement().setApplicationStatus("1002");
        when(apaPcaService.retrievePendingArrangementService.retrievePendingArrangements(request)).thenReturn(true);
        when(apaPcaService.eidvStatusProcessor.validateApplicationAndGetFulfilmentEligibilityFlag(request.getProductArrangement(), request.getHeader(),request.getSourceSystemIdentifier())).thenReturn(true);
        apaPcaService.activateProductArrangement(request);
        verify(apaPcaService.activateRequestValidator).validateRequest(any(String.class), any(String.class));
        verify(apaPcaService.retrievePendingArrangementService).retrievePendingArrangements(request);

    }

    @Test(expected = ActivateProductArrangementDataNotAvailableErrorMsg.class)
    public void testActivateProductArrangementDataNotAiailableError() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForPca(1L);
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        request.getProductArrangement().setApplicationStatus("1002");
        when(apaPcaService.headerRetriever.getContactPoint(request.getHeader().getLloydsHeaders())).thenReturn(contactPoint);
        when(apaPcaService.lookUpValueRetriever.getChannelId("0000777505", request.getHeader())).thenReturn(testDataHelper.createChannelIdLookupData());
        when(apaPcaService.lookUpValueRetriever.getLookUpValues(TestDataHelper.groupCodeList, "LTB")).thenReturn(testDataHelper.createLookupData());
        when(apaPcaService.activateRequestValidator.validateRequest("1002", "3")).thenReturn(false);
        when(apaPcaService.proposedProductArrangementService.callProposeProductArrangementAndSetAccountNumber(any(ActivateProductArrangementRequest.class), any(ActivateProductArrangementResponse.class))).thenThrow(ActivateProductArrangementDataNotAvailableErrorMsg.class);
        when(apaPcaService.exceptionUtilityActivate.dataNotAvailableError(any(String.class), any(String.class), any(String.class), any(RequestHeader.class))).thenThrow(ActivateProductArrangementDataNotAvailableErrorMsg.class);
        apaPcaService.activateProductArrangement(request);
    }

    @Test(expected = ActivateProductArrangementExternalSystemErrorMsg.class)
    public void testActivateProductArrangementExternalSystemError() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForPca(1L);
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        when(apaPcaService.headerRetriever.getContactPoint(request.getHeader().getLloydsHeaders())).thenReturn(contactPoint);
        when(apaPcaService.lookUpValueRetriever.getChannelId("0000777505", request.getHeader())).thenReturn(testDataHelper.createChannelIdLookupData());
        when(apaPcaService.lookUpValueRetriever.getLookUpValues(TestDataHelper.groupCodeList, "LTB")).thenReturn(testDataHelper.createLookupData());
        request.getProductArrangement().setApplicationStatus("1002");
        when(apaPcaService.proposedProductArrangementService.callProposeProductArrangementAndSetAccountNumber(any(ActivateProductArrangementRequest.class), any(ActivateProductArrangementResponse.class))).thenThrow(ActivateProductArrangementExternalSystemErrorMsg.class);
        apaPcaService.activateProductArrangement(request);
    }

    @Test(expected = ActivateProductArrangementInternalSystemErrorMsg.class)
    public void testActivateProductArrangementInternalServiceError() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForPca(1L);
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        when(apaPcaService.headerRetriever.getContactPoint(request.getHeader().getLloydsHeaders())).thenReturn(contactPoint);
        when(apaPcaService.lookUpValueRetriever.getChannelId("0000777505", request.getHeader())).thenReturn(testDataHelper.createChannelIdLookupData());
        when(apaPcaService.lookUpValueRetriever.getLookUpValues(TestDataHelper.groupCodeList, "LTB")).thenReturn(testDataHelper.createLookupData());
        request.setProductArrangement(testDataHelper.createDepositArrangementForB750());
        request.getProductArrangement().setGuardianDetails(new Customer());
        request.getProductArrangement().getGuardianDetails().getCustomerScore().add(new CustomerScore());
        request.getProductArrangement().getGuardianDetails().getCustomerScore().get(0).setScoreResult("REFER");
        request.getProductArrangement().setApplicationStatus("1002");
        request.getProductArrangement().setApplicationType("1234");
        when(apaPcaService.retrievePendingArrangementService.retrievePendingArrangements(request)).thenReturn(true);
        when(apaPcaService.exceptionUtilityActivate.internalServiceError("820001", "The Product Eligibility Type of the Application is Invalid", request.getHeader())).thenReturn(new ActivateProductArrangementInternalSystemErrorMsg());
        when(apaPcaService.eidvStatusProcessor.validateApplicationAndGetFulfilmentEligibilityFlag(request.getProductArrangement(), request.getHeader(),request.getSourceSystemIdentifier())).thenThrow(ActivateProductArrangementExternalSystemErrorMsg.class);
        apaPcaService.activateProductArrangement(request);
    }
}
