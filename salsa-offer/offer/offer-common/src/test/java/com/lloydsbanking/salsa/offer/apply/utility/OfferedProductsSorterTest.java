package com.lloydsbanking.salsa.offer.apply.utility;

import com.lloydsbanking.salsa.UnitTest;
import junit.framework.TestCase;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductOffer;
import lib_sim_bo.businessobjects.ProductOptions;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

@Category(UnitTest.class)
public class OfferedProductsSorterTest extends TestCase {
    private static final String PROD_PRIORITY = "PrdPriority";
    private static final String OPTION_CODE_FOR_UPSELL = "UP_SELL_DISPLAY_VALUE";

    OfferedProductsSorter offeredProductsSorter;
    RetrieveProductConditionsResponse response;

    @Before
    public void setUp() {
        offeredProductsSorter = new OfferedProductsSorter();
        response = new RetrieveProductConditionsResponse();
    }

    @Test
    public void testGetSortedProductsForDownsell() {
        List<Product> productList = new ArrayList<>();
        productList.add(getProduct("91", "0"));
        productList.add(getProduct("94", "2"));
        productList.add(getProduct("93", "3"));
        productList.add(getProduct("20349", "7"));
        response.getProduct().addAll(productList);
        Product product = getAssociatedProduct("92", "1");
        offeredProductsSorter.getSortedProducts(product, response);
        assertEquals(3, response.getProduct().size());
        assertEquals("2003", response.getProduct().get(0).getProductoffer().get(0).getOfferType());
        assertEquals("94", response.getProduct().get(0).getProductIdentifier());
        assertEquals("93", response.getProduct().get(1).getProductIdentifier());
        assertEquals("20349", response.getProduct().get(2).getProductIdentifier());
    }

    @Test
    public void testGetSortedProductsForNormal() {
        List<Product> productList = new ArrayList<>();
        productList.add(getProduct("92", "0"));
        productList.add(getProduct("92", "2"));
        productList.add(getProduct("92", "3"));
        productList.add(getProduct("20349", "7"));
        response.getProduct().addAll(productList);
        Product product = getAssociatedProduct("92", "1");
        offeredProductsSorter.getSortedProducts(product, response);
        assertEquals(1, response.getProduct().size());
        assertEquals("2001", response.getProduct().get(0).getProductoffer().get(0).getOfferType());
        assertEquals("92", response.getProduct().get(0).getProductIdentifier());
    }


    @Test
    public void testGetSortedProductsWithEmptyOptionsValue() {
        List<Product> productList = new ArrayList<>();
        productList.add(getProduct("92", ""));
        response.getProduct().addAll(productList);
        Product product = getAssociatedProduct("92", "");
        offeredProductsSorter.getSortedProducts(product, response);
        assertEquals(1, response.getProduct().size());
        assertEquals("2001", response.getProduct().get(0).getProductoffer().get(0).getOfferType());
        assertEquals("92", response.getProduct().get(0).getProductIdentifier());
    }

    @Test
    public void testProductOfferListForOfferType() {
        List<Product> productList = new ArrayList<>();
        productList.add(getProduct("92", "0"));
        productList.add(getProduct("92", "2"));
        productList.add(getProduct("92", "3"));
        productList.add(getProduct("20349", "7"));
        response.getProduct().addAll(productList);
        Product product = getAssociatedProduct("92", "1");
        offeredProductsSorter.getSortedProducts(product, response);
        assertEquals(1, response.getProduct().size());
        assertEquals("2001", response.getProduct().get(0).getProductoffer().get(0).getOfferType());
        assertEquals("92", response.getProduct().get(0).getProductIdentifier());
    }

    @Test
    public void testGetSortedProductsForUpsell() {
        List<Product> productList = new ArrayList<>();
        productList.add(getProduct("94", "0"));
        productList.add(getProduct("91", "2"));
        productList.add(getProduct("92", "3"));
        productList.add(getProduct("20349", "7"));
        response.getProduct().addAll(productList);
        Product product = getAssociatedProduct("92", "1");
        offeredProductsSorter.getSortedProducts(product, response);
        assertEquals(2, response.getProduct().size());
        assertEquals("2002", response.getProduct().get(0).getProductoffer().get(0).getOfferType());
        assertEquals("91", response.getProduct().get(0).getProductIdentifier());
        assertEquals("92", response.getProduct().get(1).getProductIdentifier());
    }

    private static Product getProduct(String id, String val) {
        Product product = new Product();
        product.setProductIdentifier(id);
        ProductOptions productOptions = new ProductOptions();
        productOptions.setOptionsType(PROD_PRIORITY);
        productOptions.setOptionsValue(val);
        product.getProductoptions().add(productOptions);
        product.getProductoffer().add(new ProductOffer());
        return product;
    }

    private static Product getAssociatedProduct(String id, String val) {
        Product product = new Product();
        product.setProductIdentifier(id);
        ProductOptions productOptions = new ProductOptions();
        productOptions.setOptionsCode(OPTION_CODE_FOR_UPSELL);
        productOptions.setOptionsValue(val);
        product.getProductoptions().add(productOptions);
        return product;
    }


}
