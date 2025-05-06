package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lib_sim_bo.businessobjects.Individual;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class QuestionIsIndividualANationalOfTest {

    @Test
    public void testIsIndividualANationalOfReturnsFalse() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        Individual individual = mock(Individual.class);
        when(individual.getNationality()).thenReturn("AFG");
        boolean ask = QuestionIsIndividualANationalOf.pose().givenAnIndividual(individual).givenAValue("AFG").ask();
        assertFalse(ask);
        verify(individual).getNationality();
    }

    @Test
    public void testIsIndividualANationalOfReturnsTrue() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        Individual individual = mock(Individual.class);
        when(individual.getNationality()).thenReturn("Nationality");
        boolean ask = QuestionIsIndividualANationalOf.pose().givenAnIndividual(individual).givenAValue("AFG").ask();
        assertTrue(ask);
        verify(individual).getNationality();
    }

    @Test
    public void testIsIndividualANationalOfReturnsFalseIfIndividualIsAlsoOfABlockedNationality() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        Individual individual = mock(Individual.class);
        when(individual.getNationality()).thenReturn("AFG");
        boolean ask = QuestionIsIndividualANationalOf.pose().givenAnIndividual(individual).givenAValue("AFG:CMR").ask();
        assertFalse(ask);
        verify(individual).getNationality();
    }
    @Test
    public void testIsIndividualANationalOfReturnsFalseIfIndividualHasPreviousNationalityWhichIsBlocked() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        Individual individual = mock(Individual.class);
        ArrayList<String> previousNationalities = new ArrayList<String>();
        previousNationalities.add("AFG");
        when(individual.getNationality()).thenReturn("GBR");
        when(individual.getPreviousNationalities()).thenReturn(previousNationalities);
        boolean ask = QuestionIsIndividualANationalOf.pose().givenAnIndividual(individual).givenAValue("AFG").ask();
        assertFalse(ask);
        verify(individual, times(1)).getPreviousNationalities();
        verify(individual, times(1)).getNationality();
    }

    @Test
    public void testIsIndividualANationalOfReturnsTrueForBlockedNationalityNull() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        Individual individual = mock(Individual.class);
        when(individual.getNationality()).thenReturn("AFG");
        boolean ask = QuestionIsIndividualANationalOf.pose().givenAnIndividual(individual).givenAValue(null).ask();
        assertTrue(ask);
        verify(individual).getNationality();
    }

}