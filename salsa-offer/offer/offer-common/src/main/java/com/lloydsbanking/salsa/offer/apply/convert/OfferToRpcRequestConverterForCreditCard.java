package com.lloydsbanking.salsa.offer.apply.convert;


import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class OfferToRpcRequestConverterForCreditCard {
    private static final String APPLICATION_STATUS_UNSCORED = "1005";

    public RetrieveProductConditionsRequest convertOfferToRpcRequestForCreditCard(RequestHeader requestHeader, List<ProductFamily> productFamily, FinanceServiceArrangement financeServiceArrangement) {

        RetrieveProductConditionsRequest rpcRequest = new RetrieveProductConditionsRequest();

        List<ProductOffer> productOfferList = financeServiceArrangement.getAssociatedProduct().getProductoffer();

        rpcRequest.setHeader(requestHeader);
        Product product = new Product();
        product.setProductPropositionIdentifier(financeServiceArrangement.getAssociatedProduct().getProductPropositionIdentifier());
        ProductOffer productOffer = new ProductOffer();
        if (!CollectionUtils.isEmpty(productOfferList)) {
            productOffer.setOfferType(productOfferList.get(0).getOfferType());
            product.getProductoffer().add(0, productOffer);
        }
        rpcRequest.setProduct(product);

        if (APPLICATION_STATUS_UNSCORED.equals(financeServiceArrangement.getApplicationStatus()) && !CollectionUtils.isEmpty(productOfferList)) {
            if (!CollectionUtils.isEmpty(product.getProductoffer())) {
                rpcRequest.getProduct().getProductoffer().get(0).setProdOfferIdentifier(productOfferList.get(0).getProdOfferIdentifier());
            }
        } else {
            if (productFamily != null && !productFamily.isEmpty() && productFamily.get(0) != null) {
                if (!checkExtSysIdentifier(productFamily)) {
                    rpcRequest.getProductFamily().addAll(productFamily);
                } else if (checkOfferAmount(productFamily)) {
                    CurrencyAmount currencyAmount = new CurrencyAmount();
                    currencyAmount.setAmount(productFamily.get(0).getProductFamily().get(0).getProductoffer().get(0).getOfferAmount().getAmount());
                    rpcRequest.getProduct().getProductoffer().get(0).setOfferAmount(currencyAmount);
                }
            }
        }

        return rpcRequest;
    }

    private boolean checkExtSysIdentifier(List<ProductFamily> productFamily) {
        List<Product> productFamily1 = productFamily.get(0).getProductFamily();
        boolean isProductFamily = productFamily1 == null ||
                productFamily1.isEmpty() || productFamily1.get(0) == null || productFamily1.get(0).getProductoffer() == null;

        boolean isPricePointPresent = isProductFamily || productFamily1.get(0).getProductoffer().get(0).getPricepoint() == null ||
                productFamily1.get(0).getProductoffer().get(0).getPricepoint().get(0).getExternalSystemIdentifier() == null
                || productFamily1.get(0).getProductoffer().get(0).getPricepoint().get(0).getExternalSystemIdentifier().equals("");
        return isPricePointPresent;

    }

    private boolean checkOfferAmount(List<ProductFamily> productFamilyList) {
        List<Product> productFamily1 = productFamilyList.get(0).getProductFamily();
        boolean isProductFamily = productFamily1 == null ||
                productFamily1.isEmpty() || productFamily1.get(0) == null || productFamily1.get(0).getProductoffer() == null;

        boolean checkIsOfferAmountPresent = isProductFamily || productFamily1.get(0).getProductoffer().get(0) == null
                || productFamily1.get(0).getProductoffer().get(0).getOfferAmount() == null;

        return !(checkIsOfferAmountPresent);
    }
}
