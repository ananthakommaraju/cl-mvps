package com.lloydsbanking.salsa.eligibility.service.rules;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.rules.EligibilityAnalyser;
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
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Category(UnitTest.class)
public class RBBSEligibilityAnalyserTest {
    TestDataHelper dataHelper = new TestDataHelper();

    RBBSEligibilityAnalyser rbbsEligibilityAnalyser = new RBBSEligibilityAnalyser();

    CustomerInstruction customerInstruction;

    XMLGregorianCalendar birthDate;

    DetermineElegibileInstructionsRequest upstreamRequest;

    @Before
    public void setUp() {
        customerInstruction = new CustomerInstruction();
        customerInstruction.setEligibilityIndicator(true);
        DatatypeFactory datatypeFactory = null;

        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        birthDate = datatypeFactory.newXMLGregorianCalendar("1960-05-05");
        upstreamRequest = dataHelper.createEligibilityRequest("P_BOD_RBB", dataHelper.TEST_OCIS_ID, dataHelper.TEST_RETAIL_CHANNEL_ID, dataHelper.TEST_CONTACT_POINT_ID);
        rbbsEligibilityAnalyser.eligibilityAnalyser = mock(EligibilityAnalyser.class);
    }

    @Test
    public void testCheckEligibilityForRBBSProducts() throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        List<RefInstructionRulesDto> rulesDtos = new ArrayList();
        List<RefInstructionRulesDto> defaultRuleCond = new ArrayList();
        List<RefInstructionRulesDto> cbsIndRuleCond = new ArrayList();
        List<RefInstructionRulesDto> audEvntsRuleCond = new ArrayList();
        List<RefInstructionRulesDto> strctFlgRuleCond = new ArrayList();

        rulesDtos.add(setRule("CR002"));
        rulesDtos.add(setRule("CR051"));
        rulesDtos.add(setRule("CR050"));
        rulesDtos.add(setRule("CR056"));
        rulesDtos.add(setRule("CR059"));
        rulesDtos.add(setRule("CR057"));
        rulesDtos.add(setRule("CR058"));

        defaultRuleCond.add(setRule("CR002"));
        defaultRuleCond.add(setRule("CR051"));
        defaultRuleCond.add(setRule("CR050"));

        cbsIndRuleCond.add(setRule("CR056"));
        cbsIndRuleCond.add(setRule("CR059"));

        audEvntsRuleCond.add(setRule("CR057"));

        strctFlgRuleCond.add(setRule("CR058"));

        rbbsEligibilityAnalyser.checkEligibilityForRBBSProducts(upstreamRequest.getHeader(), rulesDtos, birthDate, "10621", dataHelper.TEST_OCIS_ID, customerInstruction, upstreamRequest.getCustomerArrangements(), upstreamRequest.getSelctdArr(), "P_BOD_RBB", upstreamRequest.getBusinessArrangements(), upstreamRequest.getSelctdBusnsId());

        verify(rbbsEligibilityAnalyser.eligibilityAnalyser, times(1)).checkEligibility(upstreamRequest.getHeader(), defaultRuleCond, birthDate, "10621", dataHelper.TEST_OCIS_ID, customerInstruction, upstreamRequest.getCustomerArrangements(), upstreamRequest.getSelctdArr(), "P_BOD_RBB", upstreamRequest.getBusinessArrangements(), upstreamRequest.getSelctdBusnsId());
        verify(rbbsEligibilityAnalyser.eligibilityAnalyser, times(1)).checkEligibility(upstreamRequest.getHeader(), cbsIndRuleCond, birthDate, "10621", dataHelper.TEST_OCIS_ID, customerInstruction, upstreamRequest.getCustomerArrangements(), upstreamRequest.getSelctdArr(), "P_BOD_RBB", upstreamRequest.getBusinessArrangements(), upstreamRequest.getSelctdBusnsId());
        verify(rbbsEligibilityAnalyser.eligibilityAnalyser, times(1)).checkEligibility(upstreamRequest.getHeader(), audEvntsRuleCond, birthDate, "10621", dataHelper.TEST_OCIS_ID, customerInstruction, upstreamRequest.getCustomerArrangements(), upstreamRequest.getSelctdArr(), "P_BOD_RBB", upstreamRequest.getBusinessArrangements(), upstreamRequest.getSelctdBusnsId());
        verify(rbbsEligibilityAnalyser.eligibilityAnalyser, times(1)).checkEligibility(upstreamRequest.getHeader(), strctFlgRuleCond, birthDate, "10621", dataHelper.TEST_OCIS_ID, customerInstruction, upstreamRequest.getCustomerArrangements(), upstreamRequest.getSelctdArr(), "P_BOD_RBB", upstreamRequest.getBusinessArrangements(), upstreamRequest.getSelctdBusnsId());
    }

    @Test
    public void testCheckEligibilityForRBBSProductsEvaluatesOnlyDefaultRuleList() throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        List<RefInstructionRulesDto> rulesDtos = new ArrayList();
        List<RefInstructionRulesDto> defaultRuleCond = new ArrayList();
        List<RefInstructionRulesDto> cbsIndRuleCond = new ArrayList();
        List<RefInstructionRulesDto> audEvntsRuleCond = new ArrayList();
        List<RefInstructionRulesDto> strctFlgRuleCond = new ArrayList();

        rulesDtos.add(setRule("CR002"));
        rulesDtos.add(setRule("CR051"));
        rulesDtos.add(setRule("CR050"));
        rulesDtos.add(setRule("CR056"));
        rulesDtos.add(setRule("CR059"));
        rulesDtos.add(setRule("CR057"));
        rulesDtos.add(setRule("CR058"));

        defaultRuleCond.add(setRule("CR002"));
        defaultRuleCond.add(setRule("CR051"));
        defaultRuleCond.add(setRule("CR050"));

        cbsIndRuleCond.add(setRule("CR056"));
        cbsIndRuleCond.add(setRule("CR059"));

        audEvntsRuleCond.add(setRule("CR057"));

        strctFlgRuleCond.add(setRule("CR058"));
        customerInstruction.setEligibilityIndicator(false);

        rbbsEligibilityAnalyser.checkEligibilityForRBBSProducts(upstreamRequest.getHeader(), rulesDtos, birthDate, "10621", dataHelper.TEST_OCIS_ID, customerInstruction, upstreamRequest.getCustomerArrangements(), upstreamRequest.getSelctdArr(), "P_BOD_RBB", upstreamRequest.getBusinessArrangements(), upstreamRequest.getSelctdBusnsId());

        verify(rbbsEligibilityAnalyser.eligibilityAnalyser, times(1)).checkEligibility(upstreamRequest.getHeader(), defaultRuleCond, birthDate, "10621", dataHelper.TEST_OCIS_ID, customerInstruction, upstreamRequest.getCustomerArrangements(), upstreamRequest.getSelctdArr(), "P_BOD_RBB", upstreamRequest.getBusinessArrangements(), upstreamRequest.getSelctdBusnsId());
        verify(rbbsEligibilityAnalyser.eligibilityAnalyser, never()).checkEligibility(upstreamRequest.getHeader(), cbsIndRuleCond, birthDate, "10621", dataHelper.TEST_OCIS_ID, customerInstruction, upstreamRequest.getCustomerArrangements(), upstreamRequest.getSelctdArr(), "P_BOD_RBB", upstreamRequest.getBusinessArrangements(), upstreamRequest.getSelctdBusnsId());
        verify(rbbsEligibilityAnalyser.eligibilityAnalyser, never()).checkEligibility(upstreamRequest.getHeader(), audEvntsRuleCond, birthDate, "10621", dataHelper.TEST_OCIS_ID, customerInstruction, upstreamRequest.getCustomerArrangements(), upstreamRequest.getSelctdArr(), "P_BOD_RBB", upstreamRequest.getBusinessArrangements(), upstreamRequest.getSelctdBusnsId());
        verify(rbbsEligibilityAnalyser.eligibilityAnalyser, never()).checkEligibility(upstreamRequest.getHeader(), strctFlgRuleCond, birthDate, "10621", dataHelper.TEST_OCIS_ID, customerInstruction, upstreamRequest.getCustomerArrangements(), upstreamRequest.getSelctdArr(), "P_BOD_RBB", upstreamRequest.getBusinessArrangements(), upstreamRequest.getSelctdBusnsId());
    }

    private RefInstructionRulesDto setRule(String rule) {
        RefInstructionRulesDto instructionRulesDto = new RefInstructionRulesDto();
        instructionRulesDto.setRule(rule);
        return instructionRulesDto;
    }

}
