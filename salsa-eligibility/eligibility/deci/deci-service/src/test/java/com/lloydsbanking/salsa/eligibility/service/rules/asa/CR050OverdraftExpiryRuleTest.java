package com.lloydsbanking.salsa.eligibility.service.rules.asa;


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
import lb_gbo_sales.businessobjects.ArrangementIdentifier;
import lb_gbo_sales.businessobjects.CustomerInstruction;
import lb_gbo_sales.businessobjects.OvrdrftDtls;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalBusinessErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsResourceNotAvailableErrorMsg;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

@Category(UnitTest.class)
public class CR050OverdraftExpiryRuleTest {

    DetermineElegibileInstructionsRequest upstreamRequest;

    CustomerInstruction customerInstruction;

    RefInstructionRulesDto rulesDto;

    List<ProductArrangement> productArrangementList;

    private TestDataHelper testDataHelper;

    private CR050OverdraftExpiryRule rule;

    @Before
    public void setUp() throws DatatypeConfigurationException {
        rule = new CR050OverdraftExpiryRule();
        testDataHelper = new TestDataHelper();
        upstreamRequest = testDataHelper.createBusinessEligibilityRequest("P_BLN_RBB", null, testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID, 40, 15);

        rulesDto = new RefInstructionRulesDto();
        customerInstruction = new CustomerInstruction();
        productArrangementList = new ArrayList();
        customerInstruction.setEligibilityIndicator(true);
    }

    @Test
    public void testCR050OverdraftExpiryRuleFailsForLessThanParamDaysOne() throws DatatypeConfigurationException, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {


        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setSortCode("770908");
        productArrangement.setAccountNumber("22562768");

        OvrdrftDtls ovrdrftDtls = new OvrdrftDtls();
        ovrdrftDtls.setStartDate(testDataHelper.subtractFromCurrentDate(25));
        ovrdrftDtls.setEndDate(testDataHelper.subtractFromCurrentDate(25));
        productArrangement.setOvrdrftDtls(ovrdrftDtls);

        productArrangementList.add(productArrangement);

        ArrangementIdentifier arrangementIdentifier = new ArrangementIdentifier();
        arrangementIdentifier.setAccNum("22562768");
        arrangementIdentifier.setSortCode("770908");

        RuleDataHolder ruleDataHolder = new RuleDataHolder();

        ruleDataHolder.setRule("CR050");
        ruleDataHolder.setRuleParamValue("28:75");
        ruleDataHolder.setArrangementIdentifier(arrangementIdentifier);
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangementList));
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertEquals("Overdraft has been applied in last 28 days on the account", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }

    @Test
    public void testCR050OverdraftExpiryRulePassesForLessThanParamDaysOne() throws DatatypeConfigurationException, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {


        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setSortCode("770908");
        productArrangement.setAccountNumber("22562768");

        OvrdrftDtls ovrdrftDtls = new OvrdrftDtls();
        ovrdrftDtls.setStartDate(testDataHelper.addToCurrentDate(31));
        ovrdrftDtls.setEndDate(testDataHelper.addToCurrentDate(77));
        productArrangement.setOvrdrftDtls(ovrdrftDtls);

        productArrangementList.add(productArrangement);

        ArrangementIdentifier arrangementIdentifier = new ArrangementIdentifier();
        arrangementIdentifier.setAccNum("22562768");
        arrangementIdentifier.setSortCode("770908");

        RuleDataHolder ruleDataHolder = new RuleDataHolder();

        ruleDataHolder.setRule("CR050");
        ruleDataHolder.setRuleParamValue("28:75");
        ruleDataHolder.setArrangementIdentifier(arrangementIdentifier);
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangementList));
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);

        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

    @Test
    public void testCR050OverdraftExpiryRuleFails() throws DatatypeConfigurationException, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {


        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setSortCode("770908");
        productArrangement.setAccountNumber("22562768");

        OvrdrftDtls ovrdrftDtls = new OvrdrftDtls();
        ovrdrftDtls.setStartDate(testDataHelper.addToCurrentDate(35));
        ovrdrftDtls.setEndDate(testDataHelper.subtractFromCurrentDate(70));
        productArrangement.setOvrdrftDtls(ovrdrftDtls);

        productArrangementList.add(productArrangement);

        ArrangementIdentifier arrangementIdentifier = new ArrangementIdentifier();
        arrangementIdentifier.setAccNum("22562768");
        arrangementIdentifier.setSortCode("770908");

        RuleDataHolder ruleDataHolder = new RuleDataHolder();

        ruleDataHolder.setRule("CR050");
        ruleDataHolder.setRuleParamValue("28:75");
        ruleDataHolder.setArrangementIdentifier(arrangementIdentifier);
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangementList));

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertEquals("Overdraft is expiring in less than 75 days on the account", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }

    @Test
    public void testCR050OverdraftExpiryRulePasses() throws DatatypeConfigurationException, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {


        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setSortCode("770908");
        productArrangement.setAccountNumber("22562768");

        OvrdrftDtls ovrdrftDtls = new OvrdrftDtls();
        ovrdrftDtls.setStartDate(testDataHelper.addToCurrentDate(31));
        ovrdrftDtls.setEndDate(testDataHelper.addToCurrentDate(77));
        productArrangement.setOvrdrftDtls(ovrdrftDtls);

        productArrangementList.add(productArrangement);

        ArrangementIdentifier arrangementIdentifier = new ArrangementIdentifier();
        arrangementIdentifier.setAccNum("22562768");
        arrangementIdentifier.setSortCode("770908");


        RuleDataHolder ruleDataHolder = new RuleDataHolder();

        ruleDataHolder.setRule("CR050");
        ruleDataHolder.setRuleParamValue("28:75");
        ruleDataHolder.setArrangementIdentifier(arrangementIdentifier);
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangementList));

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

    @Test
    public void testCR050OverdraftExpiryRuleFailsForLessThanParamDaysTwo() throws DatatypeConfigurationException, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {


        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setSortCode("770908");
        productArrangement.setAccountNumber("22562768");

        OvrdrftDtls ovrdrftDtls = new OvrdrftDtls();
        ovrdrftDtls.setStartDate(testDataHelper.addToCurrentDate(35));
        ovrdrftDtls.setEndDate(testDataHelper.addToCurrentDate(74));
        productArrangement.setOvrdrftDtls(ovrdrftDtls);

        productArrangementList.add(productArrangement);

        ArrangementIdentifier arrangementIdentifier = new ArrangementIdentifier();
        arrangementIdentifier.setAccNum("22562768");
        arrangementIdentifier.setSortCode("770908");

        RuleDataHolder ruleDataHolder = new RuleDataHolder();

        ruleDataHolder.setRule("CR050");
        ruleDataHolder.setRuleParamValue("28:75");
        ruleDataHolder.setArrangementIdentifier(arrangementIdentifier);
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangementList));

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertEquals("Overdraft is expiring in less than 75 days on the account", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }

    @Test
    public void testCR050OverdraftExpiryRulePassesForLessThanParamDaysTwo() throws DatatypeConfigurationException, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {


        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setSortCode("770908");
        productArrangement.setAccountNumber("22562768");

        OvrdrftDtls ovrdrftDtls = new OvrdrftDtls();
        ovrdrftDtls.setStartDate(testDataHelper.addToCurrentDate(31));
        ovrdrftDtls.setEndDate(testDataHelper.addToCurrentDate(78));
        productArrangement.setOvrdrftDtls(ovrdrftDtls);

        productArrangementList.add(productArrangement);

        ArrangementIdentifier arrangementIdentifier = new ArrangementIdentifier();
        arrangementIdentifier.setAccNum("22562768");
        arrangementIdentifier.setSortCode("770908");
        RuleDataHolder ruleDataHolder = new RuleDataHolder();

        ruleDataHolder.setRule("CR050");
        ruleDataHolder.setRuleParamValue("28:75");
        ruleDataHolder.setArrangementIdentifier(arrangementIdentifier);
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangementList));

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }
}

