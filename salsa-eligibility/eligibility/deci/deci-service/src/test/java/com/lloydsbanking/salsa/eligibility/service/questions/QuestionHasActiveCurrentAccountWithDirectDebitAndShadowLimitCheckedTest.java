package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.ShadowLimitRetriever;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import lb_gbo_sales.Customer;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.businessobjects.ProductArrangementLifecycleStatus;
import lb_gbo_sales.messages.RequestHeader;
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
public class QuestionHasActiveCurrentAccountWithDirectDebitAndShadowLimitCheckedTest {

    private TestDataHelper testDataHelper;


    private List<ProductArrangementFacade> productArrangementsList;

    private ProductArrangementFacade productArrangementFacade;

    @Deprecated
    private ProductArrangement productArrangement;

    @Before
    public void before() {
        productArrangementsList = new ArrayList<>();
        productArrangement = mock(ProductArrangement.class);
        testDataHelper = new TestDataHelper();
    }

    @Test
    public void hasActiveCurrentAccountWithDirectDebitAndShadowLimitGreaterThanThresholdReturnTrue() throws Exception {
        RequestHeader header = mock(RequestHeader.class);
        ProductArrangement arrangement = mock(ProductArrangement.class);
        when(arrangement.getArrangementType()).thenReturn("CURRENT");
        when(arrangement.getSortCode()).thenReturn(testDataHelper.TEST_SORT_CODE);
        when(arrangement.getLifecycleStatus()).thenReturn(ProductArrangementLifecycleStatus.EFFECTIVE);

        ArrayList<String> relatedEvenList = new ArrayList<String>();
        relatedEvenList.add("37");
        relatedEvenList.add("54");
        when(arrangement.getRelatedEvents()).thenReturn(relatedEvenList);

        ArrayList<Customer> customers = new ArrayList<Customer>();
        Customer customer = new Customer();
        customer.setPartyId("772519");
        customers.add(customer);
        when(arrangement.getParticipantCusomters()).thenReturn(customers);
        productArrangementFacade = new ProductArrangementFacade(arrangement);
        productArrangementsList.add(productArrangementFacade);

        AppGroupRetriever appGroupRetriever = mock(AppGroupRetriever.class);
        ShadowLimitRetriever shadowLimitRetriever = mock(ShadowLimitRetriever.class);


        String appGroup = "30";
        when(appGroupRetriever.callRetrieveCBSAppGroup(header, testDataHelper.TEST_SORT_CODE, false)).thenReturn(appGroup);
        when(shadowLimitRetriever.getShadowLimit(header, testDataHelper.TEST_SORT_CODE, "772519", appGroup)).thenReturn("338");
        ;

        boolean ask = QuestionHasActiveCurrentAccountWithDirectDebitAndShadowLimitChecked.pose()
                .givenShadowLimitRetrieverClientInstance(shadowLimitRetriever)
                .givenAProductList(productArrangementsList)
                .givenAValue("0:37")
                .givenRequestHeader(header)
                .givenAppGroupRetrieverClientInstance(appGroupRetriever)
                .ask();

        assertTrue(ask);

    }

    @Test
    public void hasActiveCurrentAccountWithDirectDebitAndShadowLimitGreaterLessThanThresholdReturnFalse() throws Exception {
        RequestHeader header = mock(RequestHeader.class);
        ProductArrangement arrangement = mock(ProductArrangement.class);
        when(arrangement.getArrangementType()).thenReturn("CURRENT");
        when(arrangement.getSortCode()).thenReturn(testDataHelper.TEST_SORT_CODE);
        when(arrangement.getLifecycleStatus()).thenReturn(ProductArrangementLifecycleStatus.EFFECTIVE);

        ArrayList<String> relatedEvenList = new ArrayList<String>();
        relatedEvenList.add("37");
        relatedEvenList.add("54");
        when(arrangement.getRelatedEvents()).thenReturn(relatedEvenList);

        ArrayList<Customer> customers = new ArrayList<Customer>();
        Customer customer = new Customer();
        customer.setPartyId("772519");
        customers.add(customer);
        when(arrangement.getParticipantCusomters()).thenReturn(customers);
        productArrangementFacade = new ProductArrangementFacade(arrangement);
        productArrangementsList.add(productArrangementFacade);

        AppGroupRetriever appGroupRetriever = mock(AppGroupRetriever.class);

        ShadowLimitRetriever shadowLimitRetriever = mock(ShadowLimitRetriever.class);

        String appGroup = "30";
        when(appGroupRetriever.callRetrieveCBSAppGroup(header, testDataHelper.TEST_SORT_CODE, false)).thenReturn(appGroup);
        when(shadowLimitRetriever.getShadowLimit(header, testDataHelper.TEST_SORT_CODE, testDataHelper.TEST_CUSTOMER_ID, appGroup)).thenReturn("36");

        boolean ask = QuestionHasActiveCurrentAccountWithDirectDebitAndShadowLimitChecked.pose()
                .givenShadowLimitRetrieverClientInstance(shadowLimitRetriever)
                .givenAProductList(productArrangementsList)
                .givenAValue("7:37")
                .givenRequestHeader(header)
                .givenAppGroupRetrieverClientInstance(appGroupRetriever)
                .ask();

        assertFalse(ask);

    }

    @Test
    public void hasActiveCurrentAccountWithDirectDebitAndShadowLimitGreaterGreaterThanThresholdAndAccountIsDormantReturnFalse() throws Exception {
        RequestHeader header = mock(RequestHeader.class);
        ProductArrangement arrangement = mock(ProductArrangement.class);
        when(arrangement.getArrangementType()).thenReturn("CURRENT");
        when(arrangement.getSortCode()).thenReturn(testDataHelper.TEST_SORT_CODE);
        when(arrangement.getLifecycleStatus()).thenReturn(ProductArrangementLifecycleStatus.EFFECTIVE);

        ArrayList<String> relatedEvenList = new ArrayList<String>();
        relatedEvenList.add("37");
        relatedEvenList.add("54");
        when(arrangement.getRelatedEvents()).thenReturn(relatedEvenList);

        ArrayList<Customer> customers = new ArrayList<Customer>();
        Customer customer = new Customer();
        customer.setPartyId("772519");
        customers.add(customer);
        when(arrangement.getParticipantCusomters()).thenReturn(customers);
        productArrangementFacade = new ProductArrangementFacade(arrangement);
        productArrangementsList.add(productArrangementFacade);

        AppGroupRetriever appGroupRetriever = mock(AppGroupRetriever.class);

        ShadowLimitRetriever shadowLimitRetriever = mock(ShadowLimitRetriever.class);

        String appGroup = "30";
        when(appGroupRetriever.callRetrieveCBSAppGroup(header, testDataHelper.TEST_SORT_CODE, false)).thenReturn(appGroup);
        when(shadowLimitRetriever.getShadowLimit(header, testDataHelper.TEST_SORT_CODE, testDataHelper.TEST_CUSTOMER_ID, appGroup)).thenReturn("37");

        boolean ask = QuestionHasActiveCurrentAccountWithDirectDebitAndShadowLimitChecked.pose()
                .givenShadowLimitRetrieverClientInstance(shadowLimitRetriever)
                .givenAProductList(productArrangementsList)
                .givenAValue("0:38")
                .givenRequestHeader(header)
                .givenAppGroupRetrieverClientInstance(appGroupRetriever)
                .ask();

        assertFalse(ask);

    }
}