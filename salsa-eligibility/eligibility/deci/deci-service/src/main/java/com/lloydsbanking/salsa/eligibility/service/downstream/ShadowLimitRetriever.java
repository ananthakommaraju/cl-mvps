package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.brand.ChannelToBrandMapping;
import com.lloydsbanking.salsa.downstream.cbs.client.e220.E220Client;
import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import com.lloydsbanking.salsa.eligibility.service.converter.CbsRequestFactory;
import com.lloydsbanking.salsa.eligibility.service.utility.ExceptionUtility;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.header.HeaderRetriever;
import com.lloydsbanking.salsa.soap.cbs.e220.objects.E220Req;
import com.lloydsbanking.salsa.soap.cbs.e220.objects.E220Resp;
import com.lloydstsb.schema.infrastructure.soap.CBSAppGrp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lb_gbo_sales.messages.RequestHeader;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class ShadowLimitRetriever {
    private static final Logger LOGGER = Logger.getLogger(ShadowLimitRetriever.class);

    @Autowired
    CbsRequestFactory cbsRequestFactory;

    Map<String, E220Client> cbsE220ClientMap;

    @Autowired
    ExceptionUtility exceptionUtility;

    @Autowired
    ChannelToBrandMapping channelToBrandMapping;

    @Autowired
    SwitchService switchClient;

    @Autowired
    HeaderRetriever headerRetriever;

    private static final String SWITCH_CBS_GENERIC_GATEWAY = "SW_CBSGenGtwy";

    private static final String DEFAULT_BRAND = "LTB";

    public String getShadowLimit(RequestHeader header, String sortCode, String customerId, String cbsAppGroup) throws SalsaInternalServiceException, SalsaInternalResourceNotAvailableException {
        LOGGER.info("Entering ShadowLimitRetriever getShadowLimit. customerId: " + customerId + " sortCode: "+ sortCode);
        E220Req request = createShadowLimitRequest(header, sortCode, customerId);
        E220Resp response = retrieveShadowLimit(request, header, cbsAppGroup);
        if (null != response.getDecisionGp() && !CollectionUtils.isEmpty(response.getDecisionGp().getDecnSubGp())) {
            LOGGER.info("Exiting ShadowLimitRetriever getShadowLimit. ShadowLimit: " + response.getDecisionGp().getDecnSubGp().get(0).getShdwDcnLoanLwrLmtAm());
            return response.getDecisionGp().getDecnSubGp().get(0).getShdwDcnLoanLwrLmtAm();
        }
        return null;
    }

    public int getStrictFlag(RequestHeader header, String sortCode, String customerId, String cbsAppGroup) throws SalsaInternalServiceException, SalsaInternalResourceNotAvailableException {
        LOGGER.info("Entering ShadowLimitRetriever getStrictFlag. customerId: " + customerId);
        E220Req request = createShadowLimitRequest(header, sortCode, customerId);
        E220Resp response = retrieveShadowLimit(request, header, cbsAppGroup);
        if (null != response.getDecisionGp() && !CollectionUtils.isEmpty(response.getDecisionGp().getDecnSubGp())) {
            LOGGER.info("Exiting ShadowLimitRetriever getStrictFlag. StrictFlag: " + response.getDecisionGp().getDecnSubGp().get(0).getStrictCd());
        }
        return response.getDecisionGp().getDecnSubGp().get(0).getStrictCd();
    }

    private E220Resp retrieveShadowLimit(E220Req request, RequestHeader header, String cbsAppGroup) throws SalsaInternalResourceNotAvailableException {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        CBSAppGrp cbsAppGrp = new CBSAppGrp();
        cbsAppGrp.setCBSApplicationGroupNumber(cbsAppGroup);
        try {
            LOGGER.info("Calling E220 ");
            return e220Client(headerRetriever.getBapiInformationHeader(header).getBAPIHeader().getChanid()).getShadowLimit(request, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp);

        }
        catch (Exception e) { //NOSONAR
            LOGGER.error("Exception occurred while calling E220. Returning ResourceNotAvailable Error ", e);
            throw new SalsaInternalResourceNotAvailableException(e.getMessage());
        }

    }

    private E220Req createShadowLimitRequest(RequestHeader header, String sortCode, String customerId) throws SalsaInternalServiceException {
        try {
            return cbsRequestFactory.createE220Request(sortCode, customerId);
        }
        catch (Exception e) {
            LOGGER.error("Exception occurred while creating request for E220. Returning InternalServiceError ;", e);
            throw new SalsaInternalServiceException(e.getMessage());
        }
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

    private E220Client e220Client(String channel) {
        String brand = DEFAULT_BRAND;
        if (isGenericGatewayEnabled(channel)) {
            brand = channelToBrandMapping.getBrandForChannel(channel);
        }
        return cbsE220ClientMap.get(brand);
    }

    public Map<String, E220Client> getCbsE220ClientMap() {
        return cbsE220ClientMap;
    }

    public void setCbsE220ClientMap(Map<String, E220Client> cbsE220ClientMap) {
        this.cbsE220ClientMap = cbsE220ClientMap;
    }
}
