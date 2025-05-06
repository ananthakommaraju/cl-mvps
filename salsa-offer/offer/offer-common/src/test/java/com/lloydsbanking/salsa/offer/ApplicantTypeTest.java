package com.lloydsbanking.salsa.offer;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
@Category(UnitTest.class)
public class ApplicantTypeTest {

    @Test
    public void testApplicantType(){
        assertEquals("00",ApplicantType.SELF.getValue());
        assertEquals("01",ApplicantType.DEPENDENT.getValue());
        assertEquals("02",ApplicantType.GUARDIAN.getValue());

        assertEquals(ApplicantType.SELF,ApplicantType.getApplicantType("00"));
        assertEquals(ApplicantType.DEPENDENT,ApplicantType.getApplicantType("01"));
        assertEquals(ApplicantType.GUARDIAN,ApplicantType.getApplicantType("02"));

    }
}
