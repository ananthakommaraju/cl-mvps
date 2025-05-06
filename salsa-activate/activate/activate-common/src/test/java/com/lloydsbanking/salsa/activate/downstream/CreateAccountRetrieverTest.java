package com.lloydsbanking.salsa.activate.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.converter.B765ToDepositArrangementResponse;
import com.lloydsbanking.salsa.activate.converter.DepositArrangementToB765Request;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.downstream.account.client.AccountClient;
import com.lloydsbanking.salsa.soap.fs.account.StAccount;
import com.lloydsbanking.salsa.soap.fs.account.StError;
import com.lloydstsb.ib.wsbridge.account.StB765AAccCreateAccount;
import com.lloydstsb.ib.wsbridge.account.StB765BAccCreateAccount;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.ws.WebServiceException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CreateAccountRetrieverTest {
    CreateAccountRetriever createAccountRetriever;

    DepositArrangement depositArrangement;

    RequestHeader requestHeader;

    TestDataHelper testDataHelper;

    StB765BAccCreateAccount b765Response;

    StB765AAccCreateAccount stB765AAccCreateAccountRequest;
    Map<String, String> accountPurposeMap;
    ActivateProductArrangementResponse activateResponse;
    RequestHeader header;
    ApplicationDetails applicationDetails;

    @Before
    public void setUp() {
        header = new RequestHeader();
        activateResponse = new ActivateProductArrangementResponse();
        ProductArrangement productArrangement = new ProductArrangement();
        Organisation organisation = new Organisation();
        Customer customer = new Customer();
        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisation.getHasOrganisationUnits().add(organisationUnit);
        productArrangement.setPrimaryInvolvedParty(customer);
        productArrangement.setFinancialInstitution(organisation);
        activateResponse.setProductArrangement(productArrangement);
        accountPurposeMap = new HashMap<>();
        accountPurposeMap.put("SPORI", "1");
        testDataHelper = new TestDataHelper();
        stB765AAccCreateAccountRequest = new StB765AAccCreateAccount();
        stB765AAccCreateAccountRequest.setAccno("account");
        b765Response = new StB765BAccCreateAccount();
        StError stError = new StError();
        stError.setErrorno(0);
        b765Response.setSterror(stError);
        applicationDetails = new ApplicationDetails();
        createAccountRetriever = new CreateAccountRetriever();
        depositArrangement = testDataHelper.createDepositArrangement("123");
        createAccountRetriever.depositArrangementToB765Request = mock(DepositArrangementToB765Request.class);
        requestHeader = testDataHelper.createApaRequestHeader();
        createAccountRetriever.accountClient = mock(AccountClient.class);
        createAccountRetriever.b765ToDepositArrangementResponse = mock(B765ToDepositArrangementResponse.class);
        createAccountRetriever.updateDepositArrangementConditionAndApplicationStatusHelper = new UpdateDepositArrangementConditionAndApplicationStatusHelper();

    }

    @Test
    public void testCreateAccountForNullResponse() {
        Product product = new Product();
        product.setInstructionDetails(new InstructionDetails());
        b765Response.setSterror(null);
        when(createAccountRetriever.depositArrangementToB765Request.getCreateAccountRequest(header, depositArrangement, product, accountPurposeMap)).thenReturn(stB765AAccCreateAccountRequest);
        when(createAccountRetriever.accountClient.createAccount(stB765AAccCreateAccountRequest)).thenReturn(b765Response);
        createAccountRetriever.createAccount(header, depositArrangement, product, accountPurposeMap, activateResponse, applicationDetails);
        assertNull(applicationDetails.getApplicationSubStatus());
    }

    @Test
    public void testCreateAccountForZeroErrorNo() {
        Product product = new Product();
        product.setInstructionDetails(new InstructionDetails());
        b765Response.setSterror(new StError());
        b765Response.getSterror().setErrorno(0);
        b765Response.setStacc(new StAccount());
        when(createAccountRetriever.depositArrangementToB765Request.getCreateAccountRequest(header, depositArrangement, product, accountPurposeMap)).thenReturn(stB765AAccCreateAccountRequest);
        when(createAccountRetriever.accountClient.createAccount(stB765AAccCreateAccountRequest)).thenReturn(b765Response);
        createAccountRetriever.createAccount(header, depositArrangement, product, accountPurposeMap, activateResponse, applicationDetails);
        assertNull(applicationDetails.getApplicationSubStatus());
    }
    @Test
    public void testCreateAccountForNulInstructionDetails() {
        Product product = new Product();
        b765Response.setSterror(new StError());
        b765Response.getSterror().setErrorno(0);
        b765Response.setStacc(new StAccount());
        when(createAccountRetriever.depositArrangementToB765Request.getCreateAccountRequest(header, depositArrangement, product, accountPurposeMap)).thenReturn(stB765AAccCreateAccountRequest);
        when(createAccountRetriever.accountClient.createAccount(stB765AAccCreateAccountRequest)).thenReturn(b765Response);
        createAccountRetriever.createAccount(header, depositArrangement, product, accountPurposeMap, activateResponse, applicationDetails);
        assertEquals("1024",applicationDetails.getApplicationSubStatus());
    }

    @Test
    public void testCreateAccountForSomeErrorNo() {
        Product product = new Product();
        product.setInstructionDetails(new InstructionDetails());
        createAccountRetriever.b765ToDepositArrangementResponse = new B765ToDepositArrangementResponse();
        b765Response.setSterror(new StError());
        b765Response.setStacc(new StAccount());
        b765Response.getSterror().setErrorno(131187);
        when(createAccountRetriever.depositArrangementToB765Request.getCreateAccountRequest(header, depositArrangement, product, accountPurposeMap)).thenReturn(stB765AAccCreateAccountRequest);
        when(createAccountRetriever.accountClient.createAccount(stB765AAccCreateAccountRequest)).thenReturn(b765Response);
        createAccountRetriever.createAccount(header, depositArrangement, product, accountPurposeMap, activateResponse, applicationDetails);
        assertNull(applicationDetails.getApplicationSubStatus());

    }

    @Test
    public void testCreateAccountCallingSetApplicationDetailsMethod() {
        Product product = new Product();
        b765Response.setSterror(new StError());
        b765Response.getSterror().setErrorno(131);
        when(createAccountRetriever.depositArrangementToB765Request.getCreateAccountRequest(header, depositArrangement, product, accountPurposeMap)).thenReturn(stB765AAccCreateAccountRequest);
        when(createAccountRetriever.accountClient.createAccount(stB765AAccCreateAccountRequest)).thenReturn(b765Response);
        createAccountRetriever.createAccount(header, depositArrangement, product, accountPurposeMap, activateResponse, applicationDetails);
        assertNotNull(applicationDetails);
        assertTrue(applicationDetails.isApiFailureFlag());
        assertEquals("1009", applicationDetails.getApplicationStatus());
    }

    @Test
    public void testCreateAccountWithException() {
        Product product = new Product();
        product.setProductName("Classic Account");
        when(createAccountRetriever.depositArrangementToB765Request.getCreateAccountRequest(header, depositArrangement, product, accountPurposeMap)).thenReturn(stB765AAccCreateAccountRequest);
        when(createAccountRetriever.accountClient.createAccount(createAccountRetriever.depositArrangementToB765Request.getCreateAccountRequest(header, depositArrangement, product, accountPurposeMap))).thenThrow(WebServiceException.class);
        createAccountRetriever.createAccount(header, depositArrangement, product, accountPurposeMap, activateResponse, applicationDetails);
        assertNotNull(applicationDetails);
        assertTrue(applicationDetails.isApiFailureFlag());
        assertEquals("1009", applicationDetails.getApplicationStatus());
    }
}
