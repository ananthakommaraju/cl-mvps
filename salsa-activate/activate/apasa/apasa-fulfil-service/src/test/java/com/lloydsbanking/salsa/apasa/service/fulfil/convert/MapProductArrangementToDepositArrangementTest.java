package com.lloydsbanking.salsa.apasa.service.fulfil.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apasa.TestDataHelper;
import lib_sim_bo.businessobjects.DepositArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;

import static junit.framework.Assert.assertEquals;

@Category(UnitTest.class)
public class MapProductArrangementToDepositArrangementTest {
    MapProductArrangementToDepositArrangement mapProductArrangementToDepositArrangement;
    TestDataHelper testDataHelper;

    @Before
    public void setUp() throws DatatypeConfigurationException {
        mapProductArrangementToDepositArrangement = new MapProductArrangementToDepositArrangement();
        testDataHelper = new TestDataHelper();
    }

    @Test
    public void testCreateDepositArrangement() throws DatatypeConfigurationException, ParseException {
        DepositArrangement depositArrangement = mapProductArrangementToDepositArrangement.createDepositArrangement(testDataHelper.createDepositArrangement("123"));
        assertEquals("123", depositArrangement.getArrangementId());


    }
}
