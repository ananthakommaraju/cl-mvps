package com.lloydsbanking.salsa.ppae.service.downstream;

import com.lloydsbanking.salsa.downstream.pam.jdbc.ApplicationsDao;
import com.lloydsbanking.salsa.downstream.pam.jdbc.EventStoresDao;
import com.lloydsbanking.salsa.downstream.pam.model.Applications;
import com.lloydsbanking.salsa.downstream.pam.model.EventStores;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Component
public class BatchPAMRetriever {
    private static final Logger LOGGER = Logger.getLogger(BatchPAMRetriever.class);

    private static short NEW_EVENT_STATUS = 0;
    @Autowired
    EventStoresDao eventStoresDao;

    @Autowired
    ApplicationsDao applicationsDao;

    private static final int RETRY_COUNT = 10;

    private static final int PAUSE_INTERVAL = 50;


    private static String OBJECT_NAME = "Applications";

    @Value("${apps.batch.server.connectorId}")
    String connectorId;


    public List<EventStores> getApplicationsEvents(Pageable topEvents) {
        for (int iteration = 0; iteration < RETRY_COUNT; iteration++) {
            try {
                List<EventStores> events = eventStoresDao.findByObjectNameAndEventStatusAndConnectorIdAndEventTimeLessThan(OBJECT_NAME, topEvents, NEW_EVENT_STATUS, connectorId, new Date());
                eventStoresDao.delete(events);
                return events;
            } catch (DataAccessException e) {
                LOGGER.error("Exception while fetching Application events for attempt no. " + iteration, e);
                randomPause();
            }
        }
        // return an empty list if we can't get anything
        return new ArrayList<EventStores>();
    }

    public Applications retrieveApplication(String objectKey) {
        try {
            return applicationsDao.findOne(Long.valueOf(objectKey));
        } catch (DataAccessException e) {
            LOGGER.error("Exception while fetching Applications", e);
            throw new IllegalStateException(e);
        }
    }


    public void doDeleteEventStores(EventStores eventStores) {
        try {
            eventStoresDao.delete(eventStores);
        } catch (DataAccessException e) {
            LOGGER.error("Exception while deleting Application Event :event_stores", e);
            throw new IllegalStateException(e);
        }
    }

    public boolean eventExists(long eventId) {
        try {
            return (null != eventStoresDao.findOne(eventId));
        } catch (DataAccessException e) {
            LOGGER.error("Error validating event for id: " + eventId, e);
            throw new IllegalStateException(e);
        }
    }

    // wait between 50 and 100 milliseconds
    private void randomPause() {
        Random random = new Random();
        try {
            Thread.sleep(random.nextInt(PAUSE_INTERVAL) + PAUSE_INTERVAL);
        } catch (InterruptedException e) {
            LOGGER.info("Error pausing for Application event polling", e);
        }
    }


}
