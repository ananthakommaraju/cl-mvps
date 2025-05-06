package com.lloydsbanking.salsa.opasaving.monitoring;

import com.lloydsbanking.salsa.appinfo.AbstractSalsaServiceAppInfo;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;

public class OpaSavingAppInfo extends AbstractSalsaServiceAppInfo {
    private static final Logger LOGGER = Logger.getLogger(OpaSavingAppInfo.class);

    @Override
    protected Collection<String> getServiceNames() {
        LOGGER.info("OpaSavingAppInfo:getServiceNames()");
        return Arrays.asList(
                "ocis.f447",
                "ocis.f336",
                "ocis.f061",
                "ocis.f062",
                "asm.f204",
                "dcpc",
                "eidv.x711.bos",
                "eidv.x711.ltb",
                "eidv.x711.ver",
                "eidv.x711.hlx",
                "rpc",
                "eligibility");
    }

    @Override
    protected Collection<String> getDatabaseNames() {

        LOGGER.info("OpaSavingAppInfo:getServiceNames()");
        return Arrays.asList(
                "ibPamData",
                "ibPrdData",
                "switches");
    }
}
