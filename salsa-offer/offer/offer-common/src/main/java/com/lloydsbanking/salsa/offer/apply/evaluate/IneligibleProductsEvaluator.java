package com.lloydsbanking.salsa.offer.apply.evaluate;


import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductEligibilityDetails;
import lib_sim_bo.businessobjects.ProductOffer;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse;

import java.util.ArrayList;
import java.util.List;

public class IneligibleProductsEvaluator {
    private static final String PROD_PACK_TYPE_NORMAL = "2001";
    private static final String PROD_PACK_TYPE_UPSELL = "2002";
    private static final String PROD_PACK_TYPE_DOWNSELL = "2003";
    private static final String EXTERNAL_SYSTEM_CODE_IB = "00010";

    public List<Product> filterIneligibleProducts(List<Product> products, DetermineEligibleCustomerInstructionsResponse eligibiltyResponse) {

        List<Product> productsReturned = new ArrayList<>();
        List<String> ineligibleProducts = new ArrayList<>();

        boolean chkRes = eligibiltyResponse != null && eligibiltyResponse.getProductEligibilityDetails() != null && !eligibiltyResponse.getProductEligibilityDetails().isEmpty();

        if (chkRes) {
            for (ProductEligibilityDetails productEligibilityDetails : eligibiltyResponse.getProductEligibilityDetails()) {
                if (("false").equalsIgnoreCase(productEligibilityDetails.getIsEligible())) {
                    ineligibleProducts.add(productEligibilityDetails.getProduct().get(0).getInstructionDetails().getInstructionMnemonic());
                }
            }

            for (Product product : products) {
                if (isEligible(ineligibleProducts, product)) {
                    productsReturned.add(product);
                }
            }


        } else {
            productsReturned = products;
        }

        return getFilteredProductsWithOffer(productsReturned);
    }

    private boolean isEligible(List<String> ineligibleProducts, Product product) {
        for (String ineligibleProduct : ineligibleProducts) {
            if (ineligibleProduct!=null && ineligibleProduct.equalsIgnoreCase(getInstructionMnemonic(product.getExternalSystemProductIdentifier()))) {
                return false;
            }
        }
        return true;
    }

    private String getInstructionMnemonic(List<ExtSysProdIdentifier> externalSystemProductIdentifiers) {
        String mnemonic = "";
        for (ExtSysProdIdentifier extSysProdIdentifier : externalSystemProductIdentifiers) {
            if (EXTERNAL_SYSTEM_CODE_IB.equalsIgnoreCase(extSysProdIdentifier.getSystemCode())) {
                mnemonic = extSysProdIdentifier.getProductIdentifier();
            }
        }
        return mnemonic;
    }

    private List<Product> getFilteredProductsWithOffer(List<Product> products) {
        if (!products.isEmpty()) {
            String offerType = products.get(0).getProductoffer().get(0).getOfferType();
            if (products.size() > 1 && PROD_PACK_TYPE_DOWNSELL.equalsIgnoreCase(offerType)) {
                List<Product> productsReturned = new ArrayList<>();
                productsReturned.add(products.get(0));
                return productsReturned;

            } else if (products.size() == 1 && PROD_PACK_TYPE_UPSELL.equalsIgnoreCase(offerType)) {
                return getProductsWithOfferType(products, PROD_PACK_TYPE_NORMAL);
            }
        }

        return products;
    }

    private List<Product> getProductsWithOfferType(List<Product> products, String offerType) {
        for (Product product : products) {
            ProductOffer productOffer = new ProductOffer();
            List<ProductOffer> productOfferList = new ArrayList<>();
            productOffer.setOfferType(offerType);
            productOfferList.add(productOffer);
            product.getProductoffer().addAll(productOfferList);
        }
        return products;
    }

}
