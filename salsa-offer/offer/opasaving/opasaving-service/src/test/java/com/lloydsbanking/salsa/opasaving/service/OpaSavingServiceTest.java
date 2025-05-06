package com.lloydsbanking.salsa.opasaving.service;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.pam.service.CreatePamService;
import com.lloydsbanking.salsa.downstream.pam.service.CreateParentArrangementService;
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
import com.lloydsbanking.salsa.opasaving.logging.OpaSavingLogService;
import com.lloydsbanking.salsa.opasaving.service.convert.OfferProductArrangementResponseFactory;
import com.lloydsbanking.salsa.opasaving.service.downstream.RPCService;
import com.lloydsbanking.salsa.opasaving.service.utility.ExceptionHelper;
import com.lloydsbanking.salsa.opasaving.service.validate.RequestValidatorAndInitializer;
import lib_sim_bo.businessobjects.*;
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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class OpaSavingServiceTest {

    private OpaSavingService opasavingService;

    private OfferProductArrangementRequest opasavingRequest;

    private OfferProductArrangementResponse opasavingResponse;

    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        opasavingService = new OpaSavingService();
        testDataHelper = new TestDataHelper();

        opasavingService.responseHeaderConverter = new RequestToResponseHeaderConverter();
        opasavingService.applyService = mock(ApplyService.class);
        opasavingService.opasavingLogService = mock(OpaSavingLogService.class);
        opasavingService.createPamService = mock(CreatePamService.class);
        opasavingService.updatePamService = mock(UpdatePamService.class);
        opasavingService.createInvolvedPartyService = mock(CreateInvolvedPartyService.class);
        opasavingService.identifyService = mock(IdentifyService.class);
        opasavingService.validatorAndInitializer = mock(RequestValidatorAndInitializer.class);
        opasavingService.parentArrangementService = mock(CreateParentArrangementService.class);
        opasavingService.duplicateApplicationCheckService = mock(DuplicateApplicationCheckService.class);
        opasavingService.eligibilityService = mock(EligibilityService.class);
        opasavingService.exceptionHelper = mock(ExceptionHelper.class);
        opasavingService.offerProductArrangementResponseFactory = mock(OfferProductArrangementResponseFactory.class);
        opasavingService.productArrangementTraceLog = mock(ProductArrangementTraceLog.class);
        opasavingService.rpcService = mock(RPCService.class);
        opasavingService.verifyInvolvedPartyRoleService = mock(VerifyInvolvedPartyRoleService.class);
        opasavingRequest = testDataHelper.generateOfferProductArrangementSavingRequest("LTB");
        when(opasavingService.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class), any(String.class))).thenReturn("ProductArrangement");
    }

    @Test
    public void testOfferProductArrangementForChildCustomer() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg {
        opasavingRequest.getProductArrangement().getPrimaryInvolvedParty().setApplicantType("1");
        opasavingResponse = opasavingService.offerProductArrangement(opasavingRequest);
        verify(opasavingService.opasavingLogService).initialiseContext(opasavingRequest.getHeader());

    }

    @Test
    public void testOfferProductArrangement() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        opasavingRequest.getProductArrangement().getPrimaryInvolvedParty().setApplicantType("1");
        when(opasavingService.duplicateApplicationCheckService.checkDuplicateApplication(any(DepositArrangement.class), any(String.class))).thenReturn(false);
        when(opasavingService.eligibilityService.determineEligibility(any(ProductArrangement.class), any(List.class), any(List.class), any(RequestHeader.class), any(Boolean.class))).thenReturn(true);
        opasavingRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        opasavingRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("2");
        opasavingResponse = opasavingService.offerProductArrangement(opasavingRequest);
        verify(opasavingService.opasavingLogService).initialiseContext(opasavingRequest.getHeader());
    }

    @Test
    public void testOfferProductArrangementRelatedApplicationId() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg {
        opasavingRequest.getProductArrangement().setRelatedApplicationId("123");
        opasavingResponse = opasavingService.offerProductArrangement(opasavingRequest);
    }

    @Test
    public void testOfferProductArrangementBFPOIndicatorTrue() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg {
        when(opasavingService.validatorAndInitializer.isBfpoAddress(any(ArrayList.class))).thenReturn(true);
        opasavingResponse = opasavingService.offerProductArrangement(opasavingRequest);
    }

    @Test
    public void testOfferProductArrangementCheckDuplicateApplicationTrue() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg {
        when(opasavingService.duplicateApplicationCheckService.checkDuplicateApplication(any(DepositArrangement.class), any(String.class))).thenReturn(true);
        opasavingResponse = opasavingService.offerProductArrangement(opasavingRequest);
    }

    @Test
    public void testOfferProductArrangementEidvStatusDecline() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        when(opasavingService.duplicateApplicationCheckService.checkDuplicateApplication(any(DepositArrangement.class), any(String.class))).thenReturn(false);
        when(opasavingService.eligibilityService.determineEligibility(any(ProductArrangement.class), any(List.class), any(List.class), any(RequestHeader.class), any(Boolean.class))).thenReturn(true);
        opasavingRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        opasavingRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("DECLINE");
        opasavingResponse = opasavingService.offerProductArrangement(opasavingRequest);
    }

    @Test
    public void testOfferProductArrangemenIneligibleCustomer() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        when(opasavingService.duplicateApplicationCheckService.checkDuplicateApplication(any(DepositArrangement.class), any(String.class))).thenReturn(false);
        when(opasavingService.eligibilityService.determineEligibility(any(ProductArrangement.class), any(List.class), any(List.class), any(RequestHeader.class), any(Boolean.class))).thenReturn(false);
        opasavingResponse = opasavingService.offerProductArrangement(opasavingRequest);
    }

    @Test
    public void testOfferProductArrangementNewCustomer() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        when(opasavingService.duplicateApplicationCheckService.checkDuplicateApplication(any(DepositArrangement.class), any(String.class))).thenReturn(false);
        when(opasavingService.eligibilityService.determineEligibility(any(ProductArrangement.class), any(List.class), any(List.class), any(RequestHeader.class), any(Boolean.class))).thenReturn(true);
        opasavingRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        opasavingRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("ACCEPT");
        opasavingRequest.getProductArrangement().getPrimaryInvolvedParty().setNewCustomerIndicator(true);
        opasavingResponse = opasavingService.offerProductArrangement(opasavingRequest);
    }

    @Test
    public void testOfferProductArrangementGuardian() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        opasavingRequest.getProductArrangement().getPrimaryInvolvedParty().setApplicantType("02");
        when(opasavingService.duplicateApplicationCheckService.checkDuplicateApplication(any(DepositArrangement.class), any(String.class))).thenReturn(false);
        when(opasavingService.eligibilityService.determineEligibility(any(ProductArrangement.class), any(List.class), any(List.class), any(RequestHeader.class), any(Boolean.class))).thenReturn(true);
        opasavingRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        opasavingRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("ACCEPT");
        opasavingRequest.getProductArrangement().getPrimaryInvolvedParty().setNewCustomerIndicator(true);
        opasavingResponse = opasavingService.offerProductArrangement(opasavingRequest);
    }

    @Test
    public void testOfferProductArrangementNotGuardian() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        opasavingRequest.getProductArrangement().getPrimaryInvolvedParty().setApplicantType("01");
        when(opasavingService.duplicateApplicationCheckService.checkDuplicateApplication(any(DepositArrangement.class), any(String.class))).thenReturn(false);
        when(opasavingService.eligibilityService.determineEligibility(any(ProductArrangement.class), any(List.class), any(List.class), any(RequestHeader.class), any(Boolean.class))).thenReturn(true);
        opasavingRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        opasavingRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("ACCEPT");
        opasavingRequest.getProductArrangement().getPrimaryInvolvedParty().setNewCustomerIndicator(true);
        opasavingResponse = opasavingService.offerProductArrangement(opasavingRequest);
    }


    @Test(expected = OfferException.class)
    public void testOpaSavingServiceThrowsException() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        opasavingRequest.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.GUARDIAN.getValue());
        opasavingRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        opasavingRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("NOTDECLINE");
        when(opasavingService.validatorAndInitializer.isBfpoAddress(any(ArrayList.class))).thenThrow(OfferException.class);
        when(opasavingService.eligibilityService.determineEligibility(any(DepositArrangement.class), any(ArrayList.class), any(ArrayList.class), any(RequestHeader.class), anyBoolean())).thenReturn(true);
        doThrow(OfferException.class).when(opasavingService.exceptionHelper).setResponseHeaderAndThrowException(any(OfferException.class), any(ResponseHeader.class));
        OfferProductArrangementResponse opapcaResponse = opasavingService.offerProductArrangement(opasavingRequest);
        verify(opasavingService.validatorAndInitializer).initialiseVariables(opasavingRequest, opapcaResponse);
    }

    @Test
    public void testOpapcaServiceProductHoldingsNonEmpty() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        opasavingRequest.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.GUARDIAN.getValue());
        opasavingRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        opasavingRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("NOTDECLINE");
        List<Product> productList = new ArrayList<Product>();
        productList.add(new Product());
        when(opasavingService.identifyService.identifyInvolvedParty(any(RequestHeader.class), any(Customer.class))).thenReturn(productList);
        opasavingService.offerProductArrangement(opasavingRequest);
    }

}
