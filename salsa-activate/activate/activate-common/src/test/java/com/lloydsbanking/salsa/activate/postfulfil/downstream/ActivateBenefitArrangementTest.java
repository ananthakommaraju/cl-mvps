package com.lloydsbanking.salsa.activate.postfulfil.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.postfulfil.convert.CreateServiceArrangementRequestFactory;
import com.lloydsbanking.salsa.downstream.soa.servicearrangement.client.CSAClient;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.soa.servicearrangement.CreateServiceArrangementRequest;
import com.lloydsbanking.salsa.soap.soa.servicearrangement.CreateServiceArrangementResponse;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class ActivateBenefitArrangementTest {
    ActivateBenefitArrangement activateBenefitArrangement;
    TestDataHelper dataHelper;
    HeaderRetriever headerRetriever;
    @Before
    public void setUp() {
        activateBenefitArrangement = new ActivateBenefitArrangement();
        dataHelper = new TestDataHelper();
        activateBenefitArrangement.csaClient = mock(CSAClient.class);
        activateBenefitArrangement.headerRetriever = new HeaderRetriever();
        activateBenefitArrangement.requestFactory = mock(CreateServiceArrangementRequestFactory.class);
        headerRetriever = new HeaderRetriever();
    }
    @Test
    public void testActivateArrangement(){
        RequestHeader requestHeader = dataHelper.createApaRequestHeader();
        ContactPoint contactPoint = headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(), "http://www.lloydstsb.com/Schema/Enterprise/LCSM_ArrangementNegotiation/activateBenefitArrangement", "ActivateBenefitArrangement");
        when(activateBenefitArrangement.requestFactory.convert(any(ProductArrangement.class),any(String.class))).thenReturn(dataHelper.createServiceArrangement());
        when(activateBenefitArrangement.csaClient.createServiceArrangement(any(CreateServiceArrangementRequest.class),
                any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(new CreateServiceArrangementResponse());
        activateBenefitArrangement.activateArrangement(dataHelper.createDepositArrangement("1"),dataHelper.createApaRequestHeader(),"3");
        verify(activateBenefitArrangement.csaClient).createServiceArrangement(dataHelper.createServiceArrangement(),contactPoint, serviceRequest,securityHeaderType);
    }

}