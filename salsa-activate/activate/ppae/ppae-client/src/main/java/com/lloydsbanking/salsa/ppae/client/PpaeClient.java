package com.lloydsbanking.salsa.ppae.client;

import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;

public interface PpaeClient {

    void processPendingArrangementEvent(ProcessPendingArrangementEventRequest processPendingArrangementEventRequest);
    public void setSoapMessagesLogFilename(final String soapMessagesLogFilename);
}
