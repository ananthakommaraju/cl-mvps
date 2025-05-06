package com.lloydsbanking.salsa.eligibility.service.rules.aga.wz;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class CR042MaxInstanceOfProductsRuleTest {
    private CR042MaxInstanceOfProductsRule rule;

    RuleDataHolder ruleDataHolder;

    TestDataHelper dataHelper;

    @Before
    public void setUp() {
        rule = new CR042MaxInstanceOfProductsRule();
        ruleDataHolder = new RuleDataHolder();
        dataHelper = new TestDataHelper();
    }

    @Test
    public void testEvaluateReturnsNull() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        List<ProductArrangement> productArrangements = new ArrayList<>();
        productArrangements.add(dataHelper.createExistingDepositArrangements());
        ruleDataHolder.setRuleParamValue("3");
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));

        EligibilityDecision evaluate = rule.evaluate("P_NEW_BASIC", ruleDataHolder);
        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

    @Test
    public void testEvaluateReturnsDeclineReason() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        List<ProductArrangement> productArrangements = new ArrayList<>();
        productArrangements.add(dataHelper.createExistingDepositArrangements());
        productArrangements.get(0).getAssociatedProduct().setProductType("1");
        ruleDataHolder.setRuleParamValue("1");
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));

        EligibilityDecision evaluate = rule.evaluate("P_NEW_BASIC", ruleDataHolder);
        assertEquals("Customer cannot have more than 1 instances of the product.", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }
}
