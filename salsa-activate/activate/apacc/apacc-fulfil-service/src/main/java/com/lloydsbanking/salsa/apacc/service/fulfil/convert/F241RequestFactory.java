package com.lloydsbanking.salsa.apacc.service.fulfil.convert;


import com.lloydsbanking.salsa.soap.fdi.f241.objects.AccountDataSegment;
import com.lloydsbanking.salsa.soap.fdi.f241.objects.CardDataSegment;
import com.lloydsbanking.salsa.soap.fdi.f241.objects.F241Req;
import com.lloydsbanking.salsa.soap.fdi.f241.objects.MSCardLogoTable;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.ProductAttributes;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class F241RequestFactory {

    private static final Logger LOGGER = Logger.getLogger(F241RequestFactory.class);
    @Autowired
    F241MarketingDataSegmentFactory f241MarketingDataSegmentFactory;

    @Autowired
    F241AccountDataSegmentFactory f241AccountDataSegmentFactory;

    @Autowired
    F241CustomerDataSegmentFactory f241CustomerDataSegmentFactory;

    public F241Req convert(FinanceServiceArrangement financeServiceArrangement) {
        F241Req f241Req = new F241Req();
        Customer customer = financeServiceArrangement.getPrimaryInvolvedParty();
        f241Req.setMaxRepeatGroupQy(1);
        f241Req.setMessageVersionNo("E8V4");
        f241Req.setEmbosserRecActionCd("A");
        CardDataSegment cardDataSegment = getCardDataSegment(financeServiceArrangement.isIsJointParty());
        AccountDataSegment accountDataSegment = f241AccountDataSegmentFactory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
        f241Req.setMarketingDataSegment(f241MarketingDataSegmentFactory.getMarketingDataSegment(financeServiceArrangement));
        f241Req.setCustomerDataSegment(f241CustomerDataSegmentFactory.getCustomerDataSegment(financeServiceArrangement));
        f241Req.setAccountDataSegment(accountDataSegment);
        if (financeServiceArrangement.isIsJointParty()) {
            f241Req.setCustRecActionCd("A");
        } else {
            if (customer.getCustomerNumber() != null) {
                f241Req.setCustRecActionCd("U");
            } else {
                f241Req.setCustRecActionCd("A");
            }
            f241Req.setAcRecActionCd("A");
        }
        f241Req.setMaxRepeatGroupQy(1);
        setMsCardLogoTable(financeServiceArrangement, f241Req);
        f241Req.setCardDataSegment(cardDataSegment);
        return f241Req;
    }

    private void setMsCardLogoTable(FinanceServiceArrangement financeServiceArrangement, F241Req f241Req) {
        List<ProductAttributes> productAttributesList = financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes();
        if (!CollectionUtils.isEmpty(productAttributesList)) {
            for (ProductAttributes productAttributes : productAttributesList) {
                if (!StringUtils.isEmpty(productAttributes.getAttributeCode()) && StringUtils.isNumeric(productAttributes.getAttributeValue())) {
                    if ("MUL_SC_1".equalsIgnoreCase(productAttributes.getAttributeCode()) || "MUL_SC_2".equalsIgnoreCase(productAttributes.getAttributeCode())) {
                        MSCardLogoTable msCardLogoTable = new MSCardLogoTable();
                        msCardLogoTable.setMultiSchemeCardLogoId(Short.valueOf(productAttributes.getAttributeValue()));
                        f241Req.getMSCardLogoTable().add(msCardLogoTable);
                    }
                }
            }
        }
    }

    private CardDataSegment getCardDataSegment(Boolean isJointParty) {
        CardDataSegment cardDataSegment = new CardDataSegment();
        if (isJointParty) {
            cardDataSegment.setCardholderPrimaryTypeCd("0");
        } else {
            cardDataSegment.setCardholderPrimaryTypeCd("1");
        }
        cardDataSegment.setCardReissueStatusCd((short) 1);
        cardDataSegment.setCardholderEmbossedNm("");
        cardDataSegment.setOfflinePINCd("1");
        return cardDataSegment;
    }
}
