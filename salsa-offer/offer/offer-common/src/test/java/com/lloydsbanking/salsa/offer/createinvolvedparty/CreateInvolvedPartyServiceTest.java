package com.lloydsbanking.salsa.offer.createinvolvedparty;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.logging.application.CustomerTraceLog;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.offer.createinvolvedparty.convert.AuditDataFactory;
import com.lloydsbanking.salsa.offer.createinvolvedparty.downstream.CreateOcisCustomer;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Resp;
import lib_sim_bo.businessobjects.AuditData;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_gmo.exception.ExternalBusinessErrorMsg;
import lib_sim_gmo.exception.ExternalServiceErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CreateInvolvedPartyServiceTest {

    private CreateInvolvedPartyService service;
    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        service = new CreateInvolvedPartyService();
        testDataHelper = new TestDataHelper();

        service.createOcisCustomer = mock(CreateOcisCustomer.class);
        service.auditFactory = mock(AuditDataFactory.class);
        service.exceptionUtility = mock(ExceptionUtility.class);
        service.customerTraceLog = mock(CustomerTraceLog.class);
        when(service.customerTraceLog.getCustomerTraceEventMessage(any(Customer.class), any(String.class))).thenReturn("Customer");
    }

    @Test
    public void testCreateInvolvedParty() throws OfferException, ExternalBusinessErrorMsg, ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg {

        Customer customer = new Customer();
        customer.setApplicantType("1");

        RequestHeader header = testDataHelper.createOpaPcaRequestHeader("LTB");

        F062Resp f062Resp = new F062Resp();
        f062Resp.setAdditionalDataIn(1);
        f062Resp.setPartyId(12l);
        f062Resp.setCIDPersId("1123");

        List<AuditData> auditDataList = new ArrayList<>();
        AuditData auditData = new AuditData();
        auditData.setAuditTime("1222");
        auditDataList.add(auditData);
        when(service.createOcisCustomer.create(anyString(), any(Customer.class), any(RequestHeader.class), anyBoolean())).thenReturn(f062Resp);
        service.createInvolvedParty("asm", true, customer, header);

        assertEquals("1123", customer.getCidPersID());
        assertEquals("12", customer.getCustomerIdentifier());
    }

    @Test(expected = OfferException.class)
    public void testCreateInvolvedPartythrowsExternalServiceError() throws OfferException, ExternalBusinessErrorMsg, ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg {

        Customer customer = new Customer();
        customer.setApplicantType("1");

        RequestHeader header = testDataHelper.createOpaPcaRequestHeader("LTB");

        F062Resp f062Resp = new F062Resp();
        f062Resp.setAdditionalDataIn(1);
        f062Resp.setPartyId(12l);
        f062Resp.setCIDPersId("1123");

        List<AuditData> auditDataList = new ArrayList<>();
        AuditData auditData = new AuditData();
        auditData.setAuditTime("1222");
        auditDataList.add(auditData);
        when(service.createOcisCustomer.create(anyString(), any(Customer.class), any(RequestHeader.class), anyBoolean())).thenThrow(ExternalServiceErrorMsg.class);
        service.createInvolvedParty("asm", true, customer, header);

    }

    @Test(expected = OfferException.class)
    public void testCreateInvolvedPartythrowsExternalBusinessError() throws OfferException, ExternalBusinessErrorMsg, ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg {

        Customer customer = new Customer();
        customer.setApplicantType("1");

        RequestHeader header = testDataHelper.createOpaPcaRequestHeader("LTB");

        F062Resp f062Resp = new F062Resp();
        f062Resp.setAdditionalDataIn(1);
        f062Resp.setPartyId(12l);
        f062Resp.setCIDPersId("1123");

        List<AuditData> auditDataList = new ArrayList<>();
        AuditData auditData = new AuditData();
        auditData.setAuditTime("1222");
        auditDataList.add(auditData);
        when(service.createOcisCustomer.create(anyString(), any(Customer.class), any(RequestHeader.class), anyBoolean())).thenThrow(ExternalBusinessErrorMsg.class);
        service.createInvolvedParty("asm", true, customer, header);

    }

    @Test(expected = OfferException.class)
    public void testCreateInvolvedPartythrowsInternalServiceError() throws OfferException, ExternalBusinessErrorMsg, ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg {

        Customer customer = new Customer();
        customer.setApplicantType("1");

        RequestHeader header = testDataHelper.createOpaPcaRequestHeader("LTB");

        F062Resp f062Resp = new F062Resp();
        f062Resp.setAdditionalDataIn(1);
        f062Resp.setPartyId(12l);
        f062Resp.setCIDPersId("1123");

        List<AuditData> auditDataList = new ArrayList<>();
        AuditData auditData = new AuditData();
        auditData.setAuditTime("1222");
        auditDataList.add(auditData);
        when(service.createOcisCustomer.create(anyString(), any(Customer.class), any(RequestHeader.class), anyBoolean())).thenThrow(InternalServiceErrorMsg.class);
        service.createInvolvedParty("asm", true, customer, header);

    }

    @Test(expected = OfferException.class)
    public void testCreateInvolvedPartythrowsResourceNotAvailableError() throws OfferException, ExternalBusinessErrorMsg, ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg {

        Customer customer = new Customer();
        customer.setApplicantType("1");

        RequestHeader header = testDataHelper.createOpaPcaRequestHeader("LTB");

        F062Resp f062Resp = new F062Resp();
        f062Resp.setAdditionalDataIn(1);
        f062Resp.setPartyId(12l);
        f062Resp.setCIDPersId("1123");

        List<AuditData> auditDataList = new ArrayList<>();
        AuditData auditData = new AuditData();
        auditData.setAuditTime("1222");
        auditDataList.add(auditData);
        when(service.createOcisCustomer.create(anyString(), any(Customer.class), any(RequestHeader.class), anyBoolean())).thenThrow(ResourceNotAvailableErrorMsg.class);
        service.createInvolvedParty("asm", true, customer, header);

    }

}
