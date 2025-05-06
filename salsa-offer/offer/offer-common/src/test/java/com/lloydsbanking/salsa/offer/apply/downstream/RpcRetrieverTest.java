package com.lloydsbanking.salsa.offer.apply.downstream;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.offer.apply.convert.OfferToRpcRequestConverter;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductFamily;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.faults.DatabaseServiceError;
import lib_sim_gmo.faults.InternalServiceError;
import lib_sim_gmo.faults.ResourceNotAvailableError;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class RpcRetrieverTest {
    private RpcRetriever retriever;
    private RetrieveProductConditionsResponse resp;
    private RetrieveProductConditionsRequest rpcRequest;

    @Before
    public void setUp() {
        retriever = new RpcRetriever();

        retriever.offerToRpcRequestConverter = mock(OfferToRpcRequestConverter.class);
        retriever.rpcServiceClient = mock(RpcServiceClient.class);
        rpcRequest = new RetrieveProductConditionsRequest();
        resp = null;
    }

    @Test
    public void testCallRpcService() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, DataNotAvailableErrorMsg {
        RetrieveProductConditionsRequest rpcRequest = new RetrieveProductConditionsRequest();
        Product product = new Product();
        product.setProductIdentifier("1");
        rpcRequest.setProduct(product);
        RequestHeader requestHeader = new RequestHeader();
        requestHeader.setArrangementId("1");
        rpcRequest.setHeader(requestHeader);
        ProductFamily productFamily = new ProductFamily();
        productFamily.setFamilyDescription("desc");
        productFamily.setFamilyIdentifier("11");
        rpcRequest.getProductFamily().add(productFamily);

        resp = retriever.callRpcService(rpcRequest);

        verify(retriever.rpcServiceClient).retrieveProductConditions(rpcRequest);


    }

    @Test(expected = DataNotAvailableErrorMsg.class)
    public void testCallRpcServiceDataNotAvailableError() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, DataNotAvailableErrorMsg {
        retriever.exceptionUtility = mock(ExceptionUtility.class);
        RetrieveProductConditionsDataNotAvailableErrorMsg errorMsg = mock(RetrieveProductConditionsDataNotAvailableErrorMsg.class);
        when(errorMsg.getFaultInfo()).thenReturn(new DatabaseServiceError());
        when(retriever.rpcServiceClient.retrieveProductConditions(any(RetrieveProductConditionsRequest.class))).thenThrow(errorMsg);
        when(retriever.exceptionUtility.dataNotAvailableError(any(String.class), any(String.class), any(String.class), any(String.class))).thenReturn(new DataNotAvailableErrorMsg());
        retriever.callRpcService(rpcRequest);
    }

    @Test(expected = InternalServiceErrorMsg.class)
    public void testCallRpcServiceInternalServiceError() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, DataNotAvailableErrorMsg {

        retriever.exceptionUtility = mock(ExceptionUtility.class);
        RetrieveProductConditionsInternalServiceErrorMsg errorMsg = mock(RetrieveProductConditionsInternalServiceErrorMsg.class);
        when(errorMsg.getFaultInfo()).thenReturn(new InternalServiceError());
        when(retriever.rpcServiceClient.retrieveProductConditions(any(RetrieveProductConditionsRequest.class))).thenThrow(errorMsg);
        when(retriever.exceptionUtility.internalServiceError(any(String.class), any(String.class))).thenReturn(new InternalServiceErrorMsg());
        retriever.callRpcService(rpcRequest);

    }

    @Test(expected = ResourceNotAvailableErrorMsg.class)
    public void testCallRpcServiceResourceNotAvailableError() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, DataNotAvailableErrorMsg {
        retriever.exceptionUtility = mock(ExceptionUtility.class);
        RetrieveProductConditionsResourceNotAvailableErrorMsg errorMsg = mock(RetrieveProductConditionsResourceNotAvailableErrorMsg.class);
        when(errorMsg.getFaultInfo()).thenReturn(new ResourceNotAvailableError());
        when(retriever.rpcServiceClient.retrieveProductConditions(any(RetrieveProductConditionsRequest.class))).thenThrow(errorMsg);
        when(retriever.exceptionUtility.resourceNotAvailableError(any(String.class))).thenReturn(new ResourceNotAvailableErrorMsg());
        retriever.callRpcService(rpcRequest);

    }


}
