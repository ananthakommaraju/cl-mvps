package com.lloydsbanking.salsa.apapca.service.fulfil.converter;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.soap.pega.objects.CreateCaseRequestType;
import com.lloydsbanking.xml.schema.pega.industryaccountswitching.CreateCasePayloadRequestType;
import com.lloydsbanking.xml.schema.pega.industryaccountswitching.NewAccountType;
import com.lloydsbanking.xml.schema.pega.industryaccountswitching.OldAccountType;
import com.lloydsbanking.xml.schema.pega.industryaccountswitching.PartyPostalAddressType;
import lib_sim_bo.businessobjects.*;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class CreateCaseRequestFactoryTest {
    private CreateCaseRequestFactory createCaseRequestFactory;
    private TestDataHelper testDataHelper;
    private DepositArrangement depositArrangement;

    @Before
    public void setUp() {
        createCaseRequestFactory = new CreateCaseRequestFactory();
        testDataHelper = new TestDataHelper();
        depositArrangement = testDataHelper.createDepositArrangementResp();
        createCaseRequestFactory.createCaseAccountFactory = mock(CreateCaseAccountFactory.class);
    }

    @Test
    public void testConvert() {
        depositArrangement.setAccountSwitchingDetails(new DirectDebit());
        depositArrangement.getAccountSwitchingDetails().setSwitchDate(new DateFactory().stringToXMLGregorianCalendar("02101990", FastDateFormat.getInstance("ddmmyyyy")));
        depositArrangement.getAccountSwitchingDetails().setOverdraftHeldIndicator("102451");
        Customer customer = new Customer();
        customer.getPostalAddress().add(new PostalAddress());
        customer.getPostalAddress().get(0).setIsBFPOAddress(false);
        depositArrangement.setPrimaryInvolvedParty(customer);
        depositArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).setStructuredAddress(testDataHelper.getStructureAddress());
        depositArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).setUnstructuredAddress(testDataHelper.getUnstructuredAddress());
        depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).setSortCode("001005");
        when(createCaseRequestFactory.createCaseAccountFactory.createNewAccount(any(DepositArrangement.class), any(PartyPostalAddressType.class), any(String.class))).thenReturn(new NewAccountType());
        when(createCaseRequestFactory.createCaseAccountFactory.createOldAccountType(any(DirectDebit.class))).thenReturn(new OldAccountType());
        CreateCaseRequestType request = createCaseRequestFactory.convert(depositArrangement, testDataHelper.createApaRequestHeader());
        CreateCasePayloadRequestType requestType = (CreateCasePayloadRequestType) request.getPayload();
        assertEquals("PCAOnline", requestType.getInitiateSwitchIn().getRequestedBySystemId());
        assertEquals("IAS-Retail-Current-Full", requestType.getInitiateSwitchIn().getSwitchDetails().get(0).getSwitchScenario().value());
        assertEquals("001005", requestType.getInitiateSwitchIn().getSwitchDetails().get(0).getCustomerInterviewDetails().getBranchSortCode());
        assertEquals(false, requestType.getInitiateSwitchIn().getSwitchDetails().get(0).getCustomerInterviewDetails().isSwitchersOverdraftOfferAgreedIndicator());
    }

    @Test
    public void testConvertWithAddressAsNull() {
        Customer customer = new Customer();
        customer.getPostalAddress().add(new PostalAddress());
        customer.getPostalAddress().get(0).setIsBFPOAddress(false);
        depositArrangement.setPrimaryInvolvedParty(customer);
        depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).setSortCode(null);
        depositArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).setStructuredAddress(new StructuredAddress());
        depositArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).setUnstructuredAddress(new UnstructuredAddress());
        when(createCaseRequestFactory.createCaseAccountFactory.createNewAccount(any(DepositArrangement.class), any(PartyPostalAddressType.class), any(String.class))).thenReturn(new NewAccountType());
        when(createCaseRequestFactory.createCaseAccountFactory.createOldAccountType(any(DirectDebit.class))).thenReturn(new OldAccountType());
        CreateCaseRequestType request = createCaseRequestFactory.convert(depositArrangement, testDataHelper.createApaRequestHeader());
        CreateCasePayloadRequestType requestType = (CreateCasePayloadRequestType) request.getPayload();
        assertEquals("PCAOnline", requestType.getInitiateSwitchIn().getRequestedBySystemId());
        assertEquals("IAS-Retail-Current-Full", requestType.getInitiateSwitchIn().getSwitchDetails().get(0).getSwitchScenario().value());
    }

    @Test
    public void testConvertWithBFPOAddressAsTrue() {
        depositArrangement.getFinancialInstitution().getHasOrganisationUnits().clear();
        Customer customer = new Customer();
        customer.getPostalAddress().add(new PostalAddress());
        customer.getPostalAddress().get(0).setIsBFPOAddress(true);
        when(createCaseRequestFactory.createCaseAccountFactory.createNewAccount(any(DepositArrangement.class), any(PartyPostalAddressType.class), any(String.class))).thenReturn(new NewAccountType());
        when(createCaseRequestFactory.createCaseAccountFactory.createOldAccountType(any(DirectDebit.class))).thenReturn(new OldAccountType());
        CreateCaseRequestType request = createCaseRequestFactory.convert(depositArrangement, testDataHelper.createApaRequestHeader());
        CreateCasePayloadRequestType requestType = (CreateCasePayloadRequestType) request.getPayload();
        assertEquals("PCAOnline", requestType.getInitiateSwitchIn().getRequestedBySystemId());
        assertEquals("IAS-Retail-Current-Full", requestType.getInitiateSwitchIn().getSwitchDetails().get(0).getSwitchScenario().value());
    }

}
