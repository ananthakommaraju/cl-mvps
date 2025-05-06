package com.lloydsbanking.salsa.opapca.client;

import lib_sim_bo.businessobjects.CurrencyAmount;
import lib_sim_bo.businessobjects.Individual;
import lib_sim_bo.businessobjects.IndividualName;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.List;

public class IndividualBuilder {
    Individual isPlayedBy;

    public IndividualBuilder() {
        this.isPlayedBy = new Individual();
    }

    public Individual build() {
        return isPlayedBy;
    }

    public IndividualBuilder individualName(List<IndividualName> individualNameList) {
        isPlayedBy.getIndividualName().addAll(individualNameList);
        return this;
    }

    public IndividualBuilder residentialStatus(String residentialStatus) {
        isPlayedBy.setResidentialStatus(residentialStatus);
        return this;
    }

    public IndividualBuilder birthDate(XMLGregorianCalendar birthDate) {
        isPlayedBy.setBirthDate(birthDate);
        return this;
    }

    public IndividualBuilder nationality(String nationality) {
        isPlayedBy.setNationality(nationality);
        return this;
    }

    public IndividualBuilder countryOfBirth(String countryOfBirth) {
        isPlayedBy.setCountryOfBirth(countryOfBirth);
        return this;
    }

    public IndividualBuilder numberOfDependents(BigInteger numberOfDependents) {
        isPlayedBy.setNumberOfDependents(numberOfDependents);
        return this;
    }

    public IndividualBuilder maritalStatus(String maritalStatus) {
        isPlayedBy.setMaritalStatus(maritalStatus);
        return this;
    }

    public IndividualBuilder gender(String gender) {
        isPlayedBy.setGender(gender);
        return this;
    }

    public IndividualBuilder employmentStatus(String employmentStatus) {
        isPlayedBy.setEmploymentStatus(employmentStatus);
        return this;
    }

    public IndividualBuilder currentEmploymentDuration(String currentEmploymentDuration) {
        isPlayedBy.setCurrentEmploymentDuration(currentEmploymentDuration);
        return this;
    }

    public IndividualBuilder grossAnnualIncome(CurrencyAmount grossAnnualIncome) {
        isPlayedBy.setGrossAnnualIncome(grossAnnualIncome);
        return this;
    }

    public IndividualBuilder occupation(String occupation) {
        isPlayedBy.setOccupation(occupation);
        return this;
    }

    public IndividualBuilder totalSavingsAmount(CurrencyAmount totalSavingsAmount) {
        isPlayedBy.setTotalSavingsAmount(totalSavingsAmount);
        return this;
    }

    public IndividualBuilder netMonthlyIncome(CurrencyAmount netMonthlyIncome) {
        isPlayedBy.setNetMonthlyIncome(netMonthlyIncome);
        return this;
    }

    public IndividualBuilder monthlyLoanRepaymentAmount(CurrencyAmount monthlyLoanRepaymentAmount) {
        isPlayedBy.setMonthlyLoanRepaymentAmount(monthlyLoanRepaymentAmount);
        return this;
    }

    public IndividualBuilder monthlyMortgageAmount(CurrencyAmount monthlyMortgageAmount) {
        isPlayedBy.setMonthlyMortgageAmount(monthlyMortgageAmount);
        return this;
    }

    public IndividualBuilder otherMonthlyIncomeAmount(CurrencyAmount otherMonthlyIncomeAmount) {
        isPlayedBy.setOtherMonthlyIncomeAmount(otherMonthlyIncomeAmount);
        return this;
    }

}
