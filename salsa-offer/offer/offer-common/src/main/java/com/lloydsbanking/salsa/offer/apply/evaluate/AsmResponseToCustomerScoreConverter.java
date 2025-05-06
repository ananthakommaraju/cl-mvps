package com.lloydsbanking.salsa.offer.apply.evaluate;

import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.AsmDecision;
import com.lloydsbanking.salsa.soap.asm.f204.objects.DecisionDetails;
import com.lloydsbanking.salsa.soap.asm.f204.objects.F204Resp;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Resp;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Resp;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.ReferralCode;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class AsmResponseToCustomerScoreConverter {
    private static final Logger LOGGER = Logger.getLogger(AsmResponseToCustomerScoreConverter.class);
    private static final String GROUP_CODE_FOR_FRAUD = "ASM_DECLINE_CODE";
    private static final String ASSESSMENT_TYPE_ASM = "ASM";
    private static final Integer ERR_EDS_NO_DECISION = 150900;

    @Autowired
    HighestPriorityReferralCodeEvaluator highestPriorityReferralCodeEvaluator;
    @Autowired
    HeaderRetriever headerRetriever;

    public void fraudResponseToCustomerScoreConverter(F204Resp f204Resp, CustomerScore customerScore, RequestHeader requestHeader) throws DataNotAvailableErrorMsg {
        if (f204Resp != null) {
            if (isValidResultConditionAndCreditScoreResult(f204Resp) && f204Resp.getF204Result().getResultCondition().getSeverityCode() == 1) {
                customerScore.setScoreResult(AsmDecision.REFERRED.getValue());
            } else if (!AsmDecision.APPROVED.getValue().equals(f204Resp.getASMCreditScoreResultCd()) && !AsmDecision.REFERRED.getValue().equals(f204Resp.getASMCreditScoreResultCd())) {
                customerScore.setScoreResult(AsmDecision.DECLINED.getValue());
            } else {
                customerScore.setScoreResult(f204Resp.getASMCreditScoreResultCd());
            }
            customerScore.setScoreIdentifier(String.valueOf(f204Resp.getCreditScoreId()));
            customerScore.setAssessmentType(ASSESSMENT_TYPE_ASM);
            for (DecisionDetails decisionDetails : f204Resp.getDecisionDetails()) {
                ReferralCode referralCode = new ReferralCode();
                referralCode.setCode(decisionDetails.getCSDecisionReasonTypeCd());
                referralCode.setDescription(decisionDetails.getCSDecisionReasonTypeNr());
                customerScore.getReferralCode().add(referralCode);
            }
            if (AsmDecision.DECLINED.getValue().equals(f204Resp.getASMCreditScoreResultCd())) {
                highestPriorityReferralCodeEvaluator.findHighestPriorityCode(requestHeader.getChannelId(), GROUP_CODE_FOR_FRAUD, customerScore.getReferralCode());
            }
        }
    }

    public void creditScoreResponseToCustomerScoreConverter(F205Resp f205Resp, CustomerScore customerScoreApply, String previousApplicationStatus, RequestHeader requestHeader) throws DataNotAvailableErrorMsg {

        if (f205Resp != null) {
            if (isValidResultConditionAndCreditScoreResult(f205Resp) && f205Resp.getF205Result().getResultCondition().getSeverityCode() == 1) {
                customerScoreApply.setScoreResult(AsmDecision.REFERRED.getValue());
            } else {
                customerScoreApply.setScoreResult(f205Resp.getASMCreditScoreResultCd());
            }
            customerScoreApply.setScoreIdentifier(String.valueOf(f205Resp.getCreditScoreId()));
            List<ReferralCode> referralCodeList = new ArrayList<>();
            for (com.lloydsbanking.salsa.soap.asm.f205.objects.DecisionDetails decisionDetails : f205Resp.getDecisionDetails()) {
                ReferralCode referralCode = new ReferralCode();
                referralCode.setCode(decisionDetails.getCSDecisionReasonTypeCd());
                referralCode.setDescription(decisionDetails.getCSDecisionReasonTypeNr());
                referralCodeList.add(referralCode);
            }
            handleCreditScore(customerScoreApply, previousApplicationStatus, referralCodeList, requestHeader);
        }
    }

    private void handleCreditScore(CustomerScore customerScoreApply, String previousApplicationStatus, List<ReferralCode> referralCodeList, RequestHeader requestHeader) throws DataNotAvailableErrorMsg {
        if (null != customerScoreApply.getScoreResult()) {
            if (AsmDecision.APPROVED.getValue().equals(customerScoreApply.getScoreResult())) {
                setCustomerScoreForAccept(previousApplicationStatus, customerScoreApply, referralCodeList);
            } else if (AsmDecision.REFERRED.getValue().equals(customerScoreApply.getScoreResult())) {
                setCustomerScoreForRefer(previousApplicationStatus, customerScoreApply, referralCodeList);

            } else if (AsmDecision.DECLINED.getValue().equals(customerScoreApply.getScoreResult())) {
                highestPriorityReferralCodeEvaluator.findHighestPriorityCode(requestHeader.getChannelId(), GROUP_CODE_FOR_FRAUD, referralCodeList);
                customerScoreApply.getReferralCode().clear();
                customerScoreApply.getReferralCode().addAll(referralCodeList);
            } else {
                customerScoreApply.setScoreResult(AsmDecision.DECLINED.getValue());
                setReferralCodeInCustomerScore(customerScoreApply, referralCodeList);
            }
        }
    }

    private void setCustomerScoreForAccept(String previousApplicationStatus, CustomerScore customerScoreApply, List<ReferralCode> referralCodeList) {
        if (ApplicationStatus.REFERRED.getValue().equals(previousApplicationStatus)) {
            customerScoreApply.setScoreResult(AsmDecision.REFERRED.getValue());
        } else {
            customerScoreApply.setScoreResult(AsmDecision.APPROVED.getValue());
            setReferralCodeInCustomerScore(customerScoreApply, referralCodeList);
        }
    }

    private void setCustomerScoreForRefer(String previousApplicationStatus, CustomerScore customerScoreApply, List<ReferralCode> referralCodeList) {
        if (ApplicationStatus.APPROVED.getValue().equals(previousApplicationStatus)) {
            if (!referralCodeList.isEmpty()) {
                customerScoreApply.getReferralCode().clear();
                customerScoreApply.getReferralCode().addAll(referralCodeList);
            }
        } else if (ApplicationStatus.REFERRED.getValue().equals(previousApplicationStatus)) {
            setReferralCodeInCustomerScore(customerScoreApply, referralCodeList);
        }
    }

    private void setReferralCodeInCustomerScore(CustomerScore customerScore, List<ReferralCode> referralCodeList) {
        if (!referralCodeList.isEmpty()) {
            customerScore.getReferralCode().addAll(referralCodeList);
        }
    }

    private Boolean isValidResultConditionAndCreditScoreResult(F204Resp f204Resp) {
        Boolean isValidResultCondition = f204Resp.getF204Result() != null && f204Resp.getF204Result().getResultCondition() != null;
        Boolean isValidASMCreditScoreResultCd = StringUtils.isEmpty(f204Resp.getASMCreditScoreResultCd()) || f204Resp.getASMCreditScoreResultCd().trim().length() == 0;
        return isValidResultCondition && isValidASMCreditScoreResultCd;
    }

    private Boolean isValidResultConditionAndCreditScoreResult(F205Resp f205Resp) {
        Boolean isValidResultCondition = f205Resp.getF205Result() != null && f205Resp.getF205Result().getResultCondition() != null;
        Boolean isValidASMCreditScoreResultCd = StringUtils.isEmpty(f205Resp.getASMCreditScoreResultCd()) || f205Resp.getASMCreditScoreResultCd().trim().length() == 0;
        return isValidResultCondition && isValidASMCreditScoreResultCd;
    }

    public void creditDecisionResponseToCustomerScoreConverterForCC(F424Resp f424Resp, CustomerScore customerScore, RequestHeader requestHeader) throws DataNotAvailableErrorMsg {
        if (f424Resp != null) {
            LOGGER.info("Checking ASM Credit Decision");
            if (isValidResultConditionAndCreditScoreResult(f424Resp) && f424Resp.getF424Result().getResultCondition().getSeverityCode() == 1 && ERR_EDS_NO_DECISION.equals(f424Resp.getF424Result().getResultCondition().getReasonCode())) {
                LOGGER.info("ASM Credit Decision is REFERRED");
                customerScore.setScoreResult(AsmDecision.REFERRED.getValue());
            } else if (!AsmDecision.APPROVED.getValue().equals(f424Resp.getASMCreditScoreResultCd()) && !AsmDecision.REFERRED.getValue().equals(f424Resp.getASMCreditScoreResultCd())) {
                LOGGER.info("ASM Credit Decision is DECLINED");
                customerScore.setScoreResult(AsmDecision.DECLINED.getValue());
            } else {
                customerScore.setScoreResult(f424Resp.getASMCreditScoreResultCd());
            }

            customerScore.setScoreIdentifier(String.valueOf(f424Resp.getCreditScoreId()));
            customerScore.setAssessmentType(ASSESSMENT_TYPE_ASM);
            if (customerScore.getReferralCode() != null) {
                for (com.lloydsbanking.salsa.soap.asm.f424.objects.DecisionDetails decisionDetails : f424Resp.getDecisionDetails()) {
                    ReferralCode referralCode = new ReferralCode();
                    referralCode.setCode(decisionDetails.getCSDecisionReasonTypeCd());
                    referralCode.setDescription(decisionDetails.getCSDecisionReasonTypeNr());
                    customerScore.getReferralCode().add(referralCode);
                }
            }
            if (AsmDecision.DECLINED.getValue().equals(f424Resp.getASMCreditScoreResultCd())) {
                highestPriorityReferralCodeEvaluator.findHighestPriorityCode(requestHeader.getChannelId(), GROUP_CODE_FOR_FRAUD, customerScore.getReferralCode());
            }
        }
    }

    private Boolean isValidResultConditionAndCreditScoreResult(F424Resp f424Resp) {
        Boolean isValidResultCondition = f424Resp.getF424Result() != null && f424Resp.getF424Result().getResultCondition() != null;
        Boolean isValidASMCreditScoreResultCd = StringUtils.isEmpty(f424Resp.getASMCreditScoreResultCd()) || f424Resp.getASMCreditScoreResultCd().trim().length() == 0;
        return isValidResultCondition && isValidASMCreditScoreResultCd;
    }

}
