package com.lloydsbanking.salsa.eligibility.service.rules.cst.wz;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.Individual;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertEquals;

@Category(UnitTest.class)
public class CR039NationalityOfCustomerRuleTest {
    private RuleDataHolder ruleDataHolder;

    private CR039NationalityOfCustomerRule rule;

    private EligibilityDecision testEligibility;

    @Before
    public void setUp() {
        ruleDataHolder = new RuleDataHolder();
        rule = new CR039NationalityOfCustomerRule();

    }

    @Test
    public void testEvaluateReturnsNull() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        ruleDataHolder.setCustomerDetails(new Customer());
        ruleDataHolder.getCustomerDetails().setIsPlayedBy(new Individual());
        ruleDataHolder.getCustomerDetails().getIsPlayedBy().setNationality("individual");
        ruleDataHolder.setRuleParamValue("AGF");
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(new ArrayList<ProductArrangement>()));
        testEligibility = rule.evaluate(ruleDataHolder, null, "112233", "334455");
        assertNull(testEligibility.getReasonText());
    }

    @Test
    public void testEvaluateReturnsDeclineReason() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        ruleDataHolder.setCustomerDetails(new Customer());
        ruleDataHolder.getCustomerDetails().setIsPlayedBy(new Individual());
        ruleDataHolder.getCustomerDetails().getIsPlayedBy().setNationality("AFG");
        ruleDataHolder.setRuleParamValue("AFG");
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(new ArrayList<ProductArrangement>()));
        testEligibility = rule.evaluate(ruleDataHolder, null, "112233", "334455");
        assertEquals("nationality is not allowed", testEligibility.getReasonText());
    }
}
