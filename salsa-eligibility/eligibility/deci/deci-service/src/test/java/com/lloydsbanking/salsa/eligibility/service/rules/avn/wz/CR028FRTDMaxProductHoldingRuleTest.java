package com.lloydsbanking.salsa.eligibility.service.rules.avn.wz;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lib_sim_bo.businessobjects.InstructionDetails;
import lib_sim_bo.businessobjects.Product;
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
public class CR028FRTDMaxProductHoldingRuleTest {
    private CR028FRTDMaxProductHoldingRule rule;

    @Before
    public void setUp() {
        rule = new CR028FRTDMaxProductHoldingRule();
    }

    @Test
    public void testEvaluateReturnsNull() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        List<ProductArrangement> productArrangements = new ArrayList<>();
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setAssociatedProduct(new Product());
        productArrangement.getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        productArrangement.getAssociatedProduct().getInstructionDetails().setInstructionMnemonic("P_TDO_1Y_M");
        productArrangement.getAssociatedProduct().getInstructionDetails().setParentInstructionMnemonic("G_ONL_FRTD");
        productArrangements.add(productArrangement);
        productArrangements.add(productArrangement);
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        ruleDataHolder.setRuleParamValue("03");
        ruleDataHolder.setRuleInsMnemonic("G_ONL_FRTD");

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder, "P_TDO_1Y_M");

        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

    @Test
    public void testEvaluateReturnsDeclineReason() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        List<ProductArrangement> productArrangements = new ArrayList<>();
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setAssociatedProduct(new Product());
        productArrangement.getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        productArrangement.getAssociatedProduct().getInstructionDetails().setInstructionMnemonic("P_TDO_1Y_M");
        productArrangement.getAssociatedProduct().getInstructionDetails().setParentInstructionMnemonic("G_ONL_FRTD");
        productArrangements.add(productArrangement);
        productArrangements.add(productArrangement);
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        ruleDataHolder.setRuleParamValue("01");
        ruleDataHolder.setRuleInsMnemonic("G_ONL_FRTD");

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder, "P_TDO_1Y_M");

        assertEquals("Customer cannot have more than 5 instances of the product.", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }
}
