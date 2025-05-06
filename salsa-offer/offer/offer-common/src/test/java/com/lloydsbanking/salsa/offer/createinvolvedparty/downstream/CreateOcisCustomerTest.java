package com.lloydsbanking.salsa.offer.createinvolvedparty.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ocis.client.f062.F062Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.offer.createinvolvedparty.convert.CreateOcisCustomerRequestFactory;
import com.lloydsbanking.salsa.offer.createinvolvedparty.errorcode.RetrieveOcisErrorMap;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Req;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Resp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_gmo.exception.ExternalBusinessErrorMsg;
import lib_sim_gmo.exception.ExternalServiceErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.WebServiceException;
import java.text.ParseException;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CreateOcisCustomerTest {

    @Autowired
    private CreateOcisCustomer createOcisCustomer = new CreateOcisCustomer();

    @Before
    public void setUp() {
        createOcisCustomer.errorMapOcis = new RetrieveOcisErrorMap();
        createOcisCustomer.exceptionUtility = new ExceptionUtility();
        createOcisCustomer.headerRetriever = new HeaderRetriever();
        createOcisCustomer.createOcisCustomerRequestFactory = mock(CreateOcisCustomerRequestFactory.class);
        createOcisCustomer.f062Client = mock(F062Client.class);
    }

    @Test
    public void createSuccessfulTest() throws ExternalBusinessErrorMsg, ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg {
        TestDataHelper dataHelper = new TestDataHelper();
        DepositArrangement depositArrangement = dataHelper.createDepositArrangement();
        when(createOcisCustomer.f062Client.updateCustomerRecord(any(F062Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(dataHelper.createF062Response(0));
        F062Resp f062Resp = createOcisCustomer.create("CA", depositArrangement.getPrimaryInvolvedParty(), dataHelper.createOpaPcaRequestHeader("LTB"), true);
        assertEquals("cidPersId", f062Resp.getCIDPersId());
        assertEquals(345, f062Resp.getPartyId());
    }

    @Test(expected = InternalServiceErrorMsg.class)
    public void createTestInternalServiceError() throws ExternalBusinessErrorMsg, ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, ParseException {
        TestDataHelper dataHelper = new TestDataHelper();
        DepositArrangement depositArrangement = dataHelper.createDepositArrangement();
        when(createOcisCustomer.createOcisCustomerRequestFactory.convert("CA", depositArrangement.getPrimaryInvolvedParty(), true, "LTB")).thenThrow(ParseException.class);
        createOcisCustomer.create("CA", depositArrangement.getPrimaryInvolvedParty(), dataHelper.createOpaPcaRequestHeader("LTB"), true);
    }

    @Test(expected = ResourceNotAvailableErrorMsg.class)
    public void createTestResourceNotAvailableErrorMsg() throws ExternalBusinessErrorMsg, ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, ParseException {
        TestDataHelper dataHelper = new TestDataHelper();
        DepositArrangement depositArrangement = dataHelper.createDepositArrangement();
        when(createOcisCustomer.f062Client.updateCustomerRecord(any(F062Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenThrow(WebServiceException.class);
        createOcisCustomer.create("CA", depositArrangement.getPrimaryInvolvedParty(), dataHelper.createOpaPcaRequestHeader("LTB"), true);
    }

    @Test(expected = ExternalServiceErrorMsg.class)
    public void createTestExternalServiceError() throws ExternalBusinessErrorMsg, ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, ParseException {
        TestDataHelper dataHelper = new TestDataHelper();
        DepositArrangement depositArrangement = dataHelper.createDepositArrangement();
        F062Resp f062Resp = dataHelper.createF062Response(163002);
        when(createOcisCustomer.f062Client.updateCustomerRecord(any(F062Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f062Resp);
        createOcisCustomer.create("CA", depositArrangement.getPrimaryInvolvedParty(), dataHelper.createOpaPcaRequestHeader("LTB"), true);
    }

    @Test(expected = ExternalBusinessErrorMsg.class)
    public void createTestExternalBusinessError() throws ExternalBusinessErrorMsg, ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, ParseException {
        TestDataHelper dataHelper = new TestDataHelper();
        DepositArrangement depositArrangement = dataHelper.createDepositArrangement();
        F062Resp f062Resp = dataHelper.createF062Response(161031);
        when(createOcisCustomer.f062Client.updateCustomerRecord(any(F062Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f062Resp);
        createOcisCustomer.create("CA", depositArrangement.getPrimaryInvolvedParty(), dataHelper.createOpaPcaRequestHeader("LTB"), true);
    }

    @Test(expected = ExternalServiceErrorMsg.class)
    public void createTestExternalServiceErrorNotKnown() throws ExternalBusinessErrorMsg, ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, ParseException {
        TestDataHelper dataHelper = new TestDataHelper();
        DepositArrangement depositArrangement = dataHelper.createDepositArrangement();
        F062Resp f062Resp = dataHelper.createF062Response(1);
        when(createOcisCustomer.f062Client.updateCustomerRecord(any(F062Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f062Resp);
        createOcisCustomer.create("CA", depositArrangement.getPrimaryInvolvedParty(), dataHelper.createOpaPcaRequestHeader("LTB"), true);
    }
}
