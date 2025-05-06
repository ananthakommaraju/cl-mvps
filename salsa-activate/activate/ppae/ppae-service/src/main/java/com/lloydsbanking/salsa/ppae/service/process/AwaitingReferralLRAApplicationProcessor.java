package com.lloydsbanking.salsa.ppae.service.process;


import com.lloydsbanking.salsa.activate.communication.downstream.CommunicationManager;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.constants.EmailTemplateEnum;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.brand.Brand;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ApplicationsDao;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamService;
import com.lloydsbanking.salsa.ppae.service.downstream.LoanDetailsRetriever;
import com.lloydsbanking.salsa.ppae.service.downstream.PersonalDetailsRetriever;
import com.lloydsbanking.salsa.ppae.service.downstream.PrepareFinanceServiceArrangement;
import com.lloydsbanking.salsa.soap.pad.q028.objects.ApplicationDetails;
import com.lloydsbanking.salsa.soap.pad.q028.objects.DecisionReasons;
import com.lloydsbanking.salsa.soap.pad.q028.objects.Q028Resp;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class AwaitingReferralLRAApplicationProcessor {
    private static final Logger LOGGER = Logger.getLogger(AwaitingFulfilmentApplicationProcessor.class);

    @Autowired
    PersonalDetailsRetriever personalDetailsRetriever;
    @Autowired
    LoanDetailsRetriever loanDetailsRetriever;
    @Autowired
    CommunicationManager communicationManager;
    @Autowired
    ApplicationsDao applicationsDao;
    @Autowired
    LookUpValueRetriever lookUpValueRetriever;
    @Autowired
    PrepareFinanceServiceArrangement prepareFinanceServiceArrangement;
    @Autowired
    UpdatePamService updatePamService;

    private static final String DECLINE_EMAIL_GROUP_CODE = "DECLINE_EMAILS_LRA";
    private static final int PAD_STATUS_REFER = 23;

    public void process(ProductArrangement productArrangement, RequestHeader header) {
        productArrangement.setArrangementType(ArrangementType.LOAN.getValue());
        Q028Resp q028Resp = loanDetailsRetriever.retrieve(productArrangement, header);

        if (isAsmDecisionAcceptOrDecline(q028Resp)) {
            if (PAD_STATUS_REFER == q028Resp.getApplicationDetails().getLoanApplnStatusCd()) {
                prepareFinanceServiceArrangement.process(q028Resp, header);
            }
            personalDetailsRetriever.retrieve(productArrangement.getPrimaryInvolvedParty(), header);
            communicateLRAApplicationStatus(productArrangement, header, q028Resp);
        }

        updatePamService.updateLRAOROPSApplicationInPam(ApplicationStatus.FULFILLED.getValue(), productArrangement.getArrangementId());
    }

    private void communicateLRAApplicationStatus(ProductArrangement productArrangement, RequestHeader header, Q028Resp q028Resp) {
        if (null != q028Resp) {
            String notificationEmail = getNotificationEmailForLRA(q028Resp.getApplicationDetails());
            if (!StringUtils.isEmpty(productArrangement.getPrimaryInvolvedParty().getEmailAddress()) && null != notificationEmail) {
                communicationManager.callSendCommunicationService(productArrangement, notificationEmail, header, null, ActivateCommonConstant.CommunicationTypes.EMAIL);
            }
        }
    }

    private boolean isAsmDecisionAcceptOrDecline(Q028Resp q028Resp) {
        return null != q028Resp && (ActivateCommonConstant.AsmDecision.ACCEPT.equals(q028Resp.getApplicationDetails().getASMCreditScoreResultCd()) || ActivateCommonConstant.AsmDecision.DECLINED.equals(q028Resp.getApplicationDetails().getASMCreditScoreResultCd()));
    }

    private String getNotificationEmailForLRA(ApplicationDetails applicationDetails) {
        String notificationEmail = null;
        if (null != applicationDetails) {
            if (ActivateCommonConstant.AsmDecision.ACCEPT.equals(applicationDetails.getASMCreditScoreResultCd())) {
                notificationEmail = EmailTemplateEnum.LRA_ACCEPT_MSG.getTemplate();
            } else if (ActivateCommonConstant.AsmDecision.DECLINED.equals(applicationDetails.getASMCreditScoreResultCd())) {
                notificationEmail = getDeclineTemplate(applicationDetails.getDecisionReasons());
            }
        }
        return notificationEmail;
    }

    private String getDeclineTemplate(List<DecisionReasons> decisionReasonsList) {
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        try {
            LOGGER.info("Entering RetrieveDeclineEmailLookupvalues with GroupCode " + DECLINE_EMAIL_GROUP_CODE);
            referenceDataLookUpList = lookUpValueRetriever.retrieveLookUpValues(null, Brand.LLOYDS.asString(), Arrays.asList(DECLINE_EMAIL_GROUP_CODE));
        } catch (DataAccessException e) {
            LOGGER.info("Error while retrieving look up date with group code : " + DECLINE_EMAIL_GROUP_CODE + e);
        }
        LOGGER.info("Exiting RetrieveDeclineEmailLookupvalues");

        if (!referenceDataLookUpList.isEmpty() && !decisionReasonsList.isEmpty() && decisionReasonsList.get(0).getCSDecisionReasonTypeCd().equalsIgnoreCase(referenceDataLookUpList.get(0).getLookupValueDesc())) {
            return EmailTemplateEnum.LRA_DECLINE_MSG.getTemplate() + referenceDataLookUpList.get(0).getLookupValueDesc();
        } else {
            return EmailTemplateEnum.LRA_DECLINE_MSG.getTemplate();
        }
    }

}
