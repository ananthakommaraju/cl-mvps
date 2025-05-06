package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;

import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class QuestionIsCurrentAccountTest {

    private static final String PRODUCT_TYPE_CURRENT_ACCOUNT = "1";
    private static final String EXT_SYS_ID_CURRENT_ACCOUNT = "00004";
    private static final String PRODUCT_TYPE_OTHER_ACCOUNT = "type_other_account";
    private static final String EXT_SYS_ID_OTHER_ACCOUNT = "ext_id_other_account";

    private List<ProductArrangementFacade> productArrangementFacadeList;
    private ProductArrangementFacade currentAccountArrangementFacade;
    private ProductArrangementFacade notCurrentAccountArrangementFacade;
    private ExtSysProdIdentifier extSysProdIdentifier;
    private List externalSystemIdentifier;
    private Product notCurrentAccountProduct;
    private Product currentAccountProduct;

    @Before
    public void before(){
        productArrangementFacadeList = new ArrayList<>();

        externalSystemIdentifier = mock(List.class);
        extSysProdIdentifier = mock(ExtSysProdIdentifier.class);
        when(externalSystemIdentifier.get(0)).thenReturn(extSysProdIdentifier);

        ProductArrangement currentAccountArrangement = mock(ProductArrangement.class);
        currentAccountArrangementFacade = spy(new ProductArrangementFacade(currentAccountArrangement));
        currentAccountProduct = mock(Product.class);
        when(currentAccountArrangementFacade.getAssociatedProduct()).thenReturn(currentAccountProduct);
        when(currentAccountProduct.getProductType()).thenReturn(PRODUCT_TYPE_CURRENT_ACCOUNT);
        when(currentAccountProduct.getExternalSystemProductIdentifier()).thenReturn(externalSystemIdentifier);

        ProductArrangement nonCurrentAccountArrangement = mock(ProductArrangement.class);
        notCurrentAccountArrangementFacade = spy(new ProductArrangementFacade(nonCurrentAccountArrangement));
        notCurrentAccountProduct = mock(Product.class);
        when(notCurrentAccountArrangementFacade.getAssociatedProduct()).thenReturn(notCurrentAccountProduct);
        when(notCurrentAccountProduct.getProductType()).thenReturn(PRODUCT_TYPE_OTHER_ACCOUNT);
        when(notCurrentAccountProduct.getExternalSystemProductIdentifier()).thenReturn(externalSystemIdentifier);
    }

    @Test
    public void returnsFalseWhenNoAccountsBothZones() throws Exception {
        boolean ask = QuestionIsCurrentAccount.pose().givenAProductList(productArrangementFacadeList).ask();
        assertFalse(ask);
    }

    @Test
    public void returnsFalseWhenNoCurrentAccountWZ() throws Exception {
        productArrangementFacadeList.add(null);
        productArrangementFacadeList.add(notCurrentAccountArrangementFacade);
        productArrangementFacadeList.add(notCurrentAccountArrangementFacade);
        productArrangementFacadeList.add(notCurrentAccountArrangementFacade);
        productArrangementFacadeList.add(null);

        boolean ask = QuestionIsCurrentAccount.pose().givenAProductList(productArrangementFacadeList).ask();

        verify(notCurrentAccountProduct, times(3)).getProductType();
        assertFalse(ask);
    }

    @Test
    public void returnsFalseWhenOneCurrentAccountButExternalSystemIdentifierNullWZ() throws Exception {
        when(externalSystemIdentifier.get(0)).thenReturn(null);
        productArrangementFacadeList.add(notCurrentAccountArrangementFacade);
        productArrangementFacadeList.add(currentAccountArrangementFacade);
        productArrangementFacadeList.add(notCurrentAccountArrangementFacade);

        boolean ask = QuestionIsCurrentAccount.pose().givenAProductList(productArrangementFacadeList).ask();

        verify(notCurrentAccountProduct, times(2)).getProductType();
        verify(currentAccountProduct, times(1)).getProductType();
        assertFalse(ask);
    }

    @Test
    public void returnsFalseWhenOneCurrentAccountButNotExternalAccountWZ() throws Exception {
        when(extSysProdIdentifier.getSystemCode()).thenReturn(EXT_SYS_ID_OTHER_ACCOUNT);
        productArrangementFacadeList.add(notCurrentAccountArrangementFacade);
        productArrangementFacadeList.add(currentAccountArrangementFacade);
        productArrangementFacadeList.add(notCurrentAccountArrangementFacade);

        boolean ask = QuestionIsCurrentAccount.pose().givenAProductList(productArrangementFacadeList).ask();

        verify(notCurrentAccountProduct, times(2)).getProductType();
        verify(currentAccountProduct, times(1)).getProductType();
        assertFalse(ask);
    }

    @Test
    public void returnsTrueWhenOneCurrentAccountAndIsExternalAccountWZ() throws Exception {
        when(extSysProdIdentifier.getSystemCode()).thenReturn(EXT_SYS_ID_CURRENT_ACCOUNT);
        productArrangementFacadeList.add(notCurrentAccountArrangementFacade);
        productArrangementFacadeList.add(currentAccountArrangementFacade);
        productArrangementFacadeList.add(notCurrentAccountArrangementFacade);

        boolean ask = QuestionIsCurrentAccount.pose().givenAProductList(productArrangementFacadeList).ask();

        verify(notCurrentAccountProduct, times(1)).getProductType();
        verify(currentAccountProduct, times(1)).getProductType();
        assertTrue(ask);
    }

    @Test
    public void returnsFalseAlwaysForBlueZoneArrangements() throws Exception {
        lb_gbo_sales.ProductArrangement blueZoneArrangement = new lb_gbo_sales.ProductArrangement();
        ProductArrangementFacade blueZoneArrangementFacade = spy(new ProductArrangementFacade(blueZoneArrangement));
        productArrangementFacadeList.add(blueZoneArrangementFacade);
        productArrangementFacadeList.add(blueZoneArrangementFacade);

        boolean ask = QuestionIsCurrentAccount.pose().givenAProductList(productArrangementFacadeList).ask();

        verify(blueZoneArrangementFacade, times(2)).getAssociatedProduct();
        assertFalse(ask);
    }

    @Test(expected = EligibilityException.class)
    public void testAskThrowsEligibilityExceptionForExtSysProductIdListEmpty() throws EligibilityException {
        when(currentAccountProduct.getExternalSystemProductIdentifier()).thenReturn(new ArrayList<ExtSysProdIdentifier>());
        productArrangementFacadeList.add(notCurrentAccountArrangementFacade);
        productArrangementFacadeList.add(currentAccountArrangementFacade);
        productArrangementFacadeList.add(notCurrentAccountArrangementFacade);

        QuestionIsCurrentAccount.pose().givenAProductList(productArrangementFacadeList).ask();

    }

}