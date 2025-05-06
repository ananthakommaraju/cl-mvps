package com.lloydsbanking.salsa.eligibility.service.rules.common;


import com.lloydsbanking.salsa.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class EligibilityDecisionTest {

    @Test
    public void createInstanceWithDeclinedReasonAndEligibilitySetToFalse() {
        EligibilityDecision eligibilityDecision = new EligibilityDecision("Declined Reason");

        assertNotNull(eligibilityDecision.getReasonText());
        assertFalse(eligibilityDecision.isEligible());
    }

    @Test
    public void createInstanceWithEligibilitySetToTrueAndDeclinedReasonIsNull(){

        EligibilityDecision eligibilityDecision = new EligibilityDecision(true);

        assertNull(eligibilityDecision.getReasonText());
        assertTrue(eligibilityDecision.isEligible());
    }

    @Test
    public void createInstanceWithEligibilitySetToTrueShadowLimitAndRiskBandPopulated(){
        EligibilityDecision eligibilityDecision = new EligibilityDecision(true, "500", "5");

        assertNull(eligibilityDecision.getReasonText());
        assertTrue(eligibilityDecision.isEligible());
        assertEquals("500", eligibilityDecision.getShadowLimit());
        assertEquals("5", eligibilityDecision.getRiskBand());
    }

    @Test
    public void createInstanceWithEligibilitySetToFalseShadowLimitAndRiskBandPopulated(){
        EligibilityDecision eligibilityDecision = new EligibilityDecision("Declined Reason", "500", "5");

        assertEquals("Declined Reason", eligibilityDecision.getReasonText());
        assertFalse(eligibilityDecision.isEligible());
        assertEquals("500", eligibilityDecision.getShadowLimit());
        assertEquals("5", eligibilityDecision.getRiskBand());
    }
}