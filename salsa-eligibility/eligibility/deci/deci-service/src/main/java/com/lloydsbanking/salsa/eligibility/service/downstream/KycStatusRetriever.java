package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.downstream.ocis.client.f075.F075Client;
import com.lloydsbanking.salsa.eligibility.service.utility.ExceptionUtility;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.EligibilityErrorCodes;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.EligibilityServiceConstants;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.Description;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.ReasonText;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalBusinessException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.header.HeaderRetriever;
import com.lloydsbanking.salsa.logging.application.TraceLogUtility;
import com.lloydsbanking.salsa.soap.ocis.f075.objects.AddrEvidence;
import com.lloydsbanking.salsa.soap.ocis.f075.objects.F075Req;
import com.lloydsbanking.salsa.soap.ocis.f075.objects.F075Resp;
import com.lloydsbanking.salsa.soap.ocis.f075.objects.PartyEvidence;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lb_gbo_sales.messages.RequestHeader;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

public class KycStatusRetriever {
    private static final Logger LOGGER = Logger.getLogger(KycStatusRetriever.class);

    @Autowired
    F075Client f075Client;

    @Autowired
    ExceptionUtility exceptionUtility;

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    TraceLogUtility traceLogUtility;

    public static final int MAX_REPEAT_GROUP_QTY = 15;

    public static final short EXTERNAL_SYS_ID = 19;

    public static final List<Integer> EXT_BUSINESS_ERR_CODES = Arrays.asList(EligibilityErrorCodes.OCIS_ERR_EXTSYSID_NOT_ON_OCIS, EligibilityErrorCodes.OCIS_ERR_PARTY_NOT_MASTER, EligibilityErrorCodes.OCIS_ERR_UNKNOWN_EXT_PARTY);

    public static final List<Integer> EXT_BUSINESS_ERR_CODES_FOR_WZ = Arrays.asList(EligibilityErrorCodes.OCIS_ERR_PARTY_NOT_MASTER, EligibilityErrorCodes.OCIS_ERR_PARTY_NOT_FOUND, EligibilityErrorCodes.OCIS_ERR_UNKNOWN_EXT_PARTY);

    public static final List<Integer> EXT_SERVICE_ERR_CODES = Arrays.asList(EligibilityErrorCodes.OCIS_ERR_SEVERE_ERROR, EligibilityErrorCodes.OCIS_ERR_USERID_NOT_SUPPLIED, EligibilityErrorCodes.OCIS_ERR_INVALID_USERID_TYPE_CODE, EligibilityErrorCodes.OCIS_ERR_INVALID_CHANOUTID, EligibilityErrorCodes.OCIS_ERR_INVALID_CHANOUT_TYPECODE, EligibilityErrorCodes.OCIS_ERR_EXTSYSID_NOT_ON_OCIS, EligibilityErrorCodes.OCIS_ERR_PARTY_ID_NOT_SUPPLIED);

    public String getKycStatus(RequestHeader header, String customerIdentifier, List<String> lookUpValues, boolean isCheckForPartyIdEvidenceStatus) throws SalsaExternalBusinessException, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException {
        LOGGER.info(traceLogUtility.getMiscTraceEventMessage("partyId: ", customerIdentifier, "Entering KycStatusRetriever (F075)"));
        F075Req request = createF075Request(header, customerIdentifier);
        F075Resp response = getF075Resp(header, request, isCheckForPartyIdEvidenceStatus);
        if (isCheckForPartyIdEvidenceStatus) {
            return getPartyIdEvidenceStatus(response, lookUpValues);
        }
        else {
            LOGGER.info(traceLogUtility.getMiscTraceEventMessage("KYCStatusCode: ", (null != response.getKYCControlData() ? response.getKYCControlData().getDataCollectedStatusCd() : "null"), "Exiting KycStatusRetriever (F075)"));
            return response.getKYCControlData().getDataCollectedStatusCd();
        }
    }

    private F075Resp getF075Resp(RequestHeader header, F075Req request, boolean isCheckForPartyIdEvidenceStatus) throws SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException, SalsaInternalServiceException {
        F075Resp response = retrieveF075Response(request, header);
        if (null != response.getF075Result() && null != response.getF075Result().getResultCondition()) {
            if (null != response.getF075Result().getResultCondition().getReasonCode() && response.getF075Result().getResultCondition().getReasonCode() != 0) {
                throwErrorInResponse(response.getF075Result().getResultCondition().getReasonCode(), response.getF075Result().getResultCondition().getReasonText(), isCheckForPartyIdEvidenceStatus);
            }
        }
        return response;
    }

    private void throwErrorInResponse(Integer reasonCode, String reasonText, boolean isWZService) throws SalsaExternalBusinessException, SalsaInternalServiceException {
        if (EXT_BUSINESS_ERR_CODES.contains(reasonCode) || EXT_BUSINESS_ERR_CODES_FOR_WZ.contains(reasonCode)) {
            String message = "Returning ExternalBusinessError after calling OCIS F075. ErrorNo | ErrorMsg ; " + reasonCode + " | " + reasonText;
            String errorCode = getEligibilityErrorCodeForExternalBusinessException(reasonCode, isWZService);
            throw new SalsaExternalBusinessException(message, errorCode, new Description(reasonText));
        }
        if (EXT_SERVICE_ERR_CODES.contains(reasonCode)) {
            String message = "Returning InternalServiceError after calling OCIS F075. ErrorNo | ErrorMsg ; " + reasonCode + " | " + reasonText;
            LOGGER.error(message);
            throw new SalsaInternalServiceException(message, EligibilityErrorCodes.ERR_EXT_SERVICE_ERROR, new ReasonText(reasonText));
        }
        else {
            String message = "Returning InternalServiceError after calling OCIS F075. ErrorNo | ErrorMsg ; " + reasonCode + " | " + reasonText;
            LOGGER.error(message);
            throw new SalsaInternalServiceException(message, (reasonCode == EligibilityErrorCodes.DB_TIMEOUT ? EligibilityErrorCodes.ERR_EXTERNAL_SYSTEM_ERROR : EligibilityErrorCodes.ERR_INVALID_REQUEST), new ReasonText(reasonText));
        }

    }

    private String getEligibilityErrorCodeForExternalBusinessException(Integer reasonCode, boolean isCheckForPartyIdEvidenceStatus) {
        if (EXT_BUSINESS_ERR_CODES_FOR_WZ.contains(reasonCode) && isCheckForPartyIdEvidenceStatus) {
            return EligibilityErrorCodes.F075_BUSINESS_ERROR;
        }
        else {
            return EligibilityErrorCodes.ERR_ACCOUNT_LOOKUP_ERROR;
        }
    }

    private F075Resp retrieveF075Response(F075Req request, RequestHeader header) throws SalsaInternalResourceNotAvailableException {
        F075Resp response;
        try {
            LOGGER.info("Calling OCIS F075 retrieveF075Response ");
            ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
            ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
            SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
            response = f075Client.knowYourCustomerStatus(request, contactPoint, serviceRequest, securityHeaderType);
        }
        catch (Exception e) {
            LOGGER.error("Exception occurred while calling OCIS F075. Returning ResourceNotAvailableError ;", e);
            throw new SalsaInternalResourceNotAvailableException(e.getMessage());

        }
        return response;
    }

    private F075Req createF075Request(RequestHeader header, String customerIdentifier) throws SalsaInternalServiceException {
        try {
            BapiInformation bapiInformation = headerRetriever.getBapiInformationHeader(header.getLloydsHeaders());
            String host = bapiInformation.getBAPIHeader().getStpartyObo().getHost();
            F075Req request = new F075Req();
            request.setMaxRepeatGroupQy(MAX_REPEAT_GROUP_QTY);
            request.setExtSysId(EXTERNAL_SYS_ID);
            if (StringUtils.isEmpty(customerIdentifier)) {
                request.setExtPartyIdTx(bapiInformation.getBAPIHeader().getStpartyObo().getPartyid());
                request.setPartyExtSysId(host.endsWith("L") ? (short) 1 : (short) 2);
            }
            else {
                request.setPartyId(Long.parseLong(customerIdentifier));
                request.setPartyExtSysId("L".equals(host) ? (short) 1 : (short) 2);
            }
            return request;
        }
        catch (Exception e) {
            String message = "Exception occurred while creating request for F075. Returning InternalServiceError ;";
            LOGGER.error(message, e);
            throw new SalsaInternalServiceException(e.getMessage());
        }
    }

    private String getPartyIdEvidenceStatus(F075Resp retrievedKYCDetails, List<String> lookUpValues) {
        String partyIdEvidenceStatus = EligibilityServiceConstants.EVIDENCE_STATUS_NOT_AVAILABLE;
        String partyEvidenceTypeCd = null;
        String partyEvidenceRefTx = null;
        String addrEvidenceTypeCd = null;
        String addrEvidenceRefTx = null;
        if (null != retrievedKYCDetails && null != retrievedKYCDetails.getEvidenceData()) {
            if (!CollectionUtils.isEmpty(retrievedKYCDetails.getEvidenceData().getPartyEvidence())) {
                List<PartyEvidence> partyEvidenceList = retrievedKYCDetails.getEvidenceData().getPartyEvidence();
                partyEvidenceTypeCd = partyEvidenceList.get(0).getPartyEvidenceTypeCd();
                partyEvidenceRefTx = partyEvidenceList.get(0).getPartyEvidenceRefTx();
            }
            if (!CollectionUtils.isEmpty(retrievedKYCDetails.getEvidenceData().getAddrEvidence())) {
                List<AddrEvidence> addrEvidenceList = retrievedKYCDetails.getEvidenceData().getAddrEvidence();
                addrEvidenceTypeCd = addrEvidenceList.get(0).getAddrEvidenceTypeCd();
                addrEvidenceRefTx = addrEvidenceList.get(0).getAddrEvidenceRefTx();
            }
        }
        if (!StringUtils.isEmpty(partyEvidenceRefTx) && !StringUtils.isEmpty(partyEvidenceTypeCd) && !StringUtils.isEmpty(addrEvidenceRefTx) && !StringUtils.isEmpty(addrEvidenceTypeCd)) {
            partyIdEvidenceStatus = EligibilityServiceConstants.EVIDENCE_STATUS_FOUND;
        }
        partyIdEvidenceStatus = getPartyIdEvidenceStatusBasedOnLookUpValues(lookUpValues, partyIdEvidenceStatus, partyEvidenceTypeCd, partyEvidenceRefTx, addrEvidenceTypeCd, addrEvidenceRefTx);
        LOGGER.info(traceLogUtility.getMiscTraceEventMessage("KYCStatusCode: ", partyIdEvidenceStatus, "Exiting KycStatusRetriever (F075)"));
        return partyIdEvidenceStatus;
    }

    private String getPartyIdEvidenceStatusBasedOnLookUpValues(final List<String> lookUpValues, String partyIdEvidenceStatus, final String partyEvidenceTypeCd, final String partyEvidenceRefTx, final String addrEvidenceTypeCd, final String addrEvidenceRefTx) {
        boolean partyEvidenceTypeCdFlag = false;
        boolean addrEvidenceTypeCdFlag = false;
        if (!StringUtils.isEmpty(partyEvidenceTypeCd) && (StringUtils.isEmpty(partyEvidenceRefTx))) {
            if (!StringUtils.isEmpty(addrEvidenceTypeCd) && (StringUtils.isEmpty(addrEvidenceRefTx))) {
                partyEvidenceTypeCdFlag = compareEvidenceTypeCdAndLookUpValue(lookUpValues, partyEvidenceTypeCd);
                addrEvidenceTypeCdFlag = compareEvidenceTypeCdAndLookUpValue(lookUpValues, addrEvidenceTypeCd);
            }
        }
        if (partyEvidenceTypeCdFlag && addrEvidenceTypeCdFlag) {
            return EligibilityServiceConstants.EVIDENCE_STATUS_FOUND;
        }
        return partyIdEvidenceStatus;
    }

    private boolean compareEvidenceTypeCdAndLookUpValue(List<String> lookUpValues, String evidenceTypeCd) {
        if (!CollectionUtils.isEmpty(lookUpValues)) {
            for (String lookUpValue : lookUpValues) {
                if (null != lookUpValue && lookUpValue.equalsIgnoreCase(evidenceTypeCd)) {
                    return true;
                }
            }
        }
        return false;
    }
}