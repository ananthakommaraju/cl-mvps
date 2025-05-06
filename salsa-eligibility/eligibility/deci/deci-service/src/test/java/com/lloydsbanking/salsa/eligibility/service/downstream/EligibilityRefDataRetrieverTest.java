package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ref.jdbc.RefInstructionHierarchyDao;
import com.lloydsbanking.salsa.downstream.ref.jdbc.RefInstructionLookupDao;
import com.lloydsbanking.salsa.downstream.ref.jdbc.RefInstructionRulesDao;
import com.lloydsbanking.salsa.downstream.ref.jdbc.RefLookupDao;
import com.lloydsbanking.salsa.downstream.ref.model.*;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.ExceptionUtility;
import com.lloydsbanking.salsa.header.RequestToResponseHeaderConverter;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsResourceNotAvailableErrorMsg;
import lb_gbo_sales.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class EligibilityRefDataRetrieverTest {
    TestDataHelper dataHelper = new TestDataHelper();

    RequestHeader header;

    RefInstructionLookupDao refInstructionLookupDao;

    EligibilityRefDataRetriever eligibilityRefDataRetriever;

    RefInstructionHierarchyDao refInstructionHierarchyDao;

    RefInstructionRulesDao refInstructionRulesDao;

    ExceptionUtility exceptionUtility;

    DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg dataNotAvailableErrorfaultMsg;

    RefLookupDao refLookupDao;

    private RefInstructionRulesDto lookupDto = new RefInstructionRulesDto("G_PPC", "GR0003", "No valid credit card account for PPC", "GR0003", "CR012", "Customer already has PPC on an account", "CR012", "GRP", "AGT", null, "IBL", new BigDecimal("1"));

    private RefLookupDto refDataLookupDto = new RefLookupDto();

    @Before
    public void setUp() {
        refInstructionLookupDao = mock(RefInstructionLookupDao.class);
        refInstructionHierarchyDao = mock(RefInstructionHierarchyDao.class);
        refLookupDao = mock(RefLookupDao.class);
        refInstructionRulesDao = mock(RefInstructionRulesDao.class);
        refLookupDao = mock(RefLookupDao.class);
        exceptionUtility = new ExceptionUtility(new RequestToResponseHeaderConverter());
        eligibilityRefDataRetriever = new EligibilityRefDataRetriever(refInstructionLookupDao, exceptionUtility, refInstructionHierarchyDao, refInstructionRulesDao, refLookupDao);
        header = dataHelper.createEligibilityRequestHeader(dataHelper.TEST_RETAIL_CHANNEL_ID, dataHelper.TEST_INTERACTION_ID, dataHelper.TEST_OCIS_ID, dataHelper.TEST_CUSTOMER_ID, dataHelper.TEST_CONTACT_POINT_ID);
    }

    @Test
    public void testGetParentInstructionReturnsParentMnemonic() throws DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg {

        RefInstructionHierarchyDto hierarchyDto = new RefInstructionHierarchyDto("P_RB_1Y_Y", "Reset Bond 1 Year Y", null, "G_RB_TD", "IBV", "Reset Bond");
        when(refInstructionHierarchyDao.findOne(new InstructionHierarchyId("P_RB_1Y_Y", dataHelper.TEST_RETAIL_CHANNEL_ID))).thenReturn(hierarchyDto);
        eligibilityRefDataRetriever.getParentInstruction("P_RB_1Y_Y", dataHelper.TEST_RETAIL_CHANNEL_ID, header);
        assertEquals("G_RB_TD", eligibilityRefDataRetriever.getParentInstruction("P_RB_1Y_Y", dataHelper.TEST_RETAIL_CHANNEL_ID, header));
    }

    @Test
    public void testGetParentInstructionReturnsErrorForNullResponse() throws DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg {
        try {
            when(refInstructionHierarchyDao.findOne(new InstructionHierarchyId("P_RB_1Y_Y", dataHelper.TEST_RETAIL_CHANNEL_ID))).thenReturn(null);
            eligibilityRefDataRetriever.getParentInstruction("P_RB_1Y_Y", dataHelper.TEST_RETAIL_CHANNEL_ID, header);
        } catch (DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg errorfault1Msg) {
            dataNotAvailableErrorfaultMsg = errorfault1Msg;
            assertEquals("No matching records found, error code: ", dataNotAvailableErrorfaultMsg.getFaultInfo().getDescription());
            assertEquals("P_RB_1Y_Y", dataNotAvailableErrorfaultMsg.getFaultInfo().getKey());
            assertEquals("INSTRUCTION_HIERARCHY_VW", dataNotAvailableErrorfaultMsg.getFaultInfo().getEntity());
            assertEquals("ins_mnemonic", dataNotAvailableErrorfaultMsg.getFaultInfo().getField());
        }

    }

    @Test
    public void testGetProductArrangementInstruction() {
        RefInstructionLookupDto businessLookupDto = new RefInstructionLookupDto("P_BSAVINGS", "T", new BigDecimal("4"), "STL", "8000776000");
        RefInstructionLookupDto retailLookupDto = new RefInstructionLookupDto("P_CLUB", "T", new BigDecimal("4"), "IBL", "1001776000");
        when(refInstructionLookupDao.findOne(new InstructionLookupId("T", dataHelper.TEST_RETAIL_CHANNEL_ID, "8000776000"))).thenReturn(businessLookupDto);
        when(refInstructionLookupDao.findOne(new InstructionLookupId("T", dataHelper.TEST_RETAIL_CHANNEL_ID, "1001776000"))).thenReturn(retailLookupDto);
        assertEquals("P_BSAVINGS", eligibilityRefDataRetriever.getProductArrangementInstruction("T", "8000776000", dataHelper.TEST_RETAIL_CHANNEL_ID));
        assertEquals("P_CLUB", eligibilityRefDataRetriever.getProductArrangementInstruction("T", "1001776000", dataHelper.TEST_RETAIL_CHANNEL_ID));
    }

    @Test
    public void testGetProductArrangementInstructionForNullResponse() throws DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg {

        when(refInstructionLookupDao.findOne(new InstructionLookupId("T", dataHelper.TEST_RETAIL_CHANNEL_ID, "8000776000"))).thenReturn(null);
        when(refInstructionLookupDao.findOne(new InstructionLookupId("T", dataHelper.TEST_RETAIL_CHANNEL_ID, "1001776000"))).thenReturn(null);
        assertEquals(null, eligibilityRefDataRetriever.getProductArrangementInstruction("T", "8000776000", dataHelper.TEST_RETAIL_CHANNEL_ID));
        assertEquals(null, eligibilityRefDataRetriever.getProductArrangementInstruction("T", "1001776000", dataHelper.TEST_RETAIL_CHANNEL_ID));

    }

    @Test
    public void testGetChildInstructions() {
        RefInstructionHierarchyDto hierarchyDto = new RefInstructionHierarchyDto("P_RB_1Y_Y", "Reset Bond 1 Year Y", null, "G_RB_TD", "IBL", "Reset Bond");
        List<RefInstructionHierarchyDto> refInstructionHierarchyDtos = new ArrayList();
        refInstructionHierarchyDtos.add(hierarchyDto);
        when(refInstructionHierarchyDao.findByParInsMnemonicAndChannel("G_RB_TD", dataHelper.TEST_RETAIL_CHANNEL_ID)).thenReturn(refInstructionHierarchyDtos);

        assertEquals("P_RB_1Y_Y", eligibilityRefDataRetriever.getChildInstructions("G_RB_TD", dataHelper.TEST_RETAIL_CHANNEL_ID).get(0));

    }

    @Test
    public void testGetChildInstructionsForNullResponse() {
        when(refInstructionHierarchyDao.findByParInsMnemonicAndChannel("G_RB_TD", dataHelper.TEST_RETAIL_CHANNEL_ID)).thenReturn(null);

        assertNull(null, eligibilityRefDataRetriever.getChildInstructions("G_RB_TD", dataHelper.TEST_RETAIL_CHANNEL_ID));

    }

    @Test
    public void testGetCompositeInstructionCondition() throws DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg {
        List<RefInstructionRulesDto> refInstructionRulesDtos = new ArrayList();
        refInstructionRulesDtos.add(lookupDto);
        when(refInstructionRulesDao.getRefInstructionRulesByInsMnemonicAndChannel("P_PPC", "IBL")).thenReturn(refInstructionRulesDtos);
        RefInstructionHierarchyDto hierarchyDto = new RefInstructionHierarchyDto("P_PPC", "Payment Protection Cover", null, "G_PPC", "IBL", "Payment Protection Cover");

        when(refInstructionHierarchyDao.findOne(new InstructionHierarchyId("P_PPC", dataHelper.TEST_RETAIL_CHANNEL_ID))).thenReturn(hierarchyDto);
        try {
            assertEquals("G_PPC", eligibilityRefDataRetriever.getParentInstruction("P_PPC", dataHelper.TEST_RETAIL_CHANNEL_ID, header));
        } catch (DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg dataNotAvailableErrorfault1Msg) {
            dataNotAvailableErrorfault1Msg.printStackTrace();
        }
        List<InstructionHierarchyId> instructionPNRulesIdArrayList = new ArrayList();
        instructionPNRulesIdArrayList.add(new InstructionHierarchyId("G_PPC", "IBL"));
        when(refInstructionRulesDao.getRefInstructionRulesByInsMnemonicAndChannel("G_PPC", "IBL")).thenReturn(refInstructionRulesDtos);

        assertEquals("G_PPC", eligibilityRefDataRetriever.getCompositeInstructionCondition("P_PPC", dataHelper.TEST_RETAIL_CHANNEL_ID, header).get(0).getInsMnemonic());
        assertEquals("AGT", eligibilityRefDataRetriever.getCompositeInstructionCondition("P_PPC", dataHelper.TEST_RETAIL_CHANNEL_ID, header).get(0).getRuleType());
        assertEquals("GR0003", eligibilityRefDataRetriever.getCompositeInstructionCondition("P_PPC", dataHelper.TEST_RETAIL_CHANNEL_ID, header).get(0).getGroupRule());
    }

    @Test
    public void testGetCompositeInstructionConditionThrowsErrorWhenRulesNotAvailableForChildAndParentUnavailable() throws DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg {

        List<InstructionHierarchyId> instructionRulesIdArrayList = new ArrayList();
        instructionRulesIdArrayList.add(new InstructionHierarchyId("G_PPC", "IBL"));

        //parent is unavailable
        when(refInstructionRulesDao.getRefInstructionRulesByInsMnemonicAndChannel("G_PPC", "IBL")).thenReturn(null);
        try {
            eligibilityRefDataRetriever.getCompositeInstructionCondition("G_PPC", dataHelper.TEST_RETAIL_CHANNEL_ID, header);
        } catch (DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg dataNotAvailableErrorfaultMsg) {
            assertEquals("INSTRUCTION_HIERARCHY_VW", dataNotAvailableErrorfaultMsg.getFaultInfo().getEntity());
            assertEquals("ins_mnemonic", dataNotAvailableErrorfaultMsg.getFaultInfo().getField());
            assertEquals("No matching records found, error code: ", dataNotAvailableErrorfaultMsg.getFaultInfo().getDescription());
            assertEquals("G_PPC", dataNotAvailableErrorfaultMsg.getFaultInfo().getKey());

        }

    }

    @Test
    public void testGetInstructionPriority() {

        RefInstructionHierarchyDto hierarchyDto = new RefInstructionHierarchyDto("P_RB_1Y_Y", "Reset Bond 1 Year Y", null, "G_RB_TD", "IBV", "Reset Bond");
        when(refInstructionHierarchyDao.findOne(new InstructionHierarchyId("P_RB_1Y_Y", dataHelper.TEST_RETAIL_CHANNEL_ID))).thenReturn(hierarchyDto);
        eligibilityRefDataRetriever.getInstructionPriority("P_RB_1Y_Y", dataHelper.TEST_RETAIL_CHANNEL_ID);
        assertEquals(0, eligibilityRefDataRetriever.getInstructionPriority("P_RB_1Y_Y", dataHelper.TEST_RETAIL_CHANNEL_ID));
    }

    @Test
    public void testGetChannelIdFromContactPointId() throws DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg {

        header = dataHelper.createEligibilityRequestHeader(dataHelper.TEST_RETAIL_CHANNEL_ID, dataHelper.TEST_INTERACTION_ID, dataHelper.TEST_OCIS_ID, dataHelper.TEST_CUSTOMER_ID, dataHelper.TEST_CONTACT_POINT_ID);

        when(refLookupDao.getChannelFromContactPointId(dataHelper.TEST_CONTACT_POINT_ID)).thenReturn(dataHelper.TEST_RETAIL_CHANNEL_ID);
        String channel = eligibilityRefDataRetriever.getChannelIdFromContactPointId(dataHelper.TEST_CONTACT_POINT_ID, header);
        assertEquals("IBL", channel);
    }

    @Test
    public void testGetChannelIdFromContactPointIdThrowsErrorForNullChannel() throws DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg {

        header = dataHelper.createEligibilityRequestHeader(dataHelper.TEST_RETAIL_CHANNEL_ID, dataHelper.TEST_INTERACTION_ID, dataHelper.TEST_OCIS_ID, dataHelper.TEST_CUSTOMER_ID, dataHelper.TEST_CONTACT_POINT_ID);

        when(refLookupDao.getChannelFromContactPointId(dataHelper.TEST_CONTACT_POINT_ID)).thenReturn(null);
        try {
            eligibilityRefDataRetriever.getChannelIdFromContactPointId(dataHelper.TEST_CONTACT_POINT_ID, header);
        } catch (DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg dataNotAvailableErrorfault1Msg) {
            assertEquals("VW_IB_LOOKUPS", dataNotAvailableErrorfault1Msg.getFaultInfo().getEntity());
            assertEquals("lookup_value_sd", dataNotAvailableErrorfault1Msg.getFaultInfo().getField());
            assertEquals("No matching records found, error code: ", dataNotAvailableErrorfault1Msg.getFaultInfo().getDescription());
            assertEquals(dataHelper.TEST_CONTACT_POINT_ID, dataNotAvailableErrorfault1Msg.getFaultInfo().getKey());
        }
    }

    @Test
    public void testRetrieveRestrictedPostCode() {
        List<RefLookupDto> refLookupDto = new ArrayList();
        refDataLookupDto.setLookupTxt("135");
        refLookupDto.add(refDataLookupDto);
        List<String> groupCodes = new ArrayList<>();
        groupCodes.add("RSTRCTD_PST_CDE");
        when(refLookupDao.findByChannelAndGroupCdIn("IBL", groupCodes)).thenReturn(refLookupDto);

        List<String> restrictedPostCodes = eligibilityRefDataRetriever.retrieveRestrictedPostCode("IBL");
        assertEquals(1, restrictedPostCodes.size());
        assertEquals("135", restrictedPostCodes.get(0));
    }

    @Test
    public void testRetrieveRestrictedPostCodeReturnsEmptyList() {
        List<RefLookupDto> refLookupDto = new ArrayList();
        List<String> groupCodes = new ArrayList<>();
        groupCodes.add("RSTRCTD_PST_CDE");
        when(refLookupDao.findByChannelAndGroupCdIn("IBL", groupCodes)).thenReturn(refLookupDto);

        List<String> restrictedPostCodes = eligibilityRefDataRetriever.retrieveRestrictedPostCode("IBL");
        assertTrue(restrictedPostCodes.isEmpty());

    }

}









