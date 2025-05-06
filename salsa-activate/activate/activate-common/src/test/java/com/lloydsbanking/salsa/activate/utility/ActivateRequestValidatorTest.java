package com.lloydsbanking.salsa.activate.utility;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@Category(UnitTest.class)
public class ActivateRequestValidatorTest {

    ActivateRequestValidator activateRequestValidator = new ActivateRequestValidator();

    @Test
    public void validateAppRequestFromOAP() {
        boolean requestValid = activateRequestValidator.validateRequest("1007", "3");
        assertTrue(requestValid);
    }

    @Test
    public void validateAppRequestFromDBEvent() {
        boolean requestValid;

        requestValid = activateRequestValidator.validateRequest("1002", "2");
        assertTrue(requestValid);

        requestValid = activateRequestValidator.validateRequest("1003", "2");
        assertTrue(requestValid);

        requestValid = activateRequestValidator.validateRequest("1001", "2");
        assertFalse(requestValid);

        requestValid = activateRequestValidator.validateRequest("1012", "2");
        assertTrue(requestValid);
    }

    @Test
    public void validateAppRequestFromGalaxy() {
        boolean requestValid;

        requestValid = activateRequestValidator.validateRequest("1002", "1");
        assertTrue(requestValid);

        requestValid = activateRequestValidator.validateRequest("1003", "1");
        assertTrue(requestValid);

        requestValid = activateRequestValidator.validateRequest("1007", "1");
        assertFalse(requestValid);

        requestValid = activateRequestValidator.validateRequest("1005", "1");
        assertTrue(requestValid);
    }

}
