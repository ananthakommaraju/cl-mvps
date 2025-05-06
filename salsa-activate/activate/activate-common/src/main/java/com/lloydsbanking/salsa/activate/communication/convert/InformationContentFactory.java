package com.lloydsbanking.salsa.activate.communication.convert;

import com.lloydsbanking.salsa.activate.constants.CommunicationKeysEnum;
import com.lloydsbanking.salsa.date.DateFactory;
import lib_sim_bo.businessobjects.InformationContent;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_bo.businessobjects.Rates;
import lib_sim_bo.businessobjects.RuleCondition;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

@Component
public class InformationContentFactory {

    private static final String[] VALID_RULE_CONDITIONS = {"WWTI", "HECP", "MOPI", "AAUR", "SECI"};
    private static final String RULE_CONDITION_ALERT_MESSAGES = "ALERT_MSGES";
    private static final String RATE_TYPE_FEE_OVERDRAFT = "INT_FREE_OVERDRAFT";

    public boolean containsContentKey(String key, List<InformationContent> informationContentList) {
        boolean flag = false;
        for (InformationContent informationContent : informationContentList) {
            if (key.equals(informationContent.getKey())) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public InformationContent getInformationContent(String key, String value, Integer order) {
        InformationContent informationContent = new InformationContent();
        informationContent.setKey(key);
        informationContent.setValue(value);
        if (order != null) {
            informationContent.setOrder(order);
        }
        return informationContent;
    }

    public List<InformationContent> getInformationContentIfNotNull(String key, String value) {
        List<InformationContent> informationContentList = new ArrayList<>();
        if (value != null) {
            informationContentList.add(getInformationContent(key, value, 0));
        }
        return informationContentList;
    }

    public void setInformationContent(String key, String value, List<InformationContent> informationContentList) {
        if (key != null) {
            informationContentList.add(getInformationContent(key, value != null ? value : "", null));
        }
    }

    private List<InformationContent> getRuleConditionInformationContent(List<RuleCondition> ruleConditionList) {
        List<InformationContent> informationContentList = new ArrayList<>();
        for (RuleCondition ruleCondition : ruleConditionList) {
            String ruleConditionName = ruleCondition.getName();
            if (ArrayUtils.contains(VALID_RULE_CONDITIONS, ruleConditionName)) {
                informationContentList.add(getInformationContent(CommunicationKeysEnum.PRODUCT_PCA_COMMON.getKey() + ruleConditionName, CommunicationKeysEnum.PRODUCT_PCA_COMMON.getKey() + ruleConditionName, 0));
            }
        }
        return informationContentList;
    }

    public List<InformationContent> getBenefitMessagesInformationContent(List<RuleCondition> ruleConditionList) {
        List<InformationContent> informationContentList = new ArrayList<>();
        String benefitMessages = null;
        for (RuleCondition ruleCondition : ruleConditionList) {
            if (RULE_CONDITION_ALERT_MESSAGES.equalsIgnoreCase(ruleCondition.getName())) {
                benefitMessages = ruleCondition.getResult();
                break;
            }
        }
        if (benefitMessages != null) {
            informationContentList.addAll(getRuleConditionInformationContent(ruleConditionList));
            StringTokenizer st = new StringTokenizer(benefitMessages, ",");
            while (st.hasMoreElements()) {
                String token = (String) st.nextElement();
                informationContentList.add(getInformationContent(CommunicationKeysEnum.PRODUCT_PCA_COMMON.getKey() + token, CommunicationKeysEnum.PRODUCT_PCA_COMMON.getKey() + token, 0));
            }
        }
        return informationContentList;
    }


    public List<InformationContent> getFeeOverdraftRateInformationContent(List<Rates> rateList) {
        List<InformationContent> informationContentList = new ArrayList<>();
        for (Rates rates : rateList) {
            if (RATE_TYPE_FEE_OVERDRAFT.equalsIgnoreCase(rates.getType())) {
                informationContentList.add(getInformationContent(CommunicationKeysEnum.PRODUCT_PCA_OD_INTEREST_FREE_AMOUNT.getKey(), String.valueOf(rates.getValue()), 0));
                break;
            }
        }
        return informationContentList;
    }

    public InformationContent getNoOfDaysInformationContent(ProductArrangement productArrangement, int configuredDays) {

        InformationContent informationContent = new InformationContent();
        Date modifiedDate = new DateFactory().convertXMLGregorianToDateFormat(productArrangement.getLastModifiedDate());
        Date currentDate = new Date();
        long numOfDays = new DateFactory().differenceInDays(modifiedDate, currentDate);
        informationContent.setKey(CommunicationKeysEnum.NUMBER_OF_DAYS.getKey());
        informationContent.setValue(String.valueOf(configuredDays - numOfDays));

        return informationContent;
    }


}
