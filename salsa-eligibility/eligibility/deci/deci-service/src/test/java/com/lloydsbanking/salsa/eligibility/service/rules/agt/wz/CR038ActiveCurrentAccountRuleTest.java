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
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;

@Category(UnitTest.class)
public class CR038ActiveCurrentAccountRuleTest

{
    private RuleDataHolder ruleDataHolder;
    private CR038ActiveCurrentAccountRule rule;

    @Before
    public void setUp() {
        ruleDataHolder = new RuleDataHolder();
        rule = new CR038ActiveCurrentAccountRule();

    }

    @Test
    public void testEvaluateReturnsDeclineReason() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        List<ProductArrangement> productArrangementList = new ArrayList();
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setAssociatedProduct(new Product());
        productArrangement.getAssociatedProduct().setStatusCode("002");
        productArrangement.setLifecycleStatus("Dormant");
        productArrangementList.add(productArrangement);
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangementList));
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertEquals("Customer doesn't have an active current account ", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());

    }

    @Test
    public void testEvaluateReturnsEligibilityAsTrue() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        List<ProductArrangement> productArrangementList = new ArrayList();
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setAssociatedProduct(new Product());
        productArrangement.getAssociatedProduct().setStatusCode("001");
        productArrangement.setLifecycleStatus("Effective");
        productArrangementList.add(productArrangement);
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangementList));
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }


}
