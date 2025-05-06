package com.lloydsbanking.salsa.apasa.service.fulfil.downstream;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.apasa.service.fulfil.convert.E032RequestFactory;
import com.lloydsbanking.salsa.brand.Channel;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.cbs.client.e032.E032Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.cbs.e032.objects.E032Req;
import com.lloydsbanking.salsa.soap.cbs.e032.objects.E032Resp;
import com.lloydstsb.schema.infrastructure.soap.CBSAppGrp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.Condition;
import lib_sim_bo.businessobjects.ExtraConditions;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.WebServiceException;
import java.util.Map;

@Repository
public class CreateStandingOrder {

    private static final Logger LOGGER = Logger.getLogger(CreateStandingOrder.class);

    private static final int EXTERNAL_BUSINESS_ERROR = 8210002;

    private static final String RESOURCE_NOT_AVAILABLE_ERROR = "820001";

    private static final String EXTERNAL_SERVICE_ERROR = "827001";

    @Autowired
    AppGroupRetriever appGroupRetriever;

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    E032RequestFactory requestFactory;

    @Autowired
    UpdateDepositArrangementConditionAndApplicationStatusHelper updateAppDetails;

    Map<String, E032Client> cbsE032ClientMap;

    public ExtraConditions e032CreateStandingOrder(String cbsAppGroup, String sortCode, String accNo, String beneficiaryAccountNumber, String beneficiarySortCode, RequestHeader requestHeader, String transactionName, ApplicationDetails applicationDetails, Integer retryCount) {
        E032Req request = requestFactory.convert(sortCode, accNo, beneficiaryAccountNumber, beneficiarySortCode, transactionName);
        CBSAppGrp cbsAppGrp = new CBSAppGrp();
        cbsAppGrp.setCBSApplicationGroupNumber(cbsAppGroup);
        E032Resp e032Resp = null;
        ExtraConditions extraConditions = null;
        try {
            ContactPoint contactPoint = headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
            SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());
            ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(), "http://www.lloydstsb.com/Schema/Personal/RetailParty/CBS", "E032");
            e032Resp = e032Client(headerRetriever.getChannelId(requestHeader)).createStandingOrder(request, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp);
            extraConditions = checkResponse(e032Resp, applicationDetails, retryCount);

        } catch (WebServiceException e) {
            LOGGER.info("Exception occurred while calling Retrieve standing Payment Definition. Returning ResourceNotAvailableError ;", e);
            updateAppDetails.setApplicationDetails(retryCount, RESOURCE_NOT_AVAILABLE_ERROR, e.getMessage(), ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.STANDING_ORDER_CREATION_FAILURE, applicationDetails);
            extraConditions = assignResponse(new ExtraConditions(), new Condition(), RESOURCE_NOT_AVAILABLE_ERROR, e.getMessage());
        }
        return extraConditions;
    }

    public ExtraConditions checkResponse(E032Resp resp, ApplicationDetails applicationDetails, Integer retryCount) {
        ExtraConditions extraConditions = null;
        if (resp != null) {
            if (resp.getE032Result() != null && resp.getE032Result().getResultCondition() != null) {
                Integer reasonCode = resp.getE032Result().getResultCondition().getReasonCode();
                int severityCode = resp.getE032Result().getResultCondition().getSeverityCode();
                if ((reasonCode != null && reasonCode == EXTERNAL_BUSINESS_ERROR) || severityCode > 0) {
                    LOGGER.info("External error occurred while calling Create Standing Order. Returning ExternalBusinessError " + reasonCode);
                    updateAppDetails.setApplicationDetails(retryCount, String.valueOf(reasonCode), resp.getE032Result().getResultCondition().getReasonText(), ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.STANDING_ORDER_CREATION_FAILURE, applicationDetails);
                    extraConditions = assignResponse(new ExtraConditions(), new Condition(), EXTERNAL_SERVICE_ERROR, resp.getE032Result().getResultCondition().getReasonText());
                }
            }
        }
        return extraConditions;
    }

    private ExtraConditions assignResponse(ExtraConditions extraConditions, Condition condition, String reasonCode, String reasonText) {
        if (condition != null) {
            condition.setReasonCode(reasonCode);
            condition.setReasonText(reasonText);
            extraConditions.getConditions().add(condition);
        }
        return extraConditions;
    }

    private E032Client e032Client(String channelAsString) {
        String brand = Channel.getBrandForChannel(Channel.fromString(channelAsString)).asString();
        return cbsE032ClientMap.get(brand);
    }

    public Map<String, E032Client> getCbsE032ClientMap() {
        return cbsE032ClientMap;
    }

    public void setCbsE032ClientMap(Map<String, E032Client> cbsE032ClientMap) {
        this.cbsE032ClientMap = cbsE032ClientMap;
    }
}
