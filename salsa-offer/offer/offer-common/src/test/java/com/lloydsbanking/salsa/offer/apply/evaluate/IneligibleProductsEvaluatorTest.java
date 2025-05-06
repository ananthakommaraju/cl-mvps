package com.lloydsbanking.salsa.offer.apply.evaluate;


import com.lloydsbanking.salsa.UnitTest;
import lib_sim_bo.businessobjects.*;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
@Category(UnitTest.class)
public class IneligibleProductsEvaluatorTest {

    private IneligibleProductsEvaluator ineligibleProductsEvaluator;

    @Before
    public void setUp() {
        ineligibleProductsEvaluator = new IneligibleProductsEvaluator();
    }

    @Test
    public void testWhenEligibilityRespIsNull() {
        DetermineEligibleCustomerInstructionsResponse eligibilityResponse = null;

        List<Product> rpcProdList = new ArrayList<>();
        Product rpcProd1 = new Product();
        rpcProd1.setProductIdentifier("1");
        ProductOffer prdOffer1 = new ProductOffer();
        prdOffer1.setOfferType("2003");
        rpcProd1.getProductoffer().add(prdOffer1);
        rpcProdList.add(rpcProd1);

        Product rpcProd2 = new Product();
        rpcProd2.setProductIdentifier("2");
        rpcProdList.add(rpcProd2);

        Product rpcProd3 = new Product();
        rpcProd3.setProductIdentifier("3");
        rpcProdList.add(rpcProd3);

        List<Product> productList = ineligibleProductsEvaluator.filterIneligibleProducts(rpcProdList, eligibilityResponse);

        assertEquals(1, productList.size());
        assertEquals("2003", productList.get(0).getProductoffer().get(0).getOfferType());

    }

    @Test
    public void testWhenEligibilityRespIsNullAndRpcHasOneProdOnlyWithOfferTypeUpsell() {
        DetermineEligibleCustomerInstructionsResponse eligibilityResponse = null;

        List<Product> rpcProdList = new ArrayList<>();
        Product rpcProd1 = new Product();
        rpcProd1.setProductIdentifier("1");
        ProductOffer prdOffer1 = new ProductOffer();
        prdOffer1.setOfferType("2002");
        rpcProd1.getProductoffer().add(prdOffer1);
        rpcProdList.add(rpcProd1);

        List<Product> productList = ineligibleProductsEvaluator.filterIneligibleProducts(rpcProdList, eligibilityResponse);

        assertEquals(1, productList.size());
        assertEquals(2, productList.get(0).getProductoffer().size());
        assertEquals("2002", productList.get(0).getProductoffer().get(0).getOfferType());
        assertEquals("2001", productList.get(0).getProductoffer().get(1).getOfferType());

    }

    @Test
    public void testWhenEligibilityResponseIsPresentAndInsMnemonicDoesNotMatch() {
        DetermineEligibleCustomerInstructionsResponse eligibilityResponse = new DetermineEligibleCustomerInstructionsResponse();

        ProductEligibilityDetails productEligDetail1 = new ProductEligibilityDetails();
        productEligDetail1.setIsEligible("yes");
        eligibilityResponse.getProductEligibilityDetails().add(productEligDetail1);

        ProductEligibilityDetails productEligDetail2 = new ProductEligibilityDetails();
        productEligDetail2.setIsEligible("yes");
        eligibilityResponse.getProductEligibilityDetails().add(productEligDetail2);

        ProductEligibilityDetails productEligDetail3 = new ProductEligibilityDetails();
        productEligDetail3.setIsEligible("false");
        Product product1 = new Product();
        InstructionDetails instructionDetails = new InstructionDetails();
        instructionDetails.setInstructionMnemonic("insMnemonic");
        product1.setInstructionDetails(instructionDetails);
        productEligDetail3.getProduct().add(product1);
        eligibilityResponse.getProductEligibilityDetails().add(productEligDetail3);

        //....................

        List<Product> rpcProdList = new ArrayList<>();
        Product rpcProd1 = new Product();
        rpcProd1.setProductIdentifier("1");
        ProductOffer prdOffer1 = new ProductOffer();
        prdOffer1.setOfferType("2003");
        rpcProd1.getProductoffer().add(prdOffer1);

        ExtSysProdIdentifier extSysProdIdentifier1 = new ExtSysProdIdentifier();
        extSysProdIdentifier1.setProductIdentifier("10");
        rpcProd1.getExternalSystemProductIdentifier().add(extSysProdIdentifier1);

        ExtSysProdIdentifier extSysProdIdentifier2 = new ExtSysProdIdentifier();
        extSysProdIdentifier2.setProductIdentifier("20");
        extSysProdIdentifier2.setSystemCode("00010");
        rpcProd1.getExternalSystemProductIdentifier().add(extSysProdIdentifier2);

        rpcProdList.add(rpcProd1);

        Product rpcProd2 = new Product();
        rpcProd2.setProductIdentifier("2");

        ExtSysProdIdentifier extSysProdIdentifier3 = new ExtSysProdIdentifier();
        extSysProdIdentifier3.setProductIdentifier("30");
        rpcProd2.getExternalSystemProductIdentifier().add(extSysProdIdentifier3);

        rpcProdList.add(rpcProd2);

        Product rpcProd3 = new Product();
        rpcProd3.setProductIdentifier("3");
        rpcProdList.add(rpcProd3);


        List<Product> productList = ineligibleProductsEvaluator.filterIneligibleProducts(rpcProdList, eligibilityResponse);

        assertEquals(1, productList.size());
        assertEquals(1, productList.get(0).getProductoffer().size());
        assertEquals("2003", productList.get(0).getProductoffer().get(0).getOfferType());


    }

    @Test
    public void testWhenEligibilityResponseIsPresentAndInsMnemonicMatches() {
        DetermineEligibleCustomerInstructionsResponse eligibilityResponse = new DetermineEligibleCustomerInstructionsResponse();

        ProductEligibilityDetails productEligDetail1 = new ProductEligibilityDetails();
        productEligDetail1.setIsEligible("yes");
        eligibilityResponse.getProductEligibilityDetails().add(productEligDetail1);

        ProductEligibilityDetails productEligDetail2 = new ProductEligibilityDetails();
        productEligDetail2.setIsEligible("false");

        Product product2 = new Product();
        InstructionDetails instructionDetails2 = new InstructionDetails();
        instructionDetails2.setInstructionMnemonic("15");
        product2.setInstructionDetails(instructionDetails2);
        productEligDetail2.getProduct().add(product2);

        eligibilityResponse.getProductEligibilityDetails().add(productEligDetail2);

        ProductEligibilityDetails productEligDetail3 = new ProductEligibilityDetails();
        productEligDetail3.setIsEligible("false");
        Product product1 = new Product();
        InstructionDetails instructionDetails = new InstructionDetails();
        instructionDetails.setInstructionMnemonic("20");
        product1.setInstructionDetails(instructionDetails);
        productEligDetail3.getProduct().add(product1);
        eligibilityResponse.getProductEligibilityDetails().add(productEligDetail3);


        List<Product> rpcProdList = new ArrayList<>();
        Product rpcProd1 = new Product();
        rpcProd1.setProductIdentifier("1");
        ProductOffer prdOffer1 = new ProductOffer();
        prdOffer1.setOfferType("2003");
        rpcProd1.getProductoffer().add(prdOffer1);

        ExtSysProdIdentifier extSysProdIdentifier1 = new ExtSysProdIdentifier();
        extSysProdIdentifier1.setProductIdentifier("10");
        rpcProd1.getExternalSystemProductIdentifier().add(extSysProdIdentifier1);

        ExtSysProdIdentifier extSysProdIdentifier2 = new ExtSysProdIdentifier();
        extSysProdIdentifier2.setProductIdentifier("15");
        extSysProdIdentifier2.setSystemCode("00010");
        rpcProd1.getExternalSystemProductIdentifier().add(extSysProdIdentifier2);

        rpcProdList.add(rpcProd1);

        Product rpcProd2 = new Product();
        rpcProd2.setProductIdentifier("2");

        ExtSysProdIdentifier extSysProdIdentifier3 = new ExtSysProdIdentifier();
        extSysProdIdentifier3.setProductIdentifier("30");
        rpcProd2.getExternalSystemProductIdentifier().add(extSysProdIdentifier3);

        ProductOffer prdOffer4 = new ProductOffer();
        prdOffer4.setOfferType("2001");
        rpcProd2.getProductoffer().add(prdOffer4);

        rpcProdList.add(rpcProd2);

        Product rpcProd3 = new Product();
        rpcProd3.setProductIdentifier("3");
        rpcProdList.add(rpcProd3);


        List<Product> productList = ineligibleProductsEvaluator.filterIneligibleProducts(rpcProdList, eligibilityResponse);

        assertEquals(2, productList.size());

    }


}
