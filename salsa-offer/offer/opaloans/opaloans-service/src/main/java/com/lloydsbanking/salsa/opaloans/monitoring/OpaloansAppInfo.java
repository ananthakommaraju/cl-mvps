package com.lloydsbanking.salsa.opaloans.monitoring;

import com.lloydsbanking.salsa.appinfo.AbstractSalsaServiceAppInfo;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;

public class OpaloansAppInfo extends AbstractSalsaServiceAppInfo {
    private static final Logger LOGGER = Logger.getLogger(OpaloansAppInfo.class);

    @Override
    protected Collection<String> getServiceNames() {
        LOGGER.info("OpaloansAppInfo:getServiceNames()");
        return Arrays.asList(
                "ocis.f336",
                "ocis.f061",
                "ocis.c216",
                "fs.loan",
                "eligibility",
                "eidv.x711.bos",
                "eidv.x711.ltb",
                "eidv.x711.ver",
                "eidv.x711.hlx");
    }

    @Override
    protected Collection<String> getDatabaseNames() {
        LOGGER.info("OpalaonsAppInfo:getDatabaseNames()");
        return Arrays.asList(
                "ibPamData",
                "ibPrdData",
                "switches");
    }
}
