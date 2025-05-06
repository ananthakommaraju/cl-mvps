package com.lloydsbanking.salsa.eligibility.service.rules.cst.wz;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lib_sim_bo.businessobjects.InstructionDetails;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class CR049ClubAccountHoldingRuleTest {
    private CR049ClubAccountHoldingRule rule;

    private EligibilityDecision testEligibility;

    @Before
    public void setUp() {
        rule = new CR049ClubAccountHoldingRule();
    }

    @Test
    public void testEvaluateReturnsNull() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        List<ProductArrangement> productArrangements = new ArrayList<>();
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setAssociatedProduct(new Product());
        productArrangement.getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        productArrangement.getAssociatedProduct().getInstructionDetails().setInstructionMnemonic("P_PREM_CLB");
        productArrangements.add(productArrangement);
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        DateFactory dateFactory = new DateFactory();
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd");
        XMLGregorianCalendar date = dateFactory.stringToXMLGregorianCalendar("2015-10-20", sdf);

        testEligibility = rule.evaluate(ruleDataHolder, date, "112233", "334455");

        assertTrue(testEligibility.isEligible());
        assertNull(testEligibility.getReasonText());
    }

    @Test
    public void testEvaluateReturnsDeclineReason() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        List<ProductArrangement> productArrangements = new ArrayList<>();
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setAssociatedProduct(new Product());
        productArrangement.getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        productArrangement.getAssociatedProduct().getInstructionDetails().setInstructionMnemonic("G_ONL_FRTD");
        productArrangements.add(productArrangement);
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        DateFactory dateFactory = new DateFactory();
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd");
        XMLGregorianCalendar date = dateFactory.stringToXMLGregorianCalendar("2015-10-20", sdf);

        testEligibility = rule.evaluate(ruleDataHolder, date, "112233", "334455");

        assertEquals("Customer does not hold Club Account", testEligibility.getReasonText());
        assertFalse(testEligibility.isEligible());
    }
}
