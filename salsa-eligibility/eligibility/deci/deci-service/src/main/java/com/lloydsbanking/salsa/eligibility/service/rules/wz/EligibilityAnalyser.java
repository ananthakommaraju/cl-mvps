package com.lloydsbanking.salsa.eligibility.service.rules.wz;

import com.lloydsbanking.salsa.downstream.prd.model.RefInstructionRulesPrdDto;
import com.lloydsbanking.salsa.eligibility.service.rules.common.DeclineReasonAdder;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.ReasonText;
import com.lloydsbanking.salsa.eligibility.service.utility.wz.ExceptionUtility;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsExternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsInternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

public class EligibilityAnalyser {
    private static final Logger LOGGER = Logger.getLogger(EligibilityAnalyser.class);

    @Autowired
    ExceptionUtility exceptionUtilityWZ;

    @Autowired
    public DeclineReasonAdder declineReasonAdder;

    @Autowired
    RuleEvaluatorWZ ruleEvaluatorWZ;

    public void checkEligibility(RequestHeader header, List<RefInstructionRulesPrdDto> ruleDtoList, XMLGregorianCalendar birthDate, List<ProductEligibilityDetails> eligibilityDetails, String candidateInstruction, List<ProductArrangement> productArrangements, String sortCode, String customerId, Customer customerDetails, Product associatedProduct, String arrangementType, String channel, ResultCondition resultCondition, List<String> candidateInstructionList) throws DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg {
        LOGGER.info("Check eligibility");
        boolean anyCRFail = false;
        ProductEligibilityDetails eligibilityDetail = eligibilityDetails.get(0);

        if (ruleDtoList.isEmpty()) {
            eligibilityDetail.setIsEligible(String.valueOf(true));

            LOGGER.info(String.format("Rule list is empty, setting eligibility indicator to true ."));

        } else {
            eligibilityDetail.setIsEligible(String.valueOf(false));

        }
        StringBuffer rules = new StringBuffer("Rules to be evaluated: ");
        for (RefInstructionRulesPrdDto ruleDto : ruleDtoList) {
            rules.append(ruleDto.getRule());
            rules.append(",");
        }
        LOGGER.info(rules);

        for (RefInstructionRulesPrdDto ruleDto : ruleDtoList) {

            try {
                evaluateRule(header, ruleDto, birthDate, eligibilityDetails, candidateInstruction, productArrangements, sortCode, customerId, customerDetails, associatedProduct, arrangementType, channel, resultCondition, candidateInstructionList);

            } catch (NullPointerException npe) {
                String message = String.format("Null Pointer Exception while evaluating rule %s for candidate Instruction %s.", ruleDto.getRule(), candidateInstruction);
                LOGGER.error(message, npe);
                throw exceptionUtilityWZ.internalServiceError(null, new ReasonText(message), header);
            } catch (EligibilityException e) {
                LOGGER.debug(e);
                e.getExternalException(header, candidateInstruction, ruleDto.getRule(), exceptionUtilityWZ);
            }
            String isEligible = eligibilityDetail.getIsEligible();
            if (!isEligible.equalsIgnoreCase("true")) {
                anyCRFail = true;

                String declineReasonCode = "";
                String declineReasonDescription = "";
                if (eligibilityDetail.getDeclineReasons().isEmpty()) {
                    List<ReasonCode> reasonCodes = eligibilityDetail.getDeclineReasons();
                    ReasonCode reason = reasonCodes.get(reasonCodes.size() - 1);

                    declineReasonCode = reason.getCode();
                    declineReasonDescription = reason.getDescription();
                }
                LOGGER.info(String.format("Eligibility failed for candidate Instruction '%s' for customer rule %s  '%s' : '%s'.", candidateInstruction, ruleDto.getRule(), declineReasonCode, declineReasonDescription));
            }

        }
        if (anyCRFail) {
            // Since the rules all individually update the indicator state, a previous false value may have been
            // overwritten with a subsequent true value so we need to ensure that it is reset to false here.
            // This code should be refactored to remove the need to do this.
            eligibilityDetail.setIsEligible(String.valueOf(false));
        }

        LOGGER.info(String.format("Eligibility Status of candidate Instruction '%s' is '%s'.", candidateInstruction, eligibilityDetails.get(0).getIsEligible()));
    }

    private void evaluateRule(RequestHeader header, RefInstructionRulesPrdDto ruleDto, XMLGregorianCalendar birthDate, List<ProductEligibilityDetails> eligibilityDetails, String candidateInstruction, List<ProductArrangement> productArrangements, String sortCode, String customerId, Customer customerDetails, Product associatedProduct, String arrangementType, String channel, ResultCondition resultCondition, List<String> candidateInstructionList) throws EligibilityException, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg {
        EligibilityDecision eligibilityDecision = ruleEvaluatorWZ.evaluateRule(header, ruleDto, birthDate, productArrangements, candidateInstruction, sortCode, customerId, customerDetails, associatedProduct, arrangementType, channel, candidateInstructionList);
        setEligibilityStatus(eligibilityDecision, ruleDto.getRule(), eligibilityDetails, resultCondition);
    }

    private void setEligibilityStatus(EligibilityDecision eligibilityDecision, String cmsReason, List<ProductEligibilityDetails> eligibilityDetails, ResultCondition resultCondition) {
        if (null != eligibilityDecision.getReasonText()) {
            declineReasonAdder.addDeclineReason(cmsReason, eligibilityDecision.getReasonText(), eligibilityDetails.get(0));
        }
        if (eligibilityDecision.isEligible()) {
            eligibilityDetails.get(0).setIsEligible(String.valueOf(eligibilityDecision.isEligible()));
        }
        if (!StringUtils.isEmpty(eligibilityDecision.getRiskBand())) {
            eligibilityDetails.get(0).setRiskBand(eligibilityDecision.getRiskBand());
        }
        if (!StringUtils.isEmpty(eligibilityDecision.getShadowLimit())) {
            eligibilityDetails.get(0).setShadowLimit(eligibilityDecision.getShadowLimit());
        }
        ProductEligibilityDetails eligibilityDetailForKYCStatus = new ProductEligibilityDetails();
        if (null != eligibilityDecision.getKycStatus()) {
            eligibilityDetailForKYCStatus.setKycStatus(eligibilityDecision.getKycStatus().getStatus());
            eligibilityDetails.add(eligibilityDetailForKYCStatus);
        }

        if (null != eligibilityDecision.getExtraConditions()) {
            resultCondition.setExtraConditions(eligibilityDecision.getExtraConditions());
        }
    }
}
