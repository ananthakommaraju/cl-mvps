package com.lloydsbanking.salsa.apapca.service.propose;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.apapca.service.propose.downstream.AlternateSortCodeRetriever;
import com.lloydsbanking.salsa.apapca.service.propose.downstream.ProposeAccountRetriever;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.cbs.e229.objects.CbsAcTypeChkDgt;
import com.lloydsbanking.salsa.soap.cbs.e229.objects.E229Resp;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalBusinessErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class ProposedProductArrangementServiceTest {

    ProposedProductArrangementService proposedProductArrangementService;

    RequestHeader requestHeader;

    TestDataHelper testDataHelper;

    String cbsAppGroup = "09";

    ActivateProductArrangementRequest request;

    ActivateProductArrangementResponse activateProductArrangementResponse;

    @Before
    public void setUp() {
        proposedProductArrangementService = new ProposedProductArrangementService();
        proposedProductArrangementService.appGroupRetriever = mock(AppGroupRetriever.class);
        proposedProductArrangementService.proposeAccountRetriever = mock(ProposeAccountRetriever.class);
        proposedProductArrangementService.alternateSortCodeRetriever = mock(AlternateSortCodeRetriever.class);
        proposedProductArrangementService.exceptionUtilityActivate = mock(ExceptionUtilityActivate.class);
        proposedProductArrangementService.headerRetriever = new HeaderRetriever();
        proposedProductArrangementService.updateDepositArrangementConditionAndApplicationStatusHelper = new UpdateDepositArrangementConditionAndApplicationStatusHelper();
        testDataHelper = new TestDataHelper();
        requestHeader = testDataHelper.createApaRequestHeader();
        request = testDataHelper.createApaRequestForPca();
        activateProductArrangementResponse = new ActivateProductArrangementResponse();

    }

    @Test
    public void testProposedProductArrangementResponse() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg {
        when(proposedProductArrangementService.appGroupRetriever.callRetrieveCBSAppGroup(requestHeader, "007505")).thenReturn(cbsAppGroup);
        E229Resp response = testDataHelper.createE229Resp();
        response.getE229Result().getResultCondition().setReasonCode(135);
        when(proposedProductArrangementService.proposeAccountRetriever.proposeAccount(requestHeader, "007505", cbsAppGroup)).thenReturn(response);
        proposedProductArrangementService.callProposeProductArrangementAndSetAccountNumber(request, activateProductArrangementResponse);
        assertTrue(proposedProductArrangementService.callProposeProductArrangementAndSetAccountNumber(request, activateProductArrangementResponse));
    }

    @Test
    public void testProductArrangementResponseWhenAccNoIsNotNull() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg {
        request.getProductArrangement().setApplicationStatus(ApplicationStatus.APPROVED.getValue());
        request.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).setSortCode(null);
        when(proposedProductArrangementService.appGroupRetriever.callRetrieveCBSAppGroup(requestHeader, "007505")).thenReturn(cbsAppGroup);
        E229Resp response = testDataHelper.createE229Resp();
        response.getE229Result().getResultCondition().setReasonCode(135);
        CbsAcTypeChkDgt cbsAcTypeChkDgt = new CbsAcTypeChkDgt();
        cbsAcTypeChkDgt.setCBSAccountTypeCd("6");
        cbsAcTypeChkDgt.setCheckDigitId("3");
        response.getCbsAcTypeChkDgt().add(cbsAcTypeChkDgt);
        when(proposedProductArrangementService.proposeAccountRetriever.proposeAccount(requestHeader, "007505", cbsAppGroup)).thenReturn(response);
        proposedProductArrangementService.callProposeProductArrangementAndSetAccountNumber(request, activateProductArrangementResponse);
        assertTrue(proposedProductArrangementService.callProposeProductArrangementAndSetAccountNumber(request, activateProductArrangementResponse));
    }

    @Test
    public void testProductArrangementResponseWhenSystemIsNotAvailable() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg {
        when(proposedProductArrangementService.appGroupRetriever.callRetrieveCBSAppGroup(requestHeader, "007505")).thenReturn(cbsAppGroup);
        E229Resp response = testDataHelper.createE229Resp();
        when(proposedProductArrangementService.alternateSortCodeRetriever.proposeAccount(requestHeader, cbsAppGroup, TestDataHelper.TEST_CONTACT_POINT_ID)).thenReturn("00793");
        when(proposedProductArrangementService.proposeAccountRetriever.proposeAccount(requestHeader, "007505", cbsAppGroup)).thenReturn(response);
        when(proposedProductArrangementService.proposeAccountRetriever.proposeAccount(requestHeader, "00793", cbsAppGroup)).thenReturn(response);
        when(proposedProductArrangementService.exceptionUtilityActivate.externalBusinessError(requestHeader, testDataHelper.createE229Resp().getE229Result().getResultCondition().getReasonText(), testDataHelper.createE229Resp().getE229Result().getResultCondition().getReasonCode().toString())).thenReturn(new ActivateProductArrangementExternalBusinessErrorMsg());
        proposedProductArrangementService.callProposeProductArrangementAndSetAccountNumber(request, activateProductArrangementResponse);
    }

    @Test
    public void testResultConditionForError() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        request.getProductArrangement().setApplicationStatus(ApplicationStatus.APPROVED.getValue());
        request.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).setSortCode(null);
        when(proposedProductArrangementService.appGroupRetriever.callRetrieveCBSAppGroup(requestHeader, "007505")).thenReturn(cbsAppGroup);
        E229Resp response = testDataHelper.createE229Resp();
        CbsAcTypeChkDgt cbsAcTypeChkDgt = new CbsAcTypeChkDgt();
        cbsAcTypeChkDgt.setCBSAccountTypeCd("6");
        cbsAcTypeChkDgt.setCheckDigitId("3");
        response.getCbsAcTypeChkDgt().add(cbsAcTypeChkDgt);
        when(proposedProductArrangementService.proposeAccountRetriever.proposeAccount(any(RequestHeader.class), any(String.class), any(String.class))).thenReturn(response);
        when(proposedProductArrangementService.exceptionUtilityActivate.externalBusinessError(any(RequestHeader.class), any(String.class), any(String.class))).thenReturn(new ActivateProductArrangementExternalBusinessErrorMsg());
        proposedProductArrangementService.callProposeProductArrangementAndSetAccountNumber(request, activateProductArrangementResponse);
    }

    @Test
    public void testResultConditionWithoutError() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        request.getProductArrangement().setApplicationStatus(ApplicationStatus.APPROVED.getValue());
        request.getProductArrangement().getFinancialInstitution().getHasOrganisationUnits().get(0).setSortCode(null);
        when(proposedProductArrangementService.appGroupRetriever.callRetrieveCBSAppGroup(requestHeader, "007505")).thenReturn(cbsAppGroup);
        E229Resp response = testDataHelper.createE229Resp();
        response.getE229Result().getResultCondition().setReasonCode(135);
        CbsAcTypeChkDgt cbsAcTypeChkDgt = new CbsAcTypeChkDgt();
        cbsAcTypeChkDgt.setCBSAccountTypeCd("6");
        cbsAcTypeChkDgt.setCheckDigitId("3");
        response.getCbsAcTypeChkDgt().add(cbsAcTypeChkDgt);
        when(proposedProductArrangementService.proposeAccountRetriever.proposeAccount(any(RequestHeader.class), any(String.class), any(String.class))).thenReturn(response);
        proposedProductArrangementService.callProposeProductArrangementAndSetAccountNumber(request, activateProductArrangementResponse);
        assertTrue(proposedProductArrangementService.callProposeProductArrangementAndSetAccountNumber(request, activateProductArrangementResponse));
    }

}
