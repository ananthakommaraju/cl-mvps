package com.lloydsbanking.salsa.apacc.service.fulfil.convert;


import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.AccountDataSegmentV1;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.CardDataSegment;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.F241Req;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.MSCardLogoTable;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.ProductAttributes;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class F241V1RequestFactory {

    @Autowired
    F241V1MarketingDataSegmentFactory f241V1MarketingDataSegmentFactory;

    @Autowired
    F241V1CustomerDataSegmentFactory f241V1CustomerDataSegmentFactory;

    @Autowired
    F241V1AccountDataSegmentFactory f241V1AccountDataSegmentFactory;

    public F241Req convert(FinanceServiceArrangement financeServiceArrangement) {
        F241Req f241Req = new F241Req();

        Customer customer = financeServiceArrangement.getPrimaryInvolvedParty();
        f241Req.setMaxRepeatGroupQy(1);
        f241Req.setMessageVersionNo("E8V7");
        f241Req.setEmbosserRecActionCd("A");
        CardDataSegment cardDataSegment = getCardDataSegment();
        AccountDataSegmentV1 accountDataSegment = f241V1AccountDataSegmentFactory.getAccountDataSegment(financeServiceArrangement, cardDataSegment);
        f241Req.setMarketingDataSegment(f241V1MarketingDataSegmentFactory.getMarketingDataSegment(financeServiceArrangement));
        f241Req.setCustomerDataSegment(f241V1CustomerDataSegmentFactory.getCustomerDataSegment(financeServiceArrangement));
        f241Req.setAccountDataSegment(accountDataSegment);
        if (customer.getCustomerNumber() != null) {
            f241Req.setCustRecActionCd("U");
        } else {
            f241Req.setCustRecActionCd("A");
        }
        f241Req.setAcRecActionCd("A");
        f241Req.setMaxRepeatGroupQy(1);
        setMsCardLogoTable(financeServiceArrangement, f241Req);
        f241Req.setCardDataSegment(cardDataSegment);
        return f241Req;
    }

    private void setMsCardLogoTable(FinanceServiceArrangement financeServiceArrangement, F241Req f241Req) {
        List<ProductAttributes> productAttributesList = financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProductattributes();
        if (!CollectionUtils.isEmpty(productAttributesList)) {
            for (ProductAttributes productAttributes : productAttributesList) {
                if (!StringUtils.isEmpty(productAttributes.getAttributeCode()) && StringUtils.isNumeric(productAttributes.getAttributeValue()) && "MUL_SC_1".equalsIgnoreCase(productAttributes.getAttributeCode())) {
                    MSCardLogoTable msCardLogoTable = new MSCardLogoTable();
                    msCardLogoTable.setMultiSchemeCardLogoId(Short.valueOf(productAttributes.getAttributeValue()));
                    f241Req.getMSCardLogoTable().add(msCardLogoTable);
                }
                if (!StringUtils.isEmpty(productAttributes.getAttributeCode()) && StringUtils.isNumeric(productAttributes.getAttributeValue()) && "MUL_SC_2".equalsIgnoreCase(productAttributes.getAttributeCode())) {
                    MSCardLogoTable msCardLogoTable = new MSCardLogoTable();
                    msCardLogoTable.setMultiSchemeCardLogoId(Short.valueOf(productAttributes.getAttributeValue()));
                    f241Req.getMSCardLogoTable().add(msCardLogoTable);
                }
            }
        }
    }

    private CardDataSegment getCardDataSegment() {
        CardDataSegment cardDataSegment = new CardDataSegment();
        cardDataSegment.setCardholderPrimaryTypeCd("0");
        cardDataSegment.setCardholderPrimaryTypeCd("1");
        cardDataSegment.setCardReissueStatusCd((short) 1);
        cardDataSegment.setCardholderEmbossedNm("");
        cardDataSegment.setOfflinePINCd("1");
        return cardDataSegment;
    }
}
