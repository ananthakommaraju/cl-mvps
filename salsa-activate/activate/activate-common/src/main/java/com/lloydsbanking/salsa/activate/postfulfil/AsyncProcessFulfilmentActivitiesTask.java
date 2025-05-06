package com.lloydsbanking.salsa.activate.postfulfil;


import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import org.apache.log4j.Logger;

public class AsyncProcessFulfilmentActivitiesTask implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(AsyncProcessFulfilmentActivitiesCaller.class);
    ProcessPostFulfilmentActivitiesService processPostFulfilmentActivitiesService;
    ActivateProductArrangementRequest request;

    public AsyncProcessFulfilmentActivitiesTask(ProcessPostFulfilmentActivitiesService processPostFulfilmentActivitiesService, ActivateProductArrangementRequest request) {
        this.processPostFulfilmentActivitiesService = processPostFulfilmentActivitiesService;
        this.request = request;

    }


    @Override
    public void run() {
        try {
            processPostFulfilmentActivitiesService.processPostFulfilmentActivitiesResponse(request);
        } catch (Exception e) //NO SONAR
        {
            LOGGER.info("Error while calling processPostFulfilmentService", e);
        }
    }
}
