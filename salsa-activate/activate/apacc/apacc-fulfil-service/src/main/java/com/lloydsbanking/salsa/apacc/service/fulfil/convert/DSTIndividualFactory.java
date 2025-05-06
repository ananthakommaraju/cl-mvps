package com.lloydsbanking.salsa.apacc.service.fulfil.convert;

import com.lloydsbanking.salsa.apacc.service.fulfil.rules.DSTFieldKeys;
import com.lloydsbanking.salsa.constant.Gender;
import lib_sbo_cardacquire.businessojects.Field;
import lib_sim_bo.businessobjects.Individual;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DSTIndividualFactory {

    private static final int INDEX_ZERO_FOR_PREFIX_TITLE = 0;
    private static final int INDEX_ONE_FOR_FIRST_NAME = 1;
    private static final int INDEX_TWO_FOR_MIDDLE_NAME = 2;
    private static final int INDEX_THREE_FOR_LAST_NAME = 3;
    private static final int INDEX_FOUR_FOR_BIRTH_DATE = 4;
    private static final int INDEX_FIVE_FOR_GENDER_CODE = 5;
    private static final int INDEX_SIX_FOR_NATIONALITY = 6;

    @Autowired
    DSTFieldHelper dstFieldHelper;

    public List<Field> getIndividualDetailsFieldList(Individual isPlayedBy) {
        List<Field> fieldList = new ArrayList<>();
        if (isPlayedBy != null) {
            fieldList.add(dstFieldHelper.getFieldForStringValue(DSTFieldKeys.MSTS.getKey(), isPlayedBy.getMaritalStatus()));
            if (Arrays.asList("001", "002", "013").contains(isPlayedBy.getResidentialStatus())) {
                fieldList.add(dstFieldHelper.getField(DSTFieldKeys.RSTS.getKey(), "000"));
            } else if (Arrays.asList("003", "004", "008", "009", "010", "014").contains(isPlayedBy.getResidentialStatus())) {
                fieldList.add(dstFieldHelper.getField(DSTFieldKeys.RSTS.getKey(), "003"));
            } else if ("005".equals(isPlayedBy.getResidentialStatus())) {
                fieldList.add(dstFieldHelper.getField(DSTFieldKeys.RSTS.getKey(), "005"));
            } else if ("006".equals(isPlayedBy.getResidentialStatus())) {
                fieldList.add(dstFieldHelper.getField(DSTFieldKeys.RSTS.getKey(), "006"));
            }
            fieldList.add(dstFieldHelper.getFieldForStringValue(DSTFieldKeys.ESTS.getKey(), isPlayedBy.getEmploymentStatus()));
            fieldList.add(dstFieldHelper.getFieldForStringValue(DSTFieldKeys.TEMP.getKey(), isPlayedBy.getCurrentEmploymentDuration()));

            if (isPlayedBy.getGrossAnnualIncome() != null) {
                fieldList.add(dstFieldHelper.getFieldForStringValue(DSTFieldKeys.GINC.getKey(), String.valueOf(isPlayedBy.getGrossAnnualIncome().getAmount())));
            } else if (isPlayedBy.getNetMonthlyIncome() != null) {
                fieldList.add(dstFieldHelper.getFieldForStringValue(DSTFieldKeys.GINC.getKey(), String.valueOf(isPlayedBy.getNetMonthlyIncome().getAmount())));
            }

            if (isPlayedBy.getCurrentEmployer() != null) {
                fieldList.add(dstFieldHelper.getFieldForStringValue(DSTFieldKeys.ENAM.getKey(), isPlayedBy.getCurrentEmployer().getName()));
            }
        }
        return fieldList;
    }


    public List<Field> getIndividualPrimaryDetailsFieldList(Individual individual, List<String> keyList) {
        List<Field> fieldList = new ArrayList<>();
        if (individual != null) {
            if (!individual.getIndividualName().isEmpty()) {
                fieldList.add(dstFieldHelper.getFieldForStringValue(keyList.get(INDEX_ZERO_FOR_PREFIX_TITLE), individual.getIndividualName().get(0).getPrefixTitle()));
                fieldList.add(dstFieldHelper.getFieldForStringValue(keyList.get(INDEX_ONE_FOR_FIRST_NAME), individual.getIndividualName().get(0).getFirstName()));
                if (!individual.getIndividualName().get(0).getMiddleNames().isEmpty()) {
                    fieldList.add(dstFieldHelper.getFieldForStringValue(keyList.get(INDEX_TWO_FOR_MIDDLE_NAME), individual.getIndividualName().get(0).getMiddleNames().get(0)));
                }
                fieldList.add(dstFieldHelper.getFieldForStringValue(keyList.get(INDEX_THREE_FOR_LAST_NAME), individual.getIndividualName().get(0).getLastName()));
            }
            fieldList.add(dstFieldHelper.getFieldForDateValue(keyList.get(INDEX_FOUR_FOR_BIRTH_DATE), individual.getBirthDate(), "ddMMyyyy", ""));
            fieldList.add(dstFieldHelper.getFieldForStringValue(keyList.get(INDEX_FIVE_FOR_GENDER_CODE), Gender.getGenderCode(individual.getGender())));
            fieldList.add(dstFieldHelper.getFieldForStringValue(keyList.get(INDEX_SIX_FOR_NATIONALITY), individual.getNationality()));
        }
        return fieldList;
    }
}
