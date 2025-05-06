package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.downstream.prd.jdbc.FetchChildInstructionDao;
import com.lloydsbanking.salsa.downstream.prd.jdbc.RefInstructionHierarchyPrdDao;
import com.lloydsbanking.salsa.downstream.prd.jdbc.RefInstructionLookupPrdDao;
import com.lloydsbanking.salsa.downstream.prd.jdbc.RefInstructionRulesPrdDao;
import com.lloydsbanking.salsa.downstream.prd.model.FetchChildInstructionDto;
import com.lloydsbanking.salsa.downstream.prd.model.RefInstructionHierarchyPrdDto;
import com.lloydsbanking.salsa.downstream.prd.model.RefInstructionLookupPrdDto;
import com.lloydsbanking.salsa.downstream.prd.model.RefInstructionRulesPrdDto;
import com.lloydsbanking.salsa.eligibility.service.utility.wz.ExceptionUtility;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public class EligibilityPRDRetriever {
    static final Logger LOGGER = Logger.getLogger(EligibilityPRDRetriever.class);

    RefInstructionLookupPrdDao refInstructionLookupPrdDao;

    FetchChildInstructionDao fetchChildInstructionDao;

    RefInstructionHierarchyPrdDao refInstructionHierarchyPrdDao;

    RefInstructionRulesPrdDao refInstructionRulesPrdDao;

    RefInstructionPrdRulesRetriever refInstructionPrdRulesRetriever;

    ExceptionUtility exceptionUtilityWZ;

    @Autowired
    public EligibilityPRDRetriever(RefInstructionLookupPrdDao refInstructionLookupPrdDao, FetchChildInstructionDao fetchChildInstructionDao, RefInstructionHierarchyPrdDao refInstructionHierarchyPrdDao, RefInstructionRulesPrdDao refInstructionRulesPrdDao, ExceptionUtility exceptionUtilityWZ, RefInstructionPrdRulesRetriever refInstructionPrdRulesRetriever) {
        this.refInstructionLookupPrdDao = refInstructionLookupPrdDao;
        this.fetchChildInstructionDao = fetchChildInstructionDao;
        this.refInstructionHierarchyPrdDao = refInstructionHierarchyPrdDao;
        this.refInstructionRulesPrdDao = refInstructionRulesPrdDao;
        this.exceptionUtilityWZ = exceptionUtilityWZ;
        this.refInstructionPrdRulesRetriever = refInstructionPrdRulesRetriever;
    }

    @Transactional(readOnly = true)
    public String getChildInstructions(String extSysId, String prodId, String brand) {
        List<RefInstructionLookupPrdDto> refInstructionLookupDtos;
        if (null != extSysId) {
            if (null != brand) {
                refInstructionLookupDtos = refInstructionLookupPrdDao.findByExtSysIdAndProdIdAndBrand(extSysId, prodId, brand);
            }
            else {
                refInstructionLookupDtos = refInstructionLookupPrdDao.findByInstructionLookupIdExtSysIdAndInstructionLookupIdProdId(extSysId, prodId);
            }
        }
        else {
            if (null != brand) {
                refInstructionLookupDtos = refInstructionLookupPrdDao.findByInstructionLookupIdProdIdAndInstructionLookupIdBrand(prodId, brand);
            }
            else {
                refInstructionLookupDtos = refInstructionLookupPrdDao.findByInstructionLookupIdProdId(prodId);
            }
        }

        if (!CollectionUtils.isEmpty(refInstructionLookupDtos)) {
            return refInstructionLookupDtos.get(0).getInstructionLookupId().getInstMnemonic();
        }
        return null;

    }

    @Transactional(readOnly = true)
    public List<String> retrieveInstructionsHierarchyForGrndPrnt(String mnemonic, String brandCode) {
        List<String> childInstructions = new ArrayList<>();
        List<FetchChildInstructionDto> fetchChildInstructionDtos = fetchChildInstructionDao.findByParentMnemonicAndBrandCode(mnemonic, brandCode);
        if (!CollectionUtils.isEmpty(fetchChildInstructionDtos)) {

            for (FetchChildInstructionDto fetchChildInstructionDto : fetchChildInstructionDtos) {
                childInstructions.add(fetchChildInstructionDto.getChildMnemonic());
            }

        }
        return childInstructions;
    }

    @Transactional(readOnly = true)
    public List<String> retrieveChildInstructionsHierarchy(String parentMnemonic, String brandCode) {
        List<String> childInstructions = new ArrayList<>();

        List<RefInstructionHierarchyPrdDto> refInstructionHierarchyDtos = refInstructionHierarchyPrdDao.findByParInsMnemonicAndBraCode(parentMnemonic, brandCode);
        if (!CollectionUtils.isEmpty(refInstructionHierarchyDtos)) {

            for (RefInstructionHierarchyPrdDto hierarchyDto : refInstructionHierarchyDtos) {
                childInstructions.add(hierarchyDto.getInsMnemonic());
            }

        }
        return childInstructions;
    }

    @Transactional(readOnly = true)
    public List<RefInstructionRulesPrdDto> retrieveProductEligibilityRules(String candidateInstruction, String brandCode) {
        return refInstructionPrdRulesRetriever.getRefInstructionRulesByInsMnemonicAndBracode(candidateInstruction, brandCode);

    }

    @Transactional(readOnly = true)
    public List<RefInstructionRulesPrdDto> getCompositeInstructionConditions(String childInstruction, String channel, RequestHeader header, String customerInstruction) throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg {

        List<RefInstructionRulesPrdDto> refInstructionRulesPrdDtos = retrieveProductEligibilityRules(childInstruction, channel);
        if (CollectionUtils.isEmpty(refInstructionRulesPrdDtos)) {
            String parentOrCustomerInstruction = null;
            if (!StringUtils.isEmpty(customerInstruction) && customerInstruction.equals(childInstruction)) {
                try {
                    parentOrCustomerInstruction = getParentInstruction(childInstruction, channel, header);
                }
                catch (DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg dataNotAvailableErrorMsg) {
                    LOGGER.info("No Parent Instruction found for childInstruction", dataNotAvailableErrorMsg);
                }
            }
            else{
                parentOrCustomerInstruction = customerInstruction;
            }

            if (null != parentOrCustomerInstruction) {
                refInstructionRulesPrdDtos = retrieveProductEligibilityRules(parentOrCustomerInstruction, channel);
            }

        }
        return (!CollectionUtils.isEmpty(refInstructionRulesPrdDtos) ? refInstructionRulesPrdDtos : new ArrayList<RefInstructionRulesPrdDto>());

    }

    @Transactional(readOnly = true)
    public String getParentInstruction(String candidateInstruction, String channelId, RequestHeader header) throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg {
        List<RefInstructionHierarchyPrdDto> instructionHierarchys = refInstructionHierarchyPrdDao.findByInsMnemonicAndBraCode(candidateInstruction, channelId);

        if (CollectionUtils.isEmpty(instructionHierarchys)) {
            LOGGER.info("No matching records found for table INSTRUCTION_HIERARCHY_VW for INS_MNEMONIC: " + candidateInstruction + " channelId: " + channelId);
            throw exceptionUtilityWZ.dataNotAvailableError(candidateInstruction, "ins_mnemonic", "INSTRUCTION_HIERARCHY_VW", "No matching records found, error code: ", header);
        }
        return instructionHierarchys.get(0).getParInsMnemonic();

    }
}










