package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import lib_sim_bo.businessobjects.Organisation;
import lib_sim_bo.businessobjects.OrganisationUnit;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QuestionIsSortCodePresentTest {

    private ProductArrangement productArrangement;

    private List<ProductArrangementFacade> productArrangementFacadeList;

    private ProductArrangementFacade productArrangementFacade;

    @Before
    public void setUp() throws Exception {
        productArrangementFacadeList = new ArrayList();
        productArrangement = mock(ProductArrangement.class);

    }

    @Test
    public void testAskReturnsTrue() throws EligibilityException {
        Organisation organisation= new Organisation();
        organisation.getHasOrganisationUnits().add(new OrganisationUnit());
        organisation.getHasOrganisationUnits().get(0).setSortCode("1234");
        when(productArrangement.getFinancialInstitution()).thenReturn(organisation);
        when(productArrangement.getAccountNumber()).thenReturn("8945");
        productArrangementFacadeList.add(new ProductArrangementFacade(productArrangement));
        boolean ask=QuestionIsSortCodePresent.pose()
            .givenAProductList(productArrangementFacadeList)
            .ask();
        assertTrue(ask);
    }

    @Test
    public void testAskReturnsFalse() throws EligibilityException {
        Organisation organisation= new Organisation();
        when(productArrangement.getFinancialInstitution()).thenReturn(organisation);
        when(productArrangement.getAccountNumber()).thenReturn("");
        productArrangementFacadeList.add(new ProductArrangementFacade(productArrangement));
        boolean ask=QuestionIsSortCodePresent.pose()
            .givenAProductList(productArrangementFacadeList)
            .ask();
        assertFalse(ask);
    }
}
