package com.lloydsbanking.salsa.activate.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.downstream.dp.client.encrypt.EncryptClient;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.encrpyt.objects.*;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.ExtraConditions;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.ws.WebServiceException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class EncryptDataRetrieverTest {

    private EncryptDataRetriever encryptDataRetriever;
    private TestDataHelper testDataHelper;
    private RequestHeader requestHeader;
    private ServiceRequest serviceRequest;
    private ContactPoint contactPoint;
    private SecurityHeaderType securityHeaderType;

    @Before
    public void setUp() {
        encryptDataRetriever = new EncryptDataRetriever();
        testDataHelper = new TestDataHelper();
        requestHeader = testDataHelper.createApaRequestHeader();
        encryptDataRetriever.encryptClient = mock(EncryptClient.class);
        encryptDataRetriever.exceptionUtilityActivate = mock(ExceptionUtilityActivate.class);
        encryptDataRetriever.headerRetriever = new HeaderRetriever();
        serviceRequest = encryptDataRetriever.headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders());
        contactPoint = encryptDataRetriever.headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        securityHeaderType = encryptDataRetriever.headerRetriever.getSecurityHeader(requestHeader);
    }

    @Test
    public void testRetrieveEncryptCardNumber() throws ActivateProductArrangementResourceNotAvailableErrorMsg {
        EncryptDataRequest encryptDataRequest = getEncryptDataRequest();
        EncryptDataResponse encryptDataResponse = getEncryptDataResponse();
        when(encryptDataRetriever.encryptClient.retrieveEncryptData(encryptDataRequest, contactPoint, serviceRequest, securityHeaderType)).thenReturn(encryptDataResponse);
        List<String> creditNumbers = encryptDataRetriever.retrieveEncryptCardNumber(requestHeader, getCreditNumbers(), "10024");
        assertNotNull(creditNumbers);
        assertEquals("DataEncrypt", creditNumbers.get(0));
    }

    @Test(expected = ActivateProductArrangementResourceNotAvailableErrorMsg.class)
    public void testRetrieveEncryptCardNumberWithException() throws ActivateProductArrangementResourceNotAvailableErrorMsg {
        EncryptDataRequest encryptDataRequest = getEncryptDataRequest();
        when(encryptDataRetriever.encryptClient.retrieveEncryptData(encryptDataRequest, contactPoint, serviceRequest, securityHeaderType)).thenThrow(WebServiceException.class);
        when(encryptDataRetriever.exceptionUtilityActivate.resourceNotAvailableError(any(RequestHeader.class), any(String.class))).thenThrow(ActivateProductArrangementResourceNotAvailableErrorMsg.class);
        encryptDataRetriever.retrieveEncryptCardNumber(requestHeader, getCreditNumbers(), "10024");
    }

    @Test
    public void testCallEncryptData() throws ActivateProductArrangementResourceNotAvailableErrorMsg {
        EncryptDataResponse encryptDataResponse = getEncryptDataResponse();
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        financeServiceArrangement.setCreditCardNumber("1089765");
        financeServiceArrangement.setAddOnCreditCardNumber("234598");
        when(encryptDataRetriever.encryptClient.retrieveEncryptData(any(EncryptDataRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(encryptDataResponse);
        encryptDataRetriever.callEncryptData("10024", financeServiceArrangement, requestHeader, new ApplicationDetails(), new ExtraConditions());
        verify(encryptDataRetriever.encryptClient).retrieveEncryptData(any(EncryptDataRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class));
    }

    @Test
    public void testCallEncryptDataForException() {
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        financeServiceArrangement.setCreditCardNumber("1089765");
        financeServiceArrangement.setAddOnCreditCardNumber("234598");
        when(encryptDataRetriever.encryptClient.retrieveEncryptData(any(EncryptDataRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenThrow(WebServiceException.class);
        when(encryptDataRetriever.exceptionUtilityActivate.resourceNotAvailableError(any(RequestHeader.class), any(String.class))).thenThrow(ActivateProductArrangementResourceNotAvailableErrorMsg.class);
        encryptDataRetriever.callEncryptData("10024", financeServiceArrangement, requestHeader, new ApplicationDetails(), new ExtraConditions());

    }

    private EncryptDataRequest getEncryptDataRequest() {
        EncryptDataRequest encryptDataRequest = new EncryptDataRequest();
        Indetails indetails = new Indetails();
        indetails.setIntext("1024578895");
        indetails.setEncryptKey("10024");
        indetails.setEncryptType(EncryptionType.SYMM);
        indetails.setInpEncode("base64");
        encryptDataRequest.getIndetails().add(indetails);
        return encryptDataRequest;
    }

    private EncryptDataResponse getEncryptDataResponse() {
        EncryptDataResponse encryptDataResponse = new EncryptDataResponse();
        Outdetails outDetails = new Outdetails();
        outDetails.setOuttextDetails("DataEncrypt");
        Outdetails outdetails = new Outdetails();
        outdetails.setOuttextDetails("CreditDataEncrypt");
        encryptDataResponse.getOutdetails().add(outDetails);
        encryptDataResponse.getOutdetails().add(outdetails);
        return encryptDataResponse;
    }

    private List<String> getCreditNumbers() {

        List<String> creditNumberList = new ArrayList<>();
        creditNumberList.add("1024578895");
        return creditNumberList;
    }

}

