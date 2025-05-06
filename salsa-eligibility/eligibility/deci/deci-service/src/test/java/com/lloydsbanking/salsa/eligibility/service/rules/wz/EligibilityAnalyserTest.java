package com.lloydsbanking.salsa.eligibility.service.rules.wz;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.prd.model.RefInstructionRulesPrdDto;
import com.lloydsbanking.salsa.eligibility.service.rules.aga.AGAEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.aga.CR016ISACurrentYearRule;
import com.lloydsbanking.salsa.eligibility.service.rules.avn.AVNEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.avn.CR017MaxProductHoldingRule;
import com.lloydsbanking.salsa.eligibility.service.rules.common.DeclineReasonAdder;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.KYCStatus;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.rules.cst.CR003CustomerAgeRule;
import com.lloydsbanking.salsa.eligibility.service.rules.cst.CSTEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.cst.wz.CR066UKResidencyCheckRule;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.ReasonText;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.wz.ExceptionUtility;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsExternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsInternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
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
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class EligibilityAnalyserTest {
    private TestDataHelper testDataHelper;

    EligibilityAnalyser eligibilityAnalyser;

    String candidateInstruction;
    ResultCondition resultCondition;
    Product associatedProduct;

    Customer customerDetails;

    List<ProductArrangement> productArrangements;

    List<ProductEligibilityDetails> eligibilityDetails;

    DetermineEligibleCustomerInstructionsRequest upstreamRequest;

    Map<String, CSTEligibilityRule> cstRuleMap;

    Map<String, AGAEligibilityRule> agaRuleMap;

    Map<String, AVNEligibilityRule> avnRuleMap;

    List<InstructionDetails> instructionDetails;

    CR003CustomerAgeRule cr003CustomerAgeRule;

    CR016ISACurrentYearRule cr016ISACurrentYearRule;

    RuleEvaluatorWZ testRuleEvaluatorWZ;

    CR017MaxProductHoldingRule cr017MaxProductHoldingRule;

    @Before
    public void setUp() {
        candidateInstruction = "P_INS_MNEMONIC";
        testDataHelper = new TestDataHelper();
        instructionDetails = new ArrayList();
        testRuleEvaluatorWZ = new RuleEvaluatorWZ();
        eligibilityAnalyser = new EligibilityAnalyser();
        eligibilityAnalyser.ruleEvaluatorWZ = testRuleEvaluatorWZ;
        upstreamRequest = testDataHelper.createEligibilityRequest(candidateInstruction, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        cstRuleMap = new HashMap<>();
        agaRuleMap = new HashMap<>();
        avnRuleMap = new HashMap<>();
        eligibilityDetails = new ArrayList<>();
        eligibilityDetails.add(new ProductEligibilityDetails());
        cr003CustomerAgeRule = mock(CR003CustomerAgeRule.class);
        cr016ISACurrentYearRule = mock(CR016ISACurrentYearRule.class);
        cr017MaxProductHoldingRule = mock(CR017MaxProductHoldingRule.class);
        eligibilityAnalyser.exceptionUtilityWZ = new ExceptionUtility(new RequestToResponseHeaderConverter());
        eligibilityAnalyser.declineReasonAdder = new DeclineReasonAdder();
        eligibilityAnalyser.exceptionUtilityWZ = mock(ExceptionUtility.class);
        resultCondition=new ResultCondition();
    }

    @Test
    public void testCheckEligibilityCST() throws DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, DatatypeConfigurationException {
        RefInstructionRulesPrdDto rulesPrdDto = new RefInstructionRulesPrdDto();
        rulesPrdDto.setBracode("LTB");
        rulesPrdDto.setGroupRule("GR007");
        rulesPrdDto.setRule("CR003");
        rulesPrdDto.setRuleType("CST");
        rulesPrdDto.setRuleDesc("ruleDesc");
        List<RefInstructionRulesPrdDto> ruleDtoList = new ArrayList();
        ruleDtoList.add(rulesPrdDto);
        cstRuleMap.put("CR003", cr003CustomerAgeRule);
        testRuleEvaluatorWZ.setCstRuleMap(cstRuleMap);
        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        XMLGregorianCalendar birthDate = datatypeFactory.newXMLGregorianCalendar("1960-05-05");
        when(cr003CustomerAgeRule.evaluate((any(RuleDataHolder.class)), eq(birthDate), eq("12345"), eq(TestDataHelper.TEST_OCIS_ID))).thenReturn(new EligibilityDecision(true));
        List<String> candidateInstructionList = new ArrayList<>();
        candidateInstructionList.add(candidateInstruction);
        eligibilityAnalyser.checkEligibility(upstreamRequest.getHeader(), ruleDtoList, birthDate, eligibilityDetails, candidateInstruction, productArrangements, "12345", TestDataHelper.TEST_OCIS_ID, customerDetails, associatedProduct, null, null, resultCondition, candidateInstructionList);
        assertEquals("true", eligibilityDetails.get(0).getIsEligible());
        assertEquals("LTB", ruleDtoList.get(0).getBracode());
        assertEquals("ruleDesc", ruleDtoList.get(0).getRuleDesc());
    }

    @Test
    public void testCheckEligibilityAga() throws DatatypeConfigurationException, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        RefInstructionRulesPrdDto rulesPrdDto = new RefInstructionRulesPrdDto();
        rulesPrdDto.setBracode("LTB");
        rulesPrdDto.setGroupRule("GR007");
        rulesPrdDto.setRule("CR016");
        rulesPrdDto.setRuleType("AGA");
        rulesPrdDto.setRuleDesc("ruleDesc");

        List<RefInstructionRulesPrdDto> ruleDtoList = new ArrayList();
        ruleDtoList.add(rulesPrdDto);
        agaRuleMap.put("CR016", cr016ISACurrentYearRule);

        productArrangements = new ArrayList();
        ProductArrangement productArrangement = new ProductArrangement();
        Product associateProduct = new Product();
        associateProduct.setProductIdentifier("3001116000");
        productArrangement.setAssociatedProduct(associateProduct);
        productArrangement.setLifecycleStatus("Effective");
        productArrangement.setAccountNumber("121212121");
        productArrangement.setArrangementStartDate(testDataHelper.createXMLGregorianCalendar(2014, 10, 20));
        productArrangements.add(productArrangement);
        testRuleEvaluatorWZ.setAgaRuleMap(agaRuleMap);
        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        XMLGregorianCalendar birthDate = datatypeFactory.newXMLGregorianCalendar("1960-05-05");

        when(cr016ISACurrentYearRule.evaluate((any(String.class)), (any(RuleDataHolder.class)))).thenReturn(new EligibilityDecision("some text"));
        List<String> candidateInstructionList = new ArrayList<>();
        candidateInstructionList.add(candidateInstruction);
        eligibilityAnalyser.checkEligibility(upstreamRequest.getHeader(), ruleDtoList, birthDate, eligibilityDetails, candidateInstruction, productArrangements, null, null, customerDetails, associateProduct, null, null, resultCondition, candidateInstructionList);

        assertEquals("false", eligibilityDetails.get(0).getIsEligible());
        assertEquals("LTB", ruleDtoList.get(0).getBracode());
        assertEquals("ruleDesc", ruleDtoList.get(0).getRuleDesc());

    }

    @Test(expected = DetermineEligibleCustomerInstructionsInternalServiceErrorMsg.class)
    public void testCheckEligibilityWithNoCST() throws DatatypeConfigurationException, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {

        RefInstructionRulesPrdDto rulesPrdDto = new RefInstructionRulesPrdDto();
        rulesPrdDto.setBracode("LTB");
        rulesPrdDto.setGroupRule("GR007");
        rulesPrdDto.setRule("CR003");
        rulesPrdDto.setRuleType("agt");
        rulesPrdDto.setRuleDesc("ruleDesc");

        List<RefInstructionRulesPrdDto> ruleDtoList = new ArrayList();
        ruleDtoList.add(rulesPrdDto);
        cstRuleMap.put("CR003", cr003CustomerAgeRule);

        testRuleEvaluatorWZ.setCstRuleMap(cstRuleMap);

        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        XMLGregorianCalendar birthDate = datatypeFactory.newXMLGregorianCalendar("1960-05-05");
        ;
        eligibilityAnalyser.exceptionUtilityWZ = mock(ExceptionUtility.class);

        when(eligibilityAnalyser.exceptionUtilityWZ.internalServiceError(any(String.class), any(ReasonText.class), any(RequestHeader.class))).thenThrow(DetermineEligibleCustomerInstructionsInternalServiceErrorMsg.class);
        List<String> candidateInstructionList = new ArrayList<>();
        candidateInstructionList.add(candidateInstruction);
        eligibilityAnalyser.checkEligibility(upstreamRequest.getHeader(), ruleDtoList, birthDate, eligibilityDetails, candidateInstruction, productArrangements, null, null, customerDetails, associatedProduct, null, null, resultCondition, candidateInstructionList);

    }

    @Test
    public void testCheckEligibilityForEmptyList() throws DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, DatatypeConfigurationException {

        List<RefInstructionRulesPrdDto> ruleDtoList = new ArrayList();
        cstRuleMap.put("CR003", cr003CustomerAgeRule);

        testRuleEvaluatorWZ.setCstRuleMap(cstRuleMap);

        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        XMLGregorianCalendar birthDate = datatypeFactory.newXMLGregorianCalendar("1960-05-05");
        List<String> candidateInstructionList = new ArrayList<>();
        candidateInstructionList.add(candidateInstruction);
        eligibilityAnalyser.checkEligibility(upstreamRequest.getHeader(), ruleDtoList, birthDate, eligibilityDetails, candidateInstruction, productArrangements, null, null, customerDetails, associatedProduct, null, null, resultCondition, candidateInstructionList);

        assertEquals("true", eligibilityDetails.get(0).getIsEligible());

    }

    @Test
    public void testCheckEligibilityAvn() throws DatatypeConfigurationException, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        RefInstructionRulesPrdDto rulesPrdDto = new RefInstructionRulesPrdDto();
        rulesPrdDto.setBracode("LTB");
        rulesPrdDto.setGroupRule("GR007");
        rulesPrdDto.setRule("CR017");
        rulesPrdDto.setRuleType("AVN");
        rulesPrdDto.setRuleDesc("ruleDesc");

        List<RefInstructionRulesPrdDto> ruleDtoList = new ArrayList();
        ruleDtoList.add(rulesPrdDto);
        avnRuleMap.put("CR017", cr017MaxProductHoldingRule);

        productArrangements = new ArrayList();
        ProductArrangement productArrangement = new ProductArrangement();
        Product associateProduct = new Product();
        associateProduct.setProductIdentifier("3001116000");
        productArrangement.setAssociatedProduct(associateProduct);
        productArrangement.setLifecycleStatus("Effective");
        productArrangement.setAccountNumber("121212121");
        productArrangement.setArrangementStartDate(testDataHelper.createXMLGregorianCalendar(2014, 10, 20));
        productArrangements.add(productArrangement);
        testRuleEvaluatorWZ.setAvnRuleMap(avnRuleMap);
        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        XMLGregorianCalendar birthDate = datatypeFactory.newXMLGregorianCalendar("1960-05-05");
        when(cr017MaxProductHoldingRule.evaluate((any(RuleDataHolder.class)), (any(String.class)))).thenReturn(new EligibilityDecision("reasonText"));
        List<String> candidateInstructionList = new ArrayList<>();
        candidateInstructionList.add(candidateInstruction);
        eligibilityAnalyser.checkEligibility(upstreamRequest.getHeader(), ruleDtoList, birthDate, eligibilityDetails, candidateInstruction, productArrangements, null, null, customerDetails, null, null, null, resultCondition, candidateInstructionList);

        assertEquals("false", eligibilityDetails.get(0).getIsEligible());
    }

    @Test
    public void productEligibilityListShouldContainTwoItemsWhenKYCStatusIsNotNullAndCR066IsInvokedAndIsEligibilityIsTrue() throws Exception {

        RefInstructionRulesPrdDto rulesPrdDto = new RefInstructionRulesPrdDto();
        rulesPrdDto.setBracode("LTB");
        rulesPrdDto.setGroupRule("CR066");
        rulesPrdDto.setRule("CR066");
        rulesPrdDto.setRuleType("CST");
        rulesPrdDto.setRuleDesc("ruleDesc");
        CR066UKResidencyCheckRule cr066UKResidencyCheckRule = mock(CR066UKResidencyCheckRule.class);

        List<RefInstructionRulesPrdDto> ruleDtoList = new ArrayList();
        ruleDtoList.add(rulesPrdDto);
        cstRuleMap.put("CR066", cr066UKResidencyCheckRule);

        productArrangements = new ArrayList();
        ProductArrangement productArrangement = new ProductArrangement();
        Product associateProduct = new Product();
        associateProduct.setProductIdentifier("3001116000");
        productArrangement.setAssociatedProduct(associateProduct);
        productArrangement.setLifecycleStatus("Effective");
        productArrangement.setAccountNumber("121212121");
        productArrangement.setArrangementStartDate(testDataHelper.createXMLGregorianCalendar(2014, 10, 20));
        productArrangements.add(productArrangement);
        testRuleEvaluatorWZ.setCstRuleMap(cstRuleMap);
        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        XMLGregorianCalendar birthDate = datatypeFactory.newXMLGregorianCalendar("1960-05-05");
        when(cr066UKResidencyCheckRule.evaluate((any(RuleDataHolder.class)), any(XMLGregorianCalendar.class), (any(String.class)), (any(String.class)))).thenReturn(new EligibilityDecision(true, new KYCStatus("F")));
        List<String> candidateInstructionList = new ArrayList<>();
        candidateInstructionList.add(candidateInstruction);
        eligibilityAnalyser.checkEligibility(upstreamRequest.getHeader(), ruleDtoList, birthDate, eligibilityDetails, candidateInstruction, productArrangements, null, null, customerDetails, null, null, null, resultCondition, candidateInstructionList);

        assertEquals("F", eligibilityDetails.get(1).getKycStatus());
        assertEquals("true", eligibilityDetails.get(0).getIsEligible());
        assertNull(eligibilityDetails.get(0).getKycStatus());
        assertEquals(2, eligibilityDetails.size());

    }

    @Test
    public void productEligibilityListShouldContainTwoItemsWhenKYCStatusIsNotNullAndCR066IsInvokedAndIsEligibilityIsFalse() throws Exception {

        RefInstructionRulesPrdDto rulesPrdDto = new RefInstructionRulesPrdDto();
        rulesPrdDto.setBracode("LTB");
        rulesPrdDto.setGroupRule("CR066");
        rulesPrdDto.setRule("CR066");
        rulesPrdDto.setRuleType("CST");
        rulesPrdDto.setRuleDesc("ruleDesc");
        CR066UKResidencyCheckRule cr066UKResidencyCheckRule = mock(CR066UKResidencyCheckRule.class);

        List<RefInstructionRulesPrdDto> ruleDtoList = new ArrayList();
        ruleDtoList.add(rulesPrdDto);
        cstRuleMap.put("CR066", cr066UKResidencyCheckRule);

        productArrangements = new ArrayList();
        ProductArrangement productArrangement = new ProductArrangement();
        Product associateProduct = new Product();
        associateProduct.setProductIdentifier("3001116000");
        productArrangement.setAssociatedProduct(associateProduct);
        productArrangement.setLifecycleStatus("Effective");
        productArrangement.setAccountNumber("121212121");
        productArrangement.setArrangementStartDate(testDataHelper.createXMLGregorianCalendar(2014, 10, 20));
        productArrangements.add(productArrangement);
        testRuleEvaluatorWZ.setCstRuleMap(cstRuleMap);
        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        XMLGregorianCalendar birthDate = datatypeFactory.newXMLGregorianCalendar("1960-05-05");
        when(cr066UKResidencyCheckRule.evaluate((any(RuleDataHolder.class)), any(XMLGregorianCalendar.class), (any(String.class)), (any(String.class)))).thenReturn(new EligibilityDecision("Some declined reason", new KYCStatus("F")));
        List<String> candidateInstructionList = new ArrayList<>();
        candidateInstructionList.add(candidateInstruction);
        eligibilityAnalyser.checkEligibility(upstreamRequest.getHeader(), ruleDtoList, birthDate, eligibilityDetails, candidateInstruction, productArrangements, null, null, customerDetails, null, null, null, resultCondition, candidateInstructionList);

        assertEquals("F", eligibilityDetails.get(1).getKycStatus());
        assertEquals("false", eligibilityDetails.get(0).getIsEligible());
        assertNull(eligibilityDetails.get(0).getKycStatus());
        assertEquals(2, eligibilityDetails.size());

    }

    @Test
    public void productEligibilityListShouldContainSingleItemWhenKYCStatusIsNullAndCR066IsNotInvokedAndIsEligibilityIsFalse() throws Exception {

        RefInstructionRulesPrdDto rulesPrdDto = new RefInstructionRulesPrdDto();
        rulesPrdDto.setBracode("LTB");
        rulesPrdDto.setGroupRule("GR007");
        rulesPrdDto.setRule("CR017");
        rulesPrdDto.setRuleType("AVN");
        rulesPrdDto.setRuleDesc("ruleDesc");

        List<RefInstructionRulesPrdDto> ruleDtoList = new ArrayList();
        ruleDtoList.add(rulesPrdDto);
        avnRuleMap.put("CR017", cr017MaxProductHoldingRule);

        productArrangements = new ArrayList();
        ProductArrangement productArrangement = new ProductArrangement();
        Product associateProduct = new Product();
        associateProduct.setProductIdentifier("3001116000");
        productArrangement.setAssociatedProduct(associateProduct);
        productArrangement.setLifecycleStatus("Effective");
        productArrangement.setAccountNumber("121212121");
        productArrangement.setArrangementStartDate(testDataHelper.createXMLGregorianCalendar(2014, 10, 20));
        productArrangements.add(productArrangement);
        testRuleEvaluatorWZ.setAvnRuleMap(avnRuleMap);
        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        XMLGregorianCalendar birthDate = datatypeFactory.newXMLGregorianCalendar("1960-05-05");
        when(cr017MaxProductHoldingRule.evaluate((any(RuleDataHolder.class)), (any(String.class)))).thenReturn(new EligibilityDecision("reasonText"));
        List<String> candidateInstructionList = new ArrayList<>();
        candidateInstructionList.add(candidateInstruction);
        eligibilityAnalyser.checkEligibility(upstreamRequest.getHeader(), ruleDtoList, birthDate, eligibilityDetails, candidateInstruction, productArrangements, null, null, customerDetails, null, null, null, resultCondition, candidateInstructionList);

        assertEquals("false", eligibilityDetails.get(0).getIsEligible());
        assertEquals(1, eligibilityDetails.size());

    }

    @Test
    public void productEligibilityListShouldContainSingleItemWhenKYCStatusIsNullAndCR066IsNotInvokedAndIsEligibilityIsTrue() throws Exception {

        RefInstructionRulesPrdDto rulesPrdDto = new RefInstructionRulesPrdDto();
        rulesPrdDto.setBracode("LTB");
        rulesPrdDto.setGroupRule("GR007");
        rulesPrdDto.setRule("CR017");
        rulesPrdDto.setRuleType("AVN");
        rulesPrdDto.setRuleDesc("ruleDesc");

        List<RefInstructionRulesPrdDto> ruleDtoList = new ArrayList();
        ruleDtoList.add(rulesPrdDto);
        avnRuleMap.put("CR017", cr017MaxProductHoldingRule);

        productArrangements = new ArrayList();
        ProductArrangement productArrangement = new ProductArrangement();
        Product associateProduct = new Product();
        associateProduct.setProductIdentifier("3001116000");
        productArrangement.setAssociatedProduct(associateProduct);
        productArrangement.setLifecycleStatus("Effective");
        productArrangement.setAccountNumber("121212121");
        productArrangement.setArrangementStartDate(testDataHelper.createXMLGregorianCalendar(2014, 10, 20));
        productArrangements.add(productArrangement);
        testRuleEvaluatorWZ.setAvnRuleMap(avnRuleMap);
        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        XMLGregorianCalendar birthDate = datatypeFactory.newXMLGregorianCalendar("1960-05-05");
        when(cr017MaxProductHoldingRule.evaluate((any(RuleDataHolder.class)), (any(String.class)))).thenReturn(new EligibilityDecision(true));
        List<String> candidateInstructionList = new ArrayList<>();
        candidateInstructionList.add(candidateInstruction);
        eligibilityAnalyser.checkEligibility(upstreamRequest.getHeader(), ruleDtoList, birthDate, eligibilityDetails, candidateInstruction, productArrangements, null, null, customerDetails, null, null, null, resultCondition, candidateInstructionList);

        assertEquals("true", eligibilityDetails.get(0).getIsEligible());
        assertEquals(1, eligibilityDetails.size());

    }

}



