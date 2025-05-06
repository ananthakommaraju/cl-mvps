package com.lloydsbanking.salsa.opasaving.constants;

public enum SavingsProducts {
    EASY_SAVER("Easy Saver", "10"),
    ISA_SAVER_VARIABLE("ISA Saver Variable", "35"),
    FIXED_RATE_BOND_2YR("Fixed Rate Bond 2yr", "602"),
    ESAVINGS("eSavings", "12"),
    CASH_ISA("Cash ISA", "14"),
    TRACKER_BOND("Tracker Bond", "609");

    String productId;
    String productName;

    SavingsProducts(String productName, String productId){
        this.productId = productId;
        this.productName = productName;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public static SavingsProducts findProduct(String status) {
        String product = status.replace(" ", "_").toUpperCase();
        return valueOf(product);
    }
}
