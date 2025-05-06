package com.lloydsbanking.salsa.activate.sira.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.activate.sira.convert.SiraRequestFactory;
import com.lloydsbanking.salsa.activate.sira.utility.SiraHelper;
import com.lloydsbanking.salsa.downstream.pam.jdbc.*;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationActivityHistory;
import com.lloydsbanking.salsa.downstream.pam.model.Applications;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.downstream.pam.model.TmxDetails;
import com.lloydsbanking.salsa.downstream.pam.service.update.UpdateApplicationParamValues;
import com.lloydsbanking.salsa.downstream.pam.service.update.UpdateApplicationParamValuesForSira;
import com.lloydsbanking.salsa.downstream.sira.client.SiraClient;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.synectics_solutions.ws.webservices.dataservices.v2.servicesaccess.AuthenticationHeader;
import com.synectics_solutions.ws.webservices.dataservices.v2.servicesaccess.Source;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.Individual;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.ws.WebServiceException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import  static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class SiraRetrieverTest {
    private SiraRetriever siraRetriever;
    private TestDataHelper testDataHelper;
    private ApplicationDetails applicationDetails;
    @Before
    public void setUp() {
        siraRetriever = new SiraRetriever();
        siraRetriever.tmxDao = mock(TmxDetailsDao.class);
        testDataHelper = new TestDataHelper();
        siraRetriever.siraClient = mock(SiraClient.class);
        siraRetriever.siraHelper=mock(SiraHelper.class);
        applicationDetails = new ApplicationDetails();
        siraRetriever.siraRequestFactory=mock(SiraRequestFactory.class);
        siraRetriever.headerRetriever=new HeaderRetriever();
        siraRetriever.updateApplicationParamValuesForSira=mock(UpdateApplicationParamValuesForSira.class);
        siraRetriever.updateDepositArrangementConditionAndApplicationStatusHelper = new UpdateDepositArrangementConditionAndApplicationStatusHelper();
    }

    @Test
    public void retrieveSiraDecisionOnSuccessfulConnectionToSira() {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForPca();
        request.getProductArrangement().getPrimaryInvolvedParty().setIsPlayedBy(new Individual());
        request.getProductArrangement().setArrangementId("123");


        when(siraRetriever.tmxDao.findByApplicationsId(any(Long.class))).thenReturn(testDataHelper.createTmxDetails());
        when(siraRetriever.siraClient.submitWorkItemResult(any(Source.class), any(String.class), any(Boolean.class), any(AuthenticationHeader.class))).thenReturn(testDataHelper.createSubmitWorkItemResult(new BigInteger("0")));
        CustomerScore eidvCustomerScore = new CustomerScore();
        eidvCustomerScore.setAssessmentType("EIDV");
        eidvCustomerScore.setScoreResult("Accept");
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(eidvCustomerScore);
        CustomerScore asmCustomerScore = new CustomerScore();
        asmCustomerScore.setAssessmentType("ASM");
        asmCustomerScore.setScoreResult(ActivateCommonConstant.AsmDecision.ACCEPT);
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(asmCustomerScore);
        TmxDetails tmxDetails=new TmxDetails();
        tmxDetails.setApplications(new Applications());
        tmxDetails.setDeviceData("Device");
        when(siraRetriever.tmxDao.findByApplicationsId(Long.valueOf(request.getProductArrangement().getArrangementId()))).thenReturn(tmxDetails);
        List<ReferenceDataLookUp> referenceDataLookUpListExpected = new ArrayList<>();
        referenceDataLookUpListExpected.add(new ReferenceDataLookUp());
        referenceDataLookUpListExpected.get(0).setLookupValueDesc("0");
        siraRetriever.retrieveSiraDecision((DepositArrangement) request.getProductArrangement(), applicationDetails,request.getHeader(),true);

        assertEquals(3, request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().size());
        assertNull(request.getProductArrangement().getApplicationSubStatus());
       assertEquals("False",request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(2).getCustomerDecision().getConnectivityErrorFlag());

    }

    @Test
    public void retrieveSiraDecisionOnGettingErrorFromSira() {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForPca();
        request.getProductArrangement().getPrimaryInvolvedParty().setIsPlayedBy(new Individual());
        request.getProductArrangement().setArrangementId("123");
        ApplicationActivityHistory applicationActivityHistory=new ApplicationActivityHistory();
        applicationActivityHistory.setDateModified(new Date());
        List<ApplicationActivityHistory> applicationActivityHistoryList=new ArrayList<>();
        applicationActivityHistoryList.add(applicationActivityHistory);

        when(siraRetriever.tmxDao.findByApplicationsId(any(Long.class))).thenReturn(testDataHelper.createTmxDetails());
        when(siraRetriever.siraClient.submitWorkItemResult(any(Source.class), any(String.class), any(Boolean.class), any(AuthenticationHeader.class))).thenThrow(WebServiceException.class);
        CustomerScore eidvCustomerScore = new CustomerScore();
        eidvCustomerScore.setAssessmentType("EIDV");
        eidvCustomerScore.setScoreResult("Accept");
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(eidvCustomerScore);
        CustomerScore asmCustomerScore = new CustomerScore();
        asmCustomerScore.setAssessmentType("ASM");
        asmCustomerScore.setScoreResult(ActivateCommonConstant.AsmDecision.ACCEPT);
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(asmCustomerScore);
        TmxDetails tmxDetails=new TmxDetails();
        tmxDetails.setApplications(new Applications());
        tmxDetails.setDeviceData("Device");
        when(siraRetriever.tmxDao.findByApplicationsId(Long.valueOf(request.getProductArrangement().getArrangementId()))).thenReturn(tmxDetails);
        List<ReferenceDataLookUp> referenceDataLookUpListExpected = new ArrayList<>();
        referenceDataLookUpListExpected.add(new ReferenceDataLookUp());
        referenceDataLookUpListExpected.get(0).setLookupValueDesc("0");
        siraRetriever.retrieveSiraDecision((DepositArrangement) request.getProductArrangement(), applicationDetails,request.getHeader(),true);

        assertEquals(3, request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().size());
        assertNotNull(request.getProductArrangement().getApplicationSubStatus());
        assertEquals("True",request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(2).getCustomerDecision().getConnectivityErrorFlag());

        assertEquals("5008",request.getProductArrangement().getApplicationSubStatus());

    }


}
