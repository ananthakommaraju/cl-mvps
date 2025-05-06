package com.lloydsbanking.salsa.activate.postfulfil.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.activate.postfulfil.convert.RecordInvolvedPartyDetailsRequestFactory;
import com.lloydsbanking.salsa.activate.postfulfil.convert.RetrieveInvolvedPartyDetailsRequestFactory;
import com.lloydsbanking.salsa.downstream.soaipm.client.involvedpartymanager.IPMClient;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.logging.application.CustomerTraceLog;
import com.lloydsbanking.salsa.logging.application.TraceLogUtility;
import com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RecordInvolvedPartyDetailsResponse;
import com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RetrieveInvolvedPartyDetailsRequest;
import com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RetrieveInvolvedPartyDetailsResponse;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.ifwxml.ResponseHeader;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.ifwxml.ResultCondition;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import com.lloydstsb.schema.involvedpartymanagement.ifw.ErrorInfo;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_bo.businessobjects.TaxResidencyDetails;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.ws.WebServiceException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class RecordCustomerDetailsTest {
    private static final String SERVICE_NAME = "http://www.lloydstsb.com/Schema/InvolvedPartyManagement/IFW";
    private static final String RECORD_SERVICE_ACTION = "RecordCustomerDetails";
    private static final String RETRIEVE_SERVICE_ACTION = "RetrieveInvolvedParty";
    private RecordCustomerDetails recordCustomerDetails;
    private TestDataHelper testDataHelper;
    private ProductArrangement depositArrangement;
    private ContactPoint contactPoint;
    private SecurityHeaderType securityHeaderType;
    private ServiceRequest serviceRequest;
    private Map<String, String> countryCodeMap;

    @Before
    public void setUp() {
        recordCustomerDetails = new RecordCustomerDetails();
        testDataHelper = new TestDataHelper();
        recordCustomerDetails.headerRetriever = new HeaderRetriever();
        contactPoint = recordCustomerDetails.headerRetriever.getContactPoint(testDataHelper.createApaRequestHeader());
        securityHeaderType = recordCustomerDetails.headerRetriever.getSecurityHeader(testDataHelper.createApaRequestHeader());
        serviceRequest = recordCustomerDetails.headerRetriever.getServiceRequest(testDataHelper.createApaRequestHeader().getLloydsHeaders(), SERVICE_NAME, RETRIEVE_SERVICE_ACTION);
        recordCustomerDetails.recordInvolvedPartyDetailsRequestFactory = mock(RecordInvolvedPartyDetailsRequestFactory.class);
        recordCustomerDetails.retrieveInvolvedPartyDetailsRequestFactory = new RetrieveInvolvedPartyDetailsRequestFactory();
        recordCustomerDetails.soaIpmClient = mock(IPMClient.class);
        recordCustomerDetails.updateAppDetails = new UpdateDepositArrangementConditionAndApplicationStatusHelper();
        countryCodeMap = new HashMap<>();
        countryCodeMap.put("United Kingdom", "London");
        recordCustomerDetails.customerTraceLog = mock(CustomerTraceLog.class);
        recordCustomerDetails.traceLogUtility = mock(TraceLogUtility.class);
        when(recordCustomerDetails.customerTraceLog.getCustomerTraceEventMessage(any(Customer.class), any(String.class))).thenReturn("Customer ");
        when(recordCustomerDetails.traceLogUtility.getTaxResidencyTraceEventMessage(any(TaxResidencyDetails.class), any(String.class))).thenReturn("Tax Residency Details ");
    }

    @Test
    public void testRecordCustomerDetails() throws ErrorInfo {
        depositArrangement = testDataHelper.createDepositArrangement("3376");
        List<String> previousNationalities = new ArrayList<>();
        previousNationalities.add("GBR");
        TaxResidencyDetails taxResidencyDetails = new TaxResidencyDetails();
        taxResidencyDetails.getTaxResidencyCountries().add("GBR");
        taxResidencyDetails.setTaxPayerIdNumber("2");
        depositArrangement.getPrimaryInvolvedParty().setTaxResidencyDetails(taxResidencyDetails);
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getPreviousNationalities().addAll(previousNationalities);
        RetrieveInvolvedPartyDetailsResponse retrieveInvolvedPartyDetailsResponse = new RetrieveInvolvedPartyDetailsResponse();
        retrieveInvolvedPartyDetailsResponse.setResponseHeader(new ResponseHeader());
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().setResultConditions(new ResultCondition());
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().setSeverityCode("1");
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().getExtraConditions().add(new ResultCondition());
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().getExtraConditions().get(0).setReasonCode("3002");
        RetrieveInvolvedPartyDetailsRequest request = recordCustomerDetails.retrieveInvolvedPartyDetailsRequestFactory.convert("123");
        Customer customer = new Customer();
        customer.setCustomerIdentifier("216248241");
        customer.setIsPlayedBy(new lib_sim_bo.businessobjects.Individual());
        customer.getIsPlayedBy().setNationality("United Kingdom");
        customer.setTaxResidencyDetails(taxResidencyDetails);
        customer.getIsPlayedBy().getPreviousNationalities().addAll(previousNationalities);

        when(recordCustomerDetails.soaIpmClient.retrieveInvolvedPartyDetails(request, contactPoint, serviceRequest, securityHeaderType)).thenReturn(retrieveInvolvedPartyDetailsResponse);
        when(recordCustomerDetails.recordInvolvedPartyDetailsRequestFactory.convert(retrieveInvolvedPartyDetailsResponse.getInvolvedParty(), customer, "GBR", false)).thenReturn(testDataHelper.createRecordInvolvedPartyRequest());
        RecordInvolvedPartyDetailsResponse recordInvolvedPartyDetailsResponse = new RecordInvolvedPartyDetailsResponse();
        ApplicationDetails applicationDetails = new ApplicationDetails();
        recordInvolvedPartyDetailsResponse.setResponseHeader(new ResponseHeader());
        recordInvolvedPartyDetailsResponse.getResponseHeader().setResultConditions(new ResultCondition());
        recordInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().setSeverityCode("1");
        recordInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().getExtraConditions().add(new ResultCondition());
        recordInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().getExtraConditions().get(0).setReasonCode("3002");
        when(recordCustomerDetails.soaIpmClient.recordInvolvedPartyDetails(testDataHelper.createRecordInvolvedPartyRequest(), contactPoint, serviceRequest, securityHeaderType)).thenReturn(recordInvolvedPartyDetailsResponse);
        recordCustomerDetails.recordCustomerDetails(depositArrangement, testDataHelper.createApaRequestHeader(), applicationDetails, countryCodeMap, false);
        assertNull(applicationDetails.getApplicationSubStatus());
    }

    @Test
    public void testRecordCustomerDetailsWhenRetrieveInvolvedPartyDetailsThrowsExternalServiceError() throws ErrorInfo {
        depositArrangement = testDataHelper.createDepositArrangement("3376");
        List<String> previousNationalities = new ArrayList<>();
        previousNationalities.add("GBR");
        TaxResidencyDetails taxResidencyDetails = new TaxResidencyDetails();
        taxResidencyDetails.getTaxResidencyCountries().add("GBR");
        taxResidencyDetails.setTaxPayerIdNumber("2");
        depositArrangement.getPrimaryInvolvedParty().setTaxResidencyDetails(taxResidencyDetails);
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getPreviousNationalities().addAll(previousNationalities);
        RetrieveInvolvedPartyDetailsResponse retrieveInvolvedPartyDetailsResponse = new RetrieveInvolvedPartyDetailsResponse();
        retrieveInvolvedPartyDetailsResponse.setResponseHeader(new ResponseHeader());
        ApplicationDetails applicationDetails = new ApplicationDetails();
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().setResultConditions(new ResultCondition());
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().setSeverityCode("2");
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().getExtraConditions().add(new ResultCondition());
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().getExtraConditions().get(0).setReasonCode("1031");
        RetrieveInvolvedPartyDetailsRequest request = recordCustomerDetails.retrieveInvolvedPartyDetailsRequestFactory.convert("123");
        Customer customer = new Customer();
        customer.setCustomerIdentifier("216248241");
        customer.setIsPlayedBy(new lib_sim_bo.businessobjects.Individual());
        customer.getIsPlayedBy().setNationality("United Kingdom");
        customer.setTaxResidencyDetails(taxResidencyDetails);
        customer.getIsPlayedBy().getPreviousNationalities().addAll(previousNationalities);
        when(recordCustomerDetails.soaIpmClient.retrieveInvolvedPartyDetails(request, contactPoint, serviceRequest, securityHeaderType)).thenReturn(retrieveInvolvedPartyDetailsResponse);
        recordCustomerDetails.recordCustomerDetails(depositArrangement, testDataHelper.createApaRequestHeader(), applicationDetails, countryCodeMap, false);
        assertEquals("1032", applicationDetails.getApplicationSubStatus());
    }

    @Test
    public void testRecordCustomerDetailsWhenRetrieveInvolvedPartyDetailsThrowsExternalBusinessError() throws ErrorInfo {
        depositArrangement = testDataHelper.createDepositArrangement("3376");
        List<String> previousNationalities = new ArrayList<>();
        previousNationalities.add("GBR");
        TaxResidencyDetails taxResidencyDetails = new TaxResidencyDetails();
        taxResidencyDetails.getTaxResidencyCountries().add("GBR");
        taxResidencyDetails.setTaxPayerIdNumber("2");
        depositArrangement.getPrimaryInvolvedParty().setTaxResidencyDetails(taxResidencyDetails);
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getPreviousNationalities().addAll(previousNationalities);
        RetrieveInvolvedPartyDetailsResponse retrieveInvolvedPartyDetailsResponse = new RetrieveInvolvedPartyDetailsResponse();
        retrieveInvolvedPartyDetailsResponse.setResponseHeader(new ResponseHeader());
        ApplicationDetails applicationDetails = new ApplicationDetails();
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().setResultConditions(new ResultCondition());
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().setSeverityCode("2");
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().getExtraConditions().add(new ResultCondition());
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().getExtraConditions().get(0).setReasonCode("3002");
        RetrieveInvolvedPartyDetailsRequest request = recordCustomerDetails.retrieveInvolvedPartyDetailsRequestFactory.convert("123");
        Customer customer = new Customer();
        customer.setCustomerIdentifier("216248241");
        customer.setIsPlayedBy(new lib_sim_bo.businessobjects.Individual());
        customer.getIsPlayedBy().setNationality("United Kingdom");
        customer.setTaxResidencyDetails(taxResidencyDetails);
        customer.getIsPlayedBy().getPreviousNationalities().addAll(previousNationalities);
        when(recordCustomerDetails.soaIpmClient.retrieveInvolvedPartyDetails(request, contactPoint, serviceRequest, securityHeaderType)).thenReturn(retrieveInvolvedPartyDetailsResponse);
        recordCustomerDetails.recordCustomerDetails(depositArrangement, testDataHelper.createApaRequestHeader(), applicationDetails, countryCodeMap, false);
        assertEquals("1032", applicationDetails.getApplicationSubStatus());
    }

    @Test
    public void testRecordCustomerDetailsWhenRecordInvolvedPartyThrowsExternalServiceError() throws ErrorInfo {
        depositArrangement = testDataHelper.createDepositArrangement("3376");
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setNationality("GBR");
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setCountryOfBirth("United Kingdom");
        List<String> previousNationalities = new ArrayList<>();
        previousNationalities.add("GBR");
        TaxResidencyDetails taxResidencyDetails = new TaxResidencyDetails();
        taxResidencyDetails.getTaxResidencyCountries().add("GBR");
        taxResidencyDetails.setTaxPayerIdNumber("2");
        depositArrangement.getPrimaryInvolvedParty().setTaxResidencyDetails(taxResidencyDetails);
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getPreviousNationalities().addAll(previousNationalities);
        RetrieveInvolvedPartyDetailsResponse retrieveInvolvedPartyDetailsResponse = new RetrieveInvolvedPartyDetailsResponse();
        retrieveInvolvedPartyDetailsResponse.setResponseHeader(new ResponseHeader());
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().setResultConditions(new ResultCondition());
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().setSeverityCode("2");
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().getExtraConditions().add(new ResultCondition());
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().getExtraConditions().get(0).setReasonCode("3002");
        RetrieveInvolvedPartyDetailsRequest request = recordCustomerDetails.retrieveInvolvedPartyDetailsRequestFactory.convert("123");
        ApplicationDetails applicationDetails = new ApplicationDetails();
        Customer customer = new Customer();
        customer.setCustomerIdentifier("216248241");
        customer.setIsPlayedBy(new lib_sim_bo.businessobjects.Individual());
        customer.getIsPlayedBy().setNationality("United Kingdom");
        customer.setTaxResidencyDetails(taxResidencyDetails);
        customer.getIsPlayedBy().getPreviousNationalities().addAll(previousNationalities);
        when(recordCustomerDetails.recordInvolvedPartyDetailsRequestFactory.convert(retrieveInvolvedPartyDetailsResponse.getInvolvedParty(), customer, "GBR", false)).thenReturn(testDataHelper.createRecordInvolvedPartyRequest());
        when(recordCustomerDetails.soaIpmClient.retrieveInvolvedPartyDetails(request, contactPoint, serviceRequest, securityHeaderType)).thenReturn(retrieveInvolvedPartyDetailsResponse);
        serviceRequest = recordCustomerDetails.headerRetriever.getServiceRequest(testDataHelper.createApaRequestHeader().getLloydsHeaders(), SERVICE_NAME, RECORD_SERVICE_ACTION);
        RecordInvolvedPartyDetailsResponse recordInvolvedPartyDetailsResponse = new RecordInvolvedPartyDetailsResponse();
        recordInvolvedPartyDetailsResponse.setResponseHeader(new ResponseHeader());
        recordInvolvedPartyDetailsResponse.getResponseHeader().setResultConditions(new ResultCondition());
        recordInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().setSeverityCode("2");
        recordInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().getExtraConditions().add(new ResultCondition());
        recordInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().getExtraConditions().get(0).setReasonCode("999");
        when(recordCustomerDetails.soaIpmClient.recordInvolvedPartyDetails(testDataHelper.createRecordInvolvedPartyRequest(), contactPoint, serviceRequest, securityHeaderType)).thenReturn(recordInvolvedPartyDetailsResponse);
        recordCustomerDetails.recordCustomerDetails(depositArrangement, testDataHelper.createApaRequestHeader(), applicationDetails, countryCodeMap, false);
        assertEquals("1032", applicationDetails.getApplicationSubStatus());
    }

    @Test
    public void testRecordCustomerDetailsWhenRecordInvolvedPartyThrowsExternalBusinessError() throws ErrorInfo {
        depositArrangement = testDataHelper.createDepositArrangement("3376");
        List<String> previousNationalities = new ArrayList<>();
        previousNationalities.add("GBR");
        TaxResidencyDetails taxResidencyDetails = new TaxResidencyDetails();
        taxResidencyDetails.getTaxResidencyCountries().add("GBR");
        taxResidencyDetails.setTaxPayerIdNumber("2");
        depositArrangement.getPrimaryInvolvedParty().setTaxResidencyDetails(taxResidencyDetails);
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getPreviousNationalities().addAll(previousNationalities);
        RetrieveInvolvedPartyDetailsResponse retrieveInvolvedPartyDetailsResponse = new RetrieveInvolvedPartyDetailsResponse();
        retrieveInvolvedPartyDetailsResponse.setResponseHeader(new ResponseHeader());
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().setResultConditions(new ResultCondition());
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().setSeverityCode("1");
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().getExtraConditions().add(new ResultCondition());
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().getExtraConditions().get(0).setReasonCode("3002");
        RetrieveInvolvedPartyDetailsRequest request = recordCustomerDetails.retrieveInvolvedPartyDetailsRequestFactory.convert("123");
        Customer customer = new Customer();
        customer.setCustomerIdentifier("216248241");
        customer.setIsPlayedBy(new lib_sim_bo.businessobjects.Individual());
        customer.getIsPlayedBy().setNationality("United Kingdom");
        customer.setTaxResidencyDetails(taxResidencyDetails);
        customer.getIsPlayedBy().getPreviousNationalities().addAll(previousNationalities);
        ApplicationDetails applicationDetails = new ApplicationDetails();
        when(recordCustomerDetails.soaIpmClient.retrieveInvolvedPartyDetails(request, contactPoint, serviceRequest, securityHeaderType)).thenReturn(retrieveInvolvedPartyDetailsResponse);
        when(recordCustomerDetails.recordInvolvedPartyDetailsRequestFactory.convert(retrieveInvolvedPartyDetailsResponse.getInvolvedParty(), customer, "GBR", false)).thenReturn(testDataHelper.createRecordInvolvedPartyRequest());
        RecordInvolvedPartyDetailsResponse recordInvolvedPartyDetailsResponse = new RecordInvolvedPartyDetailsResponse();
        recordInvolvedPartyDetailsResponse.setResponseHeader(new ResponseHeader());
        recordInvolvedPartyDetailsResponse.getResponseHeader().setResultConditions(new ResultCondition());
        recordInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().setSeverityCode("2");
        recordInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().getExtraConditions().add(new ResultCondition());
        recordInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().getExtraConditions().get(0).setReasonCode("9701");
        when(recordCustomerDetails.soaIpmClient.recordInvolvedPartyDetails(testDataHelper.createRecordInvolvedPartyRequest(), contactPoint, serviceRequest, securityHeaderType)).thenReturn(recordInvolvedPartyDetailsResponse);
        recordCustomerDetails.recordCustomerDetails(depositArrangement, testDataHelper.createApaRequestHeader(), applicationDetails, countryCodeMap, false);
        assertNull(applicationDetails.getApplicationSubStatus());
    }

    @Test
    public void testRecordCustomerDetailsWhenRecordCustomerDetailsThrowsResourceNotAvailableError() throws ErrorInfo {
        depositArrangement = testDataHelper.createDepositArrangement("3376");
        List<String> previousNationalities = new ArrayList<>();
        previousNationalities.add("GBR");
        TaxResidencyDetails taxResidencyDetails = new TaxResidencyDetails();
        taxResidencyDetails.getTaxResidencyCountries().add("GBR");
        taxResidencyDetails.setTaxPayerIdNumber("2");
        depositArrangement.getPrimaryInvolvedParty().setTaxResidencyDetails(taxResidencyDetails);
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getPreviousNationalities().addAll(previousNationalities);
        RetrieveInvolvedPartyDetailsResponse retrieveInvolvedPartyDetailsResponse = new RetrieveInvolvedPartyDetailsResponse();
        retrieveInvolvedPartyDetailsResponse.setResponseHeader(new ResponseHeader());
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().setResultConditions(new ResultCondition());
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().setSeverityCode("1");
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().getExtraConditions().add(new ResultCondition());
        retrieveInvolvedPartyDetailsResponse.getResponseHeader().getResultConditions().getExtraConditions().get(0).setReasonCode("3002");
        RetrieveInvolvedPartyDetailsRequest request = recordCustomerDetails.retrieveInvolvedPartyDetailsRequestFactory.convert("123");
        ApplicationDetails applicationDetails = new ApplicationDetails();
        when(recordCustomerDetails.soaIpmClient.retrieveInvolvedPartyDetails(request, contactPoint, serviceRequest, securityHeaderType)).thenThrow(WebServiceException.class);
        recordCustomerDetails.recordCustomerDetails(depositArrangement, testDataHelper.createApaRequestHeader(), applicationDetails, countryCodeMap, false);
        assertEquals("1032", applicationDetails.getApplicationSubStatus());
    }
}
