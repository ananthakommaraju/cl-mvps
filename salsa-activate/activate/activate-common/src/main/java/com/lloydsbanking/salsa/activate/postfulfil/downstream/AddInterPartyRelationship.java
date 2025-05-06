package com.lloydsbanking.salsa.activate.postfulfil.downstream;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.ocis.client.c241.C241Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.ocis.c241.objects.C241Req;
import com.lloydsbanking.salsa.soap.ocis.c241.objects.C241Resp;
import com.lloydsbanking.salsa.soap.ocis.c241.objects.C241Result;
import com.lloydsbanking.salsa.soap.ocis.c241.objects.StPartyRelData;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.WebServiceException;

@Repository
public class AddInterPartyRelationship {
    public static final int MAX_REPEAT_GROUP_QY = 1;
    public static final short EXT_SYS_SALSA = 19;
    private static final Logger LOGGER = Logger.getLogger(AddInterPartyRelationship.class);
    private static final String SERVICE_NAME = "http://www.lloydstsb.com/Schema/Personal/RetailParty/OCIS";
    private static final String SERVICE_ACTION = "C241_AddPartyRelat";

    @Autowired
    C241Client c241Client;

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    UpdateDepositArrangementConditionAndApplicationStatusHelper updateAppDetails;

    public void invokeAddInterPartyRelationship(ProductArrangement productArrangement, String extPartyIdTx, String partyId, String stPartyId, String relTypeCd, RequestHeader requestHeader, ApplicationDetails applicationDetails) {
        LOGGER.info("Entering AddInterPartyRelationship (OCIS C241)");
        C241Req c241Req = createC241Request(extPartyIdTx, partyId, stPartyId, relTypeCd);
        try {
            C241Resp c241Resp = getC241Resp(c241Req, requestHeader);
            C241Result c241Result = c241Resp.getC241Result();
            boolean isErrorScenario = checkErrorScenarios(c241Result);
            if (isErrorScenario) {
                updateAppDetails.setApplicationDetails(productArrangement.getRetryCount(), null, null, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.PARTY_RELATIONSHIP_UPDATE_FAILURE, applicationDetails);
            }
        } catch (WebServiceException e) {
            LOGGER.info("Exception while calling C241 for AddInterPartyRelationship " + e);
            updateAppDetails.setApplicationDetails(productArrangement.getRetryCount(), null, null, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.PARTY_RELATIONSHIP_UPDATE_FAILURE, applicationDetails);
        }
        productArrangement.setApplicationSubStatus(applicationDetails.getApplicationSubStatus());
    }

    private boolean checkErrorScenarios(C241Result c241Result) {
        boolean isErrorScenario = false;
        if (c241Result != null && c241Result.getResultCondition() != null) {
            if (c241Result.getResultCondition().getSeverityCode() > 0) {
                isErrorScenario = true;
                int reasonCode = c241Result.getResultCondition().getReasonCode();
                String reasonText = c241Result.getResultCondition().getReasonText();
                LOGGER.info("C241 :External Service Error. ErrorCode | ErrorReason: " + reasonCode + " | " + reasonText);
            }
        }
        return isErrorScenario;
    }


    private C241Resp getC241Resp(C241Req c241Req, RequestHeader requestHeader) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(), SERVICE_NAME, SERVICE_ACTION);
        return c241Client.c241(c241Req, contactPoint, serviceRequest, securityHeaderType);
    }

    private C241Req createC241Request(String extPartyIdTx, String partyId, String stPartyId, String relTypeCd) {
        C241Req c241Req = new C241Req();
        c241Req.setMaxRepeatGroupQy(MAX_REPEAT_GROUP_QY);
        c241Req.setExtSysId(EXT_SYS_SALSA);
        c241Req.setExtPartyIdTx(extPartyIdTx);
        if (!StringUtils.isEmpty(partyId)) {
            c241Req.setPartyId(Long.parseLong(partyId));
        }
        c241Req.setPartyExtSysId(EXT_SYS_SALSA);
        StPartyRelData stPartyRelData = new StPartyRelData();
        if (!StringUtils.isEmpty(stPartyId)) {
            stPartyRelData.setPartyId(Long.parseLong(stPartyId));
        }
        stPartyRelData.setExtSysId(EXT_SYS_SALSA);
        stPartyRelData.setRelTypeCd(relTypeCd);
        c241Req.setStPartyRelData(stPartyRelData);
        return c241Req;
    }
}
