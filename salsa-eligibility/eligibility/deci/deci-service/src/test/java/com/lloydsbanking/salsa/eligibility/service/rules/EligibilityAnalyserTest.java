package com.lloydsbanking.salsa.eligibility.service.rules;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.CBSIndicatorRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.aga.AGAEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.aga.CR006ExistingProductRule;
import com.lloydsbanking.salsa.eligibility.service.rules.aga.CR053AccountOpeningDateRule;
import com.lloydsbanking.salsa.eligibility.service.rules.aga.CR059IndicatorTypeRule;
import com.lloydsbanking.salsa.eligibility.service.rules.agt.AGTEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.agt.CR007CreditCardStatusRule;
import com.lloydsbanking.salsa.eligibility.service.rules.common.DeclineReasonAdder;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.rules.cst.CR001CustomerAgeRule;
import com.lloydsbanking.salsa.eligibility.service.rules.cst.CR019CustomerAgeRule;
import com.lloydsbanking.salsa.eligibility.service.rules.cst.CR022ShadowLimitRule;
import com.lloydsbanking.salsa.eligibility.service.rules.cst.CSTEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.utility.ExceptionUtility;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.header.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.soap.cbs.e184.objects.E184Resp;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.businessobjects.ArrangementIdentifier;
import lb_gbo_sales.businessobjects.CreditCardFinanceServiceArrangement;
import lb_gbo_sales.businessobjects.CreditCardStatus;
import lb_gbo_sales.businessobjects.CustomerInstruction;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalBusinessErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsResourceNotAvailableErrorMsg;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@Category(UnitTest.class)
public class EligibilityAnalyserTest {

    private TestDataHelper testDataHelper;

    EligibilityAnalyser eligibilityAnalyser;

    DetermineElegibileInstructionsRequest upstreamRequest;

    CustomerInstruction customerInstruction;

    RefInstructionRulesDto rulesDto;

    String candidateInstruction;

    List<ProductArrangement> customerArrangements;

    Map<String, CSTEligibilityRule> cstRuleMap;

    Map<String, AGAEligibilityRule> agaRuleMap;

    Map<String, AGTEligibilityRule> agtRuleMap;

    CR001CustomerAgeRule cr001CustomerAgeRule;

    CR006ExistingProductRule cr006ExistingProductRule;

    CR053AccountOpeningDateRule cr053AccountOpeningDateRule;

    CR059IndicatorTypeRule cr059IndicatorTypeRule;

    CR022ShadowLimitRule cr022CR022ShadowLimitRule;

    RuleEvaluatorBZ testRuleEvaluatorBZ = new RuleEvaluatorBZ();

    @Before
    public void setUp() {
        eligibilityAnalyser = new EligibilityAnalyser();
        eligibilityAnalyser.ruleEvaluatorBZ = testRuleEvaluatorBZ;
        eligibilityAnalyser.exceptionUtility = new ExceptionUtility(new RequestToResponseHeaderConverter());
        eligibilityAnalyser.declineReasonAdder = new DeclineReasonAdder();
        testDataHelper = new TestDataHelper();
        candidateInstruction = "P_MNEMONIC";
        cr006ExistingProductRule = new CR006ExistingProductRule();
        cr053AccountOpeningDateRule = new CR053AccountOpeningDateRule();
        cr059IndicatorTypeRule = new CR059IndicatorTypeRule();


        upstreamRequest = testDataHelper.createEligibilityRequest(candidateInstruction, testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);
        cstRuleMap = new HashMap<>();
        agaRuleMap = new HashMap<>();
        agtRuleMap = new HashMap<>();

        customerInstruction = new CustomerInstruction();
        customerInstruction.setEligibilityIndicator(false);


        cr001CustomerAgeRule = mock(CR001CustomerAgeRule.class);

        customerArrangements = new ArrayList();

        cr022CR022ShadowLimitRule = mock(CR022ShadowLimitRule.class);

    }

    @Test
    public void testCheckEligibilityCST() throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg, DetermineEligibleInstructionsExternalServiceErrorMsg, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException {


        DatatypeFactory datatypeFactory = null;

        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        rulesDto = new RefInstructionRulesDto();
        rulesDto.setRule("CR001");
        rulesDto.setChannel(testDataHelper.TEST_RETAIL_CHANNEL_ID);
        rulesDto.setRuleType("CST");
        rulesDto.setRuleParamValue("75");
        rulesDto.setCmsReason("CR001");


        List<RefInstructionRulesDto> rulesDtoList = new ArrayList();
        rulesDtoList.add(rulesDto);

        cstRuleMap.put("CR001", cr001CustomerAgeRule);
        testRuleEvaluatorBZ.setCstRuleMap(cstRuleMap);

        XMLGregorianCalendar birthDate = datatypeFactory.newXMLGregorianCalendar("1960-05-05");
        EligibilityDecision eligibilityDecision = new EligibilityDecision("some text");
        when(cr001CustomerAgeRule.evaluate(any(RuleDataHolder.class), eq(birthDate), eq("12345"), eq(testDataHelper.TEST_OCIS_ID))).thenReturn(eligibilityDecision);
        eligibilityAnalyser.checkEligibility(upstreamRequest.getHeader(), rulesDtoList, birthDate, "12345", testDataHelper.TEST_OCIS_ID, customerInstruction, upstreamRequest.getCustomerArrangements(), new ArrangementIdentifier(), candidateInstruction, null, null);
        assertFalse(customerInstruction.isEligibilityIndicator());

    }

    @Test
    public void testCheckEligibilityAGA() throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg, DetermineEligibleInstructionsExternalServiceErrorMsg {

        rulesDto = new RefInstructionRulesDto();
        rulesDto.setRule("CR006");
        rulesDto.setCmsReason("reason");
        rulesDto.setChannel(testDataHelper.TEST_RETAIL_CHANNEL_ID);
        rulesDto.setRuleType("AGA");

        List<RefInstructionRulesDto> rulesDtoList = new ArrayList();
        rulesDtoList.add(rulesDto);

        rulesDto = new RefInstructionRulesDto();
        rulesDto.setRule("CR053");
        rulesDto.setCmsReason("reason");
        rulesDto.setChannel(testDataHelper.TEST_RETAIL_CHANNEL_ID);
        rulesDto.setRuleType("AGA");
        rulesDto.setRuleParamValue("28");
        rulesDtoList.add(rulesDto);

        String candidateInstruction = "c_ins";


        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setInstructionMnemonic("ins");
        productArrangement.setParentInstructionMnemonic("AGA");
        productArrangement.setArrangementType("CREDIT_CARD");
        productArrangement.setStartDate(testDataHelper.addToCurrentDate(29));
        customerArrangements.add(productArrangement);
        agaRuleMap.put("CR006", cr006ExistingProductRule);
        agaRuleMap.put("CR053", cr053AccountOpeningDateRule);
        testRuleEvaluatorBZ.setAgaRuleMap(agaRuleMap);

        eligibilityAnalyser.checkEligibility(upstreamRequest.getHeader(), rulesDtoList, null, "224567", testDataHelper.TEST_OCIS_ID, customerInstruction, customerArrangements, new ArrangementIdentifier(), candidateInstruction, null, null);
        assertTrue(customerInstruction.isEligibilityIndicator());
    }

    @Test
    public void verifyDownstreamSystemsAreCalledWhenEvaluatingRule059() throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg, DetermineEligibleInstructionsExternalServiceErrorMsg, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException {

        E184Resp e184Resp = testDataHelper.createE184Response(123);
        rulesDto = new RefInstructionRulesDto();
        rulesDto.setRule("CR059");
        rulesDto.setCmsReason("reason");
        rulesDto.setChannel(testDataHelper.TEST_RETAIL_CHANNEL_ID);
        rulesDto.setRuleType("AGA");
        rulesDto.setRuleParamValue("646");

        List<RefInstructionRulesDto> rulesDtoList = new ArrayList();
        rulesDtoList.add(rulesDto);

        String candidateInstruction = "c_ins";

        cr059IndicatorTypeRule.cbsIndicatorRetriever = mock(CBSIndicatorRetriever.class);
        cr059IndicatorTypeRule.appGroupRetriever = mock(AppGroupRetriever.class);

        when(cr059IndicatorTypeRule.appGroupRetriever.callRetrieveCBSAppGroup(upstreamRequest.getHeader(), testDataHelper.TEST_SORT_CODE, false)).thenReturn(testDataHelper.TEST_CBS_APP_GRP);
        when(cr059IndicatorTypeRule.cbsIndicatorRetriever.getCbsIndicator(upstreamRequest.getHeader(), testDataHelper.TEST_SORT_CODE, testDataHelper.TEST_ACCOUNT_NUMBER, testDataHelper.TEST_CBS_APP_GRP))
                .thenReturn(e184Resp.getIndicator1Gp().getStandardIndicators1Gp());

        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setInstructionMnemonic("ins");
        productArrangement.setParentInstructionMnemonic("AGA");
        productArrangement.setArrangementType("CURRENT");
        productArrangement.setCapAccountRestricted(false);
        productArrangement.setSortCode(testDataHelper.TEST_SORT_CODE);
        productArrangement.setAccountNumber(testDataHelper.TEST_ACCOUNT_NUMBER);
        customerArrangements.add(productArrangement);
        agaRuleMap.put("CR059", cr059IndicatorTypeRule);
        testRuleEvaluatorBZ.setAgaRuleMap(agaRuleMap);

        eligibilityAnalyser.checkEligibility(upstreamRequest.getHeader(), rulesDtoList, null, "224567", testDataHelper.TEST_OCIS_ID, customerInstruction, customerArrangements, new ArrangementIdentifier(), candidateInstruction, null, null);
        assertTrue(customerInstruction.isEligibilityIndicator());
        verify(cr059IndicatorTypeRule.cbsIndicatorRetriever, times(1)).getCbsIndicator(upstreamRequest.getHeader(), testDataHelper.TEST_SORT_CODE, testDataHelper.TEST_ACCOUNT_NUMBER, testDataHelper.TEST_CBS_APP_GRP);
        verify(cr059IndicatorTypeRule.appGroupRetriever, times(1)).callRetrieveCBSAppGroup(upstreamRequest.getHeader(), testDataHelper.TEST_SORT_CODE, false);
    }


    @Test
    public void testCheckEligibilityAGAFails() throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg, DetermineEligibleInstructionsExternalServiceErrorMsg {
        rulesDto = new RefInstructionRulesDto();
        rulesDto.setRule("CR006");
        rulesDto.setCmsReason("reason");
        rulesDto.setChannel(testDataHelper.TEST_RETAIL_CHANNEL_ID);
        rulesDto.setRuleType("AGA");


        List<RefInstructionRulesDto> rulesDtoList = new ArrayList();
        rulesDtoList.add(rulesDto);

        rulesDto = new RefInstructionRulesDto();
        rulesDto.setRule("CR053");
        rulesDto.setCmsReason("reason");
        rulesDto.setChannel(testDataHelper.TEST_RETAIL_CHANNEL_ID);
        rulesDto.setRuleType("AGA");
        rulesDto.setRuleParamValue("28");
        rulesDtoList.add(rulesDto);

        String candidateInstruction = "c_ins";


        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setInstructionMnemonic("c_ins");
        productArrangement.setParentInstructionMnemonic("AGA");
        productArrangement.setArrangementType("CREDIT_CARD");
        productArrangement.setStartDate(testDataHelper.subtractFromCurrentDate(29));
        customerArrangements.add(productArrangement);
        agaRuleMap.put("CR006", cr006ExistingProductRule);
        agaRuleMap.put("CR053", cr053AccountOpeningDateRule);
        testRuleEvaluatorBZ.setAgaRuleMap(agaRuleMap);
        eligibilityAnalyser.checkEligibility(upstreamRequest.getHeader(), rulesDtoList, null, "224567", testDataHelper.TEST_OCIS_ID, customerInstruction, customerArrangements, new ArrangementIdentifier(), candidateInstruction, null, null);
        assertFalse(customerInstruction.isEligibilityIndicator());
    }

    @Test
    public void testMultipleEligibilityFailureAndMultipleDeclineReasons() throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg, DetermineEligibleInstructionsExternalServiceErrorMsg {

        rulesDto = new RefInstructionRulesDto();
        rulesDto.setRule("CR006");
        rulesDto.setCmsReason("reason");
        rulesDto.setChannel(testDataHelper.TEST_RETAIL_CHANNEL_ID);
        rulesDto.setRuleType("AGA");

        List<RefInstructionRulesDto> rulesDtoList = new ArrayList();
        rulesDtoList.add(rulesDto);

        rulesDto = new RefInstructionRulesDto();
        rulesDto.setRule("CR053");
        rulesDto.setCmsReason("reason");
        rulesDto.setRuleParamValue("28");
        rulesDto.setChannel(testDataHelper.TEST_RETAIL_CHANNEL_ID);
        rulesDto.setRuleType("AGA");

        rulesDtoList.add(rulesDto);

        String candidateInstruction = "P_CISA_SAV";


        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setInstructionMnemonic("P_CISA_SAV");
        productArrangement.setArrangementType("CREDIT_CARD");
        customerArrangements.add(productArrangement);
        productArrangement = new ProductArrangement();
        productArrangement.setArrangementType("CREDIT_CARD");
        productArrangement.setStartDate(testDataHelper.subtractFromCurrentDate(25));
        customerArrangements.add(productArrangement);


        agaRuleMap.put("CR006", cr006ExistingProductRule);
        agaRuleMap.put("CR053", cr053AccountOpeningDateRule);
        testRuleEvaluatorBZ.setAgaRuleMap(agaRuleMap);

        eligibilityAnalyser.checkEligibility(upstreamRequest.getHeader(), rulesDtoList, null, "224567", testDataHelper.TEST_OCIS_ID, customerInstruction, customerArrangements, new ArrangementIdentifier(), candidateInstruction, null, null);
        assertFalse(customerInstruction.isEligibilityIndicator());
        assertTrue(customerInstruction.getDeclineReasons().size() == 2);
        assertEquals("Customer cannot apply for a product of the same type as they already have.", customerInstruction.getDeclineReasons().get(0).getReasonDescription());
        assertEquals("Customer holds a credit card opened in last " + rulesDtoList.get(1).getRuleParamValue() + " days", customerInstruction.getDeclineReasons()
                .get(1)
                .getReasonDescription());

    }

    @Test
    public void checkEligibilityShouldPassWithCorrectRequestWithRuleTypeAGT() throws Exception {

        CR007CreditCardStatusRule cr007CreditCardStatusRule = new CR007CreditCardStatusRule();
        agtRuleMap.put("CR007", cr007CreditCardStatusRule);
        rulesDto = new RefInstructionRulesDto();
        rulesDto.setRule("CR007");
        rulesDto.setCmsReason("reason");
        rulesDto.setChannel(testDataHelper.TEST_RETAIL_CHANNEL_ID);
        rulesDto.setRuleType("AGT");

        List<RefInstructionRulesDto> rulesDtoList = new ArrayList();
        rulesDtoList.add(rulesDto);
        CreditCardFinanceServiceArrangement creditCardFinanceServiceArrangement = new CreditCardFinanceServiceArrangement();
        creditCardFinanceServiceArrangement.setCardStatus(CreditCardStatus.C);
        customerArrangements.add(creditCardFinanceServiceArrangement);
        testRuleEvaluatorBZ.setAgtRuleMap(agtRuleMap);
        eligibilityAnalyser.checkEligibility(upstreamRequest.getHeader(), rulesDtoList, null, "224567", testDataHelper.TEST_OCIS_ID, customerInstruction, customerArrangements, new ArrangementIdentifier(), candidateInstruction, null, null);

        assertTrue(customerInstruction.isEligibilityIndicator());

    }

    @Test
    public void checkEligibilityShouldFailWithIncorrectRequestWithRuleTypeAGT() throws Exception {

        CR007CreditCardStatusRule cr007CreditCardStatusRule = new CR007CreditCardStatusRule();
        agtRuleMap.put("CR007", cr007CreditCardStatusRule);
        rulesDto = new RefInstructionRulesDto();
        rulesDto.setRule("CR007");
        rulesDto.setCmsReason("reason");
        rulesDto.setChannel(testDataHelper.TEST_RETAIL_CHANNEL_ID);
        rulesDto.setRuleType("AGT");

        List<RefInstructionRulesDto> rulesDtoList = new ArrayList();
        rulesDtoList.add(rulesDto);
        CreditCardFinanceServiceArrangement creditCardFinanceServiceArrangement = new CreditCardFinanceServiceArrangement();
        creditCardFinanceServiceArrangement.setCardStatus(CreditCardStatus.B);
        customerArrangements.add(creditCardFinanceServiceArrangement);
        testRuleEvaluatorBZ.setAgtRuleMap(agtRuleMap);
        eligibilityAnalyser.checkEligibility(upstreamRequest.getHeader(), rulesDtoList, null, "224567", testDataHelper.TEST_OCIS_ID, customerInstruction, customerArrangements, new ArrangementIdentifier(), candidateInstruction, null, null);

        assertFalse(customerInstruction.isEligibilityIndicator());
        assertEquals(DeclineReasons.CR007_DECLINE_REASON, customerInstruction.getDeclineReasons().get(0).getReasonDescription());

    }

    @Test
    public void checkEligibilityShouldSetEligibilityToFalseIfAnyCRIsFalse() throws Exception {
        DatatypeFactory datatypeFactory = null;

        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        rulesDto = new RefInstructionRulesDto();
        rulesDto.setRule("CR001");
        rulesDto.setChannel(testDataHelper.TEST_RETAIL_CHANNEL_ID);
        rulesDto.setRuleType("CST");
        rulesDto.setRuleParamValue("75");
        rulesDto.setCmsReason("CR001");


        List<RefInstructionRulesDto> rulesDtoList = new ArrayList();
        rulesDtoList.add(rulesDto);

        RefInstructionRulesDto ruleCr019 = new RefInstructionRulesDto();
        ruleCr019.setRule("CR019");
        ruleCr019.setChannel(testDataHelper.TEST_RETAIL_CHANNEL_ID);
        ruleCr019.setRuleType("CST");
        ruleCr019.setRuleParamValue("75");
        ruleCr019.setCmsReason("CR019");

        rulesDtoList.add(ruleCr019);
        CR019CustomerAgeRule cr019CustomerAgeRule = mock(CR019CustomerAgeRule.class);
        cstRuleMap.put("CR001", cr001CustomerAgeRule);
        cstRuleMap.put("CR019", cr019CustomerAgeRule);
        testRuleEvaluatorBZ.setCstRuleMap(cstRuleMap);

        XMLGregorianCalendar birthDate = datatypeFactory.newXMLGregorianCalendar("1960-05-05");
        when(cr001CustomerAgeRule.evaluate(any(RuleDataHolder.class), eq(birthDate), eq("12345"), eq(testDataHelper.TEST_OCIS_ID))).thenReturn(new EligibilityDecision("declined"));
        when(cr019CustomerAgeRule.evaluate(any(RuleDataHolder.class), eq(birthDate), eq("12345"), eq(testDataHelper.TEST_OCIS_ID))).thenReturn(new EligibilityDecision(true));
        eligibilityAnalyser.checkEligibility(upstreamRequest.getHeader(), rulesDtoList, birthDate, "12345", testDataHelper.TEST_OCIS_ID, customerInstruction, upstreamRequest.getCustomerArrangements(), new ArrangementIdentifier(), candidateInstruction, null, null);
        assertFalse(customerInstruction.isEligibilityIndicator());

    }

    @Test(expected = DetermineEligibleInstructionsInternalServiceErrorMsg.class)
    public void checkEligibilityShouldThrowDetermineEligibleInstructionsInternalServiceErrorMsgExceptionWhenRuleTypeIsInvalid() throws Exception {
        DatatypeFactory datatypeFactory = null;

        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        rulesDto = new RefInstructionRulesDto();
        rulesDto.setRule("CR001");
        rulesDto.setChannel(testDataHelper.TEST_RETAIL_CHANNEL_ID);
        rulesDto.setRuleType("invalid");
        rulesDto.setRuleParamValue("75");
        rulesDto.setCmsReason("CR001");


        List<RefInstructionRulesDto> rulesDtoList = new ArrayList();
        rulesDtoList.add(rulesDto);
        CR019CustomerAgeRule cr019CustomerAgeRule = mock(CR019CustomerAgeRule.class);
        cstRuleMap.put("CR001", cr001CustomerAgeRule);
        testRuleEvaluatorBZ.setCstRuleMap(cstRuleMap);

        XMLGregorianCalendar birthDate = datatypeFactory.newXMLGregorianCalendar("1960-05-05");
        when(cr001CustomerAgeRule.evaluate(any(RuleDataHolder.class), eq(birthDate), eq("12345"), eq(testDataHelper.TEST_OCIS_ID))).thenReturn(new EligibilityDecision("declined"));
        when(cr019CustomerAgeRule.evaluate(any(RuleDataHolder.class), eq(birthDate), eq("12345"), eq(testDataHelper.TEST_OCIS_ID))).thenReturn(new EligibilityDecision(true));
        eligibilityAnalyser.checkEligibility(upstreamRequest.getHeader(), rulesDtoList, birthDate, "12345", testDataHelper.TEST_OCIS_ID, customerInstruction, upstreamRequest.getCustomerArrangements(), new ArrangementIdentifier(), candidateInstruction, null, null);
        assertFalse(customerInstruction.isEligibilityIndicator());

    }

    @Test
    public void testCheckEligibilityTrueForNoRules() throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalServiceErrorMsg {
        List<RefInstructionRulesDto> rulesDtoList = new ArrayList();
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setInstructionMnemonic("P_EXcept");
        productArrangement.setParentInstructionMnemonic("AGA");
        productArrangement.setArrangementType("CREDIT_CARD");
        productArrangement.setStartDate(testDataHelper.subtractFromCurrentDate(29));
        customerArrangements.add(productArrangement);
        agaRuleMap.put("CR006", cr006ExistingProductRule);
        testRuleEvaluatorBZ.setAgaRuleMap(agaRuleMap);
        eligibilityAnalyser.checkEligibility(upstreamRequest.getHeader(), rulesDtoList, null, "224567", testDataHelper.TEST_OCIS_ID, customerInstruction, customerArrangements, new ArrangementIdentifier(), null, null, null);
        assertTrue(customerInstruction.isEligibilityIndicator());
    }

}
