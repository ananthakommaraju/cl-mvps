package com.lloydsbanking.salsa.ppae.client;

import lib_sim_bo.businessobjects.CurrencyAmount;
import lib_sim_bo.businessobjects.Individual;
import lib_sim_bo.businessobjects.IndividualName;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.List;

public class IndividualBuilderSA {
    Individual isPlayedBy;

    public IndividualBuilderSA() {
        this.isPlayedBy = new Individual();
    }

    public Individual build() {
        return isPlayedBy;
    }

    public IndividualBuilderSA individualName(List<IndividualName> individualNameList) {
        isPlayedBy.getIndividualName().addAll(individualNameList);
        return this;
    }

    public IndividualBuilderSA residentialStatus(String residentialStatus) {
        isPlayedBy.setResidentialStatus(residentialStatus);
        return this;
    }

    public IndividualBuilderSA personalDetails(XMLGregorianCalendar birthDate, String nationality, String countryOfBirth) {
        isPlayedBy.setBirthDate(birthDate);
        isPlayedBy.setNationality(nationality);
        isPlayedBy.setCountryOfBirth(countryOfBirth);
        return this;
    }

    public IndividualBuilderSA genderMaritalAndDependentDetails(BigInteger numberOfDependents, String maritalStatus, String gender) {
        isPlayedBy.setNumberOfDependents(numberOfDependents);
        isPlayedBy.setMaritalStatus(maritalStatus);
        isPlayedBy.setGender(gender);
        return this;
    }
    public IndividualBuilderSA employmentStatusAndCurrentEmploymentDuration(String employmentStatus, String currentEmploymentDuration) {
        isPlayedBy.setEmploymentStatus(employmentStatus);
        isPlayedBy.setCurrentEmploymentDuration(currentEmploymentDuration);
        return this;
    }

    public IndividualBuilderSA savingAndMonthlyIncome(CurrencyAmount totalSavingsAmount, CurrencyAmount netMonthlyIncome) {
        isPlayedBy.setTotalSavingsAmount(totalSavingsAmount);
        isPlayedBy.setNetMonthlyIncome(netMonthlyIncome);
        return this;
    }


    public IndividualBuilderSA monthlyLoanRepaymentAndMortgageAmount(CurrencyAmount monthlyLoanRepaymentAmount, CurrencyAmount monthlyMortgageAmount) {
        isPlayedBy.setMonthlyLoanRepaymentAmount(monthlyLoanRepaymentAmount);
        isPlayedBy.setMonthlyMortgageAmount(monthlyMortgageAmount);
        return this;
    }

    public IndividualBuilderSA otherMonthlyIncomeAmount(CurrencyAmount otherMonthlyIncomeAmount) {
        isPlayedBy.setOtherMonthlyIncomeAmount(otherMonthlyIncomeAmount);
        return this;
    }

}
