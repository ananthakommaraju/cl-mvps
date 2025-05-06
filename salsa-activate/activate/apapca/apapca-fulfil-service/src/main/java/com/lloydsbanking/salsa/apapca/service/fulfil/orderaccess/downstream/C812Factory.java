package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream;

import com.lloydsbanking.salsa.soap.cmas.c812.objects.*;

public final class C812Factory {

    public static final int MAX_REPEAT_GROUP_QY = 1;

    public static final int EXTERNAL_SYS_SALSA = 19;

    public static final String OPERATOR_NM = "Internet Banking";

    public static final String BSO2_AUDIT_CD = "Y";

    public static final String BSO2_PROGRAM_NM = "C812-IB";

    private C812Factory() {}

    public static C812Req createC812Request(CardOrderNew cardOrderNew, CardOrderCBSData cardOrderCBSData, CardOrderCBSAddress cardOrderCBSAddress) {
        C812Req c812Req = new C812Req();
        c812Req.setMaxRepeatGroupQy(MAX_REPEAT_GROUP_QY);
        c812Req.setExtSysId(EXTERNAL_SYS_SALSA);
        c812Req.setOperatorNm(OPERATOR_NM);
        BSO2AuditControl bso2AuditControl = new BSO2AuditControl();
        bso2AuditControl.setBSO2AuditCd(BSO2_AUDIT_CD);
        bso2AuditControl.setBSO2ProgramNm(BSO2_PROGRAM_NM);
        c812Req.setBSO2AuditControl(bso2AuditControl);
        c812Req.setCardOrderNew(cardOrderNew);
        c812Req.setCardOrderCBSData(cardOrderCBSData);
        c812Req.setCardOrderCBSAddress(cardOrderCBSAddress);
        return c812Req;
    }
}
