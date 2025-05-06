package com.lloydsbanking.salsa.eligibility.service.rules.agt.wz;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import lib_sim_bo.businessobjects.InstructionDetails;
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
public class CR032ExistingProductRuleTest {
    private CR032ExistingProductRule rule;

    RuleDataHolder ruleDataHolder;

    TestDataHelper dataHelperWZ;

    @Before
    public void setUp() {
        rule = new CR032ExistingProductRule();
        ruleDataHolder = new RuleDataHolder();
        dataHelperWZ = new TestDataHelper();
    }

    @Test
    public void testEvaluateReturnsNull() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        List<ProductArrangement> productArrangements = new ArrayList<>();
        ProductArrangement productArrangement = dataHelperWZ.createExistingDepositArrangements();
        productArrangement.getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        productArrangement.getAssociatedProduct().getInstructionDetails().setInstructionMnemonic("P_PLAT");
        productArrangements.add(productArrangement);

        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

    @Test
    public void testEvaluateReturnsDeclineReason() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        List<ProductArrangement> productArrangements = new ArrayList<>();
        ProductArrangement productArrangement = dataHelperWZ.createExistingDepositArrangements();
        productArrangement.getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        productArrangement.getAssociatedProduct().getInstructionDetails().setInstructionMnemonic("G_ONL_FRTD");
        productArrangements.add(productArrangement);

        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertEquals("Customer does not have AVA Account", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }

    @Test
    public void testEvaluateWhenProductArrangementListEmpty() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        List<ProductArrangement> productArrangements = new ArrayList<>();

        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertEquals("Customer does not have any Account", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }
}
