package com.lloydsbanking.salsa.offer.identify.downstream;

import com.lloydsbanking.salsa.downstream.ocis.client.f061.F061Client;
import com.lloydsbanking.salsa.downstream.ocis.client.f061.F061RequestBuilder;

import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.F061Req;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.F061Resp;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.F061Result;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_gmo.exception.ExternalBusinessErrorMsg;
import lib_sim_gmo.exception.ExternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;

import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.WebServiceException;
import java.util.Arrays;
import java.util.List;

public class InvolvedPartyRetriever {
    public static final Short DEFAULT_PARTY_EXT_SYS_ID = 2;

    public static final Short PARTY_EXT_SYS_ID = 1;

    private static final Logger LOGGER = Logger.getLogger(InvolvedPartyRetriever.class);

    @Autowired(required = false)
    F061Client f061Client;

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    ExceptionUtility exceptionUtility;

    private static final String EXTERNAL_SERVICE_ERROR_CODE = "823005";

    private static final String EXTERNAL_BUSINESS_ERROR_CODE = "813003";

    private static final String CURRENT_ADDRESS_NOT_PRESENT_CODE = "001";

    private static final String EXTERNAL_SYSTEM_HOST = "L";

    private static final Short DEFAULT_EXTERNAL_SYSTEM_ID = 19;

    private static final List<Integer> EXTERNAL_SERVICE_ERROR_CODES = Arrays.asList(160999, 161031, 163000, 163006, 163001, 163002, 163003, 163008);

    private static final List<Integer> EXTERNAL_BUSINESS_ERROR_CODES = Arrays.asList(163004, 163007, 163009);


    public F061Resp retrieveInvolvedPartyDetails(RequestHeader header, String customerIdentifier) throws ExternalServiceErrorMsg, ExternalBusinessErrorMsg, ResourceNotAvailableErrorMsg {

        F061Req f061Request = createRetrieveInvolvedPartyDetailsRequest(header, customerIdentifier);
        F061Resp response = retrieveF061Response(f061Request, header);
        F061Result f061Result = response.getF061Result();
        if (null != f061Result && null != f061Result.getResultCondition()) {
            if (null != f061Result.getResultCondition().getReasonCode() && 0 != f061Result.getResultCondition().getReasonCode()) {
                throwErrorInResponse(f061Result.getResultCondition().getReasonCode(), f061Result.getResultCondition().getReasonText());
            }
        }
        if (null == response.getPartyEnqData() || null == response.getPartyEnqData().getAddressData() || !CURRENT_ADDRESS_NOT_PRESENT_CODE.equals(response.getPartyEnqData().getAddressData().getAddressStatusCd()) || !CURRENT_ADDRESS_NOT_PRESENT_CODE.equals(response.getPartyEnqData().getAddressData().getAddressTypeCd())) {
            LOGGER.error("Returning ExternalBusinessError after calling OCIS F061. ErrorNo | ErrorMsg ; " + f061Result
                    .getResultCondition()
                    .getReasonCode() + " | " + f061Result.getResultCondition().getReasonText());
            throw exceptionUtility.externalBusinessError(EXTERNAL_BUSINESS_ERROR_CODE, f061Result
                    .getResultCondition()
                    .getReasonCode() + ":" + f061Result.getResultCondition().getReasonText());
        }
        return response;
    }

    private void throwErrorInResponse(Integer reasonCode, String reasonText) throws ExternalServiceErrorMsg, ExternalBusinessErrorMsg {
        if (EXTERNAL_SERVICE_ERROR_CODES.contains(reasonCode)) {
            LOGGER.error("Returning ExternalServiceError after calling OCIS F061. ErrorNo | ErrorMsg ; " + reasonCode + " | " + reasonText);
            throw exceptionUtility.externalServiceError(EXTERNAL_SERVICE_ERROR_CODE, reasonCode + ":" + reasonText);
        }
        if (EXTERNAL_BUSINESS_ERROR_CODES.contains(reasonCode)) {
            LOGGER.error("Returning ExternalBusinessError after calling OCIS F061. ErrorNo | ErrorMsg ; " + reasonCode + " | " + reasonText);
            throw exceptionUtility.externalBusinessError(EXTERNAL_BUSINESS_ERROR_CODE, reasonCode + ":" + reasonText);
        }
    }

    private F061Resp retrieveF061Response(F061Req getStaffIndicatorRequest, RequestHeader header) throws ResourceNotAvailableErrorMsg {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header);
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header);
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header);
        F061Resp response;
        try {
            LOGGER.info("Calling OCIS F061 GetStaffIndicator");
            response = f061Client.f061(getStaffIndicatorRequest, contactPoint, serviceRequest, securityHeaderType);
        } catch (WebServiceException | IllegalStateException e) {
            LOGGER.error("Exception occurred while calling OCIS F061. Returning ResourceNotAvailableError ;", e);
            throw exceptionUtility.resourceNotAvailableError(e.getMessage());
        }
        return response;
    }

    private F061Req createRetrieveInvolvedPartyDetailsRequest(RequestHeader header, String customerIdentifier) {
        BapiInformation bapiInformation = headerRetriever.getBapiInformationHeader(header);
        String extPartyId = "";
        String host = "";
        Short partyExtSysId = DEFAULT_PARTY_EXT_SYS_ID;
        if (null != bapiInformation) {
            extPartyId = bapiInformation.getBAPIHeader().getStpartyObo().getPartyid();
            host = bapiInformation.getBAPIHeader().getStpartyObo().getHost();
            if (EXTERNAL_SYSTEM_HOST.equals(host)) {
                partyExtSysId = PARTY_EXT_SYS_ID;
            }
        }
        F061RequestBuilder requestBuilder = new F061RequestBuilder();
        F061Req request = requestBuilder.extPartyIdTx(extPartyId).partyExtSysId(partyExtSysId).build();
        request.setExtSysId(DEFAULT_EXTERNAL_SYSTEM_ID);
        request.setPartyId(Long.valueOf(customerIdentifier));
        return request;
    }
}
