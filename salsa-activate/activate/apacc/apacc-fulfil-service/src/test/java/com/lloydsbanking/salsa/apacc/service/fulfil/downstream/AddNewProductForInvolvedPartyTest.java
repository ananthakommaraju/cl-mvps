package com.lloydsbanking.salsa.apacc.service.fulfil.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.apacc.TestDataHelper;
import com.lloydsbanking.salsa.apacc.service.fulfil.convert.F259RequestFactory;
import com.lloydsbanking.salsa.downstream.ocis.client.f259.F259Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import com.lloydsbanking.salsa.soap.ocis.f259.objects.F259Req;
import com.lloydsbanking.salsa.soap.ocis.f259.objects.F259Resp;
import com.lloydsbanking.salsa.soap.ocis.f259.objects.F259Result;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.ws.WebServiceException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class AddNewProductForInvolvedPartyTest {
    private static final String F259_SERVICE_NAME = "http://www.lloydstsb.com/Schema/Personal/RetailParty/OCIS/F259_AddProductNewPartner";
    private static final String F259_ACTION_NAME = "F259";
    private AddNewProductForInvolvedParty addNewProductForInvolvedParty;
    private TestDataHelper testDataHelper;
    private RequestHeader requestHeader;
    private HeaderRetriever headerRetriever = new HeaderRetriever();
    FinanceServiceArrangement financeServiceArrangement;

    @Before
    public void setUp() {
        addNewProductForInvolvedParty = new AddNewProductForInvolvedParty();
        testDataHelper = new TestDataHelper();
        financeServiceArrangement = testDataHelper.createFSAForCC(1234);
        requestHeader = testDataHelper.createApaRequestHeader();
        requestHeader.setChannelId("LTB");
        addNewProductForInvolvedParty.f259Client = mock(F259Client.class);
        addNewProductForInvolvedParty.f259RequestFactory = new F259RequestFactory();
        addNewProductForInvolvedParty.headerRetriever = mock(HeaderRetriever.class);
        addNewProductForInvolvedParty.exceptionUtilityActivate = mock(ExceptionUtilityActivate.class);
        addNewProductForInvolvedParty.productArrangementTraceLog = mock(ProductArrangementTraceLog.class);
        when(addNewProductForInvolvedParty.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class), any(String.class))).thenReturn("ProdArrngemnt");

    }

    @Test
    public void testAddNewProduct() {
        F259RequestFactory f259RequestFactory = new F259RequestFactory();
        F259Req f259Req = f259RequestFactory.convert(new FinanceServiceArrangement(), "LTB");
        when(addNewProductForInvolvedParty.headerRetriever.getContactPoint(requestHeader.getLloydsHeaders())).thenReturn(headerRetriever.getContactPoint(requestHeader));
        when(addNewProductForInvolvedParty.headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders())).thenReturn(headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders()));
        when(addNewProductForInvolvedParty.headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(),F259_SERVICE_NAME,F259_ACTION_NAME)).thenReturn(headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(),F259_SERVICE_NAME,F259_ACTION_NAME));
        when(addNewProductForInvolvedParty.headerRetriever.getChannelId(requestHeader)).thenReturn("LTB");
        when(addNewProductForInvolvedParty.f259Client.addNewCustomer(f259Req, headerRetriever.getContactPoint(requestHeader), headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(),F259_SERVICE_NAME,F259_ACTION_NAME), headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders()))).thenReturn(new F259Resp());
        addNewProductForInvolvedParty.addNewProduct(requestHeader, financeServiceArrangement, new ApplicationDetails());
        verify(addNewProductForInvolvedParty.f259Client).addNewCustomer(f259Req, headerRetriever.getContactPoint(requestHeader), headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(),F259_SERVICE_NAME,F259_ACTION_NAME), headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders()));
    }

    @Test
    public void testAddNewProductCallFailed() {
        ApplicationDetails applicationDetails = new ApplicationDetails();
        F259RequestFactory f259RequestFactory = new F259RequestFactory();
        F259Req f259Req = f259RequestFactory.convert(testDataHelper.createFSAForCC(1234), "LTB");
        when(addNewProductForInvolvedParty.headerRetriever.getContactPoint(requestHeader.getLloydsHeaders())).thenReturn(headerRetriever.getContactPoint(requestHeader));
        when(addNewProductForInvolvedParty.headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders())).thenReturn(headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders()));
        when(addNewProductForInvolvedParty.headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(),F259_SERVICE_NAME,F259_ACTION_NAME)).thenReturn(headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(),F259_SERVICE_NAME,F259_ACTION_NAME));
        when(addNewProductForInvolvedParty.headerRetriever.getChannelId(requestHeader)).thenReturn("LTB");
        when(addNewProductForInvolvedParty.f259Client.addNewCustomer(f259Req, headerRetriever.getContactPoint(requestHeader), headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(),F259_SERVICE_NAME,F259_ACTION_NAME), headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders()))).thenThrow(new WebServiceException());
        when(addNewProductForInvolvedParty.exceptionUtilityActivate.resourceNotAvailableError(any(RequestHeader.class), any(String.class))).thenReturn(new ActivateProductArrangementResourceNotAvailableErrorMsg());
        addNewProductForInvolvedParty.addNewProduct(requestHeader, financeServiceArrangement, applicationDetails);
        assertEquals("Failed to add card product on OCIS", applicationDetails.getConditionList().get(0).getReasonText());
        assertEquals("004", applicationDetails.getConditionList().get(0).getReasonCode());
    }

    @Test
    public void testAddNewProductForResourceNotAvailableError() {
        ApplicationDetails applicationDetails = new ApplicationDetails();
        F259RequestFactory f259RequestFactory = new F259RequestFactory();
        F259Req f259Req = f259RequestFactory.convert(new FinanceServiceArrangement(), "LTB");
        when(addNewProductForInvolvedParty.f259Client.addNewCustomer(f259Req, headerRetriever.getContactPoint(requestHeader), headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders()), headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders()))).thenThrow(WebServiceException.class);
        when(addNewProductForInvolvedParty.exceptionUtilityActivate.resourceNotAvailableError(any(RequestHeader.class), any(String.class))).thenThrow(ActivateProductArrangementResourceNotAvailableErrorMsg.class);
        addNewProductForInvolvedParty.addNewProduct(requestHeader, financeServiceArrangement, applicationDetails);
    }

    @Test
    public void testAddNewProductForExternalServiceError() {
        ApplicationDetails applicationDetails = new ApplicationDetails();
        F259RequestFactory f259RequestFactory = new F259RequestFactory();
        F259Req f259Req = f259RequestFactory.convert(new FinanceServiceArrangement(), "LTB");
        F259Resp f259Resp = new F259Resp();
        f259Resp.setF259Result(new F259Result());
        f259Resp.getF259Result().setResultCondition(new ResultCondition());
        f259Resp.getF259Result().getResultCondition().setReasonCode(1);
        f259Resp.getF259Result().getResultCondition().setSeverityCode((byte) 1);
        when(addNewProductForInvolvedParty.exceptionUtilityActivate.externalServiceError(any(RequestHeader.class), any(String.class), any(String.class))).thenThrow(ActivateProductArrangementExternalSystemErrorMsg.class);
        addNewProductForInvolvedParty.addNewProduct(requestHeader, financeServiceArrangement, applicationDetails);
    }
}
