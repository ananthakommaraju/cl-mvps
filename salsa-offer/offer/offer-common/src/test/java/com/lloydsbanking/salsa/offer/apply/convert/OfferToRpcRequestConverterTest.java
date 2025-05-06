package com.lloydsbanking.salsa.offer.apply.convert;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Resp;
import lib_sim_bo.businessobjects.ExtSysProdFamilyIdentifier;
import lib_sim_bo.businessobjects.ProductFamily;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class OfferToRpcRequestConverterTest {

    private OfferToRpcRequestConverter converter;
    private RetrieveProductConditionsRequest rpcReq;

    @Before
    public void setUp() {
        converter = new OfferToRpcRequestConverter();
        rpcReq = null;
        converter.asmResponseToProductFamilyConverter = mock(AsmResponseToProductFamilyConverter.class);
    }

    @Test
    public void testConvertOfferToRpcRequest() {
        F205Resp f205Resp = new F205Resp();
        RequestHeader requestHeader = new RequestHeader();
        requestHeader.setArrangementId("1");
        List<ProductFamily> productFamilyList = new ArrayList<>();
        ProductFamily productFamily = new ProductFamily();
        productFamily.setFamilyIdentifier("id");
        productFamily.setFamilyDescription("desc");
        productFamilyList.add(productFamily);
        productFamilyList.get(0).getExtsysprodfamilyidentifier().add(new ExtSysProdFamilyIdentifier());
        productFamilyList.get(0).getExtsysprodfamilyidentifier().get(0).setSystemCode("sysCode");
        when(converter.asmResponseToProductFamilyConverter.creditScoreResponseToProductFamilyConverter(any(F205Resp.class))).thenReturn(productFamilyList);
        rpcReq = converter.convertOfferToRpcRequest(f205Resp, requestHeader);

        assertEquals("desc", rpcReq.getProductFamily().get(0).getFamilyDescription());
        assertEquals("id", rpcReq.getProductFamily().get(0).getFamilyIdentifier());


    }

    @Test
    public void testConvertOfferTorpcRequestWhenProdFamilyIsNull() {

        RequestHeader requestHeader = new RequestHeader();
        requestHeader.setArrangementId("1");

        F205Resp f205Resp = new F205Resp();
        rpcReq = converter.convertOfferToRpcRequest(f205Resp, requestHeader);

        assertEquals(0, rpcReq.getProductFamily().size());

    }


}
