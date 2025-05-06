package com.lloydsbanking.salsa.opapca.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ApplicationsDao;
import com.lloydsbanking.salsa.downstream.pam.jdbc.TmxDetailsDao;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationActivityHistory;
import com.lloydsbanking.salsa.downstream.pam.model.Applications;
import com.lloydsbanking.salsa.downstream.sira.client.SiraClient;
import com.lloydsbanking.salsa.offer.AsmDecision;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import com.lloydsbanking.salsa.opapca.service.TestDataHelper;
import com.lloydsbanking.salsa.opapca.service.convert.SiraRequestFactory;
import com.lloydsbanking.salsa.opapca.service.utility.SiraHelper;
import com.lloydsbanking.salsa.opapca.service.utility.SiraStatus;
import com.synectics_solutions.ws.webservices.dataservices.v2.servicesaccess.AuthenticationHeader;
import com.synectics_solutions.ws.webservices.dataservices.v2.servicesaccess.Source;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.RuleCondition;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class SiraRetrieverTest {
    private SiraRetriever siraRetriever;
    private TestDataHelper testDataHelper;
    private static final List<String> APLICATION_STATUS = Arrays.asList("1002", "1003", "1004", "1007");

    @Before
    public void setUp() {
        siraRetriever = new SiraRetriever();
        siraRetriever.siraClient = mock(SiraClient.class);
        siraRetriever.applicationsDao = mock(ApplicationsDao.class);
        siraRetriever.tmxDao = mock(TmxDetailsDao.class);
        siraRetriever.lookupDataRetriever = mock(LookupDataRetriever.class);
        siraRetriever.siraRequestFactory = mock(SiraRequestFactory.class);
        siraRetriever.siraHelper = new SiraHelper();
        testDataHelper = new TestDataHelper();
    }

    @Test
    public void retrieveSiraDecisionWithSiraDecisionAsAccept() throws DataNotAvailableErrorMsg {
        OfferProductArrangementRequest request = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getProductArrangement().setArrangementId("123");
        request.getProductArrangement().setSIRAEnabledSwitch(true);
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("INTEND_TO_SWITCH");
        request.getProductArrangement().getConditions().add(ruleCondition);
        ApplicationActivityHistory applicationActivityHistory = new ApplicationActivityHistory();
        applicationActivityHistory.setDateModified(new Date());
        List<ApplicationActivityHistory> applicationActivityHistoryList = new ArrayList<>();
        applicationActivityHistoryList.add(applicationActivityHistory);
        when(siraRetriever.applicationsDao.findOne(Long.valueOf("123"))).thenReturn(new Applications());
        when(siraRetriever.siraClient.submitWorkItemResult(any(Source.class), any(String.class), any(Boolean.class), any(AuthenticationHeader.class))).thenReturn(testDataHelper.createubmitWorkItemResult(new BigInteger("0")));
        when(siraRetriever.lookupDataRetriever.getLookupListFromChannelAndGroupCodeListAndSequence("LTB", Arrays.asList("SIRA_THRESHOLD_VALUE"))).thenReturn(testDataHelper.createSiraThresholdValuesList());
        CustomerScore eidvCustomerScore = new CustomerScore();
        eidvCustomerScore.setAssessmentType("EIDV");
        eidvCustomerScore.setScoreResult("Accept");
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(eidvCustomerScore);
        CustomerScore asmCustomerScore = new CustomerScore();
        asmCustomerScore.setAssessmentType("ASM");
        asmCustomerScore.setScoreResult(AsmDecision.APPROVED.getValue());
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(asmCustomerScore);
        siraRetriever.retrieveSiraDecision((DepositArrangement) request.getProductArrangement(), request.getHeader());
        assertEquals(3, request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().size());
        assertEquals(SiraStatus.ACCEPT.getValue(), request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(2).getCustomerDecision().getResultStatus());
        assertEquals("1002", request.getProductArrangement().getApplicationStatus());
        assertNull(request.getProductArrangement().getApplicationSubStatus());
    }

    @Test
    public void retrieveSiraDecisionWithSiraDecisionAsReferFraud() throws DataNotAvailableErrorMsg {
        OfferProductArrangementRequest request = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("INTEND_TO_SWITCH");
        request.getProductArrangement().getConditions().add(ruleCondition);
        request.getProductArrangement().setArrangementId("123");
        request.getProductArrangement().setSIRAEnabledSwitch(true);
        ApplicationActivityHistory applicationActivityHistory = new ApplicationActivityHistory();
        applicationActivityHistory.setDateModified(new Date());
        List<ApplicationActivityHistory> applicationActivityHistoryList = new ArrayList<>();
        applicationActivityHistoryList.add(applicationActivityHistory);
        when(siraRetriever.applicationsDao.findOne(Long.valueOf("123"))).thenReturn(new Applications());
        when(siraRetriever.siraClient.submitWorkItemResult(any(Source.class), any(String.class), any(Boolean.class), any(AuthenticationHeader.class))).thenReturn(testDataHelper.createubmitWorkItemResult(new BigInteger("390")));
        when(siraRetriever.lookupDataRetriever.getLookupListFromChannelAndGroupCodeListAndSequence("LTB", Arrays.asList("SIRA_THRESHOLD_VALUE"))).thenReturn(testDataHelper.createSiraThresholdValuesList());
        CustomerScore eidvCustomerScore = new CustomerScore();
        eidvCustomerScore.setAssessmentType("EIDV");
        eidvCustomerScore.setScoreResult("Accept");
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(eidvCustomerScore);
        CustomerScore asmCustomerScore = new CustomerScore();
        asmCustomerScore.setAssessmentType("ASM");
        asmCustomerScore.setScoreResult(AsmDecision.APPROVED.getValue());
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(asmCustomerScore);
        siraRetriever.retrieveSiraDecision((DepositArrangement) request.getProductArrangement(), request.getHeader());
        assertEquals(3, request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().size());
        assertEquals(SiraStatus.REFER_FRAUD.getValue(), request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(2).getCustomerDecision().getResultStatus());
        assertEquals("1003", request.getProductArrangement().getApplicationStatus());
        assertEquals("5001", request.getProductArrangement().getApplicationSubStatus());
    }

    @Test
    public void retrieveSiraDecisionWithSiraDecisionAsReferIDV() throws DataNotAvailableErrorMsg {
        OfferProductArrangementRequest request = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getProductArrangement().setArrangementId("123");
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("INTEND_TO_SWITCH");
        request.getProductArrangement().getConditions().add(ruleCondition);
        request.getProductArrangement().setSIRAEnabledSwitch(true);
        ApplicationActivityHistory applicationActivityHistory = new ApplicationActivityHistory();
        applicationActivityHistory.setDateModified(new Date());
        List<ApplicationActivityHistory> applicationActivityHistoryList = new ArrayList<>();
        applicationActivityHistoryList.add(applicationActivityHistory);
        when(siraRetriever.applicationsDao.findOne(Long.valueOf("123"))).thenReturn(new Applications());
        when(siraRetriever.siraClient.submitWorkItemResult(any(Source.class), any(String.class), any(Boolean.class), any(AuthenticationHeader.class))).thenReturn(testDataHelper.createubmitWorkItemResult(new BigInteger("560")));
        when(siraRetriever.lookupDataRetriever.getLookupListFromChannelAndGroupCodeListAndSequence("LTB", Arrays.asList("SIRA_THRESHOLD_VALUE"))).thenReturn(testDataHelper.createSiraThresholdValuesList());
        CustomerScore eidvCustomerScore = new CustomerScore();
        eidvCustomerScore.setAssessmentType("EIDV");
        eidvCustomerScore.setScoreResult("Accept");
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(eidvCustomerScore);
        CustomerScore asmCustomerScore = new CustomerScore();
        asmCustomerScore.setAssessmentType("ASM");
        asmCustomerScore.setScoreResult(AsmDecision.APPROVED.getValue());
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(asmCustomerScore);
        siraRetriever.retrieveSiraDecision((DepositArrangement) request.getProductArrangement(), request.getHeader());
        assertEquals(3, request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().size());
        assertEquals(SiraStatus.REFER_IDV.getValue(), request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(2).getCustomerDecision().getResultStatus());
        assertEquals("1002", request.getProductArrangement().getApplicationStatus());
        assertNull(request.getProductArrangement().getApplicationSubStatus());
    }

    @Test
    public void retrieveSiraDecisionWithSiraDecisionAsDecline() throws DataNotAvailableErrorMsg {
        OfferProductArrangementRequest request = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getProductArrangement().setArrangementId("123");
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("INTEND_TO_SWITCH");
        request.getProductArrangement().getConditions().add(ruleCondition);
        request.getProductArrangement().setSIRAEnabledSwitch(true);
        ApplicationActivityHistory applicationActivityHistory = new ApplicationActivityHistory();
        applicationActivityHistory.setDateModified(new Date());
        List<ApplicationActivityHistory> applicationActivityHistoryList = new ArrayList<>();
        applicationActivityHistoryList.add(applicationActivityHistory);
        when(siraRetriever.applicationsDao.findOne(Long.valueOf("123"))).thenReturn(new Applications());
        when(siraRetriever.siraClient.submitWorkItemResult(any(Source.class), any(String.class), any(Boolean.class), any(AuthenticationHeader.class))).thenReturn(testDataHelper.createubmitWorkItemResult(new BigInteger("10000")));
        when(siraRetriever.lookupDataRetriever.getLookupListFromChannelAndGroupCodeListAndSequence("LTB", Arrays.asList("SIRA_THRESHOLD_VALUE"))).thenReturn(testDataHelper.createSiraThresholdValuesList());
        CustomerScore eidvCustomerScore = new CustomerScore();
        eidvCustomerScore.setAssessmentType("EIDV");
        eidvCustomerScore.setScoreResult("Accept");
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(eidvCustomerScore);
        CustomerScore asmCustomerScore = new CustomerScore();
        asmCustomerScore.setAssessmentType("ASM");
        asmCustomerScore.setScoreResult(AsmDecision.APPROVED.getValue());
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(asmCustomerScore);
        siraRetriever.retrieveSiraDecision((DepositArrangement) request.getProductArrangement(), request.getHeader());
        assertEquals(3, request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().size());
        assertEquals(SiraStatus.DECLINE.getValue(), request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(2).getCustomerDecision().getResultStatus());
        assertEquals("1004", request.getProductArrangement().getApplicationStatus());
        assertEquals("5006", request.getProductArrangement().getApplicationSubStatus());
    }

    @Test
    public void retrieveSiraDecisionWithASMDecisionAsReferAndSiraAsAccept() throws DataNotAvailableErrorMsg {
        OfferProductArrangementRequest request = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getProductArrangement().setArrangementId("123");
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("INTEND_TO_SWITCH");
        request.getProductArrangement().getConditions().add(ruleCondition);
        request.getProductArrangement().setSIRAEnabledSwitch(true);
        ApplicationActivityHistory applicationActivityHistory = new ApplicationActivityHistory();
        applicationActivityHistory.setDateModified(new Date());
        List<ApplicationActivityHistory> applicationActivityHistoryList = new ArrayList<>();
        applicationActivityHistoryList.add(applicationActivityHistory);
        when(siraRetriever.applicationsDao.findOne(Long.valueOf("123"))).thenReturn(new Applications());
        when(siraRetriever.siraClient.submitWorkItemResult(any(Source.class), any(String.class), any(Boolean.class), any(AuthenticationHeader.class))).thenReturn(testDataHelper.createubmitWorkItemResult(new BigInteger("0")));
        when(siraRetriever.lookupDataRetriever.getLookupListFromChannelAndGroupCodeListAndSequence("LTB", Arrays.asList("SIRA_THRESHOLD_VALUE"))).thenReturn(testDataHelper.createSiraThresholdValuesList());
        CustomerScore eidvCustomerScore = new CustomerScore();
        eidvCustomerScore.setAssessmentType("EIDV");
        eidvCustomerScore.setScoreResult("Accept");
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(eidvCustomerScore);
        CustomerScore asmCustomerScore = new CustomerScore();
        asmCustomerScore.setAssessmentType("ASM");
        asmCustomerScore.setScoreResult(AsmDecision.REFERRED.getValue());
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(asmCustomerScore);
        siraRetriever.retrieveSiraDecision((DepositArrangement) request.getProductArrangement(), request.getHeader());
        assertEquals(3, request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().size());
        assertEquals(SiraStatus.ACCEPT.getValue(), request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(2).getCustomerDecision().getResultStatus());
        assertEquals("1003", request.getProductArrangement().getApplicationStatus());
        assertEquals("5002", request.getProductArrangement().getApplicationSubStatus());
    }

    @Test
    public void retrieveSiraDecisionWithASMDecisionAsDeclineAndSiraAsAccept() throws DataNotAvailableErrorMsg {
        OfferProductArrangementRequest request = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getProductArrangement().setArrangementId("123");
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("INTEND_TO_SWITCH");
        request.getProductArrangement().getConditions().add(ruleCondition);
        request.getProductArrangement().setSIRAEnabledSwitch(true);
        ApplicationActivityHistory applicationActivityHistory = new ApplicationActivityHistory();
        applicationActivityHistory.setDateModified(new Date());
        List<ApplicationActivityHistory> applicationActivityHistoryList = new ArrayList<>();
        applicationActivityHistoryList.add(applicationActivityHistory);
        when(siraRetriever.applicationsDao.findOne(Long.valueOf("123"))).thenReturn(new Applications());
        when(siraRetriever.siraClient.submitWorkItemResult(any(Source.class), any(String.class), any(Boolean.class), any(AuthenticationHeader.class))).thenReturn(testDataHelper.createubmitWorkItemResult(new BigInteger("0")));
        when(siraRetriever.lookupDataRetriever.getLookupListFromChannelAndGroupCodeListAndSequence("LTB", Arrays.asList("SIRA_THRESHOLD_VALUE"))).thenReturn(testDataHelper.createSiraThresholdValuesList());
        CustomerScore eidvCustomerScore = new CustomerScore();
        eidvCustomerScore.setAssessmentType("EIDV");
        eidvCustomerScore.setScoreResult("Accept");
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(eidvCustomerScore);
        CustomerScore asmCustomerScore = new CustomerScore();
        asmCustomerScore.setAssessmentType("ASM");
        asmCustomerScore.setScoreResult(AsmDecision.DECLINED.getValue());
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(asmCustomerScore);
        siraRetriever.retrieveSiraDecision((DepositArrangement) request.getProductArrangement(), request.getHeader());
        assertEquals(3, request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().size());
        assertEquals(SiraStatus.ACCEPT.getValue(), request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(2).getCustomerDecision().getResultStatus());
        assertEquals("1004", request.getProductArrangement().getApplicationStatus());
        assertEquals("5007", request.getProductArrangement().getApplicationSubStatus());
    }

    @Test
    public void retrieveSiraDecisionWithASMDecisionAsReferAndSiraAsRefer() throws DataNotAvailableErrorMsg {
        OfferProductArrangementRequest request = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getProductArrangement().setArrangementId("123");
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("INTEND_TO_SWITCH");
        request.getProductArrangement().getConditions().add(ruleCondition);
        request.getProductArrangement().setSIRAEnabledSwitch(true);
        ApplicationActivityHistory applicationActivityHistory = new ApplicationActivityHistory();
        applicationActivityHistory.setDateModified(new Date());
        List<ApplicationActivityHistory> applicationActivityHistoryList = new ArrayList<>();
        applicationActivityHistoryList.add(applicationActivityHistory);
        when(siraRetriever.applicationsDao.findOne(Long.valueOf("123"))).thenReturn(new Applications());
        when(siraRetriever.siraClient.submitWorkItemResult(any(Source.class), any(String.class), any(Boolean.class), any(AuthenticationHeader.class))).thenReturn(testDataHelper.createubmitWorkItemResult(new BigInteger("20")));
        when(siraRetriever.lookupDataRetriever.getLookupListFromChannelAndGroupCodeListAndSequence("LTB", Arrays.asList("SIRA_THRESHOLD_VALUE"))).thenReturn(testDataHelper.createSiraThresholdValuesList());
        CustomerScore eidvCustomerScore = new CustomerScore();
        eidvCustomerScore.setAssessmentType("EIDV");
        eidvCustomerScore.setScoreResult("Refer");
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(eidvCustomerScore);
        CustomerScore asmCustomerScore = new CustomerScore();
        asmCustomerScore.setAssessmentType("ASM");
        asmCustomerScore.setScoreResult(AsmDecision.REFERRED.getValue());
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(asmCustomerScore);
        siraRetriever.retrieveSiraDecision((DepositArrangement) request.getProductArrangement(), request.getHeader());
        assertEquals(3, request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().size());
        assertEquals(SiraStatus.REFER_FRAUD.getValue(), request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(2).getCustomerDecision().getResultStatus());
        assertEquals("1003", request.getProductArrangement().getApplicationStatus());
        assertEquals("5003", request.getProductArrangement().getApplicationSubStatus());
    }

    @Test
    public void retrieveSiraDecisionWithEIDVDecisionAsReferAndSiraAsAccept() throws DataNotAvailableErrorMsg {
        OfferProductArrangementRequest request = testDataHelper.generateOfferProductArrangementPCARequest("LTB");
        request.getProductArrangement().setArrangementId("123");
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("INTEND_TO_SWITCH");
        request.getProductArrangement().getConditions().add(ruleCondition);
        request.getProductArrangement().setSIRAEnabledSwitch(true);
        ApplicationActivityHistory applicationActivityHistory = new ApplicationActivityHistory();
        applicationActivityHistory.setDateModified(new Date());
        List<ApplicationActivityHistory> applicationActivityHistoryList = new ArrayList<>();
        applicationActivityHistoryList.add(applicationActivityHistory);
        when(siraRetriever.applicationsDao.findOne(Long.valueOf("123"))).thenReturn(new Applications());
        when(siraRetriever.siraClient.submitWorkItemResult(any(Source.class), any(String.class), any(Boolean.class), any(AuthenticationHeader.class))).thenReturn(testDataHelper.createubmitWorkItemResult(new BigInteger("0")));
        when(siraRetriever.lookupDataRetriever.getLookupListFromChannelAndGroupCodeListAndSequence("LTB", Arrays.asList("SIRA_THRESHOLD_VALUE"))).thenReturn(testDataHelper.createSiraThresholdValuesList());
        CustomerScore eidvCustomerScore = new CustomerScore();
        eidvCustomerScore.setAssessmentType("EIDV");
        eidvCustomerScore.setScoreResult("Refer");
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(eidvCustomerScore);
        CustomerScore asmCustomerScore = new CustomerScore();
        asmCustomerScore.setAssessmentType("ASM");
        asmCustomerScore.setScoreResult(AsmDecision.APPROVED.getValue());
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(asmCustomerScore);
        siraRetriever.retrieveSiraDecision((DepositArrangement) request.getProductArrangement(), request.getHeader());
        assertEquals(3, request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().size());
        assertEquals(SiraStatus.ACCEPT.getValue(), request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(2).getCustomerDecision().getResultStatus());
        assertEquals("1002", request.getProductArrangement().getApplicationStatus());
        assertNull(request.getProductArrangement().getApplicationSubStatus());
    }

}
