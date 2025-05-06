package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream;

import com.lloydsbanking.salsa.soap.cmas.c846.objects.C846Req;

public final class C846Factory {

    public static final int MAX_REPEAT_GROUP_QY = 0;

    public static final int EXTERNAL_SYS_SALSA = 19;

    private C846Factory() {
    }

    public static C846Req createC846Request(String productIdentifier, String cCAApplicableIndicator, String decisionText, Integer decisionCode, long customerIdentifier) {
        C846Req c846Req = new C846Req();
        c846Req.setMaxRepeatGroupQy(MAX_REPEAT_GROUP_QY);
        c846Req.setExtSysId(EXTERNAL_SYS_SALSA);
        c846Req.setExtProdIdTx(productIdentifier);
        c846Req.setCCAApplicableIn(cCAApplicableIndicator);
        c846Req.setCustomerDecisionTypeCd(decisionText);
        if (null!=decisionCode) {
            c846Req.setCustomerDecisionValueCd(decisionCode);
        }
        c846Req.setCardAuthorisingPartyId(customerIdentifier);
        return c846Req;
    }
}
