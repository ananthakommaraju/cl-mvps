package com.lloydsbanking.salsa.activate.postfulfil.rules;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@Category(UnitTest.class)
public class C658ErrorSetTest {
    private C658ErrorSet c658ErrorSet;

    @Before
    public void setUp() {
        c658ErrorSet = new C658ErrorSet();
    }

    @Test
    public void checkExternalServiceError() {
        assertTrue(c658ErrorSet.isExternalServiceError(165107));
        assertFalse(c658ErrorSet.isExternalServiceError(0));
    }
}