package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream;

import com.lloydsbanking.salsa.soap.cmas.c808.objects.C808Req;

public final class C808Factory {

    public static final String CARD_ATH_RSG_PARTY_TYPE_CD = "P";

    public static final int MAX_REPEAT_GROUP_QY = 1;

    public static final int EXTERNAL_SYS_SALSA = 19;

    private C808Factory() {}

    public static C808Req createC808Request(String sortCode, String accountNumber, long customerIdentifier) {
        C808Req c808Req = new C808Req();
        c808Req.setMaxRepeatGroupQy(MAX_REPEAT_GROUP_QY);
        c808Req.setExtSysId(EXTERNAL_SYS_SALSA);
        c808Req.setCardAuthorisingPartyId(customerIdentifier);
        c808Req.setCardAthrsgPartyTypeCd(CARD_ATH_RSG_PARTY_TYPE_CD);
        c808Req.setCardHoldingPartyId(customerIdentifier);
        c808Req.setSortCd(sortCode);
        c808Req.setAccountNo8(accountNumber);
        return c808Req;
    }
}