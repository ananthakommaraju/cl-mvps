package com.lloydsbanking.salsa.apacc.service.fulfil.convert;

import com.lloydsbanking.salsa.soap.fdi.f251.objects.F251Req;
import com.lloydsbanking.salsa.soap.fdi.f251.objects.OfferData;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductAttributes;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class F251RequestFactory {
    public static final String ACCOUNT_USER_CODE_2 = "0000000";
    public static final String OFFER_ID_NEW_ACCOUNT = "NEWACC";
    public static final String OFFER_ID_ADD_CARD = "ADDCARD";
    public static final String OFFER_STATUS_CD_2 = "2";
    public static final String OFFER_STATUS_CD_1 = "1";
    public static final int CHANNEL_CODE_LENGTH = 3;


    public F251Req convert(FinanceServiceArrangement financeServiceArrangement, String contactPointId) {
        F251Req f251Req = new F251Req();
        f251Req.setCardNo(financeServiceArrangement.getAccountNumber());
        f251Req.setMaxRepeatGroupQy(0);
        f251Req.setMessageVersionNo("R8V1");
        if (financeServiceArrangement.getInitiatedThrough() != null) {
            String channelCode = financeServiceArrangement.getInitiatedThrough().getChannelCode();
            if (!StringUtils.isEmpty(channelCode) && channelCode.length() == CHANNEL_CODE_LENGTH) {
                f251Req.setCommsTypeCd(channelCode.substring(2));
            }
        }
        f251Req.getOfferData().addAll(getOfferDataList(financeServiceArrangement, contactPointId));

        return f251Req;
    }

    private List<OfferData> getOfferDataList(FinanceServiceArrangement financeServiceArrangement, String contactPointId) {
        List<OfferData> offerDataList = new ArrayList<>();
        Product product = financeServiceArrangement.getAssociatedProduct();
        String contactPoint = contactPointId.replaceFirst("^0+(?!$)", "");
        String offerStatusCode = (!financeServiceArrangement.getBalanceTransfer().isEmpty()) ? OFFER_STATUS_CD_2 : OFFER_STATUS_CD_1;
        offerDataList.add(getOfferData(OFFER_ID_NEW_ACCOUNT, OFFER_STATUS_CD_2, contactPoint));
        offerDataList.add(getOfferData(OFFER_ID_ADD_CARD, OFFER_STATUS_CD_2, contactPoint));
        if (product != null && !product.getProductoffer().isEmpty() && product.getProductoffer().get(0) != null) {
            List<ProductAttributes> productAttributesList = product.getProductoffer().get(0).getProductattributes();
            for (ProductAttributes productAttributes : productAttributesList) {
                if ("BT_OF".equalsIgnoreCase(productAttributes.getAttributeType())) {
                    if (!StringUtils.isEmpty(productAttributes.getAttributeValue())) {
                        offerDataList.add(getOfferData(productAttributes.getAttributeValue(), offerStatusCode, contactPoint));
                    }
                }
            }
        }
        return offerDataList;
    }

    private OfferData getOfferData(String offerId, String statusCd, String contactPoint) {
        OfferData offerData = new OfferData();
        offerData.setRelationshipOfferId(offerId);
        offerData.setOfferStatusCd(statusCd);
        offerData.setAcctUser1Cd(contactPoint);
        offerData.setAcctUser2Cd(ACCOUNT_USER_CODE_2);
        return offerData;
    }
}
