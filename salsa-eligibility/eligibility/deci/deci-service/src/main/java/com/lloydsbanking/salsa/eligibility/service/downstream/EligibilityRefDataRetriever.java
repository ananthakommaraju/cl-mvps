package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.downstream.ref.jdbc.RefInstructionHierarchyDao;
import com.lloydsbanking.salsa.downstream.ref.jdbc.RefInstructionLookupDao;
import com.lloydsbanking.salsa.downstream.ref.jdbc.RefInstructionRulesDao;
import com.lloydsbanking.salsa.downstream.ref.jdbc.RefLookupDao;
import com.lloydsbanking.salsa.downstream.ref.model.InstructionHierarchyId;
import com.lloydsbanking.salsa.downstream.ref.model.InstructionLookupId;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionHierarchyDto;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionLookupDto;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.downstream.ref.model.RefLookupDto;
import com.lloydsbanking.salsa.eligibility.service.utility.ExceptionUtility;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsResourceNotAvailableErrorMsg;
import lb_gbo_sales.messages.RequestHeader;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public class EligibilityRefDataRetriever {
    static final Logger LOGGER = Logger.getLogger(EligibilityRefDataRetriever.class);

    RefInstructionLookupDao refInstructionLookupDao;

    RefInstructionHierarchyDao refInstructionHierarchyDao;

    ExceptionUtility exceptionUtility;

    RefInstructionRulesDao refInstructionRulesDao;

    RefLookupDao refLookupDao;

    private static final String GRP_CODE_RESTRICTED_POST_CODE = "RSTRCTD_PST_CDE";

    @Autowired
    public EligibilityRefDataRetriever(RefInstructionLookupDao refInstructionLookupDao, ExceptionUtility exceptionUtility, RefInstructionHierarchyDao refInstructionHierarchyDao, RefInstructionRulesDao refInstructionRulesDao, RefLookupDao refLookupDao) {

        this.refInstructionLookupDao = refInstructionLookupDao;
        this.refInstructionHierarchyDao = refInstructionHierarchyDao;
        this.exceptionUtility = exceptionUtility;
        this.refInstructionRulesDao = refInstructionRulesDao;
        this.refLookupDao = refLookupDao;

    }

    @Transactional(readOnly = true)
    public String getParentInstruction(String candidateInstruction, String channelId, RequestHeader header) throws DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg {
        RefInstructionHierarchyDto instructionHierarchy = refInstructionHierarchyDao.findOne(new InstructionHierarchyId(candidateInstruction, channelId));
        if (null == instructionHierarchy) {
            LOGGER.error("Data not available Error occured while getting parent instruction mnemonic");
            throw exceptionUtility.dataNotAvailableError(candidateInstruction, "ins_mnemonic", "INSTRUCTION_HIERARCHY_VW", "No matching records found, error code: ", header);
        }
        return instructionHierarchy.getParInsMnemonic();

    }

    @Transactional(readOnly = true)
    public String getProductArrangementInstruction(String host, String ephText, String channel) {
        String instruction = null;
        RefInstructionLookupDto instructionLookupDto = refInstructionLookupDao.findOne(new InstructionLookupId(host, channel, ephText));
        if (instructionLookupDto != null) {
            instruction = instructionLookupDto.getInsMnemonic();
        }
        return instruction;
    }

    @Transactional(readOnly = true)
    public List<String> getChildInstructions(String candidateInstruction, String channelId) {

        List<RefInstructionHierarchyDto> refInstructionHierarchyDtos = refInstructionHierarchyDao.findByParInsMnemonicAndChannel(candidateInstruction, channelId);
        List<String> childMnemonics = null;
        if (!CollectionUtils.isEmpty(refInstructionHierarchyDtos)) {
            childMnemonics = new ArrayList<>();
            for (RefInstructionHierarchyDto refInstructionHierarchyDto : refInstructionHierarchyDtos) {
                childMnemonics.add(refInstructionHierarchyDto.getInsMnemonic());
            }
        }
        return childMnemonics;

    }

    @Transactional(readOnly = true)
    private List<RefInstructionRulesDto> getInstructionCondition(String insMnemonic, String channel) {
        return refInstructionRulesDao.getRefInstructionRulesByInsMnemonicAndChannel(insMnemonic, channel);

    }

    @Transactional(readOnly = true)
    public List<RefInstructionRulesDto> getCompositeInstructionCondition(String childInstruction, String channel, RequestHeader header) throws DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg {

        List<RefInstructionRulesDto> refInstructionRulesDtosForChild = getInstructionCondition(childInstruction, channel);

        if (CollectionUtils.isEmpty(refInstructionRulesDtosForChild)) {
            String parent = getParentInstruction(childInstruction, channel, header);
            refInstructionRulesDtosForChild = getInstructionCondition(parent, channel);

        }

        return (!CollectionUtils.isEmpty(refInstructionRulesDtosForChild) ? refInstructionRulesDtosForChild : new ArrayList<RefInstructionRulesDto>());
    }

    @Transactional(readOnly = true)
    public int getInstructionPriority(String candidateInstruction, String channelId) {

        RefInstructionHierarchyDto instructionHierarchy = refInstructionHierarchyDao.findOne(new InstructionHierarchyId(candidateInstruction, channelId));
        return (null != instructionHierarchy.getInsPriority() ? instructionHierarchy.getInsPriority().intValue() : 0);

    }

    @Transactional(readOnly = true)
    public String getChannelIdFromContactPointId(String contactPointId, RequestHeader requestHeader) throws DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg {
        String channel = null;
        try {
            channel = refLookupDao.getChannelFromContactPointId(contactPointId);
        }
        catch (DataAccessException e) {
            throw e;
        }
        if (StringUtils.isEmpty(channel)) {
            LOGGER.error("Data Not Available Error occured while fetching channel Id from Contact id ");
            throw exceptionUtility.dataNotAvailableError(contactPointId, "lookup_value_sd", "VW_IB_LOOKUPS", "No matching records found, error code: ", requestHeader);
        }

        return channel;
    }

    @Transactional(readOnly = true)
    public List<String> retrieveRestrictedPostCode(String channelId) {
        LOGGER.info("Entering retrieveRestrictedPostCode for groupCode: " + GRP_CODE_RESTRICTED_POST_CODE);
        List<String> restrictedPostCode = new ArrayList();
        List<String> groupCodes = new ArrayList<>();
        StringBuffer postCodes = new StringBuffer(",");
        groupCodes.add(GRP_CODE_RESTRICTED_POST_CODE);
        List<RefLookupDto> refLookupDtos = refLookupDao.findByChannelAndGroupCdIn(channelId, groupCodes);

        if (!CollectionUtils.isEmpty(refLookupDtos)) {
            for (RefLookupDto refLookupDto : refLookupDtos) {
                restrictedPostCode.add(refLookupDto.getLookupTxt());
                postCodes.append(refLookupDto.getLookupTxt());
            }
        }
        LOGGER.info("Exiting retrieveRestrictedPostCode with postCodes: " + postCodes.toString());
        return restrictedPostCode;
    }

}
