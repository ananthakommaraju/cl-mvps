package com.lloydsbanking.salsa.offer.apply.convert;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Resp;
import com.lloydsbanking.salsa.soap.asm.f205.objects.ProductsOffered;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Resp;
import com.lloydsbanking.salsa.soap.asm.f424.objects.ProductOffered;
import lib_sim_bo.businessobjects.ProductFamily;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class AsmResponseToProductFamilyConverterTest {
    private AsmResponseToProductFamilyConverter converter;
    private List<ProductFamily> productFamilyList;

    @Before
    public void setUp() {
        converter = new AsmResponseToProductFamilyConverter();
        productFamilyList = null;
    }

    @Test
    public void creditScoreResponseToProductFamilyConverterTest() {

        F205Resp f205Resp = new F205Resp();
        f205Resp.getProductsOffered().add(new ProductsOffered());
        f205Resp.getProductsOffered().get(0).setCSProductsOfferedCd("1");
        productFamilyList = converter.creditScoreResponseToProductFamilyConverter(f205Resp);

        assertEquals("1", productFamilyList.get(0).getExtsysprodfamilyidentifier().get(0).getProductFamilyIdentifier());
    }

    @Test
    public void testCreditDecisionToProductFamilyConverterTest() {
        F424Resp f424Resp = new F424Resp();
        ProductOffered productOffered = new ProductOffered();
        productOffered.setProductOfferedCd("1");
        productOffered.setProductOfferedAm("100");
        productOffered.setPriceTierCd("200");

        f424Resp.getProductOffered().add(productOffered);

        ProductOffered productOffered1 = new ProductOffered();
        productOffered1.setProductsOfferedCd("2");

        f424Resp.getProductOffered().add(productOffered1);

        productFamilyList = converter.creditDecisionResponseToProductFamilyConverter(f424Resp);

        assertEquals(1l, productFamilyList.get(0).getProductFamily().get(0).getProductoffer().get(0).getOfferAmount().getAmount().longValue());
        assertEquals("200", productFamilyList.get(0).getProductFamily().get(0).getProductoffer().get(0).getPricepoint().get(0).getExternalSystemIdentifier());
        assertEquals("00107", productFamilyList.get(0).getProductFamily().get(0).getProductoffer().get(0).getPricepoint().get(0).getSystemCode());
        assertEquals("1", productFamilyList.get(0).getExtsysprodfamilyidentifier().get(0).getProductFamilyIdentifier());

    }

}
