package com.lloydsbanking.salsa.apapca.logging;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.logging.LogItem;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.MDC;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class ApaPcaLogServiceTest {
    ApaPcaLogService apaPcaLogService;

    @Before
    public void setUp() {
        apaPcaLogService = new ApaPcaLogService();
    }

    @Test
    public void testInitialiseContext() throws Exception {

        TestDataHelper testDataHelper = new TestDataHelper();
        RequestHeader requestHeader = testDataHelper.createApaRequestHeader();
        apaPcaLogService.cloneName = "TestClone";

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setLocalAddr("1.2.3.4");
        servletRequest.setRemoteAddr("5.6.7.8");

        apaPcaLogService.initialiseContext(requestHeader);

        assertEquals("003", MDC.get(LogItem.CHANNEL.name()));
        assertEquals("TestClone", MDC.get(LogItem.NODE_ID.name()));
        assertEquals("Salsa", MDC.get(LogItem.COMPONENT.name()));
        assertEquals("Salsa APA PCA", MDC.get(LogItem.APPLICATION.name()));
    }
}