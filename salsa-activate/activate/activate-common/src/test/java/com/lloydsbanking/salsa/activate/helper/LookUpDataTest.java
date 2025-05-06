package com.lloydsbanking.salsa.activate.helper;


import com.lloydsbanking.salsa.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class LookUpDataTest {

    LookUpData lookUpData;

    @Before
    public void setUp() {
        lookUpData = new LookUpData();
    }

    @Test
    public void testPartyEvidenceTypeCode() {
        lookUpData.setPartyEvidenceTypeCode("partyEvidenceTypeCode");
        assertEquals("partyEvidenceTypeCode", lookUpData.getPartyEvidenceTypeCode());
    }

    @Test
    public void testPartyEvidencePurposeCode() {
        lookUpData.setPartyEvidencePurposeCode("partyEvidencePurposeCode");
        assertEquals("partyEvidencePurposeCode", lookUpData.getPartyEvidencePurposeCode());
    }

    @Test
    public void testAddressEvidenceTypeCode() {
        lookUpData.setAddressEvidenceTypeCode("AddressEvidenceTypeCode");
        assertEquals("AddressEvidenceTypeCode", lookUpData.getAddressEvidenceTypeCode());
    }

    @Test
    public void testAddressEvidencePurposeCode() {
        lookUpData.setAddressEvidencePurposeCode("AddressEvidencePurposeCode");
        assertEquals("AddressEvidencePurposeCode", lookUpData.getAddressEvidencePurposeCode());
    }

}
