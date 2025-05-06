package com.lloydsbanking.salsa.apacc.service.fulfil.convert;

import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.AccountDataSegmentV1;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.CardDataSegment;
import lib_sim_bo.businessobjects.DirectDebit;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.ProductAttributes;
import lib_sim_bo.businessobjects.ProductOffer;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class F241V1AccountDataSegmentFactory {

    private static final short DEFAULT_DD_NOM_AMT = 3;
    private static final int DEFAULT_SORT_CODE = 999999998;
    private static final int DEFAULT_CAMPAIGN_CODE_LENGTH = 17;
    private static final int DEFAULT_AFFILIATE_ID_LENGTH = 3;
    private static final int DATE_OF_MONTH_25 = 25;
    private static final int MONTH_DECEMBER = 11;
    private static final String DEFAULT_FORMAT_FOR_DATE = "yyyy/MM/dd";
    private static final String DD_TYPE_FIXED = "FIXED";
    private static final String DD_TYPE_FULL = "FULL";
    private static final String DD_TYPE_MIN = "MIN";
    private static final String ACCOUNT_ORGANISATIONAL_ID = "ACC_ORG";
    private static final String ACCOUNT_LOGO_ID = "ACC_LOGO";
    private static final String RESIDENCE_ID = "RES_ID";
    private static final String STATE_OF_ISSUE_ID = "ISS_ID";
    private static final String CASH_PLAN_ID = "DEF_CH_PL";
    private static final String RETAIL_PLAN_ID = "DEF_RTL_PL";
    private static final String CREDIT_PLAN_ID = "DEF_PRM_PL";
    private static final String CAMPAIGN_ID = "MAC";
    private static final String OFFER_OVERRIDE_ID = "BT_OFF_1";

    public AccountDataSegmentV1 getAccountDataSegment(FinanceServiceArrangement financeServiceArrangement, CardDataSegment cardDataSegment) {
        AccountDataSegmentV1 accountDataSegment = new AccountDataSegmentV1();
        if (isBooleanNotNullAndTrue(financeServiceArrangement.isIsDirectDebitRequired())) {
            setAccountDataSegmentForDebit(financeServiceArrangement, accountDataSegment);
        }
        ProductOffer productOffer = financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0);
        accountDataSegment.setCreditLimitAm(getCreditLimitAmount(productOffer));
        accountDataSegment.setSortCd(DEFAULT_SORT_CODE);
        if (financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes() != null &&
                !financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().isEmpty()) {
            List<ProductAttributes> productAttributesList = financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes();
            setAccountIdDetails(cardDataSegment, accountDataSegment, productAttributesList);
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
            if (financeServiceArrangement.getAffiliatedetails().get(0) != null && financeServiceArrangement.getAffiliatedetails().get(0).isIsCreditIntermediary()) {
                accountDataSegment.setIntermediaryId(financeServiceArrangement.getAffiliatedetails().get(0).getAffiliateIdentifier());
            }
            accountDataSegment.setPromotionalDataTx(setPromotionalData
                    (financeServiceArrangement.getAffiliatedetails().get(0).getAffiliateIdentifier(), financeServiceArrangement.getCampaignCode()));
        } else {
            accountDataSegment.setPromotionalDataTx(setPromotionalData
                    ("", financeServiceArrangement.getCampaignCode()));
        }
        return accountDataSegment;
    }

    private void setAccountIdDetails(CardDataSegment cardDataSegment, AccountDataSegmentV1 accountDataSegment, List<ProductAttributes> productAttributesList) {
        Map<String, String> codeValue = new HashMap<>();
        for (ProductAttributes productAttributes : productAttributesList) {
            if (productAttributes.getAttributeCode() != null) {
                codeValue.put(productAttributes.getAttributeCode(), productAttributes.getAttributeValue());
            }
        }
        accountDataSegment.setOrganisationalUnitId(codeValue.get(ACCOUNT_ORGANISATIONAL_ID) != null ? Short.valueOf(codeValue.get(ACCOUNT_ORGANISATIONAL_ID)) : null);
        accountDataSegment.setAcctLogoId(codeValue.get(ACCOUNT_LOGO_ID) != null ? Short.valueOf(codeValue.get(ACCOUNT_LOGO_ID)) : null);
        accountDataSegment.setStateOfResidenceId(codeValue.get(RESIDENCE_ID));
        accountDataSegment.setStateOfIssueId(codeValue.get(STATE_OF_ISSUE_ID));
        accountDataSegment.setDfltCashCreditPlanId(codeValue.get(CASH_PLAN_ID) != null ? Integer.valueOf(codeValue.get(CASH_PLAN_ID)) : null);
        accountDataSegment.setDfltRetailCreditPlanId(codeValue.get(RETAIL_PLAN_ID) != null ? Integer.valueOf(codeValue.get(RETAIL_PLAN_ID)) : null);
        accountDataSegment.setCreditPlanId(codeValue.get(CREDIT_PLAN_ID) != null ? Integer.valueOf(codeValue.get(CREDIT_PLAN_ID)) : null);
        accountDataSegment.setCampaignId(codeValue.get(CAMPAIGN_ID));
        cardDataSegment.setOfferOverrideId(codeValue.get(OFFER_OVERRIDE_ID));
        setSrvcChgDetails(accountDataSegment, codeValue);
        setLoyaltyCd(accountDataSegment, codeValue);
    }

    private void setSrvcChgDetails(AccountDataSegmentV1 accountDataSegment, Map<String, String> codeValue) {
        if (!StringUtils.isEmpty(codeValue.get("SrvcChrgFeeOvrd"))) {
            accountDataSegment.setCreditCardSvcChgFeeTableId(codeValue.get("SrvcChrgFeeOvrd"));
            accountDataSegment.setCreditCardSvcChgFeeStartDt("00000000");
        }
        String serviceChargeEndDt = codeValue.get("SrvcChrgfeeOvrdEndDtPrd");
        if (!StringUtils.isEmpty(serviceChargeEndDt)) {
            if ("999".equals(serviceChargeEndDt)) {
                accountDataSegment.setCreditCardSvcChgFeeEndDt("29991231");
            } else {
                accountDataSegment.setCreditCardSvcChgFeeEndDt(getNextBusinessDate(Integer.valueOf(serviceChargeEndDt)));
            }
        }
    }

    private void setLoyaltyCd(AccountDataSegmentV1 accountDataSegment, Map<String, String> codeValue) {
        accountDataSegment.setCreditCardActTblOverrideId(codeValue.get("AcntOvrd"));
        if (!StringUtils.isEmpty(codeValue.get("LyltCd"))) {
            accountDataSegment.setCreditCardLoyaltyCd("00000000000".concat(codeValue.get("LyltCd")));
        }
        if (!StringUtils.isEmpty(codeValue.get("LyltPromCd"))) {
            accountDataSegment.setCreditCardLoyaltyPromotionalCd("00000000000".concat(codeValue.get("LyltPromCd")));
        }
    }

    private void setAccountDataSegmentForDebit(FinanceServiceArrangement financeServiceArrangement, AccountDataSegmentV1 accountDataSegment) {
        DirectDebit directDebit = financeServiceArrangement.getDirectDebit();
        if (directDebit != null) {
            accountDataSegment.setDirectDebitAccSortCdExtId(Integer.valueOf(directDebit.getSortCode()));
            accountDataSegment.setDDBankAccountTypeCd("D");
            accountDataSegment.setAccountNo(directDebit.getAccountNumber());
            if (DD_TYPE_FIXED.equalsIgnoreCase(directDebit.getDdType())) {
                accountDataSegment.setCreditCardDDPaymentCd((short) 2);
                accountDataSegment.setDDNomAmtFlagCd((short) 1);
                if (directDebit.getAmount() != null && directDebit.getAmount().getAmount() != null) {
                    accountDataSegment.setDDNomAm(String.valueOf(directDebit.getAmount().getAmount().multiply(new BigDecimal("100")).toBigInteger()));
                } else {
                    accountDataSegment.setDDNomAm("0");
                }
            } else if (DD_TYPE_MIN.equalsIgnoreCase(directDebit.getDdType())) {
                accountDataSegment.setCreditCardDDPaymentCd((short) 1);
                accountDataSegment.setDDNomAmtFlagCd((short) 0);
                accountDataSegment.setDDNomAm("0");
            } else if (DD_TYPE_FULL.equalsIgnoreCase(directDebit.getDdType())) {
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

    private String getCreditLimitAmount(ProductOffer productOffer) {
        String creditLimitAmount = "0";
        if (productOffer.getOfferAmount() != null && productOffer.getOfferAmount().getAmount() != null) {
            creditLimitAmount = String.valueOf(productOffer.getOfferAmount().getAmount().multiply(new BigDecimal("100")).toBigInteger());
        }
        return creditLimitAmount;
    }

    private String setPromotionalData(String affiliateIdentifier, String campaignCode) {
        String affiliateIdPadded = (affiliateIdentifier == null) ? getPaddedString("", DEFAULT_AFFILIATE_ID_LENGTH, "0") : getPaddedString(affiliateIdentifier, DEFAULT_AFFILIATE_ID_LENGTH, "0");
        String campaignCodePadded = (campaignCode == null) ? getPaddedString("", DEFAULT_CAMPAIGN_CODE_LENGTH, "0") : getPaddedString(campaignCode, DEFAULT_CAMPAIGN_CODE_LENGTH, "0");
        return affiliateIdPadded.concat(campaignCodePadded);
    }

    private String getPaddedString(String name, int size, String padStr) {
        return org.apache.commons.lang3.StringUtils.leftPad(name, size, padStr);
    }

    private String getNextBusinessDate(int days) {
        String nextDate;
        DateFactory dateFactory = new DateFactory();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateFactory.addDays(new Date(), days));
        if (cal.get(Calendar.DATE) == DATE_OF_MONTH_25 && cal.get(Calendar.MONTH) == MONTH_DECEMBER) {
            cal.add(Calendar.DATE, 1);
        }
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY) {
            cal.add(Calendar.DATE, 2);
        } else if (dayOfWeek == Calendar.SUNDAY) {
            cal.add(Calendar.DATE, 1);
        }
        nextDate = FastDateFormat.getInstance(DEFAULT_FORMAT_FOR_DATE).format(cal.getTime());
        return nextDate.replace("/", "");
    }

    private boolean isBooleanNotNullAndTrue(Boolean aBoolean) {
        return (aBoolean != null && aBoolean);
    }
}
