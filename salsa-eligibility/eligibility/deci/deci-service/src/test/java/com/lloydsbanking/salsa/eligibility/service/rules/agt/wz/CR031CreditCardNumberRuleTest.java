package com.lloydsbanking.salsa.eligibility.service.rules.agt.wz;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

@Category(UnitTest.class)
public class CR031CreditCardNumberRuleTest {
    private RuleDataHolder ruleDataHolder;
    private CR031CreditCardNumberRule rule;

    @Before
    public void setUp() {
        ruleDataHolder = new RuleDataHolder();
        rule = new CR031CreditCardNumberRule();

    }

    @Test
    public void testCR031CreditCardNumberRuleReturnDeclineReason() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        List<ProductArrangement> productArrangementList = new ArrayList();
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setAssociatedProduct(new Product());
        productArrangement.getAssociatedProduct().setProductType("3");
        ProductArrangement productArrangement1 = new ProductArrangement();
        productArrangement1.setAssociatedProduct(new Product());
        productArrangement1.getAssociatedProduct().setProductType("3");
        productArrangementList.add(productArrangement);
        productArrangementList.add(productArrangement1);
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangementList));
        ruleDataHolder.setRuleParamValue("1");

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertEquals("Customer has 2 or more credit cards", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }

    @Test
    public void testCR031CreditCardNumberRuleReturnNull() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        List<ProductArrangement> productArrangementList = new ArrayList();
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setAssociatedProduct(new Product());
        productArrangement.getAssociatedProduct().setProductType("3");
        ProductArrangement productArrangement1 = new ProductArrangement();
        productArrangement1.setAssociatedProduct(new Product());
        productArrangement1.getAssociatedProduct().setProductType("2");
        productArrangementList.add(productArrangement);
        productArrangementList.add(productArrangement1);
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangementList));
        ruleDataHolder.setRuleParamValue("1");

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertTrue(evaluate.isEligible());
        assertNull(evaluate.getReasonText());
    }
}
