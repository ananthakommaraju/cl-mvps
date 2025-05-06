package com.lloydsbanking.salsa.activate.postfulfil;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class AsyncProcessFulfilmentActivitiesTaskTest {

    AsyncProcessFulfilmentActivitiesTask asyncProcessFulfilmentActivitiesTask;
    ProcessPostFulfilmentActivitiesService processPostFulfilmentActivitiesService;
    ActivateProductArrangementRequest request;
    TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        processPostFulfilmentActivitiesService = mock(ProcessPostFulfilmentActivitiesService.class);
        testDataHelper = new TestDataHelper();
        request = testDataHelper.createApaRequestForPca();
        asyncProcessFulfilmentActivitiesTask = new AsyncProcessFulfilmentActivitiesTask(processPostFulfilmentActivitiesService, request);
    }

    @Test
    public void testAsyncTaskForConstructor() {
        ProcessPostFulfilmentActivitiesService processPostFulfilmentActivitiesService1 = new ProcessPostFulfilmentActivitiesService();
        asyncProcessFulfilmentActivitiesTask = new AsyncProcessFulfilmentActivitiesTask(processPostFulfilmentActivitiesService1, request);
        assertEquals(request, asyncProcessFulfilmentActivitiesTask.request);
        assertEquals(processPostFulfilmentActivitiesService1, asyncProcessFulfilmentActivitiesTask.processPostFulfilmentActivitiesService);
    }

    @Test
    public void testAsyncTask() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        doNothing().when(processPostFulfilmentActivitiesService).processPostFulfilmentActivitiesResponse(request);
        asyncProcessFulfilmentActivitiesTask.run();
        verify(processPostFulfilmentActivitiesService).processPostFulfilmentActivitiesResponse(request);
    }

    @Test
    public void testAsyncTaskWithDataNotAvailableError() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        doThrow(ActivateProductArrangementDataNotAvailableErrorMsg.class).when(processPostFulfilmentActivitiesService).processPostFulfilmentActivitiesResponse(request);
        asyncProcessFulfilmentActivitiesTask.run();
        verify(processPostFulfilmentActivitiesService).processPostFulfilmentActivitiesResponse(request);
    }

    @Test
    public void testAsyncTaskWithResourceNotAvailableError() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        doThrow(ActivateProductArrangementResourceNotAvailableErrorMsg.class).when(processPostFulfilmentActivitiesService).processPostFulfilmentActivitiesResponse(request);
        asyncProcessFulfilmentActivitiesTask.run();
        verify(processPostFulfilmentActivitiesService).processPostFulfilmentActivitiesResponse(request);
    }


}
