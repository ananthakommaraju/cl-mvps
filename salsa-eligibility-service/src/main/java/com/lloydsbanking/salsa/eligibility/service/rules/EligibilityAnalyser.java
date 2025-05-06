package com.lloydsbanking.salsa.eligibility.service.rules;


import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.service.rules.common.DeclineReasonAdder;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ExceptionUtility;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.ReasonText;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.businessobjects.ArrangementIdentifier;
import lb_gbo_sales.businessobjects.BusinessArrangement;
import lb_gbo_sales.businessobjects.CustomerInstruction;
import lb_gbo_sales.businessobjects.DeclineReason;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalBusinessErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsResourceNotAvailableErrorMsg;
import lb_gbo_sales.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

public class EligibilityAnalyser {

    private static final Logger LOGGER = Logger.getLogger(EligibilityAnalyser.class);

    @Autowired
    ExceptionUtility exceptionUtility;

    @Autowired
    public DeclineReasonAdder declineReasonAdder;

    @Autowired
    RuleEvaluatorBZ ruleEvaluatorBZ;


    public void checkEligibility(RequestHeader header, List<RefInstructionRulesDto> ruleDtoList, XMLGregorianCalendar birthDate, String sortCode, String customerId, CustomerInstruction customerInstruction, List<ProductArrangement> productArrangements, ArrangementIdentifier arrangementIdentifier, String candidateInstruction, List<BusinessArrangement> businessArrangements, String selectedBusinessId) throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg, DetermineEligibleInstructionsExternalServiceErrorMsg {
        LOGGER.info("Check eligibility. " + "BirthDate: " + birthDate + " customerId: " + customerId + " arrangementIdentifier: " + arrangementIdentifier + " selectedBusinessId: " + selectedBusinessId);
        boolean anyCRFail = false;

        if (ruleDtoList.isEmpty()) {
            customerInstruction.setEligibilityIndicator(true);

            LOGGER.info(String.format("Rule list is empty, setting eligibility indicator to true ."));

        }
        StringBuffer rules = new StringBuffer("Rules to be evaluated: ");
        for (RefInstructionRulesDto ruleDto : ruleDtoList) {
            rules.append(ruleDto.getRule());
            rules.append(",");
        }
        LOGGER.info(rules);
        for (RefInstructionRulesDto ruleDto : ruleDtoList) {
            try {
                evaluateRule(header, birthDate, sortCode, customerId, customerInstruction, productArrangements, arrangementIdentifier, candidateInstruction, businessArrangements, selectedBusinessId, ruleDto);
            }
            catch (NullPointerException npe) {
                String message = String.format("Null Pointer Exception while evaluating rule %s for candidate Instruction %s.", ruleDto.getRule(), candidateInstruction);
                LOGGER.error(message, npe);
                throw exceptionUtility.internalServiceError(null, new ReasonText(message), header);
            }
            catch (EligibilityException e) {
                LOGGER.debug(e);
                e.getExternalException(header, candidateInstruction, ruleDto.getRule(), exceptionUtility);
            }
            if (!customerInstruction.isEligibilityIndicator()) {
                anyCRFail = true;

                String declineReasonCode = "";
                String declineReasonDescription = "";
                if (!customerInstruction.getDeclineReasons().isEmpty()) {
                    List<DeclineReason> declineReasons = customerInstruction.getDeclineReasons();
                    DeclineReason declineReason = declineReasons.get(declineReasons.size() - 1);
                    declineReasonCode = declineReason.getReasonCode();
                    declineReasonDescription = declineReason.getReasonDescription();
                }
                LOGGER.info(String.format("Eligibility failed for candidate Instruction '%s' for customer rule %s  '%s' : '%s'.", candidateInstruction, ruleDto.getRule(), declineReasonCode, declineReasonDescription));
            }

        }
        if (anyCRFail) {
            // Since the rules all individually update the indicator state, a previous false value may have been
            // overwritten with a subsequent true value so we need to ensure that it is reset to false here.
            // This code should be refactored to remove the need to do this.
            customerInstruction.setEligibilityIndicator(false);
        }
        LOGGER.info(String.format("Eligibility Status of candidate Instruction '%s' is '%s'.", candidateInstruction, customerInstruction.isEligibilityIndicator()));

    }

    private void evaluateRule(RequestHeader header, XMLGregorianCalendar birthDate, String sortCode, String customerId, CustomerInstruction customerInstruction, List<ProductArrangement> productArrangements, ArrangementIdentifier arrangementIdentifier, String candidateInstruction, List<BusinessArrangement> businessArrangements, String selectedBusinessId, RefInstructionRulesDto ruleDto) throws EligibilityException, DetermineEligibleInstructionsInternalServiceErrorMsg {
        EligibilityDecision eligibilityDecision = ruleEvaluatorBZ.evaluateRule(header, birthDate, sortCode, customerId, customerInstruction, productArrangements, arrangementIdentifier, candidateInstruction, businessArrangements, selectedBusinessId, ruleDto);
        setEligibilityStatus(eligibilityDecision, ruleDto.getCmsReason(), customerInstruction);
    }

    private void setEligibilityStatus(EligibilityDecision eligibilityDecision, String cmsReason, CustomerInstruction customerInstruction) {
        if (eligibilityDecision.isEligible()) {
            customerInstruction.setEligibilityIndicator(true);
        }
        else {
            declineReasonAdder.addDeclineReason(cmsReason, eligibilityDecision.getReasonText(), customerInstruction);
        }
    }
}
