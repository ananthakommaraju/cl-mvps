package com.lloydsbanking.salsa.eligibility;

import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.messages.RequestHeader;

import java.math.BigDecimal;
import java.util.List;

public interface ScenarioHelper {
    void clearUp();

    void expectGetParentInstructionCall(String insMnemonic, String insNarrative, Integer insPriority, String parInsMnemonic, String channel, String parInsNarrative);

    void expectGetParentInstructionCallWithEmptyResult();

    void expectGetProductArrangementInstructionCall(String insMnemonic, String host, String extSysId, String channel, String ephText);

    void expectGetProductArrangementInstructionCallWithEmptyResult();

    public void expectGetCompositeInstructionConditionCall(List<RefInstructionRulesDto> refInstructionRulesDtos);

    void expectGetChannelFromContactPointId(String contactPointid, String channel);

    void expectRefDataAvailable();

    void expectF075Call(RequestHeader header, String kycStatus) throws DetermineEligibleInstructionsInternalServiceErrorMsg;

    void expectF075CallWithErrorCode(RequestHeader header, int errorCode163004) throws DetermineEligibleInstructionsInternalServiceErrorMsg;

    void expectE220Call(RequestHeader header, String sortCode, String participantId, String shadowLimit, String strictFlag);

    public void expectCBSGenericGatewaySwitchCall(String channel, boolean switchStatus);

    public void expectB766Call(RequestHeader requestHeader, String sortCode);

    public void expectF336Call(RequestHeader header, int productOneGroupId, int productTwoGroupId);

    public void expectB162Call(RequestHeader header, String spndngRewardId, String productOneAccountType, String productTwoAccountTyp, String sellerEntity);

    public void expectCompositeInstructionConditionCall(String insMnemonic, String groupRule, String groupDesc, String groupCmsReason, String rule, String ruleDesc, String cmsReason, String groupRuleType, String ruleType, String ruleParamValue, String channel, BigDecimal ruleParamSeq);

    void expectB093Call(String threshold, RequestHeader header, boolean isAboveThreshold) throws DetermineEligibleInstructionsInternalServiceErrorMsg;

    public void expectE184CallForCBSIndicator(RequestHeader header, String sortCode, String accountNo, int cbsIndicator);

    public void expectRBBSlookupCall(String channel);
}
