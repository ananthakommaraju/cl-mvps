package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.businessobjects.OvrdrftDtls;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class QuestionHasAccountTypeAndInValidThresholdTimeTest {
    public static final String CREDIT_CARD = "CREDIT_CARD";

    public static final String CURRENT = "CURRENT";

    private TestDataHelper testDataHelper;

    private List<ProductArrangementFacade> productArrangementsList;

    private ProductArrangementFacade productArrangementFacade;

    private ProductArrangement productArrangement;

    @Before
    public void before() {
        productArrangementsList = new ArrayList<>();
        testDataHelper = new TestDataHelper();
    }

    @Test
    public void hasAccountTypeAndInValidThresholdTimeShouldReturnFalse() throws Exception {
        ProductArrangement productArrangement = mock(ProductArrangement.class);
        when(productArrangement.getArrangementType()).thenReturn(CREDIT_CARD);
        when(productArrangement.getStartDate()).thenReturn(testDataHelper.addToCurrentDate(30));
        productArrangementFacade = new ProductArrangementFacade(productArrangement);
        productArrangementsList.add(productArrangementFacade);

        boolean ask = QuestionHasAccountTypeAndInValidThresholdTime.pose().givenAProductList(productArrangementsList).givenAValue("28").givenAnAccountType(CREDIT_CARD).ask();

        verify(productArrangement, times(0)).getOvrdrftDtls();
        verify(productArrangement, times(2)).getStartDate();
        assertFalse(ask);

    }

    @Test
    public void hasAccountTypeAndInValidThresholdTimeShouldReturnTrue() throws Exception {
        OvrdrftDtls ovrdrftDtls = mock(OvrdrftDtls.class);
        productArrangement = mock(ProductArrangement.class);
        when(ovrdrftDtls.getStartDate()).thenReturn(testDataHelper.subtractFromCurrentDate(25));
        when(productArrangement.getOvrdrftDtls()).thenReturn(ovrdrftDtls);
        when(productArrangement.getArrangementType()).thenReturn(CREDIT_CARD);
        when(productArrangement.getStartDate()).thenReturn(testDataHelper.subtractFromCurrentDate(25));
        productArrangementFacade = new ProductArrangementFacade(productArrangement);
        productArrangementsList.add(productArrangementFacade);
        boolean ask = QuestionHasAccountTypeAndInValidThresholdTime.pose().givenAProductList(productArrangementsList).givenAValue("28").givenAnAccountType(CREDIT_CARD).ask();

        verify(productArrangement, times(0)).getOvrdrftDtls();
        verify(productArrangement, times(2)).getStartDate();

        assertTrue(ask);

    }

    @Test
    public void hasAccountTypeAndInValidThresholdTimeShouldReturnTrueWhenArrangementTypeIsCurrentAccountAndThresholdIsGreaterThanStartDate() throws Exception {
        OvrdrftDtls ovrdrftDtls = mock(OvrdrftDtls.class);
        ProductArrangement productArrangement = mock(ProductArrangement.class);

        when(ovrdrftDtls.getStartDate()).thenReturn(testDataHelper.addToCurrentDate(25));
        when(productArrangement.getOvrdrftDtls()).thenReturn(ovrdrftDtls);
        when(productArrangement.getArrangementType()).thenReturn(CURRENT);
        when(productArrangement.getStartDate()).thenReturn(testDataHelper.subtractFromCurrentDate(25));
        ProductArrangementFacade productArrangementFacade = new ProductArrangementFacade(productArrangement);
        List<ProductArrangementFacade> productArrangementsList = new ArrayList<>();
        productArrangementsList.add(productArrangementFacade);
        boolean ask = QuestionHasAccountTypeAndInValidThresholdTime.pose().givenAProductList(productArrangementsList).givenAValue("28").givenAnAccountType(CURRENT).ask();

        verify(productArrangement, times(3)).getOvrdrftDtls();
        verify(ovrdrftDtls, times(2)).getStartDate();
        verify(productArrangement, times(0)).getStartDate();
        assertTrue(ask);

    }

    @Test
    public void hasAccountTypeAndInValidThresholdTimeShouldReturnFalseWhenArrangementTypeIsCurrentAccountAndThresholdIsLessThanStartDate() throws Exception {
        OvrdrftDtls ovrdrftDtls = mock(OvrdrftDtls.class);
        ProductArrangement productArrangement = mock(ProductArrangement.class);
        ovrdrftDtls.setStartDate(testDataHelper.addToCurrentDate(30));
        when(productArrangement.getArrangementType()).thenReturn(CURRENT);
        when(productArrangement.getOvrdrftDtls()).thenReturn(ovrdrftDtls);
        when(ovrdrftDtls.getStartDate()).thenReturn(testDataHelper.addToCurrentDate(30));
        ProductArrangementFacade productArrangementFacade = new ProductArrangementFacade(productArrangement);
        List<ProductArrangementFacade> productArrangementsList = new ArrayList<>();
        productArrangementsList.add(productArrangementFacade);
        boolean ask = QuestionHasAccountTypeAndInValidThresholdTime.pose().givenAProductList(productArrangementsList).givenAValue("28").givenAnAccountType(CURRENT).ask();

        verify(productArrangement, times(3)).getOvrdrftDtls();
        verify(ovrdrftDtls, times(2)).getStartDate();
        verify(productArrangement, times(0)).getStartDate();

        assertFalse(ask);

    }

    @Test
    public void hasAccountTypeAndInValidThresholdTimeShouldReturnFalseWhenArrangementTypeIsCurrentAccountAndThresholdIsLessThanStartDateForNegativeValue() throws Exception {
        OvrdrftDtls ovrdrftDtls = mock(OvrdrftDtls.class);
        ProductArrangement productArrangement = mock(ProductArrangement.class);
        ovrdrftDtls.setStartDate(testDataHelper.addToCurrentDate(-30));
        when(productArrangement.getArrangementType()).thenReturn(CURRENT);
        when(productArrangement.getOvrdrftDtls()).thenReturn(ovrdrftDtls);
        when(ovrdrftDtls.getStartDate()).thenReturn(testDataHelper.addToCurrentDate(-30));
        ProductArrangementFacade productArrangementFacade = new ProductArrangementFacade(productArrangement);
        List<ProductArrangementFacade> productArrangementsList = new ArrayList<>();
        productArrangementsList.add(productArrangementFacade);
        boolean ask = QuestionHasAccountTypeAndInValidThresholdTime.pose().givenAProductList(productArrangementsList).givenAValue("28").givenAnAccountType(CURRENT).ask();

        verify(productArrangement, times(3)).getOvrdrftDtls();
        verify(ovrdrftDtls, times(2)).getStartDate();
        verify(productArrangement, times(0)).getStartDate();

        assertFalse(ask);

    }
}