package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ocis.client.f336.F336Client;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.ExceptionUtility;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.header.HeaderRetriever;
import com.lloydsbanking.salsa.header.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.F336Req;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.F336Resp;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.ProductPartyData;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import lb_gbo_sales.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import java.util.List;

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

    DetermineElegibileInstructionsRequest upstreamRequest;

    F336Resp f336Resp;

    F336Req f336Req;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        productHoldingRetriever = new ProductHoldingRetriever();

        productHoldingRetriever.headerRetriever = new HeaderRetriever();
        productHoldingRetriever.exceptionUtility = createExceptionUtility();
        productHoldingRetriever.f336Client = mock(F336Client.class);

        upstreamRequest = testDataHelper.createEligibilityRequest("abc", testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);
        header = upstreamRequest.getHeader();

        f336Resp = testDataHelper.createF336Response(2, 3);
        productHoldingRetriever.headerRetriever.getBapiInformationHeader(header).getBAPIHeader().getStpartyObo().setHost("HOST");
        productHoldingRetriever.headerRetriever.getBapiInformationHeader(header).getBAPIHeader().getStpartyObo().setPartyid("Party");

        f336Req = testDataHelper.createF336Request(productHoldingRetriever.headerRetriever.getBapiInformationHeader(header));


    }

    @Test
    public void testGetProductDataIsSuccessful() throws SalsaInternalServiceException {
        {
            ContactPoint contactPoint = productHoldingRetriever.headerRetriever.getContactPoint(header.getLloydsHeaders());
            ServiceRequest serviceRequest = productHoldingRetriever.headerRetriever.getServiceRequest(header.getLloydsHeaders());
            SecurityHeaderType securityHeaderType = productHoldingRetriever.headerRetriever.getSecurityHeader(header.getLloydsHeaders());

            f336Resp.setAdditionalDataIn(1);
            f336Resp.setSMMTokenTx("123");
            when(productHoldingRetriever.f336Client.getProductHoldings(f336Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(f336Resp);


            when(productHoldingRetriever.f336Client.getProductHoldings(createF336Request(), contactPoint, serviceRequest, securityHeaderType)).thenReturn(createF336Response());
            List<ProductPartyData> productPartyData = productHoldingRetriever.getProductHoldings(header);

            assertNotNull(productPartyData);
            assertEquals("123", f336Resp.getSMMTokenTx());
            assertEquals(true, productPartyData.containsAll(f336Resp.getProductPartyData()));
            assertEquals(true, productPartyData.containsAll(createF336Response().getProductPartyData()));
            assertEquals("Party", f336Req.getExtPartyIdTx());
            assertEquals(f336Req.getPartyExtSysId().intValue(), 2);

        }

    }

    public void testCreateF336RequestWithNullBapi() {

        f336Req = testDataHelper.createF336Request(null);
    }

    @Test
    public void testGetProductDataForInternalServiceError() throws SalsaInternalResourceNotAvailableException {

        F336Resp response = testDataHelper.createF336ResponseWithInternalServiceError();
        when(productHoldingRetriever.f336Client.getProductHoldings(any(F336Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(response);

        try {
            productHoldingRetriever.getProductHoldings(header);
        } catch (SalsaInternalServiceException errorMsg) {
            assertEquals(null, errorMsg.getReasonCode());
            assertEquals("failure", errorMsg.getReasonText());
        }
    }

    private ExceptionUtility createExceptionUtility() {
        RequestToResponseHeaderConverter converter = new RequestToResponseHeaderConverter();
        ExceptionUtility exceptionUtility = new ExceptionUtility(converter);
        return exceptionUtility;
    }

    private F336Resp createF336Response() {
        F336Resp f336Resp1 = testDataHelper.createF336Response(2, 3);
        f336Resp1.setAdditionalDataIn(2);
        return f336Resp1;

    }

    private F336Req createF336Request() {
        F336Req f336Req1 = testDataHelper.createF336Request(productHoldingRetriever.headerRetriever.getBapiInformationHeader(header));
        f336Req1.setSMMTokenTx("123");
        return f336Req1;

    }
}

