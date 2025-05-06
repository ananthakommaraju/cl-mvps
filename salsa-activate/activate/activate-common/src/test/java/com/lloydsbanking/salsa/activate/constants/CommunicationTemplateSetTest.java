package com.lloydsbanking.salsa.activate.constants;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class CommunicationTemplateSetTest {

    @Test
    public void testIsEmailTemplate() {
        assertTrue(CommunicationTemplateSet.isEmailTemplate("WELCOME_MSG"));
    }

    @Test
    public void testIsEmailTemplateWithFalseCondition() {
        assertFalse(CommunicationTemplateSet.isEmailTemplate("WELCOME_M"));
    }

}
