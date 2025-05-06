package com.lloydsbanking.salsa.opacc.service;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.pam.service.CreatePamService;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamService;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import com.lloydsbanking.salsa.offer.ApplicantType;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.apply.downstream.ApplyServiceCC;
import com.lloydsbanking.salsa.offer.identify.IdentifyService;
import com.lloydsbanking.salsa.offer.pam.service.DuplicateApplicationCheckService;
import com.lloydsbanking.salsa.offer.verify.VerifyInvolvedPartyRoleService;
import com.lloydsbanking.salsa.opacc.logging.OpaccLogService;
import com.lloydsbanking.salsa.opacc.service.convert.OfferProductArrangementResponseFactory;
import com.lloydsbanking.salsa.opacc.service.downstream.EligibilityService;
import com.lloydsbanking.salsa.opacc.service.downstream.EncryptDataService;
import com.lloydsbanking.salsa.opacc.service.utility.ExceptionHelper;
import com.lloydsbanking.salsa.opacc.service.validate.RequestValidatorAndInitializer;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class OpaccServiceTest {
    TestDataHelper testDataHelper;

    OpaccService opaccService;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        opaccService = new OpaccService();

        opaccService.responseHeaderConverter = new RequestToResponseHeaderConverter();
        opaccService.applyServiceCC = mock(ApplyServiceCC.class);
        opaccService.opaccLogService = mock(OpaccLogService.class);
        opaccService.encryptDataService = mock(EncryptDataService.class);
        opaccService.identifyService = mock(IdentifyService.class);
        opaccService.createPamService = mock(CreatePamService.class);
        opaccService.updatePamService = mock(UpdatePamService.class);
        opaccService.duplicateApplicationCheckService = mock(DuplicateApplicationCheckService.class);
        opaccService.exceptionHelper = mock(ExceptionHelper.class);
        opaccService.validatorAndInitializer = mock(RequestValidatorAndInitializer.class);
        opaccService.eligibilityService = mock(EligibilityService.class);
        opaccService.verifyInvolvedPartyRoleService = mock(VerifyInvolvedPartyRoleService.class);
        opaccService.offerProductArrangementResponseFactory = new OfferProductArrangementResponseFactory();
        opaccService.productArrangementTraceLog = mock(ProductArrangementTraceLog.class);

        when(opaccService.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class), any(String.class))).thenReturn("Product Arrangement");
    }


    @Test
    public void testOPACCWithoutDuplicateApplicationHavingCidPersIDAsNull() throws Exception {
        OfferProductArrangementRequest opaccRequest = testDataHelper.generateOfferProductArrangementPCCRequest("LTB");
        opaccRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().addAll(testDataHelper.createCustomerScoreList());
        opaccRequest.getProductArrangement().getPrimaryInvolvedParty().setCidPersID(null);
        Customer customer = opaccRequest.getProductArrangement().getPrimaryInvolvedParty();

        OfferProductArrangementResponse opaccResponse = opaccService.offerProductArrangement(opaccRequest);

        verify(opaccService.opaccLogService).initialiseContext(opaccRequest.getHeader());
        verify(opaccService.encryptDataService).retrieveEncryptData(customer.getAccessToken(), opaccRequest.getHeader());
        verify(opaccService.identifyService).identifyInvolvedParty(opaccRequest.getHeader(), customer);
        verify(opaccService.createPamService).createPendingArrangement(opaccRequest.getProductArrangement());
        verify(opaccService.applyServiceCC).applyCreditRatingScaleForCC(null, opaccRequest.getProductArrangement(), opaccRequest.getHeader());
        verify(opaccService.updatePamService).updatePamDetailsForOffer(opaccRequest.getProductArrangement());

        assertNotNull(opaccResponse);
        assertEquals(opaccRequest.getProductArrangement().getPrimaryInvolvedParty().getAuditData(), opaccResponse.getProductArrangement().getPrimaryInvolvedParty().getAuditData());
        assertEquals(2, opaccResponse.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().size());
        assertEquals(testDataHelper.createCustomerScoreList(), opaccResponse.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore());
        assertEquals(opaccRequest.getProductArrangement().getArrangementId(), opaccResponse.getProductArrangement().getArrangementId());
        assertEquals(opaccRequest.getProductArrangement().getPrimaryInvolvedParty().getIndividualIdentifier(), opaccResponse.getProductArrangement().getPrimaryInvolvedParty().getIndividualIdentifier());
        assertEquals(opaccRequest.getProductArrangement().getApplicationStatus(), opaccResponse.getProductArrangement().getApplicationStatus());
        assertNull(opaccResponse.getProductArrangement().getPrimaryInvolvedParty().getAccessToken());

    }


    @Test
    public void testOPACCWithoutDuplicateApplicationHavingDuplicateApplicationCheckAsFalse() throws Exception {
        OfferProductArrangementRequest opaccRequest = testDataHelper.generateOfferProductArrangementPCCRequest("LTB");
        opaccRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().addAll(testDataHelper.createCustomerScoreList());
        opaccRequest.getProductArrangement().getPrimaryInvolvedParty().setCustomerIdentifier(null);
        Customer customer = opaccRequest.getProductArrangement().getPrimaryInvolvedParty();

        verify(opaccService.duplicateApplicationCheckService, never()).checkDuplicateApplication(opaccRequest.getProductArrangement(), "LTB");

        OfferProductArrangementResponse opaccResponse = opaccService.offerProductArrangement(opaccRequest);

        verify(opaccService.opaccLogService).initialiseContext(opaccRequest.getHeader());
        verify(opaccService.encryptDataService).retrieveEncryptData(customer.getAccessToken(), opaccRequest.getHeader());
        verify(opaccService.identifyService).identifyInvolvedParty(opaccRequest.getHeader(), customer);
        verify(opaccService.createPamService).createPendingArrangement(opaccRequest.getProductArrangement());
        verify(opaccService.applyServiceCC).applyCreditRatingScaleForCC(null, opaccRequest.getProductArrangement(), opaccRequest.getHeader());
        verify(opaccService.updatePamService).updatePamDetailsForOffer(opaccRequest.getProductArrangement());

        assertNotNull(opaccResponse);
        assertEquals(opaccRequest.getProductArrangement().getPrimaryInvolvedParty().getAuditData(), opaccResponse.getProductArrangement().getPrimaryInvolvedParty().getAuditData());
        assertEquals(2, opaccResponse.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().size());
        assertEquals(testDataHelper.createCustomerScoreList(), opaccResponse.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore());
        assertEquals(opaccRequest.getProductArrangement().getArrangementId(), opaccResponse.getProductArrangement().getArrangementId());
        assertEquals(opaccRequest.getProductArrangement().getPrimaryInvolvedParty().getIndividualIdentifier(), opaccResponse.getProductArrangement().getPrimaryInvolvedParty().getIndividualIdentifier());
        assertEquals(opaccRequest.getProductArrangement().getApplicationStatus(), opaccResponse.getProductArrangement().getApplicationStatus());
    }

    @Test
    public void testOfferProductArrangementCCWithDuplicateApplication2() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        OfferProductArrangementRequest opaccRequest = testDataHelper.generateOfferProductArrangementPCCRequest("LTB");
        opaccRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().addAll(testDataHelper.createCustomerScoreList());
        opaccRequest.getProductArrangement().getPrimaryInvolvedParty().setCidPersID("1");
        opaccRequest.getProductArrangement().getPrimaryInvolvedParty().setAccessToken(null);
        Customer customer = opaccRequest.getProductArrangement().getPrimaryInvolvedParty();

        verify(opaccService.duplicateApplicationCheckService, never()).checkDuplicateApplication(opaccRequest.getProductArrangement(), "LTB");

        OfferProductArrangementResponse opaccResponse = opaccService.offerProductArrangement(opaccRequest);

        verify(opaccService.opaccLogService).initialiseContext(opaccRequest.getHeader());
        verify(opaccService.encryptDataService).retrieveEncryptData(customer.getAccessToken(), opaccRequest.getHeader());
        verify(opaccService.identifyService).identifyInvolvedParty(opaccRequest.getHeader(), customer);
        verify(opaccService.createPamService).createPendingArrangement(opaccRequest.getProductArrangement());
        verify(opaccService.applyServiceCC).applyCreditRatingScaleForCC(null, opaccRequest.getProductArrangement(), opaccRequest.getHeader());

        assertNotNull(opaccResponse);
        assertEquals(0, opaccResponse.getProductArrangement().getPrimaryInvolvedParty().getAuditData().size());
        assertEquals(2, opaccResponse.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().size());
        assertNull(opaccResponse.getProductArrangement().getArrangementId());
        assertNull(opaccResponse.getProductArrangement().getPrimaryInvolvedParty().getIndividualIdentifier());
        assertNull(opaccResponse.getProductArrangement().getApplicationStatus());
        assertNull(opaccResponse.getProductArrangement().getPrimaryInvolvedParty().getAccessToken());
        assertEquals(0, opaccResponse.getProductArrangement().getAffiliatedetails().size());
    }

    @Test(expected = OfferException.class)
    public void testOpaccServiceThrowsException() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        OfferProductArrangementRequest opaccRequest = testDataHelper.generateOfferProductArrangementPCCRequest("LTB");
        opaccRequest.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.GUARDIAN.getValue());
        opaccRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        opaccRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("NOTDECLINE");
        when(opaccService.validatorAndInitializer.isBfpoAddress(any(ArrayList.class))).thenThrow(OfferException.class);
        doThrow(OfferException.class).when(opaccService.exceptionHelper).setResponseHeaderAndThrowException(any(OfferException.class), any(ResponseHeader.class));
        OfferProductArrangementResponse opapcaResponse = opaccService.offerProductArrangement(opaccRequest);
        verify(opaccService.validatorAndInitializer).initialiseVariables(opaccRequest, opapcaResponse);
    }

    @Test
    public void testOpapcaServiceProductHoldingsNonEmpty() throws OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferException {
        OfferProductArrangementRequest opaccRequest = testDataHelper.generateOfferProductArrangementPCCRequest("LTB");
        opaccRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        opaccRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("NOTDECLINE");
        opaccRequest.getProductArrangement().getPrimaryInvolvedParty().setApplicantType(ApplicantType.GUARDIAN.getValue());
        when(opaccService.validatorAndInitializer.isBfpoAddress(any(ArrayList.class))).thenReturn(false);
        List<Product> productList = new ArrayList<Product>();
        productList.add(new Product());
        when(opaccService.identifyService.identifyInvolvedParty(any(RequestHeader.class), any(Customer.class))).thenReturn(productList);
        opaccService.offerProductArrangement(opaccRequest);
    }

}
