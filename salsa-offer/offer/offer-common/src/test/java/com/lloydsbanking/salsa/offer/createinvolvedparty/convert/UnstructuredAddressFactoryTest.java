package com.lloydsbanking.salsa.offer.createinvolvedparty.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.UnstructuredAddressType;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class UnstructuredAddressFactoryTest {

    @Test
    public void testGenerateUnstructuredAddressForBfpoAddressIndicatorTrue() {
        UnstructuredAddress unstructuredAddress = new TestDataHelper().createUnstructuredAddressWithAllAddressLines();
        UnstructuredAddressType unstructuredAddressType = new UnstructuredAddressFactory().generateUnstructuredAddress(unstructuredAddress, true);

        assertEquals(unstructuredAddress.getAddressLine1(), unstructuredAddressType.getAddressLine1Tx());
        assertEquals(unstructuredAddress.getAddressLine2(), unstructuredAddressType.getAddressLine2Tx());
        assertEquals(unstructuredAddress.getAddressLine3(), unstructuredAddressType.getAddressLine3Tx());
        assertEquals(unstructuredAddress.getAddressLine4(), unstructuredAddressType.getAddressLine4Tx());
        assertEquals("ZZ00ZZ", unstructuredAddressType.getPostCd());
    }

    @Test
    public void testGenerateUnstructuredAddressForAddressLine2NotNullAndCombinedAddressLessThanMaxLength() {
        UnstructuredAddress unstructuredAddress = new TestDataHelper().createUnstructuredAddressWithAllAddressLines();
        UnstructuredAddressType unstructuredAddressType = new UnstructuredAddressFactory().generateUnstructuredAddress(unstructuredAddress, false);

        assertEquals(unstructuredAddress.getAddressLine3() +" "+ unstructuredAddress.getAddressLine2(), unstructuredAddressType.getAddressLine1Tx());
        assertEquals(unstructuredAddress.getAddressLine1() +" "+ unstructuredAddress.getAddressLine4(), unstructuredAddressType.getAddressLine2Tx());
        assertEquals(unstructuredAddress.getAddressLine5(), unstructuredAddressType.getAddressLine3Tx());
        assertEquals(unstructuredAddress.getAddressLine6(), unstructuredAddressType.getAddressLine4Tx());
    }

    @Test
    public void testGenerateUnstructuredAddressForAddressLine2NotNullAndCombinedAddressGreaterThanMaxLength() {
        UnstructuredAddress unstructuredAddress = new TestDataHelper().createUnstructuredAddressWithAllAddressLines();
        unstructuredAddress.setAddressLine2("abcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcde");
        UnstructuredAddressType unstructuredAddressType = new UnstructuredAddressFactory().generateUnstructuredAddress(unstructuredAddress, false);
        assertEquals("3 abcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabc", unstructuredAddressType.getAddressLine1Tx());
        assertEquals(unstructuredAddress.getAddressLine1() +" "+ unstructuredAddress.getAddressLine4(), unstructuredAddressType.getAddressLine2Tx());
        assertEquals(unstructuredAddress.getAddressLine5(), unstructuredAddressType.getAddressLine3Tx());
        assertEquals(unstructuredAddress.getAddressLine6(), unstructuredAddressType.getAddressLine4Tx());
    }

    @Test
    public void testGenerateUnstructuredAddressForAddressLine2NotNullAndAddressLine1And3And5Null() {
        UnstructuredAddress unstructuredAddress = new TestDataHelper().createUnstructuredAddressWithAllAddressLines();
        unstructuredAddress.setAddressLine1(null);
        unstructuredAddress.setAddressLine3(null);
        unstructuredAddress.setAddressLine5(null);
        UnstructuredAddressType unstructuredAddressType = new UnstructuredAddressFactory().generateUnstructuredAddress(unstructuredAddress, false);
        assertEquals(unstructuredAddress.getAddressLine2(), unstructuredAddressType.getAddressLine1Tx());
        assertEquals(unstructuredAddress.getAddressLine4(), unstructuredAddressType.getAddressLine2Tx());
        assertEquals(unstructuredAddress.getAddressLine6(), unstructuredAddressType.getAddressLine3Tx());
        assertEquals(unstructuredAddress.getAddressLine7(), unstructuredAddressType.getAddressLine4Tx());
    }

    @Test
    public void testGenerateUnstructuredAddressForAddressLine2Null() {
        UnstructuredAddress unstructuredAddress = new TestDataHelper().createUnstructuredAddressWithAllAddressLines();
        unstructuredAddress.setAddressLine2(null);

        UnstructuredAddressType unstructuredAddressType = new UnstructuredAddressFactory().generateUnstructuredAddress(unstructuredAddress, false);
        assertEquals(unstructuredAddress.getAddressLine3() +" "+ unstructuredAddress.getAddressLine1() +" "+ unstructuredAddress.getAddressLine4(), unstructuredAddressType.getAddressLine1Tx());
        assertEquals(unstructuredAddress.getAddressLine5(), unstructuredAddressType.getAddressLine2Tx());
        assertEquals(unstructuredAddress.getAddressLine6(), unstructuredAddressType.getAddressLine3Tx());
        assertEquals(unstructuredAddress.getAddressLine7(), unstructuredAddressType.getAddressLine4Tx());
    }

    @Test
    public void testGenerateUnstructuredAddressForAddressLine2NullAndCombinedAddressGreaterThanMaxLength() {
        UnstructuredAddress unstructuredAddress = new TestDataHelper().createUnstructuredAddressWithAllAddressLines();
        unstructuredAddress.setAddressLine2(null);
        unstructuredAddress.setAddressLine1("abcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdef");
        UnstructuredAddressType unstructuredAddressType = new UnstructuredAddressFactory().generateUnstructuredAddress(unstructuredAddress, false);
        assertEquals("3 abcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcd", unstructuredAddressType.getAddressLine1Tx());
        assertEquals(unstructuredAddress.getAddressLine5(), unstructuredAddressType.getAddressLine2Tx());
        assertEquals(unstructuredAddress.getAddressLine6(), unstructuredAddressType.getAddressLine3Tx());
        assertEquals(unstructuredAddress.getAddressLine7(), unstructuredAddressType.getAddressLine4Tx());
    }

    @Test
    public void testGenerateUnstructuredAddressForAddressLine2NullAndAddressLine3And5Null() {
        UnstructuredAddress unstructuredAddress = new TestDataHelper().createUnstructuredAddressWithAllAddressLines();
        unstructuredAddress.setAddressLine2(null);
        unstructuredAddress.setAddressLine3(null);
        unstructuredAddress.setAddressLine5(null);
        UnstructuredAddressType unstructuredAddressType = new UnstructuredAddressFactory().generateUnstructuredAddress(unstructuredAddress, false);
        assertEquals(unstructuredAddress.getAddressLine1() +" "+ unstructuredAddress.getAddressLine4(), unstructuredAddressType.getAddressLine1Tx());
        assertEquals(unstructuredAddress.getAddressLine6(), unstructuredAddressType.getAddressLine2Tx());
        assertEquals(unstructuredAddress.getAddressLine7(), unstructuredAddressType.getAddressLine3Tx());

    }
}
