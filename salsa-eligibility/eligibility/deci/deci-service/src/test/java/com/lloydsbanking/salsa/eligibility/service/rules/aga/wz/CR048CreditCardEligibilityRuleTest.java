package com.lloydsbanking.salsa.eligibility.service.rules.aga.wz;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.prd.service.AdministerProductSelectionService;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import com.lloydsbanking.salsa.logging.application.ProductTraceLog;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CR048CreditCardEligibilityRuleTest {
    CR048CreditCardEligibilityRule rule;

    RuleDataHolder ruleDataHolder;

    TestDataHelper dataHelper;

    @Before
    public void setUp() {
        rule = new CR048CreditCardEligibilityRule();
        ruleDataHolder = new RuleDataHolder();
        dataHelper = new TestDataHelper();
        rule.administerProductSelectionService = mock(AdministerProductSelectionService.class);
        rule.productTraceLog = mock(ProductTraceLog.class);
    }

    @Test
    public void testEvaluateReturnsNull() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        List<ProductArrangement> productArrangements = new ArrayList<>();
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setAssociatedProduct(new Product());
        productArrangement.getAssociatedProduct().setProductType("0");
        productArrangements.add(productArrangement);
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        EligibilityDecision evaluate = rule.evaluate("G_CREDCARD", ruleDataHolder);
        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

    @Test
    public void testEvaluateReturnsDeclineReason() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg {
        List<ProductArrangement> productArrangements = new ArrayList<>();
        Product product = new Product();
        Product associatedProduct = new Product();
        List<Product> ccProductList = new ArrayList();
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setAssociatedProduct(product);
        productArrangement.getAssociatedProduct().setProductType("3");
        productArrangements.add(productArrangement);
        ccProductList.add(productArrangement.getAssociatedProduct());
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        ruleDataHolder.setAssociatedProduct(associatedProduct);
        when(rule.administerProductSelectionService.administerProductSelection(ccProductList, associatedProduct, null)).thenReturn("INELIGIBLE");
        EligibilityDecision evaluate = rule.evaluate("G_CREDCARD", ruleDataHolder);
        ccProductList.add(product);
        assertEquals("Customer not eligible to apply for a credit card", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }
}

