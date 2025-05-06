package com.lloydsbanking.salsa.activate.postfulfil.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.downstream.ocis.client.c241.C241Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.ocis.c241.objects.C241Req;
import com.lloydsbanking.salsa.soap.ocis.c241.objects.C241Resp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.ws.WebServiceException;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class AddInterPartyRelationshipTest {
    private AddInterPartyRelationship interPartyRelationship;

    private TestDataHelper testDataHelper;

    private ProductArrangement productArrangement;

    private RequestHeader requestHeader;

    private ContactPoint contactPoint;

    private SecurityHeaderType securityHeaderType;

    private ServiceRequest serviceRequest;

    private C241Req c241Req;

    private static final String SERVICE_NAME = "http://www.lloydstsb.com/Schema/Personal/RetailParty/OCIS";

    private static final String SERVICE_ACTION = "C241_AddPartyRelat";

    @Before
    public void setUp() {
        interPartyRelationship = new AddInterPartyRelationship();
        testDataHelper = new TestDataHelper();
        productArrangement = testDataHelper.createDepositArrangementResp();
        productArrangement.getPrimaryInvolvedParty().setCustomerIdentifier("123");
        interPartyRelationship.headerRetriever = new HeaderRetriever();
        interPartyRelationship.updateAppDetails = new UpdateDepositArrangementConditionAndApplicationStatusHelper();
        requestHeader = testDataHelper.createApaRequestHeader();
        c241Req = testDataHelper.createC241Request("19", 1, 2, "123");
        interPartyRelationship.c241Client = mock(C241Client.class);
        contactPoint = interPartyRelationship.headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        securityHeaderType = interPartyRelationship.headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());
        serviceRequest = interPartyRelationship.headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(), SERVICE_NAME, SERVICE_ACTION);

    }

    @Test
    public void testInvokeInterPartyRelationship() {
        C241Resp c241Resp = testDataHelper.createC241Resp();
        c241Resp.getC241Result().getResultCondition().setSeverityCode((byte) 0);
        when(interPartyRelationship.c241Client.c241(c241Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(c241Resp);
        ApplicationDetails applicationDetails = new ApplicationDetails();
        interPartyRelationship.invokeAddInterPartyRelationship(productArrangement, "19", "1", "2", "123", requestHeader, new ApplicationDetails());
        assertNull(applicationDetails.getApplicationStatus());
    }

    @Test
    public void testInvokeInterPartyRelationshipWithExternalServiceError() {
        ApplicationDetails applicationDetails = new ApplicationDetails();
        when(interPartyRelationship.c241Client.c241(c241Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(testDataHelper.createC241Resp());
        interPartyRelationship.invokeAddInterPartyRelationship(productArrangement, "19", "1", "2", "123", requestHeader, applicationDetails);
        assertEquals("1009", applicationDetails.getApplicationStatus());
        assertEquals("1031", applicationDetails.getApplicationSubStatus());
    }

    @Test
    public void testInvokeInterPartyRelationshipWithResourceNotAvailableError() {
        ApplicationDetails applicationDetails = new ApplicationDetails();
        when(interPartyRelationship.c241Client.c241(c241Req, contactPoint, serviceRequest, securityHeaderType)).thenThrow(new WebServiceException());
        interPartyRelationship.invokeAddInterPartyRelationship(productArrangement, "19", "1", "2", "123", requestHeader, applicationDetails);
        assertEquals("1009", applicationDetails.getApplicationStatus());
        assertEquals("1031", applicationDetails.getApplicationSubStatus());
    }
}
