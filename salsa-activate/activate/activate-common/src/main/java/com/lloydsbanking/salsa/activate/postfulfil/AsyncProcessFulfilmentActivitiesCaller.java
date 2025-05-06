package com.lloydsbanking.salsa.activate.postfulfil;


import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class AsyncProcessFulfilmentActivitiesCaller {

    @Autowired
    ProcessPostFulfilmentActivitiesService processPostFulfilmentActivitiesService;

    @Autowired
    ThreadPoolTaskExecutor executor;


    public void callAsyncMethod(final ActivateProductArrangementRequest request) {
        AsyncProcessFulfilmentActivitiesTask asyncProcessFulfilmentActivitiesTask = new AsyncProcessFulfilmentActivitiesTask(processPostFulfilmentActivitiesService, request);
        executor.submit(asyncProcessFulfilmentActivitiesTask);
    }
}
