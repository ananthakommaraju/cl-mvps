package com.lloydsbanking.salsa.apasa.service.fulfil.downstream;


import com.lloydsbanking.salsa.activate.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.apasa.service.fulfil.convert.E502RequestFactory;
import com.lloydsbanking.salsa.brand.Channel;
import com.lloydsbanking.salsa.downstream.cbs.client.e502.E502Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.cbs.e502.objects.E502Req;
import com.lloydsbanking.salsa.soap.cbs.e502.objects.E502Resp;
import com.lloydstsb.schema.infrastructure.soap.CBSAppGrp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.Condition;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.ExtraConditions;
import lib_sim_bo.businessobjects.RuleCondition;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.WebServiceException;
import java.util.Map;

@Repository
public class AmendRollOverAccount {
    public static final int E502_REASON_CODE = 202;
    public static final String E502_REASON_TEXT = "Error from E502";
    public static final String E502_CONDITION_NAME = "LINKING";
    public static final String E502_RESULT_TRUE = "TRUE";
    public static final String E502_RESULT_FALSE = "FALSE";
    private static final Logger LOGGER = Logger.getLogger(CreateStandingOrder.class);
    @Autowired
    AppGroupRetriever appGroupRetriever;

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    E502RequestFactory requestFactory;

    Map<String, E502Client> cbsE502ClientMap;


    public ExtraConditions amendRollOverAccount(DepositArrangement depositArrangement, RequestHeader requestHeader) {
        LOGGER.info("Entering  E502 amend roll over account");
        ExtraConditions extraConditions = null;
        String cbsAppGroup = depositArrangement.getFinancialInstitution().getChannel();
        E502Req request = requestFactory.convert(depositArrangement.getAccountDetails().getAccountNumber(), depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode() + depositArrangement.getAccountNumber());
        CBSAppGrp cbsAppGrp = new CBSAppGrp();
        cbsAppGrp.setCBSApplicationGroupNumber(cbsAppGroup);
        E502Resp e502Resp;
        try {
            ContactPoint contactPoint = headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
            SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());
            ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(), "http://www.lloydstsb.com/Schema/Personal/RetailParty/CBS", "E502");
            e502Resp = e502Client(headerRetriever.getChannelId(requestHeader)).amendRollOverAccount(request, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp);
            extraConditions = checkResponse(e502Resp, depositArrangement);
        } catch (WebServiceException e) {
            LOGGER.info("Exception occurred while calling Amend Roll Over Account. Returning ResourceNotAvailableError ;", e);
            extraConditions = assignResponse(new ExtraConditions(), new Condition(), depositArrangement, E502_REASON_TEXT, E502_REASON_CODE, null, E502_CONDITION_NAME);
        }
        LOGGER.info("Exiting  E502 amend roll over account");
        return extraConditions;
    }

    public ExtraConditions checkResponse(E502Resp resp, DepositArrangement depositArrangement) {
        ExtraConditions extraConditions = null;
        if (resp != null) {
            if (resp.getE502Result() != null && resp.getE502Result().getResultCondition() != null) {
                Integer reasonCode = resp.getE502Result().getResultCondition().getReasonCode();
                int severityCode = resp.getE502Result().getResultCondition().getSeverityCode();
                String reasonText = resp.getE502Result().getResultCondition().getReasonText();
                if (((null != reasonCode && reasonCode > 0) || severityCode > 0)) {
                    LOGGER.info("Exception occurred while calling Amend Roll Over Account. Returning ExternalServiceError " + reasonCode);
                    extraConditions = assignResponse(new ExtraConditions(), new Condition(), depositArrangement, reasonText, E502_REASON_CODE, E502_RESULT_FALSE, E502_CONDITION_NAME);
                } else {
                    extraConditions = assignResponse(null, null, depositArrangement, null, 0, E502_RESULT_TRUE, E502_CONDITION_NAME);
                }
            }

        }
        return extraConditions;
    }

    private ExtraConditions assignResponse(ExtraConditions extraConditions, Condition condition, DepositArrangement depositArrangement, String reasonText, int reasonCode, String result, String reasonName) {
        if (condition != null) {
            condition.setReasonCode(String.valueOf(reasonCode));
            condition.setReasonText(reasonText);
            if (!CollectionUtils.isEmpty(depositArrangement.getConditions())) {
                depositArrangement.getConditions().get(0).setReasonText(reasonText);
            }
            extraConditions.getConditions().add(condition);
        }
        if (CollectionUtils.isEmpty(depositArrangement.getConditions())) {
            //Seems overting the the first condition but it is as present in WPS
            depositArrangement.getConditions().add(new RuleCondition());
        }
        depositArrangement.getConditions().get(0).setName(reasonName);
        if (result != null) {
            depositArrangement.getConditions().get(0).setResult(result);
        }
        return extraConditions;
    }


    private E502Client e502Client(String channelAsString) {
        String brand = Channel.getBrandForChannel(Channel.fromString(channelAsString)).asString();

        return cbsE502ClientMap.get(brand);
    }


    public Map<String, E502Client> getCbsE502ClientMap() {
        return cbsE502ClientMap;
    }

    public void setCbsE502ClientMap(Map<String, E502Client> cbsE502ClientMap) {
        this.cbsE502ClientMap = cbsE502ClientMap;
    }
}
