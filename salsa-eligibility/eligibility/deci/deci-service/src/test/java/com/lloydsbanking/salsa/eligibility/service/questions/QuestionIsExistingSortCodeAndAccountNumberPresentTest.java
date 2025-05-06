package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class QuestionIsExistingSortCodeAndAccountNumberPresentTest {

    @Test
    public void testAskReturnsTrue() throws EligibilityException {
        boolean result= QuestionIsExistingSortCodeAndAccountNumberPresent.pose()
            .givenAnExistingAccountNumber("1234")
            .givenAnExistingSortCode("123456")
            .ask();
        assertTrue(result);
    }

    @Test
    public void testAskReturnsFalse() throws EligibilityException {
        boolean result= QuestionIsExistingSortCodeAndAccountNumberPresent.pose()
            .givenAnExistingSortCode("123456")
            .ask();
        assertFalse(result);
    }
}
