package com.lloydsbanking.salsa.opaloans;

public enum ProductOptionsCode {
    CURRENCY_CODE("CURRENCY_CODE"),
    MINIMUM_LOAN_AMOUNT("MINIMUM_LOAN_AMOUNT"),
    MAXIMUM_LOAN_AMOUNT("MAXIMUM_LOAN_AMOUNT"),
    MINIMUM_LOAN_TERM("MINIMUM_LOAN_TERM"),
    MAXIMUM_LOAN_TERM("MAXIMUM_LOAN_TERM"),
    INSURANCE_AVAILABLE_INDICATOR("INSURANCE_AVAILABLE_INDICATOR"),
    LETTER_CHARGES("LETTER_CHARGES"),
    DAYS_INTEREST_CHARGED("DAYS_INTEREST_CHARGED"),
    MAXIMUM_CHARGE_AMOUNT("MAXIMUM_CHARGE_AMOUNT"),
    ADMIN_CHARGES("ADMIN_CHARGES"),
    LOAN_TERM_EXEMPTION_START_DATE("LOAN_TERM_EXEMPTION_START_DATE"),
    LOAN_TERM_EXEMPTION_END_DATE("LOAN_TERM_EXEMPTION_END_DATE"),
    MINIMUM_LOAN_DEFER_TERM("MINIMUM_LOAN_DEFER_TERM"),
    MAXIMUM_LOAN_DEFER_TERM("MAXIMUM_LOAN_DEFER_TERM"),
    MINIMUM_LOAN_TERM_REPAYMENT_HOLIDAYS("MINIMUM_LOAN_TERM_REPAYMENT_HOLIDAYS"),
    MAXIMUM_LOAN_TERM_REPAYMENT_HOLIDAYS("MAXIMUM_LOAN_TERM_REPAYMENT_HOLIDAYS"),
    URL_TEXT_DISPLAY("URL_TEXT_DISPLAY"),
    URL("URL");

    private final String value;

    ProductOptionsCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static ProductOptionsCode getProductOptionsCode(String optionsCode) {
        for (ProductOptionsCode productOptionsCode : values()) {
            if (productOptionsCode.getValue().equals(optionsCode)) {
                return productOptionsCode;
            }
        }
        return null;
    }
}
