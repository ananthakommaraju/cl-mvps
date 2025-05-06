package com.lloydsbanking.salsa.offer.identify.utility;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.constant.CustomerSegment;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.offer.LegalEntityMapUtility;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
import lib_sim_bo.businessobjects.Product;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

@Category(UnitTest.class)
public class CustomerUtilityTest {

    private CustomerUtility customerUtility;
    private TestDataHelper testDataHelper;
    private LegalEntityMapUtility legalEntityMapUtility;
    private HashMap<String, String> legalEntityMap;
    private List<ReferenceDataLookUp> referenceDataLkp;
    List<Product> productList;

    @Before
    public void setUp() {
        customerUtility = new CustomerUtility();
        testDataHelper = new TestDataHelper();
        legalEntityMap = new HashMap<>();
        legalEntityMap.put("LTB", "ibl");
        productList = new ArrayList<>();
    }

    @Test
    public void testGetCustomerSegmentNonFranchised() {
        assertEquals(CustomerSegment.NON_FRANCHISED.getValue(), customerUtility.getCustomerSegment(productList));
    }

    @Test
    public void testGetCustomerSegmentNonAligned() {
        Product product = new Product();
        product.setBrandName("LTB");
        productList.add(product);
        assertEquals(CustomerSegment.NON_ALIGNED.getValue(), customerUtility.getCustomerSegment(productList));
    }

    @Test
    public void testGetCustomerSegmentFranchised() {
        Product product = new Product();
        product.setBrandName("LTB");
        product.setProductIdentifier("1");
        productList.add(product);
        assertEquals(CustomerSegment.FRANCHISED.getValue(), customerUtility.getCustomerSegment(productList));
    }
/*
    @Test
    public void testGetCustomerSegmentNonAligned() {
        ProductPartyData productPartyData=new ProductPartyData();
        productPartyData.setSellerLegalEntCd("");
        productList.add(productPartyData);
        assertEquals(CustomerUtility.NON_ALIGNED, customerUtility.getCustomerSegment(productList, "LTB", legalEntityMap));
    }*/

    @Test
    public void testGetCBSCustomerNumberNull() {
        assertNull(customerUtility.getCBSCustomerNumber(productList));
    }

    @Test
    public void testGetCBSCustomerNumberNotNull() {
        Product product = new Product();
        product.setBrandName("LTB");
        product.setExtPartyIdTx("2");
        ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
        extSysProdIdentifier.setSystemCode("00004");
        productList.add(product);
        assertEquals(null, customerUtility.getCBSCustomerNumber(productList));
    }

    @Test
    public void testCheckCustomerIdRageEqualToRange() {
        assertFalse(customerUtility.checkCustomerIdRange("9120996000000000000"));
    }

    @Test
    public void testCheckCustomerIdRageInsideRange() {
        assertFalse(customerUtility.checkCustomerIdRange("9120996000000000001"));
    }

    @Test
    public void testCheckCustomerIdRageOutsideRange() {
        assertTrue(customerUtility.checkCustomerIdRange("9120995000000000045"));
    }

    @Test
    public void testCheckCustomerIdRageExtSysPartyIdNull() {
        assertFalse(customerUtility.checkCustomerIdRange(null));
    }

    @Test
    public void testGetFDICustomerIDNotNull() {
        Product product = new Product();
        product.setBrandName("LTB");
        product.setExtPartyIdTx(" 9120995000000000045 ");
        productList.add(product);
        assertEquals(null, customerUtility.getFDICustomerID(productList));
    }

    @Test
    public void testGetFDIWIthEmpty() {
        Product product = new Product();
        product.setBrandName("LTB");
        product.setExtPartyIdTx(" 9120995000000000045 ");
        product.getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        product.getExternalSystemProductIdentifier().get(0).setSystemCode("");
        productList.add(product);
        assertEquals(null, customerUtility.getFDICustomerID(productList));
    }

    @Test
    public void testGetFDICustomerIDNull() {
        Product product = new Product();
        product.setBrandName("LTB");
        product.setExtPartyIdTx("9120996000000000000");
        productList.add(product);
        assertNull(customerUtility.getFDICustomerID(productList));
    }

    @Test
    public void testCbsCustomerNumber() {
        Product product = new Product();
        product.setBrandName("LTB");
        product.setExtPartyIdTx("2");
        ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
        extSysProdIdentifier.setSystemCode("00004");
        product.getExternalSystemProductIdentifier().add(extSysProdIdentifier);
        product.setProductIdentifier("1");

        productList.add(product);
        String cbsCustomerNumber = customerUtility.getCBSCustomerNumber(productList);
        Assert.assertEquals("2", cbsCustomerNumber);
    }

    @Test
    public void testCbsCustomerNumberWhenProdIdISNot1() {
        Product product = new Product();
        product.setBrandName("LTB");
        product.setExtPartyIdTx("4");
        ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
        extSysProdIdentifier.setSystemCode("00004");
        product.getExternalSystemProductIdentifier().add(extSysProdIdentifier);
        product.setProductIdentifier("2");

        productList.add(product);
        String cbsCustomerNumber = customerUtility.getCBSCustomerNumber(productList);
        Assert.assertEquals("4", cbsCustomerNumber);
    }

    @Test
    public void testFdiCustomerId() {
        Product product = new Product();
        product.setBrandName("LTB");
        product.setExtPartyIdTx("9120996000000000000");
        ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
        extSysProdIdentifier.setSystemCode("13");
        product.getExternalSystemProductIdentifier().add(extSysProdIdentifier);
        productList.add(product);
        String fdiCustomerId = customerUtility.getFDICustomerID(productList);
        assertEquals(null, fdiCustomerId);
    }

}
