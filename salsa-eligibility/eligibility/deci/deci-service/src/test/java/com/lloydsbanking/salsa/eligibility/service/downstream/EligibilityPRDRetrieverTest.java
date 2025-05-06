package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.prd.jdbc.FetchChildInstructionDao;
import com.lloydsbanking.salsa.downstream.prd.jdbc.RefInstructionHierarchyPrdDao;
import com.lloydsbanking.salsa.downstream.prd.jdbc.RefInstructionLookupPrdDao;
import com.lloydsbanking.salsa.downstream.prd.jdbc.RefInstructionRulesPrdDao;
import com.lloydsbanking.salsa.downstream.prd.model.FetchChildInstructionDto;
import com.lloydsbanking.salsa.downstream.prd.model.InstructionLookupId;
import com.lloydsbanking.salsa.downstream.prd.model.RefInstructionHierarchyPrdDto;
import com.lloydsbanking.salsa.downstream.prd.model.RefInstructionLookupPrdDto;
import com.lloydsbanking.salsa.downstream.prd.model.RefInstructionRulesPrdDto;
import com.lloydsbanking.salsa.eligibility.service.utility.wz.ExceptionUtility;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class EligibilityPRDRetrieverTest {
    TestDataHelper dataHelper = new TestDataHelper();

    RequestHeader header;

    EligibilityPRDRetriever eligibilityPRDRetriever;

    RefInstructionLookupPrdDao refInstructionLookupPrdDao;

    FetchChildInstructionDao fetchChildInstructionDao;

    RefInstructionHierarchyPrdDao refInstructionHierarchyPrdDao;

    RefInstructionRulesPrdDao refInstructionRulesPrdDao;

    RefInstructionPrdRulesRetriever refInstructionPrdRulesRetriever;

    @Before
    public void setUp() {

        refInstructionLookupPrdDao = mock(RefInstructionLookupPrdDao.class);
        fetchChildInstructionDao = mock(FetchChildInstructionDao.class);
        refInstructionHierarchyPrdDao = mock(RefInstructionHierarchyPrdDao.class);
        refInstructionRulesPrdDao = mock(RefInstructionRulesPrdDao.class);
        refInstructionPrdRulesRetriever = mock(RefInstructionPrdRulesRetriever.class);

        eligibilityPRDRetriever = new EligibilityPRDRetriever(refInstructionLookupPrdDao, fetchChildInstructionDao, refInstructionHierarchyPrdDao, refInstructionRulesPrdDao, new ExceptionUtility(mock(RequestToResponseHeaderConverter.class)), refInstructionPrdRulesRetriever);
        eligibilityPRDRetriever.exceptionUtilityWZ = mock(ExceptionUtility.class);
        header = dataHelper.createEligibilityRequestHeader(dataHelper.TEST_RETAIL_CHANNEL_ID, dataHelper.TEST_INTERACTION_ID, dataHelper.TEST_OCIS_ID, dataHelper.TEST_CUSTOMER_ID, dataHelper.TEST_CONTACT_POINT_ID);

    }

    @Test
    public void testGetChildInstructions() {
        InstructionLookupId instructionLookupId = new InstructionLookupId("P_AB_1Y_Y", "0004", "IBL", "1524306000");
        RefInstructionLookupPrdDto lookupDto = new RefInstructionLookupPrdDto(instructionLookupId);
        List<RefInstructionLookupPrdDto> refInstructionLookupDtos = new ArrayList();
        refInstructionLookupDtos.add(lookupDto);
        when(refInstructionLookupPrdDao.findByExtSysIdAndProdIdAndBrand("0004", dataHelper.TEST_PROD_ID, dataHelper.TEST_BRAND)).thenReturn(refInstructionLookupDtos);
        assertEquals("P_AB_1Y_Y", eligibilityPRDRetriever.getChildInstructions("0004", "1524306000", "IBL"));

    }

    @Test
    public void testGetChildInstructionsForNullResponse() {
        List<RefInstructionLookupPrdDto> refInstructionLookupDtos = new ArrayList();
        when(refInstructionLookupPrdDao.findByExtSysIdAndProdIdAndBrand("0004", "1524306000", "IBL")).thenReturn(refInstructionLookupDtos);
        assertEquals(null, eligibilityPRDRetriever.getChildInstructions("0004", "1524306000", "IBL"));

    }

    @Test
    public void testRetrieveInstructionsHierarchyForGrndPrnt()

    {

        FetchChildInstructionDto fetchChildInstructionDto = new FetchChildInstructionDto("P_CISA_FR_1Y_M", "Fixed Rate ISA Online 18Month", "null", 1000494L, "G_ISA", "ISA", "1000006", "LTB");
        List<FetchChildInstructionDto> fetchChildInstructionDtos = new ArrayList<>();

        fetchChildInstructionDtos.add(fetchChildInstructionDto);
        when(fetchChildInstructionDao.findByParentMnemonicAndBrandCode("P_CISA_FR_1Y_M", "LTB")).thenReturn(fetchChildInstructionDtos);
        assertEquals("P_CISA_FR_1Y_M", eligibilityPRDRetriever.retrieveInstructionsHierarchyForGrndPrnt("P_CISA_FR_1Y_M", "LTB").get(0));

    }

    @Test
    public void testRetrieveInstructionsHierarchyForGrndPrntForNullResponse() {
        List<FetchChildInstructionDto> fetchChildInstructionDtos = new ArrayList<>();
        when(fetchChildInstructionDao.findByParentMnemonicAndBrandCode("P_CISA_FR_1Y_M", "LTB")).thenReturn(fetchChildInstructionDtos);
        assertTrue(eligibilityPRDRetriever.retrieveInstructionsHierarchyForGrndPrnt("P_CISA_FR_1Y_M", "LTB").isEmpty());

    }

    @Test
    public void testRetrieveChildInstructionsHierarchy() {
        RefInstructionHierarchyPrdDto refInstructionHierarchyPrdDto = new RefInstructionHierarchyPrdDto("P_RB_1Y_Y", "Reset bond for one year", "G_RB_TD", "Reset Bond", null, "HLX");
        List<RefInstructionHierarchyPrdDto> refInstructionHierarchyDtos = new ArrayList<>();

        refInstructionHierarchyDtos.add(refInstructionHierarchyPrdDto);
        when(refInstructionHierarchyPrdDao.findByParInsMnemonicAndBraCode("P_RB_1Y_Y", "HLX")).thenReturn(refInstructionHierarchyDtos);
        assertEquals("P_RB_1Y_Y", eligibilityPRDRetriever.retrieveChildInstructionsHierarchy("P_RB_1Y_Y", "HLX").get(0));
    }

    @Test
    public void testretRieveChildInstructionsHierarchyForNullResponse() {
        List<RefInstructionHierarchyPrdDto> refInstructionHierarchyPrdDtos = new ArrayList<>();
        when(refInstructionHierarchyPrdDao.findByParInsMnemonicAndBraCode("P_RB_1Y_Y", "HLX")).thenReturn(refInstructionHierarchyPrdDtos);
        assertTrue(eligibilityPRDRetriever.retrieveChildInstructionsHierarchy("P_RB_1Y_Y", "HLX").isEmpty());

    }

    @Test
    public void testGetCompositeInstructionConditionsForRefInstructionRulesPrdDtosIsSuccessful() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg {
        RefInstructionRulesPrdDto refInstructionRulesPrdDto = new RefInstructionRulesPrdDto("G_PPC", "GR0003", "No valid credit card account for PPC", "CR012", "Customer already has PPC on an account", "GRP", "AGT", null, "IBL", new BigDecimal("1"));

        List<RefInstructionRulesPrdDto> refInstructionRulesPrdDtos = new ArrayList<>();
        refInstructionRulesPrdDtos.add(refInstructionRulesPrdDto);

        when(refInstructionPrdRulesRetriever.getRefInstructionRulesByInsMnemonicAndBracode("P_RB_1Y_Y", TestDataHelper.TEST_RETAIL_CHANNEL_ID)).thenReturn(refInstructionRulesPrdDtos);

        List<RefInstructionRulesPrdDto> instructionRulesPrdDtoList = eligibilityPRDRetriever.getCompositeInstructionConditions("P_RB_1Y_Y", TestDataHelper.TEST_RETAIL_CHANNEL_ID, header, "P_RB_1Y_Y");

        assertEquals(refInstructionRulesPrdDtos, instructionRulesPrdDtoList);
    }

    @Test
    public void testGetCompositeInstructionConditionsForRefInstructionRulesPrdDtosNullForChild() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg {
        RefInstructionRulesPrdDto refInstructionRulesPrdDto = new RefInstructionRulesPrdDto("G_PPC", "GR0003", "No valid credit card account for PPC", "CR012", "Customer already has PPC on an account", "GRP", "AGT", null, "IBL", new BigDecimal("1"));

        List<RefInstructionRulesPrdDto> refInstructionRulesPrdDtos = new ArrayList<>();
        refInstructionRulesPrdDtos.add(refInstructionRulesPrdDto);

        RefInstructionHierarchyPrdDto instructionHierarchy = new RefInstructionHierarchyPrdDto("P_RB_1Y_Y", "Reset bond for one year", "G_RB_TD", "Reset Bond", null, "HLX");
        List<RefInstructionHierarchyPrdDto> refInstructionHierarchyPrdDtos = new ArrayList<>();
        refInstructionHierarchyPrdDtos.add(instructionHierarchy);

        when(refInstructionPrdRulesRetriever.getRefInstructionRulesByInsMnemonicAndBracode("P_RB_1Y_Y", TestDataHelper.TEST_RETAIL_CHANNEL_ID)).thenReturn(null);
        when(refInstructionHierarchyPrdDao.findByInsMnemonicAndBraCode("P_RB_1Y_Y", TestDataHelper.TEST_RETAIL_CHANNEL_ID)).thenReturn(refInstructionHierarchyPrdDtos);
        when(refInstructionPrdRulesRetriever.getRefInstructionRulesByInsMnemonicAndBracode(instructionHierarchy.getParInsMnemonic(), TestDataHelper.TEST_RETAIL_CHANNEL_ID)).thenReturn(refInstructionRulesPrdDtos);

        List<RefInstructionRulesPrdDto> instructionRulesPrdDtoList = eligibilityPRDRetriever.getCompositeInstructionConditions("P_RB_1Y_Y", TestDataHelper.TEST_RETAIL_CHANNEL_ID, header, "P_RB_1Y_Y");

        assertEquals(refInstructionRulesPrdDtos, instructionRulesPrdDtoList);
    }

    @Test
    public void testGetCompositeInstructionConditionsForRefInstructionRulesPrdDtosNullForChildAndParentNotFound() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg {
        RefInstructionRulesPrdDto refInstructionRulesPrdDto = new RefInstructionRulesPrdDto("G_PPC", "GR0003", "No valid credit card account for PPC", "CR012", "Customer already has PPC on an account", "GRP", "AGT", null, "IBL", new BigDecimal("1"));

        List<RefInstructionRulesPrdDto> refInstructionRulesPrdDtos = new ArrayList<>();
        refInstructionRulesPrdDtos.add(refInstructionRulesPrdDto);

        RefInstructionHierarchyPrdDto instructionHierarchy = new RefInstructionHierarchyPrdDto("P_RB_1Y_Y", "Reset bond for one year", null, "Reset Bond", null, "HLX");
        List<RefInstructionHierarchyPrdDto> refInstructionHierarchyPrdDtos = new ArrayList<>();
        refInstructionHierarchyPrdDtos.add(instructionHierarchy);

        when(refInstructionPrdRulesRetriever.getRefInstructionRulesByInsMnemonicAndBracode("P_RB_1Y_Y", TestDataHelper.TEST_RETAIL_CHANNEL_ID)).thenReturn(null);
        when(refInstructionHierarchyPrdDao.findByInsMnemonicAndBraCode("P_RB_1Y_Y", TestDataHelper.TEST_RETAIL_CHANNEL_ID)).thenReturn(refInstructionHierarchyPrdDtos);
        when(refInstructionPrdRulesRetriever.getRefInstructionRulesByInsMnemonicAndBracode(instructionHierarchy.getParInsMnemonic(), TestDataHelper.TEST_RETAIL_CHANNEL_ID)).thenReturn(refInstructionRulesPrdDtos);

        List<RefInstructionRulesPrdDto> instructionRulesPrdDtoList = eligibilityPRDRetriever.getCompositeInstructionConditions("P_RB_1Y_Y", TestDataHelper.TEST_RETAIL_CHANNEL_ID, header, "P_RB_1Y_Y");

        assertTrue(instructionRulesPrdDtoList.isEmpty());
    }

    @Test
    public void testGetCompositeInstructionConditionsForRefInstructionRulesPrdDtosNullForChildAndParent() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg {

        RefInstructionHierarchyPrdDto instructionHierarchy = new RefInstructionHierarchyPrdDto("P_RB_1Y_Y", "Reset bond for one year", "G_RB_TD", "Reset Bond", null, "HLX");
        List<RefInstructionHierarchyPrdDto> refInstructionHierarchyPrdDtos = new ArrayList<>();
        refInstructionHierarchyPrdDtos.add(instructionHierarchy);
        when(refInstructionPrdRulesRetriever.getRefInstructionRulesByInsMnemonicAndBracode("P_RB_1Y_Y", TestDataHelper.TEST_RETAIL_CHANNEL_ID)).thenReturn(null);
        when(refInstructionHierarchyPrdDao.findByInsMnemonicAndBraCode("P_RB_1Y_Y", TestDataHelper.TEST_RETAIL_CHANNEL_ID)).thenReturn(refInstructionHierarchyPrdDtos);
        when(refInstructionPrdRulesRetriever.getRefInstructionRulesByInsMnemonicAndBracode(instructionHierarchy.getParInsMnemonic(), TestDataHelper.TEST_RETAIL_CHANNEL_ID)).thenReturn(new ArrayList<RefInstructionRulesPrdDto>());

        List<RefInstructionRulesPrdDto> instructionRulesPrdDtoList = eligibilityPRDRetriever.getCompositeInstructionConditions("P_RB_1Y_Y", TestDataHelper.TEST_RETAIL_CHANNEL_ID, header, "P_RB_1Y_Y");

        assertTrue(instructionRulesPrdDtoList.isEmpty());
    }

    @Test
    public void testGetCompositeInstructionConditionsForRefInstructionRulesPrdDtosEmpty() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg {
        RefInstructionRulesPrdDto refInstructionRulesPrdDto = new RefInstructionRulesPrdDto("G_PPC", "GR0003", "No valid credit card account for PPC", "CR012", "Customer already has PPC on an account", "GRP", "AGT", null, "IBL", new BigDecimal("1"));

        List<RefInstructionRulesPrdDto> rulesPrdDtos = new ArrayList<>();

        List<RefInstructionRulesPrdDto> refInstructionRulesPrdDtos = new ArrayList<>();
        refInstructionRulesPrdDtos.add(refInstructionRulesPrdDto);

        RefInstructionHierarchyPrdDto instructionHierarchy = new RefInstructionHierarchyPrdDto("P_RB_1Y_Y", "Reset bond for one year", "G_RB_TD", "Reset Bond", null, "HLX");
        List<RefInstructionHierarchyPrdDto> refInstructionHierarchyPrdDtos = new ArrayList<>();
        refInstructionHierarchyPrdDtos.add(instructionHierarchy);
        when(refInstructionPrdRulesRetriever.getRefInstructionRulesByInsMnemonicAndBracode("P_RB_1Y_Y", TestDataHelper.TEST_RETAIL_CHANNEL_ID)).thenReturn(rulesPrdDtos);
        when(refInstructionHierarchyPrdDao.findByInsMnemonicAndBraCode("P_RB_1Y_Y", TestDataHelper.TEST_RETAIL_CHANNEL_ID)).thenReturn(refInstructionHierarchyPrdDtos);
        when(refInstructionPrdRulesRetriever.getRefInstructionRulesByInsMnemonicAndBracode(instructionHierarchy.getParInsMnemonic(), TestDataHelper.TEST_RETAIL_CHANNEL_ID)).thenReturn(refInstructionRulesPrdDtos);

        List<RefInstructionRulesPrdDto> instructionRulesPrdDtoList = eligibilityPRDRetriever.getCompositeInstructionConditions("P_RB_1Y_Y", TestDataHelper.TEST_RETAIL_CHANNEL_ID, header, "P_RB_1Y_Y");

        assertEquals(refInstructionRulesPrdDtos, instructionRulesPrdDtoList);
    }

    @Test(expected = DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg.class)
    public void testGetParentInstructionForNullResponse() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg {
        when(eligibilityPRDRetriever.exceptionUtilityWZ.dataNotAvailableError("P_RB_1Y_Y", "ins_mnemonic", "INSTRUCTION_HIERARCHY_VW", "No matching records found, error code: ", header)).thenThrow(DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg.class);
        eligibilityPRDRetriever.getParentInstruction("P_RB_1Y_Y", TestDataHelper.TEST_RETAIL_CHANNEL_ID, header);
    }

    @Test
    public void testGetChildInstructionsWhenExtSysCdIsNull() {
        InstructionLookupId instructionLookupId = new InstructionLookupId("P_AB_1Y_Y", "0004", "IBL", "1524306000");
        RefInstructionLookupPrdDto lookupDto = new RefInstructionLookupPrdDto(instructionLookupId);
        List<RefInstructionLookupPrdDto> refInstructionLookupDtos = new ArrayList();
        refInstructionLookupDtos.add(lookupDto);
        when(eligibilityPRDRetriever.refInstructionLookupPrdDao.findByInstructionLookupIdProdIdAndInstructionLookupIdBrand(dataHelper.TEST_PROD_ID, dataHelper.TEST_BRAND)).thenReturn(refInstructionLookupDtos);
        assertEquals("P_AB_1Y_Y", eligibilityPRDRetriever.getChildInstructions(null, "1524306000", "IBL"));
        verify(eligibilityPRDRetriever.refInstructionLookupPrdDao).findByInstructionLookupIdProdIdAndInstructionLookupIdBrand(dataHelper.TEST_PROD_ID, dataHelper.TEST_BRAND);
    }

    @Test
    public void testGetChildInstructionsWhenExtSysCdAndBrandIsNull() {
        InstructionLookupId instructionLookupId = new InstructionLookupId("P_AB_1Y_Y", "0004", "IBL", "1524306000");
        RefInstructionLookupPrdDto lookupDto = new RefInstructionLookupPrdDto(instructionLookupId);
        List<RefInstructionLookupPrdDto> refInstructionLookupDtos = new ArrayList();
        refInstructionLookupDtos.add(lookupDto);
        when(eligibilityPRDRetriever.refInstructionLookupPrdDao.findByInstructionLookupIdProdId(dataHelper.TEST_PROD_ID)).thenReturn(refInstructionLookupDtos);
        assertEquals("P_AB_1Y_Y", eligibilityPRDRetriever.getChildInstructions(null, "1524306000", null));
        verify(eligibilityPRDRetriever.refInstructionLookupPrdDao).findByInstructionLookupIdProdId(dataHelper.TEST_PROD_ID);
    }

    @Test
    public void testGetChildInstructionsWhenBrandIsNull() {
        InstructionLookupId instructionLookupId = new InstructionLookupId("P_AB_1Y_Y", "0004", "IBL", "1524306000");
        RefInstructionLookupPrdDto lookupDto = new RefInstructionLookupPrdDto(instructionLookupId);
        List<RefInstructionLookupPrdDto> refInstructionLookupDtos = new ArrayList();
        refInstructionLookupDtos.add(lookupDto);
        when(eligibilityPRDRetriever.refInstructionLookupPrdDao.findByInstructionLookupIdExtSysIdAndInstructionLookupIdProdId("0004", dataHelper.TEST_PROD_ID)).thenReturn(refInstructionLookupDtos);
        assertEquals("P_AB_1Y_Y", eligibilityPRDRetriever.getChildInstructions("0004", "1524306000", null));
        verify(eligibilityPRDRetriever.refInstructionLookupPrdDao).findByInstructionLookupIdExtSysIdAndInstructionLookupIdProdId("0004", dataHelper.TEST_PROD_ID);
    }
}



