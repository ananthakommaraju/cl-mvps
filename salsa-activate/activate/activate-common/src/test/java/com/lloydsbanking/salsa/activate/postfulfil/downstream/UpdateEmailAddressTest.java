package com.lloydsbanking.salsa.activate.postfulfil.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.activate.postfulfil.convert.C658RequestFactory;
import com.lloydsbanking.salsa.activate.postfulfil.rules.C658ErrorSet;
import com.lloydsbanking.salsa.downstream.ocis.client.c658.C658Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.ocis.c658.objects.C658Req;
import com.lloydsbanking.salsa.soap.ocis.c658.objects.C658Resp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.DepositArrangement;
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
public class UpdateEmailAddressTest {
    private UpdateEmailAddress updateEmailAddress;

    private TestDataHelper testDataHelper;

    private DepositArrangement depositArrangement;

    private RequestHeader requestHeader;

    private ContactPoint contactPoint;

    private SecurityHeaderType securityHeaderType;

    private ServiceRequest serviceRequest;

    private static final String SERVICE_NAME = "http://www.lloydstsb.com/Schema/Personal/RetailParty/OCIS/C658_ChaPtyTelecomPartner";

    private static final String SERVICE_ACTION = "C658";

    @Before
    public void setUp() {
        updateEmailAddress = new UpdateEmailAddress();
        updateEmailAddress.updateAppDetails=new UpdateDepositArrangementConditionAndApplicationStatusHelper();
        testDataHelper = new TestDataHelper();
        depositArrangement = testDataHelper.createDepositArrangementResp();
        depositArrangement.getPrimaryInvolvedParty().setCustomerIdentifier("123");
        updateEmailAddress.headerRetriever = new HeaderRetriever();
        requestHeader = testDataHelper.createApaRequestHeader();
        updateEmailAddress.c658RequestFactory = new C658RequestFactory();
        updateEmailAddress.c658Client = mock(C658Client.class);
        updateEmailAddress.c658ErrorSet = new C658ErrorSet();
        contactPoint = updateEmailAddress.headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        securityHeaderType = updateEmailAddress.headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());
        serviceRequest = updateEmailAddress.headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(),SERVICE_NAME,SERVICE_ACTION);

    }

    @Test
    public void testUpdateEmail() {
        C658Req c658Req = updateEmailAddress.c658RequestFactory.convert(depositArrangement);
        ApplicationDetails applicationDetails=new ApplicationDetails();
        when(updateEmailAddress.c658Client.c658(c658Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(testDataHelper.createC658Resp());
        updateEmailAddress.updateEmail(depositArrangement, requestHeader, applicationDetails);
        assertNull(applicationDetails.getApplicationSubStatus());
    }

    @Test
    public void testUpdateEmailWithExternalServiceError() {
        C658Req c658Req = updateEmailAddress.c658RequestFactory.convert(depositArrangement);
        C658Resp c658Resp = testDataHelper.createC658Resp();
        c658Resp.getC658Result().getResultCondition().setReasonCode(165107);
        ApplicationDetails applicationDetails=new ApplicationDetails();
        when(updateEmailAddress.c658Client.c658(c658Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(c658Resp);
        updateEmailAddress.updateEmail(depositArrangement, requestHeader,applicationDetails);
        assertEquals("1009", applicationDetails.getApplicationStatus());
        assertEquals("1030", applicationDetails.getApplicationSubStatus());
    }

    @Test
    public void testUpdateEmailWithResourceNotAvailableError() {
        C658Req c658Req = updateEmailAddress.c658RequestFactory.convert(depositArrangement);
        ApplicationDetails applicationDetails=new ApplicationDetails();
        when(updateEmailAddress.c658Client.c658(c658Req, contactPoint, serviceRequest, securityHeaderType)).thenThrow(new WebServiceException());
       updateEmailAddress.updateEmail(depositArrangement, requestHeader,applicationDetails);
        assertEquals("1009", applicationDetails.getApplicationStatus());
        assertEquals("1030", applicationDetails.getApplicationSubStatus());
    }

    @Test
    public void testUpdateEmailWithReasonCodeAsNull() {
        C658Req c658Req = updateEmailAddress.c658RequestFactory.convert(depositArrangement);
        C658Resp c658Resp = testDataHelper.createC658Resp();
        c658Resp.getC658Result().getResultCondition().setReasonCode(null);
        ApplicationDetails applicationdetails=new ApplicationDetails();
        when(updateEmailAddress.c658Client.c658(c658Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(c658Resp);
        updateEmailAddress.updateEmail(depositArrangement, requestHeader, applicationdetails);
        assertNull(applicationdetails.getApplicationStatus());
    }

}
