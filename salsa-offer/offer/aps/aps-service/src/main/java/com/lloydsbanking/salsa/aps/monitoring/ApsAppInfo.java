package com.lloydsbanking.salsa.aps.monitoring;

import com.lloydsbanking.salsa.appinfo.AbstractSalsaServiceAppInfo;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ApsAppInfo extends AbstractSalsaServiceAppInfo {
    private static final Logger LOGGER = Logger.getLogger(ApsAppInfo.class);
    @Override
    protected Collection<String> getServiceNames() {
        LOGGER.info("ApsAppInfo:getServiceNames()");
        return new ArrayList<String>();
    }

    @Override
    protected Collection<String> getDatabaseNames() {

        LOGGER.info("ApsAppInfo:getServiceNames()");
        return Arrays.asList(
                "ibPrdData");
    }
}
