package com.lloydsbanking.salsa.activate.validator;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.registration.RegistrationService;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementInternalSystemErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class ApplicationTypeValidatorTest {

    TestDataHelper dataHelper;

    ApplicationTypeValidator applicationTypeValidator;
    Boolean isApplicationFulfiled;

    ProductArrangement productArrangement;


    @Before
    public void setUp() {
        dataHelper = new TestDataHelper();
        productArrangement = dataHelper.createDepositArrangementAfterPAMCall();
        applicationTypeValidator = new ApplicationTypeValidator();
        applicationTypeValidator.registrationService = mock(RegistrationService.class);
        applicationTypeValidator.exceptionUtilityActivate = mock(ExceptionUtilityActivate.class);

    }

    @Test
    public void testValidateArrangementFulfilmentConditionShouldBeFalseForNonTradeApplication() throws ActivateProductArrangementInternalSystemErrorMsg {
        isApplicationFulfiled = applicationTypeValidator.checkApplicationTypeForArrangement(productArrangement, dataHelper.createApaRequestHeader());
        assertFalse(isApplicationFulfiled);
    }

    @Test
    public void testValidateArrangementFulfilmentConditionShouldBeTrueForTradeApplication() throws ActivateProductArrangementInternalSystemErrorMsg {
        productArrangement.setApplicationType("10002");
        isApplicationFulfiled = applicationTypeValidator.checkApplicationTypeForArrangement(productArrangement, dataHelper.createApaRequestHeader());
        assertTrue(isApplicationFulfiled);
    }

    @Test(expected = ActivateProductArrangementInternalSystemErrorMsg.class)
    public void testWhenApplicationTypeIsInvalid() throws ActivateProductArrangementInternalSystemErrorMsg {
        productArrangement.setApplicationType("10004");
        when(applicationTypeValidator.exceptionUtilityActivate.internalServiceError("820001", "The Product eligibility type of the application is invalid", dataHelper.createApaRequestHeader())).thenReturn(new ActivateProductArrangementInternalSystemErrorMsg());
        isApplicationFulfiled = applicationTypeValidator.checkApplicationTypeForArrangement(productArrangement, dataHelper.createApaRequestHeader());
    }

    @Test(expected = ActivateProductArrangementInternalSystemErrorMsg.class)
    public void testWhenApplicationTypeIsNull() throws ActivateProductArrangementInternalSystemErrorMsg {
        productArrangement.setApplicationType(null);
        when(applicationTypeValidator.exceptionUtilityActivate.internalServiceError("820001", "The Product eligibility type of the application is invalid", dataHelper.createApaRequestHeader())).thenReturn(new ActivateProductArrangementInternalSystemErrorMsg());
        isApplicationFulfiled = applicationTypeValidator.checkApplicationTypeForArrangement(productArrangement, dataHelper.createApaRequestHeader());
    }

}
