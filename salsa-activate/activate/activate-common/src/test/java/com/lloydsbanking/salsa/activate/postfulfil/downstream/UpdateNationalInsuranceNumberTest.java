package com.lloydsbanking.salsa.activate.postfulfil.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.activate.postfulfil.convert.C234RequestFactory;
import com.lloydsbanking.salsa.downstream.ocis.client.c234.C234Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.ocis.c234.objects.C234Req;
import com.lloydsbanking.salsa.soap.ocis.c234.objects.C234Resp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.ws.WebServiceException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class UpdateNationalInsuranceNumberTest {
    private UpdateNationalInsuranceNumber updateNationalInsuranceNumber;
    private TestDataHelper testDataHelper;
    private ProductArrangement productArrangement;
    private RequestHeader requestHeader;

    private ContactPoint contactPoint;

    private SecurityHeaderType securityHeaderType;

    private ServiceRequest serviceRequest;

    @Before
    public void setUp() {
        updateNationalInsuranceNumber = new UpdateNationalInsuranceNumber();
        updateNationalInsuranceNumber.updateAppDetails=new UpdateDepositArrangementConditionAndApplicationStatusHelper();
        testDataHelper = new TestDataHelper();
        productArrangement = testDataHelper.createDepositArrangement("123");
        requestHeader = testDataHelper.createApaRequestHeader();
        updateNationalInsuranceNumber.headerRetriever = new HeaderRetriever();
        updateNationalInsuranceNumber.c234RequestFactory = new C234RequestFactory();
        updateNationalInsuranceNumber.c234Client = mock(C234Client.class);
        contactPoint = updateNationalInsuranceNumber.headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        serviceRequest = updateNationalInsuranceNumber.headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(),"http://www.lloydstsb.com/Schema/Personal/RetailParty/OCIS","C234_ChaPersPty");
        securityHeaderType = updateNationalInsuranceNumber.headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());
    }

    @Test
    public void testUpdateNationalInsNumber() {
        C234Req c234Req = updateNationalInsuranceNumber.c234RequestFactory.convert(productArrangement.getPrimaryInvolvedParty());
        ApplicationDetails applicationDetails = new ApplicationDetails();
        when(updateNationalInsuranceNumber.c234Client.c234(c234Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(testDataHelper.createC234Resp());
        updateNationalInsuranceNumber.updateNationalInsNumber(testDataHelper.createDepositArrangement("123"), requestHeader, applicationDetails);
        assertNull(applicationDetails.getApplicationStatus());
    }

    @Test
    public void testUpdateNationalInsNumberWithExternalServiceError() {
        C234Req c234Req = updateNationalInsuranceNumber.c234RequestFactory.convert(testDataHelper.createDepositArrangement("123").getPrimaryInvolvedParty());
        ApplicationDetails applicationDetails = new ApplicationDetails();
        C234Resp c234Resp = testDataHelper.createC234Resp();
        c234Resp.getC234Result().getResultCondition().setSeverityCode(new Byte("1"));
        c234Resp.getC234Result().getResultCondition().setReasonCode(1);
        when(updateNationalInsuranceNumber.c234Client.c234(c234Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(c234Resp);
        updateNationalInsuranceNumber.updateNationalInsNumber(testDataHelper.createDepositArrangement("123"), requestHeader, applicationDetails);
        assertEquals("1009", applicationDetails.getApplicationStatus());
        assertEquals("1021", applicationDetails.getApplicationSubStatus());
    }

    @Test
    public void testUpdateNationalInsNumberWithResourceNotAvailableError() {
        C234Req c234Req = updateNationalInsuranceNumber.c234RequestFactory.convert(productArrangement.getPrimaryInvolvedParty());
        ApplicationDetails applicationDetails = new ApplicationDetails();
        when(updateNationalInsuranceNumber.c234Client.c234(c234Req, contactPoint, serviceRequest, securityHeaderType)).thenThrow(new WebServiceException());
        updateNationalInsuranceNumber.updateNationalInsNumber(testDataHelper.createDepositArrangement("123"), requestHeader, applicationDetails);
        assertEquals("1009", applicationDetails.getApplicationStatus());
        assertEquals("1021", applicationDetails.getApplicationSubStatus());
    }
}
