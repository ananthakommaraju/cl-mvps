package com.lloydsbanking.salsa.apapca.service.fulfil;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.RuleCondition;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@Category(UnitTest.class)
public class ValidateFulfillPendingBankAccountArrangementTest {

    private ValidateFulfillPendingBankAccountArrangement validateFulfillPendingBankAccountArrangement;
    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        validateFulfillPendingBankAccountArrangement = new ValidateFulfillPendingBankAccountArrangement();
        validateFulfillPendingBankAccountArrangement.switchClient = mock(SwitchService.class);
        testDataHelper = new TestDataHelper();
    }

    @Test
    public void testCheckCondition() {
        assertTrue(validateFulfillPendingBankAccountArrangement.checkCondition(false, null, "INTEND_TO_SWITCH"));
        assertTrue(validateFulfillPendingBankAccountArrangement.checkCondition(false, "INTEND_TO_SWITCH", "INTEND_TO_SWITCH"));
        assertFalse(validateFulfillPendingBankAccountArrangement.checkCondition(true, null, "INTEND_TO_SWITCH"));
        assertTrue(validateFulfillPendingBankAccountArrangement.checkCondition(true, "INTEND_TO_SWITCH", "INTEND_TO_SWITCH"));
    }

    @Test
    public void testCheckPCAReEngineering() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("10954");
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("INTEND_TO_SWITCH");
        ruleCondition.setResult("Y");
        RuleCondition ruleCondition1 = new RuleCondition();
        depositArrangement.getConditions().add(ruleCondition);
        depositArrangement.getConditions().add(ruleCondition1);
        assertTrue(validateFulfillPendingBankAccountArrangement.checkPCAReEngineering(depositArrangement));
    }

    @Test
    public void testCheckPCAReEngineeringWithNoConditions() {
        DepositArrangement depositArrangement = new DepositArrangement();
        assertFalse(validateFulfillPendingBankAccountArrangement.checkPCAReEngineering(depositArrangement));
    }

    @Test
    public void testIsDebitCardRequired() {
        List<RuleCondition> conditions = new ArrayList<>();
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("DEBIT_CARD_REQUIRED_FLAG");
        ruleCondition.setResult("Y");
        RuleCondition ruleCondition1 = new RuleCondition();
        ruleCondition1.setName("DEBIT_CARD_REQUIRED_FLAG");
        ruleCondition1.setResult("N");
        RuleCondition ruleCondition2 = new RuleCondition();
        ruleCondition2.setName(null);
        conditions.add(ruleCondition1);
        conditions.add(ruleCondition2);
        conditions.add(ruleCondition);
        assertTrue(validateFulfillPendingBankAccountArrangement.isDebitCardRequired(conditions,"1026"));
    }

    @Test
    public void testCheckApplicationSubStatus() {
        assertTrue(validateFulfillPendingBankAccountArrangement.checkApplicationSubStatus("1018"));
        assertTrue(validateFulfillPendingBankAccountArrangement.checkApplicationSubStatus("1030"));
        assertTrue(validateFulfillPendingBankAccountArrangement.checkApplicationSubStatus("1029"));
        assertTrue(validateFulfillPendingBankAccountArrangement.checkApplicationSubStatus("1032"));
        assertTrue(validateFulfillPendingBankAccountArrangement.checkApplicationSubStatus("1033"));
        assertFalse(validateFulfillPendingBankAccountArrangement.checkApplicationSubStatus("1019"));
        assertFalse(validateFulfillPendingBankAccountArrangement.checkApplicationSubStatus(null));
    }

}
