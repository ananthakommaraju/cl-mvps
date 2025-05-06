package com.lloydsbanking.salsa.offer.identify.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.ocis.client.f336.F336Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.logging.application.ProductTraceLog;
import com.lloydsbanking.salsa.offer.LegalEntityMapUtility;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.offer.identify.convert.ProductPartyDataToProductConverter;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.F336Req;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.F336Resp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_gmo.exception.ExternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.Product;
import lib_sim_gmo.messages.RequestHeader;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class ProductHoldingRetrieverTest {
    private ProductHoldingRetriever productHoldingRetriever;

    private TestDataHelper testDataHelper;

    RequestHeader header;

    DepositArrangement depositArrangement;

    F336Resp f336Resp;

    F336Req f336Req;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        productHoldingRetriever = new ProductHoldingRetriever();

        productHoldingRetriever.headerRetriever = new HeaderRetriever();
        productHoldingRetriever.exceptionUtility = createExceptionUtility();
        productHoldingRetriever.f336Client = mock(F336Client.class);
        productHoldingRetriever.productPartyDataToProductConverter = new ProductPartyDataToProductConverter(new DateFactory());
        productHoldingRetriever.productTraceLog = mock(ProductTraceLog.class);

        depositArrangement = testDataHelper.createDepositArrangement();
        depositArrangement.getPrimaryInvolvedParty().setPartyIdentifier("1129336");
        header = testDataHelper.createOpaccRequestHeader("LTB");
        Map<String, String> legalEntityMap = new HashMap<>();
        legalEntityMap.put("LTB", "LTB");

        LegalEntityMapUtility.setLegalEntityMap(legalEntityMap);

        f336Resp = testDataHelper.createF336Response(2, 3);
        f336Req = testDataHelper.createF336Request(productHoldingRetriever.headerRetriever.getBapiInformationHeader(header), depositArrangement.getPrimaryInvolvedParty().getPartyIdentifier());
    }

    @Test
    public void testGetProductDataIsSuccessful() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg {

        ContactPoint contactPoint = productHoldingRetriever.headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = productHoldingRetriever.headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = productHoldingRetriever.headerRetriever.getSecurityHeader(header.getLloydsHeaders());

        f336Resp.setAdditionalDataIn(1);
        f336Resp.setSMMTokenTx("123");
        when(productHoldingRetriever.f336Client.getProductHoldings(f336Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(f336Resp);
        when(productHoldingRetriever.f336Client.getProductHoldings(createF336Request(), contactPoint, serviceRequest, securityHeaderType)).thenReturn(createF336Response());
        when(productHoldingRetriever.productTraceLog.getProdListTraceEventMessage(any(List.class), any(String.class))).thenReturn("Product");
        List<Product> productList = productHoldingRetriever.getProductHoldings(header, depositArrangement.getPrimaryInvolvedParty().getPartyIdentifier());
        assertNotNull(productList);
        assertEquals(2, productList.size());
    }

    @Test
    public void testGetProductDataForExternalServiceError() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg {
        ExternalServiceErrorMsg offerProductArrangementExternalServiceErrorMsg = new ExternalServiceErrorMsg();
        F336Resp response = testDataHelper.createF336ResponseWithExternalServiceError();
        when(productHoldingRetriever.f336Client.getProductHoldings(any(F336Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(response);

        try {
            productHoldingRetriever.getProductHoldings(header, depositArrangement.getPrimaryInvolvedParty().getPartyIdentifier());
        } catch (ExternalServiceErrorMsg errorMsg) {
            offerProductArrangementExternalServiceErrorMsg = errorMsg;
        }
        assertEquals("823009", offerProductArrangementExternalServiceErrorMsg.getFaultInfo().getReasonCode());
        assertEquals("1:failure", offerProductArrangementExternalServiceErrorMsg.getFaultInfo().getReasonText());
    }

    private ExceptionUtility createExceptionUtility() {
        ExceptionUtility exceptionUtility = new ExceptionUtility();
        return exceptionUtility;
    }

    private F336Resp createF336Response() {
        F336Resp f336Resp1 = testDataHelper.createF336Response(2, 3);
        f336Resp1.setAdditionalDataIn(2);
        return f336Resp1;

    }

    private F336Req createF336Request() {
        F336Req f336Req1 = testDataHelper.createF336Request(productHoldingRetriever.headerRetriever.getBapiInformationHeader(header), depositArrangement.getPrimaryInvolvedParty().getPartyIdentifier());
        f336Req1.setSMMTokenTx("123");
        return f336Req1;

    }
}

