package com.lloydsbanking.salsa.apacc.service.fulfil.convert;


import com.lloydsbanking.salsa.soap.fdi.f241.objects.AccountDataSegment;
import com.lloydsbanking.salsa.soap.fdi.f241.objects.CardDataSegment;
import lib_sim_bo.businessobjects.DirectDebit;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.ProductAttributes;
import lib_sim_bo.businessobjects.ProductOffer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

@Component
public class F241AccountDataSegmentFactory {
    private static final short DEFAULT_DD_NOM_AMT = 3;
    private static final int DEFAULT_SORT_CODE = 999999998;
    private static final String ACCOUNT_ORGANISATIONAL_ID = "ACC_ORG";
    private static final String ACCOUNT_LOGO_ID = "ACC_LOGO";
    private static final String RESIDENCE_ID = "RES_ID";
    private static final String STATE_OF_ISSUE_ID = "ISS_ID";
    private static final String CASH_PLAN_ID = "DEF_CH_PL";
    private static final String RETAIL_PLAN_ID = "DEF_RTL_PL";
    private static final String CREDIT_PLAN_ID = "DEF_PRM_PL";
    private static final String CAMPAIGN_ID = "MAC";
    private static final String OFFER_OVERRIDE_ID = "BT_OFF_1";
    private static final int DEFAULT_CAMPAIGN_CODE_LENGTH = 17;
    private static final int DEFAULT_AFFILIATE_ID_LENGTH = 3;

    public AccountDataSegment getAccountDataSegment(FinanceServiceArrangement financeServiceArrangement, CardDataSegment cardDataSegment) {
        AccountDataSegment accountDataSegment = new AccountDataSegment();

        if (isBooleanNotNullAndTrue(financeServiceArrangement.isIsJointParty())) {
            accountDataSegment.setAccountNumberExternalId(financeServiceArrangement.getAccountNumber());
        } else {
            if (isBooleanNotNullAndTrue(financeServiceArrangement.isIsDirectDebitRequired())) {
                setAccountDataSegmentForDebit(financeServiceArrangement, accountDataSegment);
            }
            ProductOffer productOffer = financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0);
            accountDataSegment.setCreditLimitAm(getCreditLimitAmount(productOffer));
            accountDataSegment.setSortCd(DEFAULT_SORT_CODE);
        }
        if (!financeServiceArrangement.getAssociatedProduct().getProductoffer().isEmpty() &&
                financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes() != null) {
            List<ProductAttributes> productAttributesList = financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes();
            setAccountIdDetails(cardDataSegment, accountDataSegment, productAttributesList, financeServiceArrangement.isIsJointParty());
        }
        accountDataSegment.setConvChequeOrderIn("N");
        accountDataSegment.setCardDeliveryMethodCd("D");
        String employeeCd = "0";
        if (isBooleanNotNullAndTrue(financeServiceArrangement.getPrimaryInvolvedParty().getIsPlayedBy().isIsStaffMember())) {
            employeeCd = "1";
        }
        accountDataSegment.setPersonEmployeeCd(employeeCd);
        accountDataSegment.setCreditCardSourceChannelCd("I");
        if (!CollectionUtils.isEmpty(financeServiceArrangement.getAffiliatedetails())) {
            if (financeServiceArrangement.getAffiliatedetails().get(0) != null && financeServiceArrangement.getAffiliatedetails().get(0).isIsCreditIntermediary() != null && financeServiceArrangement.getAffiliatedetails().get(0).isIsCreditIntermediary()) {
                accountDataSegment.setIntermediaryId(financeServiceArrangement.getAffiliatedetails().get(0).getAffiliateIdentifier());
                accountDataSegment.setPromotionalDataTx(setPromotionalData
                        (financeServiceArrangement.getAffiliatedetails().get(0).getAffiliateIdentifier(), financeServiceArrangement.getCampaignCode()));
            }
        }
        return accountDataSegment;
    }

    private void setAccountIdDetails(CardDataSegment cardDataSegment, AccountDataSegment accountDataSegment, List<ProductAttributes> productAttributesList, Boolean isJointParty) {
        for (ProductAttributes productAttributes : productAttributesList) {
            String attributeCode = productAttributes.getAttributeCode();
            String attributeValue = productAttributes.getAttributeValue();
            if (attributeCode != null) {
                if (ACCOUNT_ORGANISATIONAL_ID.equals(attributeCode)&& StringUtils.isNumeric(attributeValue)) {
                    accountDataSegment.setOrganisationalUnitId(Short.valueOf(attributeValue));
                } else if (ACCOUNT_LOGO_ID.equals(attributeCode) && StringUtils.isNumeric(attributeValue)) {
                    accountDataSegment.setAcctLogoId(Short.valueOf(attributeValue));
                } else if (OFFER_OVERRIDE_ID.equals(attributeCode)) {
                    cardDataSegment.setOfferOverrideId(attributeValue);
                } else if (!isJointParty) {
                    setAccountDetailIdsNotJointParty(accountDataSegment, attributeCode, attributeValue);
                }
            }
        }
    }

    private void setAccountDetailIdsNotJointParty(AccountDataSegment accountDataSegment, String attributeCode, String attributeValue) {
        if (RESIDENCE_ID.equals(attributeCode)) {
            accountDataSegment.setStateOfResidenceId(attributeValue);
        } else if (STATE_OF_ISSUE_ID.equals(attributeCode)) {
            accountDataSegment.setStateOfIssueId(attributeValue);
        } else if (CASH_PLAN_ID.equals(attributeCode) && StringUtils.isNumeric(attributeValue)) {
            accountDataSegment.setDfltCashCreditPlanId(Integer.valueOf(attributeValue));
        } else if (RETAIL_PLAN_ID.equals(attributeCode) && StringUtils.isNumeric(attributeValue)) {
            accountDataSegment.setDfltRetailCreditPlanId(Integer.valueOf(attributeValue));
        } else if (CREDIT_PLAN_ID.equals(attributeCode) && StringUtils.isNumeric(attributeValue)) {
            accountDataSegment.setCreditPlanId(Integer.valueOf(attributeValue));
        } else if (CAMPAIGN_ID.equals(attributeCode)) {
            accountDataSegment.setCampaignId(attributeValue);
        }
    }

    private String getCreditLimitAmount(ProductOffer productOffer) {
        String creditLimitAmount = "0";
        if (productOffer.getOfferAmount() != null && productOffer.getOfferAmount().getAmount() != null) {
            creditLimitAmount = String.valueOf(productOffer.getOfferAmount().getAmount().multiply(new BigDecimal("100")).toBigInteger());
        }
        return creditLimitAmount;
    }

    private void setAccountDataSegmentForDebit(FinanceServiceArrangement financeServiceArrangement, AccountDataSegment accountDataSegment) {
        DirectDebit directDebit = financeServiceArrangement.getDirectDebit();
        if (directDebit != null) {
            accountDataSegment.setDirectDebitAccSortCdExtId(Integer.valueOf(directDebit.getSortCode()));
            accountDataSegment.setDDBankAccountTypeCd("D");
            accountDataSegment.setAccountNo(directDebit.getAccountNumber());
            if ("FIXED".equalsIgnoreCase(directDebit.getDdType())) {
                accountDataSegment.setCreditCardDDPaymentCd((short) 2);
                accountDataSegment.setDDNomAmtFlagCd((short) 1);
                if (directDebit.getAmount() != null && directDebit.getAmount().getAmount() != null) {
                    accountDataSegment.setDDNomAm(String.valueOf(directDebit.getAmount().getAmount().multiply(new BigDecimal("100")).toBigInteger()));
                } else {
                    accountDataSegment.setDDNomAm("0");
                }
            } else if ("MIN".equalsIgnoreCase(directDebit.getDdType())) {
                accountDataSegment.setCreditCardDDPaymentCd((short) 1);
                accountDataSegment.setDDNomAmtFlagCd((short) 0);
                accountDataSegment.setDDNomAm("0");
            } else if ("FULL".equalsIgnoreCase(directDebit.getDdType())) {
                accountDataSegment.setCreditCardDDPaymentCd((short) 2);
                accountDataSegment.setDDNomAmtFlagCd(DEFAULT_DD_NOM_AMT);
                accountDataSegment.setDDNomAm("0");
            } else {
                accountDataSegment.setCreditCardDDPaymentCd((short) 0);
                accountDataSegment.setDDNomAmtFlagCd((short) 0);
                accountDataSegment.setDDNomAm("0");
            }
        }
    }

    private String setPromotionalData(String affiliateIdentifier, String campaignCode) {
        String affiliateIdPadded = (affiliateIdentifier == null) ? getPaddedString("", DEFAULT_AFFILIATE_ID_LENGTH, "0") : getPaddedString(affiliateIdentifier, DEFAULT_AFFILIATE_ID_LENGTH, "0");
        String campaignCodePadded = (campaignCode == null) ? getPaddedString("", DEFAULT_CAMPAIGN_CODE_LENGTH, "0") : getPaddedString(campaignCode, DEFAULT_CAMPAIGN_CODE_LENGTH, "0");
        return affiliateIdPadded.concat(campaignCodePadded);
    }

    private String getPaddedString(String name, int size, String padStr) {
        return org.apache.commons.lang3.StringUtils.leftPad(name, size, padStr);
    }

    private boolean isBooleanNotNullAndTrue(Boolean aBoolean) {
        return (aBoolean != null && aBoolean);
    }
}
