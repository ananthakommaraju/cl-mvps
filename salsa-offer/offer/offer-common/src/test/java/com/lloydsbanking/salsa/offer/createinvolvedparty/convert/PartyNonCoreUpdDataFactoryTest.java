package com.lloydsbanking.salsa.offer.createinvolvedparty.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.PartyNonCoreUpdDataType;
import lib_sim_bo.businessobjects.Individual;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class PartyNonCoreUpdDataFactoryTest {


    @Test
    public void testGeneratePartyNonCoreUpdData() {
        Individual isPlayedBy = new TestDataHelper().createIsPlayedBy();
        PartyNonCoreUpdDataType partyNonCoreUpdDataType = new PartyNonCoreUpdDataFactory().generatePartyNonCoreUpdData(isPlayedBy);

        assertEquals("1", String.valueOf(partyNonCoreUpdDataType.getMaritalStatusCd()));
        assertEquals("3", String.valueOf(partyNonCoreUpdDataType.getEmploymentStatusCd()));
        assertEquals(Short.valueOf(isPlayedBy.getResidentialStatus()), partyNonCoreUpdDataType.getResidStatusCd());
        assertEquals(Short.valueOf("0"),partyNonCoreUpdDataType.getOccupationalRoleCd());
    }

    @Test
    public void testGeneratePartyNonCoreUpdDataForNotNullOccupationType() {
        Individual isPlayedBy = new TestDataHelper().createIsPlayedByWithOccupationType();
        PartyNonCoreUpdDataType partyNonCoreUpdDataType = new PartyNonCoreUpdDataFactory().generatePartyNonCoreUpdData(isPlayedBy);

        assertEquals(Short.valueOf("1"), partyNonCoreUpdDataType.getOccupationalRoleCd());

    }
}
