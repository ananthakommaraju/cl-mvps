package com.lloydsbanking.salsa.activate.converter;

import com.lloydsbanking.salsa.UnitTest;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.PreferentialRate;
import lib_sim_bo.businessobjects.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class ObtainAddressProductAccountAndTariffAndPprIdForSATest {
    DepositArrangement depositArrangement;
    ObtainAddressProductAccountAndTariffAndPprIdForSA obtainAddressProductAccountAndTariffAndPprIdForSA;
    Map<String, String> productOptionMap;

    @Before
    public void setUp() {
        productOptionMap = new HashMap<>();
        productOptionMap.put("MTRF", "astrf");
        depositArrangement = new DepositArrangement();
        Product product = new Product();
        PreferentialRate preferentialRate = new PreferentialRate();
        preferentialRate.setPreferentialRateIdentifier("123");
        product.getProductPreferentialRate().add(preferentialRate);
        depositArrangement.setAssociatedProduct(product);
        depositArrangement.setInterestPaidfrequency("MIPF");
        obtainAddressProductAccountAndTariffAndPprIdForSA = new ObtainAddressProductAccountAndTariffAndPprIdForSA();

    }


    @Test
    public void checkPprID() {
        assertEquals("123", obtainAddressProductAccountAndTariffAndPprIdForSA.getPprID(depositArrangement));

    }

    @Test
    public void checkTariffSA() {

        assertEquals("astrf", obtainAddressProductAccountAndTariffAndPprIdForSA.getTariffSA(depositArrangement, productOptionMap));
    }

    @Test
    public void checkTariffSAForISA() {
        productOptionMap = new HashMap<>();
        productOptionMap.put("ISA", "1");
        productOptionMap.put("TRF", "trf");
        depositArrangement = new DepositArrangement();
        assertEquals("trf", obtainAddressProductAccountAndTariffAndPprIdForSA.getTariffSA(depositArrangement, productOptionMap));
    }

    @Test
    public void checkTariffSAForATRF() {
        productOptionMap = new HashMap<>();
        productOptionMap.put("ATRF", "atrf");
        depositArrangement = new DepositArrangement();
        assertEquals("atrf", obtainAddressProductAccountAndTariffAndPprIdForSA.getTariffSA(depositArrangement, productOptionMap));
    }

    @Test
    public void checkTariffSAForTRF() {
        productOptionMap = new HashMap<>();
        productOptionMap.put("TRF", "trf");
        depositArrangement = new DepositArrangement();
        assertEquals("trf", obtainAddressProductAccountAndTariffAndPprIdForSA.getTariffSA(depositArrangement, productOptionMap));
    }

    @Test
    public void checkTariffSAForMTRF() {
        productOptionMap = new HashMap<>();
        productOptionMap.put("MTRF", "mtrf");
        depositArrangement = new DepositArrangement();
        assertEquals("mtrf", obtainAddressProductAccountAndTariffAndPprIdForSA.getTariffSA(depositArrangement, productOptionMap));
    }

    @Test
    public void checkTariffSAForOMTRF() {
        productOptionMap = new HashMap<>();
        productOptionMap.put("OMTRF", "omtrf");
        depositArrangement = new DepositArrangement();
        assertEquals("omtrf", obtainAddressProductAccountAndTariffAndPprIdForSA.getTariffSA(depositArrangement, productOptionMap));
    }

}
