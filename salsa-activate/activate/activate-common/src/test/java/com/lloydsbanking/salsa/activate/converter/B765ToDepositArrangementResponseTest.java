package com.lloydsbanking.salsa.activate.converter;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydstsb.ib.wsbridge.account.StB765BAccCreateAccount;
import lib_sim_bo.businessobjects.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Category(UnitTest.class)
public class B765ToDepositArrangementResponseTest {
    B765ToDepositArrangementResponse b765ToDepositArrangementResponse;

    DepositArrangement depositArrangement;

    StB765BAccCreateAccount response;

    TestDataHelper testDataHelper;
    ActivateProductArrangementResponse activateResponse;

    @Before
    public void setUp() {

        b765ToDepositArrangementResponse = new B765ToDepositArrangementResponse();
        testDataHelper = new TestDataHelper();
        response = testDataHelper.createResponseB765("6574321", "773315");
        depositArrangement = testDataHelper.createDepositArrangementResp();
        activateResponse = new ActivateProductArrangementResponse();
        ProductArrangement productArrangement = new ProductArrangement();
        Organisation organisation = new Organisation();
        Customer customer = new Customer();
        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisation.getHasOrganisationUnits().add(organisationUnit);
        productArrangement.setPrimaryInvolvedParty(customer);
        productArrangement.setFinancialInstitution(organisation);
        activateResponse.setProductArrangement(productArrangement);

    }

    @Test
    public void testCreateDepositArrangementResponse() {
        b765ToDepositArrangementResponse.createDepositArrangementResponse(response, depositArrangement, activateResponse);
        assertEquals("sCode", depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode());
        assertNull(depositArrangement.getAccountNumber());
        assertEquals("227323270", activateResponse.getProductArrangement().getPrimaryInvolvedParty().getCbsCustomerNumber());
        assertEquals("227323270", depositArrangement.getPrimaryInvolvedParty().getCustomerNumber());
    }
}
