package com.lloydsbanking.salsa.offer.identify.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ocis.client.f447.F447ClientImpl;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.offer.identify.convert.EnquirePartyIdRequestFactory;
import com.lloydsbanking.salsa.soap.ocis.f447.objects.F447Req;
import com.lloydsbanking.salsa.soap.ocis.f447.objects.F447Resp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_gmo.exception.ExternalBusinessErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_gmo.messages.RequestHeader;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class InvolvedPartyMatchRetrieverTest {

    F447Resp response;

    InvolvedPartyMatchRetriever involvedPartyMatchRetriever;
    
    DepositArrangement depositArrangement;

    RequestHeader requestHeader;

    @Before
    public void setUp() {
        involvedPartyMatchRetriever = new InvolvedPartyMatchRetriever();
        involvedPartyMatchRetriever.f447Client = mock(F447ClientImpl.class);
        involvedPartyMatchRetriever.f447RequestFactory = new EnquirePartyIdRequestFactory();
        involvedPartyMatchRetriever.headerRetriever = new HeaderRetriever();
        involvedPartyMatchRetriever.exceptionUtility = new ExceptionUtility();
        depositArrangement = new TestDataHelper().createDepositArrangement();
        requestHeader = new TestDataHelper().createOpaPcaRequestHeader("LTB");
    }

    @Test
    public void testGetInvolvedPartyMatchIsSuccessful() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg {

        response = new TestDataHelper().createF447Response(0);
        when(involvedPartyMatchRetriever.f447Client.enquirePartyId(any(F447Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(response);

        F447Resp response = involvedPartyMatchRetriever.getInvolvedPartyMatch(depositArrangement
                .getPrimaryInvolvedParty()
                .getPostalAddress(), depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy(), requestHeader);
        assertEquals("0", response.getF447Result().getResultCondition().getReasonCode().toString());
    }

    @Test
    public void testGetInvolvedPartyMatchThrowsExternalServiceError() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg {

        response = new TestDataHelper().createF447Response(163135);
        when(involvedPartyMatchRetriever.f447Client.enquirePartyId(any(F447Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(response);

        try {
            F447Resp response = involvedPartyMatchRetriever.getInvolvedPartyMatch(depositArrangement
                    .getPrimaryInvolvedParty()
                    .getPostalAddress(), depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy(), requestHeader);

        } catch (ExternalBusinessErrorMsg externalServiceErrorMsg) {
            assertEquals("823012", externalServiceErrorMsg.getFaultInfo().getReasonCode());
            assertEquals("163135:abc", externalServiceErrorMsg.getFaultInfo().getReasonText());
        }
    }

    @Test
    public void testGetInvolvedPartyMatchThrowsExternalBusinessError() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg {

        response = new TestDataHelper().createF447Response(163150);
        when(involvedPartyMatchRetriever.f447Client.enquirePartyId(any(F447Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(response);

        try {
            F447Resp response = involvedPartyMatchRetriever.getInvolvedPartyMatch(depositArrangement
                    .getPrimaryInvolvedParty()
                    .getPostalAddress(), depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy(), requestHeader);
        } catch (ExternalBusinessErrorMsg externalBusinessErrorMsg) {
            assertEquals("813005", externalBusinessErrorMsg.getFaultInfo().getReasonCode());
            assertEquals("163150:abc", externalBusinessErrorMsg.getFaultInfo().getReasonText());
        }
    }

    @Test
    public void testGetInvolvedPartyMatchThrowsInternalServiceError() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg {

        response = new TestDataHelper().createF447Response(163150);
        when(involvedPartyMatchRetriever.f447Client.enquirePartyId(any(F447Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(response);

        try {
            F447Resp response = involvedPartyMatchRetriever.getInvolvedPartyMatch(depositArrangement
                    .getPrimaryInvolvedParty()
                    .getPostalAddress(), null, requestHeader);
        } catch (InternalServiceErrorMsg internalServiceErrorMsg) {
            assertEquals("82001", internalServiceErrorMsg.getFaultInfo().getReasonCode());
        }
    }

}
