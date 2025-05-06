package com.lloydsbanking.salsa.eligibility.service.rules.aga;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.businessobjects.CustomerInstruction;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@Category(UnitTest.class)
public class CR053AccountOpeningDateRuleTest {
    private TestDataHelper testDataHelper;

    private CR053AccountOpeningDateRule rule;

    DetermineElegibileInstructionsRequest upstreamRequest;

    CustomerInstruction customerInstruction;

    RefInstructionRulesDto rulesDto;

    List<ProductArrangement> productArrangementList;

    @Before
    public void setUp() throws DatatypeConfigurationException {
        rule = new CR053AccountOpeningDateRule();
        testDataHelper = new TestDataHelper();
        upstreamRequest = testDataHelper.createBusinessEligibilityRequest("P_BLN_RBB", null, testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID, 40, 15);

        rulesDto = new RefInstructionRulesDto();
        customerInstruction = new CustomerInstruction();
        productArrangementList = new ArrayList();
        customerInstruction.setEligibilityIndicator(true);
    }

    @Test
    public void testCR053AccountOpeningDateRulePassesAndAccountTypeIsCreditCard() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setArrangementType("CREDIT_CARD");
        productArrangement.setStartDate(testDataHelper.subtractFromCurrentDate(25));
        productArrangementList.add(productArrangement);

        RuleDataHolder ruleDataHolder = new RuleDataHolder();
         ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangementList));
        ruleDataHolder.setRule("CR053");
        ruleDataHolder.setRuleParamValue("28");

        EligibilityDecision evaluate = rule.evaluate(null, ruleDataHolder);

        assertEquals("Customer holds a credit card opened in last " + ruleDataHolder.getRuleParamValue() + " days", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }

    @Test
    public void testCR053AccountOpeningDateRuleFailsAndAccountTypeIsCreditCard() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setArrangementType("CREDIT_CARD");
        productArrangement.setStartDate(testDataHelper.addToCurrentDate(30));
        productArrangementList.add(productArrangement);

        RuleDataHolder ruleDataHolder = new RuleDataHolder();
         ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangementList));
        ruleDataHolder.setRule("CR053");
        ruleDataHolder.setRuleParamValue("28");

        EligibilityDecision evaluate = rule.evaluate(null, ruleDataHolder);

        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());

    }

    @Test
    public void testAccountOpeningDateRulePassesAndAccountTypeIsDefault() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setArrangementType("DEFAULT");
        productArrangementList.add(productArrangement);

        RuleDataHolder ruleDataHolder = new RuleDataHolder();
         ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangementList));
        ruleDataHolder.setRule("default");
        ruleDataHolder.setRuleParamValue("03");
        EligibilityDecision evaluate = rule.evaluate(null, ruleDataHolder);
        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }



}



