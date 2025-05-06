package com.lloydsbanking.salsa.apacc.service.fulfil.convert;

import com.lloydsbanking.salsa.brand.Brand;
import com.lloydsbanking.salsa.soap.ocis.f259.objects.F259Req;
import com.lloydsbanking.salsa.soap.ocis.f259.objects.PartyRoleCodes;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.ProductAttributes;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class F259RequestFactory {
    private static final int MAX_REPEAT_GROUP_QY = 0;

    private static final int EXTERNAL_SYS_ID = 19;

    private static final int EXTERNAL_SYS_SALSA = 13;

    private static final int PROD_EXTERNAL_SYS_SALSA = 13;

    private static final String PROD_HELD_STATUS_CODE1 = "001";

    private static final String PROD_HELD_STATUS_CODE2 = "002";

    private static final String EMBEDDED_INS_CODE = "000";

    private static final String ATTRIBUTE_CODE_ORG = "ACC_ORG";

    private static final String ATTRIBUTE_CODE_LOGO = "ACC_LOGO";

    private static final String ATTRIBUTE_CODE_BIN = "ACC_BIN";

    private static final String SELLER_CODE_HAL = "HAL";

    private static final String SELLER_CODE_VTB = "VTB";

    public F259Req convert(FinanceServiceArrangement financeServiceArrangement, String channelId) {
        F259Req f259Req = new F259Req();
        f259Req.setMaxRepeatGroupQy(MAX_REPEAT_GROUP_QY);
        f259Req.setExtSysId(EXTERNAL_SYS_ID);
        f259Req.setProdExtSysId(PROD_EXTERNAL_SYS_SALSA);
        f259Req.setProdHeldStatusCd(PROD_HELD_STATUS_CODE1);
        f259Req.setEmbeddedInsCd(EMBEDDED_INS_CODE);

        f259Req.setExtProdIdTx("");
        if (financeServiceArrangement.getAssociatedProduct() != null && !financeServiceArrangement.getAssociatedProduct().getProductoffer().isEmpty()
                && !financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes().isEmpty()) {
            String accOrg = "";
            String logo = "";
            String bin = "";
            for (ProductAttributes productAttributes : financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes()) {
                if (!productAttributes.getAttributeCode().isEmpty()) {
                    if (productAttributes.getAttributeCode().equals(ATTRIBUTE_CODE_ORG)) {
                        accOrg = productAttributes.getAttributeValue();
                    }
                    if (productAttributes.getAttributeCode().equals(ATTRIBUTE_CODE_LOGO)) {
                        logo = productAttributes.getAttributeValue();
                    }
                    if (productAttributes.getAttributeCode().equals(ATTRIBUTE_CODE_BIN)) {
                        bin = productAttributes.getAttributeValue();
                    }
                }
            }
            f259Req.setExtProdIdTx(accOrg + logo + bin);
        }
        if (!StringUtils.isEmpty(financeServiceArrangement.getCreditCardNumber())) {
            f259Req.setExtProdHeldIdTx(String.valueOf(Long.valueOf(financeServiceArrangement.getCreditCardNumber())));
        }
        f259Req.setSellerLegalEntCd(getSellerLegalCode(channelId));
        f259Req.getPartyRoleCodes().add(createPartyRoleCodes(financeServiceArrangement.getPrimaryInvolvedParty()));
        return f259Req;
    }

    private PartyRoleCodes createPartyRoleCodes(Customer primaryInvolvedParty) {
        PartyRoleCodes partyRoleCodes = new PartyRoleCodes();
        partyRoleCodes.setExtSysId(EXTERNAL_SYS_SALSA);
        partyRoleCodes.setProdHeldRoleCd(PROD_HELD_STATUS_CODE2);
        if (primaryInvolvedParty != null) {
            partyRoleCodes.setExtPartyIdTx(primaryInvolvedParty.getPartyIdentifier());
            if (!StringUtils.isEmpty(primaryInvolvedParty.getCustomerIdentifier())) {
                partyRoleCodes.setPartyId(Long.valueOf(primaryInvolvedParty.getCustomerIdentifier()));
            }
        }
        return partyRoleCodes;
    }

    private String getSellerLegalCode(String channelId) {
        String sellerLegalEntCd = null;
        if (Brand.LLOYDS.asString().equalsIgnoreCase(channelId) || Brand.BANK_OF_SCOTLAND.asString().equalsIgnoreCase(channelId)) {
            sellerLegalEntCd = channelId;
        }
        if (Brand.HALIFAX.asString().equalsIgnoreCase(channelId)) {
            sellerLegalEntCd = SELLER_CODE_HAL;
        }
        if (Brand.TRUSTEE_SAVINGS_BANK.asString().equalsIgnoreCase(channelId)) {
            sellerLegalEntCd = SELLER_CODE_VTB;
        }
        return sellerLegalEntCd;
    }
}
