package com.lloydsbanking.salsa.apacc.service.fulfil;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.registration.downstream.ActivateIBApplication;
import com.lloydsbanking.salsa.apacc.TestDataHelper;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import lib_sim_bo.businessobjects.ExtraConditions;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class IBRegistrationTest {
    IBRegistration ibRegistration;

    ActivateProductArrangementRequest request;

    TestDataHelper testDataHelper;

    FinanceServiceArrangement financeServiceArrangement;

    ExtraConditions extraConditions;

    ApplicationDetails applicationDetails;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        financeServiceArrangement = testDataHelper.createFinanceServiceArrangement();
        request = testDataHelper.createApaRequestByDBEvent();
        ibRegistration = new IBRegistration();
        ibRegistration.activateIBApplication = mock(ActivateIBApplication.class);
        extraConditions = new ExtraConditions();
        applicationDetails = new ApplicationDetails();
        ibRegistration.productArrangementTraceLog = mock(ProductArrangementTraceLog.class);
        when(ibRegistration.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class), any(String.class))).thenReturn("ProdArrngemnt");

    }

    @Test
    public void testIbRegistrationCall() {
        when(ibRegistration.activateIBApplication.retrieveActivateIBApplication(financeServiceArrangement, request.getHeader())).thenReturn(testDataHelper.createB751Response(-5));
        ibRegistration.ibRegistrationCall(request.getHeader(), financeServiceArrangement, applicationDetails);
        assertEquals("777602816", financeServiceArrangement.getJointParties().get(0).getIsRegisteredIn().getProfile().getUserName());
    }

    @Test
    public void testIbRegistrationCallWithError() {
        when(ibRegistration.activateIBApplication.retrieveActivateIBApplication(financeServiceArrangement, request.getHeader())).thenReturn(null);
        ibRegistration.ibRegistrationCall(request.getHeader(), financeServiceArrangement, applicationDetails);
        assertEquals("008", applicationDetails.getConditionList().get(0).getReasonCode());
        assertEquals("Failed to do IB Registration", applicationDetails.getConditionList().get(0).getReasonText());
    }
}
