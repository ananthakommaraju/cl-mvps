package com.lloydsbanking.salsa.ppae.service.appstatus;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.administer.AdministerReferredLookUpData;
import com.lloydsbanking.salsa.activate.communication.convert.NotificationEmailTemplates;
import com.lloydsbanking.salsa.activate.communication.downstream.CommunicationManager;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_bo.businessobjects.ReferralCode;
import lib_sim_communicationmanager.messages.SendCommunicationResponse;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class CreditRatingScoreDecisionEvaluatorTest {

    private static final String ASM_DECLINE_CODE = "ASM_DECLINE_CODE";

    CreditRatingScoreDecisionEvaluator creditRatingScoreDecisionEvaluator;
    ProductArrangement productArrangement;
    TestDataHelper testDataHelper;
    ProcessPendingArrangementEventRequest request;

    String productOfferIdentifier = null;
    String productIdentifier = null;


    @Before
    public void setUp() throws DatatypeConfigurationException {

        creditRatingScoreDecisionEvaluator = new CreditRatingScoreDecisionEvaluator();
        testDataHelper = new TestDataHelper();
        productArrangement = testDataHelper.createProductArrangement();
        request = testDataHelper.createPpaeRequest("1", "LTB");
        creditRatingScoreDecisionEvaluator.processAsmAcceptDecision = mock(ProcessAsmAcceptDecision.class);
        creditRatingScoreDecisionEvaluator.lookUpValueRetriever = mock(LookUpValueRetriever.class);
        creditRatingScoreDecisionEvaluator.communicationManager = mock(CommunicationManager.class);
        creditRatingScoreDecisionEvaluator.notificationEmailTemplates = mock(NotificationEmailTemplates.class);
        productOfferIdentifier = productArrangement.getAssociatedProduct().getProductoffer().get(0).getProdOfferIdentifier();
        productIdentifier = productArrangement.getAssociatedProduct().getProductIdentifier();
        creditRatingScoreDecisionEvaluator.administerReferredLookUpData = mock(AdministerReferredLookUpData.class);
    }

    @Test
    public void testApplyCreditRatingScoreForReferAnd501() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        creditRatingScoreDecisionEvaluator.applyCreditRatingScore(productArrangement, request.getHeader(), productOfferIdentifier, productIdentifier, new PpaeInvocationIdentifier());
        assertEquals(ApplicationStatus.AWAITING_RESCORE.getValue(), productArrangement.getApplicationStatus());

    }

    @Test
    public void testApplyCreditRatingScoreForOther() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        List<String> groupCodeList = new ArrayList<>();
        groupCodeList.add(ASM_DECLINE_CODE);
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        ReferenceDataLookUp referenceDataLookUp = new ReferenceDataLookUp();
        referenceDataLookUp.setDescription("ASM Decline Code");
        referenceDataLookUp.setLookupText("309");
        referenceDataLookUpList.add(referenceDataLookUp);
        productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(1).setScoreResult("0");
        creditRatingScoreDecisionEvaluator.applyCreditRatingScore(productArrangement, request.getHeader(), productOfferIdentifier, productIdentifier, new PpaeInvocationIdentifier());
        verify(creditRatingScoreDecisionEvaluator.administerReferredLookUpData).getDeclineSource(productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(1).getReferralCode().get(0).getCode(), request.getHeader().getChannelId());
    }

    @Test
    public void testApplyCreditRatingScoreForASMRefer() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("2");
        ReferralCode referralCode = new ReferralCode();
        referralCode.setCode("501");
        productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().add(referralCode);
        creditRatingScoreDecisionEvaluator.applyCreditRatingScore(productArrangement, request.getHeader(), productOfferIdentifier, productIdentifier, new PpaeInvocationIdentifier());
        verify(creditRatingScoreDecisionEvaluator.processAsmAcceptDecision, never()).checkProductAndStatus(productArrangement, request.getHeader(), productOfferIdentifier, productIdentifier, new PpaeInvocationIdentifier());
    }

    @Test
    public void testApplyCreditRatingScoreWhenCustomerScoreIsNull() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        List<String> groupCodeList = new ArrayList<>();
        groupCodeList.add(ASM_DECLINE_CODE);
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        ReferenceDataLookUp referenceDataLookUp = new ReferenceDataLookUp();
        referenceDataLookUp.setGroupCode(ASM_DECLINE_CODE);
        referenceDataLookUp.setDescription("ASM Decline Code");
        referenceDataLookUp.setLookupText("309");
        referenceDataLookUp.setLookupValueDesc("Decline");
        referenceDataLookUpList.add(referenceDataLookUp);
        productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult(null);
        when(creditRatingScoreDecisionEvaluator.lookUpValueRetriever.retrieveLookUpValues(request.getHeader(), groupCodeList)).thenReturn(referenceDataLookUpList);
        when(creditRatingScoreDecisionEvaluator.notificationEmailTemplates.getNotificationEmailForDeclined(any(String.class), any(String.class))).thenReturn(new String());
        when(creditRatingScoreDecisionEvaluator.communicationManager.callSendCommunicationService(any(ProductArrangement.class), any(String.class), any(RequestHeader.class), any(String.class), any(String.class))).thenReturn(new SendCommunicationResponse());
        creditRatingScoreDecisionEvaluator.applyCreditRatingScore(productArrangement, request.getHeader(), productOfferIdentifier, productIdentifier, new PpaeInvocationIdentifier());
    }

}
