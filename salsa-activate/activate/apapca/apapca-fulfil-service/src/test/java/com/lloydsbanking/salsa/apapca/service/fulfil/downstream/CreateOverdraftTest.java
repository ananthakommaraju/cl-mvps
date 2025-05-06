package com.lloydsbanking.salsa.apapca.service.fulfil.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.apapca.service.fulfil.converter.B276RequestFactory;
import com.lloydsbanking.salsa.downstream.account.client.AccountClient;
import com.lloydsbanking.salsa.soap.fs.account.StError;
import com.lloydstsb.ib.wsbridge.account.StB276AAccProcessOverdraft;
import com.lloydstsb.ib.wsbridge.account.StB276BAccProcessOverdraft;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.ws.WebServiceException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CreateOverdraftTest {
    CreateOverdraft createOverdraft;

    TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        createOverdraft = new CreateOverdraft();
        testDataHelper = new TestDataHelper();
        createOverdraft.b276RequestFactory = mock(B276RequestFactory.class);
        createOverdraft.accountClient = mock(AccountClient.class);
        createOverdraft.updateDepositArrangementConditionAndApplicationStatusHelper = mock(UpdateDepositArrangementConditionAndApplicationStatusHelper.class);
    }

    @Test
    public void createAccountProcessOverdraftTest() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("1");
        RequestHeader requestHeader = testDataHelper.createApaRequestHeader();
        StB276AAccProcessOverdraft b276Req = new StB276AAccProcessOverdraft();
        StB276BAccProcessOverdraft b276Resp = new StB276BAccProcessOverdraft();
        b276Resp.setSterror(new StError());
        b276Resp.getSterror().setErrorno(123);
        when(createOverdraft.b276RequestFactory.convert(depositArrangement, requestHeader)).thenReturn(b276Req);
        when(createOverdraft.accountClient.retrieveAccountProcessOverdraft(b276Req)).thenReturn(b276Resp);
        ApplicationDetails applicationDetails = new ApplicationDetails();
        createOverdraft.createAccountProcessOverdraft(depositArrangement, requestHeader, applicationDetails);
        assertNotNull(applicationDetails);
    }

    @Test
    public void createAccountProcessOverdraftResponseNullTest() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("1");
        RequestHeader requestHeader = testDataHelper.createApaRequestHeader();
        StB276AAccProcessOverdraft b276Req = new StB276AAccProcessOverdraft();
        StB276BAccProcessOverdraft b276Resp = new StB276BAccProcessOverdraft();
        b276Resp.setSterror(new StError());
        when(createOverdraft.b276RequestFactory.convert(depositArrangement, requestHeader)).thenReturn(b276Req);
        when(createOverdraft.accountClient.retrieveAccountProcessOverdraft(b276Req)).thenReturn(b276Resp);
        ApplicationDetails applicationDetails = new ApplicationDetails();
        createOverdraft.createAccountProcessOverdraft(depositArrangement, requestHeader, applicationDetails);
    }

    @Test
    public void createAccountProcessOverdraftTestWithException() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("1");
        RequestHeader requestHeader = testDataHelper.createApaRequestHeader();
        StB276AAccProcessOverdraft b276Req = new StB276AAccProcessOverdraft();
        StB276BAccProcessOverdraft b276Resp = new StB276BAccProcessOverdraft();
        when(createOverdraft.b276RequestFactory.convert(depositArrangement, requestHeader)).thenReturn(b276Req);
        when(createOverdraft.accountClient.retrieveAccountProcessOverdraft(b276Req)).thenThrow(WebServiceException.class);
        createOverdraft.createAccountProcessOverdraft(depositArrangement, requestHeader, new ApplicationDetails());
    }

    @Test
    public void createAccountProcessOverdraftTestWithStErrorAsNull() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("1");
        RequestHeader requestHeader = testDataHelper.createApaRequestHeader();
        StB276AAccProcessOverdraft b276Req = new StB276AAccProcessOverdraft();
        StB276BAccProcessOverdraft b276Resp = new StB276BAccProcessOverdraft();
        when(createOverdraft.b276RequestFactory.convert(depositArrangement, requestHeader)).thenReturn(b276Req);
        when(createOverdraft.accountClient.retrieveAccountProcessOverdraft(b276Req)).thenReturn(b276Resp);
        ApplicationDetails applicationDetails = new ApplicationDetails();
        createOverdraft.createAccountProcessOverdraft(depositArrangement, requestHeader, applicationDetails);
    }


}
