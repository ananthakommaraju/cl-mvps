package com.lloydsbanking.salsa.opapca.service.utility;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.pam.service.constant.PamConstant;
import com.lloydsbanking.salsa.opapca.service.TestDataHelper;
import lib_sim_bo.businessobjects.CustomerDeviceDetails;
import lib_sim_bo.businessobjects.CustomerScore;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@Category(UnitTest.class)
public class SiraHelperTest {
    private SiraHelper siraHelper;
    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        siraHelper = new SiraHelper();
        testDataHelper = new TestDataHelper();
    }

    @Test
    public void testToSerialisedString() {
        CustomerDeviceDetails customerDeviceDetails = new CustomerDeviceDetails();
        customerDeviceDetails.setAccountLogin("124");
        assertNotNull(siraHelper.toSerializedString(customerDeviceDetails));
        assertNull(siraHelper.toSerializedString(null));
    }

    @Test
    public void testCalculateEidvAndAsmScore() {
        List<CustomerScore> customerScoreList = new ArrayList<>();
        CustomerScore eidvCustomerScore = new CustomerScore();
        eidvCustomerScore.setAssessmentType("EIDV");
        eidvCustomerScore.setScoreResult("Accept");
        customerScoreList.add(eidvCustomerScore);
        CustomerScore asmCustomer = new CustomerScore();
        asmCustomer.setAssessmentType(PamConstant.ASM_ASSESSMENT_TYPE);
        asmCustomer.setScoreResult("Decline");
        customerScoreList.add(asmCustomer);
        List<String> customerList = siraHelper.calculateEidvAndAsmScore(customerScoreList);
        assertEquals(2, customerList.size());
        assertEquals("Accept", customerList.get(0));
    }
}
