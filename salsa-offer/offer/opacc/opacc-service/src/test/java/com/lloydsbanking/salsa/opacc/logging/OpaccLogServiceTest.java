package com.lloydsbanking.salsa.opacc.logging;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.logging.LogItem;
import com.lloydsbanking.salsa.logging.layout.AbstractSalsaLayout;
import com.lloydsbanking.salsa.opacc.service.TestDataHelper;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.MDC;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class OpaccLogServiceTest {

    OpaccLogService opaccLogService;

    @Before
    public void setUp() {
        opaccLogService = new OpaccLogService();
    }


    @Test
    public void testInitialiseContext() throws Exception {

        TestDataHelper testDataHelper = new TestDataHelper();
        RequestHeader requestHeader = testDataHelper.createOpaccRequestHeader("IBL");
        opaccLogService.cloneName = "TestClone";

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setLocalAddr("1.2.3.4");
        servletRequest.setRemoteAddr("5.6.7.8");

        opaccLogService.initialiseContext(requestHeader);

        assertEquals("003", MDC.get(LogItem.CHANNEL.name()));
        assertEquals("TestClone", MDC.get(LogItem.NODE_ID.name()));
        assertEquals("Salsa", MDC.get(LogItem.COMPONENT.name()));
        assertEquals("Salsa Opacc", MDC.get(LogItem.APPLICATION.name()));
        assertEquals("offerProductArrangement", MDC.get(AbstractSalsaLayout.EVENT_ID_PREFIX_KEY));
    }
}
