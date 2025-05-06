package com.lloydsbanking.salsa.offer.apply.downstream;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.logging.application.ProductFamilyTraceLog;
import com.lloydsbanking.salsa.logging.application.ProductTraceLog;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.apply.utility.OfferedProductsSorter;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductOptions;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.mockito.Mockito.*;
@Category(UnitTest.class)
public class ProductConditionsRetrieverTest {
    private ProductConditionsRetriever productConditionsRetriever;

    @Before
    public void setUp() {
        productConditionsRetriever = new ProductConditionsRetriever();
        productConditionsRetriever.rpcRetriever = mock(RpcRetriever.class);
        productConditionsRetriever.offeredProductsSorter = mock(OfferedProductsSorter.class);
        productConditionsRetriever.productTraceLog = mock(ProductTraceLog.class);
        productConditionsRetriever.productFamilyTraceLog = mock(ProductFamilyTraceLog.class);
        when(productConditionsRetriever.productFamilyTraceLog.getProdFamilyListTraceEventMessage(any(List.class), any(String.class))).thenReturn("ProductFamilyList");
    }

    @Test
    public void testRetrieveRPCResponse() throws OfferException, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, DataNotAvailableErrorMsg {
        RetrieveProductConditionsRequest retrieveProductConditionsRequest = new RetrieveProductConditionsRequest();
        RetrieveProductConditionsResponse retrieveProductConditionsResponse = new RetrieveProductConditionsResponse();
        retrieveProductConditionsResponse.getProduct().add(new Product());
        retrieveProductConditionsResponse.getProduct().get(0).setProductIdentifier("1");
        retrieveProductConditionsResponse.getProduct().get(0).getProductoptions().add(new ProductOptions());
        retrieveProductConditionsResponse.getProduct().get(0).getProductoptions().get(0).setOptionsCode("22");
        Product associatedProduct = new Product();
        associatedProduct.setProductIdentifier("100");
        associatedProduct.getProductoptions().add(new ProductOptions());
        associatedProduct.getProductoptions().get(0).setOptionsCode("1");

        when(productConditionsRetriever.rpcRetriever.callRpcService(retrieveProductConditionsRequest)).thenReturn(retrieveProductConditionsResponse);
        when(productConditionsRetriever.productTraceLog.getProdListTraceEventMessage(any(List.class), any(String.class))).thenReturn("Product");
        productConditionsRetriever.retrieveRPCResponse(retrieveProductConditionsRequest, associatedProduct);
        verify(productConditionsRetriever.offeredProductsSorter).getSortedProducts(associatedProduct, retrieveProductConditionsResponse);

    }

    @Test(expected = OfferException.class)
    public void testRetrieveRPCResponseThrowsResourceNotAvaialableException() throws OfferException, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, DataNotAvailableErrorMsg {

        RetrieveProductConditionsRequest retrieveProductConditionsRequest = new RetrieveProductConditionsRequest();
        RetrieveProductConditionsResponse retrieveProductConditionsResponse = new RetrieveProductConditionsResponse();
        retrieveProductConditionsResponse.getProduct().add(new Product());
        retrieveProductConditionsResponse.getProduct().get(0).setProductIdentifier("1");
        retrieveProductConditionsResponse.getProduct().get(0).getProductoptions().add(new ProductOptions());
        retrieveProductConditionsResponse.getProduct().get(0).getProductoptions().get(0).setOptionsCode("22");
        Product associatedProduct = new Product();
        associatedProduct.setProductIdentifier("100");
        associatedProduct.getProductoptions().add(new ProductOptions());
        associatedProduct.getProductoptions().get(0).setOptionsCode("1");

        when(productConditionsRetriever.rpcRetriever.callRpcService(retrieveProductConditionsRequest)).thenThrow(ResourceNotAvailableErrorMsg.class);

        productConditionsRetriever.retrieveRPCResponse(retrieveProductConditionsRequest, associatedProduct);

        verify(productConditionsRetriever.offeredProductsSorter, never()).getSortedProducts(associatedProduct, retrieveProductConditionsResponse);

    }

    @Test(expected = OfferException.class)
    public void testRetrieveRPCResponseThrowsInternalServiceException() throws OfferException, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, DataNotAvailableErrorMsg {

        RetrieveProductConditionsRequest retrieveProductConditionsRequest = new RetrieveProductConditionsRequest();
        RetrieveProductConditionsResponse retrieveProductConditionsResponse = new RetrieveProductConditionsResponse();
        retrieveProductConditionsResponse.getProduct().add(new Product());
        retrieveProductConditionsResponse.getProduct().get(0).setProductIdentifier("1");
        retrieveProductConditionsResponse.getProduct().get(0).getProductoptions().add(new ProductOptions());
        retrieveProductConditionsResponse.getProduct().get(0).getProductoptions().get(0).setOptionsCode("22");
        Product associatedProduct = new Product();
        associatedProduct.setProductIdentifier("100");
        associatedProduct.getProductoptions().add(new ProductOptions());
        associatedProduct.getProductoptions().get(0).setOptionsCode("1");

        when(productConditionsRetriever.rpcRetriever.callRpcService(retrieveProductConditionsRequest)).thenThrow(InternalServiceErrorMsg.class);


        productConditionsRetriever.retrieveRPCResponse(retrieveProductConditionsRequest, associatedProduct);

        verify(productConditionsRetriever.offeredProductsSorter, never()).getSortedProducts(associatedProduct, retrieveProductConditionsResponse);

    }

    @Test(expected = OfferException.class)
    public void testRetrieveRPCResponseThrowsDataNotAvaailableException() throws OfferException, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, DataNotAvailableErrorMsg {

        RetrieveProductConditionsRequest retrieveProductConditionsRequest = new RetrieveProductConditionsRequest();
        RetrieveProductConditionsResponse retrieveProductConditionsResponse = new RetrieveProductConditionsResponse();
        retrieveProductConditionsResponse.getProduct().add(new Product());
        retrieveProductConditionsResponse.getProduct().get(0).setProductIdentifier("1");
        retrieveProductConditionsResponse.getProduct().get(0).getProductoptions().add(new ProductOptions());
        retrieveProductConditionsResponse.getProduct().get(0).getProductoptions().get(0).setOptionsCode("22");
        Product associatedProduct = new Product();
        associatedProduct.setProductIdentifier("100");
        associatedProduct.getProductoptions().add(new ProductOptions());
        associatedProduct.getProductoptions().get(0).setOptionsCode("1");

        when(productConditionsRetriever.rpcRetriever.callRpcService(retrieveProductConditionsRequest)).thenThrow(DataNotAvailableErrorMsg.class);


        productConditionsRetriever.retrieveRPCResponse(retrieveProductConditionsRequest, associatedProduct);

        verify(productConditionsRetriever.offeredProductsSorter, never()).getSortedProducts(associatedProduct, retrieveProductConditionsResponse);

    }

}
