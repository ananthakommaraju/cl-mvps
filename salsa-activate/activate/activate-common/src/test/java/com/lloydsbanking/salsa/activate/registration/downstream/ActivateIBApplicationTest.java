package com.lloydsbanking.salsa.activate.registration.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.postfulfil.downstream.CommunicatePostFulfilmentActivities;
import com.lloydsbanking.salsa.activate.registration.converter.B751RequestFactory;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.downstream.application.client.ApplicationClient;
import com.lloydstsb.ib.wsbridge.application.StB751AAppPerCCRegAuth;
import com.lloydstsb.ib.wsbridge.application.StB751BAppPerCCRegAuth;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.InternetBankingProfile;
import lib_sim_bo.businessobjects.InternetBankingRegistration;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalBusinessErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.ws.WebServiceException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class ActivateIBApplicationTest {
    TestDataHelper testDataHelper;
    RequestHeader requestHeader;
    ActivateIBApplication activateIBApplication;
    StB751AAppPerCCRegAuth b751Request;
    StB751BAppPerCCRegAuth b751Response;
    ProductArrangement productArrangement;



    @Before
    public void setUp(){
        activateIBApplication=new ActivateIBApplication();
        testDataHelper=new TestDataHelper();
        requestHeader = testDataHelper.createApaRequestHeader();
        productArrangement=testDataHelper.createApaRequestByDBEvent().getProductArrangement();
        activateIBApplication.b751RequestFactory=mock(B751RequestFactory.class);
        activateIBApplication.applicationClient=mock(ApplicationClient.class);
        b751Request=new StB751AAppPerCCRegAuth();
        b751Response=new StB751BAppPerCCRegAuth();
        activateIBApplication.communicatePostFulfilmentActivities=mock(CommunicatePostFulfilmentActivities.class);

    }
    @Test
     public void testRetrieveActivateIBApplication() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg {
        when(activateIBApplication.b751RequestFactory.convert(productArrangement,requestHeader)).thenReturn(testDataHelper.createB751Request());
        when(activateIBApplication.applicationClient.createAppPerCCRegAuth(testDataHelper.createB751Request())).thenReturn(testDataHelper.createB751Response());
        b751Response=activateIBApplication.retrieveActivateIBApplication(productArrangement,requestHeader);
        assertEquals(testDataHelper.createB751Response().getPartyidEmergingChannelUserId(), b751Response.getPartyidEmergingChannelUserId());
        assertEquals(testDataHelper.createB751Response().getTacver(), b751Response.getTacver());
    }

    @Test
    public void testRetrieveActivateIBApplicationThrowsResourceNotAvailableErrorMsg(){
        when(activateIBApplication.b751RequestFactory.convert(productArrangement,requestHeader)).thenReturn(testDataHelper.createB751Request());
        when(activateIBApplication.applicationClient.createAppPerCCRegAuth(testDataHelper.createB751Request())).thenThrow(WebServiceException.class);
        b751Response=activateIBApplication.retrieveActivateIBApplication(productArrangement,requestHeader);
        assertNull(b751Response);
    }

    @Test
    public void testRetrieveActivateIBApplicationWithExternalBusinessError() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg {
        when(activateIBApplication.b751RequestFactory.convert(productArrangement,requestHeader)).thenReturn(testDataHelper.createB751Request());
        when(activateIBApplication.applicationClient.createAppPerCCRegAuth(testDataHelper.createB751Request())).thenReturn(testDataHelper.createB751ResponseWithError());
        b751Response=activateIBApplication.retrieveActivateIBApplication(productArrangement,requestHeader);
        assertEquals(testDataHelper.createB751ResponseWithError().getSterror().getErrorno(), b751Response.getSterror().getErrorno());
    }

    @Test
    public void testRetrieveActivateIBApplicationForCC(){
        productArrangement.setArrangementType(ArrangementType.CREDITCARD.getValue());
        when(activateIBApplication.b751RequestFactory.convert(productArrangement, requestHeader)).thenReturn(testDataHelper.createB751Request());
        when(activateIBApplication.applicationClient.createAppPerCCRegAuth(testDataHelper.createB751Request())).thenReturn(testDataHelper.createB751Response());
        b751Response=activateIBApplication.retrieveActivateIBApplication(productArrangement,requestHeader);
        assertEquals(testDataHelper.createB751Response().getPartyidEmergingChannelUserId(), b751Response.getPartyidEmergingChannelUserId());
        assertEquals(testDataHelper.createB751Response().getTacver(), b751Response.getTacver());
    }

    @Test
    public void testRetrieveActivateIBApplicationWhenB751RespIsNull(){
        productArrangement.setArrangementType(ArrangementType.CREDITCARD.getValue());
        when(activateIBApplication.b751RequestFactory.convert(productArrangement, requestHeader)).thenReturn(testDataHelper.createB751Request());
        when(activateIBApplication.applicationClient.createAppPerCCRegAuth(testDataHelper.createB751Request())).thenReturn(null);
        b751Response=activateIBApplication.retrieveActivateIBApplication(productArrangement,requestHeader);
        assertNull(b751Response);
    }

    @Test
    public void testRetrieveActivateIBApplicationWhenProfileIsNotNull(){
        productArrangement.setArrangementType(ArrangementType.CREDITCARD.getValue());
        productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().setProfile(new InternetBankingProfile());
        productArrangement.getJointParties().add(new Customer());
        productArrangement.getJointParties().get(0).setIsRegisteredIn(new InternetBankingRegistration());
        productArrangement.getJointParties().get(0).getIsRegisteredIn().setProfile(new InternetBankingProfile());
        when(activateIBApplication.b751RequestFactory.convert(productArrangement, requestHeader)).thenReturn(testDataHelper.createB751Request());
        when(activateIBApplication.applicationClient.createAppPerCCRegAuth(testDataHelper.createB751Request())).thenReturn(testDataHelper.createB751Response());
        b751Response=activateIBApplication.retrieveActivateIBApplication(productArrangement,requestHeader);
        assertEquals(testDataHelper.createB751Response().getPartyidEmergingChannelUserId(), b751Response.getPartyidEmergingChannelUserId());
        assertEquals(testDataHelper.createB751Response().getTacver(), b751Response.getTacver());
    }

    @Test
    public void testRetrieveActivateIBApplicationWhenPartyIdIsNull(){
        StB751BAppPerCCRegAuth b751Response1 = testDataHelper.createB751Response();
        b751Response1.setPartyidEmergingChannelUserId(null);
        when(activateIBApplication.b751RequestFactory.convert(productArrangement, requestHeader)).thenReturn(testDataHelper.createB751Request());
        when(activateIBApplication.applicationClient.createAppPerCCRegAuth(testDataHelper.createB751Request())).thenReturn( b751Response1);
        b751Response=activateIBApplication.retrieveActivateIBApplication(productArrangement,requestHeader);
        assertEquals(testDataHelper.createB751Response().getTacver(), b751Response.getTacver());
    }
}
