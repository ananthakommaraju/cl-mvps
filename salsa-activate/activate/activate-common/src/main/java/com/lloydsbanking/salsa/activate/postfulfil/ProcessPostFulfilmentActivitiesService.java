package com.lloydsbanking.salsa.activate.postfulfil;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.downstream.CustomerSegmentCheckService;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.activate.downstream.UpdateMarketingPreferences;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.postfulfil.downstream.*;
import com.lloydsbanking.salsa.activate.postfulfil.rules.ValidateProcessPostFulfilment;
import com.lloydsbanking.salsa.activate.registration.downstream.ActivateIBApplication;
import com.lloydsbanking.salsa.activate.sira.downstream.SiraRetriever;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamService;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamServiceForActivateDA;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
public class ProcessPostFulfilmentActivitiesService {
    private static final Logger LOGGER = Logger.getLogger(ProcessPostFulfilmentActivitiesService.class);
    private static final String LIFE_STYLE = "LIFE_STYLE_BENEFIT";
    private static final String ISO_COUNTRY_CODE = "ISO_COUNTRY_CODE";
    private static final String SWITCH_FATCA_UPDATE = "SW_FATCAupdate";
    private static final String SWITCH_KYC_DETAILS = "SW_EnDispKYCDtls";
    private static final String SWITCH_SIRA_DETAILS = "SW_EnSIRAFrdChk";
    private static final int CUSTOMER_SCORE_LIST_SIZE = 3;
    @Autowired
    ActivateIBApplication activateIBApplication;
    @Autowired
    UpdateEmailAddress updateEmailAddress;
    @Autowired
    UpdateNationalInsuranceNumber updateNationalInsuranceNumber;
    @Autowired
    UpdateMarketingPreferences updateMarketingPreferences;
    @Autowired
    ValidateProcessPostFulfilment validateProcessPostFulfilment;
    @Autowired
    AddInterPartyRelationship addInterPartyRelationship;
    @Autowired
    CustomerSegmentCheckService customerSegmentCheckService;
    @Autowired
    LookUpValueRetriever lookUpValueRetriever;
    @Autowired
    ActivateBenefitArrangement activateBenefitArrangement;
    @Autowired
    RecordCustomerDetails recordCustomerDetails;
    @Autowired
    CommunicatePostFulfilmentActivities communicatePostFulfilmentActivities;
    @Autowired
    RecordDocumentMetaContent recordDocumentMetaContent;
    @Autowired
    SiraRetriever siraRetriever;
    @Autowired
    UpdatePamServiceForActivateDA updatePamServiceForActivateDA;
    @Autowired
    UpdatePamService updatePamService;

    public void processPostFulfilmentActivitiesResponse(final ActivateProductArrangementRequest request) throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        LOGGER.info("Entering processPostFulfilmentActivities service " + request.getProductArrangement().getArrangementId() + " | " + request.getProductArrangement().getAssociatedProduct().getProductName());
        ProductArrangement productArrangement = request.getProductArrangement();
        if (validateProcessPostFulfilment.checkAppSubStatus(productArrangement.getApplicationSubStatus(), ActivateCommonConstant.AppSubStatus.ACCOUNT_SWITCHING_FAILURE)) {
            if (validateProcessPostFulfilment.isActivateIBApplicationRequired(productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn())) {
                activateIBApplication.retrieveActivateIBApplication(productArrangement, request.getHeader());
            }
            productArrangement.setApplicationSubStatus(null);
        }
        ApplicationDetails applicationDetails = new ApplicationDetails();
        customerSegmentCheckService.updateCustomerAndGuardianId(request.getSourceSystemIdentifier(), productArrangement, request.getHeader(), applicationDetails);
        if (validateProcessPostFulfilment.isUpdateEmailAddressRequired(productArrangement, request.getSourceSystemIdentifier())) {
            updateEmailAddress.updateEmail(productArrangement, request.getHeader(), applicationDetails);
        }
        if (validateProcessPostFulfilment.isUpdateNINumberRequired(productArrangement)) {
            updateNationalInsuranceNumber.updateNationalInsNumber(productArrangement, request.getHeader(), applicationDetails);
        }
        if (validateProcessPostFulfilment.isUpdateMarketingPreferencesRequired(productArrangement)) {
            updateMarketingPreferences.marketingPreferencesUpdate(request.getHeader(), productArrangement, applicationDetails);
        }
        updateBenefitArrangement(productArrangement, request.getHeader());
        callInterPartyRelationship(productArrangement, request.getHeader(), applicationDetails);
        if (validateProcessPostFulfilment.checkAppSubStatus(productArrangement.getApplicationSubStatus(), ActivateCommonConstant.AppSubStatus.CUSTOMER_DETAILS_UPDATE_FAILURE) || ActivateCommonConstant.AppSubStatus.AWAITING_CRS_FULFILLMENT_FAILURE.equalsIgnoreCase(productArrangement.getApplicationSubStatus())) {
            boolean fatcaSwitch = validateProcessPostFulfilment.retrieveSwitchValue(request.getHeader().getChannelId(), SWITCH_FATCA_UPDATE);
            boolean crsSwitch = validateProcessPostFulfilment.retrieveSwitchValueBranded(request.getHeader().getChannelId(), SWITCH_KYC_DETAILS);
            callRecordDocumentMetaContent(crsSwitch, productArrangement, request.getHeader(), applicationDetails);
            Map<String, String> countryCodeMap = retrieveLookUpValues(request.getHeader());
            callRecordCustomerDetails(fatcaSwitch, crsSwitch, productArrangement, request.getHeader(), applicationDetails, countryCodeMap);
            if (productArrangement.getApplicationSubStatus() == null) {
                communicatePostFulfilmentActivities.communicateWelcomeMessageAndFundReminder(productArrangement, request.getHeader(), request.getHeader().getChannelId(), request.getSourceSystemIdentifier());
                request.getProductArrangement().setApplicationStatus(ApplicationStatus.FULFILLED.getValue());
            }
        }
        callSiraService(productArrangement, request.getHeader(), applicationDetails);
        if (null != request.getProductArrangement().getApplicationSubStatus()) {
            request.getProductArrangement().setApplicationStatus(applicationDetails.getApplicationStatus());
            request.getProductArrangement().setRetryCount(applicationDetails.getRetryCount());
        }
        LOGGER.info("Sub Status and retry count is " + request.getProductArrangement().getApplicationSubStatus()+request.getProductArrangement().getRetryCount());
        updatePamService.updateOcisDetailsInPam(productArrangement.getGuardianDetails(), productArrangement.getArrangementId(), productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier(), productArrangement.getArrangementType(), productArrangement.getApplicationType(), productArrangement.getPrimaryInvolvedParty().getCidPersID());
        updatePamServiceForActivateDA.update(productArrangement, request.getSourceSystemIdentifier(), ActivateCommonConstant.Operation.SAVINGS);
        LOGGER.info("Exiting processPostFulfilmentActivities service " + request.getProductArrangement().getArrangementId());
    }
    private void callRecordCustomerDetails(boolean fatcaSwitch, boolean crsSwitch, ProductArrangement productArrangement, RequestHeader requestHeader, ApplicationDetails applicationDetails, Map<String, String> countryCodeMap) {
        if (fatcaSwitch || crsSwitch) {
            recordCustomerDetails.recordCustomerDetails(productArrangement, requestHeader, applicationDetails, countryCodeMap, crsSwitch);
        }
    }
    private void callRecordDocumentMetaContent(boolean crsSwitch, ProductArrangement productArrangement, RequestHeader requestHeader, ApplicationDetails applicationDetails) {
        if (crsSwitch) {
            if (validateProcessPostFulfilment.checkAppSubStatus(productArrangement.getApplicationSubStatus(), ActivateCommonConstant.AppSubStatus.AWAITING_CRS_FULFILLMENT_FAILURE)) {
                recordDocumentMetaContent.recordDocMetaContent(applicationDetails, productArrangement, requestHeader);
            }
        }
    }
    private void updateBenefitArrangement(ProductArrangement productArrangement, RequestHeader requestHeader) {
        if (StringUtils.isEmpty(productArrangement.getApplicationSubStatus()) && productArrangement.getConditions() != null) {
            for (RuleCondition condition : productArrangement.getConditions()) {
                if (condition != null && (LIFE_STYLE + "_CODE").equalsIgnoreCase(condition.getName())) {
                    String benefitLookupDesc = getLifeStyleBenefitLookUpValue(condition.getResult(), requestHeader.getChannelId(), productArrangement);
                    activateBenefitArrangement.activateArrangement(productArrangement, requestHeader, benefitLookupDesc);
                    break;
                }
            }
        }
    }
    private String getLifeStyleBenefitLookUpValue(String lookUpText, String channelId, ProductArrangement productArrangement) {
        List<ReferenceDataLookUp> refLookUpList = new ArrayList<>();
        try {
            refLookUpList = lookUpValueRetriever.retrieveLookUpValues(Arrays.asList(lookUpText), channelId, Arrays.asList(LIFE_STYLE));
        } catch (DataAccessException e) {
            LOGGER.info("Error while retrieving LookupValue " + e);
        }
        for (ReferenceDataLookUp lookUp : refLookUpList) {
            if (LIFE_STYLE.equalsIgnoreCase(lookUp.getGroupCode()) && lookUp.getLookupValueDesc() != null) {
                return lookUp.getLookupValueDesc();
            }
        }
        return null;
    }
    private void callInterPartyRelationship(ProductArrangement productArrangement, RequestHeader requestHeader, ApplicationDetails applicationDetails) {
        if (validateProcessPostFulfilment.checkAppSubStatus(productArrangement.getApplicationSubStatus(), ActivateCommonConstant.AppSubStatus.PARTY_RELATIONSHIP_UPDATE_FAILURE) && productArrangement.getGuardianDetails() != null && productArrangement.getGuardianDetails().getCustomerIdentifier() != null) {
            Customer guardian = productArrangement.getGuardianDetails();
            Customer childDetails = productArrangement.getPrimaryInvolvedParty();
            addInterPartyRelationship.invokeAddInterPartyRelationship(productArrangement, childDetails.getCidPersID(), childDetails.getCustomerIdentifier(), guardian.getCustomerIdentifier(), "008", requestHeader, applicationDetails);
        }
    }
    private Map<String, String> retrieveLookUpValues(RequestHeader header) throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        List<String> groupCodes = new ArrayList<>();
        List<ReferenceDataLookUp> lookUpValues;
        Map<String, String> countryCodeMap = new HashMap<>();
        groupCodes.add(ISO_COUNTRY_CODE);
        lookUpValues = lookUpValueRetriever.retrieveLookUpValues(header, groupCodes);
        for (ReferenceDataLookUp lookUp : lookUpValues) {
            if (lookUp.getGroupCode().equals(ISO_COUNTRY_CODE) && lookUp.getLookupText() != null && lookUp.getLookupValueDesc() != null) {
                countryCodeMap.put(lookUp.getLookupValueDesc(), lookUp.getLookupText());
            }
        }
        return countryCodeMap;
    }
    private void callSiraService(ProductArrangement productArrangement, RequestHeader requestHeader, ApplicationDetails applicationDetails) {
        boolean isSiraEnabled = false;
        if (null != productArrangement.isSIRAEnabledSwitch()) {
            if (productArrangement.isSIRAEnabledSwitch()) {
                isSiraEnabled = true;
            }
        } else {
            isSiraEnabled = validateProcessPostFulfilment.retrieveSwitchValueBranded(requestHeader.getChannelId(), SWITCH_SIRA_DETAILS);
        }
        if (isSiraEnabled && (ActivateCommonConstant.AppSubStatus.SIRA_FAILURE_SUB_STATUS.equalsIgnoreCase(productArrangement.getApplicationSubStatus())|| isSiraCallRequired(productArrangement.getPrimaryInvolvedParty().getCustomerScore()))) {
            boolean isPCAReEngineering = false;
            for (RuleCondition condition : productArrangement.getConditions()) {
                if (("INTEND_TO_SWITCH").equalsIgnoreCase(condition.getName()) && !StringUtils.isEmpty(condition.getResult())) {
                    isPCAReEngineering = true;
                }
            }
            if (isPCAReEngineering) {
                LOGGER.info("SIRA call for retry " + productArrangement.getArrangementId() + " | " + productArrangement.getApplicationStatus()+ " | " +productArrangement.getApplicationSubStatus()+ " | " +productArrangement.getRetryCount());
                siraRetriever.retrieveSiraDecision((DepositArrangement) productArrangement, applicationDetails, requestHeader, true);
            }
        }
    }
    private boolean isSiraCallRequired(List<CustomerScore> customerScoreList) {
        if (!CollectionUtils.isEmpty(customerScoreList) && customerScoreList.size() >= CUSTOMER_SCORE_LIST_SIZE) {
            if (customerScoreList.get(2) != null && customerScoreList.get(2).getCustomerDecision() != null) {
                return "FAILED".equalsIgnoreCase(customerScoreList.get(2).getCustomerDecision().getResultStatus());
            }
        }
        return false;
    }
}