package com.lloydsbanking.salsa.offer.createinvolvedparty.evaluate;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.PersonalUpdDataType;
import lib_sim_bo.businessobjects.Customer;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;

import static org.junit.Assert.*;

@Category(UnitTest.class)
public class EvaluatePersonalUpdDataTest {



    EvaluatePersonalUpdData evaluatePersonalUpdData = new EvaluatePersonalUpdData();

    @Test
    public void testGeneratePersonalUpdDataForMiddleNameSize1() throws ParseException, DatatypeConfigurationException {
        Customer primaryInvolvedParty = new TestDataHelper().primaryInvolvedParty();
        primaryInvolvedParty.getIsPlayedBy().getIndividualName().add(new TestDataHelper().setIndividualName(1));
        PersonalUpdDataType personalUpdDataType = (evaluatePersonalUpdData.generatePersonalUpdData("CA", primaryInvolvedParty, false));
        assertEquals("P", personalUpdDataType.getPartyTypeCd());
        assertEquals("04041991", personalUpdDataType.getBirthDt());
        assertEquals("Jaiswal", personalUpdDataType.getSurname());
        assertEquals("Amita", personalUpdDataType.getFirstForeNm());
        assertEquals("sunil", personalUpdDataType.getSecondForeNm());
        assertEquals("prefix", personalUpdDataType.getPartyTl());
    }

    @Test
    public void testGeneratePersonalUpdDataForMiddleNameSize2() throws ParseException, DatatypeConfigurationException {
        Customer primaryInvolvedParty = new TestDataHelper().primaryInvolvedParty();
        primaryInvolvedParty.getIsPlayedBy().getIndividualName().add(new TestDataHelper().setIndividualName(2));
        PersonalUpdDataType personalUpdDataType = (evaluatePersonalUpdData.generatePersonalUpdData("CA", primaryInvolvedParty, false));
        assertEquals("P", personalUpdDataType.getPartyTypeCd());
        assertEquals("04041991", personalUpdDataType.getBirthDt());
        assertEquals("Jaiswal", personalUpdDataType.getSurname());
        assertEquals("Amita", personalUpdDataType.getFirstForeNm());
        assertEquals("sunil", personalUpdDataType.getSecondForeNm());
        assertEquals("kumar", personalUpdDataType.getThirdForeNm());
        assertEquals("prefix", personalUpdDataType.getPartyTl());
    }

    @Test
    public void testGeneratePersonalUpdDataForMiddleNameSize0() throws ParseException, DatatypeConfigurationException {
        Customer primaryInvolvedParty = new TestDataHelper().primaryInvolvedParty();
        primaryInvolvedParty.getIsPlayedBy().getIndividualName().add(new TestDataHelper().setIndividualName(0));
        PersonalUpdDataType personalUpdDataType = (evaluatePersonalUpdData.generatePersonalUpdData("CA", primaryInvolvedParty, false));
        assertEquals("04041991", personalUpdDataType.getBirthDt());
        assertEquals("Jaiswal", personalUpdDataType.getSurname());
        assertEquals("Amita", personalUpdDataType.getFirstForeNm());
        assertTrue(personalUpdDataType.getThirdForeNm().isEmpty());
        assertEquals("prefix", personalUpdDataType.getPartyTl());


    }

    @Test
    public void testGeneratePersonalUpdData() throws ParseException, DatatypeConfigurationException {
        Customer primaryInvolvedParty = new TestDataHelper().primaryInvolvedParty();
        primaryInvolvedParty.getIsPlayedBy().getIndividualName().add(new TestDataHelper().setIndividualName(1));

        primaryInvolvedParty.setApplicantType("02");

        PersonalUpdDataType personalUpdDataType = (evaluatePersonalUpdData.generatePersonalUpdData("SA", primaryInvolvedParty, true));
        assertEquals("P", personalUpdDataType.getPartyTypeCd());
        assertEquals("04041991", personalUpdDataType.getBirthDt());
        assertEquals("Jaiswal", personalUpdDataType.getSurname());
        assertEquals("Amita", personalUpdDataType.getFirstForeNm());
        assertEquals("sunil", personalUpdDataType.getSecondForeNm());
        assertEquals("prefix", personalUpdDataType.getPartyTl());
        System.out.println(personalUpdDataType.getMailMkt());
        assertEquals("Y",personalUpdDataType.getMailMkt().getMktAuthMailIn());
        assertEquals("Y",personalUpdDataType.getPhoneMkt().getMktAuthPhoneIn());
    }


}
