package com.lloydsbanking.salsa.offer.apply.downstream;


import com.lloydsbanking.salsa.downstream.asm.client.f205.F205Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.apply.convert.RetrieveCreditScoreRequestFactory;
import com.lloydsbanking.salsa.offer.apply.errorcode.RetrieveCreditScoreErrorMap;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Req;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Resp;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Result;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.exception.ExternalBusinessErrorMsg;
import lib_sim_gmo.exception.ExternalServiceErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.xml.ws.WebServiceException;
import java.text.ParseException;
import java.util.List;

public class CreditScoreRetriever {
    private static final String INTERNAL_SERVICE_ERROR = "Internal Service Error";
    private static final String EXTERNAL_BUSINESS_ERROR = "External Business Error";
    private static final String EXTERNAL_SERVICE_ERROR = "External Service Error";

    @Autowired
    RetrieveCreditScoreRequestFactory f205RequestFactory;

    @Autowired
    ExceptionUtility exceptionUtility;

    @Autowired(required = false)
    F205Client f205Client;

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    RetrieveCreditScoreErrorMap errorMap;

    private static final Logger LOGGER = Logger.getLogger(CreditScoreRetriever.class);

    public F205Resp retrieveCreditDecision(ProductArrangement productArrangement, RequestHeader header) throws OfferException {
        F205Resp f205Resp = null;
        if (productArrangement.getAssociatedProduct() != null) {
            try {
                f205Resp = getCreditScore(productArrangement, header);
            } catch (InternalServiceErrorMsg | ExternalBusinessErrorMsg | ExternalServiceErrorMsg | ResourceNotAvailableErrorMsg errorMsg) {
                throw new OfferException(errorMsg);
            }
        }
        return f205Resp;
    }

    private F205Resp getCreditScore(ProductArrangement productArrangement, RequestHeader requestHeader) throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalServiceErrorMsg, ExternalBusinessErrorMsg {
        String regionCode = null, areaCode = null;
        if (productArrangement.getFinancialInstitution() != null && !CollectionUtils.isEmpty(productArrangement.getFinancialInstitution().getHasOrganisationUnits())) {
            regionCode = productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getRegionCode();
            areaCode = productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getAreaCode();
        }
        String contactPointId = headerRetriever.getContactPoint(requestHeader).getContactPointId();
        F205Req creditScoreRequest = createCreditScoreRequest(contactPointId, productArrangement.getArrangementId(), productArrangement.getAssociatedProduct()
                .getBrandName(), productArrangement.getAssociatedProduct()
                .getExternalSystemProductIdentifier(), productArrangement.getPrimaryInvolvedParty(), areaCode, regionCode, productArrangement.getAccountPurpose(), productArrangement
                .getExistingProducts(), productArrangement.getConditions());
        F205Resp creditScoreResponse = retrieveF205Resp(creditScoreRequest, requestHeader);
        if (isError(creditScoreResponse)) {
            throwErrorInResponse(creditScoreResponse.getF205Result());
        }
        return creditScoreResponse;
    }

    private void throwErrorInResponse(F205Result f205Result) throws ExternalServiceErrorMsg, ExternalBusinessErrorMsg, InternalServiceErrorMsg {
        int reasonCode = f205Result.getResultCondition().getReasonCode();
        String reasonText = f205Result.getResultCondition().getReasonText();
        if (isErrorType(EXTERNAL_SERVICE_ERROR, reasonCode, reasonText)) {
            throw exceptionUtility.externalServiceError(errorMap.getAsmErrorCode(reasonCode), f205Result.getResultCondition().getReasonCode() + ":" + f205Result.getResultCondition().getReasonText());
        } else if (isErrorType(EXTERNAL_BUSINESS_ERROR, reasonCode, reasonText)) {
            throw exceptionUtility.externalBusinessError(errorMap.getAsmErrorCode(reasonCode), f205Result.getResultCondition().getReasonCode() + ":" + f205Result.getResultCondition().getReasonText());
        } else if (isErrorType(INTERNAL_SERVICE_ERROR, reasonCode, reasonText)) {
            throw exceptionUtility.internalServiceError(errorMap.getAsmErrorCode(reasonCode), f205Result.getResultCondition().getReasonCode() + ":" + f205Result.getResultCondition().getReasonText());
        } else {
            LOGGER.info("Error while retrieving Credit score from ASM F205 . ErrorNo | ErrorMsg ; " + reasonCode + " | " + reasonText);
        }
    }

    private boolean isErrorType(String error, Integer reasonCode, String reasonText) {
        if (error.equals(errorMap.getAsmErrorCode(reasonCode))) {
            LOGGER.error(error + " while retrieving Credit score from ASM F205 . ErrorNo | ErrorMsg ; " + reasonCode + " | " + reasonText);
            return true;
        }
        return false;
    }

    private boolean isError(F205Resp response) {
        if (null != response && null != response.getF205Result() && null != response.getF205Result().getResultCondition()) {
            if (null != response.getF205Result().getResultCondition().getReasonCode() && response.getF205Result().getResultCondition().getReasonCode() != 0) {
                return true;
            }
        }
        return false;
    }

    private F205Resp retrieveF205Resp(F205Req creditScoreRequest, RequestHeader header) throws ResourceNotAvailableErrorMsg {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header);
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header);
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header);
        F205Resp f205Resp;
        try {
            LOGGER.info("Calling ASM F205 RetrieveCreditScore ");
            f205Resp = f205Client.fetchCreditDecisionForCurrentAccount(creditScoreRequest, contactPoint, serviceRequest, securityHeaderType);
        } catch (WebServiceException e) {
            //catching exceptions while calling client to throw ResourceNotAvailableError only in that case
            LOGGER.error("Exception occurred while calling ASM F205. Returning ResourceNotAvailableError ;", e);
            throw exceptionUtility.resourceNotAvailableError(e.getMessage());
        }
        return f205Resp;
    }

    private F205Req createCreditScoreRequest(String contactPointId, String arrangementId, String associatedProductBrandName, List<ExtSysProdIdentifier> externalSystemProductIdentifiers, Customer primaryInvolvedParty, String areaCode, String regionCode, String accountPurpose, List<Product> existingProducts, List<RuleCondition> ruleConditionList) throws InternalServiceErrorMsg {
        try {
            return f205RequestFactory.create(contactPointId, arrangementId, associatedProductBrandName, externalSystemProductIdentifiers, primaryInvolvedParty, areaCode, regionCode, accountPurpose, existingProducts, ruleConditionList);
        } catch (ParseException e) {
            //catching exceptions while creating requests to throw internalService error only in that case
            LOGGER.error("Exception occurred while creating request for ASM F205. Returning InternalServiceError ;", e);
            throw exceptionUtility.internalServiceError(null, e.getMessage());
        }
    }
}
