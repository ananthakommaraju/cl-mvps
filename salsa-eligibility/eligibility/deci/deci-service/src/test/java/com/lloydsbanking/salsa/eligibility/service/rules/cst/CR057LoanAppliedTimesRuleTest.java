package com.lloydsbanking.salsa.eligibility.service.rules.cst;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ref.jdbc.RefLookupDao;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.downstream.ref.model.RefLookupDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.downstream.AccountEventRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalBusinessException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydstsb.ib.wsbridge.system.StB093BEventLogReadList;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CR057LoanAppliedTimesRuleTest {

    TestDataHelper testDataHelper;

    CR057LoanAppliedTimesRule rule;

    StB093BEventLogReadList b093Resp;

    DetermineElegibileInstructionsRequest upstreamRequest;

    RefInstructionRulesDto rulesDto;
    private EligibilityDecision testEligibility;
    RefLookupDao refLookupDao;

    @Before
    public void setUp() {

        rule = new CR057LoanAppliedTimesRule();
        testDataHelper = new TestDataHelper();
        upstreamRequest = testDataHelper.createEligibilityRequest("abc", testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);
        rule.accountEventRetriever = mock(AccountEventRetriever.class);
        rule.refLookupDao = mock(RefLookupDao.class);
        b093Resp = new StB093BEventLogReadList();
        rulesDto = new RefInstructionRulesDto();
        rulesDto.setRuleParamValue("30:1");
        rulesDto.setCmsReason("CR057");
        refLookupDao = mock(RefLookupDao.class);
    }

    @Test
    public void testCR057isSuccessful() throws DatatypeConfigurationException, SalsaExternalServiceException, SalsaInternalServiceException, SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException, EligibilityException {

        List refLookupDtos = new ArrayList<RefLookupDto>();
        RefLookupDto refLookupDto = new RefLookupDto();
        refLookupDto.setLookupValSd("B_LN_ACCPT");
        refLookupDto.setGroupCd("RBB_LOAN_ACCPT");
        refLookupDtos.add(refLookupDto);

        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setHeader(upstreamRequest.getHeader());
        ruleDataHolder.setRuleParamValue(rulesDto.getRuleParamValue());
        List<String> accountEventList = new ArrayList<>();
        accountEventList.add("111999");
        accountEventList.add("110999");

        when(rule.accountEventRetriever.getAccountEvents("30:1", null, upstreamRequest.getHeader())).thenReturn(accountEventList);
        when(rule.refLookupDao.findByChannelAndGroupCdIn(upstreamRequest.getHeader().getChannelId(),rule.groupCodeList)).thenReturn(refLookupDtos);
        testEligibility = rule.evaluate(ruleDataHolder, null, null, null);
        assertTrue(testEligibility.isEligible());
        assertNull(testEligibility.getReasonText());
    }

    @Test
    public void testCR057isUnsuccessful() throws DatatypeConfigurationException, SalsaExternalServiceException, SalsaInternalServiceException, SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException, EligibilityException {
        List refLookupDtos = new ArrayList<RefLookupDto>();
        RefLookupDto refLookupDto = new RefLookupDto();
        refLookupDto.setLookupValSd("B_LN_ACCPT");
        refLookupDto.setGroupCd("RBB_LOAN_ACCPT");
        refLookupDtos.add(refLookupDto);

        List<String> accountEventList = new ArrayList<>();
        accountEventList.add("Loan Accept; Loan Term =12; SL at time of application = &#163; 050000.00;Total exposure at time of application= &#163;000100.00;Arrangement Fee=&#163;100.00; Business entity= 004;Repayment amount=&#163; 000099.24; Loan Type = CLP Loan;Accept Text =Business Loan is accepted B_LN_ACCPT");
        accountEventList.add("XYZ B_OD_ACCPT");

        when(rule.accountEventRetriever.getAccountEvents("30:1", null, upstreamRequest.getHeader())).thenReturn(accountEventList);
        when(rule.refLookupDao.findByChannelAndGroupCdIn(upstreamRequest.getHeader().getChannelId(),rule.groupCodeList)).thenReturn(refLookupDtos);
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRuleParamValue(rulesDto.getRuleParamValue());
        ruleDataHolder.setHeader(upstreamRequest.getHeader());
        testEligibility = rule.evaluate(ruleDataHolder, null, null, null);


        //assertFalse(testEligibility.isEligible());
        assertEquals("Customer has applied for loan/overdraft 1 times which is more than threshold 1  in last 30 days", testEligibility.getReasonText());

    }

}
