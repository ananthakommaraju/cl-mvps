package com.lloydsbanking.salsa.apacc.service.fulfil.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apacc.service.fulfil.rules.DSTFieldKeys;
import lib_sbo_cardacquire.businessojects.Field;
import lib_sim_bo.businessobjects.CurrencyAmount;
import lib_sim_bo.businessobjects.Employer;
import lib_sim_bo.businessobjects.Individual;
import lib_sim_bo.businessobjects.IndividualName;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.*;

@Category(UnitTest.class)
public class DSTIndividualFactoryTest {
    DSTIndividualFactory dstIndividualFactory;

    @Before
    public void setUp() {
        dstIndividualFactory = new DSTIndividualFactory();
        dstIndividualFactory.dstFieldHelper = new DSTFieldHelper();
    }

    @Test
    public void testGetIndividualDetailsFieldListWithResidenceStatus013() {
        Individual isPlayedBy = new Individual();
        isPlayedBy.setResidentialStatus("013");
        isPlayedBy.setGrossAnnualIncome(new CurrencyAmount());
        isPlayedBy.setCurrentEmployer(new Employer());
        isPlayedBy.getCurrentEmployer().setName("ADF");
        List<Field> fieldList = dstIndividualFactory.getIndividualDetailsFieldList(isPlayedBy);
        assertEquals(6, fieldList.size());
        assertTrue(fieldList.contains(dstIndividualFactory.dstFieldHelper.getField(DSTFieldKeys.RSTS.getKey(), "000")));
        assertFalse(fieldList.contains(dstIndividualFactory.dstFieldHelper.getField(DSTFieldKeys.RSTS.getKey(), "003")));
    }

    @Test
    public void testGetIndividualDetailsFieldListWithResidenceStatus003() {
        Individual isPlayedBy = new Individual();
        isPlayedBy.setResidentialStatus("003");
        isPlayedBy.setGrossAnnualIncome(new CurrencyAmount());
        isPlayedBy.setCurrentEmployer(new Employer());
        isPlayedBy.getCurrentEmployer().setName("ADF");
        List<Field> fieldList = dstIndividualFactory.getIndividualDetailsFieldList(isPlayedBy);
        assertEquals(6, fieldList.size());
        assertFalse(fieldList.contains(dstIndividualFactory.dstFieldHelper.getField(DSTFieldKeys.RSTS.getKey(), "000")));
        assertTrue(fieldList.contains(dstIndividualFactory.dstFieldHelper.getField(DSTFieldKeys.RSTS.getKey(), "003")));
    }

    @Test
    public void testGetIndividualDetailsFieldListWithResidenceStatus005() {
        Individual isPlayedBy = new Individual();
        isPlayedBy.setResidentialStatus("005");
        isPlayedBy.setGrossAnnualIncome(new CurrencyAmount());
        isPlayedBy.setCurrentEmployer(new Employer());
        isPlayedBy.getCurrentEmployer().setName("ADF");
        List<Field> fieldList = dstIndividualFactory.getIndividualDetailsFieldList(isPlayedBy);
        assertEquals(6, fieldList.size());
        assertFalse(fieldList.contains(dstIndividualFactory.dstFieldHelper.getField(DSTFieldKeys.RSTS.getKey(), "000")));
        assertTrue(fieldList.contains(dstIndividualFactory.dstFieldHelper.getField(DSTFieldKeys.RSTS.getKey(), "005")));
    }

    @Test
    public void testGetIndividualDetailsFieldListWithResidenceStatus006() {
        Individual isPlayedBy = new Individual();
        isPlayedBy.setResidentialStatus("006");
        isPlayedBy.setNetMonthlyIncome(new CurrencyAmount());
        isPlayedBy.setCurrentEmployer(new Employer());
        isPlayedBy.getCurrentEmployer().setName("ADF");
        List<Field> fieldList = dstIndividualFactory.getIndividualDetailsFieldList(isPlayedBy);
        assertEquals(6, fieldList.size());
        assertFalse(fieldList.contains(dstIndividualFactory.dstFieldHelper.getField(DSTFieldKeys.RSTS.getKey(), "005")));
        assertTrue(fieldList.contains(dstIndividualFactory.dstFieldHelper.getField(DSTFieldKeys.RSTS.getKey(), "006")));
        assertTrue(fieldList.contains(dstIndividualFactory.dstFieldHelper.getFieldForStringValue(DSTFieldKeys.GINC.getKey(), String.valueOf(isPlayedBy.getNetMonthlyIncome().getAmount()))));
    }

    @Test
    public void testGetIndividualPrimaryDetailsFieldList() {
        Individual isPlayedBy = new Individual();
        isPlayedBy.getIndividualName().add(new IndividualName());
        isPlayedBy.getIndividualName().get(0).setPrefixTitle("Mr");
        isPlayedBy.getIndividualName().get(0).getMiddleNames().add("Kumar");
        List<String> keyList = new ArrayList<>();
        keyList.addAll(Arrays.asList(DSTFieldKeys.ATLE.getKey(), DSTFieldKeys.AFNM.getKey(),
                DSTFieldKeys.AMNM.getKey(), DSTFieldKeys.ASRN.getKey(), DSTFieldKeys.SDOB.getKey(), DSTFieldKeys.SEXA.getKey(), DSTFieldKeys.SNAT.getKey()));
        List<Field> fieldList = dstIndividualFactory.getIndividualPrimaryDetailsFieldList(isPlayedBy, keyList);
        assertEquals(7, fieldList.size());
    }
}
