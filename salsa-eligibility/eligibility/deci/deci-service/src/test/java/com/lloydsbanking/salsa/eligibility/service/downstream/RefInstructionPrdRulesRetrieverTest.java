package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.SpringJndiJunit4TestRunner;
import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.config.JndiProperties;
import com.lloydsbanking.salsa.downstream.prd.model.RefInstructionRulesPrdDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category(UnitTest.class)
@RunWith(SpringJndiJunit4TestRunner.class)
@JndiProperties("") // SpringJndiJunit4TestRunner needs this even though we have no properties
@ContextConfiguration(locations = {"classpath:com/lloydsbanking/salsa/eligibility/service/dao-config.xml"})
public class RefInstructionPrdRulesRetrieverTest {
    RefInstructionPrdRulesRetriever retriever;

    @Autowired
    DataSource dataSource;

    @Before
    public void setUp() {
        retriever = new RefInstructionPrdRulesRetriever(dataSource);
        retriever.jdbcTemplateObject.execute("delete from instruction_rules_vw");
    }

    @Test
    public void testGetRefInstructionRulesByInsMnemonicAndBracode() throws SQLException {
        retriever.jdbcTemplateObject.update("insert into instruction_rules_vw (ins_mnemonic,bra_code,rule_param_seq,rule,group_rule, group_desc, rule_desc, group_rule_type, rule_type) values('G_PPC','LTB',1,'CR012', 'GR001', 'No valid credit card account for PPC', 'Customer already has PPC on an account', 'GRP', 'AGT')");
        List<RefInstructionRulesPrdDto> dtos = retriever.getRefInstructionRulesByInsMnemonicAndBracode("G_PPC", "LTB");

        assertEquals(0, dtos.get(0).getRuleParamSeq().compareTo(new BigDecimal(1)));
        assertEquals("LTB", dtos.get(0).getBracode());
        assertEquals("G_PPC", dtos.get(0).getInsMnemonic());
        assertEquals("No valid credit card account for PPC", dtos.get(0).getGroupDesc());
        assertEquals("GR001", dtos.get(0).getGroupRule());
        assertEquals("CR012", dtos.get(0).getRule());
        assertEquals("Customer already has PPC on an account", dtos.get(0).getRuleDesc());
        assertEquals("GRP", dtos.get(0).getGroupRuleType());
        assertEquals("AGT", dtos.get(0).getRuleType());
        assertEquals(null, dtos.get(0).getRuleParamValue());
    }

    @Test
    public void testGetRefInstructionRulesByInsMnemonicAndBracodeReturnsEmptyList() throws SQLException {
        List<RefInstructionRulesPrdDto> dtos = retriever.getRefInstructionRulesByInsMnemonicAndBracode("G_PPC", "BOS");

        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }
}
