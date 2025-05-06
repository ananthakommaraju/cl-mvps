package com.lloydsbanking.salsa.apacc.service.fulfil;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.downstream.*;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.apacc.TestDataHelper;
import com.lloydsbanking.salsa.apacc.service.fulfil.downstream.AddNewProductForInvolvedParty;
import com.lloydsbanking.salsa.apacc.service.fulfil.downstream.AddOMSOffer;
import com.lloydsbanking.salsa.apacc.service.fulfil.downstream.CreateCreditCardAccountRetriever;
import com.lloydsbanking.salsa.apacc.service.fulfil.downstream.RetrieveNextBusinessDay;
import com.lloydsbanking.salsa.apacc.service.fulfil.gendoc.downstream.GenerateDocumentRetriever;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamServiceForActivateFSA;
import com.lloydstsb.ib.wsbridge.system.StB748BWrkngDateAfterXDays;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class FulfilPendingCreditCardArrangementTest {

    FulfilPendingCreditCardArrangement fulfilPendingCreditCardArrangement;

    ActivateProductArrangementRequest request;

    ActivateProductArrangementResponse response;

    Product product = new Product();

    TestDataHelper testDataHelper;
    Map<String, String> encryptionKeyMap;

    @Before
    public void setUp() {
        fulfilPendingCreditCardArrangement = new FulfilPendingCreditCardArrangement();
        fulfilPendingCreditCardArrangement.retrieveProductFeatures = mock(RetrieveProductFeatures.class);
        fulfilPendingCreditCardArrangement.lookUpValueRetriever = mock(LookUpValueRetriever.class);
        fulfilPendingCreditCardArrangement.addNewProductForInvolvedParty = mock(AddNewProductForInvolvedParty.class);
        fulfilPendingCreditCardArrangement.customerSegmentCheckService = mock(CustomerSegmentCheckService.class);
        fulfilPendingCreditCardArrangement.updateMarketingPreferences = mock(UpdateMarketingPreferences.class);
        fulfilPendingCreditCardArrangement.createCreditCardAccountRetriever = mock(CreateCreditCardAccountRetriever.class);
        fulfilPendingCreditCardArrangement.generateDocumentRetriever = mock(GenerateDocumentRetriever.class);
        fulfilPendingCreditCardArrangement.addOMSOffer = mock(AddOMSOffer.class);
        fulfilPendingCreditCardArrangement.validateFulfilPendingCreditCardArrangement = mock(ValidateFulfilPendingCreditCardArrangement.class);
        fulfilPendingCreditCardArrangement.createCardAndAddNewProductForInvolvedParty = mock(CreateCardAndAddNewProductForInvolvedParty.class);
        fulfilPendingCreditCardArrangement.ibRegistration = mock(IBRegistration.class);
        fulfilPendingCreditCardArrangement.communicateFulfilActivities = mock(CommunicateFulfilActivities.class);
        fulfilPendingCreditCardArrangement.encryptDataRetriever = mock(EncryptDataRetriever.class);
        fulfilPendingCreditCardArrangement.updatePamServiceForActivateFSA = mock(UpdatePamServiceForActivateFSA.class);
        fulfilPendingCreditCardArrangement.retrieveNextBusinessDay = mock(RetrieveNextBusinessDay.class);
        testDataHelper = new TestDataHelper();
        request = testDataHelper.createApaRequestForCc(100);
        response = new ActivateProductArrangementResponse();
        product.getProductoffer().add(new ProductOffer());
        product.getProductoffer().get(0).setProdOfferIdentifier("1234");
        encryptionKeyMap = new HashMap<>();
        encryptionKeyMap.put("", "");
    }

    @Test
    public void testFulfilPendingCreditCardArrangement() throws ActivateProductArrangementResourceNotAvailableErrorMsg {
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        response.setProductArrangement(financeServiceArrangement);
        when(fulfilPendingCreditCardArrangement.createCreditCardAccountRetriever.createCreditCardAccount(any(FinanceServiceArrangement.class), any(RequestHeader.class), any(ApplicationDetails.class))).thenReturn(testDataHelper.createF241Response("0"));
        doNothing().when(fulfilPendingCreditCardArrangement.addNewProductForInvolvedParty).addNewProduct(any(RequestHeader.class), any(FinanceServiceArrangement.class), any(ApplicationDetails.class));
        when(fulfilPendingCreditCardArrangement.retrieveProductFeatures.getProduct(any(ProductArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class))).thenReturn(product);
        fulfilPendingCreditCardArrangement.fulfilPendingCreditCardArrangement(request, response, encryptionKeyMap);
        assertNotNull(response);
        verify(fulfilPendingCreditCardArrangement.retrieveProductFeatures).getProduct(any(ProductArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class));
    }

    @Test
    public void testExtSysProdIdentifier() throws ActivateProductArrangementResourceNotAvailableErrorMsg {
        product.getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        response.setProductArrangement(financeServiceArrangement);
        product.getExternalSystemProductIdentifier().get(0).setSystemCode("00010");
        when(fulfilPendingCreditCardArrangement.createCreditCardAccountRetriever.createCreditCardAccount(any(FinanceServiceArrangement.class), any(RequestHeader.class), any(ApplicationDetails.class))).thenReturn(testDataHelper.createF241Response("0"));
        doNothing().when(fulfilPendingCreditCardArrangement.addNewProductForInvolvedParty).addNewProduct(any(RequestHeader.class), any(FinanceServiceArrangement.class), any(ApplicationDetails.class));
        when(fulfilPendingCreditCardArrangement.retrieveProductFeatures.getProduct(any(ProductArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class))).thenReturn(product);
        fulfilPendingCreditCardArrangement.fulfilPendingCreditCardArrangement(request, response, encryptionKeyMap);
        verify(fulfilPendingCreditCardArrangement.retrieveProductFeatures).getProduct(any(ProductArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class));
    }

    @Test
    public void testFulfilPendingCreditCardArrangementForCreditCardAccount() throws ActivateProductArrangementResourceNotAvailableErrorMsg {
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        financeServiceArrangement.setAddOnCreditCardNumber("98450021");
        response.setProductArrangement(financeServiceArrangement);
        when(fulfilPendingCreditCardArrangement.createCreditCardAccountRetriever.createCreditCardAccount(any(FinanceServiceArrangement.class), any(RequestHeader.class), any(ApplicationDetails.class))).thenReturn(testDataHelper.createF241Response("0"));
        doNothing().when(fulfilPendingCreditCardArrangement.addNewProductForInvolvedParty).addNewProduct(any(RequestHeader.class), any(FinanceServiceArrangement.class), any(ApplicationDetails.class));
        when(fulfilPendingCreditCardArrangement.retrieveProductFeatures.getProduct(any(ProductArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class))).thenReturn(product);
        when(fulfilPendingCreditCardArrangement.validateFulfilPendingCreditCardArrangement.isFulfillNewApplication(any(String.class))).thenReturn(true);
        when(fulfilPendingCreditCardArrangement.validateFulfilPendingCreditCardArrangement.checkIfAddCardHolderFailureOrIsJointParty(any(FinanceServiceArrangement.class), any(ApplicationDetails.class))).thenReturn(true);
        when(fulfilPendingCreditCardArrangement.validateFulfilPendingCreditCardArrangement.isAddOMSRequired(true, request.getProductArrangement().getApplicationSubStatus())).thenReturn(true);
        when(fulfilPendingCreditCardArrangement.addOMSOffer.addOMSOffers(any(FinanceServiceArrangement.class), any(RequestHeader.class), any(ApplicationDetails.class))).thenReturn(true);
        when(fulfilPendingCreditCardArrangement.validateFulfilPendingCreditCardArrangement.isIBActivationRequired(true, true, request.getProductArrangement().getPrimaryInvolvedParty())).thenReturn(true);
        fulfilPendingCreditCardArrangement.fulfilPendingCreditCardArrangement(request, response, encryptionKeyMap);
        assertNotNull(response);
        verify(fulfilPendingCreditCardArrangement.retrieveProductFeatures).getProduct(any(ProductArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class));
    }

    @Test
    public void testCustomerSegmentCheckService() {
        FinanceServiceArrangement financeServiceArrangement = testDataHelper.createFinanceServiceArrangement("1");
        financeServiceArrangement.getBalanceTransfer().add(new BalanceTransfer());
        financeServiceArrangement.setApplicationSubStatus("1029");
        request.setProductArrangement(financeServiceArrangement);
        response.setProductArrangement(financeServiceArrangement);
        when(fulfilPendingCreditCardArrangement.createCreditCardAccountRetriever.createCreditCardAccount(any(FinanceServiceArrangement.class), any(RequestHeader.class), any(ApplicationDetails.class))).thenReturn(testDataHelper.createF241Response("0"));
        doNothing().when(fulfilPendingCreditCardArrangement.addNewProductForInvolvedParty).addNewProduct(any(RequestHeader.class), any(FinanceServiceArrangement.class), any(ApplicationDetails.class));
        when(fulfilPendingCreditCardArrangement.retrieveProductFeatures.getProduct(any(ProductArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class))).thenReturn(product);
        when(fulfilPendingCreditCardArrangement.retrieveNextBusinessDay.retrieveNextBusinessDay(any(RequestHeader.class), any(ApplicationDetails.class))).thenReturn(new StB748BWrkngDateAfterXDays());
        when(fulfilPendingCreditCardArrangement.validateFulfilPendingCreditCardArrangement.isPreviousCallSuccessful(true, "1029", "1029")).thenReturn(true);
        fulfilPendingCreditCardArrangement.fulfilPendingCreditCardArrangement(request, response, encryptionKeyMap);
        assertNotNull(response);
        verify(fulfilPendingCreditCardArrangement.retrieveProductFeatures).getProduct(any(ProductArrangement.class), any(ApplicationDetails.class), any(RequestHeader.class));

    }
}
