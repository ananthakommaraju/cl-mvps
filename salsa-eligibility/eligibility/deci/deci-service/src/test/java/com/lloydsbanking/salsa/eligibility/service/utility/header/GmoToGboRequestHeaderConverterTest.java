package com.lloydsbanking.salsa.eligibility.service.utility.header;

import com.lloydsbanking.salsa.eligibility.service.utility.header.GmoToGboRequestHeaderConverter;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import lib_sim_gmo.messages.ContextItem;
import lib_sim_gmo.messages.EATraceContextInfo;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GmoToGboRequestHeaderConverterTest {
    GmoToGboRequestHeaderConverter headerConverter;

    TestDataHelper dataHelper;
    RequestHeader gmoHeader;

    @Before
    public void setUp() {
        headerConverter = new GmoToGboRequestHeaderConverter();
        dataHelper=new TestDataHelper();
        gmoHeader=dataHelper.createEligibilityRequestHeader(TestDataHelper.TEST_RETAIL_CHANNEL_ID,
                                                            TestDataHelper.TEST_INTERACTION_ID,
                                                            TestDataHelper.TEST_OCIS_ID,
                                                            TestDataHelper.TEST_CUSTOMER_ID,
                                                            TestDataHelper.TEST_CONTACT_POINT_ID);
    }

    @Test
    public void testConvert() {
        gmoHeader.setEaTraceContextInfo(new EATraceContextInfo());
        gmoHeader.getEaTraceContextInfo().getContextItems().add(new ContextItem());
        gmoHeader.getEaTraceContextInfo().getContextItems().get(0).setName("name");
        gmoHeader.getEaTraceContextInfo().getContextItems().get(0).setValue("value");
        lb_gbo_sales.messages.RequestHeader gboHeader=headerConverter.convert(gmoHeader);
        assertEquals(gmoHeader.getChannelId(), gboHeader.getChannelId());
        assertEquals(gmoHeader.getInteractionId(), gboHeader.getInteractionId());
        assertEquals(gmoHeader.getBusinessTransaction(), gboHeader.getBusinessTransaction());
        assertEquals(gmoHeader.getLloydsHeaders().get(0).getName(), gboHeader.getLloydsHeaders().get(0).getName());
        assertEquals(gmoHeader.getLloydsHeaders().get(0).getPrefix(), gboHeader.getLloydsHeaders().get(0).getPrefix());
        assertEquals(gmoHeader.getEaTraceContextInfo().getContextItems().get(0).getName(), gboHeader.getEaTraceContextInfo().getContextItems().get(0).getName());
        assertEquals(gmoHeader.getEaTraceContextInfo().getContextItems().get(0).getValue(), gboHeader.getEaTraceContextInfo().getContextItems().get(0).getValue());
    }
}
