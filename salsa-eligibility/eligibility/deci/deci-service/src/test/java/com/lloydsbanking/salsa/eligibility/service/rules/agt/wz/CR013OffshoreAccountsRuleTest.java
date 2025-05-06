package com.lloydsbanking.salsa.eligibility.service.rules.agt.wz;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.Organisation;
import lib_sim_bo.businessobjects.OrganisationUnit;
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
public class CR013OffshoreAccountsRuleTest {
    CR013OffshoreAccountsRule rule;

    RuleDataHolder ruleDataHolder;


    @Before
    public void setUp() {
        rule = new CR013OffshoreAccountsRule();
        ruleDataHolder = new RuleDataHolder();
    }

    @Test
    public void testEvaluateWhenReturnNull() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(new ArrayList<ProductArrangement>()));
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

    @Test
    public void testEvaluateWhenCustomerSegmentIs3() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        List<ProductArrangement> productArrangements = new ArrayList<>();
        productArrangements.add(new ProductArrangement());
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        Customer customer = new Customer();
        customer.setCustomerSegment("3");
        ruleDataHolder.setCustomerDetails(customer);
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

    @Test
    public void testEvaluateForHasOnlyOffshoreAccountsRetrunsFalse() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        List<ProductArrangement> productArrangements = new ArrayList<>();
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setFinancialInstitution(new Organisation());
        productArrangement.getFinancialInstitution().getHasOrganisationUnits().add(new OrganisationUnit());
        productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).setSortCode("301642");
        productArrangements.add(productArrangement);

        ruleDataHolder.setProductArrangements((ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements)));
        ruleDataHolder.setRuleParamValue("301642");
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertEquals("Customer only has off-shore accounts", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());

    }

    @Test
    public void testEvaluateForHasOnlyOffshoreAccountsReturnsNull() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        List<ProductArrangement> productArrangements = new ArrayList<>();
        ProductArrangement productArrangement = new DepositArrangement();
        productArrangement.setFinancialInstitution(new Organisation());
        productArrangement.getFinancialInstitution().getHasOrganisationUnits().add(new OrganisationUnit());
        productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).setSortCode("301642");
        productArrangements.add(productArrangement);

        ruleDataHolder.setProductArrangements((ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements)));
        ruleDataHolder.setRuleParamValue("10245");
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertTrue(evaluate.isEligible());
        assertNull(evaluate.getReasonText());

    }

}
