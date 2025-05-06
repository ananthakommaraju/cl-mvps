package com.lloydsbanking.salsa.apapca.service.fulfil.converter;

import com.lloydsbanking.salsa.downstream.cbs.client.e226.E226RequestBuilder;
import com.lloydsbanking.salsa.soap.cbs.e226.objects.E226Req;
import lib_sim_bo.businessobjects.Condition;
import lib_sim_bo.businessobjects.RuleCondition;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class DepositArrangementToE226Request {

    private static final String CONDITION_FOR_PCA_RE_ENGINEERING = "INTEND_TO_SWITCH";

    private static final String CONDITION_FOR_OVERDRAFT = "OVERDRAFT_RISK_CODE";

    private static final int RISK_CODE_DEFAULT = 0;

    private static final int RISK_CODE_FORMAL_ORDER = 2;

    public E226Req getAddInterPartyRelationshipRequest(List<RuleCondition> ruleConditions, String custNumber, BigDecimal overdraftAmount) {

        List<Condition> conditions = new ArrayList<>();
        E226RequestBuilder e226RequestBuilder = new E226RequestBuilder();
        e226RequestBuilder.defaults();
        e226RequestBuilder.nationalSortcodeId(custNumber);
        e226RequestBuilder.shdwDcnFrmlOdrLmtAm(overdraftAmount);
        conditions.addAll(ruleConditions);
        e226RequestBuilder.cardOfferCd(conditions);
        E226Req e226Req = e226RequestBuilder.build();
        e226Req.setRskBndFrmlOdrCd(getRiskCode(ruleConditions));
        return e226Req;
    }

    private int getRiskCode(List<RuleCondition> ruleConditions) {
        boolean isPCAReEngineering = false;
        int riskCode = RISK_CODE_DEFAULT;
        for (RuleCondition ruleCondition : ruleConditions) {
            if (ruleCondition.getResult() != null && CONDITION_FOR_OVERDRAFT.equalsIgnoreCase(ruleCondition.getName())) {
                riskCode = Integer.valueOf(ruleCondition.getResult());
            } else if (ruleCondition.getResult() != null && CONDITION_FOR_PCA_RE_ENGINEERING.equalsIgnoreCase(ruleCondition.getName())) {
                isPCAReEngineering = true;
            }
        }
        return !isPCAReEngineering ? RISK_CODE_FORMAL_ORDER : riskCode;
    }
}
