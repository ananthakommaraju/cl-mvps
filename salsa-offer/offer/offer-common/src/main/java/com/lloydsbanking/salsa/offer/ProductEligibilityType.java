package com.lloydsbanking.salsa.offer;


public enum ProductEligibilityType {

    NEW("NEW", "10001"),
    CO_HOLD("CO_HOLD", "10001"),
    TRADE("TRADE", "10002"),
    INELIGIBLE("INELIGIBLE", "INELIGIBLE");

    private final String key;
    private final String value;

    ProductEligibilityType(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

    public static String getApplicationType(String prodEligibilityType) {
        for (ProductEligibilityType productEligibilityType : values()) {
            if (productEligibilityType.getKey().equals(prodEligibilityType)) {
                return productEligibilityType.getValue();
            }
        }
        return INELIGIBLE.getValue();
    }
}
