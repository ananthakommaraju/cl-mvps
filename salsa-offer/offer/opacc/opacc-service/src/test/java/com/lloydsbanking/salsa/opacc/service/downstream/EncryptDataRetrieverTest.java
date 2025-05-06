package com.lloydsbanking.salsa.opacc.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.dp.client.encrypt.EncryptClient;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.opacc.service.TestDataHelper;
import com.lloydsbanking.salsa.soap.encrpyt.objects.EncryptDataRequest;
import com.lloydsbanking.salsa.soap.encrpyt.objects.EncryptDataResponse;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.Header;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.ws.WebServiceException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class EncryptDataRetrieverTest {
    private EncryptDataRetriever retriever;

    @Before
    public void setUp(){
        retriever = new EncryptDataRetriever();
        retriever.exceptionUtility = new ExceptionUtility();
        retriever.headerRetriever = mock(HeaderRetriever.class);
        retriever.encryptClient = mock(EncryptClient.class);

    }

    @Test
    public void testretrieveEncryptDataResponse() throws ResourceNotAvailableErrorMsg {
        EncryptDataRequest request = new EncryptDataRequest();
        RequestHeader header = new RequestHeader();
        EncryptDataResponse response = new EncryptDataResponse();
        when(retriever.headerRetriever.getContactPoint(any(Header.class))).thenReturn(new ContactPoint());
        when(retriever.headerRetriever.getServiceRequest(any(Header.class))).thenReturn(new ServiceRequest());
        when(retriever.headerRetriever.getSecurityHeader(any(Header.class))).thenReturn(new SecurityHeaderType());

        when(retriever.encryptClient.retrieveEncryptData(any(EncryptDataRequest.class),any(ContactPoint.class),any(ServiceRequest.class),any(SecurityHeaderType.class))).thenReturn(response);

        retriever.retrieveEncryptDataResponse(request,header);

    }

    @Test(expected =  ResourceNotAvailableErrorMsg.class)
    public void testretrieveEncryptDataResponseThrowsResourceNotAvailableError() throws ResourceNotAvailableErrorMsg {
        EncryptDataRequest request = new EncryptDataRequest();
        RequestHeader header = new RequestHeader();
        EncryptDataResponse response = new EncryptDataResponse();
        when(retriever.headerRetriever.getContactPoint(any(Header.class))).thenReturn(new ContactPoint());
        when(retriever.headerRetriever.getServiceRequest(any(Header.class))).thenReturn(new ServiceRequest());
        when(retriever.headerRetriever.getSecurityHeader(any(Header.class))).thenReturn(new SecurityHeaderType());

        when(retriever.encryptClient.retrieveEncryptData(any(EncryptDataRequest.class),any(ContactPoint.class),any(ServiceRequest.class),any(SecurityHeaderType.class))).thenThrow(WebServiceException.class);

        retriever.retrieveEncryptDataResponse(request,header);

    }


}
