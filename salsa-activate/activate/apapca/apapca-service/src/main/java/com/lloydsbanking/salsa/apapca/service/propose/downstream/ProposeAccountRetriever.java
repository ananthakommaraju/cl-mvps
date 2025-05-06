package com.lloydsbanking.salsa.apapca.service.propose.downstream;

import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.brand.Channel;
import com.lloydsbanking.salsa.downstream.cbs.client.e229.E229Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.cbs.e229.objects.CBSRequestGp2;
import com.lloydsbanking.salsa.soap.cbs.e229.objects.E229Req;
import com.lloydsbanking.salsa.soap.cbs.e229.objects.E229Resp;
import com.lloydstsb.schema.infrastructure.soap.CBSAppGrp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.WebServiceException;
import java.util.Map;

public class ProposeAccountRetriever {
    private static final Logger LOGGER = Logger.getLogger(ProposeAccountRetriever.class);

    public static final int MAX_REPEAT_GROUP_QY = 0;

    public static final int INPUT_OFFICER_FLAG_STATUS_CD = 0;

    public static final int INPUT_OFFICER_STATUS_CD = 0;

    public static final int OVERRIDE_DETAILS_CD = 0;

    public static final String OVERRIDING_OFFICER_STAFF_NO = "0";

    Map<String, E229Client> cbsE229ClientMap;

    private static final int STEM_NOT_AVAILABLE = 1458;

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    ExceptionUtilityActivate exceptionUtilityActivate;

    public E229Resp proposeAccount(RequestHeader header, String sortCode, String cbsAppGroup) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {

        E229Req e229Req = createE229Request(sortCode);
        return retrieverE229(e229Req, cbsAppGroup, header);

    }

    private E229Resp retrieverE229(E229Req e229Req, String cbsAppGroup, RequestHeader header) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {

        E229Resp e229Resp = null;
        CBSAppGrp cbsAppGrp = new CBSAppGrp();
        cbsAppGrp.setCBSApplicationGroupNumber(cbsAppGroup);
        try {
            LOGGER.info("Calling API E229 proposeAccount");
            ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
            ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
            SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
            e229Resp = e229Client(headerRetriever.getBapiInformationHeader(header.getLloydsHeaders()).getBAPIHeader().getChanid()).proposeAccount(e229Req, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp);
        } catch (WebServiceException e) {
            LOGGER.error("Exception occurred while calling E229. Returning ResourceNotAvailable Error ", e);
            throw exceptionUtilityActivate.resourceNotAvailableError(header, e.getMessage());

        }
        if (e229Resp != null && e229Resp.getE229Result() != null && e229Resp.getE229Result().getResultCondition() != null && iSSystemAvailable(e229Resp)) {
            LOGGER.error("External Service Error occurred while calling E229");
            throw exceptionUtilityActivate.externalServiceError(header, e229Resp.getE229Result().getResultCondition().getReasonText(), e229Resp.getE229Result().getResultCondition().getReasonCode().toString());
        }

        return e229Resp;
    }

    private boolean iSSystemAvailable(final E229Resp e229Resp) {
        return e229Resp.getE229Result().getResultCondition().getReasonCode() != null && e229Resp.getE229Result().getResultCondition().getReasonCode() != 0 && !e229Resp.getE229Result().getResultCondition().getReasonCode().equals(STEM_NOT_AVAILABLE);
    }

    private E229Req createE229Request(String sortCode) {
        E229Req e229Request = new E229Req();
        e229Request.setMaxRepeatGroupQy(MAX_REPEAT_GROUP_QY);
        CBSRequestGp2 cbsRequestGp2 = new CBSRequestGp2();
        cbsRequestGp2.setInputOfficerFlagStatusCd(INPUT_OFFICER_FLAG_STATUS_CD);
        cbsRequestGp2.setInputOfficerStatusCd(INPUT_OFFICER_STATUS_CD);
        cbsRequestGp2.setOverrideDetailsCd(OVERRIDE_DETAILS_CD);
        cbsRequestGp2.setOverridingOfficerStaffNo(OVERRIDING_OFFICER_STAFF_NO);
        e229Request.setCBSRequestGp2(cbsRequestGp2);
        e229Request.setSortCd(sortCode);
        return e229Request;

    }


    private E229Client e229Client(String channel) {
        String brand = Channel.getBrandForChannel(Channel.fromString(channel)).asString();
        return cbsE229ClientMap.get(brand);
    }

    public Map<String, E229Client> getCbsE229ClientMap() {
        return cbsE229ClientMap;
    }

    public void setCbsE229ClientMap(Map<String, E229Client> cbsE229ClientMap) {
        this.cbsE229ClientMap = cbsE229ClientMap;
    }
}

