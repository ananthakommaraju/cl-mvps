package com.lloydsbanking.salsa.offer.apply.utility;


import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductOffer;
import lib_sim_bo.businessobjects.ProductOptions;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import org.apache.cxf.common.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OfferedProductsSorter {

    private static final String PROD_PRIORITY = "PrdPriority";
    private static final String OPTION_CODE_FOR_UPSELL = "UP_SELL_DISPLAY_VALUE";
    private static final String PROD_PACK_TYPE_NORMAL = "2001";
    private static final String PROD_PACK_TYPE_UPSELL = "2002";
    private static final String PROD_PACK_TYPE_DOWNSELL = "2003";

    public void getSortedProducts(Product associatedProduct, RetrieveProductConditionsResponse prdResponse) {

        List<Product> currentProductList = new ArrayList();
        List<Product> packedProductList = new ArrayList();

        List<ProductOptions> productOptions;
        int currentProductOptionPriority = 0;
        int associatedProductPriority = 0;
        int switchValue;
        String productId, offerType;
        boolean isNonZeroPriorityProduct = true;

        List<Product> products = copyProductList(prdResponse.getProduct());

        String associatedProductIdentifier = associatedProduct.getProductIdentifier();
        productOptions = associatedProduct.getProductoptions();
        switchValue = getSwitchValue(productOptions);

        List<Product> zeroPriorityNonMatchingProduct = new ArrayList<>();
        for (Product product : products) {
            productId = product.getProductIdentifier();
            productOptions = product.getProductoptions();

            for (ProductOptions currentProductOptions : productOptions) {
                if (PROD_PRIORITY.equalsIgnoreCase(currentProductOptions.getOptionsType())) {
                    if (!StringUtils.isEmpty(currentProductOptions.getOptionsValue())) {
                        currentProductOptionPriority = Integer.parseInt(currentProductOptions.getOptionsValue());
                    }
                    if (associatedProductIdentifier.equalsIgnoreCase(productId)) {
                        associatedProductPriority = currentProductOptionPriority;
                    }
                }
            }
            if (currentProductOptionPriority == 0) {
                if (productId.equalsIgnoreCase(associatedProductIdentifier)) {
                    currentProductList.add(product);
                    offerType = PROD_PACK_TYPE_NORMAL;
                    prdResponse.getProduct().addAll(getProductsWithOfferType(currentProductList, offerType));
                    isNonZeroPriorityProduct = false;
                    break;
                } else {
                    zeroPriorityNonMatchingProduct.add(product);
                }
            }
        }
        products.removeAll(zeroPriorityNonMatchingProduct);
        if (isNonZeroPriorityProduct) {
            getOfferedProductsForNonZeroPriorityProduct(prdResponse, currentProductList, packedProductList, associatedProductPriority, switchValue, products, associatedProductIdentifier);
        }
    }

    private List<Product> copyProductList(List<Product> prdResponseProductList) {
        List<Product> products = new ArrayList<>();
        int size = prdResponseProductList.size();
        for (int i = 0; i < size; i++) {
            products.add(prdResponseProductList.remove(0));
        }
        return products;
    }

    private void getOfferedProductsForNonZeroPriorityProduct(RetrieveProductConditionsResponse prdResponse, List<Product> currentProductList, List<Product> packedProductList, int associatedProductPriority, int switchValue, List<Product> products, String associatedProductIdentifier) {
        String offerType;
        String productId;
        List<ProductOptions> productOptions;
        int currentProductOptionPriority;
        List<Product> sortedOfferedProducts = new ArrayList<>(products);
        Collections.sort(sortedOfferedProducts, new PriorityComparator());
        currentProductList.addAll(sortedOfferedProducts);

        offerType = getOfferType(currentProductList, packedProductList, associatedProductIdentifier);

        if (PROD_PACK_TYPE_UPSELL.equalsIgnoreCase(offerType)) {
            if (switchValue == 0) {
                offerType = PROD_PACK_TYPE_NORMAL;
                List<Product> packedProductNormalTypeList = new ArrayList<>();
                packedProductNormalTypeList.add(packedProductList.get(packedProductList.size() - 1));
                prdResponse.getProduct().addAll(getProductsWithOfferType(packedProductNormalTypeList, offerType));
            } else {
                List<Product> priorityProductList = new ArrayList();
                for (Product product : packedProductList) {
                    productId = product.getProductIdentifier();
                    productOptions = product.getProductoptions();
                    for (ProductOptions productOptions1 : productOptions) {
                        if (PROD_PRIORITY.equalsIgnoreCase(productOptions1.getOptionsType())) {
                            currentProductOptionPriority = Integer.parseInt(productOptions1.getOptionsValue());
                            if (!(associatedProductPriority == currentProductOptionPriority && !productId.equalsIgnoreCase(associatedProductIdentifier))) {
                                priorityProductList.add(product);
                            }
                        }
                    }
                    for (int l = (priorityProductList.size() - switchValue - 2); l > -1; l--) {
                        priorityProductList.remove(l);
                    }
                }
                prdResponse.getProduct().addAll(getProductsWithOfferType(priorityProductList, offerType));
            }
        } else if (PROD_PACK_TYPE_DOWNSELL.equalsIgnoreCase(offerType)) {
            prdResponse.getProduct().addAll(getProductsWithOfferType(currentProductList, offerType));
        } else {
            prdResponse.getProduct().addAll(getProductsWithOfferType(packedProductList, offerType));
        }
    }

    private String getOfferType(List<Product> currentProductList, List<Product> packedProductList, String actProdId) {
        String offerType = null;
        String productId;
        for (Product product : currentProductList) {
            productId = (product.getProductIdentifier());
            int indexOfCurrentProduct = currentProductList.indexOf(product);
            if (indexOfCurrentProduct == 0 && productId.equalsIgnoreCase(actProdId)) {
                packedProductList.add(currentProductList.get(indexOfCurrentProduct));
                offerType = PROD_PACK_TYPE_NORMAL;
                break;
            } else if (indexOfCurrentProduct > 0 && productId.equalsIgnoreCase(actProdId)) {
                for (int counter = 0; counter < indexOfCurrentProduct + 1; counter++) {
                    packedProductList.add(currentProductList.get(counter));
                }
                offerType = PROD_PACK_TYPE_UPSELL;
                break;
            } else {
                offerType = PROD_PACK_TYPE_DOWNSELL;
            }
        }
        return offerType;
    }

    private int getSwitchValue(List<ProductOptions> productOptions) {
        int switchValue = 0;
        for (ProductOptions productOption : productOptions) {
            if (OPTION_CODE_FOR_UPSELL.equalsIgnoreCase(productOption.getOptionsCode())) {
                if (!StringUtils.isEmpty(productOption.getOptionsValue())) {
                    switchValue = Integer.parseInt(productOption.getOptionsValue());
                }
            }
        }
        return switchValue;
    }

    private List<Product> getProductsWithOfferType(List<Product> products, String offerType) {
        for (Product product : products) {
            List<ProductOffer> productOffer = new ArrayList<>();
            ProductOffer prodOffer = new ProductOffer();
            prodOffer.setOfferType(offerType);
            productOffer.add(prodOffer);
            product.getProductoffer().clear();
            product.getProductoffer().addAll(productOffer);
        }
        return products;
    }
}