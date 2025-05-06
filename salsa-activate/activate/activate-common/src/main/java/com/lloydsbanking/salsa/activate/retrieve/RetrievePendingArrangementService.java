package com.lloydsbanking.salsa.activate.retrieve;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.downstream.pam.service.RetrievePamService;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementInternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class RetrievePendingArrangementService {
    @Autowired
    RetrievePamService retrievePamService;
    @Autowired
    ExceptionUtilityActivate exceptionUtilityActivate;
    @Autowired
    RetrievePendingArrangementServiceHelper serviceHelper;
    @Autowired
    LookUpValueRetriever lookUpValueRetriever;
    private static final Logger LOGGER = Logger.getLogger(RetrievePendingArrangementService.class);
    private static final String OVERDRAFT_VIEWED = "ODPCI_VIEWED";
    private static final String OVERDRAFT_VIEWED_VALUE = "Y";
    private static final String CONTACT_POINT_PORTFOLIO = "Cnt_Pnt_Prtflio";
    private static final String CONDITION_FOR_PCA_RE_ENGINEERING = "INTEND_TO_SWITCH";

    public boolean retrievePendingArrangements(ActivateProductArrangementRequest upStreamRequest) throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        boolean isPCAReEngineering = false;
        String sourceSystemIdentifier = upStreamRequest.getSourceSystemIdentifier();
        ProductArrangement upStreamProductArrangement = upStreamRequest.getProductArrangement();
        if (!ActivateCommonConstant.SourceSystemIdentifier.GALAXY_DB_EVENT_SOURCE_SYSTEM_IDENTIFIER.equals(sourceSystemIdentifier)) {
            ProductArrangement productArrangement = callRetrievePamService(upStreamRequest.getHeader(), upStreamProductArrangement.getArrangementId(), upStreamProductArrangement.getReferral());
            upStreamRequest.getHeader().setChannelId(productArrangement.getAssociatedProduct().getBrandName());
            setContactPointId(upStreamRequest.getHeader(), productArrangement.getAssociatedProduct().getBrandName());
            if (!ActivateCommonConstant.SourceSystemIdentifier.GALAXY_OAP_SOURCE_SYSTEM_IDENTIFIER.equals(sourceSystemIdentifier)) {
                if (productArrangement.getArrangementType().equalsIgnoreCase(ArrangementType.SAVINGS.getValue())) {
                    upStreamProductArrangement.getOfferedProducts().addAll(productArrangement.getOfferedProducts());
                    upStreamProductArrangement.setAssociatedProduct(productArrangement.getAssociatedProduct());
                    upStreamProductArrangement.setGuardianDetails(productArrangement.getGuardianDetails());
                    serviceHelper.setCommonPamDetailsForGalaxy(upStreamProductArrangement, productArrangement);
                    setAccountRemittanceDetails(upStreamProductArrangement, productArrangement);
                } else if (ArrangementType.CURRENT_ACCOUNT.getValue().equalsIgnoreCase(productArrangement.getArrangementType())) {
                    setCustomerLocation(upStreamProductArrangement, productArrangement);
                    serviceHelper.setCommonPamDetailsForGalaxy(upStreamProductArrangement, productArrangement);
                    isPCAReEngineering = setPamDetailsForGalaxyCA(upStreamProductArrangement, productArrangement);
                } else {
                    upStreamProductArrangement.getOfferedProducts().addAll(productArrangement.getOfferedProducts());
                    upStreamProductArrangement.getAssociatedProduct().setBrandName(productArrangement.getAssociatedProduct().getBrandName());
                    upStreamProductArrangement.setCampaignCode(productArrangement.getCampaignCode());
                    serviceHelper.setCommonPamDetailsForGalaxy(upStreamProductArrangement, productArrangement);
                    setMarketingPreferenceIndicator(upStreamProductArrangement, productArrangement);
                    setPcciAndAcDate(upStreamProductArrangement, productArrangement);
                }
            } else {
                setPamDetailsForOAPMode(upStreamRequest, productArrangement);
            }
        }
        return isPCAReEngineering;
    }

    private void setMarketingPreferenceIndicator(ProductArrangement upStreamProductArrangement, ProductArrangement productArrangement) {
        if (upStreamProductArrangement instanceof FinanceServiceArrangement && productArrangement instanceof FinanceServiceArrangement) {
            ((FinanceServiceArrangement) upStreamProductArrangement).setMarketingPrefereceIndicator(((FinanceServiceArrangement) productArrangement).isMarketingPrefereceIndicator());
        }
    }

    private boolean setPamDetailsForGalaxyCA(ProductArrangement upStreamProductArrangement, ProductArrangement productArrangement) {
        boolean isPCAReEngineering = false;
        for (RuleCondition condition : productArrangement.getConditions()) {
            if ((CONDITION_FOR_PCA_RE_ENGINEERING).equalsIgnoreCase(condition.getName()) && !StringUtils.isEmpty(condition.getResult())) {
                isPCAReEngineering = true;
            }
        }
        upStreamProductArrangement.getConditions().addAll(productArrangement.getConditions());
        if (upStreamProductArrangement instanceof DepositArrangement) {
            DepositArrangement depositArrangement = (DepositArrangement) upStreamProductArrangement;
            if (null != depositArrangement.isIsOverdraftRequired() && depositArrangement.isIsOverdraftRequired()) {
                RuleCondition ruleCondition = new RuleCondition();
                ruleCondition.setName(OVERDRAFT_VIEWED);
                ruleCondition.setResult(OVERDRAFT_VIEWED_VALUE);
                upStreamProductArrangement.getConditions().add(ruleCondition);
            }
        }
        return isPCAReEngineering;
    }

    private void setPamDetailsForOAPMode(ActivateProductArrangementRequest upStreamRequest, ProductArrangement productArrangement) {
        List<AssessmentEvidence> primaryAssessmentEvidences = new ArrayList<>();
        List<AssessmentEvidence> guardianAssessmentEvidences = new ArrayList<>();
        if (!CollectionUtils.isEmpty(upStreamRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore())) {
            primaryAssessmentEvidences = upStreamRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentEvidence();
        }
        if (upStreamRequest.getProductArrangement().getGuardianDetails() != null && !CollectionUtils.isEmpty(upStreamRequest.getProductArrangement().getGuardianDetails().getCustomerScore())) {
            guardianAssessmentEvidences = upStreamRequest.getProductArrangement().getGuardianDetails().getCustomerScore().get(0).getAssessmentEvidence();
        }
        String userType = upStreamRequest.getProductArrangement().getPrimaryInvolvedParty().getUserType();
        String internalUserId = upStreamRequest.getProductArrangement().getPrimaryInvolvedParty().getInternalUserIdentifier();
        upStreamRequest.setProductArrangement(productArrangement);
        if (!CollectionUtils.isEmpty(primaryAssessmentEvidences)) {
            upStreamRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentEvidence().clear();
            upStreamRequest.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentEvidence().addAll(primaryAssessmentEvidences);
        }
        if (!StringUtils.isEmpty(internalUserId)) {
            upStreamRequest.getProductArrangement().getPrimaryInvolvedParty().setInternalUserIdentifier(internalUserId);
        }
        if (!StringUtils.isEmpty(userType)) {
            upStreamRequest.getProductArrangement().getPrimaryInvolvedParty().setUserType(userType);
        }
        if (productArrangement.getGuardianDetails() != null && !StringUtils.isEmpty(productArrangement.getGuardianDetails().getCustomerIdentifier()) && !CollectionUtils.isEmpty(guardianAssessmentEvidences)) {
            upStreamRequest.getProductArrangement().getGuardianDetails().getCustomerScore().get(0).getAssessmentEvidence().clear();
            upStreamRequest.getProductArrangement().getGuardianDetails().getCustomerScore().get(0).getAssessmentEvidence().addAll(guardianAssessmentEvidences);
        }
    }
    private void setContactPointId(RequestHeader requestHeader, String brandName) {
        List<ReferenceDataLookUp> refLookUpList = new ArrayList<>();
        try {
            refLookUpList = lookUpValueRetriever.retrieveLookUpValues(new ArrayList<String>(), brandName, Arrays.asList(CONTACT_POINT_PORTFOLIO));
        } catch (DataAccessException e) {
            LOGGER.info("Error while retrieving LookupValue " + e);
        }
        for (ReferenceDataLookUp lookUp : refLookUpList) {
            requestHeader.setContactPointId(lookUp.getLookupValueDesc());
        }
    }
    private void setPcciAndAcDate(ProductArrangement upStreamProductArrangement, ProductArrangement productArrangement) {
        if (productArrangement instanceof FinanceServiceArrangement && upStreamProductArrangement instanceof FinanceServiceArrangement) {
            FinanceServiceArrangement financeServiceArrangementPam = (FinanceServiceArrangement) productArrangement;
            FinanceServiceArrangement financeServiceArrangementActivate = (FinanceServiceArrangement) upStreamProductArrangement;
            if (financeServiceArrangementPam.isMarketingPrefereceIndicator() != null) {
                financeServiceArrangementActivate.setMarketingPrefereceIndicator(financeServiceArrangementPam.isMarketingPrefereceIndicator());
            }
            if (financeServiceArrangementPam.getPcciViewedDate() != null) {
                financeServiceArrangementActivate.setPcciViewedDate(financeServiceArrangementPam.getPcciViewedDate());
            }
            if (financeServiceArrangementPam.getAgreementAcceptedDate() != null) {
                financeServiceArrangementActivate.setAgreementAcceptedDate(financeServiceArrangementPam.getAgreementAcceptedDate());
            }
        }
    }

    private void setAccountRemittanceDetails(ProductArrangement upStreamProductArrangement, ProductArrangement productArrangement) {
        if (productArrangement instanceof DepositArrangement && upStreamProductArrangement instanceof DepositArrangement) {
            DepositArrangement depositArrangementPam = (DepositArrangement) productArrangement;
            DepositArrangement depositArrangementActivate = (DepositArrangement) upStreamProductArrangement;
            if (depositArrangementPam.getInterestRemittanceAccountDetails() != null) {
                depositArrangementActivate.setInterestRemittanceAccountDetails(depositArrangementPam.getInterestRemittanceAccountDetails());
            }
            if (depositArrangementPam.getISABalance() != null) {
                depositArrangementActivate.setISABalance(depositArrangementPam.getISABalance());
            }
            if (depositArrangementPam.getAccountPurpose() != null) {
                depositArrangementActivate.setAccountPurpose(depositArrangementPam.getAccountPurpose());
            }
            if (depositArrangementPam.isIsR85Opt() != null && depositArrangementPam.isIsR85Opt()) {
                depositArrangementActivate.setIsSecondaryAccount(depositArrangementPam.isIsR85Opt());
            }
            if (depositArrangementPam.isIsCashCard() != null && depositArrangementPam.isIsCashCard()) {
                depositArrangementActivate.setIsCashCard(depositArrangementPam.isIsCashCard());
            }
        }
    }

    private void setCustomerLocation(ProductArrangement upStreamProductArrangement, ProductArrangement productArrangement) {
        if (upStreamProductArrangement.getPrimaryInvolvedParty().getIsPlayedBy() != null && upStreamProductArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getCustomerLocation() != null) {
            productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setCustomerLocation(upStreamProductArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getCustomerLocation());
        }
    }

    private ProductArrangement callRetrievePamService(RequestHeader requestHeader, String arrangementId, List<Referral> referralList) throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        try {
            return retrievePamService.retrievePendingArrangement(requestHeader.getChannelId(), arrangementId, referralList);
        } catch (DataNotAvailableErrorMsg errorMsg) {
            LOGGER.info("Exiting activateProductArrangement service with Data Not Available Error Message Fault");
            throw exceptionUtilityActivate.dataNotAvailableError(null, null, "Data Not Available Error:" + errorMsg, requestHeader);
        } catch (ResourceNotAvailableErrorMsg resourceNotAvailableErrorMsg) {
            throw exceptionUtilityActivate.resourceNotAvailableError(requestHeader, "Resource Not Available Error:" + resourceNotAvailableErrorMsg);
        } catch (InternalServiceErrorMsg internalServiceErrorMsg) {
            LOGGER.info("Exiting activateProductArrangement service with Internal Service Error Message Fault" + internalServiceErrorMsg);
            throw exceptionUtilityActivate.internalServiceError(null, internalServiceErrorMsg.getFaultInfo().getReasonText(), requestHeader);
        }
    }
}