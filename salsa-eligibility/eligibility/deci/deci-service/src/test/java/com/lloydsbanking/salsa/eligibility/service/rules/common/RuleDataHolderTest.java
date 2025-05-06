package com.lloydsbanking.salsa.eligibility.service.rules.common;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@Category(UnitTest.class)
public class RuleDataHolderTest {

    @Test
    public void shouldReturnRule() {
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule("14");

        assertEquals("14", ruleDataHolder.getRule());

    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionWhenReturningEmptyRule() {
        RuleDataHolder ruleDataHolder = new RuleDataHolder();

        ruleDataHolder.getRule();


    }

    @Test
    public void shouldReturnRuleInsMnemonic() {
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRuleInsMnemonic("G_ISA");

        assertEquals("G_ISA", ruleDataHolder.getRuleInsMnemonic());
    }

    @Test
    public void shouldReturnArrangementType() {
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setArrangementType("CA");

        assertEquals("CA", ruleDataHolder.getArrangementType());
    }

    @Test
    public void shouldReturnCandidateInstructionList() {
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        List<String> candidateInstructions = new ArrayList<>();
        candidateInstructions.add("G_ISA");
        candidateInstructions.add("P_CLUB");
        ruleDataHolder.setCandidateInstructions(candidateInstructions);

        assertEquals(candidateInstructions, ruleDataHolder.getCandidateInstructions());
    }
}