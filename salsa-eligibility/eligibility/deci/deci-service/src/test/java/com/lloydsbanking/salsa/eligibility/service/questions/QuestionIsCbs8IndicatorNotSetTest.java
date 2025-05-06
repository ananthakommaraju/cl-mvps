package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.CBSIndicatorRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.soap.cbs.e184.objects.E184Resp;
import com.lloydsbanking.salsa.soap.cbs.e184.objects.StandardIndicators1Gp;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class QuestionIsCbs8IndicatorNotSetTest {

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
    public void isCbs8IndicatorNotSetShouldReturnFalse() throws Exception {
        E184Resp e184Resp = testDataHelper.createE184Response(8);

        when(productArrangement.getArrangementType()).thenReturn("CURRENT");
        when(productArrangement.getSortCode()).thenReturn(testDataHelper.TEST_SORT_CODE);
        when(productArrangement.getAccountNumber()).thenReturn(testDataHelper.TEST_ACCOUNT_NUMBER);
        when(productArrangement.isCapAccountRestricted()).thenReturn(false);

        AppGroupRetriever appGroupRetriever = mock(AppGroupRetriever.class);
        CBSIndicatorRetriever cbsIndicatorRetriever = mock(CBSIndicatorRetriever.class);

        String appGroup = "01";
        when(appGroupRetriever.callRetrieveCBSAppGroup(any(RequestHeader.class), eq(testDataHelper.TEST_SORT_CODE), any(boolean.class))).thenReturn(appGroup);
        List<StandardIndicators1Gp> standardIndicators1Gp = e184Resp.getIndicator1Gp().getStandardIndicators1Gp();
        when(cbsIndicatorRetriever.getCbsIndicator(any(RequestHeader.class), eq(testDataHelper.TEST_SORT_CODE), eq(testDataHelper.TEST_ACCOUNT_NUMBER), eq(appGroup))).thenReturn(standardIndicators1Gp);

        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRuleParamValue("8");

        boolean ask = QuestionIsCbs8IndicatorNotSet.pose()
                .givenAProductList(productArrangementFacadeList)
                .givenAValue("8")
                .givenAppGroupRetrieverClientInstance(appGroupRetriever)
                .givenCbsIndicatorRetriever(cbsIndicatorRetriever)
                .ask();
        assertFalse(ask);
        verify(productArrangement, times(1)).getArrangementType();
        verify(productArrangement, times(1)).getSortCode();
        verify(productArrangement, times(1)).getAccountNumber();
        verify(productArrangement, times(2)).isCapAccountRestricted();
        verify(appGroupRetriever, times(1)).callRetrieveCBSAppGroup(any(RequestHeader.class), eq(testDataHelper.TEST_SORT_CODE), any(boolean.class));
        verify(cbsIndicatorRetriever, times(1)).getCbsIndicator(any(RequestHeader.class), eq(testDataHelper.TEST_SORT_CODE), eq(testDataHelper.TEST_ACCOUNT_NUMBER), eq(appGroup));

    }

    @Test
    public void isCbs8IndicatorNotSetShouldReturnTrue() throws Exception {

        E184Resp e184Resp = testDataHelper.createE184Response(123);

        when(productArrangement.getArrangementType()).thenReturn("CURRENT");
        when(productArrangement.getSortCode()).thenReturn(testDataHelper.TEST_SORT_CODE);
        when(productArrangement.getAccountNumber()).thenReturn(testDataHelper.TEST_ACCOUNT_NUMBER);
        when(productArrangement.isCapAccountRestricted()).thenReturn(false);

        AppGroupRetriever appGroupRetriever = mock(AppGroupRetriever.class);
        CBSIndicatorRetriever cbsIndicatorRetriever = mock(CBSIndicatorRetriever.class);

        String appGroup = "01";
        when(appGroupRetriever.callRetrieveCBSAppGroup(any(RequestHeader.class), eq(testDataHelper.TEST_SORT_CODE), any(boolean.class))).thenReturn(appGroup);
        List<StandardIndicators1Gp> standardIndicators1Gp = e184Resp.getIndicator1Gp().getStandardIndicators1Gp();
        when(cbsIndicatorRetriever.getCbsIndicator(any(RequestHeader.class), eq(testDataHelper.TEST_SORT_CODE), eq(testDataHelper.TEST_ACCOUNT_NUMBER), eq(appGroup))).thenReturn(standardIndicators1Gp);

        boolean ask = QuestionIsCbs8IndicatorNotSet.pose()
                .givenAProductList(productArrangementFacadeList)
                .givenAValue("8")
                .givenAppGroupRetrieverClientInstance(appGroupRetriever)
                .givenCbsIndicatorRetriever(cbsIndicatorRetriever)
                .ask();
        assertTrue(ask);
        verify(productArrangement, times(1)).getArrangementType();
        verify(productArrangement, times(1)).getSortCode();
        verify(productArrangement, times(1)).getAccountNumber();
        verify(productArrangement, times(2)).isCapAccountRestricted();
        verify(appGroupRetriever, times(1)).callRetrieveCBSAppGroup(any(RequestHeader.class), eq(testDataHelper.TEST_SORT_CODE), any(boolean.class));
        verify(cbsIndicatorRetriever, times(1)).getCbsIndicator(any(RequestHeader.class), eq(testDataHelper.TEST_SORT_CODE), eq(testDataHelper.TEST_ACCOUNT_NUMBER), eq(appGroup));

    }

}