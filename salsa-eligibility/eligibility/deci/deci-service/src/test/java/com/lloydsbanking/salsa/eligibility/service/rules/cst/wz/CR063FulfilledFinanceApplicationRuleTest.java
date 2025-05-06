package com.lloydsbanking.salsa.eligibility.service.rules.cst.wz;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.eligibility.service.downstream.EligibilityPAMRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CR063FulfilledFinanceApplicationRuleTest {
    private CR063FulfilledFinanceApplicationRule rule;

    private RuleDataHolder ruleDataHolder;

    TestDataHelper dataHelper;

    @Before
    public void setUp() {
        rule = new CR063FulfilledFinanceApplicationRule();
        ruleDataHolder = new RuleDataHolder();
        dataHelper = new TestDataHelper();
        rule.eligibilityPAMRetriever = mock(EligibilityPAMRetriever.class);
    }


    @Test
    public void testEvaluateReturnsTrue() {
        when(rule.eligibilityPAMRetriever.getNumberOfFulfilledFinanceApplication("542107294")).thenReturn(0);
        DateFactory dateFactory = new DateFactory();
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd");
        XMLGregorianCalendar date = dateFactory.stringToXMLGregorianCalendar("2015-10-20", sdf);
        EligibilityDecision eligibilityDecision = rule.evaluate(ruleDataHolder, date, "111618", "542107294");
        assertTrue(eligibilityDecision.isEligible());
    }

    @Test
    public void testEvaluateReturnsFalse() {
        when(rule.eligibilityPAMRetriever.getNumberOfFulfilledFinanceApplication("542107294")).thenReturn(1);
        DateFactory dateFactory = new DateFactory();
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd");
        XMLGregorianCalendar date = dateFactory.stringToXMLGregorianCalendar("2015-10-20", sdf);
        EligibilityDecision eligibilityDecision = rule.evaluate(ruleDataHolder, date, "111618", "542107294");
        assertFalse(eligibilityDecision.isEligible());
    }
}
