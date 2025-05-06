package com.lloydsbanking.salsa.eligibility.service.questions;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class QuestionIsIndividualANationalOf extends AbstractIndividualQuestion implements AskQuestion {
    public static QuestionIsIndividualANationalOf pose() {
        return new QuestionIsIndividualANationalOf();
    }

    public boolean ask() {
        String nationality = individual.getNationality();
        List<String> nationalityList = new ArrayList();
        nationalityList.add(0, nationality);
        List<String> previousNationalityList = individual.getPreviousNationalities();
        if (!CollectionUtils.isEmpty(previousNationalityList)) {
            for (String prevNationality : previousNationalityList) {
                nationalityList.add(prevNationality);
            }
        }
        return (!isNationalityBlocked(threshold, nationalityList));
    }

    private boolean isNationalityBlocked(String blockedNationality, List<String> nationalityList) {
        boolean isBlocked = false;
        if (!StringUtils.isEmpty(blockedNationality)) {
            String[] nationalities = blockedNationality.split(":");
            for (String nationality : nationalities) {
                if (nationalityList.contains(nationality)) {
                    isBlocked = true;
                }
            }
        }
        return isBlocked;
    }
}
