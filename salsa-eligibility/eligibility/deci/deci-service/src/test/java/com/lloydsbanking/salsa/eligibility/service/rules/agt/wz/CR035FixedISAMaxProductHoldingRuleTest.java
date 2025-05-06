package com.lloydsbanking.salsa.eligibility.service.rules.agt.wz;

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

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

@Category(UnitTest.class)
public class CR035FixedISAMaxProductHoldingRuleTest {

    private CR035FixedISAMaxProductHoldingRule rule;

    @Before
    public void setUp() {
        rule = new CR035FixedISAMaxProductHoldingRule();

    }
    @Test
    public void testCR035FixedISAMaxProductHoldingRuleReturnDeclineReason() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        List<ProductArrangement> productArrangements = new ArrayList<>();
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setAssociatedProduct(new Product());
        productArrangement.getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        productArrangement.getAssociatedProduct().getInstructionDetails().setInstructionMnemonic("P_ISA_F_1Y");
        productArrangement.getAssociatedProduct().getInstructionDetails().setParentInstructionMnemonic("G_ISA");
        productArrangements.add(productArrangement);
        productArrangements.add(productArrangement);
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);

        assertEquals("Maximum number of fixed rate ISA\u0092s held", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }

    @Test
    public void testCR035FixedISAMaxProductHoldingRuleReturnNull() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        List<ProductArrangement> productArrangements = new ArrayList<>();
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setAssociatedProduct(new Product());
        productArrangement.getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        productArrangement.getAssociatedProduct().getInstructionDetails().setInstructionMnemonic("P_ISA_F3_1Y");
        productArrangement.getAssociatedProduct().getInstructionDetails().setParentInstructionMnemonic("G_ISA");
        productArrangements.add(productArrangement);
        productArrangements.add(productArrangement);
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);

        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }
}
