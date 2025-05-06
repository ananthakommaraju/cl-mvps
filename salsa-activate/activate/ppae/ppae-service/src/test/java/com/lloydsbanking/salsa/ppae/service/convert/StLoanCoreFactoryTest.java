package com.lloydsbanking.salsa.ppae.service.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import com.lloydsbanking.salsa.ppae.service.rule.EmploymentStatus;
import com.lloydsbanking.salsa.soap.fs.loan.St2LoanCoreDetails;
import com.lloydsbanking.salsa.soap.pad.f263.objects.F263Resp;
import lib_sim_bo.businessobjects.CurrencyAmount;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.math.BigDecimal;
import java.math.BigInteger;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

@Category(UnitTest.class)
public class StLoanCoreFactoryTest {

    TestDataHelper testDataHelper;

    StLoanCoreFactory stLoanCoreFactory;

    F263Resp f263Resp;

    @Before
    public void setUp() throws DatatypeConfigurationException {
        testDataHelper = new TestDataHelper();
        f263Resp = testDataHelper.createF263Resp();
        stLoanCoreFactory = new StLoanCoreFactory();
        stLoanCoreFactory.employmentStatus = new EmploymentStatus();
    }

    @Test
    public void getStLoanDetailsTest() {
        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setAmount(new BigDecimal("100"));
        St2LoanCoreDetails st2LoanCoreDetails = stLoanCoreFactory.getStLoanDetails(f263Resp);
        assertEquals(new BigInteger("299512626"), st2LoanCoreDetails.getStloanhdr().getOcisid());
        assertEquals("+00382350249", st2LoanCoreDetails.getStloanhdr().getPartyidPersId());
        assertEquals("11032350008507", st2LoanCoreDetails.getStloanhdr().getCustnum());
    }

    @Test
    public void getStLoanDetailsNegativeConditionTest() {
        f263Resp.getApplicantDetails().getParty().get(0).setPartyId(null);
        f263Resp.getApplicantDetails().getParty().get(0).setEmploymentStatusCd(null);
        St2LoanCoreDetails st2LoanCoreDetails = stLoanCoreFactory.getStLoanDetails(f263Resp);
        assertNull(st2LoanCoreDetails.getStloanhdr().getOcisid());
        assertEquals("+00382350249", st2LoanCoreDetails.getStloanhdr().getPartyidPersId());
        assertEquals("11032350008507", st2LoanCoreDetails.getStloanhdr().getCustnum());
    }
}
