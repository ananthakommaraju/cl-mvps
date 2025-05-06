package com.lloydsbanking.salsa.offer.createinvolvedparty.errorcode;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class OcisErrorCodesTest {
    private RetrieveOcisErrorMap errorMap;
    private static final String EXTERNAL_BUSINESS_ERROR = "External Business Error";
    private static final String EXTERNAL_SERVICE_ERROR = "External Service Error";

    @Before
    public void setUp() {
        errorMap = new RetrieveOcisErrorMap();
    }

    @Test
    public void testGetOcisErrorCode() {
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(163002));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165084));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165035));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165036));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165038));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165043));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165046));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165066));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165067));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165019));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165022));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165025));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165030));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165032));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165033));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165002));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165003));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165004));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165005));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165006));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165008));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165011));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165001));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165010));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165018));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165021));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165039));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(165007));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(163002));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(163003));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(163008));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(163050));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(163051));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(163000));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(163004));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(163006));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(163007));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(163009));
        assertEquals(EXTERNAL_BUSINESS_ERROR, errorMap.getOcisErrorCode(161031));
        assertEquals(EXTERNAL_BUSINESS_ERROR, errorMap.getOcisErrorCode(165009));
        assertEquals(EXTERNAL_BUSINESS_ERROR, errorMap.getOcisErrorCode(165023));
        assertEquals(EXTERNAL_BUSINESS_ERROR, errorMap.getOcisErrorCode(165026));
        assertEquals(EXTERNAL_BUSINESS_ERROR, errorMap.getOcisErrorCode(165041));
        assertEquals(EXTERNAL_BUSINESS_ERROR, errorMap.getOcisErrorCode(165044));
        assertEquals(EXTERNAL_BUSINESS_ERROR, errorMap.getOcisErrorCode(165047));
        assertEquals(EXTERNAL_BUSINESS_ERROR, errorMap.getOcisErrorCode(165083));
        assertEquals(EXTERNAL_BUSINESS_ERROR, errorMap.getOcisErrorCode(165085));
        assertEquals(EXTERNAL_SERVICE_ERROR, errorMap.getOcisErrorCode(160999));
        assertEquals(EXTERNAL_BUSINESS_ERROR, errorMap.getOcisErrorCode(165031));
        assertEquals(EXTERNAL_BUSINESS_ERROR, errorMap.getOcisErrorCode(165034));
        assertEquals(EXTERNAL_BUSINESS_ERROR, errorMap.getOcisErrorCode(165037));
        assertEquals(EXTERNAL_BUSINESS_ERROR, errorMap.getOcisErrorCode(161031));


    }

}