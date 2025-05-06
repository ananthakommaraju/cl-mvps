package com.lloydsbanking.salsa.eligibility.service.rules;

import com.lloydsbanking.salsa.eligibility.service.rules.aga.AGAEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.agt.AGTEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.asa.ASAEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.asb.ASBEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.avn.AVNEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.cst.CSTEligibilityRule;

import java.util.Map;

public class CommonRuleEvaluator {
    public static final String CST_RULE_TYPE = "CST";

    public static final String AGT_RULE_TYPE = "AGT";

    public static final String AGA_RULE_TYPE = "AGA";

    public static final String ASA_RULE_TYPE = "ASA";

    public static final String AVN_RULE_TYPE = "AVN";

    public static final String ASB_RULE_TYPE = "ASB";

    protected Map<String, CSTEligibilityRule> cstRuleMap;

    protected Map<String, AGTEligibilityRule> agtRuleMap;

    protected Map<String, AGAEligibilityRule> agaRuleMap;

    protected Map<String, ASAEligibilityRule> asaRuleMap;

    protected Map<String, AVNEligibilityRule> avnRuleMap;

    protected Map<String, ASBEligibilityRule> asbRuleMap;

    public void setCstRuleMap(Map<String, CSTEligibilityRule> cstRuleMap) {
        this.cstRuleMap = cstRuleMap;
    }


    public void setAgtRuleMap(Map<String, AGTEligibilityRule> agtRuleMap) {
        this.agtRuleMap = agtRuleMap;
    }


    public void setAgaRuleMap(Map<String, AGAEligibilityRule> agaRuleMap) {
        this.agaRuleMap = agaRuleMap;
    }


    public void setAsaRuleMap(Map<String, ASAEligibilityRule> asaRuleMap) {
        this.asaRuleMap = asaRuleMap;
    }


    public void setAvnRuleMap(Map<String, AVNEligibilityRule> avnRuleMap) {
        this.avnRuleMap = avnRuleMap;
    }


    public void setAsbRuleMap(Map<String, ASBEligibilityRule> asbRuleMap) {
        this.asbRuleMap = asbRuleMap;
    }
}
