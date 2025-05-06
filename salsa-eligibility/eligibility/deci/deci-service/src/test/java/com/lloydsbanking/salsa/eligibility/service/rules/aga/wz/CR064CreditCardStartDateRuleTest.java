package com.lloydsbanking.salsa.eligibility.service.rules.aga.wz;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;


@Category(UnitTest.class)
public class CR064CreditCardStartDateRuleTest
{

    RuleDataHolder ruleDataHolder;

    CR064CreditCardStartDateRule rule;

    TestDataHelper dataHelper;

    @Before
    public void setUp() {
        rule = new CR064CreditCardStartDateRule();
        ruleDataHolder = new RuleDataHolder();
        dataHelper = new TestDataHelper();
    }


    @Test
    public void testEvaluateReturnsDeclineReason() throws DatatypeConfigurationException, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        List<ProductArrangement> productArrangements = new ArrayList<>();
        ProductArrangement productArrangement = new ProductArrangement();
        DateFactory dateFactory = new DateFactory();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        productArrangement.setArrangementStartDate(dateFactory.dateToXMLGregorianCalendar(dateFactory.addDays(new Date(), -30)));
        productArrangement.setAssociatedProduct(new Product());
        productArrangement.getAssociatedProduct().setProductType("3");
        productArrangements.add(productArrangement);

        ruleDataHolder.setRuleParamValue("200");
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        EligibilityDecision evaluate = rule.evaluate("G_CREDCARD", ruleDataHolder);
        assertEquals("Customer holds a credit card opened in last 200 days", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());

    }

    @Test
    public void testEvaluateReturnsNull() throws DatatypeConfigurationException, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        List<ProductArrangement> productArrangements = new ArrayList<>();
        ProductArrangement productArrangement = new ProductArrangement();
        DateFactory dateFactory = new DateFactory();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        productArrangement.setArrangementStartDate(dateFactory.dateToXMLGregorianCalendar(dateFactory.addDays(new Date(), -160)));
        productArrangement.setAssociatedProduct(new Product());
        productArrangement.getAssociatedProduct().setProductType("3");
        productArrangements.add(productArrangement);

        ruleDataHolder.setRuleParamValue("150");
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        EligibilityDecision evaluate = rule.evaluate("G_CREDCARD", ruleDataHolder);
        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());

    }
}
