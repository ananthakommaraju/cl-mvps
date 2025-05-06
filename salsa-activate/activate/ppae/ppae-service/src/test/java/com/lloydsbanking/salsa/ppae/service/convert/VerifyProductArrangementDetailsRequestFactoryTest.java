package com.lloydsbanking.salsa.ppae.service.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.VerifyProductArrangementDetailsRequest;
import lib_sim_bo.businessobjects.AccountDetails;
import lib_sim_bo.businessobjects.BalanceTransfer;
import lib_sim_bo.businessobjects.Customer;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@Category(UnitTest.class)
public class VerifyProductArrangementDetailsRequestFactoryTest {

    VerifyProductArrangementDetailsRequestFactory requestFactory;
    TestDataHelper testDataHelper;

    @Before
    public void setUp() throws DatatypeConfigurationException {
        requestFactory = new VerifyProductArrangementDetailsRequestFactory();
        testDataHelper = new TestDataHelper();
        requestFactory.postalAddressHelper = mock(PostalAddressHelper.class);
    }

    @Test
    public void convertTest() {
        VerifyProductArrangementDetailsRequest request = requestFactory.convert(new BalanceTransfer(), new Customer());
        assertTrue(request.getArrangementToVerify().isEmpty());
    }

    @Test
    public void convertTestWithBalanceTransferAndCustomer() {
        BalanceTransfer balanceTransfer = new BalanceTransfer();
        balanceTransfer.setCreditCardNumber("123456789");
        balanceTransfer.setExpiryDate("122017");
        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setAccountNumber("1234567");
        accountDetails.setSortCode("7894");
        balanceTransfer.setCurrentAccountDetails(accountDetails);

        Customer customer = testDataHelper.createFinanceArrangementForCC().getPrimaryInvolvedParty();
        VerifyProductArrangementDetailsRequest request = requestFactory.convert(balanceTransfer, customer);
        assertEquals(2, request.getArrangementToVerify().size());
    }

    @Test
    public void convertTestWithoutCurrentAddress() {
        BalanceTransfer balanceTransfer = new BalanceTransfer();
        balanceTransfer.setCreditCardNumber("123456789");
        balanceTransfer.setExpiryDate("122017");
        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setAccountNumber("1234567");
        accountDetails.setSortCode("7894");
        balanceTransfer.setCurrentAccountDetails(accountDetails);

        Customer customer = testDataHelper.createFinanceArrangementForCC().getPrimaryInvolvedParty();
        customer.getPostalAddress().clear();
        VerifyProductArrangementDetailsRequest request = requestFactory.convert(balanceTransfer, customer);
        assertEquals(2, request.getArrangementToVerify().size());
    }
}
