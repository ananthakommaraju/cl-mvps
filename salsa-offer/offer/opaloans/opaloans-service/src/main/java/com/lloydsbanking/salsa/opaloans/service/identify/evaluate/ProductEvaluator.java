package com.lloydsbanking.salsa.opaloans.service.identify.evaluate;

import com.lloydsbanking.salsa.offer.LegalEntityMapUtility;
import lib_sim_bo.businessobjects.Product;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductEvaluator {
    private static final Logger LOGGER = Logger.getLogger(ProductEvaluator.class);

    private static final String BRAND_VER = "VER";

    public List<Product> getBrandSpecificProducts(List<Product> allProductHoldings, String brand, boolean isVerdeSwitchOn) {
        LOGGER.info("Offer: ProductList: " + allProductHoldings + " and channelID: " + brand);
        List<Product> brandSpecificProducts = new ArrayList<>();
        for (Product product : allProductHoldings) {
            String productActualBrand = LegalEntityMapUtility.getLegalEntityMap().get(product.getBrandName());
            LOGGER.info("Offer: productActualBrand: " + productActualBrand);
            if ((productActualBrand != null && productActualBrand.equals(brand)) || (isVerdeSwitchOn && BRAND_VER.equals(productActualBrand))) {
                brandSpecificProducts.add(product);
            }
        }
        return brandSpecificProducts;
    }

    public boolean isVerdeProduct(Product product, String brand, boolean isVerdeSwitchOn) {
        //TODO Only brandSpecificProducts are populated by ProductPartyDataToProductConverter. Verde Products may bot be present.
        if (isVerdeSwitchOn && !BRAND_VER.equals(brand)) {
            String brandCode = LegalEntityMapUtility.getLegalEntityMap().get(product.getBrandName());
            if (BRAND_VER.equals(brandCode)) {
                return true;
            }
        }
        return false;
    }

}
