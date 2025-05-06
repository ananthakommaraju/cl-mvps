package com.lloydsbanking.salsa.activate.administer.convert;


import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.soap.asm.f425.objects.DecisionDetails;
import com.lloydsbanking.salsa.soap.asm.f425.objects.F425Resp;
import com.lloydsbanking.salsa.soap.asm.f425.objects.FacilitiesOffered;
import com.lloydsbanking.salsa.soap.asm.f425.objects.ProductOffered;
import lib_sim_bo.businessobjects.*;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class F425ResponseToApplicationDetailsConverter {


    public static final String ASM_AMOUNT_SCALING_FACTOR = "100";

    public ApplicationDetails convert(F425Resp f425Response) {
        ApplicationDetails applicationDetails = new ApplicationDetails();
        List<ProductOffered> productOfferedList = f425Response.getProductOffered();
        if (productOfferedList != null) {

            for (ProductOffered productOffered : productOfferedList) {
                ProductFamily productFamily = getProductFamily(productOffered);
                applicationDetails.getProductFamilies().add(productFamily);
            }
            if (!productOfferedList.isEmpty() && !StringUtils.isEmpty(productOfferedList.get(0).getProductOfferedAm())) {
                applicationDetails.setCreditLimit(getShadowLimitAmount(productOfferedList.get(0).getProductOfferedAm()));
            }
        }

        if (f425Response.getResultsDetails() != null) {
            applicationDetails.setApplicationStatus(f425Response.getResultsDetails().getASMCreditScoreResultCd());
            applicationDetails.setScoreResult(f425Response.getResultsDetails().getASMCreditScoreResultCd());
        }

        if (f425Response.getDecisionDetails() != null) {
            for (DecisionDetails decisionDetails : f425Response.getDecisionDetails()) {
                applicationDetails.getReferralCodes().add(getReferralCode(decisionDetails));
            }
        }
        if (f425Response.getFacilitiesOffered() != null) {
            for (FacilitiesOffered facilitiesOffered : f425Response.getFacilitiesOffered()) {
                applicationDetails.getProductOptions().add(getProductOptions(facilitiesOffered));
            }
        }
        return applicationDetails;
    }

    private ProductFamily getProductFamily(ProductOffered productOffered) {
        ExtSysProdFamilyIdentifier identifier = new ExtSysProdFamilyIdentifier();
        identifier.setProductFamilyIdentifier(productOffered.getProductsOfferedCd());
        ProductFamily productFamily = new ProductFamily();
        productFamily.getExtsysprodfamilyidentifier().add(identifier);
        return productFamily;
    }

    private CurrencyAmount getShadowLimitAmount(String productOfferedAmount) {
        CurrencyAmount shadowLimitAmout = new CurrencyAmount();
        BigDecimal amount = (new BigDecimal(productOfferedAmount)).divide(new BigDecimal(ASM_AMOUNT_SCALING_FACTOR));
        shadowLimitAmout.setAmount(amount);
        return shadowLimitAmout;
    }

    private ReferralCode getReferralCode(DecisionDetails decisionDetails) {
        ReferralCode referralCode = new ReferralCode();
        referralCode.setCode(decisionDetails.getCSDecisionReasonTypeCd());
        referralCode.setDescription(decisionDetails.getCSDecisnReasonTypeNr());
        return referralCode;
    }

    private ProductOptions getProductOptions(FacilitiesOffered facilitiesOffered) {
        ProductOptions productOption = new ProductOptions();
        productOption.setOptionsCode(facilitiesOffered.getCSFacilityOfferedCd());
        if (!StringUtils.isEmpty(facilitiesOffered.getCSFacilityOfferedAm())) {
            Double amount = Double.valueOf(facilitiesOffered.getCSFacilityOfferedAm()) / Double.valueOf(ASM_AMOUNT_SCALING_FACTOR);
            productOption.setOptionsValue(String.valueOf(amount));
        }
        return productOption;
    }
}
