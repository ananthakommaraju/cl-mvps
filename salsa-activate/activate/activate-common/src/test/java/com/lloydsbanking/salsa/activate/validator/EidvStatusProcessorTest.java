package com.lloydsbanking.salsa.activate.validator;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.communication.convert.NotificationEmailTemplates;
import com.lloydsbanking.salsa.activate.communication.downstream.CommunicationManager;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class EidvStatusProcessorTest {
    EidvStatusProcessor eidvStatusProcessor;
    TestDataHelper testDataHelper;
    ProductArrangement productArrangement;
    RequestHeader requestHeader;

    @Before
    public void setUp() {
        eidvStatusProcessor = new EidvStatusProcessor();
        eidvStatusProcessor.switchClient=mock(SwitchService.class);
        eidvStatusProcessor.communicationManager = mock(CommunicationManager.class);
        eidvStatusProcessor.notificationEmailTemplates = mock(NotificationEmailTemplates.class);
        testDataHelper = new TestDataHelper();
        productArrangement = new ProductArrangement();
        productArrangement.setPrimaryInvolvedParty(new Customer());
        productArrangement.getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        productArrangement.setGuardianDetails(new Customer());
        productArrangement.getGuardianDetails().getCustomerScore().add(new CustomerScore());
        productArrangement.setAssociatedProduct(new Product());
        productArrangement.getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        requestHeader = testDataHelper.createApaRequestHeader();
    }

    @Test
    public void testValidateApplication() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        productArrangement.setApplicationType("1002");
        boolean isEligibleForFulfilment = eidvStatusProcessor.validateApplicationAndGetFulfilmentEligibilityFlag(productArrangement, requestHeader,"1");
        assertFalse(isEligibleForFulfilment);
    }

    @Test
    public void testValidateApplicationForApplicationStatusApproved() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        productArrangement.setApplicationStatus(ApplicationStatus.APPROVED.getValue());
        productArrangement.setRelatedApplicationId("123");
        boolean isEligibleForFulfilment = eidvStatusProcessor.validateApplicationAndGetFulfilmentEligibilityFlag(productArrangement, requestHeader,"1");
        assertTrue(isEligibleForFulfilment);
    }

    @Test
    public void testValidateApplicationForValidateInstructionMnemonic() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        productArrangement.setApplicationStatus(ApplicationStatus.APPROVED.getValue());
        productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult("REFER");
        productArrangement.getGuardianDetails().getCustomerScore().get(0).setScoreResult("ACCEPT");
        productArrangement.getAssociatedProduct().getInstructionDetails().setInstructionMnemonic("P_KRS");
        boolean isEligibleForFulfilment = eidvStatusProcessor.validateApplicationAndGetFulfilmentEligibilityFlag(productArrangement, requestHeader,"1");
        assertFalse(isEligibleForFulfilment);
    }

    @Test
    public void testValidateApplicationAndGetFulfilmentEligibilityFlagForEidvStatusRefer() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        productArrangement.setApplicationStatus(ApplicationStatus.APPROVED.getValue());
        productArrangement.getGuardianDetails().getCustomerScore().get(0).setScoreResult("REFER");
        when(eidvStatusProcessor.notificationEmailTemplates.getNotificationEmailForReferredToBranch("SA")).thenReturn("123");
        boolean isEligibleForFulfilment = eidvStatusProcessor.validateApplicationAndGetFulfilmentEligibilityFlag(productArrangement, requestHeader,"1");
        assertFalse(isEligibleForFulfilment);
        verify(eidvStatusProcessor.communicationManager).callSendCommunicationService(any(ProductArrangement.class), any(String.class), any(RequestHeader.class), any(String.class), any(String.class));
    }

    @Test
    public void testValidateApplicationAndGetFulfilmentEligibilityFlagForEidvStatusAccept() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        productArrangement.setApplicationStatus(ApplicationStatus.APPROVED.getValue());
        productArrangement.getGuardianDetails().getCustomerScore().get(0).setScoreResult("ACCEPT");
        boolean isEligibleForFulfilment = eidvStatusProcessor.validateApplicationAndGetFulfilmentEligibilityFlag(productArrangement, requestHeader,"1");
        assertTrue(isEligibleForFulfilment);
    }
}
