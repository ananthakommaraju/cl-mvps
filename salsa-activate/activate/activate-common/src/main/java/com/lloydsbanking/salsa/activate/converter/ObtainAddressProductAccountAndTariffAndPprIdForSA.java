package com.lloydsbanking.salsa.activate.converter;

import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.DepositArrangement;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ObtainAddressProductAccountAndTariffAndPprIdForSA {
    public static final String INTEREST_FREQUENCY_MIPF = "MIPF";

    public static final String INTEREST_FREQUENCY_AIPF = "AIPF";

    public static final String INTEREST_FREQUENCY_HIPF = "HIPF";

    public static final String INTEREST_FREQUENCY_QIPF = "QIPF";

    public static final String INTEREST_FREQUENCY_OMIPF = "OMIPF";

    public static final String INTEREST_FREQUENCY_MTRF = "MTRF";

    public static final String INTEREST_FREQUENCY_ATRF = "ATRF";

    public static final String INTEREST_FREQUENCY_HTRF = "HTRF";

    public static final String INTEREST_FREQUENCY_QTRF = "QTRF";

    public static final String INTEREST_FREQUENCY_OMTRF = "OMTRF";

    public static final String PRODUCT_OPTION_STRF = "STRF";

    public static final String PRODUCT_OPTION_TRF = "TRF";

    public static final String PRODUCT_OPTION_ISA = "ISA";

    public static final String PRODUCT_OPTION_ATRF = "ATRF";

    public static final String PRODUCT_OPTION_MTRF = "MTRF";

    public static final String PRODUCT_OPTION_OMTRF = "OMTRF";


    public String getPprID(DepositArrangement depositArrangement) {
        String pprId = null;
        if (depositArrangement.getAssociatedProduct() != null && !CollectionUtils.isEmpty(depositArrangement.getAssociatedProduct().getProductPreferentialRate())) {
            pprId = depositArrangement.getAssociatedProduct().getProductPreferentialRate().get(0).getPreferentialRateIdentifier();
        }
        return pprId;
    }

    private Map<String, String> frequencyMap() {
        Map<String, String> interestPaidFrequencyMap = new HashMap<>();
        interestPaidFrequencyMap.put(INTEREST_FREQUENCY_MIPF, INTEREST_FREQUENCY_MTRF);
        interestPaidFrequencyMap.put(INTEREST_FREQUENCY_AIPF, INTEREST_FREQUENCY_ATRF);
        interestPaidFrequencyMap.put(INTEREST_FREQUENCY_HIPF, INTEREST_FREQUENCY_HTRF);
        interestPaidFrequencyMap.put(INTEREST_FREQUENCY_QIPF, INTEREST_FREQUENCY_QTRF);
        interestPaidFrequencyMap.put(INTEREST_FREQUENCY_OMIPF, INTEREST_FREQUENCY_OMTRF);
        return interestPaidFrequencyMap;
    }

    public String getTariffSA(DepositArrangement depositArrangement, Map<String, String> productOptionMap) {
        String tariff;
        Customer customer = depositArrangement.getPrimaryInvolvedParty();

        if (!StringUtils.isEmpty(depositArrangement.getInterestPaidfrequency())) {
            tariff = productOptionMap.get(frequencyMap().get(depositArrangement.getInterestPaidfrequency()));
        } else if ("1".equals(productOptionMap.get(PRODUCT_OPTION_ISA))) {
            if (customer != null && customer.getIsPlayedBy() != null && customer.getIsPlayedBy().isIsStaffMember()) {
                tariff = productOptionMap.get(PRODUCT_OPTION_STRF);
            } else {
                tariff = productOptionMap.get(PRODUCT_OPTION_TRF);
            }
        } else if (productOptionMap.get(PRODUCT_OPTION_ATRF) != null) {
            tariff = productOptionMap.get(PRODUCT_OPTION_ATRF);
        } else if (productOptionMap.get(PRODUCT_OPTION_MTRF) != null) {
            tariff = productOptionMap.get(PRODUCT_OPTION_MTRF);
        } else if (productOptionMap.get(PRODUCT_OPTION_OMTRF) != null) {
            tariff = productOptionMap.get(PRODUCT_OPTION_OMTRF);
        } else {
            tariff = productOptionMap.get(PRODUCT_OPTION_TRF);
        }

        return tariff;
    }

}
