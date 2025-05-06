package com.lloydsbanking.salsa.activate.communication.convert;

import com.lloydsbanking.salsa.UnitTest;
import lib_sim_bo.businessobjects.InformationContent;
import lib_sim_bo.businessobjects.Rates;
import lib_sim_bo.businessobjects.RuleCondition;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class InformationContentFactoryTest {

    private InformationContentFactory informationContentFactory;

    @Before
    public void setUp() {
        informationContentFactory = new InformationContentFactory();
    }

    @Test
    public void testGetInformationContent() {
        InformationContent informationContent = informationContentFactory.getInformationContent("125", "548", 1);
        assertEquals("125", informationContent.getKey());
        assertEquals("548", informationContent.getValue());
        assertEquals("1", informationContent.getOrder().toString());
    }

    @Test
    public void testContainsContentKey() {
        List<InformationContent> informationContentList = new ArrayList<>();
        InformationContent informationContent = new InformationContent();
        informationContent.setKey("125");
        informationContentList.add(informationContent);
        assertTrue(informationContentFactory.containsContentKey("125", informationContentList));
    }

    @Test
    public void testGetInformationContentIfValueNotNull() {
        List<InformationContent> informationContents = informationContentFactory.getInformationContentIfNotNull("125", "548");
        assertEquals("125", informationContents.get(0).getKey());
        assertEquals("548", informationContents.get(0).getValue());
        assertEquals("0", informationContents.get(0).getOrder().toString());
    }

    @Test
    public void testGetInformationContentIfValueNull() {
        List<InformationContent> informationContents = informationContentFactory.getInformationContentIfNotNull("125", null);
        assertTrue(informationContents.isEmpty());
    }

    @Test
    public void testGetFeeOverdraftRateInformationContent() {
        List<Rates> ratesList = new ArrayList<>();
        ratesList.add(new Rates());
        ratesList.get(0).setType("INT_FREE_OVERDRAFT");
        ratesList.get(0).setValue(BigDecimal.TEN);
        List<InformationContent> informationContentList = informationContentFactory.getFeeOverdraftRateInformationContent(ratesList);
        assertEquals("10", informationContentList.get(0).getValue());
        assertEquals("IB.Product.PCA.ODInterestFreeAmount", informationContentList.get(0).getKey());
        assertEquals((long) 0, (long) informationContentList.get(0).getOrder());
    }

    @Test
    public void testGetBenefitMessagesInformationContent() {
        List<RuleCondition> ruleConditionList = new ArrayList<>();
        ruleConditionList.add(new RuleCondition());
        ruleConditionList.get(0).setName("ALERT_MSGES");
        ruleConditionList.get(0).setResult("ResultWhenAlert");
        ruleConditionList.add(new RuleCondition());
        ruleConditionList.get(1).setName("HECP");
        ruleConditionList.get(1).setResult("ResultWhenNotAlert");

        List<InformationContent> informationContents = informationContentFactory.getBenefitMessagesInformationContent(ruleConditionList);
        assertEquals("IB.Product.PCA.HECP", informationContents.get(0).getValue());
        assertEquals("IB.Product.PCA.HECP", informationContents.get(0).getKey());
        assertEquals((long) 0, (long) informationContents.get(0).getOrder());
        assertEquals("IB.Product.PCA.ResultWhenAlert", informationContents.get(1).getValue());
        assertEquals("IB.Product.PCA.ResultWhenAlert", informationContents.get(1).getKey());
        assertEquals((long) 0, (long) informationContents.get(1).getOrder());
    }

}
