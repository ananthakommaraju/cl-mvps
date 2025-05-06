package com.lloydsbanking.salsa.eligibility.service.rules;

import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ExceptionUtility;
import com.lloydsbanking.salsa.header.RequestToResponseHeaderConverter;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.businessobjects.ArrangementIdentifier;
import lb_gbo_sales.businessobjects.BusinessArrangement;
import lb_gbo_sales.businessobjects.CustomerInstruction;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import lb_gbo_sales.messages.RequestHeader;
import org.junit.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class RuleEvaluatorBZTest {
    DetermineElegibileInstructionsRequest upstreamRequest;

    @Test(expected = EligibilityException.class)
    public void shouldThrowEligibilityExceptionWhenRuleTypeDoesNotMatch() throws Exception {
        String candidateInstruction = "P_MNEMONIC";
        TestDataHelper testDataHelper = new TestDataHelper();
        upstreamRequest = testDataHelper.createEligibilityRequest(candidateInstruction, testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);
        RuleEvaluatorBZ ruleEvaluatorBZ = new RuleEvaluatorBZ();
        ruleEvaluatorBZ.exceptionUtility = new ExceptionUtility(new RequestToResponseHeaderConverter());

        RefInstructionRulesDto ruleDto = mock(RefInstructionRulesDto.class);
        when(ruleDto.getRuleType()).thenReturn("CRHDD");
        when(ruleDto.getRule()).thenReturn("CRHDDddd");
        when(ruleDto.getRuleParamValue()).thenReturn("CRHDddddDddd");

        XMLGregorianCalendar xmlGregorianCalendar = mock(XMLGregorianCalendar.class);
        CustomerInstruction customerInstruction = mock(CustomerInstruction.class);
        ArrangementIdentifier arrangementIdentifier = mock(ArrangementIdentifier.class);
        List<ProductArrangement> productArrangements = mock(ArrayList.class);
        List<BusinessArrangement> businessArrangment = mock(ArrayList.class);

        ruleEvaluatorBZ.evaluateRule(upstreamRequest.getHeader(), xmlGregorianCalendar, "1111", "2222", customerInstruction, productArrangements, arrangementIdentifier, "fsfdfd", businessArrangment, "sdsds", ruleDto);

    }
}