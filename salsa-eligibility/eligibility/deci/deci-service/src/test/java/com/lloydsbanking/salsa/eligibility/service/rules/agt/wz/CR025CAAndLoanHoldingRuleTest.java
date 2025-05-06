package com.lloydsbanking.salsa.eligibility.service.rules.agt.wz;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;

@Category(UnitTest.class)
public class CR025CAAndLoanHoldingRuleTest {

    @Test
    public void shouldAlwaysReturnEligibilityAsTrue() throws Exception {

        CR025CAAndLoanHoldingRule cr025CAAndLoanHoldingRule = new CR025CAAndLoanHoldingRule();

        EligibilityDecision evaluate = cr025CAAndLoanHoldingRule.evaluate(new RuleDataHolder());

        assertTrue(evaluate.isEligible());
        assertNull(evaluate.getReasonText());

    }
}