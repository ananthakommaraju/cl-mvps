package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream.InitiateCardOrderRetriever;
import com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream.RetrieveEligibleCardsRetriver;
import com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream.ValidateCardOrderRetriever;
import com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.evaluate.CustomerCardHolderNameEvaluator;
import com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.utility.OrderAccessCardDetails;
import com.lloydsbanking.salsa.soap.cmas.c808.objects.C808Req;
import com.lloydsbanking.salsa.soap.cmas.c808.objects.C808Resp;
import com.lloydsbanking.salsa.soap.cmas.c808.objects.CardOrderCBSAddress;
import com.lloydsbanking.salsa.soap.cmas.c808.objects.CardholderNew;
import com.lloydsbanking.salsa.soap.cmas.c812.objects.C812Req;
import com.lloydsbanking.salsa.soap.cmas.c812.objects.C812Resp;
import com.lloydsbanking.salsa.soap.cmas.c846.objects.C846Req;
import com.lloydsbanking.salsa.soap.cmas.c846.objects.C846Resp;
import com.lloydsbanking.salsa.soap.cmas.c846.objects.PlasticType;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_bo.businessobjects.Product;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class OrderAccessServiceTest {
    OrderAccessService service;

    TestDataHelper testDataHelper;

    RequestHeader header;

    @Before
    public void setUp() throws Exception {
        service = new OrderAccessService();
        testDataHelper = new TestDataHelper();
        service.initiateCardOrderRetriever = mock(InitiateCardOrderRetriever.class);
        service.retrieveEligibleCardsRetriver = mock(RetrieveEligibleCardsRetriver.class);
        service.customerCardHolderName = new CustomerCardHolderNameEvaluator();
        service.validateCardOrderRetriever = mock(ValidateCardOrderRetriever.class);
        service.orderAccessCardDetails = mock(OrderAccessCardDetails.class);
        service.customerCardHolderName = mock(CustomerCardHolderNameEvaluator.class);
        header = new RequestHeader();

    }

    @Test
    public void testOrderAccessService() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        Customer customer = new Customer();
        customer.setEmailAddress("sdfgd@fgh.com");
        PostalAddress postalAddress = testDataHelper.getPrimaryInvolvedParty().getPostalAddress().get(0);
        customer.getPostalAddress().add(postalAddress);
        customer.setCustomerIdentifier("123");
        ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
        extSysProdIdentifier.setProductIdentifier("1234");
        extSysProdIdentifier.setSystemCode("1001");
        Product product = new Product();
        product.getExternalSystemProductIdentifier().add(extSysProdIdentifier);
        C808Resp c808Resp = testDataHelper.createC808Res();
        when(service.initiateCardOrderRetriever.getResponse(any(C808Req.class), any(RequestHeader.class))).thenReturn(c808Resp);
        when(service.customerCardHolderName.getCardHolderName(any(CardholderNew.class), any(PlasticType.class))).thenReturn("name");
        C846Resp c846Resp = testDataHelper.createC846Response();
        when(service.retrieveEligibleCardsRetriver.getResponse(any(C846Req.class), any(RequestHeader.class))).thenReturn(c846Resp);
        when(service.orderAccessCardDetails.getCardOrderCBSData(anyInt(), anyString(), anyString(), anyInt())).thenReturn(testDataHelper.createCardOrderCBSData());
        when(service.validateCardOrderRetriever.getResponse(any(C812Req.class), any(RequestHeader.class))).thenReturn(testDataHelper.createC812Response());
        when(service.orderAccessCardDetails.fullFillCardOrder(anyString(), anyString(), any(Customer.class), any(RequestHeader.class), anyString(), anyString(), anyInt(), any(C846Resp.class), anyString(), any(C812Resp.class), anyInt())).thenReturn(testDataHelper.createC818Resp());
        assertEquals("281254276", service.orderAccessServiceResponse("112345", "1100001156", customer, header));
    }

    @Test
    public void testOrderAccessServiceWhenOrderIsDeclined() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        Customer customer = new Customer();
        customer.setEmailAddress("sdfgd@fgh.com");
        PostalAddress postalAddress = testDataHelper.getPrimaryInvolvedParty().getPostalAddress().get(0);
        customer.getPostalAddress().add(postalAddress);
        customer.setCustomerIdentifier("123");
        ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
        extSysProdIdentifier.setProductIdentifier("1234");
        extSysProdIdentifier.setSystemCode("1001");
        Product product = new Product();
        product.getExternalSystemProductIdentifier().add(extSysProdIdentifier);
        C808Resp c808Resp = testDataHelper.createC808Res();
        C808Req c808Req = testDataHelper.createC808Request("112345", "1100001156", Long.valueOf("123"));
        when(service.initiateCardOrderRetriever.getResponse(any(C808Req.class), any(RequestHeader.class))).thenReturn(c808Resp);
        C846Resp c846Resp = testDataHelper.createC846Response();
        final C846Req c846Request = testDataHelper.createC846Request("0071776000", "1", "R", 50, Long.valueOf("123"));
        when(service.customerCardHolderName.getCardHolderName(any(CardholderNew.class), any(PlasticType.class))).thenReturn("name");
        when(service.retrieveEligibleCardsRetriver.getResponse(any(C846Req.class), any(RequestHeader.class))).thenReturn(c846Resp);
        when(service.orderAccessCardDetails.getCardOrderCBSData(50, "1", "R", 50)).thenReturn(testDataHelper.createCardOrderCBSData());
        C812Resp c812Resp = testDataHelper.createC812Response();
        c812Resp.getCardOrderReferralReasons().getCardOrderReferralReason().get(0).setCardOrderReferralReasonCd(6363l);
        when(service.validateCardOrderRetriever.getResponse(any(C812Req.class), any(RequestHeader.class))).thenReturn(c812Resp);
        assertEquals("0000000000", service.orderAccessServiceResponse("112345", "1100001156", customer, header));
        verify(service.orderAccessCardDetails, times(0)).fullFillCardOrder("112345", "1100001156", customer, header, "1", "0071776000", 4, c846Resp, "FGHI/ABCDEFHI.MR", c812Resp, 3);
    }

    @Test
    public void testOrderAccessServiceC808HasNewAccount() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        Customer customer = new Customer();
        customer.setEmailAddress("sdfgd@fgh.com");
        PostalAddress postalAddress = testDataHelper.getPrimaryInvolvedParty().getPostalAddress().get(0);
        customer.getPostalAddress().add(postalAddress);
        customer.setCustomerIdentifier("123");
        ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
        extSysProdIdentifier.setProductIdentifier("1234");
        extSysProdIdentifier.setSystemCode("1001");
        Product product = new Product();
        product.getExternalSystemProductIdentifier().add(extSysProdIdentifier);
        C808Resp c808Resp = testDataHelper.createC808Res();
        c808Resp.getCardOrderNewAccount().setCardOrderCBSAddress(new CardOrderCBSAddress());
        when(service.initiateCardOrderRetriever.getResponse(any(C808Req.class), any(RequestHeader.class))).thenReturn(c808Resp);
        when(service.customerCardHolderName.getCardHolderName(any(CardholderNew.class), any(PlasticType.class))).thenReturn("name");
        C846Resp c846Resp = testDataHelper.createC846Response();
        when(service.retrieveEligibleCardsRetriver.getResponse(any(C846Req.class), any(RequestHeader.class))).thenReturn(c846Resp);
        when(service.orderAccessCardDetails.getCardOrderCBSData(anyInt(), anyString(), anyString(), anyInt())).thenReturn(testDataHelper.createCardOrderCBSData());
        C812Resp c812Resp = testDataHelper.createC812Response();
        c812Resp.getCardOrderReferralReasons().getCardOrderReferralReason().clear();
        when(service.validateCardOrderRetriever.getResponse(any(C812Req.class), any(RequestHeader.class))).thenReturn(c812Resp);
        when(service.orderAccessCardDetails.fullFillCardOrder(anyString(), anyString(), any(Customer.class), any(RequestHeader.class), anyString(), anyString(), anyInt(), any(C846Resp.class), anyString(), any(C812Resp.class), anyInt())).thenReturn(testDataHelper.createC818Resp());
        assertEquals("281254276", service.orderAccessServiceResponse("112345", "1100001156", customer, header));
    }
}
