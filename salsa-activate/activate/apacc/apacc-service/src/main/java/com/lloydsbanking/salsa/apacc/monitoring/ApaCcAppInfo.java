package com.lloydsbanking.salsa.apacc.monitoring;

import com.lloydsbanking.salsa.appinfo.AbstractSalsaServiceAppInfo;

import java.util.Arrays;
import java.util.Collection;

public class ApaCcAppInfo extends AbstractSalsaServiceAppInfo {
    @Override
    protected Collection<String> getServiceNames() {
        return Arrays.asList("fdi.f251", "ocis.f061", "ocis.f062", "ocis.f060", "ocis.f259", "fdi.f241", "fdi.f241v1",
                "fs.system", "dp.encrypt", "rpc", "generatedocument", "tms.x741", "sendcommunication", "schedulecommunication", "asm.f425",
                "fs.application", "soa.sas", "soa.ipm", "ocis.c241", "soa.dms", "ocis.c658", "ocis.c234", "fs.account");
    }

    @Override
    protected Collection<String> getDatabaseNames() {
        return Arrays.asList("ibPamData", "switches");
    }
}
