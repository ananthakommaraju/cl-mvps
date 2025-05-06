package com.lloydsbanking.salsa.activate.constants;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class SMSTemplateEnumTest {


    @Test
    public void testSMSTemplateEnum() {
        assertEquals("STPSAVSUCCESS", SMSTemplateEnum.STPSAVSUCCESS.getTemplate());
        assertEquals("STPSAVREMINDER", SMSTemplateEnum.STPSAVREMINDER.getTemplate());
        assertEquals("STPSAVFUNDREMIN", SMSTemplateEnum.STPSAVFUNDREMIN.getTemplate());
        assertEquals("STPCCRSUCCESS", SMSTemplateEnum.STPCCRSUCCESS.getTemplate());
        assertEquals("STPCCRREMINDER", SMSTemplateEnum.STPCCRREMINDER.getTemplate());
        assertEquals("STPSAVINGS", SMSTemplateEnum.STP_SAV_SOURCE.getTemplate());
        assertEquals("STPCC", SMSTemplateEnum.STP_CC_SOURCE.getTemplate());
    }
}
