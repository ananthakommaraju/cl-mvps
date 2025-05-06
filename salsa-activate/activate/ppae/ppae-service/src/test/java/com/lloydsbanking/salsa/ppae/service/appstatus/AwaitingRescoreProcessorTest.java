package com.lloydsbanking.salsa.ppae.service.appstatus;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.downstream.PrdClient;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.logging.application.ProductTraceLog;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.apply.ApplyService;
import com.lloydsbanking.salsa.offer.apply.downstream.ApplyServiceCC;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import com.lloydsbanking.salsa.ppae.service.convert.PrdRequestFactory;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class AwaitingRescoreProcessorTest {

    AwaitingRescoreProcessor awaitingRescoreProcessor;
    TestDataHelper testDataHelper;
    ProductArrangement productArrangement;
    RequestHeader requestHeader;
    ProcessPendingArrangementEventRequest upStreamRequest;
    RetrieveProductConditionsResponse retrieveProductConditionsResponse;
    String productOfferIdentifier = null;
    String productIdentifier = null;


    @Before
    public void setUp() throws DatatypeConfigurationException {
        awaitingRescoreProcessor = new AwaitingRescoreProcessor();
        testDataHelper = new TestDataHelper();
        productArrangement = testDataHelper.createProductArrangement();
        retrieveProductConditionsResponse = testDataHelper.createRetrieveProductConditionsResponse();
        awaitingRescoreProcessor.prdClient = mock(PrdClient.class);
        awaitingRescoreProcessor.prdRequestFactory = mock(PrdRequestFactory.class);
        awaitingRescoreProcessor.creditRatingScoreDecisionEvaluator = mock(CreditRatingScoreDecisionEvaluator.class);
        awaitingRescoreProcessor.applyServiceCC = mock(ApplyServiceCC.class);
        awaitingRescoreProcessor.applyService = mock(ApplyService.class);
        awaitingRescoreProcessor.productTraceLog = mock(ProductTraceLog.class);
        when(awaitingRescoreProcessor.productTraceLog.getProdListTraceEventMessage(any(List.class), any(String.class))).thenReturn("Logging");
        requestHeader = new RequestHeader();
        upStreamRequest = new ProcessPendingArrangementEventRequest();
        productOfferIdentifier = productArrangement.getAssociatedProduct().getProductoffer().get(0).getProdOfferIdentifier();
        productIdentifier = productArrangement.getAssociatedProduct().getProductIdentifier();
    }

    @Test
    public void testRetrieveProductDetails() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        when(awaitingRescoreProcessor.prdClient.retrieveProductConditions(awaitingRescoreProcessor.prdRequestFactory.convert(productArrangement, upStreamRequest.getHeader()))).thenReturn(retrieveProductConditionsResponse);
        awaitingRescoreProcessor.retrieveProductDetailsAndCreditRating(productArrangement, upStreamRequest.getHeader(), new PpaeInvocationIdentifier());
        assertEquals("3", productArrangement.getPrimaryInvolvedParty().getSourceSystemId());
        assertNull(productArrangement.getPrimaryInvolvedParty().getPartyIdentifier());
    }

    @Test
    public void testRetrieveProductDetailsForFirstCatch() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        productArrangement.setArrangementType("SA");
        when(awaitingRescoreProcessor.prdClient.retrieveProductConditions(awaitingRescoreProcessor.prdRequestFactory.convert(productArrangement, upStreamRequest.getHeader()))).thenThrow(RetrieveProductConditionsInternalServiceErrorMsg.class);
        awaitingRescoreProcessor.retrieveProductDetailsAndCreditRating(productArrangement, upStreamRequest.getHeader(), new PpaeInvocationIdentifier());
    }

    @Test
    public void testRetrieveProductDetailsForSecondCatch() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        productArrangement.setArrangementType("SA");
        awaitingRescoreProcessor.retrieveProductDetailsAndCreditRating(productArrangement, upStreamRequest.getHeader(), new PpaeInvocationIdentifier(true, true));
        verify(awaitingRescoreProcessor.creditRatingScoreDecisionEvaluator).applyCreditRatingScore(any(ProductArrangement.class), any(RequestHeader.class), any(String.class), any(String.class), any(PpaeInvocationIdentifier.class));
    }

    @Test
    public void testRetrieveProductConditionsWhenArrangementTypeIsSetAsCA() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        productArrangement.setArrangementType(ArrangementType.CURRENT_ACCOUNT.getValue());
        when(awaitingRescoreProcessor.prdClient.retrieveProductConditions(awaitingRescoreProcessor.prdRequestFactory.convert(productArrangement, upStreamRequest.getHeader()))).thenReturn(retrieveProductConditionsResponse);
        awaitingRescoreProcessor.retrieveProductDetailsAndCreditRating(productArrangement, upStreamRequest.getHeader(), new PpaeInvocationIdentifier());
        assertEquals("3", productArrangement.getPrimaryInvolvedParty().getSourceSystemId());
        assertNull(productArrangement.getPrimaryInvolvedParty().getPartyIdentifier());
    }

    @Test
    public void testApplyCreditRatingIsCalledForCC() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, OfferException {
        FinanceServiceArrangement productArrangement = testDataHelper.createFinanceArrangementForCC();
        productArrangement.setArrangementType(ArrangementType.CREDITCARD.getValue());
        CustomerScore customerScore = new CustomerScore();
        CustomerScore customerScore1 = new CustomerScore();
        productArrangement.getPrimaryInvolvedParty().getCustomerScore().add(customerScore);
        productArrangement.getPrimaryInvolvedParty().getCustomerScore().add(customerScore1);
        when(awaitingRescoreProcessor.prdClient.retrieveProductConditions(awaitingRescoreProcessor.prdRequestFactory.convert(productArrangement, upStreamRequest.getHeader()))).thenReturn(retrieveProductConditionsResponse);
        awaitingRescoreProcessor.retrieveProductDetailsAndCreditRating(productArrangement, upStreamRequest.getHeader(), new PpaeInvocationIdentifier());
        verify(awaitingRescoreProcessor.applyServiceCC).applyCreditRatingScaleForCC(null, productArrangement, upStreamRequest.getHeader());


    }

    @Test
    public void testApplyCreditRatingIsCalledForCA() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, OfferException {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangementForSA();
        depositArrangement.setArrangementType(ArrangementType.CURRENT_ACCOUNT.getValue());
        CustomerScore customerScore = new CustomerScore();
        CustomerScore customerScore1 = new CustomerScore();
        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().add(customerScore);
        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().add(customerScore1);
        when(awaitingRescoreProcessor.prdClient.retrieveProductConditions(awaitingRescoreProcessor.prdRequestFactory.convert(productArrangement, upStreamRequest.getHeader()))).thenReturn(retrieveProductConditionsResponse);
        awaitingRescoreProcessor.retrieveProductDetailsAndCreditRating(depositArrangement, upStreamRequest.getHeader(), new PpaeInvocationIdentifier());
        verify(awaitingRescoreProcessor.applyService).applyCreditRatingScale(depositArrangement, upStreamRequest.getHeader(), false, true);


    }

    @Test
    public void testApplyCreditRatingIsCalledForSA() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, OfferException {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangementForSA();
        depositArrangement.setArrangementType(ArrangementType.SAVINGS.getValue());
        CustomerScore customerScore = new CustomerScore();
        CustomerScore customerScore1 = new CustomerScore();
        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().add(customerScore);
        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().add(customerScore1);
        when(awaitingRescoreProcessor.prdClient.retrieveProductConditions(awaitingRescoreProcessor.prdRequestFactory.convert(productArrangement, upStreamRequest.getHeader()))).thenReturn(retrieveProductConditionsResponse);
        awaitingRescoreProcessor.retrieveProductDetailsAndCreditRating(depositArrangement, upStreamRequest.getHeader(), new PpaeInvocationIdentifier());
        verify(awaitingRescoreProcessor.applyService).applyCreditRatingScale(depositArrangement, upStreamRequest.getHeader(), false, true);


    }

    @Test
    public void testApplyCreditRatingCCThrowsError() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, OfferException {
        FinanceServiceArrangement productArrangement = testDataHelper.createFinanceArrangementForCC();
        productArrangement.setArrangementType(ArrangementType.CREDITCARD.getValue());
        CustomerScore customerScore = new CustomerScore();
        CustomerScore customerScore1 = new CustomerScore();
        productArrangement.getPrimaryInvolvedParty().getCustomerScore().add(customerScore);
        productArrangement.getPrimaryInvolvedParty().getCustomerScore().add(customerScore1);
        when(awaitingRescoreProcessor.prdClient.retrieveProductConditions(awaitingRescoreProcessor.prdRequestFactory.convert(productArrangement, upStreamRequest.getHeader()))).thenReturn(retrieveProductConditionsResponse);
        doThrow(OfferException.class).when(awaitingRescoreProcessor.applyServiceCC).applyCreditRatingScaleForCC(null, productArrangement, upStreamRequest.getHeader());
        awaitingRescoreProcessor.retrieveProductDetailsAndCreditRating(productArrangement, upStreamRequest.getHeader(), new PpaeInvocationIdentifier());

    }

    @Test
    public void testErrorCallingApplyCreditRatingIsCalledForCA() throws RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg, OfferException {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangementForSA();
        depositArrangement.setArrangementType(ArrangementType.CURRENT_ACCOUNT.getValue());
        CustomerScore customerScore = new CustomerScore();
        CustomerScore customerScore1 = new CustomerScore();
        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().add(customerScore);
        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().add(customerScore1);
        when(awaitingRescoreProcessor.prdClient.retrieveProductConditions(awaitingRescoreProcessor.prdRequestFactory.convert(productArrangement, upStreamRequest.getHeader()))).thenReturn(retrieveProductConditionsResponse);
        doThrow(OfferException.class).when(awaitingRescoreProcessor.applyService).applyCreditRatingScale(depositArrangement, upStreamRequest.getHeader(), false, true);
        awaitingRescoreProcessor.retrieveProductDetailsAndCreditRating(depositArrangement, upStreamRequest.getHeader(), new PpaeInvocationIdentifier());


    }


}
