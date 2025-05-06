package com.lloydsbanking.salsa.eligibility.service.utility;

import com.lloydsbanking.salsa.UnitTest;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class ProductArrangementFacadeFactoryTest {


    @Test
    public void shouldReturnEmptyListWhenArrangementListIsEmpty() throws Exception {
        List<ProductArrangement> productArrangements = new ArrayList<>();

        List<ProductArrangementFacade> productArrangementFacadeServicing = ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements);

        assertTrue(CollectionUtils.isEmpty(productArrangementFacadeServicing));

    }

    @Test
    public void shouldReturnListContainingSalesProductArrangmentFacades() {
        List<lb_gbo_sales.ProductArrangement> productArrangements = new ArrayList<>();

        lb_gbo_sales.ProductArrangement productArrangement = new lb_gbo_sales.ProductArrangement();
        productArrangement.setAccountNumber("13242");
        productArrangements.add(productArrangement);

        List<ProductArrangementFacade> productArrangementFacadeSales = ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements);

        assertFalse(CollectionUtils.isEmpty(productArrangementFacadeSales));

        assertTrue(productArrangementFacadeSales.get(0).salesProductArrangement instanceof lb_gbo_sales.ProductArrangement);

    }

    @Test
    public void shouldReturnListContainingServiceingProductArrangmentFacades() {
        List<lib_sim_bo.businessobjects.ProductArrangement> productArrangements = new ArrayList<>();

        lib_sim_bo.businessobjects.ProductArrangement productArrangement = new lib_sim_bo.businessobjects.ProductArrangement();
        productArrangement.setAccountNumber("13242");
        productArrangements.add(productArrangement);

        List<ProductArrangementFacade> productArrangementFacadeServicing = ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements);

        assertFalse(CollectionUtils.isEmpty(productArrangementFacadeServicing));

        assertTrue(productArrangementFacadeServicing.get(0).serviceProductArrangement instanceof lib_sim_bo.businessobjects.ProductArrangement);
    }

    @Test
    public void shouldReturnEmptyListIfNullIfProductArrangmentIsNulllServicing() throws Exception {

        List<lib_sim_bo.businessobjects.ProductArrangement> productArrangements = null;

        List<ProductArrangementFacade> productArrangementFacadeServicing = ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements);

        assertTrue(productArrangementFacadeServicing.isEmpty());
    }

    @Test
    public void shouldReturnEmptyListIfNullIfProductArrangementIsNulllSales() throws Exception {

        List<lb_gbo_sales.ProductArrangement> productArrangements = null;

        List<ProductArrangementFacade> productArrangementFacadeSales = ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements);

        assertTrue(productArrangementFacadeSales.isEmpty());
    }
}