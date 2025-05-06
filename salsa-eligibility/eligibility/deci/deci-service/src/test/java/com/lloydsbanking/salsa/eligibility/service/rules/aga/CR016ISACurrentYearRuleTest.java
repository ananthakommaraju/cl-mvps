package com.lloydsbanking.salsa.eligibility.service.rules.aga;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.businessobjects.CustomerInstruction;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import lb_gbo_sales.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;


@Category(UnitTest.class)
public class CR016ISACurrentYearRuleTest {

    CR016ISACurrentYearRule rule;

    TestDataHelper testDataHelper;

    DetermineElegibileInstructionsRequest upstreamRequest;

    RequestHeader header;


    CustomerInstruction customerInstruction;

    List<ProductArrangement> customerArrangements;

    ProductArrangement productArrangement = null;


    @Before
    public void setUp() {
        rule = new CR016ISACurrentYearRule();
        testDataHelper = new TestDataHelper();
        upstreamRequest = testDataHelper.createEligibilityRequest("abc", testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);
        header = upstreamRequest.getHeader();
        customerInstruction = new CustomerInstruction();
        customerInstruction.setEligibilityIndicator(true);
        customerArrangements = new ArrayList<>();
        productArrangement = new ProductArrangement();
        productArrangement.setInstructionMnemonic("P_CASH_ISA");
        productArrangement.setParentInstructionMnemonic("G_ISA");
        customerArrangements.add(productArrangement);

    }

    @Test
    public void testCR016ISACurrentYearRuleTestFails() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        {

            Calendar today = Calendar.getInstance();
            productArrangement.setStartDate(testDataHelper.createXMLGregorianCalendar(today.get(Calendar.YEAR), 4, 7));

            RuleDataHolder ruleDataHolder = new RuleDataHolder();
            ruleDataHolder.setRule("CR016");
            ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(customerArrangements));
            ruleDataHolder.setHeader(header);
            EligibilityDecision evaluate = rule.evaluate("can", ruleDataHolder);


            assertEquals("ISA opened this year", evaluate.getReasonText());
            assertFalse(evaluate.isEligible());

        }
    }

    @Test
    public void testCR016ISACurrentYearRuleTestPasses() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        Calendar today = Calendar.getInstance();
        productArrangement.setStartDate(testDataHelper.createXMLGregorianCalendar(today.get(Calendar.YEAR - 1), 4, 4));

        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule("CR016");
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(customerArrangements));
        ruleDataHolder.setHeader(header);
        EligibilityDecision evaluate = rule.evaluate("can", ruleDataHolder);

        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());

    }
}
