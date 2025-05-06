package com.lloydsbanking.salsa.apapca.monitoring;

import com.lloydsbanking.salsa.appinfo.AbstractSalsaServiceAppInfo;

import java.util.Arrays;
import java.util.Collection;

public class ApaPcaAppInfo extends AbstractSalsaServiceAppInfo {
    @Override
    protected Collection<String> getServiceNames() {

        return Arrays.asList("fs.account", "soa.sas", "soa.ipm", "ocis.f060", "ocis.f061", "ocis.f062", "cbs.e229.ltb", "cbs.e229.ver", "cbs.e229.bos", "cbs.e229.hlx", "cbs.e469.ltb", "cbs.e469.ver", "cbs.e469.bos", "cbs.e469.hlx"
                , "asm.f425", "fs.application", "cmas.c808", "cmas.c812", "cmas.c818", "cmas.c846", "sendcommunication", "schedulecommunication", "tms.x741"
                , "ocis.c241", "soa.dms", "cbs.e226.ltb", "cbs.e226.ver", "cbs.e226.bos", "cbs.e226.hlx", "rpc", "ocis.c234", "ocis.c658", "dp.encrypt", "pega", "fs.ou");
    }

    @Override
    protected Collection<String> getDatabaseNames() {
        return Arrays.asList("ibPamData", "switches");
    }
}
