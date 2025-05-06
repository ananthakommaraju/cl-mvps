package com.lloydsbanking.salsa.ppae.service.scheduler;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.brand.Brand;
import com.lloydsbanking.salsa.downstream.pam.model.Applications;
import com.lloydsbanking.salsa.downstream.pam.model.Brands;
import com.lloydsbanking.salsa.downstream.pam.model.EventStores;
import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import com.lloydsbanking.salsa.header.generator.HeaderGenerator;
import com.lloydsbanking.salsa.ppae.service.PpaeService;
import com.lloydsbanking.salsa.ppae.service.downstream.BatchPAMRetriever;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class PendingApplicationSchedulerTest {
    PendingApplicationScheduler pendingApplicationScheduler;
    List<EventStores> eventStoresList;
    Applications applications;
    ProcessPendingArrangementEventRequest upStreamRequest;

    @Before
    public void setUp() {
        pendingApplicationScheduler = new PendingApplicationScheduler();
        pendingApplicationScheduler.switchService = mock(SwitchService.class);
        pendingApplicationScheduler.batchPAMRetriever = mock(BatchPAMRetriever.class);
        pendingApplicationScheduler.headerGenerator = mock(HeaderGenerator.class);
        pendingApplicationScheduler.eventFailure = mock(AtomicInteger.class);
        pendingApplicationScheduler.ppaeService = mock(PpaeService.class);
        upStreamRequest = new ProcessPendingArrangementEventRequest();
        eventStoresList = new ArrayList<>();
        EventStores eventStores1 = new EventStores();
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
        eventStoresList.add(eventStores1);
        eventStoresList.add(eventStores2);
        applications = new Applications();

    }

    @Test
    public void testRetrieveEventStores() {
        applications.setBrands(new Brands());
        applications.setRetryCount(0L);
        when(pendingApplicationScheduler.switchService.getGlobalSwitchValue("SW_EnSlsPrPndgSv", Brand.LLOYDS.asString(), false)).thenReturn(true);
        when(pendingApplicationScheduler.switchService.getGlobalSwitchValue("SW_EnSlsPrPndgSv", Brand.TRUSTEE_SAVINGS_BANK.asString(), false)).thenReturn(true);
        when(pendingApplicationScheduler.batchPAMRetriever.getApplicationsEvents(any(Pageable.class))).thenReturn(eventStoresList);
        when(pendingApplicationScheduler.batchPAMRetriever.retrieveApplication(any(String.class))).thenReturn(applications);
        when(pendingApplicationScheduler.headerGenerator.generateRequestHeader(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(upStreamRequest.getHeader());
        pendingApplicationScheduler.retrieveEventStores();
    }

    @Test
    public void testRetrieveEventStoresThrowsException() {
        when(pendingApplicationScheduler.switchService.getGlobalSwitchValue("SW_EnSlsPrPndgSv", Brand.LLOYDS.asString(), false)).thenThrow(IllegalStateException.class);
        when(pendingApplicationScheduler.switchService.getGlobalSwitchValue("SW_EnSlsPrPndgSv", Brand.TRUSTEE_SAVINGS_BANK.asString(), false)).thenThrow(IllegalStateException.class);
        when(pendingApplicationScheduler.batchPAMRetriever.getApplicationsEvents(any(Pageable.class))).thenThrow(IllegalStateException.class);
        pendingApplicationScheduler.retrieveEventStores();
        verify(pendingApplicationScheduler.eventFailure).incrementAndGet();
    }

}
