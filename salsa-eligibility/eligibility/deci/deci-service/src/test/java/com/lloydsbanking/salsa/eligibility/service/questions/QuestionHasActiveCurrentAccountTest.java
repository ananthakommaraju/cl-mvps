package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class QuestionHasActiveCurrentAccountTest {
    List<ProductArrangementFacade> productArrangementsList;

    private ProductArrangement productArrangement;

    @Before
    public void setUp() {
        this.productArrangementsList = new ArrayList<>();
    }

    @Test
    public void testHasActiveCurrentAccountReturnsTrue() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setAssociatedProduct(new Product());
        productArrangement.getAssociatedProduct().setStatusCode("001");
        productArrangement.setLifecycleStatus("Effective");

        ProductArrangementFacade productArrangementFacade = new ProductArrangementFacade(productArrangement);
        productArrangementsList.add(productArrangementFacade);
        boolean ask = QuestionHasActiveCurrentAccount.pose().givenAProductList(productArrangementsList).ask();
        assertTrue(ask);
    }

    @Test
    public void testHasActiveCurrentAccountReturnsFalse() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        productArrangement = new ProductArrangement();
        productArrangement.setAssociatedProduct(new Product());
        productArrangement.getAssociatedProduct().setStatusCode("002");
        productArrangement.setLifecycleStatus("Dormant");
        ProductArrangementFacade productArrangementFacade = new ProductArrangementFacade(productArrangement);
        productArrangementsList.add(productArrangementFacade);
        boolean ask = QuestionHasActiveCurrentAccount.pose().givenAProductList(productArrangementsList).ask();

        assertFalse(ask);

    }

    @Test
    public void testHasActiveCurrentAccountReturnsTrueForLifeCycleStatusNull() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        productArrangement = new ProductArrangement();
        productArrangement.setAssociatedProduct(new Product());
        productArrangement.getAssociatedProduct().setStatusCode("001");
        productArrangement.setLifecycleStatus(null);
        ProductArrangementFacade productArrangementFacade = new ProductArrangementFacade(productArrangement);
        productArrangementsList.add(productArrangementFacade);
        boolean ask = QuestionHasActiveCurrentAccount.pose().givenAProductList(productArrangementsList).ask();

        assertTrue(ask);
    }
}