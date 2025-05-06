package com.lloydsbanking.salsa.offer.apply.evaluate;


import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.offer.apply.convert.AsmResponseToProductOptionsConverter;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Resp;
import lib_sim_bo.businessobjects.ProductOptions;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductOptionsEvaluator {

    @Autowired
    LookupDataRetriever offerLookupDataRetriever;

    @Autowired
    AsmResponseToProductOptionsConverter asmResponseToProductOptionsConverter;

    private static final String CHEQUE_BOOK_OFFERED_FLAG_CODE = "101";
    private static final String OVERDRAFT_OFFERED_FLAG_CODE = "102";
    private static final String GROUP_CODE = "CC_CROSS_SELL_FC";
    private static final String CHEQUE_BOOK_OFFERED_FLAG_FEAT = "CHECK_BOOK_OFFERED_FLAG";
    private static final String OVERDRAFT_OFFERED_FLAG_FEAT = "OVERDRAFT_OFFERED_FLAG";
    private static final String CREDIT_CARD_OFFERED_FLAG_FEAT = "CREDIT_CARD_OFFERED_FLAG";
    private static final String CREDIT_CARD_FAMILY_CODE = "CREDIT_CARD_FAMILY_CODE";
    private static final String DEBIT_CARD_RISK_CODE = "DEBIT_CARD_RISK_CODE";
    private static final String DEFAULT_DEBIT_CARD_OPTION_VALUE = "0000";
    private static final String OPTION_VALUE_ZERO = "0";
    private static final String OPTION_VALUE_FOR_CHEQUE_BOOK = "Y";
    private static final int CODE_INDEX_FOR_CHEQUE_BOOK = 0;
    private static final int CODE_INDEX_FOR_OVERDRAFT = 1;
    private static final int CODE_INDEX_FOR_CREDIT_CARD = 2;
    private static final int OPTION_VALUE_AT_FIRST_INDEX = 100;
    private static final String OVERDRAFT_RISK_CODE = "OVERDRAFT_RISK_CODE";


    public List<ProductOptions> getProductOptions(F205Resp f205Resp, String channelId) throws DataNotAvailableErrorMsg {

        List<ProductOptions> productOptionsFromAsmResponse = asmResponseToProductOptionsConverter.creditScoreResponseToProductOptionsConverter(f205Resp);
        List<ProductOptions> finalProductOptions = new ArrayList<>();
        if (productOptionsFromAsmResponse != null && !productOptionsFromAsmResponse.isEmpty()) {
            List<Integer> optionCodeList = new ArrayList<>();
            for (ProductOptions productOption : productOptionsFromAsmResponse) {
                setFinalProductOptions(channelId, productOption, finalProductOptions, optionCodeList);
            }
            ProductOptions productOptionForDebitCard = getProductOptionForDebitCard(optionCodeList);
            if (productOptionForDebitCard != null) {
                finalProductOptions.add(productOptionForDebitCard);
            }
            for (ProductOptions productOption : productOptionsFromAsmResponse) {
                String optionCode = productOption.getOptionsCode();
                if (OVERDRAFT_RISK_CODE.equalsIgnoreCase(optionCode)) {
                    finalProductOptions.add(productOption);
                }
            }

            for (ProductOptions productOption : finalProductOptions) {
                String optionCode = productOption.getOptionsCode();
                if (checkOptionCode(optionCode)) {
                    finalProductOptions.remove(productOption);
                }
            }
        }
        return finalProductOptions;
    }

    private boolean checkOptionCode(String optionCode) {
        boolean isOptionCodeCHEQUEBookOrOverDraftOrDebitCard = CHEQUE_BOOK_OFFERED_FLAG_FEAT.equalsIgnoreCase(optionCode)
                || OVERDRAFT_OFFERED_FLAG_FEAT.equalsIgnoreCase(optionCode)
                || DEBIT_CARD_RISK_CODE.equalsIgnoreCase(optionCode);
        boolean isOptionCodeCreditCardOrCreditCardFamily = CREDIT_CARD_OFFERED_FLAG_FEAT.equalsIgnoreCase(optionCode)
                || CREDIT_CARD_FAMILY_CODE.equalsIgnoreCase(optionCode) || OVERDRAFT_RISK_CODE.equalsIgnoreCase(optionCode);
        return !(isOptionCodeCHEQUEBookOrOverDraftOrDebitCard || isOptionCodeCreditCardOrCreditCardFamily);
    }

    private List<String> createCodeList(String channelId) throws DataNotAvailableErrorMsg {
        List<String> groupCodeList = new ArrayList<>();
        groupCodeList.add(GROUP_CODE);
        List<ReferenceDataLookUp> ccFamilyCodes = offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeList(channelId, groupCodeList);
        List<String> codeList = new ArrayList<>();
        codeList.add(CHEQUE_BOOK_OFFERED_FLAG_CODE);
        codeList.add(OVERDRAFT_OFFERED_FLAG_CODE);
        for (ReferenceDataLookUp familyCode : ccFamilyCodes) {
            if (GROUP_CODE.equalsIgnoreCase(familyCode.getGroupCode())) {
                codeList.add(familyCode.getLookupValueDesc());

            }
        }
        return codeList;
    }


    private void setProductOptions(ProductOptions productOption, String optionCode, String optionValue) {
        productOption.setOptionsCode(optionCode);
        productOption.setOptionsValue(optionValue);
    }


    private void setFinalProductOptions(String channelId, ProductOptions productOption, List<ProductOptions> finalProductOptions, List<Integer> optionCodeList) throws DataNotAvailableErrorMsg {
        List<String> codeList = createCodeList(channelId);
        String optionCode = productOption.getOptionsCode().replaceFirst("^0+(?!$)", "");
        String optionValue = productOption.getOptionsValue();

        try {
            optionCodeList.add(Integer.parseInt(optionCode));
        } catch (NumberFormatException e) {
        }
        int codeIndex = codeList.indexOf(optionCode);
        codeIndex = codeIndex > 1 ? 2 : codeIndex;
        switch (codeIndex) {
            case CODE_INDEX_FOR_CHEQUE_BOOK:
                setProductOptions(productOption, CHEQUE_BOOK_OFFERED_FLAG_FEAT, OPTION_VALUE_FOR_CHEQUE_BOOK);
                finalProductOptions.add(productOption);
                break;
            case CODE_INDEX_FOR_OVERDRAFT:
                setProductOptions(productOption, OVERDRAFT_OFFERED_FLAG_FEAT, optionValue);
                finalProductOptions.add(productOption);
                break;
            case CODE_INDEX_FOR_CREDIT_CARD:
                setProductOptions(productOption, CREDIT_CARD_OFFERED_FLAG_FEAT, optionValue);
                finalProductOptions.add(productOption);
                ProductOptions productOptionsForCCFamilyCode = new ProductOptions();
                setProductOptions(productOptionsForCCFamilyCode, CREDIT_CARD_FAMILY_CODE, optionCode);
                finalProductOptions.add(productOptionsForCCFamilyCode);
                break;
            default:
                break;
        }
    }

    private ProductOptions getProductOptionForDebitCard(List<Integer> optionCodeList) {

        ProductOptions productOptionsForDebitCard = null;
        if (optionCodeList != null) {
            Collections.sort(optionCodeList);
            productOptionsForDebitCard = new ProductOptions();
            Integer optionValueAtFirstIndex = optionCodeList.get(0);
            if (optionValueAtFirstIndex != null) {
                if (optionValueAtFirstIndex >= OPTION_VALUE_AT_FIRST_INDEX) {
                    setProductOptions(productOptionsForDebitCard, DEBIT_CARD_RISK_CODE, DEFAULT_DEBIT_CARD_OPTION_VALUE);
                } else if (optionValueAtFirstIndex < OPTION_VALUE_AT_FIRST_INDEX) {
                    if (OPTION_VALUE_ZERO.equalsIgnoreCase(optionValueAtFirstIndex.toString())) {
                        setProductOptions(productOptionsForDebitCard, DEBIT_CARD_RISK_CODE, DEFAULT_DEBIT_CARD_OPTION_VALUE);
                    } else {
                        setProductOptions(productOptionsForDebitCard, DEBIT_CARD_RISK_CODE, optionValueAtFirstIndex.toString());
                    }
                }
            }
        }
        return productOptionsForDebitCard;
    }
}
