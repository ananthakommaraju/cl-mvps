package com.lloydsbanking.salsa.activate.administer.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.soap.asm.f425.objects.F425Resp;
import com.lloydsbanking.salsa.soap.asm.f425.objects.ProductOffered;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Category(UnitTest.class)
public class F425ResponseToApplicationDetailsConverterTest {
    TestDataHelper testDataHelper;
    F425ResponseToApplicationDetailsConverter converter;
    F425Resp f425Resp;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        converter = new F425ResponseToApplicationDetailsConverter();
        f425Resp = testDataHelper.createF425Resp();
    }

    @Test
    public void convertTest() {
        ApplicationDetails applicationDetails = converter.convert(f425Resp);
        assertEquals("2", applicationDetails.getApplicationStatus());
        assertEquals("2", applicationDetails.getScoreResult());
        assertEquals("002", applicationDetails.getReferralCodes().get(0).getCode());
    }

    @Test
    public void convertTestWithNullResultDetails() {
        f425Resp.setResultsDetails(null);
        ApplicationDetails applicationDetails = converter.convert(f425Resp);
        assertEquals(null, applicationDetails.getApplicationStatus());
        assertEquals(null, applicationDetails.getScoreResult());
        assertEquals("002", applicationDetails.getReferralCodes().get(0).getCode());
    }

    @Test
    public void convertTestWithEmptyDecisionDetails() {
        f425Resp.getDecisionDetails().clear();
        ApplicationDetails applicationDetails = converter.convert(f425Resp);
        assertEquals("2", applicationDetails.getApplicationStatus());
        assertEquals("2", applicationDetails.getScoreResult());
        assertEquals(0, applicationDetails.getReferralCodes().size());
    }

    @Test
    public void convertTestWithEmptyFacilityOffered() {
        f425Resp.getFacilitiesOffered().clear();
        ApplicationDetails applicationDetails = converter.convert(f425Resp);
        assertEquals("2", applicationDetails.getApplicationStatus());
        assertEquals("2", applicationDetails.getScoreResult());
        assertEquals(0, applicationDetails.getProductOptions().size());

    }

    @Test
    public void testConvertForProductOffered() {
        f425Resp.getProductOffered().add(new ProductOffered());
        f425Resp.getProductOffered().get(0).setProductOfferedAm("123");
        ApplicationDetails applicationDetails = converter.convert(f425Resp);
        assertEquals("1.23", applicationDetails.getCreditLimit().getAmount().toString());
    }

    @Test
    public void testConvertForProductOfferedNull() {
        f425Resp.getFacilitiesOffered().get(0).setCSFacilityOfferedAm("");
        ApplicationDetails applicationDetails = converter.convert(f425Resp);
        assertNull( applicationDetails.getProductOptions().get(0).getOptionsValue());
    }
}
