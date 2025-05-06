package com.lloydsbanking.salsa.aps.service;


import com.lloydsbanking.salsa.downstream.prd.jdbc.ExternalSystemProductsDao;
import com.lloydsbanking.salsa.downstream.prd.jdbc.ProductEligibilityRulesDao;
import com.lloydsbanking.salsa.downstream.prd.jdbc.ProductFeatureDao;
import com.lloydsbanking.salsa.downstream.prd.model.ExternalSystemProducts;
import com.lloydsbanking.salsa.downstream.prd.model.ProductEligibilityRulesDto;
import com.lloydsbanking.salsa.downstream.prd.model.ProductFeature;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.Date;

public class RemoteMockScenarioHelper implements ScenarioHelper {

    @Autowired
    ProductFeatureDao productFeatureDao;
    @Autowired
    ExternalSystemProductsDao externalSystemProductsDao;
    @Autowired
    ProductEligibilityRulesDao productEligibilityRulesDao;

    @Override
    public void expectMaxEligibleFeatureAvailableWithMaxValueOne() {
        ProductFeature feature1 = new ProductFeature(10005L, "20001", "Max eligible cards", "MaxElgblCard", "Max eligible cards", "Advance Credit Card", "100", "1", new Date(), null);
        ProductFeature feature2 = new ProductFeature(100014L, "20002", "Max eligible cards", "MaxElgblCard", "Max eligible cards", "Advance Credit Card", "100", "1", new Date(), null);
        productFeatureDao.save(feature1);
        productFeatureDao.save(feature2);
    }

    @Override
    public void expectMaxEligibleFeatureAvailableWithMaxValueTwo() {
        ProductFeature feature = new ProductFeature(10005L, "20001", "Max eligible cards", "MaxElgblCard", "Max eligible cards", "Advance Credit Card", "100", "2", new Date(), null);
        productFeatureDao.save(feature);
    }

    @Override
    public void expectProductFromExternalSystemProducts() {
        ExternalSystemProducts externalSystemProduct1 = new ExternalSystemProducts(1002797l, "00013", 10009l, "120350546780");
        ExternalSystemProducts externalSystemProduct2 = new ExternalSystemProducts(1000023l, "00013", 10005l, "120350546780");
        ExternalSystemProducts externalSystemProduct3 = new ExternalSystemProducts(1002800l, "00013", 10015l, "190301525302");
        ExternalSystemProducts externalSystemProduct4 = new ExternalSystemProducts(1000027l, "00013", 10014l, "190301525302");

        externalSystemProductsDao.save(externalSystemProduct1);
        externalSystemProductsDao.save(externalSystemProduct2);
        externalSystemProductsDao.save(externalSystemProduct3);
        externalSystemProductsDao.save(externalSystemProduct4);
    }
    @Override
    public void expectDetailsInProductEligibilityRules() {
        Timestamp startTimestamp = new Timestamp(2014, 0, 1, 00, 00, 00, 00);
        Timestamp timestamp = new Timestamp(1099, 11, 31, 00, 00, 00, 00);
        ProductEligibilityRulesDto dto1 = new ProductEligibilityRulesDto(1004l, "CO_HOLD", 10005l, 10005l, startTimestamp, timestamp);
        ProductEligibilityRulesDto dto2 = new ProductEligibilityRulesDto(1005l, "CO_HOLD", 10006l, 10005l, startTimestamp, timestamp);
        ProductEligibilityRulesDto dto3 = new ProductEligibilityRulesDto(1006l, "CO_HOLD", 100014l, 10005l, startTimestamp, timestamp);

        productEligibilityRulesDao.save(dto1);
        productEligibilityRulesDao.save(dto2);
        productEligibilityRulesDao.save(dto3);
    }

    @Override
    public void clearUp() {
        productFeatureDao.deleteAll();
        externalSystemProductsDao.deleteAll();
        productEligibilityRulesDao.deleteAll();
    }

}
