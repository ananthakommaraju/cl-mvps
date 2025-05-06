package com.lloydsbanking.salsa.eligibility.wz;

import com.lloydsbanking.salsa.downstream.prd.model.RefInstructionRulesPrdDto;
import com.lloydstsb.schema.enterprise.lcsm_involvedpartymanagement.ErrorInfo;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.messages.RequestHeader;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public interface ScenarioHelper {
    void clearUp();

    void expectGetPamLookUpData(String groupCode, String lookUpvalueDesc, String description, String lookupText, String channel, Long sequence);

    void expectPamData();

    void expectGetPrdInstructionLookupData(String instMnemonic, String extSysId, String prodId, String brand);

    void expectGetPrdInstructionHierarchyData(String instMnemonic, String insDescription, String parInsMnemonic, String parInsDescription, Integer insPriority, String braCode);

    void expectGetPrdFetchChildInstructionData(String childMnemonic, String childDescription, String childPriority, String childInsId, String parentMnemonic, String parentDescription, String parentId, String brandCode);

    void expectRefInstructionRulesPrdData(String insMnemonic, String groupRule, String groupDesc, String rule, String groupRuleType, String ruleDesc, String ruleParamValue, String ruleType, String bracode, BigDecimal ruleParamSeq);

    void expectCompositeRefInstructionRulesPrdData(List<RefInstructionRulesPrdDto> refInstructionRulesPrdDtos);

    void expectCBSGenericGatewaySwitchCall(String channel, boolean switchStatus);

    void expectB766Call(RequestHeader requestHeader, String sortCode);

    void expectE220Call(RequestHeader header, String sortCode, String participantId, String shadowLimitZero, String stricFlag);

    void expectE591Call(RequestHeader header, String sortCode, String participantId, String shadowLimitZero, String stricFlag, String cbsDecisionCd);

    void expectE141Call(RequestHeader header, List<Integer> indicators, String sortCode, String accNo, String maxLimitAmt);

    void expectRefLookUpGrdData(BigDecimal lookupId, String groupCd, String groupCdDesc, String lookupIn, String lookupType, BigDecimal lookupCd, String lookupTxt, String lookupValSd, String lookupValLd, String lookupValMd, String lookupValLcl, String active, String channel, BigDecimal displaySqn);

    void expectF075Call(RequestHeader header, String kycStatus, String customerId, String partyEvidenceTypeCd, String partyEvidenceRefTx, String addrEvidenceTypeCd, String addrEvidenceRefTx) throws DetermineEligibleInstructionsInternalServiceErrorMsg;

    void expectPamApplications(String ocisId, int daysPrior);

    void expectMultipleCashISASwitchCall(String channel, boolean switchStatus);

    void expectB695Call(RequestHeader header, String accType, String eventType) throws ErrorInfo;

    void expectExternalSystemProductsData(Long id, String esCode, Long proId, String externalSysProdId);

    void expectProductEligibilityRulesData(final Long id, final String petCode, final Long appliedProductId, final Long existingProductID, final Timestamp startDate, final Timestamp endDate);
}
