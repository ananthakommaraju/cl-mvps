package com.lloydsbanking.salsa.ppae.monitoring;

import com.lloydsbanking.salsa.appinfo.AbstractSalsaServiceAppInfo;

import java.util.Arrays;
import java.util.Collection;

public class PpaeAppInfo extends AbstractSalsaServiceAppInfo {
    @Override
    protected Collection<String> getServiceNames() {
        return Arrays.asList("asm.f424", "asm.f204", "asm.f205", "eligibility", "eidv.x711.ltb", "eidv.x711.hlx",
                "eidv.x711.ver", "eidv.x711.bos", "fdi.f251", "ocis.f259", "fs.system", "fdi.f241", "fdi.f241v1",
                "dp.encrypt", "pad.q028", "pad.f263", "activateProductCC", "activateProductSA", "activateProductPCA", "ocis.f595", "fs.loan", "cmas.c808",
                "cmas.c812", "cmas.c818", "cmas.c846", "cbs.e226.ltb", "cbs.e226.hlx", "cbs.e226.ver", "cbs.e226.bos",
                "cbs.e469.ltb", "cbs.e469.hlx", "cbs.e469.ver", "cbs.e469.bos",
                "cbs.e502.ltb", "cbs.e502.hlx", "cbs.e502.ver", "cbs.e502.bos", "cbs.e032.ltb", "cbs.e032.hlx",
                "cbs.e032.ver", "cbs.e032.bos", "pega", "generatedocument", "ocis.f061", "ocis.f062", "ocis.f060",
                "soa.sas", "soa.ipm", "soa.dms", "ocis.c241", "ocis.c658", "ocis.c234", "asm.f425", "tms.x741", "rpc",
                "fs.application", "fs.account", "sendcommunication", "schedulecommunication");
    }

    @Override
    protected Collection<String> getDatabaseNames() {
        return Arrays.asList("ibPamData", "switches", "ibPrdData");
    }
}
