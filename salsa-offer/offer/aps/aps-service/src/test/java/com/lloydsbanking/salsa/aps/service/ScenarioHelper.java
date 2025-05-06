package com.lloydsbanking.salsa.aps.service;


public interface ScenarioHelper {

    public void expectMaxEligibleFeatureAvailableWithMaxValueOne();

    public void expectMaxEligibleFeatureAvailableWithMaxValueTwo();

    public void expectProductFromExternalSystemProducts();

    public void expectDetailsInProductEligibilityRules();

    void clearUp();
}
