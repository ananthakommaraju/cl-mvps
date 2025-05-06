package com.lloydsbanking.salsa.apaloans.monitoring;

import com.lloydsbanking.salsa.appinfo.AbstractSalsaServiceAppInfo;

import java.util.Arrays;
import java.util.Collection;

public class ApaLoansAppInfo extends AbstractSalsaServiceAppInfo {
    public ApaLoansAppInfo() {
        super();
    }

    @Override
    protected Collection<String> getServiceNames() {
        return Arrays.asList("tms.x741");
    }

    @Override
    protected Collection<String> getDatabaseNames() {
        return Arrays.asList("ibPamData", "switches");
    }
}
