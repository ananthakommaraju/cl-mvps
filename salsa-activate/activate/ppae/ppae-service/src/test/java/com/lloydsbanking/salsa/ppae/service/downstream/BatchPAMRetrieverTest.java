package com.lloydsbanking.salsa.ppae.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ApplicationsDao;
import com.lloydsbanking.salsa.downstream.pam.jdbc.EventStoresDao;
import com.lloydsbanking.salsa.downstream.pam.model.Applications;
import com.lloydsbanking.salsa.downstream.pam.model.EventStores;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class BatchPAMRetrieverTest {
    BatchPAMRetriever batchPAMRetriever;
    List<EventStores> eventStores = new ArrayList();
    Applications applications;
    EventStores eventStores1;

    @Before
    public void setUp() {
        batchPAMRetriever = new BatchPAMRetriever();
        batchPAMRetriever.applicationsDao = mock(ApplicationsDao.class);
        batchPAMRetriever.eventStoresDao = mock(EventStoresDao.class);

        eventStores1 = new EventStores();
        eventStores1.setEventId(1);
        eventStores1.setEventPriority((short) 1);
        eventStores1.setObjectKey("34298");
        eventStores1.setXid(null);
        eventStores1.setObjectName("Applications");
        eventStores1.setObjectFunction("Update");
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.YEAR, Calendar.YEAR - 1);
        calendar1.set(Calendar.MONTH, 10);
        calendar1.set(Calendar.DATE, 01);
        Date date1 = calendar1.getTime();
        eventStores1.setEventTime(date1);
        eventStores1.setEventStatus((short) 0);
        eventStores1.setEventComment("inserted by trigger APP_STATUS_CCA_TRG");
        eventStores1.setLockId(0);
        eventStores1.setConnectorId("001");
        EventStores eventStores2 = new EventStores();
        eventStores2.setEventId(9);
        eventStores2.setEventPriority((short) 1);
        eventStores2.setObjectKey("34298");
        eventStores1.setXid(null);
        eventStores2.setObjectName("Applications");
        eventStores2.setObjectFunction("Update");
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.YEAR, calendar2.get(Calendar.YEAR));
        calendar2.set(Calendar.MONTH, 10);
        calendar2.set(Calendar.DATE, 01);
        Date date2 = calendar2.getTime();
        eventStores2.setEventTime(date2);
        eventStores2.setEventStatus((short) 0);
        eventStores2.setEventComment("inserted by trigger APP_STATUS_CCA_TRG");
        eventStores2.setLockId(0);
        eventStores2.setConnectorId("001");
        eventStores.add(eventStores1);
        eventStores.add(eventStores2);
        applications = new Applications();
        applications.setProductName("abc");
    }

    @Test
    public void testGetApplicationsEvents() {

        Pageable topEvents = new PageRequest(0, 10, Sort.Direction.ASC, "eventTime");
        when(batchPAMRetriever.eventStoresDao.findByObjectNameAndEventStatusAndConnectorIdAndEventTimeLessThan(any(String.class), any(Pageable.class), any(Short.class), any(String.class), any(Date.class))).thenReturn(eventStores);

        List<EventStores> eventStoresResponse = batchPAMRetriever.getApplicationsEvents(topEvents);
        assertEquals(2, eventStoresResponse.size());
        assertEquals("001", eventStoresResponse.get(1).getConnectorId());
        assertEquals(0, eventStoresResponse.get(1).getEventStatus());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetApplicationsEventsThrowsException() {
        Pageable topEvents = new PageRequest(0, 10, Sort.Direction.ASC, "eventTime");
        when(batchPAMRetriever.eventStoresDao.findByObjectNameAndEventStatusAndConnectorIdAndEventTimeLessThan(any(String.class), any(Pageable.class), any(Short.class), any(String.class), any(Date.class))).thenThrow(IllegalStateException.class);
        batchPAMRetriever.getApplicationsEvents(topEvents);
    }

    @Test
    public void testRetrieveApplication() {

        when(batchPAMRetriever.applicationsDao.findOne(any(Long.class))).thenReturn(applications);
        Applications applications1 = batchPAMRetriever.retrieveApplication("123");
        assertEquals("abc", applications1.getProductName());
    }

    @Test(expected = IllegalStateException.class)
    public void testRetrieveApplicationThrowsException() {
        when(batchPAMRetriever.applicationsDao.findOne(any(Long.class))).thenThrow(IllegalStateException.class);
        batchPAMRetriever.retrieveApplication("123");
    }

    @Test
    public void testDoDeleteEventStores() {
        batchPAMRetriever.doDeleteEventStores(eventStores1);
        verify(batchPAMRetriever.eventStoresDao).delete(eventStores1);
    }

    @Test(expected = IllegalStateException.class)
    public void testDoDeleteEventStoresThrowsException() {
        doThrow(IllegalStateException.class).when(batchPAMRetriever.eventStoresDao).delete(eventStores1);
        batchPAMRetriever.doDeleteEventStores(eventStores1);

    }
}
