package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.utility;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream.FulfilCardOrderRetriever;
import com.lloydsbanking.salsa.soap.cmas.c812.objects.C812Resp;
import com.lloydsbanking.salsa.soap.cmas.c812.objects.CardOrderCBSData;
import com.lloydsbanking.salsa.soap.cmas.c818.objects.C818Req;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Category(UnitTest.class)
public class OrderAccessCardDetailsTest {
    OrderAccessCardDetails orderAccessCardDetails;

    TestDataHelper testDataHelper;

    RequestHeader header;

    @Before
    public void setUp() throws Exception {
        orderAccessCardDetails = new OrderAccessCardDetails();
        testDataHelper = new TestDataHelper();
        orderAccessCardDetails.orderAccessServiceHelper=new OrderAccessServiceHelper();
        orderAccessCardDetails.fulfilCardOrderRetriever = mock(FulfilCardOrderRetriever.class);
        header = new RequestHeader();

    }
    @Test
    public void testGetCardDeliveryAddress() {
        C812Resp c812Resp=testDataHelper.createC812Response();
        c812Resp.getCardOrderNewValid().getCardNewDelivery().setAddressExtSysId(4);
        assertEquals("21 PARK STREET", orderAccessCardDetails.getCardDeliveryAddress(c812Resp).getAddressLine1Tx());
    }

    @Test
    public void testFulfillCardOrder() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        Customer customer = new Customer();
        customer.setEmailAddress("sdfgd@fgh.com");

        PostalAddress postalAddress = testDataHelper.getPrimaryInvolvedParty().getPostalAddress().get(0);
        customer.getPostalAddress().add(postalAddress);
        customer.setCustomerIdentifier("123");
        orderAccessCardDetails.fullFillCardOrder("112345", "1100001156", customer, header, "1", "123", 1, testDataHelper.createC846Response(), "FGHI/ABCDEFHI.MR", testDataHelper.createC812Response(), 1);

        verify(orderAccessCardDetails.fulfilCardOrderRetriever).getResponse(any(C818Req.class), any(RequestHeader.class));
    }
    @Test
    public void testGetCardOrderCBSData() {
        CardOrderCBSData cardOrderCBSData = orderAccessCardDetails.getCardOrderCBSData(50, "1", "R", 50);
        assertEquals(50, cardOrderCBSData.getCardOrderCBSDecision().getDebitCardRenewalCd().intValue());
        assertEquals("1", cardOrderCBSData.getCCAApplicableIn());

    }
}
