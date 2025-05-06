package com.lloydsbanking.salsa.apacc.service.fulfil.convert;


import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.apacc.service.fulfil.rules.DSTFieldKeys;
import lib_sbo_cardacquire.businessojects.Field;
import lib_sim_bo.businessobjects.*;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class DSTFieldFactory {

    private static final String PRODUCT_ATTRIBUTE_ISS_ID = "ISS_ID";
    private static final String EXTERNAL_SYSTEM_ASM = "00107";
    private static final String STATUS_YES = "Y";
    private static final String STATUS_NO = "N";
    private static final String COUNTRY_UK = "UK";
    private static final int CREDIT_CARD_MAX_LENGTH = 17;
    private static final int COUNTRY_MAX_LENGTH = 18;
    private static final int ADDRESS_LINE_MAX_LENGTH = 40;
    private static final int BALANCE_TRANSFER_MAX = 4;
    private static final int INDEX_ZERO_FOR_ADDRESS_LINE = 0;
    private static final int INDEX_ONE_FOR_DISTRICT = 1;
    private static final int INDEX_TWO_FOR_POST_TOWN = 2;
    private static final int INDEX_THREE_FOR_COUNTRY = 3;
    private static final int INDEX_FOUR_FOR_PREVIOUS_ADDRESS_STATUS_CODE = 4;
    private static final int INDEX_FIVE_FOR_POST_CODE = 5;
    private static final int INDEX_SIX_FOR_DURATION_OF_STAY = 6;
    @Autowired
    DSTFieldHelper dstFieldHelper;
    @Autowired
    DSTIndividualFactory dstIndividualFactory;

    public List<Field> getFieldList(FinanceServiceArrangement financeServiceArrangement) {
        List<Field> fieldList = new ArrayList<>();
        fieldList.add(dstFieldHelper.getField(DSTFieldKeys.PCRY.getKey(), COUNTRY_UK));
        fieldList.add(dstFieldHelper.getField(DSTFieldKeys.PRES.getKey(), COUNTRY_UK));
        fieldList.add(dstFieldHelper.getFieldForStringValue(DSTFieldKeys.APNO.getKey(), financeServiceArrangement.getArrangementId()));
        fieldList.add(dstFieldHelper.getFieldForBooleanValue(DSTFieldKeys.SPRO.getKey(), financeServiceArrangement.isPaymentProtectionIndicator()));
        if (financeServiceArrangement.getCreditCardNumber() != null && financeServiceArrangement.getCreditCardNumber().length() < CREDIT_CARD_MAX_LENGTH) {
            fieldList.add(dstFieldHelper.getField(DSTFieldKeys.NCNO.getKey(), financeServiceArrangement.getCreditCardNumber()));
        }
        fieldList.addAll(getAgreementRelatedFieldList(financeServiceArrangement));
        fieldList.add(dstFieldHelper.getFieldForBooleanValue(DSTFieldKeys.CUS2.getKey(), financeServiceArrangement.isIsJointParty()));
        if (!financeServiceArrangement.getJointParties().isEmpty()) {
            Individual individual = financeServiceArrangement.getJointParties().get(0).getIsPlayedBy();
            fieldList.addAll(dstIndividualFactory.getIndividualPrimaryDetailsFieldList(individual, Arrays.asList(DSTFieldKeys.ATLE.getKey(), DSTFieldKeys.AFNM.getKey(),
                    DSTFieldKeys.AMNM.getKey(), DSTFieldKeys.ASRN.getKey(), DSTFieldKeys.SDOB.getKey(), DSTFieldKeys.SEXA.getKey(), DSTFieldKeys.SNAT.getKey())));
        }
        if (!financeServiceArrangement.getAffiliatedetails().isEmpty()) {
            fieldList.add(dstFieldHelper.getFieldForStringValue(DSTFieldKeys.INCD.getKey(), financeServiceArrangement.getAffiliatedetails().get(0).getAffiliateIdentifier()));
        }
        if (financeServiceArrangement.getDirectDebit() != null) {
            fieldList.add(dstFieldHelper.getFieldForStringValue(DSTFieldKeys.DDSC.getKey(), financeServiceArrangement.getDirectDebit().getSortCode()));
            fieldList.add(dstFieldHelper.getFieldForStringValue(DSTFieldKeys.DDAC.getKey(), financeServiceArrangement.getDirectDebit().getAccountNumber()));
            if (financeServiceArrangement.getDirectDebit().getAmount() != null && financeServiceArrangement.getDirectDebit().getAmount().getAmount() != null) {
                fieldList.add(dstFieldHelper.getFieldForStringValue(DSTFieldKeys.DDSP.getKey(), String.valueOf(financeServiceArrangement.getDirectDebit().getAmount().getAmount())));
                fieldList.add(dstFieldHelper.getField(DSTFieldKeys.DDMP.getKey(), STATUS_YES));
            }
        }
        fieldList.addAll(getBalanceTransferFieldList(financeServiceArrangement.getBalanceTransfer()));
        fieldList.addAll(getAssociatedProductFieldList(financeServiceArrangement.getAssociatedProduct()));
        fieldList.add(dstFieldHelper.getFieldForStringValue(DSTFieldKeys.AFFG.getKey(), financeServiceArrangement.getPrimaryInvolvedParty().getCustomerSegment()));
        fieldList.add(dstFieldHelper.getFieldForStringValue(DSTFieldKeys.EMAL.getKey(), financeServiceArrangement.getPrimaryInvolvedParty().getEmailAddress()));
        fieldList.add(dstFieldHelper.getFieldForStringValue(DSTFieldKeys.APSC.getKey(), financeServiceArrangement.getPrimaryInvolvedParty().getExistingSortCode()));
        fieldList.add(dstFieldHelper.getFieldForStringValue(DSTFieldKeys.APAC.getKey(), financeServiceArrangement.getPrimaryInvolvedParty().getExistingAccountNumber()));
        fieldList.add(dstFieldHelper.getFieldForStringValue(DSTFieldKeys.BKYM.getKey(), financeServiceArrangement.getPrimaryInvolvedParty().getExistingAccountDuration()));
        fieldList.addAll(getPostalAddressFieldList(financeServiceArrangement.getPrimaryInvolvedParty().getPostalAddress()));
        fieldList.add(dstFieldHelper.getFieldForBooleanValue(DSTFieldKeys.OCRD.getKey(), financeServiceArrangement.getPrimaryInvolvedParty().isHasExistingCreditCard()));
        fieldList.addAll(getTelephoneFieldList(financeServiceArrangement.getPrimaryInvolvedParty().getTelephoneNumber()));
        Individual isPlayedBy = financeServiceArrangement.getPrimaryInvolvedParty().getIsPlayedBy();
        fieldList.addAll(dstIndividualFactory.getIndividualPrimaryDetailsFieldList(isPlayedBy, Arrays.asList(DSTFieldKeys.PTLE.getKey(), DSTFieldKeys.PFNM.getKey(),
                DSTFieldKeys.PMNM.getKey(), DSTFieldKeys.PSRN.getKey(), DSTFieldKeys.PDOB.getKey(), DSTFieldKeys.SEXP.getKey(), DSTFieldKeys.PNAT.getKey())));
        fieldList.addAll(dstIndividualFactory.getIndividualDetailsFieldList(isPlayedBy));
        return fieldList;
    }

    private List<Field> getAgreementRelatedFieldList(FinanceServiceArrangement financeServiceArrangement) {
        List<Field> fieldList = new ArrayList<>();
        if (ActivateCommonConstant.ApplicationType.NEW.equals(financeServiceArrangement.getApplicationType())) {
            fieldList.add(dstFieldHelper.getField(DSTFieldKeys.TRAD.getKey(), STATUS_NO));
            fieldList.add(dstFieldHelper.getFieldForDateValue(DSTFieldKeys.APDT.getKey(), financeServiceArrangement.getAgreementAcceptedDate(), "dd/MM/yyyy", FastDateFormat.getInstance("dd/MM/yyyy").format(new Date())));
            fieldList.add(dstFieldHelper.getFieldForDateValue(DSTFieldKeys.PCCI.getKey(), financeServiceArrangement.getPcciViewedDate(), "dd/MM/yyyy", FastDateFormat.getInstance("dd/MM/yyyy").format(new Date())));
            fieldList.add(dstFieldHelper.getField(DSTFieldKeys.PSIG.getKey(), STATUS_YES));
        } else {
            fieldList.add(dstFieldHelper.getField(DSTFieldKeys.TRAD.getKey(), STATUS_YES));
            fieldList.add(dstFieldHelper.getField(DSTFieldKeys.APDT.getKey(), ""));
            fieldList.add(dstFieldHelper.getField(DSTFieldKeys.PCCI.getKey(), ""));
            fieldList.add(dstFieldHelper.getField(DSTFieldKeys.PSIG.getKey(), STATUS_NO));
        }
        return fieldList;
    }
    private List<Field> getPostalAddressFieldList(List<PostalAddress> postalAddressList) {
        List<Field> fieldList = new ArrayList<>();
        for (PostalAddress postalAddress : postalAddressList) {
            if (ActivateCommonConstant.AddressType.CURRENT.equalsIgnoreCase(postalAddress.getStatusCode())) {
                fieldList.addAll(getAddressFieldList(postalAddress, Arrays.asList(DSTFieldKeys.PAD1.getKey(), DSTFieldKeys.PAD2.getKey(),
                        DSTFieldKeys.PAD3.getKey(), DSTFieldKeys.PCTY.getKey(), null, DSTFieldKeys.PCDE.getKey(), DSTFieldKeys.TACA.getKey())));
            } else if (ActivateCommonConstant.AddressType.PREVIOUS.equalsIgnoreCase(postalAddress.getStatusCode())) {
                fieldList.addAll(getAddressFieldList(postalAddress, Arrays.asList(DSTFieldKeys.PPA1.getKey(), DSTFieldKeys.PPA2.getKey(),
                        DSTFieldKeys.PPA3.getKey(), DSTFieldKeys.PPCT.getKey(), DSTFieldKeys.PPCY.getKey(), DSTFieldKeys.PPCD.getKey(), DSTFieldKeys.TAPA.getKey())));
            }
        }
        return fieldList;
    }
    private List<Field> getAssociatedProductFieldList(Product associatedProduct) {
        List<Field> fieldList = new ArrayList<>();
        if (associatedProduct != null) {
            List<ProductOffer> productOfferList = associatedProduct.getProductoffer();
            if (!productOfferList.isEmpty()) {
                if (productOfferList.get(0).getOfferAmount() != null) {
                    fieldList.add(dstFieldHelper.getFieldForStringValue(DSTFieldKeys.CSLM.getKey(), String.valueOf(productOfferList.get(0).getOfferAmount().getAmount())));
                }
                List<ProductAttributes> productAttributeList = productOfferList.get(0).getProductattributes();
                for (ProductAttributes productAttributes : productAttributeList) {
                    if (DSTFieldKeys.MAC.getKey().equalsIgnoreCase(productAttributes.getAttributeCode())) {
                        fieldList.add(dstFieldHelper.getFieldForStringValue(DSTFieldKeys.MAC.getKey(), productAttributes.getAttributeValue()));
                    } else if (PRODUCT_ATTRIBUTE_ISS_ID.equalsIgnoreCase(productAttributes.getAttributeCode())) {
                        fieldList.add(dstFieldHelper.getFieldForStringValue(DSTFieldKeys.PCTD.getKey(), productAttributes.getAttributeValue()));
                    }
                }
            }
            for (ExtSysProdIdentifier extSysProdIdentifier : associatedProduct.getExternalSystemProductIdentifier()) {
                if (EXTERNAL_SYSTEM_ASM.equals(extSysProdIdentifier.getSystemCode()) && !StringUtils.isEmpty(extSysProdIdentifier.getProductIdentifier())) {
                    fieldList.add(dstFieldHelper.getFieldForStringValue(DSTFieldKeys.PROD.getKey(), String.format("%04d", Long.valueOf(extSysProdIdentifier.getProductIdentifier()))));
                    break;
                }
            }
        }
        return fieldList;
    }

    private List<Field> getBalanceTransferFieldList(List<BalanceTransfer> balanceTransferList) {
        List<Field> fieldList = new ArrayList<>();
        fieldList.add(dstFieldHelper.getFieldForBooleanValue(DSTFieldKeys.BTRF.getKey(), !balanceTransferList.isEmpty()));
        int count = 1;
        for (BalanceTransfer balanceTransfer : balanceTransferList) {
            if (count <= BALANCE_TRANSFER_MAX && balanceTransfer.getAmount() != null) {
                fieldList.add(dstFieldHelper.getFieldForStringValue(DSTFieldKeys.BTA.getKey() + count, String.valueOf(balanceTransfer.getAmount().getAmount())));
                count++;
            }
        }
        return fieldList;
    }

    private List<Field> getTelephoneFieldList(List<TelephoneNumber> telephoneNumberList) {
        List<Field> fieldList = new ArrayList<>();
        for (TelephoneNumber telephoneNumber : telephoneNumberList) {
            if (ActivateCommonConstant.TelephoneTypes.HOME.equals(telephoneNumber.getTelephoneType())) {
                fieldList.add(dstFieldHelper.getField(DSTFieldKeys.HSTD.getKey(), "0" + telephoneNumber.getAreaCode()));
                fieldList.add(dstFieldHelper.getFieldForStringValue(DSTFieldKeys.TELE.getKey(), telephoneNumber.getPhoneNumber()));
            } else if (ActivateCommonConstant.TelephoneTypes.BUSINESS.equals(telephoneNumber.getTelephoneType())) {
                fieldList.add(dstFieldHelper.getField(DSTFieldKeys.WSTD.getKey(), "0" + telephoneNumber.getAreaCode()));
                fieldList.add(dstFieldHelper.getFieldForStringValue(DSTFieldKeys.WTEL.getKey(), telephoneNumber.getPhoneNumber()));
            } else if (ActivateCommonConstant.TelephoneTypes.MOBILE.equals(telephoneNumber.getTelephoneType())) {
                fieldList.add(dstFieldHelper.getFieldForStringValue(DSTFieldKeys.MPHN.getKey(), "0" + telephoneNumber.getPhoneNumber()));
            }
        }
        return fieldList;
    }

    private List<Field> getAddressFieldList(PostalAddress postalAddress, List<String> keyList) {
        List<Field> fieldList = new ArrayList<>();
        StructuredAddress structuredAddress = postalAddress.getStructuredAddress();
        UnstructuredAddress unstructuredAddress = postalAddress.getUnstructuredAddress();
        if (postalAddress.isIsPAFFormat() && structuredAddress != null) {
            String addressLine = (structuredAddress.getBuildingNumber() + "," + structuredAddress.getBuilding() + "," + structuredAddress.getAddressLinePAFData().get(0) + ",").replaceAll("null,", "").replaceAll(",$", "");
            fieldList.add(dstFieldHelper.getField(keyList.get(INDEX_ZERO_FOR_ADDRESS_LINE), addressLine));
            fieldList.add(dstFieldHelper.getFieldForStringValue(keyList.get(INDEX_ONE_FOR_DISTRICT), structuredAddress.getDistrict()));
            fieldList.add(dstFieldHelper.getFieldForStringValue(keyList.get(INDEX_TWO_FOR_POST_TOWN), structuredAddress.getPostTown()));
            fieldList.add(dstFieldHelper.getFieldForStringValue(keyList.get(INDEX_THREE_FOR_COUNTRY), dstFieldHelper.getFormattedString(structuredAddress.getCounty(), COUNTRY_MAX_LENGTH)));
            if (ActivateCommonConstant.AddressType.PREVIOUS.equalsIgnoreCase(postalAddress.getStatusCode())) {
                fieldList.add(dstFieldHelper.getFieldForStringValue(keyList.get(INDEX_FOUR_FOR_PREVIOUS_ADDRESS_STATUS_CODE), structuredAddress.getCountry()));
            }
            if (structuredAddress.getPostCodeOut() != null && structuredAddress.getPostCodeIn() != null) {
                fieldList.add(dstFieldHelper.getField(keyList.get(INDEX_FIVE_FOR_POST_CODE), structuredAddress.getPostCodeOut() + structuredAddress.getPostCodeIn()));
            }
        } else if (unstructuredAddress != null) {
            String addressLine = (unstructuredAddress.getAddressLine1() + "," + unstructuredAddress.getAddressLine2() + "," + unstructuredAddress.getAddressLine3() + "," + unstructuredAddress.getAddressLine4() + ",").replaceAll("null,", "").replaceAll(",$", "");
            fieldList.add(dstFieldHelper.getField(keyList.get(INDEX_ZERO_FOR_ADDRESS_LINE), dstFieldHelper.getFormattedString(addressLine, ADDRESS_LINE_MAX_LENGTH)));
            fieldList.add(dstFieldHelper.getFieldForStringValue(keyList.get(INDEX_ONE_FOR_DISTRICT), dstFieldHelper.getFormattedString(unstructuredAddress.getAddressLine5(), ADDRESS_LINE_MAX_LENGTH)));
            fieldList.add(dstFieldHelper.getFieldForStringValue(keyList.get(INDEX_TWO_FOR_POST_TOWN), dstFieldHelper.getFormattedString(unstructuredAddress.getAddressLine6(), ADDRESS_LINE_MAX_LENGTH)));
            fieldList.add(dstFieldHelper.getFieldForStringValue(keyList.get(INDEX_THREE_FOR_COUNTRY), dstFieldHelper.getFormattedString(unstructuredAddress.getAddressLine7(), COUNTRY_MAX_LENGTH)));
            if (ActivateCommonConstant.AddressType.PREVIOUS.equalsIgnoreCase(postalAddress.getStatusCode())) {
                fieldList.add(dstFieldHelper.getFieldForStringValue(keyList.get(INDEX_FOUR_FOR_PREVIOUS_ADDRESS_STATUS_CODE), unstructuredAddress.getAddressLine7()));
            }
            fieldList.add(dstFieldHelper.getFieldForStringValue(keyList.get(INDEX_FIVE_FOR_POST_CODE), unstructuredAddress.getPostCode()));
        }
        fieldList.add(dstFieldHelper.getFieldForStringValue(keyList.get(INDEX_SIX_FOR_DURATION_OF_STAY), postalAddress.getDurationofStay()));
        return fieldList;
    }
}