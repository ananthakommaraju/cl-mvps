package com.lloydsbanking.salsa.apacc.service.fulfil.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apacc.TestDataHelper;
import com.lloydsbanking.salsa.soap.ocis.f259.objects.F259Req;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.ProductAttributes;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category(UnitTest.class)
public class F259RequestFactoryTest {
    F259RequestFactory f259RequestFactory = new F259RequestFactory();

    private TestDataHelper testDataHelper;

    FinanceServiceArrangement financeServiceArrangement;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        financeServiceArrangement = testDataHelper.createFSAForCC(1234);
    }

    @Test
    public void testConvert() {
        F259Req f259Req = f259RequestFactory.convert(financeServiceArrangement, "LTB");
        assertNotNull(f259Req);
        assertNotNull(f259Req.getPartyRoleCodes());
        assertEquals(0, f259Req.getMaxRepeatGroupQy());
        assertEquals(19, f259Req.getExtSysId());
        assertEquals(13, f259Req.getProdExtSysId());
        assertEquals("000", f259Req.getEmbeddedInsCd());
        assertEquals("001", f259Req.getProdHeldStatusCd());
        assertEquals("LTB", f259Req.getSellerLegalEntCd());
        assertEquals("002", f259Req.getPartyRoleCodes().get(0).getProdHeldRoleCd());
    }

    @Test
    public void testConvertForProductAttributes() {
        ProductAttributes productAttributes = new ProductAttributes();
        ProductAttributes productAttributes1 = new ProductAttributes();
        ProductAttributes productAttributes2 = new ProductAttributes();
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().add(productAttributes);
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().add(productAttributes1);
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().add(productAttributes2);
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(0).setAttributeCode("ACC_ORG");
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(1).setAttributeCode("ACC_LOGO");
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(2).setAttributeCode("ACC_BIN");
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(0).setAttributeValue("12");
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(1).setAttributeValue("24");
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().get(2).setAttributeValue("45");
        financeServiceArrangement.setCreditCardNumber("0005467803750460513");
        F259Req f259Req = f259RequestFactory.convert(financeServiceArrangement, "LTB");
        assertEquals("122445", f259Req.getExtProdIdTx());
        assertEquals("5467803750460513", f259Req.getExtProdHeldIdTx());
        assertEquals("LTB", f259Req.getSellerLegalEntCd());
    }
}
