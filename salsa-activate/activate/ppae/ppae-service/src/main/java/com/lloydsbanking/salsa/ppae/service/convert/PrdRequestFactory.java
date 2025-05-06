package com.lloydsbanking.salsa.ppae.service.convert;


import com.lloydsbanking.salsa.constant.ArrangementType;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class PrdRequestFactory {

    private static final Logger LOGGER = Logger.getLogger(PrdRequestFactory.class);

    private static final String IS_ACCEPTED = "isAccepted";


    public RetrieveProductConditionsRequest convert(ProductArrangement productArrangement, RequestHeader requestHeader) {
        RetrieveProductConditionsRequest retrieveProductConditionsRequest = new RetrieveProductConditionsRequest();
        retrieveProductConditionsRequest.setHeader(requestHeader);
        retrieveProductConditionsRequest.setProduct(createProductForPrdRequest(productArrangement));

        if (!(productArrangement.getArrangementType().equals(ArrangementType.SAVINGS.getValue()) || hasProductOffer(retrieveProductConditionsRequest))) {
            productArrangement.getAssociatedProduct().getProductoffer().clear();
            productArrangement.getAssociatedProduct().getProductoffer().addAll(retrieveProductConditionsRequest.getProduct().getProductoffer());
        }
        return retrieveProductConditionsRequest;
    }

    private boolean hasProductOffer(RetrieveProductConditionsRequest retrieveProductConditionsRequest) {
        return (null == retrieveProductConditionsRequest.getProduct() || null == retrieveProductConditionsRequest.getProduct().getProductoffer() || retrieveProductConditionsRequest.getProduct().getProductoffer().isEmpty());
    }


    private Product createProductForPrdRequest(ProductArrangement productArrangement) {
        if (productArrangement.getArrangementType().equalsIgnoreCase(ArrangementType.CREDITCARD.getValue())) {
            for (Product offerProduct : productArrangement.getOfferedProducts()) {
                if (null != offerProduct.getStatusCode() && (IS_ACCEPTED).equalsIgnoreCase(offerProduct.getStatusCode())) {
                    return offerProduct;
                }
            }
        } else if (null != productArrangement.getAssociatedProduct() && null != productArrangement.getAssociatedProduct().getProductIdentifier()) {

            return productArrangement.getAssociatedProduct();
        }
        return null;
    }
}
