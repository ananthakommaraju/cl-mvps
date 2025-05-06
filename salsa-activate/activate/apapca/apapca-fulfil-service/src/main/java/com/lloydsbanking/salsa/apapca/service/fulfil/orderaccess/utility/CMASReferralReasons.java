package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.utility;

public enum CMASReferralReasons {

    PLASTIC_CONFLICT(6363),
    AWAIT_PARENT_CONSENT(6366),
    AUTH_RECEIVED(6364),
    RISK_IND_LIMIT(6361),
    MAN_RISK_ASSES_REC_36(6362),
    MAN_RISK_ASSES_REC_80(6802),
    DEC_VALUE_LIMIT(9311),
    DEC_ASSES_REC(9312),
    REN_CODE_LIMIT(9324),
    INVALID_DOB(6368),
    AUTH_REQD(9350),
    AGREEMENT_AWAITED(6365);

    private long reasonCode;

    public long getCode() {
        return reasonCode;
    }

    public boolean equalsCode(long other) {
        return other == reasonCode;
    }

    CMASReferralReasons(long reasonCode) {
        this.reasonCode = reasonCode;
    }
}
