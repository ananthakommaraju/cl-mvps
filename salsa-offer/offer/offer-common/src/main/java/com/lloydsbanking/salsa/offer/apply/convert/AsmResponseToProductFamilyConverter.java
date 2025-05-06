package com.lloydsbanking.salsa.offer.apply.convert;


import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Resp;
import com.lloydsbanking.salsa.soap.asm.f205.objects.ProductsOffered;
import com.lloydsbanking.salsa.soap.asm.f424.objects.F424Resp;
import com.lloydsbanking.salsa.soap.asm.f424.objects.ProductOffered;
import lib_sim_bo.businessobjects.*;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AsmResponseToProductFamilyConverter {

    private static final String EXTERNAL_SYSTEM_ASM = "00107";

    public List<ProductFamily> creditScoreResponseToProductFamilyConverter(F205Resp f205Resp) {
        List<ProductFamily> productFamilyList = new ArrayList<>();
        if (f205Resp.getProductsOffered() != null) {
            for (ProductsOffered productsOffered : f205Resp.getProductsOffered()) {
                ProductFamily productFamily = new ProductFamily();
                ExtSysProdFamilyIdentifier extSysProdFamilyIdentifier = new ExtSysProdFamilyIdentifier();
                extSysProdFamilyIdentifier.setProductFamilyIdentifier(productsOffered.getCSProductsOfferedCd());
                productFamily.getExtsysprodfamilyidentifier().add(extSysProdFamilyIdentifier);
                productFamilyList.add(productFamily);
            }
        }
        return productFamilyList;
    }

    public List<ProductFamily> creditDecisionResponseToProductFamilyConverter(F424Resp f424Resp) {

        List<ProductFamily> productFamilyList = new ArrayList<>();
        if (f424Resp.getProductOffered() != null) {
            for (ProductOffered productOffered : f424Resp.getProductOffered()) {

                ProductFamily productFamily = new ProductFamily();
                Product product = new Product();
                ProductOffer productOffer = new ProductOffer();
                CurrencyAmount offerAmount = new CurrencyAmount();
                PricePoint pricePoint = new PricePoint();
                ExtSysProdFamilyIdentifier extSysProdFamilyIdentifier = new ExtSysProdFamilyIdentifier();

                if (!StringUtils.isEmpty(productOffered.getProductOfferedAm())) {

                    offerAmount.setAmount(new BigDecimal(productOffered.getProductOfferedAm()));
                    BigDecimal amount = offerAmount.getAmount();
                    String divider = "100";
                    offerAmount.setAmount(divide(amount, divider));
                    productOffer.setOfferAmount(offerAmount);


                }
                if (!StringUtils.isEmpty(productOffered.getPriceTierCd())) {
                    pricePoint.setExternalSystemIdentifier(productOffered.getPriceTierCd());
                    pricePoint.setSystemCode(EXTERNAL_SYSTEM_ASM);
                    productOffer.getPricepoint().add(pricePoint);
                }
                product.getProductoffer().add(productOffer);
                productFamily.getProductFamily().add(product);

                if (!StringUtils.isEmpty(productOffered.getProductOfferedCd())) {
                    extSysProdFamilyIdentifier.setProductFamilyIdentifier(productOffered.getProductOfferedCd());
                    productFamily.getExtsysprodfamilyidentifier().add(extSysProdFamilyIdentifier);
                }

                productFamilyList.add(productFamily);

            }
        }
        return productFamilyList;
    }

    public static BigDecimal divide(BigDecimal amount, String divider) {
        BigDecimal finalAmount = amount;
        BigDecimal dividerValue = new BigDecimal(divider);
        if (amount != null) {
            finalAmount = amount.divide(dividerValue);
            finalAmount = finalAmount.setScale(2);
        }
        return finalAmount;
    }


}
