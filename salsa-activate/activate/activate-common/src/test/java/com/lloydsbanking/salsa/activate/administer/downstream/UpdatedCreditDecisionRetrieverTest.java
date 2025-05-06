package com.lloydsbanking.salsa.activate.administer.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.administer.convert.F425ResponseToApplicationDetailsConverter;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.downstream.asm.client.f425.F425Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.asm.f425.objects.F425Req;
import com.lloydsbanking.salsa.soap.asm.f425.objects.F425Resp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.ws.WebServiceException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class UpdatedCreditDecisionRetrieverTest {
    UpdatedCreditDecisionRetriever updatedCreditDecisionRetriever;
    ProductArrangement productArrangement;
    TestDataHelper testDataHelper;
    RequestHeader header;
    F425Resp f425res;
    F425ResponseToApplicationDetailsConverter f425ResponseToApplicationDetailsConverter;

    @Before
    public void setUp() {
        updatedCreditDecisionRetriever = new UpdatedCreditDecisionRetriever();
        testDataHelper = new TestDataHelper();
        productArrangement = testDataHelper.createApaRequestByDBEvent().getProductArrangement();
        header = testDataHelper.createApaRequestHeader();
        updatedCreditDecisionRetriever.headerRetriever = new HeaderRetriever();
        updatedCreditDecisionRetriever.f425Client = mock(F425Client.class);
        updatedCreditDecisionRetriever.applicationDetailsConverter = mock(F425ResponseToApplicationDetailsConverter.class);
        f425res = testDataHelper.createF425Resp();
    }

    @Test
    public void testRetrieveUpdatedCreditDecisionWithArranngementTypeCC() {
        productArrangement.setArrangementType("cc");
        when(updatedCreditDecisionRetriever.f425Client.f425(any(F425Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f425res);
        when(updatedCreditDecisionRetriever.applicationDetailsConverter.convert(f425res)).thenReturn(testDataHelper.createApplicationDetails());
        ApplicationDetails applicationDetails = updatedCreditDecisionRetriever.retrieveUpdatedCreditDecision(productArrangement, header, "154");
        assertEquals(testDataHelper.createApplicationDetails().getReferralCodes().get(0).getCode(), applicationDetails.getReferralCodes().get(0).getCode());
        assertEquals(testDataHelper.createApplicationDetails().getProductOptions().get(0).getOptionsCode(), applicationDetails.getProductOptions().get(0).getOptionsCode());
        assertNull(applicationDetails.getRetryCount());
        assertEquals(null, applicationDetails.getApplicationStatus());
    }

    @Test
    public void testRetrieveUpdatedCreditDecisionWithNullF425Result() {
        f425res.setF425Result(null);
        productArrangement.setArrangementType("cc");
        when(updatedCreditDecisionRetriever.f425Client.f425(any(F425Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f425res);
        when(updatedCreditDecisionRetriever.applicationDetailsConverter.convert(f425res)).thenReturn(testDataHelper.createApplicationDetails());
        ApplicationDetails applicationDetails = updatedCreditDecisionRetriever.retrieveUpdatedCreditDecision(productArrangement, header, "154");
        assertEquals(testDataHelper.createApplicationDetails().getReferralCodes().get(0).getCode(), applicationDetails.getReferralCodes().get(0).getCode());
        assertEquals(testDataHelper.createApplicationDetails().getProductOptions().get(0).getOptionsCode(), applicationDetails.getProductOptions().get(0).getOptionsCode());
        assertNull(applicationDetails.getRetryCount());
        assertEquals(null, applicationDetails.getApplicationStatus());
    }

    @Test
    public void testRetrieveUpdatedCreditDecisionWithNullResultCondition() {
        productArrangement.setArrangementType("cc");
        f425res.getF425Result().setResultCondition(null);
        when(updatedCreditDecisionRetriever.f425Client.f425(any(F425Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f425res);
        when(updatedCreditDecisionRetriever.applicationDetailsConverter.convert(f425res)).thenReturn(testDataHelper.createApplicationDetails());
        ApplicationDetails applicationDetails = updatedCreditDecisionRetriever.retrieveUpdatedCreditDecision(productArrangement, header, "154");
        assertEquals(testDataHelper.createApplicationDetails().getReferralCodes().get(0).getCode(), applicationDetails.getReferralCodes().get(0).getCode());
        assertEquals(testDataHelper.createApplicationDetails().getProductOptions().get(0).getOptionsCode(), applicationDetails.getProductOptions().get(0).getOptionsCode());
        assertNull(applicationDetails.getRetryCount());
        assertEquals(null, applicationDetails.getApplicationStatus());
    }

    @Test
    public void testRetrieveUpdatedCreditDecisionWithSeverityCodeNonZero() {
        productArrangement.setArrangementType("cc");
        f425res.getF425Result().getResultCondition().setSeverityCode((byte) 4);
        when(updatedCreditDecisionRetriever.f425Client.f425(any(F425Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f425res);
        when(updatedCreditDecisionRetriever.applicationDetailsConverter.convert(f425res)).thenReturn(testDataHelper.createApplicationDetails());
        ApplicationDetails applicationDetails = updatedCreditDecisionRetriever.retrieveUpdatedCreditDecision(productArrangement, header, "154");
        assertEquals(1, applicationDetails.getRetryCount().intValue());
        assertEquals("001", applicationDetails.getConditionList().get(0).getReasonCode());
        assertEquals("1008", applicationDetails.getApplicationStatus());
    }

    @Test
    public void testRetrieveUpdatedCreditDecisionWithException() {
        productArrangement.setArrangementType("cc");
        when(updatedCreditDecisionRetriever.f425Client.f425(any(F425Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenThrow(WebServiceException.class);
        when(updatedCreditDecisionRetriever.applicationDetailsConverter.convert(f425res)).thenReturn(testDataHelper.createApplicationDetails());
        ApplicationDetails applicationDetails = updatedCreditDecisionRetriever.retrieveUpdatedCreditDecision(productArrangement, header, "154");
        assertEquals(1, applicationDetails.getRetryCount().intValue());
        assertEquals("001", applicationDetails.getConditionList().get(0).getReasonCode());
        assertEquals("1008", applicationDetails.getApplicationStatus());
    }
}
