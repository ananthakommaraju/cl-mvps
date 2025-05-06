package com.lloydsbanking.salsa.ppae.service.appstatus;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamService;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import com.lloydsbanking.salsa.ppae.service.downstream.ActivateProductManager;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;

import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class ProcessPendingApplicationsTest {

    ProcessPendingApplications processPendingApplications;
    ProductArrangement productArrangement;
    TestDataHelper testDataHelper;
    ProcessPendingArrangementEventRequest request;


    @Before
    public void setUp() throws DatatypeConfigurationException {
        processPendingApplications = new ProcessPendingApplications();
        testDataHelper = new TestDataHelper();
        request = testDataHelper.createPpaeRequest("1", "LTB");
        productArrangement = testDataHelper.createProductArrangement();
        processPendingApplications.updatePamService = mock(UpdatePamService.class);
        processPendingApplications.activateProductManager = mock(ActivateProductManager.class);
        processPendingApplications.updatePamService = mock(UpdatePamService.class);
    }

    @Test
    public void testActivateArrangementCalled() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        processPendingApplications.modifyAndActivatePendingApplications(productArrangement, request.getHeader(), new PpaeInvocationIdentifier(false, true));
        verify(processPendingApplications.activateProductManager).activateProduct(productArrangement, request.getHeader());
    }

    @Test
    public void testActivateModifyProductArrangementCalled() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        processPendingApplications.modifyAndActivatePendingApplications(productArrangement, request.getHeader(), new PpaeInvocationIdentifier(true, false));
        verify(processPendingApplications.updatePamService).updateApplicationsInPam(productArrangement);
    }

    @Test
    public void testModifyProductArrangementCalledWithInternalSystemErrorMsg() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        doThrow(ActivateProductArrangementInternalSystemErrorMsg.class).when(processPendingApplications.activateProductManager).activateProduct(productArrangement, request.getHeader());
        processPendingApplications.modifyAndActivatePendingApplications(productArrangement, request.getHeader(), new PpaeInvocationIdentifier(false, true));
    }

    @Test
    public void testModifyProductArrangementCalledWithExternalBusinessErrorMsg() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        doThrow(ActivateProductArrangementExternalBusinessErrorMsg.class).when(processPendingApplications.activateProductManager).activateProduct(productArrangement, request.getHeader());
        processPendingApplications.modifyAndActivatePendingApplications(productArrangement, request.getHeader(), new PpaeInvocationIdentifier(false, true));
    }

    @Test
    public void testModifyProductArrangementCalledWithExternalSystemErrorMsg() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        doThrow(ActivateProductArrangementExternalSystemErrorMsg.class).when(processPendingApplications.activateProductManager).activateProduct(productArrangement, request.getHeader());
        processPendingApplications.modifyAndActivatePendingApplications(productArrangement, request.getHeader(), new PpaeInvocationIdentifier(false, true));
    }

    @Test
    public void testModifyProductArrangementCalledWithResourceNotAvailableErrorMsg() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        doThrow(ActivateProductArrangementResourceNotAvailableErrorMsg.class).when(processPendingApplications.activateProductManager).activateProduct(productArrangement, request.getHeader());
        processPendingApplications.modifyAndActivatePendingApplications(productArrangement, request.getHeader(), new PpaeInvocationIdentifier(false, true));
    }

    @Test
    public void testModifyProductArrangementCalledWithDataNotAvailableErrorMsg() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        doThrow(ActivateProductArrangementDataNotAvailableErrorMsg.class).when(processPendingApplications.activateProductManager).activateProduct(productArrangement, request.getHeader());
        processPendingApplications.modifyAndActivatePendingApplications(productArrangement, request.getHeader(), new PpaeInvocationIdentifier(false, true));
    }
}
