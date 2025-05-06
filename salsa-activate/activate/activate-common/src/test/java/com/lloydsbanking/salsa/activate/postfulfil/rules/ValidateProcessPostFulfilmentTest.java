package com.lloydsbanking.salsa.activate.postfulfil.rules;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.ws.WebServiceException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class ValidateProcessPostFulfilmentTest {

    private ValidateProcessPostFulfilment validateProcessPostFulfilment;
    private TestDataHelper testDataHelper;
    private ProductArrangement productArrangement;

    @Before
    public void setUp() {
        validateProcessPostFulfilment = new ValidateProcessPostFulfilment();
        testDataHelper = new TestDataHelper();
        productArrangement = testDataHelper.createDepositArrangement("1234");
        validateProcessPostFulfilment.switchClient = mock(SwitchService.class);
    }

    @Test
    public void testIsUpdateEmailAddressRequired() {
        productArrangement.getPrimaryInvolvedParty().setCustomerSegment("3");
        assertTrue(validateProcessPostFulfilment.isUpdateEmailAddressRequired(productArrangement, "3"));
        assertTrue(validateProcessPostFulfilment.isUpdateEmailAddressRequired(productArrangement, "4"));
        assertTrue(validateProcessPostFulfilment.isUpdateEmailAddressRequired(productArrangement, "4"));
        productArrangement.setApplicationSubStatus("1021");
        productArrangement.getPrimaryInvolvedParty().setCustomerSegment("5");
        assertFalse(validateProcessPostFulfilment.isUpdateEmailAddressRequired(productArrangement, "4"));
        productArrangement.setApplicationSubStatus("1030");
        assertTrue(validateProcessPostFulfilment.isUpdateEmailAddressRequired(productArrangement, "4"));
    }

    @Test
    public void testIsUpdateNINumberRequired() {
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setNationalInsuranceNumber("1");
        productArrangement.setApplicationSubStatus(null);
        assertTrue(validateProcessPostFulfilment.isUpdateNINumberRequired(productArrangement));
        productArrangement.setApplicationSubStatus("1021");
        assertTrue(validateProcessPostFulfilment.isUpdateNINumberRequired(productArrangement));
        productArrangement.setApplicationSubStatus(null);
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setNationalInsuranceNumber(null);
        assertFalse(validateProcessPostFulfilment.isUpdateNINumberRequired(productArrangement));

    }

    @Test
    public void testCheckAppSubStatus() {
        assertTrue(validateProcessPostFulfilment.checkAppSubStatus(null, "123"));
        assertTrue(validateProcessPostFulfilment.checkAppSubStatus("123", "123"));
        assertFalse(validateProcessPostFulfilment.checkAppSubStatus("123", "1"));
    }

    @Test
    public void testIsUpdateMarketingPreferencesRequired() {
        productArrangement.setApplicationSubStatus(null);
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setNationalInsuranceNumber(null);
        assertTrue(validateProcessPostFulfilment.isUpdateMarketingPreferencesRequired(productArrangement));
        productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setNationalInsuranceNumber("123");
        assertTrue(validateProcessPostFulfilment.isUpdateMarketingPreferencesRequired(productArrangement));
        productArrangement.setApplicationSubStatus("1029");
        assertTrue(validateProcessPostFulfilment.isUpdateMarketingPreferencesRequired(productArrangement));
    }

    @Test
    public void testRetrieveSwitchValue() {
        when(validateProcessPostFulfilment.switchClient.getGlobalSwitchValue("SW_FATCAupdate", "VER", false)).thenReturn(true);
        assertTrue(validateProcessPostFulfilment.retrieveSwitchValue("VER", "SW_FATCAupdate"));
    }

    @Test
    public void testRetrieveSwitchValueWhenSwitchClientThrowsException() {
        when(validateProcessPostFulfilment.switchClient.getGlobalSwitchValue("SW_FATCAupdate", "VER", false)).thenThrow(new WebServiceException());
        assertFalse(validateProcessPostFulfilment.retrieveSwitchValue("VER", "SW_FATCAupdate"));
    }


}
