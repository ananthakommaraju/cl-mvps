package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.CheckBalanceRetriever;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.Mnemonics;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.header.GmoToGboRequestHeaderConverter;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import com.lloydsbanking.salsa.soap.cbs.e141.objects.E141Resp;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class QuestionIsCBSIndicatorPresentOnAllAccountsTest {
    private List<ProductArrangementFacade> productArrangementFacadeList;

    private ProductArrangementFacade productArrangementFacade;

    private TestDataHelper testDataHelper;

    @Before
    public void setUp() throws Exception {
        testDataHelper = new TestDataHelper();
        productArrangementFacadeList = new ArrayList();

    }

    @Test
    public void testIsCBSIndicatorPresentOnAllAccountsReturnsFalse() throws SalsaInternalResourceNotAvailableException, SalsaExternalServiceException, SalsaInternalServiceException, EligibilityException {
        GmoToGboRequestHeaderConverter headerConverter = new GmoToGboRequestHeaderConverter();
        RequestHeader header = testDataHelper.createEligibilityRequestHeader("LTB", TestDataHelper.TEST_INTERACTION, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        lb_gbo_sales.messages.RequestHeader gboHeader = headerConverter.convert(header);
        productArrangementFacade = new ProductArrangementFacade(createMockArrangement());
        productArrangementFacadeList.add(productArrangementFacade);
        int indicator = 665;

        AppGroupRetriever appGroupRetriever1 = mock(AppGroupRetriever.class);
        CheckBalanceRetriever checkBalanceRetriever = mock(CheckBalanceRetriever.class);
        when(appGroupRetriever1.callRetrieveCBSAppGroup(gboHeader, "111619", true)).thenReturn("01");
        when(checkBalanceRetriever.getCBSIndicators(gboHeader, "111619", "50001763", "01")).thenReturn(testDataHelper.getProdIndicators(indicator));

        boolean ask = QuestionIsCBSIndicatorPresentOnAllAccounts.pose()
                .givenCheckBalanceRetrieverClientInstance(checkBalanceRetriever)
                .givenEitherCurrentOrSavingsAccount(true)
                .givenAProductList(productArrangementFacadeList)
                .givenRequestHeader(gboHeader)
                .givenAValue("664")
                .givenAppGroupRetrieverClientInstance(appGroupRetriever1)
                .ask();

        assertFalse(ask);
        verify(checkBalanceRetriever, times(1)).getCBSIndicators(gboHeader, "111619", "50001763", "01");
        verify(appGroupRetriever1, times(1)).callRetrieveCBSAppGroup(gboHeader, "111619", true);
    }

    @Test
    public void testIsCBSIndicatorPresentOnAllAccountsReturnsFalseForMoreThanOneIndicator() throws SalsaInternalResourceNotAvailableException, SalsaExternalServiceException, SalsaInternalServiceException, EligibilityException {
        GmoToGboRequestHeaderConverter headerConverter = new GmoToGboRequestHeaderConverter();
        RequestHeader header = testDataHelper.createEligibilityRequestHeader("LTB", TestDataHelper.TEST_INTERACTION, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        lb_gbo_sales.messages.RequestHeader gboHeader = headerConverter.convert(header);
        productArrangementFacade = new ProductArrangementFacade(createMockArrangement());
        productArrangementFacadeList.add(productArrangementFacade);
        int indicator = 665;

        AppGroupRetriever appGroupRetriever1 = mock(AppGroupRetriever.class);
        CheckBalanceRetriever checkBalanceRetriever = mock(CheckBalanceRetriever.class);
        when(appGroupRetriever1.callRetrieveCBSAppGroup(gboHeader, "111619", true)).thenReturn("01");
        when(checkBalanceRetriever.getCBSIndicators(gboHeader, "111619", "50001763", "01")).thenReturn(testDataHelper.getProdIndicators(indicator));

        boolean ask = QuestionIsCBSIndicatorPresentOnAllAccounts.pose()
                .givenCheckBalanceRetrieverClientInstance(checkBalanceRetriever)
                .givenEitherCurrentOrSavingsAccount(true)
                .givenAProductList(productArrangementFacadeList)
                .givenRequestHeader(gboHeader)
                .givenAValue("662:664")
                .givenAppGroupRetrieverClientInstance(appGroupRetriever1)
                .ask();

        assertFalse(ask);
        verify(checkBalanceRetriever, times(1)).getCBSIndicators(gboHeader, "111619", "50001763", "01");
        verify(appGroupRetriever1, times(1)).callRetrieveCBSAppGroup(gboHeader, "111619", true);
    }

    @Test
    public void testIsCBSIndicatorPresentOnAllAccountsReturnsTrue() throws SalsaInternalResourceNotAvailableException, SalsaExternalServiceException, SalsaInternalServiceException, EligibilityException {
        GmoToGboRequestHeaderConverter headerConverter = new GmoToGboRequestHeaderConverter();
        RequestHeader header = testDataHelper.createEligibilityRequestHeader("LTB", TestDataHelper.TEST_INTERACTION, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        lb_gbo_sales.messages.RequestHeader gboHeader = headerConverter.convert(header);
        productArrangementFacade = new ProductArrangementFacade(createMockArrangement());
        productArrangementFacadeList.add(productArrangementFacade);
        int indicator = 664;
        AppGroupRetriever appGroupRetriever1 = mock(AppGroupRetriever.class);
        CheckBalanceRetriever checkBalanceRetriever = mock(CheckBalanceRetriever.class);
        when(appGroupRetriever1.callRetrieveCBSAppGroup(gboHeader, "111619", true)).thenReturn("01");
        when(checkBalanceRetriever.getCBSIndicators(gboHeader, "111619", "50001763", "01")).thenReturn(testDataHelper.getProdIndicators(indicator));

        boolean ask = QuestionIsCBSIndicatorPresentOnAllAccounts.pose()
                .givenCheckBalanceRetrieverClientInstance(checkBalanceRetriever)
                .givenEitherCurrentOrSavingsAccount(true)
                .givenAProductList(productArrangementFacadeList)
                .givenRequestHeader(gboHeader)
                .givenAValue("664")
                .givenAppGroupRetrieverClientInstance(appGroupRetriever1)
                .ask();

        assertTrue(ask);
        verify(checkBalanceRetriever, times(1)).getCBSIndicators(gboHeader, "111619", "50001763", "01");
        verify(appGroupRetriever1, times(1)).callRetrieveCBSAppGroup(gboHeader, "111619", true);
    }

    @Test
    public void testIsCBSIndicatorPresentOnAllAccountsWhenNeitherCurrentNorSavingsAccount() throws SalsaInternalResourceNotAvailableException, SalsaExternalServiceException, SalsaInternalServiceException, EligibilityException {
        GmoToGboRequestHeaderConverter headerConverter = new GmoToGboRequestHeaderConverter();
        RequestHeader header = testDataHelper.createEligibilityRequestHeader("LTB", TestDataHelper.TEST_INTERACTION, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        lb_gbo_sales.messages.RequestHeader gboHeader = headerConverter.convert(header);
        productArrangementFacade = new ProductArrangementFacade(createMockArrangement());
        productArrangementFacadeList.add(productArrangementFacade);
        AppGroupRetriever appGroupRetriever1 = mock(AppGroupRetriever.class);
        CheckBalanceRetriever checkBalanceRetriever = mock(CheckBalanceRetriever.class);

        boolean ask = QuestionIsCBSIndicatorPresentOnAllAccounts.pose()
                .givenCheckBalanceRetrieverClientInstance(checkBalanceRetriever)
                .givenEitherCurrentOrSavingsAccount(false)
                .givenAProductList(productArrangementFacadeList)
                .givenRequestHeader(gboHeader)
                .givenAValue("664")
                .givenAppGroupRetrieverClientInstance(appGroupRetriever1)
                .ask();

        assertFalse(ask);
        verify(checkBalanceRetriever, times(0)).getCheckBalance(gboHeader, "111619", "50001763", "01");
        verify(appGroupRetriever1, times(0)).callRetrieveCBSAppGroup(gboHeader, "111619", true);
    }

    public DepositArrangement createMockArrangement() {
        DepositArrangement productArrangement = mock(DepositArrangement.class);
        when(productArrangement.getAccountNumber()).thenReturn("50001763");
        Product associateProduct = mock(Product.class);
        InstructionDetails instructionDetails = mock(InstructionDetails.class);
        when(instructionDetails.getParentInstructionMnemonic()).thenReturn(Mnemonics.GROUP_ISA);
        when(associateProduct.getInstructionDetails()).thenReturn(instructionDetails);
        when(associateProduct.getProductIdentifier()).thenReturn("3001116001");

        ExtSysProdIdentifier externalSystemProductIdentifier = mock(ExtSysProdIdentifier.class);
        when(externalSystemProductIdentifier.getSystemCode()).thenReturn("00004");

        List<ExtSysProdIdentifier> externalSysPricePointIdentifierList = new ArrayList();
        externalSysPricePointIdentifierList.add(externalSystemProductIdentifier);

        when(associateProduct.getExternalSystemProductIdentifier()).thenReturn(externalSysPricePointIdentifierList);

        when(productArrangement.getAssociatedProduct()).thenReturn(associateProduct);
        ISABalance isaBalance = mock(ISABalance.class);
        CurrencyAmount currencyAmount = mock(CurrencyAmount.class);
        when(currencyAmount.getAmount()).thenReturn(new BigDecimal(0.0));
        when(isaBalance.getMaximumLimitAmount()).thenReturn(currencyAmount);
        when(productArrangement.getISABalance()).thenReturn(isaBalance);
        when(productArrangement.getLifecycleStatus()).thenReturn("Effective");

        Organisation financialInstitution = mock(Organisation.class);
        List<OrganisationUnit> hasOrganisationUnits = new ArrayList();
        OrganisationUnit organisationUnit = mock(OrganisationUnit.class);
        when(organisationUnit.getSortCode()).thenReturn("111619");
        hasOrganisationUnits.add(organisationUnit);
        when(financialInstitution.getHasOrganisationUnits()).thenReturn(hasOrganisationUnits);

        when(productArrangement.getFinancialInstitution()).thenReturn(financialInstitution);
        when(productArrangement.getArrangementStartDate()).thenReturn(testDataHelper.createXMLGregorianCalendar(2014, 10, 20));
        List<String> relatedEvents = new ArrayList();
        relatedEvents.add("37");
        relatedEvents.add("55");
        relatedEvents.add("30");
        relatedEvents.add("251");
        relatedEvents.add("333");
        when(productArrangement.getRelatedEvents()).thenReturn(relatedEvents);
        return productArrangement;
    }

    @Test
    public void testIsCBSIndicatorPresentOnAllAccountsReturnsFalseForEmptyProductArrangementList() throws SalsaInternalResourceNotAvailableException, SalsaExternalServiceException, SalsaInternalServiceException, EligibilityException {
        GmoToGboRequestHeaderConverter headerConverter = new GmoToGboRequestHeaderConverter();
        RequestHeader header = testDataHelper.createEligibilityRequestHeader("LTB", TestDataHelper.TEST_INTERACTION, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        lb_gbo_sales.messages.RequestHeader gboHeader = headerConverter.convert(header);
        productArrangementFacade = new ProductArrangementFacade(createMockArrangement());
        productArrangementFacadeList.add(productArrangementFacade);
        AppGroupRetriever appGroupRetriever1 = mock(AppGroupRetriever.class);
        CheckBalanceRetriever checkBalanceRetriever = mock(CheckBalanceRetriever.class);

        boolean ask = QuestionIsCBSIndicatorPresentOnAllAccounts.pose()
            .givenCheckBalanceRetrieverClientInstance(checkBalanceRetriever)
            .givenEitherCurrentOrSavingsAccount(true)
            .givenAProductList(new ArrayList<ProductArrangementFacade>())
            .givenRequestHeader(gboHeader)
            .givenAValue("664")
            .givenAppGroupRetrieverClientInstance(appGroupRetriever1)
            .ask();

        assertFalse(ask);
    }

    @Test
    public void testAskForExternalSystemProductIdentifierEmpty() throws SalsaInternalResourceNotAvailableException, SalsaExternalServiceException, SalsaInternalServiceException, EligibilityException {
        GmoToGboRequestHeaderConverter headerConverter = new GmoToGboRequestHeaderConverter();
        RequestHeader header = testDataHelper.createEligibilityRequestHeader("LTB", TestDataHelper.TEST_INTERACTION, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        lb_gbo_sales.messages.RequestHeader gboHeader = headerConverter.convert(header);
        DepositArrangement depositArrangement = createMockArrangement();
        productArrangementFacade = new ProductArrangementFacade(depositArrangement);
        when(depositArrangement.getAssociatedProduct().getExternalSystemProductIdentifier()).thenReturn(new ArrayList<ExtSysProdIdentifier>());
        productArrangementFacadeList.add(productArrangementFacade);
        int indicator = 665;

        AppGroupRetriever appGroupRetriever1 = mock(AppGroupRetriever.class);
        CheckBalanceRetriever checkBalanceRetriever = mock(CheckBalanceRetriever.class);
        when(appGroupRetriever1.callRetrieveCBSAppGroup(gboHeader, "111619", true)).thenReturn("01");
        when(checkBalanceRetriever.getCBSIndicators(gboHeader, "111619", "50001763", "01")).thenReturn(testDataHelper.getProdIndicators(indicator));

        boolean ask = QuestionIsCBSIndicatorPresentOnAllAccounts.pose()
            .givenCheckBalanceRetrieverClientInstance(checkBalanceRetriever)
            .givenEitherCurrentOrSavingsAccount(true)
            .givenAProductList(productArrangementFacadeList)
            .givenRequestHeader(gboHeader)
            .givenAValue("664")
            .givenAppGroupRetrieverClientInstance(appGroupRetriever1)
            .ask();

        assertFalse(ask);
        verify(checkBalanceRetriever, times(1)).getCBSIndicators(gboHeader, "111619", "50001763", "01");
        verify(appGroupRetriever1, times(1)).callRetrieveCBSAppGroup(gboHeader, "111619", true);
    }

}
