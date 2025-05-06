package com.lloydsbanking.salsa.activate.helper;

import com.lloydsbanking.salsa.UnitTest;
import lib_sim_bo.businessobjects.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@Category(UnitTest.class)
public class ApplicationDetailsTest {
    ApplicationDetails applicationDetails;

    @Before
    public void setUp() {
        applicationDetails = new ApplicationDetails();
    }

    @Test
    public void testApplicationStatus() {
        applicationDetails.setApplicationStatus("app");
        assertEquals("app", applicationDetails.getApplicationStatus());
    }

    @Test
    public void testApplicationSubStatus() {
        applicationDetails.setApplicationSubStatus("applk");
        assertEquals("applk", applicationDetails.getApplicationSubStatus());
    }

    @Test
    public void testRetryCount() {
        applicationDetails.setRetryCount(24);
        assertEquals(24, applicationDetails.getRetryCount().intValue());
    }

    @Test
    public void testApiFailureFlag() {
        boolean flag = false;
        applicationDetails.setApiFailureFlag(flag);
        assertEquals(flag, applicationDetails.isApiFailureFlag());
    }

    @Test
    public void testCondition() {
        Condition condition = new Condition();
        applicationDetails.getConditionList().add(condition);
        assertEquals(condition, applicationDetails.getConditionList().get(0));
    }

    @Test
    public void testScoreResult() {
        applicationDetails.setScoreResult("result");
        assertEquals("result", applicationDetails.getScoreResult());
    }

    @Test
    public void testProductFamilies() {
        applicationDetails.setProductFamilies(new ArrayList<ProductFamily>());
        assertNotNull(applicationDetails.getProductFamilies());
    }

    @Test
    public void testProductOptions() {
        applicationDetails.setProductOptions(new ArrayList<ProductOptions>());
        assertNotNull(applicationDetails.getProductOptions());
    }

    @Test
    public void testCreditLimit() {
        CurrencyAmount currencyAmount = new CurrencyAmount();
        applicationDetails.setCreditLimit(currencyAmount);
        assertEquals(currencyAmount, applicationDetails.getCreditLimit());
    }

    @Test
    public void testReferralCodes() {
        List<ReferralCode> referralCodes = new ArrayList<>();
        ReferralCode referralCode = new ReferralCode();
        referralCodes.add(referralCode);
        applicationDetails.setReferralCodes(referralCodes);
        assertEquals(referralCode, applicationDetails.getReferralCodes().get(0));
    }


}
