package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.brand.Channel;
import com.lloydsbanking.salsa.downstream.cbs.client.e141.E141Client;
import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.header.HeaderRetriever;
import com.lloydsbanking.salsa.soap.cbs.e141.objects.CBSRequestGp;
import com.lloydsbanking.salsa.soap.cbs.e141.objects.E141Req;
import com.lloydsbanking.salsa.soap.cbs.e141.objects.E141Resp;
import com.lloydsbanking.salsa.soap.cbs.e141.objects.Indicator2Gp;
import com.lloydsbanking.salsa.soap.cbs.e141.objects.StandardIndicators2Gp;
import com.lloydsbanking.salsa.soap.cbs.e141.objects.StandardIndicatorsGp;
import com.lloydstsb.schema.infrastructure.soap.CBSAppGrp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lb_gbo_sales.messages.RequestHeader;
import lib_sim_bo.businessobjects.ProductArrangementIndicator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.WebServiceException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckBalanceRetriever {
    private static final Logger LOGGER = Logger.getLogger(CheckBalanceRetriever.class);

    Map<String, E141Client> cbsE141ClientMap;

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    SwitchService switchClient;

    private static final String SWITCH_CBS_GENERIC_GATEWAY = "SW_CBSGenGtwy";

    private static final String DEFAULT_BRAND = "LTB";

    private static final String EXTERNAL_SERVICE_ERROR_CODE = "00728100";

    public static final int DEFAULT_MAX_REPEAT_GROUP = 0;

    public static final int DEFAULT_INPUT_OFFICER_FLAG_STATUS = 0;

    public static final int DEFAULT_OVERRIDE_DETAILS_CODE = 0;

    public static final int PROHIBITIVE_INDICATOR_ERROR = 2958;

    public E141Resp getCheckBalance(RequestHeader header, String sortCode, String accountNumber, String cbsAppGroup) throws SalsaInternalResourceNotAvailableException, SalsaExternalServiceException {
        LOGGER.info("Calling E141 sortCode: " + sortCode + " accountNumber: " + accountNumber);
        E141Req e141Req = createCheckBalanceRequest(sortCode, accountNumber);
        return getProductArrangementIndicators(e141Req, header, cbsAppGroup);
    }

    public List<ProductArrangementIndicator> getCBSIndicators(RequestHeader header, String sortCode, String accountNumber, String cbsAppGroup) throws SalsaInternalResourceNotAvailableException, SalsaExternalServiceException {
        LOGGER.info("Calling E141 sortCode: " + sortCode + " accountNumber: " + accountNumber);
        E141Resp e141Resp = this.getCheckBalance(header, sortCode, accountNumber, cbsAppGroup);
        List<ProductArrangementIndicator> indicatorList = new ArrayList<>();
        if (e141Resp != null && e141Resp.getE141Result() != null && e141Resp.getE141Result().getResultCondition() != null) {
            if (null == e141Resp.getE141Result().getResultCondition().getReasonCode() || PROHIBITIVE_INDICATOR_ERROR != e141Resp.getE141Result().getResultCondition().getReasonCode()) {
                indicatorList = convertToIndicatorListFrom2GP(e141Resp.getIndicator2Gp());
            }
            else if (null != e141Resp.getIndicatorGp()) {
                for (StandardIndicatorsGp indicatorsGp : e141Resp.getIndicatorGp().getStandardIndicatorsGp()) {
                    if (null != indicatorsGp) {
                        ProductArrangementIndicator indicator = new ProductArrangementIndicator();
                        indicator.setCode(indicatorsGp.getIndicatorCd());
                        if (!StringUtils.isEmpty(indicatorsGp.getIndicatorTx())) {
                            indicator.setText(indicatorsGp.getIndicatorTx());
                        }
                        indicatorList.add(indicator);
                    }
                }
            }

        }
        LOGGER.info("IndicatorList size returned by E141: " + indicatorList.size());
        return indicatorList;

    }

    private List<ProductArrangementIndicator> convertToIndicatorListFrom2GP(Indicator2Gp indicator2Gp) {
        List<ProductArrangementIndicator> productArrangementIndicators = new ArrayList<>();
        if (null != indicator2Gp) {
            if (!CollectionUtils.isEmpty(indicator2Gp.getStandardIndicators2Gp())) {
                for (StandardIndicators2Gp indicators2Gp : indicator2Gp.getStandardIndicators2Gp()) {
                    ProductArrangementIndicator indicator = new ProductArrangementIndicator();
                    indicator.setCode(indicators2Gp.getIndicator2Cd());
                    if (!StringUtils.isEmpty(indicators2Gp.getIndicator2Tx())) {
                        indicator.setText(indicators2Gp.getIndicator2Tx());
                    }
                    productArrangementIndicators.add(indicator);
                }
            }
        }
        return productArrangementIndicators;
    }

    private E141Resp getProductArrangementIndicators(E141Req request, RequestHeader header, String cbsAppGroup) throws SalsaInternalResourceNotAvailableException, SalsaExternalServiceException {
        E141Resp e141Resp;
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        CBSAppGrp cbsAppGrp = new CBSAppGrp();
        cbsAppGrp.setCBSApplicationGroupNumber(cbsAppGroup);

        try {
            e141Resp = e141Client(headerRetriever.getBapiInformationHeader(header.getLloydsHeaders()).getBAPIHeader().getChanid()).getProductArrangementIndicator(request, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp);
        }
        catch (WebServiceException e) {
            String message = "Exception occurred while calling E141. Returning Resource Not available error ";
            LOGGER.error(message, e);
            throw new SalsaInternalResourceNotAvailableException(message, e);
        }

        if (e141Resp != null && e141Resp.getE141Result() != null && e141Resp.getE141Result().getResultCondition() != null) {
            checkE141Error(e141Resp.getE141Result().getResultCondition());
        }
        return e141Resp;
    }

    private void checkE141Error(ResultCondition resultCondition) throws SalsaExternalServiceException {

        if (resultCondition.getSeverityCode() != (byte) 0 && (null != resultCondition.getReasonCode() && 0 < resultCondition.getReasonCode() && PROHIBITIVE_INDICATOR_ERROR != resultCondition.getReasonCode())) {
            String message = "E141 responded with non zero severity code. Returning ExternalServiceError. ReasonCode | ReasonText " + resultCondition.getReasonCode() + " | " + resultCondition.getReasonText();
            LOGGER.error(message);
            throw new SalsaExternalServiceException(message, EXTERNAL_SERVICE_ERROR_CODE, resultCondition.getReasonText());
        }
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

    private E141Client e141Client(String channelAsString) {
        String brand = DEFAULT_BRAND;
        if (isGenericGatewayEnabled(channelAsString)) {
            Channel channel = Channel.fromString(channelAsString);
            brand = Channel.getBrandForChannel(channel).asString();
        }
        return cbsE141ClientMap.get(brand);
    }

    public Map<String, E141Client> getCbsE141ClientMap() {
        return cbsE141ClientMap;
    }

    public void setCbsE141ClientMap(Map<String, E141Client> cbsE141ClientMap) {
        this.cbsE141ClientMap = cbsE141ClientMap;
    }

    private E141Req createCheckBalanceRequest(String sortCode, String accNo) {
        E141Req request = new E141Req();

        request.setMaxRepeatGroupQy(DEFAULT_MAX_REPEAT_GROUP);
        request.setCBSRequestGp(new CBSRequestGp());
        request.getCBSRequestGp().setInputOfficerFlagStatusCd(DEFAULT_INPUT_OFFICER_FLAG_STATUS);
        request.getCBSRequestGp().setOverrideDetailsCd(DEFAULT_OVERRIDE_DETAILS_CODE);
        request.setCBSAccountNoId(sortCode.concat(accNo));
        return request;
    }
}
