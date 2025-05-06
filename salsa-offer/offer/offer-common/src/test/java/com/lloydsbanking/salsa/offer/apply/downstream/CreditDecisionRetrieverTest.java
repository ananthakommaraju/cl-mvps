package com.lloydsbanking.salsa.offer.apply.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.asm.client.f424.F424ClientImpl;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.offer.apply.convert.RetrieveCreditDecisionRequestFactory;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Req;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Resp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.CurrencyAmount;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.ProductEligibilityDetails;
import lib_sim_gmo.exception.ExternalBusinessErrorMsg;
import lib_sim_gmo.exception.ExternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CreditDecisionRetrieverTest {

    private CreditDecisionRetriever creditDecisionRetriever;
    private F424Resp f424Resp;
    private TestDataHelper dataHelper;

    @Before
    public void setUp() {
        creditDecisionRetriever = new CreditDecisionRetriever();
        creditDecisionRetriever.f424Client = mock(F424ClientImpl.class);
        creditDecisionRetriever.f424RequestFactory = mock(RetrieveCreditDecisionRequestFactory.class);
        creditDecisionRetriever.headerRetriever = new HeaderRetriever();
        creditDecisionRetriever.exceptionUtility = mock(ExceptionUtility.class);
        dataHelper = new TestDataHelper();
    }

    @Test
    public void testGetFraudDecisionIsSuccessful() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg {
        f424Resp = dataHelper.createF424Response2(0);
        when(creditDecisionRetriever.f424Client.f424(any(F424Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f424Resp);
        FinanceServiceArrangement financeServiceArrangement = dataHelper.createFinanceServiceArrangementCC();
        financeServiceArrangement.getAssociatedProduct().setEligibilityDetails(new ProductEligibilityDetails());
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine8("n");
        F424Resp f424Resp = creditDecisionRetriever.retrieveCreditDecision(
                "0000777505",
                financeServiceArrangement.getArrangementId(),
                financeServiceArrangement.getAssociatedProduct().getGuaranteedOfferCode(),
                financeServiceArrangement.getAssociatedProduct().getExternalSystemProductIdentifier().get(0).getProductIdentifier(),
                financeServiceArrangement.getAssociatedProduct().getEligibilityDetails().getIsEligible(),
                financeServiceArrangement.getInitiatedThrough().getSubChannelCode(),
                financeServiceArrangement.getAffiliatedetails().get(0).getAffiliateIdentifier(),
                financeServiceArrangement.getPrimaryInvolvedParty(),
                financeServiceArrangement.getArrangementType(),
                financeServiceArrangement.isMarketingPrefereceIndicator(),
                financeServiceArrangement.getBalanceTransfer(),
                financeServiceArrangement.getTotalBalanceTransferAmount(),
                dataHelper.createOpaccRequestHeader("LTB"));
        assertEquals("0", f424Resp.getF424Result().getResultCondition().getReasonCode().toString());
        assertEquals("reasonText", f424Resp.getF424Result().getResultCondition().getReasonText());
    }


    @Test(expected = ExternalServiceErrorMsg.class)
    public void testGetFraudDecisionThrowsExternalServiceError() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg {
        f424Resp = dataHelper.createF424Response2(159259);
        when(creditDecisionRetriever.f424RequestFactory.create(any(String.class), any(String.class), any(String.class), any(String.class), any(String.class), any(String.class), any(String.class), any(Customer.class), any(String.class), any(Boolean.class), any(String.class), any(CurrencyAmount.class))).thenReturn(dataHelper.createF424Req());
        when(creditDecisionRetriever.f424Client.f424(any(F424Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f424Resp);
        when(creditDecisionRetriever.exceptionUtility.externalServiceError(any(String.class), any(String.class))).thenThrow(ExternalServiceErrorMsg.class);
        FinanceServiceArrangement financeServiceArrangement = dataHelper.createFinanceServiceArrangementCC();
        financeServiceArrangement.getAssociatedProduct().setEligibilityDetails(new ProductEligibilityDetails());
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine8("n");
        F424Resp f424Resp = creditDecisionRetriever.retrieveCreditDecision(
                "0000777505",
                financeServiceArrangement.getArrangementId(),
                financeServiceArrangement.getAssociatedProduct().getGuaranteedOfferCode(),
                financeServiceArrangement.getAssociatedProduct().getExternalSystemProductIdentifier().get(0).getProductIdentifier(),
                financeServiceArrangement.getAssociatedProduct().getEligibilityDetails().getIsEligible(),
                financeServiceArrangement.getInitiatedThrough().getSubChannelCode(),
                financeServiceArrangement.getAffiliatedetails().get(0).getAffiliateIdentifier(),
                financeServiceArrangement.getPrimaryInvolvedParty(),
                financeServiceArrangement.getArrangementType(),
                financeServiceArrangement.isMarketingPrefereceIndicator(),
                financeServiceArrangement.getBalanceTransfer(),
                financeServiceArrangement.getTotalBalanceTransferAmount(),
                dataHelper.createOpaccRequestHeader("LTB"));
    }

    @Test(expected = ExternalBusinessErrorMsg.class)
    public void testGetFraudDecisionThrowsExternalBusinessError() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg {
        f424Resp = new TestDataHelper().createF424Response2(159179);
        when(creditDecisionRetriever.f424RequestFactory.create(any(String.class), any(String.class), any(String.class), any(String.class), any(String.class), any(String.class), any(String.class), any(Customer.class), any(String.class), any(Boolean.class), any(String.class), any(CurrencyAmount.class))).thenReturn(dataHelper.createF424Req());
        when(creditDecisionRetriever.f424Client.f424(any(F424Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f424Resp);
        when(creditDecisionRetriever.exceptionUtility.externalBusinessError(any(String.class), any(String.class))).thenThrow(ExternalBusinessErrorMsg.class);
        FinanceServiceArrangement financeServiceArrangement = dataHelper.createFinanceServiceArrangementCC();
        financeServiceArrangement.getAssociatedProduct().setEligibilityDetails(new ProductEligibilityDetails());
        financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getUnstructuredAddress().setAddressLine8("n");
        F424Resp f424Resp = creditDecisionRetriever.retrieveCreditDecision(
                "0000777505",
                financeServiceArrangement.getArrangementId(),
                financeServiceArrangement.getAssociatedProduct().getGuaranteedOfferCode(),
                financeServiceArrangement.getAssociatedProduct().getExternalSystemProductIdentifier().get(0).getProductIdentifier(),
                financeServiceArrangement.getAssociatedProduct().getEligibilityDetails().getIsEligible(),
                financeServiceArrangement.getInitiatedThrough().getSubChannelCode(),
                financeServiceArrangement.getAffiliatedetails().get(0).getAffiliateIdentifier(),
                financeServiceArrangement.getPrimaryInvolvedParty(),
                financeServiceArrangement.getArrangementType(),
                financeServiceArrangement.isMarketingPrefereceIndicator(),
                financeServiceArrangement.getBalanceTransfer(),
                financeServiceArrangement.getTotalBalanceTransferAmount(),
                dataHelper.createOpaccRequestHeader("LTB"));
    }


}
