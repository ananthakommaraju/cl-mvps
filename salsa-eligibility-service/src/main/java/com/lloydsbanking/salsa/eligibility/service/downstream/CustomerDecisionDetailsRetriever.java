package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.brand.Channel;
import com.lloydsbanking.salsa.downstream.cbs.client.e591.E591Client;
import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.header.HeaderRetriever;
import com.lloydsbanking.salsa.soap.cbs.e591.objects.CustNoGp;
import com.lloydsbanking.salsa.soap.cbs.e591.objects.DecnSubGp;
import com.lloydsbanking.salsa.soap.cbs.e591.objects.E591Req;
import com.lloydsbanking.salsa.soap.cbs.e591.objects.E591Resp;
import com.lloydstsb.schema.infrastructure.soap.CBSAppGrp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lb_gbo_sales.messages.RequestHeader;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.WebServiceException;
import java.util.Map;

@Repository
public class CustomerDecisionDetailsRetriever {
    private static final Logger LOGGER = Logger.getLogger(CustomerDecisionDetailsRetriever.class);

    @Autowired
    HeaderRetriever headerRetriever;

    Map<String, E591Client> cbsE591ClientMap;

    private static final String DEFAULT_BRAND = "LTB";

    private static final String SWITCH_CBS_GENERIC_GATEWAY = "SW_CBSGenGtwy";

    private static final int MAX_REPEAT_GROUP_QTY = 10;

    private static final int CAPS_SHADOW_DECISION_SCR_FLAG_CODE = 1;

    private static final String CAPS_SHADOW_DECISION_SCR_CODE = "A";

    public static final int MAX_LENGTH = 14;

    @Autowired
    SwitchService switchClient;

    public DecnSubGp getCustomerDecisionDetails(RequestHeader header, String customerId, String cbsAppGroup) throws SalsaInternalServiceException, SalsaInternalResourceNotAvailableException {
        E591Req request = createCustomerDecisionDetailsRequest(customerId);
        E591Resp response = retrieveCustomerDecisionDetails(header, request, cbsAppGroup);
        if (null != response.getDecisionGp() && !CollectionUtils.isEmpty(response.getDecisionGp().getDecnSubGp())) {
            LOGGER.info("Exiting E591 cbsDecisionCode: " + response.getDecisionGp().getDecnSubGp().get(0).getDcnCdCarLoanFinancIn() + " shadowLimit: " + response.getDecisionGp().getDecnSubGp().get(0).getCarLoanMnhShdwlmtIn() + " riskBand: " + response.getDecisionGp().getDecnSubGp().get(0).getRskBndCdCarLoanIn());
            return response.getDecisionGp().getDecnSubGp().get(0);
        }
        return null;
    }

    private E591Resp retrieveCustomerDecisionDetails(RequestHeader header, E591Req request, String cbsAppGroup) throws SalsaInternalResourceNotAvailableException {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        CBSAppGrp cbsAppGrp = new CBSAppGrp();
        cbsAppGrp.setCBSApplicationGroupNumber(cbsAppGroup);
        try {
            LOGGER.info("Calling E591 with cbsCustomerNo.: " + (null != request.getCustNoGp() ? request.getCustNoGp().getCBSCustNo() : "null"));
            return e591Client(headerRetriever.getBapiInformationHeader(header).getBAPIHeader().getChanid()).enqCbsCustDecnTrl(request, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp);
        }
        catch (WebServiceException e) {
            LOGGER.error("Exception occurred while calling E591. Returning ResourceNotAvailable Error ", e);
            throw new SalsaInternalResourceNotAvailableException(e.getMessage());
        }

    }

    private E591Req createCustomerDecisionDetailsRequest(String customerId) {
        E591Req e591Req = new E591Req();
        e591Req.setMaxRepeatGroupQy(MAX_REPEAT_GROUP_QTY);
        e591Req.setCustNoGp(new CustNoGp());
        if (!StringUtils.isEmpty(customerId)) {
            e591Req.getCustNoGp().setNationalSortcodeId(customerId.substring(0, 2));
            if (customerId.length() > MAX_LENGTH) {
                e591Req.getCustNoGp().setCBSCustNo(customerId.substring(2, MAX_LENGTH));
            }
            else {
                e591Req.getCustNoGp().setCBSCustNo(customerId.substring(2, customerId.length()));
            }
        }
        e591Req.setCAPSShdwDecnScrFlagCd(CAPS_SHADOW_DECISION_SCR_FLAG_CODE);
        e591Req.setCAPSShdwDecnScrCd(CAPS_SHADOW_DECISION_SCR_CODE);
        return e591Req;
    }

    private E591Client e591Client(String channelAsString) {
        String brand = DEFAULT_BRAND;
        if (isGenericGatewayEnabled(channelAsString)) {
            Channel channel = Channel.fromString(channelAsString);
            brand = Channel.getBrandForChannel(channel).asString();
        }
        return cbsE591ClientMap.get(brand);
    }

    public Map<String, E591Client> getCbsE591ClientMap() {
        return cbsE591ClientMap;
    }

    public void setCbsE591ClientMap(Map<String, E591Client> cbsE591ClientMap) {
        this.cbsE591ClientMap = cbsE591ClientMap;
    }

    private boolean isGenericGatewayEnabled(String channel) {
        try {
            return switchClient.getBrandedSwitchValue(SWITCH_CBS_GENERIC_GATEWAY, channel, false);
        }
        catch (WebServiceException e) {
            LOGGER.info("Error occurred while fetching Switch value for channel " + channel + e);
            return false;
        }
    }
}
