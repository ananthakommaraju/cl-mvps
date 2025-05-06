package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.CheckBalanceRetriever;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import com.lloydsbanking.salsa.soap.cbs.e141.objects.E141Resp;
import lb_gbo_sales.DepositArrangement;
import lb_gbo_sales.businessobjects.LimitCondition;
import lb_gbo_sales.messages.RequestHeader;
import lib_sim_bo.businessobjects.InstructionDetails;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class QuestionHasDepositedFundsThisYearTest {
    private DepositArrangement depositArrangement;

    private List<ProductArrangementFacade> productArrangementFacadeList;

    private List<ProductArrangementFacade> productArrangementFacadeListWZ;

    private ProductArrangement productArrangement;

    private TestDataHelper dataHelper;

    private RequestHeader header;

    private QuestionHasDepositedFundsThisYear question;

    @Before
    public void setUp() throws Exception {
        depositArrangement = mock(DepositArrangement.class);
        ProductArrangementFacade productArrangementFacade = new ProductArrangementFacade(depositArrangement);
        productArrangementFacadeList = new ArrayList();
        productArrangementFacadeList.add(productArrangementFacade);
        dataHelper = new TestDataHelper();
        productArrangement = dataHelper.createExistingProductArrangments("3001116000", "00004", null, "50001762", 2014);
        productArrangement.getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        productArrangement.getAssociatedProduct().getInstructionDetails().setParentInstructionMnemonic("G_ISA");
        ProductArrangementFacade productArrangementFacadeWZ = new ProductArrangementFacade(productArrangement);
        productArrangementFacadeListWZ = new ArrayList();
        productArrangementFacadeListWZ.add(productArrangementFacadeWZ);
        header = new com.lloydsbanking.salsa.eligibility.TestDataHelper().createEligibilityRequestHeader("IBL", TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        question = QuestionHasDepositedFundsThisYear.pose();
        question.appGroupRetriever = mock(AppGroupRetriever.class);
        question.checkBalanceRetriever = mock(CheckBalanceRetriever.class);
    }

    @Test
    public void hasDepositedFundsThisYearShouldReturnTrueIfMnemonicIsISAAndConditionsLimitsAreNotTheSame() throws Exception {
        LimitCondition maximumTransactionAmountLimitCondition = mock(LimitCondition.class);
        LimitCondition headRoomAmountLimitCondition = mock(LimitCondition.class);
        when(maximumTransactionAmountLimitCondition.getValue()).thenReturn(new BigDecimal(12));
        when(headRoomAmountLimitCondition.getValue()).thenReturn(new BigDecimal(10));

        when(depositArrangement.getParentInstructionMnemonic()).thenReturn("G_ISA");
        when(depositArrangement.getMaximumTransactionAmount()).thenReturn(maximumTransactionAmountLimitCondition);
        when(depositArrangement.getHeadRoomAmount()).thenReturn(headRoomAmountLimitCondition);

        boolean ask = QuestionHasDepositedFundsThisYear.pose().givenAProductList(productArrangementFacadeList).ask();
        assertTrue(ask);
        verify(depositArrangement, times(2)).getParentInstructionMnemonic();
        verify(depositArrangement, times(3)).getMaximumTransactionAmount();
        verify(depositArrangement, times(2)).getHeadRoomAmount();
        verify(maximumTransactionAmountLimitCondition, times(2)).getValue();
        verify(headRoomAmountLimitCondition, times(1)).getValue();

    }

    @Test
    public void hasDepositedFundsThisYearShouldReturnFalseIfMnemonicIsISAAndConditionsLimitsAreTheSame() throws Exception {
        LimitCondition maximumTransactionAmountLimitCondition = mock(LimitCondition.class);
        LimitCondition headRoomAmountLimitCondition = mock(LimitCondition.class);
        when(maximumTransactionAmountLimitCondition.getValue()).thenReturn(new BigDecimal(10));
        when(headRoomAmountLimitCondition.getValue()).thenReturn(new BigDecimal(10));

        when(depositArrangement.getParentInstructionMnemonic()).thenReturn("G_ISA");
        when(depositArrangement.getMaximumTransactionAmount()).thenReturn(maximumTransactionAmountLimitCondition);
        when(depositArrangement.getHeadRoomAmount()).thenReturn(headRoomAmountLimitCondition);

        boolean ask = QuestionHasDepositedFundsThisYear.pose().givenAProductList(productArrangementFacadeList).ask();
        assertFalse(ask);
        verify(depositArrangement, times(2)).getParentInstructionMnemonic();
        verify(depositArrangement, times(3)).getMaximumTransactionAmount();
        verify(depositArrangement, times(2)).getHeadRoomAmount();
        verify(maximumTransactionAmountLimitCondition, times(2)).getValue();
        verify(headRoomAmountLimitCondition, times(1)).getValue();

    }

    @Test
    public void hasDepositedFundsThisYearShouldReturnTrueForWZ() throws EligibilityException, SalsaInternalResourceNotAvailableException, SalsaExternalServiceException {
        when(question.appGroupRetriever.callRetrieveCBSAppGroup(header, "111618", true)).thenReturn("01");
        when(question.checkBalanceRetriever.getCheckBalance(header, "111618", "50001762", "01")).thenReturn(dataHelper.createE141Response(new ArrayList<Integer>(), "1200"));
        boolean ask = question
            .givenEitherCurrentOrSavingsAccount(true)
            .givenIsWzRequest(true)
            .givenCheckBalanceRetrieverClientInstance(question.checkBalanceRetriever)
            .givenAppGroupRetrieverClientInstance(question.appGroupRetriever)
            .givenAProductList(productArrangementFacadeListWZ)
            .givenRequestHeader(header).ask();

        assertTrue(ask);
    }

    @Test
    public void hasDepositedFundsThisYearShouldReturnFalseForWZWhenAccountIsNotCAorSA() throws EligibilityException, SalsaInternalResourceNotAvailableException, SalsaExternalServiceException {
        when(question.appGroupRetriever.callRetrieveCBSAppGroup(header, "111618", true)).thenReturn("01");
        when(question.checkBalanceRetriever.getCheckBalance(header, "111618", "50001762", "01")).thenReturn(dataHelper.createE141Response(new ArrayList<Integer>(), "1200"));
        boolean ask = question
            .givenEitherCurrentOrSavingsAccount(false)
            .givenIsWzRequest(true)
            .givenCheckBalanceRetrieverClientInstance(question.checkBalanceRetriever)
            .givenAppGroupRetrieverClientInstance(question.appGroupRetriever)
            .givenAProductList(productArrangementFacadeListWZ)
            .givenRequestHeader(header).ask();

        assertFalse(ask);
    }

    @Test
    public void hasDepositedFundsThisYearShouldReturnFalseForWZWhenAccountIsNotCbs() throws EligibilityException, SalsaInternalResourceNotAvailableException, SalsaExternalServiceException {
        when(question.appGroupRetriever.callRetrieveCBSAppGroup(header, "111618", true)).thenReturn("01");
        when(question.checkBalanceRetriever.getCheckBalance(header, "111618", "50001762", "01")).thenReturn(dataHelper.createE141Response(new ArrayList<Integer>(), "1200"));
        productArrangement.getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00001");
        productArrangement.getAssociatedProduct().getInstructionDetails().setParentInstructionMnemonic(null);
        ProductArrangementFacade productArrangementFacadeWZ = new ProductArrangementFacade(productArrangement);
        productArrangementFacadeListWZ.set(0, productArrangementFacadeWZ);
        boolean ask = question
            .givenEitherCurrentOrSavingsAccount(true)
            .givenIsWzRequest(true)
            .givenCheckBalanceRetrieverClientInstance(question.checkBalanceRetriever)
            .givenAppGroupRetrieverClientInstance(question.appGroupRetriever)
            .givenAProductList(productArrangementFacadeListWZ)
            .givenRequestHeader(header).ask();

        assertFalse(ask);
    }

    @Test
    public void hasDepositedFundsThisYearShouldReturnFalseForWZWhenParentIsNotG_ISA() throws EligibilityException, SalsaInternalResourceNotAvailableException, SalsaExternalServiceException {
        when(question.appGroupRetriever.callRetrieveCBSAppGroup(header, "111618", true)).thenReturn("01");
        when(question.checkBalanceRetriever.getCheckBalance(header, "111618", "50001762", "01")).thenReturn(dataHelper.createE141Response(new ArrayList<Integer>(), "1200"));
        productArrangement.getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        productArrangement.getAssociatedProduct().getInstructionDetails().setParentInstructionMnemonic("P_LOAN_STP");
        productArrangement.getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00001");
        ProductArrangementFacade productArrangementFacadeWZ = new ProductArrangementFacade(productArrangement);
        productArrangementFacadeListWZ.set(0, productArrangementFacadeWZ);
        boolean ask = question
            .givenEitherCurrentOrSavingsAccount(true)
            .givenIsWzRequest(true)
            .givenCheckBalanceRetrieverClientInstance(question.checkBalanceRetriever)
            .givenAppGroupRetrieverClientInstance(question.appGroupRetriever)
            .givenAProductList(productArrangementFacadeListWZ)
            .givenRequestHeader(header).ask();

        assertFalse(ask);
    }

    @Test
    public void hasDepositedFundsThisYearShouldReturnFalseForWZWhenISABalanceReturnedIsNull() throws EligibilityException, SalsaInternalResourceNotAvailableException, SalsaExternalServiceException {
        when(question.appGroupRetriever.callRetrieveCBSAppGroup(header, "111618", true)).thenReturn("01");
        when(question.checkBalanceRetriever.getCheckBalance(header, "111618", "50001762", "01")).thenReturn(new E141Resp());
        boolean ask = question
            .givenEitherCurrentOrSavingsAccount(true)
            .givenIsWzRequest(true)
            .givenCheckBalanceRetrieverClientInstance(question.checkBalanceRetriever)
            .givenAppGroupRetrieverClientInstance(question.appGroupRetriever)
            .givenAProductList(productArrangementFacadeListWZ)
            .givenRequestHeader(header).ask();

        assertFalse(ask);
    }

    @Test
    public void hasDepositedFundsThisYearShouldReturnFalseForWZWhenMaxLimitAmtIs0() throws EligibilityException, SalsaInternalResourceNotAvailableException, SalsaExternalServiceException {
        when(question.appGroupRetriever.callRetrieveCBSAppGroup(header, "111618", true)).thenReturn("01");
        E141Resp e141Resp=dataHelper.createE141Response(new ArrayList<Integer>(), "1200");
        e141Resp.getISADetailsGp().getISADetailsSubGp().setTaxYearTotalDepositAm("0");
        when(question.checkBalanceRetriever.getCheckBalance(header, "111618", "50001762", "01")).thenReturn(e141Resp);
        boolean ask = question
            .givenEitherCurrentOrSavingsAccount(true)
            .givenIsWzRequest(true)
            .givenCheckBalanceRetrieverClientInstance(question.checkBalanceRetriever)
            .givenAppGroupRetrieverClientInstance(question.appGroupRetriever)
            .givenAProductList(productArrangementFacadeListWZ)
            .givenRequestHeader(header).ask();

        assertFalse(ask);
    }

    @Test
    public void hasDepositedFundsThisYearShouldReturnFalseForWZWhenParentIsNotG_ISAWithDepositArrangement() throws EligibilityException, SalsaInternalResourceNotAvailableException, SalsaExternalServiceException {
        when(question.appGroupRetriever.callRetrieveCBSAppGroup(header, "111618", true)).thenReturn("01");
        when(question.checkBalanceRetriever.getCheckBalance(header, "111618", "50001762", "01")).thenReturn(dataHelper.createE141Response(new ArrayList<Integer>(), "1200"));
        when(depositArrangement.getParentInstructionMnemonic()).thenReturn("P_LOAN_STP");
        boolean ask = question
            .givenEitherCurrentOrSavingsAccount(true)
            .givenIsWzRequest(true)
            .givenCheckBalanceRetrieverClientInstance(question.checkBalanceRetriever)
            .givenAppGroupRetrieverClientInstance(question.appGroupRetriever)
            .givenAProductList(productArrangementFacadeList)
            .givenRequestHeader(header).ask();

        assertFalse(ask);
    }

    @Test
    public void testAskForExternalSystemProductIdentifierEmpty() throws EligibilityException, SalsaInternalResourceNotAvailableException, SalsaExternalServiceException {
        when(question.appGroupRetriever.callRetrieveCBSAppGroup(header, "111618", true)).thenReturn("01");
        when(question.checkBalanceRetriever.getCheckBalance(header, "111618", "50001762", "01")).thenReturn(dataHelper.createE141Response(new ArrayList<Integer>(), "1200"));
        productArrangement.getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        productArrangement.getAssociatedProduct().getInstructionDetails().setParentInstructionMnemonic("P_LOAN_STP");
        productArrangement.getAssociatedProduct().getExternalSystemProductIdentifier().clear();
        ProductArrangementFacade productArrangementFacadeWZ = new ProductArrangementFacade(productArrangement);
        productArrangementFacadeListWZ.set(0, productArrangementFacadeWZ);
        boolean ask = question
            .givenEitherCurrentOrSavingsAccount(true)
            .givenIsWzRequest(true)
            .givenCheckBalanceRetrieverClientInstance(question.checkBalanceRetriever)
            .givenAppGroupRetrieverClientInstance(question.appGroupRetriever)
            .givenAProductList(productArrangementFacadeListWZ)
            .givenRequestHeader(header).ask();

        assertFalse(ask);
    }
}