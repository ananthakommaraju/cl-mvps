package com.lloydsbanking.salsa.offer.apply.errorcode;


import com.lloydsbanking.salsa.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class RetrieveCreditScoreErrorMapTest {

    private com.lloydsbanking.salsa.offer.apply.errorcode.RetrieveCreditScoreErrorMap errorMap;
    private static final String INTERNAL_SERVICE_ERROR = "Internal Service Error";
    private static final String EXTERNAL_BUSINESS_ERROR = "External Business Error";
    private static final String EXTERNAL_SERVICE_ERROR = "External Service Error";

    @Before
    public void setUp() {
        errorMap = new com.lloydsbanking.salsa.offer.apply.errorcode.RetrieveCreditScoreErrorMap();
    }

    @Test
    public void testGetAsmErrorCode() {
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(155012));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159100));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159101));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159102));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159103));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159105));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159106));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159107));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159109));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159110));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159111));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159112));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159113));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159114));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159116));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159117));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159119));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159120));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159122));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159123));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159201));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159202));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159203));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159204));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159205));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159206));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159207));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159208));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159209));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159235));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159236));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159237));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159403));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159421));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159446));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159124));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159125));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159126));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159130));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159138));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159163));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159164));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159165));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159166));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159168));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159175));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159176));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159180));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159181));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159182));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159183));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159184));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159185));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159186));
        assertEquals(INTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(159187));

        assertEquals(EXTERNAL_BUSINESS_ERROR, errorMap.getAsmErrorCode(159179));
        assertEquals(EXTERNAL_BUSINESS_ERROR, errorMap.getAsmErrorCode(159170));
        assertEquals(EXTERNAL_BUSINESS_ERROR, errorMap.getAsmErrorCode(159171));
        assertEquals(EXTERNAL_BUSINESS_ERROR, errorMap.getAsmErrorCode(159172));
        assertEquals(EXTERNAL_BUSINESS_ERROR, errorMap.getAsmErrorCode(159167));
        assertEquals(EXTERNAL_BUSINESS_ERROR, errorMap.getAsmErrorCode(159160));
        assertEquals(EXTERNAL_BUSINESS_ERROR, errorMap.getAsmErrorCode(159140));
        assertEquals(EXTERNAL_BUSINESS_ERROR, errorMap.getAsmErrorCode(159121));
        assertEquals(EXTERNAL_BUSINESS_ERROR, errorMap.getAsmErrorCode(159197));
        assertEquals(EXTERNAL_BUSINESS_ERROR, errorMap.getAsmErrorCode(159413));

        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getAsmErrorCode(153116));


    }
}
