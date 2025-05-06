package com.lloydsbanking.salsa.apapca.service.fulfil.converter;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.soap.cbs.e226.objects.E226Req;
import lib_sim_bo.businessobjects.RuleCondition;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Category(UnitTest.class)
public class DepositArrangementToE226RequestTest {
    DepositArrangementToE226Request depositArrangementToE226Request;

    E226Req e226Req;

    List<RuleCondition> ruleConditions;

    RuleCondition ruleCondition;

    @Before
    public void setUp() {
        depositArrangementToE226Request = new DepositArrangementToE226Request();
        e226Req = new E226Req();
        ruleConditions = new ArrayList<>();
        ruleCondition = new RuleCondition();

    }

    @Test
    public void testGetAddInterPartyRelationshipRequest() {

        ruleCondition.setName("Heloo");
        ruleCondition.setResult("123");
        ruleConditions.add(ruleCondition);
        e226Req = depositArrangementToE226Request.getAddInterPartyRelationshipRequest(ruleConditions, "customer", new BigDecimal(45));
        assertEquals("cu", e226Req.getCustNoGp().getNationalSortcodeId());
        assertEquals("45", e226Req.getShdwDcnFrmlOdrLmtAm());
        assertEquals("0", e226Req.getShdwDcnCrcdLmtAm());
        assertEquals("D", e226Req.getShdwDcnCrcdCd());

    }

    @Test
    public void testGetAddInterPartyRelationshipRequestWithNull() {
        ruleConditions.add(new RuleCondition());
        e226Req = depositArrangementToE226Request.getAddInterPartyRelationshipRequest(ruleConditions, "", new BigDecimal(45));
        assertNull(e226Req.getCustNoGp());

    }

}
