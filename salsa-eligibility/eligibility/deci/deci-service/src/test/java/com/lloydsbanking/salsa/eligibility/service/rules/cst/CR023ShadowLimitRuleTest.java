package com.lloydsbanking.salsa.eligibility.service.rules.cst;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.brand.ChannelToBrandMapping;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.ChannelSpecificArrangements;
import com.lloydsbanking.salsa.eligibility.service.downstream.ShadowLimitRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.soap.fs.user.StAccountListDetail;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
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
public class CR023ShadowLimitRuleTest {
    private TestDataHelper testDataHelper;

    private CR023ShadowLimitRule rule;

    DetermineElegibileInstructionsRequest upstreamRequest;

    private EligibilityDecision testEligibility;

    @Before
    public void setUp() {

        rule = new CR023ShadowLimitRule();
        testDataHelper = new TestDataHelper();

        upstreamRequest = testDataHelper.createEligibilityRequest("abc", testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);


        rule.shadowLimitRetriever = mock(ShadowLimitRetriever.class);

        rule.appGroupRetriever = mock(AppGroupRetriever.class);

        rule.channelSpecificArrangements = mock(ChannelSpecificArrangements.class);

        rule.channelToBrandMapping = new ChannelToBrandMapping();

    }


    @Test
    public void testCR023ShadowLimitRuleFails() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, EligibilityException {
        DatatypeFactory datatypeFactory = null;

        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        RefInstructionRulesDto rulesDto = new RefInstructionRulesDto();

        rulesDto.setRuleParamValue("2");
        rulesDto.setRule("CR023");
        StAccountListDetail productArrangement = new StAccountListDetail();
        productArrangement.setAccountcategory("C");
        productArrangement.setBrandcode("LTB");
        List<StAccountListDetail> productArrangements = new ArrayList<>();
        productArrangements.add(productArrangement);
        when(rule.channelSpecificArrangements.getChannelSpecificArrangements(upstreamRequest.getHeader())).thenReturn(productArrangements);
        when(rule.shadowLimitRetriever.getShadowLimit(upstreamRequest.getHeader(), "772519", null, rule.appGroupRetriever.callRetrieveCBSAppGroup(upstreamRequest.getHeader(), "772519", false)))
                .thenReturn("1");

        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRuleParamValue(rulesDto.getRuleParamValue());
        ruleDataHolder.setRule(rulesDto.getRule());
        ruleDataHolder.setHeader(upstreamRequest.getHeader());

        testEligibility  = rule.evaluate(ruleDataHolder, datatypeFactory.newXMLGregorianCalendar(), "772519", null);

        assertFalse(testEligibility.isEligible());
        assertEquals("Customer has current account and Shadow Limit amount is less than threshold" + rulesDto.getRuleParamValue(), testEligibility.getReasonText());
    }

    @Test
    public void testCR023PassesForShadowLimitGreaterThanThreshold() throws EligibilityException, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException {
        DatatypeFactory datatypeFactory = null;

        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        RefInstructionRulesDto rulesDto = new RefInstructionRulesDto();

        rulesDto.setRuleParamValue("2");
        rulesDto.setRule("CR023");
        StAccountListDetail productArrangement = new StAccountListDetail();
        productArrangement.setAccountcategory("C");
        productArrangement.setBrandcode("LTB");
        List<StAccountListDetail> productArrangements = new ArrayList<>();
        productArrangements.add(productArrangement);
        when(rule.channelSpecificArrangements.getChannelSpecificArrangements(upstreamRequest.getHeader())).thenReturn(productArrangements);
        when(rule.shadowLimitRetriever.getShadowLimit(upstreamRequest.getHeader(), "772519", null, rule.appGroupRetriever.callRetrieveCBSAppGroup(upstreamRequest.getHeader(), "772519", false)))
                .thenReturn("4");

        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRuleParamValue(rulesDto.getRuleParamValue());
        ruleDataHolder.setRule(rulesDto.getRule());
        ruleDataHolder.setHeader(upstreamRequest.getHeader());

       testEligibility = rule.evaluate(ruleDataHolder, datatypeFactory.newXMLGregorianCalendar(), "772519", null);

        assertTrue(testEligibility.isEligible());
        assertNull(testEligibility.getReasonText());
    }

    @Test
    public void testCR023PassesForShadowLimitLessThanThresholdAndAccountTypeNotC() throws EligibilityException, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException {
        DatatypeFactory datatypeFactory = null;

        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        RefInstructionRulesDto rulesDto = new RefInstructionRulesDto();

        rulesDto.setRuleParamValue("2");
        rulesDto.setRule("CR023");
        StAccountListDetail productArrangement = new StAccountListDetail();
        productArrangement.setAccountcategory("B");
        productArrangement.setBrandcode("LTB");
        List<StAccountListDetail> productArrangements = new ArrayList<>();
        productArrangements.add(productArrangement);
        when(rule.channelSpecificArrangements.getChannelSpecificArrangements(upstreamRequest.getHeader())).thenReturn(productArrangements);
        when(rule.shadowLimitRetriever.getShadowLimit(upstreamRequest.getHeader(), "772519", null, rule.appGroupRetriever.callRetrieveCBSAppGroup(upstreamRequest.getHeader(), "772519", false)))
                .thenReturn("2");

        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRuleParamValue(rulesDto.getRuleParamValue());
        ruleDataHolder.setRule(rulesDto.getRule());
        ruleDataHolder.setHeader(upstreamRequest.getHeader());

       testEligibility = rule.evaluate(ruleDataHolder, datatypeFactory.newXMLGregorianCalendar(), "772519", null);

        assertTrue(testEligibility.isEligible());
        assertNull(testEligibility.getReasonText());
    }

    @Test
    public void testCR023PassesForShadowLimitLessThanThresholdAndAccountTypeBAndSellerLegalEntityNotMatched() throws EligibilityException, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException {
        DatatypeFactory datatypeFactory = null;

        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        RefInstructionRulesDto rulesDto = new RefInstructionRulesDto();

        rulesDto.setRuleParamValue("2");
        rulesDto.setRule("CR023");
        StAccountListDetail productArrangement = new StAccountListDetail();
        productArrangement.setAccountcategory("C");
        productArrangement.setBrandcode("VER");
        List<StAccountListDetail> productArrangements = new ArrayList<>();
        productArrangements.add(productArrangement);
        when(rule.channelSpecificArrangements.getChannelSpecificArrangements(upstreamRequest.getHeader())).thenReturn(productArrangements);
        when(rule.shadowLimitRetriever.getShadowLimit(upstreamRequest.getHeader(), "772519", null, rule.appGroupRetriever.callRetrieveCBSAppGroup(upstreamRequest.getHeader(), "772519", false)))
                .thenReturn("2");

        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRuleParamValue(rulesDto.getRuleParamValue());
        ruleDataHolder.setRule(rulesDto.getRule());
        ruleDataHolder.setHeader(upstreamRequest.getHeader());

        testEligibility = rule.evaluate(ruleDataHolder, datatypeFactory.newXMLGregorianCalendar(), "772519", null);
        assertTrue(testEligibility.isEligible());
        assertNull(testEligibility.getReasonText());
    }

}