package com.lloydsbanking.salsa.offer.apply.evaluate;

import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.offer.AsmDecision;
import com.lloydsbanking.salsa.soap.asm.f204.objects.F204Resp;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Resp;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Resp;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.ReferralCode;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ApplicationStatusEvaluator {

    @Autowired
    AsmResponseToCustomerScoreConverter asmResponseToCustomerScoreConverter;

    private static final String ASM_DECISION_ACCEPT_01 = "1";
    private static final String ASM_DECISION_REFER_02 = "2";
    private static final String ASM_DECISION_DECLINE_03 = "3";

    private static final String EXPERIAN_AVAILABLE_REFERRAL_CODE = "501";

    public String getApplicationStatusForAsmFraudDecision(CustomerScore customerScore, F204Resp f204Resp, RequestHeader requestHeader) throws DataNotAvailableErrorMsg {
        asmResponseToCustomerScoreConverter.fraudResponseToCustomerScoreConverter(f204Resp, customerScore, requestHeader);
        return getApplicationStatusFromCustomerScore(customerScore);
    }

    private String getApplicationStatusFromCustomerScore(CustomerScore customerScore) {
        String applicationStatus = null;
        if (!StringUtils.isEmpty(customerScore.getScoreResult())) {
            if (isApplicationStatusAccept(customerScore.getScoreResult())) {
                applicationStatus = ApplicationStatus.APPROVED.getValue();
            } else if (isApplicationStatusRefer(customerScore.getScoreResult())) {
                applicationStatus = checkReferralCode(customerScore.getReferralCode());
            } else if (isApplicationStatusDecline(customerScore.getScoreResult())) {
                applicationStatus = ApplicationStatus.DECLINED.getValue();
            }
        } else {
            applicationStatus = ApplicationStatus.DECLINED.getValue();
        }

        return applicationStatus;
    }

    public String getApplicationStatusForCreditScoreDecision(F205Resp f205Resp, String previousApplicationStatus, CustomerScore customerScore, RequestHeader requestHeader) throws DataNotAvailableErrorMsg {
        asmResponseToCustomerScoreConverter.creditScoreResponseToCustomerScoreConverter(f205Resp, customerScore, previousApplicationStatus, requestHeader);
        String applicationStatus = previousApplicationStatus;
        if (customerScore != null && f205Resp.getASMCreditScoreResultCd() != null) {
            if (isApplicationStatusAccept(f205Resp.getASMCreditScoreResultCd())) {
                if (previousApplicationStatus.equals(ApplicationStatus.REFERRED.getValue())) {
                    applicationStatus = ApplicationStatus.REFERRED.getValue();
                } else {
                    applicationStatus = ApplicationStatus.APPROVED.getValue();
                }
            } else if (isApplicationStatusRefer(f205Resp.getASMCreditScoreResultCd())) {
                applicationStatus = ApplicationStatus.REFERRED.getValue();
                if (!customerScore.getReferralCode().isEmpty()) {
                    applicationStatus = checkReferralCode(customerScore.getReferralCode());
                }
            } else {
                applicationStatus = ApplicationStatus.DECLINED.getValue();
            }
        }
        return applicationStatus;

    }

    private String checkReferralCode(List<ReferralCode> referralCodeList) {
        String applicationStatus = ApplicationStatus.REFERRED.getValue();
        if (!referralCodeList.isEmpty()) {
            for (ReferralCode referralCode : referralCodeList) {
                if (EXPERIAN_AVAILABLE_REFERRAL_CODE.equals(referralCode.getCode())) {
                    applicationStatus = ApplicationStatus.UNSCORED.getValue();
                }
            }
        }
        return applicationStatus;
    }

    private boolean isApplicationStatusAccept(String scoreResult) {
        return ASM_DECISION_ACCEPT_01.equalsIgnoreCase(scoreResult);
    }

    private boolean isApplicationStatusRefer(String scoreResult) {
        return ASM_DECISION_REFER_02.equalsIgnoreCase(scoreResult);
    }

    private boolean isApplicationStatusDecline(String scoreResult) {
        return ASM_DECISION_DECLINE_03.equalsIgnoreCase(scoreResult);
    }

    public String getApplicationStatusForAsmCreditDecisionForCC(CustomerScore customerScore, F424Resp f424Resp, RequestHeader requestHeader) throws DataNotAvailableErrorMsg {
        asmResponseToCustomerScoreConverter.creditDecisionResponseToCustomerScoreConverterForCC(f424Resp, customerScore, requestHeader);
        clearDescriptionFromHighestPriorityReferralCode(customerScore.getReferralCode(), f424Resp);
        return getApplicationStatusFromCustomerScore(customerScore);
    }

    private void clearDescriptionFromHighestPriorityReferralCode(List<ReferralCode> referralCode, F424Resp f424Resp) {
        if (null != f424Resp && AsmDecision.DECLINED.getValue().equals(f424Resp.getASMCreditScoreResultCd()) && !CollectionUtils.isEmpty(referralCode)) {
            referralCode.get(0).setDescription(null);
        }
    }
}
