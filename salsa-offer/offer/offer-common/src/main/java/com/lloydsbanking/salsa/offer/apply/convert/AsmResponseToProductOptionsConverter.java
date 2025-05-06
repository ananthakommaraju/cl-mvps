package com.lloydsbanking.salsa.offer.apply.convert;

import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Resp;
import com.lloydsbanking.salsa.soap.asm.f205.objects.FacilitiesOffered;
import lib_sim_bo.businessobjects.ProductOptions;
import org.apache.cxf.common.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AsmResponseToProductOptionsConverter {

    private static final double MOVE_TWO_DECIMAL_PLACE_DIVISOR = 100.0;
    private static final String OVERDRAFT_RISK_CODE = "OVERDRAFT_RISK_CODE";

    public List<ProductOptions> creditScoreResponseToProductOptionsConverter(F205Resp f205Resp) {

        List<ProductOptions> productOptionsList = new ArrayList<>();
        if (f205Resp.getFacilitiesOffered() != null) {
            for (FacilitiesOffered facilitiesOffered : f205Resp.getFacilitiesOffered()) {
                ProductOptions productOptions = new ProductOptions();
                productOptions.setOptionsCode(facilitiesOffered.getCSFacilityOfferedCd());
                if(!StringUtils.isEmpty(facilitiesOffered.getCSFacilityOfferedAm())) {
                    Double csFacilityOfferedAmount = Double.parseDouble(facilitiesOffered.getCSFacilityOfferedAm()) / MOVE_TWO_DECIMAL_PLACE_DIVISOR;
                    productOptions.setOptionsValue(String.valueOf(csFacilityOfferedAmount.intValue()));
                }
                productOptionsList.add(productOptions);

            }
        }

        if (f205Resp.getFormalOverdraftRiskCd()!=null && !f205Resp.getFormalOverdraftRiskCd().isEmpty()){
            ProductOptions productOptions = new ProductOptions();
            productOptions.setOptionsValue(f205Resp.getFormalOverdraftRiskCd());
            productOptions.setOptionsCode(OVERDRAFT_RISK_CODE);
            productOptionsList.add(productOptions);
        }
        return productOptionsList;
    }
}
