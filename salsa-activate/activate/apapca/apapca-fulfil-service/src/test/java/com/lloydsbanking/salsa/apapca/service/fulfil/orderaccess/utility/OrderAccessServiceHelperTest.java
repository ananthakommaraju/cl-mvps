package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.utility;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.soap.cmas.c812.objects.C812Resp;
import com.lloydsbanking.salsa.soap.cmas.c818.objects.CardOrderActions;
import com.lloydsbanking.salsa.soap.cmas.c818.objects.CardOrderAdd;
import com.lloydsbanking.salsa.soap.cmas.c846.objects.PlasticType;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_bo.businessobjects.Product;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertEquals;

@Category(UnitTest.class)
public class OrderAccessServiceHelperTest {
    OrderAccessServiceHelper serviceHelper;

    TestDataHelper testDataHelper;

    RequestHeader header;

    @Before
    public void setUp() throws Exception {
        serviceHelper = new OrderAccessServiceHelper();
        testDataHelper = new TestDataHelper();
        header = new RequestHeader();

    }

    @Test
    public void testGetCardOrderAdd() {
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
        PlasticType plasticType = testDataHelper.createC846Response().getPlasticTypes().getPlasticType().get(0);
        CardOrderAdd cardOrderAdd = serviceHelper.getCardOrderAdd("112345", "1100001156", customer, "1234", 1, "FGHI/ABCDEFHI.MR", plasticType, "3", 1);
        assertEquals("3", cardOrderAdd.getPlasticTypeServiceLevelCd());
        assertEquals("Y", cardOrderAdd.getCardOrderAuthorityCd());
    }

    @Test
    public void testGetCardOrderActions() {
        C812Resp c812Resp = testDataHelper.createC812Response();
        CardOrderActions cardOrderActions = serviceHelper.getCardOrderActions(1, c812Resp.getCardOrderReferralReasons());
        assertEquals(6, cardOrderActions.getCardOrderAction().get(1).getCMASActionCd().intValue());
        CardOrderActions cardOrderActions1 = serviceHelper.getCardOrderActions(3, c812Resp.getCardOrderReferralReasons());
        assertEquals(5, cardOrderActions1.getCardOrderAction().get(1).getCMASActionCd().intValue());
    }

    @Test
    public void testGetCMASActionTypeCode() {
        assertEquals(1, serviceHelper.getCMASActionTypeCode(6364l));
        assertEquals(4, serviceHelper.getCMASActionTypeCode(6365l));
        assertEquals(0, serviceHelper.getCMASActionTypeCode(6361l));
    }



}
