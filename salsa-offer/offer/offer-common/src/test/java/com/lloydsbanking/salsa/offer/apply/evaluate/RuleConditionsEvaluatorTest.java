package com.lloydsbanking.salsa.offer.apply.evaluate;

import com.lloydsbanking.salsa.UnitTest;
import lib_sim_bo.businessobjects.CurrencyAmount;
import lib_sim_bo.businessobjects.OverdraftDetails;
import lib_sim_bo.businessobjects.ProductOptions;
import lib_sim_bo.businessobjects.RuleCondition;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@Category(UnitTest.class)
public class RuleConditionsEvaluatorTest {
    private RuleConditionsEvaluator ruleConditionsEvaluator;

    @Before
    public void setUp() {
        ruleConditionsEvaluator = new RuleConditionsEvaluator();
    }

    @Test
    public void testSetFacilitiesOfferedForChequeBookOffered() {
        List<ProductOptions> productOptionsList = new ArrayList<>();
        ProductOptions productOptions = new ProductOptions();
        productOptions.setOptionsCode("CHECK_BOOK_OFFERED_FLAG");
        productOptions.setOptionsType("a");
        productOptions.setOptionsValue("10");
        productOptionsList.add(productOptions);

        ProductOptions productOptions1 = new ProductOptions();
        productOptions1.setOptionsCode("2");
        productOptions1.setOptionsType("b");
        productOptions1.setOptionsValue("11");
        productOptionsList.add(productOptions1);

        OverdraftDetails overdraftDetails = new OverdraftDetails();
        CurrencyAmount value = new CurrencyAmount();
        value.setAmount(BigDecimal.valueOf(100));
        overdraftDetails.setAmount(value);

        List<RuleCondition> ruleConditionList = new ArrayList<>();
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("ruleCond");
        ruleConditionList.add(ruleCondition);

        String overdraftLimit = "100";

        boolean isOverdraftRequired = ruleConditionsEvaluator.setFacilitiesOffered(productOptionsList, overdraftDetails, ruleConditionList, overdraftLimit);

        assertFalse(isOverdraftRequired);
        assertEquals("ruleCond", ruleConditionList.get(0).getName());
        assertEquals("CHECK_BOOK_OFFERED_FLAG", ruleConditionList.get(1).getName());
    }

    @Test
    public void testSetFacilitiesOfferedWhenOdLmtIsNotNull() {
        List<ProductOptions> productOptionsList = new ArrayList<>();
        ProductOptions productOptions = new ProductOptions();
        productOptions.setOptionsCode("CHECK_BOOK_OFFERED_FLAG");
        productOptions.setOptionsType("a");
        productOptions.setOptionsValue("10");
        productOptionsList.add(productOptions);

        ProductOptions productOptions1 = new ProductOptions();
        productOptions1.setOptionsCode("OVERDRAFT_OFFERED_FLAG");
        productOptions1.setOptionsType("b");
        productOptions1.setOptionsValue("101");
        productOptionsList.add(productOptions1);

        OverdraftDetails overdraftDetails = new OverdraftDetails();
        CurrencyAmount value = new CurrencyAmount();
        value.setAmount(BigDecimal.valueOf(100));
        overdraftDetails.setAmount(value);

        List<RuleCondition> ruleConditionList = new ArrayList<>();
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("ruleCond");
        ruleConditionList.add(ruleCondition);

        String overdraftLimit = "100";

        boolean isOverdraftRequired = ruleConditionsEvaluator.setFacilitiesOffered(productOptionsList, overdraftDetails, ruleConditionList, overdraftLimit);

        assertTrue(isOverdraftRequired);
        assertEquals("ruleCond", ruleConditionList.get(0).getName());
        assertEquals("CHECK_BOOK_OFFERED_FLAG", ruleConditionList.get(1).getName());
        assertEquals(BigDecimal.valueOf(101), overdraftDetails.getAmount().getAmount());
    }

    @Test
    public void testSetFacilitiesOfferedForCreditCardLimitAmount() {
        List<ProductOptions> productOptionsList = new ArrayList<>();
        ProductOptions productOptions = new ProductOptions();
        productOptions.setOptionsCode("aaa");
        productOptions.setOptionsType("a");
        productOptions.setOptionsValue("10");
        productOptionsList.add(productOptions);

        ProductOptions productOptions1 = new ProductOptions();
        productOptions1.setOptionsCode("CREDIT_CARD_OFFERED_FLAG");
        productOptions1.setOptionsType("b");
        productOptions1.setOptionsValue("101");
        productOptionsList.add(productOptions1);

        OverdraftDetails overdraftDetails = new OverdraftDetails();
        CurrencyAmount value = new CurrencyAmount();
        value.setAmount(BigDecimal.valueOf(100));
        overdraftDetails.setAmount(value);

        List<RuleCondition> ruleConditionList = new ArrayList<>();
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("ruleCond");
        ruleConditionList.add(ruleCondition);

        String overdraftLimit = "100";

        boolean isOverdraftRequired = ruleConditionsEvaluator.setFacilitiesOffered(productOptionsList, overdraftDetails, ruleConditionList, overdraftLimit);

        assertFalse(isOverdraftRequired);
        assertEquals("ruleCond", ruleConditionList.get(0).getName());
        assertEquals("CHECK_BOOK_OFFERED_FLAG", ruleConditionList.get(1).getName());
        assertEquals("CREDIT_CARD_LIMIT_AMOUNT", ruleConditionList.get(2).getName());
        assertEquals(BigDecimal.valueOf(100), overdraftDetails.getAmount().getAmount());
    }

    @Test
    public void testSetFacilitiesOfferedForDebitCardRiskCode() {
        List<ProductOptions> productOptionsList = new ArrayList<>();
        ProductOptions productOptions = new ProductOptions();
        productOptions.setOptionsCode("DEBIT_CARD_RISK_CODE");
        productOptions.setOptionsType("a");
        productOptions.setOptionsValue("10");
        productOptionsList.add(productOptions);

        List<RuleCondition> ruleConditionList = new ArrayList<>();
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("ruleCond");
        ruleConditionList.add(ruleCondition);

        OverdraftDetails overdraftDetails = new OverdraftDetails();
        CurrencyAmount value = new CurrencyAmount();
        value.setAmount(BigDecimal.valueOf(100));
        overdraftDetails.setAmount(value);

        String overdraftLimit = "100";

        boolean isOverdraftRequired = ruleConditionsEvaluator.setFacilitiesOffered(productOptionsList, overdraftDetails, ruleConditionList, overdraftLimit);

        assertFalse(isOverdraftRequired);
        assertEquals("ruleCond", ruleConditionList.get(0).getName());
        assertEquals(2, ruleConditionList.size());
        assertEquals("DEBIT_CARD_RISK_CODE", ruleConditionList.get(1).getName());

    }


}
