package com.lloydsbanking.salsa.offer.verify.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.eidv.client.x711.X711Client;
import com.lloydsbanking.salsa.downstream.eidv.client.x711.X711ClientImpl;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.logging.application.CustomerTraceLog;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.offer.verify.convert.RetrieveEIDVScoreRequestFactory;
import com.lloydsbanking.salsa.offer.verify.evaluate.EidvStatusEvaluator;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lloydstsb.schema.personal.customer.partyidandv.IdentifyParty;
import lloydstsb.schema.personal.customer.partyidandv.IdentifyPartyResp;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class EidvRetrieverTest {

    private EidvRetriever eidvRetriever;
    private TestDataHelper dataHelper;

    @Before
    public void setUp() throws Exception {
        eidvRetriever = new EidvRetriever();
        dataHelper = new TestDataHelper();

        eidvRetriever.eidvStatusEvaluator = mock(EidvStatusEvaluator.class);
        eidvRetriever.exceptionUtility = new ExceptionUtility();
        eidvRetriever.retrieveEIDVScoreRequestFactory = mock(RetrieveEIDVScoreRequestFactory.class);
        eidvRetriever.headerRetriever = new HeaderRetriever();
        eidvRetriever.customerTraceLog = mock(CustomerTraceLog.class);
        eidvRetriever.x711ClientMap = new HashMap<String, X711Client>();
        eidvRetriever.x711ClientMap.put("LTB", mock(X711ClientImpl.class));
        when(eidvRetriever.customerTraceLog.getCustomerTraceEventMessage(any(Customer.class), any(String.class))).thenReturn("Customer");
    }

    @Test
    public void testGetEidvScore() throws Exception {
        ProductArrangement productArrangement = dataHelper.createDepositArrangement();
        when(eidvRetriever.retrieveEIDVScoreRequestFactory.create(productArrangement.getPrimaryInvolvedParty(), "0000777505")).thenReturn(new RetrieveEIDVScoreRequestFactory().create(productArrangement.getPrimaryInvolvedParty(), "0000777505"));
        IdentifyPartyResp identifyPartyResp = dataHelper.getIdentifyPartyResp();
        when(eidvRetriever.x711ClientMap.get("LTB").retrieveIdentityAndScores(any(IdentifyParty.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(identifyPartyResp);
        CustomerScore customerScore = eidvRetriever.getEidvScore(productArrangement.getPrimaryInvolvedParty(), dataHelper.createOpaPcaRequestHeader("LTB"));
        assertEquals("I", customerScore.getDecisionCode());
        assertEquals("900", customerScore.getAssessmentEvidence().get(0).getIdentityStrength());
        assertEquals("900", customerScore.getAssessmentEvidence().get(0).getAddressStrength());
    }

    @Test
    public void testGetEidvScoreReferralReasonNull() throws Exception {
        ProductArrangement productArrangement = dataHelper.createDepositArrangement();
        when(eidvRetriever.retrieveEIDVScoreRequestFactory.create(productArrangement.getPrimaryInvolvedParty(), "0000777505")).thenReturn(new RetrieveEIDVScoreRequestFactory().create(productArrangement.getPrimaryInvolvedParty(), "0000777505"));
        IdentifyPartyResp identifyPartyResp = dataHelper.getIdentifyPartyResp();
        identifyPartyResp.getIdentifyPartyReturn().getIdentifyPartyOutput().setReferralReasons(null);
        when(eidvRetriever.x711ClientMap.get("LTB").retrieveIdentityAndScores(any(IdentifyParty.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(identifyPartyResp);
        CustomerScore customerScore = eidvRetriever.getEidvScore(productArrangement.getPrimaryInvolvedParty(), dataHelper.createOpaPcaRequestHeader("LTB"));
        assertEquals("I", customerScore.getDecisionCode());
        assertEquals("900", customerScore.getAssessmentEvidence().get(0).getIdentityStrength());
        assertEquals("900", customerScore.getAssessmentEvidence().get(0).getAddressStrength());
    }

    @Test(expected = InternalServiceErrorMsg.class)
    public void testGetEidvScoreError() throws Exception {
        ProductArrangement productArrangement = dataHelper.createDepositArrangement();
        when(eidvRetriever.retrieveEIDVScoreRequestFactory.create(productArrangement.getPrimaryInvolvedParty(), "0000777505")).thenReturn(new RetrieveEIDVScoreRequestFactory().create(productArrangement.getPrimaryInvolvedParty(), "0000777505"));
        IdentifyPartyResp identifyPartyResp = dataHelper.getIdentifyPartyResp();
        identifyPartyResp.getIdentifyPartyReturn().setResultCondition(new ResultCondition());
        identifyPartyResp.getIdentifyPartyReturn().getResultCondition().setSeverityCode((byte) 2);
        when(eidvRetriever.x711ClientMap.get("LTB").retrieveIdentityAndScores(any(IdentifyParty.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(identifyPartyResp);
        eidvRetriever.getEidvScore(productArrangement.getPrimaryInvolvedParty(), dataHelper.createOpaPcaRequestHeader("LTB"));
    }

    @Test
    public void testGetEidvScoreResponseNull() throws Exception {
        ProductArrangement productArrangement = dataHelper.createDepositArrangement();
        when(eidvRetriever.retrieveEIDVScoreRequestFactory.create(productArrangement.getPrimaryInvolvedParty(), "0000777505")).thenReturn(new RetrieveEIDVScoreRequestFactory().create(productArrangement.getPrimaryInvolvedParty(), "0000777505"));
        IdentifyPartyResp identifyPartyResp = new IdentifyPartyResp();
        when(eidvRetriever.x711ClientMap.get("LTB").retrieveIdentityAndScores(any(IdentifyParty.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(identifyPartyResp);
        CustomerScore customerScore = eidvRetriever.getEidvScore(productArrangement.getPrimaryInvolvedParty(), dataHelper.createOpaPcaRequestHeader("LTB"));
        assertNull( customerScore);
    }
}