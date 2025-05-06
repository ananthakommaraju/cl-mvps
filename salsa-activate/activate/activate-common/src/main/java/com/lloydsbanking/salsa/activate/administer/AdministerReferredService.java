package com.lloydsbanking.salsa.activate.administer;

import com.lloydsbanking.salsa.activate.administer.downstream.CreateTask;
import com.lloydsbanking.salsa.activate.administer.downstream.ReferralTeamRetriever;
import com.lloydsbanking.salsa.activate.administer.downstream.UpdatedCreditDecisionRetriever;
import com.lloydsbanking.salsa.activate.communication.convert.NotificationEmailTemplates;
import com.lloydsbanking.salsa.activate.communication.downstream.CommunicationManager;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.downstream.pam.model.ReferralTeams;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdministerReferredService {
    private static final String CREDIT_SCORE_SOURCE_SYSTEM_F204_SA_CA = "025";
    private static final String CREDIT_SCORE_SOURCE_SYSTEM_F205_CA = "012";
    private static final String ASSESSMENT_TYPE_ASM = "ASM";
    private static final String REFERRAL_STATUS_CODE = "PEN";

    @Autowired
    CreateTask createTask;
    @Autowired
    UpdatedCreditDecisionRetriever updatedCreditDecisionRetriever;
    @Autowired
    AdministerReferredLookUpData administerReferredLookUpData;
    @Autowired
    ReferralTeamRetriever referralTeamRetriever;
    @Autowired
    CommunicationManager communicationManager;
    @Autowired
    NotificationEmailTemplates notificationEmailTemplates;

    public ProductArrangement administerReferredArrangement(ProductArrangement productArrangement, String sourceSystemId, ExtraConditions extraConditions, RequestHeader header) throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        ProductArrangement response = initializeResponse();

        ApplicationDetails applicationDetailsForF204 = updatedCreditDecisionRetriever.retrieveUpdatedCreditDecision(productArrangement, header, CREDIT_SCORE_SOURCE_SYSTEM_F204_SA_CA);
        setResponseFromApplicationDetails(productArrangement, extraConditions, response, applicationDetailsForF204);
        List<ProductFamily> productFamilyListFromCreditDecision = applicationDetailsForF204.getProductFamilies();

        if (ArrangementType.CURRENT_ACCOUNT.getValue().equals(productArrangement.getArrangementType())) {
            if (!ActivateCommonConstant.AsmDecision.DECLINED.equalsIgnoreCase(applicationDetailsForF204.getApplicationStatus())) {
                productFamilyListFromCreditDecision = updateCreditDecisionAndGetFamilyList(productArrangement, extraConditions, header, response);
            }
            if (ActivateCommonConstant.AsmDecision.ACCEPT.equalsIgnoreCase(response.getApplicationStatus())) {
                checkFraudCaseAndUpdateAsmDecision(productArrangement, response, productFamilyListFromCreditDecision);
            }
        }

        if (ActivateCommonConstant.AsmDecision.REFERRED.equalsIgnoreCase(response.getApplicationStatus())) {
            createTaskAndSetResponse(productArrangement, sourceSystemId, extraConditions, header, response);
        } else if (ActivateCommonConstant.AsmDecision.DECLINED.equalsIgnoreCase(response.getApplicationStatus()) || ActivateCommonConstant.AsmDecision.UNSCORED.equalsIgnoreCase(response.getApplicationStatus())) {

            if (!ActivateCommonConstant.SourceSystemIdentifier.GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER.equalsIgnoreCase(sourceSystemId)) {
                String declineSource = administerReferredLookUpData.getDeclineSource(productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().get(0).getCode(), header.getChannelId());
                String notificationEmail = notificationEmailTemplates.getNotificationEmailForDeclined(productArrangement.getArrangementType(), declineSource);
                communicationManager.callSendCommunicationService(productArrangement, notificationEmail, header, null, ActivateCommonConstant.CommunicationTypes.EMAIL);
            }
            response.setApplicationStatus(ApplicationStatus.DECLINED.getValue());

        } else if (ActivateCommonConstant.AsmDecision.ACCEPT.equalsIgnoreCase(response.getApplicationStatus())) {
            setCreditLimitAndAppStatusInResponse(productArrangement.getArrangementType(), applicationDetailsForF204.getCreditLimit(), response);
        }

        return response;
    }

    private List<ProductFamily> updateCreditDecisionAndGetFamilyList(ProductArrangement productArrangement, ExtraConditions extraConditions, RequestHeader header, ProductArrangement response) {
        ApplicationDetails applicationDetailsForF205 = updatedCreditDecisionRetriever.retrieveUpdatedCreditDecision(productArrangement, header, CREDIT_SCORE_SOURCE_SYSTEM_F205_CA);
        List<ProductFamily> productFamilyListFromCreditDecision = new ArrayList<>();
        if (!(ActivateCommonConstant.AsmDecision.DECLINED.equalsIgnoreCase(applicationDetailsForF205.getScoreResult()) || ActivateCommonConstant.AsmDecision.UNSCORED.equalsIgnoreCase(applicationDetailsForF205.getScoreResult()))) {

            if (ActivateCommonConstant.AsmDecision.REFERRED.equalsIgnoreCase(applicationDetailsForF205.getScoreResult()) && ActivateCommonConstant.AsmDecision.REFERRED.equalsIgnoreCase(response.getApplicationStatus())) {
                productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().addAll(applicationDetailsForF205.getReferralCodes());
                applicationDetailsForF205.setReferralCodes(productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode());
            } else if (ActivateCommonConstant.AsmDecision.REFERRED.equalsIgnoreCase(response.getApplicationStatus())) {
                applicationDetailsForF205.setApplicationStatus(response.getApplicationStatus());
                applicationDetailsForF205.getReferralCodes().clear();
                applicationDetailsForF205.getReferralCodes().addAll(productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode());
            }

            productFamilyListFromCreditDecision = applicationDetailsForF205.getProductFamilies();
            setResponseFromApplicationDetails(productArrangement, extraConditions, response, applicationDetailsForF205);
            response.getOfferedProducts().get(0).getProductoptions().addAll(applicationDetailsForF205.getProductOptions());
        }
        return productFamilyListFromCreditDecision;
    }

    private void checkFraudCaseAndUpdateAsmDecision(ProductArrangement productArrangement, ProductArrangement response, List<ProductFamily> productFamilyListFromCreditDecision) {
        if (!administerReferredLookUpData.checkIfFamilyIDSameAsCreditDecision(productArrangement.getAssociatedProduct(), productFamilyListFromCreditDecision)) {
            response.setApplicationStatus(ActivateCommonConstant.AsmDecision.DECLINED);
            response.getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult(ActivateCommonConstant.AsmDecision.DECLINED);
            response.getPrimaryInvolvedParty().getCustomerScore().get(0).setAssessmentType(ASSESSMENT_TYPE_ASM);
        }
    }

    private void createTaskAndSetResponse(ProductArrangement productArrangement, String sourceSystemId, ExtraConditions extraConditions, RequestHeader header, ProductArrangement response) throws ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {

        if (ActivateCommonConstant.SourceSystemIdentifier.GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER.equalsIgnoreCase(sourceSystemId)) {
            response.setApplicationStatus(productArrangement.getApplicationStatus());
        } else {
            if (ActivateCommonConstant.SourceSystemIdentifier.GALAXY_OFFLINE_SOURCE_SYSTEM_IDENTIFIER.equalsIgnoreCase(sourceSystemId)) {
                String notificationEmail = notificationEmailTemplates.getNotificationEmailForReferred(productArrangement.getArrangementType());
                communicationManager.callSendCommunicationService(productArrangement, notificationEmail, header, null, ActivateCommonConstant.CommunicationTypes.EMAIL);
            }
            List<ReferenceDataLookUp> refLookUpList = administerReferredLookUpData.retrieveLookUpValuesByGroupCodeAndLookUpText(productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().get(0).getCode(), header.getChannelId());
            List<ReferralTeams> referralTeamsList = referralTeamRetriever.retrieveReferralTeams(refLookUpList, header);
            setOrganisationUnitIdAndReferralDetailsInRequest(productArrangement, referralTeamsList);
            ApplicationDetails applicationDetails = new ApplicationDetails();
            String taskId = createTask.taskCreation(productArrangement, header, applicationDetails);
            if (taskId != null && !referralTeamsList.isEmpty()) {
                String taskType = referralTeamsList.get(0).getTaskType();
                String referralTeamId = String.valueOf(referralTeamsList.get(0).getId());
                setReferralDetailsInResponse(refLookUpList, taskType, taskId, referralTeamId, response);
            } else {
                setAppStatusAndConditionInResponse(applicationDetails, response, extraConditions);
            }
        }

    }

    private void setOrganisationUnitIdAndReferralDetailsInRequest(ProductArrangement productArrangement, List<ReferralTeams> referralTeamsList) {
        if (!referralTeamsList.isEmpty()) {
            if (productArrangement.getFinancialInstitution() == null) {
                productArrangement.setFinancialInstitution(new Organisation());
            }
            if (productArrangement.getFinancialInstitution().getHasOrganisationUnits().isEmpty()) {
                productArrangement.getFinancialInstitution().getHasOrganisationUnits().add(new OrganisationUnit());
            }
            productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).setOrganisationUnitIdentifer(referralTeamsList.get(0).getOuId());
            if (productArrangement.getReferral().isEmpty()) {
                productArrangement.getReferral().add(new Referral());
            }
            productArrangement.getReferral().get(0).setTaskTypeId(Integer.valueOf(referralTeamsList.get(0).getTaskType()));
            productArrangement.getReferral().get(0).setTaskTypeNarrative(referralTeamsList.get(0).getName());
        }
    }

    private ProductArrangement initializeResponse() {
        ProductArrangement response = new ProductArrangement();
        Customer customer = new Customer();
        CustomerScore customerScore = new CustomerScore();
        customer.getCustomerScore().add(customerScore);
        response.setPrimaryInvolvedParty(customer);
        Product product = new Product();
        product.getProductoffer().add(new ProductOffer());
        response.getOfferedProducts().add(product);
        return response;
    }

    private void setReferralDetailsInResponse(List<ReferenceDataLookUp> refLookUpList, String taskType, String taskId, String referralTeamId, ProductArrangement response) {
        String referralCode = administerReferredLookUpData.getReferralCode(refLookUpList, taskType);
        Referral referral = new Referral();
        referral.setStatusCode(REFERRAL_STATUS_CODE);
        referral.setTmsTaskIdentifier(taskId);
        referral.setReferralTeamIdentifier(referralTeamId);
        referral.setReferralCode(referralCode);
        response.getReferral().add(referral);
        response.setRetryCount(0);
        response.setApplicationSubStatus(null);
        response.setApplicationStatus(ApplicationStatus.AWAITING_REFERRAL_PROCESSING.getValue());
    }

    private void setCreditLimitAndAppStatusInResponse(String arrangementType, CurrencyAmount creditLimit, ProductArrangement response) {
        response.setApplicationStatus(ApplicationStatus.APPROVED.getValue());
        if (ArrangementType.CREDITCARD.getValue().equalsIgnoreCase(arrangementType)) {
            response.getOfferedProducts().get(0).getProductoffer().get(0).setOfferAmount(creditLimit);
        }
    }

    private void setAppStatusAndConditionInResponse(ApplicationDetails applicationDetails, ProductArrangement response, ExtraConditions extraConditions) {
        response.setApplicationStatus(applicationDetails.getApplicationStatus());
        response.setApplicationSubStatus(applicationDetails.getApplicationSubStatus());
        response.setRetryCount(applicationDetails.getRetryCount());
        extraConditions.getConditions().addAll(applicationDetails.getConditionList());
    }

    private void setResponseFromApplicationDetails(ProductArrangement productArrangement, ExtraConditions extraConditions, ProductArrangement response, ApplicationDetails applicationDetails) {
        response.setApplicationStatus(applicationDetails.getApplicationStatus());
        if (!ApplicationStatus.AWAITING_REFERRAL_PROCESSING.getValue().equalsIgnoreCase(applicationDetails.getApplicationStatus())) {
            response.getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult(applicationDetails.getApplicationStatus());
            productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().clear();
            productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().addAll(applicationDetails.getReferralCodes());
        } else {
            response.setRetryCount(applicationDetails.getRetryCount());
            extraConditions.getConditions().addAll(applicationDetails.getConditionList());
        }
    }
}
