package com.lloydsbanking.salsa.activate.registration;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.registration.downstream.IbRegistrationRetriever;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class RegistrationServiceTest {
    TestDataHelper testDataHelper;

    RequestHeader requestHeader;

    RegistrationService registrationService;

    @Before
    public void setUp() {
        registrationService = new RegistrationService();
        registrationService.ibRegistrationRetriever = mock(IbRegistrationRetriever.class);
        testDataHelper = new TestDataHelper();
        requestHeader = testDataHelper.createApaRequestHeader();
        registrationService.productArrangementTraceLog = mock(ProductArrangementTraceLog.class);
        when(registrationService.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class), any(String.class))).thenReturn("ProdArrngemnt");

    }

    @Test
    public void testRegisterForInternetBanking() {
        ProductArrangement productArrangement = testDataHelper.createProductArrangementAsAnInstanceOfFinanceServiceArrangement();
        registrationService.serviceCallForIBRegistration(requestHeader, productArrangement);
        verify(registrationService.ibRegistrationRetriever).registerForInternetBanking(requestHeader, productArrangement.getPrimaryInvolvedParty(), productArrangement.getAccountNumber(), 1, "A");
    }

    @Test
    public void testRegisterForInternetBankingWithMarketingIndicatorOff() {
        ProductArrangement productArrangement = testDataHelper.createProductArrangementAsAnInstanceOfFinanceServiceArrangement();
        ((FinanceServiceArrangement) productArrangement).setMarketingPrefereceIndicator(false);
        registrationService.serviceCallForIBRegistration(requestHeader, productArrangement);
        verify(registrationService.ibRegistrationRetriever).registerForInternetBanking(requestHeader, productArrangement.getPrimaryInvolvedParty(), productArrangement.getAccountNumber(), 0, "A");
    }

    @Test
    public void testRegisterForInternetBankingWhenArrangementTypeIsCurrent() {
        ProductArrangement productArrangement = testDataHelper.createProductArrangementAsAnInstanceOfFinanceServiceArrangement();
        productArrangement.setArrangementType("CA");
        registrationService.serviceCallForIBRegistration(requestHeader, productArrangement);
        verify(registrationService.ibRegistrationRetriever).registerForInternetBanking(requestHeader, productArrangement.getPrimaryInvolvedParty(), productArrangement.getAccountNumber(), 1, "C");
    }

    @Test
    public void testRegisterForInternetBankingWhenProductArrangementIsNotAnInstanceOfFinanceService() {
        ProductArrangement productArrangement = testDataHelper.createProductArrangement();
        registrationService.serviceCallForIBRegistration(requestHeader, productArrangement);
        verify(registrationService.ibRegistrationRetriever).registerForInternetBanking(requestHeader, productArrangement.getPrimaryInvolvedParty(), productArrangement.getAccountNumber(), 0, "A");
    }

    @Test
    public void testRegisterForInternetBankingWhenCustomerIsNotRegistered() {
        ProductArrangement productArrangement = testDataHelper.createProductArrangementAsAnInstanceOfFinanceServiceArrangement();
        productArrangement.setArrangementType("CA");
        productArrangement.getPrimaryInvolvedParty().setIsRegistrationSelected(false);
        registrationService.serviceCallForIBRegistration(requestHeader, productArrangement);
        verify(registrationService.ibRegistrationRetriever, times(0)).registerForInternetBanking(requestHeader, productArrangement.getPrimaryInvolvedParty(), productArrangement.getAccountNumber(), 1, "C");
    }
}
