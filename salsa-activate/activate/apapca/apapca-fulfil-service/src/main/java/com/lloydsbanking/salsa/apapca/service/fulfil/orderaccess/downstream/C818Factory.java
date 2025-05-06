package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream;

import com.lloydsbanking.salsa.soap.cmas.c818.objects.*;

public final class C818Factory {

    private C818Factory() {}

    private static final int EXTERNAL_SYS_SALSA = 19;

    private static final String OPERATOR_NM = "Internet Banking";

    public static C818Req createC818Request(CardOrderAdd cardOrderAdd, CardOrderAddNew cardOrderAddNew, CardOrderCBSCCA cardOrderCBSCCA, CardDeliveryAddress cardDeliveryAddress, CardOrderActions cardOrderActions) {
        C818Req c818Req = new C818Req();
        c818Req.setExtSysId(EXTERNAL_SYS_SALSA);
        c818Req.setOperatorNm(OPERATOR_NM);
        c818Req.setCardOrderAdd(cardOrderAdd);
        c818Req.setCardOrderAddNew(cardOrderAddNew);
        c818Req.setCardOrderCBSCCA(cardOrderCBSCCA);
        c818Req.setCardDeliveryAddress(cardDeliveryAddress);
        c818Req.setCardOrderActions(cardOrderActions);
        return c818Req;
    }
}
