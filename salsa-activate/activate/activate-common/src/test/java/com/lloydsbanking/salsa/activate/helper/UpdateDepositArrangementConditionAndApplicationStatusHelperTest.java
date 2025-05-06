package com.lloydsbanking.salsa.activate.helper;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.ExtraConditions;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class UpdateDepositArrangementConditionAndApplicationStatusHelperTest {
    UpdateDepositArrangementConditionAndApplicationStatusHelper updateDepositArrangementConditionAndApplicationStatusHelper;

    ApplicationDetails applicationDetails;
    DepositArrangement depositArrangement;
    TestDataHelper testDataHelper;
    ExtraConditions extraConditions1;

    @Before
    public void setUp() {
        updateDepositArrangementConditionAndApplicationStatusHelper = new UpdateDepositArrangementConditionAndApplicationStatusHelper();
        testDataHelper = new TestDataHelper();
        applicationDetails = new ApplicationDetails();
        depositArrangement = testDataHelper.createDepositArrangementForB750();
        extraConditions1 = new ExtraConditions();
    }

    @Test
    public void testSetApplicationDetails() {
        updateDepositArrangementConditionAndApplicationStatusHelper.setApplicationDetails(123, "234", "Failed to update", "failed", "failed substatus", applicationDetails);
        assertEquals(124, applicationDetails.getRetryCount().intValue());
        assertEquals("failed", applicationDetails.getApplicationStatus());
    }


    @Test
    public void testUpdateDepositArrangementAndSetApplicationDetails() {
        updateDepositArrangementConditionAndApplicationStatusHelper.setApplicationDetails(1,"123","102", "Failed to update", "failed", applicationDetails);
        assertEquals(2, applicationDetails.getRetryCount().intValue());
        assertEquals("failed", applicationDetails.getApplicationSubStatus());
    }

}
