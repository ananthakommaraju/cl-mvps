package com.lloydsbanking.salsa.offer.apply.evaluate;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.soap.asm.f204.objects.F204Resp;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Resp;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Resp;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.ReferralCode;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@Category(UnitTest.class)
public class ApplicationStatusEvaluatorTest {

    private ApplicationStatusEvaluator applicationStatusEvaluator;
    private RequestHeader requestHeader;
    private F204Resp f204Resp;
    private F205Resp f205Resp;
    private F424Resp f424Resp;

    @Before
    public void setUp() {

        applicationStatusEvaluator = new ApplicationStatusEvaluator();
        applicationStatusEvaluator.asmResponseToCustomerScoreConverter = mock(AsmResponseToCustomerScoreConverter.class);
        TestDataHelper dataHelper = new TestDataHelper();
        f204Resp = dataHelper.createF204Response(0);
        f205Resp = dataHelper.createF205Response(0);
        f424Resp = dataHelper.createF424Response(0);
        requestHeader = dataHelper.createOpaPcaRequestHeader("LTB");
    }

    @Test
    public void testGetApplicationStatusForAsmFraudDecision() throws DataNotAvailableErrorMsg {
        String applicationStatus = null;
        CustomerScore customerScore = new CustomerScore();
        customerScore.setAssessmentType("asm");
        customerScore.setScoreResult("1");

        applicationStatus = applicationStatusEvaluator.getApplicationStatusForAsmFraudDecision(customerScore, f204Resp, requestHeader);
        assertEquals("1002", applicationStatus);

        customerScore.setScoreResult("2");
        applicationStatus = applicationStatusEvaluator.getApplicationStatusForAsmFraudDecision(customerScore, f204Resp, requestHeader);
        assertEquals("1003", applicationStatus);

        customerScore.setScoreResult("2");
        ReferralCode referralCode = new ReferralCode();
        referralCode.setDescription("desc");
        referralCode.setCode("501");
        customerScore.getReferralCode().add(referralCode);

        applicationStatus = applicationStatusEvaluator.getApplicationStatusForAsmFraudDecision(customerScore, f204Resp, requestHeader);
        assertEquals("1005", applicationStatus);

        customerScore.setScoreResult("3");
        applicationStatus = applicationStatusEvaluator.getApplicationStatusForAsmFraudDecision(customerScore, f204Resp, requestHeader);
        assertEquals("1004", applicationStatus);

        customerScore.setScoreResult(null);
        applicationStatus = applicationStatusEvaluator.getApplicationStatusForAsmFraudDecision(customerScore, f204Resp, requestHeader);
        assertEquals("1004", applicationStatus);

    }

    @Test
    public void testGetApplicationStatusForCreditScoreDecision() throws DataNotAvailableErrorMsg {
        String applicationStatus = null;
        CustomerScore customerScore = new CustomerScore();
        customerScore.setAssessmentType("asm");
        customerScore.setScoreResult("1");

        f205Resp.setASMCreditScoreResultCd("1");
        applicationStatus = applicationStatusEvaluator.getApplicationStatusForCreditScoreDecision(f205Resp, "1003", customerScore, requestHeader);
        assertEquals("1003", applicationStatus);

        applicationStatus = applicationStatusEvaluator.getApplicationStatusForCreditScoreDecision(f205Resp, "1001", customerScore, requestHeader);
        assertEquals("1002", applicationStatus);

        customerScore.setScoreResult("2");
        f205Resp.setASMCreditScoreResultCd("2");
        applicationStatus = applicationStatusEvaluator.getApplicationStatusForCreditScoreDecision(f205Resp, "1002", customerScore, requestHeader);
        assertEquals("1003", applicationStatus);

        customerScore.setScoreResult("2");
        ReferralCode referralCode = new ReferralCode();
        referralCode.setDescription("desc");
        referralCode.setCode("501");
        customerScore.getReferralCode().add(referralCode);
        f205Resp.setASMCreditScoreResultCd("2");
        applicationStatus = applicationStatusEvaluator.getApplicationStatusForCreditScoreDecision(f205Resp, "1002", customerScore, requestHeader);
        assertEquals("1005", applicationStatus);

        customerScore.setScoreResult("3");
        f205Resp.setASMCreditScoreResultCd("3");
        applicationStatus = applicationStatusEvaluator.getApplicationStatusForCreditScoreDecision(f205Resp, "1002", customerScore, requestHeader);
        assertEquals("1004", applicationStatus);

        customerScore.setScoreResult("4");
        f205Resp.setASMCreditScoreResultCd("3");
        applicationStatus = applicationStatusEvaluator.getApplicationStatusForCreditScoreDecision(f205Resp, "1001", customerScore, requestHeader);
        assertEquals("1004", applicationStatus);


    }

    @Test
    public void testGetApplicationStatusForAsmCreditDecisionForCC() throws DataNotAvailableErrorMsg {
        String applicationStatus = null;
        CustomerScore customerScore = new CustomerScore();
        customerScore.setAssessmentType("asm");
        customerScore.setScoreResult("1");

        applicationStatus = applicationStatusEvaluator.getApplicationStatusForAsmCreditDecisionForCC(customerScore, f424Resp, requestHeader);
        assertEquals("1002", applicationStatus);

        customerScore.setScoreResult("2");
        applicationStatus = applicationStatusEvaluator.getApplicationStatusForAsmCreditDecisionForCC(customerScore, f424Resp, requestHeader);
        assertEquals("1003", applicationStatus);

        customerScore.setScoreResult("2");
        ReferralCode referralCode = new ReferralCode();
        referralCode.setDescription("desc");
        referralCode.setCode("501");
        customerScore.getReferralCode().add(referralCode);

        applicationStatus = applicationStatusEvaluator.getApplicationStatusForAsmCreditDecisionForCC(customerScore, f424Resp, requestHeader);
        assertEquals("1005", applicationStatus);

        customerScore.setScoreResult("3");
        applicationStatus = applicationStatusEvaluator.getApplicationStatusForAsmCreditDecisionForCC(customerScore, f424Resp, requestHeader);
        assertEquals("1004", applicationStatus);

        customerScore.setScoreResult(null);
        applicationStatus = applicationStatusEvaluator.getApplicationStatusForAsmCreditDecisionForCC(customerScore, f424Resp, requestHeader);
        assertEquals("1004", applicationStatus);

    }

    @Test
    public void testGetApplicationStatusForAsmCreditDecisionForCCWithEmptyScoreResule() throws DataNotAvailableErrorMsg {
        String applicationStatus = null;
        CustomerScore customerScore = new CustomerScore();
        customerScore.setAssessmentType("asm");
        customerScore.setScoreResult("");

        applicationStatus = applicationStatusEvaluator.getApplicationStatusForAsmCreditDecisionForCC(customerScore, f424Resp, requestHeader);
        assertEquals("1004", applicationStatus);

        customerScore.setScoreResult("2");
        applicationStatus = applicationStatusEvaluator.getApplicationStatusForAsmCreditDecisionForCC(customerScore, f424Resp, requestHeader);
        assertEquals("1003", applicationStatus);

        customerScore.setScoreResult("2");
        ReferralCode referralCode = new ReferralCode();
        referralCode.setDescription("desc");
        referralCode.setCode("501");
        customerScore.getReferralCode().add(referralCode);

        applicationStatus = applicationStatusEvaluator.getApplicationStatusForAsmCreditDecisionForCC(customerScore, f424Resp, requestHeader);
        assertEquals("1005", applicationStatus);

        customerScore.setScoreResult("3");
        applicationStatus = applicationStatusEvaluator.getApplicationStatusForAsmCreditDecisionForCC(customerScore, f424Resp, requestHeader);
        assertEquals("1004", applicationStatus);

        customerScore.setScoreResult(null);
        applicationStatus = applicationStatusEvaluator.getApplicationStatusForAsmCreditDecisionForCC(customerScore, f424Resp, requestHeader);
        assertEquals("1004", applicationStatus);

    }
}
