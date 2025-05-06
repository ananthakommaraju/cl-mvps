package com.lloydsbanking.salsa.apacc.service.fulfil.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apacc.TestDataHelper;
import com.lloydsbanking.salsa.soap.fdi.f241.objects.CustomerDataSegment;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Category(UnitTest.class)
public class F241CustomerDataSegmentFactoryTest {
    F241CustomerDataSegmentFactory f241CustomerDataSegmentFactory;
    TestDataHelper testDataHelper;
    FinanceServiceArrangement financeServiceArrangement;

    @Before
    public void setUp() {
        f241CustomerDataSegmentFactory = new F241CustomerDataSegmentFactory();
        testDataHelper = new TestDataHelper();
        financeServiceArrangement= testDataHelper.createFinanceServiceArrangement();
        f241CustomerDataSegmentFactory.f241PhoneNumberFactory = new F241PhoneNumberFactory();
        f241CustomerDataSegmentFactory.f241PostalAddressFactory = new F241PostalAddressFactory();
    }

    @Test
    public void testGetCustomerDataSegmentWithNullCustomerNumber() {
        CustomerDataSegment customerDataSegment = f241CustomerDataSegmentFactory.getCustomerDataSegment(financeServiceArrangement);
        customerDataSegment.getAcctCustNoSameIn();
        assertEquals("Mr",customerDataSegment.getOwnerData().getPartyTl());
        assertEquals("0",customerDataSegment.getAcctCustNoSameIn());
        assertEquals("0",customerDataSegment.getForeignUseIn());
        assertEquals("0",customerDataSegment.getOwnerContactData().getFaxStatusCd());
        assertEquals("1",customerDataSegment.getOwnerContactData().getTelephoneStatusCd());
        assertEquals("0001200000026267042",customerDataSegment.getUniqueCustomerId());
    }

    @Test
    public void testGetCustomerDataSegmentWithNotNullCustomerNumber() {
        financeServiceArrangement.getPrimaryInvolvedParty().setCustomerNumber("06564534231");
        CustomerDataSegment customerDataSegment = f241CustomerDataSegmentFactory.getCustomerDataSegment(financeServiceArrangement);
        customerDataSegment.getAcctCustNoSameIn();
        assertEquals("06564534231",customerDataSegment.getCustomerNumberExternalId());
        assertEquals("0",customerDataSegment.getAcctCustNoSameIn());
        assertEquals("0",customerDataSegment.getForeignUseIn());
        assertEquals("0",customerDataSegment.getOwnerContactData().getFaxStatusCd());
        assertEquals("1",customerDataSegment.getOwnerContactData().getTelephoneStatusCd());
    }

    @Test
    public void testGetCustomerDataSegmentWithJointParty() {
        financeServiceArrangement.getPrimaryInvolvedParty().setCustomerNumber("06564534231");
        FinanceServiceArrangement financeServiceArrangement1 = testDataHelper.createFinanceServiceArrangementForJointParty("123");
        CustomerDataSegment customerDataSegment = f241CustomerDataSegmentFactory.getCustomerDataSegment(financeServiceArrangement1);
        customerDataSegment.getAcctCustNoSameIn();
        assertEquals("0",customerDataSegment.getAcctCustNoSameIn());
        assertEquals("0",customerDataSegment.getForeignUseIn());
        assertEquals("0",customerDataSegment.getOwnerContactData().getFaxStatusCd());
        assertEquals("0",customerDataSegment.getOwnerContactData().getTelephoneStatusCd());
    }

    @Test
    public void testGetCustomerDataSegmentWithGenderNullCidIdNullBrandName() {
        financeServiceArrangement.getAssociatedProduct().setBrandName(null);
        financeServiceArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setGender("002");
        financeServiceArrangement.getPrimaryInvolvedParty().setEmailAddress(null);
        financeServiceArrangement.getPrimaryInvolvedParty().setCidPersID(null);
        CustomerDataSegment customerDataSegment = f241CustomerDataSegmentFactory.getCustomerDataSegment(financeServiceArrangement);
        customerDataSegment.getAcctCustNoSameIn();
        assertEquals("0",customerDataSegment.getAcctCustNoSameIn());
        assertEquals("0",customerDataSegment.getForeignUseIn());
        assertEquals("2",customerDataSegment.getOwnerContactData().getGenderCd());
        assertEquals("1",customerDataSegment.getOwnerContactData().getTelephoneStatusCd());
        assertNull(customerDataSegment.getUniqueCustomerId());
    }

}
