package com.lloydsbanking.salsa.apasa.monitoring;

import com.lloydsbanking.salsa.appinfo.AbstractSalsaServiceAppInfo;

import java.util.Arrays;
import java.util.Collection;

public class ApaSaAppInfo extends AbstractSalsaServiceAppInfo {
    @Override
    protected Collection<String> getServiceNames() {
        return Arrays.asList("fs.account", "rpc", "tms.x741", "ocis.f061", "ocis.f062", "ocis.f060", "sendcommunication", "schedulecommunication", "asm.f425"
                , "soa.sas", "soa.ipm", "ocis.c241", "soa.dms", "ocis.c658", "ocis.c234", "fs.application", "cbs.e502.ltb", "cbs.e502.ver", "cbs.e502.bos", "cbs.e502.hlx",
                "cbs.e032.ltb", "cbs.e032.ver", "cbs.e032.bos", "cbs.e032.hlx");
    }

    @Override
    protected Collection<String> getDatabaseNames() {
        return Arrays.asList("ibPamData", "switches");
    }
}
