package com.lloydsbanking.salsa.opapca.service;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.pam.service.CreatePamService;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamService;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import com.lloydsbanking.salsa.offer.ApplicantType;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.apply.ApplyService;
import com.lloydsbanking.salsa.offer.createinvolvedparty.CreateInvolvedPartyService;
import com.lloydsbanking.salsa.offer.eligibility.downstream.EligibilityService;
import com.lloydsbanking.salsa.offer.identify.IdentifyService;
import com.lloydsbanking.salsa.offer.pam.service.DuplicateApplicationCheckService;
import com.lloydsbanking.salsa.offer.verify.VerifyInvolvedPartyRoleService;
import com.lloydsbanking.salsa.opapca.logging.OpapcaLogService;
import com.lloydsbanking.salsa.opapca.service.downstream.SiraRetriever;
import com.lloydsbanking.salsa.opapca.service.utility.ExceptionHelper;
import com.lloydsbanking.salsa.opapca.service.validate.RequestValidatorAndInitializer;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_gmo.messages.ResponseHeader;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class OpaPcaServiceTest {
    private OpaPcaService opaPcaService;


    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        opaPcaService = new OpaPcaService();
        opaPcaService.responseHeaderConverter = new RequestToResponseHeaderConverter();
        opaPcaService.applyService = mock(ApplyService.class);
        opaPcaService.opapcaLogService = mock(OpapcaLogService.class);
        opaPcaService.createPamService = mock(CreatePamService.class);
        opaPcaService.siraRetriever=mock(SiraRetriever.class);
        opaPcaService.createInvolvedPartyService = mock(CreateInvolvedPartyService.class);
        opaPcaService.duplicateApplicationCheckService = mock(DuplicateApplicationCheckService.class);
        opaPcaService.involvedPartyIdentifier = mock(IdentifyService.class);
        opaPcaService.updatePamService = mock(UpdatePamService.class);
        opaPcaService.validatorAndInitializer = mock(RequestValidatorAndInitializer.class);
        opaPcaService.eligibilityService = mock(EligibilityService.class);
        opaPcaService.verifyInvolvedPartyRoleService = mock(VerifyInvolvedPartyRoleService.class);
        opaPcaService.productArrangementTraceLog = mock(ProductArrangementTraceLog.class);
        opaPcaService.exceptionHelper = mock(ExceptionHelper.class);

        when(opaPcaService.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class), any(String.class))).thenReturn("ProductArrangement");
    }

    @Test
    public void testOpapcaServiceWhenBFPOIndicatorIsPresent() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        OfferProductArrangementRequest opapcaRequest = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.GUARDIAN.getValue());

        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("NOTDECLINE");

        when(opaPcaService.validatorAndInitializer.isBfpoAddress(any(ArrayList.class))).thenReturn(true);
        when(opaPcaService.eligibilityService.determineEligibility(any(DepositArrangement.class), any(ArrayList.class), any(ArrayList.class), any(RequestHeader.class), anyBoolean())).thenReturn(true);
        OfferProductArrangementResponse opapcaResponse = opaPcaService.offerProductArrangement(opapcaRequest);
        verify(opaPcaService.validatorAndInitializer).initialiseVariables(opapcaRequest, opapcaResponse);
        verify(opaPcaService.verifyInvolvedPartyRoleService).verify(any(DepositArrangement.class), any(RequestHeader.class));

    }

    @Test
    public void testOpapcaServiceWhenBFPOIndicatorIsNotPresent() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        OfferProductArrangementRequest opapcaRequest = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.GUARDIAN.getValue());

        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("NOTDECLINE");

        when(opaPcaService.validatorAndInitializer.isBfpoAddress(any(ArrayList.class))).thenReturn(false);
        when(opaPcaService.eligibilityService.determineEligibility(any(DepositArrangement.class), any(ArrayList.class), any(ArrayList.class), any(RequestHeader.class), anyBoolean())).thenReturn(true);
        OfferProductArrangementResponse opapcaResponse = opaPcaService.offerProductArrangement(opapcaRequest);
        verify(opaPcaService.validatorAndInitializer).initialiseVariables(opapcaRequest, opapcaResponse);
        verify(opaPcaService.involvedPartyIdentifier).identifyInvolvedParty(any(RequestHeader.class), any(Customer.class));
        verify(opaPcaService.verifyInvolvedPartyRoleService).verify(any(DepositArrangement.class), any(RequestHeader.class));

    }

    @Test
    public void testOpapcaServiceWhenDuplicateApplicationIsPresent() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        OfferProductArrangementRequest opapcaRequest = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.GUARDIAN.getValue());

        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("NOTDECLINE");

        when(opaPcaService.validatorAndInitializer.isBfpoAddress(any(ArrayList.class))).thenReturn(true);
        when(opaPcaService.eligibilityService.determineEligibility(any(DepositArrangement.class), any(ArrayList.class), any(ArrayList.class), any(RequestHeader.class), anyBoolean())).thenReturn(true);
        when(opaPcaService.duplicateApplicationCheckService.checkDuplicateApplication(any(ProductArrangement.class), anyString())).thenReturn(true);
        OfferProductArrangementResponse opapcaResponse = opaPcaService.offerProductArrangement(opapcaRequest);
        verify(opaPcaService.validatorAndInitializer).initialiseVariables(opapcaRequest, opapcaResponse);

    }

    @Test
    public void testOpapcaServiceWhenDuplicateApplicationIsNotPresent() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        OfferProductArrangementRequest opapcaRequest = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.GUARDIAN.getValue());

        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("NOTDECLINE");

        when(opaPcaService.validatorAndInitializer.isBfpoAddress(any(ArrayList.class))).thenReturn(true);
        when(opaPcaService.eligibilityService.determineEligibility(any(DepositArrangement.class), any(ArrayList.class), any(ArrayList.class), any(RequestHeader.class), anyBoolean())).thenReturn(true);
        when(opaPcaService.duplicateApplicationCheckService.checkDuplicateApplication(any(ProductArrangement.class), anyString())).thenReturn(false);
        OfferProductArrangementResponse opapcaResponse = opaPcaService.offerProductArrangement(opapcaRequest);
        verify(opaPcaService.validatorAndInitializer).initialiseVariables(opapcaRequest, opapcaResponse);

    }


    @Test
    public void testOpapcaServiceWhenCustomerIsIneligibile() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        OfferProductArrangementRequest opapcaRequest = testDataHelper.generateOfferProductArrangementPCARequest("LTB");

        OfferProductArrangementResponse opapcaResponse = opaPcaService.offerProductArrangement(opapcaRequest);
        verify(opaPcaService.validatorAndInitializer).initialiseVariables(opapcaRequest, opapcaResponse);

    }

    @Test
    public void testOpapcaServiceWhenCustomerIsEligibileAndEidvStatusIsDecline() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        OfferProductArrangementRequest opapcaRequest = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.GUARDIAN.getValue());

        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("DECLINE");

        when(opaPcaService.eligibilityService.determineEligibility(any(DepositArrangement.class), any(ArrayList.class), any(ArrayList.class), any(RequestHeader.class), anyBoolean())).thenReturn(true);
        OfferProductArrangementResponse opapcaResponse = opaPcaService.offerProductArrangement(opapcaRequest);
        verify(opaPcaService.validatorAndInitializer).initialiseVariables(opapcaRequest, opapcaResponse);
        verify(opaPcaService.verifyInvolvedPartyRoleService).verify(any(DepositArrangement.class), any(RequestHeader.class));

    }

    @Test
    public void testOpapcaServiceWhenCustomerIsEligibileAndEidvStatusIsNotDecline() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        OfferProductArrangementRequest opapcaRequest = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.GUARDIAN.getValue());

        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("NOTDECLINE");

        when(opaPcaService.eligibilityService.determineEligibility(any(DepositArrangement.class), any(ArrayList.class), any(ArrayList.class), any(RequestHeader.class), anyBoolean())).thenReturn(true);
        OfferProductArrangementResponse opapcaResponse = opaPcaService.offerProductArrangement(opapcaRequest);
        verify(opaPcaService.validatorAndInitializer).initialiseVariables(opapcaRequest, opapcaResponse);
        verify(opaPcaService.verifyInvolvedPartyRoleService).verify(any(DepositArrangement.class), any(RequestHeader.class));

    }

    @Test
    public void testOpapcaServiceWhenNewCustomerOnOcis() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        OfferProductArrangementRequest opapcaRequest = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.GUARDIAN.getValue());

        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("NOTDECLINE");

        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setNewCustomerIndicator(true);
        when(opaPcaService.validatorAndInitializer.isBfpoAddress(any(ArrayList.class))).thenReturn(true);
        when(opaPcaService.eligibilityService.determineEligibility(any(DepositArrangement.class), any(ArrayList.class), any(ArrayList.class), any(RequestHeader.class), anyBoolean())).thenReturn(true);
        when(opaPcaService.duplicateApplicationCheckService.checkDuplicateApplication(any(ProductArrangement.class), anyString())).thenReturn(false);
        OfferProductArrangementResponse opapcaResponse = opaPcaService.offerProductArrangement(opapcaRequest);
        verify(opaPcaService.validatorAndInitializer).initialiseVariables(opapcaRequest, opapcaResponse);
        verify(opaPcaService.createInvolvedPartyService).createInvolvedParty(anyString(), anyBoolean(), any(Customer.class), any(RequestHeader.class));

    }

    @Test
    public void testOpapcaServiceWhenNotNewCustomerOnOcis() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        OfferProductArrangementRequest opapcaRequest = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.GUARDIAN.getValue());

        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("NOTDECLINE");

        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setNewCustomerIndicator(false);
        when(opaPcaService.validatorAndInitializer.isBfpoAddress(any(ArrayList.class))).thenReturn(true);
        when(opaPcaService.eligibilityService.determineEligibility(any(DepositArrangement.class), any(ArrayList.class), any(ArrayList.class), any(RequestHeader.class), anyBoolean())).thenReturn(true);
        when(opaPcaService.duplicateApplicationCheckService.checkDuplicateApplication(any(ProductArrangement.class), anyString())).thenReturn(false);
        OfferProductArrangementResponse opapcaResponse = opaPcaService.offerProductArrangement(opapcaRequest);
        verify(opaPcaService.validatorAndInitializer).initialiseVariables(opapcaRequest, opapcaResponse);

    }

    @Test
    public void testOpapcaServiceForCreatePamApplyAndUpdatePam() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        OfferProductArrangementRequest opapcaRequest = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.GUARDIAN.getValue());

        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("NOTDECLINE");

        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setNewCustomerIndicator(false);
        when(opaPcaService.validatorAndInitializer.isBfpoAddress(any(ArrayList.class))).thenReturn(true);
        when(opaPcaService.eligibilityService.determineEligibility(any(DepositArrangement.class), any(ArrayList.class), any(ArrayList.class), any(RequestHeader.class), anyBoolean())).thenReturn(true);
        when(opaPcaService.duplicateApplicationCheckService.checkDuplicateApplication(any(ProductArrangement.class), anyString())).thenReturn(false);
        OfferProductArrangementResponse opapcaResponse = opaPcaService.offerProductArrangement(opapcaRequest);
        verify(opaPcaService.validatorAndInitializer).initialiseVariables(opapcaRequest, opapcaResponse);
        verify(opaPcaService.createPamService).createPendingArrangement(any(ProductArrangement.class));
        verify(opaPcaService.applyService).applyCreditRatingScale(any(DepositArrangement.class), any(RequestHeader.class), anyBoolean(), anyBoolean());
        verify(opaPcaService.updatePamService).updatePamDetailsForOffer(any(ProductArrangement.class));


    }

    @Test
    public void testOPAPCAForApplicantTypeDependentAndWithoutDuplicateApplicationHavingCidPersIDAsNull() throws Exception {
        OfferProductArrangementRequest opapcaRequest = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().addAll(testDataHelper.createCustomerScoreList());
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getAuditData().add(0, new AuditData());
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).setAuditType("ADDRESS");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).setAuditDate("17112015");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).setAuditTime("031119");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setCidPersID(null);
        opapcaRequest.getProductArrangement().setApplicationStatus("1002");
        opapcaRequest.getProductArrangement().getOfferedProducts().add(0, new Product());
        opapcaRequest.getProductArrangement().getOfferedProducts().get(0).setProductIdentifier("20198");
        opapcaRequest.getProductArrangement().getAffiliatedetails().addAll(testDataHelper.createAffiliateDetailsList());


        OfferProductArrangementResponse opapcaResponse = opaPcaService.offerProductArrangement(opapcaRequest);

        verify(opaPcaService.validatorAndInitializer).initialiseVariables(opapcaRequest, opapcaResponse);
        verify(opaPcaService.opapcaLogService).initialiseContext(opapcaRequest.getHeader());
        verify(opaPcaService.involvedPartyIdentifier).identifyInvolvedParty(opapcaRequest.getHeader(), opapcaRequest.getProductArrangement().getPrimaryInvolvedParty());

        assertNotNull(opapcaResponse);
        assertEquals(1, opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getAuditData().size());
        assertEquals("ADDRESS", opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).getAuditType());
        assertEquals("17112015", opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).getAuditDate());
        assertEquals("031119", opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).getAuditTime());
        assertEquals(2, opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().size());
        assertEquals(testDataHelper.createCustomerScoreList(), opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore());
        assertEquals("EIDV", opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
        assertEquals("ASM", opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getAssessmentType());
        assertEquals("1002", opapcaResponse.getProductArrangement().getApplicationStatus());
        assertEquals(null, opapcaResponse.getProductArrangement().getArrangementId());
        assertEquals(1, opapcaResponse.getProductArrangement().getOfferedProducts().size());
        assertEquals("20198", opapcaResponse.getProductArrangement().getOfferedProducts().get(0).getProductIdentifier());
        assertEquals(1, opapcaResponse.getProductArrangement().getConditions().size());
        assertEquals("EIDV_REFERRAL_DISABLED_SWITCH", opapcaResponse.getProductArrangement().getConditions().get(0).getName());
        assertEquals("DISABLED", opapcaResponse.getProductArrangement().getConditions().get(0).getResult());
    }

    @Test
    public void testOPAPCAForApplicantTypeGuardianAndWithoutDuplicateApplicationHavingCustomerIdentifierAsNull() throws Exception {
        OfferProductArrangementRequest opapcaRequest = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.GUARDIAN.getValue());
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().addAll(testDataHelper.createCustomerScoreList());
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setCidPersID("+00005070963");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setCustomerIdentifier(null);
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getAuditData().add(0, new AuditData());
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).setAuditType("ADDRESS");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).setAuditDate("17112015");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).setAuditTime("031119");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setCidPersID(null);
        opapcaRequest.getProductArrangement().setApplicationStatus("1002");
        opapcaRequest.getProductArrangement().getOfferedProducts().add(0, new Product());
        opapcaRequest.getProductArrangement().getOfferedProducts().get(0).setProductIdentifier("20198");
        opapcaRequest.getProductArrangement().getAffiliatedetails().addAll(testDataHelper.createAffiliateDetailsList());

        OfferProductArrangementResponse opapcaResponse = opaPcaService.offerProductArrangement(opapcaRequest);

        verify(opaPcaService.opapcaLogService).initialiseContext(opapcaRequest.getHeader());
        verify(opaPcaService.involvedPartyIdentifier).identifyInvolvedParty(opapcaRequest.getHeader(), opapcaRequest.getProductArrangement().getPrimaryInvolvedParty());
        verify(opaPcaService.createPamService, never()).createPendingArrangement(opapcaRequest.getProductArrangement());
        verify(opaPcaService.updatePamService, never()).updatePamDetailsForOffer(opapcaRequest.getProductArrangement());

        assertNotNull(opapcaResponse);
        assertEquals(1, opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getAuditData().size());
        assertEquals("ADDRESS", opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).getAuditType());
        assertEquals("17112015", opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).getAuditDate());
        assertEquals("031119", opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).getAuditTime());
        assertEquals(2, opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().size());
        assertEquals(testDataHelper.createCustomerScoreList(), opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore());
        assertEquals("EIDV", opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
        assertEquals("ASM", opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getAssessmentType());
        assertEquals("1002", opapcaResponse.getProductArrangement().getApplicationStatus());
        assertEquals(null, opapcaResponse.getProductArrangement().getArrangementId());
        assertEquals(1, opapcaResponse.getProductArrangement().getOfferedProducts().size());
        assertEquals("20198", opapcaResponse.getProductArrangement().getOfferedProducts().get(0).getProductIdentifier());
        assertEquals(1, opapcaResponse.getProductArrangement().getConditions().size());
        assertEquals("EIDV_REFERRAL_DISABLED_SWITCH", opapcaResponse.getProductArrangement().getConditions().get(0).getName());
        assertEquals("DISABLED", opapcaResponse.getProductArrangement().getConditions().get(0).getResult());
    }

    @Test
    public void testOPAPCAForApplicantTypeGuardianAndWithoutDuplicateApplicationHavingCheckDuplicateApplicationAsFalse() throws Exception {
        OfferProductArrangementRequest opapcaRequest = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.GUARDIAN.getValue());
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().addAll(testDataHelper.createCustomerScoreList());
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setCidPersID("+00005070963");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setCustomerIdentifier("1062379318");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getAuditData().add(0, new AuditData());
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).setAuditType("ADDRESS");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).setAuditDate("17112015");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).setAuditTime("031119");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setCidPersID(null);
        opapcaRequest.getProductArrangement().setApplicationStatus("1002");
        opapcaRequest.getProductArrangement().getOfferedProducts().add(0, new Product());
        opapcaRequest.getProductArrangement().getOfferedProducts().get(0).setProductIdentifier("20198");
        opapcaRequest.getProductArrangement().getAffiliatedetails().addAll(testDataHelper.createAffiliateDetailsList());

        when(opaPcaService.duplicateApplicationCheckService.checkDuplicateApplication(any(ProductArrangement.class), any(String.class))).thenReturn(false);

        OfferProductArrangementResponse opapcaResponse = opaPcaService.offerProductArrangement(opapcaRequest);

        verify(opaPcaService.opapcaLogService).initialiseContext(opapcaRequest.getHeader());
        verify(opaPcaService.involvedPartyIdentifier).identifyInvolvedParty(opapcaRequest.getHeader(), opapcaRequest.getProductArrangement().getPrimaryInvolvedParty());
        verify(opaPcaService.createPamService, never()).createPendingArrangement(opapcaRequest.getProductArrangement());
        verify(opaPcaService.updatePamService, never()).updatePamDetailsForOffer(opapcaRequest.getProductArrangement());

        assertNotNull(opapcaResponse);
        assertEquals(1, opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getAuditData().size());
        assertEquals("ADDRESS", opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).getAuditType());
        assertEquals("17112015", opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).getAuditDate());
        assertEquals("031119", opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getAuditData().get(0).getAuditTime());
        assertEquals(2, opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().size());
        assertEquals(testDataHelper.createCustomerScoreList(), opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore());
        assertEquals("EIDV", opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
        assertEquals("ASM", opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(1).getAssessmentType());
        assertEquals("1002", opapcaResponse.getProductArrangement().getApplicationStatus());
        assertEquals(null, opapcaResponse.getProductArrangement().getArrangementId());
        assertEquals(1, opapcaResponse.getProductArrangement().getOfferedProducts().size());
        assertEquals("20198", opapcaResponse.getProductArrangement().getOfferedProducts().get(0).getProductIdentifier());
        assertEquals(1, opapcaResponse.getProductArrangement().getConditions().size());
        assertEquals("EIDV_REFERRAL_DISABLED_SWITCH", opapcaResponse.getProductArrangement().getConditions().get(0).getName());
        assertEquals("DISABLED", opapcaResponse.getProductArrangement().getConditions().get(0).getResult());
    }

    @Test
    public void testOPAPCAForApplicantTypeGuardianAndWithDuplicateApplicationHavingCheckDuplicateApplicationAsTrue() throws Exception {
        OfferProductArrangementRequest opapcaRequest = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.GUARDIAN.getValue());
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().addAll(testDataHelper.createCustomerScoreList());
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setCidPersID("+00005070963");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setCustomerIdentifier("1062379318");

        when(opaPcaService.duplicateApplicationCheckService.checkDuplicateApplication(any(ProductArrangement.class), any(String.class))).thenReturn(true);

        OfferProductArrangementResponse opapcaResponse = opaPcaService.offerProductArrangement(opapcaRequest);

        verify(opaPcaService.opapcaLogService).initialiseContext(opapcaRequest.getHeader());
        verify(opaPcaService.involvedPartyIdentifier).identifyInvolvedParty(opapcaRequest.getHeader(), opapcaRequest.getProductArrangement().getPrimaryInvolvedParty());
        verify(opaPcaService.createPamService, never()).createPendingArrangement(opapcaRequest.getProductArrangement());
        verify(opaPcaService.createInvolvedPartyService, never()).createInvolvedParty("CA", false, opapcaRequest.getProductArrangement().getPrimaryInvolvedParty(), opapcaRequest.getHeader());
        verify(opaPcaService.updatePamService, never()).updatePamDetailsForOffer(opapcaRequest.getProductArrangement());

        assertNotNull(opapcaResponse);
        assertEquals(0, opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getAuditData().size());
        assertEquals(0, opapcaResponse.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().size());
        assertEquals(null, opapcaResponse.getProductArrangement().getApplicationStatus());
        assertEquals(null, opapcaResponse.getProductArrangement().getArrangementId());
        assertEquals(0, opapcaResponse.getProductArrangement().getOfferedProducts().size());
        assertEquals(0, opapcaResponse.getProductArrangement().getConditions().size());
        assertEquals(0, opapcaResponse.getProductArrangement().getAffiliatedetails().size());
    }

    @Test
    public void testOpapcaServiceForCrossSellWhenAppStatusNotPresent() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        OfferProductArrangementRequest opapcaRequest = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        opapcaRequest.getProductArrangement().setRelatedApplicationId("12334");
        OfferProductArrangementResponse opapcaResponse = opaPcaService.offerProductArrangement(opapcaRequest);
        assertEquals(null, opapcaResponse.getProductArrangement().getApplicationStatus());

    }

    @Test
    public void testOpapcaServiceForCrossSellWhenAppStatusPresent() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        OfferProductArrangementRequest opapcaRequest = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        opapcaRequest.getProductArrangement().setRelatedApplicationId("12334");
        opapcaRequest.getProductArrangement().setRelatedApplicationStatus("reltd");
        OfferProductArrangementResponse opapcaResponse = opaPcaService.offerProductArrangement(opapcaRequest);
        assertEquals("1014", opapcaResponse.getProductArrangement().getApplicationStatus());

    }

    @Test(expected = InternalServiceErrorMsg.class)
    public void testOpapcaServiceThrowsException() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        OfferProductArrangementRequest opapcaRequest = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.GUARDIAN.getValue());

        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("NOTDECLINE");

        when(opaPcaService.validatorAndInitializer.isBfpoAddress(any(ArrayList.class))).thenThrow(InternalServiceErrorMsg.class);
        when(opaPcaService.eligibilityService.determineEligibility(any(DepositArrangement.class), any(ArrayList.class), any(ArrayList.class), any(RequestHeader.class), anyBoolean())).thenReturn(true);
        OfferProductArrangementResponse opapcaResponse = opaPcaService.offerProductArrangement(opapcaRequest);
        verify(opaPcaService.validatorAndInitializer).initialiseVariables(opapcaRequest, opapcaResponse);

    }

    @Test(expected = OfferException.class)
    public void testOpaPcaServiceThrowsException() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        OfferProductArrangementRequest opapcaRequest = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        when(opaPcaService.validatorAndInitializer.isBfpoAddress(any(ArrayList.class))).thenThrow(OfferException.class);
        doThrow(OfferException.class).when(opaPcaService.exceptionHelper).setResponseHeaderAndThrowException(any(OfferException.class), any(ResponseHeader.class));
        OfferProductArrangementResponse opapcaResponse = opaPcaService.offerProductArrangement(opapcaRequest);
        verify(opaPcaService.validatorAndInitializer).initialiseVariables(opapcaRequest, opapcaResponse);
    }

    @Test
    public void testOpapcaServiceProductHoldingsNonEmpty() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        OfferProductArrangementRequest opapcaRequest = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.GUARDIAN.getValue());

        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        opapcaRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("NOTDECLINE");

        when(opaPcaService.validatorAndInitializer.isBfpoAddress(any(ArrayList.class))).thenReturn(false);
        when(opaPcaService.eligibilityService.determineEligibility(any(DepositArrangement.class), any(ArrayList.class), any(ArrayList.class), any(RequestHeader.class), anyBoolean())).thenReturn(true);
        List<Product> productList = new ArrayList<Product>();
        productList.add(new Product());
        when(opaPcaService.involvedPartyIdentifier.identifyInvolvedParty(any(RequestHeader.class), any(Customer.class))).thenReturn(productList);
        OfferProductArrangementResponse opapcaResponse = opaPcaService.offerProductArrangement(opapcaRequest);
        verify(opaPcaService.validatorAndInitializer).initialiseVariables(opapcaRequest, opapcaResponse);
        verify(opaPcaService.involvedPartyIdentifier).identifyInvolvedParty(any(RequestHeader.class), any(Customer.class));
        verify(opaPcaService.verifyInvolvedPartyRoleService).verify(any(DepositArrangement.class), any(RequestHeader.class));
    }
}
