package com.lloydsbanking.salsa.eligibility.service.rules;

import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.service.rules.EligibilityAnalyser;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.businessobjects.ArrangementIdentifier;
import lb_gbo_sales.businessobjects.BusinessArrangement;
import lb_gbo_sales.businessobjects.CustomerInstruction;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalBusinessErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsResourceNotAvailableErrorMsg;
import lb_gbo_sales.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class RBBSEligibilityAnalyser {
    private static final Logger LOGGER = Logger.getLogger(RBBSEligibilityAnalyser.class);
    @Autowired
    EligibilityAnalyser eligibilityAnalyser;

    Set<String> defaultRules = new HashSet(
            Arrays.asList("CR002", "CR050", "CR051", "CR052", "CR053", "CR054", "CR055"));
    Set<String> cbsIndRules = new HashSet(
            Arrays.asList("CR056", "CR059", "CR061"));
    Set<String> audEvntsRules = new HashSet(
            Arrays.asList("CR057"));
    Set<String> strctFlgRules = new HashSet(
            Arrays.asList("CR058", "CR060"));


    public void checkEligibilityForRBBSProducts(RequestHeader header, List<RefInstructionRulesDto> rulesDtos, XMLGregorianCalendar birthDate, String sortCode, String customerId, CustomerInstruction customerInstruction, List<ProductArrangement> productArrangements, ArrangementIdentifier arrangementIdentifier, String candidateInstruction, List<BusinessArrangement> businessArrangements, String selectedBusinessId) throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg, DetermineEligibleInstructionsExternalServiceErrorMsg {
        LOGGER.info("Check eligibility for RBBS. ");
        List<RefInstructionRulesDto> defaultRuleCond = new ArrayList();
        List<RefInstructionRulesDto> cbsIndRuleCond = new ArrayList();
        List<RefInstructionRulesDto> audEvntsRuleCond = new ArrayList();
        List<RefInstructionRulesDto> strctFlgRuleCond = new ArrayList();
        List<RefInstructionRulesDto> allOtherRuleCond = new ArrayList();

        for (RefInstructionRulesDto instructionRulesDto : rulesDtos) {
            if (defaultRules.contains(instructionRulesDto.getRule())) {
                defaultRuleCond.add(instructionRulesDto);
            } else if (cbsIndRules.contains(instructionRulesDto.getRule())) {
                cbsIndRuleCond.add(instructionRulesDto);
            } else if (audEvntsRules.contains(instructionRulesDto.getRule())) {
                audEvntsRuleCond.add(instructionRulesDto);
            } else if (strctFlgRules.contains(instructionRulesDto.getRule())) {
                strctFlgRuleCond.add(instructionRulesDto);
            } else {
                allOtherRuleCond.add(instructionRulesDto);
            }

        }

        eligibilityAnalyser.checkEligibility(header, defaultRuleCond, birthDate, sortCode, customerId, customerInstruction,
            productArrangements, arrangementIdentifier, candidateInstruction, businessArrangements, selectedBusinessId);
        if (customerInstruction.isEligibilityIndicator()) {
            eligibilityAnalyser.checkEligibility(header, cbsIndRuleCond, birthDate, sortCode, customerId, customerInstruction,
                productArrangements, arrangementIdentifier, candidateInstruction, businessArrangements, selectedBusinessId);
        }
        if (customerInstruction.isEligibilityIndicator()) {
            eligibilityAnalyser.checkEligibility(header, audEvntsRuleCond, birthDate, sortCode, customerId, customerInstruction,
                productArrangements, arrangementIdentifier, candidateInstruction, businessArrangements, selectedBusinessId);
        }
        if (customerInstruction.isEligibilityIndicator()) {
            eligibilityAnalyser.checkEligibility(header, strctFlgRuleCond, birthDate, sortCode, customerId, customerInstruction,
                productArrangements, arrangementIdentifier, candidateInstruction, businessArrangements, selectedBusinessId);
        }
    }

}
