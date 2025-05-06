package com.lloydsbanking.salsa.offer.apply.evaluate;


import lib_sim_bo.businessobjects.CurrencyAmount;
import lib_sim_bo.businessobjects.OverdraftDetails;
import lib_sim_bo.businessobjects.ProductOptions;
import lib_sim_bo.businessobjects.RuleCondition;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class RuleConditionsEvaluator {

    public static final String CHEQUE_BOOK_OFFERED_FLAG_FEAT = "CHECK_BOOK_OFFERED_FLAG";
    public static final String OVERDRAFT_OFFERED_FLAG_FEAT = "OVERDRAFT_OFFERED_FLAG";
    public static final String CREDIT_CARD_OFFERED_FLAG_FEAT = "CREDIT_CARD_OFFERED_FLAG";
    public static final String CREDIT_CARD_LIMIT_AMOUNT_FEAT = "CREDIT_CARD_LIMIT_AMOUNT";
    public static final String DEBIT_CARD_RISK_CODE = "DEBIT_CARD_RISK_CODE";
    public static final String CREDIT_CARD_FAMILY_CODE = "CREDIT_CARD_FAMILY_CODE";
    private static final String OVERDRAFT_RISK_CODE = "OVERDRAFT_RISK_CODE";
    private static final int FEATURE_INDEX_FOR_CHEQUE_BOOK = 0;
    private static final int FEATURE_INDEX_FOR_OVERDRAFT = 1;
    private static final int FEATURE_INDEX_FOR_CREDIT_CARD_OFFERED_FLAG = 2;
    private static final int FEATURE_INDEX_FOR_DEBIT_CARD = 3;
    private static final int FEATURE_INDEX_FOR_CREDIT_CARD_FAMILY_CODE = 4;
    private static final int FEATURE_INDEX_FOR_OVERDRAFT_RISK_CODE = 5;


    public boolean setFacilitiesOffered(List<ProductOptions> productOptions, OverdraftDetails overdraftDetails, List<RuleCondition> conditions,
                                        String overdraftLmt) {
        boolean isOverdraftRequired = false;
        String[] features = {CHEQUE_BOOK_OFFERED_FLAG_FEAT, OVERDRAFT_OFFERED_FLAG_FEAT, CREDIT_CARD_OFFERED_FLAG_FEAT, DEBIT_CARD_RISK_CODE, CREDIT_CARD_FAMILY_CODE, OVERDRAFT_RISK_CODE};

        List<String> featList = Arrays.asList(features);


        String optionCode = "";
        String optionValue = "";
        for (ProductOptions productOptions1 : productOptions) {

            RuleCondition ruleCondition = new RuleCondition();
            CurrencyAmount currencyAmount = new CurrencyAmount();

            optionCode = productOptions1.getOptionsCode();
            optionValue = productOptions1.getOptionsValue();
            int codeIndex = featList.indexOf(optionCode);
            switch (codeIndex) {
                case FEATURE_INDEX_FOR_CHEQUE_BOOK:
                    ruleCondition.setName(CHEQUE_BOOK_OFFERED_FLAG_FEAT);
                    ruleCondition.setResult(optionValue);
                    conditions.add(ruleCondition);
                    break;

                case FEATURE_INDEX_FOR_OVERDRAFT:
                    isOverdraftRequired = isOverdraftRequired(overdraftDetails, optionValue, currencyAmount, overdraftLmt);
                    break;

                case FEATURE_INDEX_FOR_CREDIT_CARD_OFFERED_FLAG:
                    ruleCondition.setName(CREDIT_CARD_LIMIT_AMOUNT_FEAT);
                    currencyAmount.setAmount(BigDecimal.valueOf(Long
                            .valueOf(optionValue)));
                    ruleCondition.setValue(currencyAmount);
                    conditions.add(ruleCondition);
                    break;

                case FEATURE_INDEX_FOR_DEBIT_CARD:
                    ruleCondition.setName(DEBIT_CARD_RISK_CODE);
                    ruleCondition.setResult(optionValue);
                    conditions.add(ruleCondition);
                    break;

                case FEATURE_INDEX_FOR_CREDIT_CARD_FAMILY_CODE:
                    ruleCondition.setName(CREDIT_CARD_FAMILY_CODE);
                    ruleCondition.setResult(optionValue);
                    conditions.add(ruleCondition);
                    break;

                case FEATURE_INDEX_FOR_OVERDRAFT_RISK_CODE:
                    ruleCondition.setName(OVERDRAFT_RISK_CODE);
                    ruleCondition.setResult(optionValue);
                    conditions.add(ruleCondition);
                    break;

                default:
                    isOverdraftRequired = false;
                    ruleCondition.setName(CHEQUE_BOOK_OFFERED_FLAG_FEAT);
                    ruleCondition.setResult("N");
                    conditions.add(ruleCondition);
                    break;
            }

        }

        return isOverdraftRequired;
    }

    private boolean isOverdraftRequired(OverdraftDetails overdraftDetails, String optionValue, CurrencyAmount currencyAmount, String overdraftLmt) {
        boolean isOverdraftRequired;
        if (null != overdraftLmt) {
            long dbValue = Long.valueOf(overdraftLmt);
            long asmValue = Long.valueOf(optionValue);

            if (asmValue <= dbValue) {

                isOverdraftRequired = false;
            } else {
                isOverdraftRequired = true;
                currencyAmount.setAmount(BigDecimal.valueOf(Long.valueOf(optionValue)));
                overdraftDetails.setAmount(currencyAmount);

            }
        } else if ("0".equalsIgnoreCase(optionValue)) {
            isOverdraftRequired = false;
        } else {
            isOverdraftRequired = true;
            currencyAmount.setAmount(BigDecimal.valueOf(Long.valueOf(optionValue)));
            overdraftDetails.setAmount(currencyAmount);

        }
        return isOverdraftRequired;
    }
}
