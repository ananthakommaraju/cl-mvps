package com.lloydsbanking.salsa.eligibility.service.utility;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.rules.common.DeclineReasonAdder;
import lb_gbo_sales.businessobjects.CustomerInstruction;
import lb_gbo_sales.businessobjects.DeclineReason;
import lb_gbo_sales.messages.RequestHeader;
import lib_sim_bo.businessobjects.ProductEligibilityDetails;
import lib_sim_bo.businessobjects.ReasonCode;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class DeclineReasonAdderTest {

    private String cmsReason = "cmsReason";

    private String reason = "reason";

    TestDataHelper dataHelper = new TestDataHelper();

    DeclineReasonAdder declineReasonAdder = new DeclineReasonAdder();

    RequestHeader header;

    @Before
    public void setUp() {
        header = dataHelper.createEligibilityRequestHeader(dataHelper.TEST_RETAIL_CHANNEL_ID, dataHelper.TEST_INTERACTION_ID, dataHelper.TEST_OCIS_ID, dataHelper.TEST_CUSTOMER_ID, dataHelper.TEST_CONTACT_POINT_ID);
    }

    @Test
    public void setDeclineReasonTest() {
        CustomerInstruction customerInstruction = new CustomerInstruction();


        declineReasonAdder.addDeclineReason(cmsReason, reason, customerInstruction);
        List<DeclineReason> declineReasonList = customerInstruction.getDeclineReasons();

        assertEquals(cmsReason, declineReasonList.get(declineReasonList.size() - 1).getReasonCode());
        assertEquals(reason, declineReasonList.get(declineReasonList.size() - 1).getReasonDescription());
        assertEquals(false, customerInstruction.isEligibilityIndicator());

    }

    @Test
    public void setDeclineReasonEligibilityDetailsTest() {
        ProductEligibilityDetails eligibilityDetails = new ProductEligibilityDetails();
        declineReasonAdder.addDeclineReason(cmsReason, reason, eligibilityDetails);
        List<ReasonCode> reasonCodeList = eligibilityDetails.getDeclineReasons();
        assertEquals(cmsReason, reasonCodeList.get(reasonCodeList.size() - 1).getCode());
        assertEquals(reason, reasonCodeList.get(reasonCodeList.size() - 1).getDescription());
        assertEquals(String.valueOf(false), eligibilityDetails.getIsEligible());

    }

}


