package com.lloydsbanking.salsa.eligibility.service.rules.avn;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalBusinessErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsResourceNotAvailableErrorMsg;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
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
    private TestDataHelper testDataHelper;

    private CR028FRTDMaxProductHoldingRule rule;

    DetermineElegibileInstructionsRequest upstreamRequest;

    List<ProductArrangement> customerArrangements;

    @Before
    public void setUp() {

        rule = new CR028FRTDMaxProductHoldingRule();

        testDataHelper = new TestDataHelper();

        upstreamRequest = testDataHelper.createEligibilityRequest("abc", testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);

        customerArrangements = new ArrayList();

    }

    @Test
    public void testCR028FRTDProductHoldingRuleFails() throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        RefInstructionRulesDto rulesDto = new RefInstructionRulesDto();
        rulesDto.setCmsReason("CR012");
        rulesDto.setRule("CR028");
        rulesDto.setRuleParamValue("5");


        for (int i = 0; i < 5; i++) {
            ProductArrangement productArrangement = new ProductArrangement();
            productArrangement.setParentInstructionMnemonic("G_ONL_FRTD");
            customerArrangements.add(productArrangement);
        }

        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule("CR028");
        ruleDataHolder.setRuleParamValue("5");
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(customerArrangements));

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder, "AVN");
        assertEquals("Customer cannot have more than 5 instances of the product.", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());

    }

    @Test
    public void testCR028FRTDProductHoldingRulePasses() throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        for (int i = 0; i < 3; i++) {
            ProductArrangement productArrangement = new ProductArrangement();
            productArrangement.setParentInstructionMnemonic("G_ONL_FRTD");
            customerArrangements.add(productArrangement);
        }


        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule("CR028");
        ruleDataHolder.setRuleParamValue("5");
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(customerArrangements));

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder, "AVN");
        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }
}
