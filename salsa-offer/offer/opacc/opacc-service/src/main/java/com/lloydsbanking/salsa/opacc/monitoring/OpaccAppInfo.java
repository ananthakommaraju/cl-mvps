package com.lloydsbanking.salsa.opacc.monitoring;

import com.lloydsbanking.salsa.appinfo.AbstractSalsaServiceAppInfo;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;

public class OpaccAppInfo extends AbstractSalsaServiceAppInfo {
    private static final Logger LOGGER = Logger.getLogger(OpaccAppInfo.class);

    @Override
    protected Collection<String> getServiceNames() {
        LOGGER.info("OpaccAppInfo:getServiceNames()");

        return Arrays.asList(
                "ocis.f447",
                "ocis.f336",
                "ocis.f061",
                "ocis.f062",
                "asm.f424",
                "eidv.x711.bos",
                "eidv.x711.ltb",
                "eidv.x711.ver",
                "eidv.x711.hlx",
                "dp.encrypt",
                "rpc",
                "eligibility");
    }

    @Override
    protected Collection<String> getDatabaseNames() {
        LOGGER.info("OpaccAppInfo:getDatabaseNames()");
        return Arrays.asList(
                "ibPamData",
                "ibPrdData",
                "switches");
    }
}
