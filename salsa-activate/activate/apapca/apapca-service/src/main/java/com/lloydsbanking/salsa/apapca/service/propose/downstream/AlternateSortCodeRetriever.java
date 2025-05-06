package com.lloydsbanking.salsa.apapca.service.propose.downstream;

import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.brand.Channel;
import com.lloydsbanking.salsa.downstream.cbs.client.e469.E469Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.cbs.e469.objects.CBSRequestGp2;
import com.lloydsbanking.salsa.soap.cbs.e469.objects.E469Req;
import com.lloydsbanking.salsa.soap.cbs.e469.objects.E469Resp;
import com.lloydstsb.schema.infrastructure.soap.CBSAppGrp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.WebServiceException;
import java.util.Map;

public class AlternateSortCodeRetriever {
    private static final Logger LOGGER = Logger.getLogger(ProposeAccountRetriever.class);

    public static final int MAX_REPEAT_GROUP_QY = 0;

    public static final int INPUT_OFFICER_FLAG_STATUS_CD = 0;

    public static final int OVERRIDE_DETAILS_CD = 0;

    public static final int SORT_CODE_START_INDEX = 4;

    public static final int SORT_CODE_END_INDEX = 10;

    Map<String, E469Client> cbsE469ClientMap;

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    ExceptionUtilityActivate exceptionUtilityActivate;

    public String proposeAccount(RequestHeader header, String cbsAppGroup, String contactPointID) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        E469Req e469Req = createE469Request(contactPointID);
        E469Resp e469Resp = alternateSortCodeRes(e469Req, header, cbsAppGroup);
        return e469Resp.getAlternateSortCodeBranchId();

    }

    private E469Resp alternateSortCodeRes(E469Req request, RequestHeader header, String cbsAppGroup) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        E469Resp e469Resp = null;
        CBSAppGrp cbsAppGrp = new CBSAppGrp();
        cbsAppGrp.setCBSApplicationGroupNumber(cbsAppGroup);

        try {
            LOGGER.info("Calling API E469 alternateSortCodeRes ");
            ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
            ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders(), "http://xml.lloydsbanking.com/Schema/Enterprise/CoreBanking/CBS/E469_EnqCBSAltSortCd", "E469");
            SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
            e469Resp = e469Client(headerRetriever.getBapiInformationHeader(header.getLloydsHeaders()).getBAPIHeader().getChanid()).retrieveAlternateSortCode(request, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp);
        } catch (WebServiceException e) {
            LOGGER.error("Exception occurred while calling E469. Returning ResourceNotAvailable Error ", e);
            throw exceptionUtilityActivate.resourceNotAvailableError(header, e.getMessage());
        }

        if (e469Resp != null && e469Resp.getE469Result() != null && e469Resp.getE469Result().getResultCondition() != null) {
            checkResponseForError(e469Resp.getE469Result().getResultCondition(), header);
        }

        return e469Resp;
    }

    private void checkResponseForError(ResultCondition resultCondition, RequestHeader header) throws ActivateProductArrangementExternalSystemErrorMsg {
        if (resultCondition.getSeverityCode() != (byte) 0) {
            LOGGER.error("E469 responded with non zero severity code. Returning ExternalServiceError. ReasonCode | ReasonText " + resultCondition.getReasonCode() + " | " + resultCondition.getReasonText());
            throw exceptionUtilityActivate.externalServiceError(header, resultCondition.getReasonText(), null != resultCondition.getReasonCode() ? resultCondition.getReasonCode().toString() : null);

        }
    }

    private E469Req createE469Request(String contactPointID) {
        E469Req e469Request = new E469Req();
        e469Request.setMaxRepeatGroupQy(MAX_REPEAT_GROUP_QY);
        CBSRequestGp2 cbsRequestGp2 = new CBSRequestGp2();
        cbsRequestGp2.setInputOfficerFlagStatusCd(INPUT_OFFICER_FLAG_STATUS_CD);
        cbsRequestGp2.setOverrideDetailsCd(OVERRIDE_DETAILS_CD);
        e469Request.setCBSRequestGp2(cbsRequestGp2);
        if (!StringUtils.isEmpty(contactPointID)) {
            e469Request.setSortCd(contactPointID.substring(SORT_CODE_START_INDEX, SORT_CODE_END_INDEX));
        }
        return e469Request;

    }

    private E469Client e469Client(String channel) {
        String brand = Channel.getBrandForChannel(Channel.fromString(channel)).asString();
        return cbsE469ClientMap.get(brand);
    }

    public Map<String, E469Client> getCbsE469ClientMap() {
        return cbsE469ClientMap;
    }

    public void setCbsE469ClientMap(Map<String, E469Client> cbsE469ClientMap) {
        this.cbsE469ClientMap = cbsE469ClientMap;
    }

}
