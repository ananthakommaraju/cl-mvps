package com.lloydsbanking.salsa.activate.downstream;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.LookUpData;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import lib_sim_bo.businessobjects.AssessmentEvidence;
import lib_sim_bo.businessobjects.Condition;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CustomerSegmentCheckService {
    private static final Logger LOGGER = Logger.getLogger(CustomerSegmentCheckService.class);
    @Autowired
    LookUpValueRetriever lookUpValueRetriever;
    @Autowired
    CreateInvolvedParty createInvolvedParty;
    @Autowired
    UpdateDepositArrangementConditionAndApplicationStatusHelper updateAppDetails;

    public void updateCustomerAndGuardianId(String sourceId, ProductArrangement productArrangement, RequestHeader requestHeader, ApplicationDetails applicationDetails) {

        if (isRecordCustomerUpdateRequired(productArrangement) && isOldCustomer(sourceId, productArrangement)) {
            LookUpData lookUpData = retrieveLookUpValues(requestHeader, applicationDetails, productArrangement.getRetryCount());
            AssessmentEvidence assessmentEvidence = getAssessmentEvidence(productArrangement.getPrimaryInvolvedParty(), lookUpData, sourceId);
            String custId = createInvolvedParty.create(productArrangement.getPrimaryInvolvedParty(), productArrangement.getArrangementType(),
                    assessmentEvidence, requestHeader, applicationDetails, productArrangement.getRetryCount());
            if (!applicationDetails.isApiFailureFlag()) {
                productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(custId);
            }

            if (productArrangement.getGuardianDetails() != null && !StringUtils.isEmpty(productArrangement.getGuardianDetails().getCustomerIdentifier())) {
                if (productArrangement.getGuardianDetails().isNewCustomerIndicator() == null || (productArrangement.getGuardianDetails().isNewCustomerIndicator() != null && !productArrangement.getGuardianDetails().isNewCustomerIndicator())) {
                    assessmentEvidence = getAssessmentEvidence(productArrangement.getGuardianDetails(), lookUpData, sourceId);
                    String guardianId = createInvolvedParty.create(productArrangement.getGuardianDetails(), productArrangement.getArrangementType(),
                            assessmentEvidence, requestHeader, applicationDetails, productArrangement.getRetryCount());
                    if (!applicationDetails.isApiFailureFlag()) {
                        productArrangement.getGuardianDetails().setCustomerIdentifier(guardianId);
                    }
                }
            }
            productArrangement.setApplicationSubStatus(applicationDetails.getApplicationSubStatus());
        }
    }

    private AssessmentEvidence getAssessmentEvidence(Customer involvedParty, LookUpData lookUpData, String sourceId) {
        AssessmentEvidence assessmentEvidence = new AssessmentEvidence();
        if (isLookUpDataPresent(involvedParty, lookUpData)) {
            String addressStrength = lookUpData.getAddressEvidenceTypeCode() + ":" + lookUpData.getAddressEvidencePurposeCode();
            String identifyStrength = lookUpData.getPartyEvidenceTypeCode() + ":" + lookUpData.getPartyEvidencePurposeCode();
            if (!ActivateCommonConstant.SourceSystemIdentifier.GALAXY_OAP_SOURCE_SYSTEM_IDENTIFIER.equals(sourceId)) {
                assessmentEvidence.setAddressStrength(addressStrength);
                assessmentEvidence.setIdentityStrength(identifyStrength);
            } else {
                assessmentEvidence = involvedParty.getCustomerScore().get(0).getAssessmentEvidence().get(0);
            }
        }
        return assessmentEvidence;
    }

    private boolean isOldCustomer(String sourceSystemIdentifier, ProductArrangement productArrangement) {
        boolean flag = true;
        if (!ActivateCommonConstant.SourceSystemIdentifier.GALAXY_OAP_SOURCE_SYSTEM_IDENTIFIER.equals(sourceSystemIdentifier)) {
            if (productArrangement.getPrimaryInvolvedParty().isNewCustomerIndicator() != null && productArrangement.getPrimaryInvolvedParty().isNewCustomerIndicator()) {
                if (!(ActivateCommonConstant.AppSubStatus.UPDATE_CUSTOMER_RECORD_FAILURE).equalsIgnoreCase(productArrangement.getApplicationSubStatus())) {
                    flag = false;
                }
            }
        }
        return flag;
    }

    private LookUpData retrieveLookUpValues(RequestHeader header, ApplicationDetails applicationDetails, Integer retryCount) {
        LookUpData lookUpData = new LookUpData();
        List<String> groupCodes = Arrays.asList(ActivateCommonConstant.PamGroupCodes.PTY_EVIDENCE_GROUP_CODE, ActivateCommonConstant.PamGroupCodes.ADD_EVIDENCE_GROUP_CODE, ActivateCommonConstant.PamGroupCodes.ADD_PURPOSE_GROUP_CODE, ActivateCommonConstant.PamGroupCodes.PTY_PURPOSE_GROUP_CODE);
        List<ReferenceDataLookUp> lookUpValues = new ArrayList<>();
        try {
            lookUpValues = lookUpValueRetriever.retrieveLookUpValues(header, groupCodes);
        } catch (ActivateProductArrangementDataNotAvailableErrorMsg | ActivateProductArrangementResourceNotAvailableErrorMsg errorMsg) {
            LOGGER.info("Exception while fetching look up values " + errorMsg);
            updateAppDetails.setApplicationDetails(retryCount, null, null, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.UPDATE_CUSTOMER_RECORD_FAILURE, applicationDetails);
        }
        for (ReferenceDataLookUp lookUp : lookUpValues) {
            if (lookUp.getLookupValueDesc() != null) {
                switch (lookUp.getGroupCode()) {
                    case ActivateCommonConstant.PamGroupCodes.PTY_EVIDENCE_GROUP_CODE:
                        lookUpData.setPartyEvidenceTypeCode(lookUp.getLookupValueDesc());
                        break;
                    case ActivateCommonConstant.PamGroupCodes.ADD_EVIDENCE_GROUP_CODE:
                        lookUpData.setAddressEvidenceTypeCode(lookUp.getLookupValueDesc());
                        break;
                    case ActivateCommonConstant.PamGroupCodes.PTY_PURPOSE_GROUP_CODE:
                        lookUpData.setPartyEvidencePurposeCode(lookUp.getLookupValueDesc());
                        break;
                    case ActivateCommonConstant.PamGroupCodes.ADD_PURPOSE_GROUP_CODE:
                        lookUpData.setAddressEvidencePurposeCode(lookUp.getLookupValueDesc());
                        break;
                    default:
                        break;
                }
            }
        }
        return lookUpData;
    }

    private boolean isLookUpDataPresent(Customer involvedParty, LookUpData lookUpData) {
        if (involvedParty.getCustomerScore() != null && involvedParty.getCustomerScore().get(0).getAssessmentEvidence() != null) {
            if (isEvidencePresent(lookUpData.getAddressEvidencePurposeCode(), lookUpData.getAddressEvidenceTypeCode())
                    && isEvidencePresent(lookUpData.getPartyEvidencePurposeCode(), lookUpData.getPartyEvidenceTypeCode())) {
                return true;
            }
        }
        return false;
    }

    private boolean isEvidencePresent(String purposeCode, String typeCode) {
        return (!StringUtils.isEmpty(purposeCode) && !StringUtils.isEmpty(typeCode));
    }

    private boolean isRecordCustomerUpdateRequired(ProductArrangement productArrangement) {
        return (productArrangement.getPrimaryInvolvedParty().getCustomerSegment() != null && StringUtils.isEmpty(productArrangement.getApplicationSubStatus())) ||
                (ActivateCommonConstant.AppSubStatus.UPDATE_CUSTOMER_RECORD_FAILURE).equalsIgnoreCase(productArrangement.getApplicationSubStatus());

    }

    public Condition getCondition() {
        Condition condition = new Condition();
        condition.setReasonCode("009");
        condition.setReasonText("Failed to update customer record on OCIS");
        return condition;
    }

}
