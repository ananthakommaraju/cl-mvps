package com.lloydsbanking.salsa.eligibility.service.rules.aga.wz;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.prd.model.RefInstructionRulesPrdDto;
import com.lloydsbanking.salsa.eligibility.service.downstream.EligibilityPRDRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import lib_sim_bo.businessobjects.InstructionDetails;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CR043MaxInstanceOfProductsForGivenInstructionsRuleTest {

    public static final String CANDIDATE_INSTRUCTION = "P_CLSCVTG";

    public static final String BRAND_CODE = "IBL";

    public static final String CR_043 = "CR043";

    private String thresholdMnemonic = "P_CLSCVTG:P_SLVRVTG:P_GOLDVTG:P_PLATVTG";

    CR043MaxInstanceOfProductsForGivenInstructionsRule cr043MaxInstanceOfProductsForGivenInstructionsRule;

    TestDataHelper testDataHelper;

    private EligibilityPRDRetriever mockEligibilityPRDRetriever;

    private BigDecimal sequenceParam;

    private ArrayList<ProductArrangementFacade> productArrangementFacades;

    private RuleDataHolder ruleDataHolder;

    private ArrayList<ProductArrangement> productArrangements;

    private ProductArrangement productArrangement;

    private Product product;

    @Before
    public void setUp() {
        cr043MaxInstanceOfProductsForGivenInstructionsRule = new CR043MaxInstanceOfProductsForGivenInstructionsRule();
        testDataHelper = new TestDataHelper();
        mockEligibilityPRDRetriever = mock(EligibilityPRDRetriever.class);
        cr043MaxInstanceOfProductsForGivenInstructionsRule.eligibilityPRDRetriever = mockEligibilityPRDRetriever;
        productArrangements = new ArrayList();
        productArrangementFacades = new ArrayList();
        productArrangement = mock(ProductArrangement.class);
        product = mock(Product.class);
    }

    @Test
    public void shouldReturnEligibilityTrueWhenCustomerHasNoExistingArrangments() throws Exception {

        RuleDataHolder ruleDataHolder = mock(RuleDataHolder.class);
        when(ruleDataHolder.getProductArrangements()).thenReturn(productArrangementFacades);

        EligibilityDecision evaluate = cr043MaxInstanceOfProductsForGivenInstructionsRule.evaluate(CANDIDATE_INSTRUCTION, ruleDataHolder);
        assertTrue(evaluate.isEligible());
        assertNull(evaluate.getReasonText());

        verify(ruleDataHolder).getProductArrangements();
    }

    @Test
    public void shouldReturnEligibilityFalseWhenCustomerExceedsMaximumProductHoldingUsingASingleProductType() throws Exception {
        String thresholdCount = "3";
        RuleDataHolder ruleDataHolder = mock(RuleDataHolder.class);
        when(ruleDataHolder.getChannel()).thenReturn(BRAND_CODE);
        when(ruleDataHolder.getRule()).thenReturn(CR_043);
        addToListOfArrangements(createMockProduct("P_CLSCVTG"), 4);
        when(ruleDataHolder.getProductArrangements()).thenReturn(productArrangementFacades);

        ArrayList<RefInstructionRulesPrdDto> refInstructionRulesPrdDtos = mockRefInstructionRulesPrdDtos(thresholdCount);

        when(mockEligibilityPRDRetriever.getCompositeInstructionConditions(CANDIDATE_INSTRUCTION, BRAND_CODE, new RequestHeader(), CANDIDATE_INSTRUCTION)).thenReturn(refInstructionRulesPrdDtos);

        EligibilityDecision evaluate = cr043MaxInstanceOfProductsForGivenInstructionsRule.evaluate(CANDIDATE_INSTRUCTION, ruleDataHolder);
        assertFalse(evaluate.isEligible());
        assertEquals("Customer cannot have more than " + thresholdCount + "products for group of mnemonics defined (cr 043 rule)", evaluate.getReasonText());
        verify(productArrangement, times(9)).getAssociatedProduct();
        verify(product, times(6)).getInstructionDetails();
    }

    @Test
    public void shouldReturnEligibilityFalseWhenCustomerExceedsMaximumProductHoldingUsingTwoProductType() throws Exception {
        String thresholdCount = "3";
        ruleDataHolder = mock(RuleDataHolder.class);
        when(ruleDataHolder.getChannel()).thenReturn(BRAND_CODE);
        when(ruleDataHolder.getRule()).thenReturn(CR_043);
        addToListOfArrangements(createMockProduct("P_CLSCVTG"), 2);
        addToListOfArrangements(createMockProduct("P_SLVRVTG"), 2);
        when(ruleDataHolder.getProductArrangements()).thenReturn(productArrangementFacades);

        ArrayList<RefInstructionRulesPrdDto> refInstructionRulesPrdDtos = mockRefInstructionRulesPrdDtos(thresholdCount);

        when(mockEligibilityPRDRetriever.getCompositeInstructionConditions(CANDIDATE_INSTRUCTION, BRAND_CODE, new RequestHeader(), CANDIDATE_INSTRUCTION)).thenReturn(refInstructionRulesPrdDtos);

        EligibilityDecision evaluate = cr043MaxInstanceOfProductsForGivenInstructionsRule.evaluate(CANDIDATE_INSTRUCTION, ruleDataHolder);
        assertFalse(evaluate.isEligible());
        assertEquals("Customer cannot have more than " + thresholdCount + "products for group of mnemonics defined (cr 043 rule)", evaluate.getReasonText());
        verify(productArrangement, times(9)).getAssociatedProduct();
        verify(product, times(6)).getInstructionDetails();
    }

    @Test
    public void shouldReturnEligibilityFalseWhenCustomerHasProductsEqualToMaximumThresholdUsingTwoProductType() throws Exception {
        String thresholdCount = "3";
        ruleDataHolder = mock(RuleDataHolder.class);
        when(ruleDataHolder.getChannel()).thenReturn(BRAND_CODE);
        when(ruleDataHolder.getRule()).thenReturn(CR_043);
        addToListOfArrangements(createMockProduct("P_CLSCVTG"), 1);
        addToListOfArrangements(createMockProduct("P_SLVRVTG"), 2);
        when(ruleDataHolder.getProductArrangements()).thenReturn(productArrangementFacades);

        ArrayList<RefInstructionRulesPrdDto> refInstructionRulesPrdDtos = mockRefInstructionRulesPrdDtos(thresholdCount);

        when(mockEligibilityPRDRetriever.getCompositeInstructionConditions(CANDIDATE_INSTRUCTION, BRAND_CODE, new RequestHeader(), CANDIDATE_INSTRUCTION)).thenReturn(refInstructionRulesPrdDtos);

        EligibilityDecision evaluate = cr043MaxInstanceOfProductsForGivenInstructionsRule.evaluate(CANDIDATE_INSTRUCTION, ruleDataHolder);
        assertFalse(evaluate.isEligible());
        assertEquals("Customer cannot have more than " + thresholdCount + "products for group of mnemonics defined (cr 043 rule)", evaluate.getReasonText());
        verify(productArrangement, times(9)).getAssociatedProduct();
        verify(product, times(6)).getInstructionDetails();
    }

    @Test
    public void shouldReturnEligibilityTrueWhenCustomerHasProductsHoldingLessThanThresholdUsingTwoProductType() throws Exception {
        String thresholdCount = "3";
        ruleDataHolder = mock(RuleDataHolder.class);
        when(ruleDataHolder.getChannel()).thenReturn(BRAND_CODE);
        when(ruleDataHolder.getRule()).thenReturn(CR_043);
        addToListOfArrangements(createMockProduct("P_CLSCVTG"), 1);
        addToListOfArrangements(createMockProduct("P_SLVRVTG"), 1);
        when(ruleDataHolder.getProductArrangements()).thenReturn(productArrangementFacades);

        ArrayList<RefInstructionRulesPrdDto> refInstructionRulesPrdDtos = mockRefInstructionRulesPrdDtos(thresholdCount);

        when(mockEligibilityPRDRetriever.getCompositeInstructionConditions(CANDIDATE_INSTRUCTION, BRAND_CODE, new RequestHeader(), CANDIDATE_INSTRUCTION)).thenReturn(refInstructionRulesPrdDtos);

        EligibilityDecision evaluate = cr043MaxInstanceOfProductsForGivenInstructionsRule.evaluate(CANDIDATE_INSTRUCTION, ruleDataHolder);
        assertTrue(evaluate.isEligible());
        assertNull(evaluate.getReasonText());
        verify(productArrangement, times(6)).getAssociatedProduct();
        verify(product, times(4)).getInstructionDetails();
    }

    @Test
    public void shouldReturnEligibilityTrueWhenEmptyListForRulesIsReturned() throws Exception {
        ruleDataHolder = mock(RuleDataHolder.class);
        when(ruleDataHolder.getChannel()).thenReturn(BRAND_CODE);
        when(ruleDataHolder.getRule()).thenReturn(CR_043);
        addToListOfArrangements(createMockProduct("P_CLSCVTG"), 1);
        addToListOfArrangements(createMockProduct("P_SLVRVTG"), 1);
        when(ruleDataHolder.getProductArrangements()).thenReturn(productArrangementFacades);

        when(mockEligibilityPRDRetriever.getCompositeInstructionConditions(CANDIDATE_INSTRUCTION, BRAND_CODE, new RequestHeader(), CANDIDATE_INSTRUCTION)).thenReturn(new ArrayList<RefInstructionRulesPrdDto>());

        EligibilityDecision evaluate = cr043MaxInstanceOfProductsForGivenInstructionsRule.evaluate(CANDIDATE_INSTRUCTION, ruleDataHolder);
        assertTrue(evaluate.isEligible());
        assertNull(evaluate.getReasonText());
        verify(productArrangement, times(6)).getAssociatedProduct();
        verify(product, times(4)).getInstructionDetails();
    }

    private ArrayList<RefInstructionRulesPrdDto> mockRefInstructionRulesPrdDtos(String thresholdLimit) {
        ArrayList<RefInstructionRulesPrdDto> refInstructionRulesPrdDtos = new ArrayList();
        RefInstructionRulesPrdDto refInstructionRulesPrdDtoWithSequennceParamOne = mock(RefInstructionRulesPrdDto.class);
        when(refInstructionRulesPrdDtoWithSequennceParamOne.getRule()).thenReturn(CR_043);
        sequenceParam = new BigDecimal(1);
        when(refInstructionRulesPrdDtoWithSequennceParamOne.getRuleParamSeq()).thenReturn(sequenceParam);
        when(refInstructionRulesPrdDtoWithSequennceParamOne.getRuleParamValue()).thenReturn(thresholdMnemonic);

        RefInstructionRulesPrdDto refInstructionRulesPrdDtoWithSequenceParamTwo = mock(RefInstructionRulesPrdDto.class);
        when(refInstructionRulesPrdDtoWithSequenceParamTwo.getRule()).thenReturn(CR_043);
        sequenceParam = new BigDecimal(2);

        when(refInstructionRulesPrdDtoWithSequenceParamTwo.getRuleParamSeq()).thenReturn(sequenceParam);
        when(refInstructionRulesPrdDtoWithSequenceParamTwo.getRuleParamValue()).thenReturn(thresholdLimit);

        refInstructionRulesPrdDtos.add(refInstructionRulesPrdDtoWithSequennceParamOne);
        refInstructionRulesPrdDtos.add(refInstructionRulesPrdDtoWithSequenceParamTwo);
        return refInstructionRulesPrdDtos;
    }

    private void addToListOfArrangements(ProductArrangement productArrangement, int numberOfProducts) {
        for (int i = 0; i < numberOfProducts; i++) {
            productArrangements.add(productArrangement);
            productArrangementFacades.add(new ProductArrangementFacade(productArrangement));

        }
    }

    private ProductArrangement createMockProduct(String mnemonic) {

        InstructionDetails instructionDetails = mock(InstructionDetails.class);
        when(product.getInstructionDetails()).thenReturn(instructionDetails);
        when(instructionDetails.getInstructionMnemonic()).thenReturn(mnemonic);
        when(productArrangement.getAssociatedProduct()).thenReturn(product);
        return productArrangement;
    }
}
