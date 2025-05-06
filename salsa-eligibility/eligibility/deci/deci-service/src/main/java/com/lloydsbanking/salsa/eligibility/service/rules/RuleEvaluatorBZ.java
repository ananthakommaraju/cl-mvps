package com.lloydsbanking.salsa.eligibility.service.rules;

import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.service.rules.common.BusinessArrangementHandler;
import com.lloydsbanking.salsa.eligibility.service.rules.common.BusinessArrangementHandlerBZ;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ExceptionUtility;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.ReasonText;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.businessobjects.ArrangementIdentifier;
import lb_gbo_sales.businessobjects.BusinessArrangement;
import lb_gbo_sales.businessobjects.CustomerInstruction;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

public class RuleEvaluatorBZ extends CommonRuleEvaluator {
    private static final Logger LOGGER = Logger.getLogger(RuleEvaluatorBZ.class);

    @Autowired
    ExceptionUtility exceptionUtility;


    public EligibilityDecision evaluateRule(RequestHeader header, XMLGregorianCalendar birthDate, String sortCode, String customerId, CustomerInstruction customerInstruction, List<ProductArrangement> productArrangements, ArrangementIdentifier arrangementIdentifier, String candidateInstruction, List<BusinessArrangement> businessArrangements, String selectedBusinessId, RefInstructionRulesDto ruleDto) throws EligibilityException, DetermineEligibleInstructionsInternalServiceErrorMsg {
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule(ruleDto.getRule());
        ruleDataHolder.setRuleParamValue(ruleDto.getRuleParamValue());
        ruleDataHolder.setHeader(header);
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        ruleDataHolder.setArrangementIdentifier(arrangementIdentifier);
        BusinessArrangementHandler businessArrangementHandler = new BusinessArrangementHandlerBZ(businessArrangements);
        ruleDataHolder.setBusinessArrangement(businessArrangementHandler);

        return getDeclineReason(header, birthDate, sortCode, customerId, candidateInstruction, selectedBusinessId, ruleDto, ruleDataHolder);
    }

    private EligibilityDecision getDeclineReason(RequestHeader header, XMLGregorianCalendar birthDate, String sortCode, String customerId, String candidateInstruction, String selectedBusinessId, RefInstructionRulesDto ruleDto, RuleDataHolder ruleDataHolder) throws EligibilityException, DetermineEligibleInstructionsInternalServiceErrorMsg {
        switch (ruleDto.getRuleType()) {
            case CST_RULE_TYPE:
                return cstRuleMap.get(ruleDto.getRule()).evaluate(ruleDataHolder, birthDate, sortCode, customerId);
            case AGT_RULE_TYPE:
                return agtRuleMap.get(ruleDto.getRule()).evaluate(ruleDataHolder);
            case AGA_RULE_TYPE:
                return agaRuleMap.get(ruleDto.getRule()).evaluate(candidateInstruction, ruleDataHolder);
            case ASA_RULE_TYPE:
                return asaRuleMap.get(ruleDto.getRule()).evaluate(ruleDataHolder);
            case AVN_RULE_TYPE:
                return avnRuleMap.get(ruleDto.getRule()).evaluate(ruleDataHolder, candidateInstruction);
            case ASB_RULE_TYPE:
                return asbRuleMap.get(ruleDto.getRule()).evaluate(ruleDataHolder, selectedBusinessId, sortCode, customerId);
            default:
                LOGGER.error(String.format("Did not match any rule type '%s'.", ruleDto.getRuleType()));
                throw exceptionUtility.internalServiceError(null, new ReasonText(("Rule Code invalid: " + ruleDto.getRuleType())), header);
        }
    }
}
