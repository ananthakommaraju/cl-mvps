package com.lloydsbanking.salsa.opacc.service.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.opacc.service.TestDataHelper;
import lib_sim_bo.businessobjects.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class OfferProductArrangementResponseFactoryTest {

    OfferProductArrangementResponseFactory offerProductArrangementResponseFactory;
    TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        offerProductArrangementResponseFactory = new OfferProductArrangementResponseFactory();
        testDataHelper = new TestDataHelper();
    }

    @Test
    public void testSetOfferProductArrangementResponse() {
        FinanceServiceArrangement requestFinanceServiceArrangement = getFinanceServiceArrangement();
        offerProductArrangementResponseFactory.setOfferProductArrangementResponse(requestFinanceServiceArrangement, new FinanceServiceArrangement(), false);
    }

    @Test
    public void testSetOfferProductArrangementResponseWithDuplicateTrue() {
        FinanceServiceArrangement requestFinanceServiceArrangement = getFinanceServiceArrangement();
        offerProductArrangementResponseFactory.setOfferProductArrangementResponse(requestFinanceServiceArrangement, new FinanceServiceArrangement(), true);
    }

    @Test
    public void testSetOfferProductArrangementResponseForCrossSell() {
        FinanceServiceArrangement requestFinanceServiceArrangement = getFinanceServiceArrangement();
        offerProductArrangementResponseFactory.setOfferProductArrangementResponseForCrossSell(requestFinanceServiceArrangement, new FinanceServiceArrangement());
    }

    private FinanceServiceArrangement getFinanceServiceArrangement() {
        FinanceServiceArrangement requestFinanceServiceArrangement = new FinanceServiceArrangement();
        Customer customer = new Customer();
        customer.getCustomerScore().add(new CustomerScore());
        customer.getAuditData().add(new AuditData());
        customer.setIndividualIdentifier("1245");
        customer.setCustomerIdentifier("2235");
        customer.setNewCustomerIndicator(true);
        customer.setIsAuthCustomer(true);
        customer.setCustomerSegment("4545");
        customer.setCidPersID("14587");
        requestFinanceServiceArrangement.setArrangementType("INELIGIBLE");
        requestFinanceServiceArrangement.setIsDirectDebitRequired(true);
        requestFinanceServiceArrangement.setApplicationStatus("1002");
        requestFinanceServiceArrangement.setArrangementId("00001245");
        requestFinanceServiceArrangement.setPrimaryInvolvedParty(customer);
        requestFinanceServiceArrangement.setAssociatedProduct(new Product());
        requestFinanceServiceArrangement.getOfferedProducts().add(new Product());
        requestFinanceServiceArrangement.getExistingProducts().add(new Product());
        return requestFinanceServiceArrangement;
    }

}
