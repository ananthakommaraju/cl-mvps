package com.lloydsbanking.salsa.ppae.client;

import lib_sim_bo.businessobjects.CurrencyAmount;
import lib_sim_bo.businessobjects.Employer;
import lib_sim_bo.businessobjects.Individual;
import lib_sim_bo.businessobjects.IndividualName;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

public class IndividualBuilderCC {
    Individual isPlayedBy;

    public IndividualBuilderCC() {
        this.isPlayedBy = new Individual();
    }

    public Individual build() {
        return isPlayedBy;
    }

    public IndividualBuilderCC individualName(List<IndividualName> individualNameList) {
        isPlayedBy.getIndividualName().addAll(individualNameList);
        return this;
    }

    public IndividualBuilderCC residentialStatus(String residentialStatus) {
        isPlayedBy.setResidentialStatus(residentialStatus);
        return this;
    }

    public IndividualBuilderCC birthDate(XMLGregorianCalendar birthDate) {
        isPlayedBy.setBirthDate(birthDate);
        return this;
    }

    public IndividualBuilderCC nationality(String nationality) {
        isPlayedBy.setNationality(nationality);
        return this;
    }


    public IndividualBuilderCC maritalStatus(String maritalStatus) {
        isPlayedBy.setMaritalStatus(maritalStatus);
        return this;
    }

    public IndividualBuilderCC gender(String gender) {
        isPlayedBy.setGender(gender);
        return this;
    }



    public IndividualBuilderCC setEmploymentDetails(String employmentStatus, String currentEmploymentDuration, CurrencyAmount grossAnnualIncome, String occupation) {
        isPlayedBy.setEmploymentStatus(employmentStatus);
        isPlayedBy.setCurrentEmploymentDuration(currentEmploymentDuration);
        isPlayedBy.setGrossAnnualIncome(grossAnnualIncome);
        isPlayedBy.setOccupation(occupation);
        return this;
    }


    public IndividualBuilderCC currentEmployer(Employer currentEmployer) {
        isPlayedBy.setCurrentEmployer(currentEmployer);
        return this;
    }


}
