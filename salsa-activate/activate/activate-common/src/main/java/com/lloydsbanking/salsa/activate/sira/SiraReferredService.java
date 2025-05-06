package com.lloydsbanking.salsa.activate.sira;

import com.lloydsbanking.salsa.activate.administer.downstream.CreateTask;
import com.lloydsbanking.salsa.activate.communication.convert.NotificationEmailTemplates;
import com.lloydsbanking.salsa.activate.communication.downstream.CommunicationManager;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.brand.Brand;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ReferralTeamsDao;
import com.lloydsbanking.salsa.downstream.pam.model.ReferralTeams;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SiraReferredService {

    private static final Logger LOGGER = Logger.getLogger(SiraReferredService.class);
    @Autowired
    CreateTask createTask;
    @Autowired
    CommunicationManager communicationManager;
    @Autowired
    NotificationEmailTemplates notificationEmailTemplates;

    @Autowired
    ReferralTeamsDao referralTeamsDao;

    @Autowired
    ExceptionUtilityActivate exceptionUtilityActivate;

    private static final String REFERRAL_STATUS_CODE = "PEN";
    private static final String SIRA_TASK_NAME_PREFIX = "RFU";
    private static final String BRAND_LTSB = "LTSB";
    private static final String BRAND_HAL = "HAL";


    public ProductArrangement siraReferredArrangement(ProductArrangement productArrangement, String sourceSystemId, ExtraConditions extraConditions, RequestHeader header) throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        LOGGER.info("Entering SiraReferred Service");
        ProductArrangement response = initializeResponse();
        if (ActivateCommonConstant.SourceSystemIdentifier.GALAXY_OFFLINE_SOURCE_SYSTEM_IDENTIFIER.equalsIgnoreCase(sourceSystemId)) {
            String notificationEmail = notificationEmailTemplates.getNotificationEmailForReferred(productArrangement.getArrangementType());
            communicationManager.callSendCommunicationService(productArrangement, notificationEmail, header, null, ActivateCommonConstant.CommunicationTypes.EMAIL);
        }
        List<ReferralTeams> referralTeamsList = retrieveReferralTeamsForSIRA(header);
        setOrganisationUnitIdAndReferralDetailsInRequest(productArrangement, referralTeamsList);
        ApplicationDetails applicationDetails = new ApplicationDetails();
        String taskId = createTask.taskCreation(productArrangement, header, applicationDetails);
        if (taskId != null && !referralTeamsList.isEmpty()) {
            String referralTeamId = String.valueOf(referralTeamsList.get(0).getId());
            setReferralDetailsInResponse(taskId, referralTeamId, response);
        } else {
            setAppStatusAndConditionInResponse(applicationDetails, response, extraConditions);
        }

        return response;
    }

    private List<ReferralTeams> retrieveReferralTeamsForSIRA(RequestHeader header) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        List<ReferralTeams> referralTeamsList;
        String brand = header.getChannelId();
        if (Brand.LLOYDS.asString().equalsIgnoreCase(brand)) {
            brand = BRAND_LTSB;
        } else if (Brand.HALIFAX.asString().equalsIgnoreCase(brand)) {
            brand = BRAND_HAL;
        }
        try {
            referralTeamsList = referralTeamsDao.findByNameIgnoreCaseOrderByPriorityAsc(SIRA_TASK_NAME_PREFIX + "(" + brand + ")");
        } catch (DataAccessException e) {
            LOGGER.error("Error while retrieving referral Team Details. Database Error:  " + e);
            throw exceptionUtilityActivate.resourceNotAvailableError(header, e.getMessage());
        }
        if (referralTeamsList.isEmpty()) {
            LOGGER.error("Error while retrieving referral Team Details. No Records found.");
            throw exceptionUtilityActivate.dataNotAvailableError(null, null, "No referral team record found", header);
        }
        return referralTeamsList;
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


    private void setAppStatusAndConditionInResponse(ApplicationDetails applicationDetails, ProductArrangement response, ExtraConditions extraConditions) {
        response.setApplicationStatus(applicationDetails.getApplicationStatus());
        response.setApplicationSubStatus(applicationDetails.getApplicationSubStatus());
        response.setRetryCount(applicationDetails.getRetryCount());
        extraConditions.getConditions().addAll(applicationDetails.getConditionList());
    }


    private void setReferralDetailsInResponse(String taskId, String referralTeamId, ProductArrangement response) {
        Referral referral = new Referral();
        referral.setStatusCode(REFERRAL_STATUS_CODE);
        referral.setTmsTaskIdentifier(taskId);
        referral.setReferralTeamIdentifier(referralTeamId);
        response.getReferral().add(referral);
        response.setRetryCount(0);
        response.setApplicationSubStatus(null);
        response.setApplicationStatus(ApplicationStatus.AWAITING_REFERRAL_PROCESSING.getValue());
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
}
