package com.lloydsbanking.salsa.activate.communication.convert;

import com.lloydsbanking.salsa.activate.constants.CommunicationKeysEnum;
import com.lloydsbanking.salsa.activate.constants.EmailTemplateEnum;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.brand.Brand;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import lib_sim_bo.businessobjects.*;
import org.apache.cxf.common.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ProductRelatedTokenFactory {
    private static final String APP_PARAMETER_CAR_FINANCE_EXPIRY_DATE = "APP_PARAMETER_CAR_FINANCE_EXPIRY_DATE";
    private static final int DATE_START_INDEX = 6;
    private static final int MONTH_START_INDEX = 4;
    private static final int YEAR_START_INDEX = 0;
    private static final int LENGTH_OF_XX = 2;
    private static final int LENGTH_OF_XXXX = 4;
    private static final int ACCOUNT_NUMBER_START_INDEX = 4;
    private static final int ACCOUNT_NUMBER_END_INDEX = 8;
    private static final String ACCOUNT_NUMBER_PREFIX = "XXXX";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_FAIL = "FAIL";
    private static final String POUND = "\u00A3";

    @Autowired
    PostCodeFactory postCodeFactory;
    @Autowired
    InformationContentFactory informationContentFactory;
    @Autowired
    LookUpValueRetriever lookUpValueRetriever;

    public List<InformationContent> getProductRelatedToken(ProductArrangement productArrangenment) {
        List<InformationContent> informationContentList = new ArrayList<>();
        String maskedPostCode = postCodeFactory.getMaskedPostcode(productArrangenment);
        String fundingDays = productArrangenment.getFundingDays() != null ? String.valueOf(productArrangenment.getFundingDays()) : "0";
        informationContentList.addAll(informationContentFactory.getInformationContentIfNotNull(CommunicationKeysEnum.PRODUCT_SA_FUNDING_DAYS.getKey(), fundingDays));
        if (ArrangementType.LOAN_REFERRAL_AUTOMATION.getValue().equalsIgnoreCase(productArrangenment.getArrangementType()) || ArrangementType.FINANCE_ACCOUNT.getValue().equalsIgnoreCase(productArrangenment.getArrangementType())) {
            informationContentList.addAll(informationContentFactory.getInformationContentIfNotNull(CommunicationKeysEnum.PRODUCT_MASKED_POSTCODE.getKey(), maskedPostCode));
        }
        if (productArrangenment.getAssociatedProduct() != null) {
            informationContentList.add(getMaskedAccountNumberContent(productArrangenment.getAccountNumber(), maskedPostCode));
            if (ArrangementType.CREDITCARD.getValue().equalsIgnoreCase(productArrangenment.getArrangementType())) {
                informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PRODUCT_CC_NAME.getKey(), productArrangenment.getAssociatedProduct().getProductName(), 0));
                informationContentList.addAll(informationContentFactory.getInformationContentIfNotNull(CommunicationKeysEnum.PRODUCT_MASKED_POSTCODE.getKey(), maskedPostCode));
            } else if (ArrangementType.CURRENT_ACCOUNT.getValue().equalsIgnoreCase(productArrangenment.getArrangementType())) {
                informationContentList.addAll(getInformationContentForCurrentAccount(productArrangenment, maskedPostCode));
            } else if (ArrangementType.LOAN.getValue().equals(productArrangenment.getArrangementType())) {
                informationContentList.addAll(getInformationContentForLoanAccount(productArrangenment, maskedPostCode));
            } else if (ArrangementType.FINANCE_ACCOUNT.getValue().equals(productArrangenment.getArrangementType())) {
                informationContentList.addAll(informationContentFactory.getInformationContentIfNotNull(CommunicationKeysEnum.FA_EXPIRY_DATE.getKey(), getExpiryDate(productArrangenment.getConditions())));
            } else {
                informationContentList.addAll(getInformationContentIfNoMatchingArrangementTypeFound(productArrangenment, maskedPostCode));
            }
        }
        return informationContentList;
    }

    public List<InformationContent> getDataTokensForBTFulfilment(FinanceServiceArrangement financeServiceArrangement) {
        List<InformationContent> informationContentList = new ArrayList<>();
        List<ReferenceDataLookUp> lookUpList = lookUpValueRetriever.getLookUpValues(Arrays.asList(EmailTemplateEnum.BT_FULFILLED_EMAIL.getTemplate()), Brand.LLOYDS.asString());
        if (!lookUpList.isEmpty()) {
            informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PRODUCT_CC_BT_CONTENT_ID.getKey(), lookUpList.get(0).getLookupValueDesc(), 0));
        }
        String maskedPostCode = postCodeFactory.getMaskedPostcode(financeServiceArrangement);
        informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PRODUCT_MASKED_POSTCODE.getKey(), maskedPostCode, 0));
        int count = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (BalanceTransfer balanceTransfer : financeServiceArrangement.getBalanceTransfer()) {
            count++;
            informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PRODUCT_CC_BT_CARD_NUMBER.getKey() + "_" + count, balanceTransfer.getMaskedCreditCardNumber(), 0));
            String status = STATUS_SUCCESS.equalsIgnoreCase(balanceTransfer.getStatus()) ? STATUS_SUCCESS : STATUS_FAIL;
            informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PRODUCT_CC_BT_STATUS.getKey() + "_" + count, status, 0));
            informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PRODUCT_CC_BT_AMOUNT.getKey() + "_" + count, String.valueOf(balanceTransfer.getAmount().getAmount()), 0));
            totalAmount = totalAmount.add(balanceTransfer.getAmount().getAmount());
        }

        if (totalAmount.compareTo(BigDecimal.ZERO) != 0) {
            informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PRODUCT_CC_TOTAL_BT_AMOUNT.getKey(), POUND + " " + String.valueOf(totalAmount), 0));
        }

        if (financeServiceArrangement.getAssociatedProduct() != null) {
            informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PRODUCT_CC_NAME.getKey(), financeServiceArrangement.getAssociatedProduct().getProductName(), 0));
        }
        if (financeServiceArrangement.getPrimaryInvolvedParty() != null && financeServiceArrangement.getPrimaryInvolvedParty().getIsPlayedBy() != null && !financeServiceArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().isEmpty()) {
            IndividualName individualName = financeServiceArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().get(0);
            informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.CUSTOMER_LASTNAME.getKey(), individualName.getLastName(), 0));
            informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.CUSTOMER_TITLE.getKey(), individualName.getPrefixTitle(), 0));
        }
        return informationContentList;
    }

    private List<InformationContent> getInformationContentIfNoMatchingArrangementTypeFound(ProductArrangement productArrangenment, String maskedPostCode) {
        List<InformationContent> informationContentList = new ArrayList<>();
        informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PRODUCT_SA_NAME.getKey(), productArrangenment.getAssociatedProduct().getProductName(), 0));
        informationContentList.addAll(getSAAccountNumber(productArrangenment));
        informationContentList.addAll(informationContentFactory.getInformationContentIfNotNull(productArrangenment.getGuardianDetails() != null ? CommunicationKeysEnum.PARENT_PRODUCT_MASKED_POSTCODE.getKey() : CommunicationKeysEnum.PRODUCT_MASKED_POSTCODE.getKey(), maskedPostCode));
        if (productArrangenment.getFinancialInstitution() != null && !CollectionUtils.isEmpty(productArrangenment.getFinancialInstitution().getHasOrganisationUnits())) {
            informationContentList.addAll(informationContentFactory.getInformationContentIfNotNull(CommunicationKeysEnum.PRODUCT_SA_SORT_CODE.getKey(), productArrangenment.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode()));
        }
        return informationContentList;
    }

    private List<InformationContent> getInformationContentForLoanAccount(ProductArrangement productArrangenment, String maskedPostCode) {
        List<InformationContent> informationContentList = new ArrayList<>();
        informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PRODUCT_LOAN_NAME.getKey(), productArrangenment.getAssociatedProduct().getProductName(), 0));
        informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PRODUCT_LOAN_REMINDER_DATE.getKey(), productArrangenment.getCommunicationOption(), 0));
        informationContentList.addAll(informationContentFactory.getInformationContentIfNotNull(CommunicationKeysEnum.PRODUCT_MASKED_POSTCODE.getKey(), maskedPostCode));
        return informationContentList;
    }

    private String getExpiryDate(List<RuleCondition> ruleConditionList) {
        for (RuleCondition ruleCondition : ruleConditionList) {
            if (APP_PARAMETER_CAR_FINANCE_EXPIRY_DATE.equalsIgnoreCase(ruleCondition.getName())) {
                return ruleCondition.getResult().substring(DATE_START_INDEX, DATE_START_INDEX + LENGTH_OF_XX) + "/" + ruleCondition.getResult().substring(MONTH_START_INDEX, MONTH_START_INDEX + LENGTH_OF_XX) + "/" + ruleCondition.getResult().substring(YEAR_START_INDEX, YEAR_START_INDEX + LENGTH_OF_XXXX);
            }
        }
        return null;
    }

    private List<InformationContent> getSAAccountNumber(ProductArrangement productArrangenment) {
        List<InformationContent> informationContentList = new ArrayList<>();
        if (productArrangenment.getAccountNumber() != null) {
            informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PRODUCT_SA_ACCOUNT_NUMBER.getKey(), productArrangenment.getAccountNumber(), 0));
            informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PRODUCT_SA_MASKED_ACCOUNT_NUMBER.getKey(),
                    getMaskedAccountNumber(productArrangenment.getAccountNumber()), 0));
        }
        return informationContentList;
    }

    private List<InformationContent> getCAAccountNumber(ProductArrangement productArrangement) {
        List<InformationContent> informationContentList = new ArrayList<>();
        if (productArrangement.getAccountNumber() != null) {
            informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PRODUCT_PCA_ACCOUNT_NUMBER.getKey(), productArrangement.getAccountNumber(), 0));
        }
        return informationContentList;
    }

    private InformationContent getMaskedAccountNumberContent(String accountNumber, String maskedPostCode) {
        InformationContent informationContent;
        if (accountNumber != null) {
            informationContent = informationContentFactory.getInformationContent(CommunicationKeysEnum.PRODUCT_MASKED_ACCOUNT_NUMBER.getKey(), getMaskedAccountNumber(accountNumber), 0);
        } else {
            informationContent = informationContentFactory.getInformationContent(CommunicationKeysEnum.PRODUCT_MASKED_ACCOUNT_NUMBER.getKey(), maskedPostCode, 0);
        }
        return informationContent;
    }

    private List<InformationContent> getInformationContentForCurrentAccount(ProductArrangement productArrangement, String maskedPostCode) {
        List<InformationContent> informationContentList = new ArrayList<>();
        informationContentList.addAll(informationContentFactory.getBenefitMessagesInformationContent(productArrangement.getConditions()));
        informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PRODUCT_PCA_NAME.getKey(), productArrangement.getAssociatedProduct().getProductName(), 0));
        informationContentList.addAll(getCAAccountNumber(productArrangement));
        informationContentList.addAll(informationContentFactory.getInformationContentIfNotNull(CommunicationKeysEnum.PRODUCT_MASKED_POSTCODE.getKey(), maskedPostCode));
        if (postCodeFactory.getPostCode(productArrangement) != null) {
            informationContentList.addAll(informationContentFactory.getInformationContentIfNotNull(CommunicationKeysEnum.PRODUCT_POSTCODE.getKey(), maskedPostCode));
        }
        if (productArrangement.getFinancialInstitution() != null && !productArrangement.getFinancialInstitution().getHasOrganisationUnits().isEmpty()) {
            informationContentList.addAll(informationContentFactory.getInformationContentIfNotNull(CommunicationKeysEnum.PRODUCT_PCA_SORT_CODE.getKey(), productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode()));
        }
        informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PRODUCT_PCA_APPREFNUM.getKey(), productArrangement.getArrangementId(), 0));
        if (productArrangement instanceof DepositArrangement && ((DepositArrangement) productArrangement).getOverdraftDetails() != null) {
            DepositArrangement depositArrangement = (DepositArrangement) productArrangement;
            informationContentList.addAll(informationContentFactory.getFeeOverdraftRateInformationContent(depositArrangement.getOverdraftDetails().getInterestRates()));
            if (depositArrangement.getOverdraftDetails().getAmount() != null && depositArrangement.getOverdraftDetails().getAmount().getAmount() != null) {
                informationContentList.add(informationContentFactory.getInformationContent(CommunicationKeysEnum.PRODUCT_PCA_OD_AMOUNT.getKey(), String.valueOf(depositArrangement.getOverdraftDetails().getAmount().getAmount()), 0));
            }
        }
        return informationContentList;
    }

    public String getMaskedAccountNumber(String accountNumber) {
        return ACCOUNT_NUMBER_PREFIX.concat(accountNumber.substring(ACCOUNT_NUMBER_START_INDEX, ACCOUNT_NUMBER_END_INDEX));
    }
}
