package com.lloydsbanking.salsa.ppae.service.scheduler;

import com.lloydsbanking.salsa.brand.Brand;
import com.lloydsbanking.salsa.downstream.pam.model.Applications;
import com.lloydsbanking.salsa.downstream.pam.model.EventStores;
import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import com.lloydsbanking.salsa.header.generator.HeaderGenerator;
import com.lloydsbanking.salsa.ppae.service.PpaeService;
import com.lloydsbanking.salsa.ppae.service.constant.PPAEServiceConstant;
import com.lloydsbanking.salsa.ppae.service.downstream.BatchPAMRetriever;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PendingApplicationScheduler {
    private static final Logger LOGGER = Logger.getLogger(PendingApplicationScheduler.class);

    @Autowired
    SwitchService switchService;

    AtomicInteger eventFailure = new AtomicInteger(0);

    private static String SALSA_PPAE_SWITCH = "SW_EnSlsPrPndgSv";

    @Autowired
    HeaderGenerator headerGenerator;

    @Autowired
    @Qualifier("ppaeService")
    PpaeService ppaeService;

    @Autowired
    BatchPAMRetriever batchPAMRetriever;

    @Value("${apps.batch.size}")
    int batchSize = 1;

    @Value("${apps.retry.limit}")
    int retryLimit;

    public void retrieveEventStores() {
        try {
            if (switchService.getGlobalSwitchValue(SALSA_PPAE_SWITCH, Brand.LLOYDS.asString(), false) && switchService.getGlobalSwitchValue(SALSA_PPAE_SWITCH, Brand.TRUSTEE_SAVINGS_BANK.asString(), false)) {
                Pageable topEvents = new PageRequest(0, batchSize, Sort.Direction.ASC, "eventTime");
                List<EventStores> eventStoresList = batchPAMRetriever.getApplicationsEvents(topEvents);
                for (EventStores event : eventStoresList) {
                    processEventStores(event);
                }
            }
        } catch (IllegalStateException e) {
            eventFailure.incrementAndGet();
            LOGGER.info("Error while retrieving event stores from PendingApplicationScheduler scheduler", e);
        }
    }

    private void processEventStores(EventStores event) {
        LOGGER.info("Application ID fetched for application :" + event.getObjectKey());
        Applications application = batchPAMRetriever.retrieveApplication(event.getObjectKey());
        if (null != application && null != application.getBrands()) {
            if (application.getRetryCount() == null || (null != application.getRetryCount() && application.getRetryCount() <= retryLimit)) {
                LOGGER.info("Processing event " + event.getEventId() + " for application Id " + application.getId());
                ProcessPendingArrangementEventRequest processPendingArrangementEventRequest = new ProcessPendingArrangementEventRequest();
                processPendingArrangementEventRequest.setHeader(headerGenerator.generateRequestHeader(application.getBrands().getCode(), PPAEServiceConstant.SERVICE_NAME, PPAEServiceConstant.SERVICE_ACTION, PPAEServiceConstant.BUSINESS_TRANSACTION));
                processPendingArrangementEventRequest.setApplicationId(event.getObjectKey());
                ppaeService.processPendingArrangementEvent(processPendingArrangementEventRequest);

            }
        }
    }
}