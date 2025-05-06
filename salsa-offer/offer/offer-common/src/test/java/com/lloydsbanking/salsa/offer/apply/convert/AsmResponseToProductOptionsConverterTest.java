package com.lloydsbanking.salsa.offer.apply.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Resp;
import com.lloydsbanking.salsa.soap.asm.f205.objects.FacilitiesOffered;
import lib_sim_bo.businessobjects.ProductOptions;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Category(UnitTest.class)
public class AsmResponseToProductOptionsConverterTest {
    private AsmResponseToProductOptionsConverter converter;
    private List<ProductOptions> productOptionsList;

    @Before
    public void setUp() {
        converter = new AsmResponseToProductOptionsConverter();
        productOptionsList = null;
    }

    @Test
    public void creditScoreResponseToProductOptionsConverterTest() {
        F205Resp f205Resp = new F205Resp();
        f205Resp.getFacilitiesOffered().add(new FacilitiesOffered());
        f205Resp.getFacilitiesOffered().get(0).setCSFacilityOfferedAm("100");
        productOptionsList = converter.creditScoreResponseToProductOptionsConverter(f205Resp);

        assertEquals("1", productOptionsList.get(0).getOptionsValue());
    }

    @Test
    public void creditScoreResponseToProductOptionsConverterWithEmptyCSFacilityOfferedAmTest() {
        F205Resp f205Resp = new F205Resp();
        f205Resp.getFacilitiesOffered().add(new FacilitiesOffered());
        f205Resp.getFacilitiesOffered().get(0).setCSFacilityOfferedAm("");
        productOptionsList = converter.creditScoreResponseToProductOptionsConverter(f205Resp);
        assertNull(productOptionsList.get(0).getOptionsValue());
    }


}
