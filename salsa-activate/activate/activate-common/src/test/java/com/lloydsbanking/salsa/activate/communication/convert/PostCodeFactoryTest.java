package com.lloydsbanking.salsa.activate.communication.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import lib_sim_bo.businessobjects.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Category(UnitTest.class)
public class PostCodeFactoryTest {
    private PostCodeFactory postCodeFactory;
    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        postCodeFactory = new PostCodeFactory();
        testDataHelper = new TestDataHelper();
    }

    @Test
     public void testGetMaskedPostcode() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("9052");
        Customer customer = new Customer();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsPAFFormat(true);
        postalAddress.setStructuredAddress(new StructuredAddress());
        postalAddress.getStructuredAddress().setPostCodeIn("12365");
        customer.getPostalAddress().add(postalAddress);
        depositArrangement.setGuardianDetails(customer);
        assertEquals("XXX12365", postCodeFactory.getMaskedPostcode(depositArrangement));
    }

    @Test
    public void testGetMaskedPostcodeWithEmptyPostCodeInUnstructuredAddress() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("9052");
        Customer customer = new Customer();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsPAFFormat(true);
        postalAddress.setUnstructuredAddress(new UnstructuredAddress());
        postalAddress.getUnstructuredAddress().setPostCode("");
        customer.getPostalAddress().add(postalAddress);
        depositArrangement.setGuardianDetails(customer);
        assertNull(postCodeFactory.getMaskedPostcode(depositArrangement));
    }

    @Test
    public void testGetMaskedPostcodeWithPostCodeOfLength4InUnstructuredAddress() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("9052");
        Customer customer = new Customer();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsPAFFormat(true);
        postalAddress.setUnstructuredAddress(new UnstructuredAddress());
        postalAddress.getUnstructuredAddress().setPostCode("1234");
        customer.getPostalAddress().add(postalAddress);
        depositArrangement.setGuardianDetails(customer);
        assertNull(postCodeFactory.getMaskedPostcode(depositArrangement));
    }

    @Test
    public void testGetPostCode() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("9052");
        Customer customer = new Customer();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsPAFFormat(true);
        postalAddress.setStructuredAddress(new StructuredAddress());
        postalAddress.getStructuredAddress().setPostCodeIn("12365");
        customer.getPostalAddress().add(postalAddress);
        depositArrangement.setPrimaryInvolvedParty(customer);
        assertEquals("12365", postCodeFactory.getPostCode(depositArrangement));
    }

    @Test
    public void testMaskedPostCodeForUnstructuredAddress(){
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("9052");
        Customer customer = new Customer();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setUnstructuredAddress(new UnstructuredAddress());
        postalAddress.getUnstructuredAddress().setPostCode("324556");
        customer.getPostalAddress().add(postalAddress);
        depositArrangement.setGuardianDetails(customer);
        assertEquals("XXX556", postCodeFactory.getMaskedPostcode(depositArrangement));
    }

    @Test
    public void testGetPostCodeForUnstructuredAddress() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("9052");
        Customer customer = new Customer();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setUnstructuredAddress(new UnstructuredAddress());
        postalAddress.getUnstructuredAddress().setPostCode("12365");
        customer.getPostalAddress().add(postalAddress);
        depositArrangement.setPrimaryInvolvedParty(customer);
        assertEquals("12365", postCodeFactory.getPostCode(depositArrangement));
    }

    @Test
    public void testGetMaskedPostcodeWhenStructureAddressIsNull() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("9052");
        Customer customer = new Customer();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsPAFFormat(true);
        customer.getPostalAddress().add(postalAddress);
        depositArrangement.setGuardianDetails(customer);
        assertNull(postCodeFactory.getMaskedPostcode(depositArrangement));
    }

    @Test
    public void testGetPostCodeWhenStructureAddressIsNull() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("9052");
        Customer customer = new Customer();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsPAFFormat(true);
        customer.getPostalAddress().add(postalAddress);
        depositArrangement.setPrimaryInvolvedParty(customer);
        assertNull(postCodeFactory.getPostCode(depositArrangement));
    }

    @Test
    public void testGetMaskedPostcodeWhenUnStructureAddressIsNull() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("9052");
        Customer customer = new Customer();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsPAFFormat(true);
        customer.getPostalAddress().add(postalAddress);
        depositArrangement.setGuardianDetails(customer);
        assertNull(postCodeFactory.getMaskedPostcode(depositArrangement));
    }
}
