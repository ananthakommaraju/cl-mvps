package com.lloydsbanking.salsa.ppae.service.process;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.communication.downstream.CommunicationManager;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.activate.downstream.PrdClient;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import com.lloydsbanking.salsa.ppae.service.appstatus.PpaeInvocationIdentifier;
import com.lloydsbanking.salsa.ppae.service.convert.PrdRequestFactory;
import com.lloydsbanking.salsa.ppae.service.downstream.EnquirePaymentRetriever;
import com.lloydsbanking.salsa.ppae.service.downstream.IssueInPaymentInstRetriever;
import com.lloydsbanking.salsa.ppae.service.downstream.VerifyProductArrangementDetails;
import lib_sim_bo.businessobjects.*;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class AwaitingPostFulfilmentProcessorTest {

    AwaitingPostFulfilmentProcessor awaitingPostFulfilmentProcessor;
    TestDataHelper testDataHelper;
    RetrieveProductConditionsResponse retrieveProductConditionsResponse = null;
    PrdRequestFactory rpcRequestFactory;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        awaitingPostFulfilmentProcessor = new AwaitingPostFulfilmentProcessor();
        awaitingPostFulfilmentProcessor.enquirePaymentRetriever = mock(EnquirePaymentRetriever.class);
        awaitingPostFulfilmentProcessor.issueInPaymentInstRetriever = mock(IssueInPaymentInstRetriever.class);
        awaitingPostFulfilmentProcessor.rpcRequestFactory = mock(PrdRequestFactory.class);
        awaitingPostFulfilmentProcessor.prdClient = mock(PrdClient.class);
        awaitingPostFulfilmentProcessor.verifyProductArrangementDetails = mock(VerifyProductArrangementDetails.class);
        awaitingPostFulfilmentProcessor.communicationManager = mock(CommunicationManager.class);
        awaitingPostFulfilmentProcessor.lookUpValueRetriever = mock(LookUpValueRetriever.class);
        rpcRequestFactory = new PrdRequestFactory();

    }

    @Test
    public void processTest() throws DatatypeConfigurationException, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg {
        ProductArrangement productArrangement = testDataHelper.createProductArrangement();
        ProcessPendingArrangementEventRequest request = testDataHelper.createPpaeRequest();
        awaitingPostFulfilmentProcessor.process(productArrangement, request, new PpaeInvocationIdentifier());
        verify(awaitingPostFulfilmentProcessor.rpcRequestFactory).convert(productArrangement, request.getHeader());
        verify(awaitingPostFulfilmentProcessor.prdClient).retrieveProductConditions(any(RetrieveProductConditionsRequest.class));
    }

    @Test
    public void processTestWithBalanceTransfer() throws DatatypeConfigurationException, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg {
        FinanceServiceArrangement financeServiceArrangement = testDataHelper.createFinanceArrangementForCC();
        BalanceTransfer balanceTransfer = new BalanceTransfer();
        financeServiceArrangement.getBalanceTransfer().add(balanceTransfer);
        ProcessPendingArrangementEventRequest request = testDataHelper.createPpaeRequest();
        awaitingPostFulfilmentProcessor.process(financeServiceArrangement, request, new PpaeInvocationIdentifier());
        verify(awaitingPostFulfilmentProcessor.rpcRequestFactory).convert(financeServiceArrangement, request.getHeader());
        verify(awaitingPostFulfilmentProcessor.prdClient).retrieveProductConditions(any(RetrieveProductConditionsRequest.class));
    }

    @Test
    public void testIssueInPaymentInstRetriever() throws DatatypeConfigurationException, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg {
        FinanceServiceArrangement productArrangement = testDataHelper.createFinanceArrangementForCC();
        ProcessPendingArrangementEventRequest request = testDataHelper.createPpaeRequest();
        retrieveProductConditionsResponse = new RetrieveProductConditionsResponse();
        Product product = new Product();
        ProductOffer productOffer = new ProductOffer();
        productOffer.setProdOfferIdentifier("BT_OFF_1");
        ProductAttributes productAttributes = new ProductAttributes();
        productAttributes.setAttributeCode("BT_OFF_1");
        productAttributes.setAttributeValue("1234");
        productOffer.getProductattributes().add(productAttributes);
        product.getProductoffer().add(productOffer);
        productArrangement.getBalanceTransfer().clear();
        retrieveProductConditionsResponse.getProduct().add(product);
        BalanceTransfer balanceTransfer = new BalanceTransfer();
        productArrangement.getBalanceTransfer().add(balanceTransfer);
        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setAccountNumber("12345");
        accountDetails.setSortCode("444");
        productArrangement.setCreditCardNumber("1222222");
        balanceTransfer.setCreditCardNumber("12345678");
        balanceTransfer.setCurrentAccountDetails(accountDetails);
        balanceTransfer.setAmount(new CurrencyAmount());
        balanceTransfer.getAmount().setAmount(new BigDecimal("0"));
        when(awaitingPostFulfilmentProcessor.rpcRequestFactory.convert(productArrangement, request.getHeader())).thenReturn(new RetrieveProductConditionsRequest());
        when(awaitingPostFulfilmentProcessor.prdClient.retrieveProductConditions(any(RetrieveProductConditionsRequest.class))).thenReturn(retrieveProductConditionsResponse);
        when(awaitingPostFulfilmentProcessor.verifyProductArrangementDetails.verify(balanceTransfer, productArrangement.getPrimaryInvolvedParty(), request.getHeader())).thenReturn(true);
        awaitingPostFulfilmentProcessor.process(productArrangement, request, new PpaeInvocationIdentifier());
        verify(awaitingPostFulfilmentProcessor.verifyProductArrangementDetails).verify(balanceTransfer, productArrangement.getPrimaryInvolvedParty(), request.getHeader());
        verify(awaitingPostFulfilmentProcessor.issueInPaymentInstRetriever).invoke("1234", request.getHeader(), balanceTransfer, productArrangement.getCreditCardNumber());
    }

    @Test
    public void testIssueInPaymentInstRetrieverWithAmtLessThanZero() throws DatatypeConfigurationException, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg {
        FinanceServiceArrangement productArrangement = testDataHelper.createFinanceArrangementForCC();
        ProcessPendingArrangementEventRequest request = testDataHelper.createPpaeRequest();
        retrieveProductConditionsResponse = new RetrieveProductConditionsResponse();
        Product product = new Product();
        ProductOffer productOffer = new ProductOffer();
        productOffer.setProdOfferIdentifier("BT_OFF_1");
        ProductAttributes productAttributes = new ProductAttributes();
        productAttributes.setAttributeCode("BT_OFF_1");
        productAttributes.setAttributeValue("1234");
        productOffer.getProductattributes().add(productAttributes);
        product.getProductoffer().add(productOffer);
        productArrangement.getBalanceTransfer().clear();
        retrieveProductConditionsResponse.getProduct().add(product);
        BalanceTransfer balanceTransfer = new BalanceTransfer();
        productArrangement.getBalanceTransfer().add(balanceTransfer);
        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setAccountNumber("12345");
        accountDetails.setSortCode("444");
        productArrangement.setCreditCardNumber("1222222");
        balanceTransfer.setCreditCardNumber("12345678");
        balanceTransfer.setCurrentAccountDetails(accountDetails);
        balanceTransfer.setAmount(new CurrencyAmount());
        balanceTransfer.getAmount().setAmount(new BigDecimal("-1"));
        when(awaitingPostFulfilmentProcessor.rpcRequestFactory.convert(productArrangement, request.getHeader())).thenReturn(new RetrieveProductConditionsRequest());
        when(awaitingPostFulfilmentProcessor.prdClient.retrieveProductConditions(any(RetrieveProductConditionsRequest.class))).thenReturn(retrieveProductConditionsResponse);
        when(awaitingPostFulfilmentProcessor.verifyProductArrangementDetails.verify(balanceTransfer, productArrangement.getPrimaryInvolvedParty(), request.getHeader())).thenReturn(true);
        awaitingPostFulfilmentProcessor.process(productArrangement, request, new PpaeInvocationIdentifier());
        verify(awaitingPostFulfilmentProcessor.rpcRequestFactory).convert(productArrangement, request.getHeader());
        verify(awaitingPostFulfilmentProcessor.verifyProductArrangementDetails, times(0)).verify(balanceTransfer, productArrangement.getPrimaryInvolvedParty(), request.getHeader());
        verify(awaitingPostFulfilmentProcessor.issueInPaymentInstRetriever).invoke("1234", request.getHeader(), balanceTransfer, productArrangement.getCreditCardNumber());
    }

    @Test
    public void testIssueInPaymentInstRetrieverWithSuccessLookUpCall() throws DatatypeConfigurationException, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg {
        FinanceServiceArrangement productArrangement = testDataHelper.createFinanceArrangementForCC();
        ProcessPendingArrangementEventRequest request = testDataHelper.createPpaeRequest();
        retrieveProductConditionsResponse = new RetrieveProductConditionsResponse();
        Product product = new Product();
        ProductOffer productOffer = new ProductOffer();
        productOffer.setProdOfferIdentifier("BT_OFF_1");
        ProductAttributes productAttributes = new ProductAttributes();
        productAttributes.setAttributeCode("BT_OFF_1");
        productAttributes.setAttributeValue("1234");
        productOffer.getProductattributes().add(productAttributes);
        product.getProductoffer().add(productOffer);
        productArrangement.getBalanceTransfer().clear();
        retrieveProductConditionsResponse.getProduct().add(product);
        BalanceTransfer balanceTransfer = new BalanceTransfer();
        productArrangement.getBalanceTransfer().add(balanceTransfer);
        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setAccountNumber("12345");
        accountDetails.setSortCode("444");
        productArrangement.setCreditCardNumber("1222222");
        balanceTransfer.setCreditCardNumber("12345678");
        balanceTransfer.setCurrentAccountDetails(accountDetails);
        balanceTransfer.setAmount(new CurrencyAmount());
        balanceTransfer.getAmount().setAmount(new BigDecimal("2"));
        ReferenceDataLookUp refLookUp1 = new ReferenceDataLookUp();
        refLookUp1.setGroupCode("BT_AVS_THRESHOLD");
        refLookUp1.setLookupValueDesc("2");
        ReferenceDataLookUp refLookUp2 = new ReferenceDataLookUp();
        refLookUp2.setGroupCode("BT_CA_AVS_THRESHOLD");
        refLookUp2.setLookupValueDesc("0");
        when(awaitingPostFulfilmentProcessor.lookUpValueRetriever.getLookUpValues(any(List.class), any(String.class))).thenReturn(Arrays.asList(refLookUp1, refLookUp2));
        when(awaitingPostFulfilmentProcessor.rpcRequestFactory.convert(productArrangement, request.getHeader())).thenReturn(new RetrieveProductConditionsRequest());
        when(awaitingPostFulfilmentProcessor.prdClient.retrieveProductConditions(any(RetrieveProductConditionsRequest.class))).thenReturn(retrieveProductConditionsResponse);
        when(awaitingPostFulfilmentProcessor.verifyProductArrangementDetails.verify(balanceTransfer, productArrangement.getPrimaryInvolvedParty(), request.getHeader())).thenReturn(true);
        awaitingPostFulfilmentProcessor.process(productArrangement, request, new PpaeInvocationIdentifier());
        verify(awaitingPostFulfilmentProcessor.verifyProductArrangementDetails).verify(balanceTransfer, productArrangement.getPrimaryInvolvedParty(), request.getHeader());
        verify(awaitingPostFulfilmentProcessor.issueInPaymentInstRetriever).invoke("1234", request.getHeader(), balanceTransfer, productArrangement.getCreditCardNumber());
    }

}
