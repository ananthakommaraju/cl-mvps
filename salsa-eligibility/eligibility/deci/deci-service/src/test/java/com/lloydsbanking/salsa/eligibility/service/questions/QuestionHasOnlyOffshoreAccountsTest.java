package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import lb_gbo_sales.DepositArrangement;
import lb_gbo_sales.ProductArrangement;

import lib_sim_bo.businessobjects.Organisation;
import lib_sim_bo.businessobjects.OrganisationUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class QuestionHasOnlyOffshoreAccountsTest {

    private ProductArrangementFacade productArrangementFacade;

    private List<ProductArrangementFacade> productArrangementFacadeList;

    private ProductArrangement productArrangement;

    @Before
    public void before() {
        productArrangementFacadeList = new ArrayList<>();
        productArrangement = mock(DepositArrangement.class);
        productArrangementFacade = spy(new ProductArrangementFacade(productArrangement));
        productArrangementFacadeList.add(productArrangementFacade);
    }

    @Test
    public void hasOnlyOffShoreAccountReturnTrueIfSortIsNotPresentInDepositArrangement() throws Exception {
        when(productArrangement.getSortCode()).thenReturn("55556");

        boolean ask = QuestionHasOnlyOffshoreAccounts.pose().givenAProductList(productArrangementFacadeList).givenAValue("not-present").ask();
        verify(productArrangementFacade, times(1)).isSalesDepositArrangement();
        verify(productArrangementFacade, times(1)).getSortCode();
        assertTrue(ask);
    }

    @Test
    public void hasOnlyOffShoreAccountReturnFalseIfSortIsPresent() throws Exception {
        when(productArrangement.getSortCode()).thenReturn("55556");

        boolean ask = QuestionHasOnlyOffshoreAccounts.pose().givenAProductList(productArrangementFacadeList).givenAValue("55556").ask();
        verify(productArrangementFacade, times(1)).isSalesDepositArrangement();
        verify(productArrangementFacade, times(1)).getSortCode();
        assertFalse(ask);
    }

    @Test
    public void hasOnlyOffShoreAccountReturnFalseIfSortIsPresentWithTwoSortCodes() throws Exception {
        when(productArrangement.getSortCode()).thenReturn("55556");

        boolean ask = QuestionHasOnlyOffshoreAccounts.pose().givenAProductList(productArrangementFacadeList).givenAValue("55556:650563").ask();
        verify(productArrangementFacade, times(1)).isSalesDepositArrangement();
        verify(productArrangementFacade, times(1)).getSortCode();
        assertFalse(ask);
    }

    @Test
    public void hasOnlyOffShoreAccountReturnTrueIfSortIsNotPresentWithTwoSortCodes() throws Exception {
        when(productArrangement.getSortCode()).thenReturn("55556");

        boolean ask = QuestionHasOnlyOffshoreAccounts.pose().givenAProductList(productArrangementFacadeList).givenAValue("55557:650563").ask();
        verify(productArrangementFacade, times(1)).isSalesDepositArrangement();
        verify(productArrangementFacade, times(1)).getSortCode();
        assertTrue(ask);
    }

    @Test
    public void hasOnlyOffShoreAccountReturnTrueIfSortIsNotPresentWithTwoSortCodesAndProductArrangementIsServicing() throws Exception {
        productArrangementFacadeList = new ArrayList<>();
        lib_sim_bo.businessobjects.ProductArrangement productArrangement = mock(lib_sim_bo.businessobjects.ProductArrangement.class);
        productArrangementFacade = spy(new ProductArrangementFacade(productArrangement));
        productArrangementFacadeList.add(productArrangementFacade);

        Organisation organisation = mock(Organisation.class);
        ArrayList<OrganisationUnit> organisationUnits = new ArrayList();
        OrganisationUnit organisationUnit = mock(OrganisationUnit.class);
        when(organisationUnit.getSortCode()).thenReturn("55556");
        organisationUnits.add(organisationUnit);
        when(organisation.getHasOrganisationUnits()).thenReturn(organisationUnits);
        when(productArrangement.getFinancialInstitution()).thenReturn(organisation);

        boolean ask = QuestionHasOnlyOffshoreAccounts.pose().givenAProductList(productArrangementFacadeList).givenAValue("55557:650563").ask();
        verify(productArrangementFacade, times(1)).isServicingProductArrangement();
        verify(productArrangementFacade, times(1)).getSortCode();
        verify(organisationUnit, times(2)).getSortCode();
        assertTrue(ask);
    }

    @Test
    public void hasOnlyOffShoreAccountReturnFalseIfSortIsPresentWithTwoSortCodesAndProductArrangementIsServicing() throws Exception {
        productArrangementFacadeList = new ArrayList<>();
        lib_sim_bo.businessobjects.ProductArrangement productArrangement = mock(lib_sim_bo.businessobjects.ProductArrangement.class);
        productArrangementFacade = spy(new ProductArrangementFacade(productArrangement));
        productArrangementFacadeList.add(productArrangementFacade);

        Organisation organisation = mock(Organisation.class);
        ArrayList<OrganisationUnit> organisationUnits = new ArrayList();
        OrganisationUnit organisationUnit = mock(OrganisationUnit.class);
        when(organisationUnit.getSortCode()).thenReturn("55556");
        organisationUnits.add(organisationUnit);
        when(organisation.getHasOrganisationUnits()).thenReturn(organisationUnits);
        when(productArrangement.getFinancialInstitution()).thenReturn(organisation);

        boolean ask = QuestionHasOnlyOffshoreAccounts.pose().givenAProductList(productArrangementFacadeList).givenAValue("55556:650563").ask();
        verify(productArrangementFacade, times(1)).isServicingProductArrangement();
        verify(productArrangementFacade, times(1)).getSortCode();
        verify(organisationUnit, times(2)).getSortCode();
        assertFalse(ask);
    }
    @Test
    public void hasOnlyOffShoreAccountReturnTrueIfSortIsNotPresentInProductArrangementAndProductArrangementIsServicing() throws Exception {
        productArrangementFacadeList = new ArrayList<>();
        lib_sim_bo.businessobjects.ProductArrangement productArrangement = mock(lib_sim_bo.businessobjects.ProductArrangement.class);
        productArrangementFacade = spy(new ProductArrangementFacade(productArrangement));
        productArrangementFacadeList.add(productArrangementFacade);

        Organisation organisation = mock(Organisation.class);
        ArrayList<OrganisationUnit> organisationUnits = new ArrayList();
        OrganisationUnit organisationUnit = mock(OrganisationUnit.class);
        when(organisationUnit.getSortCode()).thenReturn("55556");
        organisationUnits.add(organisationUnit);
        when(organisation.getHasOrganisationUnits()).thenReturn(organisationUnits);
        when(productArrangement.getFinancialInstitution()).thenReturn(organisation);

        boolean ask = QuestionHasOnlyOffshoreAccounts.pose().givenAProductList(productArrangementFacadeList).givenAValue("not-present").ask();
        verify(productArrangementFacade, times(1)).isServicingProductArrangement();
        verify(productArrangementFacade, times(1)).getSortCode();
        assertTrue(ask);
    }

    @Test
    public void hasOnlyOffShoreAccountReturnFalseIfSortIsPresentAndProductArrangementIsServicing() throws Exception {
        productArrangementFacadeList = new ArrayList<>();
        lib_sim_bo.businessobjects.ProductArrangement productArrangement = mock(lib_sim_bo.businessobjects.ProductArrangement.class);
        productArrangementFacade = spy(new ProductArrangementFacade(productArrangement));
        productArrangementFacadeList.add(productArrangementFacade);

        Organisation organisation = mock(Organisation.class);
        ArrayList<OrganisationUnit> organisationUnits = new ArrayList();
        OrganisationUnit organisationUnit = mock(OrganisationUnit.class);
        when(organisationUnit.getSortCode()).thenReturn("55556");
        organisationUnits.add(organisationUnit);
        when(organisation.getHasOrganisationUnits()).thenReturn(organisationUnits);
        when(productArrangement.getFinancialInstitution()).thenReturn(organisation);

        boolean ask = QuestionHasOnlyOffshoreAccounts.pose().givenAProductList(productArrangementFacadeList).givenAValue("55556").ask();
        verify(productArrangementFacade, times(1)).isServicingProductArrangement();
        verify(productArrangementFacade, times(1)).getSortCode();
        verify(productArrangementFacade, times(1)).getSortCode();
        assertFalse(ask);
    }
}