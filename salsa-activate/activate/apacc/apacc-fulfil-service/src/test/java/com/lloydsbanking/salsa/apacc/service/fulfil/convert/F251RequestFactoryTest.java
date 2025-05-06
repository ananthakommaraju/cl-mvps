package com.lloydsbanking.salsa.apacc.service.fulfil.convert;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apacc.TestDataHelper;
import com.lloydsbanking.salsa.soap.fdi.f251.objects.F251Req;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductAttributes;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertEquals;

@Category(UnitTest.class)
public class F251RequestFactoryTest {
    F251RequestFactory f251RequestFactory;
    F251Req f251Req;
    FinanceServiceArrangement financeServiceArrangement;
    TestDataHelper testDataHelper;
    Product product;
    @Before
    public void setUp() {
        f251RequestFactory = new F251RequestFactory();
        f251Req = new F251Req();
        testDataHelper = new TestDataHelper();
        financeServiceArrangement = testDataHelper.createFinanceServiceArrangement();
        product = new Product();
    }

    @Test
    public void testConvert() {
        f251Req = f251RequestFactory.convert(financeServiceArrangement,"122");
        assertEquals("4",f251Req.getCommsTypeCd());
        product = financeServiceArrangement.getAssociatedProduct();
        product.getProductoffer().get(0).getProductattributes().add(new ProductAttributes());
        product.getProductoffer().get(0).getProductattributes().get(0).setAttributeType("a");
        product.getProductoffer().get(0).getProductattributes().get(0).setAttributeValue("123");
        assertEquals("NEWACC", f251Req.getOfferData().get(0).getRelationshipOfferId());
        assertEquals("2",f251Req.getOfferData().get(0).getOfferStatusCd());
        assertEquals("122",f251Req.getOfferData().get(0).getAcctUser1Cd());
    }
    @Test
    public void testConvertforNullInitiatedThrough() {
        financeServiceArrangement.setInitiatedThrough(null);
        f251Req = f251RequestFactory.convert(financeServiceArrangement,"122");

        product = financeServiceArrangement.getAssociatedProduct();
        product.getProductoffer().get(0).getProductattributes().add(new ProductAttributes());
        product.getProductoffer().get(0).getProductattributes().get(0).setAttributeType("a");
        product.getProductoffer().get(0).getProductattributes().get(0).setAttributeValue("123");
        assertEquals("NEWACC",f251Req.getOfferData().get(0).getRelationshipOfferId());
        assertEquals("2",f251Req.getOfferData().get(0).getOfferStatusCd());
        assertEquals("122",f251Req.getOfferData().get(0).getAcctUser1Cd());
        assertEquals(null,f251Req.getCommsTypeCd());

    }

    @Test
    public void testConvertForChannelCode() {
        financeServiceArrangement.getInitiatedThrough().setChannelCode("11");
        f251Req = f251RequestFactory.convert(financeServiceArrangement,"122");
        product = financeServiceArrangement.getAssociatedProduct();
        product.getProductoffer().get(0).getProductattributes().add(new ProductAttributes());
        product.getProductoffer().get(0).getProductattributes().get(0).setAttributeType("a");
        product.getProductoffer().get(0).getProductattributes().get(0).setAttributeValue("123");
        assertEquals("NEWACC",f251Req.getOfferData().get(0).getRelationshipOfferId());
        assertEquals("2",f251Req.getOfferData().get(0).getOfferStatusCd());
        assertEquals("122",f251Req.getOfferData().get(0).getAcctUser1Cd());
        assertEquals(null,f251Req.getCommsTypeCd());
    }

}
