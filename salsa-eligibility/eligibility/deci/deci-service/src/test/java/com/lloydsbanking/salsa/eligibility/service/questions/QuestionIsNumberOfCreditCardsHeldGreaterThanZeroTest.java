package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.prd.service.AdministerProductSelectionService;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.logging.application.ProductTraceLog;
import lib_sim_bo.businessobjects.ExtraConditions;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class QuestionIsNumberOfCreditCardsHeldGreaterThanZeroTest {
    private ProductArrangement productArrangement;

    private List<ProductArrangementFacade> productArrangementFacadeList;

    private ProductArrangementFacade productArrangementFacade;
    protected Product associatedProduct;
    private AdministerProductSelectionService administerProductSelectionService;
    private ProductTraceLog productTraceLog;
    List<Product> ccProductList;
    Product product;


    @Before
    public void setUp() throws Exception {
        productArrangementFacadeList = new ArrayList();
        ccProductList = new ArrayList<>();
        productArrangement = mock(ProductArrangement.class);
        productArrangementFacade = new ProductArrangementFacade(productArrangement);
        productArrangementFacadeList.add(productArrangementFacade);
        administerProductSelectionService = mock(AdministerProductSelectionService.class);
        productTraceLog = mock(ProductTraceLog.class);
        associatedProduct = new Product();
    }

    @Test
    public void testGetNumberOfCreditCardsHeldReturnsZero() throws EligibilityException {
        Product product = mock(Product.class);
        when(productArrangement.getAssociatedProduct()).thenReturn(product);
        productArrangement.setAssociatedProduct(product);
        when(product.getProductType()).thenReturn("6");
        boolean ask = QuestionIsNumberOfCreditCardsHeldGreaterThanZero.pose().givenProductTraceLogInstance(productTraceLog).givenAProductList(productArrangementFacadeList).ask();
        assertFalse(ask);
    }

    @Test
    public void testGetNumberOfCreditCardsHeldReturnsOneWithEligible() throws EligibilityException, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg {
        administerProductSelectionService = mock(AdministerProductSelectionService.class);
        Product product = mock(Product.class);
        when(product.getProductType()).thenReturn("3");
        when(productArrangement.getAssociatedProduct()).thenReturn(product);
        ccProductList.add(product);
        Product associatedProduct = new Product();

        when(administerProductSelectionService.administerProductSelection(ccProductList, associatedProduct, null)).thenReturn("ELIGIBLE");

        boolean ask = QuestionIsNumberOfCreditCardsHeldGreaterThanZero.pose()
                .givenProductTraceLogInstance(productTraceLog)
                .givenAnInstanceAdministerProductSelection(administerProductSelectionService)
                .givenAssociatedProduct(associatedProduct)
                .givenAProductList(productArrangementFacadeList).ask();
        assertFalse(ask);
    }

    @Test
    public void testGetNumberOfCreditCardsHeldReturnsOneForIneligibile() throws EligibilityException, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg {
        administerProductSelectionService = mock(AdministerProductSelectionService.class);
        Product product = mock(Product.class);
        when(product.getProductType()).thenReturn("3");
        when(productArrangement.getAssociatedProduct()).thenReturn(product);
        ccProductList.add(product);
        Product associatedProduct = new Product();

        when(administerProductSelectionService.administerProductSelection(ccProductList, associatedProduct, null)).thenReturn("INELIGIBLE");

        boolean ask = QuestionIsNumberOfCreditCardsHeldGreaterThanZero.pose()
                .givenProductTraceLogInstance(productTraceLog)
                .givenAnInstanceAdministerProductSelection(administerProductSelectionService)
                .givenAssociatedProduct(associatedProduct)
                .givenAProductList(productArrangementFacadeList).ask();
        assertTrue(ask);
    }

    @Test
    public void testGetNumberOfCreditCardsHeldReturnsOneForNotIneligibileAndEligible() throws EligibilityException, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg {
        administerProductSelectionService = mock(AdministerProductSelectionService.class);
        Product product = mock(Product.class);
        when(product.getProductType()).thenReturn("3");
        when(productArrangement.getAssociatedProduct()).thenReturn(product);
        ccProductList.add(product);
        Product associatedProduct = new Product();

        when(administerProductSelectionService.administerProductSelection(ccProductList, associatedProduct, null)).thenReturn("NotEligible");

        boolean ask = QuestionIsNumberOfCreditCardsHeldGreaterThanZero.pose()
                .givenProductTraceLogInstance(productTraceLog)
                .givenExtraCondition(new ExtraConditions())
                .givenAnInstanceAdministerProductSelection(administerProductSelectionService)
                .givenAssociatedProduct(associatedProduct)
                .givenAProductList(productArrangementFacadeList)
                .ask();
        assertFalse(ask);
    }

    @Test
    public void testGetNumberOfCreditCardsHeldWhenProductArrangementsIsEmpty() throws EligibilityException {
        productArrangementFacadeList = new ArrayList();
        productArrangementFacadeList.clear();
        boolean ask = QuestionIsNumberOfCreditCardsHeldGreaterThanZero.pose().givenProductTraceLogInstance(productTraceLog).givenAProductList(productArrangementFacadeList).ask();
        assertFalse(ask);
    }

}