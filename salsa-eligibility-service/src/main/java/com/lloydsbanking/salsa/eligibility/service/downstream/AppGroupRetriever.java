package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.appgroup.AppGroup;
import com.lloydsbanking.salsa.brand.Channel;
import com.lloydsbanking.salsa.downstream.account.client.AccountClient;
import com.lloydsbanking.salsa.downstream.account.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.header.HeaderRetriever;
import com.lloydsbanking.salsa.soap.fs.account.StHeader;
import com.lloydstsb.ib.wsbridge.account.StB766ARetrieveCBSAppGroup;
import com.lloydstsb.ib.wsbridge.account.StB766BRetrieveCBSAppGroup;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import lb_gbo_sales.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class AppGroupRetriever {
    private static final Logger LOGGER = Logger.getLogger(AppGroupRetriever.class);

    @Autowired
    AccountClient accountClient;

    @Autowired
    BapiHeaderToStHeaderConverter bapiHeaderToStHeaderConverter;

    @Autowired
    HeaderRetriever headerRetriever;

    public static final String DEFAULT_CBS_APP_GROUP = "01";

    public String callRetrieveCBSAppGroup(RequestHeader requestHeader, String sortCode, boolean isWzRequest) {
        LOGGER.info("Entering AppGroupRetriever and sortCode: " + sortCode);
        String cbsAppGroup;
        if (isWzRequest) {
            cbsAppGroup = AppGroup.fromString(Channel.getBrandForChannel(Channel.fromString(requestHeader.getChannelId())).asString()).asString();
        }
        else {
            cbsAppGroup = DEFAULT_CBS_APP_GROUP;
        }
        ContactPoint contactPoint = headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        StHeader stHeader = bapiHeaderToStHeaderConverter.convert(headerRetriever.getBapiInformationHeader(requestHeader.getLloydsHeaders()).getBAPIHeader(), headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders()), contactPoint.getContactPointId());

        StB766ARetrieveCBSAppGroup stB766ARetrieveCBSAppGroup = createRequest(stHeader, sortCode);
        StB766BRetrieveCBSAppGroup stB766BRetrieveCBSAppGroup = null;

        try {
            stB766BRetrieveCBSAppGroup = accountClient.retrieveCBSAppGroup(stB766ARetrieveCBSAppGroup);
        }
        catch (Exception e) {
            LOGGER.error("Error retrieving CBSAppGroup (FS Account B766) ", e);
        }
        if (null != stB766BRetrieveCBSAppGroup && null != stB766BRetrieveCBSAppGroup.getSterror() &&
            stB766BRetrieveCBSAppGroup.getSterror().getErrorno() == 0) {
            cbsAppGroup = stB766BRetrieveCBSAppGroup.getCbsappgroup();
            LOGGER.info("CBS App group value : " + cbsAppGroup + " for sortCode: " + sortCode);
        }

        return cbsAppGroup;
    }

    private StB766ARetrieveCBSAppGroup createRequest(StHeader stHeader, String sortCode) {
        StB766ARetrieveCBSAppGroup stB766ARetrieveCBSAppGroup = new StB766ARetrieveCBSAppGroup();

        stB766ARetrieveCBSAppGroup.setStheader(stHeader);
        stB766ARetrieveCBSAppGroup.setSortcode(sortCode);

        return stB766ARetrieveCBSAppGroup;
    }
}

