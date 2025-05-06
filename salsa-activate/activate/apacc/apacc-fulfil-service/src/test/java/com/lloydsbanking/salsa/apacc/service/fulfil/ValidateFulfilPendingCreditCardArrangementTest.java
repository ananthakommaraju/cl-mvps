package com.lloydsbanking.salsa.apacc.service.fulfil;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.InternetBankingRegistration;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@Category(UnitTest.class)
public class ValidateFulfilPendingCreditCardArrangementTest {
    ValidateFulfilPendingCreditCardArrangement validateFulfilPendingCreditCardArrangement;
    @Before
    public void setUp()
    {
        validateFulfilPendingCreditCardArrangement=new ValidateFulfilPendingCreditCardArrangement();
    }
    @Test
    public void testCheckAppSubStatus()
    {
        assertFalse(validateFulfilPendingCreditCardArrangement.checkAppSubStatus(null,"1234"));
        assertFalse(validateFulfilPendingCreditCardArrangement.checkAppSubStatus("1","1234"));
        assertTrue(validateFulfilPendingCreditCardArrangement.checkAppSubStatus("1234","1234"));
    }

    @Test
    public void testIsFulfillNewApplication()
    {
        assertTrue(validateFulfilPendingCreditCardArrangement.isFulfillNewApplication("10001"));
        assertFalse(validateFulfilPendingCreditCardArrangement.isFulfillNewApplication("1234"));
    }

    @Test
    public void testIsPreviousCallSuccessful(){
        assertFalse(validateFulfilPendingCreditCardArrangement.isPreviousCallSuccessful(false, null, "1234"));
        assertFalse(validateFulfilPendingCreditCardArrangement.isPreviousCallSuccessful(false, "1", "1234"));
        assertTrue(validateFulfilPendingCreditCardArrangement.isPreviousCallSuccessful(true,"1234","1234"));
    }

    @Test
    public void testIsAddOMSRequired(){
        assertTrue(validateFulfilPendingCreditCardArrangement.isAddOMSRequired(true,"1015"));
        assertTrue(validateFulfilPendingCreditCardArrangement.isAddOMSRequired(true,null));
        assertFalse(validateFulfilPendingCreditCardArrangement.isAddOMSRequired(false, "1234"));
    }

    @Test
    public void testIsIBActivationRequired(){
        Customer primaryInvolvedParty = new Customer();
        primaryInvolvedParty.setIsRegisteredIn(new InternetBankingRegistration());
        primaryInvolvedParty.getIsRegisteredIn().setRegistrationIdentifier("1234");
        validateFulfilPendingCreditCardArrangement.isCallAndStoreApplication(true,true);
        assertTrue(validateFulfilPendingCreditCardArrangement.isIBActivationRequired(true,true,primaryInvolvedParty));
        assertFalse(validateFulfilPendingCreditCardArrangement.isIBActivationRequired(false,false,primaryInvolvedParty));
    }

    @Test
    public void testIfAddCardHolderFailureOrIsJointParty(){
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        financeServiceArrangement.setApplicationSubStatus("1014");
        ApplicationDetails applicationDetails = new ApplicationDetails();
        applicationDetails.setApiFailureFlag(false);
        validateFulfilPendingCreditCardArrangement.isPreviousCallSuccessful(true, "1014", "1014");
        validateFulfilPendingCreditCardArrangement.checkAppSubStatus(financeServiceArrangement.getApplicationSubStatus(), ActivateCommonConstant.AppSubStatus.ADD_CARD_HOLDER_FAILURE);
        assertTrue(validateFulfilPendingCreditCardArrangement.checkIfAddCardHolderFailureOrIsJointParty(financeServiceArrangement, applicationDetails));
    }

    @Test
    public void testIsCallAndStoreApplication(){
        assertTrue(validateFulfilPendingCreditCardArrangement.isCallAndStoreApplication(true,true));
        assertFalse(validateFulfilPendingCreditCardArrangement.isCallAndStoreApplication(false,false));
    }
}
