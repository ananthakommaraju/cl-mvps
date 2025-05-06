package com.lloydsbanking.salsa.eligibility.service.rules.agt.wz;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.downstream.MandateAccessDetailsRetriever;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalBusinessException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import lb_gbo_sales.messages.RequestHeader;
import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CR037RelatedEventAndDormantStatusRuleTest {
    CR037RelatedEventAndDormantStatusRule rule;

    RequestHeader header;

    @Before
    public void setUp(){
        rule=new CR037RelatedEventAndDormantStatusRule();
        rule.mandateAccessDetailsRetriever=mock(MandateAccessDetailsRetriever.class);
        header=new TestDataHelper().createEligibilityRequestHeader(com.lloydsbanking.salsa.eligibility.wz.TestDataHelper.TEST_RETAIL_CHANNEL_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_INTERACTION_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_OCIS_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_CUSTOMER_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_CONTACT_POINT_ID);
    }

    @Test
    public void testEvaluateReturnsTrue() throws SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException, EligibilityException {
        RuleDataHolder ruleDataHolder=new RuleDataHolder();
        List<ProductArrangement> productArrangements=new ArrayList<>();
        productArrangements.add(new ProductArrangement());
        productArrangements.get(0).setLifecycleStatus("effective");
        productArrangements.get(0).setAssociatedProduct(new Product());
        productArrangements.get(0).getAssociatedProduct().getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        productArrangements.get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
        productArrangements.get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setProductIdentifier("37412590");
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        ruleDataHolder.setHeader(header);
        ruleDataHolder.setRuleParamValue("37");
        List<String> relatedEvents=new ArrayList<>();
        relatedEvents.add("37");
        when(rule.mandateAccessDetailsRetriever.getRelatedEvents(header, "T37412590")).thenReturn(relatedEvents);

        EligibilityDecision result=rule.evaluate(ruleDataHolder);

        verify(rule.mandateAccessDetailsRetriever).getRelatedEvents(header, "T37412590");
        assertTrue(result.isEligible());
    }

    @Test
    public void testEvaluateReturnsFalseWhenLifeCycleStatusIsDormant() throws SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException, EligibilityException {
        RuleDataHolder ruleDataHolder=new RuleDataHolder();
        List<ProductArrangement> productArrangements=new ArrayList<>();
        productArrangements.add(new ProductArrangement());
        productArrangements.get(0).setLifecycleStatus("Dormant");
        productArrangements.get(0).setAssociatedProduct(new Product());
        productArrangements.get(0).getAssociatedProduct().getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        productArrangements.get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
        productArrangements.get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setProductIdentifier("37412590");
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        ruleDataHolder.setHeader(header);
        ruleDataHolder.setRuleParamValue("37");
        List<String> relatedEvents=new ArrayList<>();
        relatedEvents.add("37");
        when(rule.mandateAccessDetailsRetriever.getRelatedEvents(header, "T37412590")).thenReturn(relatedEvents);

        EligibilityDecision result=rule.evaluate(ruleDataHolder);

        verify(rule.mandateAccessDetailsRetriever).getRelatedEvents(header, "T37412590");
        assertFalse(result.isEligible());
    }

    @Test
    public void testEvaluateReturnsFalseWhenRelatedEventsDoesntContainThreshold() throws SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException, EligibilityException {
        RuleDataHolder ruleDataHolder=new RuleDataHolder();
        List<ProductArrangement> productArrangements=new ArrayList<>();
        productArrangements.add(new ProductArrangement());
        productArrangements.get(0).setLifecycleStatus("effective");
        productArrangements.get(0).setAssociatedProduct(new Product());
        productArrangements.get(0).getAssociatedProduct().getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        productArrangements.get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
        productArrangements.get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setProductIdentifier("37412590");
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        ruleDataHolder.setHeader(header);
        ruleDataHolder.setRuleParamValue("37");
        List<String> relatedEvents=new ArrayList<>();
        relatedEvents.add("40");
        when(rule.mandateAccessDetailsRetriever.getRelatedEvents(header, "T37412590")).thenReturn(relatedEvents);

        EligibilityDecision result=rule.evaluate(ruleDataHolder);

        verify(rule.mandateAccessDetailsRetriever).getRelatedEvents(header, "T37412590");
        assertFalse(result.isEligible());
    }


}
