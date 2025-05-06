package com.lloydsbanking.salsa.apaloans.service;

import com.lloydsbanking.salsa.activate.administer.downstream.CreateTask;
import com.lloydsbanking.salsa.activate.administer.downstream.ReferralTeamRetriever;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.apaloans.logging.ApaLoansLogService;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.model.ReferralTeams;
import com.lloydsbanking.salsa.downstream.pam.service.CreatePamService;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApaLoansService implements IAActivateProductArrangement {
    private static final Logger LOGGER = Logger.getLogger(ApaLoansService.class);

    @Autowired
    ApaLoansLogService apaLoansLogService;
    @Autowired
    ReferralTeamRetriever referralTeamRetriever;
    @Autowired
    CreateTask createTask;
    @Autowired
    LookUpValueRetriever lookUpValueRetriever;
    @Autowired
    CreatePamService createPamService;
    @Autowired
    RequestToResponseHeaderConverter requestToResponseHeaderConverter;
    @Autowired
    ProductArrangementTraceLog productArrangementTraceLog;

    private static final String REFERRAL_STATUS_CODE_PEN = "PEN";
    private static final String SUB_CHANNEL_AFFILIATES="004";
    private static final String CHANNEL_TELEPHONE="001";

    @Override
    public ActivateProductArrangementResponse activateProductArrangement(final ActivateProductArrangementRequest upStreamRequest) throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        apaLoansLogService.initialiseContext(upStreamRequest.getHeader());
        LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(upStreamRequest.getProductArrangement(), "Entering ActivateProductArrangement "));
        ActivateProductArrangementResponse response = new ActivateProductArrangementResponse();
        response.setHeader(requestToResponseHeaderConverter.convert(upStreamRequest.getHeader()));

        try {
            lookUpValueRetriever.retrieveChannelId(upStreamRequest.getSourceSystemIdentifier(), upStreamRequest.getHeader(), upStreamRequest.getProductArrangement().getArrangementType());
            retrieveTeamDetailsAndCreateTask(upStreamRequest.getProductArrangement(), upStreamRequest.getHeader());
            response.setProductArrangement(new ProductArrangement());
            response.getProductArrangement().setArrangementType(upStreamRequest.getProductArrangement().getArrangementType());
            response.getProductArrangement().setApplicationStatus(ApplicationStatus.AWAITING_REFERRAL_PROCESSING.getValue());
            upStreamRequest.getProductArrangement().setApplicationType(ActivateCommonConstant.ApplicationType.NEW);
            if (upStreamRequest.getProductArrangement().getInitiatedThrough() == null) {
                upStreamRequest.getProductArrangement().setInitiatedThrough(new Channel());
            }
            upStreamRequest.getProductArrangement().getInitiatedThrough().setChannelCode(CHANNEL_TELEPHONE);
            upStreamRequest.getProductArrangement().getInitiatedThrough().setSubChannelCode(SUB_CHANNEL_AFFILIATES);
            upStreamRequest.getProductArrangement().getPrimaryInvolvedParty().setBrandName(upStreamRequest.getHeader().getChannelId());
            upStreamRequest.getProductArrangement().setApplicationStatus(ApplicationStatus.AWAITING_REFERRAL_PROCESSING.getValue());
            createPamService.createLRAApplication(upStreamRequest.getProductArrangement());

            response.getProductArrangement().setArrangementId(upStreamRequest.getProductArrangement().getArrangementId());

        } catch (ActivateProductArrangementDataNotAvailableErrorMsg errorMsg) {
            LOGGER.info("Exiting activateProductArrangement service with Data Not Available Error Message Fault");
            throw errorMsg;
        } catch (ActivateProductArrangementInternalSystemErrorMsg errorMsg) {
            LOGGER.info("Exiting activateProductArrangement service with Internal Service Error Message Fault");
            throw errorMsg;
        }

        LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(upStreamRequest.getProductArrangement(), "Exiting ActivateProductArrangement "));
        apaLoansLogService.clearContext();
        return response;
    }

    private void retrieveTeamDetailsAndCreateTask(ProductArrangement productArrangement, RequestHeader header) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        List<ReferralTeams> referralTeamsList = referralTeamRetriever.retrieveReferralTeamsForLRA(header);
        if (productArrangement.getFinancialInstitution() != null && !CollectionUtils.isEmpty(productArrangement.getFinancialInstitution().getHasOrganisationUnits())) {
            productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).setOrganisationUnitIdentifer(referralTeamsList.get(0).getOuId());
        } else {
            if (productArrangement.getFinancialInstitution() == null) {
                productArrangement.setFinancialInstitution(new Organisation());
            }
            OrganisationUnit organisationUnit = new OrganisationUnit();
            organisationUnit.setOrganisationUnitIdentifer(referralTeamsList.get(0).getOuId());
            productArrangement.getFinancialInstitution().getHasOrganisationUnits().add(organisationUnit);
        }
        productArrangement.getReferral().add(new Referral());
        productArrangement.getReferral().get(0).setTaskTypeId(Integer.valueOf(referralTeamsList.get(0).getTaskType()));
        productArrangement.getReferral().get(0).setTaskTypeNarrative(referralTeamsList.get(0).getName());

        String taskId = createTask.taskCreation(productArrangement, header, null);
        productArrangement.getReferral().get(0).setTmsTaskIdentifier(taskId);
        productArrangement.getReferral().get(0).setStatusCode(REFERRAL_STATUS_CODE_PEN);
        productArrangement.getReferral().get(0).setReferralTeamIdentifier(String.valueOf(referralTeamsList.get(0).getId()));
    }

}