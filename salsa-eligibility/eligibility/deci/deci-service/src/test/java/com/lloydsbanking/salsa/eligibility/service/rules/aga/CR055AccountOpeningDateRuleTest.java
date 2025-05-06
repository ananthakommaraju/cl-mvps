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
import lb_gbo_sales.businessobjects.OvrdrftDtls;
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
public class CR055AccountOpeningDateRuleTest {
    private TestDataHelper testDataHelper;

    private CR055AccountOpeningDateRule rule;

    DetermineElegibileInstructionsRequest upstreamRequest;

    List<ProductArrangement> productArrangementList;

    @Before
    public void setUp() throws DatatypeConfigurationException {
        rule = new CR055AccountOpeningDateRule();
        testDataHelper = new TestDataHelper();
        upstreamRequest = testDataHelper.createBusinessEligibilityRequest("P_BLN_RBB", null, testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID, 40, 15);
        productArrangementList = new ArrayList();
    }

    @Test
    public void testCR055AccountOpeningDateRulePassesAndAccountTypeIsCurrent() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setArrangementType("CURRENT");
        OvrdrftDtls ovrdrftDtls = new OvrdrftDtls();
        ovrdrftDtls.setStartDate(testDataHelper.subtractFromCurrentDate(26));
        productArrangement.setOvrdrftDtls(ovrdrftDtls);
        productArrangementList.add(productArrangement);


        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangementList));
        ruleDataHolder.setRule("CR055");
        ruleDataHolder.setRuleParamValue("28");
        EligibilityDecision evaluate = rule.evaluate(null, ruleDataHolder);

        assertEquals("Customer holds a current account having overdraft opened in last " + ruleDataHolder.getRuleParamValue() + " days", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }

    @Test
    public void testCR055AccountOpeningDateRuleAndAccountTypeIsCurrent() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setArrangementType("CURRENT");
        OvrdrftDtls ovrdrftDtls = new OvrdrftDtls();
        ovrdrftDtls.setStartDate(testDataHelper.addToCurrentDate(35));
        productArrangement.setOvrdrftDtls(ovrdrftDtls);
        productArrangementList.add(productArrangement);

        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangementList));
        ruleDataHolder.setRule("CR055");
        ruleDataHolder.setRuleParamValue("28");
        EligibilityDecision evaluate = rule.evaluate(null, ruleDataHolder);
        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

    @Test
    public void testCR055AccountOpeningDateRulePassesAndAccountTypeIsCurrentAccount() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setArrangementType("CURRENT");
        OvrdrftDtls ovrdrftDtls = new OvrdrftDtls();
        ovrdrftDtls.setStartDate(testDataHelper.addToCurrentDate(30));
        productArrangement.setOvrdrftDtls(ovrdrftDtls);
        productArrangementList.add(productArrangement);

        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangementList));
        ruleDataHolder.setRule("CR055");
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



