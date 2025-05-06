package com.lloydsbanking.salsa.activate.postfulfil;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Category(UnitTest.class)
public class AsyncProcessFulfilmentActivitiesCallerTest {
    AsyncProcessFulfilmentActivitiesCaller asyncProcessFulfilmentActivitiesCaller;
    ProcessPostFulfilmentActivitiesService processPostFulfilmentActivitiesService;
    ActivateProductArrangementRequest request;
    TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        asyncProcessFulfilmentActivitiesCaller = new AsyncProcessFulfilmentActivitiesCaller();
        asyncProcessFulfilmentActivitiesCaller.processPostFulfilmentActivitiesService = mock(ProcessPostFulfilmentActivitiesService.class);
        asyncProcessFulfilmentActivitiesCaller.executor = mock(ThreadPoolTaskExecutor.class);
        processPostFulfilmentActivitiesService = mock(ProcessPostFulfilmentActivitiesService.class);
        testDataHelper = new TestDataHelper();
        request = testDataHelper.createApaRequestForPca();

    }

    @Test
    public void testCallAsyncMethod() {
        asyncProcessFulfilmentActivitiesCaller.callAsyncMethod(request);
        verify(asyncProcessFulfilmentActivitiesCaller.executor).submit(any(AsyncProcessFulfilmentActivitiesTask.class));
    }

}
