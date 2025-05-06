package com.lloydsbanking.salsa.apapca.service.propose;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.apapca.service.propose.downstream.AlternateSortCodeRetriever;
import com.lloydsbanking.salsa.apapca.service.propose.downstream.ProposeAccountRetriever;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.cbs.e229.objects.CbsAcTypeChkDgt;
import com.lloydsbanking.salsa.soap.cbs.e229.objects.E229Resp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import lib_sim_bo.businessobjects.ExtraConditions;
import lib_sim_bo.businessobjects.Organisation;
import lib_sim_bo.businessobjects.ResultCondition;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalBusinessErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class ProposedProductArrangementService {
    private static final Logger LOGGER = Logger.getLogger(ProposedProductArrangementService.class);
    private static final int STEM_NOT_AVAILABLE = 1458;
    private static final String CBS_ACCOUNT_TYPE_CD = "6";
    private static final String EXTRA_CONDITION_REASON_CODE = "012";
    private static final String EXTRA_CONDITION_REASON_TEXT = "Failure in Proposing a new current account";
    @Autowired
    AppGroupRetriever appGroupRetriever;
    @Autowired
    ProposeAccountRetriever proposeAccountRetriever;
    @Autowired
    AlternateSortCodeRetriever alternateSortCodeRetriever;
    @Autowired
    ExceptionUtilityActivate exceptionUtilityActivate;
    @Autowired
    UpdateDepositArrangementConditionAndApplicationStatusHelper updateDepositArrangementConditionAndApplicationStatusHelper;
    @Autowired
    HeaderRetriever headerRetriever;

    public boolean callProposeProductArrangementAndSetAccountNumber(ActivateProductArrangementRequest request, ActivateProductArrangementResponse response) {
        if (ApplicationStatus.APPROVED.getValue().equalsIgnoreCase(request.getProductArrangement().getApplicationStatus()) && request.getProductArrangement().getAccountNumber() == null) {
            LOGGER.info("Entering proposeProductArrangement service ");
            ApplicationDetails applicationDetails = new ApplicationDetails();
            request.getProductArrangement().setAccountNumber(proposeProductArrangement(request.getProductArrangement().getFinancialInstitution(), request.getHeader(), applicationDetails, request.getProductArrangement().getRetryCount(), request.getProductArrangement().getApplicationStatus()));
            if (applicationDetails.isApiFailureFlag()) {
                updateRequestOnFailure(request, response, applicationDetails);
                return false;
            }
        }
        return true;
    }

    private String proposeProductArrangement(Organisation financialInstitution, RequestHeader requestHeader, ApplicationDetails details, Integer retryCount, String appStatus) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        if (financialInstitution != null && !financialInstitution.getHasOrganisationUnits().isEmpty()) {
            return proposedProductArrangementResponse(financialInstitution, contactPoint.getContactPointId(), requestHeader, details, retryCount, appStatus);
        }
        return null;
    }

    private String proposedProductArrangementResponse(Organisation financialInstitution, String contactPointID, RequestHeader header, ApplicationDetails details, Integer retryCount, String appStatus) {
        String accountNumber = null;
        E229Resp e229Resp;
        String channel = financialInstitution.getChannel();
        String sortCode = financialInstitution.getHasOrganisationUnits().get(0).getSortCode();
        String cbsAppGroup = getAppGroupFromSortCodeOrChannel(sortCode, channel, header);
        financialInstitution.setChannel(cbsAppGroup);
        boolean isAccountNumberGenerated = false;
        boolean alternateSortCodeCase = false;
        String alternateSortCode = sortCode;
        try {
            while (!isAccountNumberGenerated && !alternateSortCodeCase) {
                if (StringUtils.isEmpty(alternateSortCode)) {
                    alternateSortCodeCase = true;
                    alternateSortCode = alternateSortCodeRetriever.proposeAccount(header, cbsAppGroup, contactPointID);

                }
                e229Resp = proposeAccountRetriever.proposeAccount(header, alternateSortCode, cbsAppGroup);
                if (e229Resp != null) {
                    if (checkResultConditionForSystemError(e229Resp)) {
                        LOGGER.info("ExternalBusinessError Exception occurred while calling E229 " + e229Resp.getE229Result().getResultCondition().getReasonCode().toString());
                        alternateSortCode = null;
                        if (alternateSortCodeCase) {
                            throw exceptionUtilityActivate.externalBusinessError(header, e229Resp.getE229Result().getResultCondition().getReasonText(), e229Resp.getE229Result().getResultCondition().getReasonCode().toString());
                        }
                    } else {
                        accountNumber = cbsAccNo(e229Resp.getCbsAcTypeChkDgt(), e229Resp.getStemId());
                        isAccountNumberGenerated = true;
                        financialInstitution.getHasOrganisationUnits().get(0).setSortCode(alternateSortCode);
                    }
                }
            }
        } catch (ActivateProductArrangementExternalBusinessErrorMsg | ActivateProductArrangementExternalSystemErrorMsg | ActivateProductArrangementResourceNotAvailableErrorMsg error) {
            LOGGER.error("Error occured in proposeAccount service " + error);
            updateDepositArrangementConditionAndApplicationStatusHelper.setApplicationDetails(retryCount, EXTRA_CONDITION_REASON_CODE, EXTRA_CONDITION_REASON_TEXT, appStatus, ActivateCommonConstant.AppSubStatus.STEM_NOT_AVAILABLE, details);
        }
        return accountNumber;
    }

    private boolean checkResultConditionForSystemError(E229Resp e229Resp) {
        return e229Resp.getE229Result() != null && e229Resp.getE229Result().getResultCondition() != null && isSystemNotAvailable(e229Resp);
    }

    private String getAppGroupFromSortCodeOrChannel(String sortCode, String channel, RequestHeader header) {
        return (!StringUtils.isEmpty(sortCode)) ? appGroupRetriever.callRetrieveCBSAppGroup(header, sortCode) : channel;
    }

    private boolean isSystemNotAvailable(E229Resp e229Resp) {
        return e229Resp.getE229Result().getResultCondition().getReasonCode() != null && e229Resp.getE229Result().getResultCondition().getReasonCode().equals(STEM_NOT_AVAILABLE);
    }

    private String cbsAccNo(List<CbsAcTypeChkDgt> accTypeObj, String stemCd) {
        String acc = null;
        if (!StringUtils.isEmpty(stemCd)) {
            for (CbsAcTypeChkDgt accType : accTypeObj) {
                if (CBS_ACCOUNT_TYPE_CD.equalsIgnoreCase(accType.getCBSAccountTypeCd())) {
                    acc = (stemCd + CBS_ACCOUNT_TYPE_CD + accType.getCheckDigitId());
                }
            }
        }
        return acc;
    }

    private void updateRequestOnFailure(ActivateProductArrangementRequest request, ActivateProductArrangementResponse response, ApplicationDetails applicationDetails) {
        if (response.getResultCondition() == null) {
            response.setResultCondition(new ResultCondition());
            response.getResultCondition().setExtraConditions(new ExtraConditions());
        }
        response.getResultCondition().getExtraConditions().getConditions().addAll(applicationDetails.getConditionList());
        request.getProductArrangement().setApplicationStatus(applicationDetails.getApplicationStatus());
        request.getProductArrangement().setApplicationSubStatus(applicationDetails.getApplicationSubStatus());
        request.getProductArrangement().setRetryCount(applicationDetails.getRetryCount());
    }

}
