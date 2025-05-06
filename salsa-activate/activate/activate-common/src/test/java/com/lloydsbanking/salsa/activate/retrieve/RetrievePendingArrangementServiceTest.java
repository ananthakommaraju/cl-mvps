package com.lloydsbanking.salsa.activate.retrieve;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.downstream.pam.service.RetrievePamService;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementInternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;

import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class RetrievePendingArrangementServiceTest {
    TestDataHelper testDataHelper;

    RetrievePendingArrangementService retrievePendingArrangementService;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        retrievePendingArrangementService = new RetrievePendingArrangementService();
        retrievePendingArrangementService.retrievePamService = mock(RetrievePamService.class);
        retrievePendingArrangementService.lookUpValueRetriever = mock(LookUpValueRetriever.class);
        retrievePendingArrangementService.serviceHelper=new RetrievePendingArrangementServiceHelper();
    }

    @Test
    public void testRetrievePendingArrangements() throws Exception {
        ActivateProductArrangementRequest upStreamRequest = testDataHelper.createApaRequestForPca();
        when(retrievePendingArrangementService.retrievePamService.retrievePendingArrangement("IBL", upStreamRequest.getProductArrangement().getArrangementId(), upStreamRequest.getProductArrangement().getReferral())).thenReturn(testDataHelper.createDepositArrangementForB750());
        retrievePendingArrangementService.retrievePendingArrangements(upStreamRequest);
        assertNotNull(upStreamRequest.getProductArrangement().getPrimaryInvolvedParty());
        assertFalse(upStreamRequest.getProductArrangement().getPrimaryInvolvedParty().isIsRegistrationSelected());
        assertEquals("1002", upStreamRequest.getProductArrangement().getApplicationStatus());
    }

    @Test
    public void testRetrievePendingArrangementsWithSourceSystemIdentifierAsDB() throws Exception {
        ActivateProductArrangementRequest upStreamRequest = testDataHelper.createApaRequestForPca();
        upStreamRequest.setSourceSystemIdentifier("2");
        retrievePendingArrangementService.retrievePendingArrangements(upStreamRequest);
        verify(retrievePendingArrangementService.retrievePamService, times(0)).retrievePendingArrangement("IBL", upStreamRequest.getProductArrangement().getArrangementId(), upStreamRequest.getProductArrangement().getReferral());
    }

    @Test
    public void testRetrievePendingArrangementsWithSourceSystemIdentifierAsOnline() throws Exception {
        ActivateProductArrangementRequest upStreamRequest = testDataHelper.createApaRequestForPca();
        upStreamRequest.setSourceSystemIdentifier("1");
        DepositArrangement depositArrangement = (DepositArrangement) upStreamRequest.getProductArrangement();
        depositArrangement.setIsOverdraftRequired(true);
        when(retrievePendingArrangementService.retrievePamService.retrievePendingArrangement("IBL", upStreamRequest.getProductArrangement().getArrangementId(), upStreamRequest.getProductArrangement().getReferral())).thenReturn(testDataHelper.createDepositArrangementForB750());
        retrievePendingArrangementService.retrievePendingArrangements(upStreamRequest);
        assertNotNull(upStreamRequest.getProductArrangement().getPrimaryInvolvedParty());
        assertFalse(upStreamRequest.getProductArrangement().getPrimaryInvolvedParty().isIsRegistrationSelected());
        assertEquals("1002", upStreamRequest.getProductArrangement().getApplicationStatus());
    }

    @Test
    public void testRetrievePendingArrangementsWithSourceSystemIdentifierAsOffline() throws Exception {
        ActivateProductArrangementRequest upStreamRequest = testDataHelper.createApaRequestForPca();
        upStreamRequest.setSourceSystemIdentifier("4");
        DepositArrangement depositArrangement = (DepositArrangement) upStreamRequest.getProductArrangement();
        depositArrangement.setIsOverdraftRequired(true);
        when(retrievePendingArrangementService.retrievePamService.retrievePendingArrangement("IBL", upStreamRequest.getProductArrangement().getArrangementId(), upStreamRequest.getProductArrangement().getReferral())).thenReturn(testDataHelper.createDepositArrangementForB750());
        retrievePendingArrangementService.retrievePendingArrangements(upStreamRequest);
        assertNotNull(upStreamRequest.getProductArrangement().getPrimaryInvolvedParty());
        assertFalse(upStreamRequest.getProductArrangement().getPrimaryInvolvedParty().isIsRegistrationSelected());
        assertEquals("1002", upStreamRequest.getProductArrangement().getApplicationStatus());
    }

    @Test
    public void testRetrievePendingArrangementWithSourceSystemIdentifierAsOAP() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, InternalServiceErrorMsg {

        ActivateProductArrangementRequest upStreamRequest = testDataHelper.createApaRequestForPca();
        upStreamRequest.getProductArrangement().setGuardianDetails(new Customer());
        upStreamRequest.getProductArrangement().getGuardianDetails().setCustomerIdentifier("0054");
        upStreamRequest.getProductArrangement().getPrimaryInvolvedParty().setUserType("user");
        upStreamRequest.getProductArrangement().getPrimaryInvolvedParty().setInternalUserIdentifier("10010");
        ProductArrangement productArrangement = testDataHelper.createDepositArrangementForB750();
        upStreamRequest.setSourceSystemIdentifier("3");
        CustomerScore customerScore = new CustomerScore();
        customerScore.getAssessmentEvidence().add(new AssessmentEvidence());
        upStreamRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(customerScore);
        upStreamRequest.getProductArrangement().getGuardianDetails().getCustomerScore().add(customerScore);
        productArrangement.setGuardianDetails(new Customer());
        productArrangement.getGuardianDetails().getCustomerScore().add(customerScore);
        productArrangement.getGuardianDetails().setCustomerIdentifier("0054");
        when(retrievePendingArrangementService.retrievePamService.retrievePendingArrangement("IBL", upStreamRequest.getProductArrangement().getArrangementId(), upStreamRequest.getProductArrangement().getReferral())).thenReturn(productArrangement);
        retrievePendingArrangementService.retrievePendingArrangements(upStreamRequest);
        assertEquals("1002", upStreamRequest.getProductArrangement().getApplicationStatus());
    }

    @Test
    public void testRetrievePendingArrangementForSavingAccountArrangementType() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        ActivateProductArrangementRequest upStreamRequest = testDataHelper.createApaRequestForPca();
        upStreamRequest.setSourceSystemIdentifier("4");
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangementForB750();
        depositArrangement.setArrangementType("SA");
        depositArrangement.setIsOverdraftRequired(true);
        when(retrievePendingArrangementService.retrievePamService.retrievePendingArrangement("IBL", upStreamRequest.getProductArrangement().getArrangementId(), upStreamRequest.getProductArrangement().getReferral())).thenReturn(depositArrangement);
        retrievePendingArrangementService.retrievePendingArrangements(upStreamRequest);
        assertEquals("1002", upStreamRequest.getProductArrangement().getApplicationStatus());
    }

    @Test
    public void testRetrievePendingArrangementForArrangementType() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        ActivateProductArrangementRequest upStreamRequest = testDataHelper.createApaRequestForPca();
        upStreamRequest.setSourceSystemIdentifier("4");
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangementForB750();
        depositArrangement.setArrangementType("A");
        depositArrangement.setIsOverdraftRequired(true);
        when(retrievePendingArrangementService.retrievePamService.retrievePendingArrangement("IBL", upStreamRequest.getProductArrangement().getArrangementId(), upStreamRequest.getProductArrangement().getReferral())).thenReturn(depositArrangement);
        retrievePendingArrangementService.retrievePendingArrangements(upStreamRequest);
        assertEquals("1002", upStreamRequest.getProductArrangement().getApplicationStatus());
    }

    @Test
    public void testRetrievePendingArrangement() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, InternalServiceErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        ActivateProductArrangementRequest upStreamRequest = testDataHelper.createApaRequestForPca();
        upStreamRequest.setSourceSystemIdentifier("1");
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangementForB750();
        depositArrangement.setIsOverdraftRequired(true);
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setCustomerLocation(new Location());
        depositArrangement.getConditions().get(0).setName("INTEND_TO_SWITCH");
        when(retrievePendingArrangementService.retrievePamService.retrievePendingArrangement("IBL", upStreamRequest.getProductArrangement().getArrangementId(), upStreamRequest.getProductArrangement().getReferral())).thenReturn(depositArrangement);
        retrievePendingArrangementService.retrievePendingArrangements(upStreamRequest);
        assertEquals("1002", upStreamRequest.getProductArrangement().getApplicationStatus());
    }
}
