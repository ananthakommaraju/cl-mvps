package com.lloydsbanking.salsa.activate.constants;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class EmailTemplateEnumTest {

    @Test
    public void testEmailTemplateEnum(){
        assertEquals("APPL_ABANDND_MSG", EmailTemplateEnum.APPLICATION_ABANDENED_EMAIL.getTemplate());
        assertEquals("DECLINE_BANK_MSG", EmailTemplateEnum.BANK_DECLINE_EMAIL.getTemplate());
        assertEquals("CA_DECLINE_BANK_MSG", EmailTemplateEnum.CA_DECLINE_BANK_MSG.getTemplate());
    }
}
