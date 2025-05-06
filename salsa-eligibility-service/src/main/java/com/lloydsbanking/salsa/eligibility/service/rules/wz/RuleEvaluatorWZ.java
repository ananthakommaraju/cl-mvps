package com.lloydsbanking.salsa.eligibility.service.rules.wz;

import com.lloydsbanking.salsa.downstream.prd.model.RefInstructionRulesPrdDto;
import com.lloydsbanking.salsa.eligibility.service.rules.CommonRuleEvaluator;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.ReasonText;
import com.lloydsbanking.salsa.eligibility.service.utility.header.GmoToGboRequestHeaderConverter;
import com.lloydsbanking.salsa.eligibility.service.utility.wz.ExceptionUtility;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsInternalServiceErrorMsg;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

public class RuleEvaluatorWZ extends CommonRuleEvaluator {
    private static final Logger LOGGER = Logger.getLogger(RuleEvaluatorWZ.class);

    @Autowired
    ExceptionUtility exceptionUtilityWZ;

    public EligibilityDecision evaluateRule(RequestHeader header, RefInstructionRulesPrdDto ruleDto, XMLGregorianCalendar birthDate, List<ProductArrangement> productArrangements, String instruction, String sortCode, String customerId, Customer customerDetails, Product associatedProduct, String arrangementType, String channel, List<String> candidateInstructionList) throws EligibilityException, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg {
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        ruleDataHolder.setRule(ruleDto.getRule());
        ruleDataHolder.setRuleParamValue(ruleDto.getRuleParamValue());
        lb_gbo_sales.messages.RequestHeader requestHeader = new GmoToGboRequestHeaderConverter().convert(header);
        ruleDataHolder.setHeader(requestHeader);
        ruleDataHolder.setCustomerDetails(customerDetails);
        ruleDataHolder.setRuleInsMnemonic(ruleDto.getInsMnemonic());
        ruleDataHolder.setAssociatedProduct(associatedProduct);
        ruleDataHolder.setArrangementType(arrangementType);
        ruleDataHolder.setChannel(channel);
        ruleDataHolder.setCandidateInstructions(candidateInstructionList);
        return getDeclineReason(header, ruleDto, birthDate, instruction, sortCode, customerId, ruleDataHolder);
    }

    private EligibilityDecision getDeclineReason(lib_sim_gmo.messages.RequestHeader header, RefInstructionRulesPrdDto ruleDto, XMLGregorianCalendar birthDate, String candidateInstruction, String sortCode, String customerId, RuleDataHolder ruleDataHolder) throws EligibilityException, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg {
        switch (ruleDto.getRuleType()) {
            case CST_RULE_TYPE:
                return cstRuleMap.get(ruleDto.getRule()).evaluate(ruleDataHolder, birthDate, sortCode, customerId);
            case AGT_RULE_TYPE:
                return agtRuleMap.get(ruleDto.getRule()).evaluate(ruleDataHolder);
            case AGA_RULE_TYPE:
                return agaRuleMap.get(ruleDto.getRule()).evaluate(candidateInstruction, ruleDataHolder);
            case AVN_RULE_TYPE:
                return avnRuleMap.get(ruleDto.getRule()).evaluate(ruleDataHolder, candidateInstruction);
            default:
                LOGGER.error(String.format("Did not match any rule type '%s'.", ruleDto.getRuleType()));
                throw exceptionUtilityWZ.internalServiceError(null, new ReasonText("Rule Code invalid: " + ruleDto.getRuleType()), header);
        }
    }

}
