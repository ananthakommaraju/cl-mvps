package com.lloydsbanking.salsa.eligibility.service.rules.agt.wz;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.header.GmoToGboRequestHeaderConverter;
import lb_gbo_sales.messages.RequestHeader;
import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
import lib_sim_bo.businessobjects.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class CR024CAAndLoanHoldingRuleTest {
    CR024CAAndLoanHoldingRule rule;

    RuleDataHolder ruleDataHolder;

    lib_sim_gmo.messages.RequestHeader header;

    GmoToGboRequestHeaderConverter headerConverter;

    RequestHeader gboHeader;

    com.lloydsbanking.salsa.eligibility.wz.TestDataHelper dataHelper;

    @Before
    public void setUp() {
        rule = new CR024CAAndLoanHoldingRule();
        ruleDataHolder = new RuleDataHolder();
        dataHelper = new com.lloydsbanking.salsa.eligibility.wz.TestDataHelper();
        header = dataHelper.createEligibilityRequestHeader(com.lloydsbanking.salsa.eligibility.wz.TestDataHelper.TEST_RETAIL_CHANNEL_ID, com.lloydsbanking.salsa.eligibility.wz.TestDataHelper.TEST_INTERACTION_ID, com.lloydsbanking.salsa.eligibility.wz.TestDataHelper.TEST_OCIS_ID, "542107294", com.lloydsbanking.salsa.eligibility.wz.TestDataHelper.TEST_CONTACT_POINT_ID);
        this.headerConverter = new GmoToGboRequestHeaderConverter();
        this.gboHeader = headerConverter.convert(header);

    }

    @Test
    public void testEvaluateWhenReturnNull() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, DatatypeConfigurationException, SalsaExternalServiceException, EligibilityException {
        ruleDataHolder.setHeader(gboHeader);
        List<lib_sim_bo.businessobjects.ProductArrangement> productArrangements = new ArrayList<>();
        lib_sim_bo.businessobjects.ProductArrangement productArrangement = new lib_sim_bo.businessobjects.ProductArrangement();
        productArrangement.setAssociatedProduct(new Product());
        productArrangement.getAssociatedProduct().setProductType("1");
        productArrangement.getAssociatedProduct().getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        productArrangement.getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
        productArrangements.add(productArrangement);

        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertTrue(evaluate.isEligible());
        assertNull(evaluate.getReasonText());


    }

    @Test
    public void testEvaluateWhenReturnDeclineReason() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, DatatypeConfigurationException, SalsaExternalServiceException, EligibilityException {
        ruleDataHolder.setHeader(gboHeader);
        List<lib_sim_bo.businessobjects.ProductArrangement> productArrangements = new ArrayList<>();
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertEquals("Customer doesn\u0092t have current account of logged in Channel and  has a no loan", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }

}




