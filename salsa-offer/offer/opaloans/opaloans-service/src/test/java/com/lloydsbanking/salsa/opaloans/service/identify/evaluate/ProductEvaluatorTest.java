package com.lloydsbanking.salsa.opaloans.service.identify.evaluate;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.offer.LegalEntityMapUtility;
import lib_sim_bo.businessobjects.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class ProductEvaluatorTest {
    ProductEvaluator productEvaluator;

    @Before
    public void setUp() {
        productEvaluator = new ProductEvaluator();
    }

    @Test
    public void testGetBrandSpecificProductsForVerdeSwitchOn() {
        List<Product> allProductHoldings = new ArrayList();
        Product product = new Product();
        product.setProductIdentifier("1");
        product.setBrandName("LTB");
        Product product1 = new Product();
        product1.setProductIdentifier("2");
        product1.setBrandName("VTB");
        allProductHoldings.add(product);
        allProductHoldings.add(product1);
        Map<String, String> legalEntityMap = new HashMap<>();
        legalEntityMap.put("LTB", "LTB");
        legalEntityMap.put("VTB", "VER");
        LegalEntityMapUtility.setLegalEntityMap(legalEntityMap);
        List<Product> brandSpecificProducts = productEvaluator.getBrandSpecificProducts(allProductHoldings, "LTB", true);

        assertEquals(2, brandSpecificProducts.size());
        assertEquals("1", brandSpecificProducts.get(0).getProductIdentifier());
        assertEquals("2", brandSpecificProducts.get(1).getProductIdentifier());
    }

    @Test
    public void testGetBrandSpecificProductsForVerdeSwitchOff() {
        List<Product> allProductHoldings = new ArrayList();
        Product product = new Product();
        product.setProductIdentifier("1");
        product.setBrandName("LTB");
        Product product1 = new Product();
        product1.setProductIdentifier("2");
        product1.setBrandName("VTB");
        allProductHoldings.add(product);
        allProductHoldings.add(product1);
        Map<String, String> legalEntityMap = new HashMap<>();
        legalEntityMap.put("LTB", "LTB");
        legalEntityMap.put("VTB", "VER");
        LegalEntityMapUtility.setLegalEntityMap(legalEntityMap);
        List<Product> brandSpecificProducts = productEvaluator.getBrandSpecificProducts(allProductHoldings, "LTB", false);

        assertEquals(1, brandSpecificProducts.size());
        assertEquals("1", brandSpecificProducts.get(0).getProductIdentifier());
    }

    @Test
    public void testGetBrandSpecificProductsForBrandVerde() {
        List<Product> allProductHoldings = new ArrayList();
        Product product = new Product();
        product.setProductIdentifier("1");
        product.setBrandName("LTB");
        Product product1 = new Product();
        product1.setProductIdentifier("2");
        product1.setBrandName("VTB");
        allProductHoldings.add(product);
        allProductHoldings.add(product1);
        Map<String, String> legalEntityMap = new HashMap<>();
        legalEntityMap.put("LTB", "LTB");
        legalEntityMap.put("VTB", "VER");
        LegalEntityMapUtility.setLegalEntityMap(legalEntityMap);
        List<Product> brandSpecificProducts = productEvaluator.getBrandSpecificProducts(allProductHoldings, "VER", true);

        assertEquals(1, brandSpecificProducts.size());
        assertEquals("2", brandSpecificProducts.get(0).getProductIdentifier());
    }

    @Test
    public void testGetBrandSpecificProductsForBrandVerdeAndVerdeSwitchOff() {
        List<Product> allProductHoldings = new ArrayList();
        Product product = new Product();
        product.setProductIdentifier("1");
        product.setBrandName("LTB");
        Product product1 = new Product();
        product1.setProductIdentifier("2");
        product1.setBrandName("VTB");
        allProductHoldings.add(product);
        allProductHoldings.add(product1);
        Map<String, String> legalEntityMap = new HashMap<>();
        legalEntityMap.put("LTB", "LTB");
        legalEntityMap.put("VTB", "VER");
        LegalEntityMapUtility.setLegalEntityMap(legalEntityMap);
        List<Product> brandSpecificProducts = productEvaluator.getBrandSpecificProducts(allProductHoldings, "VER", false);

        assertEquals(1, brandSpecificProducts.size());
        assertEquals("2", brandSpecificProducts.get(0).getProductIdentifier());
    }

    @Test
    public void testIsVerdeProductForVerdeSwitchOff(){
        Product product = new Product();
        product.setProductIdentifier("1");
        product.setBrandName("VER");
        boolean result = productEvaluator.isVerdeProduct(product, "VER", false);

        assertFalse(result);
    }

    @Test
    public void testIsVerdeProductForVerdeSwitchOn(){
        Product product = new Product();
        product.setProductIdentifier("1");
        product.setBrandName("VER");
        boolean result = productEvaluator.isVerdeProduct(product, "LTB", true);

        assertFalse(result);
    }

    @Test
    public void testIsVerdeProductForVerdeSwitchOnAndBrandVerde(){
        Product product = new Product();
        product.setProductIdentifier("1");
        product.setBrandName("LTB");
        Map<String, String> legalEntityMap = new HashMap<>();
        legalEntityMap.put("LTB", "LTB");
        legalEntityMap.put("VTB", "VER");
        LegalEntityMapUtility.setLegalEntityMap(legalEntityMap);
        boolean result = productEvaluator.isVerdeProduct(product, "VER", true);

        assertFalse(result);
    }

    @Test
    public void testIsVerdeProductForVerdeSwitchOnProductBrandCodeVerdeAndBrandVerde(){
        Product product = new Product();
        product.setProductIdentifier("1");
        product.setBrandName("VTB");
        Map<String, String> legalEntityMap = new HashMap<>();
        legalEntityMap.put("LTB", "LTB");
        legalEntityMap.put("VTB", "VER");
        LegalEntityMapUtility.setLegalEntityMap(legalEntityMap);
        boolean result = productEvaluator.isVerdeProduct(product, "LTB", true);

        assertTrue(result);
    }

}
