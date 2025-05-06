package com.lloydsbanking.salsa.activate.converter;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.downstream.account.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.fs.account.StAddress;
import com.lloydsbanking.salsa.soap.fs.account.StHeader;
import com.lloydsbanking.salsa.soap.fs.account.StParty;
import com.lloydstsb.ib.wsbridge.account.StB765AAccCreateAccount;
import com.lloydstsb.schema.enterprise.lcsm.BAPIHeader;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigInteger;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class DepositArrangementToB765RequestTest {

    DepositArrangementToB765Request depositArrangementToB765Request;
    StB765AAccCreateAccount request;
    TestDataHelper testDataHelper;
    DepositArrangement depositArrangement;
    Product product;
    Map<String, String> accountPurposeMap;
    RequestHeader header;
    HeaderRetriever headerRetriever;
    BAPIHeader bapiHeader;
    ServiceRequest serviceRequest;
    StHeader stHeader;
    ContactPoint contactPoint;
    BapiInformation bapiInformation;

    @Before
    public void setUp() {

        bapiInformation = new BapiInformation();
        stHeader = new StHeader();
        StParty stParty = new StParty();
        stHeader.setStpartyObo(stParty);
        serviceRequest = new ServiceRequest();
        bapiHeader = new BAPIHeader();
        header = new RequestHeader();
        //headerRetriever=mock(HeaderRetriever.class);
        accountPurposeMap = new HashMap<>();
        accountPurposeMap.put("SPORI", "1");
        accountPurposeMap.put("BIEXP", "2");
        request = new StB765AAccCreateAccount();
        testDataHelper = new TestDataHelper();
        depositArrangementToB765Request = new DepositArrangementToB765Request();
        depositArrangementToB765Request.obtainAddressProductAccountAndTariff = mock(ObtainAddressProductAccountAndTariff.class);
        depositArrangement = testDataHelper.createDepositArrangement("123");
        depositArrangement.getPrimaryInvolvedParty().setPartyIdentifier("123");
        depositArrangement.getPrimaryInvolvedParty().setCustomerIdentifier("123");
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setCurrentYearOfStudy(BigInteger.valueOf(1));
        depositArrangement.getPrimaryInvolvedParty().getPostalAddress().add(new PostalAddress());
        depositArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).setStatusCode("CURRENT");
        depositArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).setStructuredAddress(new StructuredAddress());
        depositArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress().setCountry("INDIA");

        product = new Product();
        product.setProductIdentifier("456");
        product.setInstructionDetails(new InstructionDetails());
        product.getInstructionDetails().setInstructionMnemonic("P_STUDENT");
        product.getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        product.getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
        product.getExternalSystemProductIdentifier().get(0).setProductIdentifier("80");
        product.getProductoptions().add(new ProductOptions());
        product.getProductoptions().get(0).setOptionsValue("11");
        product.getProductoptions().get(0).setOptionsType("OptionsType");
        depositArrangementToB765Request.bapiHeaderToStHeaderConverter = mock(BapiHeaderToStHeaderConverter.class);
        depositArrangementToB765Request.headerRetriever = mock(HeaderRetriever.class);
        contactPoint = new ContactPoint();
        depositArrangementToB765Request.obtainAddressProductAccountAndTariffAndPprIdForSA = mock(ObtainAddressProductAccountAndTariffAndPprIdForSA.class);
    }

    @Test
    public void testGetCreateAccountRequest() {
        StAddress stAddress = new StAddress();
        stAddress.setPostcode("PostCode");

        when(depositArrangementToB765Request.bapiHeaderToStHeaderConverter.convert(bapiInformation.getBAPIHeader(), serviceRequest, contactPoint.getContactPointId())).thenReturn(stHeader);
        when(depositArrangementToB765Request.headerRetriever.getContactPoint(header)).thenReturn(contactPoint);
        when(depositArrangementToB765Request.headerRetriever.getServiceRequest(header)).thenReturn(serviceRequest);
        when(depositArrangementToB765Request.headerRetriever.getBapiInformationHeader(header)).thenReturn(bapiInformation);
        when(depositArrangementToB765Request.obtainAddressProductAccountAndTariff.getProdAcc("779129", "80", "CA")).thenReturn(Arrays.asList("8", "7"));
        when(depositArrangementToB765Request.obtainAddressProductAccountAndTariff.getAccountTariff(1)).thenReturn("OptionsType");
        when(depositArrangementToB765Request.obtainAddressProductAccountAndTariff.getStructureAddress(depositArrangement.getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress())).thenReturn(stAddress);
        request = depositArrangementToB765Request.getCreateAccountRequest(header, depositArrangement, product, accountPurposeMap);

        assertEquals("779129", request.getSortcode());
        assertEquals("7", request.getNProdNum().toString());
        assertEquals("3", request.getNAccPurposeCode().toString());
        assertEquals("PostCode", request.getStCBScutomerdetails().getStaddress().getPostcode());
    }

    @Test
    public void testGetCreateAccountRequestWhenTariffAndCustomerDetailsPresent() {
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().get(0).getMiddleNames().add("MiddleName");
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().get(0).setFirstName("FirstName");
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().get(0).setLastName("LastName");
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().get(0).setPrefixTitle("Miss");
        when(depositArrangementToB765Request.bapiHeaderToStHeaderConverter.convert(bapiInformation.getBAPIHeader(), serviceRequest, contactPoint.getContactPointId())).thenReturn(stHeader);
        when(depositArrangementToB765Request.headerRetriever.getContactPoint(header)).thenReturn(contactPoint);
        when(depositArrangementToB765Request.headerRetriever.getServiceRequest(header)).thenReturn(serviceRequest);
        when(depositArrangementToB765Request.headerRetriever.getBapiInformationHeader(header)).thenReturn(bapiInformation);

        when(depositArrangementToB765Request.obtainAddressProductAccountAndTariff.getProdAcc("779129", "80", "CA")).thenReturn(Arrays.asList("8", "7"));
        when(depositArrangementToB765Request.obtainAddressProductAccountAndTariff.getAccountTariff(1)).thenReturn("");
        request = depositArrangementToB765Request.getCreateAccountRequest(header, depositArrangement, product, accountPurposeMap);

        assertEquals("779129", request.getSortcode());
        assertEquals("7", request.getNProdNum().toString());
        assertEquals("3", request.getNAccPurposeCode().toString());
        assertEquals("FirstName", request.getStCBScutomerdetails().getFirstname());
        assertEquals("M", request.getStCBScutomerdetails().getSecondinitial());
        assertEquals("LastName", request.getStCBScutomerdetails().getSurname());
    }

    @Test
    public void testGetCreateAccountRequestWhenTariffAndCustomerDetailsPresentAgain() {
        Product product = new Product();
        InstructionDetails instructionDetails = new InstructionDetails();
        List<ExtSysProdIdentifier> extSysProdIdentifierList = new ArrayList<>();
        ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
        extSysProdIdentifier.setProductIdentifier("01000");
        extSysProdIdentifier.setSystemCode("2Vm");
        extSysProdIdentifierList.add(extSysProdIdentifier);
        product.setProductIdentifier("01000");
        product.setInstructionDetails(instructionDetails);
        product.setProductType("ABS");
        when(depositArrangementToB765Request.bapiHeaderToStHeaderConverter.convert(bapiInformation.getBAPIHeader(), serviceRequest, contactPoint.getContactPointId())).thenReturn(stHeader);
        when(depositArrangementToB765Request.headerRetriever.getContactPoint(header)).thenReturn(contactPoint);
        when(depositArrangementToB765Request.headerRetriever.getServiceRequest(header)).thenReturn(serviceRequest);
        when(depositArrangementToB765Request.headerRetriever.getBapiInformationHeader(header)).thenReturn(bapiInformation);
        request = depositArrangementToB765Request.getCreateAccountRequest(header, testDataHelper.createDepositArrangement(String.valueOf("1234")), product, accountPurposeMap);

    }
}
