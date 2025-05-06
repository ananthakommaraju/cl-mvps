package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.downstream.prd.model.RefInstructionRulesPrdDto;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RefInstructionPrdRulesRetriever {
    protected JdbcTemplate jdbcTemplateObject;

    public RefInstructionPrdRulesRetriever(DataSource dataSource) {
        this.jdbcTemplateObject = new JdbcTemplate(dataSource);
    }

    public List<RefInstructionRulesPrdDto> getRefInstructionRulesByInsMnemonicAndBracode(String insMnemonic, String bracode) {

        String query = "SELECT * FROM instruction_rules_vw WHERE INS_MNEMONIC = ? and BRA_CODE = ?";

        Object params[] = new Object[2];
        params[0] = insMnemonic;
        params[1] = bracode;
        List<Map<String, Object>> rs = jdbcTemplateObject.queryForList(query, params);

        return mapRows(rs);

    }

    private List<RefInstructionRulesPrdDto> mapRows(List<Map<String, Object>> rs) {

        List<RefInstructionRulesPrdDto> instructionRules = new ArrayList<RefInstructionRulesPrdDto>();

        for (Map result : rs) {
            RefInstructionRulesPrdDto dto = new RefInstructionRulesPrdDto();

            dto.setInsMnemonic((String) result.get("INS_MNEMONIC"));
            dto.setGroupRule((String) result.get("GROUP_RULE"));
            dto.setGroupDesc((String) result.get("GROUP_DESC"));
            dto.setRule((String) result.get("RULE"));
            dto.setGroupRuleType((String) result.get("GROUP_RULE_TYPE"));
            dto.setRuleDesc((String) result.get("RULE_DESC"));
            dto.setRuleParamValue((String) result.get("RULE_PARAM_VALUE"));
            dto.setRuleType((String) result.get("RULE_TYPE"));
            dto.setBracode((String) result.get("BRA_CODE"));
            if (null != result.get("RULE_PARAM_SEQ")) {
                dto.setRuleParamSeq(new BigDecimal(result.get("RULE_PARAM_SEQ").toString()));
            }
            instructionRules.add(dto);
        }

        return instructionRules;

    }
}