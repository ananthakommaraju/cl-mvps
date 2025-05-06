package com.lloydsbanking.salsa.activate.communication.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import lib_sim_bo.businessobjects.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class ProductRelatedTokenFactoryTest {
    private ProductRelatedTokenFactory productRelatedTokenFactory;
    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        productRelatedTokenFactory = new ProductRelatedTokenFactory();
        productRelatedTokenFactory.postCodeFactory = mock(PostCodeFactory.class);
        productRelatedTokenFactory.informationContentFactory = mock(InformationContentFactory.class);
        productRelatedTokenFactory.lookUpValueRetriever=mock(LookUpValueRetriever.class);
        testDataHelper = new TestDataHelper();
    }

    @Test
    public void testGetProductRelatedTokenWhenArrangementTypeIsLRA() {
        List<InformationContent> informationContentList = new ArrayList<>();
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("9052");
        depositArrangement.setArrangementType("LRA");
        depositArrangement.setAssociatedProduct(new Product());
        depositArrangement.setAccountNumber("122345154");
        when(productRelatedTokenFactory.postCodeFactory.getMaskedPostcode(depositArrangement)).thenReturn("XXX1235");
        when(productRelatedTokenFactory.informationContentFactory.getInformationContent(any(String.class), any(String.class), any(Integer.class))).thenReturn(new InformationContent());
        informationContentList = productRelatedTokenFactory.getProductRelatedToken(depositArrangement);
        assertNotNull(informationContentList);
    }

    @Test
    public void testGetProductRelatedTokenWithFundaingDays() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("9052");
        depositArrangement.setArrangementType("LOANS");
        depositArrangement.setAssociatedProduct(new Product());
        depositArrangement.setAccountNumber("122345154");
        depositArrangement.setFundingDays(12);
        when(productRelatedTokenFactory.postCodeFactory.getMaskedPostcode(depositArrangement)).thenReturn("XXX1235");
        when(productRelatedTokenFactory.informationContentFactory.getInformationContent(any(String.class), any(String.class), any(Integer.class))).thenReturn(new InformationContent());
        List<InformationContent> informationContentList = productRelatedTokenFactory.getProductRelatedToken(depositArrangement);
        assertNotNull(informationContentList);
    }

    @Test
    public void testGetProductRelatedTokenWhenAssociatedProductNull() {
        List<InformationContent> informationContentList = new ArrayList<>();
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("9052");
        depositArrangement.setArrangementType("FA");
        depositArrangement.setAssociatedProduct(null);
        depositArrangement.setAccountNumber("122345154");
        when(productRelatedTokenFactory.postCodeFactory.getMaskedPostcode(depositArrangement)).thenReturn("XXX1235");
        when(productRelatedTokenFactory.informationContentFactory.getInformationContent(any(String.class), any(String.class), any(Integer.class))).thenReturn(new InformationContent());
        informationContentList = productRelatedTokenFactory.getProductRelatedToken(depositArrangement);
        assertNotNull(informationContentList);
    }

    @Test
    public void testGetProductRelatedTokenWhenArrangementTypeIsCurrentAccount() {
        List<InformationContent> informationContentList = new ArrayList<>();
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("9052");
        depositArrangement.setArrangementType("CA");
        depositArrangement.setAssociatedProduct(new Product());
        depositArrangement.setAccountNumber("122345154");
        depositArrangement.getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        depositArrangement.getAssociatedProduct().getInstructionDetails().setInstructionMnemonic("IB.Product.Mnemonic");
        when(productRelatedTokenFactory.postCodeFactory.getMaskedPostcode(depositArrangement)).thenReturn("XXX1235");
        when(productRelatedTokenFactory.informationContentFactory.getInformationContent(any(String.class), any(String.class), any(Integer.class))).thenReturn(new InformationContent());
        informationContentList = productRelatedTokenFactory.getProductRelatedToken(depositArrangement);
        assertNotNull(informationContentList);
    }

    @Test
    public void testGetProductRelatedTokenWhenArrangementTypeIsCurrentAccountWithOverdraftDetails() {
        List<InformationContent> informationContentList = new ArrayList<>();
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("9052");
        depositArrangement.setArrangementType("CA");
        depositArrangement.setFinancialInstitution(null);
        depositArrangement.setOverdraftDetails(new OverdraftDetails());
        depositArrangement.getOverdraftDetails().setAmount(new CurrencyAmount());
        depositArrangement.getOverdraftDetails().getAmount().setAmount(BigDecimal.valueOf(10254));
        depositArrangement.setAssociatedProduct(new Product());
        depositArrangement.setAccountNumber("122345154");
        depositArrangement.getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        depositArrangement.getAssociatedProduct().getInstructionDetails().setInstructionMnemonic("IB.Product.Mnemonic");
        when(productRelatedTokenFactory.postCodeFactory.getMaskedPostcode(depositArrangement)).thenReturn("XXX1235");
        when(productRelatedTokenFactory.informationContentFactory.getInformationContent(any(String.class), any(String.class), any(Integer.class))).thenReturn(new InformationContent());
        informationContentList = productRelatedTokenFactory.getProductRelatedToken(depositArrangement);
        assertNotNull(informationContentList);
    }

    @Test
    public void testGetProductRelatedTokenWhenArrangementTypeIsFinanceAccount() {
        List<InformationContent> informationContentList = new ArrayList<>();
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("9052");
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("APP_PARAMETER_CAR_FINANCE_EXPIRY_DATE");
        ruleCondition.setResult("EXPIRY_CAR_FINANCE");
        depositArrangement.getConditions().add(ruleCondition);
        depositArrangement.setArrangementType("FA");
        depositArrangement.setAssociatedProduct(new Product());
        depositArrangement.setAccountNumber("122345154");
        when(productRelatedTokenFactory.postCodeFactory.getMaskedPostcode(depositArrangement)).thenReturn("XXX1235");
        when(productRelatedTokenFactory.informationContentFactory.getInformationContent(any(String.class), any(String.class), any(Integer.class))).thenReturn(new InformationContent());
        informationContentList = productRelatedTokenFactory.getProductRelatedToken(depositArrangement);
        assertNotNull(informationContentList);
    }

    @Test
    public void testGetProductRelatedTokenWhenArrangementTypeIsCreditCard() {
        List<InformationContent> informationContentList = new ArrayList<>();
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("9052");
        depositArrangement.setArrangementType("CC");
        depositArrangement.setAssociatedProduct(new Product());
        depositArrangement.setAccountNumber("122345154");
        when(productRelatedTokenFactory.postCodeFactory.getMaskedPostcode(depositArrangement)).thenReturn("XXX1235");
        when(productRelatedTokenFactory.informationContentFactory.getInformationContent(any(String.class), any(String.class), any(Integer.class))).thenReturn(new InformationContent());
        informationContentList = productRelatedTokenFactory.getProductRelatedToken(depositArrangement);
        assertNotNull(informationContentList);
    }

    @Test
    public void testGetProductRelatedTokenWhenArrangementTypeIsLoans() {
        List<InformationContent> informationContentList = new ArrayList<>();
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("9052");
        depositArrangement.setArrangementType("Loans");
        depositArrangement.setAssociatedProduct(new Product());
        depositArrangement.setAccountNumber("122345154");
        when(productRelatedTokenFactory.postCodeFactory.getMaskedPostcode(depositArrangement)).thenReturn("XXX1235");
        when(productRelatedTokenFactory.informationContentFactory.getInformationContent(any(String.class), any(String.class), any(Integer.class))).thenReturn(new InformationContent());
        informationContentList = productRelatedTokenFactory.getProductRelatedToken(depositArrangement);
        assertNotNull(informationContentList);
    }

    @Test
    public void testGetDataTokensForBTFulfilment(){
        FinanceServiceArrangement financeServiceArrangement=testDataHelper.createFinanceServiceArrangement();
        BalanceTransfer balanceTransfer=new BalanceTransfer();
        balanceTransfer.setAmount(new CurrencyAmount());
        balanceTransfer.getAmount().setAmount(BigDecimal.valueOf(10025));
        balanceTransfer.setStatus("SUCCESS");
        financeServiceArrangement.getBalanceTransfer().add(balanceTransfer);
        financeServiceArrangement.setAssociatedProduct(new Product());
        financeServiceArrangement.setPrimaryInvolvedParty(new Customer());
        financeServiceArrangement.getPrimaryInvolvedParty().setIsPlayedBy(new Individual());
        financeServiceArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().add(new IndividualName());
        financeServiceArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().get(0).setLastName("Sharma");
        financeServiceArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().get(0).setPrefixTitle("Mr");
        when(productRelatedTokenFactory.lookUpValueRetriever.getLookUpValues(any(List.class), any(String.class))).thenReturn(new ArrayList<ReferenceDataLookUp>());
        when(productRelatedTokenFactory.informationContentFactory.getInformationContent(any(String.class), any(String.class), any(Integer.class))).thenReturn(new InformationContent());
        when(productRelatedTokenFactory.postCodeFactory.getMaskedPostcode(any(FinanceServiceArrangement.class))).thenReturn("100025");
        productRelatedTokenFactory.getDataTokensForBTFulfilment(financeServiceArrangement);
    }

    @Test
    public void testGetDataTokensForBTFulfilmentWithFail(){
        FinanceServiceArrangement financeServiceArrangement=testDataHelper.createFinanceServiceArrangement();
        BalanceTransfer balanceTransfer=new BalanceTransfer();
        balanceTransfer.setAmount(new CurrencyAmount());
        balanceTransfer.getAmount().setAmount(BigDecimal.valueOf(10025));
        balanceTransfer.setStatus("FAIL");
        financeServiceArrangement.getBalanceTransfer().add(balanceTransfer);
        when(productRelatedTokenFactory.lookUpValueRetriever.getLookUpValues(any(List.class), any(String.class))).thenReturn(new ArrayList<ReferenceDataLookUp>());
        when(productRelatedTokenFactory.informationContentFactory.getInformationContent(any(String.class), any(String.class), any(Integer.class))).thenReturn(new InformationContent());
        when(productRelatedTokenFactory.postCodeFactory.getMaskedPostcode(any(FinanceServiceArrangement.class))).thenReturn("100025");
        productRelatedTokenFactory.getDataTokensForBTFulfilment(financeServiceArrangement);
    }


}
