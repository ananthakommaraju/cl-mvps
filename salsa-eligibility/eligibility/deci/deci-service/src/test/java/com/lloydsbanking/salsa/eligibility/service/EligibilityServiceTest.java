package com.lloydsbanking.salsa.eligibility.service;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.logging.EligibilityLogService;
import com.lloydsbanking.salsa.eligibility.service.downstream.EligibilityRefDataRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.EligibilityAnalyser;
import com.lloydsbanking.salsa.eligibility.service.rules.RuleEvaluatorBZ;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.ExceptionUtility;
import com.lloydsbanking.salsa.eligibility.service.validator.RequestValidator;
import com.lloydsbanking.salsa.header.HeaderRetriever;
import com.lloydsbanking.salsa.header.RequestToResponseHeaderConverter;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.*;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import lb_gbo_sales.messages.DetermineElegibleInstructionsResponse;
import lb_gbo_sales.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class EligibilityServiceTest {
    TestDataHelper dataHelper = new TestDataHelper();

    EligibilityService service;

    private RequestToResponseHeaderConverter requestToResponseHeaderConverter;

    @Before
    public void setUp() {
        service = new EligibilityService();
        service.eligibilityLogService = mock(EligibilityLogService.class);
        service.headerRetriever = new HeaderRetriever();
        service.requestValidator = mock(RequestValidator.class);
        service.eligibilityRefDataRetriever = mock(EligibilityRefDataRetriever.class);
        service.eligibilityAnalyser = mock(EligibilityAnalyser.class);
        service.requestToResponseHeaderConverter = mock(RequestToResponseHeaderConverter.class);
        service.eligibilityDataHelper = new EligibilityDataHelper();
    }

    @Test
    public void testInitialisesAndClearsLogging() throws Exception {

        RequestHeader requestHeader = (new TestDataHelper()).createEligibilityRequestHeader(dataHelper.TEST_RETAIL_CHANNEL_ID, dataHelper.TEST_INTERACTION_ID, dataHelper.TEST_OCIS_ID, dataHelper.TEST_CUSTOMER_ID, dataHelper.TEST_CONTACT_POINT_ID);
        DetermineElegibileInstructionsRequest request = new DetermineElegibileInstructionsRequest();
        request.setHeader(requestHeader);
        DetermineElegibleInstructionsResponse response = service.determineEligibleInstructions(request);
        assertNotNull(response);
        verify(service.eligibilityLogService).initialiseContext(requestHeader);
        verify(service.eligibilityLogService).clearContext();
    }

    @Test
    public void testProductMnemonicAndParentInsMnemonicSetInRequest() throws Exception {

        DetermineElegibileInstructionsRequest request = dataHelper.createEligibilityRequest("P_TRAV_MON", TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        when(service.eligibilityRefDataRetriever.getProductArrangementInstruction(request.getCustomerArrangements()
                .get(0)
                .getAccountHost(), "0071776000", TestDataHelper.TEST_RETAIL_CHANNEL_ID)).thenReturn("P_J_ISA");
        when(service.eligibilityRefDataRetriever.getProductArrangementInstruction(request.getCustomerArrangements()
                .get(1)
                .getAccountHost(), "120300552157", TestDataHelper.TEST_RETAIL_CHANNEL_ID)).thenReturn("P_JR_ISA");
        when(service.eligibilityRefDataRetriever.getParentInstruction("P_J_ISA", TestDataHelper.TEST_RETAIL_CHANNEL_ID, request.getHeader())).thenReturn("G_ISA");
        when(service.eligibilityRefDataRetriever.getParentInstruction("P_JR_ISA", TestDataHelper.TEST_RETAIL_CHANNEL_ID, request.getHeader())).thenReturn("G_ISA");
        when(service.eligibilityRefDataRetriever.getChannelIdFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, request.getHeader())).thenReturn("IBL");

        service.determineEligibleInstructions(request);
        verify(service.eligibilityRefDataRetriever).getProductArrangementInstruction(request.getCustomerArrangements()
                .get(0)
                .getAccountHost(), "0071776000", TestDataHelper.TEST_RETAIL_CHANNEL_ID);
        assertEquals("P_J_ISA", request.getCustomerArrangements().get(0).getInstructionMnemonic());
        assertEquals("P_JR_ISA", request.getCustomerArrangements().get(1).getInstructionMnemonic());
        assertEquals("G_ISA", request.getCustomerArrangements().get(0).getParentInstructionMnemonic());
        assertEquals("G_ISA", request.getCustomerArrangements().get(1).getParentInstructionMnemonic());
    }

    @Test
    public void testChildInstructionRetrieved() throws Exception {

        DetermineElegibileInstructionsRequest request = dataHelper.createEligibilityRequest("P_TRAV_MON", TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        when(service.eligibilityRefDataRetriever.getProductArrangementInstruction(request.getCustomerArrangements()
                .get(0)
                .getAccountHost(), "0071776000", TestDataHelper.TEST_RETAIL_CHANNEL_ID)).thenReturn("P_J_ISA");
        when(service.eligibilityRefDataRetriever.getProductArrangementInstruction(request.getCustomerArrangements()
                .get(1)
                .getAccountHost(), "120300552157", TestDataHelper.TEST_RETAIL_CHANNEL_ID)).thenReturn("P_JR_ISA");
        when(service.eligibilityRefDataRetriever.getParentInstruction("P_J_ISA", TestDataHelper.TEST_RETAIL_CHANNEL_ID, request.getHeader())).thenReturn("G_ISA");
        when(service.eligibilityRefDataRetriever.getParentInstruction("P_JR_ISA", TestDataHelper.TEST_RETAIL_CHANNEL_ID, request.getHeader())).thenReturn("G_ISA");
        when(service.eligibilityRefDataRetriever.getChannelIdFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, request.getHeader())).thenReturn("IBL");
        List<String> mnemonicList = new ArrayList();
        mnemonicList.add("P_ISA");
        when(service.eligibilityRefDataRetriever.getChildInstructions(request.getCandidateInstructions().get(0), TestDataHelper.TEST_RETAIL_CHANNEL_ID)).thenReturn(mnemonicList);
        DetermineElegibleInstructionsResponse response = service.determineEligibleInstructions(request);
        verify(service.eligibilityRefDataRetriever, atLeastOnce()).getChildInstructions(request.getCandidateInstructions().get(0), TestDataHelper.TEST_RETAIL_CHANNEL_ID);
        assertEquals("P_ISA", response.getCustomerInstructions().get(0).getInstructionMnemonic());
    }


    public void testAccountType() throws DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {

        DetermineElegibileInstructionsRequest request = dataHelper.createEligibilityRequest("P_TRAV_MON", TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        request.getCustomerArrangements().get(0).setAccountHost(null);
        when(service.eligibilityRefDataRetriever.getProductArrangementInstruction(request.getCustomerArrangements()
                .get(0)
                .getAccountType()
                .substring(0, 1), request.getCustomerArrangements().get(0).getAccountType().substring(1), TestDataHelper.TEST_RETAIL_CHANNEL_ID)).thenReturn("P_J_ISA");
        when(service.eligibilityRefDataRetriever.getParentInstruction("P_J_ISA", TestDataHelper.TEST_RETAIL_CHANNEL_ID, request.getHeader())).thenReturn("G_ISA");
        when(service.eligibilityRefDataRetriever.getChannelIdFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, request.getHeader())).thenReturn("IBL");

        service.determineEligibleInstructions(request);
        verify(service.eligibilityRefDataRetriever).getProductArrangementInstruction(request.getCustomerArrangements()
                .get(0)
                .getAccountHost(), "0071776000", TestDataHelper.TEST_RETAIL_CHANNEL_ID);
        assertEquals("P_J_ISA", request.getCustomerArrangements().get(0).getInstructionMnemonic());
        assertEquals("G_ISA", request.getCustomerArrangements().get(0).getParentInstructionMnemonic());

    }

    @Test(expected = DetermineEligibleInstructionsInternalServiceErrorMsg.class)
    public void responseShouldBeNullWhenAnInvalidRuleTypeIsSupplied() throws Exception {

        service.eligibilityAnalyser = new EligibilityAnalyser();
        Class analyzer = service.eligibilityAnalyser.getClass();
        setRuleEvaluator(analyzer);
        DetermineElegibileInstructionsRequest request = dataHelper.createEligibilityRequest("P_TRAV_MON", TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        when(service.eligibilityRefDataRetriever.getProductArrangementInstruction(request.getCustomerArrangements()
                .get(0)
                .getAccountType()
                .substring(0, 1), request.getCustomerArrangements().get(0).getAccountType().substring(1), TestDataHelper.TEST_RETAIL_CHANNEL_ID)).thenReturn("P_J_ISA");
        when(service.eligibilityRefDataRetriever.getParentInstruction("P_J_ISA", TestDataHelper.TEST_RETAIL_CHANNEL_ID, request.getHeader())).thenReturn("G_ISA");
        when(service.eligibilityRefDataRetriever.getChannelIdFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, request.getHeader())).thenReturn("IBL");
        ArrayList<RefInstructionRulesDto> refInstructionRulesDtos = new ArrayList<RefInstructionRulesDto>();
        RefInstructionRulesDto refInstructionRulesDto = new RefInstructionRulesDto();
        refInstructionRulesDto.setRuleType("invalid");
        refInstructionRulesDto.setRule("CR001");
        refInstructionRulesDto.setRuleParamValue("75");
        refInstructionRulesDtos.add(refInstructionRulesDto);

        when(service.eligibilityRefDataRetriever.getCompositeInstructionCondition("P_TRAV_MON", "IBL", request.getHeader())).thenReturn(refInstructionRulesDtos);

        DetermineElegibleInstructionsResponse determineElegibleInstructionsResponse = service.determineEligibleInstructions(request);

        assertNull(determineElegibleInstructionsResponse);

    }

    private void setRuleEvaluator(Class analyzer) throws NoSuchFieldException, IllegalAccessException {
        Field ruleEvaluatorBZ = getField(analyzer, "ruleEvaluatorBZ");
        ruleEvaluatorBZ.setAccessible(true);
        RuleEvaluatorBZ evaluatorBZ = new RuleEvaluatorBZ();
        Class evalutor = evaluatorBZ.getClass();
        setExceptionUtility(evalutor, evaluatorBZ);
        ruleEvaluatorBZ.set(service.eligibilityAnalyser, evaluatorBZ);
    }

    private void setExceptionUtility(Class analyzer, RuleEvaluatorBZ evaluatorBZ) throws NoSuchFieldException, IllegalAccessException {
        Field ruleEvaluatorBZ = getField(analyzer, "exceptionUtility");
        ruleEvaluatorBZ.setAccessible(true);
        ruleEvaluatorBZ.set(evaluatorBZ, new ExceptionUtility(new RequestToResponseHeaderConverter()));
    }

    private static Field getField(Class clazz, String fieldName)
            throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            } else {
                return getField(superClass, fieldName);
            }
        }
    }



}
