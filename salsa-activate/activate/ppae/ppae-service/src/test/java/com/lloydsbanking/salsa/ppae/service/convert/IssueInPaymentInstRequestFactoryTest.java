package com.lloydsbanking.salsa.ppae.service.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import com.lloydsbanking.salsa.soap.fs.ftp.IssueInpaymentInstructionRequest;
import com.lloydstsb.schema.enterprise.lcsm_arrangement.ProductAccessArrangement;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;

import static junit.framework.Assert.assertEquals;

@Category(UnitTest.class)
public class IssueInPaymentInstRequestFactoryTest {

    IssueInPaymentInstRequestFactory issueInPaymentInstRequestFactory;
    TestDataHelper testDataHelper;
    FinanceServiceArrangement financeServiceArrangement;


    @Before
    public void setUp() throws DatatypeConfigurationException {
        issueInPaymentInstRequestFactory = new IssueInPaymentInstRequestFactory();
        testDataHelper = new TestDataHelper();
        financeServiceArrangement = testDataHelper.createFinanceServiceArrangementForIssueInPayment();
    }

    @Test
    public void testGetLoanDetailsRequestForPpaeWithFinanceArr() {
        IssueInpaymentInstructionRequest request = issueInPaymentInstRequestFactory.convert("abc", financeServiceArrangement.getBalanceTransfer().get(0), financeServiceArrangement.getCreditCardNumber());
        assertEquals("CARD_NUMBER", ((ProductAccessArrangement) request.getBeneficiaryRequestDetails().getTargetArrangement().getArrangementAssociations().get(0).getRelatedArrangement()).getHasCards().get(0).getObjectReference().getAlternateId().get(0).getAttributeString());
        assertEquals("H", ((ProductAccessArrangement) request.getBeneficiaryRequestDetails().getTargetArrangement().getArrangementAssociations().get(0).getRelatedArrangement()).getHasCards().get(0).getObjectReference().getAlternateId().get(0).getSourceLogicalId());
        assertEquals("7859645", request.getSourceArrangement().getObjectReference().getAlternateId().get(0).getValue());
    }

    @Test
    public void testGetLoanDetailsRequestForNullBalTransfer() {
        IssueInpaymentInstructionRequest request = issueInPaymentInstRequestFactory.convert("abc", financeServiceArrangement.getBalanceTransfer().get(0), financeServiceArrangement.getCreditCardNumber());
        request.setBeneficiaryRequestDetails(null);
        assertEquals(null, request.getBeneficiaryRequestDetails());
        assertEquals("7859645", request.getSourceArrangement().getObjectReference().getAlternateId().get(0).getValue());
    }

    @Test
    public void testGetLoanDetailsRequestForNullCurrencyAmount() {
        IssueInpaymentInstructionRequest request = issueInPaymentInstRequestFactory.convert("abc", financeServiceArrangement.getBalanceTransfer().get(0), financeServiceArrangement.getCreditCardNumber());
        assertEquals(null, request.getBeneficiaryRequestDetails().getAmount().getTheCurrencyAmount());
        assertEquals("7859645", request.getSourceArrangement().getObjectReference().getAlternateId().get(0).getValue());
    }
}
