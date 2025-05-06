package com.lloydsbanking.salsa.offer.apply.utility;

import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductOptions;

import java.io.Serializable;
import java.util.Comparator;

public class PriorityComparator implements Comparator<Product>, Serializable {

    private static final String PROD_PRIORITY = "PrdPriority";
    private static final long serialVersionUID = 1;

    int offeredProductPriority1 = 0;
    int offeredProductPriority2 = 0;

    @Override
    public int compare(Product product1, Product product2) {
        offeredProductPriority1 = getOptionValueForPriorityProduct(product1);
        offeredProductPriority2 = getOptionValueForPriorityProduct(product2);
        return offeredProductPriority1 > offeredProductPriority2 ? +1 : offeredProductPriority1 < offeredProductPriority2 ? -1 : 0;
    }

    public int getOptionValueForPriorityProduct(Product product) {
        int currentProductOptionPriority = 0;
        for (ProductOptions productOption : product.getProductoptions()) {
            if (PROD_PRIORITY.equalsIgnoreCase(productOption.getOptionsType())) {
                if (null != productOption.getOptionsValue()) {
                    currentProductOptionPriority = Integer.parseInt(productOption.getOptionsValue());
                }
            }
        }
        return currentProductOptionPriority;
    }

}
