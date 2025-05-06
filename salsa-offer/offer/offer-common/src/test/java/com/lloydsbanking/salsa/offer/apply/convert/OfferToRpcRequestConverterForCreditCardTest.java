package com.lloydsbanking.salsa.offer.apply.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import lib_sim_bo.businessobjects.*;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class OfferToRpcRequestConverterForCreditCardTest {

    private OfferToRpcRequestConverterForCreditCard converter;
    private TestDataHelper testDataHelper;
    private RetrieveProductConditionsRequest rpcRequest;

    @Before
    public void setUp() {
        converter = new OfferToRpcRequestConverterForCreditCard();
        testDataHelper = new TestDataHelper();
        rpcRequest = null;
    }

    @Test
    public void testConvertOfferToRpcRequestForCreditCard() {
        List<ProductFamily> productFamilyList = new ArrayList<>();
        ProductFamily productFamily = new ProductFamily();
        Product product = new Product();
        ProductOffer productOffer = new ProductOffer();
        PricePoint pricePoint = new PricePoint();
        pricePoint.setExternalSystemIdentifier("12345");
        productOffer.getPricepoint().add(pricePoint);
        product.getProductoffer().add(productOffer);
        productFamily.getProductFamily().add(product);

        productFamilyList.add(productFamily);

        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        financeServiceArrangement.setAssociatedProduct(new Product());
        financeServiceArrangement.getAssociatedProduct().setProductPropositionIdentifier("236");
        ProductOffer productOffer1 = new ProductOffer();
        financeServiceArrangement.getAssociatedProduct().getProductoffer().add(productOffer1);
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).setOfferType("offerType");
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).setProdOfferIdentifier("896");
        financeServiceArrangement.setApplicationStatus("1005");

        rpcRequest = converter.convertOfferToRpcRequestForCreditCard(testDataHelper.createOpaccRequestHeader("LTB"), productFamilyList, financeServiceArrangement);

        assertEquals("LTB", rpcRequest.getHeader().getChannelId());
        assertEquals("236", rpcRequest.getProduct().getProductPropositionIdentifier());
        assertEquals("offerType", rpcRequest.getProduct().getProductoffer().get(0).getOfferType());
        assertEquals("896", rpcRequest.getProduct().getProductoffer().get(0).getProdOfferIdentifier());

        financeServiceArrangement.setApplicationStatus("1003");
        rpcRequest = converter.convertOfferToRpcRequestForCreditCard(testDataHelper.createOpaccRequestHeader("IBL"), productFamilyList, financeServiceArrangement);

        assertEquals("12345", rpcRequest.getProductFamily().get(0).getProductFamily().get(0).getProductoffer().get(0).getPricepoint().get(0).getExternalSystemIdentifier());

        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setAmount(new BigDecimal(236));
        productFamilyList.get(0).getProductFamily().get(0).getProductoffer().get(0).setOfferAmount(currencyAmount);

        rpcRequest = converter.convertOfferToRpcRequestForCreditCard(testDataHelper.createOpaccRequestHeader("IBL"), productFamilyList, financeServiceArrangement);

        assertEquals(new BigDecimal(236), rpcRequest.getProductFamily().get(0).getProductFamily().get(0).getProductoffer().get(0).getOfferAmount().getAmount());

    }
    @Test
    public void testConvertOfferToRpcRequestForCreditCardOfferAmtExists(){
        List<ProductFamily> productFamilyList = new ArrayList<>();
        ProductFamily productFamily = new ProductFamily();
        Product product = new Product();
        ProductOffer productOffer = new ProductOffer();
        PricePoint pricePoint = new PricePoint();
        pricePoint.setExternalSystemIdentifier("");
        productOffer.getPricepoint().add(pricePoint);
        product.getProductoffer().add(productOffer);
        productFamily.getProductFamily().add(product);

        productFamilyList.add(productFamily);
        productFamilyList.get(0).getProductFamily().get(0).getProductoffer().get(0).setOfferAmount(new CurrencyAmount());
        productFamilyList.get(0).getProductFamily().get(0).getProductoffer().get(0).getOfferAmount().setAmount(new BigDecimal("100"));


        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        financeServiceArrangement.setAssociatedProduct(new Product());
        financeServiceArrangement.getAssociatedProduct().setProductPropositionIdentifier("236");
        ProductOffer productOffer1 = new ProductOffer();
        financeServiceArrangement.getAssociatedProduct().getProductoffer().add(productOffer1);
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).setOfferType("offerType");
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).setProdOfferIdentifier("896");
        financeServiceArrangement.setApplicationStatus("1001");
        rpcRequest = converter.convertOfferToRpcRequestForCreditCard(testDataHelper.createOpaccRequestHeader("LTB"), productFamilyList, financeServiceArrangement);

        assertEquals(new BigDecimal("100"),rpcRequest.getProduct().getProductoffer().get(0).getOfferAmount().getAmount());
    }
    @Test
    public void testConvertOfferToRpcRequestForCreditCardOfferAmtDoNotExist(){
        List<ProductFamily> productFamilyList = new ArrayList<>();
        ProductFamily productFamily = new ProductFamily();
        Product product = new Product();
        ProductOffer productOffer = new ProductOffer();
        PricePoint pricePoint = new PricePoint();
        pricePoint.setExternalSystemIdentifier("");
        productOffer.getPricepoint().add(pricePoint);
        product.getProductoffer().add(productOffer);
        productFamily.getProductFamily().add(product);

        productFamilyList.add(productFamily);
        productFamilyList.get(0).getProductFamily().get(0).getProductoffer().get(0).setOfferAmount(null);


        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        financeServiceArrangement.setAssociatedProduct(new Product());
        financeServiceArrangement.getAssociatedProduct().setProductPropositionIdentifier("236");
        ProductOffer productOffer1 = new ProductOffer();
        financeServiceArrangement.getAssociatedProduct().getProductoffer().add(productOffer1);
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).setOfferType("offerType");
        financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).setProdOfferIdentifier("896");
        financeServiceArrangement.setApplicationStatus("1001");
        financeServiceArrangement.getAssociatedProduct().setProductPropositionIdentifier("1");
        rpcRequest = converter.convertOfferToRpcRequestForCreditCard(testDataHelper.createOpaccRequestHeader("LTB"), productFamilyList, financeServiceArrangement);
        assertEquals("1",rpcRequest.getProduct().getProductPropositionIdentifier());

    }
}
