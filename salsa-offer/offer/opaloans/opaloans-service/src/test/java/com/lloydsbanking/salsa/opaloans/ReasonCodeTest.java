package com.lloydsbanking.salsa.opaloans;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class ReasonCodeTest {

    @Test
    public void testReasonCode(){
        assertEquals("04", ReasonCodes.getReasonCode(ReasonCodes.ACCOUNT_INVALID_FOR_LOAN.getKey()));
        assertEquals("01", ReasonCodes.getReasonCode(ReasonCodes.BIRTH_DATE_NOT_MATCHED.getKey()));
        assertEquals("03", ReasonCodes.getReasonCode(ReasonCodes.VERDE_CUSTOMER.getKey()));
        assertEquals("01", ReasonCodes.getReasonCode(ReasonCodes.NAME_NOT_MATCHED_WITH_OCIS.getKey()));
    }

    @Test
    public void testReasonCodeForKeys(){
        assertEquals("Account is invalid to apply for Loan", ReasonCodes.ACCOUNT_INVALID_FOR_LOAN.getKey());
        assertEquals("BirthDate Not Matched", ReasonCodes.BIRTH_DATE_NOT_MATCHED.getKey());
        assertEquals("Verde Customer", ReasonCodes.VERDE_CUSTOMER.getKey());
        assertEquals("FirstName and LastName Not Matched with OCIS", ReasonCodes.NAME_NOT_MATCHED_WITH_OCIS.getKey());
    }
}