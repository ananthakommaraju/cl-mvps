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
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class QuestionIsOverDraftAlreadyExpiredTest {

    private ProductArrangement productArrangement;

    private List<ProductArrangementFacade> productArrangementFacadeList;

    private ProductArrangementFacade productArrangementFacade;

    private TestDataHelper testDataHelper;

    @Before
    public void setUp() throws Exception {
        testDataHelper = new TestDataHelper();
        productArrangementFacadeList = new ArrayList();
        productArrangement = mock(ProductArrangement.class);
        productArrangementFacade = new ProductArrangementFacade(productArrangement);
        productArrangementFacadeList.add(productArrangementFacade);

    }

    @Test
    public void checkOverDraftStatusReturnFalseIfEndDateIsLessThanZero() throws Exception {
        String testSortCode = testDataHelper.TEST_SORT_CODE;
        when(productArrangement.getSortCode()).thenReturn(testSortCode);
        String testAccountNumber = testDataHelper.TEST_ACCOUNT_NUMBER;
        when(productArrangement.getAccountNumber()).thenReturn(testAccountNumber);
        OvrdrftDtls ovrdrftDtls = mock(OvrdrftDtls.class);
        when(ovrdrftDtls.getEndDate()).thenReturn(testDataHelper.subtractFromCurrentDate(77));
        when(productArrangement.getOvrdrftDtls()).thenReturn(ovrdrftDtls);
        boolean ask = QuestionIsOverDraftAlreadyExpired.pose()
                .givenAnAccountNumber(testAccountNumber)
                .givenASortCode(testSortCode)
                .givenAProductList(productArrangementFacadeList)
                .ask();

        assertFalse(ask);
    }

    @Test
    public void checkOverDraftStatusReturnTrueIfEndDateIsGreaterThanZero() throws Exception {
        String testSortCode = testDataHelper.TEST_SORT_CODE;
        when(productArrangement.getSortCode()).thenReturn(testSortCode);
        String testAccountNumber = testDataHelper.TEST_ACCOUNT_NUMBER;
        when(productArrangement.getAccountNumber()).thenReturn(testAccountNumber);
        OvrdrftDtls ovrdrftDtls = mock(OvrdrftDtls.class);
        when(ovrdrftDtls.getEndDate()).thenReturn(testDataHelper.addToCurrentDate(77));
        when(productArrangement.getOvrdrftDtls()).thenReturn(ovrdrftDtls);
        boolean ask = QuestionIsOverDraftAlreadyExpired.pose()
                .givenAnAccountNumber(testAccountNumber)
                .givenASortCode(testSortCode)
                .givenAProductList(productArrangementFacadeList)
                .ask();

        assertTrue(ask);
    }
}