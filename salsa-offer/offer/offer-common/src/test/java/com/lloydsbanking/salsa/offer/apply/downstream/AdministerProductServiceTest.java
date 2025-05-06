package com.lloydsbanking.salsa.offer.apply.downstream;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.prd.service.AdministerProductSelectionService;
import com.lloydsbanking.salsa.logging.application.ProductTraceLog;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.Product;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class AdministerProductServiceTest {
    private AdministerProductService administerProductService;

    @Before
    public void setUp(){
        administerProductService = new AdministerProductService();
        administerProductService.administerProductSelectionService = mock(AdministerProductSelectionService.class);
        administerProductService.productTraceLog = mock(ProductTraceLog.class);
    }
    @Test
    public void testCallAdministerProductServiceWhenOfferedProductListExists() throws InternalServiceErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg {
        String productEligibilityTypeCode = "CO_HOLD";
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        financeServiceArrangement.setAssociatedProduct(new Product());
        financeServiceArrangement.getAssociatedProduct().setProductIdentifier("123");

        financeServiceArrangement.getOfferedProducts().add(new Product());
        financeServiceArrangement.getOfferedProducts().get(0).setProductIdentifier("124");

        financeServiceArrangement.getExistingProducts().add(new Product());
        financeServiceArrangement.getExistingProducts().get(0).setProductIdentifier("3");

        when(administerProductService.productTraceLog.getProdListTraceEventMessage(any(List.class), any(String.class))).thenReturn("Existing credit cards product list");
        when(administerProductService.productTraceLog.getProductTraceEventMessage(any(Product.class), any(String.class))).thenReturn("ASM Product");
        when(administerProductService.administerProductSelectionService.administerProductSelection(any(ArrayList.class),any(Product.class),any(String.class))).thenReturn("CO_HOLD");
        administerProductService.callAdministerProductSelectionService(financeServiceArrangement,productEligibilityTypeCode);

        assertEquals("124",financeServiceArrangement.getOfferedProducts().get(0).getProductIdentifier());
    }

    @Test
    public void testCallAdministerProductServiceWhenOfferedProductListDoNotExist() throws InternalServiceErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg {
        String productEligibilityTypeCode = "CO_HOLD";
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        financeServiceArrangement.setAssociatedProduct(new Product());
        financeServiceArrangement.getAssociatedProduct().setProductIdentifier("123");

        financeServiceArrangement.getOfferedProducts().add(new Product());
        financeServiceArrangement.getOfferedProducts().get(0).setProductIdentifier("124");

        financeServiceArrangement.getExistingProducts().add(new Product());
        financeServiceArrangement.getExistingProducts().get(0).setProductIdentifier("3");

        when(administerProductService.productTraceLog.getProdListTraceEventMessage(any(List.class), any(String.class))).thenReturn("Existing credit cards product list");
        when(administerProductService.productTraceLog.getProductTraceEventMessage(any(Product.class), any(String.class))).thenReturn("ASM Product");
        when(administerProductService.administerProductSelectionService.administerProductSelection(any(ArrayList.class),any(Product.class),any(String.class))).thenReturn(null);
        administerProductService.callAdministerProductSelectionService(financeServiceArrangement,productEligibilityTypeCode);

        assertEquals(0,financeServiceArrangement.getOfferedProducts().size());
        assertEquals("1004",financeServiceArrangement.getApplicationStatus());
        assertEquals("INELIGIBLE",financeServiceArrangement.getApplicationType());
    }

}
