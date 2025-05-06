package com.lloydsbanking.salsa.offer.apply.downstream;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.asm.client.f205.F205ClientImpl;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.offer.apply.convert.RetrieveCreditScoreRequestFactory;
import com.lloydsbanking.salsa.offer.apply.errorcode.RetrieveCreditScoreErrorMap;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Req;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Resp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_gmo.exception.ExternalBusinessErrorMsg;
import lib_sim_gmo.exception.ExternalServiceErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.datatype.DatatypeConfigurationException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CreditScoreRetrieverTest {

    private CreditScoreRetriever creditScoreRetriever;
    private F205Resp f205Resp;
    private TestDataHelper testDataHelper;
    private DepositArrangement depositArrangement;
    private RequestHeader requestHeader;

    @Before
    public void setUp() throws ParseException, DatatypeConfigurationException {
        creditScoreRetriever = new CreditScoreRetriever();
        creditScoreRetriever.f205Client = mock(F205ClientImpl.class);
        creditScoreRetriever.f205RequestFactory = mock(RetrieveCreditScoreRequestFactory.class);
        creditScoreRetriever.headerRetriever = new HeaderRetriever();
        creditScoreRetriever.exceptionUtility = new ExceptionUtility();
        creditScoreRetriever.errorMap = new RetrieveCreditScoreErrorMap();
        testDataHelper = new TestDataHelper();
        depositArrangement = testDataHelper.generateOfferProductArrangementPCARequest2();
        requestHeader = testDataHelper.createOpaccRequestHeader("LTB");
        f205Resp = null;
    }

    @Test
    public void testRetrieveCreditScoreIsSuccessful() throws OfferException {
        f205Resp = testDataHelper.createF205Response(0);
        String contactPointId = requestHeader.getContactPointId();
        when(creditScoreRetriever.f205Client.fetchCreditDecisionForCurrentAccount(any(F205Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f205Resp);
        f205Resp = creditScoreRetriever.retrieveCreditDecision(depositArrangement, requestHeader);
        assertEquals("0", f205Resp.getF205Result().getResultCondition().getReasonCode().toString());
    }

    @Test(expected = OfferException.class)
    public void testRetrieveCreditScoreThrowsInternalServiceErrorInResponse() throws OfferException {
        f205Resp = testDataHelper.createF205Response(155012);
        when(creditScoreRetriever.f205Client.fetchCreditDecisionForCurrentAccount(any(F205Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f205Resp);
        creditScoreRetriever.retrieveCreditDecision(depositArrangement, requestHeader);
    }

    @Test
    public void testRetrieveCreditScoreThrowsExternalServiceErrorInResponse() throws OfferException {
        f205Resp = testDataHelper.createF205Response(153116);
        when(creditScoreRetriever.f205Client.fetchCreditDecisionForCurrentAccount(any(F205Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f205Resp);
        try {
            creditScoreRetriever.retrieveCreditDecision(depositArrangement, requestHeader);
        } catch (OfferException errorMsg) {
            assertTrue("OfferException not instance of ExternalServiceErrorMsg", errorMsg.getErrorMsg() instanceof ExternalServiceErrorMsg);
        }
    }

    @Test
    public void testRetrieveCreditScoreThrowsExternalBusinessErrorInResponse() throws ExternalBusinessErrorMsg, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalServiceErrorMsg {
        f205Resp = testDataHelper.createF205Response(159179);
        when(creditScoreRetriever.f205Client.fetchCreditDecisionForCurrentAccount(any(F205Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f205Resp);
        try {
            creditScoreRetriever.retrieveCreditDecision(depositArrangement, requestHeader);
        } catch (OfferException errorMsg) {
            assertTrue("OfferException not instance of ExternalBusinessErrorMsg", errorMsg.getErrorMsg() instanceof ExternalBusinessErrorMsg);
        }
    }

    @Test
    public void testRetrieveCreditScoreThrowsResourceNotAvailable() throws ExternalBusinessErrorMsg, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalServiceErrorMsg, ParseException {
        when(creditScoreRetriever.f205RequestFactory.create(any(String.class), any(String.class), any(String.class), any(ArrayList.class), any(Customer.class), any(String.class), any(String.class), any(String.class), any(ArrayList.class),any(ArrayList.class))).thenReturn(new F205Req());
        when(creditScoreRetriever.f205Client.fetchCreditDecisionForCurrentAccount(any(F205Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenThrow(ResourceNotAvailableErrorMsg.class);
        try {
            creditScoreRetriever.retrieveCreditDecision(depositArrangement, requestHeader);
        } catch (OfferException errorMsg) {
            assertTrue("OfferException not instance of ResourceNotAvailableErrorMsg", errorMsg.getErrorMsg() instanceof ResourceNotAvailableErrorMsg);
        }
    }
}
