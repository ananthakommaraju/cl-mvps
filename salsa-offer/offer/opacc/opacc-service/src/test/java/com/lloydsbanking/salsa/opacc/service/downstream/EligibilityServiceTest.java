package com.lloydsbanking.salsa.opacc.service.downstream;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.switches.SwitchServiceImpl;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.downstream.EligibilityRetriever;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import com.lloydsbanking.salsa.opacc.service.TestDataHelper;
import com.lloydsbanking.salsa.opacc.service.evaluate.ApplicationTypeEvaluator;
import com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.DetermineEligibleCustomerInstructionsRequest;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_bo.businessobjects.ProductEligibilityDetails;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@Category(UnitTest.class)
public class EligibilityServiceTest {
    EligibilityService service;
    TestDataHelper dataHelper;

    @Before
    public void setUp() throws OfferProductArrangementDataNotAvailableErrorMsg {
        service = new EligibilityService();
        dataHelper = new TestDataHelper();
        service.eligibilityRetriever = mock(EligibilityRetriever.class);
        service.headerRetriever = mock(HeaderRetriever.class);
        service.offerLookupDataRetriever = mock(LookupDataRetriever.class);
        service.switchClient = mock(SwitchServiceImpl.class);
        service.applicationTypeEvaluator = mock(ApplicationTypeEvaluator.class);

    }

    @Test
    public void testProductEligibilityForAuthCustomers() throws OfferProductArrangementDataNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException, ResourceNotAvailableErrorMsg {

        List<Product> brandSpecificProducts = new ArrayList<>();
        brandSpecificProducts.add(new Product());
        brandSpecificProducts.get(0).setProductIdentifier("123");
        brandSpecificProducts.get(0).setStatusCode("111");
        brandSpecificProducts.get(0).setExtPartyIdTx("777");
        List<Product> responseExistingProducts = new ArrayList<>();
        ProductArrangement requestFinanceServiceArrangement = new ProductArrangement();
        RequestHeader requestHeader = new RequestHeader();
        List<String> applicationTypeAndEligibilityCode = new ArrayList<>();
        applicationTypeAndEligibilityCode.add("1001");
        applicationTypeAndEligibilityCode.add("co_hold");

        when(service.applicationTypeEvaluator.getApplicationType(any(ProductArrangement.class), any(Boolean.class), any(Boolean.class))).thenReturn(applicationTypeAndEligibilityCode);
        String productEligibilityType = service.getProductEligibilityTypeCodeForAuthCustomer(dataHelper.generateOfferProductArrangementPCCRequest("LTB").getProductArrangement());
        assertEquals("co_hold", productEligibilityType);

    }


    @Test(expected = OfferException.class)
    public void testProductEligibilityForAuthCustomersThrowsInternalServiceError() throws OfferProductArrangementDataNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException, ResourceNotAvailableErrorMsg {

        List<Product> brandSpecificProducts = new ArrayList<>();
        brandSpecificProducts.add(new Product());
        brandSpecificProducts.get(0).setProductIdentifier("123");
        brandSpecificProducts.get(0).setStatusCode("111");
        brandSpecificProducts.get(0).setExtPartyIdTx("777");
        List<String> applicationTypeAndEligibilityCode = new ArrayList<>();
        applicationTypeAndEligibilityCode.add("1001");
        applicationTypeAndEligibilityCode.add("co_hold");

        when(service.applicationTypeEvaluator.getApplicationType(any(ProductArrangement.class), any(Boolean.class), any(Boolean.class))).thenThrow(InternalServiceErrorMsg.class);
        service.getProductEligibilityTypeCodeForAuthCustomer(dataHelper.generateOfferProductArrangementPCCRequest("LTB").getProductArrangement());

    }

    @Test
    public void testProductEligibilityForAuthCustomersForEligibilityCodeAsNull() throws OfferProductArrangementDataNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException, ResourceNotAvailableErrorMsg {

        List<Product> brandSpecificProducts = new ArrayList<>();
        brandSpecificProducts.add(new Product());
        brandSpecificProducts.get(0).setProductIdentifier("123");
        brandSpecificProducts.get(0).setStatusCode("111");
        brandSpecificProducts.get(0).setExtPartyIdTx("777");

        List<Product> responseExistingProducts = new ArrayList<>();
        ProductArrangement requestFinanceServiceArrangement = new ProductArrangement();
        requestFinanceServiceArrangement.setAssociatedProduct(new Product());
        requestFinanceServiceArrangement.getAssociatedProduct().setEligibilityDetails(new ProductEligibilityDetails());
        RequestHeader requestHeader = new RequestHeader();

        List<String> applicationTypeAndEligibilityCode = new ArrayList<>();
        applicationTypeAndEligibilityCode.add("10001");
        applicationTypeAndEligibilityCode.add(null);


        when(service.applicationTypeEvaluator.getApplicationType(any(ProductArrangement.class), any(Boolean.class), any(Boolean.class))).thenReturn(applicationTypeAndEligibilityCode);
        String productEligibilityType = service.getProductEligibilityTypeCodeForAuthCustomer(dataHelper.generateOfferProductArrangementPCCRequest("LTB").getProductArrangement());
        assertNull(productEligibilityType);
    }

    @Test
    public void testProductEligibilityForAuthCustomersWithAssociatedProductAsNull() throws OfferProductArrangementDataNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException, ResourceNotAvailableErrorMsg {
        List<Product> brandSpecificProducts = new ArrayList<>();
        List<Product> responseExistingProducts = new ArrayList<>();
        RequestHeader requestHeader = new RequestHeader();

        brandSpecificProducts.add(new Product());
        brandSpecificProducts.get(0).setProductIdentifier("123");
        brandSpecificProducts.get(0).setStatusCode("111");
        brandSpecificProducts.get(0).setExtPartyIdTx("777");


        List<String> applicationTypeAndEligibilityCode = new ArrayList<>();
        applicationTypeAndEligibilityCode.add("1001");
        applicationTypeAndEligibilityCode.add("on_hold");

        ProductArrangement requestFinanceServiceArrangement = dataHelper.generateOfferProductArrangementPCCRequest("LTB").getProductArrangement();


        requestFinanceServiceArrangement.setAssociatedProduct(new Product());
        requestFinanceServiceArrangement.getAssociatedProduct().setEligibilityDetails(new ProductEligibilityDetails());
        requestFinanceServiceArrangement.getAssociatedProduct().getEligibilityDetails().setIsEligible("asdddw");


        when(service.applicationTypeEvaluator.getApplicationType(any(ProductArrangement.class), any(Boolean.class), any(Boolean.class))).thenReturn(applicationTypeAndEligibilityCode);
        String productEligibilityType = service.getProductEligibilityTypeCodeForAuthCustomer(requestFinanceServiceArrangement);
        assertEquals("on_hold", productEligibilityType);
    }

    @Test
    public void testProductEligibilityForAuthCustomersWithApplicationTypeAndEligibilityCode() throws OfferProductArrangementDataNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException, ResourceNotAvailableErrorMsg {
        List<Product> brandSpecificProducts = new ArrayList<>();
        List<Product> responseExistingProducts = new ArrayList<>();
        RequestHeader requestHeader = new RequestHeader();

        brandSpecificProducts.add(new Product());
        brandSpecificProducts.get(0).setProductIdentifier("123");
        brandSpecificProducts.get(0).setStatusCode("111");
        brandSpecificProducts.get(0).setExtPartyIdTx("777");


        List<String> applicationTypeAndEligibilityCode = new ArrayList<>();
        applicationTypeAndEligibilityCode.add("1001");
        applicationTypeAndEligibilityCode.add("on_hold");

        ProductArrangement requestFinanceServiceArrangement = dataHelper.generateOfferProductArrangementPCCRequest("LTB").getProductArrangement();


        requestFinanceServiceArrangement.setAssociatedProduct(new Product());
        requestFinanceServiceArrangement.getAssociatedProduct().setEligibilityDetails(new ProductEligibilityDetails());
        requestFinanceServiceArrangement.getAssociatedProduct().getEligibilityDetails().setIsEligible("asdddw");

        when(service.applicationTypeEvaluator.getApplicationType(any(ProductArrangement.class), any(Boolean.class), any(Boolean.class))).thenReturn(applicationTypeAndEligibilityCode);
        String productEligibilityType = service.getProductEligibilityTypeCodeForAuthCustomer(requestFinanceServiceArrangement);
        assertEquals("on_hold", productEligibilityType);
    }

    @Test
    public void testProductEligibilityTypeCodeForUnAuthCustomer() throws OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementExternalServiceErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException {
        List<Product> brandSpecificProducts = new ArrayList<>();
        List<Product> responseExistingProducts = new ArrayList<>();
        RequestHeader requestHeader = new RequestHeader();

        brandSpecificProducts.add(new Product());
        brandSpecificProducts.get(0).setProductIdentifier("123");
        brandSpecificProducts.get(0).setStatusCode("111");
        brandSpecificProducts.get(0).setExtPartyIdTx("777");

        List<String> applicationTypeAndEligibilityCode = new ArrayList<>();
        applicationTypeAndEligibilityCode.add("awwq");
        applicationTypeAndEligibilityCode.add("bbbi9");

        ProductArrangement requestFinanceServiceArrangement = dataHelper.generateOfferProductArrangementPCCRequest("LTB").getProductArrangement();
        requestFinanceServiceArrangement.setAssociatedProduct(new Product());
        requestFinanceServiceArrangement.getAssociatedProduct().setEligibilityDetails(new ProductEligibilityDetails());
        requestFinanceServiceArrangement.getAssociatedProduct().getEligibilityDetails().setIsEligible("asdddw");

        when(service.headerRetriever.getContactPoint(any(RequestHeader.class))).thenReturn(new ContactPoint());
        when(service.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");
        when(service.applicationTypeEvaluator.getApplicationType(any(ProductArrangement.class), any(Boolean.class), any(Boolean.class))).thenReturn(applicationTypeAndEligibilityCode);

        String productEligibilityType = service.getProductEligibilityTypeCodeForUnAuthCustomer(requestFinanceServiceArrangement, requestHeader);
        assertEquals("bbbi9", productEligibilityType);

    }

    @Test
    public void testProductEligibilityTypeCodeForUnAuthCustomerWithMultiCardSwitchEnabled() throws OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementExternalServiceErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException {
        List<Product> brandSpecificProducts = new ArrayList<>();
        List<Product> responseExistingProducts = new ArrayList<>();
        RequestHeader requestHeader = new RequestHeader();

        brandSpecificProducts.add(new Product());
        brandSpecificProducts.get(0).setProductIdentifier("123");
        brandSpecificProducts.get(0).setStatusCode("111");
        brandSpecificProducts.get(0).setExtPartyIdTx("777");

        List<String> applicationTypeAndEligibilityCode = new ArrayList<>();
        applicationTypeAndEligibilityCode.add("awwq");
        applicationTypeAndEligibilityCode.add("bbbi9");

        ProductArrangement requestFinanceServiceArrangement = dataHelper.generateOfferProductArrangementPCCRequest("LTB").getProductArrangement();
        requestFinanceServiceArrangement.setAssociatedProduct(new Product());
        requestFinanceServiceArrangement.getAssociatedProduct().setEligibilityDetails(new ProductEligibilityDetails());
        requestFinanceServiceArrangement.getAssociatedProduct().getEligibilityDetails().setIsEligible("asdddw");

        when(service.headerRetriever.getContactPoint(any(RequestHeader.class))).thenReturn(new ContactPoint());
        when(service.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");
        when(service.switchClient.getBrandedSwitchValue(any(String.class), any(String.class))).thenReturn(true);
        String productEligibilityType = service.getProductEligibilityTypeCodeForUnAuthCustomer(requestFinanceServiceArrangement, requestHeader);
        assertEquals("bbbi9", applicationTypeAndEligibilityCode.get(1));
    }

    @Test
    public void testProductEligibilityTypeCodeForUnAuthCustomerWithMultiCardSwitchEnabledAndEligibilityisTrue() throws OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementExternalServiceErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException {
        List<Product> brandSpecificProducts = new ArrayList<>();
        List<Product> responseExistingProducts = new ArrayList<>();
        RequestHeader requestHeader = new RequestHeader();

        brandSpecificProducts.add(new Product());
        brandSpecificProducts.get(0).setProductIdentifier("123");
        brandSpecificProducts.get(0).setStatusCode("111");
        brandSpecificProducts.get(0).setExtPartyIdTx("777");

        List<String> applicationTypeAndEligibilityCode = new ArrayList<>();
        applicationTypeAndEligibilityCode.add("awwq");
        applicationTypeAndEligibilityCode.add("bbbi9");

        ProductArrangement requestFinanceServiceArrangement = dataHelper.generateOfferProductArrangementPCCRequest("LTB").getProductArrangement();
        requestFinanceServiceArrangement.setAssociatedProduct(new Product());
        requestFinanceServiceArrangement.getAssociatedProduct().setEligibilityDetails(new ProductEligibilityDetails());
        requestFinanceServiceArrangement.getAssociatedProduct().getEligibilityDetails().setIsEligible("asdddw");

        DetermineEligibleCustomerInstructionsResponse response = new DetermineEligibleCustomerInstructionsResponse();
        response.getProductEligibilityDetails().add(new ProductEligibilityDetails());
        response.getProductEligibilityDetails().get(0).setIsEligible("true");

        when(service.eligibilityRetriever.callEligibilityService(any(lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest.class))).thenReturn(response);
        when(service.applicationTypeEvaluator.getApplicationType(any(ProductArrangement.class),anyBoolean(),anyBoolean())).thenReturn(applicationTypeAndEligibilityCode);
        when(service.headerRetriever.getContactPoint(any(RequestHeader.class))).thenReturn(new ContactPoint());
        when(service.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");
        when(service.switchClient.getBrandedSwitchValue(any(String.class), any(String.class))).thenReturn(true);
        String productEligibilityType = service.getProductEligibilityTypeCodeForUnAuthCustomer(requestFinanceServiceArrangement, requestHeader);
        assertEquals("bbbi9", applicationTypeAndEligibilityCode.get(1));
    }

    @Test(expected = OfferException.class)
    public void testProductEligibilityTypeCodeForUnAuthCustomerWithMultiCardSwitchEnabledThrowsResourceNotAvailableError() throws OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementExternalServiceErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException {
        List<Product> brandSpecificProducts = new ArrayList<>();
        List<Product> responseExistingProducts = new ArrayList<>();
        RequestHeader requestHeader = new RequestHeader();

        brandSpecificProducts.add(new Product());
        brandSpecificProducts.get(0).setProductIdentifier("123");
        brandSpecificProducts.get(0).setStatusCode("111");
        brandSpecificProducts.get(0).setExtPartyIdTx("777");

        List<String> applicationTypeAndEligibilityCode = new ArrayList<>();
        applicationTypeAndEligibilityCode.add("awwq");
        applicationTypeAndEligibilityCode.add("bbbi9");

        ProductArrangement requestFinanceServiceArrangement = dataHelper.generateOfferProductArrangementPCCRequest("LTB").getProductArrangement();
        requestFinanceServiceArrangement.setAssociatedProduct(new Product());
        requestFinanceServiceArrangement.getAssociatedProduct().setEligibilityDetails(new ProductEligibilityDetails());
        requestFinanceServiceArrangement.getAssociatedProduct().getEligibilityDetails().setIsEligible("asdddw");

        DetermineEligibleCustomerInstructionsResponse response = new DetermineEligibleCustomerInstructionsResponse();
        response.getProductEligibilityDetails().add(new ProductEligibilityDetails());
        response.getProductEligibilityDetails().get(0).setIsEligible("true");

        when(service.eligibilityRetriever.callEligibilityService(any(lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest.class))).thenReturn(response);
        when(service.applicationTypeEvaluator.getApplicationType(any(ProductArrangement.class),anyBoolean(),anyBoolean())).thenThrow(ResourceNotAvailableErrorMsg.class);
        when(service.headerRetriever.getContactPoint(any(RequestHeader.class))).thenReturn(new ContactPoint());
        when(service.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");
        when(service.switchClient.getBrandedSwitchValue(any(String.class), any(String.class))).thenReturn(true);
        service.getProductEligibilityTypeCodeForUnAuthCustomer(requestFinanceServiceArrangement, requestHeader);
    }
    @Test(expected = OfferException.class)
    public void testProductEligibilityTypeCodeForUnAuthCustomerWithMultiCardSwitchEnabledThrowsInternalServiceErrorMsg() throws OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementExternalServiceErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException {
        List<Product> brandSpecificProducts = new ArrayList<>();
        List<Product> responseExistingProducts = new ArrayList<>();
        RequestHeader requestHeader = new RequestHeader();

        brandSpecificProducts.add(new Product());
        brandSpecificProducts.get(0).setProductIdentifier("123");
        brandSpecificProducts.get(0).setStatusCode("111");
        brandSpecificProducts.get(0).setExtPartyIdTx("777");

        List<String> applicationTypeAndEligibilityCode = new ArrayList<>();
        applicationTypeAndEligibilityCode.add("awwq");
        applicationTypeAndEligibilityCode.add("bbbi9");

        ProductArrangement requestFinanceServiceArrangement = dataHelper.generateOfferProductArrangementPCCRequest("LTB").getProductArrangement();
        requestFinanceServiceArrangement.setAssociatedProduct(new Product());
        requestFinanceServiceArrangement.getAssociatedProduct().setEligibilityDetails(new ProductEligibilityDetails());
        requestFinanceServiceArrangement.getAssociatedProduct().getEligibilityDetails().setIsEligible("asdddw");

        DetermineEligibleCustomerInstructionsResponse response = new DetermineEligibleCustomerInstructionsResponse();
        response.getProductEligibilityDetails().add(new ProductEligibilityDetails());
        response.getProductEligibilityDetails().get(0).setIsEligible("true");

        when(service.eligibilityRetriever.callEligibilityService(any(lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest.class))).thenReturn(response);
        when(service.applicationTypeEvaluator.getApplicationType(any(ProductArrangement.class),anyBoolean(),anyBoolean())).thenThrow(InternalServiceErrorMsg.class);
        when(service.headerRetriever.getContactPoint(any(RequestHeader.class))).thenReturn(new ContactPoint());
        when(service.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");
        when(service.switchClient.getBrandedSwitchValue(any(String.class), any(String.class))).thenReturn(true);
        service.getProductEligibilityTypeCodeForUnAuthCustomer(requestFinanceServiceArrangement, requestHeader);
    }

    @Test(expected = OfferException.class)
    public void testProductEligibilityTypeCodeForUnAuthCustomerWithMultiCardSwitchEnabledThrowsDataNotAvailableError() throws OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementExternalServiceErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException {
        List<Product> brandSpecificProducts = new ArrayList<>();
        List<Product> responseExistingProducts = new ArrayList<>();
        RequestHeader requestHeader = new RequestHeader();

        brandSpecificProducts.add(new Product());
        brandSpecificProducts.get(0).setProductIdentifier("123");
        brandSpecificProducts.get(0).setStatusCode("111");
        brandSpecificProducts.get(0).setExtPartyIdTx("777");

        List<String> applicationTypeAndEligibilityCode = new ArrayList<>();
        applicationTypeAndEligibilityCode.add("awwq");
        applicationTypeAndEligibilityCode.add("bbbi9");

        ProductArrangement requestFinanceServiceArrangement = dataHelper.generateOfferProductArrangementPCCRequest("LTB").getProductArrangement();
        requestFinanceServiceArrangement.setAssociatedProduct(new Product());
        requestFinanceServiceArrangement.getAssociatedProduct().setEligibilityDetails(new ProductEligibilityDetails());
        requestFinanceServiceArrangement.getAssociatedProduct().getEligibilityDetails().setIsEligible("asdddw");

        DetermineEligibleCustomerInstructionsResponse response = new DetermineEligibleCustomerInstructionsResponse();
        response.getProductEligibilityDetails().add(new ProductEligibilityDetails());
        response.getProductEligibilityDetails().get(0).setIsEligible("true");

        when(service.eligibilityRetriever.callEligibilityService(any(lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest.class))).thenReturn(response);
        when(service.applicationTypeEvaluator.getApplicationType(any(ProductArrangement.class),anyBoolean(),anyBoolean())).thenThrow(DataNotAvailableErrorMsg.class);
        when(service.headerRetriever.getContactPoint(any(RequestHeader.class))).thenReturn(new ContactPoint());
        when(service.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");
        when(service.switchClient.getBrandedSwitchValue(any(String.class), any(String.class))).thenReturn(true);
        service.getProductEligibilityTypeCodeForUnAuthCustomer(requestFinanceServiceArrangement, requestHeader);
    }



}

