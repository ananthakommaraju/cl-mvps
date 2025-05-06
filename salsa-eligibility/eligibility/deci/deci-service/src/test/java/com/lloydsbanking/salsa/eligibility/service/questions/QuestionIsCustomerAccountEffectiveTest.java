package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.businessobjects.ProductArrangementLifecycleStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class QuestionIsCustomerAccountEffectiveTest {

    private List<ProductArrangementFacade> productArrangementsList;
    private ProductArrangementFacade productArrangementFacade;
    private ProductArrangement productArrangement;

    @Before
    public void before(){
        productArrangementsList = new ArrayList<>();
        productArrangement = mock(ProductArrangement.class);
        productArrangementFacade = new ProductArrangementFacade(productArrangement);
        productArrangementsList.add(productArrangementFacade);
    }

    @Test
    public void isCustomerAccountEffectiveShouldReturnFalseWhenLifecycleStatusIsEqualToThreshold() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        when(productArrangement.getLifecycleStatus()).thenReturn(ProductArrangementLifecycleStatus.EFFECTIVE);

        boolean ask = QuestionIsCustomerAccountEffective.pose().givenAProductList(productArrangementsList).givenAValue("Effective").ask();

        assertFalse(ask);

    }

    @Test
    public void isCustomerAccountEffectiveShouldReturnTrueWhenLifecycleStatusIsNotEqualToThreshold() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        when(productArrangement.getLifecycleStatus()).thenReturn(ProductArrangementLifecycleStatus.EFFECTIVE);


        boolean ask = QuestionIsCustomerAccountEffective.pose().givenAProductList(productArrangementsList).givenAValue("A").ask();

        assertTrue(ask);

    }

    @Test
    public void isCustomerAccountEffectiveShouldReturnFalseWhenLifecycleStatusIsNull() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        when(productArrangement.getLifecycleStatus()).thenReturn(null);

        boolean ask = QuestionIsCustomerAccountEffective.pose().givenAProductList(productArrangementsList).givenAValue("A").ask();

        assertFalse(ask);

    }

    @Test
    public void isCustomerAccountEffectiveShouldReturnFalseWhenLifecycleStatusIsEqualToThresholdForWZ() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        when(productArrangement.getLifecycleStatus()).thenReturn(ProductArrangementLifecycleStatus.EFFECTIVE);

        boolean ask = QuestionIsCustomerAccountEffective.pose()
            .givenIsWzRequest(true)
            .givenAProductList(productArrangementsList)
            .givenAValue("Effective")
            .ask();

        assertFalse(ask);

    }

    @Test
    public void isCustomerAccountEffectiveShouldReturnTrueWhenLifecycleStatusIsNotEqualToThresholdForWz() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        when(productArrangement.getLifecycleStatus()).thenReturn(ProductArrangementLifecycleStatus.EFFECTIVE);


        boolean ask = QuestionIsCustomerAccountEffective.pose()
            .givenIsWzRequest(true)
            .givenAProductList(productArrangementsList)
            .givenAValue("A")
            .ask();

        assertTrue(ask);

    }

    @Test
    public void isCustomerAccountEffectiveShouldReturnFalseWhenLifecycleStatusIsNullForWz() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        when(productArrangement.getLifecycleStatus()).thenReturn(null);

        boolean ask = QuestionIsCustomerAccountEffective.pose()
            .givenIsWzRequest(true)
            .givenAProductList(productArrangementsList)
            .givenAValue("A").ask();

        assertTrue(ask);

    }

}