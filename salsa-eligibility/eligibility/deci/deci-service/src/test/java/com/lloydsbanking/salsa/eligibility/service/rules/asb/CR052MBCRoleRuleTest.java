package com.lloydsbanking.salsa.eligibility.service.rules.asb;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.rules.common.BusinessArrangementHandlerBZ;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import lb_gbo_sales.businessobjects.BusinessArrangement;
import lb_gbo_sales.businessobjects.CustomerInstruction;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class CR052MBCRoleRuleTest {
    CR052MBCRoleRule rule;

    TestDataHelper testDataHelper;


    CustomerInstruction customerInstruction;

    List<BusinessArrangement> businessArrangements;

    BusinessArrangement businessArrangement;

    RefInstructionRulesDto rulesDto;


    @Before
    public void setUp() {
        rule = new CR052MBCRoleRule();
        testDataHelper = new TestDataHelper();
        rulesDto = new RefInstructionRulesDto();
        customerInstruction = new CustomerInstruction();
        customerInstruction.setEligibilityIndicator(true);
        businessArrangements = new ArrayList();
        businessArrangement = new BusinessArrangement();
        businessArrangement.setRolesInCtxt("CUS");
        businessArrangement.setBusinessId("+00641085068");
        businessArrangements.add(businessArrangement);

    }


    @Test
    public void testCR052MBCRoleRuleTestFails() throws EligibilityException {
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule("CR052");
        ruleDataHolder.setRuleParamValue("CUS");
        ruleDataHolder.setBusinessArrangement(new BusinessArrangementHandlerBZ(businessArrangements));

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder, "+00641085068", null, null);

        assertEquals("Customer has MBC role.", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());

    }

    @Test
    public void testCR052MBCRoleRuleTestPasses() throws EligibilityException {
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule("CR052");
        ruleDataHolder.setRuleParamValue("ABS");
        ruleDataHolder.setBusinessArrangement(new BusinessArrangementHandlerBZ(businessArrangements));

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder, "+00641085068", null, null);


        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());

    }

}
