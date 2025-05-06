package com.lloydsbanking.salsa.eligibility.monitoring;

import com.lloydsbanking.salsa.appinfo.AbstractSalsaServiceAppInfo;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;

public class EligibilityAppInfo extends AbstractSalsaServiceAppInfo {
    private static final Logger LOGGER = Logger.getLogger(EligibilityAppInfo.class);

    @Override
    public Collection<String> getServiceNames() {
        LOGGER.info("EligibilityAppInfo:getServiceNames()");
        return Arrays.asList("ocis.f075", "ocis.f336", "fs.user", "fs.account",
                "cbs.e184.ltb", "cbs.e184.ver", "cbs.e184.hlx", "cbs.e184.bos",
                "cbs.e220.ltb", "cbs.e220.ver", "cbs.e220.hlx", "cbs.e220.bos",
                "cbs.e591.ltb", "cbs.e591.ver", "cbs.e591.hlx", "cbs.e591.bos",
                "cbs.e141.ltb", "cbs.e141.ver", "cbs.e141.hlx", "cbs.e141.bos");
    }

    @Override
    public Collection<String> getDatabaseNames() {
        LOGGER.info("EligibilityAppInfo:getDatabaseNames()");
        return Arrays.asList("ibRefData", "switches", "ibPamData", "ibPrdData");
    }

}
