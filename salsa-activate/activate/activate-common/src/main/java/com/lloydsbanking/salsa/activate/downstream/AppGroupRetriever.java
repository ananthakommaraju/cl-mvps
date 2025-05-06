package com.lloydsbanking.salsa.activate.downstream;

import com.lloydsbanking.salsa.appgroup.AppGroup;
import com.lloydsbanking.salsa.brand.Channel;
import com.lloydsbanking.salsa.downstream.account.client.AccountClient;
import com.lloydsbanking.salsa.downstream.account.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.fs.account.StHeader;
import com.lloydstsb.ib.wsbridge.account.StB766ARetrieveCBSAppGroup;
import com.lloydstsb.ib.wsbridge.account.StB766BRetrieveCBSAppGroup;
import com.lloydstsb.schema.enterprise.lcsm.BAPIHeader;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_gmo.messages.Header;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.WebServiceException;

@Repository
public class AppGroupRetriever {
    private static final Logger LOGGER = Logger.getLogger(AppGroupRetriever.class);

    @Autowired
    AccountClient accountClient;

    @Autowired
    BapiHeaderToStHeaderConverter bapiHeaderToStHeaderConverterAccount;

    @Autowired
    HeaderRetriever headerRetriever;


    public String callRetrieveCBSAppGroup(RequestHeader header, String sortCode) {
        String cbsAppGroup = AppGroup.fromString(Channel.getBrandForChannel(Channel.fromString(headerRetriever.getBapiInformationHeader(header.getLloydsHeaders()).getBAPIHeader().getChanid())).asString()).asString();

        BAPIHeader bapiHeader = headerRetriever.getBapiInformationHeader(header.getLloydsHeaders()).getBAPIHeader();
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());

        StHeader stHeader = bapiHeaderToStHeaderConverterAccount.convert(bapiHeader, serviceRequest, contactPoint.getContactPointId());

        StB766ARetrieveCBSAppGroup stB766ARetrieveCBSAppGroup = createB766Request(stHeader, sortCode);
        stB766ARetrieveCBSAppGroup.getStheader().setIpAddressCaller(getIpAddress(header)+ "," + getClientIpAddress(header));
        StB766BRetrieveCBSAppGroup stB766BRetrieveCBSAppGroup = null;

        try {
            stB766BRetrieveCBSAppGroup = accountClient.retrieveCBSAppGroup(stB766ARetrieveCBSAppGroup);
        }
        catch (WebServiceException e) {
            LOGGER.info("Error retrieving CBSAppGroup (FS Account B766) and this exception will be consumed", e);

        }

        if (stB766BRetrieveCBSAppGroup != null && stB766BRetrieveCBSAppGroup.getSterror() != null && stB766BRetrieveCBSAppGroup.getSterror().getErrorno() == 0) {
            cbsAppGroup = stB766BRetrieveCBSAppGroup.getCbsappgroup();
        }
        return cbsAppGroup;
    }

    public StB766ARetrieveCBSAppGroup createB766Request(StHeader stHeader, String sortCode) {
        StB766ARetrieveCBSAppGroup stB766ARetrieveCBSAppGroup = new StB766ARetrieveCBSAppGroup();

        stB766ARetrieveCBSAppGroup.setStheader(stHeader);
        stB766ARetrieveCBSAppGroup.setSortcode(sortCode);

        return stB766ARetrieveCBSAppGroup;
    }


    private String getIpAddress(Header header) {
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header);
        return serviceRequest.getFrom();
    }

    private String getClientIpAddress(Header header) {
        BapiInformation bapiInformation = headerRetriever.getBapiInformationHeader(header);
        return bapiInformation.getBAPIHeader().getIpAddressCaller();
    }
}
