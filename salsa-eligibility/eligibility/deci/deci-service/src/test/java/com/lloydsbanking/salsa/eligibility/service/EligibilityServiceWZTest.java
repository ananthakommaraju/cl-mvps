package com.lloydsbanking.salsa.eligibility.service;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ReferenceDataLookUpDao;
import com.lloydsbanking.salsa.downstream.pam.service.RetrievePamService;
import com.lloydsbanking.salsa.downstream.prd.model.RefInstructionRulesPrdDto;
import com.lloydsbanking.salsa.eligibility.logging.wz.EligibilityLogService;
import com.lloydsbanking.salsa.eligibility.service.downstream.EligibilityPAMRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.EligibilityPRDRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.wz.EligibilityAnalyser;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.wz.ExceptionUtility;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.logging.application.CustomerTraceLog;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import com.lloydsbanking.salsa.logging.application.ProductEligibilityTraceLog;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsExternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsInternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class EligibilityServiceWZTest {
    EligibilityServiceWZ service;

    TestDataHelper dataHelper = new TestDataHelper();

    DetermineEligibleCustomerInstructionsRequest request;

    @Before
    public void setUp() {
        service = new EligibilityServiceWZ();
        service.eligibilityLogServiceWZ = mock(EligibilityLogService.class);
        service.headerRetrieverWZ = new HeaderRetriever();
        service.eligibilityPAMRetriever = mock(EligibilityPAMRetriever.class);
        service.eligibilityPRDRetriever = mock(EligibilityPRDRetriever.class);
        service.requestToResponseHeaderConverter = new RequestToResponseHeaderConverter();
        service.eligibilityAnalyserWZ = mock(EligibilityAnalyser.class);
        service.eligibilityDataHelper = mock(EligibilityDataHelper.class);
        service.exceptionUtilityWz = mock(ExceptionUtility.class);
        service.productEligibilityTraceLog = mock(ProductEligibilityTraceLog.class);
        service.customerTraceLog = mock(CustomerTraceLog.class);
        service.productArrangementTraceLog = mock(ProductArrangementTraceLog.class);
        request = dataHelper.createEligibilityRequest("G_ISA", TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID, TestDataHelper.TEST_CONTACT_POINT_ID);

    }

    @Test
    public void testInitialisesAndClearsLogging() throws Exception {

        RequestHeader requestHeader = (new TestDataHelper()).createEligibilityRequestHeader(dataHelper.TEST_RETAIL_CHANNEL_ID, dataHelper.TEST_INTERACTION_ID, dataHelper.TEST_OCIS_ID, dataHelper.TEST_CUSTOMER_ID, dataHelper.TEST_CONTACT_POINT_ID);
        DetermineEligibleCustomerInstructionsRequest request = new DetermineEligibleCustomerInstructionsRequest();
        request.setHeader(requestHeader);
        request.getCandidateInstructions().add(0, "P_CISA_SAV");
        List<RefInstructionRulesPrdDto> refInstructionRulesPrdDtos = new ArrayList<>();
        RefInstructionRulesPrdDto refInstructionRulesPrdDto = new RefInstructionRulesPrdDto();
        refInstructionRulesPrdDto.setRule("CR001");
        refInstructionRulesPrdDto.setRuleType("CST");
        refInstructionRulesPrdDto.setRuleParamValue("18");
        refInstructionRulesPrdDto.setInsMnemonic("P_CISA_SAV");
        refInstructionRulesPrdDto.setBracode("LTB");
        refInstructionRulesPrdDtos.add(refInstructionRulesPrdDto);
        when(service.eligibilityPAMRetriever.getChannelIdFromContactPointId(dataHelper.TEST_CONTACT_POINT_ID, request.getHeader())).thenReturn(TestDataHelper.TEST_RETAIL_CHANNEL_ID);
        when(service.eligibilityPRDRetriever.getCompositeInstructionConditions(request.getCandidateInstructions().get(0), TestDataHelper.TEST_RETAIL_CHANNEL_ID, request.getHeader(), request.getCandidateInstructions().get(0))).thenReturn(refInstructionRulesPrdDtos);
        DetermineEligibleCustomerInstructionsResponse response = service.determineEligibleCustomerInstructions(request);
        assertNotNull(response);
        verify(service.eligibilityLogServiceWZ).initialiseContext(requestHeader);
        verify(service.eligibilityLogServiceWZ).clearContext();
    }

    @Test(expected = DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg.class)
    public void testServiceCallWithDataNotAvailableError() throws Exception {
        ReferenceDataLookUpDao referenceDataLookUpDao;
        ExceptionUtility exceptionUtility;

        referenceDataLookUpDao = mock(ReferenceDataLookUpDao.class);
        RetrievePamService retrievePamService = mock(RetrievePamService.class);
        exceptionUtility = new ExceptionUtility(mock(RequestToResponseHeaderConverter.class));
        service.eligibilityPAMRetriever = new EligibilityPAMRetriever(referenceDataLookUpDao, exceptionUtility, retrievePamService);

        RequestHeader requestHeader = (new TestDataHelper()).createEligibilityRequestHeader(dataHelper.TEST_RETAIL_CHANNEL_ID, "4587", dataHelper.TEST_OCIS_ID, dataHelper.TEST_CUSTOMER_ID, dataHelper.TEST_CONTACT_POINT_ID);
        DetermineEligibleCustomerInstructionsRequest request = new DetermineEligibleCustomerInstructionsRequest();
        request.setHeader(requestHeader);
        service.determineEligibleCustomerInstructions(request);

    }

    @Test(expected = DetermineEligibleCustomerInstructionsExternalServiceErrorMsg.class)
    public void testParentInstructionRetrieved() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        List<String> childInstructions = new ArrayList<>();
        childInstructions.add("P_CISA_SAV");

        List<RefInstructionRulesPrdDto> refInstructionRulesPrdDtos = new ArrayList<>();

        when(service.eligibilityPAMRetriever.getChannelIdFromContactPointId(dataHelper.TEST_CONTACT_POINT_ID, request.getHeader())).thenReturn(TestDataHelper.TEST_RETAIL_CHANNEL_ID);
        when(service.eligibilityPRDRetriever.retrieveInstructionsHierarchyForGrndPrnt("G_ISA", dataHelper.TEST_RETAIL_CHANNEL_ID)).thenReturn(childInstructions);
        when(service.eligibilityPRDRetriever.getCompositeInstructionConditions(request.getCandidateInstructions().get(0), TestDataHelper.TEST_RETAIL_CHANNEL_ID, request.getHeader(), request.getCandidateInstructions().get(0))).thenReturn(refInstructionRulesPrdDtos);
        when(service.exceptionUtilityWz.externalServiceError("816002", "No data found in PRD", request.getHeader())).thenThrow(DetermineEligibleCustomerInstructionsExternalServiceErrorMsg.class);
        service.determineEligibleCustomerInstructions(request);

    }

    @Test
    public void testChildInstructionNotFound() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        request.getCandidateInstructions().clear();
        request.getCandidateInstructions().add(0, "P_CISA_SAV");
        List<String> childInstructions = new ArrayList<>();

        List<RefInstructionRulesPrdDto> refInstructionRulesPrdDtos = new ArrayList<>();
        RefInstructionRulesPrdDto refInstructionRulesPrdDto = new RefInstructionRulesPrdDto();
        refInstructionRulesPrdDto.setRule("CR001");
        refInstructionRulesPrdDto.setRuleType("CST");
        refInstructionRulesPrdDto.setRuleParamValue("18");
        refInstructionRulesPrdDto.setInsMnemonic("P_CISA_SAV");
        refInstructionRulesPrdDto.setBracode("LTB");
        refInstructionRulesPrdDtos.add(refInstructionRulesPrdDto);

        when(service.eligibilityPAMRetriever.getChannelIdFromContactPointId(TestDataHelper.TEST_CONTACT_POINT_ID, request.getHeader())).thenReturn(TestDataHelper.TEST_RETAIL_CHANNEL_ID);
        when(service.eligibilityPRDRetriever.retrieveChildInstructionsHierarchy(request.getCandidateInstructions().get(0), TestDataHelper.TEST_RETAIL_CHANNEL_ID)).thenReturn(childInstructions);
        when(service.eligibilityPRDRetriever.getCompositeInstructionConditions(request.getCandidateInstructions().get(0), TestDataHelper.TEST_RETAIL_CHANNEL_ID, request.getHeader(), request.getCandidateInstructions().get(0))).thenReturn(refInstructionRulesPrdDtos);

        DetermineEligibleCustomerInstructionsResponse response = service.determineEligibleCustomerInstructions(request);

        verify(service.eligibilityLogServiceWZ).initialiseContext(request.getHeader());
        verify(service.eligibilityLogServiceWZ).clearContext();
        assertEquals("P_CISA_SAV", response.getProductEligibilityDetails().get(0).getProduct().get(0).getInstructionDetails().getInstructionMnemonic());
    }
}
