package com.lloydsbanking.salsa.eligibility.logging.wz;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;

import com.lloydsbanking.salsa.logging.LogItem;
import com.lloydsbanking.salsa.logging.layout.AbstractSalsaLayout;

import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.MDC;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class EligibilityLogServiceTest {
    EligibilityLogService eligibilityLogServiceWZ;

    @Before
    public void setUp() {
        eligibilityLogServiceWZ = new EligibilityLogService();
    }

    @Test
    public void testInitialiseContext() throws Exception {

        TestDataHelper testDataHelper = new TestDataHelper();
        RequestHeader requestHeader = testDataHelper.createEligibilityRequestHeader(com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_RETAIL_CHANNEL_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_INTERACTION_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_OCIS_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_CUSTOMER_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_CONTACT_POINT_ID);
        eligibilityLogServiceWZ.cloneName = "TestClone";

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setLocalAddr("1.2.3.4");
        servletRequest.setRemoteAddr("5.6.7.8");

        eligibilityLogServiceWZ.initialiseContext(requestHeader);

        assertEquals("003", MDC.get(LogItem.CHANNEL.name()));
        assertEquals("TestClone", MDC.get(LogItem.NODE_ID.name()));
        assertEquals("Salsa", MDC.get(LogItem.COMPONENT.name()));
        assertEquals("Salsa EligibilityWZ", MDC.get(LogItem.APPLICATION.name()));
        assertEquals("eligibilityWZ", MDC.get(AbstractSalsaLayout.EVENT_ID_PREFIX_KEY));
    }
}