package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.brand.ChannelToBrandMapping;
import com.lloydsbanking.salsa.downstream.cbs.client.e184.E184Client;
import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import com.lloydsbanking.salsa.eligibility.service.utility.ExceptionUtility;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.ReasonText;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.header.HeaderRetriever;
import com.lloydsbanking.salsa.soap.cbs.e184.objects.E184Req;
import com.lloydsbanking.salsa.soap.cbs.e184.objects.E184Resp;
import com.lloydsbanking.salsa.soap.cbs.e184.objects.StandardIndicators1Gp;
import com.lloydstsb.schema.infrastructure.soap.CBSAppGrp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lb_gbo_sales.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class CBSIndicatorRetriever {
    private static final Logger LOGGER = Logger.getLogger(CBSIndicatorRetriever.class);

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    ExceptionUtility exceptionUtility;

    @Autowired
    SwitchService switchClient;

    Map<String, E184Client> cbsE184ClientMap;

    @Autowired
    ChannelToBrandMapping channelToBrandMapping;

    private static final String SWITCH_CBS_GENERIC_GATEWAY = "SW_CBSGenGtwy";

    private static final String DEFAULT_BRAND = "LTB";

    public List<StandardIndicators1Gp> getCbsIndicator(RequestHeader header, String sortCode, String accountNumber, String cbsAppGroup) throws SalsaInternalServiceException, SalsaInternalResourceNotAvailableException {
        E184Req e184Req = createCbsIndicatorRequest(header, sortCode, accountNumber);
        return retrieveE184Response(e184Req, header, cbsAppGroup);
    }

    private E184Req createCbsIndicatorRequest(RequestHeader header, String sortCode, String accountNumber) throws SalsaInternalServiceException {
        E184Req e184Req = new E184Req();
        try {
            e184Req.setCBSAccountNoId(sortCode + accountNumber);
        }
        catch (Exception e) {
            String message = "Exception occurred while creating request for E184. Returning InternalServiceError ;";
            LOGGER.error(message, e);
            throw new SalsaInternalServiceException(message, null, new ReasonText(e.getMessage()));
        }
        return e184Req;
    }

    private List<StandardIndicators1Gp> retrieveE184Response(E184Req e184Req, RequestHeader header, String cbsAppGroup) throws SalsaInternalResourceNotAvailableException {
        E184Resp e184Resp = null;
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders(), "{http://www.lloydstsb.com/Schema/Enterprise/LCSM_CommunicationManagement}CommunicationAcceptanceService", "determineEligibleCustomerInstruction");
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        CBSAppGrp cbsAppGrp = new CBSAppGrp();
        cbsAppGrp.setCBSApplicationGroupNumber(cbsAppGroup);
        try {
            LOGGER.info("Calling E184 for CBSAccountNumberId: " + e184Req.getCBSAccountNoId());
            e184Resp = e184Client(headerRetriever.getBapiInformationHeader(header).getBAPIHeader().getChanid()).getCbsIndicator(e184Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp);
        }
        catch (Exception e) {
            String message = "Exception occurred while calling E184. Returning Resource Not available error ";
            LOGGER.error(message, e);
            throw new SalsaInternalResourceNotAvailableException(message, e);

        }
        LOGGER.info("Size of indicators list returned by E184: " + e184Resp.getIndicator1Gp().getStandardIndicators1Gp().size());
        return e184Resp.getIndicator1Gp().getStandardIndicators1Gp();
    }

    private boolean isGenericGatewayEnabled(String channel) {
        try {
            return switchClient.getBrandedSwitchValue(SWITCH_CBS_GENERIC_GATEWAY, channel, false);
        }
        catch (Exception e) {
            LOGGER.info("Error occurred while fetching Switch value for channel " + channel + e);
            return false;
        }
    }

    private E184Client e184Client(String channel) {
        String brand = DEFAULT_BRAND;
        if (isGenericGatewayEnabled(channel)) {
            brand = channelToBrandMapping.getBrandForChannel(channel);
        }
        return cbsE184ClientMap.get(brand);
    }

    public Map<String, E184Client> getCbsE184ClientMap() {
        return cbsE184ClientMap;
    }

    public void setCbsE184ClientMap(Map<String, E184Client> cbsE184ClientMap) {
        this.cbsE184ClientMap = cbsE184ClientMap;
    }

}